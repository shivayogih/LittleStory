package com.littlestory.feature.library.stories

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.littlestory.core.ui.story.StoryItem
import com.littlestory.core.ui.story.toItem
import com.littlestory.domain.browse.catalogue
import com.littlestory.domain.browse.flatMapContext
import com.littlestory.domain.browse.observeBrowseContext
import com.littlestory.domain.model.ReadingProgress
import com.littlestory.domain.model.Story
import com.littlestory.domain.player.SimulatedPlayback
import com.littlestory.domain.repository.PreferencesRepository
import com.littlestory.domain.repository.ProfilesRepository
import com.littlestory.domain.repository.StoryStores
import com.littlestory.feature.library.navigation.LibraryTab
import com.littlestory.feature.library.navigation.MyStoriesRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

private const val SUBSCRIPTION_TIMEOUT_MS = 5_000L

/** The story to pick up again, with simulated seconds left. */
@Immutable
data class RecentlyPlayed(val item: StoryItem, val progress: Float, val remainingSeconds: Int)

@Immutable
data class MyStoriesUiState(
    val loading: Boolean = true,
    val query: String = "",
    val tab: LibraryTab = LibraryTab.All,
    val recentlyPlayed: RecentlyPlayed? = null,
    val stories: List<StoryItem> = emptyList(),
    /** Stories this child created; they open on Story Ready rather than the catalogue detail. */
    val createdIds: Set<String> = emptySet(),
)

/** SCR-12 (LS-100): the child's library across catalogue and created stories. */
@HiltViewModel
class MyStoriesViewModel
    @Inject
    constructor(
        savedState: SavedStateHandle,
        profiles: ProfilesRepository,
        preferences: PreferencesRepository,
        stores: StoryStores,
    ) : ViewModel() {
        private val catalog = stores.catalog
        private val library = stores.library
        private val favourites = stores.favourites
        private val downloads = stores.downloads
        private val progress = stores.progress

        private val browse = observeBrowseContext(profiles, preferences)
        private val query = MutableStateFlow("")
        private val tab = MutableStateFlow(savedState.toRoute<MyStoriesRoute>().tab)

        val state: StateFlow<MyStoriesUiState> =
            browse
                .flatMapContext { ctx ->
                    val sources =
                        combine(
                            catalog.catalogue(ctx.languageTag),
                            library.created(ctx.childId),
                            favourites.favouriteIds(ctx.childId),
                            downloads.downloads.map { rows -> rows.map { it.storyId }.toSet() },
                            progress.progress(ctx.childId),
                        ) { catalogue, created, favouriteIds, downloadedIds, rows ->
                            Sources(
                                catalogue.stories,
                                catalogue.categoryNames,
                                created,
                                favouriteIds,
                                downloadedIds,
                                rows,
                            )
                        }
                    combine(sources, query, tab) { s, q, t -> buildState(s, q, t) }
                }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(SUBSCRIPTION_TIMEOUT_MS), MyStoriesUiState())

        fun onQueryChange(value: String) {
            query.value = value
        }

        fun onTabSelected(value: LibraryTab) {
            tab.value = value
        }

        fun onFavouriteClick(storyId: String) {
            viewModelScope.launch {
                val childId = browse.first().childId
                favourites.toggle(childId, storyId)
            }
        }

        private data class Sources(
            val catalogue: List<Story>,
            val categoryNames: Map<String, String>,
            val created: List<Story>,
            val favouriteIds: Set<String>,
            val downloadedIds: Set<String>,
            val progress: List<ReadingProgress>,
        )

        private fun buildState(s: Sources, query: String, tab: LibraryTab): MyStoriesUiState {
            val all = s.created + s.catalogue
            fun Story.item() = toItem(s.categoryNames, s.favouriteIds, s.downloadedIds)
            val inTab =
                when (tab) {
                    LibraryTab.All -> all
                    LibraryTab.CreatedByMe -> s.created
                    LibraryTab.Downloaded -> all.filter { it.id in s.downloadedIds }
                    LibraryTab.Favourites -> all.filter { it.id in s.favouriteIds }
                }
            val needle = query.trim().lowercase(Locale.getDefault())
            val matching =
                if (needle.isEmpty()) {
                    inTab
                } else {
                    inTab.filter {
                        it.title.lowercase(Locale.getDefault()).contains(needle)
                    }
                }
            val recent =
                if (tab == LibraryTab.All && needle.isEmpty()) {
                    s.progress
                        .filter { it.listenFraction > 0f && it.listenFraction < 1f }
                        .sortedByDescending { it.updatedAtEpochMs }
                        .firstNotNullOfOrNull { row ->
                            all.firstOrNull { it.id == row.storyId }?.let { story ->
                                val total = SimulatedPlayback.totalSeconds(story.durationMinutes)
                                RecentlyPlayed(
                                    story.item(),
                                    row.listenFraction,
                                    (total * (1f - row.listenFraction)).toInt(),
                                )
                            }
                        }
                } else {
                    null
                }
            return MyStoriesUiState(
                loading = false,
                query = query,
                tab = tab,
                recentlyPlayed = recent,
                stories = matching.map { it.item() },
                createdIds = s.created.mapTo(mutableSetOf()) { it.id },
            )
        }
    }
