package com.littlestory.feature.home.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlestory.core.ui.text.UiText
import com.littlestory.domain.repository.ProfilesRepository
import com.littlestory.feature.home.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class HomeUiState(val greeting: UiText, val title: UiText)

private const val SUBSCRIPTION_TIMEOUT_MS = 5_000L

@HiltViewModel
class HomeViewModel
    @Inject
    constructor(profiles: ProfilesRepository) : ViewModel() {
        /** "Stories for {child}", or a generic title when no profile was created (Skip for now). */
        val state: StateFlow<HomeUiState> =
            profiles.activeProfile
                .map { child ->
                    HomeUiState(
                        greeting = UiText.Res(R.string.home_greeting),
                        title =
                            if (child == null) {
                                UiText.Res(R.string.home_title_generic)
                            } else {
                                UiText.Res(R.string.home_title_for_child, listOf(child.firstName))
                            },
                    )
                }.stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(SUBSCRIPTION_TIMEOUT_MS),
                    HomeUiState(UiText.Res(R.string.home_greeting), UiText.Res(R.string.home_title_generic)),
                )
    }
