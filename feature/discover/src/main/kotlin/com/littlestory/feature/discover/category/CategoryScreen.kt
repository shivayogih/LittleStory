package com.littlestory.feature.discover.category

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.littlestory.core.designsystem.component.LittleStoryEmptyState
import com.littlestory.core.designsystem.component.LittleStorySelect
import com.littlestory.core.designsystem.icon.LittleStoryIcons
import com.littlestory.core.designsystem.theme.LittleStoryTheme
import com.littlestory.core.ui.chrome.LoadingIndicator
import com.littlestory.core.ui.chrome.TopBar
import com.littlestory.core.ui.illustration.CultureGatewayArt
import com.littlestory.core.ui.story.StoryItemRow
import com.littlestory.core.ui.system.SystemBarIcons
import com.littlestory.core.ui.text.asString
import com.littlestory.feature.discover.R
import com.littlestory.core.ui.R as UiR

object CategoryTags {
    const val HERO = "category_hero"
    const val SORT = "category_sort"
    const val EMPTY = "category_empty"

    fun story(id: String) = "category_story_$id"
}

@Composable
fun CategoryRoute(onBack: () -> Unit, onStory: (String) -> Unit, viewModel: CategoryViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    CategoryScreen(state, onBack, onStory, viewModel::onSortSelected, viewModel::onFavouriteClick)
}

@Composable
fun CategoryScreen(
    state: CategoryUiState,
    onBack: () -> Unit,
    onStory: (String) -> Unit,
    onSortSelected: (String) -> Unit,
    onFavouriteClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LittleStoryTheme.colors
    val spacing = LittleStoryTheme.spacing
    val gutter = Modifier.padding(horizontal = spacing.screenHorizontal)
    SystemBarIcons(darkIcons = true)
    Column(modifier.fillMaxSize().background(colors.background)) {
        TopBar(backContentDescription = stringResource(UiR.string.action_back), onBack = onBack)
        if (state.loading) {
            LoadingIndicator(Modifier.weight(1f))
            return@Column
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(spacing.lg)) {
            item { Hero(state, gutter) }
            item {
                Column(gutter, verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                    Text(
                        stringResource(R.string.category_heading),
                        style = LittleStoryTheme.typography.homeTitle,
                        color = colors.textPrimary,
                    )
                    val labels = state.sortOptions.map { it.label.asString() }
                    LittleStorySelect(
                        selected =
                            labels.getOrElse(
                                state.sortOptions.indexOfFirst { it.id == state.selectedSort },
                            ) { "" },
                        options = labels,
                        onSelect = { onSortSelected(state.sortOptions[it].id) },
                        label = stringResource(R.string.category_sort_label),
                        modifier = Modifier.testTag(CategoryTags.SORT),
                    )
                }
            }
            if (state.stories.isEmpty()) {
                item {
                    LittleStoryEmptyState(
                        icon = LittleStoryIcons.Book,
                        title = stringResource(R.string.category_empty_title),
                        text = stringResource(R.string.category_empty_text),
                        modifier = Modifier.testTag(CategoryTags.EMPTY),
                    )
                }
            }
            items(state.stories, key = { it.id }) { item ->
                StoryItemRow(
                    item = item,
                    onClick = { onStory(item.id) },
                    onFavouriteClick = { onFavouriteClick(item.id) },
                    modifier = gutter.testTag(CategoryTags.story(item.id)),
                )
            }
            item {
                Box(
                    Modifier.windowInsetsPadding(WindowInsets.navigationBars).padding(bottom = spacing.screenBottom),
                )
            }
        }
    }
}

@Composable
private fun Hero(state: CategoryUiState, modifier: Modifier) {
    val colors = LittleStoryTheme.colors
    val spacing = LittleStoryTheme.spacing
    val dimens = LittleStoryTheme.dimens
    Box(
        modifier
            .fillMaxWidth()
            .height(dimens.categoryHeroHeight)
            .background(colors.primary, LittleStoryTheme.shapes.large)
            .testTag(CategoryTags.HERO),
    ) {
        CultureGatewayArt(
            Modifier
                .align(Alignment.BottomEnd)
                .padding(end = spacing.lg)
                .width(dimens.categoryHeroArtWidth)
                .fillMaxHeight(HERO_ART_HEIGHT_FRACTION),
        )
        Column(Modifier.padding(spacing.xl), verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
            Text(
                pluralStringResource(R.plurals.category_story_count, state.storyCount, state.storyCount),
                style = LittleStoryTheme.typography.heroKicker,
                color = colors.accent,
            )
            Text(
                state.title.asString(),
                style = LittleStoryTheme.typography.heroTitle,
                color = colors.onPrimary,
            )
            Text(
                state.description.asString(),
                style = LittleStoryTheme.typography.body,
                color = colors.onPrimary,
                modifier = Modifier.fillMaxWidth(HERO_TEXT_FRACTION),
            )
        }
    }
}

private const val HERO_TEXT_FRACTION = 0.68f
private const val HERO_ART_HEIGHT_FRACTION = 0.5f
