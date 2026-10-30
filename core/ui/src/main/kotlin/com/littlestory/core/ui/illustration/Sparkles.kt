package com.littlestory.core.ui.illustration

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import com.littlestory.core.designsystem.theme.LittleStoryTheme
import com.littlestory.core.ui.story.drawStar

private const val SPARKLE_RADIUS = 0.5f
private const val SKY_STAR_RADIUS = 0.012f

// Star positions (fractions of width, height) as drawn in the Generating and Player designs.
private val skyStars =
    listOf(
        Offset(0.15f, 0.18f),
        Offset(0.82f, 0.28f),
        Offset(0.12f, 0.6f),
        Offset(0.9f, 0.68f),
        Offset(0.24f, 0.9f),
    )

/** The four-point ✦ sparkle used on Create, the reader flourish and the night screens. Decorative. */
@Composable
fun Sparkle(
    modifier: Modifier = Modifier,
    color: Color = LittleStoryTheme.colors.highlight,
) {
    Canvas(modifier.size(LittleStoryTheme.dimens.sparkleSize).clearAndSetSemantics { }) {
        drawStar(color, center, size.minDimension * SPARKLE_RADIUS)
    }
}

/**
 * Night gradient with gently twinkling yellow stars (Generating, Player, Subscription hero).
 * Stars hold still under reduced motion; [content] is drawn on top.
 */
@Composable
fun NightSky(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit = {},
) {
    val colors = LittleStoryTheme.colors
    val motion = LittleStoryTheme.motion
    val transition = rememberInfiniteTransition(label = "nightSky")
    val phases =
        motion.twinkleGeneratingDelaysMillis.mapIndexed { index, delayMillis ->
            transition.animateFloat(
                initialValue = 0f,
                targetValue = if (motion.reduced) 0f else 1f,
                animationSpec =
                    infiniteRepeatable(
                        tween(motion.twinkleGeneratingMillis),
                        RepeatMode.Reverse,
                        StartOffset(delayMillis),
                    ),
                label = "star$index",
            )
        }
    Box(modifier.background(Brush.verticalGradient(listOf(colors.night, colors.nightSurface, colors.night)))) {
        Canvas(Modifier.fillMaxSize().clearAndSetSemantics { }) {
            skyStars.forEachIndexed { index, position ->
                val phase by phases[index % phases.size]
                val alpha = 1f - phase * (1f - motion.twinkleMinAlpha)
                drawStar(
                    colors.highlight.copy(alpha = alpha),
                    Offset(size.width * position.x, size.height * position.y),
                    size.minDimension * SKY_STAR_RADIUS,
                )
            }
        }
        content()
    }
}
