# Surface Contract: User Admin User List

- surface-id: `user-admin-user-list`
- type/version: searchable-table/v1
- owner functional agent: `user-admin-agent` (User Admin)
- composition note: list/search rows branch from `user-admin-dashboard`; account detail lives in `user-admin-user-account`.
- reusable surfaces: approval-required row actions open `decision-card`; audit/evidence links open `audit-trace-explorer`.

## Placement and graph role

This surface is a graph node reached from dashboard queues, prompt-entered requests such as `show users`, row/detail back links, and deep links through the shell request pipeline.

## Payload summary

Payload must include:

- selected `AuthContext`, scope ids, browser-safe actor capabilities, `correlationId`, trace ids, and stale marker;
- normalized filters: search text, account status, membership status, role, invitation status, support-access marker, review item type, risk level, and dashboard-origin queue id;
- paginated user rows from backend views: account summary, profile summary, scoped membership summary, invitation/support/access-review badges, last activity, risk flags, redaction markers, row trace id, and row action descriptors;
- pagination fields: `pageToken`, `nextPageToken`, `pageSize`, `sort`, stable query label;
- state variants for loading, empty, error, forbidden, stale, conflict, and partial data.

## Allowed actions

| Action | Capability hint | Qualified exposure | Result surface |
|---|---|---|---|
| Search/list users | `admin.users.search` | browser-tool, agent-tool | refresh `user-admin-user-list` |
| Open user account | `admin.users.detail.read` | browser-tool surface-request | `user-admin-user-account` |
| Create invitation | `admin.invitations.create` | browser-tool, agent-tool | invite flow, row update, or `decision-card` |
| Resend/revoke invitation | `admin.invitations.resend`, `admin.invitations.revoke` | browser-tool | row update plus audit trace |
| Add membership | `admin.memberships.add` | browser-tool | row update or `decision-card` |
| Suspend/reactivate/remove membership | `admin.memberships.suspend`, `admin.memberships.reactivate`, `admin.memberships.remove` | browser-tool | row update or denial `system_message` |
| Replace/remove role | `admin.roles.replace`, `admin.roles.remove` | browser-tool | row update or `decision-card` |
| Disable/reactivate account | `admin.users.disable`, `admin.users.reactivate` | browser-tool | row update or denial |
| Grant/revoke/extend support access | `admin.support_access.grant`, `admin.support_access.revoke`, `admin.support_access.extend` | browser-tool | row update or `decision-card` |
| Read/resolve access-review item | `admin.access_review.read`, `admin.access_review.resolve` | browser-tool | row update, detail, or `decision-card` |
| Open audit evidence | `admin.audit.read` / `audit.traces.view` | browser-tool | `audit-trace-explorer` |

Allowed actions are display hints only; backend authorization remains authoritative.

## UI states

- `loading`: preserve submitted filters and show table skeletons; disable mutation buttons.
- `empty`: distinguish no users in scope, no search matches, no queue items, and redacted result set.
- `error`: show retry with `correlationId`, submitted filters, and safe error category.
- `forbidden`: show selected scope and denial category; do not show result counts or matched identities.
- `stale`: show stale page token/reconnect banner; mutation actions disabled until fresh payload returns.

## Scope-aware variants

- SaaS Owner Admin: platform-safe rows only unless selected Tenant support-access context is active.
- Tenant Admin: Tenant employees, Customer Admins/Users, scoped invitations, support-access memberships, and access-review rows.
- Customer Admin: selected Customer users/invitations only; Tenant-wide actions and support-access administration are forbidden.

## Auth/security

- Backend view queries enforce scope before pagination and filtering; frontend filters are never a security boundary.
- Cross-tenant/customer access returns forbidden or hidden-not-found consistently and cannot leak existence through total counts.
- Row actions include denial categories for disabled actor, inactive membership, missing capability, role escalation, last-admin loss, and support-access limits.
- Browser rows exclude provider internals, raw invitation tokens/token hashes, email delivery secrets, full email bodies, and out-of-scope memberships.

## Rendering and capability tests

- SaaS Owner Admin, Tenant Admin, and Customer Admin variants show correct rows, redactions, allowed actions, and forbidden actions.
- Loading, empty, error, forbidden, stale, and responsive table-to-card states preserve safe context.
- Dashboard-origin filters open through the shell request pipeline without a page-first route dependency.
- Row actions include capability ids, governed-tool ids, browser-tool ids, idempotency requirements, trace ids, and decision-card links when risky.
