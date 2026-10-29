# Kannada typography

Purpose: make Kannada text render correctly, without clipping, in a design built on Latin-only fonts.

Status: Draft for owner review — P1, 29 Oct 2026 (ADR-008 fonts, decision D8 — Proposed)

Sources: design doc §1.2 (DM Sans + Manrope type scale) · `docs/design/design-tokens.json` · brief §4.4 rule 9, §5.1 · workbook R-03, R-04.

## 1. The gap

| Design font | Role | Kannada glyphs | Licence |
| --- | --- | --- | --- |
| DM Sans 400/500/600/700 | body, buttons, labels | no — published coverage is Latin only | SIL OFL 1.1 |
| Manrope 500/600/700/800 | headings, brand, top-bar titles, reader paragraphs | no — published coverage is Latin, Cyrillic, Greek | SIL OFL 1.1 |

Verification note: this is based on the fonts' published language coverage. The font files could not be downloaded in the P0/P1 cloud workspace (fonts.google.com is blocked, R-21), so LS-011 must confirm by inspecting the bundled TTF `cmap` tables.

Without a fallback, Android substitutes the system Kannada font, which differs per device and does not match weights. That breaks visual consistency and screenshot determinism.

## 2. Proposal

Bundle **Noto Sans Kannada** (SIL OFL 1.1, variable weight) in `:core:designsystem/src/main/res/font/` and build each `FontFamily` so Latin text uses the design font and Kannada text uses Noto Sans Kannada at a matching weight.

| Design weight | Latin font | Kannada font |
| --- | --- | --- |
| 400 | DM Sans Regular | Noto Sans Kannada Regular |
| 500 | DM Sans / Manrope Medium | Noto Sans Kannada Medium |
| 600 | DM Sans / Manrope SemiBold | Noto Sans Kannada SemiBold |
| 700–800 | DM Sans Bold / Manrope ExtraBold | Noto Sans Kannada Bold |

Implementation approach (verified in LS-011): Compose resolves a missing glyph through the platform fallback chain, so the robust option is a **locale-aware typography set** — `LittleStoryTheme` picks `KannadaTypography` (Noto Sans Kannada families, adjusted metrics) when the app locale script is Knda, and the design typography otherwise. Mixed-script strings (a Kannada sentence with "LittleStory") fall back per glyph to the platform for Latin, which is acceptable.

Alternatives considered: Baloo Tamma 2 (rounder, playful, OFL) — closer to a children's feel but weaker at small sizes; system default — rejected (non-deterministic). Final choice is the owner's (D8).

## 3. Metrics guidance

Kannada has tall ascenders, stacked conjuncts (ottakshara) and vowel signs below the baseline, so it needs more vertical room than Latin at the same size.

| Style (design) | Latin spec | Kannada proposal | Why |
| --- | --- | --- | --- |
| Onboarding / setup title | 29 / 1.12–1.18, −0.8 tracking | same size, line height ≥ 1.4, tracking 0 | conjuncts clip at 1.12; negative tracking collides glyphs |
| Hero title | 25 / 1.12, −0.7 | line height ≥ 1.4, tracking 0 | |
| Top-bar title | 17, weight 800 | weight 700 (heaviest comfortable Noto weight), allow 2 lines | Kannada words are longer |
| Body | 15 / 1.55 | 15 / 1.7 | |
| Reader paragraph | 16 / 1.95 | 16 / 2.0 | already generous |
| Chip, nav label | 12 / 10 | allow wrap to 2 lines; min height grows, never fixed height | text expansion (R-04) |
| Eyebrow / kicker (uppercase, +1.6 tracking) | 10 / 9, all caps | no case transform, tracking 0, size ≥ 11 | Kannada has no case; small Kannada is hard to read |

All values live in the `KannadaTypography` tokens, never in screens. Use `includeFontPadding = false` only after checking that descenders are not clipped.

## 4. The uppercase problem

The design uppercases eyebrows ("LITTLE STORIES. BIG IMAGINATION.", "TONIGHT'S ADVENTURE") with CSS. In Compose this must be a typography-level `TextStyle` choice, applied only for scripts with case; resources keep normal case. Kannada eyebrows instead use weight and colour for emphasis.

## 5. Layout rules for Kannada

- Text containers wrap content; no fixed widths or heights on text.
- Buttons grow vertically; the design's 52 dp is a **minimum** height.
- Bottom-nav labels may wrap or shrink to a minimum size token; test at 200% font scale.
- Avoid `maxLines = 1` with ellipsis on titles unless the design shows truncation; prefer two lines.

## 6. Testing

| Test | Locale | Pass criteria |
| --- | --- | --- |
| Glyph coverage check (LS-011) | – | every character used in `values-kn` and `stories_kn.json` exists in the bundled Kannada font |
| Screenshot tests (LS-122) | `kn` at 100% and 200% font scale | no clipped conjuncts or vowel signs, no overlap, no truncation |
| Long-title fixture | `kn` | the longest Kannada title in mock data wraps cleanly on a compact (≤360 dp) device |
| Device check | Android 8 and latest | same rendering (bundled font, not system) |
| Native review (LS-047) | `kn` | reviewer confirms readability for children |
