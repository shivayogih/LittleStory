package com.littlestory.feature.home

import com.littlestory.core.ui.story.StoryItem
import com.littlestory.core.ui.text.UiText
import com.littlestory.feature.home.home.CategoryTileUi
import com.littlestory.feature.home.home.ContinueUi
import com.littlestory.feature.home.home.HomeUiState

private fun item(id: String, title: String, category: String, palette: String, motif: String, minutes: Int, fav: Boolean = false) =
    StoryItem(id, title, category, minutes, 4, 7, palette, motif, fav, downloaded = false)

/** The populated Home of the design: child Aarav with a story at 42 %. */
val populatedHome =
    HomeUiState(
        greeting = UiText.Res(R.string.home_greeting),
        title = UiText.Res(R.string.home_title_for_child, listOf("Aarav")),
        madeForTitle = UiText.Res(R.string.home_made_for_child, listOf("Aarav")),
        avatarTone = 0,
        continueListening =
            ContinueUi(item("story_magical_cricket_bat", "Aarav and the Magical Cricket Bat", "Adventure", "adventure", "cricket", 8), 42),
        categories =
            listOf(
                CategoryTileUi("bedtime", "Bedtime"),
                CategoryTileUi("animals", "Animals"),
                CategoryTileUi("adventure", "Adventure"),
                CategoryTileUi("friendship", "Friendship"),
                CategoryTileUi("indian_tales", "Indian Tales"),
            ),
        madeFor =
            listOf(
                item("story_little_elephant", "The Little Elephant Who Shared", "Friendship", "friendship", "elephant", 6, fav = true),
                item("story_magical_cricket_bat", "Aarav and the Magical Cricket Bat", "Adventure", "adventure", "cricket", 8),
                item("story_moonlit_garden", "Mira and the Moonlit Garden", "Bedtime", "bedtime", "moon", 7),
            ),
    )

/** First run with nothing started: no continue card. */
val emptyContinueHome = populatedHome.copy(continueListening = null)
