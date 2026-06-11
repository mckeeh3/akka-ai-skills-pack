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

## Compact payload schema

```ts
type UserAdminUserAccountData = {
  authContext: SurfaceAuthContext;
  target: { accountId?: string; hiddenNotFound?: boolean; redactionProfile: string };
  accountProfile: { emailLabel?: string; displayName?: string; status?: string; omittedFieldKeys: string[] };
  memberships: Array<{ membershipId: string; tenantId: string; customerId?: string; roleIds: string[]; status: string; lastAdminRisk?: boolean; supportAccessVisible?: boolean }>;
  riskFlags: Array<{ flagId: string; severity: string; policyRef?: string; requiresDecision?: boolean }>;
  recentAuditEvents: Array<{ traceId: string; label: string; occurredAt: string }>;
};
```

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

## Action mapping

| actionId | browserToolId | governedToolId | capabilityId | exposure | resultSurfaceId | idempotency | traceRequired |
| --- | --- | --- | --- | --- | --- | --- | --- |
| `user-admin.refresh-user-account` | `user-admin.user-detail.refresh` | `useradmin.users.detail.read` | `secure-tenant-user-foundation` | browser-tool, agent-tool | `user-admin-user-account` | target account id | true |
| `user-admin.change-detail-membership` | `user-admin.user-detail.membership.change` | `useradmin.membership.change` | `secure-tenant-user-foundation` | browser-tool | `user-admin-user-account`, `decision-card`, or `system_message` | membership id + request id | true |
| `user-admin.change-detail-role` | `user-admin.user-detail.role.change` | `useradmin.role.change` | `secure-tenant-user-foundation` | browser-tool | `user-admin-user-account` or `decision-card` | membership id + target role + request id | true |
| `user-admin.change-detail-account-status` | `user-admin.user-detail.account.change-status` | `useradmin.account.change_status` | `secure-tenant-user-foundation` | browser-tool | `user-admin-user-account` or `system_message` | account id + requested status | true |
| `user-admin.change-detail-support-access` | `user-admin.user-detail.support-access.change` | `useradmin.support_access.change` | `secure-tenant-user-foundation` | browser-tool | `user-admin-user-account` or `decision-card` | support grant id + request id | true |
| `user-admin.resolve-detail-access-review` | `user-admin.user-detail.access-review.resolve` | `useradmin.access_review.resolve` | `secure-tenant-user-foundation` | browser-tool | `user-admin-user-account`, `decision-card`, or deferred `task-progress-surface` | review item id + decision id | true |
| `user-admin.open-detail-audit` | `user-admin.user-detail.audit.open` | `audit.traces.view` | `governance-decisions-audit` | browser-tool | `audit-trace-explorer` | trace id | true |

## UI states

- `loading`: preserve target identity only when already authorized.
- `empty`: not normally valid; use hidden-not-found/forbidden/system message as appropriate.
- `error`: safe category, retry, and `correlationId`.
- `forbidden`: no target existence leakage; show denial category only.
- `conflict`: projection/version conflict for concurrent membership/role changes.
- `stale`: disable mutations until refreshed.

## Auth/security

- Detail reads enforce selected organization/customer scope before loading target memberships.
- Last-admin and role-escalation protections fail closed.
- Support-access state is visible only to authorized actors and auditors according to policy.
- Provider ids, raw tokens, invitation secrets, hidden memberships, and out-of-scope roles are never sent to the browser.

## Rendering and capability tests

- Authorized Organization Admin and Customer Admin detail variants show scoped fields only.
- Cross-tenant/customer detail requests produce hidden-not-found or forbidden without identity leakage.
- Last-admin and role-escalation actions route to decision/denial surfaces and produce audit traces.
- Mutation actions preserve idempotency keys and update dashboard/list attention projections.
- Responsive layout preserves role, support-access, invitation, trace, and risk affordances.
