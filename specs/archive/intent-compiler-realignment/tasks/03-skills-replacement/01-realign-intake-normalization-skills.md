# Task: Realign Intake and Normalization Skills

## Objective

Update active intake/router/normalization skills to treat user input as incremental intent compiled into the current canonical intent graph.

## Required reads

- `AGENTS.md`
- `skills-pack/AGENTS.md`
- `specs/intent-compiler-realignment/README.md`
- `specs/intent-compiler-realignment/intent-processing-inventory.md`
- `specs/intent-compiler-realignment/sprints/03-skills-replacement-sprint.md`
- canonical intent compiler docs
- relevant current skill files from the inventory, especially intake/router/normalization skills

## In scope

- `app-description-intake-router`
- `app-description-input-normalization`
- `app-descriptions`
- direct references to old doctrine in those skills

## Out of scope

- Planning queue skills.
- Focused Akka implementation skills.

## Expected outputs

- updated or replacement intake/normalization skills
- updated `pending-tasks.md`

## Required checks

- `git diff --check`
- `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --dry-run`

## Done criteria

- Skills classify, normalize, and route incremental intent into app/domain/workstream/global artifact updates.
- Skills explicitly avoid historical clutter in current intent files.
