# Surface Contract: User Admin Dashboard

- surface-id: `user-admin-dashboard`
- type/version: dashboard/v1
- owner functional agent: `user-admin-agent` (User Admin)
- composition note: replaces the dashboard portion of the former aggregate `user-admin-command-center`; list and detail behavior live in `user-admin-user-list` and `user-admin-user-account`.
- reusable surfaces: open risky or approval-required actions in `decision-card`; open evidence and audit links in `audit-trace-explorer`.

## Payload summary

Dashboard payload is owned by the User Admin frontend API contract and must include:

- selected `AuthContext`, scope label, scope type, tenant/customer ids when applicable, and browser-safe actor capabilities;
- counts and trend cards for active users, pending invitations, suspended/disabled users, expiring support access, access-review items, and recent denied admin actions;
- queues for invitation delivery failures, stale invitations, dormant admins, support-access expiry, last-admin risks, and agent recommendations;
- navigation affordances to `user-admin-user-list` with prefilled filters and to `user-admin-user-account` only through scoped user ids returned by backend views;
- trace links: `correlationId`, `surfaceTraceId`, per-card `traceId`, and audit event ids where available;
- redaction markers for totals or queues hidden by policy.

## Allowed actions

| Action | Capability hint | Result surface |
|---|---|---|
| Refresh dashboard | `admin.users.dashboard.read` | reload `user-admin-dashboard` |
| Open all users / filtered queue | `admin.users.search` | `user-admin-user-list` with filter context |
| Create invitation | `admin.invitations.create` | invitation form or decision card when risky |
| Resend/revoke invitation from queue | `admin.invitations.resend`, `admin.invitations.revoke` | dashboard queue refresh plus audit trace |
| Open access-review item | `admin.access_review.read` | list/detail or decision card |
| Resolve low-risk review item | `admin.access_review.resolve` | dashboard queue refresh plus audit trace |
| Open support-access queue | `admin.support_access.read` | `user-admin-user-list` filtered to support users/grants |
| Open admin audit | `admin.audit.read` | `audit-trace-explorer` |

Allowed actions are display hints only; backend authorization remains authoritative.

## States

- `loading`: show skeleton cards and queue placeholders; no stale fixture data may be treated as backend state.
- `empty`: show zero-state guidance for empty tenant/customer/user scope and invitation bootstrap actions only if capability allows.
- `error`: show retry with `correlationId`; hide partial sensitive counts unless backend marks them safe.
- `forbidden`: show selected scope, denial reason category, and context-switch hint; do not show counts or row identities.
- `stale`: show stale/reconnect banner and last successful `surfaceTraceId`; disable consequential actions until refreshed.

## Scope-aware variants

- SaaS Owner Admin: may see SaaS Owner users and platform-safe tenant metadata; Tenant application user data is forbidden unless the selected context is a Tenant-created support-access membership. Tenant support-access status can be summarized only as policy-safe metadata.
- Tenant Admin: may see Tenant employees, Customer organizations/users under the Tenant, Tenant-created support access, invitations, roles, access review, and scoped audit queues.
- Customer Admin: may see only selected Customer users, invitations, Customer roles, Customer access review, and Customer audit excerpts; Tenant-level queues, Tenant employee users, support-access grants, and Tenant settings actions are forbidden.

## Auth/security

- Every count, queue item, trace link, and navigation target is backend scoped by selected `AuthContext`.
- Dashboard must not reveal cross-tenant/customer existence through counts, error messages, labels, or stale cached cards.
- Disabled actors, inactive memberships, missing capabilities, role escalation, last-admin loss, and SaaS Owner no-support-access attempts render denied/forbidden states and link to audit only when authorized.
- Raw WorkOS ids, JWTs, invitation tokens/token hashes, Resend secrets, and full email bodies are never present.

## Rendering tests

- SaaS Owner Admin, Tenant Admin, and Customer Admin variants render distinct scope labels, queues, actions, and denials.
- Loading, empty, error, forbidden, and stale states render without privileged fixture leakage.
- Dashboard cards deep-link to `user-admin-user-list` with capability-backed filter metadata.
- Invitation lifecycle, last-admin warning, support-access expiry, and admin audit links render with trace ids.
- Mobile layout preserves cards and queues without switching to a page-first route dependency.
