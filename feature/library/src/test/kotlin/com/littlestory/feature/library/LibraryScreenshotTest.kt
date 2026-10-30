package com.littlestory.feature.library

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import com.littlestory.core.designsystem.theme.LittleStoryTheme
import com.littlestory.feature.library.downloads.DownloadsScreen
import com.littlestory.feature.library.downloads.DownloadsUiState
import com.littlestory.feature.library.stories.MyStoriesActions
import com.littlestory.feature.library.stories.MyStoriesScreen
import com.littlestory.feature.library.stories.MyStoriesUiState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Renders My Stories and Downloads per locale into build/outputs/roborazzi. No baselines are committed. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [36])
class LibraryScreenshotTest {
    @get:Rule
    val rule = createComposeRule()

    private fun capture(name: String, content: @Composable () -> Unit) {
        rule.setContent { LittleStoryTheme { content() } }
        rule.onRoot().captureRoboImage("build/outputs/roborazzi/$name.png")
    }

    private fun stories(state: MyStoriesUiState): @Composable () -> Unit = {
        MyStoriesScreen(state, MyStoriesActions(), {}, {})
    }

    private fun downloads(state: DownloadsUiState): @Composable () -> Unit = { DownloadsScreen(state, {}, {}, {}) }

    @Test
    @Config(qualifiers = "en-w430dp-h1200dp-xxhdpi")
    fun myStoriesEnglish() = capture("stories_en", stories(myStoriesAll))

    @Test
    @Config(qualifiers = "kn-w430dp-h1200dp-xxhdpi")
    fun myStoriesKannada() = capture("stories_kn", stories(myStoriesAll))

    @Test
    @Config(qualifiers = "kn-w430dp-h2200dp-xxhdpi", fontScale = 2f)
    fun myStoriesKannadaLarge() = capture("stories_kn_200", stories(myStoriesAll))

    @Test
    @Config(qualifiers = "en-w430dp-h932dp-xxhdpi")
    fun myStoriesEmpty() = capture("stories_en_empty", stories(myStoriesEmptyFavourites))

    @Test
    @Config(qualifiers = "en-w430dp-h932dp-xxhdpi")
    fun downloadsEnglish() = capture("downloads_en", downloads(downloadsTwo))

    @Test
    @Config(qualifiers = "kn-w430dp-h932dp-xxhdpi")
    fun downloadsKannadaEmpty() = capture("downloads_kn_empty", downloads(downloadsEmpty))
}
