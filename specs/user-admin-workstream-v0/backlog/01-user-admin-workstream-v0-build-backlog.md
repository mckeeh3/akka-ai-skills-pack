# Backlog: User Admin Workstream v0

## Goal

Deliver the `User Admin Agent` v0 vertical without expanding scope to the other four workstreams.

## Suggested harness task breakdown

1. Write the workstream contract and capability inventory.
2. Implement backend/runtime capabilities and deterministic services needed for the v0 vertical.
3. Implement or refine request/response agent behavior and governed tools/references.
4. Add AutonomousAgent-backed task support only for durable internal/background work justified by the contract.
5. Implement frontend surfaces, actions, attention/status behavior, and trace links.
6. Verify the vertical through local checks and append follow-up tasks if gaps remain.

## Dependencies

- Shared plan task `TASK-FCPLAN-01-001` should be done before implementation tasks in this workstream.
- Existing production-ready five-core v0 baseline should remain passing.

## Required checks

- Relevant backend tests, frontend tests/typecheck/build, fullstack validation when the task changes runtime behavior.
- `git diff --check` for every task.

## Acceptance criteria

- The workstream is vertically useful at v0 scope.
- Request/response, AutonomousAgent, and deterministic internal service choices are documented and tested according to actual workstream needs.
