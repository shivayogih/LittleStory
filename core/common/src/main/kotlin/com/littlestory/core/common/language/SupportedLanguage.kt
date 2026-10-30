package com.littlestory.core.common.language

/** A language the app can be shown in. */
data class SupportedLanguage(
    /** BCP 47 tag, also the suffix of `assets/mock/stories_{tag}.json`. */
    val tag: String,
    /** Name in the language itself, shown in the picker. */
    val nativeName: String,
    val enabled: Boolean = true,
)

/**
 * The only list of supported languages. Adding one: add an entry here, an entry in
 * res/xml/locales_config.xml, and values-xx resources (docs/localization/adding-a-new-language.md).
 */
object SupportedLanguages {
    val English = SupportedLanguage(tag = "en", nativeName = "English")
    val Kannada = SupportedLanguage(tag = "kn", nativeName = "ಕನ್ನಡ")

    val all: List<SupportedLanguage> = listOf(English, Kannada)
    val enabled: List<SupportedLanguage> get() = all.filter { it.enabled }

    /** English is the default and the fallback for missing translations. */
    val default: SupportedLanguage = English

    /** Resolves a tag such as "kn-IN" to a supported language, or the default. */
    fun fromTag(tag: String?): SupportedLanguage {
        val language = tag?.substringBefore('-')?.substringBefore('_')?.lowercase()
        return enabled.firstOrNull { it.tag == language } ?: default
    }
}
