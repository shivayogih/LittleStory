# 15 — Pull request preparation

**Role:** Assigned engineer preparing a PR for human review.

**Use when:** a ticket is implemented and verified, and the owner has granted Approval D.

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

- `.github/pull_request_template.md`
- The ticket row and test evidence
- `git diff` against `development`

## Steps

1. Confirm Approval D for this ticket; without it, stop after preparing the description.
2. Rebase or merge the latest `development` per repository convention without rewriting shared history.
3. Inspect the full diff for unrelated changes, hardcoded values, missing Kannada strings, secrets and build artifacts.
4. Fill the PR template: ticket link, summary, acceptance criteria checklist, tests run with actual results, screenshots EN + KN, new string keys and config values, localization status, risks.
5. Target `development` (or the base the owner approved), request the owner's review, update the Git-PR-CI sheet.

## Always forbidden

- Hardcoded user-facing text, raw colours, raw dimensions, inline text styles, magic numbers, hardcoded routes, URLs or configuration values in feature code. Use string resources (EN + KN), design-system tokens, typed navigation destinations, `AppConfig` / `FeatureFlags` and `BuildConfig`.
- Adding a string without both an English (`values/`) and a Kannada (`values-kn/`) value. Unverified Kannada is marked "Needs native review", never "Approved".
- English text in ViewModels, use cases or domain code (use `UiText` / typed errors).
- Real AI providers, backends, authentication, payments, analytics, TTS, remote notifications, runtime machine translation, unapproved permissions or SDKs (MVP boundary, master §4.3).
- Skipping an approval level. A Discovery · B Documentation · C Implementation · D Push/PR · E Merge (owner only) · F Release (owner only). One approval never implies the next.
- Pushing, opening a PR, merging, force-pushing, deploying or publishing without explicit authorization. Never merge your own PR or bypass required checks.
- Committing secrets, tokens, child data, `local.properties` or machine-specific paths.

## Required output

- PR URL (or the ready description if Approval D is missing)

## Stop and ask the owner when

- Two authoritative sources conflict (owner instruction, approved requirements, design pack, ADRs).
- The design pack does not define a detail you need: record an open question instead of inventing it.
- The work needs an approval level that has not been granted.
- A required check cannot run in the current environment: report the exact blocker and stop claiming that check.
- The diff contains changes outside the ticket's scope.
