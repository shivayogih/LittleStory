package com.littlestory.feature.settings.language

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlestory.core.common.language.SupportedLanguage
import com.littlestory.core.common.language.SupportedLanguages
import com.littlestory.domain.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val SUBSCRIPTION_TIMEOUT_MS = 5_000L

/** [appLanguageTag] is filled in by the screen, which reads it from AppCompat. */
@Immutable
data class LanguageUiState(
    val languages: List<SupportedLanguage> = SupportedLanguages.enabled,
    val appLanguageTag: String = SupportedLanguages.default.tag,
    val storyLanguageTag: String = SupportedLanguages.default.tag,
)

/** SCR-20 (LS-046): the story language is stored in preferences; new registry entries appear without code changes. */
@HiltViewModel
class LanguageViewModel
    @Inject
    constructor(
        private val preferences: PreferencesRepository,
    ) : ViewModel() {
        val state: StateFlow<LanguageUiState> =
            preferences.contentLanguageTag
                .map { LanguageUiState(storyLanguageTag = SupportedLanguages.fromTag(it).tag) }
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(SUBSCRIPTION_TIMEOUT_MS), LanguageUiState())

        fun onStoryLanguageSelected(language: SupportedLanguage) {
            viewModelScope.launch { preferences.setContentLanguageTag(language.tag) }
        }
    }
