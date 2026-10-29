# LittleStory — Functional Requirements

Purpose: the catalogue of functional requirements (FR-01…FR-25) with priority, source, screens, acceptance criteria, tickets and tests.

Status: Draft for owner review — P1, 29 Oct 2026

Sources: workbook `docs/project-management/LittleStory_Jira_Backlog.xlsx` (Requirements, Backlog and Test Cases sheets); master orchestrator brief §6–7; `docs/design/LittleStory_Figma_Design_Details.md`; P0 discovery decisions D1–D15.

## How to read this file

- IDs are the workbook's stable IDs; tickets (LS-###) and test cases (TC-###) match the workbook.
- **Source** says where the requirement comes from. "Design SCR-xx" means the design pack extracted from the Figma Make prototype on 29 Oct 2026 (the live file could not be re-read, so these are *Needs Figma verification*). "Explicit instruction (owner)" means the master brief. "Assumption" means no design exists and the owner must confirm.
- No requirement is approved yet. Status changes only when the owner approves it.
- MoSCoW totals: 15 Must, 5 Should, 5 Could. Additional languages are **Won't** for the MVP (the architecture to add them is a Must, see NFR-03).

## Summary

| ID | Title | MoSCoW | Screens |
| --- | --- | --- | --- |
| [FR-01](#fr-01--onboarding) | Onboarding | Must | SCR-01 |
| [FR-02](#fr-02--child-profile-setup) | Child profile setup | Must | SCR-02 |
| [FR-03](#fr-03--multiple-child-profiles) | Multiple child profiles | Should | SCR-14 |
| [FR-04](#fr-04--personalised-home) | Personalised Home | Must | SCR-03 |
| [FR-05](#fr-05--discover) | Discover | Must | SCR-04a, SCR-04b |
| [FR-06](#fr-06--category-listing) | Category listing | Should | SCR-05 |
| [FR-07](#fr-07--story-detail) | Story detail | Must | SCR-06 |
| [FR-08](#fr-08--create-story-form) | Create story form | Must | SCR-07 |
| [FR-09](#fr-09--simulated-generation) | Simulated generation | Must | SCR-08 |
| [FR-10](#fr-10--story-ready) | Story ready | Must | SCR-09 |
| [FR-11](#fr-11--reader) | Reader | Must | SCR-10 |
| [FR-12](#fr-12--simulated-audio-player) | Simulated audio player | Should | SCR-11a, SCR-11b |
| [FR-13](#fr-13--favourites) | Favourites | Must | SCR-03, SCR-04a, SCR-05, SCR-06, SCR-09, SCR-10, SCR-11a |
| [FR-14](#fr-14--my-stories-library) | My Stories library | Must | SCR-12 |
| [FR-15](#fr-15--offline-downloads-simulated) | Offline downloads (simulated) | Could | SCR-17 |
| [FR-16](#fr-16--settings-hub) | Settings hub | Must | SCR-13 |
| [FR-17](#fr-17--bedtime-reminder-settings) | Bedtime reminder settings | Could | SCR-16 |
| [FR-18](#fr-18--notifications-inbox-mock) | Notifications inbox (mock) | Could | SCR-18 |
| [FR-19](#fr-19--demo-subscription) | Demo subscription | Could | SCR-15 |
| [FR-20](#fr-20--app-language-en--ಕನ್ನಡ) | App language EN / ಕನ್ನಡ | Must | SCR-02, SCR-20 |
| [FR-21](#fr-21--story-language-en--ಕನ್ನಡ) | Story language EN / ಕನ್ನಡ | Must | SCR-06, SCR-07, SCR-09, SCR-10 |
| [FR-22](#fr-22--privacy--parent-controls) | Privacy & parent controls | Should | SCR-21 |
| [FR-23](#fr-23--help--about) | Help & about | Could | SCR-22 |
| [FR-24](#fr-24--progress--resume) | Progress & resume | Should | SCR-03, SCR-10, SCR-11a, SCR-12 |
| [FR-25](#fr-25--launch--first-run-routing) | Launch & first-run routing | Must | SCR-19 |

## Owner decisions that affect these requirements

| Decision (P0) | Requirements affected | Recommendation (Proposed) |
| --- | --- | --- |
| D9 Language in onboarding | FR-02, FR-20 | Keep Setup's language field, list only registry-enabled English and ಕನ್ನಡ |
| D10 Missing designs | FR-20 (SCR-20), FR-22, FR-23, FR-25 | Provide designs or approve building from existing components |
| D13 Bedtime reminders | FR-17 | UI only in MVP; no notification permission |
| D14 Personal data | FR-02, FR-03, FR-16 | Child first name, age, avatar, interests only; drop parent email; parent name optional or removed |
| D11 Brand | FR-01, FR-16, FR-19 | LittleStory wordmark; ignore "LittleKatha" in screenshots |

## Requirements

### FR-01 — Onboarding

| Field | Value |
| --- | --- |
| Description | 3-slide intro with Skip, Continue and Get Started leading to Setup |
| Priority (MoSCoW) | Must |
| Source | Design SCR-01 |
| Screens | SCR-01 |
| Tickets | LS-060 |
| Test cases | TC-001, TC-002 |
| Status | Proposed — awaiting owner review |

Acceptance criteria (from linked tickets):

- Copy matches design in EN; KN present (LS-060)
- Continue advances; slide 3 shows Get Started → Setup (LS-060)
- Skip → Setup (LS-060)
- Pager announced (LS-060)

Test scenarios:

- TC-001 (en): Advance through onboarding — expected: Slides 1→2→3 copy matches design; Setup opens
- TC-002 (en): Skip onboarding — expected: Setup opens

### FR-02 — Child profile setup

| Field | Value |
| --- | --- |
| Description | Capture parent name, child name, avatar, age 3–12, language, optional interests and default duration |
| Priority (MoSCoW) | Must |
| Source | Design SCR-02 |
| Screens | SCR-02 |
| Tickets | LS-061, LS-062 |
| Test cases | TC-004, TC-005 |
| Status | Proposed — awaiting owner review |

Acceptance criteria (from linked tickets):

- Defaults as designed for demo data only (empty for real first run – decision) (LS-061)
- CTA text includes child name via format arg (LS-061)
- Profile saved and Home shows name (LS-061)
- Active child persists (LS-062)
- Switching child updates all screens (LS-062)

Test scenarios:

- TC-004 (en): Create profile happy path — expected: Profile saved; Home shows 'Stories for {name}'
- TC-005 (en): Required child name — expected: Validation per LS-004 decision; no navigation

### FR-03 — Multiple child profiles

| Field | Value |
| --- | --- |
| Description | List, switch active, edit and add child profiles |
| Priority (MoSCoW) | Should |
| Source | Design SCR-14 |
| Screens | SCR-14 |
| Tickets | LS-062, LS-063 |
| Test cases | TC-042 |
| Status | Proposed — awaiting owner review |

Acceptance criteria (from linked tickets):

- Active child persists (LS-062)
- Switching child updates all screens (LS-062)
- Switch changes active child (LS-063)
- Edit saves changes (LS-063)
- Add creates new profile (LS-063)

Test scenarios:

- TC-042 (en): Switch active child — expected: Ananya Active; Home shows 'Stories for Ananya'

### FR-04 — Personalised Home

| Field | Value |
| --- | --- |
| Description | Greeting, hero CTA, continue listening, category tiles, 'Made for {child}' row, culture banner |
| Priority (MoSCoW) | Must |
| Source | Design SCR-03 |
| Screens | SCR-03 |
| Tickets | LS-070 |
| Test cases | TC-008, TC-009, TC-010 |
| Status | Proposed — awaiting owner review |

Acceptance criteria (from linked tickets):

- Matches screenshot EN (LS-070)
- KN layout without clipping (LS-070)
- All tap targets navigate per spec (LS-070)
- Empty continue state handled (LS-070)

Test scenarios:

- TC-008 (en): Home populated — expected: Hero, continue card (42%), categories, Made for row, banner present
- TC-009 (en): Home with no progress — expected: Continue section hidden or empty state per decision
- TC-010 (en): Home navigation — expected: Each opens correct screen per spec

### FR-05 — Discover

| Field | Value |
| --- | --- |
| Description | Live search, featured story, category filter chips, popular grid, bedtime row, no-results state |
| Priority (MoSCoW) | Must |
| Source | Design SCR-04a/b |
| Screens | SCR-04a, SCR-04b |
| Tickets | LS-071, LS-072 |
| Test cases | TC-011, TC-012, TC-013, TC-014 |
| Status | Proposed — awaiting owner review |

Acceptance criteria (from linked tickets):

- Search filters by title in EN and KN (LS-071)
- Chip filters by category (LS-071)
- 'Results for …' title while searching (LS-071)
- Shown when no match (LS-072)
- Clear search restores default (LS-072)

Test scenarios:

- TC-011 (en): Live search — expected: Featured hidden; title 'Results for “moon”'; Mira and the Moonlit Garden shown
- TC-012 (kn): Search Kannada title — expected: Matching story shown
- TC-013 (en): No results — expected: Empty state shown; clear restores All + featured
- TC-014 (en): Category chip filter — expected: Only Space stories in grid

### FR-06 — Category listing

| Field | Value |
| --- | --- |
| Description | Category hero and story list with sort (Recommended, Shortest first, Newest) |
| Priority (MoSCoW) | Should |
| Source | Design SCR-05 |
| Screens | SCR-05 |
| Tickets | LS-073 |
| Test cases | TC-015 |
| Status | Proposed — awaiting owner review |

Acceptance criteria (from linked tickets):

- Works for every category in mock data (LS-073)
- Sort orders correct (LS-073)
- '{n} stories' uses plurals (LS-073)

Test scenarios:

- TC-015 (en): Sort shortest first — expected: Rows ordered by minutes ascending

### FR-07 — Story detail

| Field | Value |
| --- | --- |
| Description | Cover, favourite, download, metadata, synopsis, lesson, narration info, Listen/Read CTAs |
| Priority (MoSCoW) | Must |
| Source | Design SCR-06 |
| Screens | SCR-06 |
| Tickets | LS-074 |
| Test cases | TC-016 |
| Status | Proposed — awaiting owner review |

Acceptance criteria (from linked tickets):

- Data-driven for any story id (LS-074)
- Language tag reflects story language (LS-074)
- Listen → Player, Read → Reader (LS-074)

Test scenarios:

- TC-016 (en): Detail actions — expected: Player then Reader open

### FR-08 — Create story form

| Field | Value |
| --- | --- |
| Description | Text up to 500 chars, suggestions, hero, theme, more options (mood, duration, story language), empty-text validation |
| Priority (MoSCoW) | Must |
| Source | Design SCR-07 |
| Screens | SCR-07 |
| Tickets | LS-080, LS-081 |
| Test cases | TC-019, TC-020, TC-021, TC-022 |
| Status | Proposed — awaiting owner review |

Acceptance criteria (from linked tickets):

- Counter enforces limit (LS-080)
- Suggestions fill text (LS-080)
- Hero selector lists profiles (LS-080)
- Empty submit shows error, no navigation (LS-081)
- Typing clears error (LS-081)
- Story language limited to EN/KN (LS-081)

Test scenarios:

- TC-019 (en): Empty submission — expected: Error banner shown and announced; stays on Create
- TC-020 (en): Error clears on typing — expected: Error disappears
- TC-021 (en): Character limit — expected: Input capped at limit; counter shows 500/500
- TC-022 (en): Suggestion fills text — expected: Textarea filled with sample sentence

### FR-09 — Simulated generation

| Field | Value |
| --- | --- |
| Description | Deterministic progress with 3 stages, cancel → paused, try again, back to idea, auto-advance |
| Priority (MoSCoW) | Must |
| Source | Design SCR-08 |
| Screens | SCR-08 |
| Tickets | LS-082, LS-083 |
| Test cases | TC-024, TC-025, TC-026, TC-027 |
| Status | Proposed — awaiting owner review |

Acceptance criteria (from linked tickets):

- Same input → same output (LS-082)
- Cancel stops progress (LS-082)
- Result saved to Created by Me (LS-082)
- Stages change at thresholds (LS-083)
- Paused state per spec (LS-083)
- Auto-advance to Story Ready (LS-083)

Test scenarios:

- TC-024 (en): Deterministic generation — expected: Identical story output
- TC-025 (en): Stage progression — expected: Stage 1 <38%, 2 <76%, 3 ≥76%; auto-advance at 100%
- TC-026 (en): Cancel and retry — expected: Paused state; retry restarts progress
- TC-027 (en): Back to idea keeps text — expected: Create opens with previous text intact

### FR-10 — Story ready

| Field | Value |
| --- | --- |
| Description | Result with listen/read and automatic save to 'Created by Me' |
| Priority (MoSCoW) | Must |
| Source | Design SCR-09 |
| Screens | SCR-09 |
| Tickets | LS-082, LS-084 |
| Test cases | TC-028, TC-029 |
| Status | Proposed — awaiting owner review |

Acceptance criteria (from linked tickets):

- Same input → same output (LS-082)
- Cancel stops progress (LS-082)
- Result saved to Created by Me (LS-082)
- Shows generated story in selected language (LS-084)
- Row opens My Stories on Created by Me (LS-084)

Test scenarios:

- TC-028 (kn): Generated in Kannada — expected: Story Ready shows Kannada title, synopsis, peek
- TC-029 (en): Auto-saved to Created by Me — expected: My Stories opens on Created by Me with the story

### FR-11 — Reader

| Field | Value |
| --- | --- |
| Description | Paginated story, A−/A+ (0.9–1.3), reading progress bar, previous/next, play narration |
| Priority (MoSCoW) | Must |
| Source | Design SCR-10 |
| Screens | SCR-10 |
| Tickets | LS-090 |
| Test cases | TC-030, TC-031 |
| Status | Proposed — awaiting owner review |

Acceptance criteria (from linked tickets):

- Pagination and disabled states correct (LS-090)
- Text size persists (LS-090)
- Respects system font scale; KN line height (LS-090)

Test scenarios:

- TC-030 (en): Pagination bounds — expected: Previous disabled on 1; Next disabled on last
- TC-031 (en): Text size limits — expected: Scale clamps at 1.3 and 0.9

### FR-12 — Simulated audio player

| Field | Value |
| --- | --- |
| Description | Play/pause, seek, ±15 s, chapters, speed 0.75–1.5×, volume, download, favourite, sleep timer – clearly simulated |
| Priority (MoSCoW) | Should |
| Source | Design SCR-11a/b |
| Screens | SCR-11a, SCR-11b |
| Tickets | LS-092, LS-093 |
| Test cases | TC-034, TC-035, TC-037 |
| Status | Proposed — awaiting owner review |

Acceptance criteria (from linked tickets):

- Controls behave as spec (LS-092)
- Timer text formatted mm:ss (LS-092)
- Simulation clearly indicated (LS-092)
- Selecting option closes sheet and updates button (LS-093)
- Cancel/tap outside dismisses (LS-093)

Test scenarios:

- TC-034 (en): Play/pause & ±15 s — expected: State and time update; times formatted mm:ss
- TC-035 (en): Speed cycle — expected: 1× → 1.25× → 1.5× → 0.75× → 1×
- TC-037 (en): Sleep timer select — expected: Sheet closes; button reads '15 minutes'

### FR-13 — Favourites

| Field | Value |
| --- | --- |
| Description | Toggle favourite from any story surface; state consistent and persisted |
| Priority (MoSCoW) | Must |
| Source | Design (multiple) |
| Screens | SCR-03, SCR-04a, SCR-05, SCR-06, SCR-09, SCR-10, SCR-11a |
| Tickets | LS-075 |
| Test cases | TC-017, TC-018 |
| Status | Proposed — awaiting owner review |

Acceptance criteria (from linked tickets):

- Toggle on any screen reflects everywhere (LS-075)
- Persists after restart (LS-075)
- Rapid taps produce consistent state (LS-075)

Test scenarios:

- TC-017 (en): Favourite consistency — expected: Heart active everywhere; listed in Favourites
- TC-018 (en): Favourite persistence — expected: Still favourited

### FR-14 — My Stories library

| Field | Value |
| --- | --- |
| Description | Search, tabs (All, Created by Me, Downloaded, Favourites), recently played, empty states |
| Priority (MoSCoW) | Must |
| Source | Design SCR-12 |
| Screens | SCR-12 |
| Tickets | LS-100 |
| Test cases | TC-038, TC-039 |
| Status | Proposed — awaiting owner review |

Acceptance criteria (from linked tickets):

- Each tab shows correct subset (LS-100)
- Empty states per tab (LS-100)
- Search within tab (LS-100)

Test scenarios:

- TC-038 (en): Tabs filter — expected: Correct subsets
- TC-039 (en): Downloaded empty state — expected: 'Ready for offline adventures?' + Discover stories

### FR-15 — Offline downloads (simulated)

| Field | Value |
| --- | --- |
| Description | Storage summary, list, remove, empty state |
| Priority (MoSCoW) | Could |
| Source | Design SCR-17 |
| Screens | SCR-17 |
| Tickets | LS-101 |
| Test cases | TC-040 |
| Status | Proposed — awaiting owner review |

Acceptance criteria (from linked tickets):

- Download toggles reflect everywhere (LS-101)
- Storage total computed (LS-101)
- Remove updates list (LS-101)

Test scenarios:

- TC-040 (en): Remove download — expected: Row removed; storage total updates; empty state

### FR-16 — Settings hub

| Field | Value |
| --- | --- |
| Description | Parent card and grouped settings rows with working toggles and navigation |
| Priority (MoSCoW) | Must |
| Source | Design SCR-13 |
| Screens | SCR-13 |
| Tickets | LS-110 |
| Test cases | TC-041 |
| Status | Proposed — awaiting owner review |

Acceptance criteria (from linked tickets):

- Toggles independent from row navigation (LS-110)
- Subtitles reflect real settings (LS-110)
- Rows route to real screens (or disabled per LS-004) (LS-110)

Test scenarios:

- TC-041 (en): Toggle independent of row — expected: Toggle flips without navigating; row navigates

### FR-17 — Bedtime reminder settings

| Field | Value |
| --- | --- |
| Description | Enable, time, repeat days, save with toast (local only) |
| Priority (MoSCoW) | Could |
| Source | Design SCR-16 |
| Screens | SCR-16 |
| Tickets | LS-111 |
| Test cases | TC-043, TC-044 |
| Status | Proposed — awaiting owner review |

Acceptance criteria (from linked tickets):

- Disabling disables time & days (LS-111)
- Saved values persist (LS-111)
- Toast shows formatted time (LS-111)

Test scenarios:

- TC-043 (en): Disable reminder — expected: Time and days disabled
- TC-044 (kn): Saved toast time format — expected: Toast shows locale-formatted time in Kannada

### FR-18 — Notifications inbox (mock)

| Field | Value |
| --- | --- |
| Description | Grouped mock notifications, unread markers, mark all read, deep links |
| Priority (MoSCoW) | Could |
| Source | Design SCR-18 |
| Screens | SCR-18 |
| Tickets | LS-112 |
| Test cases | TC-045 |
| Status | Proposed — awaiting owner review |

Acceptance criteria (from linked tickets):

- Mark all read clears dots (LS-112)
- Each row deep-links correctly (LS-112)

Test scenarios:

- TC-045 (en): Mark all read — expected: Unread dots removed

### FR-19 — Demo subscription

| Field | Value |
| --- | --- |
| Description | Plans with illustrative ₹ pricing, selection, confirmation dialog, no payment |
| Priority (MoSCoW) | Could |
| Source | Design SCR-15 |
| Screens | SCR-15 |
| Tickets | LS-113 |
| Test cases | TC-046 |
| Status | Proposed — awaiting owner review |

Acceptance criteria (from linked tickets):

- No payment code or permission (LS-113)
- Flag off hides entry row (LS-113)
- Prices formatted for locale (LS-113)

Test scenarios:

- TC-046 (en): Demo plan confirmation — expected: Dialog says no payment; returns Home

### FR-20 — App language EN / ಕನ್ನಡ

| Field | Value |
| --- | --- |
| Description | Select app language, apply app-wide, persist, English fallback |
| Priority (MoSCoW) | Must |
| Source | Explicit instruction (owner) |
| Screens | SCR-02, SCR-20 |
| Tickets | LS-004, LS-040, LS-045, LS-046 |
| Test cases | TC-006, TC-007, TC-048, TC-049 |
| Status | Proposed — awaiting owner review |

Acceptance criteria (from linked tickets):

- Each open question in 'Risks & Questions' has an owner decision or explicit deferral (LS-004)
- Decisions recorded in docs/project-management/decision-log.md (LS-004)
- Affected tickets updated (LS-004)
- Switching language updates UI without restart and persists (LS-040)
- Registry is the only list of languages (LS-040)
- ViewModels hold no translated text (LS-040)
- Only EN/KN shown (LS-045)
- Choosing ಕನ್ನಡ switches UI immediately (LS-045)
- Selection persists (LS-045)
- Reachable from Profile → Language preferences (LS-046)
- Subtitle on Profile reflects current choice (LS-046)
- Works with future languages without code change (LS-046)

Test scenarios:

- TC-006 (en): Language list from registry — expected: Only 'English' and 'ಕನ್ನಡ' shown
- TC-007 (en→kn): Switch to Kannada in setup — expected: UI switches to Kannada immediately; persists after restart
- TC-048 (en→kn): Change language mid-journey — expected: Same story/page retained; UI in Kannada; no crash
- TC-049 (kn): Fallback when KN missing — expected: English shown, no raw key/crash (CI also fails parity)

### FR-21 — Story language EN / ಕನ್ನಡ

| Field | Value |
| --- | --- |
| Description | Catalog and created stories available in English and Kannada |
| Priority (MoSCoW) | Must |
| Source | Explicit instruction (owner) |
| Screens | SCR-06, SCR-07, SCR-09, SCR-10 |
| Tickets | LS-036, LS-044, LS-081 |
| Test cases | TC-023 |
| Status | Proposed — awaiting owner review |

Acceptance criteria (from linked tickets):

- Six design stories + reader pages load in EN (LS-036)
- Missing KN entry falls back to EN (LS-036)
- No mock content in composables/ViewModels (LS-036)
- All six stories readable in KN (LS-044)
- One deliberate gap tests EN fallback (test data only) (LS-044)
- Empty submit shows error, no navigation (LS-081)
- Typing clears error (LS-081)
- Story language limited to EN/KN (LS-081)

Test scenarios:

- TC-023 (en): Story language options — expected: Only English and ಕನ್ನಡ

### FR-22 — Privacy & parent controls

| Field | Value |
| --- | --- |
| Description | Explain data handling; allow deleting local data |
| Priority (MoSCoW) | Should |
| Source | Design row exists; screen missing |
| Screens | SCR-21 |
| Tickets | LS-004, LS-114 |
| Test cases | None yet — to be added in P1 review |
| Status | Proposed — awaiting owner review |

Acceptance criteria (from linked tickets):

- Each open question in 'Risks & Questions' has an owner decision or explicit deferral (LS-004)
- Decisions recorded in docs/project-management/decision-log.md (LS-004)
- Affected tickets updated (LS-004)
- Content approved by owner (LS-114)
- Delete clears profiles, favourites, progress, downloads (LS-114)

### FR-23 — Help & about

| Field | Value |
| --- | --- |
| Description | Version, licences, help text |
| Priority (MoSCoW) | Could |
| Source | Design row exists; screen missing |
| Screens | SCR-22 |
| Tickets | LS-004, LS-115 |
| Test cases | None yet — to be added in P1 review |
| Status | Proposed — awaiting owner review |

Acceptance criteria (from linked tickets):

- Each open question in 'Risks & Questions' has an owner decision or explicit deferral (LS-004)
- Decisions recorded in docs/project-management/decision-log.md (LS-004)
- Affected tickets updated (LS-004)
- Version shown from BuildConfig (LS-115)
- Licences listed (LS-115)

### FR-24 — Progress & resume

| Field | Value |
| --- | --- |
| Description | Reading and listening progress persisted; resume from Home/My Stories |
| Priority (MoSCoW) | Should |
| Source | Design SCR-03, SCR-12 |
| Screens | SCR-03, SCR-10, SCR-11a, SCR-12 |
| Tickets | LS-070, LS-091 |
| Test cases | TC-033 |
| Status | Proposed — awaiting owner review |

Acceptance criteria (from linked tickets):

- Matches screenshot EN (LS-070)
- KN layout without clipping (LS-070)
- All tap targets navigate per spec (LS-070)
- Empty continue state handled (LS-070)
- Resume opens last page/position (LS-091)
- Progress % shown correctly (LS-091)

Test scenarios:

- TC-033 (en): Resume reading — expected: Opens at page 3 / saved position

### FR-25 — Launch & first-run routing

| Field | Value |
| --- | --- |
| Description | Splash then onboarding on first run, Home afterwards |
| Priority (MoSCoW) | Must |
| Source | Assumption (design missing) |
| Screens | SCR-19 |
| Tickets | LS-004, LS-051 |
| Test cases | TC-057 |
| Status | Proposed — awaiting owner review |

Acceptance criteria (from linked tickets):

- Each open question in 'Risks & Questions' has an owner decision or explicit deferral (LS-004)
- Decisions recorded in docs/project-management/decision-log.md (LS-004)
- Affected tickets updated (LS-004)
- First launch → Onboarding; later → Home (LS-051)
- No blank frame (LS-051)

Test scenarios:

- TC-057 (en): First-run routing — expected: 1st → Onboarding; 2nd → Home

## Open questions carried from the design pack

| Requirement | Question | Risk / decision |
| --- | --- | --- |
| FR-01 | Should slides support swipe and back? Not in prototype. | R-09 / owner |
| FR-02 | Is parent name needed? Which fields are required? What does the avatar "+" tile do? | D14, R-11 |
| FR-04 | Greeting is fixed to "Good evening"; empty states for a new user are not designed. | R-10 |
| FR-05 | Featured "Listen now" opens Detail in the prototype; should it open Player? | owner |
| FR-06 | Category hero is hardcoded to Indian Tales; generic hero per category? | R-18 |
| FR-12 | Where is the "simulated narration" label shown? 15 s glyph overlap. | NFR-12, R-20 |
| FR-17 | Schedule a real local notification in MVP? | D13, ADR-015 |
| FR-20 | One setting for app language and story language, or two? | D9 |
