package com.littlestory.core.designsystem.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/** Corner radii from design-tokens.json `radius`. */
@Immutable
data class LittleStoryShapes(
    val large: Shape = RoundedCornerShape(24.dp),
    val medium: Shape = RoundedCornerShape(18.dp),
    val small: Shape = RoundedCornerShape(13.dp),
    val button: Shape = RoundedCornerShape(16.dp),
    val hero: Shape = RoundedCornerShape(26.dp),
    val sheet: Shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
    val chip: Shape = RoundedCornerShape(20.dp),
    val avatar: Shape = RoundedCornerShape(15.dp),
    val avatarLarge: Shape = RoundedCornerShape(19.dp),
    val iconButton: Shape = CircleShape,
)
