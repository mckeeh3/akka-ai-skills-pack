# TASK-AAFR-02-002: Fix scaffold frontend validation scripts

## Objective

Make the fresh scaffold frontend expose the validation commands required by the regression readiness queue: `npm test`, `npm run typecheck`, and `npm run build`.

## Context

`TASK-AAFR-02-001` proved backend `mvn test` passes in a fresh scaffold, but frontend validation is blocked because the scaffolded frontend package only exposes pack-oriented scripts:

- `build:web-ui`
- `check:web-ui`
- `verify:opinionated-ai-first-saas`

The required generic scripts are missing:

- `npm test`
- `npm run typecheck`
- `npm run build`

## Required checks

- `git diff --check`
- fresh scaffold frontend `npm ci`
- fresh scaffold frontend `npm test`
- fresh scaffold frontend `npm run typecheck`
- fresh scaffold frontend `npm run build`
- focused scan that the scaffolded frontend `package.json` exposes `test`, `typecheck`, and `build`

## Expected outputs

- template/package changes needed to expose the frontend validation scripts
- validation notes in the pending queue
- updated pending queue

## Commit message

`autonomous-agent-regression: fix frontend validation scripts`
