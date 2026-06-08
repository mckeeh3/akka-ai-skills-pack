# Sprint 01: Workstream Event Backbone v3

## Objective

Generalize bounded attention producers into a governed workstream event backbone for the starter template: event envelopes, source refs, publication, consumers/projections, workflow/provider lifecycle events, and update delivery hooks.

## Scope

- Contract and gap map.
- Bounded starter event publication.
- Consumer/projection path into attention/traces/dashboard state.
- Workflow/process/provider lifecycle event handling.
- Docs/handoff for v3 and future AutonomousAgent integration.

## Acceptance criteria

- Event contract preserves tenant/customer/AuthContext, source refs, idempotency, trace correlation, and capability provenance.
- At least one real event reaction path updates attention/projection state from backend events.
- Duplicate/retry behavior is safe and tested.
- Events do not bypass governed capabilities or auth.
- Future AutonomousAgent runtime integration has a clear event substrate to target.
