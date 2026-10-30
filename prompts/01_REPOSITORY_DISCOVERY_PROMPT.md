# 01 — Repository discovery (read-only)

**Role:** Android Principal Architect and Build/CI Engineer performing a read-only audit.

**Use when:** starting a session, before planning any ticket, or after a large merge, to establish the true state of the repository.

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

- The local clone and `git` metadata
- GitHub repository settings, branches, PRs, issues and Actions (read-only)
- `README.md`, `CLAUDE.md`, `.github/copilot-instructions.md`, `.github/`, `docs/`, `prompts/`
- Gradle files, `gradle/libs.versions.toml`, wrapper, manifests, `res/` folders, tests

## Steps

1. Record repository identity, local path, remote, current branch, `git status`, default branch, existing branches and open PRs. Do not assume any branch exists.
2. List the project structure, modules (`settings.gradle.kts`), application ID, namespaces, manifests and navigation entry points.
3. Record JDK, Gradle wrapper, AGP, Kotlin, Compose BOM, compile/target/minSdk and every dependency version from the version catalog. Do not upgrade anything.
4. Inspect test source sets, test dependencies, screenshot-test setup and GitHub Actions workflows.
5. Check cross-platform readiness: `gradlew` executable bit, `gradlew.bat`, `.gitattributes`, line endings, machine-specific paths, UTF-8, JDK toolchain.
6. Inventory string resources per module and locale; diff `values/` vs `values-kn/` keys; grep feature code for hardcoded text, `Color(0x`, raw `.dp`/`.sp` literals, route strings and magic numbers.
7. Check whether the network can reach `dl.google.com`, Maven Central and the Gradle Plugin Portal; if Gradle would create local artifacts, explain and ask before running it.
8. Compare findings with the backlog workbook and flag stale ticket statuses.

## Always forbidden

- Hardcoded user-facing text, raw colours, raw dimensions, inline text styles, magic numbers, hardcoded routes, URLs or configuration values in feature code. Use string resources (EN + KN), design-system tokens, typed navigation destinations, `AppConfig` / `FeatureFlags` and `BuildConfig`.
- Adding a string without both an English (`values/`) and a Kannada (`values-kn/`) value. Unverified Kannada is marked "Needs native review", never "Approved".
- English text in ViewModels, use cases or domain code (use `UiText` / typed errors).
- Real AI providers, backends, authentication, payments, analytics, TTS, remote notifications, runtime machine translation, unapproved permissions or SDKs (MVP boundary, master §4.3).
- Skipping an approval level. A Discovery · B Documentation · C Implementation · D Push/PR · E Merge (owner only) · F Release (owner only). One approval never implies the next.
- Pushing, opening a PR, merging, force-pushing, deploying or publishing without explicit authorization. Never merge your own PR or bypass required checks.
- Committing secrets, tokens, child data, `local.properties` or machine-specific paths.
- Running formatters, Gradle tasks that write outside `build/` without approval, or any write operation during discovery.

## Required output

- A structured report following master §2.2 items 1–8a, 11, 12 and 15
- A table of verified facts vs assumptions
- A list of backlog updates the owner should approve
- No file changes

## Stop and ask the owner when

- Two authoritative sources conflict (owner instruction, approved requirements, design pack, ADRs).
- The design pack does not define a detail you need: record an open question instead of inventing it.
- The work needs an approval level that has not been granted.
- A required check cannot run in the current environment: report the exact blocker and stop claiming that check.
- Any command would modify tracked or untracked files.
