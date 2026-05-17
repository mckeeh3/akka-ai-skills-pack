# Capability: Lifecycle Orchestration

This is a lightweight capability contract for future refinement. It records the governed boundary for moving customers, devices, and DCA collectors through lifecycle gates without inventing vendor thresholds, integration payloads, or implementation contracts.

## Capability definition

- capability-id: `lifecycle-orchestration`
- capability number: `CAP-01`
- class: workflow, command, read/evidence, approval
- purpose: coordinate lifecycle progression across customer, device, and DCA collector states using evidence, policy gates, approvals, and exception handling.
- business outcome: tenant operators can see and control lifecycle readiness while agents/workflows prepare evidence and recommendations; consequential transitions remain policy- or human-authorized.

## In-scope outcomes

- Track customer, device, and collector lifecycle state using the vocabulary in `../20-behavior/state-models/01-lifecycle-foundation.md`.
- Evaluate lifecycle gate readiness for onboarding, installation, operational activation, service risk, offboarding, removal, deauthorization, archive, and exception states.
- Create decision cards when evidence is missing, stale, contradictory, high-impact, or policy-sensitive.
- Pause, resume, defer, or reject lifecycle transitions with durable rationale and trace links.
- Expose scoped lifecycle queues, blocked gates, evidence summaries, and reviewer actions.

## Out-of-scope outcomes

- Vendor-specific CRM, ERP, DCA, ticketing, or installation API contracts.
- Automatic customer archival, device disposal, final billing, retention/deletion, or customer communication without accepted policy or approval.
- Replacing detailed onboarding, service, billing, offboarding, or policy-governance contracts.

## Authority and contract

- actors/callers: tenant operations supervisor, dealer owner, customer admin where delegated, lifecycle coordinator agent, onboarding/service/offboarding workflows, scheduled gate recheck timers, trusted integration consumers.
- AuthContext/scope: authenticated account or trusted service identity, active tenant membership or service ACL, selected tenant/customer context, lifecycle/device/collector permission, and tenant/customer filters on every read/write.
- inputs: scoped lifecycle transition request, entity type/id, current state, proposed next state, evidence snapshot ids, policy document/version, reason, correlation id, and idempotency key.
- outputs: lifecycle state summary, readiness gaps, allowed next actions, decision-card link, safe denial shape, trace links, and redaction markers.
- side effects: lifecycle event, gate evaluation record, workflow start/advance/pause, decision card, evidence request, timer recheck, notification, and work/audit trace.
- idempotency: duplicate transition or recheck for the same entity/state/gate/evidence version returns or updates the existing gate result instead of creating duplicate transitions or cards.
- policy/approval: consequential transitions affecting service, supplies, billing, retention, customer communication, ownership, or access require active policy authority or accountable human approval.
- exposure surfaces: lifecycle operations UI, JWT HTTP APIs, workflow steps, lifecycle views, decision cards, scoped agent recommendations, timer rechecks, and consumer reactions.

## Required future detail

- Accepted gate matrix per lifecycle transition.
- Exact policy clause ids beyond the examples in `../15-operating-model/policies-and-approval-gates.md`.
- Integration contracts for CRM/ticketing/DCA lifecycle evidence.
- Concrete test specs for transition success, blocked gates, forbidden access, idempotency, audit, and UI/API/action behavior.

## Linked layers

- behavior: `../20-behavior/flows/02-lifecycle-and-exception-flows.md`, `../20-behavior/state-models/01-lifecycle-foundation.md`
- operating model: `../15-operating-model/decisions-exceptions-and-evidence.md`, `../15-operating-model/policies-and-approval-gates.md`
- auth/security: `../40-auth-security/authorization-rules.md`, `../40-auth-security/boundary-and-surface-rules.md`
- observability: `../50-observability/audit-trace-and-outcomes.md`
- UI: `../55-ui/ui-surfaces.md`
- tests: future test refresh under `../30-tests/`
