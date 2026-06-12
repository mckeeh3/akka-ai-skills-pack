# Surface Contract: My Account Dashboard

- surface-id: `my-account-dashboard`
- type/version: dashboard/card-set/v1
- owner functional agent: `my-account-agent` (My Account)
- reusable surfaces: may open target workstream dashboard, profile/settings cards, `system_message`, and audit evidence surfaces through governed surface-request actions.

## Placement and graph role

The My Account dashboard is the current user's aggregate attention dashboard. It opens from the signed-in user tile/email in the lower-left rail, not from the top workstream rail. It is the graph trunk for self-service context, profile, settings, personal queue, and cross-workstream attention panels.


## User-visible/internal metadata boundary

Default rendering must use SaaS product language and show only information the current actor needs to decide, act, recover, or understand the business outcome. Internal ids, raw trace/event/correlation data, governed-tool/capability ids, backend component names, prompt/provider/model details, and policy implementation references are implementation metadata. Expose them only in authorized admin, support, auditor, or developer drilldowns, and keep them visually subordinate to user-meaningful labels.

## Payload summary

Payload must include:

- selected `AuthContext`, available contexts, browser-safe capability ids, account/profile/settings summary, and `correlationId`;
- cross-workstream `WorkstreamAttentionSummary[]` for visible workstreams only;
- profile/settings shortcuts as surface-request actions, not frontend-only links;
- personal queue items with target workstream/surface ids, source trace ids, severity, lifecycle state, and redaction profile;
- omitted-field markers for hidden memberships, support-only facts, disabled contexts, and forbidden workstreams.

## Compact payload schema

```ts
type MyAccountDashboardData = {
  authContext: SurfaceAuthContext;
  availableContexts: Array<{ selectedContextId: string; label: string; tenantId: string; customerId?: string; disabledReason?: string }>;
  accountSummary: { accountId: string; email: string; displayName?: string; membershipStatus: string; redactionMarkers: string[] };
  profileSettingsSummary: { profileComplete: boolean; notificationStatus?: string; settingsWarnings: string[] };
  attentionSummaries: WorkstreamAttentionSummary[];
  personalQueueItems: Array<{ itemId: string; sourceWorkstreamId: string; targetSurfaceId?: string; severity: string; lifecycle: string; traceIds: string[] }>;
};
```

## Allowed actions

| Action | Capability hint | Qualified exposure | Result surface |
|---|---|---|---|
| Refresh dashboard | `account.dashboard.read` | browser-tool, agent-tool | update `my-account-dashboard` |
| Select AuthContext | `account.context.select` | browser-tool | dashboard refresh or `system_message` denial |
| Open profile | `account.profile.read` | browser-tool surface-request | profile/settings surface or `markdown_response` SaaS Foundation App fallback |
| Open settings | `account.settings.read` | browser-tool surface-request | settings surface or `markdown_response` SaaS Foundation App fallback |
| Open attention item | `workstream.attention.open` | browser-tool surface-request | target workstream/surface item |
| Open target workstream | `workstream.open` | browser-tool surface-request | target dashboard or denial `system_message` |
| Open trace | `audit.traces.view` | browser-tool, agent-tool | `audit-trace-explorer` |

Allowed actions are display hints only; backend authorization remains authoritative.

## Action mapping

| actionId | browserToolId | governedToolId | capabilityId | exposure | resultSurfaceId | idempotency | traceRequired |
| --- | --- | --- | --- | --- | --- | --- | --- |
| `my-account.refresh-dashboard` | `my-account.dashboard.refresh` | `myaccount.dashboard.read` | `secure-tenant-user-foundation` | browser-tool, agent-tool | `my-account-dashboard` | read-only request correlation id | true |
| `my-account.select-context` | `my-account.context.select` | `myaccount.context.select` | `secure-tenant-user-foundation` | browser-tool | `my-account-dashboard` or `system_message` | selectedContextId + accountId | true |
| `my-account.open-profile` | `my-account.profile.open` | `myaccount.profile.read` | `secure-tenant-user-foundation` | browser-tool, surface-request | deferred `my-account-profile-card` or fallback | surface request correlation id | true |
| `my-account.open-settings` | `my-account.settings.open` | `myaccount.settings.read` | `secure-tenant-user-foundation` | browser-tool, surface-request | deferred `my-account-settings-card` or fallback | surface request correlation id | true |
| `my-account.open-attention-item` | `my-account.attention.open` | `workstream.attention.open` | `frontend-shell-integration-patterns` | browser-tool, surface-request | target workstream surface or `system_message` | source item id | true |
| `my-account.open-workstream` | `my-account.workstream.open` | `workstream.open` | `frontend-shell-integration-patterns` | browser-tool, surface-request | target dashboard or `system_message` | target workstream id | true |
| `my-account.open-trace` | `my-account.trace.open` | `audit.traces.view` | `governance-decisions-audit` | browser-tool, agent-tool | `audit-trace-explorer` | trace id | true |

## UI states

- `loading`: show account/context skeletons and do not flash hidden workstreams.
- `empty`: no personal attention items; still show profile/settings/context affordances.
- `error`: safe error category and readable support/reference label; raw `correlationId` appears only in authorized diagnostic detail, with no provider secrets or tokens.
- `forbidden`: disabled/no-membership recovery copy without leaking tenant/customer existence.
- `stale`: attention panels marked stale until refreshed from backend projection.

## Auth/security

- `/api/me` and dashboard reads return frontend-safe fields only.
- Cross-workstream panels expose only visible workstreams and authorized attention summaries.
- Profile/settings/context actions enforce backend authorization and disabled-user denial.
- My Account aggregation does not bypass target workstream authorization.

## Rendering and capability tests

- My Account opens from the lower-left user tile and is not duplicated in the top rail.
- Organization Admin, regular member, auditor/support-like viewer, disabled user, and no-membership variants render correct actions and denials.
- Cross-workstream attention items use the shell request pipeline with canonical prompt feedback, origin metadata, and target-workstream-only request rendering.
- Backend denial does not leak hidden workstream/surface existence.
- Trace ids and correlation ids are retained for protected payload reads and action results, but ordinary users see readable support/reference labels unless authorized for diagnostic detail.
