package com.littlestory.prototype.gemini

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GeminiStoryJsonTest {
    private val reply =
        """
        ```json
        {"status":"ok","title":"{{HERO}} and the Moon","synopsis":"s","lesson":"l","palette":"bedtime",
         "characters":"{{HERO}}: a girl","coverImagePrompt":"c","extra":1,
         "pages":[{"text":"{{HERO}} looked up.","imagePrompt":"p1"},{"text":"","imagePrompt":"p2"}]}
        ```
        """.trimIndent()

    @Test
    fun parsesFencedJsonAndFillsTheHeroOnTheDevice() {
        val story = assertNotNull(parseGeminiStory(reply))
        assertTrue(story.isUsable)
        val draft = story.toDraft("Mira", "kn", setOf("bedtime"))
        assertEquals("Mira and the Moon", draft.title)
        assertEquals(listOf("Mira looked up."), draft.pages)
        assertEquals("bedtime", draft.paletteId)
        assertEquals("kn", draft.languageTag)
    }

    @Test
    fun unknownPaletteIsDroppedAndRejectionIsNotUsable() {
        val story = assertNotNull(parseGeminiStory(reply.replace("bedtime", "neon")))
        assertNull(story.toDraft("Mira", "en", setOf("bedtime")).paletteId)
        assertFalse(assertNotNull(parseGeminiStory("""{"status":"rejected"}""")).isUsable)
        assertNull(parseGeminiStory("not json"))
    }

    @Test
    fun theChildsNameIsReplacedBeforeTheIdeaIsSent() {
        assertEquals(
            "$HERO_TOKEN played cricket with $HERO_TOKEN's friend",
            anonymise("Aarav played cricket with aarav's friend", "Aarav "),
        )
        assertEquals("A walk", anonymise("A walk", " "))
    }
}
