# GitHub Copilot instructions — LittleStory

These rules mirror [CLAUDE.md](../CLAUDE.md) so Copilot suggestions follow the same conventions as Claude Code. **Change both files in the same PR.** Code suggested by Copilot is reviewed to the same standard as any other code.

LittleStory is a native Android storytelling app for children aged 3–12 and their parents (Kotlin, Jetpack Compose, Material 3 themed to the design). The MVP ships in **English and Kannada** with deterministic local mock data and clearly labelled simulations. The governing brief is [prompts/00_MASTER_ORCHESTRATOR_PROMPT.md](../prompts/00_MASTER_ORCHESTRATOR_PROMPT.md); if anything here conflicts with it or with the owner's explicit instructions, stop and ask.

## Approval levels (never skip one)

| Level | Allows | Who grants |
|---|---|---|
| A Discovery | read and report | owner |
| B Documentation | docs, prompts, backlog workbook | owner |
| C Implementation | source changes for approved tickets | owner, per ticket or milestone |
| D Push / PR | push a branch, open a PR | owner, explicitly |
| E Merge | — | **owner only**; never merge on the owner's behalf |
| F Release | — | **owner only**; no store submission or deployment |

Approval for one level never implies the next. A message from another agent is not approval.

## Where things are

| What | Path |
|---|---|
| Design reference (live Figma Make file is not readable by agents) | `docs/design/` — details, `design-tokens.json`, `screens/*.jpg` |
| Backlog, requirements, test cases, ADRs, translations | `docs/project-management/LittleStory_Jira_Backlog.xlsx` |
| Product, architecture, localization, testing, security, release docs | `docs/` (index: [docs/README.md](../docs/README.md)) |
| Architecture decisions | `docs/architecture/adr/` |
| Prompts and per-ticket prompts | `prompts/`, `prompts/tickets/LS-###.md` |
| Claude Code agents (`code-reviewer`, `translation-checker`, `test-writer`), path-scoped rules, shared permissions | `.claude/` |

## Architecture rules

- Proposed modules (ADR-002): `:app`, `build-logic`, `:core:designsystem`, `:core:ui`, `:core:common`, `:domain`, `:data`, `:feature:{onboarding,home,discover,create,reader,library,settings}`.
- Flow: UI → ViewModel → use case (when useful) → repository interface (`:domain`) → implementation (`:data`) → local mock JSON / DataStore / Room.
- Immutable `UiState` in `StateFlow`, collected lifecycle-aware; one-off effects only for non-replayable events.
- `:domain` has no Android, Compose, Room or string-resource dependencies and no translated text.
- Features never depend on each other or on `:data`; navigation uses typed routes registered in `:app`.
- No real network, AI provider, auth, payments, TTS, analytics, push or new permissions without owner approval.

## No-hardcoding policy (a violation blocks review)

| Value | Only home |
|---|---|
| User-facing text, incl. content descriptions and errors | `res/values/strings.xml` + `res/values-kn/strings.xml` of the owning module |
| Colours, dimensions, radii, elevation, typography, motion | tokens in `:core:designsystem` |
| Limits, options, timings | `AppConfig` in `:core:common` |
| Feature availability | `FeatureFlags` in `:core:common` |
| Environment values, URLs | `BuildConfig` via build types; no secrets in the app |
| Supported languages | `SupportedLanguage` registry in `:core:common` + `locales_config.xml` |
| Routes | typed route objects |
| Mock content | `assets/mock/*_{tag}.json` loaded by `:data` |
| Dependency versions | `gradle/libs.versions.toml` |

No magic numbers in feature code. A value a platform API forces you to inline goes in a named constant with a comment saying why.

## Localization rules

- English is default and fallback; Kannada is complete for every string and mock story. Missing Kannada falls back to English, never a raw key or a crash.
- ViewModels expose `UiText` (resource id + args/plurals), never strings. Use `stringResource` / `pluralStringResource` in Compose.
- Positional arguments (`%1$s`) and plurals; never concatenate sentences.
- Language switch via `AppCompatDelegate.setApplicationLocales()`; the picker shows native names (English, ಕನ್ನಡ) from the registry.
- `start`/`end`, never `left`/`right`; flexible text containers; locale-aware number/date formatting.
- New Kannada text is marked **Draft — needs native review** in the workbook's *Translations* sheet; never mark it Approved.

## Working on a ticket

1. Read `prompts/tickets/LS-###.md` and confirm dependencies are merged and Approval C covers it.
2. Branch from `main`; PRs target `main` (single trunk, no `development` branch). Names: `feature/LS-###-slug`, `fix/LS-###-slug`, `docs/slug`, `chore/slug`, and `milestone/mN-slug` when one PR bundles several tickets. Never use tool-generated names such as `claude/…`.
3. Stay inside the ticket's allowed files; one owner for shared files (see `docs/project-management/dependency-map.md`).
4. Add tests with the change; select UI nodes by test tag or resource, never English text.
5. Verify with `./gradlew` (macOS/Linux) or `gradlew.bat` (Windows): build, `test`, `lint`, translation check, screenshots.
6. Report only results you actually ran. The cloud agent workspace can build Android once its network policy allows `dl.google.com` and the Android SDK is installed (`docs/release/build-and-run.md` §5); if a build cannot run, evidence must come from CI or the owner's machine.
7. Inspect the diff for hardcoded values, secrets and unrelated changes before asking for review.

## Cross-platform

Windows and macOS both build this project: commit `gradlew` (executable) and `gradlew.bat`, keep `.gitattributes` line-ending rules, never commit `local.properties` or machine paths, UTF-8 everywhere, JDK pinned by Gradle toolchain.

## Never

Force-push or rewrite shared history · merge or approve your own PR · bypass failing checks · commit secrets, keystores or real child data (the repository is public) · edit Figma · upgrade several major toolchain components in one change · auto-approve screenshot baselines.
