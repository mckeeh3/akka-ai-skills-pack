# Capability: Onboarding Installation

This is a lightweight capability contract for future refinement. It records the governed boundary for new customer onboarding, installation coordination, collector validation, and operational activation without inventing CRM, ticketing, DCA, or installer contracts.

## Capability definition

- capability-id: `onboarding-installation`
- capability number: `CAP-06`
- class: workflow, command, read/evidence, approval
- purpose: move a new customer from acquisition through installation and DCA validation into operational service using gate evidence, policy checks, and retained human authority.
- business outcome: tenant operators can coordinate repeatable onboarding while agents prepare plans, evidence, and blockers; activation occurs only when required gates pass or are explicitly deferred/approved.

## In-scope outcomes

- Create onboarding plans from accepted customer, contract, site, contact, device, and collector requirements.
- Validate site access, installation tickets, DCA collector deployment, discovered devices, meter baselines, contract mappings, supply monitoring, service coverage, and contact confirmation.
- Coordinate installation-ticket drafts/updates and gate rechecks through accepted integration boundaries.
- Create decision cards for unusual contract terms, missing site access, device substitution, collector not reporting, manual meter baseline, or customer communication exceptions.
- Transition customer/device/collector lifecycle states only with evidence and trace.

## Out-of-scope outcomes

- CRM deal management, contract authoring, installer scheduling APIs, DCA vendor setup details, or customer portal self-service onboarding beyond future accepted surfaces.
- Automatic operational activation when required evidence is missing, stale, contradictory, or explicitly deferred without authorization.
- Billing activation details beyond readiness evidence and handoff to `meter-billing-review`.

## Authority and contract

- actors/callers: tenant onboarding coordinator, operations supervisor, customer admin where delegated, onboarding agent, installation coordinator agent, DCA monitoring agent, installer/service integration caller, onboarding workflow.
- AuthContext/scope: authenticated account or trusted service identity, selected tenant/customer scope, onboarding/installation/device/collector permission, and tenant/customer filters.
- inputs: onboarding plan request or gate action, customer/site/contact/device/collector references, contract mapping summary, evidence snapshot ids, proposed lifecycle transition, policy version, reason, correlation id, and idempotency key.
- outputs: onboarding plan/status, gate readiness, blocker list, decision-card link, customer-safe status summary where enabled, allowed next actions, denial shape, and trace links.
- side effects: onboarding workflow start/advance, installation ticket draft/call, lifecycle gate event, decision card, evidence request, timer/reminder, notification, and trace.
- idempotency: duplicate plan/gate/ticket requests for the same customer/site/device/evidence version return or update existing onboarding work instead of duplicating tickets or transitions.
- policy/approval: apply accepted `INST-*` policy clauses; manual baselines, missing/waived collector reporting, device substitution, incomplete contract mapping, and sensitive customer communication require review.
- exposure surfaces: onboarding UI, HTTP APIs, installation workflow, lifecycle views, decision cards, customer-safe status views, scoped agent recommendations, timers, and integration calls.

## Required future detail

- Accepted onboarding gate matrix and deferral policy.
- CRM/installation/DCA/ticket integration contracts.
- Customer-safe status shape and delegation rules.
- Concrete tests for plan creation, gate blocking, approval/deferral, tenant isolation, idempotency, audit, and UI/API/tool behavior.

## Linked layers

- behavior: `../20-behavior/flows/02-lifecycle-and-exception-flows.md`, `../20-behavior/state-models/01-lifecycle-foundation.md`
- operating model: `../15-operating-model/policies-and-approval-gates.md`, `../15-operating-model/decisions-exceptions-and-evidence.md`
- auth/security: `../40-auth-security/authorization-rules.md`, `../40-auth-security/boundary-and-surface-rules.md`
- observability: `../50-observability/audit-trace-and-outcomes.md`
- UI: `../55-ui/ui-surfaces.md`
- tests: future test refresh under `../30-tests/`
