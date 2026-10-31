package com.littlestory.feature.home

import com.littlestory.core.ui.text.UiText
import com.littlestory.domain.model.ReadingProgress
import com.littlestory.feature.home.home.HomeUiState
import com.littlestory.feature.home.home.HomeViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    @get:Rule
    val main = MainDispatcherRule()

    private val catalog =
        FakeCatalog(
            listOf(
                story("s_bedtime", "bedtime"),
                story("s_friend", "friendship", tags = listOf("kindness")),
                story("s_animals", "adventure", tags = listOf("animals")),
                story("s_extra", "space"),
                story("s_extra2", "space"),
            ),
        )
    private val favourites = FakeFavourites()
    private val progress = FakeProgress()
    private val profiles = FakeProfiles(sampleChild)

    private fun viewModel(profileRepo: FakeProfiles = profiles) =
        HomeViewModel(profileRepo, FakePreferences(), catalog, favourites, progress)

    private fun TestScope.collected(vm: HomeViewModel): () -> HomeUiState {
        backgroundScope.launch { vm.state.collect {} }
        advanceUntilIdle()
        return { vm.state.value }
    }

    @Test
    fun greetsTheChildAndRanksStoriesByInterests() =
        runTest {
            val vm = viewModel()
            assertTrue(vm.state.value.loading)
            val state = collected(vm)()
            assertFalse(state.loading)
            assertEquals(UiText.Res(R.string.home_title_for_child, listOf("Aarav")), state.title)
            assertEquals(com.littlestory.core.common.config.AppConfig.Home.MADE_FOR_COUNT, state.madeFor.size)
            // The child's interests (animals, adventure) come first.
            assertEquals("s_animals", state.madeFor.first().id)
            assertEquals(1, state.avatarTone)
        }

    @Test
    fun exploreTilesFollowTheConfiguredOrderAndSkipMissingCategories() =
        runTest {
            val state = collected(viewModel())()
            assertEquals(listOf("bedtime", "animals", "friendship"), state.categories.map { it.id })
        }

    @Test
    fun noProfileUsesGenericCopy() =
        runTest {
            val state = collected(viewModel(FakeProfiles(null)))()
            assertEquals(UiText.Res(R.string.home_title_generic), state.title)
            assertEquals(UiText.Res(R.string.home_made_for_generic), state.madeForTitle)
            assertNull(state.avatarTone)
        }

    @Test
    fun continueCardShowsOnlyStartedUnfinishedStories() =
        runTest {
            val read = collected(viewModel())
            assertNull(read().continueListening)
            progress.update(ReadingProgress("child_1", "s_friend", 1, 0.42f, updatedAtEpochMs = 10))
            progress.update(ReadingProgress("child_1", "s_bedtime", 3, 1f, updatedAtEpochMs = 20))
            advanceUntilIdle()
            val item = assertNotNull(read().continueListening)
            assertEquals("s_friend", item.story.id)
            assertEquals(42, item.percent)
        }

    @Test
    fun favouriteToggleIsPerChildAndReversible() =
        runTest {
            val vm = viewModel()
            val read = collected(vm)
            vm.onFavouriteClick("s_animals")
            advanceUntilIdle()
            assertTrue(read().madeFor.first { it.id == "s_animals" }.favourite)
            vm.onFavouriteClick("s_animals")
            advanceUntilIdle()
            assertTrue(read().madeFor.none { it.favourite })
        }

    @Test
    fun guestKeepsFavouritesWithoutAProfile() =
        runTest {
            val vm = viewModel(FakeProfiles(null))
            val read = collected(vm)
            vm.onFavouriteClick("s_bedtime")
            advanceUntilIdle()
            assertTrue(read().madeFor.first { it.id == "s_bedtime" }.favourite)
        }
}
