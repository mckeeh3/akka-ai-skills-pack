---
name: akka-http-endpoint-web-ui
description: Route Akka Java SDK tasks involving packaged browser UIs, co-hosted JSON APIs, SSE pages, and WebSocket pages. Use when the service should serve a web UI from HTTP endpoints.
---

# Akka HTTP Endpoint Web UI

Use this skill when an Akka service should serve a browser-facing UI from packaged resources.

For a complete user-facing frontend app, start with `akka-web-ui-apps` first, then return here for Akka HTTP hosting, route shape, packaged build assets, and endpoint tests.

## Required reading

Read these first if present:
- `akka-context/sdk/http-endpoints.html.md`
- `../../../docs/web-ui-pattern-selection.md`
- `../../../docs/web-ui-frontend-decomposition.md`
- `../../../docs/web-ui-style-guide.md`
- `../../../docs/web-ui-frontend-project-integration.md`
- `../../../docs/web-ui-lightweight-typescript-architecture.md`
- `../../../docs/web-ui-quality-checklist.md`
- `../../../src/main/java/com/example/api/WebUiHomeEndpoint.java`
- `../../../src/main/java/com/example/api/WebUiDataEndpoint.java`
- `../../../src/main/java/com/example/api/WebUiSsePageEndpoint.java`
- `../../../src/main/java/com/example/api/WebUiWebSocketPageEndpoint.java`
- `../../../src/main/java/com/example/api/FrontendReferenceUiEndpoint.java`
- `../../../src/main/java/com/example/api/FrontendReferenceApiEndpoint.java`
- `../../../src/main/java/com/example/api/CounterStreamEndpoint.java`
- `../../../src/main/java/com/example/api/PingWebSocketEndpoint.java`
- `../../../src/test/java/com/example/application/WebUiHomeEndpointIntegrationTest.java`
- `../../../src/test/java/com/example/application/WebUiDataEndpointIntegrationTest.java`
- `../../../src/test/java/com/example/application/WebUiSsePageEndpointIntegrationTest.java`
- `../../../src/test/java/com/example/application/WebUiWebSocketPageEndpointIntegrationTest.java`
- `../../../src/test/java/com/example/application/FrontendReferenceWebUiIntegrationTest.java`

## Use this skill when

- the service should bundle a small browser UI with Akka
- `akka-web-ui-apps` has identified screens, frontend state, and browser API contracts
- the UI calls JSON endpoints in the same service
- the UI should consume SSE updates
- the UI should consume a WebSocket endpoint
- you need route-shape guidance for `/ui`, `/api`, stream, and socket paths
- a standard frontend project should build production assets into `src/main/resources/static-resources/`
- packaged CSS should apply the selected web UI style guide/theme
- if a browser UI style is missing/unselected, add or update `specs/pending-questions.md` with the style-selection question from `../../../docs/web-ui-style-guide.md` before implementing affected UI assets
- auth/security implementation details are out of scope unless the user explicitly asks for them

## Pattern selection

### Static content only
Use:
- `akka-http-endpoint-static-content`

Read first:
- `../../../src/main/java/com/example/api/StaticContentEndpoint.java`

Use this when the task is mainly packaged HTML, CSS, OpenAPI, or file-serving.

### Complete frontend app
Use first:
- `akka-web-ui-apps`

Then add focused frontend companions as needed:
- `akka-web-ui-frontend-project`
- `akka-web-ui-lightweight-typescript`
- `akka-web-ui-api-client`
- `akka-web-ui-state-rendering`
- `akka-web-ui-forms-validation`
- `akka-web-ui-realtime`
- `akka-web-ui-accessibility-responsive`
- `akka-web-ui-testing`

Use this when the browser UI has real user journeys, multiple states, forms, navigation, or frontend application logic. Prefer `akka-web-ui-frontend-project` for full React/Vite or similar app projects; use `akka-web-ui-lightweight-typescript` only when the UI is intentionally framework-free.

### UI + JSON API
Read first:
- `../../../src/main/java/com/example/api/WebUiHomeEndpoint.java`
- `../../../src/main/java/com/example/api/WebUiDataEndpoint.java`
- `../../../src/main/web-ui/web-ui/app.ts`

Use this when a page should load JSON through `fetch`.

### UI + SSE
Read first:
- `../../../src/main/java/com/example/api/WebUiSsePageEndpoint.java`
- `../../../src/main/java/com/example/api/CounterStreamEndpoint.java`
- `../../../src/main/web-ui/web-ui-sse/app.ts`

Use this when the browser only needs server-to-client live updates.

### UI + WebSocket
Read first:
- `../../../src/main/java/com/example/api/WebUiWebSocketPageEndpoint.java`
- `../../../src/main/java/com/example/api/PingWebSocketEndpoint.java`
- `../../../src/main/web-ui/web-ui-websocket/app.ts`

Use this when the browser needs two-way communication.

### Public vs protected/internal routes
Read next as needed:
- `../../../docs/web-ui-pattern-selection.md`
- `../akka-http-endpoint-jwt/SKILL.md`
- `../akka-http-endpoint-acl-internal/SKILL.md`
- `../../../src/main/java/com/example/api/InternalStatusEndpoint.java`

## Frontend asset guidance

Two frontend ownership models are valid:

### Standard frontend project

Use for full apps.

- source lives under `frontend/**`
- build output goes to `src/main/resources/static-resources/`
- build with the frontend project's script, for example `cd frontend && npm run build`
- do not hand-edit generated files in `static-resources/`
- read `docs/web-ui-frontend-project-integration.md`

### Lightweight framework-free TypeScript

Use only for small Akka-focused examples or deliberately framework-free apps.

- TypeScript source lives under `src/main/web-ui/<example>/`
- served JavaScript/CSS/HTML lives under `src/main/resources/static-resources/<example>/`
- build with `npm run build:web-ui`
- read `docs/web-ui-lightweight-typescript-architecture.md`

Akka implementation and tests remain Java-based in both models.

## Route guidance

Prefer clear route families:

- packaged UI shell and assets: `/`, `/assets/**`, `/ui...`, or explicit app-specific entry routes
- JSON APIs: `/api/...`
- SSE streams: explicit stream prefix such as `/counter-stream/...` or `/streams/...`
- WebSocket routes: `/websockets/...`

Keep those route families separate so a future agent can infer intent from the path alone. Avoid a broad `/**` SPA fallback when it overlaps `/assets/**`; use hash routing or explicit entry routes for deep links.

## Repository examples

- `WebUiHomeEndpoint` + `WebUiDataEndpoint`
  - packaged page plus JSON API
  - browser code authored in TypeScript and served as `app.js`
- `WebUiSsePageEndpoint` + `CounterStreamEndpoint`
  - packaged page consuming SSE
- `WebUiWebSocketPageEndpoint` + `PingWebSocketEndpoint`
  - packaged page consuming a WebSocket
- `StaticContentEndpoint`
  - packaged files and OpenAPI publishing without interactive browser logic
- `FrontendReferenceUiEndpoint` + `FrontendReferenceApiEndpoint`
  - modular framework-free TypeScript frontend with typed API calls, forms, validation, state rendering, accessibility, and responsive layout

## Testing rule

Default to endpoint integration tests using `httpClient` for page and asset routes.

Add route-level assertions for:
- packaged HTML
- packaged JS and CSS assets
- explicit API, SSE, or WebSocket path references in the served page

## Anti-patterns

Avoid:
- starting with the static-content skill when the main task is an interactive browser page
- mixing UI routes and API routes under one ambiguous path family
- treating generated frontend build output as source code
- hiding the frontend source path or the served JavaScript/CSS path
- relying on browser-only behavior without a route-level integration test
- treating a complete frontend app as only a static-content concern
- implementing only happy-path UI states

## Review checklist

Before finishing, verify:
- UI shell routes are explicit and do not conflict with asset wildcards
- JSON API routes are under `/api/...`
- SSE and WebSocket routes remain explicit and separate
- TypeScript source and served JavaScript paths are easy to correlate
- integration tests fetch the packaged page and CSS/JS asset routes through `httpClient`
- non-trivial UI work has been reviewed against `docs/web-ui-quality-checklist.md` and the selected style guide
