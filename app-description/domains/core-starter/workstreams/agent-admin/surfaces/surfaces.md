# Surfaces: Agent Admin

## Surface bindings

Uses global surface patterns: dashboard, list-search agent catalog, detail-edit behavior detail, governance-diff proposed changes, decision-card activation/rollback review, audit-timeline runtime traces, markdown-response, system-message.

## Action rules

Every consequential browser action has a stable action id, maps to capability `managed-agent-governance`, carries a correlation/idempotency key where needed, and returns a typed result, decision card, workflow status, markdown response, or safe system message.

## States

Surfaces define loading, empty, ready, submitting, validation-error, forbidden, conflict, stale/reconnect, partial-data, provider-fail-closed, and failure states where applicable.
