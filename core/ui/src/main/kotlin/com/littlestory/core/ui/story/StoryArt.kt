package com.littlestory.core.ui.story

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.semantics.clearAndSetSemantics
import com.littlestory.core.designsystem.theme.GradientPair
import com.littlestory.core.designsystem.theme.LittleStoryColors
import com.littlestory.core.designsystem.theme.LittleStoryTheme

/** The figure or prop drawn on a story cover; chosen by story data, never by text. */
enum class StoryArtMotif { Moon, Elephant, Cricket, Forest, Space, Festival }

/** Story categories that have a cover gradient (design-tokens.json `storyArt`). */
enum class StoryArtCategory { Friendship, Adventure, Bedtime, IndianTales, Space, Festivals }

fun LittleStoryColors.gradientFor(category: StoryArtCategory): GradientPair = when (category) {
    StoryArtCategory.Friendship -> storyArt.friendship
    StoryArtCategory.Adventure -> storyArt.adventure
    StoryArtCategory.Bedtime -> storyArt.bedtime
    StoryArtCategory.IndianTales -> storyArt.indianTales
    StoryArtCategory.Space -> storyArt.space
    StoryArtCategory.Festivals -> storyArt.festivals
}

// Proportions of the CSS cover art, as fractions of the canvas.
private const val HILL_WIDTH = 1.4f
private const val HILL_HEIGHT = 0.5f
private const val HILL_TOP = 0.68f
private const val HILL_ROTATION_DEGREES = -5f
private const val MOON_RADIUS = 0.11f
private const val MOON_BITE_OFFSET = 0.05f
private const val STAR_RADIUS = 0.04f
private const val DOT_RADIUS = 0.012f
private const val FIGURE_X = 0.5f
private const val HEAD_RADIUS = 0.075f

/**
 * Story cover drawn from tokens: a 145° gradient, a crescent moon, a star and dot, a dark hill
 * and a [motif] figure. Decorative: it has no semantics, the card around it carries the text.
 */
@Composable
fun StoryArt(
    gradient: GradientPair,
    motif: StoryArtMotif,
    modifier: Modifier = Modifier,
) {
    val colors = LittleStoryTheme.colors
    val hillAlpha = LittleStoryTheme.dimens.storyHillAlpha
    Canvas(modifier.fillMaxSize().clearAndSetSemantics { }) {
        // 145° CSS angle: top-left to bottom-right, slightly steeper than the diagonal.
        drawRect(Brush.linearGradient(listOf(gradient.start, gradient.end), start = Offset(0f, 0f), end = Offset(size.width, size.height)))
        drawCrescent(colors.moon)
        drawStar(Color.White, Offset(size.width * 0.78f, size.height * 0.22f), size.minDimension * STAR_RADIUS)
        drawCircle(Color.White, size.minDimension * DOT_RADIUS, Offset(size.width * 0.2f, size.height * 0.35f))
        drawHill(colors.night.copy(alpha = hillAlpha))
        drawMotif(motif, colors)
    }
}

private fun DrawScope.drawCrescent(fill: Color) {
    val r = size.minDimension * MOON_RADIUS
    val center = Offset(size.width * 0.24f, size.height * 0.2f)
    val disc = Path().apply { addOval(Rect(center, r)) }
    val cut = Path().apply { addOval(Rect(center + Offset(size.minDimension * MOON_BITE_OFFSET, -size.minDimension * MOON_BITE_OFFSET * 0.4f), r)) }
    drawPath(Path.combine(PathOperation.Difference, disc, cut), fill)

}

internal fun DrawScope.drawStar(color: Color, center: Offset, radius: Float) {
    val inner = radius * 0.3f
    val path = Path().apply {
        moveTo(center.x, center.y - radius)
        quadraticTo(center.x + inner, center.y - inner, center.x + radius, center.y)
        quadraticTo(center.x + inner, center.y + inner, center.x, center.y + radius)
        quadraticTo(center.x - inner, center.y + inner, center.x - radius, center.y)
        quadraticTo(center.x - inner, center.y - inner, center.x, center.y - radius)
        close()
    }
    drawPath(path, color)
}

private fun DrawScope.drawHill(color: Color) {
    val w = size.width * HILL_WIDTH
    val h = size.height * HILL_HEIGHT
    val topLeft = Offset((size.width - w) / 2f, size.height * HILL_TOP)
    rotate(HILL_ROTATION_DEGREES, pivot = Offset(size.width / 2f, size.height)) {
        drawOval(color, topLeft, Size(w, h))
    }
}

private fun DrawScope.drawMotif(motif: StoryArtMotif, colors: LittleStoryColors) {
    val art = colors.illustration
    val shirt = when (motif) {
        StoryArtMotif.Moon -> art.childTop
        StoryArtMotif.Elephant -> art.childTop
        StoryArtMotif.Cricket -> art.shirtGold
        StoryArtMotif.Forest -> colors.storyArt.indianTales.start
        StoryArtMotif.Space -> colors.storyArt.space.end
        StoryArtMotif.Festival -> art.shirtGold
    }
    val u = size.minDimension
    val cx = size.width * FIGURE_X
    val baseY = size.height * 0.88f
    if (motif == StoryArtMotif.Elephant) {
        // grey-violet body, trunk and two legs
        drawOval(art.elephant, Offset(cx - u * 0.26f, baseY - u * 0.4f), Size(u * 0.5f, u * 0.32f))
        drawCircle(art.elephantLight, u * 0.1f, Offset(cx - u * 0.26f, baseY - u * 0.28f))
        drawRect(art.elephantLeg, Offset(cx - u * 0.18f, baseY - u * 0.14f), Size(u * 0.07f, u * 0.14f))
        drawRect(art.elephantLeg, Offset(cx + u * 0.06f, baseY - u * 0.14f), Size(u * 0.07f, u * 0.14f))
        drawRect(art.elephant, Offset(cx - u * 0.36f, baseY - u * 0.26f), Size(u * 0.05f, u * 0.2f))
        return
    }
    // shirt body, face, hair band
    drawRoundRect(shirt, Offset(cx - u * 0.1f, baseY - u * 0.3f), Size(u * 0.2f, u * 0.3f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(u * 0.06f))
    val head = Offset(cx, baseY - u * 0.38f)
    drawCircle(art.skin, u * HEAD_RADIUS, head)
    drawArc(art.hair, 180f, 180f, true, Offset(head.x - u * HEAD_RADIUS, head.y - u * HEAD_RADIUS), Size(u * HEAD_RADIUS * 2, u * HEAD_RADIUS * 1.1f))
    if (motif == StoryArtMotif.Cricket) {
        drawRect(art.cricketBat, Offset(cx + u * 0.12f, baseY - u * 0.34f), Size(u * 0.05f, u * 0.3f))
    }
    if (motif == StoryArtMotif.Moon) {
        drawStar(colors.highlight, Offset(cx + u * 0.16f, baseY - u * 0.5f), u * STAR_RADIUS)
    }
}
