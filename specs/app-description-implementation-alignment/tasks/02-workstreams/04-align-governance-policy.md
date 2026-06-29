# TASK-ADIA-02-004: Align Governance/Policy implementation evidence

## Summary

Reconcile Governance/Policy lifecycle/source-alignment against real backend/frontend/tests/runtime-validation evidence, and queue exact remediation where gaps remain.

## Required reads

- `specs/app-description-implementation-alignment/workstreams/governance-policy-alignment-plan.md`
- `specs/app-description-implementation-alignment/source-evidence-inventory.md`
- `app-description/domains/core-starter/workstreams/governance-policy/**`
- relevant source/frontend/test files identified by the inventory

## Skills

- `app-description-change-impact`
- `ai-first-saas-policy-governance`
- `ai-first-saas-decision-cards`
- `akka-runtime-feature-verification`

## Expected outputs

- Updated Governance/Policy `lifecycle.md` and/or `realization/source-alignment.md` evidence.
- Exact follow-up entries in `implementation-follow-up-queue.md` for remaining gaps.

## Required checks

- `git diff --check`
- Targeted proof commands for mapped source/test/frontend files.

## Done criteria

- Governance/Policy is marked aligned, partially aligned, blocked, or still stale with evidence.
- No runtime-ready claim is made without real runtime evidence.
- Changes and queue update are committed.

## Vertical workstream contract

Governance/Policy functional-agent workstream; attention category policy approval/exception/simulation/rollback; role-specific dashboard / surface Governance/Policy dashboard/catalog/detail/draft/simulation/decision; surface graph node/action edge policy draft/simulate/approve/activate/rollback/exception results; governed-tool id/type/exposure policy lifecycle tools; actor adapter/source `surface_action`, `human_chat_tool_plan`, `agent_tool_call`, API/workflow/internal; confirmation/approval behavior human approval/decision cards; idempotency/transaction/result behavior policy version activation/rollback transaction and partial-failure surfaces; capability or foundation scope governance-policy-lifecycle; AuthContext / roles / tenant scope admin/policy operator tenant scope; API / frontend / realtime path Governance/Policy route/API mappings; audit/work trace expectation policy change/decision/simulation/exception/denial/rollback traces; validation path `git diff --check` plus mapped evidence proof.
