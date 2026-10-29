package com.littlestory.core.ui.language

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.littlestory.core.common.language.SupportedLanguage
import com.littlestory.core.common.language.SupportedLanguages

/**
 * Per-app language switching (ADR-006). AppCompat applies the locale to every activity without a
 * restart and persists it (platform storage on Android 13+, AppLocalesMetadataHolderService below).
 */
object AppLanguage {
    fun apply(language: SupportedLanguage) {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(language.tag))
    }

    /** The language chosen in the app, or the default when the system locale is in use. */
    fun current(): SupportedLanguage {
        val locales = AppCompatDelegate.getApplicationLocales()
        return if (locales.isEmpty) SupportedLanguages.default else SupportedLanguages.fromTag(locales.toLanguageTags())
    }
}
