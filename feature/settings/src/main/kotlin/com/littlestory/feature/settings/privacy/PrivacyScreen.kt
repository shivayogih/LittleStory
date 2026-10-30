package com.littlestory.feature.settings.privacy

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.littlestory.core.designsystem.component.ButtonVariant
import com.littlestory.core.designsystem.component.LittleStoryButton
import com.littlestory.core.designsystem.component.LittleStoryConfirmDialog
import com.littlestory.core.designsystem.component.LittleStoryNotesBanner
import com.littlestory.core.designsystem.theme.LittleStoryTheme
import com.littlestory.core.ui.chrome.TopBar
import com.littlestory.core.ui.language.AppLanguage
import com.littlestory.core.ui.system.SystemBarIcons
import com.littlestory.feature.settings.R
import com.littlestory.feature.settings.SettingsDivider
import com.littlestory.feature.settings.SettingsGroup
import com.littlestory.feature.settings.SettingsParagraph
import com.littlestory.core.ui.R as UiR

object PrivacyTags {
    const val DELETE = "privacy_delete"
    const val CONFIRM = "privacy_confirm"
}

data class PrivacyCallbacks(
    val onBack: () -> Unit = {},
    val onDeleteClick: () -> Unit = {},
    val onConfirmDelete: () -> Unit = {},
    val onConfirmDismiss: () -> Unit = {},
)

@Composable
fun PrivacyRoute(onBack: () -> Unit, onDeleted: () -> Unit, viewModel: PrivacyViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val deleted by rememberUpdatedState(onDeleted)
    LaunchedEffect(viewModel) {
        viewModel.effects.collect {
            AppLanguage.reset()
            deleted()
        }
    }
    PrivacyScreen(
        state,
        PrivacyCallbacks(onBack, viewModel::onDeleteClick, viewModel::onConfirmDelete, viewModel::onConfirmDismiss),
    )
}

/** SCR-21 (LS-114), built from settings components until a design exists (D10). */
@Composable
fun PrivacyScreen(state: PrivacyUiState, callbacks: PrivacyCallbacks, modifier: Modifier = Modifier) {
    val colors = LittleStoryTheme.colors
    val spacing = LittleStoryTheme.spacing
    SystemBarIcons(darkIcons = true)
    Column(modifier.fillMaxSize().background(colors.background)) {
        TopBar(
            title = stringResource(R.string.privacy_title),
            backContentDescription = stringResource(UiR.string.action_back),
            onBack = callbacks.onBack,
        )
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = spacing.screenHorizontal, vertical = spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.xl),
        ) {
            LittleStoryNotesBanner(text = stringResource(R.string.privacy_banner))
            SettingsGroup(stringResource(R.string.privacy_group_data)) {
                SettingsParagraph(
                    stringResource(R.string.privacy_stored_title),
                    stringResource(R.string.privacy_stored_body),
                )
                SettingsDivider()
                SettingsParagraph(
                    stringResource(R.string.privacy_shared_title),
                    stringResource(R.string.privacy_shared_body),
                )
                SettingsDivider()
                SettingsParagraph(
                    stringResource(R.string.privacy_creation_title),
                    stringResource(R.string.privacy_creation_body),
                )
            }
            SettingsGroup(stringResource(R.string.privacy_group_controls)) {
                SettingsParagraph(
                    stringResource(R.string.privacy_delete_title),
                    stringResource(R.string.privacy_delete_body),
                )
                LittleStoryButton(
                    text = stringResource(R.string.privacy_delete_action),
                    onClick = callbacks.onDeleteClick,
                    variant = ButtonVariant.Secondary,
                    enabled = !state.deleting,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(start = spacing.lg, end = spacing.lg, bottom = spacing.lg)
                            .testTag(PrivacyTags.DELETE),
                )
            }
        }
    }
    if (state.confirmOpen) {
        LittleStoryConfirmDialog(
            title = stringResource(R.string.privacy_confirm_title),
            text = stringResource(R.string.privacy_confirm_body),
            confirmLabel = stringResource(R.string.privacy_confirm_action),
            dismissLabel = stringResource(R.string.profile_cancel),
            onConfirm = callbacks.onConfirmDelete,
            onDismiss = callbacks.onConfirmDismiss,
            modifier = Modifier.testTag(PrivacyTags.CONFIRM),
        )
    }
}
