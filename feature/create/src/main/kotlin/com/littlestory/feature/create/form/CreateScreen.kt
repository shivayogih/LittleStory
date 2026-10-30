package com.littlestory.feature.create.form

import androidx.annotation.StringRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.littlestory.core.common.config.AppConfig
import com.littlestory.core.common.language.SupportedLanguages
import com.littlestory.core.designsystem.component.LittleStoryButton
import com.littlestory.core.designsystem.component.LittleStoryChip
import com.littlestory.core.designsystem.component.LittleStoryNotesBanner
import com.littlestory.core.designsystem.component.LittleStorySelect
import com.littlestory.core.designsystem.icon.LittleStoryIcon
import com.littlestory.core.designsystem.icon.LittleStoryIcons
import com.littlestory.core.designsystem.theme.LittleStoryTheme
import com.littlestory.core.ui.chrome.TopBar
import com.littlestory.core.ui.illustration.Sparkle
import com.littlestory.core.ui.story.Avatar
import com.littlestory.core.ui.system.SystemBarIcons
import com.littlestory.feature.create.R

object CreateTags {
    const val PROMPT = "create_prompt"
    const val COUNTER = "create_counter"
    const val ERROR = "create_error"
    const val MORE_OPTIONS = "create_more_options"
    const val SUBMIT = "create_submit"
    const val HERO = "create_hero"

    fun suggestion(index: Int) = "create_suggestion_$index"

    fun theme(id: String) = "create_theme_$id"

    fun mood(id: String) = "create_mood_$id"

    fun duration(minutes: Int) = "create_duration_$minutes"
}

// The design rotates the "More options" chevron by half a turn when open.
private const val CHEVRON_OPEN_DEGREES = 180f
private const val CHEVRON_CLOSED_DEGREES = 90f

@Composable
fun CreateRoute(onGenerate: (requestId: String) -> Unit, viewModel: CreateViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is CreateEffect.StartGeneration -> onGenerate(effect.requestId)
            }
        }
    }
    CreateScreen(
        state = state,
        callbacks =
            CreateCallbacks(
                onPromptChange = viewModel::onPromptChange,
                onHeroSelected = viewModel::onHeroSelected,
                onThemeSelected = viewModel::onThemeSelected,
                onMoodSelected = viewModel::onMoodSelected,
                onDurationSelected = viewModel::onDurationSelected,
                onLanguageSelected = viewModel::onLanguageSelected,
                onToggleMoreOptions = viewModel::onToggleMoreOptions,
                onCreate = viewModel::onCreate,
            ),
    )
}

data class CreateCallbacks(
    val onPromptChange: (String) -> Unit = {},
    val onHeroSelected: (String) -> Unit = {},
    val onThemeSelected: (String) -> Unit = {},
    val onMoodSelected: (String) -> Unit = {},
    val onDurationSelected: (Int) -> Unit = {},
    val onLanguageSelected: (String) -> Unit = {},
    val onToggleMoreOptions: () -> Unit = {},
    val onCreate: () -> Unit = {},
)

@Composable
fun CreateScreen(state: CreateUiState, callbacks: CreateCallbacks, modifier: Modifier = Modifier) {
    val colors = LittleStoryTheme.colors
    val spacing = LittleStoryTheme.spacing
    SystemBarIcons(darkIcons = true)
    Column(modifier.fillMaxSize().background(colors.background)) {
        TopBar(title = stringResource(R.string.create_title), action = { ParentGuided() })
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(bottom = spacing.screenBottom),
            verticalArrangement = Arrangement.spacedBy(spacing.xl),
        ) {
            Intro(Modifier.padding(horizontal = spacing.screenHorizontal))
            if (state.showEmptyError) ErrorBanner(Modifier.padding(horizontal = spacing.screenHorizontal))
            PromptCard(state, callbacks.onPromptChange, Modifier.padding(horizontal = spacing.screenHorizontal))
            Suggestions(state.hero?.name, callbacks.onPromptChange)
            state.hero?.let {
                HeroCard(
                    state,
                    callbacks.onHeroSelected,
                    Modifier.padding(horizontal = spacing.screenHorizontal),
                )
            }
            ThemeSection(
                state.themeId,
                callbacks.onThemeSelected,
                Modifier.padding(horizontal = spacing.screenHorizontal),
            )
            MoreOptions(state, callbacks, Modifier.padding(horizontal = spacing.screenHorizontal))
            LittleStoryNotesBanner(
                text = stringResource(R.string.create_safety_note),
                icon = LittleStoryIcons.Moon,
                modifier = Modifier.padding(horizontal = spacing.screenHorizontal),
            )
            LittleStoryButton(
                text = stringResource(R.string.create_submit),
                onClick = callbacks.onCreate,
                modifier = Modifier.padding(horizontal = spacing.screenHorizontal).testTag(CreateTags.SUBMIT),
            )
        }
    }
}

@Composable
private fun ParentGuided() {
    val colors = LittleStoryTheme.colors
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(LittleStoryTheme.spacing.xxs),
    ) {
        LittleStoryIcon(
            LittleStoryIcons.Check,
            contentDescription = null,
            tint = colors.success,
            modifier = Modifier.size(LittleStoryTheme.dimens.iconSizeSmall),
        )
        Text(
            stringResource(R.string.create_parent_guided),
            style = LittleStoryTheme.typography.chip,
            color = colors.success,
        )
    }
}

@Composable
private fun Intro(modifier: Modifier) {
    val colors = LittleStoryTheme.colors
    val spacing = LittleStoryTheme.spacing
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(spacing.lg)) {
        Box(
            Modifier
                .size(LittleStoryTheme.dimens.createOrbSize)
                .background(colors.primary, LittleStoryTheme.shapes.medium),
            contentAlignment = Alignment.Center,
        ) { Sparkle() }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
            Text(
                stringResource(R.string.create_intro_title),
                style = LittleStoryTheme.typography.homeTitle,
                color = colors.textPrimary,
            )
            Text(
                stringResource(R.string.create_intro_body),
                style = LittleStoryTheme.typography.body,
                color = colors.textSecondary,
            )
        }
    }
}

@Composable
private fun ErrorBanner(modifier: Modifier) {
    val colors = LittleStoryTheme.colors
    Text(
        stringResource(R.string.create_error_empty),
        style = LittleStoryTheme.typography.chip,
        color = colors.onPrimary,
        modifier =
            modifier
                .fillMaxWidth()
                .background(colors.error, LittleStoryTheme.shapes.medium)
                .padding(LittleStoryTheme.spacing.md)
                .semantics { liveRegion = LiveRegionMode.Assertive }
                .testTag(CreateTags.ERROR),
    )
}

@Composable
private fun PromptCard(state: CreateUiState, onPromptChange: (String) -> Unit, modifier: Modifier) {
    val colors = LittleStoryTheme.colors
    val spacing = LittleStoryTheme.spacing
    val label = stringResource(R.string.create_prompt_label)
    val placeholder =
        state.hero?.name?.let { stringResource(R.string.create_prompt_placeholder_named, it) }
            ?: stringResource(R.string.create_prompt_placeholder)
    Column(
        modifier
            .fillMaxWidth()
            .background(colors.surface, LittleStoryTheme.shapes.large)
            .border(
                LittleStoryTheme.dimens.borderWidth,
                if (state.showEmptyError) colors.error else colors.outline,
                LittleStoryTheme.shapes.large,
            ).padding(spacing.lg),
        verticalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        Text(label, style = LittleStoryTheme.typography.bodyLarge, color = colors.textPrimary)
        BasicTextField(
            value = state.prompt,
            onValueChange = onPromptChange,
            textStyle = LittleStoryTheme.typography.bodyLarge.copy(color = colors.textPrimary),
            cursorBrush = SolidColor(colors.primary),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .heightIn(min = LittleStoryTheme.dimens.promptMinHeight)
                    .semantics { contentDescription = label }
                    .testTag(CreateTags.PROMPT),
            decorationBox = { inner ->
                if (state.prompt.isEmpty()) {
                    Text(placeholder, style = LittleStoryTheme.typography.bodyLarge, color = colors.textSecondary)
                }
                inner()
            },
        )
        Text(
            stringResource(R.string.create_counter, state.prompt.length, state.maxChars),
            style = LittleStoryTheme.typography.chip,
            color = colors.textSecondary,
            modifier = Modifier.align(Alignment.End).testTag(CreateTags.COUNTER),
        )
    }
}

private val suggestionLabels =
    listOf(
        R.string.create_suggestion_school,
        R.string.create_suggestion_family,
        R.string.create_suggestion_funny,
        R.string.create_suggestion_learned,
        R.string.create_suggestion_kindness,
    )

private val suggestionFills =
    listOf(
        R.string.create_fill_school,
        R.string.create_fill_family,
        R.string.create_fill_funny,
        R.string.create_fill_learned,
        R.string.create_fill_kindness,
    )

@Composable
private fun Suggestions(heroName: String?, onPromptChange: (String) -> Unit) {
    val spacing = LittleStoryTheme.spacing
    val name = heroName ?: stringResource(R.string.create_hero_fallback)
    val fills = suggestionFills.map { stringResource(it, name) }
    LazyRow(
        contentPadding = PaddingValues(horizontal = spacing.screenHorizontal),
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        itemsIndexed(suggestionLabels.take(AppConfig.Create.suggestionIds.size)) { index, label ->
            SuggestionPill(
                text = stringResource(label),
                onClick = { onPromptChange(fills[index]) },
                modifier = Modifier.testTag(CreateTags.suggestion(index)),
            )
        }
    }
}

@Composable
private fun SuggestionPill(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = LittleStoryTheme.colors
    val shape = LittleStoryTheme.shapes.chip
    Row(
        modifier
            .heightIn(min = LittleStoryTheme.dimens.minTouchTarget)
            .clip(shape)
            .background(colors.surface)
            .border(LittleStoryTheme.dimens.borderWidth, colors.outline, shape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = LittleStoryTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(LittleStoryTheme.spacing.xs),
    ) {
        LittleStoryIcon(
            LittleStoryIcons.Plus,
            contentDescription = null,
            tint = colors.primary,
            modifier = Modifier.size(LittleStoryTheme.dimens.iconSizeSmall),
        )
        Text(text, style = LittleStoryTheme.typography.chip, color = colors.textPrimary)
    }
}

@Composable
private fun HeroCard(state: CreateUiState, onHeroSelected: (String) -> Unit, modifier: Modifier) {
    val hero = state.hero ?: return
    val colors = LittleStoryTheme.colors
    val spacing = LittleStoryTheme.spacing
    Column(
        modifier
            .fillMaxWidth()
            .background(colors.surface, LittleStoryTheme.shapes.large)
            .border(LittleStoryTheme.dimens.borderWidth, colors.outline, LittleStoryTheme.shapes.large)
            .padding(spacing.lg)
            .testTag(CreateTags.HERO),
        verticalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.md), verticalAlignment = Alignment.CenterVertically) {
            Avatar(tone = hero.avatarTone, selected = false)
            Column(Modifier.semantics(mergeDescendants = true) { }) {
                Text(
                    stringResource(R.string.create_hero_eyebrow),
                    style = LittleStoryTheme.typography.eyebrow,
                    color = colors.textSecondary,
                )
                Text(
                    pluralStringResource(R.plurals.create_hero_name_age, hero.age, hero.name, hero.age),
                    style = LittleStoryTheme.typography.bodyLarge,
                    color = colors.textPrimary,
                )
            }
        }
        if (state.heroes.size > 1) {
            LittleStorySelect(
                selected = hero.name,
                options = state.heroes.map { it.name },
                onSelect = { index -> onHeroSelected(state.heroes[index].id) },
                label = stringResource(R.string.create_hero_change),
            )
        }
    }
}

private val themeLabels =
    mapOf(
        "adventure" to R.string.create_theme_adventure,
        "bedtime" to R.string.create_theme_bedtime,
        "animals" to R.string.create_theme_animals,
        "friendship" to R.string.create_theme_friendship,
        "fantasy" to R.string.create_theme_fantasy,
        "learning" to R.string.create_theme_learning,
        "indian_folklore" to R.string.create_theme_indian_folklore,
    )

private val moodLabels =
    mapOf(
        "calm" to R.string.create_mood_calm,
        "funny" to R.string.create_mood_funny,
        "magical" to R.string.create_mood_magical,
        "adventurous" to R.string.create_mood_adventurous,
    )

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ThemeSection(selected: String, onSelect: (String) -> Unit, modifier: Modifier) {
    ChipGroup(
        title = R.string.create_theme_title,
        ids = AppConfig.Create.themes,
        labels = themeLabels,
        selected = selected,
        tag = CreateTags::theme,
        onSelect = onSelect,
        modifier = modifier,
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChipGroup(
    @StringRes title: Int,
    ids: List<String>,
    labels: Map<String, Int>,
    selected: String,
    tag: (String) -> String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LittleStoryTheme.spacing
    Column(modifier, verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
        Text(
            stringResource(title),
            style = LittleStoryTheme.typography.bodyLarge,
            color = LittleStoryTheme.colors.textPrimary,
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            verticalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            ids.forEach { id ->
                val label = labels[id] ?: return@forEach
                LittleStoryChip(
                    text = stringResource(label),
                    selected = id == selected,
                    onClick = { onSelect(id) },
                    modifier = Modifier.testTag(tag(id)),
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MoreOptions(state: CreateUiState, callbacks: CreateCallbacks, modifier: Modifier) {
    val colors = LittleStoryTheme.colors
    val spacing = LittleStoryTheme.spacing
    val motion = LittleStoryTheme.motion
    val openDegrees = CHEVRON_OPEN_DEGREES + CHEVRON_CLOSED_DEGREES
    val rotation by animateFloatAsState(
        targetValue = if (state.moreOptionsExpanded) openDegrees else CHEVRON_CLOSED_DEGREES,
        animationSpec = tween(motion.duration(motion.chevronMillis)),
        label = "chevron",
    )
    val expandedText = stringResource(R.string.create_more_options_expanded)
    val collapsedText = stringResource(R.string.create_more_options_collapsed)
    Column(modifier, verticalArrangement = Arrangement.spacedBy(spacing.lg)) {
        Box(Modifier.fillMaxWidth().background(colors.outline).heightIn(min = LittleStoryTheme.dimens.borderWidth))
        Row(
            Modifier
                .fillMaxWidth()
                .heightIn(min = LittleStoryTheme.dimens.minTouchTarget)
                .clickable(role = Role.Button, onClick = callbacks.onToggleMoreOptions)
                .semantics { stateDescription = if (state.moreOptionsExpanded) expandedText else collapsedText }
                .testTag(CreateTags.MORE_OPTIONS),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                stringResource(R.string.create_more_options),
                style = LittleStoryTheme.typography.bodyLarge,
                color = colors.primary,
                modifier = Modifier.weight(1f),
            )
            LittleStoryIcon(
                LittleStoryIcons.Chevron,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.rotate(rotation),
            )
        }
        if (state.moreOptionsExpanded) {
            ChipGroup(
                title = R.string.create_mood_title,
                ids = AppConfig.Create.moods,
                labels = moodLabels,
                selected = state.moodId,
                tag = CreateTags::mood,
                onSelect = callbacks.onMoodSelected,
            )
            Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                Text(
                    stringResource(R.string.create_duration_title),
                    style = LittleStoryTheme.typography.bodyLarge,
                    color = colors.textPrimary,
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                    verticalArrangement = Arrangement.spacedBy(spacing.sm),
                ) {
                    AppConfig.Create.durationOptionsMinutes.forEach { minutes ->
                        LittleStoryChip(
                            text = pluralStringResource(R.plurals.create_duration_minutes, minutes, minutes),
                            selected = minutes == state.durationMinutes,
                            onClick = { callbacks.onDurationSelected(minutes) },
                            modifier = Modifier.testTag(CreateTags.duration(minutes)),
                        )
                    }
                }
            }
            val languages = SupportedLanguages.enabled
            LittleStorySelect(
                selected = SupportedLanguages.fromTag(state.languageTag).nativeName,
                options = languages.map { it.nativeName },
                onSelect = { index -> callbacks.onLanguageSelected(languages[index].tag) },
                label = stringResource(R.string.create_language_title),
            )
        }
        Box(Modifier.fillMaxWidth().background(colors.outline).heightIn(min = LittleStoryTheme.dimens.borderWidth))
    }
}
