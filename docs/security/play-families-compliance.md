# Google Play Families policy and child-privacy audit

Purpose: check LittleStory against Google Play's Families policy and child-privacy rules (COPPA, GDPR-K, India's DPDP Act), list the gaps, and record what is done in the app and what still needs the owner or a legal reviewer.
Status: Draft for owner review — 31 Oct 2026
Related: [child-safety-requirements.md](child-safety-requirements.md), [security-review.md](security-review.md), [privacy-policy-draft.md](privacy-policy-draft.md)

> **Legal review required.** This is an engineering audit, not legal advice, and it does not make LittleStory compliant by itself. The privacy-policy text in the app and in [privacy-policy-draft.md](privacy-policy-draft.md) is an agent draft in English and Kannada. A qualified reviewer must approve it before release. Google's policy pages change, so re-check them in Play Console before submitting.

## 1. What the app does today (facts the audit relies on)

| Fact | Evidence |
|---|---|
| No network access: the merged manifest has no `INTERNET` permission | Merged release manifest; [security-review.md](security-review.md) T-06 |
| No ads, analytics, crash reporting, tracking or billing SDK | `gradle/libs.versions.toml` |
| No advertising ID: the `AD_ID` permission is explicitly removed | `app/src/main/AndroidManifest.xml` (this change) |
| No location, camera, microphone, contacts or notification permission | Merged manifest |
| Child data (first name, age, avatar, interests) stays in app-private storage; excluded from cloud backup and device transfer | `data_extraction_rules.xml`, `allowBackup="false"` |
| Parents can delete all data on the device | Privacy & parent controls (SCR-21) |
| Story creation is a local template, labelled as simulated, no AI | `generating_simulated` label on Create; Help and Privacy text |
| Subscription is illustrative, no payment | `SubscriptionScreen`, `FeatureFlags.demoSubscription` |

## 2. Audit against the rules

Status: **Done** = in the app after this change · **Owner** = Play Console or business decision · **Legal** = needs a qualified reviewer · **Later** = required before a backend or real AI ships.

| # | Requirement (source) | Status | Notes |
|---|---|---|---|
| F-01 | Declare target age groups and complete Families declarations (Play Console › App content › Target audience and content) | **Owner** | Ages 3–12 match `AppConfig.Setup.ageRange`; under-13 groups bring the full Families policy into scope |
| F-02 | Content rating questionnaire (IARC) | **Owner** | Mock stories are gentle; answer "no" to violence, user interaction, sharing location, purchases |
| F-03 | Privacy policy: link on the store listing **and** inside the app (Families policy, User Data policy) | **Done (draft)** in app; **Owner** for the hosted URL | In-app policy on Privacy & parent controls, labelled draft by `FeatureFlags.draftLegalText`. Host [privacy-policy-draft.md](privacy-policy-draft.md) at a public URL after legal review and add it to the listing |
| F-04 | Data safety form must match the app | **Owner** (answers drafted in §3) | Off-device transfer is what Play counts as "collected"; on-device storage alone is not |
| F-05 | No advertising ID or other persistent identifiers from children (Families policy) | **Done** | `AD_ID` removed in the manifest; no identifier is read |
| F-06 | Ads only from Families self-certified ad SDKs; no interest-based ads | **Done (n/a)** | No ads |
| F-07 | No location permission for apps aimed only at children | **Done** | None requested |
| F-08 | Third-party SDKs must be suitable for children's apps | **Done** | AndroidX, Hilt, Room, DataStore, Kotlin libraries only; no data-collecting SDK |
| F-09 | Parental gate before purchases, settings changes and links out of the app (Families best practice; COPPA-style "grown-up check") | **Done** | Multiplication question (`ParentalGateDialog`) before Manage children, editing a child, Language, Subscription and Privacy & parent controls. No external links exist yet; any future link must sit behind the gate |
| F-10 | Honest disclosure of simulated features and generated content | **Done** | Create shows the "simulated, no AI" label; Privacy explains how stories are made |
| F-11 | Parent can review and delete a child's data (COPPA, GDPR, DPDP) | **Done** | Manage children (edit) and Delete all data; both behind the gate |
| F-12 | Verifiable parental consent before collecting children's personal data (COPPA, GDPR-K, DPDP Act) | **Not triggered today; Later** | Nothing leaves the device, so there is no collection by the developer. Required (CS-11) before any backend, account or AI call |
| F-13 | AI-generated content: prevent harmful output and give users an in-app way to report it (Play AI-Generated Content policy) | **Later** | Not applicable while generation is a local template. Before real AI: moderation (CS-14), in-app "Report this story" (CS-15), consent (F-12) |
| F-14 | Account deletion inside the app and on the web (Play User Data policy) | **Done (n/a)** | No accounts. Required when accounts arrive |
| F-15 | Store listing must not mislead (e.g. narration, AI, premium) | **Owner** | Describe narration and creation as simulated until real ones ship |
| F-16 | Contact details for privacy questions | **Owner / Legal** | The policy shows a placeholder until a contact is chosen |
| F-17 | Kannada privacy text reviewed by a native speaker and the legal reviewer | **Legal** | All new Kannada strings are in the draft ledger |
| F-18 | Teacher Approved programme (optional) | **Owner** | Optional; needs the store listing and content review first |
| F-19 | Notifications to children (Families and COPPA push rules) | **Done (n/a)** | Bedtime reminder and inbox are UI-only; no notification permission (D13) |

## 3. Draft Data safety answers (for the owner to enter in Play Console)

| Question | Draft answer | Why |
|---|---|---|
| Does the app collect or share any required user data types? | **No** | The app has no network access; data never leaves the device |
| Is all user data encrypted in transit? | Not applicable (nothing is transmitted) | |
| Can users request that data is deleted? | Data is stored only on the device and can be deleted in the app | Privacy & parent controls › Delete all data |
| Independent security review | No | |

Re-answer the form before any release that adds `INTERNET`, a backend, AI, analytics or crash reporting.

## 4. What changed in the app

- `ParentalGateDialog` and `ParentGated` in `:core:ui`, questions from `ParentalGateChallenges` in `:core:common` (ranges in `AppConfig.ParentalGate`). Gated: Manage children, edit or add a child from Profile, Language, Subscription, Privacy & parent controls. Answered on an in-dialog keypad (no system keyboard), so it works the same in English and Kannada; a wrong answer swaps in a new question. Closes security-review T-08 / S1.
- Privacy & parent controls now carries **Family safety** (audience, no ads or tracking, grown-up areas, story content) and a **Privacy policy (draft)** section, with a visible draft notice controlled by `FeatureFlags.draftLegalText`.
- Manifest removes the `AD_ID` permission.

## 5. Before release (owner checklist)

1. Legal review of the policy text in both languages; then set `FeatureFlags.draftLegalText` to false.
2. Choose a privacy contact and host the approved policy at a public URL; add the URL to the listing and to the in-app policy.
3. Complete Target audience, Content rating and Data safety in Play Console (§2, §3).
4. Before any backend or AI: F-12 consent, F-13 moderation and reporting, CS-11 to CS-17 in [child-safety-requirements.md](child-safety-requirements.md).
