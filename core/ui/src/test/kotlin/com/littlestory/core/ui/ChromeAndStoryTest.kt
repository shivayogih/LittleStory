package com.littlestory.core.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.littlestory.core.designsystem.icon.LittleStoryIcons
import com.littlestory.core.designsystem.theme.LittleStoryTheme
import com.littlestory.core.ui.chrome.BottomNav
import com.littlestory.core.ui.chrome.NavTab
import com.littlestory.core.ui.chrome.TopBar
import com.littlestory.core.ui.story.StoryArtCategory
import com.littlestory.core.ui.story.StoryArtMotif
import com.littlestory.core.ui.story.StoryCard
import com.littlestory.core.ui.story.gradientFor
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ChromeAndStoryTest {
    @get:Rule
    val rule = createComposeRule()

    private val tabs =
        listOf(
            NavTab("Home", LittleStoryIcons.Home),
            NavTab("Discover", LittleStoryIcons.Compass),
            NavTab("Create", LittleStoryIcons.Plus, isCreate = true),
            NavTab("Stories", LittleStoryIcons.Book),
            NavTab("Profile", LittleStoryIcons.User),
        )

    @Test
    fun bottomNavAnnouncesTheSelectedTabAndSwitches() {
        rule.setContent {
            LittleStoryTheme {
                var selected by remember { mutableIntStateOf(0) }
                BottomNav(tabs, selected, { selected = it }, Modifier.testTag("nav"))
            }
        }
        rule.onNodeWithText("Home").assertIsSelected()
        rule.onNodeWithText("Stories").assertIsNotSelected().performClick()
        rule.onNodeWithText("Stories").assertIsSelected()
        rule.onNodeWithText("Home").assertIsNotSelected()
    }

    @Test
    fun topBarBackButtonCallsBack() {
        var backs = 0
        rule.setContent {
            LittleStoryTheme {
                TopBar(
                    title = "T",
                    backContentDescription = "Back",
                    onBack = { backs++ },
                )
            }
        }
        rule.onNodeWithContentDescription("Back").performClick()
        assertEquals(1, backs)
    }

    @Test
    fun storyCardFavouriteAndClickAreSeparateActions() {
        var favs = 0
        var opens = 0
        rule.setContent {
            LittleStoryTheme {
                StoryCard(
                    title = "Title",
                    meta = "Meta",
                    gradient = LittleStoryTheme.colors.gradientFor(StoryArtCategory.Bedtime),
                    motif = StoryArtMotif.Moon,
                    favourite = false,
                    favouriteContentDescription = "Favourite",
                    onFavouriteClick = { favs++ },
                    onClick = { opens++ },
                )
            }
        }
        rule.onNodeWithContentDescription("Favourite").performClick()
        rule.onNodeWithText("Title").performClick()
        assertEquals(1, favs)
        assertEquals(1, opens)
    }
}
