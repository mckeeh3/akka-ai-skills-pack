# Task: Realign App-Description Capture Skills

## Objective

Update active app-description capture/review/generation skills to use the new app-description intent graph and workstream binding model.

## Required reads

- `AGENTS.md`
- `skills-pack/AGENTS.md`
- `specs/intent-compiler-realignment/README.md`
- `specs/intent-compiler-realignment/intent-processing-inventory.md`
- `specs/intent-compiler-realignment/sprints/03-skills-replacement-sprint.md`
- canonical intent compiler docs
- app-description skill files identified by inventory

## In scope

- capability, behavior, tests, auth/security, observability, UI, surface, functional-agent, readiness, change-impact, and generation app-description skills.
- References to global reusable definitions and workstream-specific bindings.

## Out of scope

- PRD/backlog queue skills.
- Runtime Akka implementation skill changes.

## Expected outputs

- updated/replacement app-description skills
- updated `pending-tasks.md`

## Required checks

- `git diff --check`
- `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --dry-run`

## Done criteria

- App-description skills know where to write current intent artifacts in the new structure.
- Workstream-local artifacts and global reusable definitions are clearly distinguished.
