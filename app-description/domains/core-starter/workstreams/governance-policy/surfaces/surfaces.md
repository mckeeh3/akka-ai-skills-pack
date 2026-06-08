# Surfaces: Governance/Policy

## Surface bindings

Uses global surface patterns: dashboard, list-search policy proposals, governance-diff policy/threshold changes, decision-card approve/reject/rollback, outcome-panel results, audit-timeline policy history, markdown-response, system-message.

## Action rules

Every consequential browser action has a stable action id, maps to capability `governance-policy-lifecycle`, carries a correlation/idempotency key where needed, and returns a typed result, decision card, workflow status, markdown response, or safe system message.

## States

Surfaces define loading, empty, ready, submitting, validation-error, forbidden, conflict, stale/reconnect, partial-data, provider-fail-closed, and failure states where applicable.
