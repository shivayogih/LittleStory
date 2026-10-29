package com.littlestory.core.designsystem.theme

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** LS-010: every colour in docs/design/design-tokens.json is a token, and only token files hold hex literals. */
class TokenCompletenessTest {
    private val moduleDir = File(System.getProperty("user.dir"))
    private val repoRoot =
        generateSequence(moduleDir) { it.parentFile }
            .first { File(it, "docs/design/design-tokens.json").exists() }
    private val tokenFile = File(moduleDir, "src/main/kotlin/com/littlestory/core/designsystem/theme/Color.kt")

    private fun jsonColors(): Set<String> {
        val root = Json.parseToJsonElement(File(repoRoot, "docs/design/design-tokens.json").readText()).jsonObject
        val found = mutableSetOf<String>()
        fun walk(element: JsonElement) {
            when (element) {
                is JsonPrimitive ->
                    if (element.isString &&
                        HEX.matches(element.content)
                    ) {
                        found += element.content.uppercase()
                    }
                is JsonObject -> element.values.forEach(::walk)
                is JsonArray -> element.forEach(::walk)
            }
        }
        walk(root.getValue("color"))
        return found
    }

    private val kotlinHex = Regex("""Color\(0xFF([0-9A-Fa-f]{6})\)""")

    @Test
    fun everyJsonColourIsDefinedInColorKt() {
        val defined = kotlinHex.findAll(tokenFile.readText()).map { "#" + it.groupValues[1].uppercase() }.toSet()
        val missing = jsonColors() - defined
        assertTrue(missing.isEmpty(), "design-tokens.json colours missing from Color.kt: $missing")
    }

    @Test
    fun noColourIsDefinedTwiceInThePalette() {
        val palette = tokenFile.readText().substringAfter("object Palette").substringBefore("/** A two-stop gradient")
        val values = kotlinHex.findAll(palette).map { it.groupValues[1].uppercase() }.toList()
        val duplicates =
            values
                .groupingBy { it }
                .eachCount()
                .filterValues { it > 1 }
                .keys
        assertEquals(emptySet(), duplicates, "duplicate palette values")
    }

    @Test
    fun hexLiteralsExistOnlyInTokenFiles() {
        val sources = File(moduleDir, "src").walkTopDown().filter { it.extension == "kt" && "/test/" !in it.path }
        val offenders =
            sources
                .filter { it.name != "Color.kt" && it.name != "Elevation.kt" }
                .filter { Regex("""Color\(0x""").containsMatchIn(it.readText()) }
                .map { it.name }
                .toList()
        assertEquals(emptyList(), offenders, "colour literals outside token files")
    }

    private companion object {
        val HEX = Regex("""#[0-9a-fA-F]{6}""")
    }
}
