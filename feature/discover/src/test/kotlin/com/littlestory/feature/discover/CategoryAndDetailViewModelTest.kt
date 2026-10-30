package com.littlestory.feature.discover

import androidx.lifecycle.SavedStateHandle
import com.littlestory.core.common.config.AppConfig
import com.littlestory.core.ui.text.UiText
import com.littlestory.domain.model.StoryCategory
import com.littlestory.feature.discover.category.CategoryViewModel
import com.littlestory.feature.discover.detail.StoryDetailUiState
import com.littlestory.feature.discover.detail.StoryDetailViewModel
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
import kotlin.test.assertIs
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
@OptIn(ExperimentalCoroutinesApi::class)
class CategoryAndDetailViewModelTest {
    @get:Rule
    val main = MainDispatcherRule()

    private val stories =
        listOf(
            story("long", "space", minutes = 15, added = 5),
            story("short", "space", minutes = 5, added = 1),
            story("new", "space", minutes = 8, added = 9),
            story("other", "bedtime", minutes = 7, added = 3, tags = listOf("space")),
        )
    private val categories =
        listOf(StoryCategory("space", "Space", "Rockets", 3), StoryCategory("bedtime", "Bedtime", "Sleepy", 1))
    private val favourites = FakeFavourites()
    private val downloads = FakeDownloads()
    private val catalog = FakeCatalog(stories, categories)

    private fun category(id: String?) =
        CategoryViewModel(
            SavedStateHandle(if (id == null) emptyMap() else mapOf("categoryId" to id)),
            FakeProfiles(sampleChild),
            FakePreferences(),
            catalog,
            downloads,
            favourites,
        )

    private fun detail(id: String) =
        StoryDetailViewModel(
            SavedStateHandle(mapOf("storyId" to id)),
            FakeProfiles(sampleChild),
            FakePreferences(),
            catalog,
            favourites,
            downloads,
        )


    @Test
    fun categoryListsPrimaryAndTaggedStoriesInRecommendedOrder() =
        runTest {
            val vm = category("space")
            backgroundScope.launch { vm.state.collect {} }
            advanceUntilIdle()
            val state = vm.state.value
            assertEquals(UiText.Dynamic("Space"), state.title)
            assertEquals(listOf("long", "short", "new", "other"), state.stories.map { it.id })
            assertEquals(4, state.storyCount)
        }

    @Test
    fun sortOrdersAreCorrect() =
        runTest {
            val vm = category("space")
            backgroundScope.launch { vm.state.collect {} }
            advanceUntilIdle()
            vm.onSortSelected("shortest")
            advanceUntilIdle()
            assertEquals(listOf("short", "other", "new", "long"), vm.state.value.stories.map { it.id })
            vm.onSortSelected("newest")
            advanceUntilIdle()
            assertEquals(listOf("new", "long", "other", "short"), vm.state.value.stories.map { it.id })
        }

    @Test
    fun allCategoryShowsTheWholeCatalogue() =
        runTest {
            val vm = category(AppConfig.Discover.ALL_ID)
            backgroundScope.launch { vm.state.collect {} }
            advanceUntilIdle()
            assertEquals(4, vm.state.value.stories.size)
            assertEquals(UiText.Res(R.string.category_all_name), vm.state.value.title)
        }

    @Test
    fun downloadedStoriesAreFlaggedOffline() =
        runTest {
            downloads.start("short")
            val vm = category("space")
            backgroundScope.launch { vm.state.collect {} }
            advanceUntilIdle()
            assertTrue(vm.state.value.stories.first { it.id == "short" }.downloaded)
            assertFalse(vm.state.value.stories.first { it.id == "long" }.downloaded)
        }

    @Test
    fun detailShowsTheStoryAndTogglesFavouriteAndDownload() =
        runTest {
            val vm = detail("long")
            backgroundScope.launch { vm.state.collect {} }
            advanceUntilIdle()
            val loaded = assertIs<StoryDetailUiState.Loaded>(vm.state.value)
            assertEquals("Space", loaded.content.categoryName)
            assertEquals("English", loaded.content.languageName)
            vm.onFavouriteClick()
            vm.onDownloadClick()
            advanceUntilIdle()
            val updated = assertIs<StoryDetailUiState.Loaded>(vm.state.value).content
            assertTrue(updated.favourite && updated.downloaded)
            vm.onDownloadClick()
            advanceUntilIdle()
            assertFalse(assertIs<StoryDetailUiState.Loaded>(vm.state.value).content.downloaded)
        }

    @Test
    fun unknownStoryIdIsNotFound() =
        runTest {
            val vm = detail("missing")
            backgroundScope.launch { vm.state.collect {} }
            advanceUntilIdle()
            assertEquals(StoryDetailUiState.NotFound, vm.state.value)
        }
}
