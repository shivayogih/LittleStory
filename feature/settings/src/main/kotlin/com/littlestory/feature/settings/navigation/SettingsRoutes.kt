package com.littlestory.feature.settings.navigation

import kotlinx.serialization.Serializable

/** SCR-13: the Profile hub, the fifth bottom-navigation tab. */
@Serializable
data object ProfileRoute

/** SCR-14: manage child profiles. */
@Serializable
data object ChildProfilesRoute

/** SCR-15: illustrative subscription (behind the demoSubscription flag). */
@Serializable
data object SubscriptionRoute

/** SCR-16: bedtime reminder settings (UI only in the MVP). */
@Serializable
data object ReminderRoute

/** SCR-18: notifications inbox. */
@Serializable
data object NotificationsRoute

/** SCR-20: language preferences. */
@Serializable
data object LanguageRoute

/** SCR-21: privacy and parent controls. */
@Serializable
data object PrivacyRoute

/** SCR-22: help and about. */
@Serializable
data object HelpRoute

/** SCR-23: account settings (not yet designed). */
@Serializable
data object AccountRoute

/** SCR-23: audio preferences (not yet designed). */
@Serializable
data object AudioPreferencesRoute
