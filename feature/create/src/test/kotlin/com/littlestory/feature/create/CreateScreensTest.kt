package com.littlestory.feature.create

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import com.littlestory.core.designsystem.theme.LittleStoryTheme
import com.littlestory.feature.create.form.CreateCallbacks
import com.littlestory.feature.create.form.CreateScreen
import com.littlestory.feature.create.form.CreateTags
import com.littlestory.feature.create.generating.GeneratingScreen
import com.littlestory.feature.create.generating.GeneratingTags
import com.littlestory.feature.create.ready.ReadyActions
import com.littlestory.feature.create.ready.ReadyTags
import com.littlestory.feature.create.ready.StoryReadyScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "en-w430dp-h2000dp-xxhdpi")
class CreateScreensTest {
    @get:Rule
    val rule = createComposeRule()

    private val calls = mutableListOf<String>()

    @Test
    fun createFormReportsInputChoicesAndSubmit() {
        rule.setContent {
            LittleStoryTheme {
                CreateScreen(
                    createExpanded,
                    CreateCallbacks(
                        onPromptChange = { calls += "prompt:$it" },
                        onThemeSelected = { calls += "theme:$it" },
                        onMoodSelected = { calls += "mood:$it" },
                        onDurationSelected = { calls += "duration:$it" },
                        onToggleMoreOptions = { calls += "more" },
                        onCreate = { calls += "create" },
                    ),
                )
            }
        }
        rule.onNodeWithTag(CreateTags.PROMPT).performTextInput("!")
        rule.onNodeWithTag(CreateTags.suggestion(0)).performScrollTo().performClick()
        rule.onNodeWithTag(CreateTags.theme("animals")).performScrollTo().performClick()
        rule.onNodeWithTag(CreateTags.MORE_OPTIONS).performScrollTo().performClick()
        rule.onNodeWithTag(CreateTags.mood("funny")).performScrollTo().performClick()
        rule.onNodeWithTag(CreateTags.duration(15)).performScrollTo().performClick()
        rule.onNodeWithTag(CreateTags.SUBMIT).performScrollTo().performClick()
        assertEquals("prompt:!${createExpanded.prompt}", calls[0])
        assertEquals(listOf("theme:animals", "more", "mood:funny", "duration:15", "create"), calls.drop(2))
    }

    @Test
    fun emptyErrorIsShown() {
        rule.setContent { LittleStoryTheme { CreateScreen(createEmptyError, CreateCallbacks()) } }
        rule.onNodeWithTag(CreateTags.ERROR).assertIsDisplayed()
    }

    @Test
    fun pausedGenerationOffersTryAgainAndBackToIdea() {
        rule.setContent {
            LittleStoryTheme {
                GeneratingScreen(generatingPaused, { calls += "close" }, { calls += "retry" }, { calls += "back" })
            }
        }
        rule.onNodeWithTag(GeneratingTags.PAUSED).assertIsDisplayed()
        rule.onNodeWithTag(GeneratingTags.TRY_AGAIN).performScrollTo().performClick()
        rule.onNodeWithTag(GeneratingTags.BACK_TO_IDEA).performScrollTo().performClick()
        assertEquals(listOf("retry", "back"), calls)
    }

    @Test
    fun readyRoutesSavedFavouriteAndDownload() {
        rule.setContent {
            LittleStoryTheme {
                StoryReadyScreen(
                    readyLoaded,
                    ReadyActions(onOpenCreated = { calls += "saved" }),
                    onFavouriteClick = { calls += "fav" },
                    onDownloadClick = { calls += "download" },
                )
            }
        }
        rule.onNodeWithTag(ReadyTags.FAVOURITE).performClick()
        rule.onNodeWithTag(ReadyTags.DOWNLOAD).performClick()
        rule.onNodeWithTag(ReadyTags.SAVED).performScrollTo().performClick()
        assertEquals(listOf("fav", "download", "saved"), calls)
    }
}
