package com.littlestory.feature.onboarding.setup

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.littlestory.core.common.config.AppConfig
import com.littlestory.core.common.language.SupportedLanguages
import com.littlestory.core.ui.text.UiText
import com.littlestory.domain.model.AvatarId
import com.littlestory.domain.model.ChildProfileDraft
import com.littlestory.domain.model.ProfileError
import com.littlestory.domain.model.ProfileValidator
import com.littlestory.domain.repository.PreferencesRepository
import com.littlestory.domain.repository.ProfilesRepository
import com.littlestory.feature.onboarding.R
import com.littlestory.feature.onboarding.navigation.SetupRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SetupUiState(
    val name: String = "",
    val nameError: UiText? = null,
    val avatar: Int = 0,
    val age: Int = AppConfig.Setup.DEFAULT_AGE,
    val languageTag: String = SupportedLanguages.default.tag,
    val interestIds: Set<String> = emptySet(),
    val durationMinutes: Int = AppConfig.Setup.DEFAULT_DURATION_MINUTES,
    val isEditing: Boolean = false,
    val loading: Boolean = false,
) {
    /** "Create {name}'s profile" once a name is typed, a generic label before, "Save" when editing. */
    val ctaLabel: UiText
        get() =
            when {
                isEditing -> UiText.Res(R.string.setup_cta_save)
                name.isBlank() -> UiText.Res(R.string.setup_cta_create)
                else -> UiText.Res(R.string.setup_cta_create_named, listOf(name.trim()))
            }
}

/** Non-replayable events from Setup. */
sealed interface SetupEffect {
    /** The profile was saved or Setup was skipped; the app decides where to go next. */
    data object Done : SetupEffect

    /** The child chose another language: the UI layer applies it through AppCompat. */
    data class ApplyLanguage(val tag: String) : SetupEffect
}

@HiltViewModel
class SetupViewModel
    @Inject
    constructor(
        savedState: SavedStateHandle,
        private val profiles: ProfilesRepository,
        private val preferences: PreferencesRepository,
    ) : ViewModel() {
        private val childId: String? = savedState.toRoute<SetupRoute>().childId

        private val _state = MutableStateFlow(SetupUiState(isEditing = childId != null, loading = childId != null))
        val state: StateFlow<SetupUiState> = _state.asStateFlow()

        private val _effects = Channel<SetupEffect>(Channel.BUFFERED)
        val effects: Flow<SetupEffect> = _effects.receiveAsFlow()

        init {
            if (childId != null) {
                viewModelScope.launch {
                    val existing = profiles.profiles.first().firstOrNull { it.id == childId }
                    _state.update {
                        if (existing == null) {
                            it.copy(loading = false)
                        } else {
                            it.copy(
                                name = existing.firstName,
                                avatar = existing.avatar.index,
                                age = existing.age,
                                languageTag = existing.languageTag,
                                interestIds = existing.interestIds,
                                durationMinutes = existing.defaultDurationMinutes,
                                loading = false,
                            )
                        }
                    }
                }
            }
        }

        /** On first run the form starts in the language the app is currently shown in. */
        fun onUiLanguageKnown(tag: String) {
            if (!_state.value.isEditing) _state.update { it.copy(languageTag = SupportedLanguages.fromTag(tag).tag) }
        }

        fun onNameChange(value: String) =
            _state.update {
                it.copy(name = value.take(AppConfig.Setup.NAME_MAX_CHARS), nameError = null)
            }

        fun onAvatarSelected(index: Int) = _state.update { it.copy(avatar = index) }

        fun onAgeSelected(age: Int) = _state.update { it.copy(age = age) }

        fun onLanguageSelected(tag: String) {
            _state.update { it.copy(languageTag = tag) }
            _effects.trySend(SetupEffect.ApplyLanguage(tag))
        }

        fun onInterestToggled(id: String) =
            _state.update {
                it.copy(interestIds = if (id in it.interestIds) it.interestIds - id else it.interestIds + id)
            }

        fun onDurationSelected(minutes: Int) = _state.update { it.copy(durationMinutes = minutes) }

        fun onSubmit() {
            val current = _state.value
            val draft =
                ChildProfileDraft(
                    firstName = current.name,
                    age = current.age,
                    avatar = AvatarId(current.avatar),
                    interestIds = current.interestIds,
                    languageTag = current.languageTag,
                    defaultDurationMinutes = current.durationMinutes,
                )
            val errors = ProfileValidator.validate(draft)
            if (errors.isNotEmpty()) {
                _state.update { it.copy(nameError = errors.nameError()) }
                return
            }
            viewModelScope.launch {
                if (childId == null) {
                    profiles.create(draft)
                } else {
                    profiles.profiles.first().firstOrNull { it.id == childId }?.let {
                        profiles.update(
                            it.copy(
                                firstName = draft.firstName.trim(),
                                age = draft.age,
                                avatar = draft.avatar,
                                interestIds = draft.interestIds,
                                languageTag = draft.languageTag,
                                defaultDurationMinutes = draft.defaultDurationMinutes,
                            ),
                        )
                    }
                }
                preferences.setContentLanguageTag(current.languageTag)
                preferences.setFirstRunCompleted(true)
                _effects.send(SetupEffect.Done)
            }
        }

        /** "Skip for now": no profile is saved but first run counts as done. */
        fun onSkip() {
            viewModelScope.launch {
                preferences.setFirstRunCompleted(true)
                _effects.send(SetupEffect.Done)
            }
        }

        private fun Set<ProfileError>.nameError(): UiText? =
            when {
                ProfileError.NameEmpty in this -> UiText.Res(R.string.setup_error_name_empty)
                ProfileError.NameTooLong in this ->
                    UiText.Res(
                        R.string.setup_error_name_too_long,
                        listOf(AppConfig.Setup.NAME_MAX_CHARS),
                    )
                else -> null
            }
    }
