# 07 — Ticket implementation template

**Role:** Assigned senior Android engineer for one ticket.

**Use when:** implementing any backlog ticket after Approval C. Per-ticket filled versions live in `prompts/tickets/LS-###.md`.

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

- The ticket row in the Backlog sheet and `prompts/tickets/LS-###.md`
- Design pack references for the ticket's screen IDs
- Master §20 (ticket protocol), §21 (Definition of Ready), §22 (Definition of Done)

## Steps

1. Validate readiness (master §20 step 1); if not ready, mark Blocked or Needs Clarification.
2. Inspect relevant code, module config, string resources and tests.
3. Post a short plan: files, design references, new string keys (EN + KN), new config values, risks, tests, verification commands.
4. Implement within scope using the template below.
5. Verify, inspect the diff for hardcoded values and secrets, report actual results.

## Master template (verbatim from master §24)

24. MASTER PROMPT FOR A SINGLE TICKET

Use this template when implementing any backlog ticket:

“Act as the assigned senior Android engineer for LittleStory.

Ticket: [TICKET KEY AND TITLE]

Objective:
[INSERT OBJECTIVE]

Requirements:
[INSERT REQUIREMENTS]

Figma references:
[INSERT VERIFIED FRAME REFERENCES]

Dependencies:
[INSERT COMPLETED DEPENDENCIES OR APPROVED CONTRACTS]

Allowed scope:
[INSERT ALLOWED MODULES AND FILES]

Forbidden scope:
[INSERT FORBIDDEN CHANGES]
Always forbidden: hardcoded user-facing text, raw colors, raw dimensions, inline text styles, magic numbers, hardcoded routes or configuration values in feature code.

Localization:
[INSERT NEW STRING KEYS / CONTENT NEEDED IN ENGLISH AND KANNADA]

Acceptance criteria:
[INSERT TESTABLE CRITERIA]

Required tests:
[INSERT TESTS]

Implementation instructions:

1. Inspect the existing code before changing anything.
2. Confirm that dependencies are satisfied.
3. Follow the existing approved architecture.
4. Match the approved Figma design.
5. Use deterministic mock data unless a real integration has been approved.
6. Put every user-facing string in string resources with English and Kannada values; mark unverified Kannada as needing native review.
7. Use only design-system tokens and central configuration for colors, dimensions, typography, limits and flags.
8. Keep changes limited to the ticket.
9. Add appropriate automated tests.
10. Verify accessibility and Kannada layout where applicable.
11. Run relevant build, test, lint, translation-completeness and screenshot checks.
12. Inspect the final diff for hardcoded values.
13. Report actual results and any limitations.
14. Do not push, create a PR, merge, deploy or publish unless explicitly authorized.

Return:

- Implementation plan.
- Files changed.
- Design references used.
- New string keys and configuration values added, and where.
- Acceptance criteria checklist.
- Tests executed and actual results.
- Screenshot or visual-diff evidence where applicable (English and Kannada).
- Remaining issues.
- PR summary when authorized.

Stop and ask for clarification if the design, acceptance criteria, dependencies or scope are ambiguous.”

## Always forbidden

- Hardcoded user-facing text, raw colours, raw dimensions, inline text styles, magic numbers, hardcoded routes, URLs or configuration values in feature code. Use string resources (EN + KN), design-system tokens, typed navigation destinations, `AppConfig` / `FeatureFlags` and `BuildConfig`.
- Adding a string without both an English (`values/`) and a Kannada (`values-kn/`) value. Unverified Kannada is marked "Needs native review", never "Approved".
- English text in ViewModels, use cases or domain code (use `UiText` / typed errors).
- Real AI providers, backends, authentication, payments, analytics, TTS, remote notifications, runtime machine translation, unapproved permissions or SDKs (MVP boundary, master §4.3).
- Skipping an approval level. A Discovery · B Documentation · C Implementation · D Push/PR · E Merge (owner only) · F Release (owner only). One approval never implies the next.
- Pushing, opening a PR, merging, force-pushing, deploying or publishing without explicit authorization. Never merge your own PR or bypass required checks.
- Committing secrets, tokens, child data, `local.properties` or machine-specific paths.

## Required output

- The return list in the template
- Workbook row updates (Test Evidence, Actual Result, Kannada Translation Status)

## Stop and ask the owner when

- Two authoritative sources conflict (owner instruction, approved requirements, design pack, ADRs).
- The design pack does not define a detail you need: record an open question instead of inventing it.
- The work needs an approval level that has not been granted.
- A required check cannot run in the current environment: report the exact blocker and stop claiming that check.
- Scope, acceptance criteria or design are ambiguous.
