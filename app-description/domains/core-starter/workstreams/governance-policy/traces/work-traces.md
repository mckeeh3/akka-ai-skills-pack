# Traces: Governance/Policy

## Uses

Global traces: `../../../../../global/traces/foundation-trace-patterns.md`.

## Required evidence

policy-decision-trace, admin-audit-event, workstream-log-trace, agent-work-trace, and impact-analysis task/result events.

Trace records include actor, selected `AuthContext`, tenant/customer ids, role/capability basis, correlation id, idempotency key reference, capability/tool id, proposal/task id, command mode or impact-result disposition where applicable, policy decision, redaction decisions, denial/failure status, provider/runtime blocker status, and linked surface/workstream item.


## `human_chat_tool_plan` trace evidence

Governance/Policy must emit durable work/audit trace facts for the `human_chat_tool_plan` adapter in addition to existing surface-action traces. Required event types are `human_chat_tool_plan.proposed`, `human_chat_tool_plan.confirmed`, `human_chat_tool_plan.step_started`, `human_chat_tool_plan.step_completed`, `human_chat_tool_plan.step_failed`, `human_chat_tool_plan.step_skipped`, `human_chat_tool_plan.denied`, and `human_chat_tool_plan.provider_blocked`.

Minimum fields: trace/work trace id, correlation id, causation/parent event id, selected `AuthContext`, tenant/customer scope where applicable, functional agent `governance-policy-agent`, requestedBy, confirmedBy for execution, action ids `action-governance-policy-draft-proposal`, governed tool ids `governance.policy.propose`, capability ids `governance.policy.propose`, input schema ref, plan id, plan snapshot id, step id/sequence/dependencies, idempotency key or redacted hash, authorization decision and basis summary, policy/approval refs, prompt/skill/reference/model/tool-boundary refs for proposal generation, result surface ids `surface-governance-policy-proposal`, status, safe error code, redaction classification, and browser-safe input/output summaries.

Trace summaries must distinguish direct surface actions from `human_chat_tool_plan`, preserve no-mutation proposal evidence, record confirmation and per-step transaction outcomes, and omit raw provider secrets, JWTs, invitation tokens, raw email bodies, raw prompts/model payloads, hidden tenant/customer ids, raw tool payloads, and unredacted evidence from browser-visible views.
