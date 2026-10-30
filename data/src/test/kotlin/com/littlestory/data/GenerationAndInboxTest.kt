package com.littlestory.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.littlestory.data.catalog.MockContentSource
import com.littlestory.data.db.LittleStoryDatabase
import com.littlestory.data.generation.GenerationRepositoryImpl
import com.littlestory.data.library.LibraryRepositoryImpl
import com.littlestory.data.settings.NotificationsRepositoryImpl
import com.littlestory.data.settings.SubscriptionRepositoryImpl
import com.littlestory.domain.model.GenerationError
import com.littlestory.domain.model.GenerationState
import com.littlestory.domain.model.NotificationKind
import com.littlestory.domain.model.StoryCreationRequest
import com.littlestory.domain.model.StoryOrigin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class GenerationAndInboxTest {
    private val assetDir = File("src/main/assets/mock")
    private val source = MockContentSource { name -> File(assetDir, name).takeIf { it.exists() }?.readText() }
    private lateinit var db: LittleStoryDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, LittleStoryDatabase::class.java).allowMainThreadQueries().build()
    }

    @After
    fun tearDown() = db.close()

    private fun request(language: String, name: String = "Aarav") =
        StoryCreationRequest(
            requestId = "r_$language",
            prompt = "We played cricket",
            heroChildId = "child_1",
            heroName = name,
            heroAge = 6,
            themeId = "indian_folklore",
            moodId = "magical",
            durationMinutes = 8,
            languageTag = language,
        )

    @Test
    fun generationEndsWithASavedLocalizedStoryForTheHero() =
        runTest {
            val library = LibraryRepositoryImpl(db.generatedStoryDao()) { 1_000L }
            val repository = GenerationRepositoryImpl(source, library)
            repository.submit(request("kn"))
            val states = repository.generate("r_kn").toList()
            val done = assertIs<GenerationState.Completed>(states.last())
            assertEquals(100, (states[states.size - 2] as GenerationState.InProgress).percent)

            val story = assertNotNull(library.story(done.storyId).first())
            assertEquals(StoryOrigin.Generated, story.origin)
            assertEquals("kn", story.resolvedLanguage)
            assertTrue(story.title.startsWith("Aarav"))
            assertTrue(story.pages.none { "{child}" in it.text })
            assertEquals("indian_tales", story.categoryId)
            assertEquals(5 to 9, story.ageMin to story.ageMax)
            assertEquals(listOf(done.storyId), library.created("child_1").first().map { it.id })
        }

    @Test
    fun aHeroWithoutANameGetsTheTemplateDefault() =
        runTest {
            val library = LibraryRepositoryImpl(db.generatedStoryDao()) { 1_000L }
            val repository = GenerationRepositoryImpl(source, library)
            repository.submit(request("en", name = " "))
            val done = repository.generate("r_en").toList().last() as GenerationState.Completed
            assertTrue(
                library
                    .story(done.storyId)
                    .first()!!
                    .title
                    .startsWith("Little Star"),
            )
        }

    @Test
    fun anUnknownRequestFails() =
        runTest {
            val repository = GenerationRepositoryImpl(source, LibraryRepositoryImpl(db.generatedStoryDao()) { 0L })
            val state = repository.generate("missing").first()
            assertEquals(GenerationState.Failed("missing", GenerationError.Unknown), state)
        }

    @Test
    fun inboxIsTranslatedAndTracksReadState() =
        runBlocking {
            val repository = NotificationsRepositoryImpl(source, db.notificationReadDao(), Dispatchers.Unconfined)
            val en = repository.notifications("en").first()
            val kn = repository.notifications("kn").first()
            assertEquals(3, en.size)
            assertEquals(en.map { it.id }, kn.map { it.id })
            assertEquals(NotificationKind.StoryReady, kn.first().kind)
            assertTrue(kn.first().title != en.first().title)
            assertTrue(en.none { it.read })

            repository.markRead(en.first().id)
            assertEquals(listOf(true, false, false), repository.notifications("en").first().map { it.read })
            repository.markAllRead()
            assertTrue(repository.notifications("kn").first().all { it.read })
        }

    @Test
    fun plansAreIllustrativeAndYearlyIsBestValue() =
        runBlocking {
            val plans = SubscriptionRepositoryImpl(source, Dispatchers.Unconfined).plans.first()
            assertEquals(listOf("free", "monthly", "yearly", "family"), plans.map { it.id })
            assertEquals("yearly", plans.single { it.bestValue }.id)
        }
}
