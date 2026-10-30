package com.littlestory.domain.generation

import com.littlestory.core.common.config.AppConfig
import com.littlestory.domain.model.GenerationState
import com.littlestory.domain.model.generationStageFor
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

private const val COMPLETE_PERCENT = 100

/**
 * The simulated progress of one request (LS-082): [AppConfig.Generation.STEP_PERCENT] every
 * [AppConfig.Generation.STEP_INTERVAL_MS], from [startPercent] up to 100. Deterministic, and it uses
 * coroutine `delay`, so tests drive it with virtual time. Cancelling the collector pauses it.
 */
fun simulatedGenerationProgress(requestId: String, startPercent: Int = 0): Flow<GenerationState.InProgress> =
    flow {
        var percent = startPercent.coerceIn(0, COMPLETE_PERCENT)
        emit(GenerationState.InProgress(requestId, generationStageFor(percent), percent))
        while (percent < COMPLETE_PERCENT) {
            delay(AppConfig.Generation.STEP_INTERVAL_MS)
            percent = (percent + AppConfig.Generation.STEP_PERCENT).coerceAtMost(COMPLETE_PERCENT)
            emit(GenerationState.InProgress(requestId, generationStageFor(percent), percent))
        }
    }
