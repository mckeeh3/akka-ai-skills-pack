# Conversation Capture: Workstream Event Backbone v3

## User request

After attention v1/v2 and dogfood release-readiness completed, the assistant recommended several next options. The user chose:

> go ahead with 2. Start Workstream Event Backbone v3. 3. is also very important, but i think we do that after v3. ao you agree?

Option 2 was Workstream Event Backbone v3. Option 3 was broader AutonomousAgent runtime integration.

## Decision

Proceed with Workstream Event Backbone v3 first.

Rationale: v3 creates the typed event/source-ref/projection substrate that durable AutonomousAgent task runtime integration should emit into and consume from. Doing AutonomousAgent runtime hardening after v3 should reduce ad hoc task notification/attention wiring.

## Accepted constraints

- Preserve backend authoritative attention and governed capability boundaries.
- Do not build a loose global event bus that bypasses authorization, policy, audit, or tool boundaries.
- Keep v3 bounded to starter/reference assets and selected starter state changes.
- Do not fake model-backed AutonomousAgent success; future AutonomousAgent work remains a separate high-priority initiative.

## Relationship to previous work

- v1: shared attention backbone.
- v2: bounded producers/timed/task-state attention and refresh delivery.
- dogfood: release-ready at v1/v2 implemented starter attention scope.
- v3: generalized governed workstream event backbone.
- future: broader real AutonomousAgent runtime/task lifecycle integration.
