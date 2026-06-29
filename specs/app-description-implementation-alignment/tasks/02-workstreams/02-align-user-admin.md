# TASK-ADIA-02-002: Align User Admin implementation evidence

## Summary

Reconcile User Admin lifecycle/source-alignment against real backend/frontend/tests/runtime-validation evidence, and queue exact remediation where gaps remain.

## Required reads

- `specs/app-description-implementation-alignment/workstreams/user-admin-alignment-plan.md`
- `specs/app-description-implementation-alignment/source-evidence-inventory.md`
- `app-description/domains/core-starter/workstreams/user-admin/**`
- relevant source/frontend/test files identified by the inventory

## Skills

- `app-description-change-impact`
- `akka-basic-user-admin`
- `akka-saas-invitation-onboarding`
- `akka-runtime-feature-verification`

## Expected outputs

- Updated User Admin `lifecycle.md` and/or `realization/source-alignment.md` evidence.
- Exact follow-up entries in `implementation-follow-up-queue.md` for remaining gaps.

## Required checks

- `git diff --check`
- Targeted proof commands for mapped source/test/frontend files.

## Done criteria

- User Admin is marked aligned, partially aligned, blocked, or still stale with evidence.
- No runtime-ready claim is made without real runtime evidence.
- Changes and queue update are committed.

## Vertical workstream contract

User Admin functional-agent workstream; attention category invitation/access-review/risky-admin-action; role-specific dashboard / surface User Admin dashboard/user list/user detail/invite/access-review/admin-audit; surface graph node/action edge invite, membership, role, support-access, access-review result surfaces; governed-tool id/type/exposure invitation/membership/role/access/admin-audit tools; actor adapter/source `surface_action`, `human_chat_tool_plan`, `agent_tool_call`, API/workflow/timer/consumer/internal; confirmation/approval behavior chat confirmation, last-admin/risky approvals; idempotency/transaction/result behavior invitation idempotency and role/support transaction/result/partial-failure surfaces; capability or foundation scope user-and-access-administration; AuthContext / roles / tenant scope admin tenant/Organization scope; API / frontend / realtime path User Admin route/API/projection mappings; audit/work trace expectation admin action, invitation, denial, requestedBy/confirmedBy traces; validation path `git diff --check` plus mapped evidence proof.
