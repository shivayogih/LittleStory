package com.littlestory.feature.settings.children

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlestory.core.common.language.SupportedLanguages
import com.littlestory.domain.repository.ProfilesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val SUBSCRIPTION_TIMEOUT_MS = 5_000L

@Immutable
data class ChildCard(
    val id: String,
    val name: String,
    val age: Int,
    val avatarTone: Int,
    val languageName: String,
    val interestIds: List<String>,
    val active: Boolean,
)

@Immutable
data class ChildProfilesUiState(val loading: Boolean = true, val children: List<ChildCard> = emptyList())

/** SCR-14 (LS-063): list, switch and open the edit form for each child. */
@HiltViewModel
class ChildProfilesViewModel
    @Inject
    constructor(
        private val profiles: ProfilesRepository,
    ) : ViewModel() {
        val state: StateFlow<ChildProfilesUiState> =
            combine(profiles.profiles, profiles.activeProfile) { all, active ->
                ChildProfilesUiState(
                    loading = false,
                    children =
                        all.map {
                            ChildCard(
                                id = it.id,
                                name = it.firstName,
                                age = it.age,
                                avatarTone = it.avatar.index,
                                languageName = SupportedLanguages.fromTag(it.languageTag).nativeName,
                                interestIds = it.interestIds.sorted(),
                                active = it.id == active?.id,
                            )
                        },
                )
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(SUBSCRIPTION_TIMEOUT_MS), ChildProfilesUiState())

        fun onSwitch(childId: String) {
            viewModelScope.launch { profiles.setActive(childId) }
        }
    }
