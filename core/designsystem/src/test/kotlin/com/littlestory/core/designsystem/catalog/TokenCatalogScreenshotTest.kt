package com.littlestory.core.designsystem.catalog

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import com.littlestory.core.designsystem.theme.LittleStoryTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Renders the token catalog per locale. Images are written to build/outputs/roborazzi for review;
 * baselines are not committed until the owner approves them (CLAUDE.md: never auto-approve baselines).
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [36], qualifiers = "w430dp-h2000dp-xxhdpi")
class TokenCatalogScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    private fun render(name: String) {
        composeRule.setContent { LittleStoryTheme { TokenCatalog() } }
        composeRule.onRoot().captureRoboImage("build/outputs/roborazzi/token_catalog_$name.png")
    }

    @Test
    @Config(qualifiers = "en-w430dp-h2000dp-xxhdpi")
    fun english() = render("en")

    @Test
    @Config(qualifiers = "kn-w430dp-h2000dp-xxhdpi")
    fun kannada() = render("kn")

    @Test
    @Config(qualifiers = "en-rXA-w430dp-h2000dp-xxhdpi")
    fun pseudoLocale() = render("en-XA")
}
