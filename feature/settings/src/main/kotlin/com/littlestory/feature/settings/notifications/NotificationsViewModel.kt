package com.littlestory.feature.settings.notifications

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlestory.domain.model.NotificationItem
import com.littlestory.domain.repository.NotificationsRepository
import com.littlestory.domain.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val SUBSCRIPTION_TIMEOUT_MS = 5_000L

@Immutable
data class NotificationsUiState(val loading: Boolean = true, val items: List<NotificationItem> = emptyList()) {
    val hasUnread: Boolean get() = items.any { !it.read }
}

/** SCR-18 (LS-112): the mock inbox, in the content language. */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class NotificationsViewModel
    @Inject
    constructor(
        preferences: PreferencesRepository,
        private val notifications: NotificationsRepository,
    ) : ViewModel() {
        val state: StateFlow<NotificationsUiState> =
            preferences.contentLanguageTag
                .flatMapLatest { notifications.notifications(it) }
                .map { NotificationsUiState(loading = false, items = it) }
                .stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(SUBSCRIPTION_TIMEOUT_MS),
                    NotificationsUiState(),
                )

        fun onOpen(id: String) {
            viewModelScope.launch { notifications.markRead(id) }
        }

        fun onMarkAllRead() {
            viewModelScope.launch { notifications.markAllRead() }
        }
    }
