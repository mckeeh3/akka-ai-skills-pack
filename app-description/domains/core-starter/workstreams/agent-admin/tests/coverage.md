# Tests: Agent Admin

## Acceptance

- Given an authorized caller with selected `AuthContext`, when they open Agent Admin, then the dashboard and allowed surfaces render only scoped data and expose only authorized actions.
- Given an allowed action, when it is submitted with valid input, then capability `managed-agent-governance` returns the expected structured result and emits required traces.

## Security and negative

- Disabled users, inactive memberships, Customer Admin selected contexts, role/capability denials, and cross-tenant/customer requests are denied without protected-data leakage.
- Customer Admins cannot see Agent Admin catalog/dashboard surfaces, submit AgentAdminAgent turns, read prompt/skill/reference/manifest/model/tool-boundary/seed material, start prompt-risk review tasks, or draft/approve/activate/roll back behavior changes.
- Agent/tool calls cannot exceed the governed tool boundary or approval policy.
- Browser payloads never expose provider secrets or hidden authority state.

## Idempotency and observability

- Repeated side-effecting actions do not duplicate effects.
- Denials, approval-required outcomes, provider fail-closed states, and trace emissions are verifiable through local Akka/API/UI tests or readiness evidence.
