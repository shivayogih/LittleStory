package com.littlestory.core.designsystem.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import com.littlestory.core.designsystem.theme.LittleStoryTheme

/** Scales the element to the design's press scale while pressed; instant under reduced motion. */
@Composable
fun Modifier.pressScale(interactionSource: InteractionSource): Modifier {
    val motion = LittleStoryTheme.motion
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) motion.pressScale else 1f,
        animationSpec = tween(motion.duration(motion.pressMillis)),
        label = "pressScale",
    )
    return graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}
