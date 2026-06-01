# TASK-GPIA-02-001: Implement Governance/Policy impact runtime

## Objective

Implement backend lifecycle for Governance/Policy Impact AutonomousAgent tasks and scoped evidence collection.

## Required reads

- contract from TASK-GPIA-01-001
- existing AutonomousAgent worker pattern examples
- starter Governance/Policy, event backbone, attention, and agent runtime files

## Skills

- `akka-autonomous-agents`

## In scope

- Task start/read/cancel/accept/reject/request-changes capabilities.
- Akka AutonomousAgent setup/invocation or precise blocker.
- Scoped evidence collection for proposed policy/governance changes.
- Fail-closed adapter for missing provider/runtime config.
- Backend tests for auth, redaction, lifecycle, idempotency, fail-closed, and no fake success.

## Required checks

- `git diff --check`
- scaffolded backend Maven tests
- focused `rg` for fail-closed/no fake success/redaction guardrails

## Commit message

`governance-impact-agent: implement runtime`
