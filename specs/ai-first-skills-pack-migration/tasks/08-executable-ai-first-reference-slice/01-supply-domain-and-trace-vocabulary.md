# TASK-08-001: Implement supply domain and trace vocabulary

## Purpose

Create the pure domain vocabulary for the executable supplies autopilot reference slice before any Akka component code depends on it.

## Required reads

- `AGENTS.md`
- `docs/ai-first-saas-application-architecture.md`
- `docs/examples/ai-first-dca-app-description/app-description/60-generation/implementation-slices.md`
- `specs/ai-first-skills-pack-migration/sprints/08-executable-ai-first-reference-slice-sprint.md`
- `specs/ai-first-skills-pack-migration/backlog/08-executable-ai-first-reference-slice-build-backlog.md`

## Scope

- Add pure Java domain records/enums/helpers for supplies objective, device telemetry, supply item, evidence, policy clause refs, decision-card fields, trace events, and outcome refs.
- Include validation helpers for required evidence, stable policy clause IDs, risk/confidence bounds, trace correlation IDs, and idempotency keys.
- Add focused unit tests for valid objects, validation failures, idempotency keys, and decision-card completeness helpers.

## Non-goals

- No Akka component implementation.
- No workflow, agent, endpoint, or UI code.
- No real external integration contracts.

## Skills

- `ai-first-saas`
- `ai-first-saas-object-model`

## Expected outputs

- Supplies domain package under `src/main/java/com/example/domain/...`.
- Unit tests under `src/test/java/com/example/domain/...` or existing test convention.

## Required checks

- Run the focused unit tests for the new domain package.
- Run project compile/test command if practical for the repository.

## Done criteria

- Domain vocabulary can support decision cards, policy refs, trace facts, and outcome refs without guessing later.
- Tests cover success and validation behavior.