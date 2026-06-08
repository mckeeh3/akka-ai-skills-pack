# TASK-WGGT-99-002: Verify workstream graph governed-tools completion after pack/template repair

## Objective

Verify whether the mini-project done state is achieved after the installed-pack and starter-template propagation gap is repaired.

## Required reads

- AGENTS.md
- skills/README.md
- specs/workstream-graph-governed-tools-architecture/README.md
- specs/workstream-graph-governed-tools-architecture/conversation-capture.md
- specs/workstream-graph-governed-tools-architecture/pending-tasks.md
- specs/workstream-graph-governed-tools-architecture/final-verification.md
- specs/workstream-graph-governed-tools-architecture/tasks/05-pack-template/01-update-installed-pack-and-starter-graph-guidance.md
- docs/agent-workstream-design-review-checklist.md

## In scope

- Verify the `TASK-WGGT-05-001` repair.
- Re-run required and stale-term searches over active docs/skills/examples plus `pack/` and `templates/ai-first-saas-starter/`.
- Inspect ambiguous bare `tool` architecture usage in edited pack/template files.
- Append follow-up tasks if gaps remain.
- Record a new verification artifact.

## Checks

- `git diff --check`
- Recorded searches for `surface graph`, `role-specific dashboard`, `internal workstream agent graph`, `workstream expertise`, `governed-tool`, `browser-tool`, `agent-tool`, `internal-tool`, and ambiguous `tool` usage across the repaired scope.

## Done criteria

- Current task group and overall mini-project done state assessed.
- If complete, completion is recorded with no new tasks.
- If incomplete, new bounded tasks are appended before a new terminal verification task.
- Queue updated and committed.
