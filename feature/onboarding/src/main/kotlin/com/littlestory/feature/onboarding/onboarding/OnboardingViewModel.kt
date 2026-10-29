package com.littlestory.feature.onboarding.onboarding

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.littlestory.core.ui.text.UiText
import com.littlestory.feature.onboarding.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

data class OnboardingSlide(val title: UiText, val body: UiText, val cta: UiText)

data class OnboardingUiState(val slides: List<OnboardingSlide>, val page: Int) {
    val isLast: Boolean get() = page == slides.lastIndex
    val current: OnboardingSlide get() = slides[page]
}

/** Non-replayable events: leaving onboarding for Setup. */
sealed interface OnboardingEffect {
    data object GoToSetup : OnboardingEffect
}

@HiltViewModel
class OnboardingViewModel
    @Inject
    constructor(
        private val savedState: SavedStateHandle,
    ) : ViewModel() {
        private val _state =
            MutableStateFlow(
                OnboardingUiState(
                    slides = SLIDES,
                    page = (savedState.get<Int>(PAGE_KEY) ?: 0).coerceIn(SLIDES.indices),
                ),
            )
        val state: StateFlow<OnboardingUiState> = _state.asStateFlow()

        private val _effects = Channel<OnboardingEffect>(Channel.BUFFERED)
        val effects: Flow<OnboardingEffect> = _effects.receiveAsFlow()

        /** Continue advances; the last slide's button (Get Started) leaves for Setup. */
        fun onContinue() {
            val current = _state.value
            if (current.isLast) {
                _effects.trySend(OnboardingEffect.GoToSetup)
            } else {
                setPage(current.page + 1)
            }
        }

        fun onSkip() {
            _effects.trySend(OnboardingEffect.GoToSetup)
        }

        private fun setPage(page: Int) {
            savedState[PAGE_KEY] = page
            _state.value = _state.value.copy(page = page)
        }

        private companion object {
            const val PAGE_KEY = "page"

            val SLIDES =
                listOf(
                    OnboardingSlide(
                        UiText.Res(R.string.onboarding_title_1),
                        UiText.Res(R.string.onboarding_body_1),
                        UiText.Res(com.littlestory.core.ui.R.string.action_continue),
                    ),
                    OnboardingSlide(
                        UiText.Res(R.string.onboarding_title_2),
                        UiText.Res(R.string.onboarding_body_2),
                        UiText.Res(com.littlestory.core.ui.R.string.action_continue),
                    ),
                    OnboardingSlide(
                        UiText.Res(R.string.onboarding_title_3),
                        UiText.Res(R.string.onboarding_body_3),
                        UiText.Res(R.string.onboarding_cta_get_started),
                    ),
                )
        }
    }
