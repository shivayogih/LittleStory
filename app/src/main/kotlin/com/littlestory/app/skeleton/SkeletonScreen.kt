package com.littlestory.app.skeleton

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.platform.testTag
import androidx.compose.foundation.selection.selectable
import com.littlestory.app.R
import com.littlestory.core.common.language.SupportedLanguage
import com.littlestory.core.designsystem.theme.LittleStoryTheme

/** Test tag for a language option, e.g. `language_option_kn`. */
fun languageOptionTag(language: SupportedLanguage) = "language_option_${language.tag}"

/**
 * Walking-skeleton screen: brand, tagline and the language picker, using only theme tokens and
 * string resources. Replaced by the real onboarding (P3).
 */
@Composable
fun SkeletonScreen(
    languages: List<SupportedLanguage>,
    selected: SupportedLanguage,
    onLanguageSelected: (SupportedLanguage) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LittleStoryTheme.colors
    val spacing = LittleStoryTheme.spacing
    val typography = LittleStoryTheme.typography
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.night)
            .safeDrawingPadding()
            .padding(horizontal = spacing.screenHorizontal, vertical = spacing.xxl),
        verticalArrangement = Arrangement.spacedBy(spacing.lg),
    ) {
        Text(
            text = stringResource(R.string.app_name),
            style = typography.brandName,
            color = colors.highlight,
        )
        Text(
            text = stringResource(R.string.skeleton_tagline),
            style = typography.displayOnboarding,
            color = colors.onNight,
        )
        Text(
            text = stringResource(R.string.skeleton_language_label),
            style = typography.chip,
            color = colors.moon,
        )
        languages.forEach { language ->
            LanguageOption(
                language = language,
                isSelected = language == selected,
                onClick = { onLanguageSelected(language) },
            )
        }
    }
}

@Composable
private fun LanguageOption(
    language: SupportedLanguage,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val colors = LittleStoryTheme.colors
    val shape = LittleStoryTheme.shapes.button
    val dimens = LittleStoryTheme.dimens
    val selectedLabel = stringResource(R.string.skeleton_language_selected)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = dimens.buttonMinHeight)
            .background(if (isSelected) colors.secondaryContainer else colors.nightSurface, shape)
            .border(
                BorderStroke(dimens.selectedBorderWidth, if (isSelected) colors.accent else colors.nightSurface),
                shape,
            )
            .selectable(selected = isSelected, role = Role.RadioButton, onClick = onClick)
            .padding(horizontal = LittleStoryTheme.spacing.lg)
            .semantics { if (isSelected) contentDescription = selectedLabel }
            .testTag(languageOptionTag(language)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = language.nativeName,
            style = LittleStoryTheme.typography.button,
            color = if (isSelected) colors.onSecondaryContainer else colors.onNight,
        )
    }
}
