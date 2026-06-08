# TASK-WGGT-99-001: Verify workstream graph governed-tools completion

## Objective

Verify whether the mini-project done state is achieved. Append new bounded tasks plus a new terminal verification task if material gaps remain.

## Required reads

- AGENTS.md
- skills/README.md
- specs/workstream-graph-governed-tools-architecture/README.md
- specs/workstream-graph-governed-tools-architecture/conversation-capture.md
- specs/workstream-graph-governed-tools-architecture/pending-tasks.md
- specs/workstream-graph-governed-tools-architecture/sprints/*.md
- specs/workstream-graph-governed-tools-architecture/backlog/*.md
- specs/workstream-graph-governed-tools-architecture/tasks/**/*.md
- docs/agent-workstream-design-review-checklist.md

## In scope

- Compare active docs/skills/examples against README done state.
- Run searches for required and stale terms.
- Inspect ambiguous bare `tool` architecture usage.
- Append follow-up tasks if gaps remain.
- Record verification artifact.

## Checks

- `git diff --check`
- Recorded searches for `surface graph`, `role-specific dashboard`, `internal workstream agent graph`, `workstream expertise`, `governed-tool`, `browser-tool`, `agent-tool`, `internal-tool`, and ambiguous `tool` usage.

## Done criteria

- Current task group and overall mini-project done state assessed.
- If complete, completion recorded with no new tasks.
- If incomplete, new bounded tasks appended before a new terminal verification task.
- Queue updated and committed.
