# ADR-009: UiText abstraction

- **Status:** Proposed — awaiting owner approval
- **Approver:** Project owner
- **Date:** 2026-10-29 (P1 draft)
- **Related tickets:** LS-040

## Context

ViewModels must not hold translated strings

## Options considered

- Resource IDs
- UiText sealed type

## Decision

UiText (StringRes, PluralRes, Dynamic)

## Rationale

Testable, language-agnostic VMs

## Consequences

Small boilerplate
