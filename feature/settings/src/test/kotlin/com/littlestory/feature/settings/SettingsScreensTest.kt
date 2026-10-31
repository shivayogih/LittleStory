package com.littlestory.feature.settings

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.littlestory.core.common.safety.ParentalGateChallenge
import com.littlestory.core.designsystem.theme.LittleStoryTheme
import com.littlestory.core.ui.safety.LocalParentalGateChallenges
import com.littlestory.core.ui.safety.ParentalGateTags
import com.littlestory.feature.settings.children.ChildProfilesActions
import com.littlestory.feature.settings.children.ChildProfilesScreen
import com.littlestory.feature.settings.children.ChildProfilesTags
import com.littlestory.feature.settings.help.HelpScreen
import com.littlestory.feature.settings.help.HelpTags
import com.littlestory.feature.settings.language.LanguageCallbacks
import com.littlestory.feature.settings.language.LanguageScreen
import com.littlestory.feature.settings.language.LanguageTags
import com.littlestory.feature.settings.notifications.NotificationsActions
import com.littlestory.feature.settings.notifications.NotificationsScreen
import com.littlestory.feature.settings.notifications.NotificationsTags
import com.littlestory.feature.settings.privacy.PrivacyCallbacks
import com.littlestory.feature.settings.privacy.PrivacyScreen
import com.littlestory.feature.settings.privacy.PrivacyTags
import com.littlestory.feature.settings.privacy.PrivacyUiState
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
                CompositionLocalProvider(LocalParentalGateChallenges provides { gateChallenge }) {
                    ProfileScreen(
                        profileState,
                        ProfileActions(
                            onEditChild = { calls += "edit:$it" },
                            onChildren = { calls += "children" },
                            onReminder = { calls += "reminder" },
                            onNotifications = { calls += "notifications" },
                            onDownloads = { calls += "downloads" },
                            onSubscription = { calls += "subscription" },
                            onLanguage = { calls += "language" },
                            onPrivacy = { calls += "privacy" },
                            onHelp = { calls += "help" },
                        ),
                        ProfileCallbacks(onNotificationsToggled = { calls += "notify:$it" }),
                    )
                }
            }
        }
        rule.onNodeWithTag(ProfileTags.CHILD_CARD).performScrollTo().performClick()
        assertEquals(emptyList(), calls, "editing a child waits for the parental gate")
        gateChallenge.answer.toString().forEach {
            rule.onNodeWithTag(ParentalGateTags.key(it.digitToInt())).performScrollTo().performClick()
        }
        rule.onNodeWithTag(ParentalGateTags.SUBMIT).performScrollTo().performClick()
        rule.onNodeWithTag(ProfileTags.CHILDREN).performScrollTo().performClick()
        rule.onNodeWithTag(ProfileTags.REMINDER).performScrollTo().performClick()
        rule.onNodeWithTag("${ProfileTags.NOTIFICATIONS}_toggle").performScrollTo().performClick()
        rule.onNodeWithTag(ProfileTags.DOWNLOADS).performScrollTo().performClick()
        rule.onNodeWithTag(ProfileTags.SUBSCRIPTION).performScrollTo().performClick()
        rule.onNodeWithTag(ProfileTags.LANGUAGE).performScrollTo().performClick()
        rule.onNodeWithTag(ProfileTags.PRIVACY).performScrollTo().performClick()
        rule.onNodeWithTag(ProfileTags.HELP).performScrollTo().performClick()
        assertEquals(
            listOf(
                "edit:child_1",
                "children",
                "reminder",
                "notify:false",
                "downloads",
                "subscription",
                "language",
                "privacy",
                "help",
            ),
            calls,
        )
    }

    @Test
    fun profileCardStaysClosedWhenTheGateIsCancelled() {
        rule.setContent {
            LittleStoryTheme {
                CompositionLocalProvider(LocalParentalGateChallenges provides { gateChallenge }) {
                    ProfileScreen(
                        profileState,
                        ProfileActions(onEditChild = { calls += "edit:$it" }),
                        ProfileCallbacks(),
                    )
                }
            }
        }
        rule.onNodeWithTag(ProfileTags.CHILD_CARD).performScrollTo().performClick()
        rule.onNodeWithTag(ParentalGateTags.key(1)).performScrollTo().performClick()
        rule.onNodeWithTag(ParentalGateTags.SUBMIT).performScrollTo().performClick()
        rule.onNodeWithTag(ParentalGateTags.CANCEL).performScrollTo().performClick()
        rule.onNodeWithTag(ParentalGateTags.DIALOG).assertDoesNotExist()
        assertEquals(emptyList(), calls)
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

    @Test
    fun languageRowsReportTheChosenLanguage() {
        rule.setContent {
            LittleStoryTheme {
                LanguageScreen(
                    languageState,
                    LanguageCallbacks(onAppLanguage = { calls += "app:${it.tag}" }, onStoryLanguage = {
                        calls +=
                            "story:${it.tag}"
                    }),
                )
            }
        }
        rule.onNodeWithTag(LanguageTags.app("en")).assertIsSelected()
        rule.onNodeWithTag(LanguageTags.story("kn")).assertIsSelected()
        rule.onNodeWithTag(LanguageTags.app("kn")).performClick()
        rule.onNodeWithTag(LanguageTags.story("en")).performClick()
        assertEquals(listOf("app:kn", "story:en"), calls)
    }

    @Test
    fun privacyAsksBeforeDeleting() {
        rule.setContent {
            LittleStoryTheme {
                PrivacyScreen(
                    PrivacyUiState(confirmOpen = false),
                    PrivacyCallbacks(onDeleteClick = { calls += "delete" }),
                )
            }
        }
        rule.onNodeWithTag(PrivacyTags.DELETE).performScrollTo().performClick()
        assertEquals(listOf("delete"), calls)
    }

    @Test
    fun privacyShowsFamilySafetyAndThePolicyMarkedAsDraft() {
        rule.setContent { LittleStoryTheme { PrivacyScreen(PrivacyUiState(), PrivacyCallbacks()) } }
        rule.onNodeWithTag(PrivacyTags.DRAFT_NOTICE).assertIsDisplayed()
        rule.onNodeWithTag(PrivacyTags.SAFETY).performScrollTo().assertIsDisplayed()
        rule.onNodeWithTag(PrivacyTags.POLICY).performScrollTo().assertIsDisplayed()
    }

    @Test
    fun privacyHidesTheDraftNoticeOnceTheTextIsApproved() {
        rule.setContent {
            LittleStoryTheme {
                PrivacyScreen(
                    PrivacyUiState(showDraftNotice = false),
                    PrivacyCallbacks(),
                )
            }
        }
        rule.onNodeWithTag(PrivacyTags.DRAFT_NOTICE).assertDoesNotExist()
    }

    @Test
    fun helpShowsVersionAndLicences() {
        rule.setContent { LittleStoryTheme { HelpScreen(testAppInfo, onBack = {}) } }
        rule.onNodeWithTag(HelpTags.VERSION).performScrollTo().assertIsDisplayed()
        rule.onNodeWithTag(HelpTags.LICENCES).performScrollTo().assertIsDisplayed()
    }

    private companion object {
        val gateChallenge = ParentalGateChallenge(12, 7)
    }
}
