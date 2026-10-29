package com.littlestory.app

import com.littlestory.core.common.language.SupportedLanguages
import java.io.File
import org.junit.Test
import kotlin.test.assertEquals

/** The registry is the only list of languages: locales_config.xml must mirror it (LS-040). */
class LocalesConfigTest {
    @Test
    fun localesConfigMatchesRegistry() {
        val xml = File("src/main/res/xml/locales_config.xml").readText()
        val tags = Regex("""android:name="([^"]+)"""").findAll(xml).map { it.groupValues[1] }.toList()
        assertEquals(SupportedLanguages.enabled.map { it.tag }, tags)
    }

    @Test
    fun everyEnabledLanguageHasAStringsFile() {
        SupportedLanguages.enabled.filter { it != SupportedLanguages.default }.forEach {
            assert(File("src/main/res/values-${it.tag}/strings.xml").exists()) { "values-${it.tag} missing in :app" }
        }
    }
}
