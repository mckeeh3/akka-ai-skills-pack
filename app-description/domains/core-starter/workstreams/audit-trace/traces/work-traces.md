# Traces: Audit/Trace

## Uses

Global traces: `../../../../../global/traces/foundation-trace-patterns.md`.

## Required evidence families

Audit/Trace treats audit/work traces as governed product evidence, not merely operational logs. Required event families include:

- `audit_trace.human_surface_action_recorded`
- `audit_trace.api_call_recorded`
- `audit_trace.human_chat_plan_proposed`
- `audit_trace.human_chat_plan_confirmed`
- `audit_trace.human_chat_plan_tool_executed`
- `audit_trace.agent_tool_call_recorded`
- `audit_trace.agent_tool_call_denied`
- `audit_trace.prompt_skill_reference_model_used`
- `audit_trace.governed_tool_invocation_recorded`
- `audit_trace.data_access_recorded`
- `audit_trace.policy_invocation_recorded`
- `audit_trace.decision_or_approval_recorded`
- `audit_trace.action_denied`
- `audit_trace.trace_read_performed`
- `audit_trace.trace_read_denied`
- `audit_trace.correlation_lookup_performed`
- `audit_trace.denial_investigation_performed`
- `audit_trace.investigation_summary_generated`
- `audit_trace.support_access_granted`
- `audit_trace.support_access_used`
- `audit_trace.support_access_denied`
- `audit_trace.support_access_expired_or_revoked`
- `audit_trace.export_requested`
- `audit_trace.export_approval_required`
- `audit_trace.export_denied`
- `audit_trace.export_prepared_redacted`
- `audit_trace.runtime_validation_evidence_linked`
- `audit_trace.trace_gap_detected`
- `audit_trace.retention_expired`

## Minimum trace fields

Every consequential trace record includes:

- event id, timestamp, tenant id/safe tenant label, optional customer/account safe label;
- correlation id, causation/parent event id, session/conversation id, and workTraceId where applicable;
- worker id/type, execution harness, actor adapter/source (`surface_action`, `human_chat_tool_plan`, `agent_tool_call`, `api_call`, `consumer_reaction`, `projection_update`, `internal_call`, `timer_invocation`, or `runtime_validation`);
- human actor identity where applicable: account id, email/display label, role, org, support-access grant id/scope/expiry;
- agent identity where applicable: AgentDefinition id/version, workstream, model config ref, prompt/skill/reference/manifest versions, requested-by user, ToolPermissionBoundary decision;
- governed tool id, adapter tool id where applicable, capability id `audit-and-trace-investigation`, data resource summary, policy/approval/guardrail refs;
- action type, status, authorization decision, authorization basis summary, denial reason/policy reference when denied;
- input/output/evidence summary that is safe for the caller and result surface refs;
- redaction classification: safe, sensitive, redacted, or secret-never-store;
- partial-failure summary, trace-gap classification, runtime-validation evidence refs, export/support-access refs where applicable;
- retention classification and expiry time where known.

## Redaction, visibility, and indexing

Search rows and keyword indexes use deterministic metadata/summary fields only. Full payloads, raw prompt/model outputs, provider credentials, bearer/session tokens, invite secret tokens, backend secrets, frontend-secret material, and hidden cross-tenant identifiers are never indexed or exposed to unauthorized readers.

Default detail surfaces show safe summaries and redacted evidence. Sensitive detail requires explicit capability such as `trace.sensitive.read`. Sensitive/raw export requires explicit approval and grant; redacted export is the default allowed form when export is enabled.

Trace read access does not imply access to the underlying domain record. If underlying access is denied, Audit/Trace shows redacted summary, denial evidence, or `not_found_or_redacted` according to caller scope.

## Correlation model

Audit/Trace preserves correlation across:

```text
human surface action → api_call/internal_call → governed tool → capability → result surface
human chat request → proposed plan → confirmation → per-tool execution → result/partial failure
agent turn → prompt/skill/reference/model assembly → agent_tool_call/data access/policy decision → response/result
workflow/consumer/timer/internal call → trace event/projection update → dashboard/search/timeline
runtime-validation run → source-alignment evidence → trace/correlation/denial/runtime gap finding
```

Tool-call traces link to parent human/agent/workflow requests. Support-access traces link grant/use/read/export events. Denial traces link attempted actor adapter, governed tool, policy ref, and safe target handle. Timeline/correlation views must preserve causation links and show trace gaps instead of inventing missing events.

## Trace reads and investigation summaries

Trace reads, denied trace reads, correlation lookups, denial investigations, summary generation, support-access review, and export request/result reads are themselves durable trace events. Summary traces include evidence refs, selected scope, redaction disclaimer, requestedBy/confirmedBy where applicable, result surface, and unresolved unknowns.

## Retention model

Default retention is 90 days unless a governed tenant policy records another supported value. Records are immutable until retention expiry. Retention expiry itself remains diagnosable through retained operational evidence that does not reveal expired payloads beyond authorized retention status.
