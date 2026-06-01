# TASK-WEB3-99-001: Verify workstream event backbone v3 completion

## Objective

Verify that Workstream Event Backbone v3 reached its stated done state, or append bounded follow-up tasks plus a new terminal verification task.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/workstream-event-backbone-v3/README.md`
- `specs/workstream-event-backbone-v3/conversation-capture.md`
- `specs/workstream-event-backbone-v3/pending-tasks.md`
- all artifacts and task briefs under `specs/workstream-event-backbone-v3/`

## Required checks

- `git diff --check`
- targeted backend tests for event publication/consumer/projection/lifecycle behavior
- frontend tests/typecheck/build if update delivery changed frontend
- focused `rg` for event envelope, consumers, idempotency, source refs, lifecycle events, and future AutonomousAgent handoff

## Done criteria

- Mini-project done state is assessed.
- If complete, v3 completion is recorded.
- If incomplete, bounded follow-up tasks and a new terminal verification task are appended.
- Task changes and queue update are committed.

## Commit message

`event-backbone: verify v3 completion`
