package com.littlestory.feature.settings

import com.littlestory.core.common.config.AppConfig
import com.littlestory.core.common.config.FeatureFlags
import com.littlestory.domain.model.NotificationItem
import com.littlestory.domain.model.Plan
import com.littlestory.feature.settings.children.ChildProfilesViewModel
import com.littlestory.feature.settings.notifications.NotificationsViewModel
import com.littlestory.feature.settings.profile.ProfileViewModel
import com.littlestory.feature.settings.reminder.ReminderViewModel
import com.littlestory.feature.settings.subscription.SubscriptionViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalTime
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    @get:Rule
    val main = MainDispatcherRule()

    private val preferences = FakePreferences()

    @Test
    fun profileReflectsTheActiveChildAndSavesToggles() =
        runTest {
            val vm =
                ProfileViewModel(
                    FakeProfiles(sampleChild, secondChild),
                    preferences,
                    FeatureFlags(demoSubscription = false),
                )
            backgroundScope.launch { vm.state.collect {} }
            advanceUntilIdle()
            assertEquals("Aarav", vm.state.value.activeChildName)
            assertEquals(2, vm.state.value.childCount)
            assertFalse(vm.state.value.subscriptionVisible)
            vm.onReminderToggled(true)
            advanceUntilIdle()
            vm.onWifiOnlyToggled(false)
            advanceUntilIdle()
            vm.onLanguageSelected("kn")
            advanceUntilIdle()
            assertTrue(preferences.settingsState.value.reminder.enabled)
            assertFalse(preferences.settingsState.value.wifiOnlyDownloads)
            assertTrue(vm.state.value.reminderEnabled)
            assertEquals("kn", vm.state.value.contentLanguageTag)
        }

    @Test
    fun childProfilesMarkAndSwitchTheActiveChild() =
        runTest {
            val profiles = FakeProfiles(sampleChild, secondChild)
            val vm = ChildProfilesViewModel(profiles)
            backgroundScope.launch { vm.state.collect {} }
            advanceUntilIdle()
            assertEquals(
                listOf(true, false),
                vm.state.value.children
                    .map { it.active },
            )
            vm.onSwitch("child_2")
            advanceUntilIdle()
            assertEquals("child_2", profiles.activeId.value)
            assertEquals(
                listOf(false, true),
                vm.state.value.children
                    .map { it.active },
            )
        }

    @Test
    fun reminderEditsStayLocalUntilSaved() =
        runTest {
            val vm = ReminderViewModel(preferences)
            advanceUntilIdle()
            vm.onEnabledChange(true)
            vm.onTimeClick()
            vm.onTimeSelected(19, 45)
            vm.onDayToggled(DayOfWeek.MONDAY)
            assertFalse(vm.state.value.timePickerOpen)
            assertFalse(preferences.settingsState.value.reminder.enabled)
            vm.onSave()
            advanceUntilIdle()
            val saved = preferences.settingsState.value.reminder
            assertTrue(saved.enabled)
            assertEquals(LocalTime.of(19, 45), saved.time)
            assertEquals(setOf(DayOfWeek.MONDAY), saved.days)
            assertEquals(LocalTime.of(19, 45), vm.state.value.savedTime)
            vm.onToastShown()
            assertNull(vm.state.value.savedTime)
        }

    @Test
    fun inboxMarksOneOrAllRead() =
        runTest {
            val inbox =
                FakeNotifications(
                    listOf(
                        NotificationItem("a", "A", "a", read = false),
                        NotificationItem("b", "B", "b", read = false),
                    ),
                )
            val vm = NotificationsViewModel(preferences, inbox)
            backgroundScope.launch { vm.state.collect {} }
            advanceUntilIdle()
            assertTrue(vm.state.value.hasUnread)
            vm.onOpen("a")
            advanceUntilIdle()
            assertEquals(
                listOf(true, false),
                vm.state.value.items
                    .map { it.read },
            )
            vm.onMarkAllRead()
            advanceUntilIdle()
            assertFalse(vm.state.value.hasUnread)
        }

    @Test
    fun subscriptionDefaultsToYearlyAndConfirmsTheChoice() =
        runTest {
            val plans =
                listOf(Plan("monthly", 24_900, "INR", false), Plan("yearly", 179_900, "INR", true, bestValue = true))
            val vm = SubscriptionViewModel(FakeSubscription(plans))
            backgroundScope.launch { vm.state.collect {} }
            advanceUntilIdle()
            assertEquals(
                AppConfig.Subscription.DEFAULT_PLAN_ID,
                vm.state.value.selected
                    ?.id,
            )
            vm.onSelect("monthly")
            vm.onChoose()
            advanceUntilIdle()
            assertEquals("monthly", vm.state.value.confirmedId)
            vm.onDialogDismiss()
            advanceUntilIdle()
            assertNull(vm.state.value.confirmedId)
        }
}
