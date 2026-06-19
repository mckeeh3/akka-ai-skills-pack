# Foundation Customer Boundary Runtime Repair Verification

Date: 2026-06-16

## Scope

Terminal verification for `TASK-FCBAD-02-007`, covering the bounded drift findings recorded in `foundation-customer-boundary-runtime-drift-audit.md` after the repair tasks `TASK-FCBAD-02-001` through `TASK-FCBAD-02-006`.

Authoritative app-description nodes reviewed:

- `app-description/domains/core-starter/capabilities/user-and-access-administration.md`
- `app-description/domains/core-starter/data-state/auth-context-and-membership-state.md`
- `app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md`
- `app-description/domains/core-starter/workstreams/user-admin/agents/functional-agent.md`
- `app-description/domains/core-starter/workstreams/user-admin/realization/akka-components.md`
- `app-description/domains/core-starter/workstreams/user-admin/realization/frontend-routes.md`
- `app-description/domains/core-starter/workstreams/user-admin/tests/coverage.md`

Runtime and test evidence reviewed:

- `src/main/java/ai/first/application/coreapp/useradmin/TenantCustomerAdminService.java`
- `src/main/java/ai/first/application/coreapp/useradmin/UserAdminService.java`
- `src/main/java/ai/first/application/foundation/invitation/InvitationService.java`
- `src/main/java/ai/first/api/coreapp/admin/AdminEndpoint.java`
- `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`
- `frontend/src/workstream/surfaces/UserAdminScopedAdminSurface.tsx`
- `src/test/java/ai/first/application/coreapp/workstream/WorkstreamServiceTest.java`
- `src/test/java/ai/first/application/coreapp/useradmin/AdminEndpointIntegrationTest.java`
- `frontend/src/workstream-user-admin-vertical.contract.test.mjs`

## Verification answer

Yes. For the bounded Customer lifecycle and Customer Admin branch drift scope, the implemented foundation Customer boundary runtime now matches the active app-description closely enough to treat this runtime repair queue as complete.

No follow-up runtime repair task is required for the bounded findings verified here.

## Checklist evidence

### Customer lifecycle API and workstream paths

Status: verified.

Evidence:

- Customer lifecycle is exposed through `/api/admin/customers` and `/api/admin/customers/{customerId}` routes plus create, rename, suspend, and reactivate routes.
- Workstream Customer actions use canonical backend-authored action ids such as `action-user-admin-show-customers`, `action-customer-read`, `action-customer-create`, `action-customer-rename`, `action-customer-suspend`, and `action-customer-reactivate`.
- Tests cover selected tenant scope, idempotent create/replay behavior, redaction, safe hidden/cross-scope denials, and audit evidence.
- `WorkstreamServiceTest.tenantCustomerBranchUsesDurableCustomerLifecycleState` verifies Customer create/list/read/suspend/reactivate behavior through workstream actions.
- `AdminEndpointIntegrationTest` covers Customer create/read/rename/suspend/reactivate/idempotency and redacted account/customer evidence through HTTP routes.

### Customer Admin API target proof and role safety

Status: verified.

Evidence:

- Customer Admin APIs are under `/api/admin/customers/{customerId}/admins...`, preserving explicit target Customer proof in the route.
- Target-scope-aware role validation now prevents `TENANT_ADMIN` and `SAAS_OWNER_ADMIN` assignment through Customer Admin invitation and membership-management flows.
- Tests cover default `CUSTOMER_ADMIN` invitation roles, role escalation denial, last-customer-admin protection, cross-customer/sibling denial, and audit records for allowed/denied Customer Admin operations.

### Customer Admin workstream target propagation and customer-scoped invitation

Status: verified.

Evidence:

- Workstream Customer Admin list and invitation surfaces preserve backend-authored `customerId` and `customerName` in payloads.
- `action-user-admin-show-customer-admins` opens Customer Admin list for the selected Customer.
- `action-open-customer-admin-invitation-create` opens the selected-Customer invite form.
- `action-customer-admin-invite` submits a dedicated customer-scoped invitation using `ScopeType.CUSTOMER` and the selected `customerId`; generic tenant-scoped invitation behavior remains separate.
- `WorkstreamServiceTest.tenantCustomerBranchUsesDurableCustomerLifecycleState` verifies Customer detail -> Customer Admins -> Invite Customer Admin and asserts the resulting invitation is customer-scoped.
- `frontend/src/workstream-user-admin-vertical.contract.test.mjs` verifies frontend target payload/action preservation for the Customer Admin branch.

### Suspended Customer fail-closed behavior

Status: verified.

Evidence:

- Suspended Customers remain readable through detail surfaces and can be reactivated.
- Customer Admin list, invite, and management operations fail closed while the target Customer is suspended.
- Workstream denials return `surface-user-admin-system-message`, `reasonCode=customer-suspended`, and `noFakeSuccess=true`.
- Endpoint tests cover Customer Admin API denials for suspended Customers and recovery after reactivation.
- Workstream tests cover suspended Customer Admin list/invite denials, audit reason `customer-suspended`, and restored branch availability after reactivation.

### Customer list/search filter parity

Status: verified.

Evidence:

- `/api/admin/customers` reads `query` and `status` query parameters through request context and applies them through `TenantCustomerAdminService.listCustomers`.
- Workstream `action-user-admin-show-customers` preserves safe `query` and `status` filter input and returns filtered directory payloads.
- Tests prove query filtering, active/suspended status filtering, filtered empty state, cross-tenant omission, and no hidden-count leakage.

### Frontend scoped admin surfaces

Status: verified.

Evidence:

- `UserAdminScopedAdminSurface` preserves backend-authored Customer Directory filters and Customer Admin target context in submitted payloads.
- Frontend tests assert Customer Admin invitation action preference, role-safe options, canonical action ids, and absence of client-side authority inference.
- Browser behavior remains an action-submission/rendering path; backend capabilities and authorization stay authoritative.

### Action ids

Status: verified.

Evidence:

- App-description and runtime use canonical Customer branch action ids.
- `action-customer-list`, `action-customer-admin-list`, and `action-customer-admin-manage` are documented as non-active shorthand names, not live aliases.
- Frontend contract tests verify canonical action ids are present in app-description/runtime evidence and non-active shorthand ids are not emitted as runtime `SurfaceAction`s.

## Targeted proof commands

```bash
rg -n "customer-suspended|action-customer-admin-invite|action-user-admin-show-customer-admins|action-customer-list|action-customer-admin-list|action-customer-admin-manage|CUSTOMER_ADMIN|TENANT_ADMIN|SAAS_OWNER_ADMIN" \
  app-description/domains/core-starter/workstreams/user-admin \
  app-description/domains/core-starter/capabilities/user-and-access-administration.md \
  src/main/java/ai/first/application/coreapp \
  src/main/java/ai/first/api/coreapp/admin \
  src/test/java/ai/first/application/coreapp \
  frontend/src/workstream-user-admin-vertical.contract.test.mjs \
  frontend/src/workstream/surfaces/UserAdminScopedAdminSurface.tsx
```

This proof finds active app-description/runtime/test coverage for suspended-Customer denials, canonical Customer Admin action ids, explicit non-active shorthand action-id documentation, and Customer Admin-safe role boundaries.

```bash
rg -n "query|status|sibling|redact|customerId|last-customer|action-customer-admin-invite" \
  src/test/java/ai/first/application/coreapp/workstream/WorkstreamServiceTest.java \
  src/test/java/ai/first/application/coreapp/useradmin/AdminEndpointIntegrationTest.java \
  frontend/src/workstream-user-admin-vertical.contract.test.mjs
```

This proof finds targeted tests for Customer list/search filters, sibling/customer redaction, selected `customerId` propagation, last-customer-admin safety, and dedicated Customer Admin invitation action behavior.

## Required checks

```bash
mvn -Dtest=ai.first.application.coreapp.workstream.WorkstreamServiceTest,ai.first.application.coreapp.useradmin.AdminEndpointIntegrationTest test
```

Result: passed. Maven reported 77 tests, 0 failures, 0 errors, 0 skipped.

```bash
npm --prefix frontend test -- --run frontend/src/workstream-user-admin-vertical.contract.test.mjs
```

Result: passed. The repository frontend test script executed the contract suite and reported 154 passing tests.

```bash
git diff --check
```

Result: passed.

## Readiness/impact conclusion

Readiness state for this bounded SaaS Foundation App maintenance scope: `ready`.

The repair work is localized to the User Admin Customer lifecycle and Customer Admin branch runtime, app-description action-id normalization, tests, and verification artifacts. The active app-description remains sufficient for this bounded scope; no pending question or additional runtime repair task is needed.
