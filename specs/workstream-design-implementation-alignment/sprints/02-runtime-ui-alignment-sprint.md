# Sprint 02: Runtime and UI Alignment

## Objective

Align the runtime/API/frontend behavior with the clarified workstream contracts from Sprint 01.

## Scope

- Default dashboard loading through backend-authoritative shell/surface paths.
- Prompt-entered surface request aliases resolved through backend shell request semantics.
- Realtime/SSE semantics aligned in docs, backend, frontend, and tests at a bounded v1 scope.
- Legacy page-style artifacts reviewed and either documented as compatibility/reference or queued for removal/refactor.

## Acceptance criteria

- Selecting/opening a workstream has deterministic default dashboard behavior and tests.
- Common workstream-local prompt aliases resolve to typed shell requests, surfaces, or safe system-message denials.
- Realtime behavior is no longer ambiguous: either true live SSE is implemented at v1 scope or finite replay/stale refresh is explicitly documented and tested as v1 behavior.
- Frontend tests/typecheck/build and targeted backend tests pass for changed areas.

## Handoff notes

Preserve provider fail-closed behavior. Do not introduce fixture/demo normal-runtime paths. If a task discovers that true live SSE requires a larger Akka notification-stream refactor, narrow the v1 contract and append a follow-up task rather than over-expanding the current task.
