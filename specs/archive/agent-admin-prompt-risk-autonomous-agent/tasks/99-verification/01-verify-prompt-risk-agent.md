# TASK-AAPR-99-001: Verify Agent Admin prompt-risk AutonomousAgent

## Objective

Verify mini-project completion or append bounded follow-up tasks plus a new terminal verification task.

## Required checks

- `git diff --check`
- targeted backend tests for runtime/fail-closed/events/attention
- frontend tests/typecheck/build if surfaces changed
- focused `rg` for AutonomousAgent, prompt-risk, fail-closed, no fake success, events, attention, and surfaces

## Commit message

`agent-admin-risk: verify completion`
