package com.littlestory.feature.reader.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.littlestory.feature.reader.player.PlayerRoute as PlayerScreenRoute
import com.littlestory.feature.reader.reader.ReaderRoute as ReaderScreenRoute

fun NavGraphBuilder.readerScreen(onBack: () -> Unit, onPlay: (storyId: String) -> Unit) {
    composable<ReaderRoute> { ReaderScreenRoute(onBack, onPlay) }
}

fun NavGraphBuilder.playerScreen(onBack: () -> Unit, onStoryText: (storyId: String) -> Unit) {
    composable<PlayerRoute> { PlayerScreenRoute(onBack, onStoryText) }
}
