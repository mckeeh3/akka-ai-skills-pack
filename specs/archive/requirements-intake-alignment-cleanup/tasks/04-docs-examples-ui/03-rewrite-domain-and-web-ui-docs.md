# TASK-RIAC-04-003: Rewrite domain workstream and web UI docs

## Objective

Rebalance domain PRD and web UI/API/UX docs around functional-agent workstreams, structured surfaces, surface actions, system messages, and capability-backed APIs.

## Required reads

- AGENTS.md
- skills/README.md
- specs/requirements-intake-alignment-cleanup/content-inventory.md
- specs/requirements-intake-alignment-cleanup/prune-and-rewrite-criteria.md
- docs/domain-workstream-prd-structure.md
- docs/web-ui-api-contract-patterns.md
- docs/web-ui-style-guide.md
- docs/web-ui-ux-patterns.md
- docs/web-ui-frontend-decomposition.md
- docs/structured-surface-contracts.md
- docs/workstream-ui-reference-architecture.md

## In scope

- Replace resource API primacy with workstream/surface API envelopes.
- Rename or qualify traditional navigation/page wording.
- Rebalance examples away from list/edit/search/form dominance.
- Add decision-card, trace, governance, system-message, and AutonomousAgent progress/result surface examples.

## Checks

- `git diff --check`
- Focused stale-term search over edited docs.

## Done criteria

- UI docs describe conventional pages/routes only as implementation/deep-link support.
- Queue updated and committed.
