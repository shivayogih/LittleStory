# ADR-012: Configuration & feature flags

- **Status:** Proposed — awaiting owner approval
- **Approver:** Project owner
- **Date:** 2026-10-29 (P1 draft)
- **Related tickets:** LS-033

## Context

No hardcoding; simulated features

## Options considered

- Scattered constants
- AppConfig objects + BuildConfig

## Decision

AppConfig (limits, timings, options) + FeatureFlags + BuildConfig

## Rationale

One place to change

## Consequences

–
