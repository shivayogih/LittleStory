package com.littlestory.feature.onboarding

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import com.littlestory.core.common.language.SupportedLanguages
import com.littlestory.core.designsystem.theme.LittleStoryTheme
import com.littlestory.core.ui.text.UiText
import com.littlestory.feature.onboarding.onboarding.OnboardingScreen
import com.littlestory.feature.onboarding.onboarding.OnboardingSlide
import com.littlestory.feature.onboarding.onboarding.OnboardingUiState
import com.littlestory.feature.onboarding.setup.SetupScreen
import com.littlestory.feature.onboarding.setup.SetupUiState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Renders Onboarding and Setup per locale and font scale into build/outputs/roborazzi for owner
 * review (EN, KN, KN at 200 %, en-XA). No baselines are committed.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [36])
class ScreenshotTest {
    @get:Rule
    val rule = createComposeRule()

    private val slides =
        listOf(
            OnboardingSlide(
                UiText.Res(R.string.onboarding_title_1),
                UiText.Res(R.string.onboarding_body_1),
                UiText.Res(com.littlestory.core.ui.R.string.action_continue),
            ),
            OnboardingSlide(
                UiText.Res(R.string.onboarding_title_2),
                UiText.Res(R.string.onboarding_body_2),
                UiText.Res(com.littlestory.core.ui.R.string.action_continue),
            ),
            OnboardingSlide(
                UiText.Res(R.string.onboarding_title_3),
                UiText.Res(R.string.onboarding_body_3),
                UiText.Res(R.string.onboarding_cta_get_started),
            ),
        )

    private fun onboarding(name: String, page: Int) {
        rule.setContent { LittleStoryTheme { OnboardingScreen(OnboardingUiState(slides, page), {}, {}) } }
        rule.onRoot().captureRoboImage("build/outputs/roborazzi/onboarding_${page + 1}_$name.png")
    }

    private fun setup(name: String) {
        val state =
            SetupUiState(name = "Aarav", interestIds = setOf("animals", "cricket", "adventure"), languageTag = "en")
        rule.setContent {
            LittleStoryTheme {
                SetupScreen(state, SupportedLanguages.enabled, {}, {}, {}, {}, {}, {}, {}, {})
            }
        }
        rule.onRoot().captureRoboImage("build/outputs/roborazzi/setup_$name.png")
    }

    @Test
    @Config(qualifiers = "en-w430dp-h932dp-xxhdpi")
    fun onboardingEnglishFirst() = onboarding("en", 0)

    @Test
    @Config(qualifiers = "en-w430dp-h932dp-xxhdpi")
    fun onboardingEnglishSecond() = onboarding("en", 1)

    @Test
    @Config(qualifiers = "en-w430dp-h932dp-xxhdpi")
    fun onboardingEnglishLast() = onboarding("en", 2)

    @Test
    @Config(qualifiers = "kn-w430dp-h932dp-xxhdpi")
    fun onboardingKannada() = onboarding("kn", 0)

    @Test
    @Config(qualifiers = "kn-w430dp-h932dp-xxhdpi", fontScale = 2f)
    fun onboardingKannadaLargeText() = onboarding("kn_200", 2)

    @Test
    @Config(qualifiers = "en-rXA-w430dp-h932dp-xxhdpi")
    fun onboardingPseudo() = onboarding("en-XA", 0)

    @Test
    @Config(qualifiers = "en-w430dp-h1400dp-xxhdpi")
    fun setupEnglish() = setup("en")

    @Test
    @Config(qualifiers = "kn-w430dp-h1400dp-xxhdpi")
    fun setupKannada() = setup("kn")

    @Test
    @Config(qualifiers = "kn-w430dp-h1400dp-xxhdpi", fontScale = 2f)
    fun setupKannadaLargeText() = setup("kn_200")

    @Test
    @Config(qualifiers = "en-rXA-w430dp-h1400dp-xxhdpi")
    fun setupPseudo() = setup("en-XA")
}
