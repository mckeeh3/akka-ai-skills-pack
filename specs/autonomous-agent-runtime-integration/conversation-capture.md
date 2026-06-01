# Conversation Capture: AutonomousAgent Runtime Integration

## User request

After Workstream Event Backbone v3 completed, the assistant recommended AutonomousAgent Runtime Integration next. The user said:

> go ahead with the suggested mini-project

## Decision

Create a mini-project for real AutonomousAgent runtime integration, starting with User Admin Access Review.

## Rationale

The sequence is now ready:

1. v1 attention backbone exists.
2. v2 attention producers/update delivery exist.
3. v3 workstream event backbone exists.
4. Real durable internal/background agent work can now emit typed events, drive attention, and render surfaces without ad hoc wiring.

## Constraints

- Use a real Akka `AutonomousAgent` runtime path where feasible.
- Missing provider/model configuration must fail closed with actionable status.
- Do not use deterministic/demo/model-less normal runtime as success for model-backed work.
- Preserve governed capabilities, AuthContext, tenant/customer scope, audit/work traces, tool boundaries, and event source refs.

## First vertical

User Admin Access Review AutonomousAgent is selected because existing starter work includes access-review task-state attention and blocked/provider-fail-closed surfaces, making it the smallest high-value vertical.
