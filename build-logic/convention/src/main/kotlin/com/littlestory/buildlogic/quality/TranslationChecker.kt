package com.littlestory.buildlogic.quality

import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Element

/** One translatable resource: its placeholder signature per item (a string has one, a plural one per quantity). */
internal data class ResourceEntry(val key: String, val signatures: Map<String, List<String>>)

/**
 * Checks values-xx/strings.xml against values/strings.xml (LS-041): every translatable key is
 * present, no orphan keys exist, and format arguments (%1$s, %2$d …) match per string and plural item.
 */
internal object TranslationChecker {
    /** Returns human-readable problems; empty means the module is consistent. */
    fun check(resDir: File, tags: List<String>): List<String> {
        val base = File(resDir, "values/strings.xml")
        if (!base.exists()) return emptyList()
        val source = parse(base)
        val problems = mutableListOf<String>()
        for (tag in tags) {
            val file = File(resDir, "values-$tag/strings.xml")
            if (!file.exists()) {
                if (source.isNotEmpty()) problems += "${rel(file)}: missing file for language '$tag'"
                continue
            }
            val target = parse(file)
            for ((key, entry) in source) {
                val translated = target[key]
                if (translated == null) {
                    problems += "${rel(file)}: missing translation for '$key'"
                    continue
                }
                if (translated.signatures.keys.isNotEmpty() && entry.signatures.keys == setOf(SINGLE) &&
                    translated.signatures.keys != setOf(SINGLE)
                ) {
                    problems += "${rel(file)}: '$key' is a string in values/ but a plural in values-$tag"
                    continue
                }
                val reference = entry.signatures.values.first()
                for ((item, signature) in translated.signatures) {
                    val expected = entry.signatures[item] ?: reference
                    if (signature != expected) {
                        problems += "${rel(file)}: format arguments of '$key'${itemSuffix(item)} are $signature, expected $expected"
                    }
                }
            }
            for (key in target.keys - source.keys) {
                problems += "${rel(file)}: '$key' has no counterpart in values/strings.xml"
            }
        }
        return problems
    }

    private fun itemSuffix(item: String) = if (item == SINGLE) "" else " ($item)"

    private fun rel(file: File) = file.path.substringAfter("/LittleStory/", file.path)

    internal fun parse(file: File): Map<String, ResourceEntry> {
        val factory = DocumentBuilderFactory.newInstance().apply { isNamespaceAware = false }
        val root = factory.newDocumentBuilder().parse(file).documentElement
        val result = linkedMapOf<String, ResourceEntry>()
        val nodes = root.childNodes
        for (i in 0 until nodes.length) {
            val element = nodes.item(i) as? Element ?: continue
            if (element.getAttribute("translatable") == "false") continue
            val key = element.getAttribute("name")
            when (element.tagName) {
                // formatted="false" strings are literal text (e.g. "42%"): no format arguments to compare.
                "string" -> result[key] = ResourceEntry(
                    key,
                    mapOf(SINGLE to if (element.getAttribute("formatted") == "false") emptyList() else signature(element.textContent)),
                )
                "plurals" -> {
                    val items = element.getElementsByTagName("item")
                    val map = linkedMapOf<String, List<String>>()
                    for (j in 0 until items.length) {
                        val item = items.item(j) as Element
                        map[item.getAttribute("quantity")] = signature(item.textContent)
                    }
                    result[key] = ResourceEntry(key, map)
                }
            }
        }
        return result
    }

    private val specifier = Regex("""%(?:(\d+)\$)?[-#+ 0,(]*\d*(?:\.\d+)?([a-zA-Z])""")

    /** Sorted "index:conversion" pairs; unnumbered specifiers are numbered by order of appearance. */
    internal fun signature(text: String): List<String> {
        var next = 1
        return specifier.findAll(text.replace("%%", ""))
            .map { match ->
                val index = match.groupValues[1].ifEmpty { (next++).toString() }
                "$index:${match.groupValues[2]}"
            }
            .sorted()
            .toList()
    }

    private const val SINGLE = "string"
}
