# Tests: Audit/Trace

## Acceptance

- Given an authorized caller with selected `AuthContext`, when they open Audit/Trace, then the dashboard and allowed surfaces render only scoped data and expose only authorized actions.
- Given an allowed action, when it is submitted with valid input, then capability `audit-and-trace-investigation` returns the expected structured result and emits required traces.

## Security and negative

- Disabled users, inactive memberships, role/capability denials, and cross-tenant/customer requests are denied without protected-data leakage.
- Agent/tool calls cannot exceed the governed tool boundary or approval policy.
- Browser payloads never expose provider secrets or hidden authority state.

## Idempotency and observability

- Repeated side-effecting actions do not duplicate effects.
- Denials, approval-required outcomes, provider fail-closed states, and trace emissions are verifiable through local Akka/API/UI tests or readiness evidence.
