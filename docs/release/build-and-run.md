# Build and run

Purpose: how to set up, build, test and run LittleStory on Windows, macOS and Linux.
Status: P2, 29 Oct 2026 — verified on Linux (cloud workspace); Windows and macOS steps are untested here
Sources: master orchestrator brief §3.5, §17.7; P0 assessment §1–§2; workbook tickets LS-030, LS-132, TC-056; Risk R-21.

> Verified toolchain (see `gradle/libs.versions.toml`): Gradle 9.8.2 wrapper, AGP 9.5.0 (built-in Kotlin), Kotlin 2.4.21, Compose BOM 2026.09.00, JDK 21, compileSdk 37.2 (the Compose BOM requires it), targetSdk 36, minSdk 26. Commands in §3 were run on Linux: `./gradlew check assembleDebug` passes. The Windows and macOS column is expected to behave the same but has not been run.

## 1. Prerequisites

| Tool | Requirement | Notes |
| --- | --- | --- |
| Android Studio | A stable release that supports AGP 9.5 | Same version on Windows and macOS |
| JDK | 21, selected by the Gradle toolchain (`jdk` in the version catalog) | Do not rely on the machine's default JDK |
| Android SDK | Platform `android-37.2` and build-tools `37.0.0` | Path lives only in `local.properties` (not committed) |
| Git | With line-ending normalisation from `.gitattributes` | Windows: keep `core.autocrlf=false` or `input`; `.gitattributes` decides |
| Emulator images | API 26, 30, 33, latest (see `docs/testing/test-plan.md`) | Kannada keyboard (e.g. Gboard) for manual checks |

## 2. First-time setup

1. Clone: `git clone https://github.com/shivayogih/LittleStory.git`
2. Open the folder in Android Studio; let it create `local.properties` with your SDK path (never commit it).
3. Let Gradle sync with the wrapper (`gradle/wrapper/`), not a locally installed Gradle.

## 3. Commands

| Task | Windows (PowerShell / cmd) | macOS / Linux |
| --- | --- | --- |
| List modules | `gradlew.bat projects` | `./gradlew projects` |
| Everything CI runs | `gradlew.bat check assembleDebug` | `./gradlew check assembleDebug` |
| Detekt / ktlint | `gradlew.bat detekt ktlintCheck` (`ktlintFormat` fixes style) | `./gradlew detekt ktlintCheck` |
| Hardcoding check | `gradlew.bat checkNoHardcoding` | `./gradlew checkNoHardcoding` |
| Screenshot images (not verified until baselines are approved) | `gradlew.bat recordRoborazziDebug` | `./gradlew recordRoborazziDebug` |
| List tasks | `gradlew.bat tasks` | `./gradlew tasks` |
| Unit + Robolectric tests | `gradlew.bat test` | `./gradlew test` |
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
| GitHub Actions (LS-130) | `.github/workflows/ci.yml` runs `./gradlew check assembleDebug` on every PR. Not yet run: it starts when the PR is opened. |
| Claude Code cloud workspace | Builds once `dl.google.com` is allowed in the environment's network policy and the SDK is installed (`sdkmanager "platforms;android-37.2" "build-tools;37.0.0"`, then `sdk.dir` in `local.properties`). Maven Central may answer 429: retry with `--max-workers=2`. |

## 6. Troubleshooting (to extend in LS-030)

| Symptom | Likely cause |
| --- | --- |
| Whole-file diffs after a commit on Windows | Line endings; check `.gitattributes` and `core.autocrlf` |
| Kannada shows as boxes | Fallback font missing from `res/font` (D8) |
| `SDK location not found` | `local.properties` missing; open once in Android Studio |
| Wrong JDK used | Toolchain not resolved; check Gradle JDK setting in Android Studio |
