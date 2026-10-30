package com.littlestory.feature.settings.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
import com.littlestory.core.common.language.SupportedLanguage
import com.littlestory.core.common.language.SupportedLanguages
import com.littlestory.core.designsystem.brand.BrandMark
import com.littlestory.core.designsystem.icon.LittleStoryIcons
import com.littlestory.core.designsystem.theme.LittleStoryTheme
import com.littlestory.core.ui.chrome.TopBar
import com.littlestory.core.ui.language.AppLanguage
import com.littlestory.core.ui.story.Avatar
import com.littlestory.core.ui.system.SystemBarIcons
import com.littlestory.feature.settings.R
import com.littlestory.feature.settings.SettingsDivider
import com.littlestory.feature.settings.SettingsGroup
import com.littlestory.feature.settings.SettingsRow
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

object ProfileTags {
    const val CHILD_CARD = "profile_child_card"
    const val CHILDREN = "profile_children"
    const val LANGUAGE = "profile_language"
    const val REMINDER = "profile_reminder"
    const val NOTIFICATIONS = "profile_notifications"
    const val AUDIO = "profile_audio"
    const val DOWNLOADS = "profile_downloads"
    const val SUBSCRIPTION = "profile_subscription"
    const val PRIVACY = "profile_privacy"
    const val HELP = "profile_help"
}

/** Where the hub sends the parent; `:app` maps these to routes. */
data class ProfileActions(
    val onEditChild: (childId: String?) -> Unit = {},
    val onChildren: () -> Unit = {},
    val onReminder: () -> Unit = {},
    val onNotifications: () -> Unit = {},
    val onDownloads: () -> Unit = {},
    val onSubscription: () -> Unit = {},
    val onLanguage: () -> Unit = {},
    val onPrivacy: () -> Unit = {},
    val onHelp: () -> Unit = {},
)

data class ProfileCallbacks(
    val onReminderToggled: (Boolean) -> Unit = {},
    val onNotificationsToggled: (Boolean) -> Unit = {},
    val onWifiOnlyToggled: (Boolean) -> Unit = {},
)

@Composable
fun ProfileRoute(actions: ProfileActions, viewModel: ProfileViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ProfileScreen(
        state,
        actions,
        ProfileCallbacks(
            onReminderToggled = viewModel::onReminderToggled,
            onNotificationsToggled = viewModel::onNotificationsToggled,
            onWifiOnlyToggled = viewModel::onWifiOnlyToggled,
        ),
        appLanguage = AppLanguage.current(),
    )
}

@Composable
fun ProfileScreen(
    state: ProfileUiState,
    actions: ProfileActions,
    callbacks: ProfileCallbacks,
    modifier: Modifier = Modifier,
    appLanguage: SupportedLanguage = SupportedLanguages.default,
) {
    val colors = LittleStoryTheme.colors
    val spacing = LittleStoryTheme.spacing
    SystemBarIcons(darkIcons = true)
    Column(modifier.fillMaxSize().background(colors.background)) {
        TopBar(title = stringResource(R.string.profile_title))
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = spacing.screenHorizontal, vertical = spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.xl),
        ) {
            ChildCardHeader(state, actions)
            SettingsGroup(stringResource(R.string.profile_group_children)) {
                SettingsRow(
                    LittleStoryIcons.User,
                    stringResource(R.string.profile_manage_children),
                    pluralStringResource(R.plurals.profile_children_count, state.childCount, state.childCount),
                    ProfileTags.CHILDREN,
                    onClick = actions.onChildren,
                )
                SettingsDivider()
                SettingsRow(
                    LittleStoryIcons.Book,
                    stringResource(R.string.profile_language),
                    languageSubtitle(appLanguage, SupportedLanguages.fromTag(state.contentLanguageTag)),
                    ProfileTags.LANGUAGE,
                    onClick = actions.onLanguage,
                )
            }
            SettingsGroup(stringResource(R.string.profile_group_experience)) {
                SettingsRow(
                    LittleStoryIcons.Moon,
                    stringResource(R.string.profile_reminder),
                    reminderSubtitle(state),
                    ProfileTags.REMINDER,
                    onClick = actions.onReminder,
                    toggle = state.reminderEnabled,
                    onToggle = callbacks.onReminderToggled,
                )
                SettingsDivider()
                SettingsRow(
                    LittleStoryIcons.Bell,
                    stringResource(R.string.profile_notifications),
                    stringResource(R.string.profile_notifications_subtitle),
                    ProfileTags.NOTIFICATIONS,
                    onClick = actions.onNotifications,
                    toggle = state.notificationsEnabled,
                    onToggle = callbacks.onNotificationsToggled,
                )
                SettingsDivider()
                SettingsRow(
                    LittleStoryIcons.Volume,
                    stringResource(R.string.profile_audio),
                    stringResource(
                        R.string.profile_audio_subtitle,
                        NumberFormat.getNumberInstance(LocalLocale.current.platformLocale).format(state.playbackSpeed),
                    ),
                    ProfileTags.AUDIO,
                )
                SettingsDivider()
                SettingsRow(
                    LittleStoryIcons.Download,
                    stringResource(R.string.profile_downloads),
                    stringResource(
                        if (state.wifiOnlyDownloads) {
                            R.string.profile_downloads_wifi
                        } else {
                            R.string.profile_downloads_any
                        },
                    ),
                    ProfileTags.DOWNLOADS,
                    onClick = actions.onDownloads,
                    toggle = state.wifiOnlyDownloads,
                    onToggle = callbacks.onWifiOnlyToggled,
                )
            }
            SettingsGroup(stringResource(R.string.profile_group_app)) {
                if (state.subscriptionVisible) {
                    SettingsRow(
                        LittleStoryIcons.Heart,
                        stringResource(R.string.profile_subscription),
                        stringResource(R.string.profile_subscription_subtitle),
                        ProfileTags.SUBSCRIPTION,
                        onClick = actions.onSubscription,
                    )
                    SettingsDivider()
                }
                SettingsRow(
                    LittleStoryIcons.Settings,
                    stringResource(R.string.profile_privacy),
                    stringResource(R.string.profile_privacy_subtitle),
                    ProfileTags.PRIVACY,
                    onClick = actions.onPrivacy,
                )
                SettingsDivider()
                SettingsRow(
                    LittleStoryIcons.Book,
                    stringResource(R.string.profile_help),
                    stringResource(R.string.profile_help_subtitle),
                    ProfileTags.HELP,
                    onClick = actions.onHelp,
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

/** One name when the app and stories share a language, otherwise both (LS-046). */
@Composable
private fun languageSubtitle(app: SupportedLanguage, story: SupportedLanguage): String =
    if (app ==
        story
    ) {
        app.nativeName
    } else {
        stringResource(R.string.profile_language_split, app.nativeName, story.nativeName)
    }

@Composable
private fun reminderSubtitle(state: ProfileUiState): String {
    if (!state.reminderEnabled) return stringResource(R.string.profile_reminder_off)
    val time = state.reminderTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
    return stringResource(
        if (state.reminderEveryDay) R.string.profile_reminder_every_day else R.string.profile_reminder_some_days,
        time,
    )
}

@Composable
private fun ChildCardHeader(state: ProfileUiState, actions: ProfileActions) {
    val colors = LittleStoryTheme.colors
    val spacing = LittleStoryTheme.spacing
    Row(
        Modifier
            .fillMaxWidth()
            .clip(LittleStoryTheme.shapes.large)
            .background(colors.primary)
            .clickable(role = Role.Button) { actions.onEditChild(state.activeChildId) }
            .padding(spacing.xl)
            .testTag(ProfileTags.CHILD_CARD),
        horizontalArrangement = Arrangement.spacedBy(spacing.lg),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Avatar(tone = state.avatarTone ?: 0, selected = false, large = true)
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(spacing.xxs)) {
            Text(
                state.activeChildName ?: stringResource(R.string.profile_family),
                style = LittleStoryTheme.typography.homeTitle,
                color = colors.onPrimary,
            )
            Text(
                stringResource(R.string.profile_private_note),
                style = LittleStoryTheme.typography.chip,
                color = colors.secondaryContainer,
            )
            Text(
                stringResource(
                    if (state.activeChildId !=
                        null
                    ) {
                        R.string.profile_edit_child
                    } else {
                        R.string.profile_add_child
                    },
                ),
                style = LittleStoryTheme.typography.chip,
                color = colors.highlight,
            )
        }
    }
}
