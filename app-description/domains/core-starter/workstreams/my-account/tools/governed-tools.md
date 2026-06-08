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
