# DCA Test Index

These test specifications are authoritative app-description expectations, not generated test code. They make the secure SaaS foundation and the first detailed DCA vertical slice (`CAP-03` Supplies Autopilot) concrete enough for future realization planning.

- acceptance:
  - `acceptance/01-foundation-and-supplies-acceptance.md`
- negative:
  - `negative/01-security-and-approval-bypass.md`
- regression:
  - `regression/01-idempotency-and-policy-regression.md`
- operational:
  - `operational/01-audit-trace-and-outcomes.md`

## Required coverage summary

- `CAP-00` secure tenant/user foundation: tenant/customer isolation, disabled-user denial, role/scope denial, invitation lifecycle, support access, billing boundary, `/api/me`, AdminAuditEvent, frontend secret boundary, and admin-agent approval boundaries.
- `CAP-03` supplies autopilot: scoped recommendation/evidence queries, policy-gated auto-ship, decision-card approvals, suppression, idempotency/no-op behavior, audit/work traces, integration failure visibility, UI/API/tool/timer/consumer exposure behavior, and outcome follow-up.

## Open downstream fixtures

Future executable slices still need concrete fixture payloads for DCA telemetry, inventory, fulfillment provider responses, contract entitlement summaries, WorkOS test principals, and policy threshold datasets. These fixtures must preserve the scenarios listed in this layer rather than weakening them during implementation.
