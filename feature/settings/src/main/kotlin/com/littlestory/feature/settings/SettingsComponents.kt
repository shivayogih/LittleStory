package com.littlestory.feature.settings

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import com.littlestory.core.designsystem.component.LittleStoryToggle
import com.littlestory.core.designsystem.icon.LittleStoryIcon
import com.littlestory.core.designsystem.icon.LittleStoryIcons
import com.littlestory.core.designsystem.theme.LittleStoryTheme

/** A titled white card of settings rows separated by hairlines (SCR-13). */
@Composable
internal fun SettingsGroup(title: String, modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    val colors = LittleStoryTheme.colors
    val spacing = LittleStoryTheme.spacing
    Column(modifier, verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
        Text(
            title,
            style = LittleStoryTheme.typography.eyebrow,
            color = colors.textSecondary,
            modifier = Modifier.padding(start = spacing.xs),
        )
        Column(
            Modifier
                .fillMaxWidth()
                .clip(LittleStoryTheme.shapes.large)
                .background(colors.surface),
            content = content,
        )
    }
}

@Composable
internal fun SettingsDivider() {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = LittleStoryTheme.spacing.lg)
            .height(LittleStoryTheme.dimens.borderWidth)
            .background(LittleStoryTheme.colors.outline),
    )
}

/**
 * One settings row: icon tile, title, live subtitle. The row body opens its screen when [onClick]
 * is set; a toggle is a separate target beside it, so nothing interactive is nested (LS-110).
 */
@Composable
internal fun SettingsRow(
    @DrawableRes icon: Int,
    title: String,
    subtitle: String,
    tag: String,
    onClick: (() -> Unit)? = null,
    toggle: Boolean? = null,
    onToggle: (Boolean) -> Unit = {},
) {
    val colors = LittleStoryTheme.colors
    val spacing = LittleStoryTheme.spacing
    val dimens = LittleStoryTheme.dimens
    Row(Modifier.fillMaxWidth().padding(end = spacing.sm), verticalAlignment = Alignment.CenterVertically) {
        Row(
            Modifier
                .weight(1f)
                .heightIn(min = dimens.minTouchTarget)
                .then(if (onClick != null) Modifier.clickable(role = Role.Button, onClick = onClick) else Modifier)
                .semantics(mergeDescendants = true) { }
                .padding(spacing.lg)
                .testTag(tag),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.md),
        ) {
            Box(
                Modifier
                    .size(
                        dimens.settingsIconTile,
                    ).background(colors.secondaryContainer, LittleStoryTheme.shapes.small),
                contentAlignment = Alignment.Center,
            ) { LittleStoryIcon(icon, contentDescription = null, tint = colors.primary) }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(spacing.xxs)) {
                Text(title, style = LittleStoryTheme.typography.bodyLarge, color = colors.textPrimary)
                Text(subtitle, style = LittleStoryTheme.typography.chip, color = colors.textSecondary)
            }
            if (onClick != null && toggle == null) {
                LittleStoryIcon(LittleStoryIcons.Chevron, contentDescription = null, tint = colors.textPrimary)
            }
        }
        if (toggle != null) {
            LittleStoryToggle(
                checked = toggle,
                onCheckedChange = onToggle,
                label = title,
                modifier = Modifier.testTag("${tag}_toggle"),
            )
        }
    }
}
