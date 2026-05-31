# TASK-WGGT-01-001: Update core architecture docs

## Objective

Update the main architecture and requirements-process docs so workstream graph and governed-tool decomposition are canonical.

## Required reads

- AGENTS.md
- skills/README.md
- specs/workstream-graph-governed-tools-architecture/README.md
- specs/workstream-graph-governed-tools-architecture/conversation-capture.md
- specs/workstream-graph-governed-tools-architecture/sprints/01-doctrine-vocabulary-sprint.md
- specs/workstream-graph-governed-tools-architecture/backlog/01-doctrine-vocabulary-build-backlog.md
- docs/ai-first-saas-application-architecture.md
- docs/requirements-to-workstream-development-process.md
- docs/agent-workstream-application-architecture.md

## In scope

- Define role-specific dashboard surfaces.
- Define surface graph decomposition.
- Define internal workstream agent graph.
- Define governed-tool, browser-tool, agent-tool, and internal-tool terminology.
- Add incremental-input handling.

## Checks

- `git diff --check`
- Search touched files for new canonical terms.

## Done criteria

- Core docs present the model as the pack's default architecture.
- Queue updated and committed.
