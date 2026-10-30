package com.littlestory.feature.settings.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.littlestory.feature.settings.children.ChildProfilesActions
import com.littlestory.feature.settings.notifications.NotificationsActions
import com.littlestory.feature.settings.profile.ProfileActions
import com.littlestory.feature.settings.children.ChildProfilesRoute as ChildProfilesScreenRoute
import com.littlestory.feature.settings.notifications.NotificationsRoute as NotificationsScreenRoute
import com.littlestory.feature.settings.profile.ProfileRoute as ProfileScreenRoute
import com.littlestory.feature.settings.reminder.ReminderRoute as ReminderScreenRoute
import com.littlestory.feature.settings.subscription.SubscriptionRoute as SubscriptionScreenRoute

fun NavGraphBuilder.profileScreen(actions: ProfileActions) {
    composable<ProfileRoute> { ProfileScreenRoute(actions) }
}

fun NavGraphBuilder.childProfilesScreen(actions: ChildProfilesActions) {
    composable<ChildProfilesRoute> { ChildProfilesScreenRoute(actions) }
}

fun NavGraphBuilder.reminderScreen(onBack: () -> Unit) {
    composable<ReminderRoute> { ReminderScreenRoute(onBack) }
}

fun NavGraphBuilder.notificationsScreen(actions: NotificationsActions) {
    composable<NotificationsRoute> { NotificationsScreenRoute(actions) }
}

fun NavGraphBuilder.subscriptionScreen(onBack: () -> Unit, onDone: () -> Unit) {
    composable<SubscriptionRoute> { SubscriptionScreenRoute(onBack, onDone) }
}
