package com.littlestory.data.catalog

import kotlinx.serialization.Serializable

/** Where the bundled mock JSON comes from. Android reads assets; tests read plain files. */
fun interface MockContentSource {
    /** The text of `mock/<fileName>`, or null when the file does not exist. */
    fun read(fileName: String): String?
}

/** Story fields shared by every language; only the English file carries them. */
@Serializable
internal data class StoryFileEntry(
    val id: String,
    val title: String = "",
    val synopsis: String = "",
    val lesson: String = "",
    val pages: List<String> = emptyList(),
    val categoryId: String = "",
    val tags: List<String> = emptyList(),
    val ageMin: Int = 0,
    val ageMax: Int = 0,
    val durationMinutes: Int = 0,
    val addedEpochDay: Long = 0,
    val art: ArtEntry = ArtEntry(),
)

@Serializable
internal data class ArtEntry(val paletteId: String = "", val motifId: String = "")

@Serializable
internal data class StoriesFile(val language: String = "", val stories: List<StoryFileEntry> = emptyList())

@Serializable
internal data class CategoryFileEntry(val id: String, val name: String = "", val description: String = "")

@Serializable
internal data class CategoriesFile(val language: String = "", val categories: List<CategoryFileEntry> = emptyList())
