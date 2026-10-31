package com.littlestory.prototype.gemini

import com.littlestory.domain.generation.StoryDraft
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

internal const val HERO_TOKEN = "{{HERO}}"
private const val STATUS_OK = "ok"
private const val CODE_FENCE = "```"

@Serializable
internal data class GeminiPage(val text: String = "", val imagePrompt: String = "")

/** The JSON the system prompt asks for (assets/prototype/story_system_prompt.txt). */
@Serializable
internal data class GeminiStory(
    val status: String = "",
    val title: String = "",
    val synopsis: String = "",
    val lesson: String = "",
    val palette: String = "",
    val characters: String = "",
    val coverImagePrompt: String = "",
    val pages: List<GeminiPage> = emptyList(),
) {
    val isUsable: Boolean get() = status == STATUS_OK && title.isNotBlank() && pages.any { it.text.isNotBlank() }
}

private val json = Json { ignoreUnknownKeys = true }

/** Parses the model's reply, tolerating a Markdown code fence around the JSON; null when it is not JSON. */
internal fun parseGeminiStory(reply: String): GeminiStory? {
    val body =
        reply
            .trim()
            .removePrefix(CODE_FENCE + "json")
            .removePrefix(CODE_FENCE)
            .removeSuffix(CODE_FENCE)
            .trim()
    return runCatching { json.decodeFromString<GeminiStory>(body) }.getOrNull()
}

/** Replaces the hero token with the child's name on the device; the provider only ever saw the token. */
internal fun GeminiStory.toDraft(heroName: String, languageTag: String, palettes: Set<String>): StoryDraft {
    fun String.fill() = replace(HERO_TOKEN, heroName)
    return StoryDraft(
        title = title.fill(),
        synopsis = synopsis.fill(),
        lesson = lesson.fill(),
        pages = pages.map { it.text.fill() }.filter { it.isNotBlank() },
        languageTag = languageTag,
        paletteId = palette.takeIf { it in palettes },
    )
}

/** The parent's idea with the child's own name swapped for the token before it leaves the device. */
internal fun anonymise(idea: String, heroName: String): String =
    if (heroName.isBlank()) idea else idea.replace(heroName.trim(), HERO_TOKEN, ignoreCase = true)
