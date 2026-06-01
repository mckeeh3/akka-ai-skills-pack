# TASK-ATSA-07-001: Wire backend-derived Audit/Trace summary events, attention, and surfaces

## Objective

Wire the concrete backend Audit/Trace summary runtime into backend-derived v3 events, attention lifecycle, and Audit/Trace progress/review surface API/action results.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/audit-trace-summary-autonomous-agent/README.md`
- `specs/audit-trace-summary-autonomous-agent/audit-trace-summary-autonomous-agent-contract.md`
- `specs/audit-trace-summary-autonomous-agent/validation/02-completion-verification.md`
- `specs/audit-trace-summary-autonomous-agent/tasks/07-surfaces/01-wire-backend-events-attention-surfaces.md`
- implementation from `TASK-ATSA-06-001`
- v3 event backbone, attention, and Audit/Trace frontend/backend surface files touched by the runtime

## Skills

- web UI/frontend skills as needed

## In scope

- Publish idempotent `workflow.audit_trace.summary_*` and `worker.task.*` v3 events from backend lifecycle transitions.
- Derive task-state attention for blocked provider/runtime, failed, completed-review-required, rejected, accepted, and cancelled states.
- Return backend-owned browser-safe `audit.trace.summaryProgress.v1` and `audit.trace.summaryReview.v1` surfaces/action results from task read/start/review flows.
- Ensure frontend renders backend-derived blocked/failed/review/accepted/rejected states without inventing fixture-only success.
- Tests for event source refs, attention upsert/resolve, surface contracts, redaction, forbidden/denied state, and no direct mutation.

## Non-goals

- Do not broaden into scheduled digest orchestration.
- Do not let frontend fixture data become authoritative state.

## Required checks

- `git diff --check`
- targeted backend tests for events/attention/surface result mapping
- frontend targeted tests/typecheck/build if frontend changes
- focused `rg` for `workflow.audit_trace.summary`, `worker.task.`, `attention:worker-task`, `audit.trace.summaryProgress.v1`, `audit.trace.summaryReview.v1`, redaction, and no fake success

## Commit message

`audit-summary-agent: wire backend surfaces`
