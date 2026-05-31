# TASK-WGGT-01-003: Update routing map and terminology

## Objective

Update top-level routing guidance and run a terminology pass so active routing points to workstream graphs and qualified governed-tool terms.

## Required reads

- AGENTS.md
- skills/README.md
- specs/workstream-graph-governed-tools-architecture/README.md
- docs/ai-first-saas-application-architecture.md
- docs/requirements-to-workstream-development-process.md

## In scope

- Update `skills/README.md` generated-app handoff order.
- Add routing language for role dashboards, surface graphs, internal agent graphs, and governed-tools.
- Search for ambiguous architecture-level bare `tool` wording in touched routing docs and qualify where appropriate.

## Checks

- `git diff --check`
- `rg -n "surface graph|governed-tool|browser-tool|agent-tool|internal workstream agent graph|role-specific dashboard" skills/README.md docs/ai-first-saas-application-architecture.md docs/requirements-to-workstream-development-process.md docs/agent-workstream-application-architecture.md`

## Done criteria

- Routing map reflects the new canonical decomposition.
- Queue updated and committed.
