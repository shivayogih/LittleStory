package com.littlestory.feature.discover

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import com.littlestory.core.designsystem.theme.LittleStoryTheme
import com.littlestory.core.ui.chrome.ChromeTags
import com.littlestory.feature.discover.category.CategoryScreen
import com.littlestory.feature.discover.category.CategoryTags
import com.littlestory.feature.discover.category.CategoryUiState
import com.littlestory.feature.discover.detail.DetailActions
import com.littlestory.feature.discover.detail.DetailTags
import com.littlestory.feature.discover.detail.StoryDetailScreen
import com.littlestory.feature.discover.detail.StoryDetailUiState
import com.littlestory.feature.discover.discover.DiscoverActions
import com.littlestory.feature.discover.discover.DiscoverScreen
import com.littlestory.feature.discover.discover.DiscoverTags
import com.littlestory.feature.discover.discover.DiscoverUiState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "en-w430dp-h2000dp-xxhdpi")
class ScreensTest {
    @get:Rule
    val rule = createComposeRule()

    private val calls = mutableListOf<String>()

    private fun text(id: Int) = ApplicationProvider.getApplicationContext<Context>().getString(id)

    private fun showDiscover(state: DiscoverUiState) {
        rule.setContent {
            LittleStoryTheme {
                DiscoverScreen(
                    state = state,
                    actions =
                        DiscoverActions(
                            onNotifications = { calls += "bell" },
                            onStory = { calls += "story:$it" },
                            onCategory = { calls += "category:$it" },
                        ),
                    onQueryChange = { calls += "query:$it" },
                    onFilterSelected = { calls += "filter:$it" },
                    onClearSearch = { calls += "clear" },
                    onFavouriteClick = { calls += "fav:$it" },
                )
            }
        }
    }

    @Test
    fun discoverRoutesTapsAndReportsInput() {
        showDiscover(discoverDefault)
        rule.onNode(hasSetTextAction()).performTextInput("owl")
        rule.onNodeWithTag(DiscoverTags.chip("animals")).performScrollTo().performClick()
        rule.onNodeWithTag(DiscoverTags.FEATURED).performScrollTo().performClick()
        rule.onNodeWithText(text(R.string.discover_view_all)).performScrollTo().performClick()
        rule.onNodeWithTag(DiscoverTags.story("story_magical_cricket_bat")).performScrollTo().performClick()
        assertEquals(
            listOf(
                "query:owl",
                "filter:animals",
                "story:story_little_elephant",
                "category:all",
                "story:story_magical_cricket_bat",
            ),
            calls,
        )
    }

    @Test
    fun discoverEmptyStateOffersClearSearch() {
        showDiscover(discoverEmpty)
        rule.onNodeWithTag(DiscoverTags.EMPTY).assertIsDisplayed()
        rule.onNodeWithText(text(R.string.discover_empty_action)).performClick()
        assertEquals(listOf("clear"), calls)
    }

    @Test
    fun categoryRowsOpenTheStoryAndSortIsLabelled() {
        rule.setContent {
            LittleStoryTheme {
                CategoryScreen(categoryState, { calls += "back" }, { calls += "story:$it" }, { calls += "sort:$it" }, {
                    calls +=
                        "fav:$it"
                })
            }
        }
        rule.onNodeWithTag(CategoryTags.HERO).assertIsDisplayed()
        rule.onNodeWithTag(CategoryTags.story("story_banyan_secret")).performClick()
        assertEquals(listOf("story:story_banyan_secret"), calls)
    }

    @Test
    fun detailButtonsMatchTheSpec() {
        rule.setContent {
            LittleStoryTheme {
                StoryDetailScreen(
                    detailState,
                    DetailActions({ calls += "back" }, { calls += "listen:$it" }, { calls += "read:$it" }),
                    { calls += "fav" },
                    { calls += "download" },
                )
            }
        }
        rule.onNodeWithTag(DetailTags.FAVOURITE).performClick()
        rule.onNodeWithTag(DetailTags.DOWNLOAD).performClick()
        rule.onNodeWithText(text(R.string.detail_listen)).performClick()
        rule.onNodeWithText(text(R.string.detail_read)).performClick()
        assertEquals(listOf("fav", "download", "listen:story_little_elephant", "read:story_little_elephant"), calls)
    }

    @Test
    fun missingStoryShowsNotFound() {
        rule.setContent { LittleStoryTheme { StoryDetailScreen(StoryDetailUiState.NotFound, DetailActions(), {}, {}) } }
        rule.onNodeWithTag(DetailTags.NOT_FOUND).assertIsDisplayed()
    }

    @Test
    fun loadingShowsTheSpinnerAndNeverTheEmptyState() {
        rule.setContent {
            LittleStoryTheme {
                CategoryScreen(CategoryUiState(loading = true), { calls += "back" }, {}, {}, {})
            }
        }
        rule.onNodeWithTag(ChromeTags.LOADING).assertIsDisplayed()
        rule.onNodeWithTag(CategoryTags.EMPTY).assertDoesNotExist()
        rule.onNodeWithTag(ChromeTags.BACK).performClick()
        assertEquals(listOf("back"), calls)
    }

    @Test
    fun discoverWhileLoadingShowsTheSpinnerNotTheSections() {
        showDiscover(DiscoverUiState(loading = true))
        rule.onNodeWithTag(ChromeTags.LOADING).assertIsDisplayed()
        rule.onNodeWithTag(DiscoverTags.EMPTY).assertDoesNotExist()
    }

    @Test
    fun loadingDetailKeepsBackAvailable() {
        rule.setContent {
            LittleStoryTheme {
                StoryDetailScreen(
                    StoryDetailUiState.Loading,
                    DetailActions({ calls += "back" }),
                    {},
                    {},
                )
            }
        }
        rule.onNodeWithTag(ChromeTags.LOADING).assertIsDisplayed()
        rule.onNodeWithTag(ChromeTags.BACK).performClick()
        assertEquals(listOf("back"), calls)
    }
}
