# ADR-015: Bedtime reminder delivery

- **Status:** Pending Approval — awaiting owner approval
- **Approver:** Project owner
- **Date:** 2026-10-29 (P1 draft)
- **Related tickets:** LS-111

## Context

Design shows reminder setting; remote notifications excluded

## Options considered

- Settings only
- local scheduled notification

## Decision

Recommended (decision D13): settings UI only in the MVP — the reminder is stored but no notification is scheduled, and the screen labels it as a preview. A real local notification (POST_NOTIFICATIONS, exact-alarm rules) becomes a separate approved ticket.

## Rationale

Brief section 4.3 forbids unapproved permissions and remote notifications; UI-only keeps the designed screen honest.

## Consequences

Permission + testing
