# Task: Quarantine frontend fixtures to tests

## Objective

Ensure the starter frontend normal runtime cannot select or import fixture-backed workstream/API/realtime behavior. Fixture data and clients may remain only as test assets or test-only modules.

## Required reads

- AGENTS.md
- skills/README.md
- skills/app-description-ui/SKILL.md
- specs/real-akka-runtime-replacement/README.md
- specs/real-akka-runtime-replacement/non-akka-runtime-seam-map.md
- templates/ai-first-saas-starter/frontend/src/main.tsx
- templates/ai-first-saas-starter/frontend/src/api/index.ts
- templates/ai-first-saas-starter/frontend/src/api/FixtureWorkstreamApiClient.ts
- templates/ai-first-saas-starter/frontend/src/api/FixtureWorkstreamRealtimeClient.ts
- templates/ai-first-saas-starter/frontend/src/workstream/fixtures/**
- root `frontend/src/**` equivalents if mirror sync is needed

## Skills

- akka-web-ui-apps
- akka-web-ui-frontend-project

## In scope

- Remove runtime query/env switches such as fixture workstream mode from production entrypoints.
- Ensure production bundle imports only HTTP/realtime clients backed by real backend endpoints.
- Move fixture clients/data to test-only folders or keep them in source only if they are not exported/importable by runtime code and are clearly test-only.
- Update contract tests to read test fixtures from test-only locations.
- Sync root frontend mirror if repository convention requires.

## Out of scope

- Do not remove frontend tests that validate renderer contracts with static test data.
- Do not weaken browser auth/secret-boundary behavior.

## Expected outputs

- updated frontend entrypoint/client exports/tests
- moved or renamed fixture assets if needed
- updated static resources via build or cleaned stale output
- queue update

## Required checks

- `git diff --check`
- `cd templates/ai-first-saas-starter/frontend && npm test -- --run && npm run typecheck && npm run build`
- root frontend checks if root mirror changes: `cd frontend && npm test -- --run && npm run typecheck && npm run build`
- `rg -n "fixtureWorkstream|FixtureWorkstream|FixtureApiClient|FixtureRealtimeClient|from './workstream/fixtures'|export \* from './fixtures'" templates/ai-first-saas-starter/frontend/src --glob '!**/*.test.mjs' --glob '!**/__tests__/**'`

## Done criteria

- Production frontend runtime cannot use fixture clients/data.
- Fixtures are test-only and not exported from runtime indexes.
- Frontend checks pass.
- Changes and queue update are committed.

## Commit message

`runtime: quarantine frontend fixtures to tests`
