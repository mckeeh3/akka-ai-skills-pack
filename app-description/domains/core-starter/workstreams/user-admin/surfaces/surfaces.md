# Surfaces: User Admin

## Surface bindings

Uses global surface patterns: dashboard, list-search user directory and invitations, detail-edit user/membership/support access, decision-card risky changes, audit-timeline admin evidence, workflow-status invitation delivery, markdown-response, system-message.

## Action rules

Every consequential browser action has a stable action id, maps to capability `user-and-access-administration`, carries a correlation/idempotency key where needed, and returns a typed result, decision card, workflow status, markdown response, or safe system message.

## States

Surfaces define loading, empty, ready, submitting, validation-error, forbidden, conflict, stale/reconnect, partial-data, provider-fail-closed, and failure states where applicable.
