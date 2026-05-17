# Capability: Offboarding Retention

This is a lightweight capability contract for future refinement. It records the governed boundary for customer offboarding, removal, deauthorization, final-read, retention, archive, and access-revocation work without inventing external integration contracts or retention schedules.

## Capability definition

- capability-id: `offboarding-retention`
- capability number: `CAP-07`
- class: workflow, command, approval, scheduled, read/evidence
- purpose: safely stop operational automation, coordinate removal/deauthorization, preserve required records, and archive customers only after offboarding gates are satisfied.
- business outcome: tenant operators can decommission customers/devices/collectors without accidental shipments, dispatches, billing errors, data loss, or unauthorized access persistence.

## In-scope outcomes

- Create offboarding plans and identify affected devices, collectors, service work, supply shipments, meter reads, billing items, integrations, access, and retention obligations.
- Pause, cancel, suppress, or explicitly allow pending service/supply/billing workflows according to policy and approval.
- Coordinate device removal, disposition, collector removal/deauthorization, token/access revocation, final-read capture, final billing readiness, and archive gates.
- Create decision cards for ownership, lease, final-read, pending shipment, collector-online, retention/deletion/anonymization, dispute, or customer-sensitive communication issues.
- Apply retention holds, deletion/anonymization decisions, or archive transitions only with recorded authority.

## Out-of-scope outcomes

- Legal retention schedules, deletion algorithms, invoice generation, field-service APIs, DCA deauthorization protocols, or customer communication templates not accepted by future specs.
- Destructive deletion, anonymization, archive completion, or final billing export without authorized decision and audit trail.
- Replacing foundation user/access administration from `secure-tenant-user-foundation`.

## Authority and contract

- actors/callers: tenant operations supervisor, dealer owner, data steward, customer admin where delegated, offboarding coordinator agent, billing owner, supplies owner, offboarding workflow, retention/expiry timers, integration callers.
- AuthContext/scope: authenticated account or trusted service identity, selected tenant/customer context, offboarding/device/collector/retention/final-billing permissions, and tenant/customer filters.
- inputs: offboarding plan/action request, customer/device/collector ids, termination/reduction context, evidence snapshot ids, pending workflow references, retention basis, proposed action, correlation id, and idempotency key.
- outputs: offboarding plan/status, blocker list, affected-work summary, decision-card link, retention/archive status, safe denial shape, and trace links.
- side effects: offboarding workflow, lifecycle transitions, pause/cancel/suppress messages to affected workflows, removal/deauthorization requests, final-read requests, retention hold/deletion/anonymization proposal, access revocation handoff, timers, notifications, and traces.
- idempotency: duplicate offboarding actions for the same customer/gate/evidence version return or update existing work instead of duplicating removal tickets, cancellation requests, or destructive decisions.
- policy/approval: apply accepted `OFF-*` and related `BILL-*`/`SUP-*` clauses; destructive or externally consequential actions require human approval unless a future accepted policy grants a narrow safe boundary.
- exposure surfaces: offboarding UI, HTTP APIs, offboarding workflow, retention timers, decision cards, audit/evidence views, scoped agent recommendations, and backend integration calls.

## Required future detail

- Accepted retention/deletion/anonymization policies and legal holds.
- Integration contracts for field removal, DCA deauthorization, access revocation, and billing handoff.
- Archive package schema and redaction rules.
- Concrete tests for automation stop, final-read blockers, retention decisions, tenant isolation, idempotency, audit, and destructive-action approval.

## Linked layers

- behavior: `../20-behavior/flows/02-lifecycle-and-exception-flows.md`, `../20-behavior/state-models/01-lifecycle-foundation.md`
- operating model: `../15-operating-model/policies-and-approval-gates.md`, `../15-operating-model/decisions-exceptions-and-evidence.md`
- auth/security: `../40-auth-security/data-protection.md`, `../40-auth-security/authorization-rules.md`
- observability: `../50-observability/audit-trace-and-outcomes.md`
- UI: `../55-ui/ui-surfaces.md`
- tests: future test refresh under `../30-tests/`
