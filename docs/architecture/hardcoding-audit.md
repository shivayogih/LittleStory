# Hardcoded values audit (LS-125)

Status: done for P7, 30 Oct 2026. Re-run before every release candidate.

Scope: Kotlin sources under `app/src/main`, `core/ui/src/main`, `feature/*/src/main`, plus spot checks of `:data`, `:domain` and `:core:common`. The policy being checked is the no-hardcoding table in `CLAUDE.md` and [configuration-and-constants.md](configuration-and-constants.md).

## Method

1. `./gradlew checkNoHardcoding` (the automated scanner in `build-logic`, LS-034) over every UI module.
2. Manual searches for what the scanner did not cover yet: numeric literals in feature code, named text arguments (`title = "…"`), language tags, URLs, `Log`/`println`, raw `Locale` use, and limits that belong in `AppConfig`.
3. Each finding was fixed or recorded as accepted below. Rules that found something were added to the scanner so it cannot come back.

## Findings

| # | Where | Finding | Resolution |
|---|---|---|---|
| H1 | `core/ui/.../StoryComponents.kt` | `maxLines = 2` on card titles | Named `TITLE_MAX_LINES` with the design reason |
| H2 | `core/ui/.../NavigationChrome.kt` | `maxLines = 2` on tab labels | Named `NAV_LABEL_MAX_LINES` (Kannada may wrap) |
| H3 | `feature/settings/.../SubscriptionScreen.kt` | `maxItemsInEachRow = 2` | Named `BENEFIT_COLUMNS` (SCR-15 grid) |
| H4 | `feature/create/.../GeneratingScreen.kt` | `repeat(2)` and `/ 2` for the book's pages | Named `BOOK_PAGES` |
| H5 | animation `label = "…"` strings | Debug names for the animation inspector | Accepted: not user-facing, never shown or read aloud |
| H6 | `feature/settings/.../HelpScreen.kt` font names | DM Sans, Manrope, Noto Sans Kannada | Accepted: proper names in a list, not translated |
| H7 | geometric halves (`size / 2`, `width / 2f`) | Centre points in drawing code | Accepted: arithmetic, not configuration |

Nothing else was found: no colour, dp, sp, duration, URL, language tag or user-facing string outside its home, and no logging.

## Scanner rules added

| Rule | Catches |
|---|---|
| hardcoded text argument | `title`, `subtitle`, `placeholder`, `message`, `body`, `confirmLabel`, `dismissLabel` given a string literal |
| hardcoded language tag | `Locale("…")`, `Locale.forLanguageTag("…")`; tags come from `SupportedLanguages` |
| hardcoded URL | `"http://…"`, `"https://…"`; URLs come from `BuildConfig` |
| magic layout count | `maxLines`, `minLines`, `maxItemsInEachRow`, `maxItemsInEachColumn` given a digit |

Tests for each rule are in `build-logic/convention/src/test/.../QualityCheckTest.kt` (`./gradlew -p build-logic test`).
