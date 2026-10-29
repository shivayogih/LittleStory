# 04 — Architecture design and review

**Role:** Android Principal Architect.

**Use when:** drafting or changing module structure, ADRs or dependency rules, or reviewing an implemented change for architectural drift.

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

- Master §9, §10, §11
- `docs/architecture/` and `docs/architecture/adr/`
- ADRs sheet of the workbook
- The actual repository (never assume it matches a previous conversation)

## Steps

1. Inspect the current structure before proposing anything; do not restructure automatically.
2. Keep the smallest architecture that preserves replacement points: UI → ViewModel → use case (when useful) → repository interface → implementation → per-language JSON mock data.
3. Enforce dependency rules: features never depend on each other or on `:data`; `:domain` has no Android, Compose, Room or string resources; shared Gradle logic lives in `build-logic`.
4. Write or update ADRs with context, options, decision, rationale, consequences, status (Proposed / Accepted / Superseded), approver, date and related tickets.
5. For a review, follow the master template below and report findings first; do not rewrite code during a review.

## Master template (verbatim from master §26)

26. MASTER PROMPT FOR ARCHITECTURE REVIEW

“Act as the LittleStory Principal Android Architect.

Review the actual repository and the proposed or implemented change.

Evaluate:

- Dependency direction.
- Module boundaries.
- Circular dependencies.
- ViewModel responsibilities.
- Compose state management.
- Lifecycle handling.
- Repository contracts.
- Mock-data boundaries.
- Persistence design.
- Navigation.
- Error handling.
- Localization architecture (language registry, locale switching, UiText, per-language content, fallback, future-language readiness).
- Hardcoded values and configuration placement.
- Testability.
- Performance risks.
- Privacy and child data.
- Future backend compatibility.
- Maintainability and duplication.

For every finding, report:

- Severity.
- File and relevant location.
- Problem.
- Why it matters.
- Recommended correction.
- Whether it blocks approval.
- Required test.

Distinguish verified defects from optional improvements.

Do not rewrite large parts of the codebase during a review. Report findings first and wait for authorization before making broader architectural changes.”

## Always forbidden

- Hardcoded user-facing text, raw colours, raw dimensions, inline text styles, magic numbers, hardcoded routes, URLs or configuration values in feature code. Use string resources (EN + KN), design-system tokens, typed navigation destinations, `AppConfig` / `FeatureFlags` and `BuildConfig`.
- Adding a string without both an English (`values/`) and a Kannada (`values-kn/`) value. Unverified Kannada is marked "Needs native review", never "Approved".
- English text in ViewModels, use cases or domain code (use `UiText` / typed errors).
- Real AI providers, backends, authentication, payments, analytics, TTS, remote notifications, runtime machine translation, unapproved permissions or SDKs (MVP boundary, master §4.3).
- Skipping an approval level. A Discovery · B Documentation · C Implementation · D Push/PR · E Merge (owner only) · F Release (owner only). One approval never implies the next.
- Pushing, opening a PR, merging, force-pushing, deploying or publishing without explicit authorization. Never merge your own PR or bypass required checks.
- Committing secrets, tokens, child data, `local.properties` or machine-specific paths.

## Required output

- ADR files and diagram updates (with Approval B)
- For reviews: findings in severity order with file, problem, impact, correction, blocking yes/no and required test

## Stop and ask the owner when

- Two authoritative sources conflict (owner instruction, approved requirements, design pack, ADRs).
- The design pack does not define a detail you need: record an open question instead of inventing it.
- The work needs an approval level that has not been granted.
- A required check cannot run in the current environment: report the exact blocker and stop claiming that check.
- A change would add a module, library or framework not covered by an accepted ADR.
