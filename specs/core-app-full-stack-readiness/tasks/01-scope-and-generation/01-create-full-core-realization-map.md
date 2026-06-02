# TASK-CORE-01-001: Create full-core realization map

## Purpose

Create a concrete map from the canonical core PRD to app-description layers, capabilities, functional agents, structured surfaces, Akka substrates, APIs, tests, and module sequence.

## Required reads

- `specs/core-app-full-stack-readiness/README.md`
- `docs/examples/core-ai-first-saas-input/10-canonical-core-app-prd.md`
- `docs/examples/core-ai-first-saas-input/01-core-seed-progression-plan.md`
- `templates/ai-first-saas-starter/app-description/README.md`
- `docs/capability-first-backend-architecture.md`
- `skills/core-saas-foundation/SKILL.md`
- `skills/akka-solution-decomposition/SKILL.md`

## Expected outputs

- `specs/core-app-full-stack-readiness/full-core-realization-map.md`
- updates to related planning references if needed

## Required checks

- Map includes Access/Profile, User Admin, Agent Admin, Audit/Trace, and Governance/Policy.
- Map distinguishes full core from Module 1-only.
- `git diff --check`

## Done criteria

- Future tasks can use the map as the implementation contract.
- Queue status and changes are committed.
