package com.littlestory.domain.model

/** Where a story came from: bundled mock content or the simulated generator. */
enum class StoryOrigin { Catalog, Generated }

/** Cover art selection by stable ids; the UI maps them to tokens (ADR-010). Unknown ids use a placeholder. */
data class StoryArtSpec(val paletteId: String, val motifId: String)

data class StoryPage(val index: Int, val text: String)

/**
 * A story already resolved for one language. A missing translation falls back to English per field;
 * [resolvedLanguage] records the language the text is actually in.
 */
data class Story(
    val id: String,
    val title: String,
    val synopsis: String,
    val categoryId: String,
    /** Extra theme ids (for example `animals`) so a story can appear under several filters. */
    val tags: List<String> = emptyList(),
    val ageMin: Int,
    val ageMax: Int,
    val durationMinutes: Int,
    val lesson: String,
    /** Days since 1970-01-01 when the story joined the catalogue; drives the "Newest" sort. */
    val addedEpochDay: Long,
    val art: StoryArtSpec,
    val pages: List<StoryPage>,
    val resolvedLanguage: String,
    val origin: StoryOrigin = StoryOrigin.Catalog,
) {
    /** True when [categoryOrTagId] is this story's category or one of its tags. */
    fun belongsTo(categoryOrTagId: String): Boolean = categoryId == categoryOrTagId || categoryOrTagId in tags
}

/** A browsable collection. [name] and [description] are content resolved for the language; [storyCount] is computed from the catalogue. */
data class StoryCategory(val id: String, val name: String, val description: String, val storyCount: Int)
