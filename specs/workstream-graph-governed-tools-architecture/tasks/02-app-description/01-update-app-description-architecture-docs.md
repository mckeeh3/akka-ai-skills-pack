# TASK-WGGT-02-001: Update app-description architecture docs

## Objective

Update app-description architecture and maintenance docs so app descriptions own role dashboards, surface graphs, internal agent graphs, workstream expertise, and governed-tools.

## Required reads

- AGENTS.md
- skills/README.md
- specs/workstream-graph-governed-tools-architecture/README.md
- specs/workstream-graph-governed-tools-architecture/sprints/02-app-description-model-sprint.md
- docs/internal-app-description-architecture.md
- docs/app-description-maintenance-flow.md
- docs/description-first-application-doctrine.md

## In scope

- Specify where graph/governed-tool concepts live in existing app-description directories.
- Keep governed-tools inside `10-capabilities/` and `12-workstreams/` surface/action maps.
- Add incremental change reconciliation guidance.

## Checks

- `git diff --check`
- Focused term search over touched docs.

## Done criteria

- App-description docs can guide future skills without inventing a new top-level governed-tool directory.
- Queue updated and committed.
