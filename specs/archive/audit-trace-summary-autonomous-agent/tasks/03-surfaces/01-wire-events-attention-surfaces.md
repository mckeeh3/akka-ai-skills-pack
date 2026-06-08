# TASK-ATSA-03-001: Wire Audit/Trace events, attention, and surfaces

## Objective

Wire audit summary task lifecycle/results into v3 events, attention, and Audit/Trace structured surfaces.

## In scope

- `workflow.audit_trace.*` and `worker.task.*` events.
- Attention mappings for blocked/failed/summary-ready/acknowledged states.
- Audit summary result/progress surfaces with evidence, risks, trace refs, redaction notes, and no direct mutation.
- Backend/frontend tests.

## Required checks

- `git diff --check`
- scaffolded backend tests
- frontend tests/typecheck/build if frontend changes

## Commit message

`audit-summary-agent: wire events attention surfaces`
