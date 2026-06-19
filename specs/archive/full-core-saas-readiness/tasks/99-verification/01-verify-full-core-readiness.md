# TASK-FCSR-99-001: Verify full-core readiness mini-project

## Objective

Verify whether the full-core readiness task group and overall done state are complete. If material gaps remain, append bounded follow-up tasks and a new terminal verification task.

## Required reads

- `AGENTS.md`
- `specs/full-core-saas-readiness/README.md`
- `specs/full-core-saas-readiness/conversation-capture.md`
- `specs/full-core-saas-readiness/pending-tasks.md`
- all sprints/backlog/task briefs under this mini-project
- `specs/full-core-saas-readiness/full-core-readiness-gap-contract.md`
- runtime smoke/validation artifacts from completed tasks
- changed app-description/backend/frontend/test files from completed tasks

## Skills

- none; repository verification task

## In scope

- Compare completed work against README done state, gap contract, sprint goals, backlog, and task criteria.
- Validate app-description readiness docs match evidence.
- Run or require appropriate checks for the final state.
- Append bounded follow-up tasks and a new terminal verification task if incomplete.

## Out of scope

- Whole-repository review unrelated to full-core readiness.
- Implementing large missing features during verification.

## Expected outputs

- Updated `pending-tasks.md`.
- Optional `full-core-readiness-verification.md`.
- New bounded tasks if gaps remain.

## Required checks

- `git diff --check`
- targeted or full backend/frontend checks needed to validate final claimed state
- focused `rg` evidence for readiness docs and gap contract closure/blocker statuses

## Done criteria

- Task group and overall mini-project goals are compared against completed work.
- Unresolved questions/blockers are reviewed.
- If complete, completion is recorded with no new required work.
- If incomplete, new bounded tasks are appended before a new terminal verification task.
- Changes and queue update are committed.

## Commit message

`full-core-ready: verify readiness`
