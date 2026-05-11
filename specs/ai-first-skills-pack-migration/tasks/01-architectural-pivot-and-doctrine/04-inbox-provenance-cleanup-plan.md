# Task Brief: Inbox Provenance and Cleanup Plan

## Task ID

`TASK-01-004`

## Objective

Document the provenance and intended disposition of temporary AI-first concept-development files under `specs/ai-first-skills-pack-migration/archive/inbox/docs/`.

## Required reads

- `AGENTS.md`
- `docs/ai-first-saas-application-architecture.md`
- all files under `specs/ai-first-skills-pack-migration/archive/inbox/docs/*.md`
- `specs/ai-first-skills-pack-migration/sprints/01-architectural-pivot-and-doctrine-sprint.md`
- `specs/ai-first-skills-pack-migration/backlog/01-architectural-pivot-and-doctrine-build-backlog.md`

## Dependencies

- `TASK-01-001` complete.
- Prefer `TASK-01-002` and `TASK-01-003` complete first.

## Scope

Create a migration/provenance document, suggested path:

```text
specs/ai-first-skills-pack-migration/inbox-provenance-and-disposition.md
```

For each inbox file, record:

- file path
- purpose/source role
- whether it has been promoted, partially mined, deferred, intended for worked example, or intended for archive/removal
- target canonical destination if known
- notes for future cleanup task

## Non-goals

- Do not delete, move, or archive inbox files yet.
- Do not create the DCA worked example yet.
- Do not perform broad doc rewrites beyond this provenance plan.

## Expected outputs

- `specs/ai-first-skills-pack-migration/inbox-provenance-and-disposition.md`

## Required checks

- Confirm all current `specs/ai-first-skills-pack-migration/archive/inbox/docs/*.md` files are listed.
- Confirm the plan does not designate two different canonical docs for the same concept without explaining why.

## Done criteria

- A git commit is created for this task before marking it `done`; include the commit hash in `pending-tasks.md` notes when possible.
- Inbox files are no longer ambiguous to future agents.
- Sprint 6 has enough guidance to clean up temporary material intentionally.
- `pending-tasks.md` is updated to mark `TASK-01-004` done after completion.
