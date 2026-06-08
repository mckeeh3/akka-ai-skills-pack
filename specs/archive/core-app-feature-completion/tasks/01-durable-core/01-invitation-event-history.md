# Task Brief: Invitation Event-Sourced Lifecycle History

## Objective

Add audit-grade invitation lifecycle history to the starter while preserving the existing durable invitation repository/API/UI behavior.

## Required reads

- `specs/core-app-feature-completion/README.md`
- `specs/core-app-feature-completion/sprints/01-durable-core-sprint.md`
- `specs/core-app-feature-completion/backlog/01-core-feature-completion-build-backlog.md`
- `templates/ai-first-saas-starter/README.md`
- `specs/core-app-full-stack-readiness/full-core-realization-map.md`
- `specs/core-app-full-stack-readiness/full-core-acceptance-test-matrix.md`
- `skills/akka-event-sourced-entities/SKILL.md`
- `skills/akka-views/SKILL.md`
- `skills/akka-saas-invitation-onboarding/SKILL.md`

## In scope

- Invitation lifecycle event facts for create, resend, delivery queued/sent/failed, revoke, expire, accept, stale/no-op, and denial states.
- Idempotency/source-event handling and safe raw-token redaction.
- Projection/update path to existing invitation dashboards/surfaces.
- Focused backend tests plus fullstack validation when source changes affect scaffold output.

## Out of scope

- New production email provider beyond existing Resend boundary.
- New invitation UI redesign unrelated to event history/projection evidence.

## Checks

- `git diff --check`
- focused rendered-scaffold Maven tests for invitation history and existing invitation service tests
- `tools/validate-ai-first-saas-starter-fullstack.sh`

## Done criteria

- Rendered local Akka tests prove invitation history persistence/replay/idempotency and no raw-token leakage.
- Existing invitation runtime/API/UI behavior remains valid.
