package com.littlestory.feature.home

import android.content.Context
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ApplicationProvider
import com.littlestory.core.designsystem.theme.LittleStoryTheme
import com.littlestory.feature.home.home.HomeActions
import com.littlestory.feature.home.home.HomeScreen
import com.littlestory.feature.home.home.HomeTags
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "en-w430dp-h2000dp-xxhdpi")
class HomeScreenTest {
    @get:Rule
    val rule = createComposeRule()

    private val calls = mutableListOf<String>()
    private val actions =
        HomeActions(
            onNotifications = { calls += "notifications" },
            onProfiles = { calls += "profiles" },
            onCreate = { calls += "create" },
            onResume = { calls += "resume:$it" },
            onDiscover = { calls += "discover" },
            onCategory = { calls += "category:$it" },
            onStory = { calls += "story:$it" },
        )

    private fun show(state: com.littlestory.feature.home.home.HomeUiState = populatedHome) {
        rule.setContent { LittleStoryTheme { HomeScreen(state, actions, onFavouriteClick = { calls += "fav:$it" }) } }
    }

    private fun text(id: Int) = ApplicationProvider.getApplicationContext<Context>().getString(id)

    private fun tap(tag: String) {
        rule.onNodeWithTag(tag).performScrollTo().performClick()
    }

    @Test
    fun everyTargetNavigatesAsSpecified() {
        show()
        tap(HomeTags.BELL)
        tap(HomeTags.AVATAR)
        tap(HomeTags.CREATE)
        rule.onNodeWithText(text(R.string.home_continue_resume)).performClick()
        rule.onAllNodesWithText(text(R.string.home_see_all)).onFirst().performClick()
        tap(HomeTags.category("bedtime"))
        tap(HomeTags.story("story_little_elephant"))
        tap(HomeTags.CULTURE)
        assertEquals(
            listOf(
                "notifications",
                "profiles",
                "create",
                "resume:story_magical_cricket_bat",
                "discover",
                "category:bedtime",
                "story:story_little_elephant",
                "category:indian_tales",
            ),
            calls,
        )
    }

    @Test
    fun titleShowsTheChildName() {
        show()
        rule.onNodeWithTag(HomeTags.TITLE).assertIsDisplayed()
    }

    @Test
    fun noContinueCardWhenNothingIsStarted() {
        show(emptyContinueHome)
        rule.onAllNodesWithTag(HomeTags.CONTINUE_CARD).assertCountEquals(0)
    }
}
