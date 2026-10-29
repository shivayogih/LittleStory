# ADR-007: Localized mock content

- **Status:** Proposed — awaiting owner approval
- **Approver:** Project owner
- **Date:** 2026-10-29 (P1 draft)
- **Related tickets:** LS-036, LS-044

## Context

Stories must exist in EN & KN

## Options considered

- String resources
- per-language JSON assets

## Decision

assets/mock/*_{tag}.json keyed by stable IDs; EN fallback

## Rationale

Scales to more stories/languages; mirrors future API

## Consequences

Loader + parity check needed
