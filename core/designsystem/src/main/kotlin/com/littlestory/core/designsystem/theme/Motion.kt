package com.littlestory.core.designsystem.theme

import androidx.compose.runtime.Immutable

/**
 * Animation durations in milliseconds from design-tokens.json `motion`. Programmatic timings
 * (generation steps, player ticks) are behaviour and live in AppConfig, not here.
 */
@Immutable
data class LittleStoryMotion(
    val twinkleOnboardingMillis: Int = 2400,
    val twinkleGeneratingMillis: Int = 1700,
    val twinkleGeneratingDelaysMillis: List<Int> = listOf(0, 400, 700),
    val twinkleSparkMillis: Int = 1100,
    val pressMillis: Int = 180,
    val pressScale: Float = 0.98f,
    val toggleMillis: Int = 200,
    val iconStateMillis: Int = 200,
    val chevronMillis: Int = 200,
    val readingProgressMillis: Int = 300,
    val readerFontSizeMillis: Int = 200,
    val twinkleMinAlpha: Float = 0.3f,
    val twinkleMinScale: Float = 0.8f,
    val twinkleRotationDegrees: Float = 12f,
    /** When true (system "remove animations"), infinite twinkles stop and transitions are instant. */
    val reduced: Boolean = false,
) {
    /** Duration to use for a finite transition, honouring reduced motion. */
    fun duration(millis: Int): Int = if (reduced) 0 else millis
}
