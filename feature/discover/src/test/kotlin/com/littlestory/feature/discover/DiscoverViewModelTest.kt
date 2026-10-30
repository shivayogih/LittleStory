package com.littlestory.feature.discover

import androidx.lifecycle.SavedStateHandle
import com.littlestory.core.common.config.AppConfig
import com.littlestory.domain.model.StoryCategory
import com.littlestory.feature.discover.discover.DiscoverUiState
import com.littlestory.feature.discover.discover.DiscoverViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

// Robolectric supplies the Android Bundle that SavedStateHandle needs.
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
@OptIn(ExperimentalCoroutinesApi::class)
class DiscoverViewModelTest {
    @get:Rule
    val main = MainDispatcherRule()

    private val stories =
        listOf(
            story(
                "s_elephant",
                "friendship",
                tags = listOf("animals"),
                title = "The Little Elephant",
                synopsis = "Mangoes",
            ),
            story("s_cricket", "adventure", title = "Magical Cricket Bat", synopsis = "A glowing bat"),
            story("s_moon", "bedtime", title = "Moonlit Garden", synopsis = "Sleepy flowers"),
            story("s_kn", "space", title = "ಅನನ್ಯಾಳ ಪ್ರಯಾಣ", synopsis = "ರಾಕೆಟ್"),
            story("s_a", "space"),
            story("s_b", "space"),
        )
    private val categories =
        listOf("friendship", "adventure", "bedtime", "space", "animals").map {
            StoryCategory(it, it.replaceFirstChar(Char::uppercase), "d", 1)
        }
    private val favourites = FakeFavourites()

    private fun viewModel(language: String = "en", child: com.littlestory.domain.model.ChildProfile? = sampleChild) =
        DiscoverViewModel(
            SavedStateHandle(),
            FakeProfiles(child),
            FakePreferences(language),
            FakeCatalog(stories, categories),
            favourites,
        )

    private fun TestScope.collected(vm: DiscoverViewModel): () -> DiscoverUiState {
        backgroundScope.launch { vm.state.collect {} }
        advanceUntilIdle()
        return { vm.state.value }
    }

    @Test
    fun defaultShowsFeaturedPopularAndBedtimeRow() =
        runTest {
            val state = collected(viewModel())()
            assertNotNull(state.featured)
            assertEquals(AppConfig.Discover.POPULAR_COUNT, state.results.size)
            assertEquals(listOf("s_moon"), state.bedtime.map { it.id })
            assertTrue(state.showViewAll)
            assertFalse(state.hasNoResults)
        }

    @Test
    fun chipsStartWithAllAndFollowTheConfiguredOrder() =
        runTest {
            val state = collected(viewModel())()
            assertEquals(AppConfig.Discover.ALL_ID, state.filters.first().id)
            assertEquals(
                AppConfig.Discover.filterIds.filter { id -> id == "all" || categories.any { it.id == id } },
                state.filters.map { it.id },
            )
        }

    @Test
    fun searchIsTrimmedCaseInsensitiveAndHidesFeaturedAndBedtime() =
        runTest {
            val vm = viewModel()
            val read = collected(vm)
            vm.onQueryChange("  CRICKET ")
            advanceUntilIdle()
            assertEquals(listOf("s_cricket"), read().results.map { it.id })
            assertNull(read().featured)
            assertTrue(read().bedtime.isEmpty())
            assertFalse(read().showViewAll)
        }

    @Test
    fun searchMatchesSynopsisAndKannadaTitles() =
        runTest {
            val vm = viewModel("kn")
            val read = collected(vm)
            vm.onQueryChange("sleepy")
            advanceUntilIdle()
            assertEquals(listOf("s_moon"), read().results.map { it.id })
            vm.onQueryChange("ಪ್ರಯಾಣ")
            advanceUntilIdle()
            assertEquals(listOf("s_kn"), read().results.map { it.id })
        }

    @Test
    fun chipFiltersByCategoryOrTag() =
        runTest {
            val vm = viewModel()
            val read = collected(vm)
            vm.onFilterSelected("animals")
            advanceUntilIdle()
            assertEquals(listOf("s_elephant"), read().results.map { it.id })
            assertTrue(read().showViewAll)
            vm.onFilterSelected("space")
            advanceUntilIdle()
            assertEquals(3, read().results.size)
        }

    @Test
    fun noMatchShowsEmptyStateAndClearRestoresDefault() =
        runTest {
            val vm = viewModel()
            val read = collected(vm)
            vm.onQueryChange("zzzz")
            vm.onFilterSelected("space")
            advanceUntilIdle()
            assertTrue(read().hasNoResults)
            vm.onClearSearch()
            advanceUntilIdle()
            assertEquals("", read().query)
            assertEquals(AppConfig.Discover.ALL_ID, read().selectedFilter)
            assertFalse(read().hasNoResults)
        }

    @Test
    fun favouriteToggleUpdatesCards() =
        runTest {
            val vm = viewModel()
            val read = collected(vm)
            vm.onFavouriteClick("s_elephant")
            advanceUntilIdle()
            assertTrue(read().results.first { it.id == "s_elephant" }.favourite)
        }
}
