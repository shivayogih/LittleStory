package com.littlestory.feature.library.navigation

import kotlinx.serialization.Serializable

/** Tabs of My Stories, in display order. */
@Serializable
enum class LibraryTab { All, CreatedByMe, Downloaded, Favourites }

/** SCR-12: My Stories. */
@Serializable
data class MyStoriesRoute(val tab: LibraryTab = LibraryTab.All)

/** SCR-17: offline downloads (simulated). */
@Serializable
data object DownloadsRoute
