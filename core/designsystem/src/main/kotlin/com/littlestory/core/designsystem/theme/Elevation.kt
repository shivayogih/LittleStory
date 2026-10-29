package com.littlestory.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** A CSS box-shadow from design-tokens.json `shadow`: y offset, blur radius and colour (with alpha). */
@Immutable
data class ShadowToken(val offsetY: Dp, val blur: Dp, val color: Color)

// Shadow colours are the indigo/night brand colours with the alpha from the CSS (#RRGGBBAA).
private const val CARD_ALPHA = 0x1C / 255f
private const val SOFT_ALPHA = 0x14 / 255f
private const val PRIMARY_ALPHA = 0x33 / 255f
private const val SHELL_ALPHA = 0x24 / 255f
private const val CREATE_NAV_ALPHA = 0x40 / 255f

@Immutable
data class LittleStoryElevation(
    val card: ShadowToken = ShadowToken(12.dp, 30.dp, Palette.ShadowInk.copy(alpha = CARD_ALPHA)),
    val soft: ShadowToken = ShadowToken(5.dp, 18.dp, Palette.ShadowInk.copy(alpha = SOFT_ALPHA)),
    val primaryButton: ShadowToken = ShadowToken(8.dp, 18.dp, Palette.Indigo.copy(alpha = PRIMARY_ALPHA)),
    val hero: ShadowToken = ShadowToken(16.dp, 30.dp, Palette.Indigo.copy(alpha = PRIMARY_ALPHA)),
    val appShell: ShadowToken = ShadowToken(0.dp, 50.dp, Palette.Night.copy(alpha = SHELL_ALPHA)),
    val createNav: ShadowToken = ShadowToken(5.dp, 15.dp, Palette.Indigo.copy(alpha = CREATE_NAV_ALPHA)),
)

// Compose shadows take an elevation, not a CSS blur radius; half the blur is the closest visual match
// (docs/design/design-system.md §5 says to verify visually).
private const val BLUR_TO_ELEVATION = 0.5f

/** Draws a design shadow token behind a [shape]. */
fun Modifier.tokenShadow(token: ShadowToken, shape: Shape): Modifier = shadow(
    elevation = token.blur * BLUR_TO_ELEVATION,
    shape = shape,
    ambientColor = token.color,
    spotColor = token.color,
)
