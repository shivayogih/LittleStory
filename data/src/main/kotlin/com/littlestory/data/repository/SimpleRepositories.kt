package com.littlestory.data.repository

import com.littlestory.data.db.DownloadDao
import com.littlestory.data.db.DownloadEntity
import com.littlestory.data.db.ReadingProgressDao
import com.littlestory.data.db.ReadingProgressEntity
import com.littlestory.data.db.SavedStoryDao
import com.littlestory.data.db.SavedStoryEntity
import com.littlestory.domain.model.Download
import com.littlestory.domain.model.DownloadStatus
import com.littlestory.domain.model.ReadingProgress
import com.littlestory.domain.repository.DownloadsRepository
import com.littlestory.domain.repository.FavouritesRepository
import com.littlestory.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavouritesRepositoryImpl(private val dao: SavedStoryDao) : FavouritesRepository {
    override fun favouriteIds(childId: String): Flow<Set<String>> = dao.favouriteIds(childId).map { it.toSet() }

    override suspend fun setFavourite(childId: String, storyId: String, favourite: Boolean) {
        val current = dao.get(childId, storyId)
        dao.upsert(SavedStoryEntity(childId, storyId, favourite, current?.downloaded ?: false))
    }
}

class ProgressRepositoryImpl(private val dao: ReadingProgressDao) : ProgressRepository {
    override fun progress(childId: String): Flow<List<ReadingProgress>> =
        dao.observe(childId).map { rows ->
            rows.map { ReadingProgress(it.childId, it.storyId, it.page, it.listenFraction, it.updatedAtEpochMs) }
        }

    override suspend fun update(progress: ReadingProgress) {
        dao.upsert(
            ReadingProgressEntity(
                progress.childId,
                progress.storyId,
                progress.page,
                progress.listenFraction,
                progress.updatedAtEpochMs,
            ),
        )
    }
}

/** Downloads are simulated: a row records the story and a fixed size, nothing is fetched. */
class DownloadsRepositoryImpl(
    private val dao: DownloadDao,
    private val simulatedSizeBytes: (storyId: String) -> Long,
) : DownloadsRepository {
    override val downloads: Flow<List<Download>> =
        dao.observeAll().map { rows ->
            rows.map { Download(it.storyId, it.sizeBytes, DownloadStatus.valueOf(it.status)) }
        }

    override suspend fun start(storyId: String) {
        dao.upsert(DownloadEntity(storyId, simulatedSizeBytes(storyId), DownloadStatus.Completed.name))
    }

    override suspend fun remove(storyId: String) = dao.delete(storyId)
}
