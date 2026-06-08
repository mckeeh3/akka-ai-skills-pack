# TASK-AAI-04-001: Run AutonomousAgent runtime validation

## Objective

Validate the access-review AutonomousAgent runtime path in a fresh scaffold and record release-readiness evidence or blockers.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- mini-project README/conversation/sprint/backlog/queue entry and this task brief
- contract from TASK-AAI-01-001
- implementation notes from TASK-AAI-02-001 and TASK-AAI-03-001

## In scope

- Scaffold a fresh starter.
- Run backend tests for AutonomousAgent runtime/fail-closed/event/attention behavior.
- Run frontend tests/typecheck/build.
- If feasible, run local smoke path for start/query/task surface behavior.
- Record evidence and blockers.

## Required checks

- `git diff --check`
- scaffolded backend Maven tests
- frontend tests/typecheck/build
- manual/local smoke notes or clear blocked reason

## Done criteria

- Runtime validation evidence is captured.
- Any blockers are converted to bounded tasks or recorded as release blockers.
- Task changes and queue update are committed.

## Commit message

`autonomous-agent: validate runtime path`
