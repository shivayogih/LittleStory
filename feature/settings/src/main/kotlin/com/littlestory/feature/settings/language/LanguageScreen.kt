package com.littlestory.feature.settings.language

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.littlestory.core.common.language.SupportedLanguage
import com.littlestory.core.designsystem.component.LittleStoryNotesBanner
import com.littlestory.core.designsystem.icon.LittleStoryIcons
import com.littlestory.core.designsystem.theme.LittleStoryTheme
import com.littlestory.core.ui.chrome.TopBar
import com.littlestory.core.ui.language.AppLanguage
import com.littlestory.core.ui.system.SystemBarIcons
import com.littlestory.feature.settings.R
import com.littlestory.feature.settings.SettingsChoiceRow
import com.littlestory.feature.settings.SettingsDivider
import com.littlestory.feature.settings.SettingsGroup
import com.littlestory.core.ui.R as UiR

object LanguageTags {
    fun app(tag: String) = "language_app_$tag"

    fun story(tag: String) = "language_story_$tag"
}

data class LanguageCallbacks(
    val onBack: () -> Unit = {},
    val onAppLanguage: (SupportedLanguage) -> Unit = {},
    val onStoryLanguage: (SupportedLanguage) -> Unit = {},
)

@Composable
fun LanguageRoute(onBack: () -> Unit, viewModel: LanguageViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    // The UI language belongs to AppCompat (ADR-010); applying it recreates the activity.
    var appLanguage by remember { mutableStateOf(AppLanguage.current()) }
    LanguageScreen(
        state.copy(appLanguageTag = appLanguage.tag),
        LanguageCallbacks(
            onBack = onBack,
            onAppLanguage = {
                appLanguage = it
                AppLanguage.apply(it)
            },
            onStoryLanguage = viewModel::onStoryLanguageSelected,
        ),
    )
}

/** SCR-20 (LS-046): app language and story language, listed from the language registry. */
@Composable
fun LanguageScreen(state: LanguageUiState, callbacks: LanguageCallbacks, modifier: Modifier = Modifier) {
    val colors = LittleStoryTheme.colors
    val spacing = LittleStoryTheme.spacing
    SystemBarIcons(darkIcons = true)
    Column(modifier.fillMaxSize().background(colors.background)) {
        TopBar(
            title = stringResource(R.string.language_title),
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
            LanguageGroup(
                title = stringResource(R.string.language_app_group),
                languages = state.languages,
                selectedTag = state.appLanguageTag,
                tag = LanguageTags::app,
                onSelect = callbacks.onAppLanguage,
            )
            LanguageGroup(
                title = stringResource(R.string.language_story_group),
                languages = state.languages,
                selectedTag = state.storyLanguageTag,
                tag = LanguageTags::story,
                onSelect = callbacks.onStoryLanguage,
            )
            LittleStoryNotesBanner(
                icon = LittleStoryIcons.Book,
                text = stringResource(R.string.language_fallback_note),
            )
        }
    }
}

@Composable
private fun LanguageGroup(
    title: String,
    languages: List<SupportedLanguage>,
    selectedTag: String,
    tag: (String) -> String,
    onSelect: (SupportedLanguage) -> Unit,
) {
    SettingsGroup(title, Modifier.selectableGroup()) {
        languages.forEachIndexed { index, language ->
            if (index > 0) SettingsDivider()
            SettingsChoiceRow(
                language.nativeName,
                language.tag == selectedTag,
                tag(language.tag),
            ) { onSelect(language) }
        }
    }
}
