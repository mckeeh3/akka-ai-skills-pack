# TASK-AAFR-01-001: Fix Audit/Trace summary contract regression

## Objective

Fix the stale full-backend test or surface-contract mismatch where `WorkstreamServiceTest.auditTraceSummaryWorkerFailsClosedUntilRealAutonomousRuntimeExists` expects `audit.trace.summaryTask.v1` but current source returns `audit.trace.summaryProgress.v1`.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- mini-project README/conversation/queue entry and this task brief
- `specs/audit-trace-summary-autonomous-agent/README.md`
- `specs/audit-trace-summary-autonomous-agent/audit-trace-summary-handoff.md`
- relevant `WorkstreamServiceTest`, Audit/Trace summary surface/runtime files

## In scope

- Determine the correct canonical surface contract.
- Update test or implementation to match the intended contract.
- Avoid weakening assertions without preserving runtime/surface meaning.

## Required checks

- `git diff --check`
- targeted backend test including `WorkstreamServiceTest.auditTraceSummaryWorkerFailsClosedUntilRealAutonomousRuntimeExists`
- focused `rg` for stale `audit.trace.summaryTask.v1`/`audit.trace.summaryProgress.v1` expectations

## Commit message

`autonomous-agent-regression: fix audit summary contract`
