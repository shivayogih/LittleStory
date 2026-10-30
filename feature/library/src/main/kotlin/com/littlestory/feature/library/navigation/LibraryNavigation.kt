package com.littlestory.feature.library.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.littlestory.feature.library.stories.MyStoriesActions
import com.littlestory.feature.library.downloads.DownloadsRoute as DownloadsScreenRoute
import com.littlestory.feature.library.stories.MyStoriesRoute as MyStoriesScreenRoute

fun NavGraphBuilder.myStoriesScreen(actions: MyStoriesActions) {
    composable<MyStoriesRoute> { MyStoriesScreenRoute(actions) }
}

fun NavGraphBuilder.downloadsScreen(onBack: () -> Unit, onFindStory: () -> Unit) {
    composable<DownloadsRoute> { DownloadsScreenRoute(onBack, onFindStory) }
}
