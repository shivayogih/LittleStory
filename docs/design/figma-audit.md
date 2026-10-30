# LittleStory — Figma Audit

Purpose: record how the design was inspected, what was verified and what was not, and every discrepancy and gap found, so implementation never relies on unverified design facts silently.

Status: Draft for owner review — P1, 29 Oct 2026

Sources: Figma Make file "Mobile App UI Prototype" (`2DYtUW39GtnsDpUd5jSu6x`) — https://www.figma.com/make/2DYtUW39GtnsDpUd5jSu6x/Mobile-App-UI-Prototype; `LittleStory_Figma_Design_Details.md`, `design-tokens.json` and `screens/` in this folder; master orchestrator brief §3.1 and §5; P0 discovery assessment (29 Oct 2026).

## 1. Access status

| Check (29 Oct 2026) | Result |
| --- | --- |
| Figma account used by agents | Owner's account (View seat on two Starter teams) |
| File type | Figma **Make** (code-based prototype), not a Design file |
| Figma MCP file tools (`get_metadata`, `create_session`, design context) | Refused: "not supported for Make files" |
| Figma MCP resource read of the Make project | Not implemented in the agent client |
| Live inspection of frames, variables, prototype links | **Not possible in P0** |

Consequence: everything in this folder comes from the **design pack**, which was extracted from the Make prototype's source code, compiled stylesheet and running build earlier on 29 Oct 2026. It is the working design reference until the owner provides fresh exports or confirms it is current. Facts that could change in Figma are marked *Needs Figma verification* across the docs (risk R-23, design drift).

## 2. Method used to build the design pack

| Artefact | How it was produced | Reliability |
| --- | --- | --- |
| `design-tokens.json` | Read from the prototype's compiled CSS custom properties and rules | High for values that exist in CSS; no Figma variables exist in a Make file |
| `LittleStory_Figma_Design_Details.md` | Prototype source (`App.tsx`, components) and stylesheet; screen-by-screen specs | High for copy, structure and behaviour as coded |
| `screens/*.jpg` (22) | Screenshots of the running build at 430 px width, 2× scale | High for visuals at capture time; captured before the rename, so they show the old brand |

## 3. What was verified vs not

| Item | Verified from design pack | Not verified / missing |
| --- | --- | --- |
| Screen inventory | 18 screens, 22 captured states (SCR-01…SCR-18) | Splash (SCR-19), Language preferences (SCR-20), Privacy & parent controls (SCR-21), Help & about (SCR-22), Account settings / Audio preferences (SCR-23) — **no design** |
| Separately captured states | 22 | Discover no-results (SCR-04b) and Generating paused state: specified, not captured |
| Colour tokens | All brand, surface, accent, text, semantic, story-art and illustration colours | Figma variables/styles (none exist in Make) |
| Typography | Families, weights, sizes, line heights, tracking per role | Kannada glyph support (fonts are Latin-only — see §6) |
| Shape, shadow, layout, breakpoints | Radii, shadows, top bar 68 px, bottom nav 76 px, ≤360 px and ≥900 px breakpoints | Exact dp mapping on Android (430 CSS px shell ≠ device dp; see `design-system.md`) |
| Icons | 21 line icons, 24×24, 1.8 stroke | Source SVG paths must be re-extracted when icons are implemented (LS-013) |
| Motion | Twinkle, button press, toggles, progress timings | Screen transitions and sheet animations: **none designed** (R-09) |
| Prototype connections | Navigation per screen from source | Several rows route to placeholder screens (R-19) |
| Text inside images | None: all illustrations are drawn in code | – |
| Language selection UI | Setup "Language" select (7 options); Child profile cards show a language | Dedicated language screen; native-script display of names |
| Kannada versions of screens | – | None exist; English frames are the layout reference (brief §16) |

## 4. Brand rename

The product is **LittleStory** (formerly LittleKatha). The screenshots still show "LittleKatha" in the logo (SCR-01, SCR-03, SCR-04a, SCR-12, SCR-13) and "LITTLEKATHA PREMIUM" (SCR-15). Implementation uses LittleStory only; no "LittleKatha" string may appear in code or resources (LS-014 acceptance criterion). Final logo confirmation is decision D11 (R-01). There is no raster logo; the mark is drawn in code (skewed book outline, inner "L", crescent moon, yellow star).

## 5. Brief §5 reference palette vs extracted tokens

All 13 reference colours in the brief match the extracted tokens exactly. The extraction adds tokens the brief does not list.

| Brief token | Brief value | Extracted token | Match |
| --- | --- | --- | --- |
| Primary indigo | #35256F | `indigo` #35256F | Yes |
| Deep night | #17152F | `night` #17152F | Yes |
| Night surface | #242044 | `night-surface` #242044 | Yes |
| Lavender | #A995F5 | `lavender` #A995F5 | Yes |
| Soft lavender | #EAE3FF | `soft-lavender` #EAE3FF | Yes |
| Warm cream | #FFF9F0 | `cream` #FFF9F0 | Yes |
| White | #FFFFFF | `white` #FFFFFF | Yes |
| Peach | #FFC9AD | `peach` #FFC9AD | Yes |
| Soft yellow | #FFD977 | `yellow` #FFD977 | Yes |
| Success | #287A54 | `success` #287A54 | Yes |
| Error | #B42318 | `error` #B42318 | Yes |
| Primary text | #25213A | `text` #25213A | Yes |
| Secondary text | #716B80 | `muted` #716B80 | Yes |

Additional extracted tokens: `indigo-2` #4B388B, `surface` #F8F5FB, `line` #E9E4EC, `page-backdrop` #E8E5ED (web only, not needed on Android), `moon` #FFF1BD, `reader-body` #393348, `nav-inactive` #817B8D, `success-bg` #EAF5EE, `favourite` #9D3158, `favourite-bg` #FFEDF2, six story-art gradient pairs and nine illustration colours.

## 6. Discrepancies and issues found

| # | Issue | Where | Impact | Action |
| --- | --- | --- | --- | --- |
| A1 | Old brand in screenshots | screens/ | Visual references show wrong name | Use LittleStory (D11, R-01) |
| A2 | Setup offers 7 languages; brief MVP is EN + KN | SCR-02, SCR-14 | Scope | Registry-driven list, EN + ಕನ್ನಡ only (D9, R-02) |
| A3 | DM Sans and Manrope have no Kannada glyphs (per their published coverage; font files could not be downloaded in the agent workspace to confirm) | All text | Kannada would fall back to an arbitrary system font | Bundle Noto Sans Kannada (OFL) as fallback (D8, R-03); see `docs/localization/kannada-typography.md` |
| A4 | Skin colour differs between documents: details §1.6 says `#D58C68`; tokens JSON says `skin` `#d9986c` | Illustrations | Minor visual drift | Treat `design-tokens.json` as the token source; verify against screenshots in LS-018 |
| A5 | Details doc refers to `design-tokens/design-tokens.json` and a companion `LittleStory_App_Overview.md`; the actual path is `docs/design/design-tokens.json` and the overview file is not in the repository | Docs | Broken references | Correct references in a follow-up edit to the details doc (out of P1 scope for this file; flagged) |
| A6 | Culture banner background: workbook Screens sheet says `#F5DFC0`; tokens JSON has no such token; details calls it "peach card" | SCR-03 | Untokenised colour | Add a named token after verifying against `05-home.jpg` (LS-010) |
| A7 | Design-pack accessibility note says muted `#716B80` text is "likely below 4.5:1" | SCR-03 etc. | – | Measured: 4.87:1 on cream, 5.10:1 on white (passes AA). Nav-inactive `#817B8D` does fail (3.89:1 on cream). See `accessibility-spec.md` |
| A8 | Toggles nested inside row buttons | SCR-13 | Accessibility defect | Separate targets (TC-041) |
| A9 | Overlapping 15 s rewind/forward glyphs | SCR-11a | Visual defect | LS-094 (R-20) |
| A10 | Category hero hardcoded to Indian Tales ("18 STORIES") | SCR-05 | Not data-driven | Generic hero per category (R-18) |
| A11 | Rows route to placeholder screens (Account → Setup, Audio → Player, Language → Setup, Privacy/Help → Notifications) | SCR-13 | Missing destinations | D10 (R-19) |
| A12 | Gendered sample copy "helped his friend" | SCR-07 | Translation and inclusivity | Neutral copy (R-06) |
| A13 | Greeting always "Good evening" | SCR-03 | Content | Time-of-day or neutral greeting (R-10) |
| A14 | Fixed web shell width 430 px | All | Android screens vary | Responsive layouts; 430 px used only as the screenshot comparison width |

## 7. Requests to the owner

1. Confirm the design pack still matches the live Make prototype, or export current frames (PNG/PDF) after any change.
2. Provide designs, or approve building from existing components, for SCR-19 to SCR-23 (D10).
3. Confirm the final LittleStory wordmark (D11).
4. If Kannada screen designs are wanted, provide at least one reference frame; otherwise Kannada layout decisions are recorded per screen in `screen-inventory.md`.
