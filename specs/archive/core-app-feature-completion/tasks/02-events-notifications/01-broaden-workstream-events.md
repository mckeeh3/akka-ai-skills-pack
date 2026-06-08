# Task Brief: Broaden Workstream Event Coverage

## Objective

Expand governed workstream event coverage beyond the current bounded v3 starter event families.

## Required reads

- `specs/core-app-feature-completion/README.md`
- `specs/core-app-feature-completion/sprints/02-events-notifications-sprint.md`
- `templates/ai-first-saas-starter/README.md`
- `docs/agent-workstream-application-architecture.md`
- `docs/capability-first-backend-architecture.md`
- `skills/akka-consumers/SKILL.md`
- `skills/akka-views/SKILL.md`

## In scope

- Event envelope types for additional invitation, membership/role, support-access, governed artifact lifecycle, policy simulation, export, and notification lifecycle events.
- Idempotent event-to-attention/projection-refresh handling.
- Safe redaction and tenant/customer scoping.
- Tests for duplicate/replayed/malformed/cross-tenant events.

## Checks

- `git diff --check`
- focused backend event/consumer/projection tests
- `tools/validate-ai-first-saas-starter-fullstack.sh`

## Done criteria

- Additional event families feed backend projections/attention safely and do not create frontend-only or unauthorized attention state.
