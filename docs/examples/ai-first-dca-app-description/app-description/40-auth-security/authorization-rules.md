# Authorization Rules

## Principal categories

Initial seed-app roles:

| Role | Scope | Authority |
|---|---|---|
| `APP_ADMIN` | all tenants | Manage seed users, tenants, customers, role assignments, and security audit visibility. |
| `DEALER_OWNER` | assigned tenant | Own business outcomes, approve high-impact DCA decisions, and view tenant-wide objectives/outcomes. |
| `OPERATIONS_SUPERVISOR` | assigned tenant/customer | Supervise active delegated work, supply decisions, exceptions, and operational traces. |
| `POLICY_OWNER` | assigned tenant or all tenants | Manage policy proposals, simulations, and governed commits within scope. |
| `AUDITOR` | assigned tenant/customer or all tenants | Read audit/work/decision traces and admin audit entries within scope. |
| `CUSTOMER_ADMIN` | assigned customer | Manage customer-scoped users and view allowed customer/device activity. |
| `USER` | self or assigned scope | Access authenticated baseline surfaces allowed by role/scope. |

A future implementation may keep role constants smaller for the first seed, but it must preserve tenant/customer scope and retained-human-authority semantics.

## Backend authorization defaults

- Every `/api/...` route requires JWT unless explicitly marked public.
- Every protected operation checks local account status is `ACTIVE`.
- `DISABLED` users are denied even with a valid WorkOS token.
- `APP_ADMIN` may manage all local users, tenants, customers, and roles.
- Tenant-scoped roles may only list or mutate data in assigned tenant ids.
- Customer-scoped roles may only list or mutate data in assigned customer ids.
- No non-`APP_ADMIN` role may grant `APP_ADMIN`.
- Role assignment may not widen the actor's own scope.
- Supplies decision actions require a role with decision authority for the target tenant/customer/device context.
- Policy activation or expanded agent authority requires an explicit human-governed role such as `POLICY_OWNER` or `APP_ADMIN` plus any configured approval gate.

## Seed surface authorization matrix

| Surface or API family | Minimum authority | Backend rule |
|---|---|---|
| `/api/me` | valid WorkOS JWT plus local account link/invite | returns only caller-safe account/status/role/scope data; disabled users are denied |
| app-admin user invites and role assignment | `APP_ADMIN` | may grant roles only within app-wide policy; all changes are audited |
| tenant administration | `APP_ADMIN` or assigned `TENANT_ADMIN` | tenant-scoped admins cannot escape assigned tenant ids or grant broader scopes |
| customer administration | `APP_ADMIN`, assigned `TENANT_ADMIN`, or assigned `CUSTOMER_ADMIN` | customer-scoped admins are limited to assigned customer ids |
| supplies approvals/exceptions | `DEALER_OWNER` or `OPERATIONS_SUPERVISOR` with matching scope | actions must target a decision card in the actor's tenant/customer/device scope |
| policy proposals | `POLICY_OWNER`, `DEALER_OWNER`, or permitted supervisor within scope | proposals may be drafted without activation authority |
| policy commits or authority expansion | `POLICY_OWNER` or `APP_ADMIN` plus required approval gate | activation requires simulation/replay evidence and audit trace |
| audit/work/decision trace search | `AUDITOR`, `APP_ADMIN`, or scoped supervisory role | trace rows are filtered by tenant/customer scope and redaction class |

## Denial behavior

- Missing or invalid token: return `401` for API calls.
- Valid token but no local account/invite: return `403` or an explicit pending-invite response from `/api/me` according to implementation policy.
- Disabled local account: return `403` and do not expose privileged data.
- Cross-tenant/customer access: return `403`; do not rely on frontend hiding.
- Privilege escalation attempt: return `403` and write an audit/security trace when feasible.

## Admin API families

Expected seed APIs:

```text
GET  /api/me
GET  /api/admin/users
POST /api/admin/users/invite
POST /api/admin/users/{userId}/roles
POST /api/admin/users/{userId}/disable
POST /api/admin/users/{userId}/activate
GET  /api/tenants
POST /api/tenants
GET  /api/tenants/{tenantId}/customers
POST /api/tenants/{tenantId}/customers
```

## Optional impersonation

The PoC includes app-admin impersonation guidance. For the DCA seed app, impersonation is **not accepted by default**.

If later enabled, it must be:

- restricted to `APP_ADMIN` or an explicitly approved support role;
- visible in UI as effective-user context;
- logged with actor id, effective user id, target scope, reason, and request context;
- excluded from high-risk policy commits unless separately approved.
