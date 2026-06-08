# TASK-WGGT-01-002: Update surface, capability, domain, and expertise docs

## Objective

Update supporting doctrine docs so surfaces, capabilities, domain PRDs, and workstream expertise reflect the graph/governed-tool model.

## Required reads

- AGENTS.md
- skills/README.md
- specs/workstream-graph-governed-tools-architecture/README.md
- docs/structured-surface-contracts.md
- docs/capability-first-backend-architecture.md
- docs/domain-workstream-prd-structure.md
- docs/workstream-expertise-model.md

## In scope

- Surface graphs: nodes, edges, transitions, result/system-message surfaces.
- Governed-tools inside capability definitions and surface/action maps.
- Capability as product grouping/ability.
- Workstream expertise skills describing role dashboards, surfaces, available governed-tools, denials, and user help.

## Checks

- `git diff --check`
- Focused term search over touched docs.

## Done criteria

- Supporting docs are coherent with the core architecture updates.
- Queue updated and committed.
