package com.littlestory.feature.settings.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.littlestory.core.designsystem.component.LittleStoryEmptyState
import com.littlestory.core.designsystem.icon.LittleStoryIcon
import com.littlestory.core.designsystem.icon.LittleStoryIcons
import com.littlestory.core.designsystem.theme.LittleStoryTheme
import com.littlestory.core.ui.chrome.TopBar
import com.littlestory.core.ui.system.SystemBarIcons
import com.littlestory.domain.model.NotificationItem
import com.littlestory.domain.model.NotificationKind
import com.littlestory.domain.model.NotificationSection
import com.littlestory.feature.settings.R
import com.littlestory.core.ui.R as UiR

object NotificationsTags {
    const val MARK_ALL = "notifications_mark_all"
    const val EMPTY = "notifications_empty"

    fun item(id: String) = "notifications_item_$id"

    fun unread(id: String) = "notifications_unread_$id"
}

data class NotificationsActions(
    val onBack: () -> Unit = {},
    val onStoryReady: () -> Unit = {},
    val onReminder: () -> Unit = {},
    val onNewStories: () -> Unit = {},
)

@Composable
fun NotificationsRoute(actions: NotificationsActions, viewModel: NotificationsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    NotificationsScreen(state, actions, viewModel::onOpen, viewModel::onMarkAllRead)
}

@Composable
fun NotificationsScreen(
    state: NotificationsUiState,
    actions: NotificationsActions,
    onOpen: (String) -> Unit,
    onMarkAllRead: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LittleStoryTheme.colors
    val spacing = LittleStoryTheme.spacing
    SystemBarIcons(darkIcons = true)
    Column(modifier.fillMaxSize().background(colors.background)) {
        TopBar(
            title = stringResource(R.string.notifications_title),
            backContentDescription = stringResource(UiR.string.action_back),
            onBack = actions.onBack,
            action = {
                if (state.hasUnread) {
                    Text(
                        stringResource(R.string.notifications_mark_all),
                        style = LittleStoryTheme.typography.chip,
                        color = colors.primary,
                        modifier =
                            Modifier
                                .heightIn(min = LittleStoryTheme.dimens.minTouchTarget)
                                .clip(LittleStoryTheme.shapes.chip)
                                .clickable(role = Role.Button, onClick = onMarkAllRead)
                                .padding(spacing.md)
                                .testTag(NotificationsTags.MARK_ALL),
                    )
                }
            },
        )
        if (!state.loading && state.items.isEmpty()) {
            LittleStoryEmptyState(
                icon = LittleStoryIcons.Bell,
                title = stringResource(R.string.notifications_empty_title),
                text = stringResource(R.string.notifications_empty_text),
                modifier = Modifier.testTag(NotificationsTags.EMPTY),
            )
            return@Column
        }
        LazyColumn(
            Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = spacing.screenHorizontal, vertical = spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.md),
        ) {
            NotificationSection.entries.forEach { section ->
                val inSection = state.items.filter { it.section == section }
                if (inSection.isNotEmpty()) {
                    item(key = section.name) {
                        Text(
                            stringResource(
                                if (section ==
                                    NotificationSection.Today
                                ) {
                                    R.string.notifications_today
                                } else {
                                    R.string.notifications_earlier
                                },
                            ),
                            style = LittleStoryTheme.typography.eyebrow,
                            color = colors.textSecondary,
                            modifier = Modifier.padding(top = spacing.sm),
                        )
                    }
                    items(inSection, key = { it.id }) { item ->
                        NotificationRow(item) {
                            onOpen(item.id)
                            when (item.kind) {
                                NotificationKind.StoryReady -> actions.onStoryReady()
                                NotificationKind.Reminder -> actions.onReminder()
                                NotificationKind.NewStories -> actions.onNewStories()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationRow(item: NotificationItem, onClick: () -> Unit) {
    val colors = LittleStoryTheme.colors
    val spacing = LittleStoryTheme.spacing
    val dimens = LittleStoryTheme.dimens
    val (icon, tile) =
        when (item.kind) {
            NotificationKind.StoryReady -> LittleStoryIcons.Book to colors.secondaryContainer
            NotificationKind.Reminder -> LittleStoryIcons.Moon to colors.categoryTiles.peach
            NotificationKind.NewStories -> LittleStoryIcons.Compass to colors.categoryTiles.butter
        }
    val unreadLabel = stringResource(R.string.notifications_unread)
    Row(
        Modifier
            .fillMaxWidth()
            .clip(LittleStoryTheme.shapes.large)
            .background(colors.surface)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics(mergeDescendants = true) { if (!item.read) stateDescription = unreadLabel }
            .padding(spacing.lg)
            .testTag(NotificationsTags.item(item.id)),
        horizontalArrangement = Arrangement.spacedBy(spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(dimens.settingsIconTile).background(tile, LittleStoryTheme.shapes.small),
            contentAlignment = Alignment.Center,
        ) {
            LittleStoryIcon(icon, contentDescription = null, tint = colors.primary)
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(spacing.xxs)) {
            Text(item.title, style = LittleStoryTheme.typography.itemTitle, color = colors.textPrimary)
            Text(item.body, style = LittleStoryTheme.typography.chip, color = colors.textSecondary)
        }
        if (!item.read) {
            Box(
                Modifier
                    .size(dimens.unreadDotSize)
                    .background(colors.accent, LittleStoryTheme.shapes.iconButton)
                    .testTag(NotificationsTags.unread(item.id)),
            )
        }
    }
}
