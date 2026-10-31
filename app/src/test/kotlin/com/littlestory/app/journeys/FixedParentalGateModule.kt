package com.littlestory.app.journeys

import com.littlestory.app.di.SafetyModule
import com.littlestory.core.common.safety.ParentalGateChallenge
import com.littlestory.core.common.safety.ParentalGateChallenges
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn

/** App tests always get the same gate question, so journeys can answer it ([JourneyTest.passParentalGate]). */
@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [SafetyModule::class])
object FixedParentalGateModule {
    val challenge = ParentalGateChallenge(12, 7)

    @Provides
    fun parentalGateChallenges(): ParentalGateChallenges = ParentalGateChallenges { challenge }
}
