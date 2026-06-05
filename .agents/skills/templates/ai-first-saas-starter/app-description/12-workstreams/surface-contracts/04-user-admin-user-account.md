# Surface Contract: User Admin User Account

- surface-id: `user-admin-user-account`
- type/version: detail/action-panel/v1
- owner functional agent: `user-admin-agent` (User Admin)
- reusable surfaces: risky or approval-required actions open `decision-card`; trace links open `audit-trace-explorer`.

## Placement and graph role

This surface is the scoped account/membership detail node reached from `user-admin-user-list`, dashboard attention items, audit evidence drill-ins, prompt requests, and deep links through the shell request pipeline.

## Payload summary

Payload must include:

- selected `AuthContext`, target account id or hidden-not-found marker, `correlationId`, trace ids, and redaction profile;
- account/profile summary, scoped memberships, roles/capabilities visible to the actor, invitation/support-access/access-review state, risk flags, last-admin markers, and recent admin audit events;
- action descriptors with browser-tool id, governed-tool id, capability id, input schema ref, confirmation/approval requirements, idempotency, denial categories, and result surface behavior;
- freshness/stale marker tied to user directory and membership projections.

## Allowed actions

| Action | Capability hint | Qualified exposure | Result surface |
|---|---|---|---|
| Refresh detail | `admin.users.detail.read` | browser-tool, agent-tool | update `user-admin-user-account` |
| Add/suspend/reactivate/remove membership | `admin.memberships.*` | browser-tool | update detail, `decision-card`, or denial |
| Replace/remove role | `admin.roles.replace`, `admin.roles.remove` | browser-tool | update detail or `decision-card` |
| Disable/reactivate account | `admin.users.disable`, `admin.users.reactivate` | browser-tool | update detail or denial |
| Grant/revoke/extend support access | `admin.support_access.*` | browser-tool | update detail or `decision-card` |
| Resolve access-review item | `admin.access_review.resolve` | browser-tool | update detail, `decision-card`, or workflow status |
| Open audit evidence | `admin.audit.read` / `audit.traces.view` | browser-tool | `audit-trace-explorer` |

## UI states

- `loading`: preserve target identity only when already authorized.
- `empty`: not normally valid; use hidden-not-found/forbidden/system message as appropriate.
- `error`: safe category, retry, and `correlationId`.
- `forbidden`: no target existence leakage; show denial category only.
- `conflict`: projection/version conflict for concurrent membership/role changes.
- `stale`: disable mutations until refreshed.

## Auth/security

- Detail reads enforce selected tenant/customer scope before loading target memberships.
- Last-admin and role-escalation protections fail closed.
- Support-access state is visible only to authorized actors and auditors according to policy.
- Provider ids, raw tokens, invitation secrets, hidden memberships, and out-of-scope roles are never sent to the browser.

## Rendering and capability tests

- Authorized Tenant Admin and Customer Admin detail variants show scoped fields only.
- Cross-tenant/customer detail requests produce hidden-not-found or forbidden without identity leakage.
- Last-admin and role-escalation actions route to decision/denial surfaces and produce audit traces.
- Mutation actions preserve idempotency keys and update dashboard/list attention projections.
- Responsive layout preserves role, support-access, invitation, trace, and risk affordances.
