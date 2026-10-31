package com.littlestory.prototype.gemini

import com.littlestory.domain.generation.StoryWriter
import com.littlestory.domain.player.Narrator
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/** Binds the Gemini writer and the phone-voice narrator; present only in debug builds, so release keeps the simulations. */
@Module
@InstallIn(SingletonComponent::class)
abstract class GeminiPrototypeModule {
    @Binds
    abstract fun storyWriter(writer: GeminiStoryWriter): StoryWriter

    @Binds
    abstract fun narrator(narrator: DeviceNarrator): Narrator
}
