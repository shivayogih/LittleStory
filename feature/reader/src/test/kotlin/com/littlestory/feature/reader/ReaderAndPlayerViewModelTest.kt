package com.littlestory.feature.reader

import androidx.lifecycle.SavedStateHandle
import com.littlestory.core.common.config.AppConfig
import com.littlestory.domain.model.ReadingProgress
import com.littlestory.domain.model.SleepTimer
import com.littlestory.domain.model.StoryOrigin
import com.littlestory.domain.player.Narrator
import com.littlestory.feature.reader.player.PlayerViewModel
import com.littlestory.feature.reader.reader.ReaderUiState
import com.littlestory.feature.reader.reader.ReaderViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Optional
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
@OptIn(ExperimentalCoroutinesApi::class)
class ReaderAndPlayerViewModelTest {
    @get:Rule
    val main = MainDispatcherRule()

    private val catalog = FakeCatalog(listOf(story("s1", pages = 3), story("long", minutes = 60, pages = 4)))
    private val library = FakeLibrary(mapOf("child_1" to listOf(story("generated_1", origin = StoryOrigin.Generated))))
    private val favourites = FakeFavourites()
    private val downloads = FakeDownloads()
    private val progress = FakeProgress()
    private val preferences = FakePreferences()
    private val stores = stores(catalog, library, favourites, downloads, progress)

    private fun reader(id: String, page: Int = 0) =
        ReaderViewModel(
            SavedStateHandle(mapOf("storyId" to id, "page" to page)),
            FakeProfiles(sampleChild),
            preferences,
            stores,
        )

    private fun player(id: String, narrator: Narrator? = null) =
        PlayerViewModel(
            SavedStateHandle(mapOf("storyId" to id)),
            FakeProfiles(sampleChild),
            preferences,
            stores,
            Optional.ofNullable(narrator),
        )

    private fun TestScope.loaded(vm: ReaderViewModel): () -> ReaderUiState.Loaded {
        backgroundScope.launch { vm.state.collect {} }
        advanceUntilIdle()
        return { assertIs<ReaderUiState.Loaded>(vm.state.value) }
    }

    @Test
    fun readerPagesWithinBoundsAndSavesThePage() =
        runTest {
            progress.update(ReadingProgress("child_1", "s1", 0, 0.4f, 1))
            val vm = reader("s1")
            val state = loaded(vm)
            assertFalse(state().content.canGoBack)
            vm.onNext()
            vm.onNext()
            vm.onNext()
            advanceUntilIdle()
            assertEquals(2, state().content.page)
            assertFalse(state().content.canGoForward)
            val row = progress.rows.value.single()
            assertEquals(2, row.page)
            assertEquals(0.4f, row.listenFraction)
        }

    @Test
    fun readerTextSizeStepsAndStopsAtTheLimits() =
        runTest {
            val vm = reader("s1")
            val state = loaded(vm)
            repeat(6) {
                vm.onTextLarger()
                advanceUntilIdle()
            }
            assertEquals(AppConfig.Reader.TEXT_SCALE_MAX, state().content.textScale)
            assertFalse(state().content.canGrow)
            vm.onTextSmaller()
            advanceUntilIdle()
            assertEquals(1.2f, preferences.readingState.value.textScale)
        }

    @Test
    fun readerOpensCreatedStoriesAndReportsMissingOnes() =
        runTest {
            assertEquals("generated_1", loaded(reader("generated_1"))().content.id)
            val missing = reader("nope")
            backgroundScope.launch { missing.state.collect {} }
            advanceUntilIdle()
            assertEquals(ReaderUiState.NotFound, missing.state.value)
        }

    @Test
    fun playerResumesSavedPositionAndSavesWhenPaused() =
        runTest {
            progress.update(ReadingProgress("child_1", "s1", 1, 0.5f, 1))
            val vm = player("s1")
            advanceUntilIdle()
            assertEquals(0.5f, vm.state.value.position)
            assertEquals(3, vm.state.value.chapters)
            vm.onPlayPause()
            advanceTimeBy(AppConfig.Player.SIMULATED_TICK_MS * 4 + 1)
            vm.onPlayPause()
            advanceUntilIdle()
            val expected = 0.5f + 4 * AppConfig.Player.TICK_PERCENT / 100f
            assertEquals(expected, vm.state.value.position, 0.0001f)
            assertEquals(
                expected,
                progress.rows.value
                    .single()
                    .listenFraction,
                0.0001f,
            )
            assertEquals(
                1,
                progress.rows.value
                    .single()
                    .page,
            )
        }

    @Test
    fun aNarratorReadsThePagesAndMovesThePositionPageByPage() =
        runTest {
            val heard = mutableListOf<Pair<Int, String>>()
            val pageGate = Channel<Unit>()
            val narrator =
                object : Narrator {
                    override fun narrate(pages: List<String>, fromPage: Int, languageTag: String, speed: Float) =
                        flow {
                            heard += fromPage to languageTag
                            for (page in fromPage until pages.size) {
                                emit(page)
                                pageGate.receive()
                            }
                        }
                }
            val vm = player("s1", narrator)
            advanceUntilIdle()
            vm.onPlayPause()
            advanceUntilIdle()
            assertEquals(listOf(0 to "en"), heard)
            pageGate.send(Unit)
            advanceUntilIdle()
            assertEquals(1f / 3, vm.state.value.position, 0.0001f)
            pageGate.send(Unit)
            pageGate.send(Unit)
            advanceUntilIdle()
            assertFalse(vm.state.value.playing)
            assertEquals(1f, vm.state.value.position)
            assertEquals(
                1f,
                progress.rows.value
                    .single()
                    .listenFraction,
            )
        }

    @Test
    fun playerSkipsChaptersAndCyclesSpeed() =
        runTest {
            val vm = player("long")
            advanceUntilIdle()
            vm.onForward()
            assertEquals(15f / 3600f, vm.state.value.position, 0.0001f)
            vm.onRewind()
            vm.onRewind()
            assertEquals(0f, vm.state.value.position)
            vm.onNextChapter()
            assertEquals(1, vm.state.value.chapter)
            vm.onPreviousChapter()
            assertEquals(0, vm.state.value.chapter)
            vm.onSpeedClick()
            advanceUntilIdle()
            assertEquals(1.25f, vm.state.value.speed)
            assertEquals(1.25f, preferences.readingState.value.playbackSpeed)
        }

    @Test
    fun sleepTimerStopsPlaybackAndTurnsOff() =
        runTest {
            val vm = player("long")
            advanceUntilIdle()
            vm.onSleepTimerClick()
            assertTrue(vm.state.value.sleepSheetOpen)
            vm.onSleepTimerSelected(SleepTimer.Minutes(10))
            assertFalse(vm.state.value.sleepSheetOpen)
            vm.onPlayPause()
            // 10 simulated minutes of a 60-minute story at 9 s per tick.
            advanceTimeBy(AppConfig.Player.SIMULATED_TICK_MS * 70)
            assertFalse(vm.state.value.playing)
            assertEquals(SleepTimer.Off, vm.state.value.sleepTimer)
            assertTrue(vm.state.value.position < 0.2f)
        }

    @Test
    fun playerTogglesFavouriteAndDownload() =
        runTest {
            val vm = player("s1")
            advanceUntilIdle()
            vm.onFavouriteClick()
            vm.onDownloadClick()
            advanceUntilIdle()
            assertTrue(vm.state.value.favourite)
            assertTrue(vm.state.value.downloaded)
            vm.onDownloadClick()
            advanceUntilIdle()
            assertFalse(vm.state.value.downloaded)
        }
}
