# LittleStory prompt library

Reusable prompts for Claude Code and GitHub Copilot Chat. **`00_MASTER_ORCHESTRATOR_PROMPT.md` governs every other prompt**; if a prompt here, a ticket prompt or an agent instruction file disagrees with it, the master and the owner's explicit instructions win. Fix the contradiction in the same PR that finds it.

## Index

| Prompt | Purpose | Phase |
| --- | --- | --- |
| [00_MASTER_ORCHESTRATOR_PROMPT.md](00_MASTER_ORCHESTRATOR_PROMPT.md) | Governing brief: roles, approvals A–F, scope, localization, no-hardcoding, workflow | All |
| [01_REPOSITORY_DISCOVERY_PROMPT.md](01_REPOSITORY_DISCOVERY_PROMPT.md) | Read-only repository, toolchain and CI audit | P0 / any time |
| [02_FIGMA_AUDIT_PROMPT.md](02_FIGMA_AUDIT_PROMPT.md) | Figma / design-pack audit | P0, before each UI ticket |
| [03_REQUIREMENTS_AND_EDGE_CASES_PROMPT.md](03_REQUIREMENTS_AND_EDGE_CASES_PROMPT.md) | Requirements analysis and edge cases | P1 |
| [04_ARCHITECTURE_DESIGN_PROMPT.md](04_ARCHITECTURE_DESIGN_PROMPT.md) | Architecture design, ADRs and architecture review | P1, any review |
| [05_JIRA_BACKLOG_GENERATION_PROMPT.md](05_JIRA_BACKLOG_GENERATION_PROMPT.md) | Backlog generation and update | P1, after each merge |
| [06_PHASE_EXECUTION_PROMPT.md](06_PHASE_EXECUTION_PROMPT.md) | Phase execution and agent dispatch | P2–P8 |
| [07_TICKET_IMPLEMENTATION_TEMPLATE.md](07_TICKET_IMPLEMENTATION_TEMPLATE.md) | Ticket implementation | P2–P8 |
| [08_COMPOSE_UI_IMPLEMENTATION_PROMPT.md](08_COMPOSE_UI_IMPLEMENTATION_PROMPT.md) | Compose UI implementation | P2–P6 |
| [09_VISUAL_REGRESSION_PROMPT.md](09_VISUAL_REGRESSION_PROMPT.md) | Visual regression | P2–P7 |
| [10_INTEGRATION_TESTING_PROMPT.md](10_INTEGRATION_TESTING_PROMPT.md) | Integration testing | P5–P7 |
| [11_ACCESSIBILITY_REVIEW_PROMPT.md](11_ACCESSIBILITY_REVIEW_PROMPT.md) | Accessibility review | P2–P7 |
| [12_SECURITY_REVIEW_PROMPT.md](12_SECURITY_REVIEW_PROMPT.md) | Security and privacy review | P7, any PR touching data |
| [13_CODE_REVIEW_PROMPT.md](13_CODE_REVIEW_PROMPT.md) | PR / code review | All |
| [14_CI_DIAGNOSTICS_PROMPT.md](14_CI_DIAGNOSTICS_PROMPT.md) | CI repair | All |
| [15_PR_PREPARATION_PROMPT.md](15_PR_PREPARATION_PROMPT.md) | PR preparation | All (needs Approval D) |
| [16_POST_MERGE_VERIFICATION_PROMPT.md](16_POST_MERGE_VERIFICATION_PROMPT.md) | Post-merge verification | All |
| [17_RELEASE_READINESS_PROMPT.md](17_RELEASE_READINESS_PROMPT.md) | Release readiness | P8 (Approval F gate) |
| [18_BACKEND_INTEGRATION_PLANNING_PROMPT.md](18_BACKEND_INTEGRATION_PLANNING_PROMPT.md) | Backend planning | P8 |
| [19_LOCALIZATION_REVIEW_PROMPT.md](19_LOCALIZATION_REVIEW_PROMPT.md) | Localization review | P2–P7 |
| [20_ADD_NEW_LANGUAGE_PROMPT.md](20_ADD_NEW_LANGUAGE_PROMPT.md) | Add a new language | Future |
| [21_MAINTAINABILITY_AND_HARDCODING_REVIEW_PROMPT.md](21_MAINTAINABILITY_AND_HARDCODING_REVIEW_PROMPT.md) | Maintainability / hardcoding review | P2–P7 |
| [tickets/](tickets/) | One filled implementation prompt per ticket (`LS-###.md`), generated from the backlog workbook | P2 onwards |

## How to use

**Claude Code (CLI, IDE or cloud):** start the session in the repository root so `CLAUDE.md` loads, then paste or reference the prompt, for example: `Follow prompts/07_TICKET_IMPLEMENTATION_TEMPLATE.md for prompts/tickets/LS-030.md`. Grant approvals explicitly in the message ("Approval C for LS-030", "Approval D: push and open the PR").

**GitHub Copilot Chat:** `.github/copilot-instructions.md` carries the same rules. Attach the prompt file (`#file:prompts/13_CODE_REVIEW_PROMPT.md`) and the files under review.

## Rules for every prompt

- Approval levels: A Discovery · B Documentation · C Implementation · D Push/PR · E Merge (owner only) · F Release (owner only). One approval never implies the next.
- No hardcoded user-facing text, colours, dimensions, text styles, routes, limits, URLs or configuration in feature code.
- Every string in English (`values/`) and Kannada (`values-kn/`); Kannada stays "Needs native review" until a fluent reviewer approves it.
- Report only results that actually ran. The cloud workspace cannot build Android until `dl.google.com` is allowed (risk R-21); use CI or the owner's machine for build evidence.

## Maintaining the library

- Ticket prompts are regenerated from the Backlog sheet when tickets change; hand edits to a ticket prompt must also be made in the workbook.
- When `CLAUDE.md` or `.github/copilot-instructions.md` changes, check these prompts for contradictions in the same PR.
