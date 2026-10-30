package com.littlestory.feature.discover

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import com.littlestory.core.designsystem.theme.LittleStoryTheme
import com.littlestory.feature.discover.category.CategoryScreen
import com.littlestory.feature.discover.detail.DetailActions
import com.littlestory.feature.discover.detail.StoryDetailScreen
import com.littlestory.feature.discover.discover.DiscoverActions
import com.littlestory.feature.discover.discover.DiscoverScreen
import com.littlestory.feature.discover.discover.DiscoverUiState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Renders Discover, Category and Story Detail per locale and font scale into build/outputs/roborazzi. No baselines are committed. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [36])
class ScreenshotTest {
    @get:Rule
    val rule = createComposeRule()

    private fun capture(name: String, content: @Composable () -> Unit) {
        rule.setContent { LittleStoryTheme { content() } }
        rule.onRoot().captureRoboImage("build/outputs/roborazzi/$name.png")
    }

    private fun discover(state: DiscoverUiState): @Composable () -> Unit = {
        DiscoverScreen(state, DiscoverActions(), {}, {}, {}, {})
    }

    private val category: @Composable () -> Unit = { CategoryScreen(categoryState, {}, {}, {}, {}) }
    private val detail: @Composable () -> Unit = { StoryDetailScreen(detailState, DetailActions(), {}, {}) }

    @Test
    @Config(qualifiers = "en-w430dp-h1700dp-xxhdpi")
    fun discoverEnglish() = capture("discover_en", discover(discoverDefault))

    @Test
    @Config(qualifiers = "en-w430dp-h932dp-xxhdpi")
    fun discoverSearching() = capture("discover_en_search", discover(discoverSearching))

    @Test
    @Config(qualifiers = "en-w430dp-h932dp-xxhdpi")
    fun discoverEmpty() = capture("discover_en_empty", discover(discoverEmpty))

    @Test
    @Config(qualifiers = "kn-w430dp-h1700dp-xxhdpi")
    fun discoverKannada() = capture("discover_kn", discover(discoverDefault))

    @Test
    @Config(qualifiers = "kn-w430dp-h2600dp-xxhdpi", fontScale = 2f)
    fun discoverKannadaLarge() = capture("discover_kn_200", discover(discoverDefault))

    @Test
    @Config(qualifiers = "en-w430dp-h1100dp-xxhdpi")
    fun categoryEnglish() = capture("category_en", category)

    @Test
    @Config(qualifiers = "kn-w430dp-h1100dp-xxhdpi")
    fun categoryKannada() = capture("category_kn", category)

    @Test
    @Config(qualifiers = "kn-w430dp-h1800dp-xxhdpi", fontScale = 2f)
    fun categoryKannadaLarge() = capture("category_kn_200", category)

    @Test
    @Config(qualifiers = "en-w430dp-h932dp-xxhdpi")
    fun detailEnglish() = capture("detail_en", detail)

    @Test
    @Config(qualifiers = "kn-w430dp-h932dp-xxhdpi")
    fun detailKannada() = capture("detail_kn", detail)

    @Test
    @Config(qualifiers = "kn-w430dp-h1400dp-xxhdpi", fontScale = 2f)
    fun detailKannadaLarge() = capture("detail_kn_200", detail)

    @Test
    @Config(qualifiers = "en-rXA-w430dp-h1700dp-xxhdpi")
    fun discoverPseudo() = capture("discover_en-XA", discover(discoverDefault))
}
