# AI-first SaaS starter frontend

This React/Vite frontend is the scaffolded browser app for the AI-first SaaS starter. It runs in production-first mode by default: WorkOS/AuthKit signs in the browser user, the app calls same-origin `/api/me` and `/api/workstream/...` routes through `HttpWorkstreamApiClient`, and backend authorization remains authoritative for tenant isolation, capability checks, audit, policy commits, trace export, and durable Akka state.

Fixture data is still included for deterministic frontend inspection and contract tests, but it is opt-in only. Append `?fixtureWorkstream=1` to run the shell with `FixtureWorkstreamApiClient` and fixture realtime events when a backend or AuthKit tenant is not available.

## Local production-mode checks

Configure the WorkOS/AuthKit browser values before building or serving the production path:

```bash
cp .env.example .env.local
# set VITE_WORKOS_CLIENT_ID=client_...
# set VITE_WORKOS_REDIRECT_URI=http://localhost:5173/callback or your local callback
npm install
npm run typecheck
npm test -- --run
npm run build
```

The built app expects backend routes to enforce identity, selected AuthContext, membership, role/capability, and tenant/customer boundaries. Frontend visibility and disabled controls are UX hints only.

## Explicit fixture mode

Use fixture mode only for frontend-only inspection, story-like review, and contract tests:

```text
http://localhost:5173/?fixtureWorkstream=1
```

Fixture mode exercises the same shell, structured surfaces, capability action request shapes, stale/reconnect states, and trace affordances without issuing real backend commands. User-visible production copy should not describe default behavior as fixture-backed.

## Checks

Run from this directory:

```bash
npm test -- --run
npm run typecheck
npm run build
```

## Akka static hosting output

`npm run build` writes the Vite production build to:

```text
src/main/resources/static-resources/
```

The Akka endpoint serves `index.html`, `/favicon.ico`, and `/assets/**` from that directory. Do not hand-edit generated build output; rebuild from frontend source.

## Canonical contract scope

Frontend contract tests cover:

- `/api/me` bootstrap, selected `AuthContext`, visible capabilities, and forbidden/disabled variants.
- Role-authorized functional-agent rail with denied/hidden/collapsed states.
- Continuous workstream panel, action-feedback items, trace links, and stale/realtime states.
- Persistent selected-agent composer and command shortcuts such as `show users`.
- Structured surfaces for dashboard, list/search, detail/edit, decision, audit timeline, workflow status, governance diff, and outcome patterns.
- Capability-backed action controls with disabled/denied reasons, idempotency, confirmation, audit/trace affordances, and result-surface handling.
- Deep links for selected functional agents, stream items, and surfaces without making pages/routes the primary UI model.
- User Admin dashboard → list/search → detail/edit starter vertical through structured surfaces.
- My Account notification center rendering from backend-derived in-app notification data, including governed notification action descriptors, redacted/empty/error states, backend-owned counts, and explicit absence of implemented email/push controls.

Legacy `src/screens/**` files are retained only as quarantined mechanics references for older slice tests. New generated SaaS frontend work should extend `src/workstream/**`, `src/api/**`, and `src/main.tsx`.
