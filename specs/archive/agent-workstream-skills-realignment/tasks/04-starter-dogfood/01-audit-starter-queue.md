# TASK-AWSR-04-001: Audit starter queue against realigned workstream model

## Goal

Audit current `ai-first-saas-starter` pending tasks and identify which should be preserved, rewritten, or superseded under the workstream/surface/capability model.

## Required reads

- Sprint 03 review output
- `specs/ai-first-saas-starter-app-template/pending-tasks.md`
- `specs/ai-first-saas-starter-app-template/sprints/07-fullstack-gap-closure-sprint.md`
- `specs/ai-first-saas-starter-app-template/backlog/07-fullstack-gap-closure-build-backlog.md`
- task briefs under `specs/ai-first-saas-starter-app-template/tasks/07-fullstack-gap-closure/`
- `docs/agent-workstream-design-review-checklist.md`

## Work

1. Create `specs/agent-workstream-skills-realignment/starter-queue-gap-matrix.md`.
2. Map current starter tasks to functional agents, surfaces/actions, capabilities, and Akka/frontend/test work.
3. Mark vague/component-slice tasks for supersession.
4. Update this task entry in `pending-tasks.md` before committing.

## Required checks

- `git diff --check`

## Done criteria

- Starter queue gap matrix exists and identifies supersession targets.
- Queue status is updated.
- One git commit is created.

## Suggested commit message

`Audit starter queue workstream alignment`
