package com.littlestory.domain.model

import com.littlestory.core.common.config.AppConfig

/** Which of the drawn avatars a child uses (design shows three plus an add tile). */
@JvmInline
value class AvatarId(val index: Int)

/** A child's profile. Data is minimal on purpose (decision D14): no surname, birthday or parent email. */
data class ChildProfile(
    val id: String,
    val firstName: String,
    val age: Int,
    val avatar: AvatarId,
    val interestIds: Set<String>,
    val languageTag: String,
    val defaultDurationMinutes: Int,
)

/** Input from the Setup form before it becomes a [ChildProfile]. */
data class ChildProfileDraft(
    val firstName: String,
    val age: Int,
    val avatar: AvatarId,
    val interestIds: Set<String>,
    val languageTag: String,
    val defaultDurationMinutes: Int,
)

enum class ProfileError { NameEmpty, NameTooLong, AgeOutOfRange, DurationNotAllowed }

/** Interests are optional in the design, so an empty set is valid. */
object ProfileValidator {
    fun validate(draft: ChildProfileDraft): Set<ProfileError> =
        buildSet {
            val name = draft.firstName.trim()
            if (name.isEmpty()) add(ProfileError.NameEmpty)
            if (name.length > AppConfig.Setup.NAME_MAX_CHARS) add(ProfileError.NameTooLong)
            if (draft.age !in AppConfig.Setup.ageRange) add(ProfileError.AgeOutOfRange)
            if (draft.defaultDurationMinutes !in
                AppConfig.Setup.durationOptionsMinutes
            ) {
                add(ProfileError.DurationNotAllowed)
            }
        }
}
