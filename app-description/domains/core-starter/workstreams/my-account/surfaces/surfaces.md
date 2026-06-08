# Surfaces: My Account

## Surface bindings

Uses global surface patterns: dashboard, detail-edit profile/settings panels, notification-center, list-search personal attention, markdown-response, system-message, outcome-panel digest/export result.

## My Account notification center surface

### Intent

`surface-my-account-notification-center` exists to help the signed-in human answer one question: **what do I personally need to notice, acknowledge, or come back to in this selected context?**

The surface is a personal in-app triage workspace owned by `my-account-agent`. It is not a notification-platform console, email preference page, provider-readiness dashboard, delivery-attempt audit, or cross-channel administration view. External delivery channels and provider operations must be represented by separate governed platform/admin surfaces.

### Contract

- Surface id: `surface-my-account-notification-center`.
- Surface type: `notification-center`.
- Surface contract: `my_account.notification_center.v1`.
- Owning workstream: My Account.
- Owning functional agent: `my-account-agent`.
- Required context: current authenticated account plus selected tenant/customer membership.
- Channel boundary: `channel: "in_app"`; all counts and items are derived from authorized backend notification state for the current `AuthContext`.

### User experience model

The surface is rebuilt around triage, not infrastructure:

1. **Orient** — show selected-context boundary, unread/visible counts, redaction state, and a plain-language intent statement.
2. **Triage** — group backend-authorized notifications into a primary full-width `Needs attention` area plus compact secondary `Awareness` and `Handled` areas:
   - `Needs attention`: unread, blocked, urgent, warning, or snoozed notifications that still require awareness. This area should use a responsive card grid so multiple active notifications do not become a long single column on desktop.
   - `Awareness`: visible informational notifications that can be read or archived.
   - `Handled`: read, dismissed, archived, expired, or otherwise completed notification records kept only as recent context.
3. **Act** — expose item-level lifecycle actions only: mark read, dismiss, archive, snooze. These actions mutate notification state only and never resolve source attention/tasks/events. On desktop, item actions should wrap horizontally as compact controls rather than stack into a tall button column.
4. **Explain** — each item shows why it is visible: source/workstream label, required capability, trace links, redaction level, and safe open-target hints when present.
5. **Tune** — show a compact in-app preference summary for visible categories only; hidden categories are never enumerated.
6. **Investigate** — expose trace/correlation links for audit, with redaction maintained in browser-safe fields.

### Frontend-safe payload

- `surfaceContract`, `channel`, `unreadCount`, `visibleCount`.
- `items[]`: notification id, title, summary, status, category, priority, origin, redaction level, required capability id, owning workstream id, source refs, target surface ref, lifecycle timestamps, trace refs.
- `preferencesSummary[]`: current user's in-app preference summary for visible categories only.
- `sourceSummary`: counts by visible source/category only.
- `redaction`, `traceRefs`, `correlationId`, `capabilityIds`.

Forbidden payload/content:

- Email preferences or email channel controls.
- Resend/provider configuration, secrets, delivery attempts, channel registry, local/test outbox records.
- SMS, mobile push, webhook, Slack, Teams, or other external-channel controls.
- Hidden workstream/category/count/source identifiers.
- Fixture/mock notifications in normal runtime.

### Actions

| Action | Governed backend capability/tool | Result behavior |
|---|---|---|
| Refresh center | `notification.list_my_account_center` | Reload authorized backend projection. |
| Mark read | `notification.mark_read` | Mark one authorized notification read; return updated center or no-op system message. |
| Dismiss | `notification.dismiss` | Dismiss one authorized notification from the active center; source state is unchanged. |
| Archive | `notification.archive` | Archive one authorized notification; return updated center/history target. |
| Snooze | `notification.snooze` | Snooze one authorized notification within bounded limits and show `snoozedUntil`. |
| Update in-app preferences | `notification.update_preferences` | Update current user's in-app preference summary with audit trace refs. |
| Open source | source capability such as `attention.open_attention_item` | Reauthorize and render the target surface, or return safe `not_found_or_redacted`. |

### State and security expectations

The surface explicitly represents empty, ready, submitting, no-op, forbidden, stale/reconnect, partial-data, provider-fail-closed, failure, and `not_found_or_redacted` states where applicable. All consequential actions carry correlation/idempotency behavior where needed, are authorized server-side, and preserve tenant/customer scoping and audit/work-trace links.

## Action rules

Every consequential browser action has a stable action id, maps to a governed backend capability/tool, carries a correlation/idempotency key where needed, and returns a typed result, decision card, workflow status, markdown response, or safe system message.

## States

Surfaces define loading, empty, ready, submitting, validation-error, forbidden, conflict, stale/reconnect, partial-data, provider-fail-closed, and failure states where applicable.
