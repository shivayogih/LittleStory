package com.littlestory.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.littlestory.core.common.config.AppConfig
import com.littlestory.data.catalog.MockContentSource
import com.littlestory.data.db.LittleStoryDatabase
import com.littlestory.data.generation.GenerationRepositoryImpl
import com.littlestory.data.library.LibraryRepositoryImpl
import com.littlestory.domain.generation.StoryDraft
import com.littlestory.domain.generation.StoryWriter
import com.littlestory.domain.generation.StoryWriterResult
import com.littlestory.domain.model.GenerationState
import com.littlestory.domain.model.StoryCreationRequest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
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
class StoryWriterGenerationTest {
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

    private val request =
        StoryCreationRequest(
            requestId = "r_1",
            prompt = "A walk in the rain",
            heroChildId = "child_1",
            heroName = "Mira",
            heroAge = 5,
            themeId = "animals",
            moodId = "bedtime",
            durationMinutes = 5,
            languageTag = "en",
        )

    private fun writerReturning(result: StoryWriterResult) =
        object : StoryWriter {
            var calledWith: String? = null

            override suspend fun write(request: StoryCreationRequest, storyId: String): StoryWriterResult {
                calledWith = storyId
                return result
            }
        }

    @Test
    fun aWrittenStoryIsSavedAndProgressWaitsBelowDone() =
        runTest {
            val library = LibraryRepositoryImpl(db.generatedStoryDao()) { 1_000L }
            val draft =
                StoryDraft(
                    "Mira and the Rain",
                    "A rainy walk.",
                    "Puddles are fun.",
                    listOf("One.", "Two."),
                    "en",
                    "bedtime",
                )
            val writer = writerReturning(StoryWriterResult.Written(draft))
            val repository = GenerationRepositoryImpl(source, library, writer)
            repository.submit(request)

            val states = repository.generate("r_1").toList()
            val done = assertIs<GenerationState.Completed>(states.last())
            assertEquals(GenerationRepositoryImpl.generatedStoryId("r_1"), writer.calledWith)
            assertTrue(
                states.filterIsInstance<GenerationState.InProgress>().all {
                    it.percent <=
                        AppConfig.Generation.WRITER_WAIT_PERCENT
                },
            )

            val story = assertNotNull(library.story(done.storyId).first())
            assertEquals("Mira and the Rain", story.title)
            assertEquals(listOf("One.", "Two."), story.pages.map { it.text })
            assertEquals("bedtime", story.art.paletteId)
        }

    @Test
    fun anUnavailableWriterFallsBackToTheSimulation() =
        runTest {
            val library = LibraryRepositoryImpl(db.generatedStoryDao()) { 1_000L }
            val repository = GenerationRepositoryImpl(source, library, writerReturning(StoryWriterResult.Unavailable))
            repository.submit(request)

            val done = assertIs<GenerationState.Completed>(repository.generate("r_1").toList().last())
            assertTrue(assertNotNull(library.story(done.storyId).first()).title.contains("Mira"))
        }

    @Test
    fun aRejectedOrFailedStoryEndsInFailedAndSavesNothing() =
        runTest {
            listOf(StoryWriterResult.Rejected, StoryWriterResult.Failed).forEach { result ->
                val library = LibraryRepositoryImpl(db.generatedStoryDao()) { 1_000L }
                val repository = GenerationRepositoryImpl(source, library, writerReturning(result))
                repository.submit(request)

                assertIs<GenerationState.Failed>(repository.generate("r_1").toList().last())
                assertEquals(emptyList(), library.created("child_1").first())
            }
        }
}
