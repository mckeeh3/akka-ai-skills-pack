# TASK-WTUA-01-001: Audit current tool-use guidance

## Purpose

Create a source map of existing `skills-pack/` tool-use guidance before making broad doctrine edits.

## Required reads

- `AGENTS.md`
- `skills-pack/AGENTS.md`
- `specs/workstream-tool-use-alignment/README.md`
- `specs/workstream-tool-use-alignment/conversation-capture.md`
- `specs/workstream-tool-use-alignment/sprints/01-audit-and-canonical-doctrine.md`
- `specs/workstream-tool-use-alignment/backlog/01-workstream-tool-use-alignment-build-backlog.md`
- `skills-pack/docs/ai-first-saas-application-architecture.md`
- `skills-pack/docs/agent-workstream-application-architecture.md`
- `skills-pack/docs/workstream-contract.md`
- `skills-pack/docs/structured-surface-contracts.md`
- `skills-pack/docs/capability-first-backend-architecture.md`
- `skills-pack/docs/workstream-surface-intent-routing.md`
- `skills-pack/skills/README.md`

## Suggested search areas

Use targeted searches for terms such as:

- `governed-tool`, `governed tool`, `tool boundary`, `ToolPermissionBoundary`
- `surface action`, `browser-tool`, `agent-tool`, `FunctionTool`, `MCP-tool`
- `direct mutation`, `no mutation`, `direct command`, `surface routing`
- `confirmation`, `approval`, `human-backed`, `AI-backed`, `requestedBy`

## Expected outputs

- `specs/workstream-tool-use-alignment/tool-use-source-map.md`
- queue update

## Required checks

- `git diff --check`

## Done criteria

- Source map identifies canonical docs, focused skills, templates/examples/tools likely needing edits.
- Findings are classified as aligned, needs refinement, potentially conflicting, or out of scope.
- The source map recommends which files each later task should prioritize.
- No implementation/doc alignment edits are made beyond the source map and queue update.
- Changes and queue update are committed.
