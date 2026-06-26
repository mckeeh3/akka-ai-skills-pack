# Traces: My Account

## Uses

Global traces: `../../../../../global/traces/foundation-trace-patterns.md`.

## Required evidence

workstream-log-trace, admin-audit-event for protected self-service changes, agent-work-trace for agent turns, denial/provider traces.

Trace records include worker id/type, execution harness, actor adapter, trace source, actor or service identity, selected `AuthContext`, tenant/customer ids, role/capability basis, correlation/causation/idempotency ids, capability/tool id, policy decision, redaction decisions, denial/failure status, and linked surface/workstream item.


## `human_chat_tool_plan` trace evidence

My Account must emit durable work/audit trace facts for the `human_chat_tool_plan` adapter in addition to existing surface-action traces. Required event types are `human_chat_tool_plan.proposed`, `human_chat_tool_plan.confirmed`, `human_chat_tool_plan.step_started`, `human_chat_tool_plan.step_completed`, `human_chat_tool_plan.step_failed`, `human_chat_tool_plan.step_skipped`, `human_chat_tool_plan.denied`, and `human_chat_tool_plan.provider_blocked`.

Minimum fields: trace/work trace id, correlation id, causation/parent event id, worker id/type (`signed-in-member-human`, `my-account-functional-agent-worker`, or `my-account-system-worker`), harness, actor adapter, trace source, selected `AuthContext`, tenant/customer scope where applicable, functional agent `my-account-agent`, requestedBy, confirmedBy for execution, action ids `action-update-my-profile`, `action-update-my-settings`, `action-notification-mark-read`, `action-notification-dismiss`, `action-notification-archive`, `action-notification-snooze`, and `action-notification-update-preferences`, governed tool ids `my_account.update_profile_settings`, `notification.mark_read`, `notification.dismiss`, `notification.archive`, `notification.snooze`, and `notification.update_preferences`, matching capability ids, input schema ref, plan id, plan snapshot id, step id/sequence/dependencies, idempotency key or redacted hash, authorization decision and basis summary, policy/approval refs, prompt/skill/reference/model/tool-boundary refs for proposal generation, result surface ids `surface-my-profile`, `surface-my-settings`, and `surface-my-account-notification-center`, status, safe error code, redaction classification, and browser-safe input/output summaries.

Trace summaries must distinguish direct surface actions from `human_chat_tool_plan`, preserve no-mutation proposal evidence, record confirmation and per-step transaction outcomes, and omit raw provider secrets, JWTs, invitation tokens, raw email bodies, raw prompts/model payloads, hidden tenant/customer ids, raw tool payloads, and unredacted evidence from browser-visible views.
