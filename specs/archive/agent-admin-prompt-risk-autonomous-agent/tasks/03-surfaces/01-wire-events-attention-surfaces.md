# TASK-AAPR-03-001: Wire prompt-risk events, attention, and surfaces

## Objective

Wire prompt-risk task lifecycle/results into v3 events, attention, and Agent Admin structured surfaces.

## Required reads

- contract and runtime implementation from prior tasks
- v3 event backbone and attention implementation
- Agent Admin frontend/backend surfaces

## In scope

- `workflow.agent_admin.*` and `worker.task.*` events.
- Attention mappings for blocked/failed/review-required/accepted/rejected.
- Risk review/decision surfaces with evidence, risk findings, recommendation, no direct activation, and human actions.
- Backend/frontend tests.

## Required checks

- `git diff --check`
- scaffolded backend tests
- frontend tests/typecheck/build if frontend changes

## Done criteria

- Prompt-risk states are visible through backend-derived events, attention, and surfaces.
- Task changes and queue update are committed.

## Commit message

`agent-admin-risk: wire events attention surfaces`
