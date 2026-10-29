# Build and run

Purpose: how to set up, build, test and run LittleStory on Windows, macOS and Linux.
Status: Draft for owner review — P1, 29 Oct 2026 — **to be completed when LS-030 creates the project**
Sources: master orchestrator brief §3.5, §17.7; P0 assessment §1–§2; workbook tickets LS-030, LS-132, TC-056; Risk R-21.

> The Android project does not exist yet. Commands below are the planned standard Gradle tasks; exact module and task names, JDK and tool versions are filled in by LS-030. Nothing here has been run.

## 1. Prerequisites

| Tool | Requirement | Notes |
| --- | --- | --- |
| Android Studio | Current stable release compatible with the AGP chosen in LS-030 | Same version on Windows and macOS |
| JDK | Pinned through the Gradle toolchain (version set in LS-030) | Do not rely on the machine's default JDK |
| Android SDK | compileSdk/targetSdk set in LS-030; minSdk 26 proposed (D5) | Path lives only in `local.properties` (not committed) |
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
| List tasks | `gradlew.bat tasks` | `./gradlew tasks` |
| Unit + Robolectric tests | `gradlew.bat test` | `./gradlew test` |
| Lint | `gradlew.bat lint` | `./gradlew lint` |
| Translation parity | `gradlew.bat checkTranslations` (planned, LS-041) | `./gradlew checkTranslations` |
| Screenshot verify | task name set in LS-035 (Roborazzi proposed) | same |
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
| GitHub Actions (LS-130) | Planned shared runner for every PR |
| Claude Code cloud workspace | Cannot build Android today: `dl.google.com` (Google Maven, SDK) is blocked by the environment's network policy (R-21). Allow that host in the cloud environment's network settings, or rely on CI (decision D3). |

## 6. Troubleshooting (to extend in LS-030)

| Symptom | Likely cause |
| --- | --- |
| Whole-file diffs after a commit on Windows | Line endings; check `.gitattributes` and `core.autocrlf` |
| Kannada shows as boxes | Fallback font missing from `res/font` (D8) |
| `SDK location not found` | `local.properties` missing; open once in Android Studio |
| Wrong JDK used | Toolchain not resolved; check Gradle JDK setting in Android Studio |
