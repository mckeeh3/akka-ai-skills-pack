# TASK-WCTE-09-001: Add User Admin chat tool execution tests

## Purpose

Add focused backend/frontend/API tests for the User Admin proof path.

## Required reads

- `AGENTS.md`
- `specs/workstream-chat-tool-execution/README.md`
- `specs/workstream-chat-tool-execution/source-and-design-map.md`
- completed User Admin backend/frontend implementation files
- relevant existing WorkstreamService and frontend contract tests

## Skills

- `akka-agent-testing`
- `akka-http-endpoint-testing`
- `akka-web-ui-testing`
- `akka-runtime-feature-verification`

## Expected outputs

- Backend tests proving proposal without mutation, confirmation execution, auth denial, idempotency, partial failure, and traces.
- Frontend/contract tests proving proposal/confirmation/result rendering and no auto-submit.
- Endpoint/API tests where feasible.
- Queue update.

## Required checks

- `git diff --check`
- targeted backend tests
- `npm --prefix frontend test -- --run`
- `npm --prefix frontend run typecheck`

## Done criteria

- Tests fail if initial chat request mutates Organization or invitation state.
- Tests fail if confirmation executes a modified/unconfirmed/out-of-catalog plan.
- Tests cover provider fail-closed or test-provider distinction.
- Changes and queue update are committed.
