# Sprint 01: AutonomousAgent Runtime Integration

## Objective

Implement or prove the path for a real User Admin Access Review AutonomousAgent vertical in the starter template, integrated with governed capabilities, v3 events, attention, and workstream surfaces.

## Scope

- Contract and SDK gap check.
- Backend AutonomousAgent task runtime integration.
- Event/attention/surface wiring.
- Provider/model fail-closed validation.
- Docs/handoff and verification.

## Acceptance criteria

- The task contract is precise enough to implement safely.
- Runtime uses Akka `AutonomousAgent` where feasible or records a concrete blocker.
- Provider/model missing config fails closed; no fake success.
- Task states emit v3 events and attention.
- Surfaces render progress/result/failure/decision states.
- Tests cover auth, tenant isolation, lifecycle, event/attention linkage, and no model-less normal success.
