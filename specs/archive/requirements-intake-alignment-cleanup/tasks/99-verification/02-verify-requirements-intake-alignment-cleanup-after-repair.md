# TASK-RIAC-99-002: Verify requirements intake alignment cleanup after final repair

## Objective

Rerun terminal verification after `TASK-RIAC-06-001` and close the mini-project if no material active-guidance drift remains. Append another bounded repair task plus a new terminal verification task if material gaps remain.

## Required reads

- AGENTS.md
- skills/README.md
- specs/requirements-intake-alignment-cleanup/README.md
- specs/requirements-intake-alignment-cleanup/conversation-capture.md
- specs/requirements-intake-alignment-cleanup/pending-tasks.md
- specs/requirements-intake-alignment-cleanup/content-inventory.md
- specs/requirements-intake-alignment-cleanup/prune-and-rewrite-criteria.md
- specs/requirements-intake-alignment-cleanup/final-verification.md
- specs/requirements-intake-alignment-cleanup/tasks/06-final-repair/01-repair-final-active-guidance-drift.md
- docs/agent-workstream-design-review-checklist.md

## In scope

- Verify the final active-guidance drift findings were repaired.
- Rerun stale-term searches from `prune-and-rewrite-criteria.md` as needed.
- Record completion or append another bounded task group if material gaps remain.

## Out of scope

- Do not expand into unrelated whole-repo refactors.

## Checks

- `git diff --check`
- Stale-term searches recorded in a verification artifact or queue notes.
- Reference checks for removed files when applicable.

## Done criteria

- Current repair task and overall mini-project done state are assessed.
- If complete, completion is recorded with no new required tasks.
- If incomplete, new bounded tasks are appended before a new terminal verification task.
- Queue updated and committed.
