# Backlog: Verification

## Goal

Verify that the mini-project achieved its done state or append the next bounded task group.

## Implementation notes

- Keep verification bounded to web UI style/theme refresh scope.
- Do not perform a whole-repository UI review unless directly needed to prove this mini-project's done state.
- If material gaps remain, append new pending tasks before appending a new terminal verification task.

## Suggested harness task breakdown

1. Verify completion against README done state, conversation decisions, sprints, backlogs, tasks, and completed commits.

## Dependencies

- All planned implementation/review tasks.

## Required checks

- `git diff --check`
- Search checks over docs/skills/examples/frontend/template files for stale style/theme contradictions.
- Any frontend checks needed for changed frontend assets.

## Acceptance criteria

- Verification outcome is recorded.
- Queue either records complete state or contains bounded follow-up work plus a new terminal verification task.
