# Backlog: User Admin Vertical Contract

## Goal

Create an implementation map for the first full-core SMB User Admin vertical slices.

## Suggested harness task breakdown

1. Define User Admin vertical slice contracts and implementation map.
2. Inspect starter/source boundaries and append bounded implementation tasks for the first vertical slice.
3. Verify User Admin readiness and append follow-up tasks if gaps remain.

## Required checks

- `git diff --check`
- targeted `rg` checks proving User Admin capabilities, surfaces, invitation/member/role/status/access-review, request/response agent, AutonomousAgent/internal worker, deterministic services, audit/trace, and runtime validation are represented

## Acceptance criteria

- User Admin work can proceed one vertical slice at a time.
- The first implementation slice names capability ids, surfaces/actions, backend/API/frontend work, tests, and local validation.
- Access-review worker work is queued only after deterministic capability and surface foundations are clear.
