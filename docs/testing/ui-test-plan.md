# UI test plan

Purpose: per-screen Compose UI test scope, so every screen is verified for behaviour, states and semantics in English and Kannada.
Status: Draft for owner review — P1, 29 Oct 2026
Sources: master orchestrator brief §6.2, §17.2, §17.5; design details §3 (SCR-01…SCR-18); workbook Screens and Test Cases sheets.

Conventions: Compose UI tests on Robolectric; nodes selected by test tag or resource ID; each test parameterised over `en` and `kn`; fixed seed data; no English literals in assertions.

| Screen | States to cover | Interactions | Semantics / a11y | Test cases |
| --- | --- | --- | --- | --- |
| SCR-01 Onboarding | slides 1–3 | Continue, Get Started, Skip | Pager position announced; Skip ≥ 48 dp | TC-001, TC-002, TC-003, TC-055 |
| SCR-02 Setup | default, validation error, KN | name required, avatar select, age, language (registry-driven, EN + ಕನ್ನಡ only), interests, duration | Labels on all fields; chips expose selected state | TC-004, TC-005, TC-006, TC-007 |
| SCR-03 Home | populated, no progress | Create, Resume, See all, category tiles, story cards, heart | Hero CTA label; heart state described | TC-008, TC-009, TC-010, TC-033 |
| SCR-04a Discover | populated, searching | search, clear, filter chips, cards | Search field label; chip selected state | TC-011, TC-012, TC-014 |
| SCR-04b Discover – no results | empty | Clear search | Empty state announced | TC-013 |
| SCR-05 Category | populated, sorted | sort select, row, heart | Row reads title + meta | TC-015 |
| SCR-06 Story detail | default | back, favourite, download (simulated), Listen, Read | Floating buttons labelled | TC-016 |
| SCR-07 Create | default, more options, error | type text, counter, suggestions, theme, mood, duration, story language, submit | Error announced; counter readable | TC-019…TC-023 |
| SCR-08 Generating | progress, paused | ✕, Try again, Back to your idea | Progress announced politely; reduced motion | TC-024…TC-027, TC-055 |
| SCR-09 Story ready | default, KN content | Listen, Read, Saved row | Ribbon announced | TC-028, TC-029 |
| SCR-10 Reader | page 1, middle, last | Previous/Next bounds, A−/A+, play narration | Text size bounds; 200 % font in KN | TC-030, TC-031, TC-032 |
| SCR-11a Player | playing, paused | play/pause, ±15 s, chapter, speed, download, favourite, volume, seek | Simulation disclosed; slider semantics | TC-034, TC-035, TC-036 |
| SCR-11b Sleep timer sheet | open, option selected | pick option, Cancel, scrim dismiss | Sheet focus trapped; selected option announced | TC-037 |
| SCR-12 My Stories | All, Created, Downloaded (empty), Favourites (empty) | tabs, search, play | Tabs as tab role | TC-038, TC-039 |
| SCR-13 Profile hub | default | rows, toggles | Toggle not nested in row button | TC-041 |
| SCR-14 Child profiles | two children | Switch, edit, add | Active state announced | TC-042 |
| SCR-15 Subscription (demo) | plans, dialog | select plan, CTA, Done | "Illustrative pricing" visible | TC-046, TC-047 |
| SCR-16 Bedtime reminder | on, off | toggle disables time/days, days, Save → toast | Day buttons expose pressed state | TC-043, TC-044 |
| SCR-17 Offline downloads | populated, empty | remove (✕), Find a story | Remove labelled per story | TC-040 |
| SCR-18 Notifications | unread, all read | Mark all read, open item | Unread state described | TC-045 |
| SCR-19 Splash | first run, returning | – | – | TC-057 (design pending, D10) |
| SCR-20…SCR-23 | not designed | – | – | Added when designs or patterns are approved (D10) |

Cross-cutting checks per screen: configuration change and language change keep state (brief §6.2); 200 % font scale has no clipping; touch targets ≥ 48 dp (TC-053); content descriptions localized (TC-054).
