package com.littlestory.feature.reader

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import com.littlestory.core.designsystem.theme.LittleStoryTheme
import com.littlestory.feature.reader.player.PlayerCallbacks
import com.littlestory.feature.reader.player.PlayerScreen
import com.littlestory.feature.reader.player.PlayerUiState
import com.littlestory.feature.reader.reader.ReaderCallbacks
import com.littlestory.feature.reader.reader.ReaderScreen
import com.littlestory.feature.reader.reader.ReaderUiState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Renders the Reader and the Player per locale into build/outputs/roborazzi. No baselines are committed. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [36])
class ReaderScreenshotTest {
    @get:Rule
    val rule = createComposeRule()

    private fun capture(name: String, content: @Composable () -> Unit) {
        rule.setContent { LittleStoryTheme { content() } }
        rule.onRoot().captureRoboImage("build/outputs/roborazzi/$name.png")
    }

    private fun reader(state: ReaderUiState): @Composable () -> Unit = { ReaderScreen(state, ReaderCallbacks()) }

    private fun player(state: PlayerUiState): @Composable () -> Unit = { PlayerScreen(state, PlayerCallbacks()) }

    @Test
    @Config(qualifiers = "en-w430dp-h932dp-xxhdpi")
    fun readerFirstPage() = capture("reader_en", reader(readerState()))

    @Test
    @Config(qualifiers = "kn-w430dp-h932dp-xxhdpi")
    fun readerKannadaLargeText() = capture("reader_kn_page2", reader(readerState(page = 1, scale = 1.3f)))

    @Test
    @Config(qualifiers = "kn-w430dp-h1600dp-xxhdpi", fontScale = 2f)
    fun readerKannada200() = capture("reader_kn_200", reader(readerState()))

    @Test
    @Config(qualifiers = "en-w430dp-h932dp-xxhdpi")
    fun playerEnglish() = capture("player_en", player(playerState))

    @Test
    @Config(qualifiers = "en-w360dp-h740dp-xxhdpi")
    fun playerCompact() = capture("player_en_compact", player(playerState))

    @Test
    @Config(qualifiers = "kn-w430dp-h932dp-xxhdpi")
    fun playerKannada() = capture("player_kn", player(playerState))

    @Test
    @Config(qualifiers = "en-w430dp-h932dp-xxhdpi")
    fun sleepTimerSheet() = capture("player_en_sleep", player(playerSleepSheet))
}
