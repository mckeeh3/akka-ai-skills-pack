# Capability: Service Coordination

This is a lightweight capability contract for future refinement. It records the governed boundary for service-risk interpretation, ticket drafting/creation, dispatch review, and outcome tracing without inventing ticketing-system contracts, SLA windows, or part-cost thresholds.

## Capability definition

- capability-id: `service-coordination`
- capability number: `CAP-04`
- class: workflow, proposal, approval, command, read/evidence, reactive
- purpose: coordinate device fault, SLA, remote-fix, dispatch, parts, and replacement-review work using scoped evidence and policy gates.
- business outcome: tenant service teams can delegate routine service triage while retaining human authority for expensive, urgent, contract-sensitive, repeated-fault, replacement, or customer-sensitive actions.

## In-scope outcomes

- Interpret device fault, telemetry, service-history, lifecycle, SLA, contract, and part-availability evidence.
- Draft service recommendations, remote-fix attempts, ticket creation requests, dispatch options, replacement review cards, and outcome follow-up.
- Create or advance service workflows only within accepted policy and integration boundaries.
- Escalate high-cost, emergency, repeated-fault, contract-mismatch, or customer-sensitive cases with evidence and alternatives.
- Link resolution outcomes to device lifecycle, customer health, SLA, and traces.

## Out-of-scope outcomes

- Vendor-specific ticketing, remote-access, technician scheduling, parts, or dispatch API schemas.
- Automatic emergency dispatch, replacement authorization, contract exception, or sensitive customer communication without accepted policy or approval.
- Supplies replenishment, billing-event generation, or offboarding archive decisions except as evidence inputs or blockers.

## Authority and contract

- actors/callers: tenant service dispatcher, operations supervisor, service coordinator agent, fleet health agent, telemetry/fault consumer, service workflow, technician/service integration caller, dealer owner for high-impact approvals.
- AuthContext/scope: authenticated account or trusted service identity, active tenant/customer scope, device/service-ticket permission, SLA/dispatch capability grants, and tenant/customer filters.
- inputs: fault/evidence reference, device/customer/site ids, lifecycle state, contract/SLA summary, proposed action, part/remote-fix evidence summary, policy version, reason, correlation id, and idempotency key.
- outputs: service recommendation, ticket draft/status, decision-card link, SLA risk summary, allowed next actions, denial shape, and trace links.
- side effects: workflow start/advance, ticket draft or backend-only ticket creation call, remote-fix request where authorized, decision card, part/order request handoff, timer deadline, notification, and trace.
- idempotency: duplicate fault/action for the same device/evidence/SLA window returns or updates the existing service recommendation/ticket/card.
- policy/approval: apply `SVC-*` policy clauses where accepted; unclear SLA, emergency dispatch, expensive parts, replacement review, contract exceptions, or sensitive customer impact require review.
- exposure surfaces: service coordination UI, HTTP APIs, workflow steps, ticket/fault views, decision cards, scoped agent recommendations, integration calls, timers, and consumers.

## Required future detail

- Concrete SLA, dispatch, remote-fix, parts, and replacement policies.
- Ticketing/field-service integration contracts and safe retry semantics.
- Service outcome taxonomy and customer-health linkage.
- Concrete tests for auto/draft paths, approvals, forbidden access, duplicate faults, integration failure, audit, and UI/API/tool surfaces.

## Linked layers

- behavior: `../20-behavior/flows/02-lifecycle-and-exception-flows.md`, `../20-behavior/state-models/01-lifecycle-foundation.md`
- operating model: `../15-operating-model/policies-and-approval-gates.md`, `../15-operating-model/decisions-exceptions-and-evidence.md`
- auth/security: `../40-auth-security/authorization-rules.md`, `../40-auth-security/agent-permissions.md`
- observability: `../50-observability/audit-trace-and-outcomes.md`
- UI: `../55-ui/ui-surfaces.md`
- tests: future test refresh under `../30-tests/`
