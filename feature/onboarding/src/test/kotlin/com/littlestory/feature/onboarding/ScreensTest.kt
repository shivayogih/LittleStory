package com.littlestory.feature.onboarding

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.SavedStateHandle
import com.littlestory.core.common.language.SupportedLanguages
import com.littlestory.core.designsystem.theme.LittleStoryTheme
import com.littlestory.feature.onboarding.onboarding.OnboardingScreen
import com.littlestory.feature.onboarding.onboarding.OnboardingTags
import com.littlestory.feature.onboarding.onboarding.OnboardingViewModel
import com.littlestory.feature.onboarding.setup.SetupScreen
import com.littlestory.feature.onboarding.setup.SetupTags
import com.littlestory.feature.onboarding.setup.SetupUiState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ScreensTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun onboardingContinueAdvancesAndSkipIsAlwaysAvailable() {
        val vm = OnboardingViewModel(SavedStateHandle())
        var skips = 0
        rule.setContent {
            LittleStoryTheme {
                val state by vm.state.collectAsState()
                OnboardingScreen(state, onContinue = vm::onContinue, onSkip = { skips++ })
            }
        }
        rule.onNodeWithTag(OnboardingTags.CONTINUE).performClick()
        assertEquals(1, vm.state.value.page)
        rule.onNodeWithTag(OnboardingTags.SKIP).performClick()
        assertEquals(1, skips)
    }

    @Test
    fun setupReportsEditsAndChipSelectionIsAnnounced() {
        var state by mutableStateOf(SetupUiState())
        var submitted = 0
        rule.setContent {
            LittleStoryTheme {
                SetupScreen(
                    state = state,
                    languages = SupportedLanguages.enabled,
                    onNameChange = { state = state.copy(name = it) },
                    onAvatarSelected = { state = state.copy(avatar = it) },
                    onAgeSelected = { state = state.copy(age = it) },
                    onLanguageSelected = { state = state.copy(languageTag = it) },
                    onInterestToggled = { id ->
                        state =
                            state.copy(
                                interestIds =
                                    if (id in
                                        state.interestIds
                                    ) {
                                        state.interestIds - id
                                    } else {
                                        state.interestIds + id
                                    },
                            )
                    },
                    onDurationSelected = { state = state.copy(durationMinutes = it) },
                    onSubmit = { submitted++ },
                    onSkip = {},
                )
            }
        }
        rule.onNodeWithTag(SetupTags.NAME).performScrollTo().performTextInput("Aarav")
        assertEquals("Aarav", state.name)
        rule
            .onNodeWithTag(SetupTags.interest("space"))
            .performScrollTo()
            .performClick()
            .assertIsOn()
        rule
            .onNodeWithTag(SetupTags.avatar(2))
            .performScrollTo()
            .performClick()
            .assertIsSelected()
        rule.onNodeWithTag(SetupTags.SUBMIT).performClick()
        assertEquals(1, submitted)
    }
}
