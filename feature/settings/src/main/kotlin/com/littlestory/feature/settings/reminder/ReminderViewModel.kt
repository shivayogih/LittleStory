package com.littlestory.feature.settings.reminder

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlestory.core.common.config.AppConfig
import com.littlestory.domain.model.ReminderSettings
import com.littlestory.domain.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalTime
import javax.inject.Inject

@Immutable
data class ReminderUiState(
    val loading: Boolean = true,
    val enabled: Boolean = false,
    val time: LocalTime = LocalTime.of(AppConfig.Reminder.DEFAULT_HOUR, AppConfig.Reminder.DEFAULT_MINUTE),
    val days: Set<DayOfWeek> = DayOfWeek.entries.toSet(),
    val timePickerOpen: Boolean = false,
    /** The time just saved; the screen shows "Reminder saved for …" while it is set. */
    val savedTime: LocalTime? = null,
)

/**
 * SCR-16 (LS-111): bedtime reminder settings, kept on the device. Nothing is scheduled in the MVP
 * (decision D13, ADR-015): no notification permission is requested.
 */
@HiltViewModel
class ReminderViewModel
    @Inject
    constructor(
        private val preferences: PreferencesRepository,
    ) : ViewModel() {
        private val _state = MutableStateFlow(ReminderUiState())
        val state: StateFlow<ReminderUiState> = _state.asStateFlow()

        init {
            viewModelScope.launch {
                val reminder = preferences.parentSettings.first().reminder
                _state.update {
                    it.copy(loading = false, enabled = reminder.enabled, time = reminder.time, days = reminder.days)
                }
            }
        }

        fun onEnabledChange(enabled: Boolean) = _state.update { it.copy(enabled = enabled) }

        fun onTimeClick() = _state.update { it.copy(timePickerOpen = true) }

        fun onTimeDismiss() = _state.update { it.copy(timePickerOpen = false) }

        fun onTimeSelected(hour: Int, minute: Int) =
            _state.update {
                it.copy(time = LocalTime.of(hour, minute), timePickerOpen = false)
            }

        fun onDayToggled(day: DayOfWeek) =
            _state.update { it.copy(days = if (day in it.days) it.days - day else it.days + day) }

        fun onSave() {
            val current = _state.value
            viewModelScope.launch {
                val settings = preferences.parentSettings.first()
                preferences.setParentSettings(
                    settings.copy(reminder = ReminderSettings(current.enabled, current.time, current.days)),
                )
                _state.update { it.copy(savedTime = current.time) }
            }
        }

        fun onToastShown() = _state.update { it.copy(savedTime = null) }
    }
