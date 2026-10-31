package com.littlestory.core.ui.chrome

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.littlestory.core.common.config.AppConfig
import com.littlestory.core.designsystem.component.LittleStoryLoading
import com.littlestory.core.designsystem.theme.LittleStoryTheme
import com.littlestory.core.ui.R

/**
 * The app's loading spinner, announced as "Loading…" and shown only if loading takes a moment.
 * [onNight] picks the colours for the dark Player and Generating backgrounds.
 */
@Composable
fun LoadingIndicator(modifier: Modifier = Modifier, onNight: Boolean = false) {
    LittleStoryLoading(
        contentDescription = stringResource(R.string.state_loading),
        delayMillis = AppConfig.Ui.LOADING_INDICATOR_DELAY_MS,
        modifier = modifier.testTag(ChromeTags.LOADING),
        onNight = onNight,
    )
}

/** A whole screen that is still loading: the screen background with the spinner in the middle. */
@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(
        modifier.fillMaxSize().background(LittleStoryTheme.colors.background),
        contentAlignment = Alignment.Center,
    ) { LoadingIndicator() }
}
