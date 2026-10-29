package com.littlestory.feature.reader.navigation

import kotlinx.serialization.Serializable

/** SCR-10: the reader, opened at [page]. */
@Serializable
data class ReaderRoute(val storyId: String, val page: Int = 0)

/** SCR-11a: the narration player (the sleep-timer sheet SCR-11b is state inside it). */
@Serializable
data class PlayerRoute(val storyId: String)
