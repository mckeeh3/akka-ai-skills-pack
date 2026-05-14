# Authorization Rules

## Principal categories

Foundation role baseline for generated SaaS apps:

| Foundation role | Scope | Authority |
|---|---|---|
| `SAAS_OWNER_ADMIN` | SaaS Owner | Manage platform users, Tenants, Tenant Admin bootstrap, and platform-safe metadata without direct Tenant application-data access. |
| `TENANT_ADMIN` | assigned tenant | Manage tenant users, memberships, customer organizations, support-access grants, and tenant settings. |
| `TENANT_EMPLOYEE` | assigned tenant | Use tenant-owned application functionality according to DCA capability mappings. |
| `CUSTOMER_ADMIN` | assigned customer | Manage customer-scoped users and view allowed customer/device activity. |
| `CUSTOMER_USER` | assigned customer | Access customer-facing DCA features allowed by role/scope. |
| `AUDITOR` | assigned SaaS Owner, tenant, or customer scope | Read audit/work/decision traces and admin audit entries within scope without mutation. |

DCA-specific extension roles map to foundation scopes and capabilities; they are not the generic SaaS baseline:

| DCA role | Foundation mapping | Authority |
|---|---|---|
| `APP_ADMIN` | legacy seed alias for `SAAS_OWNER_ADMIN` plus explicit DCA bootstrap capabilities | Manage seed users, tenants, customers, role assignments, and security audit visibility in this reference example only. Prefer `SAAS_OWNER_ADMIN` for new generated apps. |
| `DEALER_OWNER` | tenant-scoped app role extending `TENANT_ADMIN` or `TENANT_EMPLOYEE` capabilities | Own business outcomes, approve high-impact DCA decisions, and view tenant-wide objectives/outcomes. |
| `OPERATIONS_SUPERVISOR` | tenant/customer-scoped app role extending tenant/customer membership capabilities | Supervise active delegated work, supply decisions, exceptions, and operational traces. |
| `POLICY_OWNER` | tenant-scoped app role extending policy-governance capabilities | Manage policy proposals, simulations, and governed commits within scope. |
| `USER` | app-specific user alias mapped to `TENANT_EMPLOYEE` or `CUSTOMER_USER` capabilities | Access authenticated baseline surfaces allowed by role/scope. |

A future implementation may keep role constants smaller for the first seed, but it must preserve the canonical foundation roles, tenant/customer scope, support-access boundaries, and retained-human-authority semantics.

## Backend authorization defaults

- Every `/api/...` route requires JWT unless explicitly marked public.
- Every protected operation checks local account status is `ACTIVE`.
- `DISABLED` users are denied even with a valid WorkOS token.
- `SAAS_OWNER_ADMIN` is the canonical SaaS Owner admin role; in this DCA reference, `APP_ADMIN` is a legacy app-specific alias mapped to SaaS Owner/bootstrap capabilities.
- Tenant-scoped roles may only list or mutate data in assigned tenant ids.
- Customer-scoped roles may only list or mutate data in assigned customer ids.
- No non-`SAAS_OWNER_ADMIN` role may grant SaaS Owner administration; no non-`APP_ADMIN` role may grant the DCA reference alias `APP_ADMIN`.
- Role assignment may not widen the actor's own scope.
- Supplies decision actions require a role with decision authority for the target tenant/customer/device context.
- Policy activation or expanded agent authority requires an explicit human-governed app role such as `POLICY_OWNER` plus tenant/customer authority, or `SAAS_OWNER_ADMIN`/DCA `APP_ADMIN` where platform/bootstrap scope is appropriate, plus any configured approval gate.

## Seed surface authorization matrix

| Surface or API family | Minimum authority | Backend rule |
|---|---|---|
| `/api/me` | valid WorkOS JWT plus local account link/invite | returns only caller-safe account/status/role/scope data; disabled users are denied |
| SaaS Owner/platform administration | `SAAS_OWNER_ADMIN` or DCA `APP_ADMIN` alias | may bootstrap Tenants and seed role assignments only within platform policy; all changes are audited |
| tenant administration | assigned `TENANT_ADMIN`; `SAAS_OWNER_ADMIN`/DCA `APP_ADMIN` only for platform-safe bootstrap metadata | tenant-scoped admins cannot escape assigned tenant ids or grant broader scopes |
| customer administration | assigned `TENANT_ADMIN` or assigned `CUSTOMER_ADMIN`; `SAAS_OWNER_ADMIN`/DCA `APP_ADMIN` only for platform-safe bootstrap metadata | customer-scoped admins are limited to assigned customer ids |
| supplies approvals/exceptions | `DEALER_OWNER` or `OPERATIONS_SUPERVISOR` with matching scope | actions must target a decision card in the actor's tenant/customer/device scope |
| policy proposals | `POLICY_OWNER`, `DEALER_OWNER`, or permitted supervisor within scope | proposals may be drafted without activation authority |
| policy commits or authority expansion | `POLICY_OWNER`, `SAAS_OWNER_ADMIN`, or DCA `APP_ADMIN` alias plus required approval gate and matching scope | activation requires simulation/replay evidence and audit trace |
| audit/work/decision trace search | `AUDITOR`, `SAAS_OWNER_ADMIN`, DCA `APP_ADMIN` alias, or scoped supervisory role | trace rows are filtered by tenant/customer scope and redaction class |

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

- restricted to `SAAS_OWNER_ADMIN` or the DCA `APP_ADMIN` alias, plus an explicitly approved support role/membership;
- visible in UI as effective-user context;
- logged with actor id, effective user id, target scope, reason, and request context;
- excluded from high-risk policy commits unless separately approved.
