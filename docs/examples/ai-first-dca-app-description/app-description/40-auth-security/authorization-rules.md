# Authorization Rules

## Purpose

Define the backend-enforced authorization contract for the DCA vertical reference, aligned with `../10-capabilities/01-secure-tenant-user-foundation.md`. All protected DCA behavior is authorized from Akka-owned `Account`, `Membership`, `Role`, `Permission/Capability`, selected `AuthContext`, and tenant/customer scope.

## Principal categories

Foundation role baseline for generated SaaS apps:

| Foundation role | Scope | Authority |
|---|---|---|
| `SAAS_OWNER_ADMIN` | SaaS Owner | Manage platform users, Tenants, Tenant Admin bootstrap, subscription/billing boundary, and platform-safe metadata without direct Tenant application-data access. |
| `TENANT_ADMIN` | assigned tenant | Manage tenant users, invitations, memberships, customer organizations, support-access grants, tenant settings, and DCA role/capability mappings. |
| `TENANT_EMPLOYEE` | assigned tenant | Use tenant-owned DCA functionality according to named capability grants. |
| `CUSTOMER_ADMIN` | assigned customer | Manage customer-scoped users and view allowed customer/device activity. |
| `CUSTOMER_USER` | assigned customer | Access customer-facing DCA features allowed by membership and capability. |
| `AUDITOR` | assigned SaaS Owner, tenant, or customer scope | Read audit/work/decision traces and admin audit entries within scope without mutation. |

DCA-specific extension roles map onto foundation roles and named capabilities; they do not replace membership status, scope checks, or foundation authority:

| DCA role | Foundation mapping | Authority |
|---|---|---|
| `DEALER_OWNER` | tenant-scoped app role extending `TENANT_ADMIN` or high-authority `TENANT_EMPLOYEE` capabilities | Own business outcomes, approve high-impact DCA decisions, and view tenant-wide objectives/outcomes. |
| `OPERATIONS_SUPERVISOR` | tenant/customer-scoped app role extending operational membership capabilities | Supervise delegated work, supply decisions, exceptions, and operational traces. |
| `POLICY_OWNER` | tenant-scoped app role extending policy-governance capabilities | Manage policy proposals, simulations, and governed commits within scope. |
| `APP_ADMIN` | legacy DCA reference alias for explicitly mapped SaaS Owner/bootstrap capabilities | May remain in example notes only; new generated apps should prefer `SAAS_OWNER_ADMIN`. |
| `USER` | app-specific user alias mapped to `TENANT_EMPLOYEE` or `CUSTOMER_USER` capabilities | Access authenticated baseline surfaces allowed by role/scope. |

## Backend authorization defaults

- Every protected capability requires authenticated account, active account status, selected `AuthContext`, active membership, matching tenant/customer scope where applicable, and required named permission/capability.
- Every `/api/...` route requires JWT unless explicitly public; every protected route, component command, view query, stream, workflow action, consumer side effect, timer action, and agent tool must call backend authorization.
- Frontend navigation, hidden buttons, cached `/api/me`, prompt instructions, or JWT role claims are never authorization controls.
- `DISABLED` accounts, inactive memberships, expired support-access memberships, missing capabilities, and cross-tenant/customer requests are denied even with a valid WorkOS token.
- Tenant-scoped roles may only list, query, mutate, or act within assigned tenant ids.
- Customer-scoped roles may only list, query, mutate, or act within assigned customer ids.
- SaaS Owner Admins may manage platform-safe metadata, Tenant bootstrap, and subscription/billing boundary records; they may not access Tenant application data without Tenant-created support-access.
- Invitation onboarding is the normal path for privileged user activation; WorkOS claims alone must not create privileged users, Tenant access, Customer access, support access, or DCA authority.
- No actor may grant a role, permission, support-access, or scope wider than their own authority.
- Last-admin removal, high-privilege role assignment, account relink/reset, bulk admin actions, tenant suspension/reactivation, and billing overrides require policy evaluation and decision-card approval when configured.

## Capability-oriented authorization matrix

| Capability/surface family | Minimum authority | Backend rule |
|---|---|---|
| `/api/me` | valid WorkOS JWT plus local account/invite or accepted membership policy | returns only caller-safe account/profile/settings/membership/context/capability data; disabled users are denied |
| SaaS Owner/platform administration | `SAAS_OWNER_ADMIN` or explicit legacy DCA `APP_ADMIN` alias | manage platform-safe Tenant bootstrap and billing metadata only; all changes are audited |
| Tenant administration | assigned `TENANT_ADMIN` | manage tenant users, invitations, memberships, roles, support-access, customer organizations, and tenant settings inside assigned tenant |
| Customer administration | assigned `TENANT_ADMIN` or assigned `CUSTOMER_ADMIN` | manage customer-scoped users and memberships inside assigned tenant/customer scope |
| Invitation lifecycle | scoped admin with invitation/manage capability | create local account/membership intent, deliver or capture email, resend, revoke/cancel, expire, accept, expose delivery failure, and audit every lifecycle step without exposing raw tokens |
| Support access | Tenant Admin grant plus accepted support operator membership | time-limited, reasoned, visible, revocable, audited; no global super-admin bypass |
| SaaS Owner billing boundary | `SAAS_OWNER_ADMIN` with billing capability | manage billing-safe Tenant metadata, plans, subscriptions, invoices/payment summaries, and entitlement state without reading Tenant application data |
| Supplies decisions/exceptions | `DEALER_OWNER` or `OPERATIONS_SUPERVISOR` with matching scope and supply capability | actions target decision cards/workflows in actor tenant/customer/device scope and honor policy gates |
| Policy proposals | `POLICY_OWNER`, `DEALER_OWNER`, or permitted supervisor within scope | proposals may be drafted without activation authority |
| Policy commits/authority expansion | `POLICY_OWNER` or approved admin role plus approval gate and matching scope | activation requires simulation/replay evidence and audit/decision trace |
| Audit/work/decision trace search | `AUDITOR`, scoped admin, or scoped supervisory role | results are server-filtered by tenant/customer scope and redaction class |
| Agent tools | agent/component principal plus delegator/workflow scope and named capability grant | read/draft/recommend by default; side effects require backend gates, policy, approval, and audit |

## Denial behavior

- Missing or invalid token: return `401` for API calls.
- Valid token but no local account/invite: return `403` or explicit pending-invite/not-invited response from `/api/me` according to implementation policy.
- Disabled account, inactive membership, expired support access, missing capability, privilege escalation, or cross-scope access: return `403` and avoid leaking unrelated tenant/customer resource existence.
- Not-found may be used instead of forbidden where revealing the target's existence would leak cross-scope data.
- Denials for protected admin reads, mutation attempts, policy checks, support-access use, and agent/tool calls should create audit/work-trace facts with permission checked, scope, reason category, and correlation id.

## Expected admin API families

The reference may expose API families such as:

```text
GET  /api/me
GET  /api/admin/users
POST /api/admin/users/invite
POST /api/admin/users/{userId}/roles
POST /api/admin/users/{userId}/disable
POST /api/admin/users/{userId}/activate
GET  /api/admin/invitations
GET  /api/admin/invitations/{invitationId}
POST /api/admin/invitations/{invitationId}/resend
POST /api/admin/invitations/{invitationId}/revoke
GET  /api/admin/invitations/{invitationId}/audit
GET  /api/admin/audit
GET  /api/tenants
POST /api/tenants
GET  /api/tenants/{tenantId}/customers
POST /api/tenants/{tenantId}/customers
```

These are exposure surfaces for `secure-tenant-user-foundation`; implementation must preserve the capability contract rather than treating routes as the root authority model.

## Optional impersonation

Impersonation is not accepted by default.

If later enabled, it must be restricted to explicitly approved support-access or SaaS Owner support policy, visibly display actor/effective-user context, require reason and scope, write audit events for start/use/end, and remain excluded from high-risk policy commits unless separately approved.
