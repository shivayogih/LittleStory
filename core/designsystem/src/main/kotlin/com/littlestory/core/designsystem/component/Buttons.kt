package com.littlestory.core.designsystem.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import com.littlestory.core.designsystem.icon.LittleStoryIcon
import com.littlestory.core.designsystem.theme.LittleStoryTheme
import com.littlestory.core.designsystem.theme.tokenShadow

enum class ButtonVariant { Primary, Secondary, Ghost }

/**
 * Full-width button (docs/design/LittleStory_Figma_Design_Details.md §2). The design's 52 dp is a
 * minimum height: long Kannada labels wrap and the button grows.
 */
@Composable
fun LittleStoryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.Primary,
    enabled: Boolean = true,
    @DrawableRes leadingIcon: Int? = null,
) {
    val colors = LittleStoryTheme.colors
    val shape = LittleStoryTheme.shapes.button
    val spacing = LittleStoryTheme.spacing
    val interactionSource = remember { MutableInteractionSource() }
    val container =
        when (variant) {
            ButtonVariant.Primary -> colors.primary
            ButtonVariant.Secondary -> colors.secondaryContainer
            ButtonVariant.Ghost -> Color.Transparent
        }
    val content =
        when (variant) {
            ButtonVariant.Primary -> colors.onPrimary
            ButtonVariant.Secondary -> colors.onSecondaryContainer
            ButtonVariant.Ghost -> colors.primary
        }
    val base =
        modifier
            .fillMaxWidth()
            .pressScale(interactionSource)
            .alpha(if (enabled) 1f else LittleStoryTheme.dimens.disabledAlpha)
            .let {
                if (variant ==
                    ButtonVariant.Primary
                ) {
                    it.tokenShadow(LittleStoryTheme.elevation.primaryButton, shape)
                } else {
                    it
                }
            }.clip(shape)
            .background(container)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            ).heightIn(min = LittleStoryTheme.dimens.buttonMinHeight)
            .padding(horizontal = spacing.lg, vertical = spacing.sm)
    Row(
        modifier = base,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leadingIcon != null) {
            LittleStoryIcon(leadingIcon, contentDescription = null, tint = content)
            Spacer(Modifier.width(spacing.sm))
        }
        Text(
            text = text,
            style = LittleStoryTheme.typography.button,
            color = content,
            textAlign = TextAlign.Center,
        )
    }
}

/**
 * Circular icon button: 42 dp visual inside a 48 dp touch target. [contentDescription] is
 * required. [active] draws the favourite style (rose on pink).
 */
@Composable
fun LittleStoryIconButton(
    @DrawableRes icon: Int,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    active: Boolean = false,
) {
    val colors = LittleStoryTheme.colors
    val dimens = LittleStoryTheme.dimens
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier =
            modifier
                .size(dimens.minTouchTarget)
                .alpha(if (enabled) 1f else dimens.disabledAlpha)
                .pressScale(interactionSource)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = enabled,
                    role = Role.Button,
                    onClick = onClick,
                ),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier =
                Modifier
                    .size(dimens.iconButtonVisual)
                    .clip(LittleStoryTheme.shapes.iconButton)
                    .background(
                        if (active) {
                            colors.favouriteContainer
                        } else {
                            colors.surface.copy(
                                alpha = ICON_BUTTON_SURFACE_ALPHA,
                            )
                        },
                    ),
            contentAlignment = Alignment.Center,
        ) {
            LittleStoryIcon(
                icon = icon,
                contentDescription = contentDescription,
                tint = if (active) colors.favourite else colors.textPrimary,
            )
        }
    }
}

// The prototype draws icon buttons as white at about 76 % opacity.
private const val ICON_BUTTON_SURFACE_ALPHA = 0.76f
