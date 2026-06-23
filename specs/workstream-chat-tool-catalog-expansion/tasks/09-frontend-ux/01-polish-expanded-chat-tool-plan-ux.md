# TASK-WCTC-09-001: Polish expanded chat tool plan UX

## Purpose

Ensure frontend plan surfaces handle the expanded catalog clearly and safely.

## Required reads

- `AGENTS.md`
- `specs/workstream-chat-tool-catalog-expansion/README.md`
- `specs/workstream-chat-tool-catalog-expansion/catalog-coverage-map.md`
- completed per-workstream implementation notes
- `frontend/src/workstream/surfaces/**`
- `frontend/src/workstream/types/**`
- `frontend/src/api/**`

## Skills

- `akka-web-ui-api-client`
- `akka-web-ui-state-rendering`
- `akka-web-ui-forms-validation`
- `akka-web-ui-accessibility-responsive`

## Expected outputs

- frontend copy/rendering improvements for executable, proposal-only, approval-gated, surface-only, blocked, and partial-failure plan states
- contract tests for expanded metadata and no auto-submit behavior
- queue update

## Required checks

- `git diff --check`
- `npm --prefix frontend test -- --run`
- `npm --prefix frontend run typecheck`

## Done criteria

- Users can distinguish executable, approval-gated, proposal-only, and blocked steps before confirming.
- UI remains accessible and browser-safe.
- Changes and queue update are committed without unrelated generated static assets unless explicitly required.
