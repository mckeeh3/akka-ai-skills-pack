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


## `human_chat_tool_plan` shared adapter catalog

The first-pass chat executable catalog for My Account reuses the same backend-governed tool ids as the corresponding surface actions. Exposure channel `human_chat_tool_plan` is proposal-and-confirmation only: the initial chat request can return a plan proposal surface, but no mutation occurs until explicit human confirmation and backend authorization succeed.

| Adapter exposure | Representative prompt | Surface action ids | Shared governed tool ids | Capability ids | Input schema and validation | Result surface(s) |
|---|---|---|---|---|---|---|
| `human_chat_tool_plan` | `change my theme to Obsidian Dark` | `action-update-my-settings` | `my_account.update_profile_settings` | `my_account.update_profile_settings` | `schema.my-account.settings.update.v1` with `preferredThemeId=obsidian-dark` selected from backend-valid theme options | `surface-my-settings` |

Execution requirements:

- proposal step validation rejects out-of-catalog action/tool/capability combinations, hidden targets, unsafe output bindings, missing provider/runtime/tool-boundary readiness, and unsupported input fields;
- confirmation must bind `planId`, `planSnapshotId`, selected `AuthContext`, requestedBy, confirmedBy, step hashes, idempotency root, correlation id, and visible side-effect acknowledgements;
- every confirmed step recomputes backend authorization and approval policy, uses its own idempotency key and transaction boundary, emits trace evidence, and returns the declared typed result surface or safe system message;
- idempotent replay returns the prior proposal/result without duplicating side effects; partial failure reports completed, failed, skipped, and recovery steps;
- no workstream agent, prompt, frontend route, disabled/visible control, or tool description grants autonomous mutation authority.
