package com.littlestory.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.littlestory.feature.home.home.HomeActions
import com.littlestory.feature.home.navigation.HomeRoute
import com.littlestory.feature.home.navigation.homeScreen
import com.littlestory.feature.onboarding.navigation.OnboardingRoute
import com.littlestory.feature.onboarding.navigation.SetupRoute
import com.littlestory.feature.onboarding.navigation.onboardingScreen
import com.littlestory.feature.onboarding.navigation.setupScreen

/**
 * The single navigation graph. Features expose `xxxScreen(...)` builders and never reference each
 * other's routes; this file is where they are connected (docs/architecture/navigation-design.md).
 */
@Composable
fun AppNavHost(
    startDestination: Any,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(navController = navController, startDestination = startDestination, modifier = modifier) {
        onboardingScreen(onFinished = { navController.navigate(SetupRoute()) })
        setupScreen(onDone = { navController.finishSetup() })
        homeScreen(HomeActions())
    }
}

/**
 * First run: Setup was reached from Onboarding, so both are popped and back from Home exits the
 * app. Opened later (for example to edit a child), Setup simply returns to where it came from.
 */
internal fun NavHostController.finishSetup() {
    val fromFirstRun = previousBackStackEntry?.destination?.hasRoute<OnboardingRoute>() == true
    if (fromFirstRun) {
        navigate(HomeRoute) { popUpTo<OnboardingRoute> { inclusive = true } }
    } else {
        popBackStack()
    }
}
