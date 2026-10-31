package com.littlestory.app.journeys

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTextInput
import com.littlestory.core.common.language.SupportedLanguages
import com.littlestory.core.ui.chrome.ChromeTags
import com.littlestory.core.ui.language.AppLanguage
import com.littlestory.core.ui.story.StoryTags
import com.littlestory.feature.create.form.CreateTags
import com.littlestory.feature.create.generating.GeneratingTags
import com.littlestory.feature.create.ready.ReadyTags
import com.littlestory.feature.discover.detail.DetailTags
import com.littlestory.feature.discover.discover.DiscoverTags
import com.littlestory.feature.home.home.HomeTags
import com.littlestory.feature.library.navigation.LibraryTab
import com.littlestory.feature.library.stories.MyStoriesTags
import com.littlestory.feature.onboarding.onboarding.OnboardingTags
import com.littlestory.feature.onboarding.setup.SetupTags
import com.littlestory.feature.reader.player.PlayerTags
import com.littlestory.feature.reader.reader.ReaderTags
import com.littlestory.feature.settings.help.HelpTags
import com.littlestory.feature.settings.language.LanguageTags
import com.littlestory.feature.settings.privacy.PrivacyTags
import com.littlestory.feature.settings.profile.ProfileTags
import com.littlestory.feature.settings.reminder.ReminderTags
import kotlinx.coroutines.flow.first
import org.junit.After
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/** Journeys A–G from `docs/product/user-journeys.md` (LS-121); subclasses pick the language. */
@OptIn(ExperimentalTestApi::class)
abstract class Journeys(
    languageTag: String,
) : JourneyTest(languageTag) {
    @After
    fun followSystemLanguageAgain() = AppLanguage.reset()

    @Test
    fun journeyA_firstTimeExperience() {
        hilt.inject()
        tap(OnboardingTags.SKIP)
        await(SetupTags.NAME)
        compose.onNodeWithTag(SetupTags.NAME).performTextInput(CHILD_NAME)
        tap(SetupTags.SUBMIT)

        await(HomeTags.TITLE)
        read {
            assertEquals(true, preferences.firstRunCompleted.first())
            assertEquals(CHILD_NAME, assertNotNull(profiles.activeProfile.first()).firstName)
        }
    }

    @Test
    fun journeyB_discoverReadAndSave() {
        val childId = signedIn()
        tab(TAB_DISCOVER)
        tap(DiscoverTags.story(STORY_ID))
        await(DetailTags.TITLE)
        tap(DetailTags.FAVOURITE)
        eventually("the favourite to be saved") { STORY_ID in favourites.favouriteIds(childId).first() }

        tap(ChromeTags.ACTION_SECONDARY)
        await(ReaderTags.PAGE)
        tap(ReaderTags.NEXT)
        eventually("reading progress to be saved") { progress.progress(childId).first().any { it.storyId == STORY_ID } }

        back() // Reader → Detail
        back() // Detail → Discover, where the bottom navigation shows again
        tab(TAB_STORIES)
        tap(MyStoriesTags.tab(LibraryTab.Favourites))
        await(MyStoriesTags.story(STORY_ID))
    }

    @Test
    fun journeyC_createAStory() {
        val childId = signedIn()
        tab(TAB_CREATE)
        tap(CreateTags.SUBMIT)
        await(CreateTags.ERROR)

        tap(CreateTags.suggestion(0))
        tap(CreateTags.SUBMIT)
        await(GeneratingTags.PERCENT)
        await(ReadyTags.TITLE)
        val created = read { library.created(childId).first() }
        assertEquals(1, created.size)

        tap(ChromeTags.ACTION_SECONDARY)
        await(ReaderTags.PAGE)
        back()
        tap(ReadyTags.SAVED)
        await(MyStoriesTags.story(created.single().id))
    }

    @Test
    fun journeyD_revisitASavedStory() {
        val childId = signedIn()
        tab(TAB_DISCOVER)
        tap(DiscoverTags.story(STORY_ID))
        // Home's continue card resumes listening, so the earlier visit is a listen in the Player.
        tap(ChromeTags.ACTION_PRIMARY)
        tap(PlayerTags.FORWARD)
        // Progress is saved on pause; two taps pause once whether or not playback started itself.
        tap(PlayerTags.PLAY_PAUSE)
        tap(PlayerTags.PLAY_PAUSE)
        eventually("listening progress to be saved") {
            progress.progress(childId).first().any { it.storyId == STORY_ID && it.listenFraction > 0f }
        }
        back() // Player → Detail
        back() // Detail → Discover
        tab(TAB_HOME)

        await(HomeTags.CONTINUE_CARD)
        tap(StoryTags.RESUME)
        await(PlayerTags.PLAY_PAUSE)
        back()
        tab(TAB_STORIES)
        tap(MyStoriesTags.RECENT)
        await(PlayerTags.PLAY_PAUSE)
    }

    @Test
    fun journeyE_parentControls() {
        signedIn()
        tab(TAB_PROFILE)
        tap(ProfileTags.REMINDER)
        await(ReminderTags.TOGGLE)
        back()
        tap(ProfileTags.HELP)
        await(HelpTags.VERSION)
        back()

        tap(ProfileTags.PRIVACY)
        passParentalGate()
        tap(PrivacyTags.DELETE)
        tap(PrivacyTags.CONFIRM)
        await(OnboardingTags.TITLE)
        read {
            assertEquals(false, preferences.firstRunCompleted.first())
            assertTrue(profiles.profiles.first().isEmpty())
        }
    }

    @Test
    fun journeyF_simulatedAudio() {
        signedIn()
        tab(TAB_DISCOVER)
        tap(DiscoverTags.story(STORY_ID))
        tap(ChromeTags.ACTION_PRIMARY)
        await(PlayerTags.SIMULATED)
        tap(PlayerTags.PLAY_PAUSE)
        tap(PlayerTags.FORWARD)

        tap(PlayerTags.STORY_TEXT)
        await(ReaderTags.PAGE)
        tap(ReaderTags.PLAY)
        await(PlayerTags.PLAY_PAUSE)
    }

    @Test
    fun journeyG_changeLanguage() {
        signedIn()
        val other = SupportedLanguages.all.first { it.tag != languageTag }.tag
        tab(TAB_PROFILE)
        tap(ProfileTags.LANGUAGE)
        passParentalGate()
        tap(LanguageTags.story(other))
        eventually("the story language to be saved") { preferences.contentLanguageTag.first() == other }

        back()
        tab(TAB_DISCOVER)
        tap(DiscoverTags.story(STORY_ID))
        val title = read { assertNotNull(catalog.story(STORY_ID, other).first()).title }
        compose.waitUntil(TIMEOUT_MS) {
            compose.onAllNodes(hasTestTag(DetailTags.TITLE)).fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithTag(DetailTags.TITLE).assertTextEquals(title)

        back()
        tab(TAB_PROFILE)
        tap(ProfileTags.LANGUAGE)
        passParentalGate()
        tap(LanguageTags.app(other))
        assertEquals(other, AppCompatDelegate.getApplicationLocales().toLanguageTags())
    }
}
