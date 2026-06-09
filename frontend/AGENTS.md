# Frontend App Realization Guidance

This directory is part of the default app-realization mode. Edit here when implementing the React/Vite/TypeScript browser UI for the runnable SaaS app.

## Scope

- Workstream shells, structured surfaces, API clients, auth-aware UI, accessibility, responsive layouts, and frontend tests.
- Domain-specific extensions should live under `frontend/src/extensions/<domain>/` when possible.

## Rules

- Keep browser secret boundaries intact. Do not expose server/provider secrets to frontend code.
- Align typed API clients and DTOs with Akka HTTP endpoint contracts.
- Render loading, empty, error, stale, and success states explicitly for workstream surfaces.
- Preserve role/tenant-aware navigation and action visibility; backend authorization remains authoritative.
- Do not edit `skills-pack/**` from this mode unless the user explicitly requests skills-pack maintenance.

## Checks

Choose the smallest proof for the change. Common frontend checks:

```bash
npm --prefix frontend test -- --run
npm --prefix frontend run typecheck
npm --prefix frontend run build
git diff --check
```
