package com.littlestory.core.ui

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.littlestory.core.common.safety.ParentalGateChallenge
import com.littlestory.core.designsystem.theme.LittleStoryTheme
import com.littlestory.core.ui.safety.LocalParentalGateChallenges
import com.littlestory.core.ui.safety.ParentGated
import com.littlestory.core.ui.safety.ParentalGateTags
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ParentalGateTest {
    @get:Rule
    val rule = createComposeRule()

    private var cancelled = 0

    // Questions in order: 12 × 7 = 84, then 13 × 7 = 91, then 14 × 7 = 98.
    private var asked = 0

    private fun nextQuestion() = ParentalGateChallenge(BASE + asked++, SEVEN)

    private fun gatedContent() {
        rule.setContent {
            LittleStoryTheme {
                CompositionLocalProvider(LocalParentalGateChallenges provides { nextQuestion() }) {
                    ParentGated(onCancel = { cancelled++ }) {
                        BasicText("parent area", Modifier.testTag(CONTENT))
                    }
                }
            }
        }
    }

    private fun answer(text: String) {
        text.forEach { rule.onNodeWithTag(ParentalGateTags.key(it.digitToInt())).performScrollTo().performClick() }
        rule.onNodeWithTag(ParentalGateTags.SUBMIT).performScrollTo().performClick()
    }

    @Test
    fun contentStaysHiddenUntilTheRightAnswer() {
        gatedContent()
        rule.onNodeWithTag(CONTENT).assertDoesNotExist()
        rule.onNodeWithTag(ParentalGateTags.SUBMIT).assertIsNotEnabled()
        answer("84")
        rule.onNodeWithTag(CONTENT).assertExists()
        rule.onNodeWithTag(ParentalGateTags.DIALOG).assertDoesNotExist()
    }

    @Test
    fun aWrongAnswerAsksANewQuestion() {
        gatedContent()
        answer("85")
        rule.onNodeWithTag(CONTENT).assertDoesNotExist()
        // The first question's answer no longer works; the newest one's does.
        answer("84")
        rule.onNodeWithTag(CONTENT).assertDoesNotExist()
        answer("98")
        rule.onNodeWithTag(CONTENT).assertExists()
    }

    @Test
    fun clearEmptiesTheAnswer() {
        gatedContent()
        rule.onNodeWithTag(ParentalGateTags.key(8)).performScrollTo().performClick()
        rule.onNodeWithTag(ParentalGateTags.CLEAR).performScrollTo().performClick()
        rule.onNodeWithTag(ParentalGateTags.SUBMIT).assertIsNotEnabled()
    }

    @Test
    fun cancelLeavesWithoutShowingTheContent() {
        gatedContent()
        rule.onNodeWithTag(ParentalGateTags.CANCEL).performScrollTo().performClick()
        assertEquals(1, cancelled)
        rule.onNodeWithTag(CONTENT).assertDoesNotExist()
    }

    private companion object {
        const val CONTENT = "gated_content"
        const val BASE = 12
        const val SEVEN = 7
    }
}
