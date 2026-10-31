package com.littlestory.app.journeys

import com.littlestory.prototype.gemini.GeminiPrototypeModule
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn

/**
 * App tests never call Gemini or the phone voice: removing the debug prototype bindings leaves the
 * optional writer and narrator empty, so journeys run the deterministic simulations.
 */
@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [GeminiPrototypeModule::class])
object NoPrototypeModule
