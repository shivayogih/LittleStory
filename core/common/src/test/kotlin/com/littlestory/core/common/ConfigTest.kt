package com.littlestory.core.common

import com.littlestory.core.common.config.AppConfig
import com.littlestory.core.common.config.FeatureFlags
import com.littlestory.core.common.language.SupportedLanguages
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ConfigTest {
    @Test
    fun configDefaultsMatchPrototype() {
        assertEquals(500, AppConfig.Create.PROMPT_MAX_CHARS)
        assertEquals(3..12, AppConfig.Setup.ageRange)
        assertEquals(4, AppConfig.Generation.STEP_PERCENT)
        assertEquals(120L, AppConfig.Generation.STEP_INTERVAL_MS)
        assertEquals(15, AppConfig.Player.SKIP_SECONDS)
        assertEquals(1_000_000_000L, AppConfig.Downloads.SIMULATED_QUOTA_BYTES)
    }

    @Test
    fun defaultsAreAmongTheOptions() {
        assertTrue(AppConfig.Create.DEFAULT_DURATION_MINUTES in AppConfig.Create.durationOptionsMinutes)
        assertTrue(AppConfig.Setup.DEFAULT_DURATION_MINUTES in AppConfig.Setup.durationOptionsMinutes)
        assertTrue(AppConfig.Setup.DEFAULT_AGE in AppConfig.Setup.ageRange)
        assertTrue(AppConfig.Player.DEFAULT_SPEED in AppConfig.Player.speedOptions)
        assertTrue(AppConfig.Create.DEFAULT_THEME in AppConfig.Create.themes)
        assertTrue(AppConfig.Create.DEFAULT_MOOD in AppConfig.Create.moods)
    }

    @Test
    fun simulatedFeaturesAreOnByDefaultAndPseudoLocalesOff() {
        val flags = FeatureFlags()
        assertTrue(flags.simulatedNarration && flags.simulatedDownloads && flags.demoSubscription)
        assertEquals(false, flags.pseudoLocales)
    }

    @Test
    fun registryListsEnglishAndKannadaWithNativeNames() {
        assertEquals(listOf("en", "kn"), SupportedLanguages.all.map { it.tag })
        assertEquals(listOf("English", "ಕನ್ನಡ"), SupportedLanguages.all.map { it.nativeName })
    }

    @Test
    fun unknownTagFallsBackToEnglish() {
        assertEquals("kn", SupportedLanguages.fromTag("kn-IN").tag)
        assertEquals("en", SupportedLanguages.fromTag("fr").tag)
        assertEquals("en", SupportedLanguages.fromTag(null).tag)
    }
}
