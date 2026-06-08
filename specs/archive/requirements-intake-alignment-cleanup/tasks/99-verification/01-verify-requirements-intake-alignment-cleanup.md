# TASK-RIAC-99-001: Verify requirements intake alignment cleanup completion

## Objective

Verify whether the current task group and the overall mini-project done state are complete. Append new bounded tasks before a new terminal verification task if material gaps remain.

## Required reads

- AGENTS.md
- skills/README.md
- specs/requirements-intake-alignment-cleanup/README.md
- specs/requirements-intake-alignment-cleanup/conversation-capture.md
- specs/requirements-intake-alignment-cleanup/pending-tasks.md
- specs/requirements-intake-alignment-cleanup/content-inventory.md
- specs/requirements-intake-alignment-cleanup/prune-and-rewrite-criteria.md
- specs/requirements-intake-alignment-cleanup/sprints/*.md
- specs/requirements-intake-alignment-cleanup/backlog/*.md
- specs/requirements-intake-alignment-cleanup/tasks/**/*.md
- docs/agent-workstream-design-review-checklist.md

## In scope

- Compare completed work against README done state.
- Re-run stale searches defined in criteria.
- Inspect any active hits that could still misroute requirements intake.
- Append follow-up tasks and a new terminal verification task if needed.
- Record completion or findings in `final-verification.md` or a numbered verification artifact.

## Out of scope

- Do not expand into unrelated whole-repo refactors. Propose separate mini-projects for unrelated issues.

## Checks

- `git diff --check`
- Stale-term searches recorded in verification artifact.
- Reference checks for removed files when applicable.

## Done criteria

- Current task group is assessed.
- Overall mini-project done state is assessed.
- If complete, completion is recorded with no new required tasks.
- If incomplete, new bounded tasks are appended before a new terminal verification task.
- Queue updated and committed.
