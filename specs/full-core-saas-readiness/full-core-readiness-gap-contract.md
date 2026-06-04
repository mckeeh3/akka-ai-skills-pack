# Full-Core Readiness Gap Contract

## Purpose

This contract defines the evidence baseline for moving the runnable five-core workstream starter toward full-core SaaS readiness. It is intentionally a planning/evidence artifact: later tasks must close gaps through the real local Akka/API/UI path, not through fixtures, deterministic demos, frontend-only behavior, or model-less normal runtime substitutes.

## Scope label and readiness state

- scope label: full core
- overall state: not full-core ready
- current usable scope: bounded five-core workstream starter alignment for My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy
- readiness rule: a gap is closed only when local runtime/API/UI behavior or focused tests prove the named backend-authorized path, tenant/customer scope, trace/audit behavior, provider fail-closed behavior where applicable, and frontend secret boundary.

## Cross-cutting full-core invariants

Every implementation task that closes a gap must preserve these invariants:

- WorkOS/AuthKit is the supported browser auth boundary; WorkOS provider secrets are never shipped to the browser.
- Resend is the supported production email service; local/dev/test delivery must use a captured outbox adapter and production readiness must fail closed when required Resend configuration is absent.
- Backend authorization and selected `AuthContext` are authoritative for protected routes, workstream shell requests, surface actions, component commands, view queries, streams, workflow actions, agent tools, consumers, and timers.
- Tenant/customer isolation, disabled-user denial, inactive-membership denial, role/scope denial, support-access checks, last-admin protection, idempotency, audit/work trace emission, and browser-safe redaction must be tested for each affected capability.
- Model-backed workstream behavior must resolve active governed agent configuration, prompt/skill/reference manifests, loader tools, tool boundaries, runtime tools, provider fail-closed behavior, and durable traces before it can be called ready.
- `frontend/src/workstream/**` is the canonical browser shell/surface implementation area; `frontend/src/__tests__/fixtures/**` and legacy/static examples may support tests only and must not become normal runtime evidence.

## Billing-boundary decision

Billing-boundary behavior is explicitly **deferred** for this mini-project except for preserving the boundary contract:

- in scope now: document that SaaS Owner billing/subscription metadata must not grant Tenant application-data access; do not introduce billing-derived authorization bypasses; keep billing-boundary checks in final readiness smoke evidence.
- out of scope now: implementing subscription lifecycle, plan management, entitlement enforcement, billing provider integration, billing UI, or payment-failure workflows.
- impact: full-core readiness for this mini-project may be reported only as `full-core except billing implementation deferred` unless a later task adds billing implementation and tests.

## Gap matrix

| Gap | Current evidence and partial implementation | Full-core requirement | Follow-up task | Required validation |
| --- | --- | --- | --- | --- |
| WorkOS/AuthKit runtime boundary and frontend secret boundary | Readiness docs mark production WorkOS/AuthKit config and fail-closed local validation incomplete. Focused `rg` found WorkOS/AuthKit fail-closed language in `src/main/java/ai/first/application/foundation/identity/EnterpriseIdentityAdminService.java`; canonical browser entry/API files include `frontend/src/main.tsx`, `frontend/src/api/HttpWorkstreamApiClient.ts`, and `/api/me` code under `src/main/java/ai/first/api/foundation/security/MeEndpoint.java`. | Validate AuthKit/JWT-bearing API path, selected `AuthContext`, `/api/me`, protected workstream shell behavior, disabled-user/role denial, no browser-exposed provider secrets, and actionable fail-closed errors when provider config is missing. | `TASK-FCSR-02-001` | `git diff --check`; focused backend auth tests; frontend tests/typecheck/build when changed; focused `rg` for forbidden frontend secret names and AuthKit boundary text. |
| Invitations and Resend/captured outbox | `src/main/java/ai/first/application/foundation/invitation/InvitationService.java`, `AkkaInvitationRepository.java`, `DurableInvitationRepositoryEntity.java`, `InvitationLifecycleHistoryEntity.java`, and `src/main/java/ai/first/application/foundation/email/` show invitation/outbox seams. Tests exist under `src/test/java/ai/first/application/foundation/invitation/` and `src/test/java/ai/first/application/coreapp/useradmin/InvitationAndUserAdminServiceTest.java`. | Complete invitation send/resend/revoke/expire/accept through backend/API/workstream path; prove Resend production config fail-closed; prove captured local/dev/test outbox, delivery attempts/failures, idempotency, audit/lifecycle history, tenant/customer scope, and admin visibility. | `TASK-FCSR-03-001` | `git diff --check`; focused invitation/email tests; broader `mvn test` if shared foundation behavior changes; API/workstream smoke evidence if endpoint behavior changes. |
| User Admin structured surfaces | Runtime ids are mapped in `app-description/70-traceability/workstream-id-map.md`; `MeResponse` exposes User Admin metadata; frontend contract tests include `frontend/src/workstream-user-admin-vertical.contract.test.mjs`; backend tests include User Admin service/access-review tests. | Complete User Admin dashboard, user list/search, invitation panel, user detail/account, roles/memberships, access review, support access, admin audit, safe mutation or decision-card action, denials, audit/work traces, and tenant/customer filtering through real backend/API/UI paths. | `TASK-FCSR-04-001` | `git diff --check`; focused backend User Admin/workstream tests; frontend contract tests; `npm --prefix frontend run typecheck`; `npm --prefix frontend run build` when production UI output changes. |
| Managed-agent foundation | Focused `rg` found `AgentDefinition`, `PromptDocument`, `SkillDocument`, `ReferenceDocument`, and `ToolPermissionBoundary` domain/application seams; tests exist under `src/test/java/ai/first/application/foundation/agent/`; Agent Admin frontend tests include `frontend/src/workstream-agent-admin-vertical.contract.test.mjs`. | Complete AgentDefinition lifecycle, governed PromptDocument/SkillDocument/ReferenceDocument versions, manifests, `ToolPermissionBoundary`, seed/import, prompt assembly, authorized `readSkill(skillId)`/`readReferenceDoc(referenceId)`, denied loads, behavior proposals/approvals, active runtime resolution, provider fail-closed behavior, and PromptAssemblyTrace/SkillLoadTrace/ReferenceLoadTrace/AgentWorkTrace. | `TASK-FCSR-05-001` | `git diff --check`; focused managed-agent backend tests; broader `mvn test` when shared runtime changes; frontend tests/typecheck/build if Agent Admin UI changes; fail-closed provider/config validation. |
| Audit/Trace investigation | Workstream ids and surfaces exist in `app-description/70-traceability/workstream-id-map.md`; backend audit services/tests exist under `src/main/java/ai/first/application/foundation/audit/`, `src/main/java/ai/first/application/coreapp/audit/`, and `src/test/java/ai/first/application/foundation/audit/`; frontend tests include `frontend/src/workstream-audit-trace-vertical.contract.test.mjs`. | Provide searchable Audit/Trace timeline/detail/investigation surfaces for AdminAuditEvent, authorization denials, data access, decisions, prompt/skill/reference/model/tool use, support access, redaction/export denial or scoped export, and correlation links across workstreams. | `TASK-FCSR-06-001` | `git diff --check`; focused audit backend/workstream tests; frontend tests/typecheck/build if UI changes; redaction/export denial evidence. |
| Governance/Policy lifecycle | Governance/Policy surfaces are mapped in `workstream-id-map.md`; `src/main/java/ai/first/application/foundation/governance/GovernancePolicyService.java` includes proposal/decision/activation/rollback seams; tests exist under foundation/coreapp governance paths and `frontend/src/workstream-governance-policy-vertical.contract.test.mjs`. | Complete policy proposal, approval gate, impact/simulation, review, activation/rollback, outcome note, authority-expansion denial, decision-card evidence, audit/work trace links, and human-governed commits through backend/API/UI paths. | `TASK-FCSR-06-001` | `git diff --check`; focused governance/backend workstream tests; frontend tests/typecheck/build if UI changes; policy-denial and decision-card evidence. |
| Support access | Readiness docs and User Admin functional-agent contract require support-access visibility and explicit support grants. Current evidence is partial in workstream/capability docs and User Admin metadata; full lifecycle evidence is not established. | Implement or explicitly block support-access grant/revoke/expiry, SaaS Owner support role visibility only with tenant-created support grant, support-use audit trace, redaction, and denial for unsupported scopes. | `TASK-FCSR-04-001` for User Admin surfaces and `TASK-FCSR-06-001` for audit/governance evidence | Focused User Admin/support-access tests; audit/redaction tests; forbidden SaaS Owner access without support grant. |
| Tenant isolation and authorization coverage | App-description requires tenant/customer scoping; focused tests exist across identity, invitation, user admin, agent, audit, and governance areas, but full cross-feature coverage is not yet proven. | Every full-core capability must prove tenant/customer filtering, disabled-user denial, role/scope denial, support-access constraints, last-admin protection where relevant, safe system-message denial, audit/work traces, and no frontend-only authorization. | All implementation tasks; final verification in `TASK-FCSR-07-001` and `TASK-FCSR-99-001` | Focused backend tests per gap; full `mvn test` in runtime smoke; frontend denial/rendering tests. |
| Runtime smoke | Readiness docs explicitly require real local Akka/API/UI validation before completion claims. | Run backend, frontend, and manual/API smoke evidence across five core workstreams, auth, invitations, User Admin, managed-agent foundation, Audit/Trace, Governance/Policy, tenant isolation, provider fail-closed behavior, traces, and frontend secret boundary. | `TASK-FCSR-07-001` | `git diff --check`; `mvn test`; `npm --prefix frontend test -- --run`; `npm --prefix frontend run typecheck`; `npm --prefix frontend run build`; repository static/secret-boundary scan if available. |

## Evidence inventory commands used for this contract

```bash
rg -n "WorkOS|AuthKit|Resend|Invitation|User Admin|AgentDefinition|PromptDocument|SkillDocument|ReferenceDocument|ToolPermissionBoundary|Audit/Trace|Governance/Policy|support access|billing|tenant isolation|frontend secret|runtime smoke" app-description src frontend specs/full-core-saas-readiness -g '!node_modules'
find src/test/java/ai/first -maxdepth 5 -type f | rg 'auth|Auth|Invitation|UserAdmin|Agent|Audit|Governance|Workstream|Secret|Tenant|Security|Me'
find frontend/src -maxdepth 4 -type f | rg 'workstream|api|test|spec|main|Auth|secret'
```

## Task ordering result

The queue order remains valid:

1. define this gap contract;
2. validate WorkOS/AuthKit and frontend secret boundary;
3. complete invitations and email outbox;
4. complete User Admin structured surfaces, including support-access surface coverage;
5. complete managed-agent foundation;
6. complete Audit/Trace and Governance/Policy readiness;
7. run full runtime smoke;
8. verify the mini-project.

No split is required before `TASK-FCSR-02-001`. Billing implementation is deferred by this contract, so no billing implementation task is inserted unless the user expands scope.
