# TASK-WCC-00-001: Create workstream contract cleanup planning scaffold

## Objective

Create a self-contained `specs/workstream-contract-cleanup/` mini-project capturing the review findings, user decisions, task queue, backlog, sprint, and fresh-session task briefs.

## Required reads

- `AGENTS.md`
- `skills-pack/AGENTS.md`
- `/home/hxmc/ai/akka-ai-skills-pack/.agents/skills/project-discussed-idea-to-pending-project/SKILL.md`
- source conversation summarized in `conversation-capture.md`

## In scope

- Planning files under `specs/workstream-contract-cleanup/**`.
- Queue entries for follow-up implementation and verification.

## Out of scope

- Implementing the schema/docs/tooling cleanup itself.
- Editing `skills-pack/docs/**`, `skills-pack/tools/**`, or `skills-pack/templates/**` for the cleanup.

## Expected outputs

- `specs/workstream-contract-cleanup/README.md`
- `specs/workstream-contract-cleanup/conversation-capture.md`
- `specs/workstream-contract-cleanup/pending-tasks.md`
- `specs/workstream-contract-cleanup/sprints/01-workstream-contract-cleanup-sprint.md`
- `specs/workstream-contract-cleanup/backlog/01-workstream-contract-cleanup-build-backlog.md`
- task briefs under `specs/workstream-contract-cleanup/tasks/**`

## Required checks

```bash
git diff --check
```

## Done criteria

- The mini-project captures the accepted decisions and non-goals.
- The pending queue includes one terminal verification task.
- The first implementation task is runnable in a fresh context.
- The scaffold is committed with the queue update.

## Commit message

```text
specs: add workstream contract cleanup queue
```
