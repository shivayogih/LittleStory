package com.littlestory.feature.settings.subscription

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.littlestory.core.designsystem.component.LittleStoryDialog
import com.littlestory.core.designsystem.component.LittleStoryIconButton
import com.littlestory.core.designsystem.icon.LittleStoryIcon
import com.littlestory.core.designsystem.icon.LittleStoryIcons
import com.littlestory.core.designsystem.theme.LittleStoryTheme
import com.littlestory.core.ui.chrome.StickyActionBar
import com.littlestory.core.ui.illustration.NightSky
import com.littlestory.core.ui.system.SystemBarIcons
import com.littlestory.domain.model.Plan
import com.littlestory.feature.settings.R
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale
import com.littlestory.core.ui.R as UiR

object SubscriptionTags {
    const val CHOOSE = "subscription_choose"
    const val NOTE = "subscription_note"

    fun plan(id: String) = "subscription_plan_$id"
}

private val planNames =
    mapOf(
        "free" to R.string.subscription_plan_free,
        "monthly" to R.string.subscription_plan_monthly,
        "yearly" to R.string.subscription_plan_yearly,
        "family" to R.string.subscription_plan_family,
    )

private val benefits =
    listOf(
        R.string.subscription_benefit_personalised,
        R.string.subscription_benefit_collections,
        R.string.subscription_benefit_narration,
        R.string.subscription_benefit_offline,
        R.string.subscription_benefit_profiles,
        R.string.subscription_benefit_weekly,
    )

@Composable
fun SubscriptionRoute(onBack: () -> Unit, onDone: () -> Unit, viewModel: SubscriptionViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    SubscriptionScreen(
        state,
        onBack,
        viewModel::onSelect,
        viewModel::onChoose,
        onDone = {
            viewModel.onDialogDismiss()
            onDone()
        },
    )
}

@Composable
private fun planName(id: String): String = planNames[id]?.let { stringResource(it) } ?: id

/** Price in the plan's currency, formatted for the current locale (illustrative only). */
private fun formatPrice(plan: Plan, locale: Locale): String {
    val currency = Currency.getInstance(plan.currencyCode)
    val format =
        NumberFormat.getCurrencyInstance(locale).apply {
            this.currency = currency
            maximumFractionDigits = 0
        }
    return format.format(BigDecimal.valueOf(plan.priceMinorUnits, currency.defaultFractionDigits))
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SubscriptionScreen(
    state: SubscriptionUiState,
    onBack: () -> Unit,
    onSelect: (String) -> Unit,
    onChoose: () -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LittleStoryTheme.colors
    val spacing = LittleStoryTheme.spacing
    SystemBarIcons(darkIcons = false)
    Column(modifier.fillMaxSize().background(colors.background)) {
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            NightSky(Modifier.fillMaxWidth()) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .padding(spacing.screenHorizontal),
                    verticalArrangement = Arrangement.spacedBy(spacing.sm),
                ) {
                    LittleStoryIconButton(LittleStoryIcons.Back, stringResource(UiR.string.action_back), onBack)
                    Box(
                        Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(spacing.md)
                            .background(colors.moon, LittleStoryTheme.shapes.iconButton)
                            .padding(LittleStoryTheme.dimens.subscriptionMoonSize / 2),
                    )
                    Text(
                        stringResource(R.string.subscription_kicker),
                        style = LittleStoryTheme.typography.heroKicker,
                        color = colors.highlight,
                    )
                    Text(
                        stringResource(R.string.subscription_title),
                        style = LittleStoryTheme.typography.heroTitle,
                        color = colors.onNight,
                    )
                    Text(
                        stringResource(R.string.subscription_body),
                        style = LittleStoryTheme.typography.body,
                        color = colors.secondaryContainer,
                    )
                }
            }
            Column(Modifier.padding(spacing.screenHorizontal), verticalArrangement = Arrangement.spacedBy(spacing.lg)) {
                FlowRow(
                    Modifier.fillMaxWidth(),
                    maxItemsInEachRow = BENEFIT_COLUMNS,
                    verticalArrangement = Arrangement.spacedBy(spacing.sm),
                ) {
                    benefits.forEach { benefit ->
                        Row(
                            Modifier.weight(1f).padding(end = spacing.sm),
                            horizontalArrangement = Arrangement.spacedBy(spacing.xs),
                        ) {
                            LittleStoryIcon(LittleStoryIcons.Check, contentDescription = null, tint = colors.success)
                            Text(
                                stringResource(benefit),
                                style = LittleStoryTheme.typography.chip,
                                color = colors.textPrimary,
                            )
                        }
                    }
                }
                state.plans.forEach { plan -> PlanCard(plan, plan.id == state.selectedId) { onSelect(plan.id) } }
                Text(
                    stringResource(R.string.subscription_note),
                    style = LittleStoryTheme.typography.chip,
                    color = colors.textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().testTag(SubscriptionTags.NOTE),
                )
            }
        }
        StickyActionBar(
            primaryLabel = stringResource(R.string.subscription_choose, planName(state.selectedId)),
            onPrimary = onChoose,
            primaryEnabled = state.selected != null,
            modifier = Modifier.testTag(SubscriptionTags.CHOOSE),
        )
    }
    state.confirmedId?.let { id ->
        LittleStoryDialog(
            title = stringResource(R.string.subscription_dialog_title),
            text = stringResource(R.string.subscription_dialog_text, planName(id)),
            confirmLabel = stringResource(R.string.subscription_dialog_done),
            onDismiss = onDone,
        )
    }
}

@Composable
private fun PlanCard(plan: Plan, selected: Boolean, onClick: () -> Unit) {
    val colors = LittleStoryTheme.colors
    val spacing = LittleStoryTheme.spacing
    val dimens = LittleStoryTheme.dimens
    val shape = LittleStoryTheme.shapes.large
    val period =
        when {
            plan.priceMinorUnits == 0L -> stringResource(R.string.subscription_period_free)
            plan.maxChildren > 1 ->
                pluralStringResource(
                    R.plurals.subscription_period_family,
                    plan.maxChildren,
                    plan.maxChildren,
                )
            plan.billedYearly -> stringResource(R.string.subscription_period_year)
            else -> stringResource(R.string.subscription_period_month)
        }
    Row(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(if (selected) colors.secondaryContainer else colors.surface)
            .border(
                if (selected) dimens.selectedBorderWidth else dimens.borderWidth,
                if (selected) colors.accent else colors.outline,
                shape,
            ).selectable(selected = selected, role = Role.RadioButton, onClick = onClick)
            .padding(spacing.lg)
            .testTag(SubscriptionTags.plan(plan.id)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(spacing.xxs)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(planName(plan.id), style = LittleStoryTheme.typography.itemTitle, color = colors.textPrimary)
                if (plan.bestValue) {
                    Text(
                        stringResource(R.string.subscription_best_value),
                        style = LittleStoryTheme.typography.eyebrow,
                        color = colors.textPrimary,
                        modifier =
                            Modifier
                                .background(colors.highlight, LittleStoryTheme.shapes.chip)
                                .padding(horizontal = spacing.sm, vertical = spacing.xxs),
                    )
                }
            }
            Text(period, style = LittleStoryTheme.typography.chip, color = colors.textSecondary)
        }
        Text(
            formatPrice(plan, LocalLocale.current.platformLocale),
            style = LittleStoryTheme.typography.topBarTitle,
            color = colors.primary,
        )
    }
}

// SCR-15 lays the four benefits out as a two-column grid.
private const val BENEFIT_COLUMNS = 2
