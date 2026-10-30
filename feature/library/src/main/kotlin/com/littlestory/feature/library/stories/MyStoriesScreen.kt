package com.littlestory.feature.library.stories

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.littlestory.core.designsystem.component.LittleStoryEmptyState
import com.littlestory.core.designsystem.component.LittleStoryIconButton
import com.littlestory.core.designsystem.component.LittleStoryReadingProgress
import com.littlestory.core.designsystem.component.LittleStorySearchField
import com.littlestory.core.designsystem.component.LittleStoryTabs
import com.littlestory.core.designsystem.icon.LittleStoryIcon
import com.littlestory.core.designsystem.icon.LittleStoryIcons
import com.littlestory.core.designsystem.theme.LittleStoryTheme
import com.littlestory.core.ui.chrome.TopBar
import com.littlestory.core.ui.story.ListStoryRow
import com.littlestory.core.ui.story.StoryArt
import com.littlestory.core.ui.story.StoryItem
import com.littlestory.core.ui.story.storyArtFor
import com.littlestory.core.ui.system.SystemBarIcons
import com.littlestory.feature.library.R
import com.littlestory.feature.library.navigation.LibraryTab
import java.text.NumberFormat

object MyStoriesTags {
    const val DOWNLOADS = "stories_downloads"
    const val SEARCH = "stories_search"
    const val RECENT = "stories_recent"
    const val EMPTY = "stories_empty"

    fun tab(tab: LibraryTab) = "stories_tab_${tab.name}"

    fun story(id: String) = "stories_story_$id"
}

private const val SECONDS_PER_MINUTE = 60

data class MyStoriesActions(
    val onDownloads: () -> Unit = {},
    val onStory: (storyId: String) -> Unit = {},
    val onCreatedStory: (storyId: String) -> Unit = {},
    val onPlay: (storyId: String) -> Unit = {},
    val onDiscover: () -> Unit = {},
)

@Composable
fun MyStoriesRoute(actions: MyStoriesActions, viewModel: MyStoriesViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    MyStoriesScreen(state, actions, viewModel::onQueryChange, viewModel::onTabSelected)
}

private val tabLabels =
    mapOf(
        LibraryTab.All to R.string.stories_tab_all,
        LibraryTab.CreatedByMe to R.string.stories_tab_created,
        LibraryTab.Downloaded to R.string.stories_tab_downloaded,
        LibraryTab.Favourites to R.string.stories_tab_favourites,
    )

@Composable
fun MyStoriesScreen(
    state: MyStoriesUiState,
    actions: MyStoriesActions,
    onQueryChange: (String) -> Unit,
    onTabSelected: (LibraryTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LittleStoryTheme.colors
    val spacing = LittleStoryTheme.spacing
    SystemBarIcons(darkIcons = true)
    Column(modifier.fillMaxSize().background(colors.background)) {
        TopBar(
            title = stringResource(R.string.stories_title),
            action = {
                LittleStoryIconButton(
                    LittleStoryIcons.Download,
                    stringResource(R.string.stories_open_downloads),
                    actions.onDownloads,
                    modifier = Modifier.testTag(MyStoriesTags.DOWNLOADS),
                )
            },
        )
        LazyColumn(
            Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = spacing.screenHorizontal, vertical = spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.md),
        ) {
            item {
                LittleStorySearchField(
                    value = state.query,
                    onValueChange = onQueryChange,
                    placeholder = stringResource(R.string.stories_search),
                    clearContentDescription = stringResource(R.string.stories_search_clear),
                    modifier = Modifier.testTag(MyStoriesTags.SEARCH),
                )
            }
            item {
                val tabs = LibraryTab.entries
                LittleStoryTabs(
                    tabs = tabs.map { stringResource(tabLabels.getValue(it)) },
                    selectedIndex = tabs.indexOf(state.tab),
                    onSelect = { onTabSelected(tabs[it]) },
                    tabTag = { MyStoriesTags.tab(tabs[it]) },
                )
            }
            state.recentlyPlayed?.let { recent ->
                item { SectionTitle(R.string.stories_recently_played) }
                item { RecentCard(recent, actions.onPlay) }
            }
            if (!state.loading && state.stories.isEmpty()) {
                item { Empty(state, actions.onDiscover) }
            } else if (state.stories.isNotEmpty()) {
                if (state.tab == LibraryTab.All) item { SectionTitle(R.string.stories_collection) }
                items(
                    state.stories,
                    key = { it.id },
                ) { story -> StoryRow(story, story.id in state.createdIds, actions) }
            }
        }
    }
}

@Composable
private fun SectionTitle(title: Int) {
    Text(
        stringResource(title),
        style = LittleStoryTheme.typography.homeTitle,
        color = LittleStoryTheme.colors.textPrimary,
        modifier = Modifier.padding(top = LittleStoryTheme.spacing.sm),
    )
}

@Composable
private fun RecentCard(recent: RecentlyPlayed, onPlay: (String) -> Unit) {
    val colors = LittleStoryTheme.colors
    val spacing = LittleStoryTheme.spacing
    val dimens = LittleStoryTheme.dimens
    val (gradient, motif) = storyArtFor(recent.item.paletteId, recent.item.motifId)
    val format = NumberFormat.getIntegerInstance()
    val remaining =
        stringResource(
            R.string.stories_remaining,
            format.format(recent.remainingSeconds / SECONDS_PER_MINUTE),
            recent.remainingSeconds % SECONDS_PER_MINUTE,
        )
    val playLabel = stringResource(R.string.stories_play, recent.item.title)
    Row(
        Modifier
            .fillMaxWidth()
            .clip(LittleStoryTheme.shapes.large)
            .background(colors.surface)
            .clickable(role = Role.Button) { onPlay(recent.item.id) }
            .semantics(mergeDescendants = true) { contentDescription = playLabel }
            .padding(spacing.md)
            .testTag(MyStoriesTags.RECENT),
        horizontalArrangement = Arrangement.spacedBy(spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(dimens.recentArtSize).clip(LittleStoryTheme.shapes.medium)) { StoryArt(gradient, motif) }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
            Text(recent.item.title, style = LittleStoryTheme.typography.itemTitle, color = colors.textPrimary)
            Text(remaining, style = LittleStoryTheme.typography.chip, color = colors.textSecondary)
            LittleStoryReadingProgress(recent.progress)
        }
        Box(
            Modifier
                .size(
                    dimens.roundPlaySize,
                ).background(colors.secondaryContainer, LittleStoryTheme.shapes.iconButton),
            contentAlignment = Alignment.Center,
        ) { LittleStoryIcon(LittleStoryIcons.Play, contentDescription = null, tint = colors.primary) }
    }
}

@Composable
private fun StoryRow(story: StoryItem, created: Boolean, actions: MyStoriesActions) {
    val colors = LittleStoryTheme.colors
    val spacing = LittleStoryTheme.spacing
    val (gradient, motif) = storyArtFor(story.paletteId, story.motifId)
    ListStoryRow(
        eyebrow = story.categoryName,
        title = story.title,
        meta = stringResource(R.string.stories_row_meta, story.durationMinutes, story.ageMin, story.ageMax),
        gradient = gradient,
        motif = motif,
        onClick = { if (created) actions.onCreatedStory(story.id) else actions.onStory(story.id) },
        offlineLabel = if (story.downloaded) stringResource(R.string.stories_offline) else null,
        modifier =
            Modifier
                .background(colors.surface, LittleStoryTheme.shapes.large)
                .padding(spacing.sm)
                .testTag(MyStoriesTags.story(story.id)),
        trailing = {
            LittleStoryIconButton(
                LittleStoryIcons.Play,
                stringResource(R.string.stories_play, story.title),
                { actions.onPlay(story.id) },
            )
        },
    )
}

@Composable
private fun Empty(state: MyStoriesUiState, onDiscover: () -> Unit) {
    val downloaded = state.tab == LibraryTab.Downloaded
    LittleStoryEmptyState(
        icon = if (downloaded) LittleStoryIcons.Download else LittleStoryIcons.Heart,
        title =
            stringResource(
                if (downloaded) R.string.stories_empty_downloaded_title else R.string.stories_empty_title,
            ),
        text =
            stringResource(
                if (downloaded) R.string.stories_empty_downloaded_text else R.string.stories_empty_text,
            ),
        actionLabel = stringResource(R.string.stories_empty_action),
        onAction = onDiscover,
        modifier = Modifier.testTag(MyStoriesTags.EMPTY),
    )
}
