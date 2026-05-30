# My Account Implementation Map

## Discovery commands used

```bash
find templates/ai-first-saas-starter -path '*/node_modules' -prune -o -type f -print | sort | rg -n "(MyAccount|my-account|my_account|/api/me|Me|AuthContext|selected context|authority|profile|settings|attention|user tile|UserTile|Trace|trace|Workstream|workstream|AgentRuntime|ToolPermissionBoundary|frontend|surface|Surface|test|api)"
rg -n "My Account|MyAccountAgent|my_account|my-account|/api/me|selected context|authority|profile|settings|personal attention|user tile|trace refs|open_authorized_workstream|ToolPermissionBoundary|provider|system_message|tenant|no duplicate top-rail|blocked_provider_or_runtime|MY_ACCOUNT|meResponse|MeResponse" templates/ai-first-saas-starter --glob '!**/node_modules/**'
rg -n "MY_ACCOUNT|my_account|agent-my-account|surface-my|action-update-my|open_authorized|personal|attention|bootstrap\(|submitMessage|dynamicSurface|profile|settings" templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamService.java
```

## Current source state

The starter already has a useful v0 My Account path, but it is not yet the full-core SMB control center.

Implemented foundations:

- `GET /api/me` is implemented by `MeEndpoint`, `MeService`, `AuthContextResolver`, and `MeResponse`.
- `/api/me` resolves WorkOS/AuthKit JWT claims to a local account, selected active membership/AuthContext, profile, settings, visible capabilities, available contexts, and functional-agent summaries.
- Disabled accounts, missing active memberships, selected-membership forgery, and tenant/customer mismatches fail closed through `AuthorizationException` and audit events.
- The workstream rail removes `agent-my-account` from the top rail and opens it only from the lower-left user tile/email in `FunctionalAgentRail.tsx`.
- `WorkstreamService` exposes initial My Account markdown, dynamic `surface-my-account-dashboard`, `surface-my-profile`, and `surface-my-settings`, plus self-service profile/settings update actions.
- Profile/settings updates are deterministic, capability-checked, idempotency-keyed at the action layer, no-op aware, and audited through `AuthContextResolver.updateOwnProfileSettings`.
- Request/response MyAccountAgent already uses the governed managed-agent runtime path via `WorkstreamService.submitMessage`, `WorkstreamAgentRuntimeInvoker`, `AgentRuntimeService`, and `agent-my-account` seeds.
- My Account seed prompt/skill/reference files exist under `backend/src/main/resources/agent-behavior-seeds/starter-v1/`.
- Frontend surface renderers already support dashboards, detail/edit forms, actions, trace links, `system_message`, and action idempotency/advisory-control copy.

Material gaps for SMB full-core:

1. My Account deterministic behavior is embedded in `WorkstreamService`/`AuthContextResolver`; there is no focused `MyAccountService` boundary for summary/context/profile/settings/attention/navigation/trace DTOs and tests.
2. `/api/me` returns minimal profile/settings/authority data and does not expose explicit authority-basis summaries, context capability grouping, personal attention counts, trace refs, or browser-safe recovery states.
3. `dynamicSurface` does not include `surface-my-account-dashboard`, `surface-my-profile`, or `surface-my-settings`; those surfaces are action-returned but not independently retrievable through `/api/workstream/surfaces/{surfaceId}`.
4. `my_account.view_context`, `my_account.list_personal_attention`, and `my_account.view_own_trace_refs` are not first-class constants/action mappings in the backend. Current fixtures use `my_account.list_next_steps`, which should be aligned to `my_account.list_personal_attention` or preserved only as an alias.
5. `open_authorized_workstream` currently records a trace and then falls through to generic action success/result shaping. It should authorize each target workstream server-side, return safe denials, and route open-workstream/open-attention-item requests through explicit backend semantics.
6. Personal attention aggregation is currently a next-step list derived from functional-agent visibility. It does not aggregate authorized sibling workstream attention items, access-review/provider blocks, policy approvals, trace failures, or hidden-workstream redactions.
7. Own trace refs are represented by static trace ids on surfaces/actions. There is no My Account-owned scoped trace-ref service/facade over Audit/Trace that returns only the signed-in user's browser-safe evidence.
8. MyAccountAgent can use loader tools, but no read-only `myAccountEvidence.read` facade exists. Model guidance cannot ask for scoped summary/context/attention/trace evidence through a named governed `ToolPermissionBoundary` grant.
9. Frontend fixtures and types do not yet include full-core My Account data for authority basis, personal attention, context switching states, no-op/idempotent settings results, provider-blocked agent guidance, and safe workstream navigation.
10. A personal digest worker remains unjustified until deterministic personal attention aggregation and trace refs exist; it should remain blocked/readiness-only in this mini-project unless a later verification explicitly appends a durable task slice.

## Vertical slice sequence

### Slice 1 — Deterministic MyAccountService, `/api/me`, context, profile/settings, and navigation foundations

Goal: make account/context/authority/profile/settings/open-workstream behavior backend-authoritative and independently testable.

Capabilities:

- `my_account.view_summary`
- `my_account.view_context`
- `my_account.switch_context` as a backend-selected-context contract, implemented through `X-Selected-Context-Id` first unless a dedicated switch action is added
- `my_account.update_profile_settings`
- `my_account.open_authorized_workstream`
- `my_account.view_own_trace_refs`

Deterministic responsibilities:

- authorize selected `AuthContext`, active membership, non-disabled account, tenant/customer scope, and exact My Account capability;
- shape `/api/me` and My Account dashboard DTOs with browser-safe account/profile/settings, selected context, available contexts, authority-basis summaries, visible/denied functional agents, and trace refs;
- validate and update only allowed self-service fields (`displayName`, `preferredColorMode` at current scope), detect no-op/idempotent updates, and audit validation/denial/no-op/success;
- add dynamic surface retrieval for `surface-my-account-dashboard`, `surface-my-profile`, and `surface-my-settings`;
- make `open_authorized_workstream` authorize the requested target workstream/surface/attention item and return `system_message` denials for hidden/forbidden targets;
- preserve lower-left user tile launch and no duplicate top-rail My Account entry.

Primary source paths:

- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/api/security/MeEndpoint.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/MeService.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/MeResponse.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/AuthContextResolver.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamService.java`
- new likely `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/MyAccountService.java`
- `templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/security/MeServiceTest.java`
- `templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamServiceTest.java`

### Slice 2 — Personal attention aggregation, own trace refs, and frontend My Account surfaces

Goal: make My Account the user's safe attention and evidence inbox without leaking hidden workstreams.

Capabilities:

- `my_account.list_personal_attention`
- `my_account.open_authorized_workstream`
- `my_account.view_own_trace_refs`

Deterministic responsibilities:

- aggregate only authorized sibling-workstream attention items from backend-derived dashboard/service evidence;
- include provider/runtime blocked states, pending approvals, failed invitations, access-review task blockers, audit/provider failures, governance decisions, and trace refs only when the current AuthContext can see the originating workstream/evidence;
- return redacted or omitted counts for hidden workstreams without naming them;
- route trace/detail links through Audit/Trace capabilities and non-enumerating not-found-or-redacted behavior;
- expose action descriptors for `open_workstream`, `open_attention_item`, and trace links that remain backend-authoritative.

Frontend responsibilities:

- update fixtures/types/renderers/contracts for My Account dashboard/profile/settings/attention/context/trace surfaces;
- prove lower-left user tile launch, no top-rail duplication, advisory controls, accessible trace links, responsive density, and safe denial/provider-blocked/no-op states.

Primary source paths:

- backend files from Slice 1 plus `AuditTraceService.java`, `WorkstreamLogRepository.java`, User Admin/Agent Admin/Governance service outputs where needed
- `templates/ai-first-saas-starter/frontend/src/workstream/fixtures/me.ts`
- `templates/ai-first-saas-starter/frontend/src/workstream/fixtures/agents.ts`
- `templates/ai-first-saas-starter/frontend/src/workstream/fixtures/surfaces.ts`
- `templates/ai-first-saas-starter/frontend/src/workstream/types/auth.ts`
- `templates/ai-first-saas-starter/frontend/src/workstream/types/surfaces.ts`
- `templates/ai-first-saas-starter/frontend/src/workstream/rail/FunctionalAgentRail.tsx`
- `templates/ai-first-saas-starter/frontend/src/workstream/shell/WorkstreamShell.tsx`
- `templates/ai-first-saas-starter/frontend/src/workstream/surfaces/DashboardSurface.tsx`
- `templates/ai-first-saas-starter/frontend/src/workstream/surfaces/DetailEditSurface.tsx`
- `templates/ai-first-saas-starter/frontend/src/workstream-actions.contract.test.mjs`
- `templates/ai-first-saas-starter/frontend/src/workstream-shell.contract.test.mjs`
- new likely `templates/ai-first-saas-starter/frontend/src/workstream-my-account-vertical.contract.test.mjs`

### Slice 3 — MyAccountAgent evidence tool, seed guidance, and provider fail-closed tests

Goal: make MyAccountAgent useful without granting authorization or mutation authority.

Capabilities/tools:

- `my_account.ask_agent`
- existing governed loader tools `readSkill(skillId)` and `readReferenceDoc(referenceId)`
- new read-only data tool candidate `myAccountEvidence.read`, capability `my_account.view_summary` or narrower `my_account.read_evidence`

Model-backed responsibilities:

- explain account/context/authority, summarize personal attention, guide safe profile/settings changes, explain denials/no-ops/provider blocks, and route to authorized workstreams;
- use concrete `WorkstreamRuntimeAgent` through `AgentRuntimeService` and configured provider/model boundaries;
- call scoped evidence and loader tools through the governed runtime/tool-boundary path;
- fail closed with typed `system_message` and trace ids when provider/model/tool-boundary config is absent;
- never claim it changed context/profile/settings/roles/policies/agent behavior or opened hidden workstreams.

Deterministic responsibilities:

- implement `MyAccountEvidenceTools` as a request-scoped read-only facade over `MyAccountService`;
- register the tool in `ToolRegistry` and grant it in `tool-boundary-my-account` only as read-only/data lookup;
- enforce AuthContext, selected tenant/customer, capability, active MyAccountAgent definition, `ToolPermissionBoundary`, redaction, and non-enumerating denials before returning evidence;
- update My Account seed prompt/skill/reference to describe full-core SMB responsibilities and no-direct-mutation boundaries;
- test provider fail-closed behavior and no deterministic/model-less normal guidance.

Primary source paths:

- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/ToolRegistry.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentRuntimeToolResolver.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentRuntimeService.java`
- new likely `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/MyAccountEvidenceTools.java`
- seed resources under `templates/ai-first-saas-starter/backend/src/main/resources/agent-behavior-seeds/starter-v1/`
- tests under `templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/` and `WorkstreamServiceTest.java`

### Slice 4 — Personal digest worker readiness decision

Do not implement a successful model-backed personal digest worker until deterministic personal attention and trace refs are complete and a real durable worker runtime is intentionally selected.

Acceptable near-term outcome:

- expose only a typed blocked/readiness surface for `my_account.personal_digest.*`, or defer entirely with a queue note;
- if later implemented, worker output can summarize authorized evidence only and must not update settings, switch context, open workstreams, grant authority, mutate policies, or bypass deterministic attention filtering.

## Target validation commands for implementation tasks

```bash
cd templates/ai-first-saas-starter/backend && mvn test -Dtest=MeServiceTest,WorkstreamServiceTest,AdminEndpointIntegrationTest
cd templates/ai-first-saas-starter/backend && mvn test -Dtest=AgentBehaviorSeedLoaderTest,AgentRuntimeServiceTest,AgentRuntimeToolResolverTest,WorkstreamRuntimeAgentTest
cd templates/ai-first-saas-starter/frontend && npm test -- --runTestsByPath src/workstream-my-account-vertical.contract.test.mjs src/workstream-shell.contract.test.mjs src/workstream-actions.contract.test.mjs src/workstream-surfaces.contract.test.mjs src/workstream-composer-message-api.contract.test.mjs src/api.contract.test.mjs
rg -n "My Account|MyAccountAgent|my_account|/api/me|selected context|authority|profile|settings|personal attention|user tile|trace refs|open_authorized_workstream|myAccountEvidence\.read|ToolPermissionBoundary|provider|system_message|tenant|no duplicate top-rail|blocked_provider_or_runtime" templates/ai-first-saas-starter --glob '!**/node_modules/**'
git diff --check
```

Before the mini-project is complete, run broad validation or record a concrete blocker:

```bash
tools/validate-ai-first-saas-starter-fullstack.sh
```

## Appended implementation tasks

- `TASK-FCSMB-MA-01-002`: implement deterministic backend My Account service, `/api/me` authority/context DTOs, profile/settings lifecycle, dynamic surfaces, and safe open-workstream authorization.
- `TASK-FCSMB-MA-01-003`: implement personal attention aggregation, own trace refs, frontend My Account surfaces/fixtures/actions, and contract tests.
- `TASK-FCSMB-MA-01-004`: implement MyAccountAgent evidence tool, seed/tool-boundary updates, provider fail-closed tests, and no-secret checks.
- `TASK-FCSMB-MA-01-005`: decide and implement only the bounded personal digest blocked/readiness path justified by completed deterministic foundations.
- `TASK-FCSMB-MA-01-006`: run integrated My Account validation and close or append blockers.

These tasks keep My Account SMB self-service scoped, preserve deterministic ownership of authorization, context, validation, attention filtering, navigation, redaction, idempotency, and traces, and reserve model-backed behavior for governed explanation/guidance or a later explicitly justified durable worker.
