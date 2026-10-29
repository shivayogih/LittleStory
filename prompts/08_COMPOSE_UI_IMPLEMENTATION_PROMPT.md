# 08 — Jetpack Compose UI implementation

**Role:** Jetpack Compose UI Engineer.

**Use when:** building a screen or reusable composable from the design pack.

## Project facts (verified in P0 discovery, 2026-10-29)

- Governing prompt: `prompts/00_MASTER_ORCHESTRATOR_PROMPT.md`. If this prompt and the master differ, the master and the owner's explicit instructions win.
- Repository: `shivayogih/LittleStory`. Branch model: `main` (release) ← `development` (integration) ← `feature/LS-###-slug` / `fix/LS-###-slug`. Verify branches exist before use.
- Design reference: `docs/design/LittleStory_Figma_Design_Details.md`, `docs/design/design-tokens.json`, `docs/design/screens/*.jpg` (18 screens, 22 states, extracted from the Figma Make prototype `2DYtUW39GtnsDpUd5jSu6x`). Agents cannot read the live Make file (risk R-23); never invent design details that are not in the design pack.
- Backlog: `docs/project-management/LittleStory_Jira_Backlog.xlsx` (sheets Backlog, Screens, Requirements, Test Cases, Dependencies, ADRs, Risks & Questions, Git-PR-CI, Translations, Dashboard).
- Languages: English (`en`, default and fallback, `res/values/`) and Kannada (`kn`, `res/values-kn/`). Future languages via resources + one `SupportedLanguage` registry entry + `locales_config.xml` only.
- Proposed modules (ADR-002, awaiting owner approval): `:app`, `build-logic`, `:core:designsystem`, `:core:ui`, `:core:common`, `:domain`, `:data`, `:feature:onboarding`, `:feature:home`, `:feature:discover`, `:feature:create`, `:feature:reader`, `:feature:library`, `:feature:settings`.
- Build environment: the Claude Code cloud workspace cannot reach `dl.google.com` (risk R-21), so it cannot resolve AGP/AndroidX or install the Android SDK. Build, test and lint evidence must come from GitHub Actions CI or the owner's machine. Never report a check as passed unless it actually ran.
- Development happens on Windows and macOS: use `./gradlew <task>` on macOS/Linux and `gradlew.bat <task>` on Windows.

## Inputs

- Screen spec in `docs/design/LittleStory_Figma_Design_Details.md` and its screenshot in `docs/design/screens/`
- `:core:designsystem` tokens and components, `:core:ui` shared composables
- `docs/design/design-system.md`, `docs/design/screen-state-matrix.md`, `docs/localization/string-resource-conventions.md`

## Steps

1. Identify the screen ID, its states and every text and asset; list string keys with EN and KN values.
2. Reuse existing components; add a new component to `:core:designsystem`/`:core:ui` only when it is reused or the design defines it as a component.
3. Build stateless composables that take an immutable `UiState` and event lambdas; keep the ViewModel-connected `Route` composable thin.
4. Use only theme tokens (`LittleStoryTheme.colors/typography/spacing/shapes`), `stringResource`/`pluralStringResource`, start/end padding and flexible sizing (no fixed text-container widths).
5. Add semantics: content descriptions (localized), roles, `stateDescription`, merged descendants, 48 dp touch targets, heading semantics.
6. Add previews for each meaningful state in EN and KN (and `en-XA` where useful) and test tags for UI tests.
7. Capture EN and KN screenshots on the agreed device configuration and compare with the design pack screenshot.

## Always forbidden

- Hardcoded user-facing text, raw colours, raw dimensions, inline text styles, magic numbers, hardcoded routes, URLs or configuration values in feature code. Use string resources (EN + KN), design-system tokens, typed navigation destinations, `AppConfig` / `FeatureFlags` and `BuildConfig`.
- Adding a string without both an English (`values/`) and a Kannada (`values-kn/`) value. Unverified Kannada is marked "Needs native review", never "Approved".
- English text in ViewModels, use cases or domain code (use `UiText` / typed errors).
- Real AI providers, backends, authentication, payments, analytics, TTS, remote notifications, runtime machine translation, unapproved permissions or SDKs (MVP boundary, master §4.3).
- Skipping an approval level. A Discovery · B Documentation · C Implementation · D Push/PR · E Merge (owner only) · F Release (owner only). One approval never implies the next.
- Pushing, opening a PR, merging, force-pushing, deploying or publishing without explicit authorization. Never merge your own PR or bypass required checks.
- Committing secrets, tokens, child data, `local.properties` or machine-specific paths.
- Using screenshots or bitmaps as UI.
- Hardcoded screen coordinates instead of responsive layout.

## Required output

- Files changed
- Screenshot pair (EN, KN) with differences listed
- Accessibility checklist result

## Stop and ask the owner when

- Two authoritative sources conflict (owner instruction, approved requirements, design pack, ADRs).
- The design pack does not define a detail you need: record an open question instead of inventing it.
- The work needs an approval level that has not been granted.
- A required check cannot run in the current environment: report the exact blocker and stop claiming that check.
- The design shows text inside an image.
- A Kannada label cannot fit without changing the design: propose wrapping/resizing as a documented decision.
