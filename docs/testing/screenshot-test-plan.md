# Screenshot test plan

Purpose: define the visual regression suite, its fixed configuration and the baseline review rules.
Status: Draft for owner review — P1, 29 Oct 2026
Sources: master orchestrator brief §16, §16.1, §25; design details §3 and `docs/design/screens/*.jpg`; workbook tickets LS-020, LS-122, LS-124; ADR-011 (tool, Proposed); P0 assessment D7.

## 1. Tool

Roborazzi on Robolectric is recommended (runs on the JVM in CI, no emulator). Compose Preview Screenshot Testing is the alternative. The choice is confirmed in ADR-011 after LS-030 checks AGP/Kotlin compatibility.

### 1.1 Compose Preview Screenshot Testing trial (30 Oct 2026)

The owner asked whether [Compose Preview Screenshot Testing](https://developer.android.com/studio/preview/compose-screenshot-testing) fits. Result: yes, alongside Roborazzi.

| Question | Finding |
|---|---|
| Compatible? | The standalone `com.android.compose.screenshot` plugin is deprecated. Its replacement is an AGP test suite, which needs AGP 9.5 (we use 9.5.0) and engine `0.0.1-alpha16`. Both flags in `gradle.properties` are experimental. |
| Where it runs | `:app/src/screenshotTest`. In a library module the renderer failed (`ClassNotFoundException … R$id`) because only the application packages every library's R classes. The previews in `:app` can show screens from any feature. |
| AGP quirk | The engine packages resources through the release unit-test component, which AGP 9 turns off by default. `app/build.gradle.kts` turns it on and keeps the `testReleaseUnitTest` task disabled. |
| CI | Renders on the JVM with no emulator: about 20 s for 15 images locally. CI runs `:app:updateDebugScreenshotTestDefaultTestSuite` after `check` and uploads the images. |
| Gate | Not yet. References in `app/src/screenshotTestDefaultDebug/reference` are git-ignored until the owner approves them. After approval, remove the ignore line, commit the images, and switch CI to `testDebugScreenshotTestDefaultTestSuite`. |
| Coverage | Home, Reader, Player, Profile and Language, each in English, Kannada, and Kannada at 200 % font scale on the 430 × 932 dp design frame (`LocalePreviews`). |

How the two tools split the work:

- **Roborazzi** stays for interaction states that need a test rule: sheets, dialogs, pseudo-locales, and every module's own screens.
- **Preview tests** give a fast, IDE-visible gallery of key screens per locale, the same `@Preview` the designer sees in Studio.

| Task | Command |
|---|---|
| Render or refresh preview images | `./gradlew :app:updateDebugScreenshotTestDefaultTestSuite` |
| Compare against approved references (after approval) | `./gradlew :app:testDebugScreenshotTestDefaultTestSuite` |
| Report | `app/build/reports/tests/<task>/index.html` |

## 2. Fixed configuration

| Setting | Value |
| --- | --- |
| Device | width 430 dp (matches the design frame), height 932 dp, density xxhdpi |
| Compact variant | width 360 dp for screens with a compact breakpoint |
| Locales | `en`, `kn`; `en-XA` for key screens |
| Font scale | 1.0 baseline; 2.0 for Reader, Setup, Create, Home |
| Theme | Light (single theme, ADR-013) |
| Data | Seed data from [test-data.md](test-data.md); fixed clock 2026-10-29 20:00 local |
| Animations | Disabled / clock paused; twinkle animations captured at frame 0 |

## 3. Suite: 22 design states × 2 locales

| # | State | Reference image | EN | KN |
| --- | --- | --- | --- | --- |
| 1–3 | SCR-01 Onboarding slides 1–3 | `01`–`03-onboarding-*.jpg` | ✓ | ✓ |
| 4 | SCR-02 Setup | `04-setup-profile.jpg` | ✓ | ✓ |
| 5 | SCR-03 Home | `05-home.jpg` | ✓ | ✓ |
| 6 | SCR-04a Discover | `06-discover.jpg` | ✓ | ✓ |
| 7 | SCR-07 Create | `07-create-story.jpg` | ✓ | ✓ |
| 8 | SCR-07 Create – options + error | `08-create-story-more-options-and-error.jpg` | ✓ | ✓ |
| 9 | SCR-08 Generating | `09-generating.jpg` | ✓ | ✓ |
| 10 | SCR-09 Story ready | `10-story-ready.jpg` | ✓ | ✓ |
| 11 | SCR-10 Reader | `11-reader.jpg` | ✓ | ✓ |
| 12 | SCR-11a Player | `12-player.jpg` | ✓ | ✓ |
| 13 | SCR-11b Sleep timer | `13-player-sleep-timer-sheet.jpg` | ✓ | ✓ |
| 14 | SCR-12 My Stories | `14-my-stories.jpg` | ✓ | ✓ |
| 15 | SCR-13 Profile | `15-profile-settings.jpg` | ✓ | ✓ |
| 16 | SCR-15 Subscription | `16-subscription.jpg` | ✓ | ✓ |
| 17 | SCR-16 Bedtime reminder | `17-bedtime-reminder.jpg` | ✓ | ✓ |
| 18 | SCR-17 Offline downloads | `18-offline-downloads.jpg` | ✓ | ✓ |
| 19 | SCR-18 Notifications | `19-notifications.jpg` | ✓ | ✓ |
| 20 | SCR-14 Child profiles | `20-child-profiles.jpg` | ✓ | ✓ |
| 21 | SCR-05 Category | `21-category-indian-tales.jpg` | ✓ | ✓ |
| 22 | SCR-06 Story detail | `22-story-detail.jpg` | ✓ | ✓ |

Plus specified-but-not-captured states: SCR-04b no results, SCR-08 paused, SCR-12 empty tabs, SCR-15 confirmation dialog, SCR-16 toast, SCR-17 empty. Plus a component catalogue (LS-020): buttons, chips, fields, toggles, segmented control, cards, sheets, dialogs, empty states, progress bars — each in EN and KN.

Note: reference screenshots show the old "LittleKatha" brand (R-01); differences in the wordmark are expected and not defects.

## 4. Baseline rules

1. Baselines are recorded only from a ticket branch and committed with the PR that changes the UI.
2. Every baseline change is reviewed by the owner in the PR; never auto-approved (brief §16.1).
3. The PR shows EN and KN images side by side with the design reference for new screens.
4. Kannada images are checked for glyph rendering, clipping, overflow and line height (brief §25 step 5).
5. A screen is reported "visually verified" only after an actual comparison was performed and recorded in the workbook (Screens sheet, LS-124).
6. Differences are logged as defects with severity and locale; the Figma design is never changed to hide a mismatch.
