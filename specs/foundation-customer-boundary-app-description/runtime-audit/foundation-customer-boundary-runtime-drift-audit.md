# Foundation Customer Boundary Runtime Drift Audit

Date: 2026-06-16

## Scope

Bounded audit of the implemented foundation Customer boundary runtime against the active app-description source of truth verified in:

- `specs/foundation-customer-boundary-app-description/verification/foundation-customer-boundary-sufficiency-review.md`
- `app-description/domains/core-starter/capabilities/user-and-access-administration.md`
- `app-description/domains/core-starter/data-state/auth-context-and-membership-state.md`
- `app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md`
- `app-description/domains/core-starter/workstreams/user-admin/agents/functional-agent.md`
- `app-description/domains/core-starter/workstreams/user-admin/realization/akka-components.md`
- `app-description/domains/core-starter/workstreams/user-admin/realization/frontend-routes.md`
- `app-description/domains/core-starter/workstreams/user-admin/tests/coverage.md`
- `app-description/domains/core-starter/workstreams/user-admin/traces/work-traces.md`

Runtime evidence inspected:

- `src/main/java/ai/first/domain/foundation/identity/Customer.java`
- `src/main/java/ai/first/application/coreapp/useradmin/TenantCustomerAdminService.java`
- `src/main/java/ai/first/application/coreapp/useradmin/UserAdminService.java`
- `src/main/java/ai/first/application/foundation/invitation/InvitationService.java`
- `src/main/java/ai/first/api/coreapp/admin/AdminEndpoint.java`
- `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`
- `frontend/src/api/ApiClient.ts`
- `frontend/src/api/HttpApiClient.ts`
- `frontend/src/api/types.ts`
- `frontend/src/workstream/surfaces/UserAdminScopedAdminSurface.tsx`
- focused backend/frontend tests.

## Summary

The core Customer lifecycle path is partially aligned: the repository has a foundation `Customer` record, tenant-scoped Customer lifecycle service, JWT-protected `/api/admin/customers...` routes, User Admin workstream Customer surfaces, frontend API methods, redaction copy, idempotency, and passing targeted tests.

However, the runtime does **not fully match** the app-description for the Customer Admin branch and some list/search/action contracts. The most important drifts are:

1. Customer Admin workstream branch surfaces do not preserve or apply the selected Customer target.
2. Customer Admin invitation from the workstream surface can route through the generic `action-invite-user` path and create a tenant-scoped invitation instead of a customer-scoped `CUSTOMER_ADMIN` invitation.
3. Customer Admin role/invitation APIs can accept roles outside the Customer Admin-safe role set when invoked by an Organization/Tenant Admin.
4. Suspended Customer boundaries do not fail closed for Customer Admin operations.
5. Concrete API/integration tests do not cover Customer lifecycle and Customer Admin branch behavior at the depth required by the app-description.

Recommendation: create a focused runtime repair queue with backend authorization/role-safety tasks first, then workstream surface/action target propagation, then tests/frontend contract alignment.

## Validation commands run

```bash
mvn -Dtest=ai.first.application.coreapp.workstream.WorkstreamServiceTest,ai.first.application.coreapp.useradmin.AdminEndpointIntegrationTest test
npm --prefix frontend test -- --run frontend/src/workstream-user-admin-vertical.contract.test.mjs
git diff --check
```

Results:

- Backend focused tests passed: 72 tests, 0 failures.
- Frontend command passed. Note: current frontend test script runs `src/*.test.mjs`, so it executed the broader frontend contract suite and reported 153 passing tests.
- `git diff --check` passed for this audit artifact.

Passing tests confirm existing covered behavior, but the audit found uncovered drift against the new app-description.

## Findings

### FCB-RD-01: Customer lifecycle service and core state mostly align

Severity: pass / minor caveat

Evidence:

- `Customer.java` is a foundation boundary record with `tenantId`, `customerId`, `displayName`, and `active`.
- `IdentityRepositoryState` and `AkkaIdentityRepository` persist/list/read Customers by `tenantId:customerId`.
- `TenantCustomerAdminService` enforces selected `ScopeType.TENANT`, capability checks for `tenant.customer.*`, idempotency for mutations, normalized display names, no-op replay behavior, safe hidden-target denial, redaction boundary notice, and `AdminAuditEvent` emission.
- `WorkstreamServiceTest.tenantCustomerBranchUsesDurableCustomerLifecycleState` covers create/list/read/suspend and SaaS Owner denial.

Caveat:

- Trace refs returned by `TenantCustomerAdminService` are synthetic strings and the service emits admin audit events but not an explicit durable workstream-log trace. The app-description asks for audit/work trace evidence. This may be acceptable as first-pass audit trace coverage, but should be clarified or enhanced if future runtime completion requires durable workstream traces for Customer lifecycle operations.

Suggested task: optional low-priority trace-hardening task after higher-risk Customer Admin drifts are fixed.

### FCB-RD-02: `/api/admin/customers` ignores query/status filters even though the contract is list/search

Severity: medium

Source-of-truth expectation:

- Customer directory is a `list-search` surface.
- `tenant.customer.list` is described as list/search browser-safe Customer boundaries.
- `TenantCustomerAdminService.listCustomers(actor, query, status, correlationId)` supports query/status filtering.

Runtime evidence:

- `AdminEndpoint.customers()` calls `listCustomers(actor, null, null, correlationId)` and does not read query parameters.
- `HttpApiClient.listCustomers(query)` sends query params, but the backend ignores them.
- `WorkstreamService.customerDirectorySurface(...)` also calls `listCustomers(actor, "", "", correlationId)` and does not propagate safe filter input.

Impact:

- Runtime directory filtering/search does not match the app-description or frontend API contract.

Suggested task:

- Implement query/status propagation for `/api/admin/customers` and workstream Customer directory refresh, with tests proving filtered list behavior and no hidden-count leakage.

### FCB-RD-03: Customer Admin branch surfaces do not preserve selected Customer target

Severity: high

Source-of-truth expectation:

- `tenant.customer_admin.*` capabilities bootstrap/maintain Customer Admin users for **one selected Customer after that Customer exists**.
- Customer Admin branch surfaces must include selected Customer scope, target-customer proof, branch metadata, and safe target context.
- Customer Admin list/invite/detail/manage surfaces must not expose sibling-customer or tenant-wide authority.

Runtime evidence:

- `WorkstreamService.customerAdminsSurface(actor, correlationId)` accepts no input and returns `adminSubjectSurface(...)` with empty rows and only generic filters `{ role, backendAuthored }`; it does not include `customerId`, `customerName`, or target Customer proof.
- `customerAdminInvitationCreateSurface(actor, correlationId)` also accepts no input and uses generic `roleScopedInvitationSurface(...)` without carrying selected `customerId`.
- `UserAdminScopedAdminSurface.ScopedInspection` invokes task actions with `branchReturnInput(envelope)`, which carries `recordId` but not `customerId`; the server-side customer-admin surfaces ignore even that record id.

Impact:

- From `surface-user-admin-customer-detail`, opening Customer Admins or Invite Customer Admin loses the selected Customer boundary. This breaks the app-description's one-selected-Customer semantics.

Suggested task:

- Add target Customer propagation to Customer Admin branch workstream actions/surfaces: input parsing, `customerId`/safe label payload fields, target Customer validation, branch navigation metadata, and tests from Customer detail -> Customer Admins -> Invite Customer Admin.

### FCB-RD-04: Customer Admin invitation from workstream can create the wrong scope

Severity: high

Source-of-truth expectation:

- Customer Admin invitation/bootstrap must target `ScopeType.CUSTOMER`, the selected `tenantId`, the selected `customerId`, and role `CUSTOMER_ADMIN` or Customer Admin-safe roles only.
- The `user-admin-agent` may prepare payloads only; backend capability and confirmation surfaces remain authoritative.

Runtime evidence:

- `customerAdminInvitationCreateSurface(...)` exposes an invite form with role `CUSTOMER_ADMIN` but uses generic `inviteAction()`.
- `WorkstreamService.runAction(...)` handles `action-invite-user` by calling `InvitationService.createInvitation` with `actor.selectedContext().scopeType()`, `actor.selectedContext().tenantId()`, and `actor.selectedContext().customerId()`.
- For an Organization/Tenant Admin creating a Customer Admin, the selected context is tenant scoped and `customerId` is null; the generic invite path would create a tenant-scoped invitation rather than a customer-scoped Customer Admin invitation.
- The dedicated HTTP API path `POST /api/admin/customers/{customerId}/admins/invitations` does pass `ScopeType.CUSTOMER` and `customerId`, so the API path is closer to the app-description than the workstream action path.

Impact:

- The primary workstream/UI path can drift from the API path and violate Customer Admin scope semantics.

Suggested task:

- Add dedicated workstream action handling for Customer Admin invitations, e.g. `action-customer-admin-invite` or a backend-authored action that carries `customerId`, validates Customer existence/active state, and calls `InvitationService.createInvitation` with `ScopeType.CUSTOMER`.

### FCB-RD-05: Customer Admin role safety allows Organization/Tenant roles through customer-admin APIs

Severity: high / security-sensitive

Source-of-truth expectation:

- Customer Admin bootstrap/maintenance must assign only Customer Admin-safe roles such as `CUSTOMER_ADMIN`.
- It must deny Organization Admin / SaaS Owner role assignment through Customer Admin flows.

Runtime evidence:

- `AdminEndpoint.createCustomerAdminInvitation(...)` supplies default `CUSTOMER_ADMIN`, but `rolesOrDefault(request.roles(), defaultRoles)` accepts any supplied `FoundationRole` enum value.
- `InvitationService.ensureRolesAssignable(...)` denies `SAAS_OWNER_ADMIN` unless actor scope is SaaS Owner and denies tenant-default-scope roles only when the actor selected context is `CUSTOMER`. It does **not** deny `TENANT_ADMIN` when an Organization/Tenant Admin is creating a `ScopeType.CUSTOMER` invitation.
- `AdminEndpoint.changeCustomerAdminRoles(...)` calls `changeScopedAdminRolesResponse(...)`, which again uses generic `rolesOrDefault(...)`.
- `UserAdminService.ensureAssignable(...)` similarly denies tenant roles only when the actor selected context is `CUSTOMER`, not when the target membership scope is `CUSTOMER`.

Impact:

- An Organization/Tenant Admin may be able to assign `TENANT_ADMIN` or other tenant-scope roles to a customer-scoped invitation/membership through Customer Admin APIs, depending on role/capability shape. This violates the app-description role-safety boundary.

Suggested task:

- Add target-scope-aware role validation for invitations and membership role changes. For `ScopeType.CUSTOMER`, allow only Customer Admin/customer-safe roles; deny tenant/admin/SaaS-owner roles regardless of actor scope. Add API and service tests proving Organization Admin/SaaS Owner role assignment through Customer Admin flows is denied.

### FCB-RD-06: Suspended Customer does not fail closed for Customer Admin operations

Severity: medium-high

Source-of-truth expectation:

- Disabled accounts, inactive memberships, suspended customers, hidden targets, sibling-customer targets, and cross-tenant/customer requests fail closed.
- Customer Admin bootstrap/maintenance requires the Customer to exist and remain an acceptable target.

Runtime evidence:

- `TenantCustomerAdminService.readCustomer(...)` returns suspended Customers with visible action `reactivate` rather than enforcing operation-specific denial.
- `AdminEndpoint.customerAdmins(...)` and `createCustomerAdminInvitation(...)` call `readCustomer(...)` only to prove existence; they do not deny suspended Customer targets.
- `WorkstreamService.customerAdminInvitationCreateSurface(...)` currently does not validate the target Customer at all due to FCB-RD-03.

Impact:

- Customer Admin list/invite/manage may remain available for suspended Customer boundaries, contrary to fail-closed lifecycle intent.

Suggested task:

- Add operation-specific active Customer validation for Customer Admin list/invite/manage actions, with safe `customer-suspended` denial/system-message behavior and tests.

### FCB-RD-07: Customer Admin list/manage API behavior is under-tested

Severity: medium-high

Source-of-truth expectation:

- Tests must cover Customer lifecycle, Customer Admin bootstrap/maintenance, forbidden Customer Admin tenant actions, cross-customer denial, role escalation, last-customer-admin protection, idempotency, audit/work trace, redaction, and frontend non-authority.

Runtime evidence:

- `AdminEndpointIntegrationTest` currently covers Organization admin APIs deeply and seeds a Customer Admin actor only for Organization denial, but does not exercise `/api/admin/customers...` lifecycle or Customer Admin list/invite/role/status APIs.
- `WorkstreamServiceTest.tenantCustomerBranchUsesDurableCustomerLifecycleState` covers Customer create/list/read/suspend and SaaS Owner denial, but not Customer Admin branch target propagation, Customer Admin invitation, role/status management, last-customer-admin protection, or suspended Customer denial.
- Frontend tests assert presence of customer surface contracts but do not prove target-customer payload propagation or Customer Admin role safety.

Impact:

- High-risk drifts above are not caught by the current test suite.

Suggested task:

- Add targeted backend integration tests first, then frontend/workstream contract tests for Customer Admin target propagation and role safety.

### FCB-RD-08: App-description and runtime action ids are not fully normalized

Severity: medium

Source-of-truth expectation:

- The app-description names Customer branch action edges including `action-user-admin-show-customers` and also, in the functional-agent binding, `action-customer-list`, `action-customer-admin-list`, `action-customer-admin-invite`, and `action-customer-admin-manage`.

Runtime evidence:

- Runtime uses `action-user-admin-show-customers`, `action-customer-read`, `action-open-customer-*`, `action-customer-*`, `action-user-admin-show-customer-admins`, and `action-open-customer-admin-invitation-create`.
- There is no runtime `action-customer-list`, `action-customer-admin-list`, `action-customer-admin-invite`, or `action-customer-admin-manage` action.

Impact:

- Future implementers may not know whether to add aliases, rename runtime actions, or adjust the app-description to canonical runtime action ids. This is not as severe as the target/role-safety bugs but should be resolved to avoid more drift.

Suggested task:

- Normalize Customer action ids in app-description and runtime/tests, or add explicit compatibility aliases with one canonical set.

## Proposed task queue

If turning this audit into implementation work, use these bounded tasks in order:

1. **Customer Admin role-safety backend repair**
   - Add target-scope-aware role validation for Customer Admin invitation and membership role changes.
   - Deny tenant/SaaS Owner roles through Customer Admin flows.
   - Add service/API tests for role-escalation denial.

2. **Customer Admin target propagation and invitation workstream repair**
   - Carry `customerId` from Customer detail rows/actions into Customer Admin list/invite/detail surfaces.
   - Add dedicated `ScopeType.CUSTOMER` workstream action path for Customer Admin invitation.
   - Add tests for Customer detail -> Customer Admins -> Invite Customer Admin.

3. **Suspended Customer fail-closed repair**
   - Deny Customer Admin list/invite/manage when target Customer is suspended.
   - Return safe system-message/API denial and audit trace.
   - Add tests.

4. **Customer list/search parity repair**
   - Propagate query/status filters through `/api/admin/customers` and workstream Customer directory.
   - Add tests proving filtered behavior and no hidden leakage.

5. **Customer Admin coverage and frontend contract hardening**
   - Add endpoint/workstream tests for Customer lifecycle, Customer Admin list/invite/status/role, last-customer-admin, cross-customer denial, redaction, and audit.
   - Add frontend contract tests for target-customer payload propagation, role-safe options, and no client-side authority.

6. **Action-id normalization**
   - Pick canonical Customer branch action ids and update app-description/runtime/tests or add explicit compatibility aliases.

## Conclusion

The implemented Customer lifecycle foundation is a good start, but runtime behavior does **not** fully match the app-description. The highest priority is Customer Admin target/role-safety repair because it touches authority scope and could create wrong-scope invitations or role assignments. The next safe step is to create a focused implementation queue from the proposed tasks above and execute one repair task per fresh harness session.
