# TASK-WCTC-11-001: Add expanded catalog regression tests

## Purpose

Add cross-cutting regression tests for expanded chat tool catalog behavior.

## Required reads

- `AGENTS.md`
- `specs/workstream-chat-tool-catalog-expansion/README.md`
- `specs/workstream-chat-tool-catalog-expansion/catalog-coverage-map.md`
- completed per-workstream implementation files and task notes
- related backend/frontend tests

## Skills

- `akka-agent-testing`
- `akka-http-endpoint-testing`
- `akka-web-ui-testing`
- `akka-runtime-feature-verification`

## Expected outputs

- backend/API tests for expanded catalog no-mutation, confirmation, denials, idempotency, approval-gated behavior, partial failure, provider fail-closed, and traces
- frontend contract tests if not already covered
- queue update

## Required checks

- `git diff --check`
- targeted backend/API expanded catalog tests
- `npm --prefix frontend test -- --run`
- `npm --prefix frontend run typecheck`
- runtime-evidence/workstream-contract validators if queue notes are updated materially

## Done criteria

- Tests fail if blocked/surface-only actions execute through chat.
- Tests cover each workstream's expanded path and at least one denial path.
- Changes and queue update are committed.
