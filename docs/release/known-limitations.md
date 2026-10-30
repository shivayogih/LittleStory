# Known limitations

Purpose: an honest list of what the MVP does not do or has not verified, kept current through release.
Status: Updated at the end of P7, 30 Oct 2026; draft for owner review
Sources: master orchestrator brief §4.3, §29; P0 assessment §2, §3, §9; workbook Risks R-01…R-24, ADR-014.

## 1. Product limitations (by design for the MVP)

| Area | Limitation |
| --- | --- |
| Story generation | Simulated and deterministic; no AI is called. Labelled as a simulation (NFR-12). |
| Narration / player | Simulated playback; no audio is produced. Controls, speed and sleep timer change UI state only. |
| Downloads | Simulated; "offline" stories are bundled mock data. |
| Subscription | Demo screen with illustrative prices; no payment processed (R-15). |
| Bedtime reminder | Settings UI only; no notification is delivered (D13). |
| Notifications | Mock inbox; no push notifications. |
| Accounts | No sign-in, sync or backup to a server; data lives on one device. |
| Languages | English and Kannada only; others are architecture-ready but not shipped. |

## 2. Verification limitations (current)

| Area | Limitation | Risk |
| --- | --- | --- |
| Design source | Live Figma Make file cannot be read by agent tools; the design pack extracted on 29 Oct 2026 is the reference. Later Figma changes are not detected automatically. | R-23 |
| Brand | Reference screenshots show the old "LittleKatha" name. | R-01 |
| Missing designs | Splash, language preferences, privacy, help, account and audio preferences screens (SCR-19…SCR-23) have no designs. | R-05 |
| Kannada | Every string and mock story has Kannada text, but all of it is an agent draft with no native review (ledger in `docs/project-management/implementation-status.md`). | R-12 |
| Fonts | Design fonts lack Kannada glyphs; Noto Sans Kannada is bundled as the Kannada face. Rendering on real devices not yet checked. | R-03 |
| TalkBack in Kannada | Depends on a Kannada TTS voice being installed on the device; not yet tried. | R-13 |
| Accessibility | Names and 48 dp targets are checked automatically on 12 screens in both languages. Contrast, TalkBack order and reduced motion still need a device pass (`docs/design/accessibility-spec.md` §6). | R-07, R-08 |
| Design fidelity | Compared by eye with the design pack (`docs/design/fidelity-review.md`); Medium differences remain and wait for the owner. No screenshot baselines are approved yet, so visual regressions are not caught automatically. | NFR-10 |
| Security | Reviewed in `docs/security/security-review.md`; release logging (T-08) and items S1–S5 are open. | — |
| Build environment | Windows and macOS builds, and the automatic JDK 21 download, have not been run by an agent. | R-21 |
| Repository | The repository is public; never commit real child data or secrets. | R-24 |

## 3. Not yet measured

- Performance (startup, memory, recomposition): no profiling done.
- Device coverage: no test on a real phone or emulator yet. All UI evidence comes from Robolectric on the JVM.
- What has run: `./gradlew check assembleDebug` (unit, Robolectric screen and journey tests, Lint, Detekt, ktlint, hardcoding scan, translation parity, Kover coverage gate) passes in CI and in the cloud workspace. Instrumented tests (`connectedDebugAndroidTest`) have not run.

Update this file whenever a limitation is resolved or discovered; the release checklist requires it to be current.
