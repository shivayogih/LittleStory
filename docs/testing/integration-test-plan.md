# Integration test plan

Purpose: list the cross-layer and end-to-end tests that prove the journeys work, in English and Kannada.
Status: Draft for owner review — P1, 29 Oct 2026
Sources: master orchestrator brief §6.1 (journeys A–G), §17.3, §17.4; workbook Test Cases (Integration rows), tickets LS-062, LS-075, LS-091, LS-121; ADR-004.

## 1. Layer integrations

| ID | Combination | What it proves | Test case(s) | Tooling |
| --- | --- | --- | --- | --- |
| IT-01 | ViewModel + fake repositories | State transitions for each screen with controlled data | per feature | JUnit, Turbine |
| IT-02 | Favourites use case + Room (in-memory) | One favourite state across Home, Discover, Detail, Player | TC-017, TC-018 | Robolectric + Room |
| IT-03 | Generation simulator + saved stories | Generated story auto-saved to "Created by Me" | TC-029 | JVM |
| IT-04 | Progress repository + Home continue card | Resume reading/listening from last position | TC-033 | Robolectric + Room |
| IT-05 | Profile repository + active child | Switching child changes Home personalisation | TC-042 | Robolectric |
| IT-06 | Language repository + catalog loader | Changing language reloads content with same IDs; saved and progress data survive | TC-007, TC-048 | Robolectric |
| IT-07 | Catalog loader fallback | Story without Kannada falls back to English fields, no crash, no raw key | TC-049 | JVM |
| IT-08 | Process death | Active child, favourites and progress restored after process recreation | TC-058 | Robolectric / instrumented |
| IT-09 | Room migrations | Schema migrations preserve data (from first release onward) | new | Room testing |

## 2. End-to-end journeys (LS-121)

Each journey runs in `en` and `kn` against the real app graph (Hilt, Room, DataStore, mock JSON) under Robolectric, so it runs in CI on every pull request with no emulator.

Implemented (P7, 30 Oct 2026): `app/src/test/kotlin/com/littlestory/app/journeys/`. `Journeys` holds the steps, `EnglishJourneysTest` and `KannadaJourneysTest` run them with the device locale set to `en` or `kn` on a 411 × 891 dp screen. Steps find nodes by test tag only; repository state is checked between steps. Run them alone with `./gradlew :app:testDebugUnitTest --tests '*JourneysTest'`. A timeout fails with the screen's semantics tree, so a CI log shows where the journey stopped.

```mermaid
flowchart LR
  A["A: First run - Onboarding, Setup, Home"]
  B["B: Discover, Detail, Read, Save, My Stories"]
  C["C: Create, Validate, Generate, Ready, Read, Save"]
  D["D: My Stories, Saved story, Reader, Progress"]
  E["E: Profile hub, Settings, Privacy"]
  F["F: Detail or Reader, Player, Controls, Back"]
  G["G: Settings, Language, Kannada, Restart persists"]
  A --> B
  A --> C
  B --> D
  A --> E
  B --> F
  E --> G
```

| Journey | Steps (abridged) | Key assertions |
| --- | --- | --- |
| A | Launch → Onboarding 3 slides or Skip → Setup → Home | First-run routing (TC-057); profile created; Home greets the child |
| B | Home → Discover → search/filter → Detail → Read → favourite → My Stories | Search works in both scripts (TC-011, TC-012); favourite visible in library |
| C | Home → Create → empty submit error → enter text → generate → Story ready → Read | Validation (TC-019/020); deterministic stages (TC-025); auto-save (TC-029) |
| D | My Stories → saved story → Reader → leave mid-story → Home continue card | Progress restored (TC-033) |
| E | Profile → settings rows → Privacy (once designed, SCR-21) | Toggles independent of rows (TC-041) |
| F | Detail → Listen → play/pause, ±15 s, speed, sleep timer → back | Simulation disclosed (TC-036); timer label updates (TC-037) |
| G | Profile → Language → ಕನ್ನಡ → UI and stories switch → kill and relaunch | Selection persists; no lost state (TC-048) |

What the automated journeys cover today, and what stays manual:

| Journey | Automated steps | Still manual |
| --- | --- | --- |
| A | Skip onboarding → Setup with a name → Home; first-run flag and profile saved | Three-slide swipe; splash timing on device |
| B | Discover → Detail → favourite (saved) → Read → next page (progress saved) → My Stories ▸ Favourites shows the story | Search in Kannada script with the IME |
| C | Create → empty submit shows the error → suggestion → generate → Story Ready (saved) → Read → Saved row opens Created by me | Pause and Try again on Generating |
| D | Listen → skip → pause (progress saved) → Home continue card resumes the Player → My Stories ▸ Recently played resumes it | Process death (IT-08) |
| E | Profile → Reminder → Help → Privacy → delete all → back to Onboarding with no profile and first run reset | Child switching |
| F | Detail → Listen (simulation label shown) → play/pause, +15 s → Story text → Reader → Play → Player | Speed, sleep timer and volume (covered by Player VM tests) |
| G | Profile → Language → other story language (saved) → Detail shows the story in that language → app language applied through AppCompat | Kill and relaunch on a device |

## 3. Rules

- Integration tests use deterministic seed data from [test-data.md](test-data.md).
- No real network; MockWebServer only after a network layer is approved.
- Database tests only once Room is introduced (LS-038).
- A dependent ticket is never reported as integration-tested against an unmerged change (brief §15.5).
