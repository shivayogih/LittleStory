package com.littlestory.app.di

import com.littlestory.app.BuildConfig
import com.littlestory.core.common.config.FeatureFlags
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    /** Pseudo-locales are offered only in debug builds; everything else uses the defaults. */
    @Provides
    @Singleton
    fun featureFlags(): FeatureFlags = FeatureFlags(pseudoLocales = BuildConfig.DEBUG)
}
