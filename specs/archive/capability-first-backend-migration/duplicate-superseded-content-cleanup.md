# Duplicate and Superseded Content Cleanup

Task: `TASK-05-005`

## Scope

Reviewed capability-first migration artifacts for duplicate, obsolete, or superseded content that should be deleted, archived, or explicitly retained before Sprint 6 final consistency review.

Reviewed files:

- `specs/capability-first-backend-migration/README.md`
- `specs/capability-first-backend-migration/pending-tasks.md`
- `specs/capability-first-backend-migration/backlog/*.md`
- `specs/capability-first-backend-migration/sprints/*.md`
- `specs/capability-first-backend-migration/*-review.md`
- `specs/capability-first-backend-migration/tasks/README.md`
- capability-first doctrine and skill references from `docs/` and `skills/`

## Findings

### No deletion or archive performed

No capability-first migration artifact was safe to delete or archive in this task.

Rationale:

- Sprint files and backlog files are planning provenance for already-completed and remaining queue tasks.
- Review files from `TASK-05-001` through `TASK-05-004` are task outputs, not duplicate doctrine. They intentionally summarize narrower review scopes and should remain as Sprint 5 evidence until final migration summary work.
- `tasks/README.md` is a placeholder/index for optional future task briefs and is referenced by the migration README read order and by repository task-brief conventions.
- The canonical reusable guidance now lives in `docs/capability-first-backend-architecture.md`, `skills/capability-first-backend/SKILL.md`, and updated focused skills; migration-local files are not routed as installed-pack guidance.

### Supersession status

The migration-local review reports are historical task records. They do not supersede canonical docs or skills and are not themselves canonical routing surfaces.

No pending queue item was found to duplicate another pending item:

- `TASK-06-001` remains the whole-pack consistency review.
- `TASK-06-002` remains the security/governance consistency review.
- `TASK-06-003` remains the example/test coverage review.
- `TASK-06-004` remains the migration completion summary.

## Checks performed

- Listed tracked files under `specs/capability-first-backend-migration/` and confirmed each current artifact has a distinct planning, sprint, backlog, review, queue, or task-brief-index role.
- Searched migration artifacts and repository guidance for duplicate/superseded/planned/future markers relevant to capability-first migration cleanup.
- Searched references to `specs/capability-first-backend-migration/tasks/README.md` and `tasks/` before deciding not to remove the task-brief index.
- Did not delete any referenced file.

## Residual work

No new duplicate-content cleanup task is required. Sprint 6 should proceed with final whole-pack consistency, security/governance, example/test coverage, and completion-summary tasks already queued.
