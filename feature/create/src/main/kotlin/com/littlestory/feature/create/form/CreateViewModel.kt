package com.littlestory.feature.create.form

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlestory.core.common.config.AppConfig
import com.littlestory.core.common.language.SupportedLanguages
import com.littlestory.domain.model.ChildProfile
import com.littlestory.domain.model.PromptError
import com.littlestory.domain.model.PromptValidator
import com.littlestory.domain.model.StoryCreationRequest
import com.littlestory.domain.repository.GenerationRepository
import com.littlestory.domain.repository.PreferencesRepository
import com.littlestory.domain.repository.ProfilesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

/** A child who can be the story hero. */
@Immutable
data class HeroOption(val id: String, val name: String, val age: Int, val avatarTone: Int)

@Immutable
data class CreateUiState(
    val prompt: String = "",
    val heroes: List<HeroOption> = emptyList(),
    val heroId: String? = null,
    val themeId: String = AppConfig.Create.DEFAULT_THEME,
    val moodId: String = AppConfig.Create.DEFAULT_MOOD,
    val durationMinutes: Int = AppConfig.Create.DEFAULT_DURATION_MINUTES,
    val languageTag: String = SupportedLanguages.default.tag,
    val moreOptionsExpanded: Boolean = false,
    val showEmptyError: Boolean = false,
) {
    val hero: HeroOption? get() = heroes.firstOrNull { it.id == heroId }
    val maxChars: Int get() = AppConfig.Create.PROMPT_MAX_CHARS
}

sealed interface CreateEffect {
    data class StartGeneration(val requestId: String) : CreateEffect
}

/** SCR-07 (LS-080, LS-081): the parent describes the day; nothing typed here leaves the device. */
@HiltViewModel
class CreateViewModel
    @Inject
    constructor(
        private val profiles: ProfilesRepository,
        private val preferences: PreferencesRepository,
        private val generation: GenerationRepository,
    ) : ViewModel() {
        private val _state = MutableStateFlow(CreateUiState())
        val state: StateFlow<CreateUiState> = _state.asStateFlow()

        private val _effects = Channel<CreateEffect>(Channel.BUFFERED)
        val effects: Flow<CreateEffect> = _effects.receiveAsFlow()

        init {
            viewModelScope.launch {
                val language = preferences.contentLanguageTag.first()
                _state.update { it.copy(languageTag = SupportedLanguages.fromTag(language).tag) }
                combine(profiles.profiles, profiles.activeProfile) { all, active -> all to active }
                    .collect { (all, active) -> onProfiles(all, active) }
            }
        }

        private fun onProfiles(all: List<ChildProfile>, active: ChildProfile?) {
            _state.update { current ->
                val heroes = all.map { HeroOption(it.id, it.firstName, it.age, it.avatar.index) }
                val keep = current.heroId?.takeIf { id -> heroes.any { it.id == id } }
                val heroId = keep ?: active?.id ?: heroes.firstOrNull()?.id
                val defaultDuration =
                    active
                        ?.defaultDurationMinutes
                        ?.takeIf { it in AppConfig.Create.durationOptionsMinutes && current.heroId == null }
                current.copy(
                    heroes = heroes,
                    heroId = heroId,
                    durationMinutes = defaultDuration ?: current.durationMinutes,
                )
            }
        }

        fun onPromptChange(value: String) =
            _state.update { it.copy(prompt = value.take(AppConfig.Create.PROMPT_MAX_CHARS), showEmptyError = false) }

        fun onHeroSelected(id: String) = _state.update { it.copy(heroId = id) }

        fun onThemeSelected(id: String) = _state.update { it.copy(themeId = id) }

        fun onMoodSelected(id: String) = _state.update { it.copy(moodId = id) }

        fun onDurationSelected(minutes: Int) = _state.update { it.copy(durationMinutes = minutes) }

        fun onLanguageSelected(tag: String) = _state.update { it.copy(languageTag = tag) }

        fun onToggleMoreOptions() = _state.update { it.copy(moreOptionsExpanded = !it.moreOptionsExpanded) }

        fun onCreate() {
            val current = _state.value
            if (PromptValidator.validate(current.prompt) == PromptError.Empty) {
                _state.update { it.copy(showEmptyError = true) }
                return
            }
            val hero = current.hero
            val request =
                StoryCreationRequest(
                    requestId = UUID.randomUUID().toString(),
                    prompt = current.prompt.trim(),
                    heroChildId = hero?.id ?: AppConfig.Profiles.GUEST_ID,
                    heroName = hero?.name.orEmpty(),
                    heroAge = hero?.age ?: AppConfig.Setup.DEFAULT_AGE,
                    themeId = current.themeId,
                    moodId = current.moodId,
                    durationMinutes = current.durationMinutes,
                    languageTag = current.languageTag,
                )
            generation.submit(request)
            _effects.trySend(CreateEffect.StartGeneration(request.requestId))
        }
    }
