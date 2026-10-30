# LittleStory — Non-Functional Requirements

Purpose: the catalogue of quality requirements (NFR-01…NFR-12 from the workbook, plus NFR-13…NFR-17 newly proposed from the brief) with acceptance criteria, tickets and tests.

Status: Draft for owner review — P1, 29 Oct 2026

Sources: workbook `docs/project-management/LittleStory_Jira_Backlog.xlsx` (Requirements, Backlog, Test Cases sheets); master orchestrator brief §3.5, §4.4–4.5, §8; P0 discovery assessment.

## Summary

| ID | Title | MoSCoW | Origin |
| --- | --- | --- | --- |
| NFR-01 | No hardcoding | Must | Workbook |
| NFR-02 | Localization completeness | Must | Workbook |
| NFR-03 | Future languages | Must | Workbook |
| NFR-04 | Accessibility | Must | Workbook |
| NFR-05 | Architecture | Must | Workbook |
| NFR-06 | Deterministic data | Must | Workbook |
| NFR-07 | Privacy & child safety | Must | Workbook |
| NFR-08 | Performance | Should | Workbook |
| NFR-09 | Cross-platform development | Must | Workbook |
| NFR-10 | Visual fidelity | Must | Workbook |
| NFR-11 | Reliability | Must | Workbook |
| NFR-12 | Honest simulations | Must | Workbook |
| NFR-13 | Locale-aware formatting | Must | NEW (proposed) — brief §4.4 rule 12, §8.7 |
| NFR-14 | RTL and text-expansion readiness | Must | NEW (proposed) — brief §4.4 rules 10–11, 18 |
| NFR-15 | Configurability and "How to change X" | Must | NEW (proposed) — brief §4.5, §8.8 |
| NFR-16 | Compatibility and device matrix | Must | NEW (proposed) — brief §8.6 |
| NFR-17 | Build verifiability | Must | NEW (proposed) — P0 finding R-21 |

## Requirements from the workbook

### NFR-01 — No hardcoding

| Field | Value |
| --- | --- |
| Description | No hardcoded user-facing text, colours, dimensions, typography, routes, limits or config |
| Priority (MoSCoW) | Must |
| Source | Explicit instruction (owner) |
| Screens | All |
| Tickets | LS-005, LS-010, LS-011, LS-012, LS-033, LS-034, LS-050, LS-125, LS-150 |
| Test cases | TC-051 |
| Status | Proposed — awaiting owner review |

Acceptance criteria (from linked tickets):

- Both files exist with consistent rules (LS-005)
- Rules reference docs paths (LS-005)
- Approval levels A–F stated (LS-005)
- Every token in design-tokens.json exists exactly once in :core:designsystem (LS-010)
- No hex literal outside token files (lint/detekt check) (LS-010)
- Preview sheet shows all swatches (LS-010)
- All text styles from design spec available as theme tokens (LS-011)
- Kannada renders with correct glyphs in every style (LS-011)
- Licences listed in Help & about / docs (LS-011)
- No inline TextStyle in feature code (LS-011)
- Tokens defined once in designsystem (LS-012)
- Compact (≤360dp) and tall (≥900dp) variants handled via window size, not hardcoded checks (LS-012)
- Reduced-motion flag disables infinite twinkle (LS-012)
- No magic numbers in feature code for these values (LS-033)
- Flags toggle features without code edits elsewhere (LS-033)
- docs/architecture/configuration-and-constants.md lists every value (LS-033)
- Introducing a hardcoded string or hex colour in a feature fails the build (LS-034)
- Baseline file empty (LS-034)
- All prototype navigation paths work (LS-050)
- Back from each screen matches spec (LS-050)
- Routes defined in one place (LS-050)
- Zero hardcoded values in feature modules (LS-125)
- Findings fixed (LS-125)
- Every central config point documented with file path (LS-150)

Test scenarios:

- TC-051 (–): Hardcoded text blocked — expected: Lint/detekt fails

### NFR-02 — Localization completeness

| Field | Value |
| --- | --- |
| Description | Every string in EN and KN; CI key-parity check; lint MissingTranslation = error |
| Priority (MoSCoW) | Must |
| Source | Explicit instruction (owner) |
| Screens | All |
| Tickets | LS-005, LS-011, LS-034, LS-040, LS-041, LS-043, LS-047 |
| Test cases | TC-003, TC-050 |
| Status | Proposed — awaiting owner review |

Acceptance criteria (from linked tickets):

- Both files exist with consistent rules (LS-005)
- Rules reference docs paths (LS-005)
- Approval levels A–F stated (LS-005)
- All text styles from design spec available as theme tokens (LS-011)
- Kannada renders with correct glyphs in every style (LS-011)
- Licences listed in Help & about / docs (LS-011)
- No inline TextStyle in feature code (LS-011)
- Introducing a hardcoded string or hex colour in a feature fails the build (LS-034)
- Baseline file empty (LS-034)
- Switching language updates UI without restart and persists (LS-040)
- Registry is the only list of languages (LS-040)
- ViewModels hold no translated text (LS-040)
- Missing KN key fails CI with clear message (LS-041)
- Mismatched %1$s fails CI (LS-041)
- 100% key coverage in KN (LS-043)
- Glossary covers recurring terms (LS-043)
- Translation Tracking sheet updated (LS-043)
- All KN rows 'Approved' (LS-047)
- Issues logged and fixed (LS-047)

Test scenarios:

- TC-003 (kn): Onboarding in Kannada — expected: All text Kannada, no clipping, button fits
- TC-050 (–): Translation parity CI — expected: Build fails with key name

### NFR-03 — Future languages

| Field | Value |
| --- | --- |
| Description | Adding a language = resources + registry + locales_config only |
| Priority (MoSCoW) | Must |
| Source | Explicit instruction (owner) |
| Screens | All |
| Tickets | LS-040, LS-042, LS-048 |
| Test cases | TC-052 |
| Status | Proposed — awaiting owner review |

Acceptance criteria (from linked tickets):

- Switching language updates UI without restart and persists (LS-040)
- Registry is the only list of languages (LS-040)
- ViewModels hold no translated text (LS-040)
- Key screens render under en-XA without clipping (LS-042)
- ar-XB mirrors layout correctly (LS-042)
- Dry run adds language with resources + registry + locales_config only (LS-048)

Test scenarios:

- TC-052 (en-XA): Pseudo-locale expansion — expected: No clipping/overlap

### NFR-04 — Accessibility

| Field | Value |
| --- | --- |
| Description | 48dp targets, WCAG AA contrast, TalkBack labels EN/KN, font scaling, reduced motion |
| Priority (MoSCoW) | Must |
| Source | Master prompt §8.4 + design a11y gaps |
| Screens | All |
| Tickets | LS-012, LS-015, LS-016, LS-017, LS-019, LS-094, LS-110, LS-123 |
| Test cases | TC-032, TC-053, TC-054, TC-055 |
| Status | Proposed — awaiting owner review |

Acceptance criteria (from linked tickets):

- Tokens defined once in designsystem (LS-012)
- Compact (≤360dp) and tall (≥900dp) variants handled via window size, not hardcoded checks (LS-012)
- Reduced-motion flag disables infinite twinkle (LS-012)
- All variants + disabled + pressed previews (LS-015)
- Touch target ≥48dp (LS-015)
- contentDescription required parameter for IconButton (LS-015)
- Each component exposes selected/checked semantics (LS-016)
- Focus states visible (LS-016)
- Long Kannada labels wrap/scroll without clipping (LS-016)
- Sheet/dialog trap focus and dismiss on back (LS-017)
- Toast duration from config (LS-017)
- Progress exposes progress semantics (LS-017)
- BottomNav labels fit in Kannada at 10sp (or approved fallback) (LS-019)
- Insets correct with edge-to-edge (LS-019)
- Selected tab announced (LS-019)
- Icons legible, labelled 'Rewind 15 seconds'/'Forward 15 seconds' (LS-094)
- Toggles independent from row navigation (LS-110)
- Subtitles reflect real settings (LS-110)
- Rows route to real screens (or disabled per LS-004) (LS-110)
- All Must a11y issues fixed or accepted by owner (LS-123)
- Report in docs/design/accessibility-spec.md (LS-123)

Test scenarios:

- TC-032 (kn): Kannada reading at 200% font — expected: Text wraps, scrolls, no clipping
- TC-053 (en): Touch targets — expected: All targets ≥48dp
- TC-054 (en/kn): TalkBack labels — expected: All controls labelled in current language; logical order
- TC-055 (en): Reduced motion — expected: Twinkle animations disabled

### NFR-05 — Architecture

| Field | Value |
| --- | --- |
| Description | Clean layers, no circular deps, domain free of Android/UI |
| Priority (MoSCoW) | Must |
| Source | Master prompt §9 |
| Screens | All |
| Tickets | LS-008, LS-031, LS-037, LS-120 |
| Test cases | None yet — to be added in P1 review |
| Status | Proposed — awaiting owner review |

Acceptance criteria (from linked tickets):

- 15 ADRs in docs/architecture/adr/ (LS-008)
- Each references affected tickets (LS-008)
- Owner approves or comments (LS-008)
- Modules compile with dependency rules enforced (LS-031)
- No duplicated Gradle blocks (LS-031)
- Module graph documented (LS-031)
- Domain has no Android/Compose imports (LS-037)
- Models documented in docs/architecture/data-models.md (LS-037)
- Coverage report in CI (LS-120)
- Agreed threshold met (LS-120)

### NFR-06 — Deterministic data

| Field | Value |
| --- | --- |
| Description | Stable IDs, per-language mock JSON, no random/timestamps in tests |
| Priority (MoSCoW) | Must |
| Source | Master prompt §10 |
| Screens | All |
| Tickets | LS-036 |
| Test cases | None yet — to be added in P1 review |
| Status | Proposed — awaiting owner review |

Acceptance criteria (from linked tickets):

- Six design stories + reader pages load in EN (LS-036)
- Missing KN entry falls back to EN (LS-036)
- No mock content in composables/ViewModels (LS-036)

### NFR-07 — Privacy & child safety

| Field | Value |
| --- | --- |
| Description | Minimal child data, no network/analytics/secrets in MVP |
| Priority (MoSCoW) | Must |
| Source | Master prompt §8.5 |
| Screens | All |
| Tickets | LS-038, LS-061, LS-114, LS-126 |
| Test cases | TC-047 |
| Status | Proposed — awaiting owner review |

Acceptance criteria (from linked tickets):

- Data survives process death (LS-038)
- In-memory Room tests (LS-038)
- No child data logged (LS-038)
- Defaults as designed for demo data only (empty for real first run – decision) (LS-061)
- CTA text includes child name via format arg (LS-061)
- Profile saved and Home shows name (LS-061)
- Content approved by owner (LS-114)
- Delete clears profiles, favourites, progress, downloads (LS-114)
- Threat model and checklist complete (LS-126)
- Findings resolved (LS-126)

Test scenarios:

- TC-047 (en): No billing in build — expected: No billing permission/library

### NFR-08 — Performance

| Field | Value |
| --- | --- |
| Description | No main-thread blocking, lazy lists, stable state |
| Priority (MoSCoW) | Should |
| Source | Master prompt §8.2 |
| Screens | All |
| Tickets | – |
| Test cases | None yet — to be added in P1 review |
| Status | Proposed — awaiting owner review |

### NFR-09 — Cross-platform development

| Field | Value |
| --- | --- |
| Description | Builds identically on Windows and macOS; UTF-8; line endings normalised |
| Priority (MoSCoW) | Must |
| Source | Explicit instruction (owner) |
| Screens | – |
| Tickets | LS-001, LS-005, LS-030, LS-132 |
| Test cases | TC-056 |
| Status | Proposed — awaiting owner review |

Acceptance criteria (from linked tickets):

- Discovery report covers all 17 items of master prompt §2.2 (LS-001)
- Zero files changed (git status clean before/after) (LS-001)
- Toolchain versions and compatibility issues listed (LS-001)
- Report ends with Approval gate question (LS-001)
- Both files exist with consistent rules (LS-005)
- Rules reference docs paths (LS-005)
- Approval levels A–F stated (LS-005)
- ./gradlew and gradlew.bat assembleDebug succeed on macOS and Windows (LS-030)
- No machine-specific paths committed (LS-030)
- No whole-file line-ending diffs (LS-030)
- A new developer builds on Windows and macOS following the doc (LS-132)

Test scenarios:

- TC-056 (–): Build on Windows & macOS — expected: Both succeed; no git diffs

### NFR-10 — Visual fidelity

| Field | Value |
| --- | --- |
| Description | Screens match the design pack screenshots to agreed tolerance |
| Priority (MoSCoW) | Must |
| Source | Master prompt §16 |
| Screens | All |
| Tickets | LS-002, LS-010, LS-013, LS-018, LS-020, LS-122, LS-124 |
| Test cases | None yet — to be added in P1 review |
| Status | Proposed — awaiting owner review |

Acceptance criteria (from linked tickets):

- All prototype screens listed with screenshot (LS-002)
- Tokens JSON produced (LS-002)
- Component catalogue and per-screen specs written (LS-002)
- Missing designs identified (LS-002)
- Every token in design-tokens.json exists exactly once in :core:designsystem (LS-010)
- No hex literal outside token files (lint/detekt check) (LS-010)
- Preview sheet shows all swatches (LS-010)
- All icons in res/drawable with consistent naming ic_* (LS-013)
- Auto-mirroring set where directional (back, chevron) for RTL (LS-013)
- Icon catalogue preview (LS-013)
- All six motifs render matching screenshots (LS-018)
- Art colours come from tokens/story data, not literals (LS-018)
- Cards announce title, category, duration, favourite state (LS-018)
- Baselines committed and reviewed by owner (LS-020)
- Catalogue excluded from release build (LS-020)
- Baselines reviewed by owner (LS-122)
- Diffs fail CI (LS-122)
- Report per screen (LS-124)
- Blocker/High fixed (LS-124)

### NFR-11 — Reliability

| Field | Value |
| --- | --- |
| Description | State survives rotation, process recreation (where persisted) and language change |
| Priority (MoSCoW) | Must |
| Source | Master prompt §8.3 |
| Screens | All |
| Tickets | LS-038 |
| Test cases | TC-058 |
| Status | Proposed — awaiting owner review |

Acceptance criteria (from linked tickets):

- Data survives process death (LS-038)
- In-memory Room tests (LS-038)
- No child data logged (LS-038)

Test scenarios:

- TC-058 (en): Process death — expected: State restored

### NFR-12 — Honest simulations

| Field | Value |
| --- | --- |
| Description | Simulated generation, audio, downloads, subscription clearly labelled |
| Priority (MoSCoW) | Must |
| Source | Master prompt §4.3 |
| Screens | SCR-08, SCR-11a, SCR-15, SCR-17 |
| Tickets | LS-082, LS-092, LS-101, LS-113 |
| Test cases | TC-036 |
| Status | Proposed — awaiting owner review |

Acceptance criteria (from linked tickets):

- Same input → same output (LS-082)
- Cancel stops progress (LS-082)
- Result saved to Created by Me (LS-082)
- Controls behave as spec (LS-092)
- Timer text formatted mm:ss (LS-092)
- Simulation clearly indicated (LS-092)
- Download toggles reflect everywhere (LS-101)
- Storage total computed (LS-101)
- Remove updates list (LS-101)
- No payment code or permission (LS-113)
- Flag off hides entry row (LS-113)
- Prices formatted for locale (LS-113)

Test scenarios:

- TC-036 (en): Simulation disclosed — expected: Simulated narration indicated

## Newly proposed requirements (NEW — not yet in the workbook)

These close gaps between the brief and the workbook. They will be added to the Requirements sheet when the owner accepts them.

### NFR-13 — Locale-aware formatting (NEW, proposed)

| Field | Value |
| --- | --- |
| Description | Numbers, percentages, currency, dates, times and durations shown to users are formatted with locale-aware APIs, never assembled by hand |
| Priority (MoSCoW) | Must |
| Source | Brief §4.4 rule 12, §8.7 |
| Screens | SCR-03 (42 % complete), SCR-08 (%), SCR-11a (2:28 / 8:00), SCR-13 (8:30 PM), SCR-15 (₹ prices), SCR-16 (reminder time, weekday initials), SCR-17 (MB) |
| Tickets | LS-040, LS-092, LS-111, LS-113, LS-101 |
| Test cases | TC-034, TC-044 (existing); add a Kannada currency/number case in P1 review |
| Status | Proposed — awaiting owner review |

Acceptance criteria:

- No string concatenation or `String.format` without a locale for user-visible numbers, dates or durations (lint/review).
- Weekday names and initials come from the platform for the active locale.
- Currency uses the locale's currency formatting with the ₹ symbol.

### NFR-14 — RTL and text-expansion readiness (NEW, proposed)

| Field | Value |
| --- | --- |
| Description | Layouts use start/end, flexible sizing and wrapping so Kannada and future RTL languages fit without clipping |
| Priority (MoSCoW) | Must |
| Source | Brief §4.4 rules 10, 11, 18; §8.7 |
| Screens | All |
| Tickets | LS-013 (auto-mirrored icons), LS-016, LS-019, LS-042 |
| Test cases | TC-052 (en-XA); add ar-XB mirroring case |
| Status | Proposed — awaiting owner review |

Acceptance criteria:

- `android:supportsRtl="true"`; no left/right padding or alignment in feature code.
- Key screens render under `en-XA` and `ar-XB` pseudo-locales without clipping or overlap.
- No fixed-height text containers; text containers use min sizes and wrap.

### NFR-15 — Configurability and "How to change X" (NEW, proposed)

| Field | Value |
| --- | --- |
| Description | Every design value, text, limit, route, feature flag and environment value has exactly one documented home; a "How to change X" guide is kept current |
| Priority (MoSCoW) | Must |
| Source | Brief §4.5, §8.8, §9.6 |
| Screens | All |
| Tickets | LS-033, LS-150, LS-125 |
| Test cases | TC-051 (hardcoded text blocked); add a hex-colour/dp literal lint case |
| Status | Proposed — awaiting owner review |

Acceptance criteria:

- `docs/architecture/configuration-and-constants.md` lists every configurable value and its file.
- `docs/architecture/how-to-change.md` covers colour, string, spacing, mock story, feature flag, language and screen.
- Changing wording, a colour, a limit or a mock story edits one file only.

### NFR-16 — Compatibility and device matrix (NEW, proposed)

| Field | Value |
| --- | --- |
| Description | Supported Android versions, screen sizes and the emulator/device test matrix are documented and tested, including per-app language behaviour below Android 13 and Kannada font rendering on older devices |
| Priority (MoSCoW) | Must |
| Source | Brief §8.6 |
| Screens | All |
| Tickets | LS-030, LS-132 |
| Test cases | To add in P1 review: per-app language on API 26–32 vs 33+; compact (≤360 dp) and tall (≥900 dp) layouts |
| Status | Proposed — awaiting owner review (minSdk 26 is decision D5, Proposed) |

### NFR-17 — Build verifiability (NEW, proposed)

| Field | Value |
| --- | --- |
| Description | Every PR's build, lint, unit, translation and screenshot checks run in an environment that can fetch Android dependencies, and results are attached before review |
| Priority (MoSCoW) | Must |
| Source | P0 finding: the cloud agent workspace cannot reach `dl.google.com` (R-21); brief §17.7 "do not report success unless the command actually completed" |
| Screens | – |
| Tickets | LS-130 (CI), LS-030 |
| Test cases | TC-056 |
| Status | Proposed — depends on decision D3 |
