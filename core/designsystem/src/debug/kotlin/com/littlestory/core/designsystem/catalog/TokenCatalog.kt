package com.littlestory.core.designsystem.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.littlestory.core.designsystem.R
import com.littlestory.core.designsystem.theme.LittleStoryColors
import com.littlestory.core.designsystem.theme.LittleStoryTheme
import com.littlestory.core.designsystem.theme.LittleStoryTypography

/**
 * Debug-only sheet showing every colour role, gradient and text style (LS-010/LS-011 preview).
 * Token names are developer identifiers, not user-facing copy.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TokenCatalog(modifier: Modifier = Modifier) {
    val colors = LittleStoryTheme.colors
    val spacing = LittleStoryTheme.spacing
    Column(
        modifier =
            modifier
                .background(colors.background)
                .padding(spacing.screenHorizontal),
        verticalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            verticalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            colors.namedSwatches().forEach { (name, color) -> Swatch(name, Brush.linearGradient(listOf(color, color))) }
            colors.namedGradients().forEach { (name, pair) -> Swatch(name, Brush.linearGradient(pair)) }
        }
        val specimen = stringResource(R.string.catalog_specimen)
        LittleStoryTheme.typography.namedStyles().forEach { (name, style) ->
            Column {
                Text(text = name, style = LittleStoryTheme.typography.chip, color = colors.textSecondary)
                Text(text = specimen, style = style, color = colors.textPrimary)
            }
        }
    }
}

@Composable
private fun Swatch(name: String, brush: Brush) {
    val spacing = LittleStoryTheme.spacing
    val colors = LittleStoryTheme.colors
    Column(Modifier.width(SWATCH_LABEL_WIDTH_DP)) {
        Box(
            Modifier
                .size(LittleStoryTheme.dimens.minTouchTarget)
                .background(brush, LittleStoryTheme.shapes.small)
                .border(LittleStoryTheme.dimens.borderWidth, colors.outline, LittleStoryTheme.shapes.small),
        )
        Text(
            text = name,
            style = LittleStoryTheme.typography.navLabel,
            color = colors.textSecondary,
            modifier = Modifier.padding(top = spacing.xs),
        )
    }
}

// Debug catalog layout only: wide enough for the longest token name at 10 sp.
private val SWATCH_LABEL_WIDTH_DP = 96.dp

internal fun LittleStoryColors.namedSwatches(): List<Pair<String, Color>> =
    listOf(
        "primary" to primary,
        "onPrimary" to onPrimary,
        "primaryVariantLabel" to primaryVariantLabel,
        "accent" to accent,
        "secondaryContainer" to secondaryContainer,
        "onSecondaryContainer" to onSecondaryContainer,
        "background" to background,
        "pageBackdrop" to pageBackdrop,
        "surface" to surface,
        "surfaceMuted" to surfaceMuted,
        "outline" to outline,
        "night" to night,
        "nightSurface" to nightSurface,
        "onNight" to onNight,
        "highlight" to highlight,
        "warmAccent" to warmAccent,
        "moon" to moon,
        "textPrimary" to textPrimary,
        "textSecondary" to textSecondary,
        "textReader" to textReader,
        "navInactive" to navInactive,
        "success" to success,
        "successContainer" to successContainer,
        "error" to error,
        "favourite" to favourite,
        "favouriteContainer" to favouriteContainer,
        "cultureBanner" to cultureBanner,
        "lessonPanel" to lessonPanel,
        "readerBackground" to readerBackground,
        "successRibbon" to successRibbon,
        "skin" to illustration.skin,
        "skinLight" to illustration.skinLight,
        "hair" to illustration.hair,
        "childTop" to illustration.childTop,
        "elephant" to illustration.elephant,
        "elephantLight" to illustration.elephantLight,
        "elephantLeg" to illustration.elephantLeg,
        "cricketBat" to illustration.cricketBat,
        "shirtGold" to illustration.shirtGold,
    )

internal fun LittleStoryColors.namedGradients(): List<Pair<String, List<Color>>> =
    listOf(
        "friendship" to storyArt.friendship,
        "adventure" to storyArt.adventure,
        "bedtime" to storyArt.bedtime,
        "indianTales" to storyArt.indianTales,
        "space" to storyArt.space,
        "festivals" to storyArt.festivals,
    ).map { (name, pair) -> name to listOf(pair.start, pair.end) }

internal fun LittleStoryTypography.namedStyles(): List<Pair<String, TextStyle>> =
    listOf(
        "displayOnboarding" to displayOnboarding,
        "displaySetup" to displaySetup,
        "heroTitle" to heroTitle,
        "homeTitle" to homeTitle,
        "topBarTitle" to topBarTitle,
        "brandName" to brandName,
        "bodyLarge" to bodyLarge,
        "body" to body,
        "readerBody" to readerBody,
        "button" to button,
        "chip" to chip,
        "eyebrow" to eyebrow,
        "heroKicker" to heroKicker,
        "navLabel" to navLabel,
    )

@Preview(widthDp = 430, heightDp = 2000)
@Composable
private fun TokenCatalogPreview() {
    LittleStoryTheme { TokenCatalog() }
}

@Preview(widthDp = 430, heightDp = 2000, locale = "kn")
@Composable
private fun TokenCatalogKannadaPreview() {
    LittleStoryTheme { TokenCatalog() }
}
