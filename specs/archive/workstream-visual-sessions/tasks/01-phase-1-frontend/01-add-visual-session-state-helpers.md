# Task Brief: Add Visual Session and Turn-Group State Helpers

## Task

Add reusable frontend types/helpers for phase 1 Akka component-backed workstream visual sessions and turn groups.

## Expected outputs

- visual-session and turn-group contracts/helpers under `frontend/src/workstream/**`
- helpers preserve traditional chat ordering: older above, newer below
- helpers support appending a new turn group and associating response surfaces with that turn
- helper policy for recent turn-group limit plus secondary surface cap
- focused contract tests for grouping, ordering, caps, and snapshot semantics
- queue status update and git commit

## Constraints

- Do not implement browser-local persistence.
- Do not implement backend-persisted sessions.
- Do not reorder existing durable workstream history.

## Completion

Mark `TASK-WVS-01-001` done after commit.
