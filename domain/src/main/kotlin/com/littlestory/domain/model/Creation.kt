package com.littlestory.domain.model

import com.littlestory.core.common.config.AppConfig

data class StoryCreationRequest(
    val requestId: String,
    val prompt: String,
    val heroChildId: String,
    val themeId: String,
    val moodId: String,
    val durationMinutes: Int,
    val languageTag: String,
)

enum class PromptError { Empty, TooLong }

object PromptValidator {
    fun validate(prompt: String): PromptError? =
        when {
            prompt.isBlank() -> PromptError.Empty
            prompt.length > AppConfig.Create.PROMPT_MAX_CHARS -> PromptError.TooLong
            else -> null
        }
}

enum class GenerationStage { Imagining, Writing, PreparingNarration }

enum class GenerationError { Interrupted, Unknown }

/** Simulated generation progress; deterministic per request in the MVP. */
sealed interface GenerationState {
    data object Idle : GenerationState

    data class InProgress(val requestId: String, val stage: GenerationStage, val percent: Int) : GenerationState

    data class Paused(val requestId: String, val percent: Int) : GenerationState

    data class Completed(val requestId: String, val storyId: String) : GenerationState

    data class Failed(val requestId: String, val error: GenerationError) : GenerationState
}
