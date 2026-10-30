package com.littlestory.data.prefs

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.littlestory.core.common.config.AppConfig
import com.littlestory.core.common.language.SupportedLanguages
import com.littlestory.domain.model.ParentSettings
import com.littlestory.domain.model.ReadingPreferences
import com.littlestory.domain.model.ReminderSettings
import com.littlestory.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import java.time.LocalTime

/** App-level settings in DataStore (ADR-004). Nothing here is child-identifying. */
class PreferencesRepositoryImpl(private val store: DataStore<Preferences>) : PreferencesRepository {
    override val firstRunCompleted: Flow<Boolean> = store.data.map { it[FIRST_RUN_COMPLETED] ?: false }

    override suspend fun setFirstRunCompleted(completed: Boolean) {
        store.edit { it[FIRST_RUN_COMPLETED] = completed }
    }

    override val contentLanguageTag: Flow<String> =
        store.data.map { it[CONTENT_LANGUAGE] ?: SupportedLanguages.default.tag }

    override suspend fun setContentLanguageTag(tag: String) {
        store.edit { it[CONTENT_LANGUAGE] = tag }
    }

    override val reading: Flow<ReadingPreferences> =
        store.data.map {
            ReadingPreferences(
                textScale = it[TEXT_SCALE] ?: AppConfig.Reader.TEXT_SCALE_DEFAULT,
                playbackSpeed = it[PLAYBACK_SPEED] ?: AppConfig.Player.DEFAULT_SPEED,
            )
        }

    override suspend fun setReading(preferences: ReadingPreferences) {
        store.edit {
            it[TEXT_SCALE] = preferences.textScale
            it[PLAYBACK_SPEED] = preferences.playbackSpeed
        }
    }

    override val parentSettings: Flow<ParentSettings> =
        store.data.map { prefs ->
            ParentSettings(
                reminder =
                    ReminderSettings(
                        enabled = prefs[REMINDER_ENABLED] ?: false,
                        time =
                            LocalTime.of(
                                prefs[REMINDER_HOUR] ?: AppConfig.Reminder.DEFAULT_HOUR,
                                prefs[REMINDER_MINUTE] ?: AppConfig.Reminder.DEFAULT_MINUTE,
                            ),
                        days =
                            (
                                prefs[REMINDER_DAYS] ?: AppConfig.Reminder.defaultDaysOfWeek
                                    .map { it.toString() }
                                    .toSet()
                            ).mapNotNull { it.toIntOrNull() }
                                .map { DayOfWeek.of(it) }
                                .toSet(),
                    ),
                notificationsEnabled = prefs[NOTIFICATIONS_ENABLED] ?: true,
                wifiOnlyDownloads = prefs[WIFI_ONLY_DOWNLOADS] ?: true,
            )
        }

    override suspend fun setParentSettings(settings: ParentSettings) {
        store.edit {
            it[REMINDER_ENABLED] = settings.reminder.enabled
            it[REMINDER_HOUR] = settings.reminder.time.hour
            it[REMINDER_MINUTE] = settings.reminder.time.minute
            it[REMINDER_DAYS] =
                settings.reminder.days
                    .map { day -> day.value.toString() }
                    .toSet()
            it[NOTIFICATIONS_ENABLED] = settings.notificationsEnabled
            it[WIFI_ONLY_DOWNLOADS] = settings.wifiOnlyDownloads
        }
    }

    private companion object {
        val FIRST_RUN_COMPLETED = booleanPreferencesKey("first_run_completed")
        val CONTENT_LANGUAGE = stringPreferencesKey("content_language_tag")
        val TEXT_SCALE = floatPreferencesKey("reader_text_scale")
        val PLAYBACK_SPEED = floatPreferencesKey("playback_speed")
        val REMINDER_ENABLED = booleanPreferencesKey("reminder_enabled")
        val REMINDER_HOUR = intPreferencesKey("reminder_hour")
        val REMINDER_MINUTE = intPreferencesKey("reminder_minute")
        val REMINDER_DAYS = stringSetPreferencesKey("reminder_days")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val WIFI_ONLY_DOWNLOADS = booleanPreferencesKey("wifi_only_downloads")
    }
}
