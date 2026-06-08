# TASK-REQWS-XX-099: Progressive sprint verification

## Objective

Verify one completed sprint/task group before downstream work depends on it. Root out anything that is not consistently prescriptive, append bounded follow-up tasks when needed, and keep the mini-project moving through a repeatable task-group loop.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/requirements-to-workstream-process-migration/README.md`
- `specs/requirements-to-workstream-process-migration/conversation-capture.md`
- `specs/requirements-to-workstream-process-migration/pending-tasks.md`
- the selected sprint file
- the selected backlog file
- all completed task outputs for the sprint being verified
- canonical process doc once it exists

## In scope

- Compare completed sprint outputs against the sprint objective, backlog acceptance criteria, task done criteria, conversation decisions, and mini-project done state.
- Search for non-prescriptive language such as optional-only mentions, stale CRUD/page/component-first routing, event-only framing, missing attention/dashboard/surface/capability/autonomous-task fields, or weak queue checks.
- Update `pending-tasks.md` with verification notes.
- Append bounded follow-up tasks before a new sprint verification task if material gaps remain.

## Out of scope

- Do not perform the follow-up implementation inside the verification task unless it is a tiny typo/link fix.
- Do not expand scope into an unrelated whole-repository review.

## Required checks

- `git diff --check`
- The sprint-specific `rg` checks listed in `pending-tasks.md`

## Done criteria

- The sprint/task group is either verified complete or new bounded follow-up tasks plus a replacement verification task are appended.
- Any unresolved blockers are recorded.
- One focused commit is made for verification outputs and queue updates.
