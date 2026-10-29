# ADR-011: Screenshot testing

- **Status:** Proposed — awaiting owner approval
- **Approver:** Project owner
- **Date:** 2026-10-29 (P1 draft)
- **Related tickets:** LS-035, LS-122

## Context

Need EN/KN visual regression

## Options considered

- Compose Preview Screenshot Testing
- Roborazzi
- Paparazzi

## Decision

Recommended: Roborazzi (JVM, Robolectric) for component and screen screenshots in EN, KN and en-XA. Compose Preview Screenshot Testing remains the alternative if LS-030 shows it is stable for the pinned AGP.

## Rationale

Runs on the JVM in CI without an emulator, supports Compose and locale qualifiers, and records/compares baselines in Gradle tasks.

## Consequences

CI runtime
