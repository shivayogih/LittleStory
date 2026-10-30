package com.littlestory.core.ui.illustration

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.semantics.clearAndSetSemantics
import com.littlestory.core.designsystem.theme.LittleStoryColors
import com.littlestory.core.designsystem.theme.LittleStoryTheme

// Scene proportions (fractions of the canvas), from the onboarding screenshots.
private const val MOON_X = 0.78f
private const val MOON_Y = 0.2f
private const val MOON_RADIUS = 0.11f
private const val GLOW_RADIUS_FACTOR = 2.2f
private const val GLOW_ALPHA = 0.28f
private const val CLOUD_ALPHA = 0.14f
private const val STAR_RADIUS = 0.028f
private const val CHILD_X = 0.5f
private const val CHILD_BASE = 0.96f
private const val CHILD_UNIT = 0.5f

/**
 * Onboarding night scene: glowing crescent, two translucent clouds, three twinkling stars and a
 * child reading an open book. Decorative only. Stars hold still under reduced motion.
 */
@Composable
fun NightScene(modifier: Modifier = Modifier) {
    val colors = LittleStoryTheme.colors
    val motion = LittleStoryTheme.motion
    val transition = rememberInfiniteTransition(label = "twinkle")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = if (motion.reduced) 0f else 1f,
        animationSpec = infiniteRepeatable(tween(motion.twinkleOnboardingMillis), RepeatMode.Reverse),
        label = "twinkleProgress",
    )
    val alpha = 1f - progress * (1f - motion.twinkleMinAlpha)
    val starScale = 1f - progress * (1f - motion.twinkleMinScale)
    val rotation = progress * motion.twinkleRotationDegrees
    Canvas(modifier.fillMaxSize().clipToBounds().clearAndSetSemantics { }) {
        drawRect(colors.night)
        drawGlowingMoon(colors)
        drawCloud(Offset(size.width * 0.12f, size.height * 0.34f), size.width * 0.3f)
        drawCloud(Offset(size.width * 0.58f, size.height * 0.5f), size.width * 0.26f)
        val starCenters =
            listOf(
                Offset(size.width * 0.2f, size.height * 0.14f),
                Offset(size.width * 0.52f, size.height * 0.3f),
                Offset(size.width * 0.88f, size.height * 0.46f),
            )
        starCenters.forEach { center ->
            rotate(rotation, pivot = center) {
                scale(starScale, pivot = center) {
                    drawStarShape(colors.highlight.copy(alpha = alpha), center, size.minDimension * STAR_RADIUS)
                }
            }
        }
        drawChildReading(colors)
    }
}

private fun DrawScope.drawGlowingMoon(colors: LittleStoryColors) {
    val center = Offset(size.width * MOON_X, size.height * MOON_Y)
    val r = size.width * MOON_RADIUS
    drawCircle(
        Brush.radialGradient(
            listOf(colors.highlight.copy(alpha = GLOW_ALPHA), Color.Transparent),
            center,
            r * GLOW_RADIUS_FACTOR,
        ),
        r * GLOW_RADIUS_FACTOR,
        center,
    )
    val disc = Path().apply { addOval(Rect(center, r)) }
    val bite = Path().apply { addOval(Rect(center + Offset(r * 0.45f, -r * 0.2f), r * 0.85f)) }
    drawPath(Path.combine(PathOperation.Difference, disc, bite), colors.moon)
}

private fun DrawScope.drawCloud(topLeft: Offset, width: Float) {
    val color = Color.White.copy(alpha = CLOUD_ALPHA)
    drawRoundRect(color, topLeft, Size(width, width * 0.22f), CornerRadius(width * 0.11f))
    drawCircle(color, width * 0.14f, topLeft + Offset(width * 0.35f, 0f))
}

private fun DrawScope.drawStarShape(color: Color, center: Offset, radius: Float) {
    val inner = radius * 0.3f
    val path =
        Path().apply {
            moveTo(center.x, center.y - radius)
            quadraticTo(center.x + inner, center.y - inner, center.x + radius, center.y)
            quadraticTo(center.x + inner, center.y + inner, center.x, center.y + radius)
            quadraticTo(center.x - inner, center.y + inner, center.x - radius, center.y)
            quadraticTo(center.x - inner, center.y - inner, center.x, center.y - radius)
            close()
        }
    drawPath(path, color)
}

private fun DrawScope.drawChildReading(colors: LittleStoryColors) {
    val art = colors.illustration
    val u = size.minDimension * CHILD_UNIT
    val cx = size.width * CHILD_X
    val base = size.height * CHILD_BASE
    // body in the purple top, head, hair, and an open cream book held in front
    drawRoundRect(
        art.childTop,
        Offset(cx - u * 0.3f, base - u * 0.62f),
        Size(u * 0.6f, u * 0.62f),
        CornerRadius(
            u * 0.2f,
        ),
    )
    drawCircle(art.skin, u * 0.17f, Offset(cx, base - u * 0.8f))
    drawArc(art.hair, 180f, 180f, true, Offset(cx - u * 0.17f, base - u * 0.97f), Size(u * 0.34f, u * 0.2f))
    drawRoundRect(
        colors.background,
        Offset(cx - u * 0.36f, base - u * 0.46f),
        Size(u * 0.34f, u * 0.26f),
        CornerRadius(
            u * 0.03f,
        ),
    )
    drawRoundRect(
        colors.background,
        Offset(cx + u * 0.02f, base - u * 0.46f),
        Size(u * 0.34f, u * 0.26f),
        CornerRadius(
            u * 0.03f,
        ),
    )
}
