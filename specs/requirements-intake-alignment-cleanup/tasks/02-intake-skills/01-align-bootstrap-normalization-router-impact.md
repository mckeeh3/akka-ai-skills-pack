# TASK-RIAC-02-001: Align bootstrap, normalization, router, and change-impact skills

## Objective

Rewrite the core app-description intake skills so broad user input is normalized/routed through five-core starter/full-core scope, functional agents, workstreams, structured surfaces, capabilities, UI, and runtime validation.

## Required reads

- AGENTS.md
- skills/README.md
- specs/requirements-intake-alignment-cleanup/content-inventory.md
- specs/requirements-intake-alignment-cleanup/prune-and-rewrite-criteria.md
- specs/requirements-intake-alignment-cleanup/sprints/02-description-intake-skills-sprint.md
- specs/requirements-intake-alignment-cleanup/backlog/02-description-intake-skills-build-backlog.md
- docs/minimum-ai-first-saas-app.md
- docs/examples/ai-first-saas-seed-app-description/README.md
- skills/app-description-bootstrap/SKILL.md
- skills/app-description-input-normalization/SKILL.md
- skills/app-description-intake-router/SKILL.md
- skills/app-description-change-impact/SKILL.md

## In scope

- Fix User-Admin-only minimum starter wording.
- Prefer AI-first seed/workstream examples over purchase-request examples.
- Add starter/basic/chatbot-like five-core routing.
- Add surface-modeling and `55-ui` handoffs where missing.

## Out of scope

- Do not rewrite PRD/backlog skills in this task.

## Checks

- `git diff --check`
- Focused `rg` checks from Backlog 02 with intentional hits reviewed.

## Done criteria

- The four intake skills no longer teach stale starter/example/routing assumptions.
- Queue updated and committed.
