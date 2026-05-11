# TASK-08-002: Implement SupplyDecision event-sourced write model

## Purpose

Implement the audit-grade decision/recommendation write model for the supplies autopilot reference slice.

## Required reads

- `AGENTS.md`
- `docs/ai-first-saas-application-architecture.md`
- `specs/ai-first-skills-pack-migration/sprints/08-executable-ai-first-reference-slice-sprint.md`
- `specs/ai-first-skills-pack-migration/backlog/08-executable-ai-first-reference-slice-build-backlog.md`
- `specs/ai-first-skills-pack-migration/tasks/08-executable-ai-first-reference-slice/01-supply-domain-and-trace-vocabulary.md`
- supplies domain files from `TASK-08-001`

## Scope

- Add `SupplyDecisionEntity` using Event Sourced Entity semantics.
- Model commands/events/state for recommendation opened, approval required, approved, rejected, suppressed, shipment prepared, stale/escalated, outcome linked, and idempotent no-op.
- Validate authority-sensitive commands include actor, rationale, policy/evidence/trace refs where required.
- Add replay/unit tests for success, validation, stale decision, duplicate command no-op, and outcome linkage.

## Non-goals

- No workflow orchestration.
- No agent/tool implementation.
- No endpoints or UI.

## Skills

- `ai-first-saas`
- `ai-first-saas-decision-cards`
- `ai-first-saas-audit-trace`
- `akka-event-sourced-entities`
- `akka-ese-domain-modeling`
- `akka-ese-application-entity`
- `akka-ese-unit-testing`

## Expected outputs

- `SupplyDecisionEntity` and supporting state/event/command classes.
- Focused entity tests.

## Required checks

- Run focused `SupplyDecisionEntity` tests.
- Run compile for affected source/test packages.

## Done criteria

- Event history reconstructs decision state and trace/outcome refs.
- Policy/evidence/authority fields are mechanically validated.