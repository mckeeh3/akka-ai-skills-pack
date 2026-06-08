# Surfaces: My Account

## Surface bindings

Uses global surface patterns: dashboard, detail-edit profile/settings panels, notification-center, list-search personal attention, markdown-response, system-message, outcome-panel digest/export result.

## Action rules

Every consequential browser action has a stable action id, maps to capability `account-context-and-profile`, carries a correlation/idempotency key where needed, and returns a typed result, decision card, workflow status, markdown response, or safe system message.

## States

Surfaces define loading, empty, ready, submitting, validation-error, forbidden, conflict, stale/reconnect, partial-data, provider-fail-closed, and failure states where applicable.
