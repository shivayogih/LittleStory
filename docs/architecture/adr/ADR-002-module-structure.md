# ADR-002: Module structure

- **Status:** Proposed — awaiting owner approval
- **Approver:** Project owner
- **Date:** 2026-10-29 (P1 draft)
- **Related tickets:** LS-031

## Context

Small MVP, many features, parallel agents

## Options considered

- Single module
- layered
- feature modules

## Decision

Recommended (P0 assessment, decision D4): 7 feature modules — :feature:onboarding, :feature:home, :feature:discover (Discover, Category, Story detail), :feature:create (Create, Generating, Story ready), :feature:reader (Reader, Player, Sleep timer), :feature:library (My Stories, Downloads), :feature:settings (Profile hub, Child profiles, Reminder, Notifications, Subscription, Language, Privacy, Help) — plus :app, build-logic, :core:designsystem, :core:ui, :core:common, :domain, :data (14 modules). Replaces the earlier 12-feature proposal.

## Rationale

Brief section 9.4 prefers fewer modules for a small MVP. Grouping by user journey keeps one owner per lane for parallel agents while cutting Gradle overhead from 19 to 14 modules.

## Consequences

Convention plugins in build-logic are still needed. Features never depend on each other; cross-feature navigation goes through routes registered in :app. Splash (SCR-19) lives in :app.
