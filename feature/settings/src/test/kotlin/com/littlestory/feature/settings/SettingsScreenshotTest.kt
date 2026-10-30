package com.littlestory.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import com.littlestory.core.designsystem.theme.LittleStoryTheme
import com.littlestory.feature.settings.children.ChildProfilesActions
import com.littlestory.feature.settings.children.ChildProfilesScreen
import com.littlestory.feature.settings.help.HelpScreen
import com.littlestory.feature.settings.language.LanguageCallbacks
import com.littlestory.feature.settings.language.LanguageScreen
import com.littlestory.feature.settings.notifications.NotificationsActions
import com.littlestory.feature.settings.notifications.NotificationsScreen
import com.littlestory.feature.settings.notifications.NotificationsUiState
import com.littlestory.feature.settings.privacy.PrivacyCallbacks
import com.littlestory.feature.settings.privacy.PrivacyScreen
import com.littlestory.feature.settings.privacy.PrivacyUiState
import com.littlestory.feature.settings.profile.ProfileActions
import com.littlestory.feature.settings.profile.ProfileCallbacks
import com.littlestory.feature.settings.profile.ProfileScreen
import com.littlestory.feature.settings.reminder.ReminderCallbacks
import com.littlestory.feature.settings.reminder.ReminderScreen
import com.littlestory.feature.settings.subscription.SubscriptionScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Renders the Profile hub and parent screens per locale into build/outputs/roborazzi. No baselines are committed. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [36])
class SettingsScreenshotTest {
    @get:Rule
    val rule = createComposeRule()

    private fun capture(name: String, content: @Composable () -> Unit) {
        rule.setContent { LittleStoryTheme { content() } }
        rule.onRoot().captureRoboImage("build/outputs/roborazzi/$name.png")
    }

    private val profile: @Composable () -> Unit = { ProfileScreen(profileState, ProfileActions(), ProfileCallbacks()) }
    private val children: @Composable () -> Unit = { ChildProfilesScreen(childrenState, ChildProfilesActions(), {}) }
    private val reminder: @Composable () -> Unit = { ReminderScreen(reminderState, ReminderCallbacks()) }
    private val subscription: @Composable () -> Unit = { SubscriptionScreen(subscriptionState, {}, {}, {}, {}) }

    private fun inbox(state: NotificationsUiState): @Composable () -> Unit = {
        NotificationsScreen(state, NotificationsActions(), {}, {})
    }

    @Test
    @Config(qualifiers = "en-w430dp-h1800dp-xxhdpi")
    fun profileEnglish() = capture("profile_en", profile)

    @Test
    @Config(qualifiers = "kn-w430dp-h1800dp-xxhdpi")
    fun profileKannada() = capture("profile_kn", profile)

    @Test
    @Config(qualifiers = "kn-w430dp-h3200dp-xxhdpi", fontScale = 2f)
    fun profileKannadaLarge() = capture("profile_kn_200", profile)

    @Test
    @Config(qualifiers = "en-w430dp-h932dp-xxhdpi")
    fun childrenEnglish() = capture("children_en", children)

    @Test
    @Config(qualifiers = "kn-w430dp-h932dp-xxhdpi")
    fun childrenKannada() = capture("children_kn", children)

    @Test
    @Config(qualifiers = "en-w430dp-h1100dp-xxhdpi")
    fun reminderEnglish() = capture("reminder_en", reminder)

    @Test
    @Config(qualifiers = "kn-w430dp-h1100dp-xxhdpi")
    fun reminderKannada() = capture("reminder_kn", reminder)

    @Test
    @Config(qualifiers = "en-w430dp-h932dp-xxhdpi")
    fun notificationsEnglish() = capture("notifications_en", inbox(notificationsState))

    @Test
    @Config(qualifiers = "kn-w430dp-h932dp-xxhdpi")
    fun notificationsKannadaEmpty() = capture("notifications_kn_empty", inbox(NotificationsUiState(loading = false)))

    @Test
    @Config(qualifiers = "en-w430dp-h1900dp-xxhdpi")
    fun subscriptionEnglish() = capture("subscription_en", subscription)

    @Test
    @Config(qualifiers = "kn-w430dp-h1900dp-xxhdpi")
    fun subscriptionKannada() = capture("subscription_kn", subscription)

    private val language: @Composable () -> Unit = { LanguageScreen(languageState, LanguageCallbacks()) }
    private val privacy: @Composable () -> Unit = { PrivacyScreen(PrivacyUiState(), PrivacyCallbacks()) }
    private val help: @Composable () -> Unit = { HelpScreen(testAppInfo, {}) }

    @Test
    @Config(qualifiers = "en-w430dp-h932dp-xxhdpi")
    fun languageEnglish() = capture("language_en", language)

    @Test
    @Config(qualifiers = "kn-w430dp-h932dp-xxhdpi")
    fun languageKannada() = capture("language_kn", language)

    @Test
    @Config(qualifiers = "en-w430dp-h1400dp-xxhdpi")
    fun privacyEnglish() = capture("privacy_en", privacy)

    @Test
    @Config(qualifiers = "kn-w430dp-h1600dp-xxhdpi")
    fun privacyKannada() = capture("privacy_kn", privacy)

    @Test
    @Config(qualifiers = "en-w430dp-h1600dp-xxhdpi")
    fun helpEnglish() = capture("help_en", help)

    @Test
    @Config(qualifiers = "kn-w430dp-h2000dp-xxhdpi")
    fun helpKannada() = capture("help_kn", help)
}
