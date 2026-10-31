package com.littlestory.domain.player

import kotlinx.coroutines.flow.Flow

/** Where the voice is: the page being read and how far into that page's text, from 0 to 1. */
data class NarrationProgress(
    val page: Int,
    val pageFraction: Float = 0f,
)

/**
 * Reads a story aloud. The MVP binds none and the player stays a labelled simulation (ADR-014);
 * the debug prototype binds the phone's own text-to-speech.
 */
interface Narrator {
    /**
     * Speaks [pages] from [fromPage] in [languageTag]; emits progress as each page starts and as
     * the voice moves through its text, and completes after the last page. Cancelling the
     * collection stops the voice.
     */
    fun narrate(
        pages: List<String>,
        fromPage: Int,
        languageTag: String,
        speed: Float,
    ): Flow<NarrationProgress>
}
