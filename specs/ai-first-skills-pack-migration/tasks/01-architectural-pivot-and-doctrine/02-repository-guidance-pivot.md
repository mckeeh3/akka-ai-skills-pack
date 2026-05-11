# Task Brief: Repository Guidance Pivot

## Task ID

`TASK-01-002`

## Objective

Update repository-level guidance so future agents understand this repository is evolving into an AI-first SaaS Akka skills pack.

## Required reads

- `AGENTS.md`
- `README.md`
- `docs/ai-first-saas-application-architecture.md`
- `skills/inbox/docs/ai-first-saas-coding-agent-framework.md` for provenance only
- `specs/ai-first-skills-pack-migration/sprints/01-architectural-pivot-and-doctrine-sprint.md`
- `specs/ai-first-skills-pack-migration/backlog/01-architectural-pivot-and-doctrine-build-backlog.md`

## Dependencies

- `TASK-01-001` complete.

## Scope

Update repository-level docs, likely:

- `AGENTS.md`
- possibly `README.md`

The update should say:

- The pack's target generated application architecture is now AI-first SaaS by default.
- Existing Akka component skills remain the implementation substrate.
- High-level user input should be interpreted through agentic operating model concepts before CRUD/component decomposition.
- `skills/inbox/` content is temporary reference material until promoted/merged/archived/deleted by explicit tasks.

## Non-goals

- Do not create new skills.
- Do not refactor app-description or PRD planning skills yet.
- Do not rewrite large sections unrelated to repository orientation.
- Do not delete inbox files.

## Expected outputs

- Updated `AGENTS.md`
- Optional updated `README.md` if needed to avoid contradiction with the new default architecture

## Required checks

- Ensure repository guidance still distinguishes source-pack development from using the installed pack.
- Ensure wording does not imply every app must use every AI-first pattern; scope should be default target architecture, not forced overengineering.

## Done criteria

- A future session-start read of `AGENTS.md` makes the AI-first pivot clear.
- Existing app-description and 3-stage Akka model guidance remains usable but is reframed as subordinate to AI-first intent interpretation for high-level product input.
- `pending-tasks.md` is updated to mark `TASK-01-002` done after completion.
