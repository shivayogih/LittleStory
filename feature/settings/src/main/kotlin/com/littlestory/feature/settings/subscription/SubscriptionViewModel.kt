package com.littlestory.feature.settings.subscription

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlestory.core.common.config.AppConfig
import com.littlestory.domain.model.Plan
import com.littlestory.domain.repository.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

private const val SUBSCRIPTION_TIMEOUT_MS = 5_000L

@Immutable
data class SubscriptionUiState(
    val plans: List<Plan> = emptyList(),
    val selectedId: String = AppConfig.Subscription.DEFAULT_PLAN_ID,
    val confirmedId: String? = null,
) {
    val selected: Plan? get() = plans.firstOrNull { it.id == selectedId }
}

/** SCR-15 (LS-113): illustrative plans behind the demoSubscription flag. No billing, nothing is charged. */
@HiltViewModel
class SubscriptionViewModel
    @Inject
    constructor(
        subscription: SubscriptionRepository,
    ) : ViewModel() {
        private val selection = MutableStateFlow(SubscriptionUiState())

        val state: StateFlow<SubscriptionUiState> =
            combine(subscription.plans, selection) { plans, current -> current.copy(plans = plans) }
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(SUBSCRIPTION_TIMEOUT_MS), SubscriptionUiState())

        fun onSelect(planId: String) = selection.update { it.copy(selectedId = planId) }

        fun onChoose() = selection.update { it.copy(confirmedId = it.selectedId) }

        fun onDialogDismiss() = selection.update { it.copy(confirmedId = null) }
    }
