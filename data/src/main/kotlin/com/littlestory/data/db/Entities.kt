package com.littlestory.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Child profile row. Interests are stored as a comma-separated list of stable ids. */
@Entity(tableName = "child_profile")
data class ChildProfileEntity(
    @PrimaryKey val id: String,
    val firstName: String,
    val age: Int,
    val avatarIndex: Int,
    val interestIds: String,
    val languageTag: String,
    val defaultDurationMinutes: Int,
    /** Insertion order, so the profile list is stable. */
    val position: Long,
)

@Entity(tableName = "saved_story", primaryKeys = ["childId", "storyId"])
data class SavedStoryEntity(
    val childId: String,
    val storyId: String,
    val favourite: Boolean,
    val downloaded: Boolean,
)

@Entity(tableName = "reading_progress", primaryKeys = ["childId", "storyId"])
data class ReadingProgressEntity(
    val childId: String,
    val storyId: String,
    val page: Int,
    val listenFraction: Float,
    val updatedAtEpochMs: Long,
)

@Entity(tableName = "download")
data class DownloadEntity(
    @PrimaryKey val storyId: String,
    val sizeBytes: Long,
    val status: String,
)

/** A story created by the simulated generator; pages are stored as one JSON array string. */
@Entity(tableName = "generated_story")
data class GeneratedStoryEntity(
    @PrimaryKey val id: String,
    val childId: String,
    val title: String,
    val synopsis: String,
    val categoryId: String,
    val durationMinutes: Int,
    val lesson: String,
    val paletteId: String,
    val motifId: String,
    val pagesJson: String,
    val languageTag: String,
    val createdAtEpochMs: Long,
)

@Entity(tableName = "notification_read")
data class NotificationReadEntity(
    @PrimaryKey val notificationId: String,
)
