package com.littlestory.feature.create.generating

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.littlestory.core.designsystem.component.ButtonVariant
import com.littlestory.core.designsystem.component.LittleStoryButton
import com.littlestory.core.designsystem.component.LittleStoryGenerationProgress
import com.littlestory.core.designsystem.component.LittleStoryIconButton
import com.littlestory.core.designsystem.icon.LittleStoryIcon
import com.littlestory.core.designsystem.icon.LittleStoryIcons
import com.littlestory.core.designsystem.theme.LittleStoryTheme
import com.littlestory.core.ui.illustration.NightSky
import com.littlestory.core.ui.illustration.Sparkle
import com.littlestory.core.ui.system.SystemBarIcons
import com.littlestory.domain.model.GenerationStage
import com.littlestory.feature.create.R

object GeneratingTags {
    const val PERCENT = "generating_percent"
    const val CLOSE = "generating_close"
    const val PAUSED = "generating_paused"
    const val TRY_AGAIN = "generating_try_again"
    const val BACK_TO_IDEA = "generating_back_to_idea"
    const val SIMULATED = "generating_simulated"
}

private const val PERCENT_MAX = 100f

private val stageLabels =
    mapOf(
        GenerationStage.Imagining to R.string.generating_stage_imagining,
        GenerationStage.Writing to R.string.generating_stage_writing,
        GenerationStage.PreparingNarration to R.string.generating_stage_narration,
    )

@Composable
fun GeneratingRoute(
    onReady: (storyId: String) -> Unit,
    onBackToIdea: () -> Unit,
    viewModel: GeneratingViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is GeneratingEffect.Ready -> onReady(effect.storyId)
            }
        }
    }
    GeneratingScreen(state, viewModel::onClose, viewModel::onTryAgain, onBackToIdea)
}

@Composable
fun GeneratingScreen(
    state: GeneratingUiState,
    onClose: () -> Unit,
    onTryAgain: () -> Unit,
    onBackToIdea: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LittleStoryTheme.spacing
    SystemBarIcons(darkIcons = false)
    NightSky(modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = spacing.screenHorizontal, vertical = spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing.xl),
        ) {
            if (state.paused) {
                Paused(onTryAgain, onBackToIdea)
            } else {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                    LittleStoryIconButton(
                        LittleStoryIcons.Close,
                        stringResource(R.string.generating_close),
                        onClose,
                        modifier = Modifier.testTag(GeneratingTags.CLOSE),
                    )
                }
                Progress(state)
            }
        }
    }
}

@Composable
private fun MagicBook() {
    val colors = LittleStoryTheme.colors
    val dimens = LittleStoryTheme.dimens
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Sparkle()
        Row(horizontalArrangement = Arrangement.spacedBy(dimens.borderWidth)) {
            repeat(BOOK_PAGES) {
                Box(
                    Modifier
                        .size(dimens.magicBookWidth / BOOK_PAGES, dimens.magicBookHeight)
                        .background(colors.moon, LittleStoryTheme.shapes.medium),
                )
            }
        }
    }
}

@Composable
private fun Progress(state: GeneratingUiState) {
    val colors = LittleStoryTheme.colors
    val spacing = LittleStoryTheme.spacing
    MagicBook()
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
        Text(
            if (state.heroName.isBlank()) {
                stringResource(R.string.generating_eyebrow)
            } else {
                stringResource(R.string.generating_eyebrow_named, state.heroName)
            },
            style = LittleStoryTheme.typography.heroKicker,
            color = colors.highlight,
            textAlign = TextAlign.Center,
        )
        Text(
            stringResource(R.string.generating_title),
            style = LittleStoryTheme.typography.heroTitle,
            color = colors.onNight,
            textAlign = TextAlign.Center,
        )
        Text(
            stringResource(R.string.generating_body),
            style = LittleStoryTheme.typography.body,
            color = colors.secondaryContainer,
            textAlign = TextAlign.Center,
        )
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
        LittleStoryGenerationProgress(state.percent / PERCENT_MAX)
        Text(
            stringResource(R.string.generating_percent, state.percent),
            style = LittleStoryTheme.typography.chip,
            color = colors.onNight,
            modifier = Modifier.testTag(GeneratingTags.PERCENT),
        )
    }
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.md)) {
        GenerationStage.entries.forEach { stage ->
            Stage(stage.ordinal + 1, stageLabels.getValue(stage), stage, state.stage)
        }
    }
    Text(
        stringResource(R.string.generating_footnote),
        style = LittleStoryTheme.typography.chip,
        color = colors.secondaryContainer,
        textAlign = TextAlign.Center,
    )
    // NFR-12: generation is a local simulation until a real provider is approved; say so.
    Text(
        stringResource(R.string.generating_simulated),
        style = LittleStoryTheme.typography.chip,
        color = colors.secondaryContainer,
        textAlign = TextAlign.Center,
        modifier = Modifier.testTag(GeneratingTags.SIMULATED),
    )
}

@Composable
private fun Stage(
    number: Int,
    @StringRes label: Int,
    stage: GenerationStage,
    current: GenerationStage,
) {
    val colors = LittleStoryTheme.colors
    val dimens = LittleStoryTheme.dimens
    val done = stage.ordinal < current.ordinal
    val active = stage == current
    val status =
        stringResource(
            when {
                done -> R.string.generating_stage_done
                active -> R.string.generating_stage_active
                else -> R.string.generating_stage_pending
            },
        )
    Row(
        Modifier.fillMaxWidth().semantics(mergeDescendants = true) { stateDescription = status },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(LittleStoryTheme.spacing.md),
    ) {
        val disc = Modifier.size(dimens.stageDiscSize)
        when {
            done ->
                Box(
                    disc.background(colors.success, LittleStoryTheme.shapes.iconButton),
                    contentAlignment = Alignment.Center,
                ) {
                    LittleStoryIcon(LittleStoryIcons.Check, contentDescription = null, tint = colors.onNight)
                }
            active ->
                Box(
                    disc.background(colors.accent, LittleStoryTheme.shapes.iconButton),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(number.toString(), style = LittleStoryTheme.typography.chip, color = colors.night)
                }
            else ->
                Box(
                    disc.border(dimens.borderWidth, colors.textSecondary, LittleStoryTheme.shapes.iconButton),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(number.toString(), style = LittleStoryTheme.typography.chip, color = colors.textSecondary)
                }
        }
        Text(
            stringResource(label),
            style =
                LittleStoryTheme.typography.bodyLarge.copy(
                    fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
                ),
            color = if (done || active) colors.onNight else colors.textSecondary,
        )
    }
}

@Composable
private fun Paused(onTryAgain: () -> Unit, onBackToIdea: () -> Unit) {
    val colors = LittleStoryTheme.colors
    val spacing = LittleStoryTheme.spacing
    Spacer(Modifier.size(LittleStoryTheme.dimens.magicBookHeight))
    Column(
        Modifier.fillMaxWidth().testTag(GeneratingTags.PAUSED),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        Box(
            Modifier
                .size(LittleStoryTheme.dimens.emptyStateIconSize)
                .background(colors.nightSurface, LittleStoryTheme.shapes.iconButton),
            contentAlignment = Alignment.Center,
        ) { LittleStoryIcon(LittleStoryIcons.Book, contentDescription = null, tint = colors.highlight) }
        Text(
            stringResource(R.string.generating_paused_title),
            style = LittleStoryTheme.typography.heroTitle,
            color = colors.onNight,
            textAlign = TextAlign.Center,
        )
        Text(
            stringResource(R.string.generating_paused_body),
            style = LittleStoryTheme.typography.body,
            color = colors.secondaryContainer,
            textAlign = TextAlign.Center,
        )
        LittleStoryButton(
            stringResource(R.string.generating_try_again),
            onTryAgain,
            modifier = Modifier.testTag(GeneratingTags.TRY_AGAIN),
        )
        LittleStoryButton(
            stringResource(R.string.generating_back_to_idea),
            onBackToIdea,
            variant = ButtonVariant.Secondary,
            modifier = Modifier.testTag(GeneratingTags.BACK_TO_IDEA),
        )
    }
}

// The open book in SCR-08 is two facing pages that share the book's width.
private const val BOOK_PAGES = 2
