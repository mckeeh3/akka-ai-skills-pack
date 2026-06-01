# TASK-AAPR-02-001: Implement prompt-risk AutonomousAgent runtime

## Objective

Implement backend lifecycle for Agent Admin Prompt-Risk AutonomousAgent tasks.

## Required reads

- mini-project required context and contract from TASK-AAPR-01-001
- User Admin AutonomousAgent implementation pattern
- starter Agent Admin behavior proposal/governance files

## Skills

- `akka-autonomous-agents`

## In scope

- Task start/query/cancel/result-review capabilities.
- Akka AutonomousAgent setup/invocation or precise blocker.
- Fail-closed adapter for missing provider/runtime config.
- Backend tests for auth, lifecycle, idempotency, fail-closed, and no fake success.

## Required checks

- `git diff --check`
- scaffolded backend Maven tests
- focused `rg` for fail-closed/no fake success guardrails

## Done criteria

- Backend runtime path exists or blocker is recorded.
- Task changes and queue update are committed.

## Commit message

`agent-admin-risk: implement runtime`
