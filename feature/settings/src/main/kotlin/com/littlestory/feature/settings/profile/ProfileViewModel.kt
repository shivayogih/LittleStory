package com.littlestory.feature.settings.profile

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlestory.core.common.config.AppConfig
import com.littlestory.core.common.config.FeatureFlags
import com.littlestory.core.common.language.SupportedLanguages
import com.littlestory.domain.model.ParentSettings
import com.littlestory.domain.repository.PreferencesRepository
import com.littlestory.domain.repository.ProfilesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject

private const val SUBSCRIPTION_TIMEOUT_MS = 5_000L

@Immutable
data class ProfileUiState(
    val activeChildId: String? = null,
    val activeChildName: String? = null,
    val avatarTone: Int? = null,
    val childCount: Int = 0,
    val contentLanguageTag: String = SupportedLanguages.default.tag,
    val reminderEnabled: Boolean = false,
    val reminderTime: LocalTime = LocalTime.of(AppConfig.Reminder.DEFAULT_HOUR, AppConfig.Reminder.DEFAULT_MINUTE),
    val reminderEveryDay: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val wifiOnlyDownloads: Boolean = true,
    val playbackSpeed: Float = AppConfig.Player.DEFAULT_SPEED,
    val subscriptionVisible: Boolean = true,
)

/** SCR-13 (LS-110): the Profile hub. Toggles save straight away; rows open their screens. */
@HiltViewModel
class ProfileViewModel
    @Inject
    constructor(
        private val profiles: ProfilesRepository,
        private val preferences: PreferencesRepository,
        flags: FeatureFlags,
    ) : ViewModel() {
        val state: StateFlow<ProfileUiState> =
            combine(
                profiles.profiles,
                profiles.activeProfile,
                preferences.contentLanguageTag,
                preferences.parentSettings,
                preferences.reading,
            ) { all, active, language, settings, reading ->
                ProfileUiState(
                    activeChildId = active?.id,
                    activeChildName = active?.firstName,
                    avatarTone = active?.avatar?.index,
                    childCount = all.size,
                    contentLanguageTag = language,
                    reminderEnabled = settings.reminder.enabled,
                    reminderTime = settings.reminder.time,
                    reminderEveryDay = settings.reminder.days.size == DAYS_IN_WEEK,
                    notificationsEnabled = settings.notificationsEnabled,
                    wifiOnlyDownloads = settings.wifiOnlyDownloads,
                    playbackSpeed = reading.playbackSpeed,
                    subscriptionVisible = flags.demoSubscription,
                )
            }.stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(SUBSCRIPTION_TIMEOUT_MS),
                ProfileUiState(subscriptionVisible = flags.demoSubscription),
            )

        fun onReminderToggled(enabled: Boolean) =
            updateSettings {
                it.copy(
                    reminder = it.reminder.copy(enabled = enabled),
                )
            }

        fun onNotificationsToggled(enabled: Boolean) = updateSettings { it.copy(notificationsEnabled = enabled) }

        fun onWifiOnlyToggled(enabled: Boolean) = updateSettings { it.copy(wifiOnlyDownloads = enabled) }

        /** Story content language; the UI language itself is switched by the screen through AppCompat. */
        fun onLanguageSelected(tag: String) {
            viewModelScope.launch { preferences.setContentLanguageTag(tag) }
        }

        private fun updateSettings(change: (ParentSettings) -> ParentSettings) {
            viewModelScope.launch { preferences.setParentSettings(change(preferences.parentSettings.first())) }
        }

        private companion object {
            const val DAYS_IN_WEEK = 7
        }
    }
