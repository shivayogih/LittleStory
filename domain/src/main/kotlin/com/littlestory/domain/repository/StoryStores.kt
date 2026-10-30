package com.littlestory.domain.repository

/**
 * The per-story repositories that the library, reader and player read together. Bundled so their
 * view models take one dependency instead of five; wired once in the data layer's DI module.
 */
data class StoryStores(
    val catalog: CatalogRepository,
    val library: LibraryRepository,
    val favourites: FavouritesRepository,
    val downloads: DownloadsRepository,
    val progress: ProgressRepository,
)
