# 02 — Figma and design-pack audit

**Role:** UI Integration Engineer and Design-System Engineer auditing the approved design.

**Use when:** before UI planning, before a UI ticket, or when the owner says the Figma Make prototype changed.

## Project facts (verified in P0 discovery, 2026-10-29)

- Governing prompt: `prompts/00_MASTER_ORCHESTRATOR_PROMPT.md`. If this prompt and the master differ, the master and the owner's explicit instructions win.
- Repository: `shivayogih/LittleStory`. Branch model: `main` (stable) ← `staging` (release candidate) ← `development` (integration, default, all PRs target it) ← `feature/LS-###-slug` / `fix/LS-###-slug` / `milestone/mN-slug`; `hotfix/slug` from `main`. See `docs/project-management/branching-strategy.md`. Verify branches exist before use.
- Design reference: `docs/design/LittleStory_Figma_Design_Details.md`, `docs/design/design-tokens.json`, `docs/design/screens/*.jpg` (18 screens, 22 states, extracted from the Figma Make prototype `2DYtUW39GtnsDpUd5jSu6x`). Agents cannot read the live Make file (risk R-23); never invent design details that are not in the design pack.
- Backlog: `docs/project-management/LittleStory_Jira_Backlog.xlsx` (sheets Backlog, Screens, Requirements, Test Cases, Dependencies, ADRs, Risks & Questions, Git-PR-CI, Translations, Dashboard).
- Languages: English (`en`, default and fallback, `res/values/`) and Kannada (`kn`, `res/values-kn/`). Future languages via resources + one `SupportedLanguage` registry entry + `locales_config.xml` only.
- Proposed modules (ADR-002, awaiting owner approval): `:app`, `build-logic`, `:core:designsystem`, `:core:ui`, `:core:common`, `:domain`, `:data`, `:feature:onboarding`, `:feature:home`, `:feature:discover`, `:feature:create`, `:feature:reader`, `:feature:library`, `:feature:settings`.
- Build environment: the Claude Code cloud workspace cannot reach `dl.google.com` (risk R-21), so it cannot resolve AGP/AndroidX or install the Android SDK. Build, test and lint evidence must come from GitHub Actions CI or the owner's machine. Never report a check as passed unless it actually ran.
- Development happens on Windows and macOS: use `./gradlew <task>` on macOS/Linux and `gradlew.bat <task>` on Windows.

## Inputs

- Figma Make URL: https://www.figma.com/make/2DYtUW39GtnsDpUd5jSu6x/Mobile-App-UI-Prototype
- `docs/design/LittleStory_Figma_Design_Details.md`, `docs/design/design-tokens.json`, `docs/design/screens/`
- Screens and Design Gallery sheets of the backlog workbook

## Steps

1. Try the authenticated Figma integration first. Figma Make files are not readable by the current Figma MCP tools; if access fails, say so and use the design pack. Never fabricate frame contents.
2. For each screen ID SCR-01…SCR-23 record frame/screenshot, layout, typography, colour tokens, spacing, radii, shadows, icons, components, states, scroll behaviour, navigation and back behaviour.
3. Compare every colour, type style and dimension with `design-tokens.json`; list discrepancies.
4. Flag text inside images or illustrations, old branding (LittleKatha), hardcoded sample data, missing states and missing screens (splash, language preferences, privacy, help, account, audio preferences).
5. Check whether DM Sans and Manrope have Kannada glyphs and record the fallback decision (ADR-008).
6. Record Kannada layout risks per screen (long labels, buttons, chips, eyebrows that rely on uppercase).

## Always forbidden

- Hardcoded user-facing text, raw colours, raw dimensions, inline text styles, magic numbers, hardcoded routes, URLs or configuration values in feature code. Use string resources (EN + KN), design-system tokens, typed navigation destinations, `AppConfig` / `FeatureFlags` and `BuildConfig`.
- Adding a string without both an English (`values/`) and a Kannada (`values-kn/`) value. Unverified Kannada is marked "Needs native review", never "Approved".
- English text in ViewModels, use cases or domain code (use `UiText` / typed errors).
- Real AI providers, backends, authentication, payments, analytics, TTS, remote notifications, runtime machine translation, unapproved permissions or SDKs (MVP boundary, master §4.3).
- Skipping an approval level. A Discovery · B Documentation · C Implementation · D Push/PR · E Merge (owner only) · F Release (owner only). One approval never implies the next.
- Pushing, opening a PR, merging, force-pushing, deploying or publishing without explicit authorization. Never merge your own PR or bypass required checks.
- Committing secrets, tokens, child data, `local.properties` or machine-specific paths.
- Changing anything in Figma.
- Redesigning a screen because another implementation is easier.

## Required output

- Screen-by-screen audit table (screen ID, source, findings, localization notes, open questions)
- Token discrepancy list
- Proposed updates to `docs/design/figma-audit.md` and the Screens sheet (apply only with Approval B)

## Stop and ask the owner when

- Two authoritative sources conflict (owner instruction, approved requirements, design pack, ADRs).
- The design pack does not define a detail you need: record an open question instead of inventing it.
- The work needs an approval level that has not been granted.
- A required check cannot run in the current environment: report the exact blocker and stop claiming that check.
- The prototype and the design pack disagree and you cannot tell which is approved.
