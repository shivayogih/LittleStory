package com.littlestory.feature.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.littlestory.feature.home.home.HomeRoute as HomeScreenRoute

fun NavGraphBuilder.homeScreen() {
    composable<HomeRoute> { HomeScreenRoute() }
}
