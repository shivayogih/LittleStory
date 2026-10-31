package com.littlestory.domain.player

import kotlinx.coroutines.flow.Flow

/**
 * Reads a story aloud. The MVP binds none and the player stays a labelled simulation (ADR-014);
 * the debug prototype binds the phone's own text-to-speech.
 */
interface Narrator {
    /**
     * Speaks [pages] from [fromPage] in [languageTag]; emits each page index as it starts and
     * completes after the last page. Cancelling the collection stops the voice.
     */
    fun narrate(pages: List<String>, fromPage: Int, languageTag: String, speed: Float): Flow<Int>
}
