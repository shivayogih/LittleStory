package com.littlestory.app.journeys

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.unit.dp
import com.littlestory.core.ui.chrome.ChromeTags
import com.littlestory.feature.create.form.CreateTags
import com.littlestory.feature.discover.detail.DetailTags
import com.littlestory.feature.discover.discover.DiscoverTags
import com.littlestory.feature.home.home.HomeTags
import com.littlestory.feature.library.stories.MyStoriesTags
import com.littlestory.feature.reader.player.PlayerTags
import com.littlestory.feature.reader.reader.ReaderTags
import com.littlestory.feature.settings.help.HelpTags
import com.littlestory.feature.settings.language.LanguageTags
import com.littlestory.feature.settings.privacy.PrivacyTags
import com.littlestory.feature.settings.profile.ProfileTags
import com.littlestory.feature.settings.reminder.ReminderTags
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import kotlin.test.assertTrue

/**
 * LS-123: checks every main screen, in English and Kannada, for the two problems a semantics tree
 * can prove: tappable elements with no accessible name, and tap targets under 48 dp. The Google
 * Accessibility Test Framework does not run under Robolectric (its checks silently pass), so
 * contrast and TalkBack order stay manual checks on a device (docs/design/accessibility-spec.md).
 * Native graphics mode is needed so text is measured like on a device.
 */
abstract class AccessibilityChecks(
    languageTag: String,
) : JourneyTest(languageTag) {
    /**
     * Every tappable node on screen must have a name for TalkBack (its own text or a content
     * description, merged from its children) and a target of at least [MIN_TARGET_DP] on each side.
     */
    private fun check() {
        val minTarget = with(compose.density) { MIN_TARGET_DP.dp.roundToPx() }
        val problems =
            compose.onAllNodes(hasClickAction()).fetchSemanticsNodes().mapNotNull { node ->
                val config = node.config
                val name =
                    config.getOrNull(SemanticsProperties.ContentDescription).orEmpty() +
                        config.getOrNull(SemanticsProperties.Text).orEmpty().map { it.text }
                val where = config.getOrNull(SemanticsProperties.TestTag) ?: name.firstOrNull() ?: "node ${node.id}"
                when {
                    name.none { it.isNotBlank() } -> "$where has no accessible name"
                    node.size.width < minTarget || node.size.height < minTarget ->
                        "$where is ${node.size.width}×${node.size.height} px, below $MIN_TARGET_DP dp"
                    else -> null
                }
            }
        assertTrue(problems.isEmpty(), problems.joinToString("\n"))
    }

    /** Waits for [tags] (a screen's loaded content, not only its first element), then checks. */
    private fun visit(vararg tags: String) {
        tags.forEach(::await)
        check()
    }

    @Test
    fun mainScreensPassAccessibilityChecks() {
        signedIn()
        visit(HomeTags.CREATE, HomeTags.SEE_ALL_EXPLORE)

        tab(TAB_DISCOVER)
        visit(DiscoverTags.SEARCH, DiscoverTags.story(STORY_ID))
        tap(DiscoverTags.story(STORY_ID))
        visit(DetailTags.TITLE)
        tap(ChromeTags.ACTION_SECONDARY)
        visit(ReaderTags.PAGE)
        tap(ReaderTags.PLAY)
        visit(PlayerTags.PLAY_PAUSE)
        back()
        back()

        tab(TAB_CREATE)
        visit(CreateTags.PROMPT)
        tab(TAB_STORIES)
        visit(MyStoriesTags.SEARCH, MyStoriesTags.story(STORY_ID))

        tab(TAB_PROFILE)
        visit(ProfileTags.LANGUAGE)
        tap(ProfileTags.LANGUAGE)
        visit(LanguageTags.app(languageTag))
        back()
        tap(ProfileTags.REMINDER)
        visit(ReminderTags.TOGGLE)
        back()
        tap(ProfileTags.PRIVACY)
        visit(PrivacyTags.DELETE)
        back()
        tap(ProfileTags.HELP)
        visit(HelpTags.VERSION)
    }
}

private const val MIN_TARGET_DP = 48

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [36], application = HiltTestApplication::class, qualifiers = "en-w411dp-h891dp")
class EnglishAccessibilityChecksTest : AccessibilityChecks("en")

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [36], application = HiltTestApplication::class, qualifiers = "kn-w411dp-h891dp")
class KannadaAccessibilityChecksTest : AccessibilityChecks("kn")
