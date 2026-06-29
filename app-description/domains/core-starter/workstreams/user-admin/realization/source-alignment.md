# User Admin source alignment

Lifecycle: ../lifecycle.md
Last reviewed: 2026-06-29
Alignment state: partially-aligned

This file was added during the initial source-alignment migration and updated during the current app-description implementation-alignment mini-project. The User Admin current-intent graph was refreshed without corresponding runtime execution evidence, so this review does not claim `manual-ready` or `runtime-ready`. TASK-ADIA-02-002 maps the refreshed graph to existing source, test, frontend, and runtime-validation scaffold evidence and marks the posture `partially-aligned` at source-evidence level only.

## TASK-ADIA-02-002 graph coverage proof

| Graph edge | Refreshed source coverage | Runtime-validation reference |
|---|---|---|
| Worker -> execution harness -> actor adapter | `../workers/**`, `../workstream.md`, `../access.md`, and `../behavior.md` define SaaS Owner Admin, Organization/Tenant Admin, Customer Admin, functional-agent, access-review, invitation/onboarding, and audit/projection workers using `surface_action`, `human_chat_tool_plan`, `agent_tool_call`, protected `api_call`, workflow/internal, consumer, and timer adapters. | `RV-USER-ADMIN-001` covers the first invitation runtime-validation path at authored-not-run status. |
| Actor adapter -> governed tool -> capability | `../tools/governed-tools.md`, `../policies/policy-bindings.md`, `capability-compatibility.md`, and `../../../capabilities/user-and-access-administration.md` bind invitation, membership, role/status, support-access, Organization/Customer lifecycle, access-review, and admin-audit tools to selected `AuthContext`, tenant/customer scope, idempotency, approval, provider fail-closed, and trace obligations. | `RV-USER-ADMIN-001` names invitation governed-tool scope, idempotency, provider/outbox evidence, and admin/non-admin denial expectations. |
| Surface graph -> result/system-message surfaces | `../surfaces/surfaces.md`, `api-contracts.md`, and `frontend-routes.md` describe dashboard, user list/detail, invite, role/status/support, access-review, Organization, Customer, admin-audit, chat-plan, and system-message surfaces plus canonical action ids and retired-id rejection. | `RV-USER-ADMIN-001` exercises invite form/result and denial surfaces when run. |
| Realization -> tests/runtime-validation -> traces | `akka-components.md`, `api-contracts.md`, `frontend-routes.md`, `../tests/coverage.md`, and `../traces/work-traces.md` map protected endpoints, Akka services, frontend renderers, test files, admin-audit/work traces, and provider/model/outbox fail-closed obligations. | Runtime-validation scenario exists but has no run record. |

Because this proof updates app-description evidence only, existing automated tests are treated as source/test evidence rather than current runtime-ready evidence.

## TASK-ADIA-02-002 focused evidence review

Review date: 2026-06-29.
Review scope: User Admin dashboard, SaaS Owner/Organization/Customer admin branches, invitations/onboarding, user membership/role/status/support/identity flows, access-review agent, `human_chat_tool_plan`, frontend surfaces, admin-audit traces, and runtime-validation scaffold.
Review basis: required app-description reads, `specs/app-description-implementation-alignment/source-evidence-inventory.md`, mapped source/test/frontend file-existence proof, and `specs/runtime-validation/scenarios/user-admin/RV-USER-ADMIN-001-invite-user.md`. This pass did not run Maven/npm tests, start Akka, authenticate through WorkOS/AuthKit, configure Resend/OpenAI, or execute browser/runtime-validation scenarios.

Current posture: `partially-aligned`. Existing source/test/frontend evidence maps meaningful slices of the refreshed User Admin graph, but runtime readiness is not claimed because protected local Akka/API/UI paths, real WorkOS/AuthKit invitation acceptance, provider-backed delivery/model behavior, and runtime-validation run records remain unverified.

| Slice | Current evidence mapping | Evidence level | Remaining gap / exact follow-up |
| --- | --- | --- | --- |
| Dashboard, scoped user directory, and admin read model | `AdminEndpoint.java`, `UserAdminService.java`, `UserDirectoryView.java`, `WorkstreamService.java`, `DashboardSurface.tsx`, `ListSearchSurface.tsx`, `AdminEndpointIntegrationTest.java`, and frontend User Admin vertical contracts map to dashboard/list/detail source evidence. | `source-evidence-mapped`; no current runtime smoke. | Run protected API/UI runtime-validation for User Admin dashboard/list/detail, no-enumeration denials, pagination/redaction, and trace refs. |
| SaaS Owner Organization and Organization Admin branch | `SaasOwnerOrganizationAdminService.java`, `AdminEndpoint.java`, `WorkstreamService.java`, `UserAdminScopedAdminSurface.tsx`, `SaasOwnerOrganizationAdminServiceTest.java`, `AdminEndpointIntegrationTest.java`, and `workstream-organization-admin-vertical.contract.test.mjs` map to Organization lifecycle and Organization Admin invitation/manage evidence. | `source-evidence-mapped`; backend/API test evidence exists but runtime not re-run. | Runtime-smoke SaaS Owner selected-context create/rename/suspend/archive/reactivate and Organization Admin invitation/denial flows through the protected workstream/API path. |
| Tenant Customer and Customer Admin branch | `TenantCustomerAdminService.java`, identity Customer foundation state, `AdminEndpoint.java`, `WorkstreamService.java`, `UserAdminScopedAdminSurface.tsx`, `AdminEndpointIntegrationTest.java`, and frontend vertical contracts map to Customer lifecycle and Customer Admin invitation/manage evidence. | `source-evidence-mapped`; no current browser/runtime record. | Runtime-smoke Tenant Admin Customer lifecycle, Customer Admin invite/manage, sibling-customer denial, terminal archive/no-reactivate, and frontend branch rendering. |
| Invitation delivery, resend/revoke, and invitee acceptance/onboarding | `InvitationAcceptanceEndpoint.java`, foundation invitation/email services/views, `ResendEmailService.java`, `InvitationAndUserAdminServiceTest.java`, `RealResendProviderSmokeTest.java`, `UserAdminTaskSurface.tsx`, and `RV-USER-ADMIN-001` map to invitation lifecycle, provider/outbox, idempotency, and safe token redaction evidence. | `source-evidence-mapped`; runtime-validation scenario authored-not-run; live Resend is config-dependent. | Execute `RV-USER-ADMIN-001` with local WorkOS/AuthKit test identities or an approved local equivalent; run configured Resend smoke when secrets are available, otherwise keep fail-closed provider blocker explicit. |
| Membership status, roles, support access, and identity exceptions | `UserAdminService.java`, identity services/repositories, `UserAdminRoleChangePreviewSurface.tsx`, `UserAdminTaskSurface.tsx`, `AdminEndpointIntegrationTest.java`, and `InvitationAndUserAdminServiceTest.java` map to lifecycle confirmation, role preview/change, support-access, identity recovery, last-admin, self-action, and cross-scope denial evidence. | `source-evidence-mapped`; source/test evidence only for this review. | Add or run runtime-validation scenarios for role/status/support/identity slices, including last-admin guards, approval-required decision cards, idempotent no-ops, disabled actor, and trace evidence. |
| Access-review agent and advisory task lifecycle | `UserAdminAccessReviewService.java`, `UserAdminAccessReviewWorker.java`, `UserAdminAccessReviewAutonomousAgent.java`, durable access-review repository/entity/runtime ports, `UserAdminAccessReview*Test.java`, and `DurableAccessReviewTaskRepositoryEntityTest.java` map to advisory task lifecycle and no-direct-mutation evidence. | `source-evidence-mapped`; model-backed normal runtime provider success unverified. | Run provider-configured access-review runtime-validation or preserve `provider-config-blocker`; verify missing provider/model/profile/tool-boundary fail-closed behavior through runtime surfaces. |
| `human_chat_tool_plan` and structured frontend surfaces | `WorkstreamService.java`, `frontend/src/workstream-user-admin-vertical.contract.test.mjs`, `UserAdminScopedAdminSurface.tsx`, `UserAdminTaskSurface.tsx`, and `ChatToolPlanSurface.tsx` source evidence map representative Organization create + Organization Admin invite, scoped user invite, Customer Admin invite, resend, and canonical action ids. | `source-evidence-mapped`; frontend contract evidence only. | Runtime-smoke exact plan proposal/confirmation, stale/cross-context denial, partial failure, idempotent replay, no pre-confirm mutation, and no fake provider/model success. |
| Admin audit, work traces, denials, and secret boundaries | foundation audit/workstream trace services, `AdminEndpointIntegrationTest.java`, `InvitationAndUserAdminServiceTest.java`, frontend trace/audit components, `../traces/work-traces.md`, and frontend contract tests map to browser-safe trace refs, requestedBy/confirmedBy obligations, provider/model/outbox blockers, and secret/redaction boundaries. | `source-evidence-mapped`; current trace-link browser review unverified. | Runtime-validation must capture success, duplicate/no-op, denial, provider/model/outbox blocked, and trace-link reauthorization evidence without raw tokens, provider secrets, or hidden targets. |

Targeted proof command recorded for this review:

```bash
set -euo pipefail
paths=(
  src/main/java/ai/first/api/coreapp/admin/AdminEndpoint.java
  src/main/java/ai/first/api/foundation/invitation/InvitationAcceptanceEndpoint.java
  src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java
  src/main/java/ai/first/application/coreapp/useradmin/UserAdminService.java
  src/main/java/ai/first/application/coreapp/useradmin/SaasOwnerOrganizationAdminService.java
  src/main/java/ai/first/application/coreapp/useradmin/TenantCustomerAdminService.java
  src/main/java/ai/first/application/coreapp/useradmin/UserAdminAccessReviewService.java
  src/main/java/ai/first/application/coreapp/useradmin/UserAdminAccessReviewWorker.java
  src/main/java/ai/first/application/coreapp/useradmin/UserAdminAccessReviewAutonomousAgent.java
  src/main/java/ai/first/application/coreapp/useradmin/UserAdminEvidenceTools.java
  src/main/java/ai/first/application/coreapp/useradmin/UserDirectoryView.java
  src/main/java/ai/first/application/foundation/invitation/InvitationView.java
  src/main/java/ai/first/application/foundation/email/ResendEmailService.java
  frontend/src/workstream/surfaces/DashboardSurface.tsx
  frontend/src/workstream/surfaces/ListSearchSurface.tsx
  frontend/src/workstream/surfaces/UserAdminScopedAdminSurface.tsx
  frontend/src/workstream/surfaces/UserAdminTaskSurface.tsx
  frontend/src/workstream/surfaces/UserAdminRoleChangePreviewSurface.tsx
  frontend/src/workstream/surfaces/AuditTimelineSurface.tsx
  frontend/src/workstream/surfaces/SystemMessageSurface.tsx
  frontend/src/workstream-user-admin-vertical.contract.test.mjs
  frontend/src/workstream-organization-admin-vertical.contract.test.mjs
  frontend/src/workstream-user-admin-expertise.contract.test.mjs
  src/test/java/ai/first/application/coreapp/useradmin/AdminEndpointIntegrationTest.java
  src/test/java/ai/first/application/coreapp/useradmin/InvitationAndUserAdminServiceTest.java
  src/test/java/ai/first/application/coreapp/useradmin/SaasOwnerOrganizationAdminServiceTest.java
  src/test/java/ai/first/application/coreapp/useradmin/UserAdminAccessReviewServiceTest.java
  src/test/java/ai/first/application/coreapp/useradmin/UserAdminAccessReviewWorkerTest.java
  src/test/java/ai/first/application/coreapp/useradmin/UserAdminAccessReviewAutonomousAgentTest.java
  src/test/java/ai/first/application/coreapp/useradmin/DurableAccessReviewTaskRepositoryEntityTest.java
)
for path in "${paths[@]}"; do test -e "$path" || { echo "missing: $path"; exit 1; }; done
printf 'verified %s mapped User Admin implementation/test/frontend paths exist\n' "${#paths[@]}"
find specs/runtime-validation/scenarios/user-admin -type f | sort
```

Observed output: 30 mapped User Admin implementation/test/frontend paths exist; `specs/runtime-validation/scenarios/user-admin/RV-USER-ADMIN-001-invite-user.md` exists. This proof supports source-evidence mapping only; it is not runtime-ready evidence.

## Alignment entries

### `user-admin.dashboard-directory`

- App-description files: `../workstream.md`, `../access.md`, `../behavior.md`, `../surfaces/surfaces.md` dashboard/users/user-detail/admin-audit sections, `../tools/governed-tools.md`, `../tests/coverage.md`, `akka-components.md`, `api-contracts.md`, `frontend-routes.md`, `../../../capabilities/user-and-access-administration.md`.
- Implementation files: `src/main/java/ai/first/api/coreapp/admin/AdminEndpoint.java`, `src/main/java/ai/first/application/coreapp/useradmin/UserAdminService.java`, `src/main/java/ai/first/application/coreapp/useradmin/UserDirectoryView.java`, `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`, `frontend/src/workstream/surfaces/DashboardSurface.tsx`, `frontend/src/workstream/surfaces/ListSearchSurface.tsx`, `frontend/src/api/**`.
- Test / validation files: `src/test/java/ai/first/application/coreapp/useradmin/AdminEndpointIntegrationTest.java`, `frontend/src/workstream-user-admin-vertical.contract.test.mjs`, `frontend/src/workstream-surfaces.contract.test.mjs`, `specs/runtime-validation/scenarios/user-admin/RV-USER-ADMIN-001-invite-user.md`.
- Last aligned evidence: TASK-ADIA-02-002 source-evidence mapping.
- Remaining validation gaps: protected local API/UI runtime, denial/no-enumeration runtime captures, and trace-link browser review.

### `user-admin.organization-customer-admin-branches`

- App-description files: `../surfaces/surfaces.md` Organization and Customer branch sections, `../workers/saas-owner-admin-human.md`, `../workers/organization-admin-human.md`, `../workers/customer-admin-human.md`, `capability-compatibility.md`, `api-contracts.md`, `frontend-routes.md`, `../../../capabilities/user-and-access-administration.md`.
- Implementation files: `src/main/java/ai/first/application/coreapp/useradmin/SaasOwnerOrganizationAdminService.java`, `src/main/java/ai/first/application/coreapp/useradmin/TenantCustomerAdminService.java`, `src/main/java/ai/first/api/coreapp/admin/AdminEndpoint.java`, `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`, `frontend/src/workstream/surfaces/UserAdminScopedAdminSurface.tsx`.
- Test / validation files: `src/test/java/ai/first/application/coreapp/useradmin/SaasOwnerOrganizationAdminServiceTest.java`, `src/test/java/ai/first/application/coreapp/useradmin/AdminEndpointIntegrationTest.java`, `frontend/src/workstream-organization-admin-vertical.contract.test.mjs`, `frontend/src/workstream-user-admin-vertical.contract.test.mjs`.
- Last aligned evidence: TASK-ADIA-02-002 source-evidence mapping.
- Remaining validation gaps: current browser/runtime validation for selected contexts, Organization/Customer lifecycle branches, Customer Admin bootstrap, support-access boundaries, terminal archive behavior, and cross-scope denials.

### `user-admin.invitation-onboarding`

- App-description files: `../workers/invitation-onboarding-system-worker.md`, `../surfaces/surfaces.md` invitation sections, `../tools/governed-tools.md`, `../tests/coverage.md`, `api-contracts.md`, `frontend-routes.md`, `../../../capabilities/user-and-access-administration.md`.
- Implementation files: `src/main/java/ai/first/api/coreapp/admin/AdminEndpoint.java`, `src/main/java/ai/first/api/foundation/invitation/InvitationAcceptanceEndpoint.java`, `src/main/java/ai/first/application/foundation/invitation/**`, `src/main/java/ai/first/application/foundation/email/**`, `src/main/java/ai/first/application/foundation/identity/**`, `frontend/src/workstream/surfaces/UserAdminTaskSurface.tsx`, `frontend/src/api/**`.
- Test / validation files: `src/test/java/ai/first/application/coreapp/useradmin/InvitationAndUserAdminServiceTest.java`, `src/test/java/ai/first/application/coreapp/useradmin/RealResendProviderSmokeTest.java`, `src/test/java/ai/first/application/coreapp/useradmin/AdminEndpointIntegrationTest.java`, `frontend/src/workstream-user-admin-vertical.contract.test.mjs`, `specs/runtime-validation/scenarios/user-admin/RV-USER-ADMIN-001-invite-user.md`.
- Last aligned evidence: TASK-ADIA-02-002 source-evidence mapping.
- Remaining validation gaps: authored runtime-validation run, real WorkOS/AuthKit acceptance path, configured live Resend success or explicit fail-closed provider evidence, duplicate/no-op captures, and non-admin denial evidence.

### `user-admin.membership-role-support-identity`

- App-description files: `../access.md`, `../behavior.md`, `../surfaces/surfaces.md` membership/role/support/identity sections, `../policies/policy-bindings.md`, `../traces/work-traces.md`, `api-contracts.md`, `frontend-routes.md`, `../../../capabilities/user-and-access-administration.md`.
- Implementation files: `src/main/java/ai/first/application/coreapp/useradmin/UserAdminService.java`, `src/main/java/ai/first/application/foundation/identity/**`, `src/main/java/ai/first/api/coreapp/admin/AdminEndpoint.java`, `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`, `frontend/src/workstream/surfaces/UserAdminTaskSurface.tsx`, `frontend/src/workstream/surfaces/UserAdminRoleChangePreviewSurface.tsx`.
- Test / validation files: `src/test/java/ai/first/application/coreapp/useradmin/AdminEndpointIntegrationTest.java`, `src/test/java/ai/first/application/coreapp/useradmin/InvitationAndUserAdminServiceTest.java`, `frontend/src/workstream-user-admin-vertical.contract.test.mjs`.
- Last aligned evidence: TASK-ADIA-02-002 source-evidence mapping.
- Remaining validation gaps: runtime-validation scenarios for role/status/support/identity slices, last-admin guards, approval/decision cards, idempotent no-ops, disabled actors, and trace evidence.

### `user-admin.access-review-agent`

- App-description files: `../workers/access-review-agent-worker.md`, `../workers/user-admin-functional-agent-worker.md`, `../agents/functional-agent.md`, `../tools/governed-tools.md`, `../surfaces/surfaces.md` access-review sections, `../traces/work-traces.md`, `../tests/coverage.md`.
- Implementation files: `src/main/java/ai/first/application/coreapp/useradmin/UserAdminAccessReviewService.java`, `src/main/java/ai/first/application/coreapp/useradmin/UserAdminAccessReviewWorker.java`, `src/main/java/ai/first/application/coreapp/useradmin/UserAdminAccessReviewAutonomousAgent.java`, `src/main/java/ai/first/application/coreapp/useradmin/DurableAccessReviewTaskRepositoryEntity.java`, `src/main/java/ai/first/application/coreapp/useradmin/UserAdminEvidenceTools.java`, `frontend/src/workstream/surfaces/UserAdminTaskSurface.tsx`.
- Test / validation files: `src/test/java/ai/first/application/coreapp/useradmin/UserAdminAccessReviewServiceTest.java`, `src/test/java/ai/first/application/coreapp/useradmin/UserAdminAccessReviewWorkerTest.java`, `src/test/java/ai/first/application/coreapp/useradmin/UserAdminAccessReviewAutonomousAgentTest.java`, `src/test/java/ai/first/application/coreapp/useradmin/DurableAccessReviewTaskRepositoryEntityTest.java`, `frontend/src/workstream-user-admin-expertise.contract.test.mjs`.
- Last aligned evidence: TASK-ADIA-02-002 source-evidence mapping.
- Remaining validation gaps: configured model/provider runtime happy path, fail-closed runtime surface capture, tool-boundary/loader denial evidence through local runtime, and manual/browser access-review result review.

### `user-admin.chat-plan-trace-frontend`

- App-description files: `../workstream.md`, `../agents/functional-agent.md`, `../tools/governed-tools.md` human chat plan catalog, `../traces/work-traces.md`, `../surfaces/surfaces.md`, `api-contracts.md`, `frontend-routes.md`.
- Implementation files: `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`, `src/main/java/ai/first/api/coreapp/workstream/WorkstreamEndpoint.java`, `frontend/src/workstream/**`, `frontend/src/api/**`, `src/main/java/ai/first/application/foundation/audit/**`, `src/main/java/ai/first/application/foundation/workstream/**`.
- Test / validation files: `frontend/src/workstream-user-admin-vertical.contract.test.mjs`, `frontend/src/workstream-user-admin-expertise.contract.test.mjs`, `frontend/src/workstream-actions.contract.test.mjs`, `src/test/java/ai/first/application/coreapp/workstream/WorkstreamServiceTest.java`, User Admin admin endpoint/invitation/access-review tests listed above.
- Last aligned evidence: TASK-ADIA-02-002 source-evidence mapping.
- Remaining validation gaps: current protected runtime path for exact confirmation, stale/cross-context denial, partial failure, trace refs, no pre-confirm mutation, frontend rendering against real API responses, and browser-safe redaction.

## Unmapped current-intent files

- None recorded during this split. This means no known User Admin app-description file was intentionally excluded, not that all current-intent files are aligned.

## Unmapped implementation files

- None recorded during this split. Shared foundation/workstream files may support multiple workstreams and need finer-grained classification in future reviews.

## Alignment notes

- Current state is `partially-aligned` because TASK-ADIA-02-002 mapped the refreshed User Admin description graph to existing source/test/frontend evidence and runtime-validation scaffold without exercising the current runtime path.
- Runtime-validation coverage remains authored-not-run for User Admin. `RV-USER-ADMIN-001` covers only invitation create/idempotency/provider-state/non-admin denial; role/status/support/identity/access-review/chat-plan runtime-validation scenarios remain follow-up work.
- Provider-backed Resend delivery and model-backed access-review/chat guidance remain `provider-config-blocker` items unless concrete local provider configuration is exercised; missing provider/model config must fail closed with actionable system-message/trace evidence.
- WorkOS/AuthKit-backed invitee acceptance and selected-context refresh remain `auth-setup-blocker` items until real local auth or an approved local equivalent is run through the protected Akka/API/UI path.
- Do not use this source-alignment file as runtime-readiness evidence. Runtime readiness still requires automated checks and real local API/UI/agent-path verification for the selected scope.
