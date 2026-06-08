# Backlog 04: PRD, Spec, and Backlog Planning Realignment

## Goal

Make direct PRD/spec/backlog planning produce vertical workstream-attention-dashboard-surface-capability-autonomous-task implementation plans.

## Suggested task breakdown

1. Update `akka-solution-decomposition` output sections and workflow.
2. Update `akka-prd-to-specs-backlog` PRD extraction and output contracts.
3. Update revised PRD/change/slice/backlog planning skills and planning docs.

## Implementation notes

- Large PRDs should split into workstreams and cross-workstream foundation concerns.
- Each workstream should start with attention/dashboard semantics.
- Backlogs should include autonomous task definitions/result DTOs/notifications where internal workers are needed.
- Keep capability-first backend contracts as the authoritative operation/query layer.

## Required checks

- `git diff --check`
- `rg -n "attention|dashboard|AutonomousAgent|autonomous task|workstream|surface action" skills/akka-solution-decomposition skills/akka-prd-to-specs-backlog skills/akka-*backlog* skills/akka-*prd* docs/prd-to-akka-flow.md docs/module-sprint-planning.md docs/solution-plan-to-implementation-queue.md`

## Acceptance criteria

- PRD/spec planning creates implementation-ready vertical increments instead of component-family-only tasks.
