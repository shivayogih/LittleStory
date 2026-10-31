package com.littlestory.domain.generation

import com.littlestory.domain.model.StoryCreationRequest

/**
 * A real story writer, such as an AI model. The MVP binds none and generation stays a labelled
 * simulation (ADR-014); the debug-only Gemini prototype binds one. Implementations must keep the
 * child's name away from any provider (docs/api/backend-ai-accounts-design.md §4).
 */
interface StoryWriter {
    suspend fun write(request: StoryCreationRequest, storyId: String): StoryWriterResult
}

/** Text of a written story in [languageTag]; pages are plain paragraphs with the hero's name filled in. */
data class StoryDraft(
    val title: String,
    val synopsis: String,
    val lesson: String,
    val pages: List<String>,
    val languageTag: String,
    val paletteId: String? = null,
)

sealed interface StoryWriterResult {
    data class Written(val draft: StoryDraft) : StoryWriterResult

    /** The idea or the story failed the safety rules; nothing is kept. */
    data object Rejected : StoryWriterResult

    /** The writer is not configured on this build or device; the simulation runs instead. */
    data object Unavailable : StoryWriterResult

    data object Failed : StoryWriterResult
}
