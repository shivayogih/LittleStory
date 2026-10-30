package com.littlestory.feature.library

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.littlestory.core.designsystem.theme.LittleStoryTheme
import com.littlestory.feature.library.downloads.DownloadsScreen
import com.littlestory.feature.library.downloads.DownloadsTags
import com.littlestory.feature.library.navigation.LibraryTab
import com.littlestory.feature.library.stories.MyStoriesActions
import com.littlestory.feature.library.stories.MyStoriesScreen
import com.littlestory.feature.library.stories.MyStoriesTags
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "en-w430dp-h2000dp-xxhdpi")
class LibraryScreensTest {
    @get:Rule
    val rule = createComposeRule()

    private val calls = mutableListOf<String>()

    @Test
    fun myStoriesRoutesCreatedAndCatalogueStoriesSeparately() {
        rule.setContent {
            LittleStoryTheme {
                MyStoriesScreen(
                    myStoriesAll,
                    MyStoriesActions(
                        onDownloads = { calls += "downloads" },
                        onStory = { calls += "story:$it" },
                        onCreatedStory = { calls += "created:$it" },
                        onPlay = { calls += "play:$it" },
                    ),
                    onQueryChange = {},
                    onTabSelected = { calls += "tab:$it" },
                )
            }
        }
        rule.onNodeWithTag(MyStoriesTags.DOWNLOADS).performClick()
        rule.onNodeWithTag(MyStoriesTags.tab(LibraryTab.Favourites)).performClick()
        rule.onNodeWithTag(MyStoriesTags.RECENT).performScrollTo().performClick()
        rule.onNodeWithTag(MyStoriesTags.story("generated_1")).performScrollTo().performClick()
        rule.onNodeWithTag(MyStoriesTags.story("fox")).performScrollTo().performClick()
        assertEquals(
            listOf("downloads", "tab:Favourites", "play:owl", "created:generated_1", "story:fox"),
            calls,
        )
    }

    @Test
    fun emptyTabOffersDiscover() {
        rule.setContent {
            LittleStoryTheme {
                MyStoriesScreen(
                    myStoriesEmptyFavourites,
                    MyStoriesActions(onDiscover = { calls += "discover" }),
                    {},
                    {},
                )
            }
        }
        rule.onNodeWithTag(MyStoriesTags.EMPTY).assertIsDisplayed()
    }

    @Test
    fun downloadsRemoveReportsTheStory() {
        rule.setContent {
            LittleStoryTheme { DownloadsScreen(downloadsTwo, {}, {}, onRemove = { calls += "remove:$it" }) }
        }
        rule.onNodeWithTag(DownloadsTags.STORAGE).assertIsDisplayed()
        rule.onNodeWithTag(DownloadsTags.remove("owl")).performScrollTo().performClick()
        assertEquals(listOf("remove:owl"), calls)
    }
}
