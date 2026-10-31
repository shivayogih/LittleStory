package com.littlestory.feature.settings.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.littlestory.core.ui.safety.ParentGated
import com.littlestory.feature.settings.children.ChildProfilesActions
import com.littlestory.feature.settings.notifications.NotificationsActions
import com.littlestory.feature.settings.profile.ProfileActions
import com.littlestory.feature.settings.children.ChildProfilesRoute as ChildProfilesScreenRoute
import com.littlestory.feature.settings.help.HelpRoute as HelpScreenRoute
import com.littlestory.feature.settings.language.LanguageRoute as LanguageScreenRoute
import com.littlestory.feature.settings.notifications.NotificationsRoute as NotificationsScreenRoute
import com.littlestory.feature.settings.privacy.PrivacyRoute as PrivacyScreenRoute
import com.littlestory.feature.settings.profile.ProfileRoute as ProfileScreenRoute
import com.littlestory.feature.settings.reminder.ReminderRoute as ReminderScreenRoute
import com.littlestory.feature.settings.subscription.SubscriptionRoute as SubscriptionScreenRoute

// Parent-only screens open behind the parental gate (Google Play Families policy, T-08); cancelling
// the gate goes back. Whichever screen links to them, the gate cannot be skipped.

fun NavGraphBuilder.profileScreen(actions: ProfileActions) {
    composable<ProfileRoute> { ProfileScreenRoute(actions) }
}

fun NavGraphBuilder.childProfilesScreen(actions: ChildProfilesActions) {
    composable<ChildProfilesRoute> { ParentGated(actions.onBack) { ChildProfilesScreenRoute(actions) } }
}

fun NavGraphBuilder.reminderScreen(onBack: () -> Unit) {
    composable<ReminderRoute> { ReminderScreenRoute(onBack) }
}

fun NavGraphBuilder.notificationsScreen(actions: NotificationsActions) {
    composable<NotificationsRoute> { NotificationsScreenRoute(actions) }
}

fun NavGraphBuilder.subscriptionScreen(onBack: () -> Unit, onDone: () -> Unit) {
    composable<SubscriptionRoute> { ParentGated(onBack) { SubscriptionScreenRoute(onBack, onDone) } }
}

fun NavGraphBuilder.languageScreen(onBack: () -> Unit) {
    composable<LanguageRoute> { ParentGated(onBack) { LanguageScreenRoute(onBack) } }
}

/** [onDeleted] runs after all local data is gone; the app should restart the first-run flow. */
fun NavGraphBuilder.privacyScreen(onBack: () -> Unit, onDeleted: () -> Unit) {
    composable<PrivacyRoute> { ParentGated(onBack) { PrivacyScreenRoute(onBack, onDeleted) } }
}

fun NavGraphBuilder.helpScreen(onBack: () -> Unit) {
    composable<HelpRoute> { HelpScreenRoute(onBack) }
}
