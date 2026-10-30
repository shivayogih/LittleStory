package com.littlestory.feature.library.downloads

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.littlestory.core.designsystem.component.LittleStoryEmptyState
import com.littlestory.core.designsystem.component.LittleStoryIconButton
import com.littlestory.core.designsystem.component.LittleStoryNotesBanner
import com.littlestory.core.designsystem.component.LittleStoryStorageBar
import com.littlestory.core.designsystem.icon.LittleStoryIcon
import com.littlestory.core.designsystem.icon.LittleStoryIcons
import com.littlestory.core.designsystem.theme.LittleStoryTheme
import com.littlestory.core.ui.chrome.TopBar
import com.littlestory.core.ui.story.StoryArt
import com.littlestory.core.ui.story.storyArtFor
import com.littlestory.core.ui.system.SystemBarIcons
import com.littlestory.feature.library.R
import java.text.NumberFormat
import com.littlestory.core.ui.R as UiR

object DownloadsTags {
    const val STORAGE = "downloads_storage"
    const val EMPTY = "downloads_empty"

    fun item(id: String) = "downloads_item_$id"

    fun remove(id: String) = "downloads_remove_$id"
}

private const val BYTES_PER_MB = 1_000_000.0
private const val BYTES_PER_GB = 1_000_000_000.0

@Composable
fun DownloadsRoute(onBack: () -> Unit, onFindStory: () -> Unit, viewModel: DownloadsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    DownloadsScreen(state, onBack, onFindStory, viewModel::onRemove)
}

@Composable
fun DownloadsScreen(
    state: DownloadsUiState,
    onBack: () -> Unit,
    onFindStory: () -> Unit,
    onRemove: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LittleStoryTheme.colors
    val spacing = LittleStoryTheme.spacing
    SystemBarIcons(darkIcons = true)
    Column(modifier.fillMaxSize().background(colors.background)) {
        TopBar(
            title = stringResource(R.string.downloads_title),
            backContentDescription = stringResource(UiR.string.action_back),
            onBack = onBack,
        )
        LazyColumn(
            Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = spacing.screenHorizontal, vertical = spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.md),
        ) {
            item { StorageCard(state) }
            if (!state.loading && state.items.isEmpty()) {
                item {
                    LittleStoryEmptyState(
                        icon = LittleStoryIcons.Download,
                        title = stringResource(R.string.downloads_empty_title),
                        text = stringResource(R.string.downloads_empty_text),
                        actionLabel = stringResource(R.string.downloads_empty_action),
                        onAction = onFindStory,
                        modifier = Modifier.testTag(DownloadsTags.EMPTY),
                    )
                }
            }
            items(state.items, key = { it.storyId }) { DownloadRow(it, onRemove) }
            item {
                LittleStoryNotesBanner(text = stringResource(R.string.downloads_tip), icon = LittleStoryIcons.Check)
            }
        }
    }
}

@Composable
private fun StorageCard(state: DownloadsUiState) {
    val colors = LittleStoryTheme.colors
    val spacing = LittleStoryTheme.spacing
    val mb = NumberFormat.getIntegerInstance().format(state.usedBytes / BYTES_PER_MB)
    val gb = NumberFormat.getNumberInstance().format(state.quotaBytes / BYTES_PER_GB)
    Column(
        Modifier
            .fillMaxWidth()
            .background(colors.primary, LittleStoryTheme.shapes.large)
            .padding(spacing.xl)
            .semantics(mergeDescendants = true) { }
            .testTag(DownloadsTags.STORAGE),
        verticalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        Text(
            stringResource(R.string.downloads_used, mb),
            style = LittleStoryTheme.typography.homeTitle,
            color = colors.onPrimary,
        )
        Text(
            stringResource(R.string.downloads_quota, gb),
            style = LittleStoryTheme.typography.chip,
            color = colors.secondaryContainer,
        )
        LittleStoryStorageBar(
            state.usedFraction,
            Modifier.background(colors.primaryVariantLabel, LittleStoryTheme.shapes.chip),
        )
    }
}

@Composable
private fun DownloadRow(item: DownloadItem, onRemove: (String) -> Unit) {
    val colors = LittleStoryTheme.colors
    val spacing = LittleStoryTheme.spacing
    val dimens = LittleStoryTheme.dimens
    val (gradient, motif) = storyArtFor(item.paletteId, item.motifId)
    val mb = NumberFormat.getIntegerInstance().format(item.sizeBytes / BYTES_PER_MB)
    Row(
        Modifier
            .fillMaxWidth()
            .background(colors.surface, LittleStoryTheme.shapes.large)
            .padding(spacing.md)
            .testTag(DownloadsTags.item(item.storyId)),
        horizontalArrangement = Arrangement.spacedBy(spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(dimens.recentArtSize).clip(LittleStoryTheme.shapes.medium)) { StoryArt(gradient, motif) }
        Column(
            Modifier.weight(1f).semantics(mergeDescendants = true) {
            },
            verticalArrangement = Arrangement.spacedBy(spacing.xxs),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.xxs),
            ) {
                LittleStoryIcon(
                    LittleStoryIcons.Check,
                    contentDescription = null,
                    tint = colors.success,
                    modifier = Modifier.size(dimens.iconSizeSmall),
                )
                Text(
                    stringResource(R.string.downloads_available),
                    style = LittleStoryTheme.typography.eyebrow,
                    color = colors.success,
                )
            }
            Text(item.title, style = LittleStoryTheme.typography.itemTitle, color = colors.textPrimary)
            Text(
                stringResource(R.string.downloads_size, mb),
                style = LittleStoryTheme.typography.chip,
                color = colors.textSecondary,
            )
        }
        LittleStoryIconButton(
            LittleStoryIcons.Close,
            stringResource(R.string.downloads_remove, item.title),
            { onRemove(item.storyId) },
            modifier = Modifier.testTag(DownloadsTags.remove(item.storyId)),
        )
    }
}
