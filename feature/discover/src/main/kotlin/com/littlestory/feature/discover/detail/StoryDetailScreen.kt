package com.littlestory.feature.discover.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.littlestory.core.designsystem.component.LittleStoryEmptyState
import com.littlestory.core.designsystem.component.LittleStoryIconButton
import com.littlestory.core.designsystem.icon.LittleStoryIcon
import com.littlestory.core.designsystem.icon.LittleStoryIcons
import com.littlestory.core.designsystem.theme.LittleStoryTheme
import com.littlestory.core.ui.chrome.StickyActionBar
import com.littlestory.core.ui.chrome.TopBar
import com.littlestory.core.ui.story.StoryArt
import com.littlestory.core.ui.story.storyArtFor
import com.littlestory.core.ui.system.SystemBarIcons
import com.littlestory.feature.discover.R
import com.littlestory.core.ui.R as UiR

object DetailTags {
    const val FAVOURITE = "detail_favourite"
    const val DOWNLOAD = "detail_download"
    const val TITLE = "detail_title"
    const val NOT_FOUND = "detail_not_found"
}

/** Where the detail screen sends the child; Player and Reader are connected in `:app`. */
data class DetailActions(
    val onBack: () -> Unit = {},
    val onListen: (storyId: String) -> Unit = {},
    val onRead: (storyId: String) -> Unit = {},
)

@Composable
fun StoryDetailRoute(actions: DetailActions, viewModel: StoryDetailViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    StoryDetailScreen(state, actions, viewModel::onFavouriteClick, viewModel::onDownloadClick)
}

@Composable
fun StoryDetailScreen(
    state: StoryDetailUiState,
    actions: DetailActions,
    onFavouriteClick: () -> Unit,
    onDownloadClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LittleStoryTheme.colors
    when (state) {
        StoryDetailUiState.Loading -> Box(modifier.fillMaxSize().background(colors.background))
        StoryDetailUiState.NotFound ->
            Column(modifier.fillMaxSize().background(colors.background)) {
                SystemBarIcons(darkIcons = true)
                TopBar(backContentDescription = stringResource(UiR.string.action_back), onBack = actions.onBack)
                LittleStoryEmptyState(
                    icon = LittleStoryIcons.Book,
                    title = stringResource(R.string.detail_not_found_title),
                    text = stringResource(R.string.detail_not_found_text),
                    modifier = Modifier.testTag(DetailTags.NOT_FOUND),
                )
            }
        is StoryDetailUiState.Loaded -> Loaded(state.content, actions, onFavouriteClick, onDownloadClick, modifier)
    }
}

@Composable
private fun Loaded(
    story: StoryDetailContent,
    actions: DetailActions,
    onFavouriteClick: () -> Unit,
    onDownloadClick: () -> Unit,
    modifier: Modifier,
) {
    val colors = LittleStoryTheme.colors
    val spacing = LittleStoryTheme.spacing
    val dimens = LittleStoryTheme.dimens
    val (gradient, motif) = storyArtFor(story.paletteId, story.motifId)
    // The cover is behind the status bar, so its icons are light-on-art (night art, dark glyphs on the buttons).
    SystemBarIcons(darkIcons = false)
    Column(modifier.fillMaxSize().background(colors.background)) {
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            Box(Modifier.fillMaxWidth().height(dimens.detailCoverHeight)) {
                StoryArt(gradient, motif)
                Row(
                    Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .padding(horizontal = spacing.screenHorizontal, vertical = spacing.sm),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                ) {
                    LittleStoryIconButton(
                        LittleStoryIcons.Back,
                        stringResource(UiR.string.action_back),
                        actions.onBack,
                    )
                    Box(Modifier.weight(1f))
                    LittleStoryIconButton(
                        icon = if (story.favourite) LittleStoryIcons.HeartFilled else LittleStoryIcons.Heart,
                        contentDescription =
                            if (story.favourite) {
                                stringResource(UiR.string.story_favourite_remove, story.title)
                            } else {
                                stringResource(UiR.string.story_favourite_add, story.title)
                            },
                        onClick = onFavouriteClick,
                        active = story.favourite,
                        modifier = Modifier.testTag(DetailTags.FAVOURITE),
                    )
                    LittleStoryIconButton(
                        icon = if (story.downloaded) LittleStoryIcons.DownloadFilled else LittleStoryIcons.Download,
                        contentDescription =
                            if (story.downloaded) {
                                stringResource(R.string.detail_download_remove, story.title)
                            } else {
                                stringResource(R.string.detail_download_add, story.title)
                            },
                        onClick = onDownloadClick,
                        active = story.downloaded,
                        modifier = Modifier.testTag(DetailTags.DOWNLOAD),
                    )
                }
            }
            Body(story, Modifier.overlapAbove(dimens.detailSheetOverlap))
        }
        StickyActionBar(
            primaryLabel = stringResource(R.string.detail_listen),
            onPrimary = { actions.onListen(story.id) },
            secondaryLabel = stringResource(R.string.detail_read),
            onSecondary = { actions.onRead(story.id) },
        )
    }
}

/** Pulls the body sheet up over the bottom of the cover so its rounded top corners sit on the art. */
private fun Modifier.overlapAbove(overlap: Dp): Modifier =
    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        val shift = overlap.roundToPx()
        layout(placeable.width, placeable.height - shift) { placeable.place(0, -shift) }
    }

@Composable
private fun Body(story: StoryDetailContent, modifier: Modifier) {
    val colors = LittleStoryTheme.colors
    val spacing = LittleStoryTheme.spacing
    Column(
        modifier
            .fillMaxWidth()
            .clip(LittleStoryTheme.shapes.sheet)
            .background(colors.surface)
            .padding(spacing.screenHorizontal),
        verticalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        Text(
            story.categoryName,
            style = LittleStoryTheme.typography.chip,
            color = colors.onSecondaryContainer,
            modifier =
                Modifier
                    .background(colors.secondaryContainer, LittleStoryTheme.shapes.chip)
                    .padding(horizontal = spacing.md, vertical = spacing.xs),
        )
        Text(
            story.title,
            style = LittleStoryTheme.typography.heroTitle,
            color = colors.textPrimary,
            modifier = Modifier.testTag(DetailTags.TITLE),
        )
        Text(
            stringResource(R.string.detail_meta, story.ageMin, story.ageMax, story.durationMinutes, story.languageName),
            style = LittleStoryTheme.typography.chip,
            color = colors.textSecondary,
        )
        Text(story.synopsis, style = LittleStoryTheme.typography.body, color = colors.textReader)
        InfoPanel(
            title = stringResource(R.string.detail_lesson_title),
            text = story.lesson,
            background = colors.lessonPanel,
            tile = colors.warmAccent,
            icon = LittleStoryIcons.Heart,
        )
        InfoPanel(
            title = stringResource(R.string.detail_narration_title),
            text = stringResource(R.string.detail_narration_text, story.languageName),
            background = colors.surfaceMuted,
            tile = colors.secondaryContainer,
            icon = LittleStoryIcons.Volume,
            trailing = stringResource(R.string.detail_narration_available),
        )
    }
}

@Composable
private fun InfoPanel(
    title: String,
    text: String,
    background: Color,
    tile: Color,
    icon: Int,
    trailing: String? = null,
) {
    val colors = LittleStoryTheme.colors
    val spacing = LittleStoryTheme.spacing
    Row(
        Modifier
            .fillMaxWidth()
            .background(background, LittleStoryTheme.shapes.medium)
            .padding(spacing.lg),
        horizontalArrangement = Arrangement.spacedBy(spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(LittleStoryTheme.dimens.minTouchTarget).background(tile, LittleStoryTheme.shapes.small),
            contentAlignment = Alignment.Center,
        ) {
            LittleStoryIcon(icon, contentDescription = null, tint = colors.primary)
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(spacing.xxs)) {
            Text(title, style = LittleStoryTheme.typography.itemTitle, color = colors.textPrimary)
            Text(text, style = LittleStoryTheme.typography.body, color = colors.textSecondary)
        }
        if (trailing != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                LittleStoryIcon(LittleStoryIcons.Check, contentDescription = null, tint = colors.success)
                Text(trailing, style = LittleStoryTheme.typography.chip, color = colors.success)
            }
        }
    }
}
