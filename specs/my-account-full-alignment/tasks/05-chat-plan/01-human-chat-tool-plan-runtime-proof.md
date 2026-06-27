# MAFA-05-001: Human chat tool-plan runtime proof

## Goal

Prove and repair the bounded My Account `human_chat_tool_plan` path for profile/settings and notification lifecycle operations.

## Required reads

- `specs/my-account-full-alignment/README.md`
- `app-description/domains/core-starter/workstreams/my-account/tools/governed-tools.md`
- `app-description/domains/core-starter/workstreams/my-account/tests/coverage.md`
- `app-description/domains/core-starter/workstreams/my-account/traces/work-traces.md`
- `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`
- `src/main/java/ai/first/application/foundation/agent/AgentBehaviorSeedLoader.java`
- existing chat-plan/backend/frontend tests

## Skills

- `akka-agent-tool-boundaries`
- `akka-agent-work-trace`
- `akka-agent-testing`
- `capability-first-backend`

## Implementation requirements

- Verify deterministic no-mutation surface routing runs before chat-plan execution where applicable.
- Verify representative proposal, especially `change my theme to Obsidian Dark`, creates a no-mutation plan with exact governed tool/capability/schema/result surface metadata.
- Verify no mutation occurs before exact `planId` + `planSnapshotId` confirmation.
- Verify confirmed execution recomputes authorization/tool boundary, uses idempotency/transaction boundaries, emits traces, and returns declared surfaces.
- Verify stale/modified/expired/cross-context/cross-tenant/out-of-catalog/unsupported-field/hidden-target/provider-runtime/tool-boundary blocked confirmations deny safely with `chat_tool_plan.system_message.v1` and `noDirectMutation=true`.
- Verify replay does not duplicate effects.
- Verify partial-failure reporting where multi-step plans are possible.
- Repair runtime/test drift discovered by these checks.

## Vertical workstream contract

- Lifecycle / readiness target: build-compile to backend-ready for chat-plan path; provider/model unavailable may be fail-closed unless configured.
- Workstream / functional agent: My Account / `my-account-agent`.
- Surface graph/action edge: chat proposal/result/system-message surfaces plus `surface-my-profile`, `surface-my-settings`, `surface-my-account-notification-center`.
- Governed-tool id and exposure: `my_account.update_profile_settings`, `notification.mark_read`, `notification.dismiss`, `notification.archive`, `notification.snooze`, `notification.update_preferences`; actor adapter `human_chat_tool_plan`.
- Confirmation / approval / transaction: exact snapshot confirmation required; per-step idempotency and transaction boundary; partial failure must report completed/failed/skipped.
- Capability id: matching My Account/notification capability ids.
- AuthContext / tenant scope: selected `AuthContext`, requestedBy/confirmedBy, tenant/customer/account scope, hidden target denial.
- Akka substrate: workstream service and agent/tool-boundary tests; no frontend auto-confirm.
- Audit/work trace requirements: proposal/confirmation/step lifecycle durable trace events or bounded blocker from MAFA-03.
- Local validation path: targeted Maven and frontend chat-plan tests.

## Required checks

```bash
mvn -Dtest='WorkstreamServiceTest,AgentBehaviorSeedLoaderTest,*ToolBoundary*Test,*Trace*Test' test
npm --prefix frontend test -- --run frontend/src/workstream-chat-tool-plan.contract.test.mjs frontend/src/workstream-my-account-vertical.contract.test.mjs
git diff --check
```

Use updated test names if needed.

## Done criteria

- Chat-plan proposal/confirmation/denial/idempotency behavior is automated-tested.
- Tool-boundary and trace evidence is present or precisely blocked with follow-up tasks.
- Source-alignment chat-plan entry is updated.
- Queue status is updated and changes are committed.
