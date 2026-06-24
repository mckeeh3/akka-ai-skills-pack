# TASK-WCTC-01-001: Inventory and classify foundation workstream actions

## Purpose

Inventory existing foundation workstream actions and classify their suitability for expanded confirmed chat tool coverage.

## Required reads

- `AGENTS.md`
- `specs/AGENTS.md`
- `specs/workstream-chat-tool-catalog-expansion/README.md`
- `specs/workstream-chat-tool-catalog-expansion/conversation-capture.md`
- `specs/workstream-chat-tool-catalog-expansion/sprints/01-inventory-and-current-intent.md`
- `specs/workstream-chat-tool-catalog-expansion/backlog/01-chat-tool-catalog-expansion-build-backlog.md`
- `specs/workstream-chat-tool-execution/source-and-design-map.md`
- `specs/workstream-chat-tool-execution/verification-notes.md`
- `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`
- `app-description/domains/core-starter/workstreams/**`
- `frontend/src/workstream/surfaces/**`

## Skills

- `agent-workstream-apps`
- `capability-first-backend`
- `akka-agent-tool-boundaries`
- `akka-agent-work-trace`

## Expected outputs

- `specs/workstream-chat-tool-catalog-expansion/catalog-inventory.md`
- queue update

## Required checks

- `git diff --check -- specs/workstream-chat-tool-catalog-expansion`

## Done criteria

- Inventory covers all five foundation workstreams.
- Each relevant action is classified with rationale, risk, prerequisites, expected tests, and first-pass recommendation.
- The first expansion set is explicit and safe.
- Changes and queue update are committed.
