package com.littlestory.domain

import com.littlestory.core.common.config.AppConfig
import com.littlestory.domain.generation.simulatedGenerationProgress
import com.littlestory.domain.model.GenerationStage
import com.littlestory.domain.model.SleepTimer
import com.littlestory.domain.model.generationStageFor
import com.littlestory.domain.player.SimulatedPlayback
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SimulationTest {
    @Test
    fun stagesFollowTheConfiguredThresholds() {
        val (writing, narration) = AppConfig.Generation.stageThresholdsPercent
        assertEquals(GenerationStage.Imagining, generationStageFor(0))
        assertEquals(GenerationStage.Imagining, generationStageFor(writing - 1))
        assertEquals(GenerationStage.Writing, generationStageFor(writing))
        assertEquals(GenerationStage.PreparingNarration, generationStageFor(narration))
        assertEquals(GenerationStage.PreparingNarration, generationStageFor(100))
    }

    @Test
    fun progressStepsToOneHundredOnVirtualTime() =
        runTest {
            val states = simulatedGenerationProgress("r1").toList()
            assertEquals(0, states.first().percent)
            assertEquals(100, states.last().percent)
            val steps = 100 / AppConfig.Generation.STEP_PERCENT
            assertEquals(steps * AppConfig.Generation.STEP_INTERVAL_MS, currentTime)
        }

    @Test
    fun retryStartsFromTheRestartPercent() =
        runTest {
            val first = simulatedGenerationProgress("r1", AppConfig.Generation.RESTART_PERCENT).toList().first()
            assertEquals(AppConfig.Generation.RESTART_PERCENT, first.percent)
        }

    @Test
    fun skipMovesFifteenSecondsAndClamps() {
        val minutes = 8
        val step = AppConfig.Player.SKIP_SECONDS.toFloat() / (minutes * 60)
        assertEquals(step, SimulatedPlayback.skip(0f, minutes, 1), 1e-6f)
        assertEquals(0f, SimulatedPlayback.skip(0f, minutes, -1))
        assertEquals(1f, SimulatedPlayback.skip(0.999f, minutes, 1))
    }

    @Test
    fun speedCyclesThroughTheConfiguredOptions() {
        val options = AppConfig.Player.speedOptions
        var speed = AppConfig.Player.DEFAULT_SPEED
        val seen =
            List(options.size) {
                speed = SimulatedPlayback.nextSpeed(speed)
                speed
            }
        assertEquals(options.toSet(), seen.toSet())
        assertEquals(AppConfig.Player.DEFAULT_SPEED, seen.last())
    }

    @Test
    fun chaptersAreEvenlySpread() {
        assertEquals(0, SimulatedPlayback.chapterOf(0f, 5))
        assertEquals(2, SimulatedPlayback.chapterOf(0.5f, 5))
        assertEquals(4, SimulatedPlayback.chapterOf(1f, 5))
        assertEquals(0.4f, SimulatedPlayback.chapterStart(2, 5))
    }

    @Test
    fun sleepTimerFiresOnlyForMinutes() {
        assertEquals(600, SimulatedPlayback.sleepAfterSeconds(SleepTimer.Minutes(10)))
        assertNull(SimulatedPlayback.sleepAfterSeconds(SleepTimer.EndOfStory))
        assertNull(SimulatedPlayback.sleepAfterSeconds(SleepTimer.Off))
    }
}
