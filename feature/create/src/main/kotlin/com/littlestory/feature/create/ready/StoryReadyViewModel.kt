package com.littlestory.feature.create.ready

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.littlestory.core.common.config.AppConfig
import com.littlestory.core.common.language.SupportedLanguages
import com.littlestory.domain.browse.flatMapContext
import com.littlestory.domain.browse.observeBrowseContext
import com.littlestory.domain.repository.DownloadsRepository
import com.littlestory.domain.repository.FavouritesRepository
import com.littlestory.domain.repository.LibraryRepository
import com.littlestory.domain.repository.PreferencesRepository
import com.littlestory.domain.repository.ProfilesRepository
import com.littlestory.feature.create.navigation.StoryReadyRoute
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

@Immutable
data class StoryReadyContent(
    val id: String,
    val title: String,
    val heroName: String?,
    val languageName: String,
    val durationMinutes: Int,
    val ageMin: Int,
    val ageMax: Int,
    val synopsis: String,
    val peek: String,
    val paletteId: String,
    val motifId: String,
    val favourite: Boolean,
    val downloaded: Boolean,
)

sealed interface StoryReadyUiState {
    data object Loading : StoryReadyUiState

    data object NotFound : StoryReadyUiState

    data class Loaded(val content: StoryReadyContent) : StoryReadyUiState
}

/** SCR-09 (LS-084): the generated story, already saved to My Stories. */
@HiltViewModel
class StoryReadyViewModel
    @Inject
    constructor(
        savedState: SavedStateHandle,
        profiles: ProfilesRepository,
        preferences: PreferencesRepository,
        library: LibraryRepository,
        private val favourites: FavouritesRepository,
        private val downloads: DownloadsRepository,
    ) : ViewModel() {
        private val storyId = savedState.toRoute<StoryReadyRoute>().storyId
        private val browse = observeBrowseContext(profiles, preferences)

        val state: StateFlow<StoryReadyUiState> =
            browse
                .flatMapContext { ctx ->
                    combine(
                        library.story(storyId),
                        favourites.favouriteIds(ctx.childId),
                        downloads.downloads.map { rows -> rows.map { it.storyId }.toSet() },
                    ) { story, favouriteIds, downloadedIds ->
                        if (story == null) {
                            StoryReadyUiState.NotFound
                        } else {
                            StoryReadyUiState.Loaded(
                                StoryReadyContent(
                                    id = story.id,
                                    title = story.title,
                                    heroName = ctx.child?.firstName,
                                    languageName = SupportedLanguages.fromTag(story.resolvedLanguage).nativeName,
                                    durationMinutes = story.durationMinutes,
                                    ageMin = story.ageMin,
                                    ageMax = story.ageMax,
                                    synopsis = story.synopsis,
                                    peek =
                                        story.pages.getOrNull(AppConfig.Generation.PEEK_PAGE_INDEX)?.text
                                            ?: story.pages
                                                .firstOrNull()
                                                ?.text
                                                .orEmpty(),
                                    paletteId = story.art.paletteId,
                                    motifId = story.art.motifId,
                                    favourite = story.id in favouriteIds,
                                    downloaded = story.id in downloadedIds,
                                ),
                            )
                        }
                    }
                }.stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(SUBSCRIPTION_TIMEOUT_MS),
                    StoryReadyUiState.Loading,
                )

        fun onFavouriteClick() {
            viewModelScope.launch {
                val childId = browse.first().childId
                favourites.toggle(childId, storyId)
            }
        }

        fun onDownloadClick() {
            viewModelScope.launch {
                val downloaded = downloads.downloads.first().any { it.storyId == storyId }
                if (downloaded) downloads.remove(storyId) else downloads.start(storyId)
            }
        }
    }
