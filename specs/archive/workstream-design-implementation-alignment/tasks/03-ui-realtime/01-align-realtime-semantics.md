# TASK-WDA-03-001: Align realtime/SSE semantics

## Objective

Resolve ambiguity in workstream realtime behavior by either implementing true continuous SSE at a bounded v1 scope or explicitly narrowing v1 to finite replay/stale refresh semantics with matching docs and tests.

## Required reads

- mini-project README, conversation capture, sprint 02, backlog, queue entry, and this task brief
- `app-description/55-ui/states-and-realtime.md`
- `app-description/12-workstreams/surfaces-index.md`
- `app-description/70-traceability/surface-to-capability-map.md`
- `src/main/java/ai/first/api/coreapp/workstream/WorkstreamEndpoint.java`
- `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`
- `src/main/java/ai/first/application/foundation/workstream/**`
- `frontend/src/api/HttpWorkstreamRealtimeClient.ts`
- `frontend/src/workstream/realtime/**`
- relevant backend/frontend tests

## Skills

- `akka-http-endpoint-sse`
- `akka-web-ui-realtime`
- `akka-kve-notifications` if entity notification streams are introduced

## In scope

- Make a bounded v1 decision and record it in app-description/UI docs.
- If true live SSE is feasible in one task, implement it and tests.
- If not feasible in one task, explicitly document finite replay/stale refresh semantics, align endpoint/frontend/test names/copy, and append a follow-up task if needed.
- Preserve tenant/customer scoping and safe stale/reconnect behavior.

## Out of scope

- Whole-repository notification-stream refactor unless it is proven small and necessary.
- Domain-specific realtime events.

## Expected outputs

- Updated docs and/or backend/frontend realtime code.
- Tests proving selected v1 semantics.
- Optional appended follow-up task if true live SSE remains a material gap.

## Required checks

- `git diff --check`
- targeted backend realtime/workstream tests
- targeted frontend realtime tests
- `npm --prefix frontend run typecheck` if TypeScript changed

## Done criteria

- Realtime behavior is explicit, non-misleading, scoped, and tested.
- Any remaining true-live gap is queued as bounded follow-up before verification.
- Changes and queue update are committed.

## Commit message

`workstream-align: align realtime semantics`
