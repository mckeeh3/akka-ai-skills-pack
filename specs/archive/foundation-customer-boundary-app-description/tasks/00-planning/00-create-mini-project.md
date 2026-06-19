# TASK-FCBAD-00-001: Create foundation customer boundary app-description mini-project

## Objective

Create the mini-project scaffold and pending-task queue for capturing the foundation customer boundary in active `app-description/` current intent.

## Required reads

- `AGENTS.md`
- `.agents/skills/project-discussed-idea-to-pending-project/SKILL.md`
- `.agents/skills/docs/current-intent-model.md`
- `.agents/skills/docs/intent-to-realization-flow.md`
- `.agents/skills/docs/intent-compiler-skill-contracts.md`
- current conversation context

## Expected outputs

- `specs/foundation-customer-boundary-app-description/README.md`
- `specs/foundation-customer-boundary-app-description/conversation-capture.md`
- `specs/foundation-customer-boundary-app-description/sprints/01-foundation-customer-boundary-description-sprint.md`
- `specs/foundation-customer-boundary-app-description/backlog/01-foundation-customer-boundary-app-description-backlog.md`
- `specs/foundation-customer-boundary-app-description/tasks/**`
- `specs/foundation-customer-boundary-app-description/pending-tasks.md`

## Required checks

- `git diff --check`

## Done criteria

- The queue has a first runnable app-description task.
- The queue includes a terminal verification task that can append follow-up tasks and a replacement terminal verification task if ambiguity remains.
- Planning scaffold and queue are committed together.
