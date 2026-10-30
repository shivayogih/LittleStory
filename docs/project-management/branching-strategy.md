# Branching strategy

Purpose: one place that says which branch is for what, where PRs go and who merges.
Status: owner decision, 2026-10-30. It extends brief §15.1 (`main`, `development`, `feature/*`, `fix/*`) with `staging`, `hotfix/*` and `milestone/*` at the owner's request. The brief itself is kept verbatim.

## Long-lived branches

| Branch | Purpose | Receives merges from | Default? |
|---|---|---|---|
| `main` | Stable, release-ready code. Tagged `vX.Y.Z` per release | `staging`, `hotfix/*` | No |
| `staging` | Release candidate: what the owner and QA test before a release | `development`, `hotfix/*` (back-merge) | No |
| `development` | Integration branch. Every ordinary PR targets it | short-lived branches below, `hotfix/*` (back-merge) | **Yes** |

## Short-lived branches

| Branch | Cut from | PR into | Use |
|---|---|---|---|
| `feature/LS-###-slug` | `development` | `development` | One ticket |
| `fix/LS-###-slug` | `development` | `development` | One bug-fix ticket |
| `milestone/mN-slug` | `development` | `development` | One PR that bundles several tickets (keep one commit per ticket) |
| `docs/slug`, `chore/slug` | `development` | `development` | Documentation, tooling, config with no ticket |
| `hotfix/slug` | `main` | `main`, then back-merge into `staging` and `development` | Urgent fix to released code |

Never use tool-generated names such as `claude/…`. Never push directly to `main`, `staging` or `development`.

## Flow

1. Work on a short-lived branch; open a PR into `development`. CI `verify` must be green. The owner reviews and merges (merge commit, so one-commit-per-ticket history stays).
2. When a milestone is ready for acceptance testing, open a promotion PR `development` → `staging`. The owner merges it and tests.
3. After sign-off, open a promotion PR `staging` → `main`, and tag the release. Release, store submission and deployment stay the owner's decision (Approval F).
4. Hotfix: branch from `main`, PR into `main`, then merge `main` back into `staging` and `development` so the fix is not lost.

Agents open PRs; **only the owner merges** (Approval E), including promotion PRs.

## CI

`.github/workflows/ci.yml` runs `./gradlew check assembleDebug` on every pull request and on pushes to `main`, `staging` and `development`. It uses a read-only token, has no secrets and deploys nothing.

## Settings only the owner can apply (GitHub → Settings)

- Branches → default branch: `development`.
- Branch protection (rulesets) on `main`, `staging` and `development`: require a pull request; require the `verify` status check to pass; block force-push and deletion. Do not require an approval count while the owner is the only reviewer (GitHub does not let an author approve their own PR); raise it when a second reviewer joins.
- Optional: delete head branches automatically after merge; delete the old `claude/*` branches once their PRs are merged.

## Open items

- `development` and `staging` start at the empty initial commit of `main`. The first PR into `development` (milestone m3) brings the whole project in; the first promotion to `staging` and `main` happens when the owner decides.
