# Build and run

Purpose: how to set up, build, test and run LittleStory on Windows, macOS and Linux.
Status: 30 Oct 2026, end of P7 — `./gradlew check assembleDebug` passes on Linux (cloud workspace and GitHub Actions). Not verified here: the automatic JDK 21 download (the sandbox blocks `api.foojay.io`), Windows and macOS, and instrumented tests on a device.
Sources: master orchestrator brief §3.5, §17.7; P0 assessment §1–§2; workbook tickets LS-030, LS-132, TC-056; Risk R-21.

> Verified toolchain (see `gradle/libs.versions.toml`): Gradle 9.8.2 wrapper, AGP 9.5.0 (built-in Kotlin), Kotlin 2.4.21, Compose BOM 2026.09.00, JDK 21, compileSdk 37.2 (the Compose BOM requires it), targetSdk 36, minSdk 26. Commands in §3 were run on Linux: `./gradlew check assembleDebug` passes. The Windows and macOS column is expected to behave the same but has not been run.

## 1. Prerequisites

| Tool | Requirement | Notes |
| --- | --- | --- |
| Android Studio | Current stable release (it must support AGP 9.5) | Same on Windows and macOS. Its bundled JDK is enough; nothing to configure |
| Android SDK | Platform `android-37.2` and build-tools `37.0.0` | Studio offers to install them on first sync. Path lives only in `local.properties` (never committed) |
| JDK for the command line | Any JDK 17 or newer on the PATH | The Gradle toolchain downloads JDK 21 automatically (foojay resolver in `settings.gradle.kts`), so no manual JDK 21 install |
| Git | With line endings from `.gitattributes` | Windows: keep `core.autocrlf=false` or `input` |
| Network | Reaches `services.gradle.org`, `dl.google.com`, Maven Central, `plugins.gradle.org`, `api.foojay.io` | First build downloads Gradle, the SDK pieces and dependencies |
| Emulator images (optional) | API 26, 30, 33, latest | Kannada keyboard (e.g. Gboard) for manual checks |

## 2. First-time setup

1. Clone: `git clone https://github.com/shivayogih/LittleStory.git`
2. Open the folder in Android Studio and let Gradle sync (it uses the committed wrapper, not a local Gradle).
3. Run the `app` configuration on an emulator or device.

Command line instead of Studio: put your SDK path in the `ANDROID_HOME` environment variable (or in an uncommitted `local.properties` as `sdk.dir=/path/to/sdk`), then `./gradlew assembleDebug`. The APK lands in `app/build/outputs/apk/debug/`.

## 3. Commands

| Task | Windows (PowerShell / cmd) | macOS / Linux |
| --- | --- | --- |
| List modules | `gradlew.bat projects` | `./gradlew projects` |
| Everything CI runs | `gradlew.bat check assembleDebug` | `./gradlew check assembleDebug` |
| Detekt / ktlint | `gradlew.bat detekt ktlintCheck` (`ktlintFormat` fixes style) | `./gradlew detekt ktlintCheck` |
| Hardcoding check | `gradlew.bat checkNoHardcoding` | `./gradlew checkNoHardcoding` |
| Screenshot images (not verified until baselines are approved) | `gradlew.bat recordRoborazziDebug` | `./gradlew recordRoborazziDebug` |
| Preview screenshot images (experimental, EN/KN) | `gradlew.bat :app:updateDebugScreenshotTestDefaultTestSuite` | `./gradlew :app:updateDebugScreenshotTestDefaultTestSuite` |
| List tasks | `gradlew.bat tasks` | `./gradlew tasks` |
| Unit + Robolectric tests | `gradlew.bat test` | `./gradlew test` |
| Journeys A–G in EN and KN (LS-121) | `gradlew.bat :app:testDebugUnitTest --tests "*JourneysTest"` | `./gradlew :app:testDebugUnitTest --tests '*JourneysTest'` |
| Accessibility walk in EN and KN (LS-123) | `gradlew.bat :app:testDebugUnitTest --tests "*AccessibilityChecksTest"` | `./gradlew :app:testDebugUnitTest --tests '*AccessibilityChecksTest'` |
| Coverage gate and report (LS-120) | `gradlew.bat koverVerify koverHtmlReport` | `./gradlew koverVerify koverHtmlReport` (report in `build/reports/kover/html`) |
| Lint | `gradlew.bat lint` | `./gradlew lint` |
| Translation parity | `gradlew.bat checkTranslations` | `./gradlew checkTranslations` |
| Debug APK | `gradlew.bat assembleDebug` | `./gradlew assembleDebug` |
| Instrumented tests | `gradlew.bat connectedDebugAndroidTest` | `./gradlew connectedDebugAndroidTest` |

If `./gradlew` is not executable on macOS/Linux: `chmod +x gradlew` (the wrapper must be committed with the executable bit).

## 4. Running the app in Kannada

- Android 13+: Settings → Apps → LittleStory → Language → ಕನ್ನಡ, or the in-app language setting.
- Android 8–12: use the in-app language setting (AppCompat per-app language backport).
- Debug builds also offer pseudo-locales `en-XA` and `ar-XB` (enable "Developer options → Pseudo-locales" where required).

## 5. Where builds run

| Environment | Status |
| --- | --- |
| Owner machines (Windows, macOS) | Reference environment |
| GitHub Actions (LS-130) | `.github/workflows/ci.yml` runs `./gradlew check assembleDebug` on every PR and on pushes to `main`, `staging` and `development`. |
| Claude Code cloud workspace | Builds once `dl.google.com` is allowed in the environment's network policy and the SDK is installed (`sdkmanager "platforms;android-37.2" "build-tools;37.0.0"`, then `sdk.dir` in `local.properties`). Maven Central may answer 429: retry with `--max-workers=2`. |

## 6. Troubleshooting (to extend in LS-030)

| Symptom | Likely cause |
| --- | --- |
| Whole-file diffs after a commit on Windows | Line endings; check `.gitattributes` and `core.autocrlf` |
| Kannada shows as boxes | Fallback font missing from `res/font` (D8) |
| `SDK location not found` | Open the project once in Android Studio, or set `ANDROID_HOME` |
| `No matching toolchains found` / JDK 21 missing | `api.foojay.io` is blocked by a proxy or firewall; install JDK 21 or allow that host |
| `Failed to install the following Android SDK packages` | Accept licences: `sdkmanager --licenses`, then rerun |
| Gradle sync error about AGP version | Update Android Studio to the current stable release |
