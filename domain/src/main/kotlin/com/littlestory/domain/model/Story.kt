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
    val ageMin: Int,
    val ageMax: Int,
    val durationMinutes: Int,
    val lesson: String,
    val art: StoryArtSpec,
    val pages: List<StoryPage>,
    val resolvedLanguage: String,
    val origin: StoryOrigin = StoryOrigin.Catalog,
)

/** [storyCount] is computed from the catalogue, never hardcoded. */
data class StoryCategory(val id: String, val storyCount: Int)
