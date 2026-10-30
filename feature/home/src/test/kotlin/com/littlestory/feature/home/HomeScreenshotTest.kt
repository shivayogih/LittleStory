package com.littlestory.feature.home

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import com.littlestory.core.designsystem.theme.LittleStoryTheme
import com.littlestory.feature.home.home.HomeActions
import com.littlestory.feature.home.home.HomeScreen
import com.littlestory.feature.home.home.HomeUiState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Renders Home per locale and font scale into build/outputs/roborazzi for owner review. No baselines are committed. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [36])
class HomeScreenshotTest {
    @get:Rule
    val rule = createComposeRule()

    private fun capture(name: String, state: HomeUiState = populatedHome) {
        rule.setContent { LittleStoryTheme { HomeScreen(state, HomeActions(), onFavouriteClick = {}) } }
        rule.onRoot().captureRoboImage("build/outputs/roborazzi/home_$name.png")
    }

    @Test
    @Config(qualifiers = "en-w430dp-h2000dp-xxhdpi")
    fun english() = capture("en")

    @Test
    @Config(qualifiers = "en-w430dp-h2000dp-xxhdpi")
    fun englishNothingStarted() = capture("en_empty", emptyContinueHome)

    @Test
    @Config(qualifiers = "kn-w430dp-h2000dp-xxhdpi")
    fun kannada() = capture("kn")

    @Test
    @Config(qualifiers = "kn-w430dp-h2800dp-xxhdpi", fontScale = 2f)
    fun kannadaLargeText() = capture("kn_200")

    @Test
    @Config(qualifiers = "en-rXA-w430dp-h2000dp-xxhdpi")
    fun pseudo() = capture("en-XA")
}
