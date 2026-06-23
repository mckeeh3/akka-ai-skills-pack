# TASK-WCTE-01-001: Audit and design confirmed chat tool execution

## Purpose

Inventory current root app workstream/action/runtime code and produce an implementation-ready design map for confirmed chat tool execution.

## Required reads

- `AGENTS.md`
- `specs/AGENTS.md`
- `specs/workstream-chat-tool-execution/README.md`
- `specs/workstream-chat-tool-execution/conversation-capture.md`
- `specs/workstream-chat-tool-execution/sprints/01-contract-and-design.md`
- `specs/workstream-chat-tool-execution/backlog/01-workstream-chat-tool-execution-build-backlog.md`
- `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`
- `src/main/java/ai/first/application/coreapp/workstream/SurfaceIntentRouter.java`
- `src/main/java/ai/first/application/coreapp/workstream/DefaultSurfaceIntentRouter.java`
- `src/main/java/ai/first/application/foundation/agent/WorkstreamRuntimeAgent.java`
- `src/main/java/ai/first/application/foundation/agent/DefaultWorkstreamAgentRuntimeInvoker.java`
- `frontend/src/workstream/**`
- `app-description/domains/core-starter/workstreams/**`

## Expected outputs

- `specs/workstream-chat-tool-execution/source-and-design-map.md`
- queue update

## Required checks

- `git diff --check`

## Done criteria

- Design map identifies exact backend/frontend/app-description files and first-pass representative tool-plan paths.
- Design map names initial governed tool ids, capability ids, action ids, plan DTOs/surfaces, trace fields, and tests to add.
- It explicitly preserves deterministic surface routing before chat tool planning.
- Changes and queue update are committed.
