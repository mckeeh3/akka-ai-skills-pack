# Task Brief: Create Package Partition Queue

## Objective

Create the mini-project planning scaffold for the Java foundation/coreapp/business package refactor.

## Required reads

- `AGENTS.md`
- `skills-pack/skills/README.md`
- `skills-pack/docs/pending-task-queue.md`
- `skills-pack/docs/pending-question-queue.md`
- conversation context agreeing to `foundation`, `coreapp`, and `business`

## In scope

- Create README, conversation capture, sprints, backlog, task briefs, and pending queue.

## Out of scope

- Moving Java source files.
- Updating imports or docs outside this mini-project.

## Expected outputs

- `specs/java-foundation-coreapp-business-partition/**`

## Required checks

- `git diff --check`
- `find specs/java-foundation-coreapp-business-partition -maxdepth 3 -type f -print | sort`

## Done criteria

- Planning artifacts exist and are internally consistent.
- Queue contains bounded tasks and a terminal verification task.
- Changes are committed with message `packages: add foundation coreapp business queue` before marking done.
