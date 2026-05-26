# Task Brief: Implement Request-Surface Anchoring and Manual-Scroll Pause

## Task

Implement phase 1 stream behavior: when a new request is appended, scroll its request surface to the top of the visible workstream panel, keep it anchored while response surfaces append, and pause automatic anchoring when the user manually scrolls.

## Expected outputs

- updated stream/shell behavior for request-surface top anchoring
- manual-scroll detection and auto-anchor pause state
- reduced-motion-safe scrolling
- focused contract tests for append order, scroll target, response append behavior, and manual-scroll pause
- queue status update and git commit

## Constraints

- Preserve traditional chat ordering.
- Do not reorder prior turn groups.
- Do not introduce persistence beyond in-memory state required for the active component/session.

## Completion

Mark `TASK-WVS-01-002` done after commit.
