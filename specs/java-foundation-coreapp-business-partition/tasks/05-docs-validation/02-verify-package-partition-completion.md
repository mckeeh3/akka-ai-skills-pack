# Task Brief: Verify Package Partition Completion

## Objective

Run terminal verification for the package partition mini-project and append follow-up tasks if material gaps remain.

## Required reads

- `AGENTS.md`
- `skills-pack/skills/README.md`
- `specs/java-foundation-coreapp-business-partition/README.md`
- `specs/java-foundation-coreapp-business-partition/conversation-capture.md`
- `specs/java-foundation-coreapp-business-partition/pending-tasks.md`
- all sprint, backlog, and task briefs in this mini-project
- `classification-and-package-map.md`

## In scope

- Compare completed work against README done state.
- Run root Java validation.
- Run docs/stale-reference checks.
- Run skills-pack checks if skills-pack files changed in this mini-project.
- Append new bounded tasks before a new terminal verification task if gaps remain.

## Out of scope

- Whole-repository review unrelated to package partitioning.
- Implementing newly discovered material gaps in the same verification task.

## Expected outputs

- Updated `pending-tasks.md` with completion notes or follow-up tasks.
- Optional verification summary file.

## Required checks

- `git diff --check`
- `mvn test`
- stale old-package search proof
- dependency-boundary check/search if added
- frontend/skills-pack checks when touched by package refs

## Done criteria

- Current task group and overall mini-project done state are checked.
- If complete, completion is recorded with no new required work.
- If incomplete, new bounded tasks are appended before a new terminal verification task.
