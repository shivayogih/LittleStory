package com.littlestory.feature.create.generating

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.littlestory.core.common.config.AppConfig
import com.littlestory.domain.model.GenerationStage
import com.littlestory.domain.model.GenerationState
import com.littlestory.domain.model.generationStageFor
import com.littlestory.domain.repository.GenerationRepository
import com.littlestory.feature.create.navigation.GeneratingRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class GeneratingUiState(
    val heroName: String = "",
    val percent: Int = 0,
    val paused: Boolean = false,
) {
    val stage: GenerationStage get() = generationStageFor(percent)
}

sealed interface GeneratingEffect {
    data class Ready(val storyId: String) : GeneratingEffect
}

/** SCR-08 (LS-083): runs the simulated generation; closing pauses it, Try again restarts at 8 %. */
@HiltViewModel
class GeneratingViewModel
    @Inject
    constructor(
        savedState: SavedStateHandle,
        private val generation: GenerationRepository,
    ) : ViewModel() {
        private val requestId = savedState.toRoute<GeneratingRoute>().requestId

        private val _state =
            MutableStateFlow(GeneratingUiState(heroName = generation.request(requestId)?.heroName.orEmpty()))
        val state: StateFlow<GeneratingUiState> = _state.asStateFlow()

        private val _effects = Channel<GeneratingEffect>(Channel.BUFFERED)
        val effects: Flow<GeneratingEffect> = _effects.receiveAsFlow()

        private var job: Job? = null

        init {
            run(startPercent = 0)
        }

        fun onClose() {
            job?.cancel()
            _state.update { it.copy(paused = true) }
        }

        fun onTryAgain() = run(AppConfig.Generation.RESTART_PERCENT)

        private fun run(startPercent: Int) {
            job?.cancel()
            _state.update { it.copy(paused = false, percent = startPercent) }
            job =
                viewModelScope.launch {
                    generation.generate(requestId, startPercent).collect { state ->
                        when (state) {
                            is GenerationState.InProgress -> _state.update { it.copy(percent = state.percent) }
                            is GenerationState.Completed -> {
                                delay(AppConfig.Generation.AUTO_ADVANCE_DELAY_MS)
                                _effects.send(GeneratingEffect.Ready(state.storyId))
                            }
                            // An unknown request (for example after the app was killed) offers Back to your idea.
                            is GenerationState.Failed, is GenerationState.Paused ->
                                _state.update {
                                    it.copy(
                                        paused = true,
                                    )
                                }
                            GenerationState.Idle -> Unit
                        }
                    }
                }
        }
    }
