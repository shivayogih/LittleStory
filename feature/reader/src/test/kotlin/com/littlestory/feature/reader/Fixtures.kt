package com.littlestory.feature.reader

import com.littlestory.domain.model.SleepTimer
import com.littlestory.feature.reader.player.PlayerUiState
import com.littlestory.feature.reader.reader.ReaderContent
import com.littlestory.feature.reader.reader.ReaderUiState

private val pages =
    listOf(
        "Once upon a time, in a village by the river, lived a little owl who was afraid of the dark.",
        "Every night the owl watched the stars and wished she could fly as far as the moon.",
        "One evening a firefly landed beside her and whispered, “The dark is just the sky resting.”",
    )

fun readerState(page: Int = 0, scale: Float = 1f) =
    ReaderUiState.Loaded(
        ReaderContent(
            id = "s1",
            title = "The Little Owl Who Loved the Moon",
            pages = pages,
            page = page,
            textScale = scale,
            paletteId = "bedtime",
            motifId = "moon",
            favourite = true,
        ),
    )

val playerState =
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
    )

val playerSleepSheet = playerState.copy(playing = false, sleepSheetOpen = true, sleepTimer = SleepTimer.Minutes(15))
