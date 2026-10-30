package com.littlestory.feature.create.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.littlestory.feature.create.ready.ReadyActions
import com.littlestory.feature.create.form.CreateRoute as CreateScreenRoute
import com.littlestory.feature.create.generating.GeneratingRoute as GeneratingScreenRoute
import com.littlestory.feature.create.ready.StoryReadyRoute as StoryReadyScreenRoute

fun NavGraphBuilder.createScreen(onGenerate: (requestId: String) -> Unit) {
    composable<CreateRoute> { CreateScreenRoute(onGenerate) }
}

fun NavGraphBuilder.generatingScreen(onReady: (storyId: String) -> Unit, onBackToIdea: () -> Unit) {
    composable<GeneratingRoute> { GeneratingScreenRoute(onReady, onBackToIdea) }
}

fun NavGraphBuilder.storyReadyScreen(actions: ReadyActions) {
    composable<StoryReadyRoute> { StoryReadyScreenRoute(actions) }
}
