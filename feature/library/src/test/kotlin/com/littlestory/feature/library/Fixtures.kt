package com.littlestory.feature.library

import com.littlestory.core.ui.story.StoryItem
import com.littlestory.feature.library.downloads.DownloadItem
import com.littlestory.feature.library.downloads.DownloadsUiState
import com.littlestory.feature.library.navigation.LibraryTab
import com.littlestory.feature.library.stories.MyStoriesUiState
import com.littlestory.feature.library.stories.RecentlyPlayed

private fun item(
    id: String,
    title: String,
    category: String,
    palette: String,
    motif: String,
    downloaded: Boolean = false,
) =
    StoryItem(id, title, category, 8, 4, 8, palette, motif, favourite = false, downloaded = downloaded)

private val created = item("generated_1", "Aarav and the Magical Cricket Bat", "Adventure", "adventure", "cricket")
private val owl = item("owl", "The Little Owl Who Loved the Moon", "Bedtime", "bedtime", "moon", downloaded = true)
private val fox = item("fox", "The Brave Little Fox", "Animals", "animals", "fox")

val myStoriesAll =
    MyStoriesUiState(
        loading = false,
        tab = LibraryTab.All,
        recentlyPlayed = RecentlyPlayed(owl, 0.42f, 278),
        stories = listOf(created, owl, fox),
        createdIds = setOf("generated_1"),
    )

val myStoriesEmptyFavourites = MyStoriesUiState(loading = false, tab = LibraryTab.Favourites)

val downloadsTwo =
    DownloadsUiState(
        loading = false,
        items =
            listOf(
                DownloadItem("owl", owl.title, 12_000_000, "bedtime", "moon"),
                DownloadItem("generated_1", created.title, 12_000_000, "adventure", "cricket"),
            ),
    )

val downloadsEmpty = DownloadsUiState(loading = false)
