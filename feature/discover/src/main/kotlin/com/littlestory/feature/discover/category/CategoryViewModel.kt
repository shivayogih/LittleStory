package com.littlestory.feature.discover.category

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
import com.littlestory.domain.repository.DownloadsRepository
import com.littlestory.domain.repository.FavouritesRepository
import com.littlestory.domain.repository.PreferencesRepository
import com.littlestory.domain.repository.ProfilesRepository
import com.littlestory.feature.discover.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val SUBSCRIPTION_TIMEOUT_MS = 5_000L
private const val KEY_CATEGORY = "categoryId"
private const val KEY_SORT = "sort"

@Immutable
data class SortOptionUi(val id: String, val label: UiText)

@Immutable
data class CategoryUiState(
    val title: UiText = UiText.Dynamic(""),
    val description: UiText = UiText.Dynamic(""),
    val storyCount: Int = 0,
    val sortOptions: List<SortOptionUi> = emptyList(),
    val selectedSort: String = AppConfig.Discover.DEFAULT_SORT_ID,
    val stories: List<StoryItem> = emptyList(),
    /** True until the catalogue first arrives, so "No stories" never flashes while loading. */
    val loading: Boolean = false,
)

@HiltViewModel
class CategoryViewModel
    @Inject
    constructor(
        private val savedState: SavedStateHandle,
        profiles: ProfilesRepository,
        preferences: PreferencesRepository,
        catalog: CatalogRepository,
        downloads: DownloadsRepository,
        private val favourites: FavouritesRepository,
    ) : ViewModel() {
        private val browse = observeBrowseContext(profiles, preferences)

        // Route arguments are stored by name (type-safe routes), like the Setup screen's child id.
        private val categoryId: String = savedState.get<String>(KEY_CATEGORY) ?: AppConfig.Discover.ALL_ID
        private val sort = savedState.getStateFlow(KEY_SORT, AppConfig.Discover.DEFAULT_SORT_ID)

        val state: StateFlow<CategoryUiState> =
            combine(
                browse.flatMapContext { ctx ->
                    combine(
                        catalog.catalogue(ctx.languageTag),
                        favourites.favouriteIds(ctx.childId),
                        downloads.downloads.map { rows -> rows.map { it.storyId }.toSet() },
                    ) { c, f, d -> Triple(c, f, d) }
                },
                sort,
            ) { (catalogue, favouriteIds, downloadedIds), sortId ->
                build(catalogue, favouriteIds, downloadedIds, sortId)
            }.stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(SUBSCRIPTION_TIMEOUT_MS),
                CategoryUiState(loading = true),
            )

        fun onSortSelected(id: String) {
            savedState[KEY_SORT] = id
        }

        fun onFavouriteClick(storyId: String) {
            viewModelScope.launch {
                val childId = browse.first().childId
                favourites.toggle(childId, storyId)
            }
        }

        private fun build(
            catalogue: Catalogue,
            favouriteIds: Set<String>,
            downloadedIds: Set<String>,
            sortId: String,
        ): CategoryUiState {
            val isAll = categoryId == AppConfig.Discover.ALL_ID
            val category = catalogue.category(categoryId)
            val members = if (isAll) catalogue.stories else catalogue.storiesIn(categoryId)
            return CategoryUiState(
                title = if (isAll) UiText.Res(R.string.category_all_name) else UiText.Dynamic(category?.name.orEmpty()),
                description =
                    if (isAll) {
                        UiText.Res(R.string.category_all_description)
                    } else {
                        UiText.Dynamic(category?.description.orEmpty())
                    },
                storyCount = members.size,
                sortOptions = sortOptions,
                selectedSort = sortId,
                stories =
                    sorted(members, sortId).map {
                        it.toItem(catalogue.categoryNames, favouriteIds, downloadedIds)
                    },
            )
        }

        private fun sorted(stories: List<Story>, sortId: String): List<Story> =
            when (sortId) {
                SORT_SHORTEST -> stories.sortedBy { it.durationMinutes }
                SORT_NEWEST -> stories.sortedByDescending { it.addedEpochDay }
                else -> stories
            }

        private companion object {
            const val SORT_SHORTEST = "shortest"
            const val SORT_NEWEST = "newest"

            val sortOptions =
                AppConfig.Discover.sortOptionIds.map { id ->
                    SortOptionUi(
                        id,
                        UiText.Res(
                            when (id) {
                                SORT_SHORTEST -> R.string.category_sort_shortest
                                SORT_NEWEST -> R.string.category_sort_newest
                                else -> R.string.category_sort_recommended
                            },
                        ),
                    )
                }
        }
    }
