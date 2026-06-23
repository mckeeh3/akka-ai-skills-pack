# Tools: My Account

## Uses

Global tool inventory: `../../../../../global/tools/foundation-governed-tools.md`.

## Workstream exposure

Allowed governed tools include:

- `read-current-account-context`: dashboard, profile, settings, context/authority, and shell bootstrap reads.
- `update-own-profile-settings`: self-service profile and preference persistence, including named theme id when allowed.
- `request-personal-digest-export`: backend-governed personal attention digest/export start/read/cancel/review flows.
- `notification.list_my_account_center`: personal in-app notification center reads.
- `notification.mark_read`, `notification.dismiss`, `notification.archive`, `notification.snooze`, `notification.update_preferences`: notification lifecycle/preferences only; these never resolve source attention/tasks/events.
- `my_account.open_authorized_workstream` and source-specific `attention.open_attention_item`: reauthorize sibling workstream/source openings and return a target surface or safe denial.
- `my_account.view_own_trace_refs`: browser-safe trace/evidence links for the signed-in user's authorized context.

Tools are exposed as browser, agent, or internal tools only as stated by the linked capability. Side-effecting or high-impact tools require idempotency, correlation, authorization, approval policy, and audit/work traces. Denied tool calls are traced and return safe feedback.

Forbidden tool exposure: role/capability grants, tenant/customer administration, provider secrets, external notification provider controls, fake digest success, hidden workstream enumeration, or client-side authority changes.


## `human_chat_tool_plan` expanded current-intent catalog

This catalog records the My Account runtime expansion implemented for the bounded `human_chat_tool_plan` path. It reuses the same governed tool ids as browser surface actions. Exposure channel `human_chat_tool_plan` remains proposal-and-confirmation only: deterministic no-mutation surface routing runs first, the initial execution-oriented chat request may only return a no-mutation plan proposal, and no state changes until exact human plan-snapshot confirmation and backend authorization succeed.

Allowed expanded entries:

| Classification | Representative prompts | Surface action ids | Shared governed tool ids | Capability ids | Input schema and validation | Result surface(s) |
|---|---|---|---|---|---|---|
| `chat-executable-now` | `change my display name to Hugh`; `change my theme to Obsidian Dark` | `action-update-my-profile`; `action-update-my-settings` | `my_account.update_profile_settings` | `my_account.update_profile_settings` | `schema.my-account.profile.update.v1` / `schema.my-account.settings.update.v1`; allow only self-scoped display name, locale, timezone, and backend-valid named theme/preference fields | `surface-my-profile`; `surface-my-settings` |
| `chat-executable-now` | `mark notification n-123 read`; `dismiss this notification`; `archive this notification`; `snooze this notification until tomorrow`; `update my notification preferences for security alerts` | `action-notification-mark-read`; `action-notification-dismiss`; `action-notification-archive`; `action-notification-snooze`; `action-notification-update-preferences` | `notification.mark_read`; `notification.dismiss`; `notification.archive`; `notification.snooze`; `notification.update_preferences` | `notification.manage_own_state`; `notification.update_own_preferences` | `schema.notification.mark-read.v1`, `.dismiss.v1`, `.archive.v1`, `.snooze.v1`, `.preferences.update.v1`; require backend-visible notification id/category, bounded snooze time, preference-category validation, selected user scope, and idempotency key | `surface-my-account-notification-center` |

Expanded classification and blocked/surface-only rationale:

| Classification | Action groups | Rationale and boundary |
|---|---|---|
| `router-only` | Dashboard/profile/settings/context opens; sibling workstream/source opens | Navigation and prefill are deterministic, no-mutation, and backend-authorized through target surfaces; chat execution would add confirmation without value and could enumerate hidden workstreams. |
| `surface-only` | `action-select-my-context`; `action-sign-out`; personal digest read/result review | Context switching, browser session boundaries, and advisory digest disposition need backend-authored choices, shell refresh, and dedicated stale/recovery UX rather than chat-plan compression. |
| `approval-gated` | Personal digest start/cancel/accept/reject | Provider/model-backed advisory digest work must fail closed and human disposition must remain explicit; chat confirmation alone is insufficient. |
| `blocked-pending-design` | External/email notification preferences and external provider readiness checks | External delivery/provider behavior needs provider/outbox policy, secret redaction, and fail-closed tests before chat exposure. |
| `internal-only` | Account bootstrap/session internals, provider identity linkage, notification producers, and attention aggregation jobs | These are service/provider/background paths and are never direct chat catalog steps. |
| `out-of-scope` | Business-domain profile extensions and non-foundation notification channels | Outside the five foundation workstreams for this catalog expansion. |

Execution requirements for every accepted My Account entry:

- proposal step validation rejects out-of-catalog action/tool/capability combinations, hidden targets, unsafe output bindings, missing provider/runtime/tool-boundary readiness, unsupported input fields, external-channel changes, and cross-account edits;
- confirmation must bind `planId`, `planSnapshotId`, selected `AuthContext`, requestedBy, confirmedBy, step hashes, idempotency root, correlation id, and visible side-effect acknowledgements;
- every confirmed step recomputes backend authorization and approval policy, uses its own idempotency key and transaction boundary, emits trace evidence, and returns the declared typed result surface or safe system message;
- idempotent replay returns the prior proposal/result without duplicating side effects; partial failure reports completed, failed, skipped, and recovery steps;
- no workstream agent, prompt, frontend route, disabled/visible control, or tool description grants autonomous mutation authority.
