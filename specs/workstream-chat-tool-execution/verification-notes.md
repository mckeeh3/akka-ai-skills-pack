# Runtime Feature Verification: Workstream Chat Tool Execution

Date: 2026-06-23  
Task: `TASK-WCTE-99-002`

## Scope

Re-verified the completed `specs/workstream-chat-tool-execution/` mini-project after follow-up repairs:

- `TASK-WCTE-12-001` / commit `b57cd35d`: repaired User Admin expertise seed/contract drift.
- `TASK-WCTE-12-002` / commit `4c609e86`: normalized runtime completion evidence in queue notes.

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
| All five foundation workstreams identify bounded tool catalogs and `human_chat_tool_plan` exposure. | README done state; `source-and-design-map.md`; `app-description/domains/core-starter/workstreams/*/tools/governed-tools.md` | `WorkstreamService.chatToolCatalog()` -> per-workstream `ChatToolCatalogEntry` records -> catalog validation before dispatch | `WorkstreamServiceTest#chatToolCatalogListsBoundedHumanChatPlanEntries`; app-description grep/source review across My Account, User Admin, Agent Admin, Audit/Trace, Governance/Policy; runtime-evidence validator passed | runtime-ready | none |
| Deterministic surface routing remains the first no-mutation path. | README done state; `surface-catalog.md`; `DefaultSurfaceIntentRouter` design | `submitMessage(...)` checks `surfaceIntentRouter.route(...)` before representative chat tool-plan classification and markdown fallback | Source review of `WorkstreamService.submitMessage(...)`; frontend router contracts in `npm --prefix frontend test -- --run` passed | runtime-ready | none |
| Execution-oriented prompts can produce plan-bound confirmation surfaces instead of direct mutation/advisory markdown. | README done state; shared surface contracts | `/api/workstream/messages` -> representative `human_chat_tool_plan` candidate -> governed runtime proposal/fail-closed result -> `chat_tool_plan_proposal` or `chat_tool_plan_system_message` surface | `WorkstreamServiceTest#submitMessageRoutesUserAdminMotivatingPromptToModelBackedPlanProposalWithoutMutation`; `frontend/src/workstream-chat-tool-plan.contract.test.mjs`; `UserAdminBrowserWorkstreamSmokeTest#protectedWorkstreamApiCoversUserAdminChatToolPlanningProviderBoundaryAndUnproposedConfirmationDenial` | runtime-ready | none |
| User Admin example has no mutation before confirmation. | README example and done state | User Admin prompt -> no-mutation plan proposal/system-message; Organization/invitation repository counts unchanged before confirmation | `WorkstreamServiceTest#chatToolPlanProposalRecordsPersistWithoutExecutingToolsAndReplayByIdempotency`; `#submitMessageRoutesUserAdminMotivatingPromptToModelBackedPlanProposalWithoutMutation`; protected API smoke checks no pre-confirmation mutation | runtime-ready | none |
| User Admin example executes after exact confirmation when authorized. | README example and done state | `confirmChatToolPlan(...)` validates plan id, snapshot id, selected AuthContext, requestedBy, step hashes, exact `CONFIRM <planSnapshotId>`, and idempotency, then dispatches Organization create and Organization Admin invitation | `WorkstreamServiceTest#confirmedChatToolPlanRequiresExactSnapshotAndExplicitHumanConfirmationBeforeExecution`; `#confirmedUserAdminChatToolPlanExecutesOrganizationAndInvitationIdempotently`; protected API smoke conditionally confirms when provider/runtime returns a proposal | runtime-ready | none |
| Provider/runtime missing config fails closed and is not counted as successful planning. | README done state; runtime completion doctrine | `DefaultWorkstreamAgentRuntimeInvoker.proposeChatToolPlan(...)` and fail-closed invoker return typed `plan_unavailable`/system-message; production path does not fabricate successful model-backed planning | `WorkstreamServiceTest#submitMessageReturnsPlanUnavailableSystemMessageWhenPlanningRuntimeFailsClosed`; `WorkstreamRuntimeAgentTest`; `AgentRuntimeServiceTest`; protected API smoke validates `noFakeSuccess` on system-message branch | runtime-ready | none |
| Normal runtime does not fake model-backed planning success. | README non-goal and done state | Production invoker prepares governed Akka Agent runtime; missing provider/runtime/tool-boundary produces fail-closed traces and typed system message | Source review of `DefaultWorkstreamAgentRuntimeInvoker`, `FailClosedWorkstreamAgentRuntimeInvoker`, `WorkstreamPlanProposalRuntimeAgent`, `AgentRuntimeService`; targeted tests passed | runtime-ready | none |
| Each executable plan step maps to governed tool id, capability id, input schema, idempotency, authorization policy, transaction boundary, and trace requirement. | `source-and-design-map.md`; app-description tools | `ChatToolPlanStep` + `ChatToolCatalogEntry`; dispatcher validates catalog/tool/capability/schema/idempotency/confirmation before existing action dispatch | `WorkstreamServiceTest#chatToolCatalogListsBoundedHumanChatPlanEntries`; `#confirmedUserAdminChatToolPlanRejectsOutOfCatalogSnapshotBeforeAnyStepExecutes`; source review of catalog/dispatcher records | runtime-ready | none |
| Confirmed execution uses the intersection of human authority, selected AuthContext, selected workstream catalog, tool boundary, and tool policy. | README done state; app-description auth/security | `activeChatToolBoundary(...)`, `validateChatToolStepAgainstCatalog(...)`, `isActionCapabilityVisible(...)`, exact selected-context/snapshot checks | `WorkstreamServiceTest#submitMessageReturnsPlanUnavailableSystemMessageWhenSelectedAuthContextCannotUseUserAdminPlanCapabilities`; out-of-catalog and confirmation mismatch tests; protected missing-bearer API smoke | runtime-ready | none |
| Multi-step execution reports completed, failed, skipped, and recovery states without inconsistent rollback. | README done state | Dispatcher executes each step as an independent transaction boundary; failed dependencies are skipped; completed results remain committed with recovery copy | `WorkstreamServiceTest#confirmedUserAdminChatToolPlanReportsPartialFailureAndRecoveryWithoutRollingBackCompletedStep`; frontend result/recovery contract tests | runtime-ready | none |
| All five foundation workstreams have representative confirmed chat tool-plan coverage. | README done state; first-pass representative paths | My Account theme update; User Admin org+invite; Agent Admin approval-gated prompt-risk review; Audit/Trace investigation note; Governance/Policy inert policy draft | `WorkstreamServiceTest#representativeChatToolPlansCoverAllFiveFoundationWorkstreamsWithConfirmationAndTraceSemantics`; frontend vertical contract tests passed in full frontend suite | runtime-ready | none |
| Frontend surfaces render plan proposal, confirmation, execution result, partial failure, denial, and recovery states accessibly without exposing secrets. | README done state; `frontend/src/workstream/types/surfaces.ts`; `ChatToolPlanSurface.tsx` | `SurfaceRenderer` dispatches `chat_tool_plan_*`; `ChatToolPlanSurface` requires exact confirmation and calls dedicated API client; system-message copy displays `noFakeSuccess` safely | `npm --prefix frontend test -- --run` passed 175 tests; `npm --prefix frontend run typecheck` passed; source review confirms no generic `globalThis.confirm` for plan execution | runtime-ready | none |
| Audit/work traces distinguish direct surface action from `human_chat_tool_plan` and include requestedBy, confirmedBy, correlation/idempotency, per-step outcomes, denials, and provider fail-closed evidence. | README done state; app-description work-trace files | Proposal, confirmation, provider-blocked, step started/completed/failed/skipped, approval-required, and denial trace refs are created in runtime/service paths and rendered browser-safely | Targeted backend tests assert proposal/confirmation/step trace refs; app-description traces cover all five workstreams; validators passed after TASK-WCTE-12-002 | runtime-ready | none |

## README done-state comparison

- **Catalog and exposure for all five workstreams:** achieved. App-description and backend catalog entries cover My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy with `human_chat_tool_plan` exposure.
- **Router-first no-mutation path:** achieved. Deterministic surface routing remains first and side-effect free before chat tool planning.
- **Plan-bound confirmation instead of direct mutation/advisory markdown:** achieved for the first-pass representative prompts, with typed `chat_tool_plan_proposal`, `chat_tool_plan_result`, and `chat_tool_plan_system_message` surfaces.
- **User Admin motivating example:** achieved at the stated scope. Tests prove no Organization/invitation mutation before confirmation and authorized execution after exact snapshot confirmation.
- **Governed model-backed planning and provider fail-closed:** achieved. The proposal path invokes the governed runtime when available and returns typed fail-closed `plan_unavailable`/system-message surfaces with `noFakeSuccess` when provider/runtime/tool-boundary checks fail.
- **No fake normal runtime:** achieved. Deterministic/test provider behavior is confined to tests; normal runtime does not count provider-unavailable results as successful model-backed planning.
- **Step mapping/transaction/idempotency/authorization/traces:** achieved through catalog records, snapshot validation, per-step dispatcher reauthorization, idempotency, transaction boundaries, and trace refs.
- **Intersection of authorities:** achieved. Execution is bounded by human authority, selected AuthContext, selected workstream catalog, active tool boundary, capability visibility, and policy/approval state.
- **Partial failure/recovery:** achieved. Tests cover completed, failed, skipped, and recovery states without rolling back committed steps.
- **All five workstream representative coverage:** achieved. Backend and frontend contract evidence covers one representative plan path per foundation workstream; high-impact actions remain blocked or approval-gated.
- **Frontend accessible rendering and secret boundary:** achieved. Frontend tests/typecheck passed; plan surfaces require exact confirmation, display denial/recovery/trace copy, and avoid exposing secrets or hidden capabilities.
- **Audit/work trace evidence:** achieved at the stated scope. Trace refs and app-description trace obligations cover direct surface-vs-chat-plan distinction, requestedBy/confirmedBy, provider-blocked, confirmation, per-step results, denials, and browser-safe redaction.
- **Local validation evidence:** achieved. Required backend/API, frontend, runtime-evidence, workstream-contract, and diff checks passed.

## Result

- readiness level: `runtime-ready` for the stated first-pass scope of confirmed human-chat tool execution across representative paths in all five foundation workstreams.
- runtime-ready: yes.
- mini-project status: closed.

## Required repairs

- app-description gaps: none found for first-pass representative scope.
- implementation gaps: none found in targeted re-verification.
- test gaps: none found; prior frontend seed-contract and evidence-validator blockers are repaired.
- provider/config blockers: none for safe runtime readiness. Provider-unavailable or runtime-unavailable planning fails closed with typed `noFakeSuccess` surfaces and is not counted as successful model-backed planning.
- queue changes: `TASK-WCTE-99-002` is marked done; no additional follow-up tasks are appended.

## Checks run

- `mvn -q -Dtest=WorkstreamServiceTest#chatToolPlanProposalRecordsPersistWithoutExecutingToolsAndReplayByIdempotency+submitMessageRoutesUserAdminMotivatingPromptToModelBackedPlanProposalWithoutMutation+submitMessageReturnsPlanUnavailableSystemMessageWhenPlanningRuntimeFailsClosed+submitMessageReturnsPlanUnavailableSystemMessageWhenSelectedAuthContextCannotUseUserAdminPlanCapabilities+chatToolCatalogListsBoundedHumanChatPlanEntries+representativeChatToolPlansCoverAllFiveFoundationWorkstreamsWithConfirmationAndTraceSemantics+confirmedChatToolPlanRequiresExactSnapshotAndExplicitHumanConfirmationBeforeExecution+confirmedUserAdminChatToolPlanExecutesOrganizationAndInvitationIdempotently+confirmedUserAdminChatToolPlanReportsPartialFailureAndRecoveryWithoutRollingBackCompletedStep+confirmedUserAdminChatToolPlanRejectsOutOfCatalogSnapshotBeforeAnyStepExecutes,UserAdminBrowserWorkstreamSmokeTest#protectedWorkstreamApiCoversUserAdminChatToolPlanningProviderBoundaryAndUnproposedConfirmationDenial,WorkstreamRuntimeAgentTest,AgentRuntimeServiceTest test` — passed.
- `npm --prefix frontend test -- --run` — passed: 175 tests.
- `npm --prefix frontend run typecheck` — passed.
- `python3 skills-pack/tools/validate-runtime-completion-evidence.py specs/workstream-chat-tool-execution/pending-tasks.md` — passed.
- `bash skills-pack/tools/validate-pending-task-workstream-contract.sh specs/workstream-chat-tool-execution/pending-tasks.md` — passed.
- `git diff --check` — passed.
- local API/UI/manual smoke — covered by targeted `UserAdminBrowserWorkstreamSmokeTest` local Akka HTTP smoke: `/ui` hosted shell loads, missing bearer on confirmation is rejected, `/api/workstream/messages` has no pre-confirmation mutation, provider-unavailable planning fails closed with `noFakeSuccess`, configured proposal branch confirms through `/api/workstream/chat-tool-plans/confirm` when provider/runtime returns a proposal, and unproposed confirmation is denied without mutation.

## Next step

No next runnable task for this mini-project.
