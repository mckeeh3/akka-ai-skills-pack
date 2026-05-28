# TASK-REQWS-99-001: Verify requirements-to-workstream process migration

## Objective

Verify the skills pack's input processing path has been realigned. Append follow-up tasks if gaps remain.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/requirements-to-workstream-process-migration/README.md`
- `specs/requirements-to-workstream-process-migration/conversation-capture.md`
- `specs/requirements-to-workstream-process-migration/pending-tasks.md`
- all sprint/backlog/task files in this mini-project
- canonical process doc from `TASK-REQWS-02-001`

## In scope

- Review top-level routing, input normalization, description-first, direct PRD/spec planning, queue docs, examples, and packaging.
- Run grep checks for stale process drift.
- Update pending queue with completion notes or append follow-up tasks plus another terminal verification task.

## Out of scope

- Do not perform broad implementation fixes in the verification task unless they are tiny documentation typo/link fixes.

## Expected outputs

- updated `pending-tasks.md`
- optional verification notes under this mini-project
- optional appended follow-up tasks

## Required checks

- `git diff --check`
- `rg -n "CRUD-first|page-first|component-first|chatbot-bolt-on|generic Akka|requirements-to-workstream|what needs my attention|AutonomousAgent" AGENTS.md pack/AGENTS.md skills docs pack/manifest.yaml`

## Done criteria

- Verification either confirms completion or appends specific follow-up tasks before a new verification task.
- One focused commit is made.
