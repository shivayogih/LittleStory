package com.littlestory.core.designsystem.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.DialogProperties
import com.littlestory.core.designsystem.icon.LittleStoryIcon
import com.littlestory.core.designsystem.icon.LittleStoryIcons
import com.littlestory.core.designsystem.theme.LittleStoryTheme
import com.littlestory.core.designsystem.theme.tokenShadow
import kotlinx.coroutines.delay

/** One tappable row of a [LittleStoryBottomSheet]. */
data class SheetOption(val label: String, val selected: Boolean = false, val onClick: () -> Unit)

/**
 * Modal bottom sheet: handle, option rows with hairlines, a ghost Cancel button and a dimmed
 * scrim. Dismisses on scrim tap and the system back gesture; focus stays inside while it is open.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LittleStoryBottomSheet(
    title: String?,
    options: List<SheetOption>,
    cancelLabel: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
) {
    val colors = LittleStoryTheme.colors
    val dimens = LittleStoryTheme.dimens
    val spacing = LittleStoryTheme.spacing
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier,
        shape = LittleStoryTheme.shapes.sheet,
        containerColor = colors.surface,
        scrimColor = colors.night.copy(alpha = dimens.scrimAlpha),
        dragHandle = {
            Box(
                Modifier
                    .padding(top = spacing.sm, bottom = spacing.md)
                    .size(dimens.sheetHandleWidth, dimens.sheetHandleHeight)
                    .background(colors.outline, LittleStoryTheme.shapes.chip),
            )
        },
    ) {
        Column(Modifier.padding(horizontal = spacing.screenHorizontal).padding(bottom = spacing.xl)) {
            if (title != null) {
                Text(title, style = LittleStoryTheme.typography.topBarTitle, color = colors.textPrimary)
            }
            if (subtitle != null) {
                Text(subtitle, style = LittleStoryTheme.typography.body, color = colors.textSecondary)
            }
            options.forEach { option ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .heightIn(min = dimens.minTouchTarget)
                        .clickable(role = Role.Button, onClick = option.onClick)
                        .padding(vertical = spacing.md),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        option.label,
                        style = LittleStoryTheme.typography.bodyLarge,
                        color = if (option.selected) colors.primary else colors.textPrimary,
                        modifier = Modifier.weight(1f),
                    )
                    if (option.selected) {
                        LittleStoryIcon(LittleStoryIcons.Check, contentDescription = null, tint = colors.primary)
                    }
                }
                Box(Modifier.fillMaxWidth().height(dimens.borderWidth).background(colors.outline))
            }
            LittleStoryButton(
                text = cancelLabel,
                onClick = onDismiss,
                variant = ButtonVariant.Ghost,
                modifier = Modifier.padding(top = spacing.sm),
            )
        }
    }
}

/** Centred card with a green check disc, title, text and a confirm button. Dismisses on back. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LittleStoryDialog(
    title: String,
    text: String,
    confirmLabel: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LittleStoryTheme.colors
    val dimens = LittleStoryTheme.dimens
    val spacing = LittleStoryTheme.spacing
    BasicAlertDialog(onDismissRequest = onDismiss, modifier = modifier, properties = DialogProperties()) {
        Column(
            Modifier
                .tokenShadow(LittleStoryTheme.elevation.card, LittleStoryTheme.shapes.large)
                .background(colors.surface, LittleStoryTheme.shapes.large)
                .padding(spacing.xxl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing.md),
        ) {
            Box(
                Modifier
                    .size(
                        dimens.dialogIconSize,
                    ).background(colors.successContainer, LittleStoryTheme.shapes.iconButton),
                contentAlignment = Alignment.Center,
            ) { LittleStoryIcon(LittleStoryIcons.Check, contentDescription = null, tint = colors.success) }
            Text(
                title,
                style = LittleStoryTheme.typography.topBarTitle,
                color = colors.textPrimary,
                textAlign = TextAlign.Center,
            )
            Text(
                text,
                style = LittleStoryTheme.typography.bodyLarge,
                color = colors.textSecondary,
                textAlign = TextAlign.Center,
            )
            LittleStoryButton(text = confirmLabel, onClick = onDismiss)
        }
    }
}

/**
 * Two-choice confirmation for an action that cannot be undone, such as deleting local data.
 * Cancel is the safe default: back and a scrim tap both dismiss.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LittleStoryConfirmDialog(
    title: String,
    text: String,
    confirmLabel: String,
    dismissLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LittleStoryTheme.colors
    val spacing = LittleStoryTheme.spacing
    BasicAlertDialog(onDismissRequest = onDismiss, modifier = modifier, properties = DialogProperties()) {
        Column(
            Modifier
                .tokenShadow(LittleStoryTheme.elevation.card, LittleStoryTheme.shapes.large)
                .background(colors.surface, LittleStoryTheme.shapes.large)
                .padding(spacing.xxl),
            verticalArrangement = Arrangement.spacedBy(spacing.md),
        ) {
            Text(title, style = LittleStoryTheme.typography.topBarTitle, color = colors.textPrimary)
            Text(text, style = LittleStoryTheme.typography.bodyLarge, color = colors.textSecondary)
            LittleStoryButton(text = confirmLabel, onClick = onConfirm, modifier = Modifier.fillMaxWidth())
            LittleStoryButton(
                text = dismissLabel,
                onClick = onDismiss,
                variant = ButtonVariant.Ghost,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

/** Pill with a check, e.g. "Reminder saved for 8:30 PM". Calls [onTimeout] after [durationMillis]. */
@Composable
fun LittleStoryToast(
    message: String,
    durationMillis: Long,
    onTimeout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LittleStoryTheme.colors
    LaunchedEffect(message, durationMillis) {
        delay(durationMillis)
        onTimeout()
    }
    Row(
        modifier
            .tokenShadow(LittleStoryTheme.elevation.soft, LittleStoryTheme.shapes.chip)
            .background(colors.night, LittleStoryTheme.shapes.chip)
            .padding(horizontal = LittleStoryTheme.spacing.lg, vertical = LittleStoryTheme.spacing.md)
            .semantics(mergeDescendants = true) { },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(LittleStoryTheme.spacing.sm),
    ) {
        LittleStoryIcon(LittleStoryIcons.Check, contentDescription = null, tint = colors.highlight)
        Text(message, style = LittleStoryTheme.typography.chip, color = colors.onNight)
    }
}

@Composable
fun LittleStoryEmptyState(
    @DrawableRes icon: Int,
    title: String,
    text: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: () -> Unit = {},
) {
    val colors = LittleStoryTheme.colors
    val spacing = LittleStoryTheme.spacing
    Column(
        modifier.fillMaxWidth().padding(spacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        Box(
            Modifier
                .size(
                    LittleStoryTheme.dimens.emptyStateIconSize,
                ).background(colors.secondaryContainer, LittleStoryTheme.shapes.iconButton),
            contentAlignment = Alignment.Center,
        ) { LittleStoryIcon(icon, contentDescription = null, tint = colors.primary) }
        Text(
            title,
            style = LittleStoryTheme.typography.topBarTitle,
            color = colors.textPrimary,
            textAlign = TextAlign.Center,
        )
        Text(
            text,
            style = LittleStoryTheme.typography.bodyLarge,
            color = colors.textSecondary,
            textAlign = TextAlign.Center,
        )
        if (actionLabel != null) {
            LittleStoryButton(actionLabel, onAction, variant = ButtonVariant.Secondary)
        }
    }
}

/** Green-tint trust or privacy note with a leading icon. */
@Composable
fun LittleStoryNotesBanner(
    text: String,
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int = LittleStoryIcons.Check,
) {
    val colors = LittleStoryTheme.colors
    Row(
        modifier
            .fillMaxWidth()
            .background(colors.successContainer, LittleStoryTheme.shapes.medium)
            .padding(LittleStoryTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(LittleStoryTheme.spacing.sm),
    ) {
        LittleStoryIcon(icon, contentDescription = null, tint = colors.success)
        Text(text, style = LittleStoryTheme.typography.chip, color = colors.textPrimary, modifier = Modifier.weight(1f))
    }
}

private fun Modifier.progressSemantics(progress: Float) =
    semantics { progressBarRangeInfo = ProgressBarRangeInfo(progress.coerceIn(0f, 1f), 0f..1f) }

/** 3 dp lavender reading-progress bar. [progress] is 0..1. */
@Composable
fun LittleStoryReadingProgress(progress: Float, modifier: Modifier = Modifier) {
    val colors = LittleStoryTheme.colors
    val shape = LittleStoryTheme.shapes.chip
    Box(
        modifier
            .fillMaxWidth()
            .height(LittleStoryTheme.dimens.readingProgressHeight)
            .clip(shape)
            .background(colors.outline)
            .progressSemantics(progress),
    ) {
        Box(
            Modifier
                .fillMaxWidth(
                    progress.coerceIn(0f, 1f),
                ).heightIn(min = LittleStoryTheme.dimens.readingProgressHeight)
                .background(colors.accent),
        )
    }
}

/** Rounded generation bar with the lavender-to-yellow gradient. */
@Composable
fun LittleStoryGenerationProgress(progress: Float, modifier: Modifier = Modifier) {
    val colors = LittleStoryTheme.colors
    val shape = LittleStoryTheme.shapes.chip
    val height = LittleStoryTheme.dimens.progressBarHeight
    Box(
        modifier
            .fillMaxWidth()
            .heightIn(
                min = height,
            ).clip(shape)
            .background(colors.nightSurface)
            .progressSemantics(progress),
    ) {
        Box(
            Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .heightIn(min = height)
                .background(Brush.horizontalGradient(listOf(colors.accent, colors.highlight))),
        )
    }
}

/** Storage-use bar: indigo track with a yellow fill. */
@Composable
fun LittleStoryStorageBar(fraction: Float, modifier: Modifier = Modifier) {
    val colors = LittleStoryTheme.colors
    val shape = LittleStoryTheme.shapes.chip
    val height = LittleStoryTheme.dimens.progressBarHeight
    Box(
        modifier
            .fillMaxWidth()
            .heightIn(
                min = height,
            ).clip(shape)
            .background(colors.primary)
            .progressSemantics(fraction),
    ) {
        Box(Modifier.fillMaxWidth(fraction.coerceIn(0f, 1f)).heightIn(min = height).background(colors.highlight))
    }
}

/** Playback position slider. [onValueChange] reports 0..1. */
@Composable
fun LittleStoryPlaybackSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    val colors = LittleStoryTheme.colors
    Slider(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.semantics { this.contentDescription = contentDescription },
        colors =
            SliderDefaults.colors(
                thumbColor = colors.highlight,
                activeTrackColor = colors.accent,
                inactiveTrackColor = colors.nightSurface,
            ),
    )
}
