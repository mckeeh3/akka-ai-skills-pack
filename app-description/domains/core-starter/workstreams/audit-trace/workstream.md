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
