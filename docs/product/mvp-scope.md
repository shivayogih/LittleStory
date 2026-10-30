# LittleStory — MVP Scope

Purpose: define what the first release includes and excludes, how simulations are labelled, and which scope decisions are still open.

Status: Draft for owner review — P1, 29 Oct 2026

Sources: master orchestrator brief §4.3–4.4; `docs/design/LittleStory_Figma_Design_Details.md`; workbook Overview, Screens and Risks sheets; P0 discovery decisions D1–D15.

## MVP definition

A native Android app (Kotlin, Jetpack Compose) that implements the designed screens with **deterministic local mock data**, fully usable in **English and Kannada**, with simulated generation, narration, downloads and subscription that are clearly labelled. No network calls.

## In scope

| Area | Included | Screens | Requirements |
| --- | --- | --- | --- |
| First run | Splash (pattern TBD), 3-slide onboarding, child profile setup incl. language | SCR-19, SCR-01, SCR-02 | FR-01, FR-02, FR-25 |
| Discovery | Home, Discover with search and filters, Category, Story detail, favourites | SCR-03–SCR-06 | FR-04–FR-07, FR-13 |
| Creation | Create form with validation, simulated generation with pause/retry, Story ready, auto-save | SCR-07–SCR-09 | FR-08–FR-10 |
| Reading and listening | Reader with text size, simulated Player with sleep timer, progress persistence | SCR-10, SCR-11a/b | FR-11, FR-12, FR-24 |
| Library | My Stories tabs and empty states; simulated offline downloads | SCR-12, SCR-17 | FR-14, FR-15 |
| Parent area | Profile hub, Child profiles, Bedtime reminder (UI only, Proposed D13), mock Notifications, demo Subscription (flagged) | SCR-13–SCR-16, SCR-18 | FR-03, FR-16–FR-19 |
| Language | App language EN / ಕನ್ನಡ applied app-wide and persisted; story content in both | SCR-02, SCR-20 | FR-20, FR-21 |
| Privacy, Help | Privacy & parent controls (incl. delete local data), Help & about — once designed or a pattern is approved | SCR-21, SCR-22 | FR-22, FR-23 |
| Quality | Accessibility, no hardcoding, automated tests, CI, cross-platform build | – | NFR-01–NFR-17 |

## Out of scope (Won't for MVP, brief §4.3)

| Excluded | Why / future path |
| --- | --- |
| Real AI story generation | Needs backend, moderation, cost controls; job-based flow planned in `docs/api/` |
| Backend services, accounts, authentication, cloud sync | MVP is local; repositories are language-aware interfaces ready for replacement |
| Real payments, Play Billing, subscription entitlements | Demo UI only; no billing library or permission |
| Production TTS or audio streaming | Player is simulated |
| Remote/push notifications | Notifications inbox is mock data |
| Production analytics or third-party SDKs | Privacy (NFR-07) |
| Runtime machine translation, translation SDKs | Translations are reviewed resources |
| Languages other than English and Kannada in the shipped UI | Architecture ready (NFR-03); delivered later |
| Unapproved permissions (e.g. POST_NOTIFICATIONS) | Only with explicit approval (D13) |
| Dark theme | Single light theme with "night" screens (ADR-013, Proposed) |

## Simulations and how they are labelled

Every simulation is behind an interface (so a real implementation can replace it), controlled by a feature flag in `FeatureFlags`, and disclosed in the UI. Wording lives in string resources in both languages.

| Simulation | Where | Disclosure (Proposed) | Flag | Requirement |
| --- | --- | --- | --- | --- |
| Story generation | SCR-08, SCR-09 | Deterministic output from mock templates; no claim of AI processing beyond the designed copy; owner to confirm whether a "demo" note is needed | `simulatedGeneration` (always on in MVP) | FR-09, NFR-12 |
| Narration playback | SCR-11a | Visible "Simulated narration" note on the Player (placement needs design, Q5) | `simulatedAudio` | FR-12, TC-036 |
| Offline downloads | SCR-06, SCR-11a, SCR-17 | "Simulated download" wording or note on Downloads screen | `simulatedDownloads` | FR-15 |
| Subscription | SCR-15 | Designed copy: "Illustrative pricing only. No real payment will be processed." and confirmation dialog | `demoSubscription` | FR-19 |
| Bedtime reminder | SCR-16 | If UI-only (D13): toast must not imply a notification is scheduled; wording to confirm | `reminders` | FR-17 |
| Notifications inbox | SCR-18 | Mock items from per-language JSON | – | FR-18 |

## Design items that change for the MVP

| Design shows | MVP behaviour (Proposed) | Decision |
| --- | --- | --- |
| "LittleKatha" in logo and Premium banner | "LittleStory" wordmark | D11 |
| Seven languages in Setup and Child profiles | Only registry-enabled English and ಕನ್ನಡ | D9, R-02 |
| Pre-filled names (Meera, Aarav, Ananya) and meera@example.com | Demo seed data only; real first run starts empty; parent email dropped | D14, R-11 |
| Gendered sample sentence | Neutral, translatable copy | R-06 |
| Fixed "Good evening" greeting | Time-of-day greeting or neutral greeting | R-10 |
| Rows routing to placeholder screens | Real screens, or rows hidden/disabled | D10, R-19 |

## Decisions pending (from P0, all Proposed)

| ID | Decision | Recommendation |
| --- | --- | --- |
| D9 | Language step in onboarding | Keep Setup field; EN and ಕನ್ನಡ only |
| D10 | Missing designs (splash, language, privacy, help, account, audio prefs) | Provide designs or approve building from existing components |
| D11 | Brand assets | Confirm LittleStory wordmark |
| D12 | Native Kannada reviewer | Name a person before P6 |
| D13 | Bedtime reminders | UI only, no notification permission |
| D14 | Personal data collected | Child first name, age, avatar, interests only |

Technical decisions (D3–D8, D15) are tracked in `docs/project-management/decision-log.md`.
