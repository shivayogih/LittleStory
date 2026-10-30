package com.littlestory.domain.browse

import com.littlestory.core.common.config.AppConfig
import com.littlestory.domain.model.ChildProfile
import com.littlestory.domain.model.Story
import com.littlestory.domain.model.StoryCategory
import com.littlestory.domain.repository.CatalogRepository
import com.littlestory.domain.repository.PreferencesRepository
import com.littlestory.domain.repository.ProfilesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest

/**
 * Who is browsing and in which content language. [childId] falls back to a guest id when Setup was
 * skipped, so favourites and progress still work without a profile.
 */
data class BrowseContext(val child: ChildProfile?, val languageTag: String) {
    val childId: String get() = child?.id ?: AppConfig.Profiles.GUEST_ID
}

fun observeBrowseContext(profiles: ProfilesRepository, preferences: PreferencesRepository): Flow<BrowseContext> =
    combine(profiles.activeProfile, preferences.contentLanguageTag) { child, tag -> BrowseContext(child, tag) }
        .distinctUntilChanged()

/** The whole catalogue in one language. */
data class Catalogue(val stories: List<Story>, val categories: List<StoryCategory>) {
    val categoryNames: Map<String, String> by lazy { categories.associate { it.id to it.name } }

    fun category(id: String): StoryCategory? = categories.firstOrNull { it.id == id }

    fun storiesIn(categoryId: String): List<Story> = stories.filter { it.belongsTo(categoryId) }
}

fun CatalogRepository.catalogue(languageTag: String): Flow<Catalogue> =
    combine(stories(languageTag), categories(languageTag), ::Catalogue)

/** Re-runs [block] whenever the browsing context changes. */
@OptIn(ExperimentalCoroutinesApi::class)
fun <T> Flow<BrowseContext>.flatMapContext(block: (BrowseContext) -> Flow<T>): Flow<T> = flatMapLatest(block)
