# ADR-018: Static analysis

- **Status:** Proposed — awaiting owner approval
- **Approver:** Project owner
- **Date:** 2026-10-29 (P1 draft)
- **Related tickets:** LS-034

## Context

No-hardcoding policy needs automated enforcement beyond Android Lint.

## Options considered

- Lint only
- Lint + Detekt
- Lint + Detekt + ktlint

## Decision

Recommended (decision D15): Android Lint (HardcodedText, MissingTranslation as errors) + Detekt (magic numbers, complexity, custom rule for raw Color/dp in feature modules) + ktlint formatting.

## Rationale

Catches magic numbers and style drift on both Windows and macOS identically.

## Consequences

Baseline files must stay empty for new code; rules configured once in build-logic.
