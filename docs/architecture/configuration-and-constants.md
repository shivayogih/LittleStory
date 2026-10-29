# Configuration and constants

Purpose: give every configurable value seen in the prototype exactly one home, so a change is made in one place (brief §4.5, NFR-01).

Status: Draft for owner review — P1, 29 Oct 2026 (ADR-012 configuration & feature flags, Proposed)

Sources: `docs/design/LittleStory_Figma_Design_Details.md` §1.3, §1.7, §3 · `docs/design/design-tokens.json` · brief §4.5, §9.6 · P0 assessment §4 (hardcoding found).

## 1. Homes

| Home | Module | Holds | Changed by |
| --- | --- | --- | --- |
| Design tokens (`LittleStoryTheme.colors/typography/shapes/spacing/motion/dimens`) | `:core:designsystem` | colours, type, radii, sizes, elevations, animation durations | design-system engineer |
| String resources (`values/`, `values-kn/`) | owning Android module | all user-facing copy, content descriptions, formats | feature engineer + translator |
| `AppConfig` | `:core:common` | validation limits, option lists, defaults, simulation timings | product decision + engineer |
| `FeatureFlags` | `:core:common` | on/off for simulated or optional features (no remote config in MVP) | product decision |
| `SupportedLanguage` registry | `:core:common` | languages, native names, enabled, default | localization engineer |
| `BuildConfig` / build types | `:app` | version name, debug-only switches (pseudo-locales) | build engineer |
| Mock data JSON (`assets/mock/`) | `:data` | stories, categories, sample children, plans, notifications | content owner |
| `gradle/libs.versions.toml` | root | dependency versions | build engineer |
| Typed routes | each feature + `:app` registry | navigation destinations | feature engineer |

## 2. `AppConfig` values (from the prototype)

| Key (proposed) | Value in prototype | Used by | Note |
| --- | --- | --- | --- |
| `Create.promptMaxChars` | 500 | SCR-07 counter + validator + error message | one constant, three uses |
| `Create.durationOptionsMinutes` / default | 5, 8, 10, 15 / 8 | SCR-07 more options | |
| `Create.themes` / default | Adventure, Bedtime, Animals, Friendship, Fantasy, Learning, Indian folklore / Adventure | SCR-07 | enum IDs; labels are strings |
| `Create.moods` / default | Calm, Funny, Magical, Adventurous / Magical | SCR-07 | |
| `Create.suggestions` | 5 suggestion IDs | SCR-07 chips | each maps to a full sentence string resource, no concatenation (see string conventions) |
| `Setup.ageRange` / default | 3..12 / 6 | SCR-02, validator | |
| `Setup.durationOptionsMinutes` / default | 5, 10, 15 / 10 | SCR-02 | differs from Create options — confirm (open question C1) |
| `Setup.interests` / defaults | Animals, Cricket, Adventure, Space, Nature, Music / first three | SCR-02 | |
| `Setup.avatarCount` | 3 (+ add tile) | SCR-02 | avatar art is drawn in code |
| `Generation.stepPercent` / `stepIntervalMs` | 4 / 120 | SCR-08 simulation | ≈3 s total |
| `Generation.autoAdvanceDelayMs` | 500 | SCR-08 → SCR-09 | |
| `Generation.stageThresholdsPercent` | 38, 76 | SCR-08 three stages | |
| `Generation.restartPercent` | 8 | "Try again" | |
| `Player.skipSeconds` | 15 | SCR-11a | |
| `Player.speedOptions` / default | 0.75, 1.0, 1.25, 1.5 / 1.0 | SCR-11a | shown with locale-aware number format |
| `Player.simulatedTickMs` / `tickPercent` | 500 / 0.25 | simulated playback | |
| `Player.defaultVolumePercent` | 75 | SCR-11a | |
| `Player.sleepTimerOptions` | 10, 15, 30 min, end of story | SCR-11b | |
| `Reader.textScaleRange` / step / default | 0.9–1.3 / 0.1 / 1.0 | SCR-10 A− A+ | |
| `Downloads.simulatedQuotaBytes` | 1 GB | SCR-17 | displayed with `Formatter.formatShortFileSize` |
| `Reminder.defaultTime` / `defaultDays` | 20:30 / all seven | SCR-16 | displayed with locale time format |
| `Discover.filterIds` | All, Animals, Adventure, Friendship, Kindness, Nature, Space, Science, Panchatantra, Indian Folk Tales, Festivals, Learning | SCR-04a | labels are strings |
| `Category.sortOptions` | Recommended, Shortest first, Newest | SCR-05 | |
| `Home.greetingHours` | not in prototype (always "Good evening") | SCR-03 | proposed morning/afternoon/evening bands; decision R-10 |

Values from the prototype that are **sample data, not config**, and move to mock JSON: child names (Aarav, Ananya), parent name (Meera — dropped per D14), voice name (Kavya), "42% complete", "2:28 / 8:00", "3:24 remaining", story counts ("18 STORIES" is computed), notification items, downloaded sizes, plan prices (₹0, ₹249, ₹1,799, ₹2,499 stored as minor units + `INR`, formatted with `NumberFormat.getCurrencyInstance(locale)`).

## 3. `FeatureFlags`

| Flag | Default (MVP) | Controls | Why a flag |
| --- | --- | --- | --- |
| `demoSubscription` | on, labelled "Illustrative pricing only" | SCR-15 entry row | R-15: owner may hide it before release |
| `simulatedNarration` | on | Listen buttons, SCR-11a | brief §4.3 simulation |
| `simulatedDownloads` | on | download buttons, SCR-17 | simulation |
| `bedtimeReminder` | on (UI only, no notification permission) | SCR-16, Profile row | D13 / ADR-015 |
| `notificationsInbox` | on (mock) | bell icons, SCR-18 | mock content |
| `pseudoLocales` | debug only (from `BuildConfig.DEBUG`) | en-XA / ar-XB in language list | brief §4.4 rule 18 |

Flags are a plain Kotlin object provided through Hilt so tests can override them; no remote config in the MVP.

## 4. Design tokens (summary)

All values are from `design-tokens.json`; the full mapping is in `docs/design/design-system.md`.

| Group | Examples | Token home |
| --- | --- | --- |
| Colour | indigo `#35256F`, cream `#FFF9F0`, night `#17152F`, success `#287A54`, error `#B42318`, 6 story-art gradients | `LittleStoryColors` |
| Typography | DM Sans body, Manrope display; onboarding title 29/1.12, reader 16/1.95 | `LittleStoryTypography` (+ Kannada line-height adjustments) |
| Shape | radius 24/18/13, button 16, hero 26, sheet 32, chip 20 | `LittleStoryShapes` |
| Dimensions | button min height 52, icon button 42 (proposed 48 for touch target, R-08), top bar 68, bottom nav 76 | `LittleStoryDimens` |
| Spacing | page padding 20 (16 on compact), steps from the stylesheet | `LittleStorySpacing` |
| Motion | twinkle 2.4 s / 1.7 s / 1.1 s, press 0.18 s, toggle/chevron 0.2 s, reader progress 0.3 s | `LittleStoryMotion` |

## 5. `BuildConfig`

| Field | Value | Note |
| --- | --- | --- |
| `VERSION_NAME` | from Gradle | replaces the hardcoded "Version 1.0 prototype" (SCR-13) |
| `DEBUG` | build type | gates pseudo-locales and debug simulation toggles |
| API base URL | not defined in MVP | added only when a backend is approved; never secrets in the APK |

## 6. Enforcement

- Lint `HardcodedText` and a Compose literal-string check (LS-034); Detekt `MagicNumber` with `AppConfig`/tokens as the only allowed homes (if D15 approved).
- Code review checklist item in the PR template: "new configurable value added to its home and to this document".
