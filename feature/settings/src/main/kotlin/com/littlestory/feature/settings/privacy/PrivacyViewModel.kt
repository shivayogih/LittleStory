package com.littlestory.feature.settings.privacy

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlestory.domain.repository.LocalDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class PrivacyUiState(val confirmOpen: Boolean = false, val deleting: Boolean = false)

sealed interface PrivacyEffect {
    /** Everything is gone; the app restarts the first-run flow. */
    data object Deleted : PrivacyEffect
}

/** SCR-21 (LS-114): explains what stays on the device and lets a parent wipe it. */
@HiltViewModel
class PrivacyViewModel
    @Inject
    constructor(
        private val localData: LocalDataRepository,
    ) : ViewModel() {
        private val _state = MutableStateFlow(PrivacyUiState())
        val state: StateFlow<PrivacyUiState> = _state.asStateFlow()

        private val _effects = Channel<PrivacyEffect>(Channel.BUFFERED)
        val effects: Flow<PrivacyEffect> = _effects.receiveAsFlow()

        fun onDeleteClick() = _state.update { it.copy(confirmOpen = true) }

        fun onConfirmDismiss() = _state.update { it.copy(confirmOpen = false) }

        fun onConfirmDelete() {
            if (_state.value.deleting) return
            _state.update { it.copy(confirmOpen = false, deleting = true) }
            viewModelScope.launch {
                localData.deleteAll()
                _state.update { it.copy(deleting = false) }
                _effects.send(PrivacyEffect.Deleted)
            }
        }
    }
