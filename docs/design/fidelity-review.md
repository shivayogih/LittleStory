# LittleStory — Design Fidelity Review (LS-124)

Status: first pass, 30 Oct 2026. Needs the owner's review per screen (`visual-regression-strategy.md` §1).

## 1. How it was done

Each of the 22 design screenshots in `docs/design/screens/` was put side by side with the app's English render of the same state. The app renders come from the Roborazzi tests (`./gradlew recordRoborazziDebug`, images under each module's `build/outputs/roborazzi/`). Both images were scaled to 430 dp wide. The Kannada renders, including 200 % font scale, were checked for clipping and overlap.

The comparison was done by eye, by an agent. Story data differs on purpose: the tests use small fixtures, so story titles, counts and durations don't match the design. The design pack still shows the old brand name (LittleKatha). Fonts look slightly lighter in the design because it was rendered in a browser. Neither of these counts as a finding.

The results go here and not into the workbook's Screens sheet, because agents don't edit the workbook (it contains drawings). The owner can copy the verdicts across.

Severity: **High** means it is visibly wrong or breaks a rule (clipping, overlap, wrong size). **Medium** means a noticeable difference in layout or style. **Low** means a detail, or a difference that was decided on purpose.

## 2. Fixed in this pass

| Finding | Severity | Screens | Fix |
| --- | --- | --- | --- |
| Card, list-row and settings-row titles were regular weight; the design uses DM Sans 600 | High | Home, Discover, Category, My Stories, Downloads, Detail, Ready, Create, Profile, Children, Notifications, Subscription | New `itemTitle` style (DM Sans 600, 15 sp; Noto Sans Kannada 600) used by every card and row title |
| "Indian Tales" category label cut at both ends by the tile's rounded clip | High | Home | The tile no longer clips its label |
| Brand wordmark and screen title overlapped at 200 % font scale in Kannada | High | Every screen with the brand top bar | `BrandMark` shows only the symbol when the wordmark doesn't fit |
| List art tile was 156 × 104 dp; the design is 78 × 90 dp (156 px was read from the 2× screenshot) | High | My Stories, Category, Downloads | `listArtWidth` 78 dp, `listArtHeight` 90 dp |
| List-row category eyebrow was mixed case; the design is upper case | Medium | My Stories, Category, Downloads | Upper case in Latin scripts, through the existing `uppercaseEyebrows` flag (Kannada has no case) |
| Onboarding text was start-aligned; the design centres it | Medium | Onboarding 1–3 | Eyebrow, title and body centred |

## 3. Per screen

| # | Screen | Verdict | Open differences |
| --- | --- | --- | --- |
| 01–03 | Onboarding | Close | The illustration is simpler than the design (Medium, LS-018 art); the title is bolder (design token is Manrope 800, so the app follows the token) |
| 04 | Setup profile | Close | No parent-name field, by decision D14; age and language are full-width pickers rather than two dropdowns side by side (Low); no "add avatar" tile (Low) |
| 05 | Home | Close | Continue card uses a full-width "Resume story" button, while the design uses a small text link with a play icon (Medium); hero title wraps to three lines (Low) |
| 06 | Discover | Close | The selected filter chip is lavender with a check, while the design uses solid indigo (Medium; same chip as Create and Setup); the featured card's "Listen now" is yellow, the design's is white (Low) |
| 07 | Create | Close | Hero card adds a "Choose the hero" picker (Low, functional); "Create My Story" has no sparkle icon (Low) |
| 08 | Create, error | Differs | The error is a solid red banner above the field; the design shows soft pink text below the field (Medium). The render has More options closed, so the open state was checked on 07 only |
| 09 | Generating | Matches | Adds the "Simulated" note (NFR-12, intended) |
| 10 | Story ready | Differs | Listen and Read sit in the sticky bottom bar, stacked; the design puts them side by side under the summary (Medium) |
| 11 | Reader | Matches | — |
| 12 | Player | Differs | Seek and volume bars use the Material 3 slider (thick track, bar thumb); the design has a thin track with a round thumb (Medium); "Simulated narration" replaces the narrator's name (intended) |
| 13 | Sleep timer sheet | Close | Options are larger and the selected one has a check (Low) |
| 14 | My Stories | Matches after fixes | — |
| 15 | Profile | Close | Child card instead of a parent account card, by decision D14 |
| 16 | Subscription | Differs | Hero moon is a full circle, not a crescent (Medium); plans have no radio buttons (Low); no Restore, Terms or Privacy links, because there are no real payments (intended) |
| 17 | Bedtime reminder | Close | The header card is start-aligned without the large bell and moon art; the design centres it (Low) |
| 18 | Offline downloads | Close | — |
| 19 | Notifications | Close | — |
| 20 | Child profiles | Close | — |
| 21 | Category | Close | The sort control is a full-width picker; the design uses a compact pill (Low) |
| 22 | Story detail | Differs | Same action-bar layout as Story ready (Medium) |

## 4. Proposed next fixes (need the owner's call)

1. Bottom actions side by side (Story ready, Story detail). This changes the shared `StickyActionBar`. Long Kannada labels would need to fall back to stacking.
2. A custom slider for the Player that matches the design's thin track and round thumb.
3. Selected chips in solid indigo, if the design is the source of truth over the current lavender style.
4. A small "Resume story" link on the Home continue card, if a 48 dp target can be kept.
