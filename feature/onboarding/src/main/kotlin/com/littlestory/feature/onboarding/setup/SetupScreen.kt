package com.littlestory.feature.onboarding.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.littlestory.core.common.config.AppConfig
import com.littlestory.core.common.language.SupportedLanguage
import com.littlestory.core.common.language.SupportedLanguages
import com.littlestory.core.designsystem.component.LittleStoryChip
import com.littlestory.core.designsystem.component.LittleStoryNotesBanner
import com.littlestory.core.designsystem.component.LittleStorySegmentedControl
import com.littlestory.core.designsystem.component.LittleStorySelect
import com.littlestory.core.designsystem.component.LittleStoryTextField
import com.littlestory.core.designsystem.icon.LittleStoryIcons
import com.littlestory.core.designsystem.theme.LittleStoryTheme
import com.littlestory.core.ui.chrome.StickyActionBar
import com.littlestory.core.ui.chrome.TopBar
import com.littlestory.core.ui.language.AppLanguage
import com.littlestory.core.ui.story.Avatar
import com.littlestory.core.ui.system.SystemBarIcons
import com.littlestory.core.ui.text.asString
import com.littlestory.feature.onboarding.R
import com.littlestory.core.ui.R as UiR

/** Test tags for Setup. */
object SetupTags {
    const val NAME = "setup_name"
    const val SUBMIT = "setup_submit"
    const val SKIP = "setup_skip"
    const val LANGUAGE = "setup_language"
    fun avatar(index: Int) = "setup_avatar_$index"
    fun interest(id: String) = "setup_interest_$id"
    fun duration(minutes: Int) = "setup_duration_$minutes"
}

@Composable
fun SetupRoute(onDone: () -> Unit, viewModel: SetupViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel) { viewModel.onUiLanguageKnown(AppLanguage.current().tag) }
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                SetupEffect.Done -> onDone()
                is SetupEffect.ApplyLanguage -> AppLanguage.apply(SupportedLanguages.fromTag(effect.tag))
            }
        }
    }
    SetupScreen(
        state = state,
        languages = SupportedLanguages.enabled,
        onNameChange = viewModel::onNameChange,
        onAvatarSelected = viewModel::onAvatarSelected,
        onAgeSelected = viewModel::onAgeSelected,
        onLanguageSelected = viewModel::onLanguageSelected,
        onInterestToggled = viewModel::onInterestToggled,
        onDurationSelected = viewModel::onDurationSelected,
        onSubmit = viewModel::onSubmit,
        onSkip = viewModel::onSkip,
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SetupScreen(
    state: SetupUiState,
    languages: List<SupportedLanguage>,
    onNameChange: (String) -> Unit,
    onAvatarSelected: (Int) -> Unit,
    onAgeSelected: (Int) -> Unit,
    onLanguageSelected: (String) -> Unit,
    onInterestToggled: (String) -> Unit,
    onDurationSelected: (Int) -> Unit,
    onSubmit: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LittleStoryTheme.colors
    val spacing = LittleStoryTheme.spacing
    val typography = LittleStoryTheme.typography
    SystemBarIcons(darkIcons = true)
    Column(modifier.fillMaxSize().background(colors.background).imePadding()) {
        TopBar(action = { if (!state.isEditing) SkipAction(onSkip) })
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = spacing.screenHorizontal, vertical = spacing.screenTop),
            verticalArrangement = Arrangement.spacedBy(spacing.lg),
        ) {
            LittleStoryNotesBanner(stringResource(R.string.setup_badge), icon = LittleStoryIcons.Heart)
            Text(stringResource(R.string.setup_title), style = typography.displaySetup, color = colors.textPrimary)
            Text(stringResource(R.string.setup_subtitle), style = typography.bodyLarge, color = colors.textSecondary)
            Column(
                Modifier.background(colors.surface, LittleStoryTheme.shapes.large).padding(spacing.lg),
                verticalArrangement = Arrangement.spacedBy(spacing.lg),
            ) {
                LittleStoryTextField(
                    value = state.name,
                    onValueChange = onNameChange,
                    label = stringResource(R.string.setup_label_name),
                    placeholder = stringResource(R.string.setup_placeholder_name),
                    error = state.nameError?.asString(),
                    inputModifier = Modifier.testTag(SetupTags.NAME),
                )
                Label(stringResource(R.string.setup_label_avatar)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(spacing.md)) {
                        repeat(AppConfig.Setup.AVATAR_COUNT) { index ->
                            Box(
                                Modifier
                                    .selectable(
                                        selected = state.avatar == index,
                                        role = Role.RadioButton,
                                        onClick = { onAvatarSelected(index) },
                                    ).testTag(SetupTags.avatar(index)),
                            ) { Avatar(tone = index, selected = state.avatar == index) }
                        }
                    }
                }
                val ages = AppConfig.Setup.ageRange.toList()
                val ageLabels = ages.map { pluralStringResource(R.plurals.setup_age_years, it, it) }
                LittleStorySelect(
                    selected = pluralStringResource(R.plurals.setup_age_years, state.age, state.age),
                    options = ageLabels,
                    onSelect = { onAgeSelected(ages[it]) },
                    label = stringResource(R.string.setup_label_age),
                )
                LittleStorySelect(
                    selected = languages.firstOrNull { it.tag == state.languageTag }?.nativeName.orEmpty(),
                    options = languages.map { it.nativeName },
                    onSelect = { onLanguageSelected(languages[it].tag) },
                    label = stringResource(R.string.setup_label_language),
                    modifier = Modifier.testTag(SetupTags.LANGUAGE),
                )
                Label(stringResource(R.string.setup_label_interests)) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                        verticalArrangement = Arrangement.spacedBy(spacing.sm),
                    ) {
                        AppConfig.Setup.interestIds.forEach { id ->
                            LittleStoryChip(
                                text = interestLabel(id),
                                selected = id in state.interestIds,
                                onClick = { onInterestToggled(id) },
                                multiSelect = true,
                                modifier = Modifier.testTag(SetupTags.interest(id)),
                            )
                        }
                    }
                }
                Label(stringResource(R.string.setup_label_duration)) {
                    val options = AppConfig.Setup.durationOptionsMinutes
                    LittleStorySegmentedControl(
                        options = options.map { stringResource(UiR.string.meta_minutes, it) },
                        selectedIndex = options.indexOf(state.durationMinutes),
                        onSelect = { onDurationSelected(options[it]) },
                    )
                }
            }
        }
        StickyActionBar(
            primaryLabel = state.ctaLabel.asString(),
            onPrimary = onSubmit,
            primaryEnabled = !state.loading,
            modifier = Modifier.testTag(SetupTags.SUBMIT),
        )
    }
}

@Composable
private fun SkipAction(onSkip: () -> Unit) {
    Box(
        Modifier
            .heightIn(min = LittleStoryTheme.dimens.minTouchTarget)
            .clip(LittleStoryTheme.shapes.chip)
            .clickable(role = Role.Button, onClick = onSkip)
            .padding(horizontal = LittleStoryTheme.spacing.md)
            .testTag(SetupTags.SKIP),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            stringResource(R.string.setup_skip),
            style = LittleStoryTheme.typography.button,
            color = LittleStoryTheme.colors.primary,
        )
    }
}

/** Form row with a 12 sp muted label above [content]. */
@Composable
private fun Label(text: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(LittleStoryTheme.spacing.xs)) {
        Text(text, style = LittleStoryTheme.typography.chip, color = LittleStoryTheme.colors.textSecondary)
        content()
    }
}

@Composable
private fun interestLabel(id: String): String =
    stringResource(
        when (id) {
            "animals" -> UiR.string.interest_animals
            "cricket" -> UiR.string.interest_cricket
            "adventure" -> UiR.string.interest_adventure
            "space" -> UiR.string.interest_space
            "nature" -> UiR.string.interest_nature
            else -> UiR.string.interest_music
        },
    )
