# How to change X

Purpose: step-by-step recipes for the most common changes, each touching one documented home (brief §4.5, LS-150).

Status: Draft for owner review — P1, 29 Oct 2026. Paths are the proposed P2 layout and will be confirmed when LS-030/LS-031 land.

Sources: [configuration-and-constants.md](configuration-and-constants.md) · [module-structure.md](module-structure.md) · [../localization/adding-a-new-language.md](../localization/adding-a-new-language.md).

Every recipe ends with the same verification: `./gradlew lint test` (macOS/Linux) or `gradlew.bat lint test` (Windows), plus the screenshot task for affected screens in EN and KN. Record the result in the PR.

## Change a colour

1. Edit the token in `:core:designsystem` (`LittleStoryColors`), not in any screen.
2. If the role is new (not just a new value), add a semantic token and use it from the component.
3. Check contrast for text on that colour (4.5:1 for body text, 3:1 for large text and icons).
4. Re-record screenshot baselines only after reviewing the diffs; update `docs/design/design-system.md`.

## Change or add a string

1. Find the owning module (prefix table in [module-structure.md](module-structure.md#4-string-ownership)).
2. Edit `values/strings.xml` **and** `values-kn/strings.xml` in that module. New Kannada text is marked `<!-- Needs native review -->` and tracked in the workbook Translations sheet.
3. Use positional arguments (`%1$s`) or a `<plurals>` entry; never concatenate.
4. `checkTranslations` (LS-041) fails if a key or argument is missing in any `values-*`.

## Change spacing or a size

1. Edit `LittleStorySpacing` / `LittleStoryDimens` / `LittleStoryShapes` in `:core:designsystem`.
2. Never add a `dp` literal in a feature module; add a token if none fits.
3. Check Kannada layouts and 200% font scale for clipping.

## Add or edit a mock story

1. Add the story to `:data/src/main/assets/mock/stories_en.json` with a new stable ID, category, ages, duration and art palette/motif.
2. Add the translated text fields to `stories_kn.json` under the same ID (or leave it out to use English fallback — the test lists it as an expected fallback).
3. Keep page counts equal across languages.
4. Run the mock-content test (IDs, page counts, required fields).

## Turn a feature on or off

1. Change the default in `FeatureFlags` (`:core:common`).
2. Features read the flag through the injected `FeatureFlags`; nothing else changes.
3. Tests override flags through Hilt test modules.

## Change a limit or option list

1. Edit the value in `AppConfig` (e.g. `Create.promptMaxChars`).
2. The validator, the counter and the error message all read the same value; no other edit is needed.
3. Update the row in [configuration-and-constants.md](configuration-and-constants.md).

## Add a language

Follow [../localization/adding-a-new-language.md](../localization/adding-a-new-language.md). It requires resources, mock content, one registry entry and one `locales_config.xml` line — no feature code.

## Add a screen

1. Choose the owning feature module (or propose a new one in an ADR).
2. Add a typed `@Serializable` route and a `NavGraphBuilder` extension in that module's `navigation` package.
3. Register it in the `:app` route registry (orchestrator-owned file).
4. Create `XxxUiState`, `XxxAction`, `XxxViewModel`, a stateless `XxxScreen` and a `XxxRoute` per [state-management.md](state-management.md).
5. Add strings in EN and KN, previews, a ViewModel test, a UI test and EN/KN screenshot tests.
6. Add the screen to the workbook Screens sheet and `docs/design/screen-inventory.md`.

## Change the app icon, name or brand mark

1. `app_name` lives in `:app` and is not translated (brand).
2. The wordmark composable lives in `:core:designsystem`; confirm the final brand (R-01, D11) before changing assets.
