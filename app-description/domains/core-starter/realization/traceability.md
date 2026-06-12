# Core starter realization traceability

This traceability map links the reconstructed current-intent graph to known root implementation and validation evidence. It is docs-only; it does not count runtime gaps as implemented.

## Shared runtime backbone

| Intent area | Implementation evidence | Validation evidence / gaps |
|---|---|---|
| Authenticated account context, selected tenant/customer scope, backend authorization | `src/main/java/ai/first/api/foundation/security/MeEndpoint.java`, `src/main/java/ai/first/application/foundation/identity/**`, `src/main/java/ai/first/domain/foundation/identity/**` | Readiness docs under `specs/full-core-saas-readiness/**`; live WorkOS/AuthKit smoke remains external-configuration dependent and must fail closed when missing. |
| Workstream shell, functional-agent routing, structured surfaces, realtime event stream | `src/main/java/ai/first/api/coreapp/workstream/WorkstreamEndpoint.java`, `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`, `src/main/java/ai/first/application/foundation/workstream/**`, `frontend/src/workstream/**`, `frontend/src/api/HttpWorkstreamApiClient.ts`, `frontend/src/api/WorkstreamApiClient.ts` | Frontend contract tests `frontend/src/workstream-*.contract.test.mjs`; backend `src/test/java/ai/first/application/coreapp/workstream/WorkstreamServiceTest.java`. |
| Protected admin/control API surface | `src/main/java/ai/first/api/coreapp/admin/AdminEndpoint.java` plus capability services listed by workstream mappings | Endpoint/service tests under `src/test/java/ai/first/application/coreapp/**`; some direct endpoint seeded-data assumptions are validation nuance, not product intent. |
| Frontend hosting and browser secret boundary | `src/main/java/ai/first/api/coreapp/workstream/StarterFrontendEndpoint.java`, `frontend/src/main.tsx`, `frontend/src/api/**` | `npm --prefix frontend` tests/typecheck/build remain the runtime UI validation path when feature-bearing changes are made. |
| Audit/work traces and denial/provider diagnostics | `src/main/java/ai/first/application/foundation/audit/**`, `src/main/java/ai/first/application/foundation/agent/AgentRuntimeTrace*`, `src/main/java/ai/first/application/foundation/workstream/**` | Foundation and coreapp tests cover trace repositories/views/services; provider-backed paths require real provider configuration or fail-closed evidence. |

## Workstream realization files

- My Account: `workstreams/my-account/realization/`
- User Admin: `workstreams/user-admin/realization/`
- Agent Admin: `workstreams/agent-admin/realization/`
- Audit/Trace: `workstreams/audit-trace/realization/`
- Governance/Policy: `workstreams/governance-policy/realization/`

## User Admin navigation-tree traceability

| Intent area | Implementation evidence | Validation evidence / gaps |
|---|---|---|
| User Admin dashboard trunk and User Directory branch | `app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md`, `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`, `frontend/src/workstream/surfaces/**` | Mini-project `specs/user-admin-surface-navigation-tree/**`; implementation follow-up must prove dashboard -> `surface-user-admin-users`, descendant **Show users** / **Back to users**, stale/forbidden `surface-user-admin-system-message`, and no frontend-only authority. |
| User Admin Organization Directory branch | `app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md`, `src/main/java/ai/first/application/coreapp/useradmin/SaasOwnerOrganizationAdminService.java`, `frontend/src/workstream/surfaces/OrganizationAdminSurface.tsx` | Mini-project `specs/user-admin-surface-navigation-tree/**`; implementation follow-up must align product capabilities on `saas_owner.organization.*`, prove authorized dashboard -> `surface-user-admin-organization-directory`, descendant **Show organizations** / **Back to organizations**, and Tenant/Customer omission or safe denial. |

## Explicit drift and deferred scope

- `frontend/src/screens/**` remains legacy/page-style reference or fixture evidence, not primary workstream runtime architecture.
- Test-only `LocalDemo*`, fixture API clients, fake providers, and deterministic model doubles prove tests only; they are not normal runtime behavior.
- Billing and timer-backed invitation reminder behavior remain deferred by active readiness documentation.
- Live WorkOS/AuthKit, Resend, and model provider smokes depend on external secrets; active descriptions must state fail-closed behavior and must not record secrets.
- Active specs that still cite legacy `app-description/` taxonomy are reconciled by `TASK-ADICM-03-001`, after these realization files exist.
