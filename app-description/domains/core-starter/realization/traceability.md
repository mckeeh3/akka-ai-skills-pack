# Core starter realization traceability

This traceability map links the reconstructed current-intent graph to known root implementation and validation evidence. It is docs-only; it does not count runtime gaps as implemented.

## Shared runtime backbone

| Intent area | Implementation evidence | Validation evidence / gaps |
|---|---|---|
| Authenticated account context, selected tenant/customer scope, backend authorization | `src/main/java/ai/first/api/foundation/security/MeEndpoint.java`, `src/main/java/ai/first/application/foundation/identity/**`, `src/main/java/ai/first/domain/foundation/identity/**` | Current app-description foundation nodes define the product contract; archived readiness specs are historical evidence only. Live WorkOS/AuthKit smoke remains external-configuration dependent and must fail closed when missing. |
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
| User Admin dashboard trunk and User Directory branch | `app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md`, `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`, `frontend/src/workstream/surfaces/**` | Current intent requires dashboard -> `surface-user-admin-users`, descendant **Show users** / **Back to users**, stale/forbidden `surface-user-admin-system-message`, and no frontend-only authority; validate through service/workstream/frontend contract tests. |
| User Admin SaaS Owner Admin branch | `app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md`, `app-description/domains/core-starter/capabilities/user-and-access-administration.md` | Current intent requires SaaS Owner Admin list/invite/manage surfaces, APIs, last-owner-admin protection, invitation/outbox integration, and safe Tenant/Customer denial; runtime evidence must come from protected API/action tests before implementation is marked done. |
| User Admin Organization Directory branch | `app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md`, `src/main/java/ai/first/application/coreapp/useradmin/SaasOwnerOrganizationAdminService.java`, `frontend/src/workstream/surfaces/OrganizationAdminSurface.tsx` | Current intent requires product capabilities on `saas_owner.organization.*`, authorized dashboard -> `surface-user-admin-organization-directory`, descendant **Show organizations** / **Back to organizations**, and Tenant/Customer omission or safe denial; validate through service/workstream/frontend contract tests. |
| User Admin Organization Admin branch | `app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md`, `app-description/55-ui/frontend-api-contracts.md` | Current intent requires Organization Admin list/invite/bootstrap/detail/manage surfaces and APIs under a selected Organization/Tenant, including `TENANT_ADMIN` role validation, invitation/outbox integration, last-organization-admin protection, and no tenant app-data/support/billing authority by implication; runtime evidence must prove the protected paths. |
| User Admin Customer and Customer Admin branch | `app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md`, `app-description/55-ui/frontend-api-contracts.md` | Current intent requires Customer list/create/detail/lifecycle plus Customer Admin list/invite/bootstrap/detail/manage surfaces and APIs under selected Organization/Tenant and Customer scope, including `CUSTOMER_ADMIN` role validation, invitation/outbox integration, last-customer-admin protection, and no sibling-customer/Organization Admin/SaaS Owner authority by implication; runtime evidence must prove the protected paths. |
| User Admin surface conformance | `app-description/domains/core-starter/workstreams/user-admin/workstream.md`, `app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md` | Current intent requires canonical surface types, backend-authored dashboard/list routing and options, `user-admin-agent` with `agent-user-admin` alias compatibility, inspection-only detail surfaces, typed `surface-user-admin-system-message` outcomes, diagnostic metadata redaction, and no inline mutation controls in normal User Admin detail/runtime paths; validate through contract and runtime tests. |

## Runtime boundaries and deferred scope

- The active frontend architecture is the workstream shell under `frontend/src/workstream/**`; removed screen modules are not current app-description targets or runtime fallbacks.
- Test-only fixtures, fake providers, and deterministic model doubles prove tests only; they are not normal runtime behavior.
- Billing and timer-backed invitation reminder behavior remain explicitly out of scope in `app-description/deferred-scope.md` unless a future current-intent change accepts them.
- Live WorkOS/AuthKit, Resend, and model provider smokes depend on external secrets; active descriptions must state fail-closed behavior and must not record secrets.
- Archived specs may still cite legacy `app-description/` taxonomy; those references are historical evidence only and do not override this current-intent graph.
