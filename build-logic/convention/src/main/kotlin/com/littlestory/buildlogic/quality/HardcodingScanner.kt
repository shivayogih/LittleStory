package com.littlestory.buildlogic.quality

import java.io.File

/**
 * Finds hardcoded values in feature/app Kotlin sources (LS-034, CLAUDE.md no-hardcoding policy).
 * Design values live in :core:designsystem, text in string resources, limits in AppConfig.
 */
internal object HardcodingScanner {
    private data class Rule(val message: String, val pattern: Regex)

    private val rules = listOf(
        Rule("raw colour literal; use a token from LittleStoryTheme.colors", Regex("""Color\(\s*0x""")),
        Rule("raw dp value; use LittleStoryTheme.spacing/dimens", Regex("""\b\d+(\.\d+)?\s*\.dp\b""")),
        Rule("raw sp value; use LittleStoryTheme.typography", Regex("""\b\d+(\.\d+)?\s*\.sp\b""")),
        Rule("inline TextStyle; use LittleStoryTheme.typography", Regex("""\bTextStyle\(""")),
        Rule("hardcoded text in Text(); use stringResource or UiText", Regex("""\bText\(\s*(text\s*=\s*)?"[^"]""")),
        Rule("hardcoded contentDescription; use stringResource", Regex("""contentDescription\s*=\s*"[^"]""")),
        Rule("hardcoded duration or delay; use AppConfig or motion tokens", Regex("""\b(delay|tween)\(\s*\d{2,}""")),
        // Added by the LS-125 audit; animation `label = "…"` strings are debug names and stay allowed.
        Rule(
            "hardcoded text argument; use stringResource or UiText",
            Regex("""\b(title|subtitle|placeholder|message|body|confirmLabel|dismissLabel)\s*=\s*"[^"]"""),
        ),
        Rule("hardcoded language tag; use the SupportedLanguages registry", Regex("""(forLanguageTag|Locale)\(\s*"""")),
        // Lines are cut at "//" before matching, so the URL rule stops at the scheme.
        Rule("hardcoded URL; use BuildConfig", Regex(""""https?:""")),
        Rule("magic layout count; use a named constant", Regex("""\b(maxLines|minLines|maxItemsInEachRow|maxItemsInEachColumn)\s*=\s*\d""")),
    )

    fun scan(files: Sequence<File>): List<String> = buildList {
        for (file in files) {
            file.readLines().forEachIndexed { index, line ->
                val code = line.substringBefore("//")
                for (rule in rules) {
                    if (rule.pattern.containsMatchIn(code)) add("${file.path}:${index + 1}: ${rule.message}")
                }
            }
        }
    }
}
