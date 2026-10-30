package com.littlestory.data.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ChildProfileDao {
    @Query("SELECT * FROM child_profile ORDER BY position")
    fun observeAll(): Flow<List<ChildProfileEntity>>

    @Query("SELECT * FROM child_profile WHERE id = :id")
    fun observe(id: String): Flow<ChildProfileEntity?>

    @Query("SELECT COALESCE(MAX(position), 0) FROM child_profile")
    suspend fun maxPosition(): Long

    @Upsert
    suspend fun upsert(entity: ChildProfileEntity)

    @Query("DELETE FROM child_profile WHERE id = :id")
    suspend fun delete(id: String)
}

@Dao
interface SavedStoryDao {
    @Query("SELECT storyId FROM saved_story WHERE childId = :childId AND favourite = 1")
    fun favouriteIds(childId: String): Flow<List<String>>

    @Upsert
    suspend fun upsert(entity: SavedStoryEntity)

    @Query("SELECT * FROM saved_story WHERE childId = :childId AND storyId = :storyId")
    suspend fun get(childId: String, storyId: String): SavedStoryEntity?

    @Query("DELETE FROM saved_story WHERE childId = :childId")
    suspend fun deleteForChild(childId: String)
}

@Dao
interface ReadingProgressDao {
    @Query("SELECT * FROM reading_progress WHERE childId = :childId ORDER BY updatedAtEpochMs DESC")
    fun observe(childId: String): Flow<List<ReadingProgressEntity>>

    @Upsert
    suspend fun upsert(entity: ReadingProgressEntity)

    @Query("DELETE FROM reading_progress WHERE childId = :childId")
    suspend fun deleteForChild(childId: String)
}

@Dao
interface DownloadDao {
    @Query("SELECT * FROM download")
    fun observeAll(): Flow<List<DownloadEntity>>

    @Upsert
    suspend fun upsert(entity: DownloadEntity)

    @Query("DELETE FROM download WHERE storyId = :storyId")
    suspend fun delete(storyId: String)
}

@Dao
interface GeneratedStoryDao {
    @Query("SELECT * FROM generated_story WHERE childId = :childId ORDER BY createdAtEpochMs DESC")
    fun observe(childId: String): Flow<List<GeneratedStoryEntity>>

    @Upsert
    suspend fun upsert(entity: GeneratedStoryEntity)

    @Query("DELETE FROM generated_story WHERE childId = :childId")
    suspend fun deleteForChild(childId: String)
}

@Dao
interface NotificationReadDao {
    @Query("SELECT notificationId FROM notification_read")
    fun observeReadIds(): Flow<List<String>>

    @Upsert
    suspend fun upsert(entity: NotificationReadEntity)
}
