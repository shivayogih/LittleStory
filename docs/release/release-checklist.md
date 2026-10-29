# Release checklist

Purpose: the gate every LittleStory release must pass before the owner grants Approval F.
Status: Draft for owner review — P1, 29 Oct 2026
Sources: master orchestrator brief §2.3 (Approval F), §19, §22, §29; workbook tickets LS-132, LS-133; [known-limitations.md](known-limitations.md).

No release, store submission or publication happens without the owner's explicit Approval F. Agents never publish to Google Play.

## 1. Scope and design

- [ ] Scope frozen; release notes list every included ticket (LS-###)
- [ ] All shipped screens match approved designs; deviations recorded as decisions
- [ ] Missing designs (SCR-19…SCR-23) either designed or approved patterns (D10)
- [ ] Brand assets show "LittleStory", not "LittleKatha" (R-01, D11)

## 2. Quality evidence

- [ ] CI green on the release commit: build, Lint (HardcodedText, MissingTranslation as errors), unit, UI, screenshot, `checkTranslations`
- [ ] E2E journeys A–G pass in English and Kannada
- [ ] Screenshot diffs reviewed by the owner in both languages
- [ ] Builds verified on Windows and macOS (TC-056)
- [ ] No open Blocker or High defects

## 3. Localization

- [ ] 100 % key parity EN ↔ KN; English fallback verified
- [ ] Every Kannada string and mock story approved by the native reviewer (LS-047, R-12)
- [ ] Store listing text in English and Kannada (if required)
- [ ] Pseudo-locales disabled in the release build

## 4. Accessibility, privacy, security

- [ ] Accessibility audit complete (LS-123): touch targets, contrast, TalkBack EN/KN, font scale 200 %
- [ ] Security checklist complete (`docs/security/security-checklist.md`)
- [ ] Privacy review complete; Data safety form and privacy policy accurate; legal review of child-data obligations done
- [ ] Simulated features clearly labelled; demo pricing not presented as real (R-15)

## 5. Build and versioning

- [ ] `versionCode` incremented, `versionName` set (scheme to be agreed)
- [ ] Release build reproducible from a tagged commit on `main`
- [ ] Signing performed outside the repository; keystore never committed
- [ ] Known limitations updated

## 6. Authorisation

- [ ] Release-readiness report (LS-133) delivered, separating verified results from assumptions
- [ ] Owner's explicit Approval F recorded (date, version, channel)
