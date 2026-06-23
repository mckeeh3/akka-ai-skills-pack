# TASK-WCTE-05-001: Add chat tool catalog and dispatcher

## Purpose

Add the backend-owned catalog and dispatcher that make confirmed chat tool execution deterministic, scoped, and transaction-safe.

## Required reads

- `AGENTS.md`
- `specs/workstream-chat-tool-execution/README.md`
- `specs/workstream-chat-tool-execution/source-and-design-map.md`
- `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`
- `app-description/domains/core-starter/workstreams/surface-catalog.md`
- related backend tests named in the design map

## Skills

- `capability-first-backend`
- `akka-agent-tool-boundaries`
- `akka-agent-work-trace`

## Expected outputs

- Chat tool catalog entries with workstream id, governed tool id, capability id, exposure channel, input schema, idempotency, policy/approval, and trace requirements.
- Dispatcher that rejects tools outside the selected workstream catalog.
- Dispatcher step execution through existing authorized action/service paths where possible.
- Per-step result model with completed/failed/skipped/recovery status.
- Queue update.

## Required checks

- `git diff --check`
- targeted backend dispatcher tests

## Done criteria

- Execution uses the intersection of human authority, selected workstream catalog, tool boundary, and tool policy.
- Each step is a transaction boundary with its own authorization and idempotency.
- Changes and queue update are committed.
