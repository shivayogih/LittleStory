package com.littlestory.data

import com.littlestory.data.catalog.CatalogRepositoryImpl
import com.littlestory.data.catalog.MockContentSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Reads the real bundled assets, so the shipped JSON is what is tested. */
class CatalogRepositoryTest {
    private val assetDir = File("src/main/assets/mock")

    private fun repository(overrides: Map<String, String?> = emptyMap()) =
        CatalogRepositoryImpl(
            source =
                MockContentSource { name ->
                    if (name in overrides) overrides[name] else File(assetDir, name).takeIf { it.exists() }?.readText()
                },
            dispatcher = Dispatchers.Unconfined,
        )

    @Test
    fun englishCatalogueHasTheSixDesignStoriesWithPages() =
        runBlocking {
            val stories = repository().stories("en").first()
            assertEquals(6, stories.size)
            assertTrue(stories.all { it.pages.isNotEmpty() && it.resolvedLanguage == "en" })
            assertEquals("The Little Elephant Who Shared", stories.first { it.id == "story_little_elephant" }.title)
        }

    @Test
    fun kannadaKeepsTheSameIdsAndTranslatesText() =
        runBlocking {
            val en = repository().stories("en").first()
            val kn = repository().stories("kn").first()
            assertEquals(en.map { it.id }, kn.map { it.id })
            val elephant = kn.first { it.id == "story_little_elephant" }
            assertEquals("kn", elephant.resolvedLanguage)
            assertEquals(en.first { it.id == "story_little_elephant" }.durationMinutes, elephant.durationMinutes)
        }

    @Test
    fun storyMissingInKannadaFallsBackToEnglish() =
        runBlocking {
            val lanterns = repository().story("story_lanterns_jaipur", "kn").first()
            assertNotNull(lanterns)
            assertEquals("en", lanterns.resolvedLanguage)
            assertEquals("The Lanterns of Jaipur", lanterns.title)
        }

    @Test
    fun regionalTagResolvesToTheLanguage() =
        runBlocking {
            assertEquals("kn", repository().story("story_little_elephant", "kn-IN").first()?.resolvedLanguage)
        }

    @Test
    fun malformedTranslationFileFallsBackToEnglish() =
        runBlocking {
            val stories = repository(mapOf("stories_kn.json" to "{ not json")).stories("kn").first()
            assertEquals(6, stories.size)
            assertTrue(stories.all { it.resolvedLanguage == "en" })
        }

    @Test
    fun missingFieldFallsBackPerField() =
        runBlocking {
            val partial = """{"stories":[{"id":"story_little_elephant","title":"ಆನೆ"}]}"""
            val story = repository(mapOf("stories_kn.json" to partial)).story("story_little_elephant", "kn").first()
            assertNotNull(story)
            assertEquals("ಆನೆ", story.title)
            assertEquals("kn", story.resolvedLanguage)
            assertTrue(story.synopsis.startsWith("A gentle adventure"))
            assertEquals(5, story.pages.size)
        }

    @Test
    fun unknownStoryIsNull() =
        runBlocking {
            assertNull(repository().story("nope", "en").first())
        }

    @Test
    fun categoryCountsAreComputedFromStories() =
        runBlocking {
            val categories = repository().categories("en").first().associateBy { it.id }
            assertEquals(1, categories.getValue("bedtime").storyCount)
            // Primary category plus tags: elephant (primary), banyan and moonlit garden (tag).
            assertEquals(3, categories.getValue("nature").storyCount)
            assertEquals(
                "ಬಾಹ್ಯಾಕಾಶ",
                repository()
                    .categories("kn")
                    .first()
                    .first { it.id == "space" }
                    .name,
            )
        }

    @Test
    fun noRawKeysOrBlankTextInAnyLanguage() =
        runBlocking {
            for (tag in listOf("en", "kn")) {
                repository().stories(tag).first().forEach {
                    assertTrue(it.title.isNotBlank() && it.synopsis.isNotBlank() && it.lesson.isNotBlank(), it.id)
                }
            }
        }
}
