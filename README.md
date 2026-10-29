# LittleStory

*Little Stories. Big Imagination.*

LittleStory is a native Android storytelling app for children aged 3–12 and their parents. Children discover, create, read and listen to gentle, culturally rooted stories; parents stay in control of profiles, language, privacy and bedtime settings.

The MVP is built with Kotlin and Jetpack Compose, ships in **English and ಕನ್ನಡ (Kannada)**, and uses deterministic local mock data. AI story generation, narration, downloads and subscriptions are simulations in the MVP and are labelled as such.

## Status

| Phase | State |
|---|---|
| P0 Discovery | Done (29 Oct 2026) |
| P1 Requirements, architecture, backlog, prompts | Merged (PR #2) |
| P2 Foundation (M2 walking skeleton) | Implemented on a branch, awaiting owner review: Gradle baseline, 14 modules, design tokens, EN/KN localization base, quality checks, CI |
| P3 onwards | Not started |

The app currently shows one tokenised screen with the English / ಕನ್ನಡ language switch. Build and run: [docs/release/build-and-run.md](docs/release/build-and-run.md).

## Repository map

| Path | Contents |
|---|---|
| [CLAUDE.md](CLAUDE.md) / [.github/copilot-instructions.md](.github/copilot-instructions.md) | Rules for AI coding assistants (kept identical) |
| [docs/](docs/README.md) | Product, design, architecture, localization, API, testing, security, project-management and release docs |
| [docs/design/](docs/design/LittleStory_Figma_Design_Details.md) | Design reference extracted from the Figma Make prototype: specs, tokens, 22 screenshots |
| [docs/project-management/LittleStory_Jira_Backlog.xlsx](docs/project-management/LittleStory_Jira_Backlog.xlsx) | Jira-style backlog, requirements, test cases, ADRs, risks, translations, dashboard |
| [prompts/](prompts/README.md) | Claude Code prompt library and per-ticket prompts |

## Ways of working

- Every change is a ticket (`LS-###`) on its own branch `feature/LS-###-slug`, with a PR to `development`.
- The project owner reviews and merges every PR and authorizes every release.
- No hardcoded text, colours, dimensions or configuration: see [CLAUDE.md](CLAUDE.md) and [docs/architecture/how-to-change.md](docs/architecture/how-to-change.md).
- Build and run instructions for Windows and macOS will live in [docs/release/build-and-run.md](docs/release/build-and-run.md) once the project skeleton exists (LS-030).
