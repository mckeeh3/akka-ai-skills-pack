# Task: Verify Audit Trace Workstream v0 completion after validation repair

## Objective

Re-run terminal verification for the `Audit Trace Workstream v0` mini-project after the static secret-marker validation repair task.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/five-core-workstreams-v0-plan/shared-five-core-v0-contract.md`
- `specs/five-core-workstreams-v0-plan/workstream-dependency-map.md`
- `specs/audit-trace-workstream-v0/README.md`
- `specs/audit-trace-workstream-v0/conversation-capture.md`
- `specs/audit-trace-workstream-v0/pending-tasks.md`
- `specs/audit-trace-workstream-v0/sprints/01-audit-trace-workstream-v0-sprint.md`
- `specs/audit-trace-workstream-v0/backlog/01-audit-trace-workstream-v0-build-backlog.md`
- `specs/audit-trace-workstream-v0/tasks/99-verification/02-verify-audit-trace-workstream-v0.md`
- `specs/audit-trace-workstream-v0/workstream-contract.md`
- `specs/audit-trace-workstream-v0/capability-inventory.md`

## Skills

- none; repository verification task

## In scope

- Compare completed work and validation-repair evidence against the mini-project done state.
- Confirm no material queue gaps remain, or append bounded follow-up tasks plus a new terminal verification task.
- Record final validation evidence in `pending-tasks.md`.

## Required checks

- `tools/validate-ai-first-saas-starter-fullstack.sh`
- `git diff --check`

## Done criteria

- Task group goals and mini-project done state are compared against completed work.
- Runtime/API/UI validation evidence or blockers are recorded.
- If complete, completion is recorded with no new required work.
- If incomplete, new bounded tasks are appended before a new terminal verification task.
- Task changes and queue update are committed.
