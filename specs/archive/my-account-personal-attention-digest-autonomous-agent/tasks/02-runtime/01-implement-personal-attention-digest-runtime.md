# TASK-MAPAD-02-001: Implement personal attention digest runtime

## Objective

Implement backend lifecycle for My Account Personal Attention Digest AutonomousAgent tasks and authorized attention evidence collection.

## Required reads

- contract from TASK-MAPAD-01-001
- existing AutonomousAgent worker pattern examples
- starter My Account, attention, event backbone, and agent runtime files

## Skills

- `akka-autonomous-agents`

## In scope

- Task start/read/cancel/acknowledge capabilities.
- Akka AutonomousAgent setup/invocation or precise blocker.
- Authorized attention evidence collection for selected AuthContext.
- Fail-closed adapter for missing provider/runtime config.
- Backend tests for auth, redaction, lifecycle, idempotency, fail-closed, and no fake success.

## Required checks

- `git diff --check`
- scaffolded backend Maven tests
- focused `rg` for fail-closed/no fake success/redaction guardrails

## Commit message

`my-account-digest-agent: implement runtime`
