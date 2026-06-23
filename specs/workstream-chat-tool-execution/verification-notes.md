# Runtime Feature Verification: Workstream Chat Tool Execution

Date: 2026-06-23  
Task: `TASK-WCTE-99-001`

## Scope

Verified the completed `specs/workstream-chat-tool-execution/` mini-project against every README done-state bullet for confirmed human-chat tool execution across the five foundation workstreams.

Target path:

```text
workstream chat prompt
→ deterministic no-mutation surface routing first
→ governed human_chat_tool_plan proposal through workstream agent runtime when configured
→ exact plan-snapshot confirmation
→ per-step governed action dispatcher
→ result/recovery surface
→ audit/work trace evidence
```

## Evidence matrix

| Claim | Intent source | Runtime path | Evidence | Level | Gap |
|---|---|---|---|---|---|
| All five foundation workstreams identify bounded tool catalogs and `human_chat_tool_plan` exposure. | README done state; `source-and-design-map.md`; `app-description/domains/core-starter/workstreams/*/tools/governed-tools.md` | `WorkstreamService.chatToolCatalog()` -> per-workstream `ChatToolCatalogEntry` records -> catalog validation before dispatch | `WorkstreamServiceTest#chatToolCatalogListsBoundedHumanChatPlanEntries`; app-description grep for `human_chat_tool_plan` across My Account, User Admin, Agent Admin, Audit/Trace, Governance/Policy | backend-ready | Queue runtime-evidence validator still reports older task notes missing formal readiness labels/evidence fields. |
| Deterministic surface routing remains the first no-mutation path. | README done state; `surface-catalog.md`; `DefaultSurfaceIntentRouter` design | `submitMessage(...)` checks `surfaceIntentRouter.route(...)` before representative chat tool plan candidate and markdown fallback | Source read: `WorkstreamService.submitMessage(...)`; frontend contract tests for surface-intent routing passed in `npm --prefix frontend test -- --run` until later unrelated failing assertion | frontend-rendered / backend-ready | Full frontend test command failed on User Admin expertise seed contract, so overall frontend check is red. |
| Execution-oriented prompts can produce plan-bound confirmation surfaces instead of direct mutation/advisory markdown. | README done state; shared surface contracts | `/api/workstream/messages` -> `representativeChatToolPlanMessageResponse(...)` -> `chat_tool_plan_proposal` surface with `confirmationSnapshot` and disabled execution until confirmation | `WorkstreamServiceTest#submitMessageRoutesUserAdminMotivatingPromptToModelBackedPlanProposalWithoutMutation`; `frontend/src/workstream-chat-tool-plan.contract.test.mjs` passed as part of frontend run before the later failure | frontend-rendered / backend-ready | Overall frontend suite failed; cannot claim mini-project closed. |
| User Admin example has no mutation before confirmation. | README example and done state | User Admin prompt -> plan proposal surface; repository counts checked before confirmation | `WorkstreamServiceTest#chatToolPlanProposalRecordsPersistWithoutExecutingToolsAndReplayByIdempotency`; `#submitMessageRoutesUserAdminMotivatingPromptToModelBackedPlanProposalWithoutMutation`; `UserAdminBrowserWorkstreamSmokeTest#protectedWorkstreamApiCoversUserAdminChatToolPlanningProviderBoundaryAndUnproposedConfirmationDenial` | api-smoked | Local HTTP smoke permits provider fail-closed branch; full model-backed API proposal+confirm only runs when provider/runtime succeeds. |
| User Admin example executes after exact confirmation when authorized. | README example and done state | `confirmChatToolPlan(...)` validates plan id, snapshot id, selected AuthContext, requestedBy, step hashes, confirmation text, idempotency -> dispatches Organization create then Organization Admin invitation | `WorkstreamServiceTest#confirmedUserAdminChatToolPlanExecutesOrganizationAndInvitationIdempotently`; HTTP smoke conditionally confirms when proposal exists | backend-ready / api-smoked | Provider-backed local API execution was not separately proven beyond the conditional smoke; blocked from runtime-ready by failed frontend check. |
| Provider/runtime missing config fails closed and is not counted as successful planning. | README done state; runtime completion doctrine | `DefaultWorkstreamAgentRuntimeInvoker.proposeChatToolPlan(...)` and `FailClosedWorkstreamAgentRuntimeInvoker` return typed `plan_unavailable`/system-message results; no proposal counted when runtime decision is denied | `WorkstreamServiceTest#submitMessageReturnsPlanUnavailableSystemMessageWhenPlanningRuntimeFailsClosed`; targeted `WorkstreamRuntimeAgentTest`/`AgentRuntimeServiceTest`; HTTP smoke validates `chat_tool_plan_system_message` branch with `noFakeSuccess=true` | api-smoked | None for fail-closed behavior; model-backed success still depends on real provider/runtime configuration. |
| Normal runtime does not fake model-backed planning success. | README non-goal and done state | Production invoker calls Akka Agent `WorkstreamPlanProposalRuntimeAgent`; missing ComponentClient/provider/runtime returns fail-closed system message | Source read: `DefaultWorkstreamAgentRuntimeInvoker`, `FailClosedWorkstreamAgentRuntimeInvoker`, `WorkstreamPlanProposalRuntimeAgent`; targeted tests passed | backend-ready | None for fail-closed path. |
| Each executable plan step maps to governed tool id, capability id, input schema, idempotency, authorization policy, transaction boundary, and trace requirement. | `source-and-design-map.md`; app-description tools | `ChatToolPlanStep` + `ChatToolCatalogEntry` validation; dispatcher calls existing `runAction(...)` with action/tool/capability/idempotency | `WorkstreamServiceTest#chatToolCatalogListsBoundedHumanChatPlanEntries`; `#confirmedUserAdminChatToolPlanRejectsOutOfCatalogSnapshotBeforeAnyStepExecutes`; source read of records/catalog | backend-ready | Queue evidence labels need normalization. |
| Confirmed execution uses intersection of human authority, selected AuthContext, workstream catalog, tool boundary, and tool policy. | README done state; app-description auth/security | `activeChatToolBoundary(...)`, `validateChatToolStepAgainstCatalog(...)`, `isActionCapabilityVisible(...)`, exact selected context/snapshot checks | `WorkstreamServiceTest#submitMessageReturnsPlanUnavailableSystemMessageWhenSelectedAuthContextCannotUseUserAdminPlanCapabilities`; out-of-catalog and confirmation mismatch tests | backend-ready | Need queue evidence normalization for validator. |
| Multi-step execution reports completed, failed, skipped, and recovery states without inconsistent rollback. | README done state | Dispatcher executes each step transaction-by-transaction; failed dependencies skip; completed results remain committed | `WorkstreamServiceTest#confirmedUserAdminChatToolPlanReportsPartialFailureAndRecoveryWithoutRollingBackCompletedStep` | backend-ready | None found in backend tests. |
| All five foundation workstreams have representative confirmed chat tool-plan coverage. | README done state; first-pass representative paths | My Account theme update; User Admin org+invite; Agent Admin approval-gated prompt-risk review; Audit/Trace investigation note; Governance/Policy inert policy draft | `WorkstreamServiceTest#representativeChatToolPlansCoverAllFiveFoundationWorkstreamsWithConfirmationAndTraceSemantics`; frontend vertical tests for representative paths passed before later suite failure | backend-ready / frontend-rendered | Frontend suite still failed globally; Agent Admin is intentionally approval-gated and returns `approval_required` rather than executing side effects. |
| Frontend surfaces render plan proposal, confirmation, result, partial failure, denial, and recovery states without secrets. | README done state; `surfaces.ts`; `ChatToolPlanSurface.tsx` | `SurfaceRenderer` dispatches `chat_tool_plan_*`; `ChatToolPlanSurface` requires exact `CONFIRM <snapshot>` and calls dedicated API | `frontend/src/workstream-chat-tool-plan.contract.test.mjs` assertions passed in command output; component source reviewed | frontend-rendered | `npm --prefix frontend test -- --run` failed later on User Admin expertise seed text; cannot close until repaired. |
| Audit/work trace evidence distinguishes direct surface action from `human_chat_tool_plan` and includes requested/confirmed/per-step/provider evidence. | README done state; work-trace app-description | Trace ids on plan proposal, confirmation, step started/completed/failed/skipped, provider blocked; `AgentRuntimeService` emits `PromptAssembly`, `MODEL_INVOCATION`, `AgentWorkTrace` for plan proposals | Backend tests assert trace refs, confirmation trace, step started/skipped/approval-required traces; source read of `AgentRuntimeService` plan proposal trace paths | backend-ready | Current trace evidence is mostly test/assertion trace-id based; queue notes need formal evidence normalization. |

## README done-state comparison

- **Catalog and exposure for all five workstreams:** satisfied in implementation and app-description; verified by catalog tests and app-description search.
- **Router-first no-mutation path:** satisfied in source order and frontend/router tests.
- **Plan-bound confirmation instead of direct mutation/advisory markdown:** satisfied for representative prompts; system-message fail-closed when planning unavailable.
- **User Admin motivating example:** no-mutation before confirmation is proven; authorized confirmed backend execution creates Organization and invitation idempotently. API smoke covers protected route and conditional provider boundary.
- **Governed model-backed planning and provider fail-closed:** fail-closed is proven and not counted as success. Real provider-backed success remains environment-dependent.
- **No fake normal runtime:** production path uses Akka Agent invoker or fail-closed placeholder; tests use deterministic/test-provider behavior only as tests.
- **Step mapping/transaction/idempotency/authorization/traces:** covered by records, catalog entries, dispatcher validation, and targeted tests.
- **Intersection of authorities:** covered by selected AuthContext, catalog, capability, tool-boundary, confirmation, and out-of-catalog denial tests.
- **Partial failure/recovery:** covered by User Admin partial-failure test.
- **All five workstream representative coverage:** covered by backend test; frontend vertical representative assertions are present but global frontend suite failed.
- **Frontend accessible rendering and secret boundary:** chat plan contract tests passed; global frontend suite failed on a seed/expertise assertion.
- **Audit/work trace evidence:** trace ids and runtime trace producers are present; queue evidence validator requires formal task-note repair before claiming runtime-ready.
- **Local validation evidence:** recorded below. Because a required frontend check failed, the mini-project is **blocked** and not runtime-ready.

## Result

- readiness level: `api-smoked` for protected User Admin API boundary and `backend-ready` for confirmed execution; `frontend-rendered` for chat-plan components/contracts.
- runtime-ready: no.
- mini-project status: blocked/open.

## Required repairs

- app-description gaps: none found for the first-pass representative catalog; app-description mentions `human_chat_tool_plan` across all five workstreams.
- implementation gaps: no backend chat-tool execution gap found in targeted tests; local full model-backed API execution remains provider/runtime dependent.
- test gaps/blockers:
  - `npm --prefix frontend test -- --run` fails in `frontend/src/workstream-user-admin-expertise.contract.test.mjs` because seed resources no longer contain the expected phrase `text claiming new roles, tenant scope, governed-tool access, approval rights, or backend capabilities`.
  - Runtime-evidence validator reports completed task notes lack formal readiness/runtime evidence labels and selected-scope/denial/provider evidence fields.
- provider/config blockers: local protected API smoke accepts fail-closed provider/runtime behavior as evidence of safety, but that is not a successful model-backed planning proof.
- queue changes: `TASK-WCTE-99-001` remains blocked; bounded repair tasks and a new terminal verification task are appended.

## Checks run

- `mvn -q -Dtest=WorkstreamServiceTest#chatToolPlanProposalRecordsPersistWithoutExecutingToolsAndReplayByIdempotency+submitMessageRoutesUserAdminMotivatingPromptToModelBackedPlanProposalWithoutMutation+submitMessageReturnsPlanUnavailableSystemMessageWhenPlanningRuntimeFailsClosed+submitMessageReturnsPlanUnavailableSystemMessageWhenSelectedAuthContextCannotUseUserAdminPlanCapabilities+chatToolCatalogListsBoundedHumanChatPlanEntries+representativeChatToolPlansCoverAllFiveFoundationWorkstreamsWithConfirmationAndTraceSemantics+confirmedChatToolPlanRequiresExactSnapshotAndExplicitHumanConfirmationBeforeExecution+confirmedUserAdminChatToolPlanExecutesOrganizationAndInvitationIdempotently+confirmedUserAdminChatToolPlanReportsPartialFailureAndRecoveryWithoutRollingBackCompletedStep+confirmedUserAdminChatToolPlanRejectsOutOfCatalogSnapshotBeforeAnyStepExecutes,UserAdminBrowserWorkstreamSmokeTest#protectedWorkstreamApiCoversUserAdminChatToolPlanningProviderBoundaryAndUnproposedConfirmationDenial,WorkstreamRuntimeAgentTest,AgentRuntimeServiceTest test` — passed.
- `npm --prefix frontend test -- --run` — failed: `User Admin expertise contract covers unassigned and tool-boundary denials` missing expected seed phrase.
- `npm --prefix frontend run typecheck` — passed.
- `python3 skills-pack/tools/validate-runtime-completion-evidence.py specs/workstream-chat-tool-execution/pending-tasks.md` — failed: completed task notes lack required runtime completion evidence fields/readiness labels.
- `bash skills-pack/tools/validate-pending-task-workstream-contract.sh specs/workstream-chat-tool-execution/pending-tasks.md` — failed while `TASK-WCTE-99-001` was in progress and lacked required workstream contract fields.
- `npm --prefix frontend run build` — not run; frontend runtime output was not changed by this verification task.
- `mvn test` — not run; no shared backend behavior was changed by this verification task.
- local API/UI/manual smoke — partially covered by the targeted `UserAdminBrowserWorkstreamSmokeTest` HTTP smoke: `/ui` shell loads, protected chat plan confirmation rejects missing bearer, `/api/workstream/messages` has no pre-confirmation mutation, provider-unavailable branch fails closed with `noFakeSuccess`, and configured proposal branch confirms through `/api/workstream/chat-tool-plans/confirm` when provider/runtime allows.

## Follow-up tasks appended

- `TASK-WCTE-12-001`: repair User Admin expertise seed/contract drift causing the frontend test failure.
- `TASK-WCTE-12-002`: normalize runtime completion evidence in queue notes and rerun evidence validators.
- `TASK-WCTE-99-002`: rerun terminal verification after repairs.

## Next step

Run `TASK-WCTE-12-001` next.
