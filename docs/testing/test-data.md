# Test data

Purpose: specify the deterministic seed data (English and Kannada) used by mock repositories, tests and screenshots.
Status: Draft for owner review — P1, 29 Oct 2026
Sources: master orchestrator brief §10; design details §1.1 (story gradients), §3; workbook tickets LS-036, LS-044; ADR-007.

> Kannada values are not written here: they are drafted in LS-044 and stay "Needs native review". English titles below come from the design pack.

## 1. Files and format (proposed)

| File | Content |
| --- | --- |
| `assets/mock/stories_en.json` | Catalog in English (complete): ids, category, tags, ages, duration, added date, art ids, synopsis, lesson, pages. Defines every structural field |
| `assets/mock/stories_kn.json` | Text only (title, synopsis, lesson, pages) for the same ids, field by field; intentionally missing `story_lanterns_jaipur` |
| `assets/mock/categories_{tag}.json` | Category IDs and names |
| `assets/mock/notifications_{tag}.json` | SCR-18 items |
| Test-only fixtures | `src/test/resources/` copies plus edge-case files below |

Story IDs are identical across languages; no random IDs or current timestamps.

## 2. Seed stories

| ID | English title (design) | Category | Duration | Ages | Sections | Kannada | Purpose |
| --- | --- | --- | --- | --- | --- | --- | --- |
| `story_little_elephant` | The Little Elephant Who Shared | Friendship | 6 min | 4–7 | 5 | yes | Featured, detail screen |
| `story_magical_cricket_bat` | Aarav and the Magical Cricket Bat | Adventure | 8 min | 5–9 | 5 | yes | Continue card at 42 %, generated-story result |
| `story_moonlit_garden` | Mira and the Moonlit Garden | Bedtime | 10 min | 3–6 | 3 | yes | Bedtime row |
| `story_banyan_secret` | The Banyan Tree's Secret | Indian Tales | 6 min | 4–7 | 4 | yes | Category screen |
| `story_journey_to_stars` | Ananya's Journey to the Stars | Space | 15 min | 7–12 | 8 | yes | Long story, older age band |
| `story_lanterns_jaipur` | The Lanterns of Jaipur | Festivals | 5 min | 4–8 | 2 | **missing** | English fallback (TC-049) |
| `story_long_title` | (test-only) a title of ~80 characters | Adventure | 5 min | 5–9 | 1 | yes, longer than EN | Wrapping and truncation |
| `story_no_art` | (test-only) | Learning | 5 min | 6–10 | 1 | yes | Missing/placeholder artwork |

Artwork uses the palette and motif fields (design §1.1) — no image files.

## 3. Profiles and state

| Fixture | Values | Used by |
| --- | --- | --- |
| Child A | `child_01`, first name "Aarav", age 6, avatar 1, interests Animals/Cricket/Adventure | Home, Create, Player |
| Child B | `child_02`, "Ananya", age 9, avatar 2, interests Space/Science/Music | Child profiles (TC-042) |
| Parent | display name only if D14 keeps it; **no email** in seed data | Profile hub |
| Progress | `story_magical_cricket_bat` section 3, 0.42 | Continue card (TC-033) |
| Favourites | `story_little_elephant` | Favourite consistency (TC-017) |
| Downloads (simulated) | two stories, 12 MB and 18 MB | SCR-17 |
| Reminder | enabled, 20:30, all days | SCR-16 |

Names are deliberately the design's sample names; no real children's data is ever used.

## 4. Edge-case sets

| Set | Content | Test |
| --- | --- | --- |
| Empty library | no favourites, no created, no downloads | TC-039, SCR-12 empty states |
| No progress | progress table empty | TC-009 |
| No search results | query `zzzz` | TC-013 |
| Corrupt JSON | malformed `stories_kn.json` | Loader falls back to EN and logs a non-PII error |
| Missing field | Kannada story without `synopsis` | Field-level fallback |
| Generation failure | simulator seeded to fail at stage 2 | Error/retry state |
| Max input | 500-character prompt; 501 rejected | TC-021 |

## 5. Determinism rules

- Inject `Clock` (fixed `2026-10-29T20:00` local for screenshots) and coroutine dispatchers.
- The generation simulator uses `AppConfig` timings and no randomness.
- Test data changes go through the same PR review as code.

## 6. Implemented in P4 (LS-036)

- Loader: `CatalogRepositoryImpl` in `:data`; English is the base, a translation file overrides text per field, a missing or malformed file falls back to English. `Story.resolvedLanguage` records the language actually shown.
- Not shipped yet: notifications, plans and profiles JSON (they arrive with their screens). The test-only stories `story_long_title` and `story_no_art` are not in `src/main`; add them as test fixtures when a test needs them.
- No progress, favourites or downloads are seeded on a real first run, so the Home continue card shows only after a child has listened.
