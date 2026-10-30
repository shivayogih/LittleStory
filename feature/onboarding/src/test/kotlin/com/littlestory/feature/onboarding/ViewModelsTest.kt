package com.littlestory.feature.onboarding

import androidx.lifecycle.SavedStateHandle
import com.littlestory.core.common.config.AppConfig
import com.littlestory.core.ui.text.UiText
import com.littlestory.domain.model.AvatarId
import com.littlestory.domain.model.ChildProfile
import com.littlestory.feature.onboarding.onboarding.OnboardingEffect
import com.littlestory.feature.onboarding.onboarding.OnboardingViewModel
import com.littlestory.feature.onboarding.setup.SetupEffect
import com.littlestory.feature.onboarding.setup.SetupViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

// Robolectric supplies the Android Bundle that SavedStateHandle needs.
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
@OptIn(ExperimentalCoroutinesApi::class)
class ViewModelsTest {
    @get:Rule
    val main = MainDispatcherRule()

    private val profiles = FakeProfilesRepository()
    private val prefs = FakePreferencesRepository()

    private fun setup(childId: String? = null) =
        SetupViewModel(
            // keys match the route's argument names (type-safe routes store arguments by name)
            SavedStateHandle(if (childId == null) emptyMap() else mapOf("childId" to childId)),
            profiles,
            prefs,
        )

    @Test
    fun onboardingAdvancesThenLeavesFromTheLastSlide() =
        runTest {
            val vm = OnboardingViewModel(SavedStateHandle())
            assertEquals(0, vm.state.value.page)
            vm.onContinue()
            vm.onContinue()
            assertTrue(vm.state.value.isLast)
            vm.onContinue()
            assertEquals(OnboardingEffect.GoToSetup, vm.effects.first())
        }

    @Test
    fun onboardingSkipGoesToSetupAndPageSurvivesRecreation() =
        runTest {
            val handle = SavedStateHandle()
            OnboardingViewModel(handle).onContinue()
            val recreated = OnboardingViewModel(handle)
            assertEquals(1, recreated.state.value.page)
            recreated.onSkip()
            assertEquals(OnboardingEffect.GoToSetup, recreated.effects.first())
        }

    @Test
    fun ctaNamesTheChildOnceANameIsTyped() {
        val vm = setup()
        assertEquals(UiText.Res(R.string.setup_cta_create), vm.state.value.ctaLabel)
        vm.onNameChange("Aarav")
        assertEquals(UiText.Res(R.string.setup_cta_create_named, listOf("Aarav")), vm.state.value.ctaLabel)
    }

    @Test
    fun submittingWithoutANameShowsAnErrorAndSavesNothing() =
        runTest {
            val vm = setup()
            vm.onSubmit()
            advanceUntilIdle()
            assertNotNull(vm.state.value.nameError)
            assertTrue(profiles.created.isEmpty())
            assertEquals(false, prefs.firstRun.value)
        }

    @Test
    fun submittingSavesTheTrimmedProfileMarksFirstRunDoneAndFinishes() =
        runTest {
            val vm = setup()
            vm.onNameChange("  Aarav ")
            vm.onAgeSelected(8)
            vm.onInterestToggled("space")
            vm.onDurationSelected(15)
            vm.onLanguageSelected("kn")
            assertEquals(SetupEffect.ApplyLanguage("kn"), vm.effects.first())
            vm.onSubmit()
            advanceUntilIdle()
            val saved = profiles.created.single()
            assertEquals("  Aarav ", saved.firstName) // trimming happens in the repository
            assertEquals(8, saved.age)
            assertEquals(setOf("space"), saved.interestIds)
            assertEquals(15, saved.defaultDurationMinutes)
            assertEquals("kn", prefs.language.value)
            assertEquals(true, prefs.firstRun.value)
            assertEquals(SetupEffect.Done, vm.effects.first())
        }

    @Test
    fun skipForNowSavesNoProfileButCompletesFirstRun() =
        runTest {
            val vm = setup()
            vm.onSkip()
            advanceUntilIdle()
            assertTrue(profiles.created.isEmpty())
            assertEquals(true, prefs.firstRun.value)
            assertEquals(SetupEffect.Done, vm.effects.first())
        }

    @Test
    fun editingLoadsTheChildAndSavesChangesWithoutCreating() =
        runTest {
            profiles.seed(ChildProfile("c1", "Ananya", 9, AvatarId(2), setOf("music"), "kn", 5))
            val vm = setup("c1")
            advanceUntilIdle()
            val loaded = vm.state.value
            assertEquals("Ananya", loaded.name)
            assertEquals(9, loaded.age)
            assertEquals(setOf("music"), loaded.interestIds)
            assertTrue(loaded.isEditing)
            vm.onNameChange("Ananya R")
            vm.onSubmit()
            advanceUntilIdle()
            assertTrue(profiles.created.isEmpty())
            assertEquals(
                "Ananya R",
                profiles.profiles
                    .first()
                    .single()
                    .firstName,
            )
        }

    @Test
    fun nameLengthIsCappedByConfigAndUiLanguageSeedsTheForm() {
        val vm = setup()
        vm.onNameChange("x".repeat(AppConfig.Setup.NAME_MAX_CHARS + 10))
        assertEquals(AppConfig.Setup.NAME_MAX_CHARS, vm.state.value.name.length)
        vm.onUiLanguageKnown("kn-IN")
        assertEquals("kn", vm.state.value.languageTag)
        assertNull(vm.state.value.nameError)
    }
}
