# Sprint 01: Agent Admin Prompt-Risk AutonomousAgent

## Objective

Implement a bounded Agent Admin AutonomousAgent worker that reviews prompt/skill/reference/model/tool-boundary change proposals and produces risk findings for human review.

## Acceptance criteria

- Contract defines task/result schemas, capabilities, events, attention, surfaces, and tests.
- Runtime follows real Akka AutonomousAgent pattern or records precise blocker.
- Provider missing config fails closed.
- Result surfaces are advisory and require human action for activation.
- Scaffolded backend/frontend checks pass.
