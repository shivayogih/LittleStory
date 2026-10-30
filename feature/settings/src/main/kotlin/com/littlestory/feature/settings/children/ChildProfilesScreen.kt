package com.littlestory.feature.settings.children

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.littlestory.core.designsystem.component.LittleStoryIconButton
import com.littlestory.core.designsystem.component.LittleStoryNotesBanner
import com.littlestory.core.designsystem.icon.LittleStoryIcon
import com.littlestory.core.designsystem.icon.LittleStoryIcons
import com.littlestory.core.designsystem.theme.LittleStoryTheme
import com.littlestory.core.ui.chrome.TopBar
import com.littlestory.core.ui.story.Avatar
import com.littlestory.core.ui.system.SystemBarIcons
import com.littlestory.feature.settings.R
import com.littlestory.core.ui.R as UiR

object ChildProfilesTags {
    const val ADD = "children_add"
    const val ADD_ROW = "children_add_row"

    fun card(id: String) = "children_card_$id"

    fun switch(id: String) = "children_switch_$id"

    fun edit(id: String) = "children_edit_$id"

    fun active(id: String) = "children_active_$id"
}

private val interestLabels =
    mapOf(
        "animals" to UiR.string.interest_animals,
        "cricket" to UiR.string.interest_cricket,
        "adventure" to UiR.string.interest_adventure,
        "space" to UiR.string.interest_space,
        "nature" to UiR.string.interest_nature,
        "music" to UiR.string.interest_music,
    )

data class ChildProfilesActions(
    val onBack: () -> Unit = {},
    val onEdit: (childId: String) -> Unit = {},
    val onAdd: () -> Unit = {},
)

@Composable
fun ChildProfilesRoute(actions: ChildProfilesActions, viewModel: ChildProfilesViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ChildProfilesScreen(state, actions, viewModel::onSwitch)
}

@Composable
fun ChildProfilesScreen(
    state: ChildProfilesUiState,
    actions: ChildProfilesActions,
    onSwitch: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LittleStoryTheme.colors
    val spacing = LittleStoryTheme.spacing
    SystemBarIcons(darkIcons = true)
    Column(modifier.fillMaxSize().background(colors.background)) {
        TopBar(
            title = stringResource(R.string.children_title),
            backContentDescription = stringResource(UiR.string.action_back),
            onBack = actions.onBack,
            action = {
                Text(
                    stringResource(R.string.children_add),
                    style = LittleStoryTheme.typography.chip,
                    color = colors.primary,
                    modifier =
                        Modifier
                            .heightIn(min = LittleStoryTheme.dimens.minTouchTarget)
                            .clip(LittleStoryTheme.shapes.chip)
                            .clickable(role = Role.Button, onClick = actions.onAdd)
                            .padding(spacing.md)
                            .testTag(ChildProfilesTags.ADD),
                )
            },
        )
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = spacing.screenHorizontal, vertical = spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.lg),
        ) {
            LittleStoryNotesBanner(stringResource(R.string.children_banner))
            state.children.forEach { child -> ChildCardView(child, actions, onSwitch) }
            AddRow(actions.onAdd)
        }
    }
}

@Composable
private fun ChildCardView(child: ChildCard, actions: ChildProfilesActions, onSwitch: (String) -> Unit) {
    val colors = LittleStoryTheme.colors
    val spacing = LittleStoryTheme.spacing
    val dimens = LittleStoryTheme.dimens
    val shape = LittleStoryTheme.shapes.large
    val interests = child.interestIds.mapNotNull { id -> interestLabels[id]?.let { stringResource(it) } }
    Row(
        Modifier
            .fillMaxWidth()
            .background(colors.surface, shape)
            .border(
                if (child.active) dimens.selectedBorderWidth else dimens.borderWidth,
                if (child.active) colors.accent else colors.outline,
                shape,
            ).padding(spacing.lg)
            .testTag(ChildProfilesTags.card(child.id)),
        horizontalArrangement = Arrangement.spacedBy(spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Avatar(tone = child.avatarTone, selected = child.active)
        Column(
            Modifier.weight(1f).semantics(mergeDescendants = true) {
            },
            verticalArrangement = Arrangement.spacedBy(spacing.xxs),
        ) {
            Text(child.name, style = LittleStoryTheme.typography.itemTitle, color = colors.textPrimary)
            Text(
                pluralStringResource(R.plurals.children_age_language, child.age, child.age, child.languageName),
                style = LittleStoryTheme.typography.chip,
                color = colors.textSecondary,
            )
            if (interests.isNotEmpty()) {
                Text(
                    interests.joinToString(stringResource(R.string.children_interest_separator)),
                    style = LittleStoryTheme.typography.chip,
                    color = colors.textSecondary,
                )
            }
        }
        if (child.active) {
            Row(
                Modifier
                    .background(colors.secondaryContainer, LittleStoryTheme.shapes.chip)
                    .padding(horizontal = spacing.sm, vertical = spacing.xs)
                    .testTag(ChildProfilesTags.active(child.id)),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LittleStoryIcon(LittleStoryIcons.Check, contentDescription = null, tint = colors.primary)
                Text(
                    stringResource(R.string.children_active),
                    style = LittleStoryTheme.typography.chip,
                    color = colors.primary,
                )
            }
        } else {
            Text(
                stringResource(R.string.children_switch),
                style = LittleStoryTheme.typography.chip,
                color = colors.primary,
                modifier =
                    Modifier
                        .heightIn(min = dimens.minTouchTarget)
                        .clip(LittleStoryTheme.shapes.chip)
                        .clickable(role = Role.Button) { onSwitch(child.id) }
                        .padding(spacing.md)
                        .testTag(ChildProfilesTags.switch(child.id)),
            )
        }
        LittleStoryIconButton(
            LittleStoryIcons.Edit,
            stringResource(R.string.children_edit, child.name),
            { actions.onEdit(child.id) },
            modifier = Modifier.testTag(ChildProfilesTags.edit(child.id)),
        )
    }
}

@Composable
private fun AddRow(onAdd: () -> Unit) {
    val colors = LittleStoryTheme.colors
    val spacing = LittleStoryTheme.spacing
    Row(
        Modifier
            .fillMaxWidth()
            .clip(LittleStoryTheme.shapes.large)
            .border(LittleStoryTheme.dimens.borderWidth, colors.accent, LittleStoryTheme.shapes.large)
            .clickable(role = Role.Button, onClick = onAdd)
            .padding(spacing.lg)
            .testTag(ChildProfilesTags.ADD_ROW),
        horizontalArrangement = Arrangement.spacedBy(spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LittleStoryIcon(LittleStoryIcons.Plus, contentDescription = null, tint = colors.primary)
        Column(Modifier.weight(1f)) {
            Text(
                stringResource(R.string.children_add_another),
                style = LittleStoryTheme.typography.itemTitle,
                color = colors.textPrimary,
            )
            Text(
                stringResource(R.string.children_add_another_body),
                style = LittleStoryTheme.typography.chip,
                color = colors.textSecondary,
            )
        }
    }
}
