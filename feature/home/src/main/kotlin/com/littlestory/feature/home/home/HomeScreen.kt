package com.littlestory.feature.home.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.littlestory.core.designsystem.theme.LittleStoryTheme
import com.littlestory.core.ui.chrome.TopBar
import com.littlestory.core.ui.system.SystemBarIcons
import com.littlestory.core.ui.text.asString

object HomeTags {
    const val TITLE = "home_title"
}

/** Home header only for now; the hero, continue card and story rows arrive with the P4 tickets. */
@Composable
fun HomeRoute(viewModel: HomeViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    HomeScreen(state)
}

@Composable
fun HomeScreen(state: HomeUiState, modifier: Modifier = Modifier) {
    val colors = LittleStoryTheme.colors
    SystemBarIcons(darkIcons = true)
    Column(modifier.fillMaxSize().background(colors.background)) {
        TopBar()
        Column(Modifier.padding(horizontal = LittleStoryTheme.spacing.screenHorizontal)) {
            Text(state.greeting.asString(), style = LittleStoryTheme.typography.chip, color = colors.textSecondary)
            Text(
                state.title.asString(),
                style = LittleStoryTheme.typography.homeTitle,
                color = colors.textPrimary,
                modifier = Modifier.testTag(HomeTags.TITLE),
            )
        }
    }
}
