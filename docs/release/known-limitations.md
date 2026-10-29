# Known limitations

Purpose: an honest list of what the MVP does not do or has not verified, kept current through release.
Status: Draft for owner review — P1, 29 Oct 2026
Sources: master orchestrator brief §4.3, §29; P0 assessment §2, §3, §9; workbook Risks R-01…R-24, ADR-014.

## 1. Product limitations (by design for the MVP)

| Area | Limitation |
| --- | --- |
| Story generation | Simulated and deterministic; no AI is called. Labelled as a simulation (NFR-12). |
| Narration / player | Simulated playback; no audio is produced. Controls, speed and sleep timer change UI state only. |
| Downloads | Simulated; "offline" stories are bundled mock data. |
| Subscription | Demo screen with illustrative prices; no payment processed (R-15). |
| Bedtime reminder | Settings UI only; no notification is delivered (D13, proposed). |
| Notifications | Mock inbox; no push notifications. |
| Accounts | No sign-in, sync or backup to a server; data lives on one device. |
| Languages | English and Kannada only; others are architecture-ready but not shipped. |

## 2. Verification limitations (current)

| Area | Limitation | Risk |
| --- | --- | --- |
| Design source | Live Figma Make file cannot be read by agent tools; the design pack extracted on 29 Oct 2026 is the reference. Later Figma changes are not detected automatically. | R-23 |
| Brand | Reference screenshots show the old "LittleKatha" name. | R-01 |
| Missing designs | Splash, language preferences, privacy, help, account and audio preferences screens (SCR-19…SCR-23) have no designs. | R-05 |
| Kannada | No Kannada copy, glossary or native reviewer yet; all Kannada text will be draft until reviewed. | R-12 |
| Fonts | Design fonts lack Kannada glyphs; a fallback font is required. | R-03 |
| TalkBack in Kannada | Depends on a Kannada TTS voice being installed on the device. | R-13 |
| Accessibility | Design contrast and touch-target gaps must be fixed in implementation. | R-07, R-08 |
| Build environment | The cloud agent workspace cannot build or test Android until `dl.google.com` is allowed; CI or local machines must run checks. | R-21 |
| Repository | Default branch currently points at a docs branch; repository is public. | R-22, R-24 |

## 3. Not yet measured

- Performance (startup, memory, recomposition) — no profiling done.
- Device coverage — no device testing done.
- Any test, build or Lint result — none run yet.

Update this file whenever a limitation is resolved or discovered; the release checklist requires it to be current.
