# TASK-AAI-99-001: Verify AutonomousAgent runtime integration

## Objective

Verify that the AutonomousAgent runtime integration mini-project reached its done state, or append bounded follow-up tasks plus a new terminal verification task.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/autonomous-agent-runtime-integration/README.md`
- `specs/autonomous-agent-runtime-integration/conversation-capture.md`
- `specs/autonomous-agent-runtime-integration/pending-tasks.md`
- all artifacts and task briefs under `specs/autonomous-agent-runtime-integration/`

## Required checks

- `git diff --check`
- targeted backend tests for AutonomousAgent runtime/fail-closed/events/attention
- frontend tests/typecheck/build if surfaces changed
- focused `rg` for AutonomousAgent, fail-closed, no fake success, v3 events, attention, and surfaces

## Done criteria

- Mini-project done state is assessed.
- If complete, completion is recorded.
- If incomplete, bounded follow-up tasks and a new terminal verification task are appended.
- Task changes and queue update are committed.

## Commit message

`autonomous-agent: verify runtime integration`
