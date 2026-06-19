# Customer-boundary evidence and gap map

## Scope and validation posture

Task: `TASK-FCBAD-01-001`.

This is a docs-only current-intent inventory for the foundation customer boundary. Runtime source, frontend source, and tests were inspected only as evidence; no runtime/API/UI behavior change is in scope for this task.

Search proof command recorded for this inventory:

```bash
rg -n "Customer|customer|tenant\.customer|action-customer|TenantCustomer" app-description src/main/java frontend/src src/test/java --glob '!**/node_modules/**'
```

The search found active app-description coverage, backend services/endpoints/workstream actions, frontend API/client/surface rendering, and focused tests for Customer and Customer Admin administration.

## Evidence summary

### Active app-description evidence

| Area | Evidence | Current assessment |
|---|---|---|
| App-level posture | `app-description/app.md` | States the secure multi-tenant AI-first SaaS core starter objective, tenant/customer scoping, backend authorization, governed AI behavior, audit/work trace obligations, and non-goal business domains. It does not explicitly name the foundation customer boundary or distinguish it from future CRM/customer-success/sales/billing/support domains. |
| Global roles | `app-description/global/roles/foundation-roles.md` | Defines `saas-owner-admin`, `tenant-admin`, `customer-admin`, auditor/support roles, and frontend non-authority. Customer Admin limits are clear. A legacy `app-admin` label still appears in the User Admin access node and should be normalized or defined as a compatibility label. |
| Global functional agents | `app-description/global/agents/foundation-functional-agents.md` | Defines `user-admin-agent` as access-operations guidance only; forbids autonomous authority expansion. It does not explicitly mention Customer lifecycle / Customer Admin preparation limits, although workstream files do. |
| Core domain | `app-description/domains/core-starter/domain.md` | Identifies the core starter boundary and excludes user-owned business domains. It does not yet explicitly state that the foundation Customer record is a security/customer-boundary substrate rather than CRM/customer-success/sales/support/billing state. |
| User/access capability | `app-description/domains/core-starter/capabilities/user-and-access-administration.md` | Strong coverage of `tenant.customer.*`, `tenant.customer_admin.*`, SaaS Owner/Organization/Admin hierarchy, scopes, denials, governed tools, outcomes, and linked workstream nodes. It needs light refactoring/annotation to separate foundation customer-boundary responsibilities from business customer-domain non-goals. |
| Auth context/data state | `app-description/domains/core-starter/data-state/auth-context-and-membership-state.md` | Covers account/profile/settings/tenant/customer organization/membership/invitation/support/audit state at a high level. It lacks explicit `Customer` state lifecycle/invariants, Customer Admin membership linkage, business-domain non-ownership, and support/service cross-layer caveat language. |
| User Admin workstream | `app-description/domains/core-starter/workstreams/user-admin/workstream.md` | Strongly captures the three admin levels and Customer lifecycle / Customer Admin branches. It is a primary current-intent node for the foundation customer boundary. |
| User Admin access | `app-description/domains/core-starter/workstreams/user-admin/access.md` | Strong authorization and denial coverage for Tenant/Organization Admin Customer actions and Customer Admin scope. It also includes legacy `app-admin` terminology that should be reconciled with canonical `saas-owner-admin`. |
| User Admin behavior | `app-description/domains/core-starter/workstreams/user-admin/behavior.md` | Strong deterministic behavior coverage for Customer lifecycle and Customer Admin lifecycle, including idempotency, no-op/conflict, audit, and provider/model/outbox fail-closed behavior. |
| User Admin surfaces | `app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md` | Strong surface graph coverage for Customer directory/detail/create/rename/suspend/reactivate and Customer Admin list/invite/detail. It explicitly states the surface-description sufficiency for collection-object readiness. |
| User Admin agent binding | `app-description/domains/core-starter/workstreams/user-admin/agents/functional-agent.md` | Captures general User Admin agent authority limits, but its `Authority` and `Surface and tool map` sections omit `manage-customers`, `manage-customer-admins`, and the Customer branch surfaces even though the workstream/tools/surfaces nodes include them. |
| User Admin tools | `app-description/domains/core-starter/workstreams/user-admin/tools/governed-tools.md` | Strongly lists `manage-customers` and `manage-customer-admins` as browser tools with human-confirmed agent preparation only. |
| User Admin policies | `app-description/domains/core-starter/workstreams/user-admin/policies/policy-bindings.md` | Covers backend default-deny, tenant-customer isolation, approval, fail-closed, redaction, last-admin, idempotency, and tool-boundary governance. It is sufficient, with possible targeted Customer boundary examples in later description edits. |
| User Admin traces | `app-description/domains/core-starter/workstreams/user-admin/traces/work-traces.md` | Explicitly requires Customer lifecycle and Customer Admin bootstrap/manage trace evidence with selected AuthContext, tenant/customer scope, capability/tool/action id, outcomes, denials, and redaction. |
| User Admin tests | `app-description/domains/core-starter/workstreams/user-admin/tests/coverage.md` | Explicitly covers Organization Admin creating Customers, bootstrapping Customer Admins, managing Customer Admins, safe denials, rendering, idempotency, observability, and secret boundaries. |
| User Admin Akka realization | `app-description/domains/core-starter/workstreams/user-admin/realization/akka-components.md` | Maps general User Admin components but omits `TenantCustomerAdminService`, `Customer`, customer repository methods, Customer Admin endpoints, and customer-specific tests from the evidence table. |
| User Admin API realization | `app-description/domains/core-starter/workstreams/user-admin/realization/api-contracts.md` | Strongly maps `tenant.customer.*` and `tenant.customer_admin.*` to `/api/admin/customers...` and Customer Admin routes. |
| User Admin frontend realization | `app-description/domains/core-starter/workstreams/user-admin/realization/frontend-routes.md` | Covers generic User Admin surfaces but does not explicitly list `UserAdminScopedAdminSurface.tsx`, `ApiClient.ts` / `HttpApiClient.ts` Customer methods, or the customer-specific contract tests as realization evidence. |
| Capability compatibility | `app-description/domains/core-starter/workstreams/user-admin/realization/capability-compatibility.md` | Documents canonical `tenant.customer.*` and `tenant.customer_admin.*` families plus compatibility aliases. Useful for subsequent description edits. |

### Runtime and frontend evidence inspected

| Area | Evidence | Notes |
|---|---|---|
| Customer state | `src/main/java/ai/first/domain/foundation/identity/Customer.java` | Minimal foundation record: `tenantId`, `customerId`, `displayName`, `active`. This supports a small security/customer-boundary substrate rather than CRM/customer profile ownership. |
| Customer lifecycle service | `src/main/java/ai/first/application/coreapp/useradmin/TenantCustomerAdminService.java` | Backend-authoritative seam for `tenant.customer.list/read/create/rename/suspend/reactivate`; requires selected TENANT scope and canonical capabilities; validates idempotency/name/reason; denies hidden/cross-scope targets; emits `AdminAuditEvent`; returns browser-safe Customer summary/detail/action result with boundary notice. |
| Admin API | `src/main/java/ai/first/api/coreapp/admin/AdminEndpoint.java` | Exposes `/api/admin/customers`, `/api/admin/customers/{customerId}`, lifecycle routes, and `/api/admin/customers/{customerId}/admins...` routes. Customer Admin invitation and membership-management routes target `ScopeType.CUSTOMER` and `CUSTOMER_ADMIN` roles. |
| Workstream action routing | `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java` | Defines `tenant.customer.*` and `tenant.customer_admin.*` constants, Customer directory/detail/task surfaces, Customer Admin surfaces, action handlers, idempotent Customer lifecycle calls, branch-return metadata, and capability visibility. Runtime still uses compatibility functional-agent id `agent-user-admin`. |
| Typed frontend API | `frontend/src/api/ApiClient.ts`, `frontend/src/api/HttpApiClient.ts`, `frontend/src/api/types.ts` | Typed Customer and Customer Admin client methods and DTOs exist for list/read/create/rename/suspend/reactivate and Customer Admin invitation/membership actions. |
| Frontend surface renderer | `frontend/src/workstream/surfaces/UserAdminScopedAdminSurface.tsx` | Renders SaaS Owner, Organization Admin, Customer, and Customer Admin scoped admin surfaces; includes Customer task form, directory, inspection, branch return, browser-secret redaction, and no client-side authority language. |
| Backend tests | `src/test/java/ai/first/application/coreapp/workstream/WorkstreamServiceTest.java`, `src/test/java/ai/first/application/coreapp/useradmin/AdminEndpointIntegrationTest.java` | Evidence includes durable Customer lifecycle state through workstream actions, Customer branch redaction, SaaS Owner Customer action denial, Customer Admin actor setup, and API-level Customer Admin seed data. |
| Frontend/contract tests | `frontend/src/workstream-user-admin-vertical.contract.test.mjs`, `frontend/src/workstream-organization-admin-vertical.contract.test.mjs` | Contract checks assert scoped admin surface coverage for Customer and Customer Admin branches and frontend secret boundaries. |

## Required app-description edits by node group

### TASK-FCBAD-01-002: domain, capability, and state intent

Primary nodes to edit:

1. `app-description/app.md`
   - Add explicit foundation customer boundary language to the app-level tenant/customer assumptions and non-goals.
   - State that Customer in the core starter is a secure SaaS authorization/audit boundary, not CRM/customer-success/sales/billing/support business state.

2. `app-description/domains/core-starter/domain.md`
   - Add a dedicated foundation customer-boundary section.
   - Place Customer records in the core starter secure foundation.
   - Explicitly exclude CRM account/contact/opportunity, customer health/renewal, support case, billing subscription, and industry-specific customer objects.
   - Note that downstream business domains may own customer-scoped records while using this boundary for authorization and audit.

3. `app-description/domains/core-starter/data-state/auth-context-and-membership-state.md`
   - Expand durable state responsibilities for `Tenant`, `Customer`, `Membership`, `AuthContext`, invitations, and audit/work trace linkage.
   - Capture `Customer` invariants: tenant-owned id, display label, active/suspended lifecycle, no business profile fields, hidden/sibling customer redaction, Customer Admin membership scope, and retention/audit obligations.
   - Add the organization-level vs customer-level scope distinction, including the special support/service caveat: support/service may span organization and customer layers only through explicitly scoped capabilities.

4. `app-description/domains/core-starter/capabilities/user-and-access-administration.md`
   - Keep the existing capability inventory, but add a concise boundary preface for `tenant.customer.*` and `tenant.customer_admin.*`.
   - Clarify that Organization/Tenant Admin Customer lifecycle is foundation boundary management, not CRM/customer-success/sales/support/billing management.
   - Preserve existing capability ids and avoid broad runtime expansion.

Secondary consistency nodes:

- `app-description/global/roles/foundation-roles.md`: likely only needs a compatibility note if the next task chooses to reconcile `app-admin` vs `saas-owner-admin` terminology in `user-admin/access.md`.
- `app-description/domains/core-starter/workstreams/user-admin/access.md`: may need terminology cleanup for legacy `app-admin` wording, but this can also be handled in TASK-FCBAD-01-003 if scoped to workstream bindings.

### TASK-FCBAD-01-003: workstream, surfaces, agents, tool, policy, trace, test, and realization bindings

Primary nodes to edit:

1. `app-description/domains/core-starter/workstreams/user-admin/agents/functional-agent.md`
   - Add `manage-customers` and `manage-customer-admins` to the explicit authority/tool map.
   - Add Customer directory/detail/lifecycle and Customer Admin surfaces to the primary surfaces list.
   - State that `user-admin-agent` may summarize/draft/prepare Customer boundary and Customer Admin actions only; it cannot autonomously create/suspend/reactivate Customers, invite Customer Admins, grant Customer Admin authority, cross scope, or mutate business customer-domain data.

2. `app-description/domains/core-starter/workstreams/user-admin/realization/akka-components.md`
   - Add Customer-specific evidence: `Customer.java`, `TenantCustomerAdminService.java`, identity repository customer methods / durable identity repository, AdminEndpoint Customer routes, WorkstreamService Customer action routing, and relevant tests.

3. `app-description/domains/core-starter/workstreams/user-admin/realization/frontend-routes.md`
   - Add Customer-specific frontend evidence: `ApiClient.ts`, `HttpApiClient.ts`, `types.ts`, `UserAdminScopedAdminSurface.tsx`, `workstream-user-admin-vertical.contract.test.mjs`, and relevant fixtures as test/examples only.

4. `app-description/domains/core-starter/workstreams/user-admin/workstream.md`, `access.md`, `behavior.md`, `surfaces/surfaces.md`, `tools/governed-tools.md`, `policies/policy-bindings.md`, `traces/work-traces.md`, and `tests/coverage.md`
   - These are mostly sufficient. Later edits should be limited to consistency updates needed after TASK-FCBAD-01-002, especially terminology normalization, foundation-vs-business boundary language, and cross-links.

## Non-goal runtime work

Do not implement or change runtime behavior for this mini-project inventory task.

The following are non-goals unless a later verification task creates a separate implementation/remediation queue:

- Adding CRM/customer-success/sales/support/billing business domains or moving `Customer` out of foundation identity state.
- Adding new Customer lifecycle fields beyond foundation boundary state.
- Changing API routes, frontend components, Akka components, or tests.
- Retiring runtime compatibility aliases such as `agent-user-admin` or uppercase `USERADMIN_*` capability names.
- Expanding Customer Admin authority or using frontend visibility, browser route state, or prompt text as authorization.

## Ambiguity and blocker assessment

No ambiguity blocks `TASK-FCBAD-01-002`.

The next description task can safely proceed with docs-only app-description edits using these assumptions:

- The core starter `Customer` is a foundation security/customer-boundary object under the selected Organization/Tenant.
- Business customer domains are additive and separate, but may reference this boundary for authorization, audit, and redaction.
- Customer Admin is a scoped User Admin branch and role, not a prompt/browser-state authority expansion.
- Support/service domain behavior is out of this mini-project except for the explicit statement that it may span organization and customer layers through scoped capabilities.

No pending question is required before the next task.
