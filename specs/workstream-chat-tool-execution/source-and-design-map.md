# Source and design map: confirmed workstream chat tool execution

Task: `TASK-WCTE-01-001`  
Scope: design/specification only; no runtime feature code was implemented.

## Executive summary

The current app already has three adjacent paths that must remain distinct:

1. **Deterministic surface routing first**: `WorkstreamService.submitMessage(...)` calls `SurfaceIntentRouter.route(...)` before invoking the model-backed agent. `DefaultSurfaceIntentRouter` only opens or prepopulates structured surfaces and explicitly rejects compound/high-risk prompts. This path must continue to run before chat tool planning and must never mutate state.
2. **General model-backed markdown response**: prompts not routed to a deterministic surface invoke `WorkstreamAgentRuntimeInvoker`, which in production calls `WorkstreamRuntimeAgent.respond(...)` through `DefaultWorkstreamAgentRuntimeInvoker` after `AgentRuntimeService` prepares prompt/model/tool-boundary trace context. This currently returns only `markdown_response` or fail-closed `system_message` surfaces.
3. **Governed surface/browser actions**: `WorkstreamService.runAction(...)` executes existing `SurfaceAction` records by `actionId`, `browserToolId`, `governedToolId`, `capabilityId`, selected `AuthContext`, idempotency, and action-specific service paths.

Confirmed chat tool execution should be inserted after deterministic routing and before ordinary markdown fallback for prompts that require governed multi-step execution. Initial implementation should produce a plan/confirmation surface with no mutation, then execute confirmed plan steps through the same governed action/service paths already used by structured surfaces.

## Current source map

### Backend files

| File | Current responsibility | Chat tool execution design implication |
|---|---|---|
| `src/main/java/ai/first/api/coreapp/workstream/WorkstreamEndpoint.java` | Protected browser/API edge for `/api/workstream/bootstrap`, `/items`, `/surfaces/{surfaceId}`, `/actions`, `/shell-requests`, `/messages`, and SSE events. | Add plan confirmation/execute API here or route confirmation through typed capability action requests only after adding plan-bound DTOs. Preserve JWT, selected context header, correlation id, and fail-closed `AuthorizationException` mapping. |
| `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java` | Main workstream adapter. Owns `submitMessage`, `runAction`, surface construction, `actionById`, idempotency for current actions, and current action-to-service dispatch. | Add shared plan records/surfaces near current `WorkstreamMessageRequest/Response`, route order `surfaceIntentRouter` -> `human_chat_tool_plan` proposal -> markdown fallback, and dispatcher entry points that re-use `actionById`, capability checks, idempotency, and per-step execution semantics. |
| `src/main/java/ai/first/application/coreapp/workstream/SurfaceIntentRouter.java` | Side-effect-free interface. Requires `noMutation=true`; cannot call providers or submit actions. | Keep as first-pass route. Do not add tool execution here. If a prompt is high confidence for opening/prefilling a surface, it must return a no-mutation surface route instead of a chat plan. |
| `src/main/java/ai/first/application/coreapp/workstream/DefaultSurfaceIntentRouter.java` | Current deterministic routes for My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy. Compound/high-risk prompts return empty. | The motivating prompt `create org "Org 1", and invite ...` remains unrouted because compound prompts return empty; this is the intended handoff to chat tool planning. Single prompt `create organization "Org 1"` still opens the prefilled create surface with no mutation. |
| `src/main/java/ai/first/application/foundation/agent/WorkstreamAgentRuntimeInvoker.java` | Production seam for model-backed workstream execution. | Extend or add a sibling method for structured `human_chat_tool_plan` proposal so normal runtime still goes through governed Akka Agent preparation and fail-closed behavior. |
| `src/main/java/ai/first/application/foundation/agent/DefaultWorkstreamAgentRuntimeInvoker.java` | Production invoker that calls `WorkstreamRuntimeAgent.respond(...)` through Akka `ComponentClient`. | Add structured plan proposal invocation here after `AgentRuntimeService` preparation; do not fabricate successful planning without configured runtime/provider. |
| `src/main/java/ai/first/application/foundation/agent/WorkstreamRuntimeAgent.java` | Akka Agent component returning `MarkdownResponse`; resolves runtime tools from `AgentRuntimeToolResolver` before model invocation. | Add a structured response method such as `proposeChatToolPlan(GovernedWorkstreamPlanRequest)` returning `ChatToolPlanProposalResponse`. System prompt must say the model proposes only catalog-bound steps; it cannot authorize or execute. |
| `src/main/java/ai/first/application/foundation/agent/AgentRuntimeService.java` | Governs prompt assembly, active agent/profile/model/tool-boundary lookup, loader trace events, model invocation trace, and fail-closed behavior. | Add plan proposal preparation/completion/failure traces or a request mode (`human_chat_tool_plan`) so traces distinguish proposal from markdown response. |
| `src/main/java/ai/first/application/foundation/agent/AgentRuntimeToolResolver.java` and `ToolRegistry.java` | Resolve active `ToolPermissionBoundary` grants to runtime tools. Currently starter runtime tools are read/evidence/loader tools. | Planning may use read/evidence tools, but execution must not happen through model tool calls. Add dispatcher/catalog checks for executable governed tools separately from model runtime tools. |
| `src/main/java/ai/first/application/coreapp/useradmin/SaasOwnerOrganizationAdminService.java` | Existing Organization lifecycle service used by `runOrganizationLifecycleAction`. | User Admin plan execution should reuse Organization create path for the motivating example step 1. |
| `src/main/java/ai/first/application/foundation/invitation/**` | Existing invitation service/view/outbox/provider boundary. | User Admin plan execution should reuse `InvitationService.createInvitation(...)` via existing action/service dispatch for invitation step 2. |

### Frontend files

| File | Current responsibility | Chat tool execution design implication |
|---|---|---|
| `frontend/src/workstream/types/actions.ts` | Browser action DTOs: `SurfaceAction`, `CapabilityActionRequest`, `CapabilityActionResult`, `WorkstreamShellRequest/Response`. | Add plan-specific request/result DTOs or import them from `frontend/src/api/WorkstreamApiClient.ts`; preserve `selectedContextId`, `correlationId`, idempotency, tool ids, and result surfaces. |
| `frontend/src/workstream/types/workstream.ts` | `WorkstreamItem`, `ComposerRequest`, selection types. | Add item kinds such as `chat_tool_plan_proposal`, `chat_tool_plan_result`, or continue using `surface`/`decision` with typed surface contracts. Waiting-for-human status should be used for unconfirmed plans. |
| `frontend/src/workstream/types/surfaces.ts` | `SurfaceEnvelope` and typed surface data unions for existing surfaces. | Add `ChatToolPlanProposalSurfaceData`, `ChatToolPlanResultSurfaceData`, and canonical surface types `chat_tool_plan_proposal`, `chat_tool_plan_confirmation`, `chat_tool_plan_result`, `chat_tool_plan_system_message` or map these to existing `decision-card`/`workflow-status` with explicit `surfaceContract`. |
| `frontend/src/workstream/composer/WorkstreamComposer.tsx` | Persistent prompt composer. | No product logic should be added here beyond rendering submit state/copy. Chat tool planning is backend-owned after composer submission. |
| `frontend/src/workstream/shell/WorkstreamShell.tsx` | Shell/rail/composer composition. | Preserve one selected functional agent and selected `AuthContext`; plan confirmation UI belongs in surfaces, not shell heuristics. |
| `frontend/src/workstream/stream/WorkstreamStream.tsx` and `WorkstreamItem.tsx` | Renders workstream items and inline structured surfaces. | Render plan proposal/result items inline by passing typed surfaces to `SurfaceRenderer`; waiting-for-human plan entries should not auto-submit. |
| `frontend/src/workstream/surfaces/SurfaceRenderer.tsx` | Dispatches surface types to surface components. | Add `ChatToolPlanSurface.tsx` or dispatch `chat_tool_plan_*` to a new component; system messages still go to `SystemMessageSurface`. |
| `frontend/src/workstream/surfaces/DecisionSurface.tsx`, `WorkflowStatusSurface.tsx`, `SystemMessageSurface.tsx` | Existing decision/progress/recovery surfaces. | Reuse patterns for evidence, required human reason, no direct mutation, trace links, and recovery states in new plan surfaces. |
| `frontend/src/workstream/actions/CapabilityActionButton.tsx`, `CapabilityActionPanel.tsx`, `capabilityActionState.ts` | Existing surface action request builder and result mapping. | Do not use generic `globalThis.confirm` as the final plan confirmation UX. Plan confirmation must be plan-bound, accessible, explicit, and submit a server-validated plan snapshot/idempotency key. |
| `frontend/src/api/WorkstreamApiClient.ts` and `HttpWorkstreamApiClient.ts` | Browser API client for workstream messages/actions/shell requests. | Add `confirmChatToolPlan(...)` or equivalent typed client method if confirmation is not represented as a normal `CapabilityActionRequest`. |
| `frontend/src/main.tsx` | Calls `submitWorkstreamMessage`, handles shell-request shortcuts, appends backend `userItem`, `agentItem`, and `surface`. | Existing composer flow can accept a plan proposal surface from `/api/workstream/messages`; add confirmation result handling without client-side model/tool heuristics. |

### App-description files

| File or directory | Current responsibility | Required follow-up in `TASK-WCTE-02-001` |
|---|---|---|
| `app-description/domains/core-starter/workstreams/surface-catalog.md` | Describes deterministic no-mutation surface intent routing and surface catalog. | Add `human_chat_tool_plan` as a separate adapter path and explicitly preserve the router-first rule. |
| `app-description/domains/core-starter/workstreams/*/tools/governed-tools.md` | Per-workstream governed tool inventory. | Add first-pass chat executable catalog entries and mark exposure channel `human_chat_tool_plan` only where bounded and confirmed. |
| `app-description/domains/core-starter/workstreams/*/workstream.md` | Workstream responsibility and capability binding. | Add selected workstream agent ownership of plan proposal, retained human confirmation authority, and non-autonomous execution boundary. |
| `app-description/domains/core-starter/workstreams/*/agents/functional-agent.md` | Functional agent behavior and boundaries. | Clarify agents may propose plans using governed catalogs but cannot authorize/execute mutations. |
| `app-description/domains/core-starter/workstreams/*/surfaces/surfaces.md` | Structured surface contracts. | Add plan proposal/confirmation/result/system-message surfaces or references to shared global plan surfaces. |
| `app-description/domains/core-starter/workstreams/*/traces/work-traces.md` | Work/audit trace obligations. | Add `human_chat_tool_plan` trace fields and per-step execution outcome fields. |
| `app-description/domains/core-starter/workstreams/*/tests/coverage.md` | Workstream-specific validation expectations. | Add no-mutation-before-confirmation, confirmation, denial, idempotency, partial failure, and trace expectations. |
| `app-description/domains/core-starter/workstreams/*/realization/api-contracts.md`, `frontend-routes.md`, `akka-components.md` | Realization traceability. | Link new DTOs/API/surfaces/tests to the existing workstream shell and governed backend paths. |

## First-pass representative tool-plan paths

These are initial design targets for the shared catalog. They are intentionally narrow and should be treated as confirmed human-chat plan exposures, not autonomous agent permissions.

| Workstream | Representative prompt | Plan step(s) | Existing action ids | Governed tool id(s) | Capability id(s) | Input schema / key inputs | Result surfaces |
|---|---|---|---|---|---|---|---|
| User Admin | `create org "Org 1", and invite mckee.hugh@gmail.com as an org admin` | 1. Create Organization. 2. Invite Organization Admin for created Organization. | `action-submit-organization-create`; `action-submit-organization-admin-invitation` | `manage-organizations`; `manage-organization-admins` | `saas_owner.tenant.manage`; `saas_owner.organization_admin.invite` | `schema.organization-admin.create.submit.v1` with `organizationName`, `reason`; `schema.organization-admin.invitation-create.v1` with `organizationId` from step 1, `email`, `displayName`, `roles=[TENANT_ADMIN]`, `reason` | `surface-user-admin-organization-detail`; `surface-user-admin-invitation-detail` |
| My Account | `change my theme to Obsidian Dark` | Update own settings after explicit confirmation. | `action-update-my-settings` | `my_account.update_profile_settings` | `my_account.update_profile_settings` | `schema.my-account.settings.update.v1` with `preferredThemeId=obsidian-dark`; idempotency from plan/selected surface item | `surface-my-settings` |
| Agent Admin | `start prompt risk review for the Agent Admin prompt proposal` | Start prompt-risk review task when target proposal is backend-visible; fail closed if provider/runtime blocked. | `action-agent-prompt-risk-review-start` | `agent_admin.start_behavior_review_task` (current code uses `AgentAdminPromptRiskReviewService.START_CAPABILITY`) | `agent_admin.start_behavior_review_task` | `schema.agent-admin.prompt-risk-review.start.v1` with `agentDefinitionId`, `proposalId`, redacted `artifactDeltas`, idempotency key | `surface-agent-admin-prompt-risk-review` |
| Audit/Trace | `append investigation note "provider blocked; retry after config" to this trace` | Append browser-safe investigation note to visible trace/correlation only. | `action-audit-trace-append-investigation-note` | `draft-investigation-note` | `audit.trace.investigation_note.append` | `schema.audit-trace.investigation-note.v1` with visible `traceId`/`correlationId`, `noteText`, idempotency key | `surface-audit-trace-investigation-note` |
| Governance/Policy | `draft a policy proposal to require approval before redacted exports` | Create inert policy proposal draft; no activation/approval. | `action-governance-policy-draft-proposal` | `governance.policy.propose` | `governance.policy.propose` | `schema.governance-policy.proposal.draft.v1` with `title`, `rationale`, browser-safe proposed change summary, idempotency key | `surface-governance-policy-proposal` |

High-impact actions such as policy activation/rollback, managed-agent activation/rollback, account disabling, role grants, trace export delivery, and support-access grants should remain blocked or approval-gated until later tasks model their exact confirmation and prerequisite checks.

## Seed and traceability update

`TASK-WCTE-11-001` aligns governed starter behavior seeds with the implemented first-pass runtime path without adding new runtime behavior:

| Workstream agent | Seed prompt/skill/reference expectation | Runtime traceability ids |
|---|---|---|
| My Account | Explain deterministic no-mutation settings/profile surface routing before confirmed chat execution; describe only the bounded theme-setting path. | `action-update-my-settings`, `my_account.update_profile_settings`, `schema.my-account.settings.update.v1`, `surface-my-settings`, `human_chat_tool_plan.*` trace events. |
| User Admin | Explain router-first Organization/invitation surfaces versus a confirmed two-step chat plan with Organization id binding. | `action-submit-organization-create`, `manage-organizations`, `saas_owner.tenant.manage`, `surface-user-admin-organization-detail`; `action-submit-organization-admin-invitation`, `manage-organization-admins`, `saas_owner.organization_admin.invite`, `surface-user-admin-invitation-detail`. |
| Agent Admin | Explain that prompt-risk review start is proposal/confirmation only and remains approval-gated. | `action-agent-prompt-risk-review-start`, `agent_admin.start_behavior_review_task`, `schema.agent-admin.prompt-risk-review.start.v1`, `surface-agent-admin-prompt-risk-review`, approval-required step trace. |
| Audit/Trace | Explain note append as a browser-safe confirmed plan and distinguish it from evidence search/export/redaction. | `action-audit-trace-append-investigation-note`, `draft-investigation-note`, `audit.trace.investigation_note.append`, `schema.audit-trace.investigation-note.v1`, `surface-audit-trace-investigation-note`. |
| Governance/Policy | Explain inert policy proposal drafts only; approvals, activation, rollback, exports, and live authority changes remain separate gates. | `action-governance-policy-draft-proposal`, `governance.policy.propose`, `schema.governance-policy.proposal.draft.v1`, `surface-governance-policy-proposal`, policy proposal trace. |

Across all five agents, seed text must state that deterministic surface routing only opens or prefills surfaces, `human_chat_tool_plan` proposals are no-mutation, execution requires exact plan snapshot confirmation, and backend authorization/idempotency/trace checks remain authoritative.

## Initial backend contract to add

### Plan proposal request/response records

Add Java records under `WorkstreamService` first for the shared prototype, then extract if they grow:

- `ChatToolPlanProposalRequest(selectedContextId, functionalAgentId, prompt, correlationId, idempotencyKey, attachedSurfaceId)` or reuse `WorkstreamMessageRequest` with backend-selected `responseKind=chat_tool_plan_proposal`.
- `ChatToolPlanProposal(planId, planSnapshotId, status, selectedContextId, functionalAgentId, requestedByAccountId, requestedAt, sourcePrompt, summary, steps, requiredCapabilities, approvalSummary, idempotencyRoot, traceIds, expiresAt, noMutation=true)`.
- `ChatToolPlanStep(stepId, sequence, label, actionId, browserToolId, governedToolId, capabilityId, inputSchemaRef, input, dependsOnStepIds, outputBindings, idempotencyKey, transactionBoundary, requiresConfirmation, requiresApproval, expectedResultSurfaceType, expectedResultSurfaceId, traceRequirements)`.
- `ChatToolPlanConfirmationRequest(selectedContextId, planId, planSnapshotId, confirmationText, idempotencyKey, correlationId)`.
- `ChatToolPlanExecutionResult(planId, planSnapshotId, status, completedSteps, failedSteps, skippedSteps, recoverySteps, resultSurfaceIds, traceIds, correlationId)`.
- `ChatToolPlanStepResult(stepId, status, message, actionId, governedToolId, capabilityId, resultSurfaceId, traceIds, startedAt, completedAt, errorCode)`.

### Surface contracts

Initial surface contracts:

- `chat_tool_plan.proposal.v1`: rendered as `surfaceType=chat_tool_plan_proposal` or `decision-card`; status `waiting-for-human`; shows proposed steps, inputs, side effects, capabilities, idempotency, approval gates, no-mutation notice, and trace ids.
- `chat_tool_plan.confirmation.v1`: plan-bound confirmation state. Confirmation must include `planId`, `planSnapshotId`, selected `AuthContext`, `requestedBy`, step hashes, and user acknowledgement.
- `chat_tool_plan.result.v1`: final/partial result with step statuses `completed`, `failed`, `skipped`, `recovery_available`; includes committed result surfaces and recovery actions.
- `chat_tool_plan.system_message.v1`: safe denial, plan unavailable, provider/runtime missing, out-of-catalog step, stale/expired plan, confirmation mismatch, or capability denial.

Suggested surface ids are deterministic and plan-scoped: `surface-chat-tool-plan-{planId}`, `surface-chat-tool-plan-result-{planId}`, and `surface-chat-tool-plan-blocked-{stableSuffix(correlationId)}`.

## Runtime and dispatcher sequence

1. Resolve selected `AuthContext` and visible functional agent exactly as `submitMessage` does today.
2. Check `workstreamLogRepository.findByIdempotencyKey(...)` before creating duplicate proposals.
3. Run `surfaceIntentRouter.route(...)` first. If present, return deterministic no-mutation routed surface and stop.
4. Classify the prompt as a candidate for `human_chat_tool_plan` only when it is execution-oriented and the selected workstream has a catalog entry for all intended operations. Otherwise continue to current model-backed markdown path.
5. For candidate plan prompts, invoke governed model planning through the Akka Agent runtime path with mode/capability distinguishing `human_chat_tool_plan`. Provider/runtime/tool-boundary failure returns `chat_tool_plan.system_message.v1`, not fake success.
6. Validate every proposed step against the backend-owned chat tool catalog: selected workstream, action id, governed tool id, capability id, input schema, dependency/output binding, idempotency rule, confirmation/approval policy, and trace requirements.
7. Persist a proposal log entry with no mutations and status `waiting-for-human`.
8. On confirmation, validate exact `planId + planSnapshotId + selectedContextId + requestedBy + confirmedBy + step hashes`. Reject modified, stale, expired, cross-context, or out-of-catalog plans.
9. Execute each confirmed step as an independent transaction boundary through existing action/service paths where possible. Reauthorize and revalidate selected `AuthContext`, capability, idempotency, and tool policy on every step.
10. Stop dependent steps after failure; report completed, failed, skipped, and recovery guidance without rolling back successful committed steps unless a tool explicitly has a compensating action in its catalog.

## Required trace fields

Every proposal, confirmation, denial, and step result should emit durable work/audit trace facts with these normalized fields:

- `traceEventId`, `workTraceId`, `correlationId`, `causationId`/`parentEventId`.
- `eventCategory`: `agent`, `tool`, `authorization`, `decision`, `workflow`, or `system`.
- `eventType`: `human_chat_tool_plan.proposed`, `human_chat_tool_plan.confirmed`, `human_chat_tool_plan.step_started`, `human_chat_tool_plan.step_completed`, `human_chat_tool_plan.step_failed`, `human_chat_tool_plan.step_skipped`, `human_chat_tool_plan.denied`, `human_chat_tool_plan.provider_blocked`.
- `tenantId`, optional `customerId`, selected `AuthContext.selectedContextId`, `membershipId`.
- `functionalAgentId`, `agentDefinitionId`, `agentDefinitionVersion`/checksum when available.
- `actorType=human`, `requestedByAccountId`, `confirmedByAccountId`; for model planning also `actorType=agent` facts for proposal generation.
- `capabilityId`, `governedToolId`, `browserToolId`, `actionId`, `inputSchemaRef`.
- `planId`, `planSnapshotId`, `stepId`, `stepSequence`, `stepDependencyIds`, `idempotencyKey` or redacted idempotency hash.
- `authorizationDecision`, `authorizationBasisSummary`, `policyRefs`, `approvalRefs`, `guardrailRefs`.
- `promptDocumentId/version`, `skillManifestId/version`, `referenceManifestId/version`, `modelConfigRef`, `toolBoundaryId/version` for plan proposal.
- `inputSummary`, `outputSummary`, `resultSurfaceId`, `status`, `safeErrorCode`, `redactionClassification`.

Do not store raw provider secrets, invitation tokens, JWTs, raw email bodies, raw provider payloads, or hidden tenant/customer identifiers in browser-visible trace summaries.

## Tests to add in later tasks

Backend:

- `src/test/java/ai/first/application/coreapp/workstream/WorkstreamServiceTest.java`: router-first behavior, no mutation before plan confirmation, duplicate idempotency returns same proposal, stale/mismatched confirmation denied, out-of-catalog step denied, partial failure reports completed/failed/skipped/recovery.
- `src/test/java/ai/first/application/coreapp/workstream/UserAdminBrowserWorkstreamSmokeTest.java`: User Admin motivating example plan proposal and confirmed execution path.
- `src/test/java/ai/first/application/foundation/agent/WorkstreamRuntimeAgentTest.java`: structured plan proposal uses governed Akka Agent path, tool boundary denials fail closed, prompt/skill/reference text cannot expand authority.
- Add focused tests for dispatcher reauthorization and tenant/customer isolation around Organization create + Organization Admin invitation.

Frontend:

- `frontend/src/workstream-chat-tool-plan.contract.test.mjs`: DTOs/surface types, plan confirmation component, no auto-submit, plan-bound confirmation inputs, safe denial/result rendering.
- Extend `frontend/src/workstream-composer-message-api.contract.test.mjs`: `/api/workstream/messages` can return a plan proposal surface while shell shortcuts and deterministic backend routing remain first.
- Extend `frontend/src/workstream-actions.contract.test.mjs`: confirmation call preserves selected context, correlation id, plan snapshot id, idempotency, and does not use generic `globalThis.confirm` for plan execution.
- Extend vertical tests: `workstream-user-admin-vertical.contract.test.mjs`, `workstream-my-account-vertical.contract.test.mjs`, `workstream-agent-admin-vertical.contract.test.mjs`, `workstream-audit-trace-vertical.contract.test.mjs`, and `workstream-governance-policy-vertical.contract.test.mjs` for one representative plan path each.

App-description/spec:

- `TASK-WCTE-02-001` should update all five `tools/governed-tools.md`, `surfaces/surfaces.md`, `traces/work-traces.md`, and `tests/coverage.md` files to include `human_chat_tool_plan` and the first-pass tool ids above.

## Open implementation risks for later tasks

- The current Akka runtime method returns only `MarkdownResponse`; structured plan output requires a new model response schema and tests.
- `ToolRegistry` currently registers evidence/loader tools for model runtime, not executable mutation tools; that is acceptable for planning, but execution needs a backend dispatcher/catalog outside model tool invocation.
- Existing action idempotency is partly in-memory (`idempotentActionResults`) for some actions; multi-step plan execution should use durable plan/step idempotency before claiming runtime-ready behavior.
- The User Admin example step 2 depends on the Organization id from step 1; the plan schema must support output bindings and must not let the model invent tenant ids.
- Frontend `CapabilityActionButton` uses `globalThis.confirm` for action confirmation. Plan confirmation requires a richer accessible surface and server-side exact snapshot validation.
