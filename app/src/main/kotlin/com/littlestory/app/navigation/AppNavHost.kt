package com.littlestory.app.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
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
import com.littlestory.feature.settings.navigation.HelpRoute
import com.littlestory.feature.settings.navigation.LanguageRoute
import com.littlestory.feature.settings.navigation.NotificationsRoute
import com.littlestory.feature.settings.navigation.PrivacyRoute
import com.littlestory.feature.settings.navigation.ProfileRoute
import com.littlestory.feature.settings.navigation.ReminderRoute
import com.littlestory.feature.settings.navigation.SubscriptionRoute
import com.littlestory.feature.settings.navigation.childProfilesScreen
import com.littlestory.feature.settings.navigation.helpScreen
import com.littlestory.feature.settings.navigation.languageScreen
import com.littlestory.feature.settings.navigation.notificationsScreen
import com.littlestory.feature.settings.navigation.privacyScreen
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
            onNotifications = { nav.go(NotificationsRoute) },
            onProfiles = { nav.go(ChildProfilesRoute) },
            onCreate = { nav.navigateToTab(CreateRoute) },
            onResume = { nav.go(PlayerRoute(it)) },
            onDiscover = { nav.navigateToTab(DiscoverRoute) },
            onCategory = { nav.go(CategoryRoute(it)) },
            onStory = { nav.go(StoryDetailRoute(it)) },
        ),
    )
    discoverScreen(
        DiscoverActions(
            onCategory = { nav.go(CategoryRoute(it)) },
            onStory = { nav.go(StoryDetailRoute(it)) },
        ),
    )
    categoryScreen(
        onBack = { nav.back() },
        onStory = { nav.go(StoryDetailRoute(it)) },
    )
    storyDetailScreen(
        DetailActions(
            onBack = { nav.back() },
            onListen = { nav.go(PlayerRoute(it)) },
            onRead = { nav.go(ReaderRoute(it)) },
        ),
    )
}

/**
 * Create → Generating → Story Ready. Once the story is ready the form and Generating both leave the
 * back stack, so back from Story Ready goes Home: the story is already saved, and showing the
 * filled form again would invite a duplicate (navigation-design.md §4, N4). Reaching Story Ready is
 * driven by the generation finishing, not by a tap, so it is never dropped while the app is paused.
 */
private fun NavGraphBuilder.createGraph(nav: NavHostController) {
    createScreen(onGenerate = { nav.go(GeneratingRoute(it)) })
    generatingScreen(
        onReady = { storyId ->
            nav.navigate(StoryReadyRoute(storyId)) { popUpTo<CreateRoute> { inclusive = true } }
        },
        onBackToIdea = { nav.back() },
    )
    storyReadyScreen(
        ReadyActions(
            onBack = { nav.back() },
            onListen = { nav.go(PlayerRoute(it)) },
            onRead = { nav.go(ReaderRoute(it)) },
            onOpenCreated = { nav.openCreatedStories() },
        ),
    )
}

/** Reader, Player, My Stories and Downloads. Reader and Player swap instead of stacking. */
private fun NavGraphBuilder.listenGraph(nav: NavHostController) {
    readerScreen(
        onBack = { nav.back() },
        onPlay = { nav.go(PlayerRoute(it)) { popUpTo<ReaderRoute> { inclusive = true } } },
    )
    playerScreen(
        onBack = { nav.back() },
        onStoryText = { nav.go(ReaderRoute(it)) { popUpTo<PlayerRoute> { inclusive = true } } },
    )
    myStoriesScreen(
        MyStoriesActions(
            onDownloads = { nav.go(DownloadsRoute) },
            onStory = { nav.go(StoryDetailRoute(it)) },
            onCreatedStory = { nav.go(StoryReadyRoute(it)) },
            onPlay = { nav.go(PlayerRoute(it)) },
            onDiscover = { nav.navigateToTab(DiscoverRoute) },
        ),
    )
    downloadsScreen(
        onBack = { nav.back() },
        onFindStory = { nav.navigateToTab(DiscoverRoute) },
    )
}

/** Profile hub and the parent screens it opens. */
private fun NavGraphBuilder.settingsGraph(nav: NavHostController) {
    profileScreen(
        ProfileActions(
            onEditChild = { nav.go(SetupRoute(it)) },
            onChildren = { nav.go(ChildProfilesRoute) },
            onReminder = { nav.go(ReminderRoute) },
            onNotifications = { nav.go(NotificationsRoute) },
            onDownloads = { nav.go(DownloadsRoute) },
            onSubscription = { nav.go(SubscriptionRoute) },
            onLanguage = { nav.go(LanguageRoute) },
            onPrivacy = { nav.go(PrivacyRoute) },
            onHelp = { nav.go(HelpRoute) },
        ),
    )
    childProfilesScreen(
        ChildProfilesActions(
            onBack = { nav.back() },
            onEdit = { nav.go(SetupRoute(it)) },
            onAdd = { nav.go(SetupRoute()) },
        ),
    )
    reminderScreen(onBack = { nav.back() })
    notificationsScreen(
        NotificationsActions(
            onBack = { nav.back() },
            onStoryReady = { nav.openCreatedStories() },
            onReminder = { nav.go(ReminderRoute) },
            onNewStories = { nav.navigateToTab(DiscoverRoute) },
        ),
    )
    subscriptionScreen(onBack = { nav.back() }, onDone = { nav.navigateToTab(HomeRoute) })
    languageScreen(onBack = { nav.back() })
    privacyScreen(
        onBack = { nav.back() },
        onDeleted = { nav.navigate(OnboardingRoute) { popUpTo(nav.graph.id) { inclusive = true } } },
    )
    helpScreen(onBack = { nav.back() })
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

/**
 * Taps navigate only while the current screen is resumed. A double tap, or a tap on a screen that
 * is still animating away, would otherwise open a screen twice or pop past where the child meant
 * to go. Navigation driven by events (generation finished, Setup done, data deleted) stays direct.
 */
private fun NavHostController.isResumed(): Boolean =
    currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED

private fun NavHostController.go(route: Any, builder: NavOptionsBuilder.() -> Unit = {}) {
    if (isResumed()) navigate(route, builder)
}

private fun NavHostController.back() {
    if (isResumed()) popBackStack()
}

/** Switching tabs keeps one copy of each tab on top of Home and restores its state. */
private fun NavHostController.navigateToTab(route: Any) {
    go(route) {
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
    go(MyStoriesRoute(LibraryTab.CreatedByMe)) { popUpTo<HomeRoute>() }
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
