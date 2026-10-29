# ADR-016: Minimum SDK

- **Status:** Proposed — awaiting owner approval
- **Approver:** Project owner
- **Date:** 2026-10-29 (P1 draft)
- **Related tickets:** LS-030, LS-040

## Context

Brief 8.6 asks to set compatibility from evidence; Kannada rendering and per-app language behaviour vary on old devices.

## Options considered

- minSdk 24
- 26
- 28

## Decision

Recommended (decision D5): minSdk 26 (Android 8.0); compile/target SDK = latest stable verified in LS-030.

## Rationale

Font and per-app-language behaviour is predictable from API 26; AppCompat backports setApplicationLocales below Android 13 via AppLocalesMetadataHolderService(autoStoreLocales=true).

## Consequences

Devices below Android 8 are unsupported; documented in known-limitations.
