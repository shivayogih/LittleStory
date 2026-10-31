package com.littlestory.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.littlestory.data.db.LittleStoryDatabase
import com.littlestory.data.prefs.ActiveProfileStore
import com.littlestory.data.prefs.PreferencesRepositoryImpl
import com.littlestory.data.repository.FavouritesRepositoryImpl
import com.littlestory.data.repository.LocalDataRepositoryImpl
import com.littlestory.data.repository.ProfilesRepositoryImpl
import com.littlestory.data.repository.ProgressRepositoryImpl
import com.littlestory.domain.model.AvatarId
import com.littlestory.domain.model.ChildProfileDraft
import com.littlestory.domain.model.ParentSettings
import com.littlestory.domain.model.ReadingPreferences
import com.littlestory.domain.model.ReadingProgress
import com.littlestory.domain.model.ReminderSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.time.DayOfWeek
import java.time.LocalTime
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class PersistenceTest {
    private lateinit var db: LittleStoryDatabase
    private lateinit var scope: CoroutineScope
    private lateinit var dataStoreFile: File
    private lateinit var store: DataStore<Preferences>
    private var nextId = 0

    private fun draft(name: String = "Aarav") =
        ChildProfileDraft(
            firstName = name,
            age = 6,
            avatar = AvatarId(1),
            interestIds = setOf("space", "animals"),
            languageTag = "kn",
            defaultDurationMinutes = 10,
        )

    private fun profiles() =
        ProfilesRepositoryImpl(
            dao = db.childProfileDao(),
            savedDao = db.savedStoryDao(),
            progressDao = db.readingProgressDao(),
            generatedDao = db.generatedStoryDao(),
            active = ActiveProfileStore(store),
            newId = { "child_${++nextId}" },
        )

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, LittleStoryDatabase::class.java).allowMainThreadQueries().build()
        scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        dataStoreFile = File.createTempFile("prefs", ".preferences_pb").apply { delete() }
        store =
            PreferenceDataStoreFactory.create(scope = scope) {
                File(
                    dataStoreFile.path.removeSuffix(".preferences_pb") + "_profiles.preferences_pb",
                )
            }
    }

    @After
    fun tearDown() {
        db.close()
        scope.cancel()
        dataStoreFile.delete()
        File(dataStoreFile.path.removeSuffix(".preferences_pb") + "_profiles.preferences_pb").delete()
    }

    @Test
    fun firstProfileBecomesActiveAndRoundTrips() =
        runBlocking {
            val repo = profiles()
            assertNull(repo.activeProfile.first())
            val created = repo.create(draft(" Aarav "))
            assertEquals("Aarav", created.firstName)
            assertEquals(created, repo.activeProfile.first())
            assertEquals(setOf("space", "animals"), repo.activeProfile.first()?.interestIds)
            assertEquals("kn", repo.activeProfile.first()?.languageTag)
        }

    @Test
    fun switchingTheActiveChildUpdatesTheFlow() =
        runBlocking {
            val repo = profiles()
            val a = repo.create(draft("Aarav"))
            val b = repo.create(draft("Ananya"))
            assertEquals(a.id, repo.activeProfile.first()?.id)
            repo.setActive(b.id)
            assertEquals(b.id, repo.activeProfile.first()?.id)
            assertEquals(listOf("Aarav", "Ananya"), repo.profiles.first().map { it.firstName })
        }

    @Test
    fun deletingAChildRemovesTheirDataAndFallsBackToTheNextProfile() =
        runBlocking {
            val repo = profiles()
            val a = repo.create(draft("Aarav"))
            val b = repo.create(draft("Ananya"))
            FavouritesRepositoryImpl(db.savedStoryDao()).setFavourite(a.id, "story_1", true)
            ProgressRepositoryImpl(db.readingProgressDao()).update(ReadingProgress(a.id, "story_1", 2, 0.4f, 1L))
            repo.delete(a.id)
            assertEquals(listOf(b.id), repo.profiles.first().map { it.id })
            assertEquals(b.id, repo.activeProfile.first()?.id)
            assertTrue(FavouritesRepositoryImpl(db.savedStoryDao()).favouriteIds(a.id).first().isEmpty())
            assertTrue(ProgressRepositoryImpl(db.readingProgressDao()).progress(a.id).first().isEmpty())
        }

    @Test
    fun favouritesArePerChild() =
        runBlocking {
            val repo = FavouritesRepositoryImpl(db.savedStoryDao())
            repo.setFavourite("c1", "s1", true)
            repo.setFavourite("c2", "s2", true)
            repo.setFavourite("c1", "s2", true)
            repo.setFavourite("c1", "s2", false)
            assertEquals(setOf("s1"), repo.favouriteIds("c1").first())
            assertEquals(setOf("s2"), repo.favouriteIds("c2").first())
        }

    @Test
    fun preferencesDefaultThenPersistAcrossARestart() =
        runBlocking {
            val firstScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
            val prefs =
                PreferencesRepositoryImpl(PreferenceDataStoreFactory.create(scope = firstScope) { dataStoreFile })
            assertEquals(false, prefs.firstRunCompleted.first())
            assertEquals("en", prefs.contentLanguageTag.first())
            prefs.setFirstRunCompleted(true)
            prefs.setContentLanguageTag("kn")
            prefs.setReading(ReadingPreferences(textScale = 1.2f, playbackSpeed = 1.25f))
            val settings =
                ParentSettings(ReminderSettings(true, LocalTime.of(19, 15), setOf(DayOfWeek.MONDAY)), false, false)
            prefs.setParentSettings(settings)

            // closing the first DataStore and opening the same file stands in for a new process
            firstScope.coroutineContext[Job]!!.cancelAndJoin()
            val reopened =
                PreferencesRepositoryImpl(
                    PreferenceDataStoreFactory.create(scope = CoroutineScope(Dispatchers.IO)) { dataStoreFile },
                )
            assertEquals(true, reopened.firstRunCompleted.first())
            assertEquals("kn", reopened.contentLanguageTag.first())
            assertEquals(ReadingPreferences(1.2f, 1.25f), reopened.reading.first())
            assertEquals(settings, reopened.parentSettings.first())
        }

    @Test
    fun rapidTogglesFromManyCallersSettleConsistently() =
        runBlocking {
            val favourites = FavouritesRepositoryImpl(db.savedStoryDao())
            // An even number of concurrent toggles must end where it started (LS-075).
            List(10) { async(Dispatchers.IO) { favourites.toggle("child_1", "s1") } }.awaitAll()
            assertTrue("s1" !in favourites.favouriteIds("child_1").first())
            assertTrue(favourites.toggle("child_1", "s1"))
            assertEquals(setOf("s1"), favourites.favouriteIds("child_1").first())
        }

    @Test
    fun deleteAllClearsTablesAndPreferences() =
        runBlocking {
            val profiles = profiles()
            val child = profiles.create(draft())
            FavouritesRepositoryImpl(db.savedStoryDao()).setFavourite(child.id, "s1", true)
            ProgressRepositoryImpl(db.readingProgressDao()).update(ReadingProgress(child.id, "s1", 1, 0.5f, 1))
            val preferences = PreferencesRepositoryImpl(store)
            preferences.setFirstRunCompleted(true)
            val images =
                kotlin.io.path
                    .createTempDirectory("story_images")
                    .toFile()
            File(images, "generated_r1").mkdirs()
            File(images, "generated_r1/cover.png").writeText("png")
            LocalDataRepositoryImpl(db, store, Dispatchers.IO, images).deleteAll()
            assertFalse(images.exists())
            assertTrue(profiles.profiles.first().isEmpty())
            assertNull(profiles.activeProfile.first())
            assertTrue(FavouritesRepositoryImpl(db.savedStoryDao()).favouriteIds(child.id).first().isEmpty())
            assertTrue(ProgressRepositoryImpl(db.readingProgressDao()).progress(child.id).first().isEmpty())
            assertEquals(false, preferences.firstRunCompleted.first())
        }
}
