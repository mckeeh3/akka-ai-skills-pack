# Surface Contract: User Admin User Account

- surface-id: `user-admin-user-account`
- type/version: detail-card+action-panel/v1
- owner functional agent: `user-admin-agent` (User Admin)
- composition note: replaces the detail/forms portion of the former aggregate `user-admin-command-center`; dashboard and list/search live in `user-admin-dashboard` and `user-admin-user-list`.
- reusable surfaces: risky mutations and approvals open `decision-card`; audit timelines and trace evidence open `audit-trace-explorer`.

## Payload summary

User account payload is owned by the User Admin frontend API contract and must include:

- selected `AuthContext`, target `accountId`, `correlationId`, `surfaceTraceId`, and browser-safe actor capabilities;
- account summary: display email/name, account status, identity link state, created/activated/disabled timestamps, last login/activity, and redaction markers;
- profile/settings visibility summary, with settings shown only when self or explicit policy allows admin visibility;
- scoped memberships: membership ids, scope labels, roles, capability summary, status, membership kind, support-access marker/expiry, last-admin risk, invitation id, audit links, and action availability;
- invitation history from `InvitationView` with delivery/expiry/resend/revoke eligibility and no raw token data;
- support-access grants related to the account, including purpose, expiry, grant status, and revocation/extension availability;
- access-review items and agent recommendations related to the account;
- recent admin audit excerpts with redacted evidence summaries and links to `audit-trace-explorer`;
- action availability for detail mutations, denial categories, idempotency-key requirements, decision-card requirements, and resulting trace ids.

## Allowed actions

| Action | Capability hint | Result surface |
|---|---|---|
| Read user account detail | `admin.users.detail.read` | load `user-admin-user-account` |
| Return to list with context | `admin.users.search` | `user-admin-user-list` |
| Patch admin-visible profile | `admin.users.profile.patch` | account detail refresh plus audit trace |
| Disable/reactivate account | `admin.users.disable`, `admin.users.reactivate` | account detail refresh or denial |
| Request/complete identity relink | `admin.users.identity_relink.request`, `admin.users.identity_relink.complete` | decision card and audit trace |
| Add membership | `admin.memberships.add` | membership section refresh |
| Suspend/reactivate/remove membership | `admin.memberships.suspend`, `admin.memberships.reactivate`, `admin.memberships.remove` | membership section refresh or denial |
| Replace/remove role | `admin.roles.replace`, `admin.roles.remove` | membership section refresh, decision card, or denial |
| Resend/revoke invitation | `admin.invitations.resend`, `admin.invitations.revoke` | invitation section refresh |
| Grant/revoke/extend support access | `admin.support_access.grant`, `admin.support_access.revoke`, `admin.support_access.extend` | support section refresh or decision card |
| Read/resolve access-review item | `admin.access_review.read`, `admin.access_review.resolve` | review section refresh or decision card |
| Open audit evidence | `admin.audit.read` | `audit-trace-explorer` |

Allowed actions are display hints only; backend authorization remains authoritative.

## UI style notes

- Render as an enterprise workstream detail/action surface: account summary, membership cards, invitation/support-access sections, access-review evidence, action panel, and audit excerpts use layered panels, visible authority badges, semantic state labels, and trace-link affordances from `55-ui/style-guide.md`.
- Risky or approval-required mutations should visually route to decision-card patterns; style must clarify evidence, policy boundary, and consequence without making UI visibility a substitute for backend authorization.

## States

- `loading`: show account skeleton, action-panel skeleton, and disabled mutation controls.
- `empty`: valid scoped target has no memberships/invitations/audit in the selected scope; show safe remediation actions only when capability allows.
- `error`: show retry with `correlationId` and safe category; do not cache sensitive detail after authorization changes.
- `forbidden`: show denial category and selected scope; hide target identity unless backend marks a browser-safe summary as allowed.
- `stale`: show stale detail banner; disable role, membership, account, support-access, and access-review mutations until refreshed.

## Scope-aware variants

- SaaS Owner Admin: may see SaaS Owner account details and platform-safe tenant bootstrap metadata; Tenant/Customer memberships and audit evidence require selected Tenant-created support-access context or are redacted/forbidden.
- Tenant Admin: may see Tenant employee details, Customer-scope memberships under the Tenant, Tenant-created support access, invitations, access-review evidence, and scoped admin audit excerpts.
- Customer Admin: may see selected Customer user details and Customer memberships/invitations/audit excerpts; Tenant employee membership data, Tenant roles, support-access administration, and Tenant-level actions are forbidden.

## Auth/security

- Detail payload must be produced by scoped backend `findScopedUser`/membership/invitation/audit queries, not by untrusted caller-known account ids alone.
- Every action recomputes authorization server-side and emits audit for allowed, denied, no-op, and failed consequential operations.
- Required denials include disabled actor, cross-tenant/customer target, inactive membership, missing role/capability, Customer Admin Tenant action, SaaS Owner without support access, role escalation, support-access policy violation, identity relink policy denial, and last-admin loss.
- Browser detail excludes raw JWTs, WorkOS provider internals, private subject ids unless explicitly policy-safe, raw invitation tokens/token hashes, Resend secrets, full email bodies, and out-of-scope memberships/evidence.

## Rendering tests

- SaaS Owner Admin, Tenant Admin, and Customer Admin account variants render correct sections, redactions, action availability, and forbidden messages.
- Loading, empty, error, forbidden, and stale states render without leaking target existence beyond policy.
- Detail actions include capability ids, idempotency-key requirements for mutations, trace ids, audit links, and decision-card links for risky operations.
- Last-admin warnings, role escalation denial, support-access expiry, invitation delivery failure, and access-review evidence are visible when authorized.
- Responsive layout preserves profile/account summary, membership cards, invitation/support/access-review sections, action panel, and audit trace links without page-first route dependency.
