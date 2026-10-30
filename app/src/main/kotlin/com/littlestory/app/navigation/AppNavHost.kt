package com.littlestory.app.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.littlestory.app.R
import com.littlestory.core.designsystem.icon.LittleStoryIcons
import com.littlestory.core.ui.chrome.BottomNav
import com.littlestory.core.ui.chrome.NavTab
import com.littlestory.feature.discover.detail.DetailActions
import com.littlestory.feature.discover.discover.DiscoverActions
import com.littlestory.feature.discover.navigation.CategoryRoute
import com.littlestory.feature.discover.navigation.DiscoverRoute
import com.littlestory.feature.discover.navigation.StoryDetailRoute
import com.littlestory.feature.discover.navigation.categoryScreen
import com.littlestory.feature.discover.navigation.discoverScreen
import com.littlestory.feature.discover.navigation.storyDetailScreen
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
 * Home and Discover show the bottom navigation; Category and Story Detail cover it.
 */
@Composable
fun AppNavHost(
    startDestination: Any,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val entry by navController.currentBackStackEntryAsState()
    val destination = entry?.destination
    val selectedTab =
        when {
            destination?.hasRoute<HomeRoute>() == true -> TAB_HOME
            destination?.hasRoute<DiscoverRoute>() == true -> TAB_DISCOVER
            else -> null
        }
    Column(modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.weight(1f),
        ) {
            onboardingScreen(onFinished = { navController.navigate(SetupRoute()) })
            setupScreen(onDone = { navController.finishSetup() })
            homeScreen(
                HomeActions(
                    onDiscover = { navController.navigateToTab(DiscoverRoute) },
                    onCategory = { navController.navigate(CategoryRoute(it)) },
                    onStory = { navController.navigate(StoryDetailRoute(it)) },
                ),
            )
            discoverScreen(
                DiscoverActions(
                    onCategory = { navController.navigate(CategoryRoute(it)) },
                    onStory = { navController.navigate(StoryDetailRoute(it)) },
                ),
            )
            categoryScreen(
                onBack = { navController.popBackStack() },
                onStory = { navController.navigate(StoryDetailRoute(it)) },
            )
            storyDetailScreen(DetailActions(onBack = { navController.popBackStack() }))
        }
        if (selectedTab != null) {
            BottomNav(
                tabs = bottomNavTabs(),
                selectedIndex = selectedTab,
                onSelect = { index ->
                    when (index) {
                        TAB_HOME -> navController.navigateToTab(HomeRoute)
                        TAB_DISCOVER -> navController.navigateToTab(DiscoverRoute)
                        // Create, My Stories and Profile are connected by the milestones that build them.
                        else -> Unit
                    }
                },
            )
        }
    }
}

private const val TAB_HOME = 0
private const val TAB_DISCOVER = 1

@Composable
private fun bottomNavTabs(): List<NavTab> =
    listOf(
        NavTab(stringResource(R.string.nav_home), LittleStoryIcons.Home),
        NavTab(stringResource(R.string.nav_discover), LittleStoryIcons.Compass),
        NavTab(stringResource(R.string.nav_create), LittleStoryIcons.Plus, isCreate = true),
        NavTab(stringResource(R.string.nav_stories), LittleStoryIcons.Book),
        NavTab(stringResource(R.string.nav_profile), LittleStoryIcons.User),
    )

/** Switching tabs keeps one copy of each tab on top of Home and restores its state. */
private fun NavHostController.navigateToTab(route: Any) {
    navigate(route) {
        popUpTo<HomeRoute> { saveState = true }
        launchSingleTop = true
        restoreState = true
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
