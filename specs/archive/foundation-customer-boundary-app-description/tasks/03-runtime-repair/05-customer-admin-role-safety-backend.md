# TASK-FCBAD-02-001: Repair Customer Admin role-safety backend enforcement

## Objective

Fix the security-sensitive runtime drift where Customer Admin invitation and membership-management APIs can accept roles outside the Customer Admin-safe role set when invoked from an Organization/Tenant Admin context.

## Source findings

- `specs/foundation-customer-boundary-app-description/runtime-audit/foundation-customer-boundary-runtime-drift-audit.md#fcb-rd-05-customer-admin-role-safety-allows-organizationtenant-roles-through-customer-admin-apis`
- Current intent: `app-description/domains/core-starter/capabilities/user-and-access-administration.md`
- Current intent: `app-description/domains/core-starter/data-state/auth-context-and-membership-state.md`

## Required reads

- `AGENTS.md`
- `specs/foundation-customer-boundary-app-description/runtime-audit/foundation-customer-boundary-runtime-drift-audit.md`
- `app-description/domains/core-starter/capabilities/user-and-access-administration.md`
- `app-description/domains/core-starter/data-state/auth-context-and-membership-state.md`
- `src/main/java/ai/first/api/coreapp/admin/AdminEndpoint.java`
- `src/main/java/ai/first/application/foundation/invitation/InvitationService.java`
- `src/main/java/ai/first/application/coreapp/useradmin/UserAdminService.java`
- `src/test/java/ai/first/application/coreapp/useradmin/AdminEndpointIntegrationTest.java`
- this task brief

## Implementation scope

- Add target-scope-aware role validation for Customer Admin invitation creation and Customer Admin membership role changes.
- For `ScopeType.CUSTOMER`, allow only Customer Admin/customer-safe roles currently accepted by product intent; at minimum `CUSTOMER_ADMIN`.
- Deny `TENANT_ADMIN`, `SAAS_OWNER_ADMIN`, and other tenant/platform roles through Customer Admin flows regardless of actor selected scope.
- Preserve existing generic tenant/user invitation behavior outside Customer Admin-specific APIs.
- Ensure denial reason is browser-safe, auditable, and does not expose hidden target state.

## Required tests

Add or update focused backend tests proving:

- `/api/admin/customers/{customerId}/admins/invitations` defaults to `CUSTOMER_ADMIN` when roles are omitted.
- The same endpoint denies `TENANT_ADMIN` and `SAAS_OWNER_ADMIN` role requests.
- `/api/admin/customers/{customerId}/admins/{accountId}/roles` denies replacing a Customer Admin with `TENANT_ADMIN` or `SAAS_OWNER_ADMIN`.
- Denials are safe 403/validation outcomes and do not create/update memberships.

## Required checks

- `mvn -Dtest=ai.first.application.coreapp.useradmin.AdminEndpointIntegrationTest test`
- `git diff --check`

## Done criteria

- Customer Admin invitation and role-management APIs cannot grant Organization Admin or SaaS Owner authority.
- Tests fail before the fix or would have failed against the drift, and pass after the fix.
- No frontend authority assumptions are added.
- Queue status and notes are updated and committed with the code/test changes.

## Vertical workstream contract

- Workstream / functional agent: User Admin / `user-admin-agent`.
- Attention category or non-attention reason: security repair; no new attention item.
- Role-specific dashboard / surface: Customer Admin branch under `surface-user-admin-customer-detail` and `/api/admin/customers/{customerId}/admins...`.
- Surface graph node/action edge: Customer Admin invitation and role-management actions.
- Governed-tool id and exposure: `manage-customer-admins` / browser-tool API exposure.
- Capability id: `tenant.customer_admin.invite`, `tenant.customer_admin.manage`.
- AuthContext / roles / tenant scope: Organization/Tenant Admin actor, selected tenant, target Customer, deny non-Customer-safe roles.
- Akka substrate: HTTP endpoint + deterministic application service validation + tests.
- API / frontend / realtime path: `/api/admin/customers/{customerId}/admins/invitations`, `/api/admin/customers/{customerId}/admins/{accountId}/roles`.
- Audit/work trace requirements: safe denial/audit where current services support it; no raw provider/token/hidden data.
- Local validation path: focused Maven test plus `git diff --check`.
