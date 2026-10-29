package com.littlestory.core.ui.catalog

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.LayoutDirection
import com.github.takahirom.roborazzi.captureRoboImage
import com.littlestory.core.designsystem.theme.LittleStoryTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Renders the component catalogue per locale (LS-020). Images go to build/outputs/roborazzi for
 * owner review; no baselines are committed until the owner approves them.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [36])
class ComponentCatalogScreenshotTest {
    @get:Rule
    val rule = createComposeRule()

    // Robolectric's ldrtl qualifier does not flip Compose, so RTL is forced through LocalLayoutDirection.
    private fun render(name: String, direction: LayoutDirection = LayoutDirection.Ltr) {
        rule.setContent {
            CompositionLocalProvider(LocalLayoutDirection provides direction) { LittleStoryTheme { ComponentCatalog() } }
        }
        rule.onRoot().captureRoboImage("build/outputs/roborazzi/component_catalog_$name.png")
    }

    @Test
    @Config(qualifiers = "en-w430dp-h3600dp-xxhdpi")
    fun english() = render("en")

    @Test
    @Config(qualifiers = "kn-w430dp-h3600dp-xxhdpi")
    fun kannada() = render("kn")

    @Test
    @Config(qualifiers = "en-rXA-w430dp-h3600dp-xxhdpi")
    fun pseudoLocale() = render("en-XA")

    @Test
    @Config(qualifiers = "ar-rXB-w430dp-h3600dp-xxhdpi")
    fun pseudoRtl() = render("ar-XB", LayoutDirection.Rtl)
}
