# TASK-WTUA-02-001: Update canonical workstream tool-use doctrine

## Purpose

Update the canonical skills-pack docs so all later skill-family edits can refer to one coherent workstream tool-use model.

## Required reads

- `AGENTS.md`
- `skills-pack/AGENTS.md`
- `specs/workstream-tool-use-alignment/README.md`
- `specs/workstream-tool-use-alignment/conversation-capture.md`
- `specs/workstream-tool-use-alignment/tool-use-source-map.md`
- `specs/workstream-tool-use-alignment/sprints/01-audit-and-canonical-doctrine.md`
- `skills-pack/docs/ai-first-saas-application-architecture.md`
- `skills-pack/docs/agent-workstream-application-architecture.md`
- `skills-pack/docs/workstream-contract.md`
- `skills-pack/docs/structured-surface-contracts.md`
- `skills-pack/docs/capability-first-backend-architecture.md`
- `skills-pack/docs/workstream-surface-intent-routing.md`
- `skills-pack/docs/intent-compiler.md`
- `skills-pack/docs/current-intent-model.md`
- `skills-pack/docs/intent-to-realization-flow.md`

## Expected outputs

- Canonical docs updated to define governed tools as the shared operation boundary for human-backed and AI-backed actor adapters.
- Human chat tool execution guidance added or refined: proposed plan, sufficient detail, explicit confirmation, deterministic authorization, transaction boundary, traces, result/partial-failure reporting.
- Existing surface-routing guidance reconciled so it remains a safe no-mutation path without globally forbidding confirmed chat tool execution.
- Queue update.

## Required checks

- `git diff --check`
- targeted search proving canonical docs mention the confirmed human-chat tool-plan path and do not contain unreconciled global no-direct-command prohibitions

## Done criteria

- Canonical docs preserve capability-first, workstream, surface, managed-agent, and runtime completion doctrine.
- The model clearly distinguishes governed tool, surface/browser adapter, human-chat plan adapter, AI agent-tool adapter, and internal/API/MCP exposure channels.
- The AI model is explicitly not the security boundary.
- Changes and queue update are committed.
