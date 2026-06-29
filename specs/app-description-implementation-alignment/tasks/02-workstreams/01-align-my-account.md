# TASK-ADIA-02-001: Align My Account implementation evidence

## Summary

Reconcile My Account lifecycle/source-alignment against real backend/frontend/tests/runtime-validation evidence, and queue exact remediation where gaps remain.

## Required reads

- `specs/app-description-implementation-alignment/workstreams/my-account-alignment-plan.md`
- `specs/app-description-implementation-alignment/source-evidence-inventory.md`
- `app-description/domains/core-starter/workstreams/my-account/**`
- relevant source/frontend/test files identified by the inventory

## Skills

- `app-description-change-impact`
- `akka-http-endpoints`
- `akka-web-ui-apps`
- `akka-runtime-feature-verification`

## Expected outputs

- Updated My Account `lifecycle.md` and/or `realization/source-alignment.md` evidence.
- Exact follow-up entries in `implementation-follow-up-queue.md` for remaining gaps.

## Required checks

- `git diff --check`
- Targeted proof commands for mapped source/test/frontend files.

## Done criteria

- My Account is marked aligned, partially aligned, blocked, or still stale with evidence.
- No runtime-ready claim is made without real runtime evidence.
- Changes and queue update are committed.

## Vertical workstream contract

My Account functional-agent workstream; attention category account/profile context; role-specific dashboard / surface My Account dashboard/profile/context; surface graph node/action edge account/profile/context read-update and result surfaces; governed-tool id/type/exposure account/profile/context tools; actor adapter/source `surface_action`, `api_call`, bounded `human_chat_tool_plan`, read/advisory `agent_tool_call`; confirmation/approval behavior profile/chat confirmation where applicable; idempotency/transaction/result behavior profile/context no-op/result surfaces; capability or foundation scope account-context-and-profile; AuthContext / roles / tenant scope signed-in member tenant/Organization scope; API / frontend / realtime path My Account route/API mappings; audit/work trace expectation account/context read-update/denial/agent-assistance traces; validation path `git diff --check` plus mapped evidence proof.
