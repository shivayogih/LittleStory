package com.littlestory.feature.create

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import com.littlestory.core.designsystem.theme.LittleStoryTheme
import com.littlestory.feature.create.form.CreateCallbacks
import com.littlestory.feature.create.form.CreateScreen
import com.littlestory.feature.create.form.CreateUiState
import com.littlestory.feature.create.generating.GeneratingScreen
import com.littlestory.feature.create.generating.GeneratingUiState
import com.littlestory.feature.create.ready.ReadyActions
import com.littlestory.feature.create.ready.StoryReadyScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Renders Create, Generating and Story Ready per locale into build/outputs/roborazzi. No baselines are committed. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [36])
class CreateScreenshotTest {
    @get:Rule
    val rule = createComposeRule()

    private fun capture(name: String, content: @Composable () -> Unit) {
        rule.setContent { LittleStoryTheme { content() } }
        rule.onRoot().captureRoboImage("build/outputs/roborazzi/$name.png")
    }

    private fun create(state: CreateUiState): @Composable () -> Unit = { CreateScreen(state, CreateCallbacks()) }

    private fun generating(state: GeneratingUiState): @Composable () -> Unit = { GeneratingScreen(state, {}, {}, {}) }

    private val ready: @Composable () -> Unit = { StoryReadyScreen(readyLoaded, ReadyActions(), {}, {}) }

    @Test
    @Config(qualifiers = "en-w430dp-h2000dp-xxhdpi")
    fun createEnglish() = capture("create_en", create(createExpanded))

    @Test
    @Config(qualifiers = "en-w430dp-h1400dp-xxhdpi")
    fun createEmptyError() = capture("create_en_error", create(createEmptyError))

    @Test
    @Config(qualifiers = "kn-w430dp-h2000dp-xxhdpi")
    fun createKannada() = capture("create_kn", create(createExpanded))

    @Test
    @Config(qualifiers = "kn-w430dp-h3000dp-xxhdpi", fontScale = 2f)
    fun createKannadaLarge() = capture("create_kn_200", create(createDefault))

    @Test
    @Config(qualifiers = "en-w430dp-h932dp-xxhdpi")
    fun generatingEnglish() = capture("generating_en", generating(generatingHalfway))

    @Test
    @Config(qualifiers = "kn-w430dp-h932dp-xxhdpi")
    fun generatingPausedKannada() = capture("generating_kn_paused", generating(generatingPaused))

    @Test
    @Config(qualifiers = "en-w430dp-h1400dp-xxhdpi")
    fun readyEnglish() = capture("ready_en", ready)

    @Test
    @Config(qualifiers = "kn-w430dp-h1400dp-xxhdpi")
    fun readyKannada() = capture("ready_kn", ready)
}
