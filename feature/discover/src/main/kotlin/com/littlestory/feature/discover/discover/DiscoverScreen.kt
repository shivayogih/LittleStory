package com.littlestory.feature.discover.discover

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.littlestory.core.designsystem.component.LittleStoryChip
import com.littlestory.core.designsystem.component.LittleStoryEmptyState
import com.littlestory.core.designsystem.component.LittleStoryIconButton
import com.littlestory.core.designsystem.component.LittleStorySearchField
import com.littlestory.core.designsystem.icon.LittleStoryIcon
import com.littlestory.core.designsystem.icon.LittleStoryIcons
import com.littlestory.core.designsystem.theme.LittleStoryTheme
import com.littlestory.core.ui.chrome.TopBar
import com.littlestory.core.ui.story.SectionHeader
import com.littlestory.core.ui.story.StoryArt
import com.littlestory.core.ui.story.StoryItem
import com.littlestory.core.ui.story.StoryItemCard
import com.littlestory.core.ui.story.storyArtFor
import com.littlestory.core.ui.system.SystemBarIcons
import com.littlestory.core.ui.text.asString
import com.littlestory.feature.discover.R

object DiscoverTags {
    const val SEARCH = "discover_search"
    const val FEATURED = "discover_featured"
    const val EMPTY = "discover_empty"
    const val VIEW_ALL = "discover_view_all"

    fun chip(id: String) = "discover_chip_$id"

    fun story(id: String) = "discover_story_$id"
}

/** Where Discover sends the child; `:app` supplies the real destinations. */
data class DiscoverActions(
    val onNotifications: () -> Unit = {},
    val onStory: (storyId: String) -> Unit = {},
    val onCategory: (categoryId: String) -> Unit = {},
)

@Composable
fun DiscoverRoute(actions: DiscoverActions, viewModel: DiscoverViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    DiscoverScreen(
        state = state,
        actions = actions,
        onQueryChange = viewModel::onQueryChange,
        onFilterSelected = viewModel::onFilterSelected,
        onClearSearch = viewModel::onClearSearch,
        onFavouriteClick = viewModel::onFavouriteClick,
    )
}

@Composable
fun DiscoverScreen(
    state: DiscoverUiState,
    actions: DiscoverActions,
    onQueryChange: (String) -> Unit,
    onFilterSelected: (String) -> Unit,
    onClearSearch: () -> Unit,
    onFavouriteClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LittleStoryTheme.colors
    val spacing = LittleStoryTheme.spacing
    val gutter = Modifier.padding(horizontal = spacing.screenHorizontal)
    SystemBarIcons(darkIcons = true)
    Column(modifier.fillMaxSize().background(colors.background)) {
        TopBar(
            title = stringResource(R.string.discover_title),
            action = {
                LittleStoryIconButton(
                    LittleStoryIcons.Bell,
                    stringResource(R.string.discover_notifications),
                    actions.onNotifications,
                )
            },
        )
        LazyColumn(verticalArrangement = Arrangement.spacedBy(spacing.xl)) {
            item {
                LittleStorySearchField(
                    value = state.query,
                    onValueChange = onQueryChange,
                    placeholder = stringResource(R.string.discover_search_placeholder),
                    clearContentDescription = stringResource(R.string.discover_search_clear),
                    modifier = gutter.testTag(DiscoverTags.SEARCH),
                )
            }
            state.featured?.let { featured ->
                item { FeaturedCard(featured, { actions.onStory(featured.story.id) }, gutter) }
            }
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = spacing.screenHorizontal),
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                ) {
                    items(state.filters, key = { it.id }) { chip ->
                        LittleStoryChip(
                            text = chip.label.asString(),
                            selected = chip.id == state.selectedFilter,
                            onClick = { onFilterSelected(chip.id) },
                            modifier = Modifier.testTag(DiscoverTags.chip(chip.id)),
                        )
                    }
                }
            }
            if (state.hasNoResults) {
                item {
                    LittleStoryEmptyState(
                        icon = LittleStoryIcons.Search,
                        title = stringResource(R.string.discover_empty_title),
                        text = stringResource(R.string.discover_empty_text),
                        actionLabel = stringResource(R.string.discover_empty_action),
                        onAction = onClearSearch,
                        modifier = Modifier.testTag(DiscoverTags.EMPTY),
                    )
                }
            } else {
                item {
                    SectionHeader(
                        title = state.sectionTitle.asString(),
                        seeAllLabel = if (state.showViewAll) stringResource(R.string.discover_view_all) else null,
                        onSeeAll = { actions.onCategory(state.selectedFilter) },
                        modifier = gutter,
                    )
                }
                items(state.results.chunked(GRID_COLUMNS)) { rowItems ->
                    Row(gutter.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(spacing.md)) {
                        rowItems.forEach { item ->
                            StoryItemCard(
                                item = item,
                                onClick = { actions.onStory(item.id) },
                                onFavouriteClick = { onFavouriteClick(item.id) },
                                modifier = Modifier.weight(1f).testTag(DiscoverTags.story(item.id)),
                            )
                        }
                        // A short last row keeps its card at half width instead of stretching.
                        repeat(GRID_COLUMNS - rowItems.size) { Box(Modifier.weight(1f)) }
                    }
                }
            }
            if (state.bedtime.isNotEmpty()) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
                        SectionHeader(stringResource(R.string.discover_bedtime), modifier = gutter)
                        BedtimeRow(state.bedtime, actions.onStory, onFavouriteClick)
                    }
                }
            }
            item { Box(Modifier.padding(bottom = spacing.screenBottom)) }
        }
    }
}

private const val GRID_COLUMNS = 2

@Composable
private fun BedtimeRow(items: List<StoryItem>, onStory: (String) -> Unit, onFavouriteClick: (String) -> Unit) {
    val spacing = LittleStoryTheme.spacing
    LazyRow(
        contentPadding = PaddingValues(horizontal = spacing.screenHorizontal),
        horizontalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        items(items, key = { it.id }) { item ->
            StoryItemCard(item, { onStory(item.id) }, { onFavouriteClick(item.id) })
        }
    }
}

@Composable
private fun FeaturedCard(featured: FeaturedUi, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = LittleStoryTheme.colors
    val spacing = LittleStoryTheme.spacing
    val (gradient, motif) = storyArtFor(featured.story.paletteId, featured.story.motifId)
    Row(
        modifier
            .fillMaxWidth()
            .clip(LittleStoryTheme.shapes.large)
            .background(colors.nightSurface)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(spacing.lg)
            .testTag(DiscoverTags.FEATURED),
        horizontalArrangement = Arrangement.spacedBy(spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
            Text(
                stringResource(R.string.discover_featured_kicker),
                style = LittleStoryTheme.typography.heroKicker,
                color = colors.accent,
            )
            Text(featured.story.title, style = LittleStoryTheme.typography.topBarTitle, color = colors.onNight)
            Text(
                featured.synopsis,
                style = LittleStoryTheme.typography.body,
                color = colors.onNight,
                maxLines = FEATURED_SYNOPSIS_LINES,
            )
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(spacing.xs)) {
                Text(
                    stringResource(R.string.discover_featured_cta),
                    style = LittleStoryTheme.typography.button,
                    color = colors.highlight,
                )
                LittleStoryIcon(LittleStoryIcons.Play, contentDescription = null, tint = colors.highlight)
            }
        }
        Box(Modifier.width(LittleStoryTheme.dimens.featuredArtWidth).aspectRatio(FEATURED_ART_RATIO).clip(LittleStoryTheme.shapes.medium)) {
            StoryArt(gradient, motif)
        }
    }
}

private const val FEATURED_SYNOPSIS_LINES = 3

// Portrait cover, as on story cards.
private const val FEATURED_ART_RATIO = 0.8f
