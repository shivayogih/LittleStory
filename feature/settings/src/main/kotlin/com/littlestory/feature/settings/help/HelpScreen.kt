package com.littlestory.feature.settings.help

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.littlestory.core.common.config.AppInfo
import com.littlestory.core.designsystem.brand.BrandMark
import com.littlestory.core.designsystem.theme.LittleStoryTheme
import com.littlestory.core.ui.chrome.TopBar
import com.littlestory.core.ui.system.SystemBarIcons
import com.littlestory.feature.settings.R
import com.littlestory.feature.settings.SettingsDivider
import com.littlestory.feature.settings.SettingsGroup
import com.littlestory.feature.settings.SettingsParagraph
import com.littlestory.core.ui.R as UiR

object HelpTags {
    const val VERSION = "help_version"
    const val LICENCES = "help_licences"
}

/** Bundled fonts and their licence; shown verbatim as names, so they are not translated. */
private val fontNames = listOf("DM Sans", "Manrope", "Noto Sans Kannada")

private val questions =
    listOf(
        R.string.help_q_stories to R.string.help_a_stories,
        R.string.help_q_offline to R.string.help_a_offline,
        R.string.help_q_language to R.string.help_a_language,
        R.string.help_q_children to R.string.help_a_children,
    )

@Composable
fun HelpRoute(onBack: () -> Unit, viewModel: HelpViewModel = hiltViewModel()) {
    HelpScreen(viewModel.appInfo, onBack)
}

/** SCR-22 (LS-115), built from settings components until a design exists (D10). */
@Composable
fun HelpScreen(appInfo: AppInfo, onBack: () -> Unit, modifier: Modifier = Modifier) {
    val colors = LittleStoryTheme.colors
    val spacing = LittleStoryTheme.spacing
    SystemBarIcons(darkIcons = true)
    Column(modifier.fillMaxSize().background(colors.background)) {
        TopBar(
            title = stringResource(R.string.help_title),
            backContentDescription = stringResource(UiR.string.action_back),
            onBack = onBack,
        )
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = spacing.screenHorizontal, vertical = spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.xl),
        ) {
            SettingsGroup(stringResource(R.string.help_group_questions)) {
                questions.forEachIndexed { index, (question, answer) ->
                    if (index > 0) SettingsDivider()
                    SettingsParagraph(stringResource(question), stringResource(answer))
                }
            }
            SettingsGroup(stringResource(R.string.help_group_about)) {
                SettingsParagraph(
                    stringResource(R.string.help_version_title),
                    stringResource(R.string.help_version_body, appInfo.versionName, appInfo.versionCode),
                    Modifier.testTag(HelpTags.VERSION),
                )
                SettingsDivider()
                SettingsParagraph(
                    stringResource(R.string.help_licences_title),
                    stringResource(R.string.help_licences_body, fontNames.joinToString()),
                    Modifier.testTag(HelpTags.LICENCES),
                )
            }
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                BrandMark()
                Text(
                    stringResource(R.string.profile_footer),
                    style = LittleStoryTheme.typography.chip,
                    color = colors.textSecondary,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
