# Notification Foundation Contract

## Status and scope

Implementation handoff for `TASK-NPF-01-001`. This contract defines the first governed **in-app notification foundation** for the AI-first SaaS starter/reference assets.

The foundation is a backend-owned user-facing projection/channel layer over authorized attention, workstream events, My Account personal attention digest task state, and selected projection-refresh evidence. It is not the source of truth for workstream state, attention semantics, task lifecycle, policy decisions, provider readiness, or authorization.

## Core rule

```text
governed backend attention/event/digest source state
→ notification projection input with tenant/customer/AuthContext provenance
→ backend-owned NotificationItem + preferences + lifecycle state
→ My Account notification center surface/API reads and lifecycle actions
```

Frontend fixtures, local component state, badges, toasts, and unseen-response flags are presentation hints only. They must never satisfy notification projection, lifecycle, preference, or completion claims.

## Non-goals and future boundary

Out of scope for this first slice:

- email, SMS, mobile push, webhook, Slack/Teams, or external delivery;
- broad enterprise notification analytics;
- user-authored notification rules beyond bounded in-app preferences;
- replacing `AttentionItem`, source events, AutonomousAgent task snapshots/results, or My Account personal digest projections as authoritative source state;
- leaking hidden workstream/item/task existence through notification counts, titles, source refs, preferences, or delivery errors.

Future email/push delivery may reuse the same source refs, preferences, and redaction decisions, but it must be added as a separate governed delivery-channel layer with explicit opt-in/quiet-hours/provider/fail-closed semantics and tests. No first-slice API or surface should imply that email/push is implemented.

## Notification item schema

Suggested DTO names:

- backend: `NotificationItem`, `NotificationPreference`, `NotificationProjectionInput`, `NotificationSourceRef`, `NotificationLifecycleStatus`, `NotificationChannel`, `NotificationCategory`, `NotificationSurfaceRef`, `MyAccountNotificationCenter`;
- frontend: `NotificationItem`, `NotificationPreference`, `NotificationCenterData`, `NotificationAction`.

Required `NotificationItem` fields:

| Field | Type | Meaning |
|---|---|---|
| `notificationId` | string | Stable backend id. Prefer deterministic source id plus user/scope channel key. |
| `tenantId` | string | Required tenant isolation key copied from source state. |
| `customerId` | string? | Optional customer scope copied from source state. |
| `accountId` | string | Recipient account for this in-app projection. Role/capability/tenant broadcasts must materialize only authorized per-account reads or enforce equivalent recipient filtering. |
| `selectedContextId` | string? | AuthContext basis used for projection/read when available. |
| `channel` | enum | `in_app` for this slice. Future values such as `email`/`push` are reserved but not implemented. |
| `title` | string | Browser-safe short label after redaction. |
| `summary` | string | Browser-safe explanation after redaction. |
| `category` | enum | See categories below. |
| `priority` | enum | `info`, `warning`, `urgent`, or `blocked`. Maps from attention severity/event hints without granting authority. |
| `status` | enum | `unread`, `read`, `dismissed`, `archived`, `snoozed`, or `expired`. |
| `sourceRefs` | array | Safe references to attention/event/digest/source evidence. |
| `surfaceRef` | object? | Authorized open target for My Account or source workstream surface. |
| `requiredCapabilityId` | string | Capability required to read/open source evidence. |
| `owningWorkstreamId` | string | Primary source/target functional-agent workstream. |
| `origin` | enum | `attention`, `workstream_event`, `personal_attention_digest`, `projection_refresh`, or `system`. |
| `redactionLevel` | enum | `full`, `summary_only`, or `not_found_or_redacted`. |
| `dedupeKey` | string | Semantic idempotency key. |
| `correlationId` | string | Request/event/task trace correlation. |
| `traceRefs` | array | Audit/work trace ids safe for the recipient. |
| `createdAt` | instant | First projection time. |
| `updatedAt` | instant | Last content/lifecycle update time. |
| `lastChangedAt` | instant | Ordering timestamp for center refresh. |
| `readAt` | instant? | Set by mark-read. |
| `dismissedAt` | instant? | Set by dismiss. |
| `archivedAt` | instant? | Set by archive. |
| `snoozedUntil` | instant? | Optional backend-owned suppression until time. |
| `expiresAt` | instant? | Optional expiration copied or computed from source state. |

`NotificationItem` content must be browser-safe by construction: no raw JWTs, provider credentials, invitation tokens, hidden prompt text, raw tool payloads, raw model prompts, secret config, cross-tenant labels, or hidden workstream names.

## Categories and source mapping

Starter categories:

- `attention_required` — source `AttentionItem` is actionable for the current account/context.
- `digest_ready` — My Account personal attention digest task result is ready for review.
- `digest_blocked` — digest task is failed, rejected, or blocked provider/runtime.
- `workstream_update` — backend projection/event indicates a visible workstream surface changed.
- `provider_readiness` — provider/model/runtime readiness blocked or restored evidence visible to the user.
- `policy_or_governance` — authorized policy/proposal decision/update needs awareness.
- `audit_or_security` — authorized audit/security evidence or denial/failure pattern needs review.
- `invitation_delivery` — authorized invitation delivery failure/recovery.

Mapping rules:

- `AttentionItem` remains authoritative for actionable attention category, severity, source refs, open target, and lifecycle. Notifications may mirror it as `attention_required`; resolving/dismissing a notification must not resolve/dismiss source attention unless a separate `attention.*` capability is invoked.
- `WorkstreamEventEnvelope` remains authoritative event evidence and projection trigger. Notification consumers may allow-list event types and projection hints, but events never grant authority or mutate source state.
- My Account personal attention digest task projections/snapshots remain authoritative for task status/result. Notifications may expose digest progress/result/blocker states only after backend task projection and source refs are available.
- Projection refresh events/hints may produce low-priority in-app notifications only when the user can see the affected workstream/surface and the message does not reveal hidden counts or source ids.

## Projection input contract

`NotificationProjectionInput` should normalize all supported source families before writing notification state:

| Field | Meaning |
|---|---|
| `inputId` | Stable event/attention/task/projection input id. |
| `inputFamily` | `attention`, `workstream_event`, `personal_attention_digest`, `projection_refresh`. |
| `tenantId` / `customerId` | Scope copied from trusted backend source state. |
| `recipientAccountId` | Recipient account or backend-resolved account candidate. |
| `authContext` | Browser-safe AuthContext basis: selected context id, tenant/customer ids, role/capability ids used for projection. |
| `owningWorkstreamId` | Source workstream. |
| `requiredCapabilityId` | Capability required to see/open the source. |
| `sourceRefs` / `traceRefs` | Browser-safe evidence refs. |
| `title` / `summary` | Already redacted safe text or source fields to redact. |
| `category` / `priority` | Notification classification. |
| `surfaceRef` | Optional open target; must be reauthorized on open. |
| `idempotencyKey` | Stable semantic projection key. |
| `correlationId` | Trace correlation. |

Dedupe key pattern:

```text
notification:<channel>:<tenantId>:<customerId-or-none>:<recipient-account-id>:<input-family>:<source-id>:<semantic-kind>
```

Consumer behavior:

- duplicate input with same semantic key updates safe summary/priority/source refs/`lastChangedAt` or no-ops; it must not create duplicate active notifications;
- source-cleared events may mark matching notification `expired` or leave it as `read/archived` according to lifecycle policy;
- stale input must not overwrite newer source evidence;
- every create/update/no-op/denial/lifecycle result emits audit/work trace evidence.

## Lifecycle

`NotificationLifecycleStatus` values:

| Status | Meaning | Idempotency |
|---|---|---|
| `unread` | New in-app item visible in notification center/counts. | Duplicate create updates existing item. |
| `read` | User marked/read or opened the item. | Re-mark-read is no-op with trace. |
| `dismissed` | User dismissed from active center view. | Re-dismiss is no-op with trace. |
| `archived` | User archived for historical view. | Re-archive is no-op with trace. |
| `snoozed` | Temporarily hidden until `snoozedUntil`. | Re-snooze replaces future time only if authorized/valid. |
| `expired` | Source no longer current or visibility no longer applies. | Re-expire is no-op with trace. |

Lifecycle operations affect notification channel state only. They must not mutate source attention/task/event state unless a separate source capability is explicitly called by a separate action and authorization path.

## Preferences

`NotificationPreference` is scoped to the signed-in account, tenant/customer/AuthContext where applicable, and channel `in_app`.

Required fields:

| Field | Meaning |
|---|---|
| `preferenceId` | Stable id for account + scope + category/channel. |
| `tenantId` / `customerId` | Scope. |
| `accountId` | Owner. |
| `channel` | `in_app` only in this slice. |
| `category` | Category or `all`. |
| `enabled` | Whether this category appears in active center by default. Security-critical categories may be non-disableable. |
| `minimumPriority` | Lowest priority included. |
| `muteUntil` | Optional temporary mute/snooze for category. |
| `includeReadInCenter` | Whether read items remain in normal center list. |
| `updatedAt` / `updatedBy` | Audit fields. |
| `correlationId` | Update correlation. |

Preference guardrails:

- preferences are backend-owned and authorized by `notification.update_preferences`;
- hidden categories/workstreams cannot be enumerated through preference APIs;
- muting notification display must not suppress required source attention, policy decisions, audit evidence, or backend authorization denials;
- future email/push preferences are separate fields/tasks and must not appear as active supported channels in first-slice responses.

## Capability and governed-tool ids

Capability grouping: `notification.in_app`.

| Governed-tool id | Class | Exposure | Purpose |
|---|---|---|---|
| `notification.list_my_account_center` | read/evidence | browser-tool, API, internal-tool | Return authorized in-app notification center for current account/AuthContext. |
| `notification.get_notification` | read/evidence | browser-tool, API | Read one authorized notification or return `not_found_or_redacted`. |
| `notification.mark_read` | command | browser-tool, API | Mark one/many authorized notifications read. |
| `notification.dismiss` | command | browser-tool, API | Dismiss authorized notification from active center. |
| `notification.archive` | command | browser-tool, API | Archive authorized notification. |
| `notification.snooze` | command | browser-tool, API | Snooze authorized notification/category within bounded limits. |
| `notification.update_preferences` | command | browser-tool, API | Update in-app notification preferences. |
| `notification.project_from_source` | command/internal | consumer-tool, timer-tool, internal-tool | Create/update/expire notification from allowed backend source input. |

Related source capabilities remain separate authorities: `attention.list_my_account_items`, `attention.open_attention_item`, `my_account.personal_attention_digest.read`, `my_account.open_authorized_workstream`, workstream-specific read/open capabilities, and audit/trace capabilities.

## Authorization, AuthContext redaction, and tenancy

Every projection, read, lifecycle, preference update, and open action must enforce:

1. authenticated account;
2. selected `AuthContext` when tenant/customer-scoped;
3. active tenant/customer membership;
4. recipient account ownership or equivalent backend-resolved recipient rule;
5. visible workstream and required source capability;
6. tenant/customer id filters copied from source state;
7. preference scope and non-enumerating hidden-category handling;
8. browser-safe output redaction;
9. audit/work trace for allowed, denied, duplicate, no-op, projection, lifecycle, and preference outcomes.

Redaction rules:

- unauthorized, hidden, cross-tenant, stale, or preference-invisible source: omit the notification from lists or return `not_found_or_redacted` for direct reads without title, count, category, source id, or workstream name;
- authorized summary but restricted details: return `summary_only` and omit sensitive source refs/surface refs;
- authorized detail: return `full` only for permitted fields;
- cross-tenant access: deny, audit, and return safe system-message/error; never partial success.

## My Account notification center surface

Primary surface id: `surface-my-account-notification-center`.

Surface type: `dashboard` or a dedicated list surface once implemented. It is launched from My Account and may be linked from the user tile/count indicator.

`NotificationCenterData` should include:

- `surfaceContract: "my_account.notification_center.v1"`;
- `channel: "in_app"`;
- `unreadCount` and `visibleCount` derived only from authorized backend notification state;
- `items: NotificationItem[]` after redaction/preferences;
- `preferencesSummary` for current `AuthContext` without hidden category enumeration;
- `sourceSummary` with counts by visible origin/category only;
- `redaction` metadata;
- `traceRefs` and `correlationId`.

Actions:

| Action | Governed tool | Result behavior |
|---|---|---|
| Refresh center | `notification.list_my_account_center` | Reload backend projection. |
| Mark read | `notification.mark_read` | Return updated item/center or no-op system message. |
| Dismiss | `notification.dismiss` | Return updated center or no-op system message. |
| Archive | `notification.archive` | Return updated center/history target. |
| Snooze | `notification.snooze` | Return updated item with `snoozedUntil`. |
| Update preferences | `notification.update_preferences` | Return preference summary and audit trace refs. |
| Open source | source capability such as `attention.open_attention_item` | Reauthorize source and render target surface or `not_found_or_redacted` system message. |

The surface must clearly label notifications as **in-app only** and must not show email/push toggles as active controls.

## Test obligations for later tasks

Backend tests should cover:

- projection from `AttentionItem`, `WorkstreamEventEnvelope`, and personal digest task source inputs;
- stable dedupe keys and duplicate no-op/update behavior;
- lifecycle idempotency for mark-read, dismiss, archive, snooze, and expire;
- preference updates and non-enumerating hidden-category behavior;
- tenant/customer/AuthContext isolation, recipient filtering, required capability checks, and redaction;
- source-open reauthorization and safe `not_found_or_redacted` direct reads;
- no source attention/task mutation from notification lifecycle actions;
- audit/work traces for projection, reads, denials, lifecycle, preferences, and no-ops.

Frontend tests should cover:

- My Account notification center rendering from backend-shaped data;
- in-app-only labels and absence of active email/push controls;
- read/dismiss/archive/snooze/preference action descriptors with governed-tool/capability ids;
- redacted/forbidden/not-found/empty/loading/error states;
- backend-derived counts distinct from frontend transient badges/toasts.

## Initial implementation handoff

Recommended source paths for later implementation tasks:

- backend domain records under `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/domain/security/`;
- backend services/repositories under `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/`;
- My Account dashboard/surface shaping in `MyAccountService` / `WorkstreamService`;
- frontend types in `templates/ai-first-saas-starter/frontend/src/workstream/types/surfaces.ts`;
- frontend My Account notification center rendering in the existing surface component family or a focused new `NotificationCenterSurface`;
- tests beside existing `AttentionServiceTest`, `WorkstreamEventBackboneServiceTest`, `MyAccountPersonalAttentionDigestServiceTest`, `WorkstreamServiceTest`, and frontend workstream contract tests.
