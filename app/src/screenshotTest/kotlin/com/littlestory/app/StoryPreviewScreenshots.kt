package com.littlestory.app

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import com.littlestory.core.designsystem.theme.LittleStoryTheme
import com.littlestory.core.ui.story.StoryItem
import com.littlestory.core.ui.text.UiText
import com.littlestory.feature.home.R
import com.littlestory.feature.home.home.CategoryTileUi
import com.littlestory.feature.home.home.ContinueUi
import com.littlestory.feature.home.home.HomeActions
import com.littlestory.feature.home.home.HomeScreen
import com.littlestory.feature.home.home.HomeUiState
import com.littlestory.feature.reader.player.PlayerCallbacks
import com.littlestory.feature.reader.player.PlayerScreen
import com.littlestory.feature.reader.player.PlayerUiState
import com.littlestory.feature.reader.reader.ReaderCallbacks
import com.littlestory.feature.reader.reader.ReaderContent
import com.littlestory.feature.reader.reader.ReaderScreen
import com.littlestory.feature.reader.reader.ReaderUiState

// Story titles and text are mock content; the screen chrome around them is localized.
private val cricketBat =
    StoryItem("story_magical_cricket_bat", "Aarav and the Magical Cricket Bat", "Adventure", 8, 4, 7, "adventure", "cricket", false, false)
private val elephant =
    StoryItem("story_little_elephant", "The Little Elephant Who Shared", "Friendship", 6, 4, 7, "friendship", "elephant", true, false)

@PreviewTest
@LocalePreviews
@Composable
fun HomePreview() {
    LittleStoryTheme {
        HomeScreen(
            HomeUiState(
                greeting = UiText.Res(R.string.home_greeting),
                title = UiText.Res(R.string.home_title_for_child, listOf("Aarav")),
                madeForTitle = UiText.Res(R.string.home_made_for_child, listOf("Aarav")),
                avatarTone = 0,
                continueListening = ContinueUi(cricketBat, 42),
                categories =
                    listOf(
                        CategoryTileUi("bedtime", "Bedtime"),
                        CategoryTileUi("animals", "Animals"),
                        CategoryTileUi("adventure", "Adventure"),
                        CategoryTileUi("friendship", "Friendship"),
                    ),
                madeFor = listOf(elephant, cricketBat),
            ),
            HomeActions(),
            onFavouriteClick = {},
        )
    }
}

@PreviewTest
@LocalePreviews
@Composable
fun ReaderPreview() {
    LittleStoryTheme {
        ReaderScreen(
            ReaderUiState.Loaded(
                ReaderContent(
                    id = "s1",
                    title = "The Little Owl Who Loved the Moon",
                    pages =
                        listOf(
                            "Once upon a time, in a village by the river, lived a little owl who was afraid of the dark.",
                            "Every night the owl watched the stars and wished she could fly as far as the moon.",
                        ),
                    page = 0,
                    textScale = 1f,
                    paletteId = "bedtime",
                    motifId = "moon",
                    favourite = true,
                ),
            ),
            ReaderCallbacks(),
        )
    }
}

@PreviewTest
@LocalePreviews
@Composable
fun PlayerPreview() {
    LittleStoryTheme {
        PlayerScreen(
            PlayerUiState(
                loading = false,
                storyId = "s1",
                title = "The Little Owl Who Loved the Moon",
                heroName = "Aarav",
                languageName = "English",
                paletteId = "bedtime",
                motifId = "moon",
                durationMinutes = 8,
                chapters = 3,
                position = 0.42f,
                playing = true,
            ),
            PlayerCallbacks(),
        )
    }
}
