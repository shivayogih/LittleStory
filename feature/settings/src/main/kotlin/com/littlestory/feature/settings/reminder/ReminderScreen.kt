package com.littlestory.feature.settings.reminder

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.littlestory.core.common.config.AppConfig
import com.littlestory.core.designsystem.component.ButtonVariant
import com.littlestory.core.designsystem.component.LittleStoryButton
import com.littlestory.core.designsystem.component.LittleStoryToast
import com.littlestory.core.designsystem.component.LittleStoryToggle
import com.littlestory.core.designsystem.icon.LittleStoryIcon
import com.littlestory.core.designsystem.icon.LittleStoryIcons
import com.littlestory.core.designsystem.theme.LittleStoryTheme
import com.littlestory.core.ui.chrome.StickyActionBar
import com.littlestory.core.ui.chrome.TopBar
import com.littlestory.core.ui.system.SystemBarIcons
import com.littlestory.feature.settings.R
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.Locale
import com.littlestory.core.ui.R as UiR

object ReminderTags {
    const val TOGGLE = "reminder_toggle"
    const val TIME = "reminder_time"
    const val SAVE = "reminder_save"
    const val TOAST = "reminder_toast"

    fun day(day: DayOfWeek) = "reminder_day_${day.name}"
}

private const val DAYS_IN_WEEK = 7L

data class ReminderCallbacks(
    val onBack: () -> Unit = {},
    val onEnabledChange: (Boolean) -> Unit = {},
    val onTimeClick: () -> Unit = {},
    val onTimeDismiss: () -> Unit = {},
    val onTimeSelected: (Int, Int) -> Unit = { _, _ -> },
    val onDayToggled: (DayOfWeek) -> Unit = {},
    val onSave: () -> Unit = {},
    val onToastShown: () -> Unit = {},
)

@Composable
fun ReminderRoute(onBack: () -> Unit, viewModel: ReminderViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ReminderScreen(
        state,
        ReminderCallbacks(
            onBack = onBack,
            onEnabledChange = viewModel::onEnabledChange,
            onTimeClick = viewModel::onTimeClick,
            onTimeDismiss = viewModel::onTimeDismiss,
            onTimeSelected = viewModel::onTimeSelected,
            onDayToggled = viewModel::onDayToggled,
            onSave = viewModel::onSave,
            onToastShown = viewModel::onToastShown,
        ),
    )
}

@Composable
private fun locale(): Locale = LocalLocale.current.platformLocale

@Composable
private fun formatTime(
    time: LocalTime,
): String = time.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(locale()))

@Composable
fun ReminderScreen(state: ReminderUiState, callbacks: ReminderCallbacks, modifier: Modifier = Modifier) {
    val colors = LittleStoryTheme.colors
    val spacing = LittleStoryTheme.spacing
    SystemBarIcons(darkIcons = true)
    Box(modifier.fillMaxSize().background(colors.background)) {
        Column(Modifier.fillMaxSize()) {
            TopBar(
                title = stringResource(R.string.reminder_title),
                backContentDescription = stringResource(UiR.string.action_back),
                onBack = callbacks.onBack,
            )
            Column(
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = spacing.screenHorizontal, vertical = spacing.md),
                verticalArrangement = Arrangement.spacedBy(spacing.xl),
            ) {
                Panel()
                SettingsCard(state, callbacks)
            }
            StickyActionBar(
                primaryLabel = stringResource(R.string.reminder_save),
                onPrimary = callbacks.onSave,
                modifier = Modifier.testTag(ReminderTags.SAVE),
            )
        }
        state.savedTime?.let { time ->
            LittleStoryToast(
                message = stringResource(R.string.reminder_saved, formatTime(time)),
                durationMillis = AppConfig.Ui.TOAST_DURATION_MS,
                onTimeout = callbacks.onToastShown,
                modifier =
                    Modifier
                        .align(
                            Alignment.BottomCenter,
                        ).padding(bottom = LittleStoryTheme.dimens.bottomNavHeight)
                        .testTag(ReminderTags.TOAST),
            )
        }
    }
    if (state.timePickerOpen) TimeDialog(state.time, callbacks)
}

@Composable
private fun Panel() {
    val colors = LittleStoryTheme.colors
    val spacing = LittleStoryTheme.spacing
    Column(
        Modifier
            .fillMaxWidth()
            .background(colors.primary, LittleStoryTheme.shapes.large)
            .padding(spacing.xl),
        verticalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
            LittleStoryIcon(LittleStoryIcons.Moon, contentDescription = null, tint = colors.highlight)
            LittleStoryIcon(LittleStoryIcons.Bell, contentDescription = null, tint = colors.highlight)
        }
        Text(
            stringResource(R.string.reminder_panel_title),
            style = LittleStoryTheme.typography.homeTitle,
            color = colors.onPrimary,
        )
        Text(
            stringResource(R.string.reminder_panel_body),
            style = LittleStoryTheme.typography.body,
            color = colors.secondaryContainer,
        )
    }
}

@Composable
private fun SettingsCard(state: ReminderUiState, callbacks: ReminderCallbacks) {
    val colors = LittleStoryTheme.colors
    val spacing = LittleStoryTheme.spacing
    val dimens = LittleStoryTheme.dimens
    Column(
        Modifier
            .fillMaxWidth()
            .background(colors.surface, LittleStoryTheme.shapes.large)
            .padding(spacing.lg),
        verticalArrangement = Arrangement.spacedBy(spacing.lg),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    stringResource(R.string.reminder_toggle),
                    style = LittleStoryTheme.typography.bodyLarge,
                    color = colors.textPrimary,
                )
                Text(
                    stringResource(R.string.reminder_toggle_hint),
                    style = LittleStoryTheme.typography.chip,
                    color = colors.textSecondary,
                )
            }
            LittleStoryToggle(
                checked = state.enabled,
                onCheckedChange = callbacks.onEnabledChange,
                label = stringResource(R.string.reminder_toggle),
                modifier = Modifier.testTag(ReminderTags.TOGGLE),
            )
        }
        val contentAlpha = if (state.enabled) 1f else dimens.disabledAlpha
        Column(Modifier.alpha(contentAlpha), verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
            Text(
                stringResource(R.string.reminder_time),
                style = LittleStoryTheme.typography.chip,
                color = colors.textSecondary,
            )
            Box(
                Modifier
                    .fillMaxWidth()
                    .heightIn(min = dimens.inputHeight)
                    .clip(LittleStoryTheme.shapes.small)
                    .border(dimens.borderWidth, colors.outline, LittleStoryTheme.shapes.small)
                    .clickable(enabled = state.enabled, role = Role.Button, onClick = callbacks.onTimeClick)
                    .padding(horizontal = spacing.md)
                    .testTag(ReminderTags.TIME),
                contentAlignment = Alignment.CenterStart,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        formatTime(state.time),
                        style = LittleStoryTheme.typography.bodyLarge,
                        color = colors.textPrimary,
                        modifier = Modifier.weight(1f),
                    )
                    LittleStoryIcon(LittleStoryIcons.Clock, contentDescription = null, tint = colors.textSecondary)
                }
            }
            Text(
                stringResource(R.string.reminder_repeat),
                style = LittleStoryTheme.typography.chip,
                color = colors.textSecondary,
            )
            DayPicker(state, callbacks.onDayToggled)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.md), verticalAlignment = Alignment.CenterVertically) {
            LittleStoryIcon(LittleStoryIcons.Moon, contentDescription = null, tint = colors.success)
            Column {
                Text(
                    stringResource(R.string.reminder_quiet_title),
                    style = LittleStoryTheme.typography.bodyLarge,
                    color = colors.textPrimary,
                )
                Text(
                    stringResource(R.string.reminder_quiet_body),
                    style = LittleStoryTheme.typography.chip,
                    color = colors.textSecondary,
                )
            }
        }
    }
}

@Composable
private fun DayPicker(state: ReminderUiState, onToggle: (DayOfWeek) -> Unit) {
    val colors = LittleStoryTheme.colors
    val dimens = LittleStoryTheme.dimens
    val locale = locale()
    val first = WeekFields.of(locale).firstDayOfWeek
    val days = (0 until DAYS_IN_WEEK).map { first.plus(it) }
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        days.forEach { day ->
            val selected = day in state.days
            val fullName = day.getDisplayName(TextStyle.FULL, locale)
            Box(
                Modifier
                    .size(dimens.minTouchTarget)
                    .toggleable(
                        value = selected,
                        enabled = state.enabled,
                        role = Role.Checkbox,
                        onValueChange = { onToggle(day) },
                    ).semantics { contentDescription = fullName }
                    .testTag(ReminderTags.day(day)),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    Modifier
                        .size(dimens.dayButtonSize)
                        .background(
                            if (selected) colors.primary else colors.surfaceMuted,
                            LittleStoryTheme.shapes.iconButton,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        day.getDisplayName(TextStyle.NARROW, locale),
                        style = LittleStoryTheme.typography.chip,
                        color = if (selected) colors.onPrimary else colors.textSecondary,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimeDialog(time: LocalTime, callbacks: ReminderCallbacks) {
    val colors = LittleStoryTheme.colors
    val spacing = LittleStoryTheme.spacing
    val picker = rememberTimePickerState(initialHour = time.hour, initialMinute = time.minute)
    Dialog(onDismissRequest = callbacks.onTimeDismiss) {
        Column(
            Modifier.background(colors.surface, LittleStoryTheme.shapes.large).padding(spacing.xl),
            verticalArrangement = Arrangement.spacedBy(spacing.md),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                stringResource(R.string.reminder_time),
                style = LittleStoryTheme.typography.topBarTitle,
                color = colors.textPrimary,
            )
            TimePicker(
                state = picker,
                colors =
                    TimePickerDefaults.colors(
                        selectorColor = colors.primary,
                        timeSelectorSelectedContainerColor = colors.secondaryContainer,
                        timeSelectorSelectedContentColor = colors.primary,
                    ),
            )
            LittleStoryButton(
                stringResource(R.string.reminder_time_done),
                { callbacks.onTimeSelected(picker.hour, picker.minute) },
            )
            LittleStoryButton(
                stringResource(R.string.profile_cancel),
                callbacks.onTimeDismiss,
                variant = ButtonVariant.Ghost,
            )
        }
    }
}
