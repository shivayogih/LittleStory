package com.littlestory.feature.onboarding

import com.littlestory.domain.model.ChildProfile
import com.littlestory.domain.model.ChildProfileDraft
import com.littlestory.domain.model.ParentSettings
import com.littlestory.domain.model.ReadingPreferences
import com.littlestory.domain.model.ReminderSettings
import com.littlestory.domain.repository.PreferencesRepository
import com.littlestory.domain.repository.ProfilesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.time.LocalTime

class FakeProfilesRepository : ProfilesRepository {
    private val all = MutableStateFlow<List<ChildProfile>>(emptyList())
    private val activeId = MutableStateFlow<String?>(null)
    var created = mutableListOf<ChildProfileDraft>()

    override val profiles: Flow<List<ChildProfile>> = all
    override val activeProfile: Flow<ChildProfile?> =
        all.map {
            it.firstOrNull { p -> p.id == activeId.value }
                ?: it.firstOrNull()
        }

    override suspend fun create(draft: ChildProfileDraft): ChildProfile {
        created += draft
        val profile =
            ChildProfile(
                "child_${all.value.size + 1}",
                draft.firstName.trim(),
                draft.age,
                draft.avatar,
                draft.interestIds,
                draft.languageTag,
                draft.defaultDurationMinutes,
            )
        all.value = all.value + profile
        return profile
    }

    override suspend fun update(profile: ChildProfile) {
        all.value = all.value.map { if (it.id == profile.id) profile else it }
    }

    override suspend fun delete(profileId: String) {
        all.value = all.value.filterNot { it.id == profileId }
    }

    override suspend fun setActive(profileId: String) {
        activeId.value = profileId
    }

    fun seed(profile: ChildProfile) {
        all.value = all.value + profile
    }
}

class FakePreferencesRepository : PreferencesRepository {
    val firstRun = MutableStateFlow(false)
    val language = MutableStateFlow("en")
    override val firstRunCompleted: Flow<Boolean> = firstRun

    override suspend fun setFirstRunCompleted(completed: Boolean) {
        firstRun.value = completed
    }

    override val contentLanguageTag: Flow<String> = language

    override suspend fun setContentLanguageTag(tag: String) {
        language.value = tag
    }

    override val reading: Flow<ReadingPreferences> = MutableStateFlow(ReadingPreferences(1f, 1f))

    override suspend fun setReading(preferences: ReadingPreferences) = Unit

    override val parentSettings: Flow<ParentSettings> =
        MutableStateFlow(ParentSettings(ReminderSettings(false, LocalTime.of(20, 30), emptySet()), true, true))

    override suspend fun setParentSettings(settings: ParentSettings) = Unit
}

/** Runs viewModelScope work on a test dispatcher. */
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(val dispatcher: TestDispatcher = StandardTestDispatcher()) : TestWatcher() {
    override fun starting(description: Description) = Dispatchers.setMain(dispatcher)

    override fun finished(description: Description) = Dispatchers.resetMain()
}
