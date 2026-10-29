package com.littlestory.feature.create.navigation

import kotlinx.serialization.Serializable

/** SCR-07: the Create-a-story form. */
@Serializable
data object CreateRoute

/** SCR-08: simulated generation, including the paused state. */
@Serializable
data class GeneratingRoute(val requestId: String)

/** SCR-09: a generated story is ready. */
@Serializable
data class StoryReadyRoute(val storyId: String)
