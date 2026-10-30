# LittleStory

*Little Stories. Big Imagination.*

LittleStory is a native Android storytelling app for children aged 3–12 and their parents. Children discover, create, read and listen to gentle, culturally rooted stories; parents stay in control of profiles, language, privacy and bedtime settings.

The MVP is built with Kotlin and Jetpack Compose, ships in **English and ಕನ್ನಡ (Kannada)**, and uses deterministic local mock data. AI story generation, narration, downloads and subscriptions are simulations in the MVP and are labelled as such.

## Quick start

You need **Git**, **Android Studio** (current stable) and an internet connection. Nothing else is installed by hand.

1. `git clone https://github.com/shivayogih/LittleStory.git`
2. Open the folder in Android Studio and wait for the Gradle sync. Studio writes `local.properties` (your SDK path) itself and offers to install any missing SDK package; accept.
3. Pick an emulator or a phone and press **Run**.

Command line (macOS/Linux `./gradlew`, Windows `gradlew.bat`): set `ANDROID_HOME` to your Android SDK folder, then `./gradlew assembleDebug` builds the debug APK and `./gradlew check` runs every test and quality check. Needs any JDK 17 or newer on the PATH; the build downloads JDK 21 for itself. Details and troubleshooting: [docs/release/build-and-run.md](docs/release/build-and-run.md).

## Status

| Phase | State |
|---|---|
| P0 Discovery | Done (29 Oct 2026) |
| P1 Requirements, architecture, backlog, prompts | Merged |
| P2 Foundation (Gradle baseline, 14 modules, design system, EN/KN base, persistence, quality checks, CI) | Merged / in review |
| P3 Navigation, onboarding, child setup | In review (first run: onboarding, setup, Home placeholder) |
| P4 onwards | Not started |

## Repository map

| Path | Contents |
|---|---|
| [CLAUDE.md](CLAUDE.md) / [.github/copilot-instructions.md](.github/copilot-instructions.md) | Rules for AI coding assistants (kept identical) |
| [docs/](docs/README.md) | Product, design, architecture, localization, API, testing, security, project-management and release docs |
| [docs/design/](docs/design/LittleStory_Figma_Design_Details.md) | Design reference extracted from the Figma Make prototype: specs, tokens, 22 screenshots |
| [docs/project-management/LittleStory_Jira_Backlog.xlsx](docs/project-management/LittleStory_Jira_Backlog.xlsx) | Jira-style backlog, requirements, test cases, ADRs, risks, translations, dashboard |
| [prompts/](prompts/README.md) | Claude Code prompt library and per-ticket prompts |

## Ways of working

- Every change is a ticket (`LS-###`) on its own branch `feature/LS-###-slug`, with a PR to `development` (default branch). `development` → `staging` → `main` are promoted by the owner; see [branching strategy](docs/project-management/branching-strategy.md).
- The project owner reviews and merges every PR and authorizes every release.
- No hardcoded text, colours, dimensions or configuration: see [CLAUDE.md](CLAUDE.md) and [docs/architecture/how-to-change.md](docs/architecture/how-to-change.md).
- Build and run: see Quick start above and [docs/release/build-and-run.md](docs/release/build-and-run.md).
