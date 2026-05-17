# Capability to Layer Map

## Purpose

This derived traceability map lets future agents navigate from each DCA capability contract to the affected app-description layers without treating endpoints, workflows, agents, entities, timers, consumers, or UI actions as capability roots.

Authoritative meaning remains in `10-capabilities/`, `15-operating-model/`, `20-behavior/`, `30-tests/`, `40-auth-security/`, `50-observability/`, and `55-ui/`. This file is a navigation and change-impact aid.

## Capability to affected layers

| Capability | Contract maturity | Behavior links | Tests affected | Auth/security links | Observability links | UI links | Generation / realization links |
|---|---|---|---|---|---|---|---|
| `CAP-00` secure tenant/user foundation | detailed foundation contract | `20-behavior/state-models/01-lifecycle-foundation.md`, `20-behavior/flows/02-lifecycle-and-exception-flows.md`; future foundation flow refresh should add explicit account, invitation, support-access, and `/api/me` behavior | foundation acceptance, negative, regression, and operational tests for tenant isolation, disabled users, role/scope denial, invite lifecycle, support access, billing boundary, `/api/me`, admin audit, and frontend secret boundary | all files in `40-auth-security/`, especially `foundation-onboarding-admin-boundaries.md`, `identity-and-trust.md`, and `authorization-rules.md` | `50-observability/audit-trace-and-outcomes.md` must include identity, membership, role, invitation, support-access, billing-boundary, protected read, denial, and admin-agent traces | `55-ui/ui-surfaces.md` must preserve sign-in, context selection, Users, Invitations, Roles/Memberships, Access Review, Support Access, Admin Audit, Tenant/Customer Settings, and foundation decision cards | seed foundation in `60-generation/implementation-slices.md`; future realization starts here before DCA-specific automation |
| `CAP-01` lifecycle orchestration | lightweight routing contract | `20-behavior/flows/02-lifecycle-and-exception-flows.md`, `20-behavior/state-models/01-lifecycle-foundation.md` | lifecycle gate, invalid transition, approval, exception, trace, and tenant-scope tests | `authorization-rules.md`, `boundary-and-surface-rules.md` | lifecycle gate, exception, policy, and decision traces in `audit-trace-and-outcomes.md` | Mission Control and Lifecycle Workbench | Slice 4 lifecycle expansion |
| `CAP-02` telemetry intelligence | lightweight routing contract | lifecycle state model plus lifecycle/exception flow; supplies flow consumes telemetry evidence | telemetry freshness, duplicate ingest, stale data, redaction, and evidence-query tests | `data-protection.md`, `boundary-and-surface-rules.md` | telemetry data-access, freshness, policy, and outcome trace events | Mission Control, Supplies Autopilot evidence, Audit & Outcomes | Slice 1 supplies evidence; Slice 4 lifecycle expansion |
| `CAP-03` supplies autopilot | detailed vertical capability contract | `20-behavior/flows/01-supplies-autopilot-flow.md`, `20-behavior/flows/02-lifecycle-and-exception-flows.md`, `20-behavior/state-models/01-lifecycle-foundation.md`, `20-behavior/rules/01-approval-and-fail-safe-rules.md` | supplies acceptance, validation, forbidden/tenant-isolation, policy/approval, idempotency/no-op, audit/trace, UI, timer, consumer, integration-failure, and outcome tests | all `40-auth-security/` files; especially backend capability grants, agent tool boundaries, support-access limits, redaction, and integration boundaries | recommendation, policy invocation, decision card, fulfillment, suppression, integration, data-access, tool-use, and outcome traces | Supplies Autopilot, Approvals & Exceptions, Shipment Trace Drawer, Audit & Outcomes | Slice 1 supplies autopilot foundation |
| `CAP-04` service coordination | lightweight routing contract | lifecycle/exception flow and lifecycle state model | service SLA, dispatch, remote-fix approval, integration, trace, and denial tests | `authorization-rules.md`, `agent-permissions.md` | service fault, dispatch, SLA, recommendation, decision, and integration traces | Mission Control, Approvals & Exceptions, Lifecycle Workbench | Slice 4 lifecycle expansion |
| `CAP-05` meter billing review | lightweight routing contract | lifecycle/exception flow and lifecycle state model | billing anomaly, final-read, approval, billing-boundary, redaction, and outcome tests | `data-protection.md`, `authorization-rules.md` | meter-read, anomaly, billing-impact approval, and outcome traces | Lifecycle Workbench, Approvals & Exceptions, Audit & Outcomes | Slice 4 lifecycle expansion |
| `CAP-06` onboarding installation | lightweight routing contract | lifecycle/exception flow and lifecycle state model | readiness gate, installer handoff, waiver/approval, customer-safe view, and trace tests | `authorization-rules.md`, `boundary-and-surface-rules.md` | onboarding gate, waiver, evidence, decision, and outcome traces | Lifecycle Workbench, Mission Control, Approvals & Exceptions | Slice 4 lifecycle expansion |
| `CAP-07` offboarding retention | lightweight routing contract | lifecycle/exception flow and lifecycle state model; supplies flow must pause/cancel shipment on offboarding | offboarding gate, retention/deauthorization, final-read, supply suppression, timer, audit, and denial tests | `data-protection.md`, `authorization-rules.md` | retention, deletion/redaction, access revocation, final-read, suppression, and policy traces | Lifecycle Workbench, Approvals & Exceptions, Audit & Outcomes | Slice 4 lifecycle expansion |
| `CAP-08` policy governance | lightweight routing contract | approval/fail-safe rules and lifecycle/exception flow | proposal-not-activation, simulation/replay, approval, rollback, policy-version citation, and authorization tests | `authorization-rules.md`, `agent-permissions.md` | policy proposal, simulation, commit, rollback, policy invocation, and outcome-impact traces | Policy Center, Decision Card, Audit & Outcomes | Slice 3 policy governance and learning loop |
| `CAP-09` owner command center | lightweight routing contract | all active workflow/decision behavior feeds command-center views | scoped dashboard, digest, supervision action, realtime/stale-state, trace-link, and denial tests | `authorization-rules.md`, `boundary-and-surface-rules.md` | material-event, digest, work, decision, and outcome summaries | Owner Brief, Mission Control, Approvals & Exceptions | Slice 2 owner mission control and digest |
| `CAP-10` audit outcome review | lightweight routing contract | all consequential behavior emits trace/outcome facts consumed here | trace search, redaction, support-access, export/retention, outcome-link, and restricted-audit tests | `data-protection.md`, `foundation-onboarding-admin-boundaries.md` | `audit-trace-and-outcomes.md` is the primary observability source | Audit & Outcomes, Owner Brief, Policy Center | Slice 1 trace foundation, expanded in all later slices |

## First detailed vertical slice trace

`CAP-03` proves the current capability-first shape for DCA-specific automation:

```text
CAP-00 AuthContext and scoped permission
-> CAP-02 telemetry/evidence refresh
-> CAP-03 supply recommendation and policy invocation
-> CAP-03 decision card or policy-gated auto-ship
-> CAP-03 fulfillment/suppression side effect
-> CAP-10 trace/outcome review
-> CAP-08 policy proposal only when a human-approved learning loop is triggered
```

Required cross-layer checks for this slice:

- Behavior: supplies flow includes auto-ship, decision-card, suppression, pause/resume, recheck, and outcome follow-up paths.
- Security: backend authorization enforces `AuthContext`, tenant/customer scope, supplies capability grants, support-access limits, and agent-tool boundaries.
- Tests: future test refresh must materialize CAP-03 success, validation, forbidden, tenant-isolation, approval-bypass, idempotency, audit/trace, frontend secret-boundary, and outcome checks.
- Observability: trace events cite policy document/version, clause ids, evidence snapshots, actor/caller, correlation id, idempotency key, decision authority, external side-effect reference when safe, and redaction marker.
- UI: Supplies Autopilot, Supply Decision Card, Shipment Trace Drawer, Approvals & Exceptions, and Audit & Outcomes all link back to the same recommendation/card/order/trace ids.
- Generation: future realization remains localized to the seed foundation plus Slice 1 unless lifecycle, policy governance, or command-center behavior is intentionally broadened.

## Change-impact rules for future agents

When a capability contract changes, update or reassess:

1. `10-capabilities/capabilities-index.md` if id, class, actors/callers, scope, or exposure surfaces changed.
2. `15-operating-model/` when goals, agents, policies, approval gates, decisions, evidence, traces, authority, or outcomes change.
3. `20-behavior/` when flow, state, transition, idempotency, no-op, failure, or side-effect semantics change.
4. `30-tests/` when success, validation, forbidden, tenant-isolation, approval, idempotency, audit, UI, integration, or outcome expectations change.
5. `40-auth-security/` when actors, permissions, support access, data boundaries, tool authority, JWT/API rules, or redaction change.
6. `50-observability/` when audit/work/decision/policy/tool/data-access/outcome events or retention/redaction expectations change.
7. `55-ui/` when available actions, decision cards, supervision queues, trace links, realtime behavior, or style constraints change.
8. `60-generation/implementation-slices.md` when realization order or localized regeneration boundary changes.
9. `00-system/readiness-status.md` when the change affects generation readiness or removes/introduces blockers.
