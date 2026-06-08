# TASK-RIAC-03-001: Align solution decomposition and PRD planning

## Objective

Rewrite solution decomposition and PRD-to-specs/backlog guidance so PRDs and broad feature inputs always preserve workstreams, attention, surfaces/actions, capabilities, agents, UI, and validation before Akka components.

## Required reads

- AGENTS.md
- skills/README.md
- specs/requirements-intake-alignment-cleanup/content-inventory.md
- specs/requirements-intake-alignment-cleanup/prune-and-rewrite-criteria.md
- specs/requirements-intake-alignment-cleanup/sprints/03-prd-spec-backlog-sprint.md
- docs/requirements-to-workstream-development-process.md
- docs/examples/requirements-to-workstream-mini-example.md
- skills/akka-solution-decomposition/SKILL.md
- skills/akka-prd-to-specs-backlog/SKILL.md

## In scope

- Fix minimum-starter drift.
- Demote purchase-request examples to mechanics-only.
- Ensure output contracts begin with workstreams/surfaces/capabilities before component mapping.
- Ensure generated tasks preserve managed-agent reference governance.

## Checks

- `git diff --check`
- Focused stale-term search over the two edited skills.

## Done criteria

- Direct PRD planning cannot plausibly be component-first for generated SaaS.
- Queue updated and committed.
