# Sprint 3 Build Backlog: Capability-First Contracts

## Purpose

Bring DCA capabilities into the current capability-first backend model before any future realization work.

## Suggested harness task breakdown

### 1. Refactor capability index to current capability-first shape

- task ID: `TASK-03-001`
- outputs: update `capabilities-index.md` with capability ids, classes, actors/callers, protected scope, and exposure surfaces.

### 2. Add detailed Supplies Autopilot capability contract

- task ID: `TASK-03-002`
- outputs: add `10-capabilities/03-supplies-autopilot.md` with full governed contract details.

### 3. Add lightweight contracts for remaining DCA capabilities

- task ID: `TASK-03-003`
- outputs: add lightweight capability files or expanded index sections for planned lifecycle, telemetry, service, billing, onboarding, offboarding, policy, command-center, and audit/outcome capabilities.

### 4. Update traceability maps for capability-first and foundation links

- task ID: `TASK-03-004`
- outputs: update traceability so foundation and capability contracts map to behavior, tests, auth/security, UI, observability, and generation slices.

## Done criteria

- Capability contracts, not Akka component names or agent tools, are the DCA backend design root.
- Supplies Autopilot is detailed enough to drive later implementation planning.
- Planned capabilities identify unknowns instead of inventing external contracts or thresholds.
