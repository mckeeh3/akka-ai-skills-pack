# Workstream UI frontend quality and packaging handoff

This localized React/Vite frontend is the implementation reference for the canonical AI-first SaaS agent workstream UI. In the source repository it lives under `frontend/**`; in an installed pack it is exported under `resources/examples/frontend/**` and scaffolded into starter apps under `frontend/**`. The source of record is this directory's `src/**`, with reusable contracts and components under `src/workstream/**`.

## Checks

Run from `frontend/`:

```bash
npm run typecheck
npm test
npm run build
```

## Akka static hosting output

`npm run build` writes the Vite production build to:

```text
src/main/resources/static-resources/
```

The Akka endpoint serves `index.html`, `/favicon.ico`, and `/assets/**` from that directory. The generated `index.html` references the active hashed CSS/JS assets. Other static reference examples under `src/main/resources/static-resources/**` are intentionally preserved by the build command as endpoint mechanics or legacy references; do not promote them as canonical generated-app UI structure.

## Canonical contract scope

Frontend contract tests cover the workstream-first reference:

- `/api/me` bootstrap, selected `AuthContext`, visible capabilities, and forbidden/disabled variants.
- Role-authorized functional-agent rail with denied/hidden/collapsed states.
- Continuous workstream panel, action-feedback items, trace links, and stale/realtime states.
- Persistent selected-agent composer and command shortcuts such as `show users`.
- Structured surfaces for dashboard, list/search, detail/edit, decision, audit timeline, workflow status, governance diff, and outcome patterns.
- Capability-backed action controls with disabled/denied reasons, idempotency, confirmation, audit/trace affordances, and result-surface handling.
- Deep links for selected functional agents, stream items, and surfaces without making pages/routes the primary UI model.
- User Admin dashboard → list/search → detail/edit reference vertical through structured surfaces.

Legacy `frontend/src/screens/**` files are retained only as quarantined drift/mechanics references. In the source repository, new generated SaaS frontend work should start from `docs/workstream-ui-reference-architecture.md`, `frontend/src/workstream/**`, `frontend/src/main.tsx`, and `frontend/src/workstream-user-admin-vertical.contract.test.mjs`. In an installed pack, the same frontend reference is under `resources/examples/frontend/**` and the architecture doc is under `docs/workstream-ui-reference-architecture.md`.

## Backend integration stance

The production path uses `HttpWorkstreamApiClient` against same-origin `/api/workstream/...` and `/api/me` routes. Use `?fixtureWorkstream=1` only for frontend-only inspection and contract tests. Backend authorization, tenant isolation, audit, policy commit execution, trace export, and durable Akka state remain backend-authoritative; frontend controls are UX hints, not authorization.
