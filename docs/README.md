# LittleStory documentation

Index of every document in `docs/`. Status of all P1 documents: draft for owner review (29 Oct 2026). Start with [product/product-vision.md](product/product-vision.md), [architecture/high-level-design.md](architecture/high-level-design.md) and [localization/localization-strategy.md](localization/localization-strategy.md).

## Product

| Document | What it covers |
|---|---|
| [accounts-and-subscriptions-plan.md](product/accounts-and-subscriptions-plan.md) | Plan login, logout, parent and child profiles, account deletion, guest/offline use and subscription readiness, and how they fit the existing screens and parental gate (proposed, 31 Oct 2026). |
| [edge-cases.md](product/edge-cases.md) | List the unusual inputs, states and environments each screen must handle, so tickets and tests cover them from the start. |
| [functional-requirements.md](product/functional-requirements.md) | The catalogue of functional requirements (FR-01…FR-25) with priority, source, screens, acceptance criteria, tickets and tests. |
| [mvp-scope.md](product/mvp-scope.md) | Define what the first release includes and excludes, how simulations are labelled, and which scope decisions are still open. |
| [non-functional-requirements.md](product/non-functional-requirements.md) | The catalogue of quality requirements (NFR-01…NFR-12 from the workbook, plus NFR-13…NFR-17 newly proposed from the brief) with acceptance criteria, tickets and tests. |
| [product-vision.md](product/product-vision.md) | State what LittleStory is, who it serves and what the MVP must achieve, so every later decision can be checked against it. |
| [requirements.md](product/requirements.md) | Entry point to the requirement catalogues and the traceability matrix linking each requirement to screens, tickets and test cases. |
| [user-journeys.md](product/user-journeys.md) | Map journeys A–G from the brief onto the screens that exist in the design pack, show each as a flow, and record where the design is silent. |

## Design

| Document | What it covers |
|---|---|
| [LittleStory_Figma_Design_Details.md](design/LittleStory_Figma_Design_Details.md) | **Source:** Figma Make file "Mobile App UI Prototype" (`2DYtUW39GtnsDpUd5jSu6x`), version 1 |
| [accessibility-spec.md](design/accessibility-spec.md) | Set the accessibility targets for the app, record measured contrast for the design tokens, and list the design gaps that implementation must fix. |
| [design-system.md](design/design-system.md) | Define how the extracted design tokens and components become the Compose design system in `:core:designsystem` and `:core:ui`, so feature screens use semantic tokens and shared com |
| [figma-audit.md](design/figma-audit.md) | Record how the design was inspected, what was verified and what was not, and every discrepancy and gap found, so implementation never relies on unverified design facts silently. |
| [screen-inventory.md](design/screen-inventory.md) | One entry per screen (SCR-01…SCR-23) with design reference, navigation, components, inputs, states, data, accessibility, localization notes, screenshot-test needs, acceptance crite |
| [screen-state-matrix.md](design/screen-state-matrix.md) | List, for every major screen, which states are meaningful and must be implemented and tested, with state diagrams for the stateful flows. |
| [visual-regression-strategy.md](design/visual-regression-strategy.md) | Define how screens are compared against the design pack and protected from visual regressions in English, Kannada and pseudo-locales. |
| [fidelity-review.md](design/fidelity-review.md) | Record the LS-124 side-by-side comparison of every design screen with the app, what was fixed and what still differs. |

## Architecture

| Document | What it covers |
|---|---|
| [configuration-and-constants.md](architecture/configuration-and-constants.md) | Give every configurable value seen in the prototype exactly one home, so a change is made in one place (brief §4.5, NFR-01). |
| [data-models.md](architecture/data-models.md) | Define the MVP domain models, their fields, identity, persistence and which fields depend on language. |
| [dependency-rules.md](architecture/dependency-rules.md) | Define which module and layer dependencies are allowed, which are forbidden, and how each rule is enforced automatically. |
| [error-handling.md](architecture/error-handling.md) | Define error types per layer, how they map to localized messages, and how retry works. |
| [hardcoding-audit.md](architecture/hardcoding-audit.md) | The LS-125 audit of hardcoded values: method, findings and the scanner rules added. |
| [high-level-design.md](architecture/high-level-design.md) | Describe how the LittleStory Android MVP is structured, how requests and state flow, and where future backend work plugs in. |
| [how-to-change.md](architecture/how-to-change.md) | Step-by-step recipes for the most common changes, each touching one documented home (brief §4.5, LS-150). |
| [module-structure.md](architecture/module-structure.md) | List every Gradle module proposed for the MVP, what it owns, what it may depend on, and which strings it owns. |
| [navigation-design.md](architecture/navigation-design.md) | Define every route, its arguments, the navigation graph and back behaviour for the MVP. |
| [state-management.md](architecture/state-management.md) | Define one pattern for screen state, user actions, one-off effects, text and lifecycle so every feature behaves the same way. |

## Architecture decisions

| Document | What it covers |
|---|---|
| [ADR-001-navigation-library.md](architecture/adr/ADR-001-navigation-library.md) | Need typed navigation for ~22 destinations |
| [ADR-002-module-structure.md](architecture/adr/ADR-002-module-structure.md) | Small MVP, many features, parallel agents |
| [ADR-003-dependency-injection.md](architecture/adr/ADR-003-dependency-injection.md) | Repositories swapped mock → real later |
| [ADR-004-persistence.md](architecture/adr/ADR-004-persistence.md) | Favourites, profiles, progress, downloads, settings must survive restart |
| [ADR-005-locale-switching.md](architecture/adr/ADR-005-locale-switching.md) | EN/KN now, more later; Android 13 per-app language |
| [ADR-006-supported-language-registry.md](architecture/adr/ADR-006-supported-language-registry.md) | Avoid scattered language lists (design lists 7) |
| [ADR-007-localized-mock-content.md](architecture/adr/ADR-007-localized-mock-content.md) | Stories must exist in EN & KN |
| [ADR-008-fonts.md](architecture/adr/ADR-008-fonts.md) | DM Sans/Manrope have no Kannada glyphs |
| [ADR-009-uitext-abstraction.md](architecture/adr/ADR-009-uitext-abstraction.md) | ViewModels must not hold translated strings |
| [ADR-010-story-artwork.md](architecture/adr/ADR-010-story-artwork.md) | Design art is CSS-drawn, no image assets |
| [ADR-011-screenshot-testing.md](architecture/adr/ADR-011-screenshot-testing.md) | Need EN/KN visual regression |
| [ADR-012-configuration-feature-flags.md](architecture/adr/ADR-012-configuration-feature-flags.md) | No hardcoding; simulated features |
| [ADR-013-theme.md](architecture/adr/ADR-013-theme.md) | Design has cream light UI + night screens; no dark mode |
| [ADR-014-simulated-features-policy.md](architecture/adr/ADR-014-simulated-features-policy.md) | No real AI/audio/billing/notifications in MVP |
| [ADR-015-bedtime-reminder-delivery.md](architecture/adr/ADR-015-bedtime-reminder-delivery.md) | Design shows reminder setting; remote notifications excluded |
| [ADR-016-minimum-sdk.md](architecture/adr/ADR-016-minimum-sdk.md) | Brief 8.6 asks to set compatibility from evidence; Kannada rendering and per-app language behaviour vary on old devices. |
| [ADR-017-build-and-test-verification-environment.md](architecture/adr/ADR-017-build-and-test-verification-environment.md) | The cloud agent workspace cannot reach dl.google.com (Google Maven, Android SDK), so agents cannot build Android there (R-21). |
| [ADR-018-static-analysis.md](architecture/adr/ADR-018-static-analysis.md) | No-hardcoding policy needs automated enforcement beyond Android Lint. |
| [README.md](architecture/adr/README.md) | Every decision is **Proposed** until the project owner approves it; approval is recorded by changing the Status line and the workbook's ADRs sheet in the same PR. |

## Localization

| Document | What it covers |
|---|---|
| [adding-a-new-language.md](localization/adding-a-new-language.md) | The complete checklist for adding a language (for example Hindi `hi`) after the MVP, without changing feature code. |
| [glossary.md](localization/glossary.md) | Keep child-friendly terminology consistent across every screen, story and future translation. |
| [kannada-typography.md](localization/kannada-typography.md) | Make Kannada text render correctly, without clipping, in a design built on Latin-only fonts. |
| [localization-strategy.md](localization/localization-strategy.md) | Define how LittleStory ships English and Kannada, falls back to English, switches language per app, and stays ready for future languages without feature-code changes. |
| [string-resource-conventions.md](localization/string-resource-conventions.md) | Tell developers and translators how strings are named, where they live, and how to write them so they translate correctly into Kannada and future languages. |

## Future API and AI (proposed, not implemented)

| Document | What it covers |
|---|---|
| [ai-generation-flow.md](api/ai-generation-flow.md) | Describe the future job-based generation flow and the safety, cost and privacy controls required before any real AI provider is used. |
| [backend-integration-plan.md](api/backend-integration-plan.md) | Define the repository boundaries that let deterministic mock data be replaced by a real backend without rewriting screens. |
| [future-api-contracts.md](api/future-api-contracts.md) | Describe a candidate HTTP contract for catalog, generation, library and progress, including language negotiation. |

## Testing

| Document | What it covers |
|---|---|
| [integration-test-plan.md](testing/integration-test-plan.md) | List the cross-layer and end-to-end tests that prove the journeys work, in English and Kannada. |
| [localization-test-plan.md](testing/localization-test-plan.md) | Verify that English and Kannada are complete, correct and robust, and that new languages can be added without feature-code changes. |
| [screenshot-test-plan.md](testing/screenshot-test-plan.md) | Define the visual regression suite, its fixed configuration and the baseline review rules. |
| [test-data.md](testing/test-data.md) | Specify the deterministic seed data (English and Kannada) used by mock repositories, tests and screenshots. |
| [test-plan.md](testing/test-plan.md) | Scope, entry/exit criteria, environments and device/locale matrix for testing the LittleStory MVP. |
| [test-strategy.md](testing/test-strategy.md) | Define the test pyramid, tools and where each kind of check runs for LittleStory. |
| [ui-test-plan.md](testing/ui-test-plan.md) | Per-screen Compose UI test scope, so every screen is verified for behaviour, states and semantics in English and Kannada. |

## Security, privacy and child safety

| Document | What it covers |
|---|---|
| [child-safety-requirements.md](security/child-safety-requirements.md) | Requirements that protect children and give parents control, for the MVP and before any backend or real AI is launched. |
| [privacy-threat-model.md](security/privacy-threat-model.md) | Identify what personal data LittleStory handles, the threats to it, and the mitigations for the MVP and a future backend. |
| [play-families-compliance.md](security/play-families-compliance.md) | Audit against Google Play's Families policy and child-privacy rules: gaps, what the app does, draft Data safety answers. |
| [privacy-policy-draft.md](security/privacy-policy-draft.md) | Draft privacy policy to host after legal review. |
| [security-review.md](security/security-review.md) | The LS-126 review: results per threat, data inventory check, and open items for the owner. |
| [security-checklist.md](security/security-checklist.md) | A reviewable checklist for every PR and for the P7 security review (LS-126). |

## Project management

| Document | What it covers |
|---|---|
| [decision-log.md](project-management/decision-log.md) | Owner decisions for LittleStory: what is decided, what is recommended and what still needs the owner. |
| [dependency-map.md](project-management/dependency-map.md) | Which tickets must wait for which, which can run in parallel, and who owns the high-conflict files. |
| [risk-register.md](project-management/risk-register.md) | Open risks and questions for LittleStory, mirrored from the workbook's *Risks & Questions* sheet plus four risks found in P0 discovery (R-21 to R-24). |
| [roadmap.md](project-management/roadmap.md) | Phases P0–P8 for the LittleStory Android MVP, their gates and current status. |
| [LittleStory_Jira_Backlog.xlsx](project-management/LittleStory_Jira_Backlog.xlsx) | Backlog, requirements, test cases, ADRs, risks, translations and dashboard workbook |

## Release

| Document | What it covers |
|---|---|
| [build-and-run.md](release/build-and-run.md) | How to set up, build, test and run LittleStory on Windows, macOS and Linux. |
| [known-limitations.md](release/known-limitations.md) | An honest list of what the MVP does not do or has not verified, kept current through release. |
| [release-checklist.md](release/release-checklist.md) | The gate every LittleStory release must pass before the owner grants Approval F. |

Design assets: [design/screens/](design/screens/) (22 screenshots) and [design/design-tokens.json](design/design-tokens.json).
