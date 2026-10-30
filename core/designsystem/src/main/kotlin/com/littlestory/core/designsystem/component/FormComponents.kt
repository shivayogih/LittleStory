package com.littlestory.core.designsystem.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import com.littlestory.core.designsystem.icon.LittleStoryIcon
import com.littlestory.core.designsystem.icon.LittleStoryIcons
import com.littlestory.core.designsystem.theme.LittleStoryTheme
import com.littlestory.core.designsystem.theme.tokenShadow

/** A labelled field wrapper: 12 sp bold muted label, content, optional error line. */
@Composable
private fun FieldFrame(
    label: String?,
    error: String?,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val spacing = LittleStoryTheme.spacing
    Column(modifier, verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
        if (label != null) {
            Text(label, style = LittleStoryTheme.typography.chip, color = LittleStoryTheme.colors.textSecondary)
        }
        content()
        if (error != null) {
            Text(error, style = LittleStoryTheme.typography.chip, color = LittleStoryTheme.colors.error)
        }
    }
}

/** Input box: 48 dp minimum, 13 dp radius, lavender border and soft ring on focus. */
@Composable
private fun InputBox(
    focused: Boolean,
    hasError: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val colors = LittleStoryTheme.colors
    val dimens = LittleStoryTheme.dimens
    val shape = LittleStoryTheme.shapes.small
    val borderColor =
        when {
            hasError -> colors.error
            focused -> colors.accent
            else -> colors.outline
        }
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .then(
                    if (focused) {
                        Modifier.border(dimens.focusRingWidth, colors.secondaryContainer, shape)
                    } else {
                        Modifier
                    },
                ).padding(dimens.focusRingWidth) // reserved always so focus does not shift the layout
                .background(colors.surface, shape)
                .border(BorderStroke(dimens.borderWidth, borderColor), shape)
                .heightIn(min = dimens.inputHeight)
                .padding(horizontal = LittleStoryTheme.spacing.md),
        contentAlignment = Alignment.CenterStart,
    ) { content() }
}

@Composable
fun LittleStoryTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    error: String? = null,
    singleLine: Boolean = true,
    imeAction: ImeAction = ImeAction.Default,
    /** Applied to the editable text node itself (for tests and accessibility actions). */
    inputModifier: Modifier = Modifier,
) {
    val colors = LittleStoryTheme.colors
    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()
    FieldFrame(label, error, modifier) {
        InputBox(focused, error != null) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = singleLine,
                interactionSource = interactionSource,
                textStyle = LittleStoryTheme.typography.bodyLarge.copy(color = colors.textPrimary),
                cursorBrush = SolidColor(colors.primary),
                keyboardOptions = KeyboardOptions(imeAction = imeAction),
                modifier = inputModifier.fillMaxWidth(),
                decorationBox = { inner ->
                    if (value.isEmpty() && placeholder != null) {
                        Text(placeholder, style = LittleStoryTheme.typography.bodyLarge, color = colors.textSecondary)
                    }
                    inner()
                },
            )
        }
    }
}

@Composable
fun LittleStorySearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    clearContentDescription: String,
    modifier: Modifier = Modifier,
) {
    val colors = LittleStoryTheme.colors
    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()
    InputBox(focused, hasError = false, modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(LittleStoryTheme.spacing.sm),
        ) {
            LittleStoryIcon(LittleStoryIcons.Search, contentDescription = null, tint = colors.textSecondary)
            Box(Modifier.weight(1f)) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    interactionSource = interactionSource,
                    textStyle = LittleStoryTheme.typography.bodyLarge.copy(color = colors.textPrimary),
                    cursorBrush = SolidColor(colors.primary),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { inner ->
                        if (value.isEmpty()) {
                            Text(
                                placeholder,
                                style = LittleStoryTheme.typography.bodyLarge,
                                color = colors.textSecondary,
                            )
                        }
                        inner()
                    },
                )
            }
            if (value.isNotEmpty()) {
                LittleStoryIconButton(
                    icon = LittleStoryIcons.Close,
                    contentDescription = clearContentDescription,
                    onClick = { onValueChange("") },
                )
            }
        }
    }
}

/** Dropdown select; shows [selected] and opens a menu of [options]. */
@Composable
fun LittleStorySelect(
    selected: String,
    options: List<String>,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
) {
    val colors = LittleStoryTheme.colors
    var expanded by remember { mutableStateOf(false) }
    FieldFrame(label, error = null, modifier = modifier) {
        Box {
            InputBox(
                focused = expanded,
                hasError = false,
                modifier =
                    Modifier.clickable(role = Role.DropdownList) {
                        expanded =
                            true
                    },
            ) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        selected,
                        style = LittleStoryTheme.typography.bodyLarge,
                        color = colors.textPrimary,
                        modifier = Modifier.weight(1f),
                    )
                    LittleStoryIcon(LittleStoryIcons.Chevron, contentDescription = null, tint = colors.textSecondary)
                }
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                containerColor = colors.surface,
            ) {
                options.forEachIndexed { index, option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                option,
                                style = LittleStoryTheme.typography.bodyLarge,
                                color = colors.textPrimary,
                            )
                        },
                        onClick = {
                            expanded = false
                            onSelect(index)
                        },
                    )
                }
            }
        }
    }
}

/**
 * Pill chip. [selected] chips get the soft-lavender fill, lavender border and a check icon.
 * Pass [multiSelect] for checkbox semantics, otherwise it is a radio-style single choice.
 */
@Composable
fun LittleStoryChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    multiSelect: Boolean = false,
) {
    val colors = LittleStoryTheme.colors
    val dimens = LittleStoryTheme.dimens
    val shape = LittleStoryTheme.shapes.chip
    Row(
        modifier =
            modifier
                .heightIn(min = dimens.minTouchTarget)
                .clip(shape)
                .background(if (selected) colors.secondaryContainer else colors.surface)
                .border(dimens.borderWidth, if (selected) colors.accent else colors.outline, shape)
                .then(
                    if (multiSelect) {
                        Modifier.toggleable(value = selected, role = Role.Checkbox, onValueChange = { onClick() })
                    } else {
                        Modifier.selectable(selected = selected, role = Role.RadioButton, onClick = onClick)
                    },
                ).padding(horizontal = LittleStoryTheme.spacing.md, vertical = LittleStoryTheme.spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(LittleStoryTheme.spacing.xs),
    ) {
        if (selected) {
            LittleStoryIcon(
                LittleStoryIcons.Check,
                contentDescription = null,
                tint = colors.onSecondaryContainer,
                modifier = Modifier.size(dimens.iconSizeSmall),
            )
        }
        Text(
            text,
            style = LittleStoryTheme.typography.chip,
            color = if (selected) colors.onSecondaryContainer else colors.textPrimary,
        )
    }
}

/** Equal-width segments on the muted track; the active one is white with a soft shadow. */
@Composable
fun LittleStorySegmentedControl(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LittleStoryTheme.colors
    val dimens = LittleStoryTheme.dimens
    val trackShape = LittleStoryTheme.shapes.small
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .background(colors.surfaceMuted, trackShape)
                .padding(dimens.segmentedInset),
        horizontalArrangement = Arrangement.spacedBy(dimens.segmentedInset),
    ) {
        options.forEachIndexed { index, option ->
            val active = index == selectedIndex
            Box(
                modifier =
                    Modifier
                        .weight(1f)
                        .heightIn(min = dimens.inputHeight - dimens.segmentedInset)
                        .then(if (active) Modifier.tokenShadowSoft(trackShape) else Modifier)
                        .clip(trackShape)
                        .background(if (active) colors.surface else colors.surfaceMuted)
                        .selectable(selected = active, role = Role.RadioButton, onClick = { onSelect(index) })
                        .padding(horizontal = LittleStoryTheme.spacing.xs, vertical = LittleStoryTheme.spacing.xs),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    option,
                    style = LittleStoryTheme.typography.chip,
                    color = if (active) colors.primary else colors.textSecondary,
                )
            }
        }
    }
}

@Composable
private fun Modifier.tokenShadowSoft(shape: androidx.compose.ui.graphics.Shape): Modifier =
    tokenShadow(LittleStoryTheme.elevation.soft, shape)

/** 43 x 25 switch with a 48 dp touch target. [label] is read by TalkBack. */
@Composable
fun LittleStoryToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colors = LittleStoryTheme.colors
    val dimens = LittleStoryTheme.dimens
    val motion = LittleStoryTheme.motion
    val travel = dimens.toggleWidth - dimens.toggleKnob - dimens.toggleKnobInset * 2
    val knobOffset by animateDpAsState(
        targetValue = if (checked) travel else Dp.Hairline,
        animationSpec = tween(motion.duration(motion.toggleMillis)),
        label = "toggleKnob",
    )
    Box(
        modifier =
            modifier
                .size(dimens.minTouchTarget)
                .toggleable(value = checked, enabled = enabled, role = Role.Switch, onValueChange = onCheckedChange)
                .semantics { contentDescription = label },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .size(dimens.toggleWidth, dimens.toggleHeight)
                .clip(LittleStoryTheme.shapes.chip)
                .background(if (checked) colors.success else colors.outline),
            contentAlignment = Alignment.CenterStart,
        ) {
            Box(
                Modifier
                    .padding(start = dimens.toggleKnobInset)
                    .offset(x = knobOffset)
                    .size(dimens.toggleKnob)
                    .background(colors.surface, LittleStoryTheme.shapes.iconButton),
            )
        }
    }
}

/** Horizontally scrolling pill tabs (My Stories). */
@Composable
fun LittleStoryTabs(
    tabs: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    tabTag: (index: Int) -> String = { "tab_$it" },
) {
    val colors = LittleStoryTheme.colors
    val dimens = LittleStoryTheme.dimens
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(LittleStoryTheme.spacing.sm),
    ) {
        tabs.forEachIndexed { index, tab ->
            val active = index == selectedIndex
            Box(
                Modifier
                    .heightIn(min = dimens.minTouchTarget)
                    .clip(LittleStoryTheme.shapes.chip)
                    .background(if (active) colors.primary else colors.surface)
                    .border(
                        dimens.borderWidth,
                        if (active) colors.primary else colors.outline,
                        LittleStoryTheme.shapes.chip,
                    ).selectable(selected = active, role = Role.Tab, onClick = { onSelect(index) })
                    .testTag(tabTag(index))
                    .padding(horizontal = LittleStoryTheme.spacing.lg, vertical = LittleStoryTheme.spacing.xs),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    tab,
                    style = LittleStoryTheme.typography.chip,
                    color = if (active) colors.onPrimary else colors.textPrimary,
                )
            }
        }
    }
}
