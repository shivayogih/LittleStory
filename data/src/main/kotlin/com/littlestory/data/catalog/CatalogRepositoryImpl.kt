package com.littlestory.data.catalog

import com.littlestory.core.common.language.SupportedLanguages
import com.littlestory.domain.model.Story
import com.littlestory.domain.model.StoryArtSpec
import com.littlestory.domain.model.StoryCategory
import com.littlestory.domain.model.StoryPage
import com.littlestory.domain.repository.CatalogRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap

private val FALLBACK = SupportedLanguages.default.tag

private val lenientJson = Json { ignoreUnknownKeys = true }

/**
 * Serves the bundled mock catalogue (ADR-007). English is the base: it defines the story ids and all
 * structural fields. A translation file only overrides text, field by field, so a missing or broken
 * translation falls back to English instead of failing.
 */
class CatalogRepositoryImpl(
    private val source: MockContentSource,
    private val dispatcher: CoroutineDispatcher,
) : CatalogRepository {
    private val cache = ConcurrentHashMap<String, Catalogue>()

    override fun categories(languageTag: String): Flow<List<StoryCategory>> =
        catalogue(
            languageTag,
        ).map { it.categories }

    override fun stories(languageTag: String): Flow<List<Story>> = catalogue(languageTag).map { it.stories }

    override fun story(id: String, languageTag: String): Flow<Story?> =
        catalogue(languageTag).map { catalogue -> catalogue.stories.firstOrNull { it.id == id } }

    private fun catalogue(languageTag: String): Flow<Catalogue> =
        flow { emit(cache.getOrPut(languageOf(languageTag)) { load(languageOf(languageTag)) }) }
            .flowOn(dispatcher)

    private fun load(language: String): Catalogue {
        val base = parse<StoriesFile>("stories_$FALLBACK.json")?.stories.orEmpty()
        val translated =
            if (language == FALLBACK) {
                emptyMap()
            } else {
                parse<StoriesFile>("stories_$language.json")?.stories.orEmpty().associateBy { it.id }
            }
        val stories = base.map { resolveStory(it, translated[it.id], language) }

        val baseCategories =
            parse<CategoriesFile>("categories_$FALLBACK.json")?.categories.orEmpty()
        val translatedCategories =
            if (language == FALLBACK) {
                emptyMap()
            } else {
                parse<CategoriesFile>("categories_$language.json")?.categories.orEmpty().associateBy { it.id }
            }
        val categories =
            baseCategories.map { category ->
                val local = translatedCategories[category.id]
                StoryCategory(
                    id = category.id,
                    name = local?.name?.ifBlank { null } ?: category.name,
                    description = local?.description?.ifBlank { null } ?: category.description,
                    storyCount = stories.count { it.belongsTo(category.id) },
                )
            }
        return Catalogue(stories, categories)
    }

    private fun resolveStory(base: StoryFileEntry, local: StoryFileEntry?, language: String): Story {
        val title = local?.title?.ifBlank { null }
        val pages = local?.pages?.takeIf { it.isNotEmpty() } ?: base.pages
        return Story(
            id = base.id,
            title = title ?: base.title,
            synopsis = local?.synopsis?.ifBlank { null } ?: base.synopsis,
            categoryId = base.categoryId,
            tags = base.tags,
            ageMin = base.ageMin,
            ageMax = base.ageMax,
            durationMinutes = base.durationMinutes,
            lesson = local?.lesson?.ifBlank { null } ?: base.lesson,
            addedEpochDay = base.addedEpochDay,
            art = StoryArtSpec(base.art.paletteId, base.art.motifId),
            pages = pages.mapIndexed { index, text -> StoryPage(index, text) },
            resolvedLanguage = if (title != null) language else FALLBACK,
        )
    }

    /** A file that is missing or malformed yields null so the caller falls back to English. */
    private inline fun <reified T> parse(fileName: String): T? =
        source.read(fileName)?.let { text -> runCatching { lenientJson.decodeFromString<T>(text) }.getOrNull() }

    private fun languageOf(tag: String): String = tag.substringBefore('-').substringBefore('_').lowercase()

    private data class Catalogue(val stories: List<Story>, val categories: List<StoryCategory>)
}
