package com.littlestory.domain.player

import com.littlestory.core.common.config.AppConfig
import com.littlestory.domain.model.SleepTimer

private const val SECONDS_PER_MINUTE = 60
private const val PERCENT = 100f

/**
 * Pure rules of the simulated narration player (LS-092/LS-093). Nothing plays: position is a
 * fraction of the story's length that advances on a timer while "playing".
 */
object SimulatedPlayback {
    fun totalSeconds(durationMinutes: Int): Int = durationMinutes.coerceAtLeast(1) * SECONDS_PER_MINUTE

    /** One timer tick: [AppConfig.Player.TICK_PERCENT] percent of the track, scaled by [speed]. */
    fun tick(position: Float, speed: Float): Float =
        (position + AppConfig.Player.TICK_PERCENT / PERCENT * speed).coerceIn(0f, 1f)

    /** Skips by [AppConfig.Player.SKIP_SECONDS] (negative [direction] rewinds). */
    fun skip(position: Float, durationMinutes: Int, direction: Int): Float {
        val step = AppConfig.Player.SKIP_SECONDS.toFloat() / totalSeconds(durationMinutes)
        return (position + step * direction.coerceIn(-1, 1)).coerceIn(0f, 1f)
    }

    /** Chapters are the story's pages, evenly spread over the track. */
    fun chapterOf(position: Float, chapters: Int): Int =
        (position * chapters.coerceAtLeast(1)).toInt().coerceIn(0, chapters.coerceAtLeast(1) - 1)

    fun chapterStart(chapter: Int, chapters: Int): Float =
        (chapter.toFloat() / chapters.coerceAtLeast(1)).coerceIn(0f, 1f)

    fun nextSpeed(current: Float): Float {
        val options = AppConfig.Player.speedOptions
        val index = options.indexOfFirst { it == current }
        return options[(index + 1).mod(options.size)]
    }

    /**
     * Simulated seconds after which the sleep timer stops playback, counted from when it was set;
     * null when the timer never fires early (off, or end of story, which stops at the end anyway).
     */
    fun sleepAfterSeconds(timer: SleepTimer): Int? =
        when (timer) {
            is SleepTimer.Minutes -> timer.value * SECONDS_PER_MINUTE
            SleepTimer.Off, SleepTimer.EndOfStory -> null
        }

    /** Simulated seconds that pass in one tick at [speed]. */
    fun secondsPerTick(durationMinutes: Int, speed: Float): Float =
        totalSeconds(durationMinutes) * AppConfig.Player.TICK_PERCENT / PERCENT * speed
}
