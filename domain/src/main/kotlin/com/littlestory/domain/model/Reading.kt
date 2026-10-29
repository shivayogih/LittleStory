package com.littlestory.domain.model

import java.time.DayOfWeek
import java.time.LocalTime

/** Saved flags per child and story. */
data class SavedStory(val childId: String, val storyId: String, val favourite: Boolean, val downloaded: Boolean)

data class ReadingProgress(
    val childId: String,
    val storyId: String,
    val page: Int,
    val listenFraction: Float,
    val updatedAtEpochMs: Long,
)

/** Sleep timer choices; [EndOfStory] stops when the story ends. */
sealed interface SleepTimer {
    data object Off : SleepTimer

    data class Minutes(val value: Int) : SleepTimer

    data object EndOfStory : SleepTimer
}

data class PlaybackState(
    val storyId: String,
    val playing: Boolean,
    val positionFraction: Float,
    val speed: Float,
    val volumePercent: Int,
    val sleepTimer: SleepTimer,
)

enum class DownloadStatus { Queued, Downloading, Completed }

data class Download(val storyId: String, val sizeBytes: Long, val status: DownloadStatus)

data class ReadingPreferences(val textScale: Float, val playbackSpeed: Float)

/** Bedtime reminder settings. Shown as UI only in the MVP (decision D13): nothing is scheduled. */
data class ReminderSettings(val enabled: Boolean, val time: LocalTime, val days: Set<DayOfWeek>)

data class ParentSettings(
    val reminder: ReminderSettings,
    val notificationsEnabled: Boolean,
    val wifiOnlyDownloads: Boolean,
)

data class NotificationItem(val id: String, val title: String, val body: String, val read: Boolean)

/** A subscription plan shown as illustrative pricing; prices are minor units with an ISO currency code. */
data class Plan(val id: String, val priceMinorUnits: Long, val currencyCode: String, val billedYearly: Boolean)
