package com.littlestory.domain.repository

import com.littlestory.domain.model.ChildProfile
import com.littlestory.domain.model.ChildProfileDraft
import com.littlestory.domain.model.Download
import com.littlestory.domain.model.GenerationState
import com.littlestory.domain.model.NotificationItem
import com.littlestory.domain.model.ParentSettings
import com.littlestory.domain.model.Plan
import com.littlestory.domain.model.ReadingPreferences
import com.littlestory.domain.model.ReadingProgress
import com.littlestory.domain.model.Story
import com.littlestory.domain.model.StoryCategory
import com.littlestory.domain.model.StoryCreationRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

/** Read-only catalogue of bundled stories, resolved for a language with English fallback. */
interface CatalogRepository {
    fun categories(languageTag: String): Flow<List<StoryCategory>>

    fun stories(languageTag: String): Flow<List<Story>>

    fun story(id: String, languageTag: String): Flow<Story?>
}

interface ProfilesRepository {
    val profiles: Flow<List<ChildProfile>>

    /** The child whose name and defaults the app shows; null before Setup is completed. */
    val activeProfile: Flow<ChildProfile?>

    suspend fun create(draft: ChildProfileDraft): ChildProfile

    suspend fun update(profile: ChildProfile)

    suspend fun delete(profileId: String)

    suspend fun setActive(profileId: String)
}

interface FavouritesRepository {
    fun favouriteIds(childId: String): Flow<Set<String>>

    suspend fun setFavourite(childId: String, storyId: String, favourite: Boolean)

    /**
     * Flips [storyId] for [childId] and returns the new state. Implementations serialise toggles so
     * rapid taps from any screen settle on a consistent value (LS-075).
     */
    suspend fun toggle(childId: String, storyId: String): Boolean {
        val favourite = storyId !in favouriteIds(childId).first()
        setFavourite(childId, storyId, favourite)
        return favourite
    }
}

/** Stories the child created, newest first. */
interface LibraryRepository {
    fun created(childId: String): Flow<List<Story>>

    /** A created story by id, whichever child made it; null when it does not exist. */
    fun story(id: String): Flow<Story?>

    suspend fun save(childId: String, story: Story)
}

/**
 * Simulated story generation (no AI provider in the MVP). Create [submit]s the request, the
 * Generating screen runs [generate] for its id; nothing leaves the device.
 */
interface GenerationRepository {
    fun submit(request: StoryCreationRequest)

    fun request(requestId: String): StoryCreationRequest?

    /** Progress from [startPercent] to 100, then the saved story; Failed when the request is unknown. */
    fun generate(requestId: String, startPercent: Int = 0): Flow<GenerationState>
}

interface ProgressRepository {
    fun progress(childId: String): Flow<List<ReadingProgress>>

    suspend fun update(progress: ReadingProgress)
}

interface DownloadsRepository {
    val downloads: Flow<List<Download>>

    suspend fun start(storyId: String)

    suspend fun remove(storyId: String)
}

/** App-level choices that survive restarts. */
interface PreferencesRepository {
    /** True once the first-run flow (onboarding, Setup) is finished or skipped. */
    val firstRunCompleted: Flow<Boolean>

    suspend fun setFirstRunCompleted(completed: Boolean)

    /** BCP 47 tag chosen for story content; the UI language is owned by AppCompat. */
    val contentLanguageTag: Flow<String>

    suspend fun setContentLanguageTag(tag: String)

    val reading: Flow<ReadingPreferences>

    suspend fun setReading(preferences: ReadingPreferences)

    val parentSettings: Flow<ParentSettings>

    suspend fun setParentSettings(settings: ParentSettings)
}

interface NotificationsRepository {
    fun notifications(languageTag: String): Flow<List<NotificationItem>>

    suspend fun markRead(id: String)

    suspend fun markAllRead()
}

interface SubscriptionRepository {
    /** Illustrative plans only; behind the demoSubscription feature flag. */
    val plans: Flow<List<Plan>>
}

/** Everything the app keeps on this device; Privacy & parent controls can wipe it (LS-114). */
interface LocalDataRepository {
    /** Deletes profiles, favourites, progress, downloads, created stories and preferences. */
    suspend fun deleteAll()
}
