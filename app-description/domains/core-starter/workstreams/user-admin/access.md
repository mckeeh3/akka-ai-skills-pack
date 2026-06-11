# Access: User Admin

## Authorized roles

- `app-admin`: app-owner level; manages App Admin users and Tenant Admin users for SMB tenant accounts. Does not automatically manage tenant employees, Customer Admins, or Customer Users without an explicit backend-authorized selected context.
- `saas-owner-admin`: SaaS Owner selected context with backend `saas_owner.tenant.read` may list/read platform-safe Organizations; with `saas_owner.tenant.manage` may create, rename, suspend, and reactivate Organizations. Organization is the browser/API term; backend authorization, audit, and state use Tenant boundaries. This role does not receive tenant/customer application-data access, support access, raw provider data, hidden counts, or billing-derived authority by implication.
- `tenant-admin`: SMB tenant level; manages tenant employee access and Customer Admin users for customers that the tenant provides access to. Cannot manage other tenants or App Admin users.
- `customer-admin`: customer level; manages only selected Customer Users, invitations, customer roles, and customer access-review evidence permitted by customer policy. Cannot manage tenant employees, tenant-level roles/settings, other customers, or App Admin/Tenant Admin accounts.
- `auditor`: read scoped evidence and audit/access-review traces where permitted; no mutation.
- `saas-owner-support`: support-only level; may view or explain tenant/customer user data only through an active tenant-created support-access grant and audited selected context. This is separate from App Admin authority.

Role names are UI/intent labels only. Backend capability grants, selected `AuthContext`, membership state, approval policy, and resource ownership are authoritative.

## Scope rules

All reads, writes, surface actions, streams, agent turns, skill/reference loads, internal worker tasks, and governed-tool invocations use backend-owned selected `AuthContext`, tenant/customer ids, account status, membership status, role/capability grants, support-access state, resource ownership, and approval policy. Organization Admin calls additionally require a SaaS Owner scoped selected context and map browser Organization ids to internal Tenant ids only inside backend-authorized service/API logic.

Frontend rail visibility, disabled buttons, hidden actions, prompt text, expertise text, and client filters never grant authority. Direct API/deep-link requests still receive backend authorization and safe denials.

## Required denials

Server-side denials include disabled actor, inactive membership, missing selected context, missing capability, SaaS Owner Admin without SaaS Owner context, SaaS Owner Admin missing `saas_owner.tenant.read/manage`, App Admin attempting tenant/customer user management without explicit selected-context authority, Tenant Admin attempting App Admin, Organization Admin, or cross-tenant administration, Customer Admin tenant-level, Organization Admin, or other-customer action, SaaS Owner without support grant, Organization Admin attempting app-data/support-access/billing-derived access, cross-tenant/customer target, hidden/not-found target, role escalation, last-admin loss, self-disable/self-admin-role-removal, support-access policy violation, identity relink policy denial, unsupported bulk side effect, unredacted audit export, provider/model/outbox unavailable, missing tool-boundary grant, and denied skill/reference load.

Denials produce safe `system-message` feedback where user-facing, emit audit/work traces, and must not reveal protected identities, hidden counts, cross-scope existence, raw provider ids, raw invitation tokens, secrets, or private evidence.

## Approval and confirmation

Organization create, rename, suspend, and reactivate require explicit human confirmation, idempotency key, reason where consequential, correlation id, and backend audit/work trace emission. Invitation send/resend/revoke, membership status changes, role changes, support-access grants/revocations/extensions, identity relink, and access-review resolution require explicit human confirmation and/or decision-card approval according to policy. Last-admin loss, role escalation, support-access expansion, identity relink/reset, bulk operations, and low-confidence recommendations are risky by default.
