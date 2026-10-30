---
paths:
  - "**/src/test/**"
  - "**/src/androidTest/**"
---

# Testing

- Select UI nodes by test tag or resource, never by English text.
- Use the v2 Compose test rules: `androidx.compose.ui.test.junit4.v2.createComposeRule` and `createAndroidComposeRule` (the old ones are deprecated). They use a standard dispatcher, so wait for conditions instead of assuming immediate execution.
- `waitUntil...` default is 1 s of real time, too short when Room or DataStore run on IO threads: pass a named, generous timeout constant.
- ViewModel tests use fakes plus `MainDispatcherRule`; `SavedStateHandle` needs Robolectric. Robolectric runs against `targetSdk`.
- Cover English and Kannada for anything with text or layout; force RTL with `LocalLayoutDirection` (Robolectric `ldrtl` does not flip Compose).
- Screenshots are produced with `./gradlew recordRoborazziDebug`. Never commit or approve baselines; the owner does.
- Never skip, disable or delete a failing test to get green.
