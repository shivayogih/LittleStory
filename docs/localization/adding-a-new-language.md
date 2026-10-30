# Adding a new language

Purpose: the complete checklist for adding a language (for example Hindi `hi`) after the MVP, without changing feature code.

Status: LS-048, updated 30 Oct 2026 against the implemented code on `development`. Paths below are real files; the dry run at the end lists what a new language touches.

Sources: brief §4.4 rule 6, §28 (add-a-language prompt) · [localization-strategy.md](localization-strategy.md) · `prompts/20_ADD_NEW_LANGUAGE_PROMPT.md`.

## Before you start

- [ ] The owner has approved the language for release.
- [ ] A fluent native reviewer is named.
- [ ] Script support checked: is a licensed font with this script's glyphs available (e.g. a Noto family)? See [kannada-typography.md](kannada-typography.md) for the pattern.
- [ ] Direction checked: if the script is right-to-left, run the `ar-XB` pseudo-locale screenshots first and fix any `left`/`right` usage.

## Steps

| # | Step | File(s) | Kind of change |
| --- | --- | --- | --- |
| 1 | Add `values-{tag}/strings.xml` (strings and plurals) next to every `values/strings.xml`. Today that is `app`, `core/ui` (main and debug), `core/designsystem` (debug), and the seven `feature/*` modules. `find . -path "*/res/values/strings.xml" -not -path "*/build/*"` lists them. | resources | translation |
| 2 | Add mock content with the same IDs: `stories_{tag}.json`, `categories_{tag}.json`, `generated_{tag}.json`, `notifications_{tag}.json`. A story may be left out on purpose; the loader falls back to English field by field. `plans.json` has no text. | `data/src/main/assets/mock/` | content |
| 3 | Add one entry to the registry: `SupportedLanguage(tag = "hi", nativeName = "हिन्दी")`, and append it to `all`. | `core/common/src/main/kotlin/com/littlestory/core/common/language/SupportedLanguage.kt` | config |
| 4 | Add `<locale android:name="{tag}"/>`. `LocalesConfigTest` fails until the registry and this file agree. | `app/src/main/res/xml/locales_config.xml` | config |
| 5 | Only for a new script: add a licensed font to `core/designsystem/src/main/res/font/`, a `TypographyScript` value and its typography set in `Type.kt`, and its mapping in `currentTypographyScript()` in `Theme.kt`. Latin-script languages need nothing here. | `:core:designsystem` | design system |
| 6 | Add a glossary column and the Translations-sheet rows, every one marked "Draft — needs native review". | `docs/localization/glossary.md`, workbook | docs |
| 7 | Add the locale to the screenshot matrix: a `@Preview(locale = "{tag}")` line in `LocalePreviews` and Roborazzi `@Config(qualifiers = "{tag}-…")` cases. Optionally add a `{Language}JourneysTest` beside the English and Kannada ones. | `app/src/screenshotTest/.../LocalePreviews.kt`, `*ScreenshotTest.kt`, `app/src/test/.../journeys/` | tests |

No feature module, ViewModel, repository or route changes. If any other file needs to change, stop: that is an architecture defect. Report it instead of patching feature code (brief §28 step 8).

## Dry run (30 Oct 2026)

A walk-through for Hindi (`hi`, Devanagari) found these touch points and nothing else:

- Steps 1–4 and 6–7 as listed.
- Step 5 applies, because Devanagari needs a font: Noto Sans Devanagari (OFL) and a `TypographyScript.Devanagari`.
- `checkTranslations` reads its tags from `locales_config.xml`, so it starts checking `hi` parity with no build change.
- The Language screen, Setup's language field and the Profile subtitle all read the registry, so ಕನ್ನಡ, English and हिन्दी appear with no UI change.

## Verify

- [ ] `checkTranslations` passes (key and argument parity).
- [ ] Lint passes with `MissingTranslation` as error.
- [ ] Registry ↔ `locales_config.xml` test passes.
- [ ] Mock-content parity test passes (IDs, page counts, declared fallbacks).
- [ ] Screenshots reviewed for every key screen in the new locale: no clipping, correct glyphs, correct line height.
- [ ] Language picker shows the new language in its own script, and switching persists after restart on Android 8–12 and 13+.
- [ ] TalkBack checked where a voice exists; limitation documented if not.
- [ ] Every new string marked "Needs native review" until the reviewer approves.

No push, PR, merge or release without the owner's authorization.
