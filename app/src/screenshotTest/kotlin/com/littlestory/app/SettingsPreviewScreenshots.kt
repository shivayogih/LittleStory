package com.littlestory.app

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import com.littlestory.core.designsystem.theme.LittleStoryTheme
import com.littlestory.feature.settings.language.LanguageCallbacks
import com.littlestory.feature.settings.language.LanguageScreen
import com.littlestory.feature.settings.language.LanguageUiState
import com.littlestory.feature.settings.profile.ProfileActions
import com.littlestory.feature.settings.profile.ProfileCallbacks
import com.littlestory.feature.settings.profile.ProfileScreen
import com.littlestory.feature.settings.profile.ProfileUiState
import java.time.LocalTime

@PreviewTest
@LocalePreviews
@Composable
fun ProfilePreview() {
    LittleStoryTheme {
        ProfileScreen(
            ProfileUiState(
                activeChildId = "child_1",
                activeChildName = "Aarav",
                avatarTone = 1,
                childCount = 2,
                reminderEnabled = true,
                reminderTime = LocalTime.of(20, 0),
            ),
            ProfileActions(),
            ProfileCallbacks(),
        )
    }
}

@PreviewTest
@LocalePreviews
@Composable
fun LanguagePreview() {
    LittleStoryTheme {
        LanguageScreen(LanguageUiState(appLanguageTag = "en", storyLanguageTag = "kn"), LanguageCallbacks())
    }
}
