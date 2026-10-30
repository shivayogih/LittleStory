package com.littlestory.app

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import com.littlestory.domain.repository.PreferencesRepository
import com.littlestory.domain.repository.ProfilesRepository
import com.littlestory.feature.home.home.HomeTags
import com.littlestory.feature.onboarding.onboarding.OnboardingTags
import com.littlestory.feature.onboarding.setup.SetupTags
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import javax.inject.Inject
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/** Fresh install: Onboarding, then Setup, then Home, with the profile and first-run flag persisted. */
@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], application = HiltTestApplication::class)
@OptIn(ExperimentalTestApi::class)
class FirstRunFlowTest {
    private val hilt = HiltAndroidRule(this)
    private val compose = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val rules: RuleChain = RuleChain.outerRule(hilt).around(compose)

    @Inject lateinit var profiles: ProfilesRepository

    @Inject lateinit var preferences: PreferencesRepository

    @Test
    fun onboardingThenSetupLeadsToHomeAndPersistsTheProfile() {
        hilt.inject()
        compose.waitUntilExactlyOneExists(hasTestTag(OnboardingTags.TITLE), WAIT_TIMEOUT_MS)
        repeat(PAGES_BEFORE_SETUP) { compose.onNodeWithTag(OnboardingTags.CONTINUE).performClick() }

        compose.waitUntilExactlyOneExists(hasTestTag(SetupTags.NAME), WAIT_TIMEOUT_MS)
        compose.onNodeWithTag(SetupTags.NAME).performTextClearance()
        compose.onNodeWithTag(SetupTags.NAME).performTextInput(CHILD_NAME)
        compose.onNodeWithTag(SetupTags.SUBMIT).performClick()

        compose.waitUntilExactlyOneExists(hasTestTag(HomeTags.TITLE), WAIT_TIMEOUT_MS)
        compose.onNodeWithTag(HomeTags.TITLE).assertIsDisplayed()
        runBlocking {
            assertEquals(true, preferences.firstRunCompleted.first())
            assertEquals(CHILD_NAME, assertNotNull(profiles.activeProfile.first()).firstName)
        }
    }

    private companion object {
        // Three onboarding slides: two Continue taps move to the last slide, a third opens Setup.
        const val PAGES_BEFORE_SETUP = 3
        const val CHILD_NAME = "Meera"

        // Saving the profile runs on real IO threads (Room, DataStore); the 1 s default is too tight on a slow CI runner.
        const val WAIT_TIMEOUT_MS = 30_000L
    }
}
