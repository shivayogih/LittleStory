package com.littlestory.core.designsystem.component

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertWidthIsAtLeast
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.littlestory.core.designsystem.icon.LittleStoryIcons
import com.littlestory.core.designsystem.theme.LittleStoryTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ComponentsTest {
    @get:Rule
    val rule = createComposeRule()

    private fun show(content: @androidx.compose.runtime.Composable () -> Unit) =
        rule.setContent {
            LittleStoryTheme { content() }
        }

    @Test
    fun buttonClicksWhenEnabledAndNotWhenDisabled() {
        var clicks = 0
        show {
            Column {
                LittleStoryButton("Go", { clicks++ }, Modifier.testTag("on"))
                LittleStoryButton("Go", { clicks++ }, Modifier.testTag("off"), enabled = false)
            }
        }
        rule.onNodeWithTag("on").assertIsEnabled().performClick()
        rule.onNodeWithTag("off").assertIsNotEnabled().performClick()
        assertEquals(1, clicks)
    }

    @Test
    fun buttonGrowsFromItsMinimumHeight() {
        show { LittleStoryButton("Go", {}, Modifier.testTag("b")) }
        rule.onNodeWithTag("b").assertHeightIsAtLeast(52.dp)
    }

    @Test
    fun iconButtonHasA48dpTouchTargetAndClicks() {
        var clicked = false
        show { LittleStoryIconButton(LittleStoryIcons.Heart, "fav", { clicked = true }, Modifier.testTag("i")) }
        rule
            .onNodeWithTag("i")
            .assertWidthIsAtLeast(48.dp)
            .assertHeightIsAtLeast(48.dp)
            .performClick()
        assertEquals(true, clicked)
    }

    @Test
    fun chipExposesSelectedStateAndRole() {
        show {
            var selected by remember { mutableStateOf(false) }
            LittleStoryChip("Animals", selected, { selected = !selected }, Modifier.testTag("c"))
        }
        rule
            .onNodeWithTag(
                "c",
            ).assertIsNotSelected()
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton))
        rule.onNodeWithTag("c").performClick().assertIsSelected()
    }

    @Test
    fun multiSelectChipUsesCheckboxSemantics() {
        show { LittleStoryChip("Animals", true, {}, Modifier.testTag("c"), multiSelect = true) }
        rule
            .onNodeWithTag(
                "c",
            ).assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Checkbox))
            .assertIsOn()
    }

    @Test
    fun toggleIsASwitchThatFlips() {
        show {
            var on by remember { mutableStateOf(false) }
            LittleStoryToggle(on, { on = it }, "Reminder", Modifier.testTag("t"))
        }
        rule
            .onNodeWithTag(
                "t",
            ).assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Switch))
            .assertIsOff()
        rule.onNodeWithTag("t").performClick().assertIsOn()
    }

    @Test
    fun segmentedControlReportsTheTappedSegment() {
        var chosen = -1
        show { LittleStorySegmentedControl(listOf("5", "10", "15"), 1, { chosen = it }, Modifier.testTag("s")) }
        rule.onNodeWithTag("s").assertExists()
        rule.onNodeWithText("15").performClick()
        assertEquals(2, chosen)
    }

    @Test
    fun searchFieldShowsClearOnlyWhenNotEmpty() {
        var value = ""
        show { LittleStorySearchField(value, { value = it }, "Search", "Clear") }
        rule.onNodeWithContentDescription("Clear").assertDoesNotExist()
    }

    @Test
    fun progressBarsExposeProgressSemantics() {
        show {
            Column {
                LittleStoryReadingProgress(0.42f, Modifier.testTag("p"))
                LittleStoryGenerationProgress(2f, Modifier.testTag("g"))
            }
        }
        rule.onNodeWithTag("p").assert(
            SemanticsMatcher.expectValue(
                SemanticsProperties.ProgressBarRangeInfo,
                ProgressBarRangeInfo(
                    0.42f,
                    0f..1f,
                ),
            ),
        )
        // values above 1 are clamped
        rule.onNodeWithTag("g").assert(
            SemanticsMatcher.expectValue(
                SemanticsProperties.ProgressBarRangeInfo,
                ProgressBarRangeInfo(
                    1f,
                    0f..1f,
                ),
            ),
        )
    }
}
