# 30 Tests

This layer defines authoritative test expectations for the DCA app-description refresh. These are behavior specifications for future generation, not executable tests.

## Index

- `test-index.md` — navigation and required coverage summary.
- `acceptance/01-foundation-and-supplies-acceptance.md` — successful secure foundation and Supplies Autopilot behavior.
- `negative/01-security-and-approval-bypass.md` — tenant isolation, disabled-user, role/scope denial, approval-bypass, and validation failures.
- `regression/01-idempotency-and-policy-regression.md` — no-op/idempotency, replay safety, policy-version, and frontend stale-state regressions.
- `operational/01-audit-trace-and-outcomes.md` — AdminAuditEvent, work/decision/policy/tool/data-access traces, frontend secret-boundary checks, integration failure visibility, and outcome loops.

## Required baseline coverage

- `CAP-00` secure tenant/user foundation must verify WorkOS authentication vs Akka-owned authorization, Tenant/Customer isolation, Account/Profile/Settings, Membership/Role/Permission, Invitation lifecycle, support access, billing boundary, `/api/me`, admin-agent boundaries, and AdminAuditEvent emission.
- `CAP-03` Supplies Autopilot must verify scoped evidence, policy-gated auto-ship, decision-card approval, suppression, idempotency, agent/tool authority, integration retry safety, UI/API/tool/timer/consumer surfaces, audit/work traces, and outcome follow-up.
- UI tests must prove frontend capability hints improve usability but never replace backend authorization, and browser assets/API payloads do not expose backend secrets.

## Remaining fixture gaps

Future executable slices must still define concrete provider and domain fixtures for DCA telemetry, contracts/entitlements, inventory/fulfillment, email delivery/outbox, WorkOS/JWT principals, policy thresholds, and deterministic agent/tool behavior.
