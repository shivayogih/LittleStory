package com.littlestory.feature.onboarding.onboarding

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
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.littlestory.core.designsystem.brand.BrandMark
import com.littlestory.core.designsystem.brand.BrandMarkVariant
import com.littlestory.core.designsystem.component.LittleStoryButton
import com.littlestory.core.designsystem.theme.LittleStoryTheme
import com.littlestory.core.ui.illustration.NightScene
import com.littlestory.core.ui.system.SystemBarIcons
import com.littlestory.core.ui.text.asString
import com.littlestory.feature.onboarding.R

/** Test tags for Onboarding (UI tests select by tag, never by text). */
object OnboardingTags {
    const val CONTINUE = "onboarding_continue"
    const val SKIP = "onboarding_skip"
    const val TITLE = "onboarding_title"
    const val PAGER = "onboarding_pager"
}

@Composable
fun OnboardingRoute(onFinished: () -> Unit, viewModel: OnboardingViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                OnboardingEffect.GoToSetup -> onFinished()
            }
        }
    }
    OnboardingScreen(state, onContinue = viewModel::onContinue, onSkip = viewModel::onSkip)
}

@Composable
fun OnboardingScreen(
    state: OnboardingUiState,
    onContinue: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LittleStoryTheme.colors
    val spacing = LittleStoryTheme.spacing
    val typography = LittleStoryTheme.typography
    // Light icons over the night scene at the top, dark navigation icons over the cream sheet.
    SystemBarIcons(darkIcons = false, darkNavigationIcons = true)
    Box(modifier.fillMaxSize().background(colors.night)) {
        Column(Modifier.fillMaxSize()) {
            Box(Modifier.weight(1f).fillMaxWidth()) { NightScene() }
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(colors.background, LittleStoryTheme.shapes.sheet)
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = spacing.screenHorizontal, vertical = spacing.xxl),
                verticalArrangement = Arrangement.spacedBy(spacing.md),
            ) {
                val eyebrow = stringResource(R.string.onboarding_eyebrow)
                Text(
                    text = if (typography.uppercaseEyebrows) eyebrow.uppercase() else eyebrow,
                    style = typography.eyebrow,
                    color = colors.primaryVariantLabel,
                )
                Text(
                    state.current.title.asString(),
                    style = typography.displayOnboarding,
                    color = colors.textPrimary,
                    modifier = Modifier.testTag(OnboardingTags.TITLE),
                )
                Text(state.current.body.asString(), style = typography.bodyLarge, color = colors.textSecondary)
                PagerDots(count = state.slides.size, selected = state.page)
                LittleStoryButton(
                    text = state.current.cta.asString(),
                    onClick = onContinue,
                    modifier = Modifier.testTag(OnboardingTags.CONTINUE),
                )
            }
        }
        Row(
            Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .heightIn(min = LittleStoryTheme.dimens.topBarHeight)
                .padding(horizontal = spacing.screenHorizontal),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BrandMark(Modifier.weight(1f), BrandMarkVariant.Light)
            Box(
                Modifier
                    .heightIn(min = LittleStoryTheme.dimens.minTouchTarget)
                    .clip(LittleStoryTheme.shapes.chip)
                    .clickable(role = Role.Button, onClick = onSkip)
                    .padding(horizontal = spacing.md)
                    .testTag(OnboardingTags.SKIP),
                contentAlignment = Alignment.Center,
            ) {
                // The design's skip colour (#D8D0F5) is not a token; soft lavender is the closest one.
                Text(
                    stringResource(com.littlestory.core.ui.R.string.action_skip),
                    style = typography.button,
                    color = colors.secondaryContainer,
                )
            }
        }
    }
}

/** Dots for the pager; the active one widens. Announced as "Slide n of m". */
@Composable
private fun PagerDots(count: Int, selected: Int) {
    val colors = LittleStoryTheme.colors
    val dimens = LittleStoryTheme.dimens
    val description = stringResource(R.string.onboarding_pager_description, selected + 1, count)
    Row(
        Modifier
            .height(
                dimens.pagerDotSize,
            ).semantics { contentDescription = description }
            .testTag(OnboardingTags.PAGER),
        horizontalArrangement = Arrangement.spacedBy(LittleStoryTheme.spacing.xs),
    ) {
        repeat(count) { index ->
            val active = index == selected
            Box(
                Modifier
                    .size(
                        width = if (active) dimens.pagerDotActiveWidth else dimens.pagerDotSize,
                        height = dimens.pagerDotSize,
                    ).background(if (active) colors.primary else colors.outline, LittleStoryTheme.shapes.chip),
            )
        }
    }
}
