# LittleStory — Edge Cases

Purpose: list the unusual inputs, states and environments each screen must handle, so tickets and tests cover them from the start.

Status: Draft for owner review — P1, 29 Oct 2026

Sources: master orchestrator brief §6.2 (screen-state matrix), §8.3, §4.4, §10; `docs/design/LittleStory_Figma_Design_Details.md`; workbook Screens and Test Cases sheets. Expected behaviour marked *Proposed* has no design and needs owner confirmation.

## Cross-cutting edge cases

| ID | Edge case | Expected behaviour | Requirement | Test |
| --- | --- | --- | --- | --- |
| EC-01 | App language changed while a screen is open (Reader page 2, Create form half-filled, Player playing) | Activity recreates; navigation back stack, form input, reader page and player position survive; UI and mock content switch language | FR-20, NFR-11 | TC-048 |
| EC-02 | A Kannada string key is missing | English value shown, never a raw key or crash; CI parity check fails the build | NFR-02 | TC-049, TC-050 |
| EC-03 | A story has no Kannada translation, or only some fields | Per-field English fallback; story ID unchanged; no "missing" text shown | FR-21, NFR-06 | add TC (P1) |
| EC-04 | Long Kannada text (titles, buttons, chips, tab labels, bottom-nav labels at 10 sp) | Text wraps or the row scrolls horizontally; nothing clipped or overlapped; buttons grow in height | NFR-14 | TC-003, TC-052 |
| EC-05 | System font scale 200 % | Every screen scrolls; no clipped text; sticky bars stay usable | NFR-04 | TC-032 |
| EC-06 | Process death in background (profile, favourites, progress, create form) | Persisted data restored; transient form input restored via saved state | NFR-11 | TC-058 |
| EC-07 | Rotation / configuration change | Same as EC-06 without data loss; layouts adapt | NFR-11 | add TC (P1) |
| EC-08 | Compact phone (≤360 dp wide) | Compact variants from design: hero title 22 sp, 16 dp side padding, smaller player cover | NFR-10, NFR-16 | add TC (P1) |
| EC-09 | Tall phone (≥900 dp high) | Larger player cover and taller onboarding art per design breakpoint | NFR-10 | add TC (P1) |
| EC-10 | Reduced motion / animations off | Infinite twinkle animations stop; transitions instant | NFR-04 | TC-055 |
| EC-11 | Rapid repeated taps (favourite heart, Create, Save reminder) | Single resulting state; no duplicate saves or double navigation | NFR-11 | TC-018 (extend) |
| EC-12 | Pseudo-locales `en-XA` and `ar-XB` in debug | No clipping, no hardcoded strings visible, layout mirrors correctly | NFR-14 | TC-052 |
| EC-13 | Kannada keyboard input in text fields (Setup names, Create text, searches) | Accepted, counted correctly by characters, searchable | FR-05, FR-08 | TC-012 |
| EC-14 | Device with no Kannada TTS voice | TalkBack limitation documented; labels still localized | NFR-04 | TC-054 |
| EC-15 | Empty data: no profile, no stories saved, no downloads, no notifications | Each list shows its empty state; screens without a designed empty state use the shared EmptyState component (Proposed) | FR-04, FR-14, FR-15, FR-18 | TC-009, TC-039 |
| EC-16 | Clock or timezone change while a reminder is set | Displayed time re-formatted for locale; no crash (reminder is UI-only in MVP, D13) | FR-17 | add TC (P1) |

## By screen

| Screen | Edge cases |
| --- | --- |
| SCR-01 Onboarding | Back button on slide 2/3 (prototype has no back: Proposed system back goes to previous slide, then exits); Kannada body taller than the 70 px min-height; reduced motion. |
| SCR-02 Setup | Empty child name (validation rule pending decision, TC-005); name with Kannada script; very long name (limit from `AppConfig`); age outside 3–12 impossible by control; no interests selected (Optional, allowed); "Skip for now" leaves no profile (EC-15); parent name/email removed if D14 approved. |
| SCR-03 Home | No continue-listening item (TC-009); no "Made for" stories; child name long or in Kannada inside "Stories for {child}" format string; greeting time-of-day (R-10). |
| SCR-04a/b Discover | Search with leading/trailing spaces; case-insensitive match; Kannada partial match (TC-012); query matching nothing (SCR-04b); chip filter plus search combined; clearing restores featured card and "All". |
| SCR-05 Category | Category with 0 or 1 stories ("{n} stories" plurals); sort ties ordered by stable ID; category without hero art. |
| SCR-06 Story detail | Story missing Kannada synopsis (EC-03); not downloaded vs downloaded; favourite toggled on another screen. |
| SCR-07 Create | Empty or whitespace-only text (error); exactly 500 characters; paste over limit (capped, TC-021); emoji and Kannada combining characters counted per user-perceived character (Proposed); suggestion chip after manual text (replaces or appends — Proposed: replaces, matching prototype); only one child profile in hero selector. |
| SCR-08 Generating | Close at any percent → Paused; Try again restarts from the configured start value; back gesture while generating (Proposed: same as Close); app backgrounded mid-generation (Proposed: simulation pauses and resumes). |
| SCR-09 Story ready | Back from Story ready (Proposed: to Home, not to Generating); story generated in Kannada. |
| SCR-10 Reader | Page 1 Previous disabled; last page Next disabled; text scale clamps 0.9–1.3 (TC-031); system font 200 % combined with A+ (EC-05); a one-page story. |
| SCR-11a/b Player | Seek to 0 and end; rewind below 0 clamps; forward past end ends playback; speed cycle wraps; sleep timer "End of story"; timer chosen then playback ends early. |
| SCR-12 My Stories | Each tab empty (two different empty messages); search within a tab with no results; a story both created and favourited appears in both tabs. |
| SCR-13 Profile | Toggle tapped vs row tapped (TC-041); demo subscription flag off hides the row; rows without destinations disabled or hidden (D10). |
| SCR-14 Child profiles | Only one child (no Switch); switching while a story plays (Proposed: player stops); deleting a profile is not designed. |
| SCR-15 Subscription | Flag off: screen unreachable; plan names long in Kannada; price formatting per locale. |
| SCR-16 Reminder | Toggle off disables time and days (TC-043); all days deselected (Proposed: Save disabled); 12/24-hour device setting. |
| SCR-17 Downloads | Storage bar minimum 3 % width when tiny; remove last item → empty state; sizes formatted per locale. |
| SCR-18 Notifications | No notifications (empty state not designed); Mark all read with none unread; deep link to a story that no longer exists. |
| SCR-19–23 | No designs; edge cases defined once designs or patterns are approved (D10). |

## Data and content edge cases (mock data, brief §10)

The deterministic seed data in `assets/mock/` must include, in both English and Kannada:

- multiple stories of different lengths, categories and reading ages;
- a story with a long title, including a long Kannada title;
- a story with a missing or placeholder cover;
- one story deliberately missing its Kannada translation (test data only) to prove English fallback;
- empty-library and error-condition fixtures for tests;
- saved and unsaved stories; two child profiles.

No random IDs or current timestamps in screenshot or unit tests.
