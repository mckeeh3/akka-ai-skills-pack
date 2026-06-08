# Surfaces: Audit/Trace

## Surface bindings

Uses global surface patterns: dashboard, list-search audit search, audit-timeline detail, detail-edit investigation note, decision-card export/redaction approval, markdown-response, system-message, outcome-panel audit summary.

## Action rules

Every consequential browser action has a stable action id, maps to capability `audit-and-trace-investigation`, carries a correlation/idempotency key where needed, and returns a typed result, decision card, workflow status, markdown response, or safe system message.

## States

Surfaces define loading, empty, ready, submitting, validation-error, forbidden, conflict, stale/reconnect, partial-data, provider-fail-closed, and failure states where applicable.
