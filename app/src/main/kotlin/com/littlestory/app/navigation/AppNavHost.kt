package com.littlestory.app.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.littlestory.app.R
import com.littlestory.core.designsystem.icon.LittleStoryIcons
import com.littlestory.core.ui.chrome.BottomNav
import com.littlestory.core.ui.chrome.NavTab
import com.littlestory.feature.create.navigation.CreateRoute
import com.littlestory.feature.create.navigation.GeneratingRoute
import com.littlestory.feature.create.navigation.StoryReadyRoute
import com.littlestory.feature.create.navigation.createScreen
import com.littlestory.feature.create.navigation.generatingScreen
import com.littlestory.feature.create.navigation.storyReadyScreen
import com.littlestory.feature.create.ready.ReadyActions
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
import com.littlestory.feature.library.navigation.DownloadsRoute
import com.littlestory.feature.library.navigation.LibraryTab
import com.littlestory.feature.library.navigation.MyStoriesRoute
import com.littlestory.feature.library.navigation.downloadsScreen
import com.littlestory.feature.library.navigation.myStoriesScreen
import com.littlestory.feature.library.stories.MyStoriesActions
import com.littlestory.feature.onboarding.navigation.OnboardingRoute
import com.littlestory.feature.onboarding.navigation.SetupRoute
import com.littlestory.feature.onboarding.navigation.onboardingScreen
import com.littlestory.feature.onboarding.navigation.setupScreen
import com.littlestory.feature.reader.navigation.PlayerRoute
import com.littlestory.feature.reader.navigation.ReaderRoute
import com.littlestory.feature.reader.navigation.playerScreen
import com.littlestory.feature.reader.navigation.readerScreen
import com.littlestory.feature.settings.children.ChildProfilesActions
import com.littlestory.feature.settings.navigation.ChildProfilesRoute
import com.littlestory.feature.settings.navigation.NotificationsRoute
import com.littlestory.feature.settings.navigation.ProfileRoute
import com.littlestory.feature.settings.navigation.ReminderRoute
import com.littlestory.feature.settings.navigation.SubscriptionRoute
import com.littlestory.feature.settings.navigation.childProfilesScreen
import com.littlestory.feature.settings.navigation.notificationsScreen
import com.littlestory.feature.settings.navigation.profileScreen
import com.littlestory.feature.settings.navigation.reminderScreen
import com.littlestory.feature.settings.navigation.subscriptionScreen
import com.littlestory.feature.settings.notifications.NotificationsActions
import com.littlestory.feature.settings.profile.ProfileActions

/**
 * The single navigation graph. Features expose `xxxScreen(...)` builders and never reference each
 * other's routes; this file is where they are connected (docs/architecture/navigation-design.md).
 * The five tab roots (Home, Discover, Create, My Stories, Profile) show the bottom navigation;
 * every screen pushed on top of them covers it.
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
            destination?.hasRoute<CreateRoute>() == true -> TAB_CREATE
            destination?.hasRoute<MyStoriesRoute>() == true -> TAB_STORIES
            destination?.hasRoute<ProfileRoute>() == true -> TAB_PROFILE
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
            browseGraph(navController)
            createGraph(navController)
            listenGraph(navController)
            settingsGraph(navController)
        }
        if (selectedTab != null) {
            BottomNav(
                tabs = bottomNavTabs(),
                selectedIndex = selectedTab,
                onSelect = { index ->
                    when (index) {
                        TAB_HOME -> navController.navigateToTab(HomeRoute)
                        TAB_DISCOVER -> navController.navigateToTab(DiscoverRoute)
                        TAB_CREATE -> navController.navigateToTab(CreateRoute)
                        TAB_STORIES -> navController.navigateToTab(MyStoriesRoute())
                        TAB_PROFILE -> navController.navigateToTab(ProfileRoute)
                    }
                },
            )
        }
    }
}

private const val TAB_HOME = 0
private const val TAB_DISCOVER = 1
private const val TAB_CREATE = 2
private const val TAB_STORIES = 3
private const val TAB_PROFILE = 4

/** Home, Discover, Category and Story Detail (P3/P4). */
private fun NavGraphBuilder.browseGraph(nav: NavHostController) {
    homeScreen(
        HomeActions(
            onNotifications = { nav.navigate(NotificationsRoute) },
            onProfiles = { nav.navigate(ChildProfilesRoute) },
            onCreate = { nav.navigateToTab(CreateRoute) },
            onResume = { nav.navigate(PlayerRoute(it)) },
            onDiscover = { nav.navigateToTab(DiscoverRoute) },
            onCategory = { nav.navigate(CategoryRoute(it)) },
            onStory = { nav.navigate(StoryDetailRoute(it)) },
        ),
    )
    discoverScreen(
        DiscoverActions(
            onCategory = { nav.navigate(CategoryRoute(it)) },
            onStory = { nav.navigate(StoryDetailRoute(it)) },
        ),
    )
    categoryScreen(
        onBack = { nav.popBackStack() },
        onStory = { nav.navigate(StoryDetailRoute(it)) },
    )
    storyDetailScreen(
        DetailActions(
            onBack = { nav.popBackStack() },
            onListen = { nav.navigate(PlayerRoute(it)) },
            onRead = { nav.navigate(ReaderRoute(it)) },
        ),
    )
}

/** Create → Generating → Story Ready. Generating leaves the back stack once the story is ready. */
private fun NavGraphBuilder.createGraph(nav: NavHostController) {
    createScreen(onGenerate = { nav.navigate(GeneratingRoute(it)) })
    generatingScreen(
        onReady = { storyId ->
            nav.navigate(StoryReadyRoute(storyId)) { popUpTo<GeneratingRoute> { inclusive = true } }
        },
        onBackToIdea = { nav.popBackStack() },
    )
    storyReadyScreen(
        ReadyActions(
            onBack = { nav.popBackStack() },
            onListen = { nav.navigate(PlayerRoute(it)) },
            onRead = { nav.navigate(ReaderRoute(it)) },
            onOpenCreated = { nav.openCreatedStories() },
        ),
    )
}

/** Reader, Player, My Stories and Downloads. Reader and Player swap instead of stacking. */
private fun NavGraphBuilder.listenGraph(nav: NavHostController) {
    readerScreen(
        onBack = { nav.popBackStack() },
        onPlay = { nav.navigate(PlayerRoute(it)) { popUpTo<ReaderRoute> { inclusive = true } } },
    )
    playerScreen(
        onBack = { nav.popBackStack() },
        onStoryText = { nav.navigate(ReaderRoute(it)) { popUpTo<PlayerRoute> { inclusive = true } } },
    )
    myStoriesScreen(
        MyStoriesActions(
            onDownloads = { nav.navigate(DownloadsRoute) },
            onStory = { nav.navigate(StoryDetailRoute(it)) },
            onCreatedStory = { nav.navigate(StoryReadyRoute(it)) },
            onPlay = { nav.navigate(PlayerRoute(it)) },
            onDiscover = { nav.navigateToTab(DiscoverRoute) },
        ),
    )
    downloadsScreen(
        onBack = { nav.popBackStack() },
        onFindStory = { nav.navigateToTab(DiscoverRoute) },
    )
}

/** Profile hub and the parent screens it opens. */
private fun NavGraphBuilder.settingsGraph(nav: NavHostController) {
    profileScreen(
        ProfileActions(
            onEditChild = { nav.navigate(SetupRoute(it)) },
            onChildren = { nav.navigate(ChildProfilesRoute) },
            onReminder = { nav.navigate(ReminderRoute) },
            onNotifications = { nav.navigate(NotificationsRoute) },
            onDownloads = { nav.navigate(DownloadsRoute) },
            onSubscription = { nav.navigate(SubscriptionRoute) },
        ),
    )
    childProfilesScreen(
        ChildProfilesActions(
            onBack = { nav.popBackStack() },
            onEdit = { nav.navigate(SetupRoute(it)) },
            onAdd = { nav.navigate(SetupRoute()) },
        ),
    )
    reminderScreen(onBack = { nav.popBackStack() })
    notificationsScreen(
        NotificationsActions(
            onBack = { nav.popBackStack() },
            onStoryReady = { nav.openCreatedStories() },
            onReminder = { nav.navigate(ReminderRoute) },
            onNewStories = { nav.navigateToTab(DiscoverRoute) },
        ),
    )
    subscriptionScreen(onBack = { nav.popBackStack() }, onDone = { nav.navigateToTab(HomeRoute) })
}

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
 * Opens My Stories on "Created by me" as a fresh tab root, so the requested tab wins over any
 * saved My Stories state.
 */
private fun NavHostController.openCreatedStories() {
    navigate(MyStoriesRoute(LibraryTab.CreatedByMe)) { popUpTo<HomeRoute>() }
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
