package com.littlestory.feature.settings

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.littlestory.core.designsystem.theme.LittleStoryTheme
import com.littlestory.feature.settings.children.ChildProfilesActions
import com.littlestory.feature.settings.children.ChildProfilesScreen
import com.littlestory.feature.settings.children.ChildProfilesTags
import com.littlestory.feature.settings.notifications.NotificationsActions
import com.littlestory.feature.settings.notifications.NotificationsScreen
import com.littlestory.feature.settings.notifications.NotificationsTags
import com.littlestory.feature.settings.profile.ProfileActions
import com.littlestory.feature.settings.profile.ProfileCallbacks
import com.littlestory.feature.settings.profile.ProfileScreen
import com.littlestory.feature.settings.profile.ProfileTags
import com.littlestory.feature.settings.reminder.ReminderCallbacks
import com.littlestory.feature.settings.reminder.ReminderScreen
import com.littlestory.feature.settings.reminder.ReminderTags
import com.littlestory.feature.settings.subscription.SubscriptionScreen
import com.littlestory.feature.settings.subscription.SubscriptionTags
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.DayOfWeek
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "en-w430dp-h2000dp-xxhdpi")
class SettingsScreensTest {
    @get:Rule
    val rule = createComposeRule()

    private val calls = mutableListOf<String>()

    @Test
    fun profileRowsOpenTheirScreensAndTogglesReport() {
        rule.setContent {
            LittleStoryTheme {
                ProfileScreen(
                    profileState,
                    ProfileActions(
                        onEditChild = { calls += "edit:$it" },
                        onChildren = { calls += "children" },
                        onReminder = { calls += "reminder" },
                        onNotifications = { calls += "notifications" },
                        onDownloads = { calls += "downloads" },
                        onSubscription = { calls += "subscription" },
                    ),
                    ProfileCallbacks(onNotificationsToggled = { calls += "notify:$it" }),
                )
            }
        }
        rule.onNodeWithTag(ProfileTags.CHILD_CARD).performScrollTo().performClick()
        rule.onNodeWithTag(ProfileTags.CHILDREN).performScrollTo().performClick()
        rule.onNodeWithTag(ProfileTags.REMINDER).performScrollTo().performClick()
        rule.onNodeWithTag("${ProfileTags.NOTIFICATIONS}_toggle").performScrollTo().performClick()
        rule.onNodeWithTag(ProfileTags.DOWNLOADS).performScrollTo().performClick()
        rule.onNodeWithTag(ProfileTags.SUBSCRIPTION).performScrollTo().performClick()
        assertEquals(listOf("edit:child_1", "children", "reminder", "notify:false", "downloads", "subscription"), calls)
    }

    @Test
    fun childProfilesSwitchEditAndAdd() {
        rule.setContent {
            LittleStoryTheme {
                ChildProfilesScreen(
                    childrenState,
                    ChildProfilesActions(onEdit = { calls += "edit:$it" }, onAdd = { calls += "add" }),
                    onSwitch = { calls += "switch:$it" },
                )
            }
        }
        rule.onNodeWithTag(ChildProfilesTags.active("child_1")).assertIsDisplayed()
        rule.onNodeWithTag(ChildProfilesTags.switch("child_2")).performClick()
        rule.onNodeWithTag(ChildProfilesTags.edit("child_1")).performClick()
        rule.onNodeWithTag(ChildProfilesTags.ADD_ROW).performScrollTo().performClick()
        assertEquals(listOf("switch:child_2", "edit:child_1", "add"), calls)
    }

    @Test
    fun reminderDaysTimeAndSave() {
        rule.setContent {
            LittleStoryTheme {
                ReminderScreen(
                    reminderState,
                    ReminderCallbacks(
                        onTimeClick = { calls += "time" },
                        onDayToggled = { calls += "day:$it" },
                        onSave = { calls += "save" },
                    ),
                )
            }
        }
        rule.onNodeWithTag(ReminderTags.TIME).performClick()
        rule.onNodeWithTag(ReminderTags.day(DayOfWeek.SATURDAY)).performClick()
        rule.onNodeWithTag(ReminderTags.SAVE).performClick()
        assertEquals(listOf("time", "day:SATURDAY", "save"), calls)
    }

    @Test
    fun notificationsOpenAndMarkAllRead() {
        rule.setContent {
            LittleStoryTheme {
                NotificationsScreen(
                    notificationsState,
                    NotificationsActions(
                        onStoryReady = { calls += "ready" },
                        onReminder = { calls += "reminderScreen" },
                    ),
                    onOpen = { calls += "open:$it" },
                    onMarkAllRead = { calls += "all" },
                )
            }
        }
        rule.onNodeWithTag(NotificationsTags.unread("ready"), useUnmergedTree = true).assertExists()
        rule.onNodeWithTag(NotificationsTags.item("ready")).performClick()
        rule.onNodeWithTag(NotificationsTags.MARK_ALL).performClick()
        assertEquals(listOf("open:ready", "ready", "all"), calls)
    }

    @Test
    fun subscriptionSelectsAPlanAndChooses() {
        rule.setContent {
            LittleStoryTheme {
                SubscriptionScreen(subscriptionState, {}, { calls += "select:$it" }, { calls += "choose" }, {})
            }
        }
        rule.onNodeWithTag(SubscriptionTags.plan("family")).performScrollTo().performClick()
        rule.onNodeWithTag(SubscriptionTags.CHOOSE).performClick()
        rule.onNodeWithTag(SubscriptionTags.NOTE).assertExists()
        assertEquals(listOf("select:family", "choose"), calls)
    }
}
