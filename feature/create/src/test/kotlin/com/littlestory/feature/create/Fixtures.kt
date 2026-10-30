package com.littlestory.feature.create

import com.littlestory.feature.create.form.CreateUiState
import com.littlestory.feature.create.form.HeroOption
import com.littlestory.feature.create.generating.GeneratingUiState
import com.littlestory.feature.create.ready.StoryReadyContent
import com.littlestory.feature.create.ready.StoryReadyUiState

val createDefault =
    CreateUiState(
        prompt = "Today Aarav played cricket with his friends and hit his first six.",
        heroes = listOf(HeroOption("child_1", "Aarav", 6, 1), HeroOption("child_2", "Meera", 4, 3)),
        heroId = "child_1",
    )

val createExpanded = createDefault.copy(moreOptionsExpanded = true)

val createEmptyError = createDefault.copy(prompt = "", showEmptyError = true)

val generatingHalfway = GeneratingUiState(heroName = "Aarav", percent = 52)

val generatingPaused = GeneratingUiState(heroName = "Aarav", percent = 52, paused = true)

val readyLoaded =
    StoryReadyUiState.Loaded(
        StoryReadyContent(
            id = "generated_1",
            title = "Aarav and the Magical Cricket Bat",
            heroName = "Aarav",
            languageName = "English",
            durationMinutes = 10,
            ageMin = 5,
            ageMax = 9,
            synopsis = "After a sunny day of cricket, Aarav finds a bat that glows when he is kind.",
            peek = "The bat hummed softly, as if it knew a secret only Aarav could hear.",
            paletteId = "adventure",
            motifId = "cricket",
            favourite = false,
            downloaded = false,
        ),
    )
