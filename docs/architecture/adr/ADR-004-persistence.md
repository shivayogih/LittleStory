# ADR-004: Persistence

- **Status:** Proposed — awaiting owner approval
- **Approver:** Project owner
- **Date:** 2026-10-29 (P1 draft)
- **Related tickets:** LS-038

## Context

Favourites, profiles, progress, downloads, settings must survive restart

## Options considered

- DataStore only
- Room + DataStore

## Decision

Room for relational data; DataStore for preferences

## Rationale

Queries for library tabs; simple prefs

## Consequences

Migration discipline
