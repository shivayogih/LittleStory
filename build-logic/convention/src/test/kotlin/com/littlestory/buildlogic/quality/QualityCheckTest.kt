package com.littlestory.buildlogic.quality

import java.io.File
import java.nio.file.Files
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Negative tests for LS-034 and LS-041: the checks must fail on bad input and pass on good input. */
class QualityCheckTest {
    private fun res(en: String, kn: String): File {
        val dir = Files.createTempDirectory("res").toFile()
        File(dir, "values").mkdirs()
        File(dir, "values-kn").mkdirs()
        File(dir, "values/strings.xml").writeText("<resources>$en</resources>")
        File(dir, "values-kn/strings.xml").writeText("<resources>$kn</resources>")
        return dir
    }

    @Test
    fun matchingTranslationsPass() {
        val dir = res(
            """<string name="a">Hi %1${'$'}s, %2${'$'}d</string><string name="b" translatable="false">Brand</string>""",
            """<string name="a">ನಮಸ್ಕಾರ %2${'$'}d %1${'$'}s</string>""",
        )
        assertEquals(emptyList(), TranslationChecker.check(dir, listOf("kn")))
    }

    @Test
    fun unformattedStringsWithPercentAreNotTreatedAsFormats() {
        val dir = res(
            "<string name=\"a\" formatted=\"false\">42% done</string>",
            "<string name=\"a\" formatted=\"false\">42% ಪೂರ್ಣ</string>",
        )
        assertEquals(emptyList(), TranslationChecker.check(dir, listOf("kn")))
    }

    @Test
    fun missingKannadaKeyFails() {
        val dir = res("""<string name="a">A</string><string name="b">B</string>""", """<string name="a">ಎ</string>""")
        val problems = TranslationChecker.check(dir, listOf("kn"))
        assertTrue(problems.single().contains("missing translation for 'b'"), problems.toString())
    }

    @Test
    fun mismatchedFormatArgumentFails() {
        val dir = res("""<string name="a">Hi %1${'$'}s</string>""", """<string name="a">ನಮಸ್ಕಾರ %1${'$'}d</string>""")
        assertTrue(TranslationChecker.check(dir, listOf("kn")).single().contains("format arguments of 'a'"))
    }

    @Test
    fun orphanKannadaKeyFails() {
        val dir = res("""<string name="a">A</string>""", """<string name="a">ಎ</string><string name="z">ಝ</string>""")
        assertTrue(TranslationChecker.check(dir, listOf("kn")).single().contains("'z' has no counterpart"))
    }

    @Test
    fun pluralItemsAreCheckedPerQuantity() {
        val dir = res(
            """<plurals name="p"><item quantity="one">%1${'$'}d story</item><item quantity="other">%1${'$'}d stories</item></plurals>""",
            """<plurals name="p"><item quantity="one">%1${'$'}d ಕಥೆ</item><item quantity="other">ಕಥೆಗಳು</item></plurals>""",
        )
        assertTrue(TranslationChecker.check(dir, listOf("kn")).single().contains("(other)"))
    }

    private fun scan(code: String): List<String> {
        val file = Files.createTempFile("Screen", ".kt").toFile().apply { writeText(code) }
        return HardcodingScanner.scan(sequenceOf(file))
    }

    @Test
    fun hardcodedTextColourAndDimensionFail() {
        assertTrue(scan("""Text("Hello")""").single().contains("hardcoded text"))
        assertTrue(scan("""val c = Color(0xFF112233)""").single().contains("colour"))
        assertTrue(scan("""Modifier.padding(16.dp)""").single().contains("dp"))
        assertTrue(scan("""Text(text = "Hi")""").isNotEmpty())
    }

    @Test
    fun auditRulesCatchTextArgumentsTagsUrlsAndLayoutCounts() {
        assertTrue(scan("""TopBar(title = "Settings")""").single().contains("text argument"))
        assertTrue(scan("""Locale.forLanguageTag("kn")""").single().contains("language tag"))
        assertTrue(scan("""val api = "https://example.com"""").single().contains("URL"))
        assertTrue(scan("""Text(name, maxLines = 2)""").single().contains("layout count"))
    }

    @Test
    fun animationLabelsAndNamedCountsPass() {
        assertEquals(emptyList(), scan("""rememberInfiniteTransition(label = "twinkle")"""))
        assertEquals(emptyList(), scan("""Text(name, maxLines = TITLE_MAX_LINES)"""))
    }

    @Test
    fun tokensAndResourcesPass() {
        assertEquals(emptyList(), scan("""Text(text = stringResource(R.string.a), style = LittleStoryTheme.typography.chip)"""))
        assertEquals(emptyList(), scan("""Modifier.padding(LittleStoryTheme.spacing.lg) // 16.dp"""))
    }
}
