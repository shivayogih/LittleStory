# LittleStory — Product Vision

Purpose: state what LittleStory is, who it serves and what the MVP must achieve, so every later decision can be checked against it.

Status: Draft for owner review — P1, 29 Oct 2026

Sources: master orchestrator brief §4 (product vision, goals, MVP boundary, languages); `docs/design/LittleStory_Figma_Design_Details.md`; workbook `docs/project-management/LittleStory_Jira_Backlog.xlsx` (Overview sheet); P0 discovery assessment (decisions D1–D15).

## Vision

**Little Stories. Big Imagination.** LittleStory is a native Android app that helps children aged about 3–12 discover, create, read, listen to and revisit imaginative stories, with parents and caregivers in control of the experience.

The app should feel magical, calm, safe, friendly and polished. It is usable in English and Kannada from the first release, and adding another language later must be a resource and configuration change, not a code rewrite.

## Target users

| User | Need | How the MVP serves it |
| --- | --- | --- |
| Child (≈3–12) | Stories that feel personal, calm bedtime listening, easy navigation | Personalised Home, story cards, Reader with text-size control, simulated narration, sleep timer |
| Parent / caregiver | Control, privacy, a quick way to create a story about the child's day | Setup, Child profiles, Create a Story, Profile hub, privacy messaging |
| English- and Kannada-speaking families | Full experience in their language | All UI and mock story content in EN and KN, language picker shows each language in its own script |
| Future speakers of other languages | Same app, new language | Language registry + resources; no feature-code change (Won't ship in MVP) |
| Engineering / QA | Maintainable, testable code | Clean architecture, no hardcoded values, deterministic mock data, automated checks |
| Future backend / CMS | Replace mocks without rewriting screens | Language-aware repository interfaces (see `docs/api/` when written) |

Do not assume every child has the same reading ability, language, attention span or accessibility needs.

## Product goals (from brief §4.2)

1. Reproduce the approved design accurately (design pack in `docs/design/` until the live Figma Make file can be re-verified).
2. Make the main storytelling journey easy to understand.
3. Support deterministic local mock data for the MVP.
4. Separate UI state, business logic and data access.
5. Support deterministic automated testing.
6. Provide accessible touch targets, semantics and readable text.
7. Prepare clean interfaces for later backend integration.
8. Keep child-related data collection minimal.
9. Never mislead users about simulated capabilities.
10. Establish reliable build, review and release processes.
11. Fully support English and Kannada for all UI text and MVP mock story content.
12. Add new languages by adding resources and one registry entry.
13. Avoid hardcoded values so every change has one central home.

## Experience principles

| Principle | What it means in the app |
| --- | --- |
| Calm, bedtime-first | Cream light UI; four deliberate "night" screens (Onboarding, Generating, Player, Premium hero); gentle twinkle motion that respects reduced-motion settings |
| Parent in control | Profiles are private and parent-managed; create flow labelled "Parent guided"; privacy notes on Setup, Create, Child profiles, Downloads |
| Honest | Simulated generation, narration, downloads and subscription are labelled as simulations; no fake "done" claims |
| Rooted in India | Indian Tales, festivals, culture banner; Kannada as a first-class language; locale-aware numbers, currency (₹) and times |
| Inclusive | 48 dp targets, WCAG AA contrast, localized TalkBack labels, font scaling to 200 %, layouts that grow with Kannada text |

## Success measures for the MVP

| Measure | Target | Evidence |
| --- | --- | --- |
| Designed journeys A–F work end to end in EN and KN | 100 % | E2E tests (LS-121) |
| User-facing strings with a Kannada value | 100 %, native-reviewed before release | Translation sheet, `checkTranslations` task (LS-041, LS-047) |
| Hardcoded user-facing text, colours, dimensions in feature code | 0 | Lint/Detekt (LS-034), maintainability review (LS-125) |
| Screens matching the design pack | All 22 captured states reviewed by the owner | Screenshot suite (LS-122), fidelity review (LS-124) |
| Child data leaving the device | None | Security review (LS-126) |

## What LittleStory is not (MVP)

Real AI generation, backend, accounts, cloud sync, payments, production TTS, push notifications, analytics, runtime machine translation, and shipped languages other than English and Kannada. See [mvp-scope.md](mvp-scope.md).

## Related

[requirements.md](requirements.md) · [user-journeys.md](user-journeys.md) · [mvp-scope.md](mvp-scope.md) · [../design/screen-inventory.md](../design/screen-inventory.md)
