# TASK-ADIA-02-005: Align Audit/Trace implementation evidence

## Summary

Reconcile Audit/Trace lifecycle/source-alignment against real backend/frontend/tests/runtime-validation evidence, and queue exact remediation where gaps remain.

## Required reads

- `specs/app-description-implementation-alignment/workstreams/audit-trace-alignment-plan.md`
- `specs/app-description-implementation-alignment/source-evidence-inventory.md`
- `app-description/domains/core-starter/workstreams/audit-trace/**`
- relevant source/frontend/test files identified by the inventory

## Skills

- `app-description-change-impact`
- `ai-first-saas-audit-trace`
- `akka-agent-work-trace`
- `akka-runtime-feature-verification`

## Expected outputs

- Updated Audit/Trace `lifecycle.md` and/or `realization/source-alignment.md` evidence.
- Exact follow-up entries in `implementation-follow-up-queue.md` for remaining gaps.

## Required checks

- `git diff --check`
- Targeted proof commands for mapped source/test/frontend files.

## Done criteria

- Audit/Trace is marked aligned, partially aligned, blocked, or still stale with evidence.
- No runtime-ready claim is made without real runtime evidence.
- Changes and queue update are committed.

## Vertical workstream contract

Audit/Trace functional-agent workstream; attention category investigation/denial/trace-gap/support-access; role-specific dashboard / surface Audit/Trace search/detail/timeline/correlation/investigation; surface graph node/action edge trace search/read/correlation/summary/export-if-allowed results; governed-tool id/type/exposure audit/work trace investigation tools; actor adapter/source `surface_action`, `human_chat_tool_plan`, `agent_tool_call`, API/projection/consumer/internal; confirmation/approval behavior read-only chat confirmation and export/support-access approval where allowed; idempotency/transaction/result behavior read-only idempotency, redacted and denied result surfaces; capability or foundation scope audit-and-trace-investigation; AuthContext / roles / tenant scope tenant admin/SaaS support/support-access; API / frontend / realtime path Audit/Trace route/API/projection mappings; audit/work trace expectation trace reads, denied trace access, investigation summaries and correlations; validation path `git diff --check` plus mapped evidence proof.
