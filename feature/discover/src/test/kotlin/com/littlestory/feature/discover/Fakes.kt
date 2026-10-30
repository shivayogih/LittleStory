package com.littlestory.feature.discover

import com.littlestory.domain.model.AvatarId
import com.littlestory.domain.model.ChildProfile
import com.littlestory.domain.model.ChildProfileDraft
import com.littlestory.domain.model.Download
import com.littlestory.domain.model.DownloadStatus
import com.littlestory.domain.model.ParentSettings
import com.littlestory.domain.model.ReadingPreferences
import com.littlestory.domain.model.ReadingProgress
import com.littlestory.domain.model.ReminderSettings
import com.littlestory.domain.model.Story
import com.littlestory.domain.model.StoryArtSpec
import com.littlestory.domain.model.StoryCategory
import com.littlestory.domain.model.StoryPage
import com.littlestory.domain.repository.CatalogRepository
import com.littlestory.domain.repository.DownloadsRepository
import com.littlestory.domain.repository.FavouritesRepository
import com.littlestory.domain.repository.PreferencesRepository
import com.littlestory.domain.repository.ProfilesRepository
import com.littlestory.domain.repository.ProgressRepository
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

fun story(
    id: String,
    categoryId: String = "friendship",
    tags: List<String> = emptyList(),
    minutes: Int = 6,
    added: Long = 1,
    title: String = "Title $id",
    synopsis: String = "Synopsis $id",
) = Story(
    id = id,
    title = title,
    synopsis = synopsis,
    categoryId = categoryId,
    tags = tags,
    ageMin = 4,
    ageMax = 7,
    durationMinutes = minutes,
    lesson = "Lesson $id",
    addedEpochDay = added,
    art = StoryArtSpec(categoryId, "moon"),
    pages = listOf(StoryPage(0, "Page")),
    resolvedLanguage = "en",
)

val sampleChild =
    ChildProfile("child_1", "Aarav", 6, AvatarId(1), setOf("animals", "adventure"), "en", 10)

class FakeCatalog(
    val stories: List<Story>,
    val categories: List<StoryCategory> =
        listOf(
            StoryCategory("friendship", "Friendship", "d", 1),
            StoryCategory("bedtime", "Bedtime", "d", 1),
            StoryCategory("animals", "Animals", "d", 1),
        ),
) : CatalogRepository {
    override fun categories(languageTag: String): Flow<List<StoryCategory>> = MutableStateFlow(categories)

    override fun stories(languageTag: String): Flow<List<Story>> = MutableStateFlow(stories)

    override fun story(id: String, languageTag: String): Flow<Story?> = MutableStateFlow(stories.firstOrNull { it.id == id })
}

class FakeFavourites : FavouritesRepository {
    val ids = MutableStateFlow<Map<String, Set<String>>>(emptyMap())

    override fun favouriteIds(childId: String): Flow<Set<String>> = ids.map { it[childId].orEmpty() }

    override suspend fun setFavourite(childId: String, storyId: String, favourite: Boolean) {
        val current = ids.value[childId].orEmpty()
        ids.value = ids.value + (childId to if (favourite) current + storyId else current - storyId)
    }
}

class FakeProgress : ProgressRepository {
    val rows = MutableStateFlow<List<ReadingProgress>>(emptyList())

    override fun progress(childId: String): Flow<List<ReadingProgress>> = rows

    override suspend fun update(progress: ReadingProgress) {
        rows.value = rows.value.filterNot { it.storyId == progress.storyId } + progress
    }
}

class FakeProfiles(child: ChildProfile? = null) : ProfilesRepository {
    private val all = MutableStateFlow(listOfNotNull(child))
    override val profiles: Flow<List<ChildProfile>> = all
    override val activeProfile: Flow<ChildProfile?> = all.map { it.firstOrNull() }

    override suspend fun create(draft: ChildProfileDraft): ChildProfile = error("not used")

    override suspend fun update(profile: ChildProfile) = Unit

    override suspend fun delete(profileId: String) = Unit

    override suspend fun setActive(profileId: String) = Unit
}

class FakePreferences(language: String = "en") : PreferencesRepository {
    val language = MutableStateFlow(language)
    override val firstRunCompleted: Flow<Boolean> = MutableStateFlow(true)

    override suspend fun setFirstRunCompleted(completed: Boolean) = Unit

    override val contentLanguageTag: Flow<String> = this.language

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

class FakeDownloads : DownloadsRepository {
    private val all = MutableStateFlow<List<Download>>(emptyList())
    override val downloads: Flow<List<Download>> = all

    override suspend fun start(storyId: String) {
        all.value = all.value + Download(storyId, 1, DownloadStatus.Completed)
    }

    override suspend fun remove(storyId: String) {
        all.value = all.value.filterNot { it.storyId == storyId }
    }
}
