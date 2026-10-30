package com.littlestory.feature.settings

import com.littlestory.domain.generation.simulatedGenerationProgress
import com.littlestory.domain.model.AvatarId
import com.littlestory.domain.model.ChildProfile
import com.littlestory.domain.model.ChildProfileDraft
import com.littlestory.domain.model.Download
import com.littlestory.domain.model.DownloadStatus
import com.littlestory.domain.model.GenerationError
import com.littlestory.domain.model.GenerationState
import com.littlestory.domain.model.NotificationItem
import com.littlestory.domain.model.ParentSettings
import com.littlestory.domain.model.Plan
import com.littlestory.domain.model.ReadingPreferences
import com.littlestory.domain.model.ReadingProgress
import com.littlestory.domain.model.ReminderSettings
import com.littlestory.domain.model.Story
import com.littlestory.domain.model.StoryArtSpec
import com.littlestory.domain.model.StoryCategory
import com.littlestory.domain.model.StoryCreationRequest
import com.littlestory.domain.model.StoryOrigin
import com.littlestory.domain.model.StoryPage
import com.littlestory.domain.repository.CatalogRepository
import com.littlestory.domain.repository.DownloadsRepository
import com.littlestory.domain.repository.FavouritesRepository
import com.littlestory.domain.repository.GenerationRepository
import com.littlestory.domain.repository.LibraryRepository
import com.littlestory.domain.repository.LocalDataRepository
import com.littlestory.domain.repository.NotificationsRepository
import com.littlestory.domain.repository.PreferencesRepository
import com.littlestory.domain.repository.ProfilesRepository
import com.littlestory.domain.repository.ProgressRepository
import com.littlestory.domain.repository.StoryStores
import com.littlestory.domain.repository.SubscriptionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
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
    minutes: Int = 6,
    title: String = "Title $id",
    pages: Int = 3,
    origin: StoryOrigin = StoryOrigin.Catalog,
) = Story(
    id = id,
    title = title,
    synopsis = "Synopsis $id",
    categoryId = categoryId,
    ageMin = 4,
    ageMax = 7,
    durationMinutes = minutes,
    lesson = "Lesson $id",
    addedEpochDay = 1,
    art = StoryArtSpec(categoryId, "moon"),
    pages = List(pages) { StoryPage(it, "Page ${it + 1} of $id") },
    resolvedLanguage = "en",
    origin = origin,
)

val sampleChild =
    ChildProfile("child_1", "Aarav", 6, AvatarId(1), setOf("animals", "adventure"), "en", 10)

val secondChild =
    ChildProfile("child_2", "Meera", 4, AvatarId(3), setOf("bedtime"), "kn", 20)

class FakeCatalog(
    val stories: List<Story>,
    val categories: List<StoryCategory> =
        listOf(
            StoryCategory("friendship", "Friendship", "d", 1),
            StoryCategory("bedtime", "Bedtime", "d", 1),
        ),
) : CatalogRepository {
    override fun categories(languageTag: String): Flow<List<StoryCategory>> = MutableStateFlow(categories)

    override fun stories(languageTag: String): Flow<List<Story>> = MutableStateFlow(stories)

    override fun story(id: String, languageTag: String): Flow<Story?> =
        MutableStateFlow(
            stories.firstOrNull {
                it.id ==
                    id
            },
        )
}

class FakeLibrary(initial: Map<String, List<Story>> = emptyMap()) : LibraryRepository {
    val byChild = MutableStateFlow(initial)

    override fun created(childId: String): Flow<List<Story>> = byChild.map { it[childId].orEmpty() }

    override fun story(id: String): Flow<Story?> =
        byChild.map { all ->
            all.values.flatten().firstOrNull { it.id == id }
        }

    override suspend fun save(childId: String, story: Story) {
        byChild.value = byChild.value + (childId to listOf(story) + byChild.value[childId].orEmpty())
    }
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

    override fun progress(childId: String): Flow<List<ReadingProgress>> =
        rows.map { r ->
            r.filter {
                it.childId ==
                    childId
            }
        }

    override suspend fun update(progress: ReadingProgress) {
        rows.value =
            rows.value.filterNot { it.storyId == progress.storyId && it.childId == progress.childId } + progress
    }
}

class FakeDownloads(initial: List<Download> = emptyList()) : DownloadsRepository {
    val all = MutableStateFlow(initial)
    override val downloads: Flow<List<Download>> = all

    override suspend fun start(storyId: String) {
        all.value = all.value + Download(storyId, DOWNLOAD_BYTES, DownloadStatus.Completed)
    }

    override suspend fun remove(storyId: String) {
        all.value = all.value.filterNot { it.storyId == storyId }
    }

    companion object {
        const val DOWNLOAD_BYTES = 4_000_000L
    }
}

class FakeProfiles(vararg children: ChildProfile) : ProfilesRepository {
    val all = MutableStateFlow(children.toList())
    val activeId = MutableStateFlow(children.firstOrNull()?.id)
    override val profiles: Flow<List<ChildProfile>> = all
    override val activeProfile: Flow<ChildProfile?> =
        kotlinx.coroutines.flow.combine(all, activeId) { list, id -> list.firstOrNull { it.id == id } }

    override suspend fun create(draft: ChildProfileDraft): ChildProfile = error("not used")

    override suspend fun update(profile: ChildProfile) = Unit

    override suspend fun delete(profileId: String) = Unit

    override suspend fun setActive(profileId: String) {
        activeId.value = profileId
    }
}

class FakePreferences(language: String = "en") : PreferencesRepository {
    val language = MutableStateFlow(language)
    val readingState = MutableStateFlow(ReadingPreferences(1f, 1f))
    val settingsState =
        MutableStateFlow(ParentSettings(ReminderSettings(false, LocalTime.of(20, 30), emptySet()), true, true))
    override val firstRunCompleted: Flow<Boolean> = MutableStateFlow(true)

    override suspend fun setFirstRunCompleted(completed: Boolean) = Unit

    override val contentLanguageTag: Flow<String> = this.language

    override suspend fun setContentLanguageTag(tag: String) {
        language.value = tag
    }

    override val reading: Flow<ReadingPreferences> = readingState

    override suspend fun setReading(preferences: ReadingPreferences) {
        readingState.value = preferences
    }

    override val parentSettings: Flow<ParentSettings> = settingsState

    override suspend fun setParentSettings(settings: ParentSettings) {
        settingsState.value = settings
    }
}

/** Uses the real simulated progress, then reports [storyId]; unknown requests fail like the data layer. */
class FakeGeneration(private val storyId: String = "generated_1") : GenerationRepository {
    val submitted = mutableListOf<StoryCreationRequest>()
    var failNext = false

    override fun submit(request: StoryCreationRequest) {
        submitted += request
    }

    override fun request(requestId: String): StoryCreationRequest? = submitted.firstOrNull { it.requestId == requestId }

    override fun generate(requestId: String, startPercent: Int): Flow<GenerationState> {
        if (request(requestId) == null || failNext) {
            failNext = false
            return flowOf(GenerationState.Failed(requestId, GenerationError.Unknown))
        }
        val progress: Flow<GenerationState> = simulatedGenerationProgress(requestId, startPercent)
        return progress
            .onCompletion { cause -> if (cause == null) emit(GenerationState.Completed(requestId, storyId)) }
    }
}

class FakeNotifications(items: List<NotificationItem>) : NotificationsRepository {
    val all = MutableStateFlow(items)

    override fun notifications(languageTag: String): Flow<List<NotificationItem>> = all

    override suspend fun markRead(id: String) {
        all.value = all.value.map { if (it.id == id) it.copy(read = true) else it }
    }

    override suspend fun markAllRead() {
        all.value = all.value.map { it.copy(read = true) }
    }
}

class FakeLocalData : LocalDataRepository {
    var deletions = 0

    override suspend fun deleteAll() {
        deletions++
    }
}

class FakeSubscription(plans: List<Plan>) : SubscriptionRepository {
    override val plans: Flow<List<Plan>> = MutableStateFlow(plans)
}

fun stores(
    catalog: FakeCatalog = FakeCatalog(emptyList()),
    library: FakeLibrary = FakeLibrary(),
    favourites: FakeFavourites = FakeFavourites(),
    downloads: FakeDownloads = FakeDownloads(),
    progress: FakeProgress = FakeProgress(),
) = StoryStores(catalog, library, favourites, downloads, progress)

/** Runs viewModelScope work on a test dispatcher. */
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(val dispatcher: TestDispatcher = StandardTestDispatcher()) : TestWatcher() {
    override fun starting(description: Description) = Dispatchers.setMain(dispatcher)

    override fun finished(description: Description) = Dispatchers.resetMain()
}
