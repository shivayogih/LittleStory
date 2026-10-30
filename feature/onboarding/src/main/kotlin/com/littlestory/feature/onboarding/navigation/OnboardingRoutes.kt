package com.littlestory.feature.onboarding.navigation

import kotlinx.serialization.Serializable

/** SCR-01: the three intro slides shown on first run. */
@Serializable
data object OnboardingRoute

/** SCR-02: child profile form. [childId] is null when creating a child, set when editing one. */
@Serializable
data class SetupRoute(val childId: String? = null)
