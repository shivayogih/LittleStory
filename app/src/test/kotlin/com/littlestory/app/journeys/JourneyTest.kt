package com.littlestory.app.journeys

import android.os.Looper
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.ComposeTimeoutException
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasScrollToNodeAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.printToString
import com.littlestory.app.MainActivity
import com.littlestory.core.common.config.AppConfig
import com.littlestory.core.ui.chrome.ChromeTags
import com.littlestory.domain.model.AvatarId
import com.littlestory.domain.model.ChildProfileDraft
import com.littlestory.domain.repository.CatalogRepository
import com.littlestory.domain.repository.FavouritesRepository
import com.littlestory.domain.repository.LibraryRepository
import com.littlestory.domain.repository.PreferencesRepository
import com.littlestory.domain.repository.ProfilesRepository
import com.littlestory.domain.repository.ProgressRepository
import com.littlestory.feature.home.home.HomeTags
import dagger.hilt.android.testing.HiltAndroidRule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.rules.RuleChain
import org.robolectric.Shadows.shadowOf
import javax.inject.Inject

/**
 * Shared driver for the LS-121 journeys. Each journey runs twice, in English and in Kannada (the
 * Robolectric locale qualifier), against the real app graph: Room, DataStore and mock JSON.
 * Nodes are found by test tag only, never by visible text, so the same steps work in both languages.
 */
@OptIn(ExperimentalTestApi::class)
abstract class JourneyTest(
    protected val languageTag: String,
) {
    protected val hilt = HiltAndroidRule(this)
    protected val compose = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val rules: RuleChain = RuleChain.outerRule(hilt).around(compose)

    @Inject lateinit var profiles: ProfilesRepository

    @Inject lateinit var preferences: PreferencesRepository

    @Inject lateinit var favourites: FavouritesRepository

    @Inject lateinit var progress: ProgressRepository

    @Inject lateinit var library: LibraryRepository

    @Inject lateinit var catalog: CatalogRepository

    /** A child already set up: the app moves from Onboarding to Home once first run is complete. */
    protected fun signedIn(): String {
        hilt.inject()
        val childId =
            read {
                val child =
                    profiles.create(
                        ChildProfileDraft(
                            firstName = CHILD_NAME,
                            age = AppConfig.Setup.DEFAULT_AGE,
                            avatar = AvatarId(0),
                            interestIds = emptySet(),
                            languageTag = languageTag,
                            defaultDurationMinutes = AppConfig.Setup.DEFAULT_DURATION_MINUTES,
                        ),
                    )
                preferences.setContentLanguageTag(languageTag)
                preferences.setFirstRunCompleted(true)
                child.id
            }
        await(HomeTags.TITLE)
        return childId
    }

    protected fun await(tag: String) = explained(tag) { compose.waitUntilAtLeastOneExists(hasTestTag(tag), TIMEOUT_MS) }

    /** On a timeout, fails with the screen's semantics tree so CI logs show where the journey was. */
    private fun explained(tag: String, wait: () -> Unit) {
        try {
            wait()
        } catch (timeout: ComposeTimeoutException) {
            val tree = runCatching { compose.onRoot(useUnmergedTree = true).printToString() }.getOrDefault("")
            throw AssertionError("'$tag' never appeared. Screen:\n$tree", timeout)
        }
    }

    protected fun awaitGone(tag: String) = compose.waitUntilDoesNotExist(hasTestTag(tag), TIMEOUT_MS)

    /**
     * Waits for [tag], bringing it into view first: lazy lists only compose what is visible, and a
     * tap outside the window is lost. Then taps it.
     */
    protected fun tap(tag: String) {
        explained(tag) { compose.waitUntil(TIMEOUT_MS) { exists(tag) || scrollListsTo(tag) } }
        val node = compose.onAllNodes(hasTestTag(tag)).onFirst()
        runCatching { node.performScrollTo() }
        node.performClick()
        compose.waitForIdle()
    }

    private fun exists(tag: String) = compose.onAllNodes(hasTestTag(tag)).fetchSemanticsNodes().isNotEmpty()

    private fun scrollListsTo(tag: String): Boolean {
        val lists = compose.onAllNodes(hasScrollToNodeAction())
        lists.fetchSemanticsNodes().indices.forEach { index ->
            runCatching { lists[index].performScrollToNode(hasTestTag(tag)) }
        }
        return exists(tag)
    }

    protected fun tab(index: Int) = tap(ChromeTags.navTab(index))

    protected fun back() {
        compose.runOnUiThread { compose.activity.onBackPressedDispatcher.onBackPressed() }
        compose.waitForIdle()
    }

    /** Every node whose test tag starts with [prefix], e.g. all story rows of a list. */
    protected fun tagsStartingWith(prefix: String): List<String> =
        compose
            .onAllNodes(
                SemanticsMatcher("tag starts with $prefix") { node ->
                    node.config.getOrNull(SemanticsProperties.TestTag)?.startsWith(prefix) == true
                },
            ).fetchSemanticsNodes()
            .mapNotNull { it.config.getOrNull(SemanticsProperties.TestTag) }

    protected fun awaitPrefix(prefix: String) =
        compose.waitUntil(TIMEOUT_MS) { tagsStartingWith(prefix).isNotEmpty() }

    /**
     * Runs repository work on a background thread while the main looper keeps turning. A plain
     * runBlocking on the test (main) thread can deadlock when Room or DataStore hands work back
     * to the main thread.
     */
    protected fun <T> read(block: suspend () -> T): T {
        val work = background.async { block() }
        val deadline = System.currentTimeMillis() + TIMEOUT_MS
        while (!work.isCompleted) {
            if (System.currentTimeMillis() > deadline) throw AssertionError("Repository call timed out")
            shadowOf(Looper.getMainLooper()).idle()
            Thread.sleep(POLL_MS)
        }
        return runBlocking { work.await() }
    }

    private val background = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * Polls a repository until [check] holds. Between polls the main looper idles, so view-model
     * coroutines on Dispatchers.Main can finish their writes; `compose.waitUntil` with a blocking
     * read would starve them.
     */
    protected fun eventually(what: String, check: suspend () -> Boolean) {
        val deadline = System.currentTimeMillis() + TIMEOUT_MS
        while (!read { check() }) {
            if (System.currentTimeMillis() > deadline) throw AssertionError("Timed out waiting for $what")
            compose.waitForIdle()
            Thread.sleep(POLL_MS)
        }
    }

    protected suspend fun activeChildId(): String = requireNotNull(profiles.activeProfile.first()).id

    protected companion object {
        const val CHILD_NAME = "Meera"
        const val STORY_ID = "story_little_elephant"

        const val TAB_HOME = 0
        const val TAB_DISCOVER = 1
        const val TAB_CREATE = 2
        const val TAB_STORIES = 3
        const val TAB_PROFILE = 4

        // Room, DataStore and the simulated generation run on real threads; the 1 s default is
        // too tight on a slow CI runner.
        const val TIMEOUT_MS = 30_000L
        const val POLL_MS = 50L
    }
}
