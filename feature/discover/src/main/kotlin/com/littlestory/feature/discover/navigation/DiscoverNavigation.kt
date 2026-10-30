package com.littlestory.feature.discover.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.littlestory.feature.discover.category.CategoryRoute as CategoryScreenRoute
import com.littlestory.feature.discover.detail.DetailActions
import com.littlestory.feature.discover.detail.StoryDetailRoute as StoryDetailScreenRoute
import com.littlestory.feature.discover.discover.DiscoverActions
import com.littlestory.feature.discover.discover.DiscoverRoute as DiscoverScreenRoute

fun NavGraphBuilder.discoverScreen(actions: DiscoverActions) {
    composable<DiscoverRoute> { DiscoverScreenRoute(actions) }
}

fun NavGraphBuilder.categoryScreen(onBack: () -> Unit, onStory: (String) -> Unit) {
    composable<CategoryRoute> { CategoryScreenRoute(onBack, onStory) }
}

fun NavGraphBuilder.storyDetailScreen(actions: DetailActions) {
    composable<StoryDetailRoute> { StoryDetailScreenRoute(actions) }
}
