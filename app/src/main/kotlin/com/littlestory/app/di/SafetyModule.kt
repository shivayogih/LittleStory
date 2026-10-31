package com.littlestory.app.di

import com.littlestory.core.common.safety.ParentalGateChallenges
import com.littlestory.core.common.safety.RandomParentalGateChallenges
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/** Parental-gate questions; app tests replace this module with fixed questions. */
@Module
@InstallIn(SingletonComponent::class)
object SafetyModule {
    @Provides
    fun parentalGateChallenges(): ParentalGateChallenges = RandomParentalGateChallenges()
}
