# Workstream UI frontend quality and packaging handoff

This localized React/Vite frontend is the source-repository implementation reference for the canonical AI-first SaaS agent workstream UI. Source of record lives under `frontend/src/**`, with reusable contracts and components under `frontend/src/workstream/**`.

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

Legacy `frontend/src/screens/**` files are retained only as quarantined drift/mechanics references. New generated SaaS frontend work should start from `docs/workstream-ui-reference-architecture.md`, `frontend/src/workstream/**`, `frontend/src/main.tsx`, and `frontend/src/workstream-user-admin-vertical.contract.test.mjs`.

## Explicit defers

The fixture frontend intentionally defers real authenticated backend integration, admin authorization enforcement, policy commit execution, trace export, and durable Akka state. Workstream components use typed client seams so later slices can replace fixture clients with real `/api/...` implementations while preserving backend-authoritative capability, tenant isolation, audit, and forbidden-state contracts.
