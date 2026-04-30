# Web UI pattern selection

Use this doc when an Akka service needs packaged browser-facing assets, not only JSON APIs.

## Default conventions

### Full frontend project mapping

Use this for real web apps:

- frontend source and tooling: `frontend/**`
- production build output: `src/main/resources/static-resources/**`
- build command: the frontend project's script, commonly `cd frontend && npm run build`

The frontend build owns generated files under `static-resources/`. Edit `frontend/src/**`, then rebuild.

### Lightweight TypeScript mapping

Use this only for deliberately small framework-free examples:

- TypeScript source: `src/main/web-ui/<example>/app.ts`
- served JavaScript asset: `src/main/resources/static-resources/<example>/app.js`
- packaged HTML/CSS: `src/main/resources/static-resources/<example>/...`
- build command: `npm run build:web-ui`

## Pattern selection

| Need | Use | First example |
| --- | --- | --- |
| Full frontend app with screens, state, forms, typed API clients, accessibility, responsive behavior, and a standard frontend build | Frontend project web app pattern | `skills/akka-web-ui-apps/SKILL.md` plus `skills/akka-web-ui-frontend-project/SKILL.md` |
| Packaged docs, HTML, CSS, OpenAPI, or simple file serving | Narrow static-content pattern | `src/main/java/com/example/api/StaticContentEndpoint.java` |
| Co-hosted browser page calling JSON endpoints | UI + JSON API pattern | `src/main/java/com/example/api/WebUiHomeEndpoint.java` and `src/main/java/com/example/api/WebUiDataEndpoint.java` |
| Browser page consuming live one-way updates | UI + SSE pattern | `src/main/java/com/example/api/WebUiSsePageEndpoint.java` plus `src/main/java/com/example/api/CounterStreamEndpoint.java` |
| Browser page needing two-way communication | UI + WebSocket pattern | `src/main/java/com/example/api/WebUiWebSocketPageEndpoint.java` plus `src/main/java/com/example/api/PingWebSocketEndpoint.java` |
| Route exposure and backend access boundaries | Defer to security-specific routing when in scope | `skills/akka-http-endpoint-jwt/SKILL.md` and `skills/akka-http-endpoint-acl-internal/SKILL.md` |

## When plain JavaScript is enough

Plain JavaScript is sufficient when:

- the page is static or nearly static
- the browser code is only a few lines
- there is no meaningful client-side state
- the example is teaching file serving rather than browser logic

Use the narrow static-content pattern for those cases.

## When a frontend project is preferred

Use a standard frontend project when the browser app has real product surface area, especially when it:

- has multiple screens or complex navigation
- needs React or another UI framework
- needs Vite or another bundler for production assets
- has componentized UI code, design-system styling, or frontend test tooling
- should be developed independently from the Akka Java source tree

Akka components, endpoints, and backend tests stay in Java. The frontend project owns browser source and generated production assets.

## When lightweight TypeScript is preferred

Use lightweight framework-free TypeScript when the UI is small, Akka-pattern-focused, or intentionally dependency-light, especially when it:

- calls a few JSON APIs with typed payloads
- consumes SSE/WebSocket updates with simple state
- has modest DOM updates and forms
- would be harder to understand with a full framework scaffold

Read next:
- `docs/web-ui-frontend-decomposition.md`
- `docs/web-ui-frontend-project-integration.md`
- `docs/web-ui-style-guide.md`
- `docs/web-ui-lightweight-typescript-architecture.md`
- `docs/web-ui-api-contract-patterns.md`
- `docs/web-ui-quality-checklist.md`

## Recommended route shape

Use route families with clear separation:

- UI shell and packaged assets: `/`, `/assets/**`, `/ui...`, or explicit app entry routes
- JSON API routes: `/api/...`
- SSE streams: `/counter-stream/...`, `/view-streams/...`, or another explicit stream prefix
- WebSocket routes: `/websockets/...`
- internal-only routes: `/internal-...` or another clearly non-public prefix

This keeps route intent visible from URL shape alone. Avoid broad `/**` SPA fallbacks when they overlap asset wildcards; use hash routing or explicit frontend entry routes instead.

## Security scope note

This pattern-selection doc focuses on web UI delivery and route integration. Public/protected/internal route policy, JWTs, identity-provider integration, and authorization UX should be handled by security-specific guidance when those tasks are in scope.

## OpenAPI publication

Use the static-content pattern when the service should publish a packaged `openapi.yaml`.

That belongs with static assets, not with the interactive UI examples.

## What to read next

Start with:

- `docs/web-ui-style-guide.md` for theme/style-guide selection when browser UI styling is not yet selected
- `skills/akka-web-ui-apps/SKILL.md` for complete frontend apps
- `skills/akka-web-ui-frontend-project/SKILL.md` for standard frontend project integration
- `skills/akka-http-endpoint-web-ui/SKILL.md` for Akka hosting and web UI delivery

Then load the focused companion skill you need:

- `skills/akka-http-endpoint-static-content/SKILL.md`
- `skills/akka-http-endpoint-sse/SKILL.md`
- `skills/akka-http-endpoint-websocket/SKILL.md`
- `skills/akka-http-endpoint-jwt/SKILL.md` only when security is in scope
- `skills/akka-http-endpoint-acl-internal/SKILL.md` only when internal-route security is in scope
