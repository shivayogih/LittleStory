package com.littlestory.domain

import com.littlestory.core.common.config.AppConfig
import com.littlestory.domain.model.AvatarId
import com.littlestory.domain.model.ChildProfileDraft
import com.littlestory.domain.model.ProfileError
import com.littlestory.domain.model.ProfileValidator
import com.littlestory.domain.model.PromptError
import com.littlestory.domain.model.PromptValidator
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ValidationTest {
    private val valid =
        ChildProfileDraft(
            firstName = "Aarav",
            age = AppConfig.Setup.DEFAULT_AGE,
            avatar = AvatarId(0),
            interestIds = setOf("animals"),
            languageTag = "en",
            defaultDurationMinutes = AppConfig.Setup.DEFAULT_DURATION_MINUTES,
        )

    @Test
    fun aCompleteDraftIsValid() = assertEquals(emptySet(), ProfileValidator.validate(valid))

    @Test
    fun blankNameAgeAndDurationAreRejected() {
        val errors =
            ProfileValidator.validate(
                valid.copy(firstName = "  ", age = 2, defaultDurationMinutes = 7, interestIds = emptySet()),
            )
        assertEquals(
            setOf(
                ProfileError.NameEmpty,
                ProfileError.AgeOutOfRange,
                ProfileError.DurationNotAllowed,
                ProfileError.NoInterests,
            ),
            errors,
        )
    }

    @Test
    fun ageBoundsAreInclusive() {
        assertEquals(emptySet(), ProfileValidator.validate(valid.copy(age = AppConfig.Setup.ageRange.first)))
        assertEquals(emptySet(), ProfileValidator.validate(valid.copy(age = AppConfig.Setup.ageRange.last)))
        assertEquals(
            setOf(ProfileError.AgeOutOfRange),
            ProfileValidator.validate(
                valid.copy(
                    age =
                        AppConfig.Setup.ageRange.last + 1,
                ),
            ),
        )
    }

    @Test
    fun promptLimitComesFromConfig() {
        assertEquals(PromptError.Empty, PromptValidator.validate(" "))
        assertNull(PromptValidator.validate("x".repeat(AppConfig.Create.PROMPT_MAX_CHARS)))
        assertEquals(PromptError.TooLong, PromptValidator.validate("x".repeat(AppConfig.Create.PROMPT_MAX_CHARS + 1)))
    }
}
