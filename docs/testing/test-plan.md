# Test plan

Purpose: scope, entry/exit criteria, environments and device/locale matrix for testing the LittleStory MVP.
Status: Draft for owner review — P1, 29 Oct 2026
Sources: master orchestrator brief §8.6, §17, §22; workbook Requirements (FR-01…FR-25, NFR-01…NFR-12) and Test Cases (TC-001…TC-058); P0 assessment D3, D5.

## 1. Scope

In scope: the 18 designed screens (SCR-01…SCR-18), the planned undesigned screens once approved (SCR-19…SCR-23), journeys A–G, English and Kannada, simulated generation/audio/downloads/subscription, local persistence, accessibility and localization quality.

Out of scope for MVP: real backend, AI, auth, payments, TTS, push notifications, analytics, languages other than EN/KN (only readiness is tested, via pseudo-locales and the registry).

## 2. Entry and exit criteria

| Level | Entry | Exit |
| --- | --- | --- |
| Ticket | Ticket meets Definition of Ready; acceptance criteria approved | Its required tests pass in CI; EN + KN screenshots reviewed; no new Lint errors |
| Phase (P3–P6) | Previous phase merged to `main` | Journey(s) for the phase pass in EN and KN; no open Blocker/High defects |
| P7 hardening | P3–P6 merged | All Must test cases pass; accessibility audit (LS-123) and security review (LS-126) complete; native Kannada review (LS-047) signed off or explicitly deferred by owner |
| Release | P7 exit | Release checklist complete; owner's Approval F |

## 3. Environments

| Environment | Purpose | Status |
| --- | --- | --- |
| GitHub Actions | PR checks: build, Lint, unit, Robolectric UI, screenshots, translation parity | To be created (LS-130) |
| Owner's Windows machine | Build parity (TC-056), manual checks | Available, versions unknown |
| Owner's macOS machine | Build parity (TC-056), manual checks | Available, versions unknown |
| Cloud agent workspace | Docs and workbook only until `dl.google.com` is allowed | Blocked (R-21) |

## 4. Device matrix (proposed, minSdk 26 per D5)

| Class | API level | Screen | Why |
| --- | --- | --- | --- |
| Oldest supported | 26 (Android 8.0) | small phone, ~360 dp wide | Per-app language via AppCompat backport; compact layout breakpoint (≤ 360 px in design) |
| Mid | 30 (Android 11) | typical phone, ~393–411 dp | Most common class |
| Platform per-app language | 33 (Android 13) | typical phone | System `LocaleManager` path; language in system settings |
| Current | 35-class (latest stable at LS-030) | large phone, ~430 dp (design frame) | Visual fidelity reference |
| Tall | any | min-height ≥ 900 dp | Design's tall breakpoint (player cover, onboarding art) |

Font scale: 100 % and 200 % on each class for EN and KN. Orientation: portrait required; rotation tested for state survival only.

## 5. Locale matrix

| Locale | Purpose | Applies to |
| --- | --- | --- |
| `en` | Default and fallback | All tests |
| `kn` | MVP second language | All UI and screenshot tests |
| `en-XA` | Pseudo-locale: text expansion and hardcoded-string detection | Key screens, debug builds only (TC-052) |
| `ar-XB` | Pseudo-locale: RTL readiness | Key screens, debug builds only |

## 6. Test types and counts (from workbook)

| Type | Test cases |
| --- | --- |
| UI | TC-001, 002, 004, 005, 006, 009, 010, 011, 013, 014, 016, 019, 020, 022, 023, 025, 026, 027, 030, 036, 037, 038, 039, 040, 041, 043, 045, 046, 057 |
| Unit | TC-015, 021, 024, 031, 034, 035 |
| Integration | TC-007, 017, 018, 029, 033, 042, 048, 058 |
| Localization | TC-003, 012, 028, 044, 049 |
| Screenshot | TC-008, 052 |
| Accessibility | TC-032, 053, 054, 055 |
| Security | TC-047 |
| Build/CI | TC-050, 051, 056 |

Gaps to add during P2–P6: Kannada screenshot cases for every screen (see [screenshot-test-plan.md](screenshot-test-plan.md)), language registry vs `locales_config.xml` check, mock-content parity per story ID, reader/player state after language change.

## 7. Defect handling

Severity: Blocker (crash, data loss, journey impossible), High (wrong behaviour, missing Kannada string, hardcoded user-facing text, a11y failure on a main flow), Medium (visual mismatch, minor flow issue), Low (cosmetic). Every defect: steps, locale, device/API, expected, actual, evidence, ticket key (`fix/LS-###-…`).

## 8. Risks

| Risk | Mitigation |
| --- | --- |
| R-21 build environment blocked | CI is the shared runner; local runs as backup |
| R-12 no native Kannada reviewer | Kannada strings stay "Needs native review"; release blocked until reviewed |
| R-13 Kannada TTS unavailable for TalkBack | Document limitation per device; test on a device with a Kannada voice if possible |
| Flaky emulator jobs | Keep E2E few; most UI coverage on Robolectric |
