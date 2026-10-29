# ADR-017: Build and test verification environment

- **Status:** Proposed — awaiting owner approval
- **Approver:** Project owner
- **Date:** 2026-10-29 (P1 draft)
- **Related tickets:** LS-030, LS-130

## Context

The cloud agent workspace cannot reach dl.google.com (Google Maven, Android SDK), so agents cannot build Android there (R-21).

## Options considered

- Allow dl.google.com in the cloud environment
- GitHub Actions only
- owner machine only

## Decision

Recommended (decision D3): allow dl.google.com in the cloud environment AND run GitHub Actions on every PR; the owner's Windows/macOS machines remain the reference for manual and device checks.

## Rationale

No PR may claim a passing build unless CI or a local run produced it.

## Consequences

CI workflow (LS-130) becomes part of the foundation milestone.
