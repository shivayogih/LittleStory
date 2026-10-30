package com.littlestory.data.generation

import com.littlestory.core.common.config.AppConfig
import com.littlestory.core.common.language.SupportedLanguages
import com.littlestory.data.catalog.MockContentSource
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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
 */
class GenerationRepositoryImpl(
    private val source: MockContentSource,
    private val library: LibraryRepository,
) : GenerationRepository {
    private val pending = ConcurrentHashMap<String, StoryCreationRequest>()

    override fun submit(request: StoryCreationRequest) {
        pending[request.requestId] = request
    }

    override fun request(requestId: String): StoryCreationRequest? = pending[requestId]

    override fun generate(requestId: String, startPercent: Int): Flow<GenerationState> =
        flow {
            val request = pending[requestId]
            if (request == null) {
                emit(GenerationState.Failed(requestId, GenerationError.Unknown))
                return@flow
            }
            simulatedGenerationProgress(requestId, startPercent).collect { emit(it) }
            val story = buildStory(request)
            library.save(request.heroChildId, story)
            emit(GenerationState.Completed(requestId, story.id))
        }

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
