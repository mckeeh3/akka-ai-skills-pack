# Task: Gate frontend fixtures and refresh static assets

## Objective

Ensure frontend fixture/demo paths are explicit local/dev inspection aids, never normal production-like runtime. Remove normal loading fallback to fixture data, synchronize root/template frontend mirrors, and regenerate or clean static resources.

## Required reads

- AGENTS.md
- specs/full-core-smb-runtime-durability-remediation/README.md
- specs/full-core-smb-runtime-durability-remediation/runtime-durability-remediation-map.md
- templates/ai-first-saas-starter/README.md
- templates/ai-first-saas-starter/frontend/src/main.tsx
- frontend/src/main.tsx
- templates/ai-first-saas-starter/frontend/src/frontend.contract.test.mjs
- frontend/src/frontend.contract.test.mjs

## In scope

- `fixtureWorkstream` gating in template frontend and root frontend mirror.
- Loading/error behavior that currently falls back to fixture `meTenantAdmin`, `initialWorkstreamItems`, or `canonicalSurfaceEnvelopes` outside explicit fixture mode.
- Contract tests that currently require fixture clients in the default main entry.
- Static resource regeneration/cleanup under `templates/ai-first-saas-starter/src/main/resources/static-resources/`.
- README copy explaining fixture inspection vs normal backend runtime.

## Out of scope

- Backend repository durability.
- Visual redesign beyond fixture gating and state copy needed for correctness.

## Expected outputs

- Updated template frontend source/tests.
- Updated root `frontend/` mirror source/tests or documented no-sync rationale.
- Refreshed static resources or cleanup if generated assets should not be committed.
- Updated queue status.

## Required checks

- `git diff --check`
- `cd templates/ai-first-saas-starter/frontend && npm test -- --run && npm run typecheck && npm run build`
- Root frontend checks if root mirror changes: `cd frontend && npm test -- --run && npm run typecheck && npm run build`
- `rg -n "fixtureWorkstream|FixtureWorkstream|fixture|demo|InMemory|fake|model-less|OPENAI_API_KEY|WORKOS_API_KEY" templates/ai-first-saas-starter/src/main/resources/static-resources --glob '!**/*.map'`

## Done criteria

- Normal frontend runtime cannot render fixture bootstrap data or fixture clients unless explicit local/dev fixture mode is enabled.
- Fixture inspection remains clearly labeled and gated.
- Static resources match the updated source or are intentionally cleaned.
- Checks pass and changes are committed.

## Commit message

- `full-core-smb: gate frontend fixtures`
