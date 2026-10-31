package com.littlestory.data.generation

import com.littlestory.core.common.config.AppConfig
import com.littlestory.core.common.language.SupportedLanguages
import com.littlestory.data.catalog.MockContentSource
import com.littlestory.domain.generation.StoryDraft
import com.littlestory.domain.generation.StoryWriter
import com.littlestory.domain.generation.StoryWriterResult
import com.littlestory.domain.generation.simulatedGenerationProgress
import com.littlestory.domain.model.GenerationError
import com.littlestory.domain.model.GenerationState
import com.littlestory.domain.model.Story
import com.littlestory.domain.model.StoryArtSpec
import com.littlestory.domain.model.StoryCreationRequest
import com.littlestory.domain.model.StoryOrigin
import com.littlestory.domain.model.StoryPage
import com.littlestory.domain.repository.GenerationRepository
import com.littlestory.domain.repository.LibraryRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.takeWhile
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap

private const val CHILD_TOKEN = "{child}"
private val FALLBACK = SupportedLanguages.default.tag
private val lenientJson = Json { ignoreUnknownKeys = true }

/** `mock/generated_{tag}.json`: one localized story template; `{child}` is replaced with the hero's name. */
@Serializable
internal data class GeneratedTemplate(
    val defaultHero: String = "",
    val title: String = "",
    val synopsis: String = "",
    val lesson: String = "",
    val pages: List<String> = emptyList(),
)

/**
 * Simulated generation (LS-082, ADR-014): deterministic progress, then a localized template filled
 * with the hero's name and saved to the library. Pending requests live in memory only; nothing
 * the parent typed is stored or sent anywhere.
 *
 * When a [writer] is bound (debug-only Gemini prototype), it writes the story while the progress
 * runs up to [AppConfig.Generation.WRITER_WAIT_PERCENT]; an unavailable writer falls back to the
 * simulation, and a rejected or failed one ends in [GenerationState.Failed] (the paused screen).
 */
class GenerationRepositoryImpl(
    private val source: MockContentSource,
    private val library: LibraryRepository,
    private val writer: StoryWriter? = null,
) : GenerationRepository {
    private val pending = ConcurrentHashMap<String, StoryCreationRequest>()

    override fun submit(request: StoryCreationRequest) {
        pending[request.requestId] = request
    }

    override fun request(requestId: String): StoryCreationRequest? = pending[requestId]

    override fun generate(requestId: String, startPercent: Int): Flow<GenerationState> {
        val request = pending[requestId]
        return when {
            request == null -> flow { emit(GenerationState.Failed(requestId, GenerationError.Unknown)) }
            writer == null -> simulated(request, startPercent)
            else -> written(request, writer, startPercent)
        }
    }

    private fun written(
        request: StoryCreationRequest,
        storyWriter: StoryWriter,
        startPercent: Int,
    ): Flow<GenerationState> {
        val requestId = request.requestId
        return channelFlow {
            val written = async { storyWriter.write(request, generatedStoryId(requestId)) }
            simulatedGenerationProgress(requestId, startPercent)
                .takeWhile { it.percent <= AppConfig.Generation.WRITER_WAIT_PERCENT }
                .collect { send(it) }
            val story =
                when (val result = written.await()) {
                    is StoryWriterResult.Written -> storyFrom(request, result.draft)
                    StoryWriterResult.Unavailable -> buildStory(request)
                    StoryWriterResult.Rejected, StoryWriterResult.Failed -> null
                }
            if (story == null) {
                send(GenerationState.Failed(requestId, GenerationError.Unknown))
            } else {
                library.save(request.heroChildId, story)
                send(GenerationState.Completed(requestId, story.id))
            }
        }
    }

    private fun simulated(request: StoryCreationRequest, startPercent: Int): Flow<GenerationState> =
        flow {
            simulatedGenerationProgress(request.requestId, startPercent).collect { emit(it) }
            val story = buildStory(request)
            library.save(request.heroChildId, story)
            emit(GenerationState.Completed(request.requestId, story.id))
        }

    private fun storyFrom(request: StoryCreationRequest, draft: StoryDraft): Story =
        buildStory(request).copy(
            title = draft.title,
            synopsis = draft.synopsis,
            lesson = draft.lesson,
            pages = draft.pages.mapIndexed { index, text -> StoryPage(index, text) },
            resolvedLanguage = draft.languageTag,
            art =
                StoryArtSpec(
                    draft.paletteId ?: AppConfig.Generation.COVER_PALETTE_ID,
                    AppConfig.Generation.COVER_MOTIF_ID,
                ),
        )

    private fun buildStory(request: StoryCreationRequest): Story {
        val language = request.languageTag.substringBefore('-').lowercase()
        val base = template(FALLBACK) ?: GeneratedTemplate()
        val local = if (language == FALLBACK) null else template(language)
        val translated = local?.title?.isNotBlank() == true
        val chosen = if (translated) local else base
        val hero = request.heroName.trim().ifEmpty { chosen.defaultHero }
        fun String.fill() = replace(CHILD_TOKEN, hero)
        val categoryId = AppConfig.Generation.themeCategoryIds[request.themeId] ?: request.themeId
        val ages = AppConfig.Setup.ageRange
        return Story(
            id = generatedStoryId(request.requestId),
            title = chosen.title.fill(),
            synopsis = chosen.synopsis.fill(),
            categoryId = categoryId,
            ageMin = (request.heroAge - AppConfig.Generation.HERO_AGES_BELOW).coerceIn(ages),
            ageMax = (request.heroAge + AppConfig.Generation.HERO_AGES_ABOVE).coerceIn(ages),
            durationMinutes = request.durationMinutes,
            lesson = chosen.lesson.fill(),
            addedEpochDay = 0,
            art = StoryArtSpec(AppConfig.Generation.COVER_PALETTE_ID, AppConfig.Generation.COVER_MOTIF_ID),
            pages = chosen.pages.mapIndexed { index, text -> StoryPage(index, text.fill()) },
            resolvedLanguage = if (translated) language else FALLBACK,
            origin = StoryOrigin.Generated,
        )
    }

    private fun template(language: String): GeneratedTemplate? =
        source.read("generated_$language.json")?.let { text ->
            runCatching { lenientJson.decodeFromString<GeneratedTemplate>(text) }.getOrNull()
        }

    companion object {
        /** One story per request, so retrying a request never creates a duplicate. */
        fun generatedStoryId(requestId: String) = "generated_$requestId"
    }
}
