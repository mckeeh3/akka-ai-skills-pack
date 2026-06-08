# TASK-REQWS-02-001: Promote canonical process doctrine

## Objective

Create or promote canonical doctrine for the requirements-to-workstream process based on the WIP and target process contract.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `docs/workstream-dashboard-attention-event-backbone-wip.md`
- `specs/requirements-to-workstream-process-migration/target-process-contract.md`
- `specs/requirements-to-workstream-process-migration/sprints/02-doctrine-consolidation-sprint.md`
- `specs/requirements-to-workstream-process-migration/backlog/02-doctrine-consolidation-backlog.md`

## In scope

- Create a canonical doc, or rename/split/promote the WIP if that is cleaner.
- Update minimal references in `skills/README.md` if a new canonical doc is introduced.
- Preserve WIP provenance if the WIP remains.

## Out of scope

- Do not update every skill in this task.

## Expected outputs

- likely `docs/requirements-to-workstream-development-process.md` or equivalent
- possible small update to `docs/workstream-dashboard-attention-event-backbone-wip.md`
- updated `pending-tasks.md`

## Required checks

- `git diff --check`
- `rg -n "requirements-to-workstream|what needs my attention|dashboard|attention|AutonomousAgent" docs skills/README.md`

## Done criteria

- There is a canonical source for the process that later skills can reference.
- One focused commit is made.
