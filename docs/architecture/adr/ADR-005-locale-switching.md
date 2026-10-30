# ADR-005: Locale switching

- **Status:** Proposed — awaiting owner approval
- **Approver:** Project owner
- **Date:** 2026-10-29 (P1 draft)
- **Related tickets:** LS-040

## Context

EN/KN now, more later; Android 13 per-app language

## Options considered

- AppCompat per-app locales
- custom context wrapper

## Decision

AppCompatDelegate.setApplicationLocales + locales_config.xml

## Rationale

Official, persists automatically, system settings integration

## Consequences

Verify minSdk behaviour
