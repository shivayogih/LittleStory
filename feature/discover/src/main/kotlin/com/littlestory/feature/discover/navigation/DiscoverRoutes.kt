package com.littlestory.feature.discover.navigation

import kotlinx.serialization.Serializable

/** SCR-04a/04b: Discover with search and filters. */
@Serializable
data object DiscoverRoute

/** SCR-05: stories in one category. */
@Serializable
data class CategoryRoute(val categoryId: String)

/** SCR-06: story detail. */
@Serializable
data class StoryDetailRoute(val storyId: String)
