# TASK-WCTE-03-001: Add chat tool plan records and surfaces

## Purpose

Add typed backend records/surface envelopes for proposed, confirmable, executing, completed, denied, and partial-failure chat tool plans without executing tools yet.

## Required reads

- `AGENTS.md`
- `specs/workstream-chat-tool-execution/README.md`
- `specs/workstream-chat-tool-execution/source-and-design-map.md`
- `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`
- `src/main/java/ai/first/application/foundation/workstream/**`
- related backend workstream tests named in the design map

## Skills

- `capability-first-backend`
- `akka-agent-work-trace`
- `akka-http-endpoint-component-client`

## Expected outputs

- Backend records/DTOs for chat tool plan proposal, step, confirmation snapshot, execution result, and partial failure.
- Typed `SurfaceEnvelope` support for proposal/confirmation/result/system-message surfaces.
- Workstream log persistence for plan proposal items.
- Tests proving proposal creation is side-effect free and idempotent where applicable.
- Queue update.

## Required checks

- `git diff --check`
- targeted backend tests for new plan record/surface behavior

## Done criteria

- Initial chat tool plan proposal records cannot execute tools.
- Plan snapshots include selected workstream, selected AuthContext, requestedBy, governed tool ids, capabilities, inputs, idempotency, and trace refs.
- Changes and queue update are committed.
