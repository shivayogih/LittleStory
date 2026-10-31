package com.littlestory.feature.create.ready

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontStyle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.littlestory.core.designsystem.component.LittleStoryEmptyState
import com.littlestory.core.designsystem.component.LittleStoryIconButton
import com.littlestory.core.designsystem.icon.LittleStoryIcon
import com.littlestory.core.designsystem.icon.LittleStoryIcons
import com.littlestory.core.designsystem.theme.LittleStoryTheme
import com.littlestory.core.ui.chrome.ChromeTags
import com.littlestory.core.ui.chrome.LoadingIndicator
import com.littlestory.core.ui.chrome.StickyActionBar
import com.littlestory.core.ui.chrome.TopBar
import com.littlestory.core.ui.story.StoryArt
import com.littlestory.core.ui.story.storyArtFor
import com.littlestory.core.ui.system.SystemBarIcons
import com.littlestory.feature.create.R
import com.littlestory.core.ui.R as UiR

object ReadyTags {
    const val TITLE = "ready_title"
    const val SAVED = "ready_saved"
    const val FAVOURITE = "ready_favourite"
    const val DOWNLOAD = "ready_download"
}

data class ReadyActions(
    val onBack: () -> Unit = {},
    val onListen: (storyId: String) -> Unit = {},
    val onRead: (storyId: String) -> Unit = {},
    val onOpenCreated: () -> Unit = {},
)

@Composable
fun StoryReadyRoute(actions: ReadyActions, viewModel: StoryReadyViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    StoryReadyScreen(state, actions, viewModel::onFavouriteClick, viewModel::onDownloadClick)
}

@Composable
fun StoryReadyScreen(
    state: StoryReadyUiState,
    actions: ReadyActions,
    onFavouriteClick: () -> Unit,
    onDownloadClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LittleStoryTheme.colors
    when (state) {
        StoryReadyUiState.Loading ->
            Column(modifier.fillMaxSize().background(colors.background)) {
                SystemBarIcons(darkIcons = true)
                TopBar(backContentDescription = stringResource(UiR.string.action_back), onBack = actions.onBack)
                LoadingIndicator(Modifier.weight(1f))
            }
        StoryReadyUiState.NotFound ->
            Column(modifier.fillMaxSize().background(colors.background)) {
                SystemBarIcons(darkIcons = true)
                TopBar(backContentDescription = stringResource(UiR.string.action_back), onBack = actions.onBack)
                LittleStoryEmptyState(
                    icon = LittleStoryIcons.Book,
                    title = stringResource(R.string.ready_not_found_title),
                    text = stringResource(R.string.ready_not_found_text),
                )
            }
        is StoryReadyUiState.Loaded -> Loaded(state.content, actions, onFavouriteClick, onDownloadClick, modifier)
    }
}

@Composable
private fun Loaded(
    story: StoryReadyContent,
    actions: ReadyActions,
    onFavouriteClick: () -> Unit,
    onDownloadClick: () -> Unit,
    modifier: Modifier,
) {
    val colors = LittleStoryTheme.colors
    val spacing = LittleStoryTheme.spacing
    SystemBarIcons(darkIcons = true)
    Column(modifier.fillMaxSize().background(colors.background)) {
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = spacing.screenHorizontal, vertical = spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.lg),
        ) {
            Ribbon()
            Cover(story, actions.onBack, onFavouriteClick, onDownloadClick)
            Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                Text(
                    stringResource(R.string.ready_eyebrow),
                    style = LittleStoryTheme.typography.eyebrow,
                    color = colors.primaryVariantLabel,
                )
                Text(
                    story.title,
                    style = LittleStoryTheme.typography.heroTitle,
                    color = colors.textPrimary,
                    modifier = Modifier.testTag(ReadyTags.TITLE),
                )
                Text(
                    stringResource(
                        R.string.ready_meta,
                        story.languageName,
                        story.durationMinutes,
                        story.ageMin,
                        story.ageMax,
                    ),
                    style = LittleStoryTheme.typography.chip,
                    color = colors.textSecondary,
                )
            }
            Text(story.synopsis, style = LittleStoryTheme.typography.body, color = colors.textReader)
            Peek(story.peek)
            SavedRow(actions.onOpenCreated)
        }
        StickyActionBar(
            primaryLabel = stringResource(R.string.ready_listen),
            onPrimary = { actions.onListen(story.id) },
            secondaryLabel = stringResource(R.string.ready_read),
            onSecondary = { actions.onRead(story.id) },
        )
    }
}

@Composable
private fun Ribbon() {
    val colors = LittleStoryTheme.colors
    Row(
        Modifier
            .fillMaxWidth()
            .background(colors.successRibbon, LittleStoryTheme.shapes.chip)
            .padding(horizontal = LittleStoryTheme.spacing.lg, vertical = LittleStoryTheme.spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(LittleStoryTheme.spacing.sm, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LittleStoryIcon(LittleStoryIcons.Check, contentDescription = null, tint = colors.success)
        Text(stringResource(R.string.ready_ribbon), style = LittleStoryTheme.typography.chip, color = colors.success)
    }
}

@Composable
private fun Cover(
    story: StoryReadyContent,
    onBack: () -> Unit,
    onFavouriteClick: () -> Unit,
    onDownloadClick: () -> Unit,
) {
    val colors = LittleStoryTheme.colors
    val spacing = LittleStoryTheme.spacing
    val (gradient, motif) = storyArtFor(story.paletteId, story.motifId)
    Box(
        Modifier
            .fillMaxWidth()
            .height(LittleStoryTheme.dimens.readyCoverHeight)
            .clip(LittleStoryTheme.shapes.hero),
    ) {
        StoryArt(gradient, motif, storyId = story.id)
        Row(Modifier.fillMaxWidth().padding(spacing.sm), horizontalArrangement = Arrangement.spacedBy(spacing.xs)) {
            LittleStoryIconButton(
                LittleStoryIcons.Back,
                stringResource(UiR.string.action_back),
                onBack,
                modifier = Modifier.testTag(ChromeTags.BACK),
            )
            Box(Modifier.weight(1f))
            LittleStoryIconButton(
                icon = if (story.favourite) LittleStoryIcons.HeartFilled else LittleStoryIcons.Heart,
                contentDescription =
                    stringResource(
                        if (story.favourite) UiR.string.story_favourite_remove else UiR.string.story_favourite_add,
                        story.title,
                    ),
                onClick = onFavouriteClick,
                active = story.favourite,
                modifier = Modifier.testTag(ReadyTags.FAVOURITE),
            )
            LittleStoryIconButton(
                icon = if (story.downloaded) LittleStoryIcons.DownloadFilled else LittleStoryIcons.Download,
                contentDescription =
                    stringResource(
                        if (story.downloaded) R.string.ready_download_remove else R.string.ready_download_add,
                        story.title,
                    ),
                onClick = onDownloadClick,
                active = story.downloaded,
                modifier = Modifier.testTag(ReadyTags.DOWNLOAD),
            )
        }
        if (story.heroName != null) {
            Text(
                stringResource(R.string.ready_made_for, story.heroName),
                style = LittleStoryTheme.typography.chip,
                color = colors.onSecondaryContainer,
                modifier =
                    Modifier
                        .align(Alignment.BottomStart)
                        .padding(spacing.md)
                        .background(colors.secondaryContainer, LittleStoryTheme.shapes.chip)
                        .padding(horizontal = spacing.md, vertical = spacing.xs),
            )
        }
    }
}

@Composable
private fun Peek(text: String) {
    val colors = LittleStoryTheme.colors
    val spacing = LittleStoryTheme.spacing
    Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
        Text(
            stringResource(R.string.ready_peek_title),
            style = LittleStoryTheme.typography.itemTitle,
            color = colors.textPrimary,
        )
        Row(Modifier.fillMaxWidth()) {
            Box(
                Modifier
                    .width(LittleStoryTheme.dimens.peekRuleWidth)
                    .heightIn(min = LittleStoryTheme.dimens.minTouchTarget)
                    .background(colors.accent),
            )
            Text(
                text,
                style = LittleStoryTheme.typography.body.copy(fontStyle = FontStyle.Italic),
                color = colors.textReader,
                modifier = Modifier.padding(start = spacing.md),
            )
        }
    }
}

@Composable
private fun SavedRow(onClick: () -> Unit) {
    val colors = LittleStoryTheme.colors
    val spacing = LittleStoryTheme.spacing
    Row(
        Modifier
            .fillMaxWidth()
            .clip(LittleStoryTheme.shapes.medium)
            .background(colors.surface)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(spacing.lg)
            .testTag(ReadyTags.SAVED),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        LittleStoryIcon(LittleStoryIcons.Book, contentDescription = null, tint = colors.primary)
        Column(Modifier.weight(1f)) {
            Text(
                stringResource(R.string.ready_saved_title),
                style = LittleStoryTheme.typography.itemTitle,
                color = colors.textPrimary,
            )
            Text(
                stringResource(R.string.ready_saved_body),
                style = LittleStoryTheme.typography.chip,
                color = colors.textSecondary,
            )
        }
        LittleStoryIcon(LittleStoryIcons.Chevron, contentDescription = null, tint = colors.textSecondary)
    }
}
