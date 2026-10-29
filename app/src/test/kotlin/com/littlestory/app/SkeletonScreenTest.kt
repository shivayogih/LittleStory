package com.littlestory.app

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.littlestory.app.skeleton.SkeletonScreen
import com.littlestory.app.skeleton.languageOptionTag
import com.littlestory.core.common.language.SupportedLanguages
import com.littlestory.core.designsystem.theme.LittleStoryTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class SkeletonScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    private fun show(initial: com.littlestory.core.common.language.SupportedLanguage, onSelect: (String) -> Unit = {}) {
        composeRule.setContent {
            var selected by remember { mutableStateOf(initial) }
            LittleStoryTheme {
                SkeletonScreen(
                    languages = SupportedLanguages.enabled,
                    selected = selected,
                    onLanguageSelected = {
                        selected = it
                        onSelect(it.tag)
                    },
                )
            }
        }
    }

    @Test
    fun registryDrivesThePicker() {
        show(SupportedLanguages.English)
        SupportedLanguages.enabled.forEach {
            composeRule.onNodeWithTag(languageOptionTag(it)).assertExists()
            composeRule.onNodeWithText(it.nativeName).assertExists()
        }
    }

    @Test
    fun selectingKannadaReportsItAndMarksItSelected() {
        val chosen = mutableListOf<String>()
        show(SupportedLanguages.English) { chosen += it }
        composeRule.onNodeWithTag(languageOptionTag(SupportedLanguages.English)).assertIsSelected()
        composeRule.onNodeWithTag(languageOptionTag(SupportedLanguages.Kannada)).performClick()
        composeRule.onNodeWithTag(languageOptionTag(SupportedLanguages.Kannada)).assertIsSelected()
        composeRule.onNodeWithTag(languageOptionTag(SupportedLanguages.English)).performClick()
        composeRule.onNodeWithTag(languageOptionTag(SupportedLanguages.English)).assertIsSelected()
        assertEquals(listOf("kn", "en"), chosen)
    }

    @Test
    @Config(qualifiers = "kn")
    fun kannadaResourcesAreUsedWhenLocaleIsKannada() {
        show(SupportedLanguages.Kannada)
        composeRule.onNodeWithText("ಪುಟ್ಟ ಕಥೆಗಳು. ದೊಡ್ಡ ಕಲ್ಪನೆ.").assertExists()
    }
}
