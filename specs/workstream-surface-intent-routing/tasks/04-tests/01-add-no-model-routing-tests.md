# Task: Add no-model routing tests

## Objective

Add focused backend and frontend tests proving deterministic surface routing avoids model invocation, avoids mutation, and preserves fallback behavior.

## Required reads

- `AGENTS.md`
- `specs/workstream-surface-intent-routing/README.md`
- `specs/workstream-surface-intent-routing/sprints/02-frontend-prefill-tests.md`
- router and User Admin routing implementation files
- relevant backend workstream tests
- `frontend/src/workstream-composer-message-api.contract.test.mjs`
- `frontend/src/workstream-organization-admin-vertical.contract.test.mjs`

## Skills

- akka-agent-testing
- akka-web-ui-testing
- akka-runtime-feature-verification

## Expected outputs

- Backend tests for matched route, fallback route, unauthorized context, and no mutation.
- Frontend/contract tests for routed response and prefill rendering.
- Test evidence in queue notes.
- Queue update.

## Required checks

- `git diff --check`
- targeted backend tests
- `npm --prefix frontend test -- --run`
- `npm --prefix frontend run typecheck`

## Done criteria

- Tests fail if matched User Admin routing calls the model runtime.
- Tests fail if Organization creation happens before surface submit.
- Tests prove ambiguous/unmatched prompts still use the governed fallback path.
- Changes and queue update are committed.
