# TASK-ATSA-02-001: Implement Audit/Trace summary runtime

## Objective

Implement backend lifecycle for Audit/Trace Summary AutonomousAgent tasks and scoped/redacted evidence collection.

## Required reads

- contract from TASK-ATSA-01-001
- existing AutonomousAgent worker pattern examples
- starter Audit/Trace, event backbone, attention, and agent runtime files

## Skills

- `akka-autonomous-agents`

## In scope

- Task start/read/cancel/acknowledge or result-review capabilities.
- Akka AutonomousAgent setup/invocation or precise blocker.
- Redacted evidence collection for authorized AuthContext.
- Fail-closed adapter for missing provider/runtime config.
- Backend tests for auth, redaction, lifecycle, idempotency, fail-closed, and no fake success.

## Required checks

- `git diff --check`
- scaffolded backend Maven tests
- focused `rg` for fail-closed/no fake success/redaction guardrails

## Commit message

`audit-summary-agent: implement runtime`
