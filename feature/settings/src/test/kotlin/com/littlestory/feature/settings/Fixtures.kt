package com.littlestory.feature.settings

import com.littlestory.core.common.config.AppInfo
import com.littlestory.domain.model.NotificationItem
import com.littlestory.domain.model.NotificationKind
import com.littlestory.domain.model.NotificationSection
import com.littlestory.domain.model.Plan
import com.littlestory.feature.settings.children.ChildCard
import com.littlestory.feature.settings.children.ChildProfilesUiState
import com.littlestory.feature.settings.language.LanguageUiState
import com.littlestory.feature.settings.notifications.NotificationsUiState
import com.littlestory.feature.settings.profile.ProfileUiState
import com.littlestory.feature.settings.reminder.ReminderUiState
import com.littlestory.feature.settings.subscription.SubscriptionUiState
import java.time.DayOfWeek
import java.time.LocalTime

val profileState =
    ProfileUiState(
        activeChildId = "child_1",
        activeChildName = "Aarav",
        avatarTone = 1,
        childCount = 2,
        reminderEnabled = true,
        reminderTime = LocalTime.of(20, 0),
    )

val childrenState =
    ChildProfilesUiState(
        loading = false,
        children =
            listOf(
                ChildCard("child_1", "Aarav", 6, 1, "English", listOf("animals", "cricket"), active = true),
                ChildCard("child_2", "Meera", 4, 3, "ಕನ್ನಡ", listOf("music"), active = false),
            ),
    )

val reminderState =
    ReminderUiState(
        loading = false,
        enabled = true,
        time = LocalTime.of(20, 30),
        days = DayOfWeek.entries.toSet() - DayOfWeek.SATURDAY,
    )

val notificationsState =
    NotificationsUiState(
        loading = false,
        items =
            listOf(
                NotificationItem(
                    "ready",
                    "Your story is ready",
                    "Aarav and the Magical Cricket Bat is waiting.",
                    read = false,
                    section = NotificationSection.Today,
                    kind = NotificationKind.StoryReady,
                ),
                NotificationItem(
                    "reminder",
                    "Time for a story",
                    "Bedtime is in 15 minutes.",
                    read = true,
                    section = NotificationSection.Earlier,
                    kind = NotificationKind.Reminder,
                ),
            ),
    )

val subscriptionState =
    SubscriptionUiState(
        plans =
            listOf(
                Plan("free", 0, "INR", false),
                Plan("monthly", 24_900, "INR", false),
                Plan("yearly", 179_900, "INR", true, bestValue = true),
                Plan("family", 249_900, "INR", true, maxChildren = 5),
            ),
    )

val languageState = LanguageUiState(appLanguageTag = "en", storyLanguageTag = "kn")

val testAppInfo = AppInfo(versionName = "0.1.0", versionCode = 1)
