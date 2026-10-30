package com.littlestory.data.repository

import com.littlestory.data.db.ChildProfileDao
import com.littlestory.data.db.ChildProfileEntity
import com.littlestory.data.db.GeneratedStoryDao
import com.littlestory.data.db.ReadingProgressDao
import com.littlestory.data.db.SavedStoryDao
import com.littlestory.data.prefs.ActiveProfileStore
import com.littlestory.domain.model.AvatarId
import com.littlestory.domain.model.ChildProfile
import com.littlestory.domain.model.ChildProfileDraft
import com.littlestory.domain.repository.ProfilesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private const val INTEREST_SEPARATOR = ","

internal fun ChildProfileEntity.toDomain() =
    ChildProfile(
        id = id,
        firstName = firstName,
        age = age,
        avatar = AvatarId(avatarIndex),
        interestIds = interestIds.split(INTEREST_SEPARATOR).filter { it.isNotEmpty() }.toSet(),
        languageTag = languageTag,
        defaultDurationMinutes = defaultDurationMinutes,
    )

internal fun ChildProfile.toEntity(position: Long) =
    ChildProfileEntity(
        id = id,
        firstName = firstName,
        age = age,
        avatarIndex = avatar.index,
        interestIds = interestIds.sorted().joinToString(INTEREST_SEPARATOR),
        languageTag = languageTag,
        defaultDurationMinutes = defaultDurationMinutes,
        position = position,
    )

class ProfilesRepositoryImpl(
    private val dao: ChildProfileDao,
    private val savedDao: SavedStoryDao,
    private val progressDao: ReadingProgressDao,
    private val generatedDao: GeneratedStoryDao,
    private val active: ActiveProfileStore,
    private val newId: () -> String,
) : ProfilesRepository {
    override val profiles: Flow<List<ChildProfile>> = dao.observeAll().map { rows -> rows.map { it.toDomain() } }

    /** The chosen child, or the first profile when none is chosen or the chosen one was deleted. */
    @OptIn(ExperimentalCoroutinesApi::class)
    override val activeProfile: Flow<ChildProfile?> =
        combine(profiles, active.activeProfileId) { all, id ->
            all.firstOrNull { it.id == id } ?: all.firstOrNull()
        }

    override suspend fun create(draft: ChildProfileDraft): ChildProfile {
        val profile =
            ChildProfile(
                id = newId(),
                firstName = draft.firstName.trim(),
                age = draft.age,
                avatar = draft.avatar,
                interestIds = draft.interestIds,
                languageTag = draft.languageTag,
                defaultDurationMinutes = draft.defaultDurationMinutes,
            )
        dao.upsert(profile.toEntity(position = dao.maxPosition() + 1))
        if (active.activeProfileId.first() == null) active.set(profile.id)
        return profile
    }

    override suspend fun update(profile: ChildProfile) {
        val position = dao.observe(profile.id).first()?.position ?: (dao.maxPosition() + 1)
        dao.upsert(profile.toEntity(position))
    }

    override suspend fun delete(profileId: String) {
        savedDao.deleteForChild(profileId)
        progressDao.deleteForChild(profileId)
        generatedDao.deleteForChild(profileId)
        dao.delete(profileId)
        if (active.activeProfileId.first() == profileId) active.set(null)
    }

    override suspend fun setActive(profileId: String) = active.set(profileId)
}
