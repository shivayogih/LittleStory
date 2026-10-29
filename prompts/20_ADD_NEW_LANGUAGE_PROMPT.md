# 20 — Add a new language

**Role:** Localization Engineer.

**Use when:** the owner approves a new language (for example Hindi, Tamil, Telugu, Marathi).

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

- `docs/localization/adding-a-new-language.md`
- `SupportedLanguage` registry in `:core:common`
- `res/xml/locales_config.xml` in `:app`

## Steps

1. Follow the master template below exactly.

## Master template (verbatim from master §28)

28. MASTER PROMPT FOR ADDING A NEW LANGUAGE (FUTURE)

“Act as the LittleStory Localization Engineer.

Add support for: [LANGUAGE NAME] ([LOCALE TAG]).

1. Read docs/localization/adding-a-new-language.md and the language registry.
2. Confirm the language is approved and that a native reviewer is available.
3. Verify font/glyph support for the script; propose a licensed font if needed.
4. Check whether the language is right-to-left and verify RTL layout readiness.
5. Create res/values-[tag]/ string files for every module that has strings.
6. Add localized mock content for every story ID, or document the English fallback.
7. Add one entry to the language registry and the locale to locales_config.xml.
8. Do not modify feature code. If a feature-code change seems necessary, stop and report it as an architecture defect.
9. Run translation-completeness, lint, unit, UI and screenshot checks in the new locale.
10. Update translation tracking and documentation.
11. Mark all translations as “Needs native review” until approved.
12. Do not push, create a PR, merge or release without authorization.”

## Always forbidden

- Hardcoded user-facing text, raw colours, raw dimensions, inline text styles, magic numbers, hardcoded routes, URLs or configuration values in feature code. Use string resources (EN + KN), design-system tokens, typed navigation destinations, `AppConfig` / `FeatureFlags` and `BuildConfig`.
- Adding a string without both an English (`values/`) and a Kannada (`values-kn/`) value. Unverified Kannada is marked "Needs native review", never "Approved".
- English text in ViewModels, use cases or domain code (use `UiText` / typed errors).
- Real AI providers, backends, authentication, payments, analytics, TTS, remote notifications, runtime machine translation, unapproved permissions or SDKs (MVP boundary, master §4.3).
- Skipping an approval level. A Discovery · B Documentation · C Implementation · D Push/PR · E Merge (owner only) · F Release (owner only). One approval never implies the next.
- Pushing, opening a PR, merging, force-pushing, deploying or publishing without explicit authorization. Never merge your own PR or bypass required checks.
- Committing secrets, tokens, child data, `local.properties` or machine-specific paths.

## Required output

- Files added
- Check results in the new locale
- Translation tracking rows

## Stop and ask the owner when

- Two authoritative sources conflict (owner instruction, approved requirements, design pack, ADRs).
- The design pack does not define a detail you need: record an open question instead of inventing it.
- The work needs an approval level that has not been granted.
- A required check cannot run in the current environment: report the exact blocker and stop claiming that check.
- Any feature-code change seems necessary: report it as an architecture defect.
