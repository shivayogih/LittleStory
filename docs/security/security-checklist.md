# Security checklist

Purpose: a reviewable checklist for every PR and for the P7 security review (LS-126).
Status: Draft for owner review — P1, 29 Oct 2026
Sources: master orchestrator brief §18, §19, §22; workbook tickets LS-126, LS-130; Risks R-24; [privacy-threat-model.md](privacy-threat-model.md).

Passing Lint does not make the app secure (brief §18). Items are checked by a human reviewer with evidence.

## 1. Per pull request

- [ ] No secrets, tokens, keystores, `local.properties` or machine paths in the diff (repository is public, R-24)
- [ ] No new permissions in any manifest without an approved ticket
- [ ] No new third-party SDK or dependency without approval; versions only in `gradle/libs.versions.toml`
- [ ] No network code, analytics or crash-reporting SDK in the MVP
- [ ] No child or parent personal data written to logs, analytics or files outside app storage
- [ ] New exported components or intent filters justified and reviewed
- [ ] Simulated features clearly labelled in UI and code (NFR-12)
- [ ] Debug-only code guarded by build type

## 2. Repository and CI

- [ ] Secret scanning enabled (GitHub secret scanning / push protection on the public repo)
- [ ] Dependency vulnerability alerts (Dependabot) enabled
- [ ] CI uses no long-lived credentials; signing keys never stored in the repository
- [ ] Branch protection on `main` and `development` (owner to confirm; not verifiable in P0)
- [ ] Actions pinned to major versions or SHAs; `permissions:` set to least privilege

## 3. App configuration (verify at P7)

- [ ] `android:allowBackup` / `dataExtractionRules` set deliberately (see threat T-02)
- [ ] `usesCleartextTraffic="false"`; network security config if networking is added
- [ ] Only the launcher activity exported
- [ ] Release build: minify/obfuscation decision recorded; no debug logging; pseudo-locales disabled
- [ ] No WebView with JavaScript in MVP
- [ ] Room database contains only data listed in the privacy inventory
- [ ] App works fully offline with no network permission (MVP)

## 4. Future backend milestone (before any real integration)

- [ ] HTTPS only, certificate validation untouched
- [ ] Provider and API secrets on the backend only
- [ ] Server-side authorization for every child-profile resource
- [ ] Input validation and moderation in every supported language
- [ ] Rate limits and abuse controls
- [ ] Consent, retention and deletion implemented (see [child-safety-requirements.md](child-safety-requirements.md))
- [ ] Privacy policy and Play Data safety form updated

Root detection or device-integrity checks are not added in the MVP (brief §18).
