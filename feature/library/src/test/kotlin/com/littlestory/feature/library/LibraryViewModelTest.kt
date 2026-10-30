package com.littlestory.feature.library

import androidx.lifecycle.SavedStateHandle
import com.littlestory.core.common.config.AppConfig
import com.littlestory.domain.model.Download
import com.littlestory.domain.model.DownloadStatus
import com.littlestory.domain.model.ReadingProgress
import com.littlestory.domain.model.StoryOrigin
import com.littlestory.feature.library.downloads.DownloadsViewModel
import com.littlestory.feature.library.navigation.LibraryTab
import com.littlestory.feature.library.stories.MyStoriesViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
@OptIn(ExperimentalCoroutinesApi::class)
class LibraryViewModelTest {
    @get:Rule
    val main = MainDispatcherRule()

    private val catalog =
        FakeCatalog(listOf(story("owl", title = "Owl and the Moon"), story("fox", title = "Brave Fox")))
    private val library =
        FakeLibrary(
            mapOf(
                "child_1" to
                    listOf(
                        story("generated_1", title = "Aarav's Cricket Bat", origin = StoryOrigin.Generated),
                    ),
            ),
        )
    private val favourites = FakeFavourites()
    private val downloads = FakeDownloads()
    private val progress = FakeProgress()

    private fun myStories(tab: LibraryTab = LibraryTab.All) =
        MyStoriesViewModel(
            SavedStateHandle(mapOf("tab" to tab)),
            FakeProfiles(sampleChild),
            FakePreferences(),
            stores(catalog, library, favourites, downloads, progress),
        )

    @Test
    fun allTabListsCreatedFirstAndMarksThem() =
        runTest {
            val vm = myStories()
            backgroundScope.launch { vm.state.collect {} }
            advanceUntilIdle()
            val state = vm.state.value
            assertEquals(listOf("generated_1", "owl", "fox"), state.stories.map { it.id })
            assertEquals(setOf("generated_1"), state.createdIds)
        }

    @Test
    fun tabsAndSearchFilter() =
        runTest {
            val vm = myStories(LibraryTab.CreatedByMe)
            backgroundScope.launch { vm.state.collect {} }
            advanceUntilIdle()
            assertEquals(
                listOf("generated_1"),
                vm.state.value.stories
                    .map { it.id },
            )
            favourites.setFavourite("child_1", "fox", true)
            downloads.start("owl")
            vm.onTabSelected(LibraryTab.Favourites)
            advanceUntilIdle()
            assertEquals(
                listOf("fox"),
                vm.state.value.stories
                    .map { it.id },
            )
            vm.onTabSelected(LibraryTab.Downloaded)
            advanceUntilIdle()
            assertEquals(
                listOf("owl"),
                vm.state.value.stories
                    .map { it.id },
            )
            vm.onTabSelected(LibraryTab.All)
            vm.onQueryChange("  MOON ")
            advanceUntilIdle()
            assertEquals(
                listOf("owl"),
                vm.state.value.stories
                    .map { it.id },
            )
        }

    @Test
    fun recentlyPlayedIsTheLatestUnfinishedListen() =
        runTest {
            progress.update(ReadingProgress("child_1", "owl", 0, 0.5f, 10))
            progress.update(ReadingProgress("child_1", "fox", 0, 1f, 20))
            val vm = myStories()
            backgroundScope.launch { vm.state.collect {} }
            advanceUntilIdle()
            val recent = assertNotNull(vm.state.value.recentlyPlayed)
            assertEquals("owl", recent.item.id)
            assertEquals(180, recent.remainingSeconds)
            vm.onQueryChange("fox")
            advanceUntilIdle()
            assertNull(vm.state.value.recentlyPlayed)
        }

    @Test
    fun downloadsListKnownStoriesSumsSizeAndRemoves() =
        runTest {
            val rows =
                FakeDownloads(
                    listOf(
                        Download("owl", 4_000_000, DownloadStatus.Completed),
                        Download("generated_1", 6_000_000, DownloadStatus.Completed),
                        Download("gone", 1, DownloadStatus.Completed),
                    ),
                )
            val vm = DownloadsViewModel(FakeProfiles(sampleChild), FakePreferences(), catalog, library, rows)
            backgroundScope.launch { vm.state.collect {} }
            advanceUntilIdle()
            assertEquals(
                listOf("owl", "generated_1"),
                vm.state.value.items
                    .map { it.storyId },
            )
            assertEquals(10_000_000, vm.state.value.usedBytes)
            assertEquals(AppConfig.Downloads.STORAGE_BAR_MIN_FRACTION, vm.state.value.usedFraction)
            vm.onRemove("owl")
            advanceUntilIdle()
            assertEquals(
                listOf("generated_1"),
                vm.state.value.items
                    .map { it.storyId },
            )
        }
}
