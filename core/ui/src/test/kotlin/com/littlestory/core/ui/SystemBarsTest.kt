package com.littlestory.core.ui

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.core.view.WindowCompat
import com.littlestory.core.ui.system.SystemBarIcons
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class SystemBarsTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private fun lightStatusBars(): Boolean {
        val window = rule.activity.window
        return WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars
    }

    @Test
    fun darkIconsOnCreamLightIconsOnNightAndRestoredOnExit() {
        val initial = lightStatusBars()
        var screen by mutableStateOf<Boolean?>(true)
        rule.setContent { screen?.let { SystemBarIcons(darkIcons = it) } }
        rule.waitForIdle()
        assertEquals(true, lightStatusBars())
        screen = false
        rule.waitForIdle()
        assertEquals(false, lightStatusBars())
        screen = null
        rule.waitForIdle()
        assertEquals(initial, lightStatusBars())
    }
}
