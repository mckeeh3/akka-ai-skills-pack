# Surface Contract: User Admin User List

- surface-id: `user-admin-user-list`
- type/version: searchable-table/v1
- owner functional agent: `user-admin-agent` (User Admin)
- composition note: replaces the directory/table portion of the former aggregate `user-admin-command-center`; dashboard summary lives in `user-admin-dashboard`, account detail lives in `user-admin-user-account`.
- reusable surfaces: approval-required row actions open `decision-card`; audit/evidence links open `audit-trace-explorer`.

## Payload summary

User list payload is owned by the User Admin frontend API contract and must include:

- selected `AuthContext`, scope type/ids, browser-safe actor capabilities, and `correlationId`;
- normalized filter state: search text, account status, membership status, role, invitation status, support-access marker, review item type, risk level, and dashboard-origin queue id;
- paginated user rows from backend views, never caller-supplied fixture ids: account summary, profile summary, scoped membership summary, invitation/support/access-review badges, last activity, risk flags, redaction markers, and row `traceId`;
- row action availability with capability ids, denial reason categories, idempotency-key requirement for mutations, and decision-card requirement where applicable;
- pagination fields: `pageToken`, `nextPageToken`, `pageSize`, `sort`, stable query label;
- surface state envelope for loading, empty, error, forbidden, and stale variants.

## Allowed actions

| Action | Capability hint | Result surface |
|---|---|---|
| Search/list users | `admin.users.search` | refresh `user-admin-user-list` |
| Open user account | `admin.users.detail.read` | `user-admin-user-account` |
| Create invitation | `admin.invitations.create` | invite flow or decision card |
| Resend/revoke invitation | `admin.invitations.resend`, `admin.invitations.revoke` | row update plus audit trace |
| Add membership | `admin.memberships.add` | row update or decision card |
| Suspend/reactivate/remove membership | `admin.memberships.suspend`, `admin.memberships.reactivate`, `admin.memberships.remove` | row update or denial |
| Replace/remove role | `admin.roles.replace`, `admin.roles.remove` | row update or decision card |
| Disable/reactivate account | `admin.users.disable`, `admin.users.reactivate` | row update or denial |
| Grant/revoke/extend support access | `admin.support_access.grant`, `admin.support_access.revoke`, `admin.support_access.extend` | row update or decision card |
| Read/resolve access-review item | `admin.access_review.read`, `admin.access_review.resolve` | row update, detail, or decision card |
| Open audit evidence | `admin.audit.read` | `audit-trace-explorer` |

Allowed actions are display hints only; backend authorization remains authoritative.

## UI style notes

- Render as a capability-backed enterprise workstream search/list surface, not a generic CRUD table: filters, scoped row metadata, risk/status badges, action availability, and audit evidence links use the named-theme tokens, readable density, and table/list rules from `55-ui/style-guide.md`.
- Responsive table-to-card fallback preserves authority, risk, invitation/support-access, decision-card, and trace affordances with text labels plus color/icon semantics in every named theme.

## States

- `loading`: preserve submitted filters and show table skeletons; disable mutation buttons.
- `empty`: distinguish no users in scope, no search matches, no queue items, and redacted result set.
- `error`: show retry with `correlationId`, submitted filters, and safe error category.
- `forbidden`: show selected scope and denial category; do not show result counts or matched identities.
- `stale`: show stale page token/reconnect banner; mutation actions disabled until fresh payload returns.

## Scope-aware variants

- SaaS Owner Admin: lists SaaS Owner users and platform-safe tenant admin bootstrap records; Tenant/Customer user rows require selected Tenant support-access context and otherwise return forbidden or redacted metadata.
- Tenant Admin: lists Tenant employees, Customer Admins/Users under the Tenant, invitations, support-access memberships, and access-review rows scoped to the Tenant.
- Customer Admin: lists only selected Customer users and invitations; Tenant employee rows, Tenant-level role actions, support-access administration, and Tenant-wide audit queues are forbidden.

## Auth/security

- Backend view queries enforce tenant/customer scope before pagination and filtering; frontend filters are never a security boundary.
- Cross-tenant/customer access returns forbidden or hidden-not-found consistently and cannot leak existence through total counts.
- Row actions must include denial categories for disabled actor, inactive membership, missing capability, Customer Admin Tenant action, SaaS Owner no support access, role escalation, and last-admin loss.
- Browser rows exclude WorkOS provider internals, raw invitation tokens/token hashes, Resend secrets, full email bodies, and out-of-scope memberships.

## Rendering tests

- SaaS Owner Admin, Tenant Admin, and Customer Admin list variants show correct rows, redactions, allowed actions, and forbidden actions.
- Loading, empty, error, forbidden, and stale states preserve safe filter context and avoid privileged fixture leakage.
- Dashboard-origin filters open the list without a page-first route dependency.
- Row actions include capability ids, idempotency-key requirements for mutations, trace ids, and decision-card links when risky.
- Responsive table-to-card fallback preserves account status, memberships, role badges, invitation/support/access-review badges, and audit links.
