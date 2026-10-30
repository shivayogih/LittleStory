package com.littlestory.feature.onboarding.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.littlestory.feature.onboarding.onboarding.OnboardingRoute as OnboardingScreenRoute
import com.littlestory.feature.onboarding.setup.SetupRoute as SetupScreenRoute

/** Registers the onboarding destinations; the app supplies where each one leads. */
fun NavGraphBuilder.onboardingScreen(onFinished: () -> Unit) {
    composable<OnboardingRoute> { OnboardingScreenRoute(onFinished) }
}

fun NavGraphBuilder.setupScreen(onDone: () -> Unit) {
    composable<SetupRoute> { SetupScreenRoute(onDone) }
}
