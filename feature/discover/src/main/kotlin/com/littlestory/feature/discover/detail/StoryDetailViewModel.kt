package com.littlestory.feature.discover.detail

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlestory.core.common.language.SupportedLanguages
import com.littlestory.domain.browse.flatMapContext
import com.littlestory.domain.browse.observeBrowseContext
import com.littlestory.domain.repository.CatalogRepository
import com.littlestory.domain.repository.DownloadsRepository
import com.littlestory.domain.repository.FavouritesRepository
import com.littlestory.domain.repository.PreferencesRepository
import com.littlestory.domain.repository.ProfilesRepository
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
private const val KEY_STORY = "storyId"

@Immutable
data class StoryDetailContent(
    val id: String,
    val title: String,
    val categoryName: String,
    val ageMin: Int,
    val ageMax: Int,
    val durationMinutes: Int,
    /** Native name of the language the text is actually in (English when a translation is missing). */
    val languageName: String,
    val synopsis: String,
    val lesson: String,
    val paletteId: String,
    val motifId: String,
    val favourite: Boolean,
    val downloaded: Boolean,
)

sealed interface StoryDetailUiState {
    data object Loading : StoryDetailUiState

    data object NotFound : StoryDetailUiState

    data class Loaded(val content: StoryDetailContent) : StoryDetailUiState
}

@HiltViewModel
class StoryDetailViewModel
    @Inject
    constructor(
        savedState: SavedStateHandle,
        profiles: ProfilesRepository,
        preferences: PreferencesRepository,
        catalog: CatalogRepository,
        private val favourites: FavouritesRepository,
        private val downloads: DownloadsRepository,
    ) : ViewModel() {
        private val browse = observeBrowseContext(profiles, preferences)
        private val storyId: String = savedState.get<String>(KEY_STORY).orEmpty()

        val state: StateFlow<StoryDetailUiState> =
            browse
                .flatMapContext { ctx ->
                    combine(
                        catalog.story(storyId, ctx.languageTag),
                        catalog.categories(ctx.languageTag),
                        favourites.favouriteIds(ctx.childId),
                        downloads.downloads.map { rows -> rows.map { it.storyId }.toSet() },
                    ) { story, categories, favouriteIds, downloadedIds ->
                        if (story == null) {
                            StoryDetailUiState.NotFound
                        } else {
                            StoryDetailUiState.Loaded(
                                StoryDetailContent(
                                    id = story.id,
                                    title = story.title,
                                    categoryName = categories.firstOrNull { it.id == story.categoryId }?.name.orEmpty(),
                                    ageMin = story.ageMin,
                                    ageMax = story.ageMax,
                                    durationMinutes = story.durationMinutes,
                                    languageName = SupportedLanguages.fromTag(story.resolvedLanguage).nativeName,
                                    synopsis = story.synopsis,
                                    lesson = story.lesson,
                                    paletteId = story.art.paletteId,
                                    motifId = story.art.motifId,
                                    favourite = story.id in favouriteIds,
                                    downloaded = story.id in downloadedIds,
                                ),
                            )
                        }
                    }
                }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(SUBSCRIPTION_TIMEOUT_MS), StoryDetailUiState.Loading)

        fun onFavouriteClick() {
            viewModelScope.launch {
                val childId = browse.first().childId
                val current = favourites.favouriteIds(childId).first()
                favourites.setFavourite(childId, storyId, storyId !in current)
            }
        }

        /** Downloads are simulated: this only records that the story is "available offline". */
        fun onDownloadClick() {
            viewModelScope.launch {
                val downloaded = downloads.downloads.first().any { it.storyId == storyId }
                if (downloaded) downloads.remove(storyId) else downloads.start(storyId)
            }
        }
    }
