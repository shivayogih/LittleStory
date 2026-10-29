# Risk register

Open risks and questions for LittleStory, mirrored from the workbook's *Risks & Questions* sheet plus four risks found in P0 discovery (R-21 to R-24).

Status: Draft for owner review — P1, 29 Oct 2026

Sources: docs/project-management/LittleStory_Jira_Backlog.xlsx, P0 Discovery Assessment (29 Oct 2026)

| ID | Risk / question | Category | Probability | Impact | Mitigation | Owner | Decision needed | Milestone | Status |
|---|---|---|---|---|---|---|---|---|---|
| R-01 | **Old brand in design assets** — Screenshots show 'LittleKatha' in logo, Profile group header and Premium banner | Design | High | Low | Rebuild brand mark with 'LittleStory'; grep check in CI | Owner | Confirm final logo | P2 | Open |
| R-02 | **Design offers 7 languages** — Setup/Create language lists show English, Hindi, Kannada, Tamil, Telugu, Marathi, Malayalam | Scope | High | Medium | Registry-driven list exposing only EN/KN | Owner | Confirm EN/KN only for MVP | P1 | Open |
| R-03 | **Fonts lack Kannada glyphs** — DM Sans and Manrope do not cover Kannada script | Localization | High | High | Noto Sans Kannada fallback + KN line-height tokens | Localization Eng. | Approve fallback font | P2 | Open |
| R-04 | **Kannada text expansion** — 10sp nav labels, chips, buttons, tabs may clip | Localization | High | Medium | Flexible layouts, pseudo-locale tests, KN screenshots | Localization Eng. | Accept wrapping rules | P3–P6 | Open |
| R-05 | **Missing designs** — Splash, Language preferences, Privacy & parent controls, Help & about, Account settings, Audio preferences, notifications empty state | Design | High | Medium | Simple pattern-based screens approved by owner | Owner | Provide designs or approve patterns | P1 | Open |
| R-06 | **Gendered sample copy** — Placeholder 'helped his friend' assumes gender; hard to translate | Content | Medium | Low | Neutral copy using child name | Owner | Approve copy | P1 | Open |
| R-07 | **Low contrast secondary text** — measured: muted #716B80 passes (4.87:1 on cream); nav-inactive #817B8D fails (3.89:1), lavender focus ring 2.53:1 and progress fill 2.02:1 fail the 3:1 non-text rule (see docs/design/accessibility-spec.md) | Accessibility | High | Medium | Measure; adjust token or size | Accessibility Eng. | Approve token change | P7 | Open |
| R-08 | **Small touch targets** — Chips and 42px icon buttons below 48dp | Accessibility | High | Medium | Expand touch area without changing visuals | Accessibility Eng. | – | P2 | Open |
| R-09 | **No motion spec for transitions** — Screens swap instantly; sheets don't slide; toasts don't auto-dismiss | Design | Medium | Low | Use platform defaults; record decision | Owner | Choose transitions | P1 | Open |
| R-10 | **Greeting fixed to 'Good evening'** — No time-of-day variants designed | Content | Medium | Low | Morning/afternoon/evening strings or keep fixed | Owner | Decide | P1 | Open |
| R-11 | **Child & parent personal data** — Parent name, email (example) and child details collected | Privacy | Medium | High | Collect minimum; local only; no email in MVP; delete action | Security Reviewer | Confirm fields | P1 | Open |
| R-12 | **Native Kannada reviewer** — Translations need fluent reviewer before release | Localization | Medium | High | Name reviewer early; review in batches | Owner | Assign reviewer | P6 | Open |
| R-13 | **Kannada TTS for TalkBack** — Device may lack Kannada voice | Accessibility | Medium | Medium | Document limitation; test on device with voice | Accessibility Eng. | – | P7 | Open |
| R-14 | **Reminder scheduling** — Real reminders need POST_NOTIFICATIONS + scheduling | Scope | Medium | Medium | Settings-only in MVP unless approved (ADR-015) | Owner | Decide | P1 | Open |
| R-15 | **Demo pricing perceived as real** — ₹ prices shown | Compliance | Low | Medium | Feature flag + explicit disclaimer; no billing code | Owner | Ship or hide | P6 | Open |
| R-16 | **Illustration fidelity effort** — CSS-drawn art must be recreated in Compose | Delivery | Medium | Medium | ADR-010; prioritise 6 motifs | UI Eng. | – | P2 | Open |
| R-17 | **Repository state unknown** — Discovery (LS-001) not yet run | Delivery | High | High | Run P0 first; no assumptions | Orchestrator | – | P0 | Open |
| R-18 | **Category screen hard-coded** — Prototype only shows Indian Tales; hero art per category undefined | Design | Medium | Low | Generic screen + per-category data | Owner | Approve generic hero | P4 | Open |
| R-19 | **Prototype placeholder routes** — Several settings rows route to unrelated screens | Design | High | Medium | Map to real or disabled destinations (LS-004) | Owner | Decide per row | P1 | Open |
| R-20 | **15 s control glyph overlap** — '15' and arrow glyphs overlap on Player | Design defect | High | Low | LS-094 – proper icons | UI Eng. | – | P5 | Open |
| R-21 | **Build environment blocked** — Cloud agent workspace gets 403 for dl.google.com (Google Maven, Android SDK) and fonts.google.com; agents cannot build or test Android there | Delivery | High | High | Allow dl.google.com in the cloud environment; GitHub Actions CI on every PR; no PR claims a build it did not run (ADR-017) | Owner | Choose build environment (D3) | P2 | Open |
| R-22 | **Default branch misconfigured** — GitHub default branch is claude/project-thread-sszyf7 (docs branch), not main; clones and new PRs default to the wrong base | Delivery | High | Medium | Owner merges PR #1, sets default branch to main, creates development from main | Owner | Approve repo hygiene (D2) | P1 | Open |
| R-23 | **Design drift** — Agents cannot re-read the Figma Make file; docs/design/ is a 29 Oct 2026 snapshot that can go stale silently | Design | Medium | Medium | Owner tells agents when Make changes; re-export pack; screen IDs reference the pack version | Owner | – | P1–P7 | Open |
| R-24 | **Public repository** — Repository is public; any secret, personal or child data committed is exposed | Security | Medium | High | Secret scanning in CI, .gitignore for local.properties/keystores, synthetic test data only | Security Reviewer | Keep public or make private | P1 | Open |
