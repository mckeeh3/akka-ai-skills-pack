# TASK-RIAC-04-002: Remove or rewrite stale app-description skill-plan doc

## Objective

Decide and execute the disposition for `docs/app-description-skills-plan-backlog.md`, which is structurally stale relative to current app-description architecture.

## Required reads

- AGENTS.md
- skills/README.md
- specs/requirements-intake-alignment-cleanup/content-inventory.md
- specs/requirements-intake-alignment-cleanup/prune-and-rewrite-criteria.md
- docs/app-description-skills-plan-backlog.md
- docs/internal-app-description-architecture.md
- docs/app-description-maintenance-flow.md
- skills/app-description-*/SKILL.md as needed for references

## In scope

- Prefer removal if the doc is obsolete and active guidance exists elsewhere.
- If kept, rewrite it around functional agents, surfaces, `12-workstreams/`, `55-ui`, managed agents, readiness, and runtime validation.
- Update all references to match the decision.

## Checks

- `git diff --check`
- `rg -n "app-description-skills-plan-backlog" . --glob '!specs/requirements-intake-alignment-cleanup/**'`

## Done criteria

- The stale plan doc no longer misleads active guidance.
- References are updated or intentionally documented.
- Queue updated and committed.
