package com.littlestory.feature.reader.reader

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.littlestory.core.common.config.AppConfig
import com.littlestory.domain.browse.flatMapContext
import com.littlestory.domain.browse.observeBrowseContext
import com.littlestory.domain.browse.observeStory
import com.littlestory.domain.model.ReadingProgress
import com.littlestory.domain.repository.PreferencesRepository
import com.littlestory.domain.repository.ProfilesRepository
import com.littlestory.domain.repository.StoryStores
import com.littlestory.feature.reader.navigation.ReaderRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

private const val SUBSCRIPTION_TIMEOUT_MS = 5_000L

// Text scale is stored as a float; steps are compared in tenths to avoid float drift.
private const val SCALE_PRECISION = 10f

@Immutable
data class ReaderContent(
    val id: String,
    val title: String,
    val pages: List<String>,
    val page: Int,
    val textScale: Float,
    val paletteId: String,
    val motifId: String,
    val favourite: Boolean,
) {
    val pageCount: Int get() = pages.size
    val canGoBack: Boolean get() = page > 0
    val canGoForward: Boolean get() = page < pages.lastIndex
    val canShrink: Boolean get() = textScale > AppConfig.Reader.TEXT_SCALE_MIN + SCALE_EPSILON
    val canGrow: Boolean get() = textScale < AppConfig.Reader.TEXT_SCALE_MAX - SCALE_EPSILON

    private companion object {
        const val SCALE_EPSILON = 0.001f
    }
}

sealed interface ReaderUiState {
    data object Loading : ReaderUiState

    data object NotFound : ReaderUiState

    data class Loaded(val content: ReaderContent) : ReaderUiState
}

/** SCR-10 (LS-090, LS-091): page-by-page reading; the page and text size are remembered. */
@HiltViewModel
class ReaderViewModel
    @Inject
    constructor(
        savedState: SavedStateHandle,
        profiles: ProfilesRepository,
        private val preferences: PreferencesRepository,
        stores: StoryStores,
    ) : ViewModel() {
        private val catalog = stores.catalog
        private val library = stores.library
        private val favourites = stores.favourites
        private val progress = stores.progress

        private val route = savedState.toRoute<ReaderRoute>()
        private val browse = observeBrowseContext(profiles, preferences)
        private val page = MutableStateFlow(route.page)

        val state: StateFlow<ReaderUiState> =
            browse
                .flatMapContext { ctx ->
                    combine(
                        observeStory(catalog, library, route.storyId, ctx.languageTag),
                        page,
                        preferences.reading,
                        favourites.favouriteIds(ctx.childId),
                    ) { story, current, reading, favouriteIds ->
                        if (story == null || story.pages.isEmpty()) {
                            ReaderUiState.NotFound
                        } else {
                            ReaderUiState.Loaded(
                                ReaderContent(
                                    id = story.id,
                                    title = story.title,
                                    pages = story.pages.map { it.text },
                                    page = current.coerceIn(0, story.pages.lastIndex),
                                    textScale = reading.textScale,
                                    paletteId = story.art.paletteId,
                                    motifId = story.art.motifId,
                                    favourite = story.id in favouriteIds,
                                ),
                            )
                        }
                    }
                }.stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(SUBSCRIPTION_TIMEOUT_MS),
                    ReaderUiState.Loading,
                )

        fun onNext() = goTo(page.value + 1)

        fun onPrevious() = goTo(page.value - 1)

        fun onTextSmaller() = changeScale(-AppConfig.Reader.TEXT_SCALE_STEP)

        fun onTextLarger() = changeScale(AppConfig.Reader.TEXT_SCALE_STEP)

        fun onFavouriteClick() {
            viewModelScope.launch {
                val childId = browse.first().childId
                val current = favourites.favouriteIds(childId).first()
                favourites.setFavourite(childId, route.storyId, route.storyId !in current)
            }
        }

        private fun goTo(target: Int) {
            val content = (state.value as? ReaderUiState.Loaded)?.content ?: return
            val next = target.coerceIn(0, content.pages.lastIndex)
            if (next == page.value) return
            page.value = next
            viewModelScope.launch {
                val childId = browse.first().childId
                val existing = progress.progress(childId).first().firstOrNull { it.storyId == route.storyId }
                progress.update(
                    ReadingProgress(
                        childId = childId,
                        storyId = route.storyId,
                        page = next,
                        listenFraction = existing?.listenFraction ?: 0f,
                        updatedAtEpochMs = System.currentTimeMillis(),
                    ),
                )
            }
        }

        private fun changeScale(delta: Float) {
            viewModelScope.launch {
                val reading = preferences.reading.first()
                val scaled = ((reading.textScale + delta) * SCALE_PRECISION).roundToInt() / SCALE_PRECISION
                val clamped = scaled.coerceIn(AppConfig.Reader.TEXT_SCALE_MIN, AppConfig.Reader.TEXT_SCALE_MAX)
                preferences.setReading(reading.copy(textScale = clamped))
            }
        }
    }
