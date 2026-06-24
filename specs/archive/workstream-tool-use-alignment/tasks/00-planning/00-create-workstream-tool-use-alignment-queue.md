# TASK-WTUA-00-001: Create Workstream Tool Use Alignment planning scaffold

## Purpose

Capture the user's workstream tool-use architecture discussion as a durable skills-pack maintenance mini-project and queue.

## Required reads

- `AGENTS.md`
- `skills-pack/AGENTS.md`
- `.agents/skills/project-discussed-idea-to-pending-project/SKILL.md`
- current conversation context

## Expected outputs

- `specs/workstream-tool-use-alignment/README.md`
- `specs/workstream-tool-use-alignment/conversation-capture.md`
- `specs/workstream-tool-use-alignment/pending-tasks.md`
- `specs/workstream-tool-use-alignment/sprints/*.md`
- `specs/workstream-tool-use-alignment/backlog/*.md`
- `specs/workstream-tool-use-alignment/tasks/**/*.md`

## Required checks

- `git diff --check`

## Done criteria

- The mini-project captures the accepted tool-use model, scope, done state, non-goals, and verification loop.
- The pending queue has bounded tasks that can be run one at a time in fresh contexts.
- The first non-done task is runnable without guessing.
- The scaffold and queue status are committed.
