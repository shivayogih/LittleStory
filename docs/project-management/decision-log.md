# Decision log

Owner decisions for LittleStory: what is decided, what is recommended and what still needs the owner.

Status: Draft for owner review — P1, 29 Oct 2026

Sources: P0 Discovery Assessment (29 Oct 2026), docs/architecture/adr/, master orchestrator brief section 2.3

A decision moves to **Approved** only when the project owner says so in the project thread or a PR review. Agents never mark their own recommendations approved.

## Approvals granted

| Date | Approval | Scope | Evidence |
|---|---|---|---|
| 29 Oct 2026 | A — Discovery | Read-only P0 assessment | Requested in the master brief |
| 29 Oct 2026 | B — Documentation | Create/update docs, prompts, agent instructions and the backlog workbook | Owner: "continue the execution" after the P0 approval question |

## Open decisions (from the P0 assessment)

| # | Decision | Recommendation | Related | Status |
|---|---|---|---|---|
| D1 | Approval B for P1 documentation | Approve | LS-003–008 | Approved 29 Oct 2026 |
| D2 | Merge PR #1, set default branch to `main`, create `development` | Approve; owner performs the merge and settings change | R-22 | Open |
| D3 | Where Android builds and tests run | Allow `dl.google.com` in the cloud environment and run GitHub Actions on every PR | ADR-017, R-21 | Open |
| D4 | Module structure | 7 feature modules | ADR-002 | Open |
| D5 | minSdk | 26 | ADR-016 | Open |
| D6 | Navigation library | Navigation Compose with type-safe routes unless LS-030 favours Navigation 3 | ADR-001 | Open |
| D7 | Screenshot testing | Roborazzi | ADR-011 | Open |
| D8 | Kannada font | Bundle Noto Sans Kannada (OFL) as fallback | ADR-008, R-03 | Open |
| D9 | Language in onboarding | Keep Setup's language field, registry-driven (English, ಕನ್ನಡ only); build the language screen from existing components | ADR-006, R-02 | Open |
| D10 | Missing designs (splash, language, privacy, help, account, audio prefs) | Provide designs or approve pattern-based screens | R-05, LS-004 | Open |
| D11 | Brand assets | Confirm the LittleStory wordmark; ignore LittleKatha in screenshots | R-01 | Open |
| D12 | Native Kannada reviewer | Name a reviewer before P6 | R-12, LS-047 | Open |
| D13 | Bedtime reminders | Settings UI only in MVP | ADR-015, R-14 | Open |
| D14 | Personal data collected | Child first name, age, avatar, interests; drop parent email | R-11 | Open |
| D15 | Detekt + ktlint | Approve both | ADR-018 | Open |

## Architecture decisions

All ADRs are in [docs/architecture/adr/](../architecture/adr/README.md); every one is Proposed until approved.
