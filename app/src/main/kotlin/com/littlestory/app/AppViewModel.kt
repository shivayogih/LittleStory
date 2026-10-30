package com.littlestory.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlestory.domain.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/** Where the app starts, decided once the first-run flag has been read. */
sealed interface StartState {
    /** Preferences are still loading: the system splash screen stays up. */
    data object Loading : StartState

    data object Onboarding : StartState

    data object Home : StartState
}

@HiltViewModel
class AppViewModel
    @Inject
    constructor(preferences: PreferencesRepository) : ViewModel() {
        val startState: StateFlow<StartState> =
            preferences.firstRunCompleted
                .map { completed -> if (completed) StartState.Home else StartState.Onboarding }
                .stateIn(viewModelScope, SharingStarted.Eagerly, StartState.Loading)
    }
