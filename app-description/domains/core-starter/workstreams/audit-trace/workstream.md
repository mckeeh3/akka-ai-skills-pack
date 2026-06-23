# Workstream: Audit/Trace

## Purpose

Search, inspect, explain, redact, summarize, export, and annotate audit/work trace evidence for the selected scope.

## Functional agent

Owns `audit-trace-agent` as its exactly-one user-facing functional-agent binding. Runtime instances are selected-context workstream logs, not page sessions.

## Capability binding

Primary capability: `../../capabilities/audit-and-trace-investigation.md`.

## Attention model

Backend-owned attention includes stable categories `audit_trace.protected_action.failed`, `audit_trace.protected_action.denied`, `audit_trace.export.approval_required`, `audit_trace.suspicious_activity.review_needed`, `audit_trace.provider_runtime.blocked`, `audit_trace.investigation_note.unresolved`, `audit_trace.trace_gap.detected`, `audit_trace.summary_task.ready_for_review`, and `audit_trace.summary_task.blocked`. Producers are audit/work trace ingestion, denial/failure consumers, export request state, summary-task lifecycle, investigation note lifecycle, retention/projection gap detection, and provider/model/tool readiness checks. Each attention item has a backend idempotency key formed from selected scope, trace/correlation or task safe handle, category, and lifecycle status; severity is backend-authored (`info`, `needs_review`, `approval_required`, `blocked`, `risk`) and terminal/resolved source states clear or downgrade the item. Counts feed the left rail and, where the signed-in human owns or is assigned to the review/export/note, My Account aggregation without exposing hidden traces, source object ids, prompts, provider data, cross-scope facts, or hidden counts.

## Readiness posture

This node captures current intent only. Runtime readiness still requires local Akka/API/UI validation and model/provider fail-closed proof where applicable.


## Confirmed human chat tool-plan exposure

This workstream exposes a bounded `human_chat_tool_plan` adapter for execution-oriented chat prompts after deterministic no-mutation surface routing declines the prompt. The adapter is current-intent only until runtime tasks implement it. It allows `audit-trace-agent` to propose a plan for the representative prompt **append investigation note "provider blocked; retry after config" to this trace**, but it never permits prompt-only mutation, hidden target enumeration, or AI-autonomous authority.

Execution is allowed only when all of the following hold: the proposal was created with `noMutation=true`; the human explicitly confirms the exact plan snapshot; the backend reauthorizes the selected `AuthContext`, actor, capability, tool boundary, lifecycle state, approval policy, tenant/customer ownership, and idempotency on every step; and each step executes through its declared governed surface/action path as a separate transaction boundary.

Representative catalog binding: actions `action-audit-trace-append-investigation-note`; governed tool ids `draft-investigation-note`; capabilities `audit.trace.investigation_note.append`; input contract `schema.audit-trace.investigation-note.v1` with visible `traceId`/`correlationId`, `noteText`, selected scope, and idempotency key; expected result surfaces `surface-audit-trace-investigation-note`. The allowed effect is to append a browser-safe human investigation note annotation to an authorized visible trace/correlation; it cannot edit retained evidence, weaken redaction, export traces, or mutate source authorization/policy state.
