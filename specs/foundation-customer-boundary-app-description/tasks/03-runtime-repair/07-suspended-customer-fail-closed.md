# TASK-FCBAD-02-003: Enforce suspended Customer fail-closed behavior for Customer Admin operations

## Objective

Ensure Customer Admin list, invitation, and management operations fail closed when the target foundation Customer boundary is suspended.

## Source finding

- `runtime-audit/foundation-customer-boundary-runtime-drift-audit.md#fcb-rd-06-suspended-customer-does-not-fail-closed-for-customer-admin-operations`

## Required reads

- `AGENTS.md`
- `specs/foundation-customer-boundary-app-description/runtime-audit/foundation-customer-boundary-runtime-drift-audit.md`
- `app-description/domains/core-starter/data-state/auth-context-and-membership-state.md`
- `app-description/domains/core-starter/capabilities/user-and-access-administration.md`
- `src/main/java/ai/first/application/coreapp/useradmin/TenantCustomerAdminService.java`
- `src/main/java/ai/first/api/coreapp/admin/AdminEndpoint.java`
- `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`
- relevant tests from prior tasks
- this task brief

## Implementation scope

- Add operation-specific active Customer validation for Customer Admin list/invite/manage actions.
- Keep Customer read/detail available for suspended Customers so admins can reactivate or inspect lifecycle state.
- Deny Customer Admin operations on suspended Customers with a safe reason such as `customer-suspended` or equivalent.
- Ensure workstream paths return a safe system-message/denial surface where applicable.
- Ensure API paths return safe 403/409/404 behavior per existing endpoint conventions without sibling-customer leakage.

## Required tests

- Customer detail for a suspended Customer is readable and shows reactivate path.
- Customer Admin list/invite/manage for a suspended Customer is denied or returns safe system-message.
- Denial is audited/traced where existing services support it.
- Reactivating the Customer restores Customer Admin branch availability.

## Required checks

- `mvn -Dtest=ai.first.application.coreapp.workstream.WorkstreamServiceTest,ai.first.application.coreapp.useradmin.AdminEndpointIntegrationTest test`
- `git diff --check`

## Done criteria

- Suspended Customers cannot be used for Customer Admin bootstrap/maintenance.
- Existing Customer lifecycle read/reactivate behavior remains intact.
- Tests prove denial and reactivation behavior.
- Queue status and notes are updated and committed with code/test changes.

## Vertical workstream contract

- Workstream / functional agent: User Admin / `user-admin-agent`.
- Attention category or non-attention reason: lifecycle security repair; no new attention item.
- Surface graph: Customer detail/read remains available; Customer Admin branch operations denied while suspended.
- Governed-tool id and exposure: `manage-customer-admins` browser-tool/API/workstream exposure.
- Capability id: `tenant.customer_admin.*` guarded by active Customer state.
- AuthContext / roles / tenant scope: Organization/Tenant Admin, selected tenant, target Customer, suspended fail-closed.
- Akka substrate: HTTP endpoint/service/workstream deterministic validation and tests.
- Audit/work trace requirements: safe denial reason and correlation id.
- Local validation path: focused Maven test plus `git diff --check`.
