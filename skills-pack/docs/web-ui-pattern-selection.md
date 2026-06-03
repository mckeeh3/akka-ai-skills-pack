# Web UI pattern selection

Use this doc when an Akka service needs a browser-facing React/Vite/TypeScript app hosted by Akka, not only JSON APIs. For generated full-stack AI-first SaaS, the default browser app is an agent workstream shell rather than a page-first console.

## Default conventions

### Full frontend project mapping

Use this for real web apps. Generated SaaS apps should implement the agent workstream shell: role-authorized functional-agent left rail, continuous main workstream panel, persistent bottom composer, context/authority indicators, and structured surfaces.

Canonical full-core starter implementation reference in this source repository: `templates/ai-first-saas-starter/**`. In an installed pack, use `resources/templates/ai-first-saas-starter/**` as the scaffold base. Reusable workstream UI architecture and source-reference modules live in `docs/workstream-ui-reference-architecture.md`, `frontend/src/workstream/**`, and `frontend/src/workstream-user-admin-vertical.contract.test.mjs`; installed packs export those UI references under `resources/examples/frontend/**`. The User Admin vertical is the canonical foundation-admin UI pattern for dashboard → list/search → detail/edit behavior through structured surfaces. Endpoint-only and static-resource examples below are delivery mechanics references; they are not canonical generated SaaS app structure.

- frontend source and tooling: `frontend/**`
- production build output: `src/main/resources/static-resources/**`
- build command: the frontend project's script, commonly `cd frontend && npm run build`

The frontend build owns generated files under `static-resources/`. Edit `frontend/src/**`, then rebuild.

## Pattern selection

| Need | Use | First example |
| --- | --- | --- |
| Full React/Vite/TypeScript agent workstream app with functional-agent rail, stream panel, composer, structured surfaces, state, forms, typed API clients, accessibility, responsive behavior, and a standard frontend build | Frontend project web app pattern | `docs/workstream-ui-reference-architecture.md`, `frontend/src/workstream/**`, `frontend/src/workstream-user-admin-vertical.contract.test.mjs`, `skills/akka-web-ui-apps/SKILL.md`, and `skills/akka-web-ui-frontend-project/SKILL.md` |
| Co-hosted frontend app calling JSON endpoints | UI + JSON API pattern | `src/main/java/com/example/api/WebUiHomeEndpoint.java` and `src/main/java/com/example/api/WebUiDataEndpoint.java` |
| Frontend app consuming live one-way updates | UI + SSE pattern | `src/main/java/com/example/api/WebUiSsePageEndpoint.java` plus `src/main/java/com/example/api/CounterStreamEndpoint.java` |
| Frontend app needing two-way communication | UI + WebSocket pattern | `src/main/java/com/example/api/WebUiWebSocketPageEndpoint.java` plus `src/main/java/com/example/api/PingWebSocketEndpoint.java` |
| Route exposure and backend access boundaries | Apply the mandatory secure SaaS foundation for protected APIs; public static asset routes are the exception | `skills/core-saas-foundation/SKILL.md`, `skills/akka-http-endpoint-jwt/SKILL.md`, and `skills/akka-http-endpoint-acl-internal/SKILL.md` |

## Frontend project requirement for workstream web apps

Use a standard React/Vite/TypeScript frontend project when the browser app has product surface area, especially when it:

- has a functional-agent rail, continuous stream, composer, structured surfaces, or complex state
- has multiple structured surfaces, direct surface URLs, or deep links that support the workstream shell
- needs React or another UI framework
- needs Vite or another bundler for production assets
- has componentized UI code, design-system styling, or frontend test tooling
- should be developed independently from the Akka Java source tree

Akka components, endpoints, and backend tests stay in Java. The frontend project owns browser source and generated production assets. Conventional route/page navigation is an implementation/deep-link detail for generated SaaS apps, not the primary decomposition. Route/page existence tests are not enough for generated SaaS UI; include shell, functional-agent rail, composer, structured-surface, action, deep-link, realtime/stale, and User Admin vertical contract coverage.

Read next:
- `docs/workstream-ui-reference-architecture.md`
- `docs/web-ui-frontend-decomposition.md`
- `docs/web-ui-frontend-project-integration.md`
- `docs/web-ui-style-guide.md`
- `docs/web-ui-api-contract-patterns.md`
- `docs/web-ui-quality-checklist.md`

## Recommended route shape

Use route families with clear separation:

- frontend app shell and generated assets: `/`, `/assets/**`, `/ui...`, or explicit app/workstream entry routes
- workstream deep links: explicit entries for selected functional agent, stream item, or structured surface when refreshable URLs are required; load protected data through APIs
- JSON API routes: `/api/...`
- SSE streams: `/counter-stream/...`, `/view-streams/...`, `/streams/...`, or another explicit stream prefix
- WebSocket routes: `/websockets/...`
- internal-only routes: `/internal-...` or another clearly non-public prefix

This keeps route intent visible from URL shape alone. Avoid broad `/**` SPA fallbacks when they overlap asset wildcards; use hash routing or explicit frontend entry routes instead. Do not use route shape, left-rail visibility, or hidden controls as authorization; backend capabilities remain authoritative.

## Security routing note

This pattern-selection doc focuses on web UI delivery and route integration. For generated SaaS applications, route policy, JWTs, identity-provider integration, AuthContext selection, backend authorization, tenant/customer filtering, and authorization UX inherit the mandatory secure foundation. Public static asset routes and static deep-link shell entries are the narrow exceptions; protected APIs, workstream data, surface payloads, and streams must use security-specific guidance.

## API documentation assets

Generated API documentation assets are not a browser UI implementation path. If a service must expose them, keep that concern separate from frontend app delivery and use official Akka HTTP resource-serving semantics directly.

## What to read next

Start with:

- `docs/web-ui-style-guide.md` for style-guide selection when browser UI styling is not yet selected
- `skills/akka-web-ui-apps/SKILL.md` for complete frontend apps
- `skills/akka-web-ui-frontend-project/SKILL.md` for standard frontend project integration
- `skills/akka-http-endpoint-web-ui/SKILL.md` for Akka hosting and web UI delivery

Then load the focused companion skill you need:

- `skills/akka-http-endpoint-sse/SKILL.md`
- `skills/akka-http-endpoint-websocket/SKILL.md`
- `skills/core-saas-foundation/SKILL.md` for mandatory SaaS identity, tenancy, authorization, `/api/me`, audit, and tenant-isolation expectations
- `skills/akka-http-endpoint-jwt/SKILL.md` for protected browser/service API routes
- `skills/akka-http-endpoint-acl-internal/SKILL.md` for internal-only routes or method-level service ACLs
