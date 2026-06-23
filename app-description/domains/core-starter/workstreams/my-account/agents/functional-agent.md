# Agent Binding: my-account-agent

## Uses

Global definition: `../../../../../global/agents/foundation-functional-agents.md`.

## Authority

The agent operates only through capability `account-context-and-profile` and governed tools `read-current-account-context`, `update-own-profile-settings`, `request-personal-digest-export`, `notification.list_my_account_center`, `notification.mark_read`, `notification.dismiss`, `notification.archive`, `notification.snooze`, `notification.update_preferences`, `my_account.open_authorized_workstream`, `attention.open_attention_item`, and `my_account.view_own_trace_refs` with selected `AuthContext`, backend authorization, tool-boundary checks, approval gates, and durable traces. For notification lifecycle, source-opening, and trace-opening paths, the agent may explain state, draft safe actions, and prepare or invoke backend-authorized personal actions only as allowed by the tool boundary; it cannot resolve source work, broaden workstream visibility, mutate roles/memberships/tasks, or treat a browser route as authority.

## Model and expertise binding

LLM-backed turns use inherited governed default model binding unless a tenant-approved override is activated for `my-account-agent`:

- `ModelConfigRef`: `foundation-my-account-default-model`.
- `ModelPolicy`: `foundation-my-account-model-policy`.
- No implicit fallback; approved fallback requires policy and trace.
- Provider secrets never appear in prompts, manifests, traces, browser payloads, or responses.

Prompt assembly includes only compact governed expertise manifest entries. Full skill/reference text loads only through authorized `readSkill(skillId)` or `readReferenceDoc(referenceId)` after active-agent assignment, selected `AuthContext`, status/version, token/redaction, and `ToolPermissionBoundary` checks.

Assigned procedural skill intents:

- `ma.account-context-explanation.v1` — explain selected context, memberships, capabilities, and safe recovery without hidden-scope enumeration.
- `ma.profile-settings-editing.v1` — distinguish editable self-service fields from provider-backed identity and forbidden authority fields.
- `ma.notification-triage.v1` — triage personal notification lanes, acknowledgement boundaries, and source-opening reauthorization.
- `ma.personal-digest-request.v1` — draft digest/export requests, provider/runtime blocker explanations, and redaction caveats.

Assigned reference intents:

- `ma.auth-context-guide.v1`.
- `ma.profile-settings-policy.v1`.
- `ma.notification-redaction-guide.v1`.
- `ma.personal-digest-export-policy.v1`.

## Prompt intent

Guide authorized users through giving the signed-in human a safe personal control point for profile, settings, selected context, personal attention, notifications, and governed digest/export requests. Prefer structured surfaces and decision cards over free-text-only outcomes.

## Required denials and recovery

The agent must safely recover from disabled actor, inactive membership, missing selected context, cross-tenant/customer reads, unsupported role/capability/account-status changes, hidden workstream/source openings, provider/runtime-unavailable digest requests, missing model/provider config, missing tool-boundary grant, unassigned/inactive/oversized/redaction-failed skill/reference load, and authority-expanding prompt/skill/reference content. Safe recovery names the visible denial category, selected scope if safe, suggested non-sensitive next step, and trace/correlation id when available.

## Tests and traces

See `../tests/coverage.md` and `../traces/work-traces.md`.


## `human_chat_tool_plan` behavior boundary

`my-account-agent` may participate in `human_chat_tool_plan` only as a plan proposer for catalog-bound, backend-visible work in this workstream. It may summarize the request, ask clarifying questions, draft safe inputs, and propose steps using the representative shared governed tool ids `my_account.update_profile_settings` for actions `action-update-my-settings`. The proposal surface must state required capabilities `my_account.update_profile_settings`, side effects, validation needs, approval gates, idempotency, transaction boundaries, result surfaces, and trace expectations.

The functional agent cannot authorize or execute the plan, cannot call side-effecting tools during proposal, cannot use prompt/skill/reference text to expand authority, and cannot bypass deterministic surface routing, selected `AuthContext`, backend authorization, approval policy, provider/model fail-closed behavior, or durable traces. Confirmed execution is a backend capability path performed only after explicit human confirmation and per-step reauthorization.
