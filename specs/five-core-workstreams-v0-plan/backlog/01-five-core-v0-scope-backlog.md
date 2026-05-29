# Backlog: Five Core v0 Scope Coordination

## Goal

Make the five workstream implementation queues coherent before any one workstream implementation begins.

## Suggested harness task breakdown

1. Capture the shared five-core v0 contract and dependency map.
2. Review and adjust the five workstream mini-project queues for consistency with the shared contract.
3. Verify the coordination plan and append follow-up tasks if gaps remain.

## Required checks

- `git diff --check`
- targeted `rg` checks proving the six mini-projects reference each other consistently

## Acceptance criteria

- Each workstream queue has a first runnable non-implementation planning/contract task.
- Each queue ends with a verification task.
- Shared runtime completion doctrine remains visible.
