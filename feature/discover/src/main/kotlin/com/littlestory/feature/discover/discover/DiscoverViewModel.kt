package com.littlestory.feature.discover.discover

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlestory.core.common.config.AppConfig
import com.littlestory.core.ui.story.StoryItem
import com.littlestory.core.ui.story.toItem
import com.littlestory.core.ui.text.UiText
import com.littlestory.domain.browse.Catalogue
import com.littlestory.domain.browse.catalogue
import com.littlestory.domain.browse.flatMapContext
import com.littlestory.domain.browse.observeBrowseContext
import com.littlestory.domain.model.Story
import com.littlestory.domain.repository.CatalogRepository
import com.littlestory.domain.repository.FavouritesRepository
import com.littlestory.domain.repository.PreferencesRepository
import com.littlestory.domain.repository.ProfilesRepository
import com.littlestory.feature.discover.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

private const val SUBSCRIPTION_TIMEOUT_MS = 5_000L
private const val KEY_QUERY = "query"
private const val KEY_FILTER = "filter"

@Immutable
data class FilterChipUi(val id: String, val label: UiText)

@Immutable
data class FeaturedUi(val story: StoryItem, val synopsis: String)

@Immutable
data class DiscoverUiState(
    val query: String = "",
    val selectedFilter: String = AppConfig.Discover.ALL_ID,
    val filters: List<FilterChipUi> = emptyList(),
    /** Hidden while searching (design). */
    val featured: FeaturedUi? = null,
    val sectionTitle: UiText = UiText.Res(R.string.discover_popular),
    val results: List<StoryItem> = emptyList(),
    /** "View all" opens the Category screen for the active filter. */
    val showViewAll: Boolean = false,
    val bedtime: List<StoryItem> = emptyList(),
) {
    val isSearching: Boolean get() = query.isNotBlank()
    val isFiltered: Boolean get() = selectedFilter != AppConfig.Discover.ALL_ID
    val hasNoResults: Boolean get() = results.isEmpty() && (isSearching || isFiltered)
}

@HiltViewModel
class DiscoverViewModel
    @Inject
    constructor(
        private val savedState: SavedStateHandle,
        profiles: ProfilesRepository,
        preferences: PreferencesRepository,
        catalog: CatalogRepository,
        private val favourites: FavouritesRepository,
    ) : ViewModel() {
        private val browse = observeBrowseContext(profiles, preferences)
        private val query = savedState.getStateFlow(KEY_QUERY, "")
        private val filter = savedState.getStateFlow(KEY_FILTER, AppConfig.Discover.ALL_ID)

        val state: StateFlow<DiscoverUiState> =
            combine(
                browse.flatMapContext { ctx ->
                    combine(catalog.catalogue(ctx.languageTag), favourites.favouriteIds(ctx.childId)) { c, f ->
                        Triple(c, f, Locale.forLanguageTag(ctx.languageTag))
                    }
                },
                query,
                filter,
            ) { (catalogue, favouriteIds, locale), q, f -> build(catalogue, favouriteIds, locale, q, f) }
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(SUBSCRIPTION_TIMEOUT_MS), DiscoverUiState())

        fun onQueryChange(value: String) {
            savedState[KEY_QUERY] = value
        }

        fun onFilterSelected(id: String) {
            savedState[KEY_FILTER] = id
        }

        /** "Clear search" resets the query and the chip to All. */
        fun onClearSearch() {
            savedState[KEY_QUERY] = ""
            savedState[KEY_FILTER] = AppConfig.Discover.ALL_ID
        }

        fun onFavouriteClick(storyId: String) {
            viewModelScope.launch {
                val childId = browse.first().childId
                val current = favourites.favouriteIds(childId).first()
                favourites.setFavourite(childId, storyId, storyId !in current)
            }
        }

        private fun build(
            catalogue: Catalogue,
            favouriteIds: Set<String>,
            locale: Locale,
            rawQuery: String,
            selected: String,
        ): DiscoverUiState {
            val names = catalogue.categoryNames
            val query = rawQuery.trim()
            val filters =
                AppConfig.Discover.filterIds.mapNotNull { id ->
                    if (id == AppConfig.Discover.ALL_ID) {
                        FilterChipUi(id, UiText.Res(R.string.discover_filter_all))
                    } else {
                        names[id]?.let { FilterChipUi(id, UiText.Dynamic(it)) }
                    }
                }
            val matches =
                catalogue.stories.filter { story ->
                    (selected == AppConfig.Discover.ALL_ID || story.belongsTo(selected)) &&
                        (query.isEmpty() || story.matches(query, names, locale))
                }
            val unfiltered = selected == AppConfig.Discover.ALL_ID && query.isEmpty()
            val results = if (unfiltered) matches.take(AppConfig.Discover.POPULAR_COUNT) else matches
            val featuredStory =
                catalogue.stories.firstOrNull { it.id == AppConfig.Discover.FEATURED_STORY_ID }
                    ?: catalogue.stories.firstOrNull()
            return DiscoverUiState(
                query = rawQuery,
                selectedFilter = selected,
                filters = filters,
                featured =
                    featuredStory?.takeIf { query.isEmpty() }?.let { FeaturedUi(it.toItem(names, favouriteIds), it.synopsis) },
                sectionTitle =
                    when {
                        query.isNotEmpty() -> UiText.Res(R.string.discover_results, listOf(query))
                        !unfiltered -> UiText.Dynamic(names[selected].orEmpty())
                        else -> UiText.Res(R.string.discover_popular)
                    },
                results = results.map { it.toItem(names, favouriteIds) },
                showViewAll = query.isEmpty() && results.isNotEmpty(),
                bedtime =
                    if (query.isEmpty()) {
                        catalogue
                            .storiesIn(AppConfig.Discover.BEDTIME_CATEGORY_ID)
                            .map { it.toItem(names, favouriteIds) }
                    } else {
                        emptyList()
                    },
            )
        }
    }

/** Free text, trimmed and case-insensitive for the content locale, over title, synopsis and category name. */
private fun Story.matches(query: String, categoryNames: Map<String, String>, locale: Locale): Boolean {
    val needle = query.lowercase(locale)
    return listOf(title, synopsis, categoryNames[categoryId].orEmpty()).any { it.lowercase(locale).contains(needle) }
}
