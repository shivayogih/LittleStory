# Child safety requirements

Purpose: requirements that protect children and give parents control, for the MVP and before any backend or real AI is launched.
Status: Draft for owner review — P1, 29 Oct 2026
Sources: master orchestrator brief §4.1, §8.5, §11.2, §18; design details §3.2, §3.7, §3.13, §3.14; workbook FR-22, NFR-07, NFR-12, Risks R-11, R-15; P0 assessment D13, D14.

> **Legal review required.** The regulatory notes below are a planning summary, not legal advice. A qualified reviewer must confirm obligations before any data leaves the device or the app is published.

## 1. MVP requirements (device-only)

| ID | Requirement | Source |
| --- | --- | --- |
| CS-01 | Collect only a child's first name, age, avatar and optional interests; no photos, surnames, school, location or contact details | Brief §8.5; D14 |
| CS-02 | All child data stays on the device; no network transmission | Brief §4.3 |
| CS-03 | Parent-managed profiles: copy states that profiles are private and parent-managed (as designed in SCR-02, SCR-14) | Design §3.2, §3.14 |
| CS-04 | Simulations are honest: generation, narration, downloads and subscription are labelled as simulated or illustrative | NFR-12 |
| CS-05 | No ads, no in-app purchases, no external links reachable by a child without a parent step | Brief §8.5 |
| CS-06 | No push or remote notifications; bedtime reminder is UI-only unless approved (D13) | Brief §4.3 |
| CS-07 | Mock stories are reviewed for age-appropriateness in both languages before release | Brief §11.2 |
| CS-08 | Parents can delete a child profile and all local data (needs design: SCR-21 Privacy & parent controls) | FR-22 |
| CS-09 | Privacy and safety information available in English and Kannada | Brief §8.5 |
| CS-10 | Story input text is not stored beyond the generated story the parent chooses to keep, and never logged | Brief §8.5 |

## 2. Before a backend or real AI launch

| ID | Requirement |
| --- | --- |
| CS-11 | Verifiable parental consent before any child data is processed off-device |
| CS-12 | Account-level data access, export and deletion within a defined period |
| CS-13 | Retention schedule for prompts, generated stories and logs (see `docs/api/ai-generation-flow.md` §6) |
| CS-14 | Content moderation of input and output in every supported language, with age-band policies |
| CS-15 | Parent reporting of a harmful story, with an incident process |
| CS-16 | No behavioural advertising or profiling of children; no tracking SDKs |
| CS-17 | Data processing agreements with any AI or hosting provider, including no training on children's data |

## 3. Regulatory considerations (legal review required)

| Jurisdiction | Instrument | Relevance (planning summary) |
| --- | --- | --- |
| India | Digital Personal Data Protection Act, 2023 (and its Rules) | Treats under-18s as children; requires verifiable consent of a parent or lawful guardian before processing a child's personal data; restricts tracking, behavioural monitoring and targeted advertising directed at children. Primary market for a Kannada audience. |
| United States (if distributed there) | COPPA (FTC rule) | Verifiable parental consent for collecting personal information from children under 13; parental review and deletion rights; data minimisation. |
| EU/UK (if distributed there) | GDPR / UK GDPR, Age Appropriate Design Code | Parental consent below the national age threshold; high-privacy defaults. |
| Google Play | Families policy, Data safety form | Designed-for-Families requirements, SDK restrictions, accurate data disclosures. |

## 4. Open decisions

1. D14 — parent name/email fields in MVP.
2. Parental gate for the settings area and subscription screen.
3. Target countries for the first release (drives which regimes apply).
4. Named owner for privacy/legal review before release.
