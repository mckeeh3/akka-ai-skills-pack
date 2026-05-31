# TASK-FC5-02-001: Add shared rich surface/action runtime path

## Objective

Implement reusable backend/frontend support for explicit full-core rich surfaces and surface actions while preserving the five `markdown_response` v0 bootstrap.

## Required reads

- `specs/full-core-five-workstreams/full-core-contract-matrix.md` if present
- `docs/structured-surface-contracts.md`
- `docs/agent-workstream-application-architecture.md`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamService.java`
- `templates/ai-first-saas-starter/frontend/src/workstream/types/surfaces.ts`
- `templates/ai-first-saas-starter/frontend/src/workstream/types/actions.ts`

## Expected outputs

- Typed backend/frontend surface and action structures for full-core surface types.
- Explicit shell request/action API path for `show_surface`, `refresh_surface`, `open_attention_item`, and action submission.
- System-message denial/deferred/error surfaces for unauthorized or unavailable rich requests.
- Tests proving rich surfaces are not part of initial v0 bootstrap unless explicitly requested.

## Checks

- `mvn test`
- `cd templates/ai-first-saas-starter/frontend && npm test -- --run`
- `cd templates/ai-first-saas-starter/frontend && npm run typecheck`
- `git diff --check`

## Done criteria

The shared runtime can carry rich typed surfaces/actions through real backend/API/frontend paths without demo-only fixtures or frontend-only authorization.
