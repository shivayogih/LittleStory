package com.littlestory.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        ChildProfileEntity::class,
        SavedStoryEntity::class,
        ReadingProgressEntity::class,
        DownloadEntity::class,
        GeneratedStoryEntity::class,
        NotificationReadEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class LittleStoryDatabase : RoomDatabase() {
    abstract fun childProfileDao(): ChildProfileDao

    abstract fun savedStoryDao(): SavedStoryDao

    abstract fun readingProgressDao(): ReadingProgressDao

    abstract fun downloadDao(): DownloadDao

    abstract fun generatedStoryDao(): GeneratedStoryDao

    abstract fun notificationReadDao(): NotificationReadDao

    companion object {
        const val NAME = "littlestory.db"
    }
}
