package com.littlestory.core.ui

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.littlestory.core.ui.text.UiText
import com.littlestory.core.ui.text.asString
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class UiTextTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun resolvesResourceText() {
        assertEquals("Try again", UiText.Res(R.string.action_try_again).asString(context))
    }

    @Test
    @Config(qualifiers = "kn")
    fun resolvesKannadaWhenLocaleIsKannada() {
        assertEquals("ಮತ್ತೆ ಪ್ರಯತ್ನಿಸಿ", UiText.Res(R.string.action_try_again).asString(context))
    }

    @Test
    fun dynamicTextIsPassedThrough() {
        assertEquals("Aarav", UiText.Dynamic("Aarav").asString(context))
    }
}
