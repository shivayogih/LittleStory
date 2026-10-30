package com.littlestory.core.ui.illustration

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.semantics.clearAndSetSemantics
import com.littlestory.core.designsystem.theme.LittleStoryTheme
import com.littlestory.core.ui.story.drawStar

// Proportions are fractions of the canvas, read from 05-home.jpg.
private const val HILL_CENTER_X = 0.62f
private const val HILL_CENTER_Y = 1.1f
private const val HILL_RADIUS = 0.62f
private const val MOON_CENTER_X = 0.6f
private const val MOON_CENTER_Y = 0.24f
private const val MOON_RADIUS = 0.3f
private const val CUT_OFFSET = 0.12f
private const val STAR_BIG = 0.06f
private const val STAR_SMALL = 0.03f
private const val CHILD_CENTER_X = 0.55f
private const val CHILD_BASE = 1f
// The child is sized from the canvas width so it keeps its proportions when the card grows taller.
private const val SHIRT_WIDTH = 0.72f
private const val SHIRT_HEIGHT = 0.5f
private const val HEAD_WIDTH = 0.46f
private const val HEAD_HEIGHT = 0.5f
private const val HAIR_HEIGHT = 0.2f
private const val BOOK_WIDTH = 0.6f
private const val BOOK_HEIGHT = 0.24f
private const val BOOK_RADIUS = 0.06f
private const val GATE_WIDTH = 0.8f
private const val GATE_HEIGHT = 0.6f
private const val GATE_TOWER = 0.34f
private const val GATE_ARCH = 0.3f

/** Home hero art: crescent moon, two stars and a child reading a cream book, on a lighter hill. Decorative. */
@Composable
fun HomeHeroScene(modifier: Modifier = Modifier) {
    val colors = LittleStoryTheme.colors
    val art = colors.illustration
    Canvas(modifier.clipToBounds().clearAndSetSemantics { }) {
        val w = size.width
        val h = size.height
        drawCircle(art.heroHill, radius = w * HILL_RADIUS, center = Offset(w * HILL_CENTER_X, h * HILL_CENTER_Y))
        val moonCenter = Offset(w * MOON_CENTER_X, h * MOON_CENTER_Y)
        drawCircle(colors.highlight, radius = w * MOON_RADIUS, center = moonCenter)
        drawCircle(
            colors.primary,
            radius = w * MOON_RADIUS,
            center = moonCenter - Offset(w * CUT_OFFSET, w * CUT_OFFSET),
        )
        drawStar(colors.highlight, Offset(w * MOON_CENTER_X - w * MOON_RADIUS, h * MOON_CENTER_Y), w * STAR_BIG)
        drawStar(colors.moon, Offset(w * (MOON_CENTER_X + MOON_RADIUS), h * MOON_CENTER_Y + w * MOON_RADIUS), w * STAR_SMALL)

        val cx = w * CHILD_CENTER_X
        val shirtTop = h * CHILD_BASE - w * SHIRT_HEIGHT
        drawRoundRect(
            art.heroShirt,
            Offset(cx - w * SHIRT_WIDTH / 2, shirtTop),
            Size(w * SHIRT_WIDTH, w * SHIRT_HEIGHT),
            CornerRadius(w * BOOK_RADIUS),
        )
        val headTop = shirtTop - w * HEAD_HEIGHT
        drawRoundRect(
            art.skin,
            Offset(cx - w * HEAD_WIDTH / 2, headTop),
            Size(w * HEAD_WIDTH, w * HEAD_HEIGHT),
            CornerRadius(w * HEAD_WIDTH / 2),
        )
        drawRoundRect(
            art.hair,
            Offset(cx - w * HEAD_WIDTH / 2, headTop),
            Size(w * HEAD_WIDTH, w * HAIR_HEIGHT),
            CornerRadius(w * HEAD_WIDTH / 2),
        )
        drawRoundRect(
            colors.moon,
            Offset(cx - w * BOOK_WIDTH / 2, shirtTop - w * BOOK_HEIGHT / 2),
            Size(w * BOOK_WIDTH, w * BOOK_HEIGHT),
            CornerRadius(w * BOOK_RADIUS),
        )
    }
}

/** Terracotta gateway with an arched doorway for the "Stories from every corner of India" banner. Decorative. */
@Composable
fun CultureGatewayArt(modifier: Modifier = Modifier) {
    val art = LittleStoryTheme.colors.illustration
    val doorway = LittleStoryTheme.colors.cultureBanner
    Canvas(modifier.clipToBounds().clearAndSetSemantics { }) {
        val w = size.width
        val h = size.height
        val bodyLeft = w * (1f - GATE_WIDTH) / 2
        val bodyTop = h * (1f - GATE_HEIGHT)
        drawRect(art.terracotta, Offset(bodyLeft, bodyTop), Size(w * GATE_WIDTH, h * GATE_HEIGHT))
        val towerWidth = w * GATE_TOWER
        drawRect(art.terracotta, Offset((w - towerWidth) / 2, bodyTop - h * GATE_ARCH), Size(towerWidth, h * GATE_ARCH))
        val archRadius = w * GATE_ARCH / 2
        val archCenter = Offset(w / 2, h - archRadius * 2)
        drawCircle(doorway, archRadius, archCenter)
        drawRect(doorway, Offset(archCenter.x - archRadius, archCenter.y), Size(archRadius * 2, h - archCenter.y))
    }
}
