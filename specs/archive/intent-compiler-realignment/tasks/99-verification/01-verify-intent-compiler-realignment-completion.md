# Task: Verify Intent Compiler Realignment Completion

## Objective

Verify whether the current task group and overall mini-project done state are complete. Append bounded follow-up tasks plus a new terminal verification task if material gaps remain.

## Required reads

- `AGENTS.md`
- `skills-pack/AGENTS.md`
- `specs/intent-compiler-realignment/README.md`
- `specs/intent-compiler-realignment/conversation-capture.md`
- `specs/intent-compiler-realignment/pending-tasks.md`
- `specs/intent-compiler-realignment/sprints/*.md`
- `specs/intent-compiler-realignment/backlog/*.md`
- `specs/intent-compiler-realignment/tasks/**/*.md`
- canonical intent compiler docs
- `specs/intent-compiler-realignment/intent-processing-inventory.md`

## In scope

- Compare completed work against README done state, sprint goals, backlog, conversation decisions, and task criteria.
- Run install/reference checks.
- Append follow-up tasks if gaps remain.

## Out of scope

- Whole-repository review beyond intent-compiler realignment scope.
- Implementing newly discovered follow-up work in the same task.

## Expected outputs

- updated `specs/intent-compiler-realignment/pending-tasks.md`
- verification notes or completion summary
- follow-up tasks if required

## Required checks

- `git diff --check`
- `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --dry-run`
- `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --check`

## Done criteria

- Current task group and mini-project done state are evaluated.
- If complete, completion is recorded with no new required work.
- If incomplete, new bounded tasks are appended before a new terminal verification task.
