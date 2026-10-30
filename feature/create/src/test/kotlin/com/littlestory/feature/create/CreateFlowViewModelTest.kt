package com.littlestory.feature.create

import androidx.lifecycle.SavedStateHandle
import com.littlestory.core.common.config.AppConfig
import com.littlestory.domain.model.StoryOrigin
import com.littlestory.feature.create.form.CreateEffect
import com.littlestory.feature.create.form.CreateViewModel
import com.littlestory.feature.create.generating.GeneratingEffect
import com.littlestory.feature.create.generating.GeneratingViewModel
import com.littlestory.feature.create.ready.StoryReadyUiState
import com.littlestory.feature.create.ready.StoryReadyViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
@OptIn(ExperimentalCoroutinesApi::class)
class CreateFlowViewModelTest {
    @get:Rule
    val main = MainDispatcherRule()

    private val generation = FakeGeneration(storyId = "generated_1")

    @Test
    fun emptyPromptShowsTheErrorAndSubmitsNothing() =
        runTest {
            val vm = CreateViewModel(FakeProfiles(sampleChild), FakePreferences(), generation)
            advanceUntilIdle()
            vm.onCreate()
            assertTrue(vm.state.value.showEmptyError)
            assertTrue(generation.submitted.isEmpty())
            vm.onPromptChange("a")
            assertEquals(false, vm.state.value.showEmptyError)
        }

    @Test
    fun promptIsCappedAndTheRequestCarriesTheChosenOptions() =
        runTest {
            val vm = CreateViewModel(FakeProfiles(sampleChild, secondChild), FakePreferences("kn"), generation)
            advanceUntilIdle()
            assertEquals("child_1", vm.state.value.heroId)
            assertEquals("kn", vm.state.value.languageTag)
            assertEquals(sampleChild.defaultDurationMinutes, vm.state.value.durationMinutes)
            vm.onPromptChange("x".repeat(AppConfig.Create.PROMPT_MAX_CHARS + 20))
            assertEquals(AppConfig.Create.PROMPT_MAX_CHARS, vm.state.value.prompt.length)
            vm.onPromptChange("  Played cricket  ")
            vm.onHeroSelected("child_2")
            vm.onThemeSelected("space")
            vm.onCreate()
            val effect = assertIs<CreateEffect.StartGeneration>(vm.effects.first())
            val request = generation.submitted.single()
            assertEquals(effect.requestId, request.requestId)
            assertEquals("Played cricket", request.prompt)
            assertEquals("Meera", request.heroName)
            assertEquals(secondChild.age, request.heroAge)
            assertEquals("space", request.themeId)
            assertEquals("kn", request.languageTag)
        }

    @Test
    fun guestRequestUsesTheGuestHero() =
        runTest {
            val vm = CreateViewModel(FakeProfiles(), FakePreferences(), generation)
            advanceUntilIdle()
            vm.onPromptChange("A rainy day")
            vm.onCreate()
            assertEquals(AppConfig.Profiles.GUEST_ID, generation.submitted.single().heroChildId)
            assertEquals("", generation.submitted.single().heroName)
        }

    private fun submitted(): String {
        val vm = CreateViewModel(FakeProfiles(sampleChild), FakePreferences(), generation)
        vm.onPromptChange("Cricket")
        vm.onCreate()
        return generation.submitted.last().requestId
    }

    private fun generating(requestId: String) =
        GeneratingViewModel(SavedStateHandle(mapOf("requestId" to requestId)), generation)

    @Test
    fun generationRunsToReadyAndCloseAndTryAgainResumeFromEight() =
        runTest {
            val vm = generating(submitted())
            runCurrent()
            assertEquals(0, vm.state.value.percent)
            advanceTimeBy(AppConfig.Generation.STEP_INTERVAL_MS * 3 + 1)
            assertEquals(AppConfig.Generation.STEP_PERCENT * 3, vm.state.value.percent)
            vm.onClose()
            advanceTimeBy(AppConfig.Generation.STEP_INTERVAL_MS * 5)
            assertTrue(vm.state.value.paused, "closed")
            assertEquals(AppConfig.Generation.STEP_PERCENT * 3, vm.state.value.percent)
            vm.onTryAgain()
            runCurrent()
            assertEquals(AppConfig.Generation.RESTART_PERCENT, vm.state.value.percent)
            advanceUntilIdle()
            assertEquals(100, vm.state.value.percent)
            assertEquals(GeneratingEffect.Ready("generated_1"), vm.effects.first())
        }

    @Test
    fun unknownRequestPausesWithBackToIdea() =
        runTest {
            val vm = generating("missing")
            advanceUntilIdle()
            assertTrue(vm.state.value.paused)
        }

    @Test
    fun readyShowsTheSavedStoryAndTogglesFavouriteAndDownload() =
        runTest {
            val created = story("generated_1", pages = 3, origin = StoryOrigin.Generated)
            val library = FakeLibrary(mapOf("child_1" to listOf(created)))
            val favourites = FakeFavourites()
            val downloads = FakeDownloads()
            val vm =
                StoryReadyViewModel(
                    SavedStateHandle(mapOf("storyId" to "generated_1")),
                    FakeProfiles(sampleChild),
                    FakePreferences(),
                    library,
                    favourites,
                    downloads,
                )
            backgroundScope.launch { vm.state.collect {} }
            advanceUntilIdle()
            val content = assertIs<StoryReadyUiState.Loaded>(vm.state.value).content
            assertEquals("Aarav", content.heroName)
            assertEquals(created.pages[AppConfig.Generation.PEEK_PAGE_INDEX].text, content.peek)
            vm.onFavouriteClick()
            vm.onDownloadClick()
            advanceUntilIdle()
            val updated = assertIs<StoryReadyUiState.Loaded>(vm.state.value).content
            assertTrue(updated.favourite)
            assertTrue(updated.downloaded)
        }

    @Test
    fun readyForAMissingStoryIsNotFound() =
        runTest {
            val vm =
                StoryReadyViewModel(
                    SavedStateHandle(mapOf("storyId" to "nope")),
                    FakeProfiles(sampleChild),
                    FakePreferences(),
                    FakeLibrary(),
                    FakeFavourites(),
                    FakeDownloads(),
                )
            backgroundScope.launch { vm.state.collect {} }
            advanceUntilIdle()
            assertEquals(StoryReadyUiState.NotFound, vm.state.value)
        }
}
