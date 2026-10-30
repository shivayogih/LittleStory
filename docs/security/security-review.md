# Security and privacy review (LS-126)

Status: done for P7, 30 Oct 2026, against `development` after P6. Re-run before every release candidate and before any network, account or AI feature.

Inputs: [privacy-threat-model.md](privacy-threat-model.md), [security-checklist.md](security-checklist.md), [child-safety-requirements.md](child-safety-requirements.md), brief §18.

## Method

1. Read the app and library manifests as merged by the build for the release variant: permissions, exported components, backup attributes.
2. Searched every main source set for logging, network code, WebViews, URLs and cleartext settings.
3. Traced each item in the privacy data inventory to where it is stored and how it is deleted.
4. Went through the repository and CI items of the checklist.
5. Fixed what could be fixed in code. The rest is recorded below with an owner.

## Results by threat

| Threat | Result | Evidence |
|---|---|---|
| T-01 Child data in logs | **Pass.** No `Log.*` or `println` in any main source set. | Source search; the hardcoding audit scanner also covers UI modules |
| T-02 Backup or device transfer exposes profiles | **Fixed.** `allowBackup="false"` covered cloud backup only up to Android 11. `data_extraction_rules.xml` now excludes every domain from cloud backup and device-to-device transfer on Android 12+. | `app/src/main/res/xml/data_extraction_rules.xml`, manifest |
| T-03 Secrets in the public repository | **Pass.** No secrets exist. `.gitignore` covers `local.properties`, `*.jks`, `*.keystore`, `keystore.properties`. CI uses a read-only token and no secrets. | `.gitignore`, `.github/workflows/ci.yml` (`permissions: contents: read`) |
| T-04 Malformed mock JSON | **Pass.** The loader falls back to English field by field. Tests cover the missing Kannada story. | `:data` catalog tests (LS-036) |
| T-05 Demo subscription mistaken for a purchase | **Pass.** No billing library. The screen states that pricing is illustrative and sits behind `FeatureFlags.demoSubscription`. | `SubscriptionScreen`, `FeatureFlags` |
| T-06 Unneeded permissions | **Pass.** The merged release manifest requests no system permission. Its only `uses-permission` is AndroidX core's own `com.littlestory.app.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION`, which has signature protection. There is no `INTERNET`, so the app cannot reach the network. The reminder schedules nothing and asks for no notification permission (D13). | Merged release manifest |
| T-07 Exported components | **Pass.** Only `MainActivity` (launcher) is exported. Library components are either not exported or guarded by a system permission: `ProfileInstallReceiver` needs `android.permission.DUMP`. Debug builds also export Compose's `PreviewActivity` and the test `ComponentActivity`; neither is in release. | Merged release manifest |
| T-08 Child changes parent settings | **Open, owner decision.** There is no parental gate. Delete all data asks for confirmation. | Decision log item to raise |
| T-09 Debug features in release | **Pass.** Pseudo-locales are enabled only in the debug build type; the component catalogue is debug-only. | `app/build.gradle.kts` |
| T-10 Excessive generation | **Pass (MVP).** Generation is a local simulation with no provider. | `SimulatedGeneration` |

## Data inventory check

| Data | Stored | Deleted by "Delete all data" |
|---|---|---|
| Child first name, age, avatar, interests, language, duration | Room `profiles` | Yes (`clearAllTables`) |
| Favourites, downloads (simulated), reading and listening progress | Room | Yes |
| Created stories | Room | Yes |
| Story language, reading size, playback speed, reminder, toggles, first-run flag, active child | DataStore | Yes (`edit { clear() }`) |
| App language | AppCompat per-app locale | Reset to the system language |
| Create-story prompt text | Memory only, for the generation request; never written to Room, DataStore or logs | Gone when the process ends |

Parent name and email are not collected (D14).

## Checklist §3 (app configuration)

- [x] Backup and device transfer set deliberately (T-02)
- [x] No network permission, so no cleartext traffic is possible; add a network security config with the first network feature
- [x] Only the launcher activity exported
- [x] Release build: `isMinifyEnabled = false` is a recorded decision for the MVP (no secrets to protect, simpler crash reading); revisit before store release. No debug logging. Pseudo-locales disabled.
- [x] No WebView
- [x] Room holds only inventory data
- [x] Works fully offline

## Open items for the owner

| # | Item | Why it matters |
|---|---|---|
| S1 | Parental gate for the Profile area (T-08) | Stops a child from deleting data or changing language |
| S2 | Turn on GitHub secret scanning, push protection and Dependabot alerts | Repository settings cannot be verified from the code |
| S3 | Branch protection for `main`, `staging`, `development` | Same; owner-only setting |
| S4 | Minify and obfuscation decision before store release | Recorded as off for the MVP |
| S5 | Privacy policy and Play Data safety form | Needed before any release (Approval F) |
