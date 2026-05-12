---
name: akka-http-endpoint-web-ui
description: Route Akka Java SDK tasks involving Akka-hosted frontend build output, co-hosted JSON APIs, SSE pages, and WebSocket pages. Use when the service should serve a React/Vite/TypeScript web UI from HTTP endpoints.
---

# Akka HTTP Endpoint Web UI

Use this skill when an Akka service should host the production build output of a browser-facing frontend app.

For a complete user-facing frontend app, start with `akka-web-ui-apps` first, then return here for Akka HTTP hosting, route shape, packaged build assets, and endpoint tests.

## Required reading

Read these first if present:
- `akka-context/sdk/http-endpoints.html.md`
- `../../../docs/web-ui-pattern-selection.md`
- `../../../docs/web-ui-frontend-decomposition.md`
- `../../../docs/web-ui-style-guide.md`
- `../../../docs/web-ui-frontend-project-integration.md`
- `../../../docs/web-ui-quality-checklist.md`
- `../../../src/main/java/com/example/api/WebUiHomeEndpoint.java`
- `../../../src/main/java/com/example/api/WebUiDataEndpoint.java`
- `../../../src/main/java/com/example/api/WebUiSsePageEndpoint.java`
- `../../../src/main/java/com/example/api/WebUiWebSocketPageEndpoint.java`
- `../../../src/main/java/com/example/api/CounterStreamEndpoint.java`
- `../../../src/main/java/com/example/api/PingWebSocketEndpoint.java`
- `../../../src/test/java/com/example/application/WebUiHomeEndpointIntegrationTest.java`
- `../../../src/test/java/com/example/application/WebUiDataEndpointIntegrationTest.java`
- `../../../src/test/java/com/example/application/WebUiSsePageEndpointIntegrationTest.java`
- `../../../src/test/java/com/example/application/WebUiWebSocketPageEndpointIntegrationTest.java`

## Use this skill when

- the service should serve generated React/Vite/TypeScript browser UI assets through Akka HTTP endpoints
- `akka-web-ui-apps` has identified screens, frontend state, and browser API contracts
- the UI calls JSON endpoints in the same service
- the UI should consume SSE updates
- the UI should consume a WebSocket endpoint
- you need route-shape guidance for `/ui`, `/api`, stream, and socket paths
- a standard frontend project should build production assets into `src/main/resources/static-resources/`
- generated CSS should apply the selected web UI style guide/theme
- if a browser UI style is missing/unselected, add or update `specs/pending-questions.md` with the style-selection question from `../../../docs/web-ui-style-guide.md` before implementing affected UI assets
- auth/security implementation details are out of scope unless the user explicitly asks for them

## Pattern selection

### Complete frontend app
Use first:
- `akka-web-ui-apps`

Then add focused frontend companions as needed:
- `akka-web-ui-frontend-project`
- `akka-web-ui-api-client`
- `akka-web-ui-state-rendering`
- `akka-web-ui-forms-validation`
- `akka-web-ui-realtime`
- `akka-web-ui-accessibility-responsive`
- `akka-web-ui-testing`

Use this when the browser UI has real user journeys, multiple states, forms, navigation, or frontend application logic. Use `akka-web-ui-frontend-project` for full React/Vite or similar app projects.

### UI + JSON API
Read first:
- `../../../src/main/java/com/example/api/WebUiHomeEndpoint.java`
- `../../../src/main/java/com/example/api/WebUiDataEndpoint.java`

Use this when a page should load JSON through `fetch`; for product UI work, keep the browser source in `frontend/src/**`.

### UI + SSE
Read first:
- `../../../src/main/java/com/example/api/WebUiSsePageEndpoint.java`
- `../../../src/main/java/com/example/api/CounterStreamEndpoint.java`

Use this when the browser needs server-to-client live updates; for product UI work, keep the browser source in `frontend/src/**`.

### UI + WebSocket
Read first:
- `../../../src/main/java/com/example/api/WebUiWebSocketPageEndpoint.java`
- `../../../src/main/java/com/example/api/PingWebSocketEndpoint.java`

Use this when the browser needs two-way communication; for product UI work, keep the browser source in `frontend/src/**`.

### Public vs protected/internal routes
Read next as needed:
- `../../../docs/web-ui-pattern-selection.md`
- `../akka-http-endpoint-jwt/SKILL.md`
- `../akka-http-endpoint-acl-internal/SKILL.md`
- `../../../src/main/java/com/example/api/InternalStatusEndpoint.java`

## Frontend asset guidance

Use a standard frontend project for full apps.

- source lives under `frontend/**`
- build output goes to `src/main/resources/static-resources/`
- build with the frontend project's script, for example `cd frontend && npm run build`
- do not hand-edit generated files in `static-resources/`
- read `docs/web-ui-frontend-project-integration.md`

Akka implementation and tests remain Java-based.

## Route guidance

Prefer clear route families:

- generated frontend app shell and assets: `/`, `/assets/**`, `/ui...`, or explicit app-specific entry routes
- JSON APIs: `/api/...`
- SSE streams: explicit stream prefix such as `/counter-stream/...` or `/streams/...`
- WebSocket routes: `/websockets/...`

Keep those route families separate so a future agent can infer intent from the path alone. Avoid a broad `/**` SPA fallback when it overlaps `/assets/**`; use hash routing or explicit entry routes for deep links.

## Repository examples

- `WebUiHomeEndpoint` + `WebUiDataEndpoint`
  - generated app shell plus JSON API hosting pattern
- `WebUiSsePageEndpoint` + `CounterStreamEndpoint`
  - generated app shell consuming SSE
- `WebUiWebSocketPageEndpoint` + `PingWebSocketEndpoint`
  - generated app shell consuming a WebSocket

## Testing rule

Default to endpoint integration tests using `httpClient` for generated app shell and asset routes.

Add route-level assertions for:
- generated `index.html`
- generated JS and CSS assets
- explicit API, SSE, or WebSocket path references in the served app shell

## Anti-patterns

Avoid:
- treating browser UI work as ad hoc HTML/file-serving work
- mixing UI routes and API routes under one ambiguous path family
- treating generated frontend build output as source code
- hiding the frontend source path or the served JavaScript/CSS path
- relying on browser-only behavior without a route-level integration test
- treating a complete frontend app as only an asset-hosting concern
- implementing only happy-path UI states

## Review checklist

Before finishing, verify:
- UI shell routes are explicit and do not conflict with asset wildcards
- JSON API routes are under `/api/...`
- SSE and WebSocket routes remain explicit and separate
- frontend source paths and served JavaScript/CSS asset paths are easy to correlate
- integration tests fetch the generated app shell and CSS/JS asset routes through `httpClient`
- non-trivial UI work has been reviewed against `docs/web-ui-quality-checklist.md` and the selected style guide
