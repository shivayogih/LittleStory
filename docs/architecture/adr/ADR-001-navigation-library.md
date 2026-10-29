# ADR-001: Navigation library

- **Status:** Proposed — awaiting owner approval
- **Approver:** Project owner
- **Date:** 2026-10-29 (P1 draft)
- **Related tickets:** LS-050

## Context

Need typed navigation for ~22 destinations

## Options considered

- Navigation-Compose 2 (type-safe routes)
- Navigation 3

## Decision

Recommended: Navigation Compose with type-safe `@Serializable` routes. Navigation 3 is re-evaluated in LS-030 against the pinned AGP, Kotlin and Compose versions; switch only if it is stable and compatible.

## Rationale

Mature, widely documented, works with Hilt ViewModels and Compose testing; type-safe routes remove string routes from feature code.

## Consequences

Affects all feature modules
