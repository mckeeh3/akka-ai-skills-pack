# Backlog: Workstream Event Backbone v3

## Goal

Implement a bounded starter/reference workstream event backbone that can carry domain/workflow/task/provider events into attention, dashboard projections, traces, and future notification/digest paths.

## Task breakdown

1. Define event envelope/source-ref contract and gap map.
2. Add domain event publication and at least one consumer/projection reaction path.
3. Add workflow/process/provider lifecycle events and map them to attention/dashboard state.
4. Harden update delivery/projection behavior from event-backed state.
5. Update docs/handoff and future AutonomousAgent sequencing.
6. Verify completion.

## Required checks

Use targeted subsets of:

- `git diff --check`
- scaffolded starter backend Maven tests for event publication/consumer/projection behavior
- frontend tests/typecheck/build if update delivery or surface contracts change
- focused `rg` checks for event envelope, consumer, idempotency, source refs, and guardrails
