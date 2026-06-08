# Sprint 01: My Account Personal Attention Digest AutonomousAgent

## Objective

Implement or validate a bounded My Account personal attention digest worker using the reusable AutonomousAgent worker runtime pattern.

## Acceptance criteria

- Contract defines task/result/evidence/capability/event/surface behavior.
- Runtime follows real Akka AutonomousAgent pattern or records blocker.
- Attention evidence is authorized and redacted.
- Provider missing config fails closed.
- Digest surfaces are advisory and do not mutate source attention.
- Scaffolded backend/frontend checks pass.
