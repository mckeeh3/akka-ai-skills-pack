# Tests: Agent Admin

## Acceptance

- Given an authorized caller with selected `AuthContext`, when they open Agent Admin, then the dashboard and allowed surfaces render only scoped data and expose only authorized actions.
- Given an allowed action, when it is submitted with valid input, then capability `managed-agent-governance` returns the expected structured result and emits required traces.
- Behavior proposal coverage includes draft, submit, approve, reject with reason, defer with reason/follow-up, cancel, ready-for-activation, activation confirmation, rollback confirmation, deactivation confirmation, stale version conflict, provider/runtime blocked, tool-boundary blocked, approval-required, and idempotent repeat/no-op paths.
- Prompt-risk review coverage includes start/read/cancel, provider/model/runtime/tool-boundary fail-closed, completed real model-backed advisory result, accept/reject into proposal evidence, and denial of blocked/deferred/fixture-only/model-less review acceptance.
- Seed material coverage includes search/filter/reset, provenance open, prepare import, start import, cancel import, conflict/customization-preservation blockers, provider/runtime blockers, open target agent detail, and redacted trace opens.

## Security and negative

- Disabled users, inactive memberships, Customer Admin selected contexts, role/capability denials, and cross-tenant/customer requests are denied without protected-data leakage.
- Customer Admins cannot see Agent Admin catalog/dashboard surfaces, submit AgentAdminAgent turns, read prompt/skill/reference/manifest/model/tool-boundary/seed material, start prompt-risk review tasks, or draft/approve/activate/roll back behavior changes.
- Agent/tool calls cannot exceed the governed tool boundary or approval policy.
- Browser payloads never expose provider secrets or hidden authority state.

## Idempotency and observability

- Repeated proposal decisions, lifecycle confirmations, prompt-risk review decisions, and seed-import starts/cancellations do not duplicate effects and retain correlation/trace evidence.
- Denials, approval-required outcomes, provider fail-closed states, tool-boundary denials, stale/conflict outcomes, hidden/not-found results, and trace emissions are verifiable through local Akka/API/UI tests or readiness evidence.
