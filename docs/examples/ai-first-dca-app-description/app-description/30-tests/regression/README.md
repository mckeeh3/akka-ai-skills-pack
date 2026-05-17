# Regression Tests

Regression cases preserve no-op, idempotency, replay, projection rebuild, policy-version, and stale-frontend behavior.

- `01-idempotency-and-policy-regression.md` covers:
  - invitation, membership, role, support-access, `/api/me`, and admin-agent retry behavior;
  - supplies natural-dedupe keys, duplicate telemetry, repeated approvals, fulfillment retry, offboarding replay, policy-gate regression, tenant isolation after projection rebuild, and UI realtime/idempotent updates.
