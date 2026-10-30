package com.littlestory.feature.home.home

import androidx.compose.runtime.Immutable
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
import com.littlestory.domain.model.ChildProfile
import com.littlestory.domain.model.ReadingProgress
import com.littlestory.domain.repository.CatalogRepository
import com.littlestory.domain.repository.FavouritesRepository
import com.littlestory.domain.repository.PreferencesRepository
import com.littlestory.domain.repository.ProfilesRepository
import com.littlestory.domain.repository.ProgressRepository
import com.littlestory.feature.home.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val SUBSCRIPTION_TIMEOUT_MS = 5_000L
private const val PERCENT = 100

/** The story the child can pick up again; [percent] is whole percent listened. */
@Immutable
data class ContinueUi(val story: StoryItem, val percent: Int)

@Immutable
data class CategoryTileUi(val id: String, val name: String)

@Immutable
data class HomeUiState(
    val greeting: UiText,
    val title: UiText,
    val madeForTitle: UiText,
    /** Avatar tone of the active child; null when Setup was skipped. */
    val avatarTone: Int? = null,
    val continueListening: ContinueUi? = null,
    val categories: List<CategoryTileUi> = emptyList(),
    val madeFor: List<StoryItem> = emptyList(),
)

private val initialState =
    HomeUiState(
        greeting = UiText.Res(R.string.home_greeting),
        title = UiText.Res(R.string.home_title_generic),
        madeForTitle = UiText.Res(R.string.home_made_for_generic),
    )

@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        profiles: ProfilesRepository,
        preferences: PreferencesRepository,
        private val catalog: CatalogRepository,
        private val favourites: FavouritesRepository,
        progress: ProgressRepository,
    ) : ViewModel() {
        private val browse = observeBrowseContext(profiles, preferences)

        val state: StateFlow<HomeUiState> =
            browse
                .flatMapContext { ctx ->
                    combine(
                        catalog.catalogue(ctx.languageTag),
                        favourites.favouriteIds(ctx.childId),
                        progress.progress(ctx.childId),
                    ) { catalogue, favouriteIds, progressRows ->
                        buildState(ctx.child, catalogue, favouriteIds, progressRows)
                    }
                }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(SUBSCRIPTION_TIMEOUT_MS), initialState)

        fun onFavouriteClick(storyId: String) {
            viewModelScope.launch {
                val childId = browse.first().childId
                favourites.toggle(childId, storyId)
            }
        }

        private fun buildState(
            child: ChildProfile?,
            catalogue: Catalogue,
            favouriteIds: Set<String>,
            progressRows: List<ReadingProgress>,
        ): HomeUiState {
            val names = catalogue.categoryNames
            val resumable =
                progressRows
                    .filter { it.listenFraction > 0f && it.listenFraction < 1f }
                    .sortedByDescending { it.updatedAtEpochMs }
                    .firstNotNullOfOrNull { row ->
                        catalogue.stories.firstOrNull { it.id == row.storyId }?.let {
                            it to
                                row
                        }
                    }
            val interests = child?.interestIds.orEmpty()
            val ranked =
                catalogue.stories.sortedBy { story -> if (interests.any(story::belongsTo)) 0 else 1 }
            return HomeUiState(
                greeting = UiText.Res(R.string.home_greeting),
                title =
                    if (child == null) {
                        UiText.Res(R.string.home_title_generic)
                    } else {
                        UiText.Res(R.string.home_title_for_child, listOf(child.firstName))
                    },
                madeForTitle =
                    if (child == null) {
                        UiText.Res(R.string.home_made_for_generic)
                    } else {
                        UiText.Res(R.string.home_made_for_child, listOf(child.firstName))
                    },
                avatarTone = child?.avatar?.index,
                continueListening =
                    resumable?.let { (story, row) ->
                        ContinueUi(story.toItem(names, favouriteIds), (row.listenFraction * PERCENT).toInt())
                    },
                categories =
                    AppConfig.Home.exploreCategoryIds.mapNotNull { id ->
                        catalogue.category(id)?.let { CategoryTileUi(it.id, it.name) }
                    },
                madeFor = ranked.take(AppConfig.Home.MADE_FOR_COUNT).map { it.toItem(names, favouriteIds) },
            )
        }
    }
