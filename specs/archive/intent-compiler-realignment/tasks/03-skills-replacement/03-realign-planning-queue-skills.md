# Task: Realign Planning and Queue Skills

## Objective

Update PRD/spec/backlog/change/pending-question/pending-task skills so they compile current intent into executable realization plans without reviving legacy structure.

## Required reads

- `AGENTS.md`
- `skills-pack/AGENTS.md`
- `specs/intent-compiler-realignment/README.md`
- `specs/intent-compiler-realignment/intent-processing-inventory.md`
- `specs/intent-compiler-realignment/sprints/03-skills-replacement-sprint.md`
- canonical intent compiler docs
- planning/queue skill files identified by inventory

## In scope

- PRD to specs/backlog
- revised PRD reconciliation
- change request to spec update
- slice/backlog/task-brief/pending-task skills
- pending question skills
- project-discussed-idea-to-pending-project if needed

## Out of scope

- Focused backend/frontend implementation skills.

## Expected outputs

- updated/replacement planning and queue skills
- updated `pending-tasks.md`

## Required checks

- `git diff --check`
- `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --dry-run`

## Done criteria

- Planning skills preserve traceability from current intent graph to specs/backlogs/tasks.
- Pending questions/tasks use current intent/workstream binding vocabulary.
