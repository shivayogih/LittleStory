# Adding a new language

Purpose: the complete checklist for adding a language (for example Hindi `hi`) after the MVP, without changing feature code.

Status: Draft for owner review — P1, 29 Oct 2026. Applies once the localization foundation (LS-040, LS-041) is merged.

Sources: brief §4.4 rule 6, §28 (add-a-language prompt) · [localization-strategy.md](localization-strategy.md) · `prompts/20_ADD_NEW_LANGUAGE_PROMPT.md`.

## Before you start

- [ ] The owner has approved the language for release.
- [ ] A fluent native reviewer is named.
- [ ] Script support checked: is a licensed font with this script's glyphs available (e.g. a Noto family)? See [kannada-typography.md](kannada-typography.md) for the pattern.
- [ ] Direction checked: if the script is right-to-left, run the `ar-XB` pseudo-locale screenshots first and fix any `left`/`right` usage.

## Steps

| # | Step | File(s) | Feature code touched? |
| --- | --- | --- | --- |
| 1 | Add `values-{tag}/strings.xml` (and `plurals`) to every module that has `values/strings.xml` | `:app`, `:core:ui`, each `:feature:*` | no |
| 2 | Add localized mock content keyed by the same story IDs, or list stories that intentionally fall back to English | `:data/src/main/assets/mock/stories_{tag}.json`, `notifications_{tag}.json` | no |
| 3 | Add one registry entry (`tag`, native name in its own script, `enabled = true`) | `SupportedLanguage` in `:core:common` | no (config) |
| 4 | Add the locale to `res/xml/locales_config.xml` | `:app` | no |
| 5 | Add the font fallback for the script if needed | `:core:designsystem` font families | no (design system) |
| 6 | Add glossary column and translation tracking rows | `docs/localization/glossary.md`, workbook Translations sheet | no |
| 7 | Add the locale to the screenshot test matrix | test config | no |

If any other file needs to change, stop: that is an architecture defect. Report it instead of patching feature code (brief §28 step 8).

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
