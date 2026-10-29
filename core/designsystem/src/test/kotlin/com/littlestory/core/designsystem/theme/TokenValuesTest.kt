package com.littlestory.core.designsystem.theme

import androidx.compose.ui.unit.dp
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** LS-012: token values match docs/design/design-tokens.json. */
class TokenValuesTest {
    @Test
    fun layoutDimensionsMatchDesign() {
        val dimens = LittleStoryDimens()
        assertEquals(68.dp, dimens.topBarHeight)
        assertEquals(76.dp, dimens.bottomNavHeight)
        assertEquals(52.dp, dimens.buttonMinHeight)
        assertTrue(dimens.minTouchTarget >= 48.dp, "touch targets must be at least 48 dp")
    }

    @Test
    fun compactSpacingOnlyChangesSidePadding() {
        assertEquals(20.dp, DefaultSpacing.screenHorizontal)
        assertEquals(16.dp, CompactSpacing.screenHorizontal)
        assertEquals(DefaultSpacing.copy(screenHorizontal = 16.dp), CompactSpacing)
    }

    @Test
    fun motionDurationsMatchDesign() {
        val motion = LittleStoryMotion()
        assertEquals(2400, motion.twinkleOnboardingMillis)
        assertEquals(1700, motion.twinkleGeneratingMillis)
        assertEquals(1100, motion.twinkleSparkMillis)
        assertEquals(180, motion.pressMillis)
        assertEquals(200, motion.toggleMillis)
        assertEquals(300, motion.readingProgressMillis)
    }

    @Test
    fun reducedMotionMakesTransitionsInstant() {
        assertEquals(200, LittleStoryMotion(reduced = false).duration(200))
        assertEquals(0, LittleStoryMotion(reduced = true).duration(200))
    }

    @Test
    fun kannadaTypographyAvoidsNegativeTrackingAndUppercase() {
        val kn = KannadaTypography
        assertFalse(kn.uppercaseEyebrows)
        assertTrue(LatinTypography.uppercaseEyebrows)
        listOf(kn.displayOnboarding, kn.heroTitle, kn.topBarTitle).forEach {
            assertTrue(it.letterSpacing.value.isNaN() || it.letterSpacing.value >= 0f, "no negative tracking in Kannada")
            assertTrue(it.lineHeight.value / it.fontSize.value >= 1.4f - 0.001f, "Kannada titles need line height >= 1.4")
        }
        assertTrue(kn.eyebrow.fontSize.value >= 11f)
    }

    @Test
    fun navInactiveMeetsContrastByUsingMutedText() {
        assertEquals(LightLittleStoryColors.textSecondary, LightLittleStoryColors.navInactive)
    }
}
