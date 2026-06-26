# Traces: Governance/Policy

## Uses

Global traces: `../../../../../global/traces/foundation-trace-patterns.md`.

## Required evidence

Governance/Policy emits admin-audit events, policy-decision traces, workstream-log traces, and agent-work traces.

Trace records include actor, selected `AuthContext`, tenant/customer/account scope where applicable, role/capability basis, correlation id, idempotency key reference for writes, policy id, policy value type, scope selector, source of effective value (`saas_default` or `tenant_override`), old value, new value, effective value, change reason for writes, override indicator, winning-scope explanation, denial/failure status, redaction decisions, and linked surface/workstream item.

Runtime policy-decision traces should explain why an action was allowed or governed. They include the effective policy value, the winning scope, whether the value came from SaaS default or tenant override, who last changed the winning override when applicable, when it changed, the recorded reason, and the protected action/tool/agent/workstream/role/customer/account context used for the decision.

Policy history may also link runtime outcomes influenced by a policy when practical, such as aggregated counts or trace references, without exposing hidden tenant/customer facts or raw sensitive payloads.

## `human_chat_tool_plan` trace evidence

Governance/Policy must emit durable work/audit trace facts for the `human_chat_tool_plan` adapter in addition to direct surface-action traces. Required event types are `human_chat_tool_plan.proposed`, `human_chat_tool_plan.confirmed`, `human_chat_tool_plan.step_started`, `human_chat_tool_plan.step_completed`, `human_chat_tool_plan.step_failed`, `human_chat_tool_plan.step_skipped`, and `human_chat_tool_plan.denied`.

Minimum fields: trace/work trace id, correlation id, selected `AuthContext`, functional agent `governance-policy-agent`, requestedBy, confirmedBy for execution, action id, governed tool id, capability id, input schema ref, plan id, plan snapshot id, idempotency key or redacted hash, authorization decision and basis summary, policy refs, result surface id, status, safe error code, redaction classification, and browser-safe input/output summaries.

Trace summaries must distinguish direct surface actions from `human_chat_tool_plan`, preserve no-mutation proposal evidence, record confirmation and per-step transaction outcomes, and omit raw provider secrets, JWTs, invitation tokens, raw email bodies, raw prompts/model payloads, hidden tenant/customer ids, raw tool payloads, and unredacted evidence from browser-visible views.
