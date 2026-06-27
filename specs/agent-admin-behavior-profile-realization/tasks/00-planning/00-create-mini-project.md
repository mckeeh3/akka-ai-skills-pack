# Task AABP-00-001: Create Agent Admin behavior-profile realization mini-project

## Goal

Create the durable mini-project scaffold for completing Agent Admin code changes after the app-description update.

## Required reads

- `AGENTS.md`
- `specs/AGENTS.md`
- `app-description/domains/core-starter/workstreams/agent-admin/workstream.md`
- `app-description/domains/core-starter/workstreams/agent-admin/behavior.md`
- `app-description/domains/core-starter/workstreams/agent-admin/realization/api-contracts.md`
- `app-description/domains/core-starter/workstreams/agent-admin/realization/frontend-routes.md`
- `app-description/domains/core-starter/workstreams/agent-admin/tests/coverage.md`
- `app-description/domains/core-starter/workstreams/agent-admin/realization/source-alignment.md`

## Skills

- `project-discussed-idea-to-pending-project`

## Expected outputs

- `specs/agent-admin-behavior-profile-realization/README.md`
- `specs/agent-admin-behavior-profile-realization/conversation-capture.md`
- `specs/agent-admin-behavior-profile-realization/sprints/*.md`
- `specs/agent-admin-behavior-profile-realization/backlog/*.md`
- `specs/agent-admin-behavior-profile-realization/tasks/**/*.md`
- `specs/agent-admin-behavior-profile-realization/pending-tasks.md`

## Done criteria

- Mini-project captures current intent, known drift, done state, non-goals, ordered implementation tasks, and terminal verification loop.
- Queue starts with exactly one first runnable non-done task after this scaffold task.
- Planning scaffold passes whitespace checks and is committed.

## Required checks

```bash
git diff --check -- specs/agent-admin-behavior-profile-realization
```

## Commit message

`Add Agent Admin behavior profile realization plan`
