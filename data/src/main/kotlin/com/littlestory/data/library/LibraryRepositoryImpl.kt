package com.littlestory.data.library

import com.littlestory.core.common.config.AppConfig
import com.littlestory.data.db.GeneratedStoryDao
import com.littlestory.data.db.GeneratedStoryEntity
import com.littlestory.domain.model.Story
import com.littlestory.domain.model.StoryArtSpec
import com.littlestory.domain.model.StoryOrigin
import com.littlestory.domain.model.StoryPage
import com.littlestory.domain.repository.LibraryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit

private val pagesFormat = Json { ignoreUnknownKeys = true }

/** Stories made by the simulated generator, stored per child in Room (ADR-004). [clock] stamps creation time. */
class LibraryRepositoryImpl(
    private val dao: GeneratedStoryDao,
    private val clock: () -> Long,
) : LibraryRepository {
    override fun created(childId: String): Flow<List<Story>> =
        dao.observe(childId).map { rows -> rows.map { it.toDomain() } }

    override fun story(id: String): Flow<Story?> = dao.observeById(id).map { it?.toDomain() }

    override suspend fun save(childId: String, story: Story) {
        dao.upsert(story.toEntity(childId, clock()))
    }
}

internal fun Story.toEntity(childId: String, createdAtEpochMs: Long) =
    GeneratedStoryEntity(
        id = id,
        childId = childId,
        title = title,
        synopsis = synopsis,
        categoryId = categoryId,
        durationMinutes = durationMinutes,
        lesson = lesson,
        paletteId = art.paletteId,
        motifId = art.motifId,
        pagesJson = pagesFormat.encodeToString(pages.map { it.text }),
        languageTag = resolvedLanguage,
        createdAtEpochMs = createdAtEpochMs,
        ageMin = ageMin,
        ageMax = ageMax,
    )

internal fun GeneratedStoryEntity.toDomain(): Story {
    val ages = AppConfig.Setup.ageRange
    val texts = runCatching { pagesFormat.decodeFromString<List<String>>(pagesJson) }.getOrDefault(emptyList())
    return Story(
        id = id,
        title = title,
        synopsis = synopsis,
        categoryId = categoryId,
        ageMin = if (ageMin > 0) ageMin else ages.first,
        ageMax = if (ageMax > 0) ageMax else ages.last,
        durationMinutes = durationMinutes,
        lesson = lesson,
        addedEpochDay = TimeUnit.MILLISECONDS.toDays(createdAtEpochMs),
        art = StoryArtSpec(paletteId, motifId),
        pages = texts.mapIndexed { index, text -> StoryPage(index, text) },
        resolvedLanguage = languageTag,
        origin = StoryOrigin.Generated,
    )
}
