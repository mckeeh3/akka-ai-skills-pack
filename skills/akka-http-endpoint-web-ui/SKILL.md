---
name: akka-http-endpoint-web-ui
description: Route Akka Java SDK tasks involving packaged browser UIs, co-hosted JSON APIs, SSE pages, and WebSocket pages. Use when the service should serve a web UI from HTTP endpoints.
---

# Akka HTTP Endpoint Web UI

Use this skill when an Akka service should serve a browser-facing UI from packaged resources.

For a complete user-facing frontend app, start with `akka-web-ui-apps` first, then return here for Akka HTTP hosting, route shape, packaged assets, and endpoint tests.

## Required reading

Read these first if present:
- `akka-context/sdk/http-endpoints.html.md`
- `../../../docs/web-ui-pattern-selection.md`
- `../../../docs/web-ui-frontend-decomposition.md`
- `../../../docs/web-ui-style-guide.md`
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
- TypeScript-authored browser logic should stay minimal and framework-free
- packaged CSS should apply the selected web UI style guide/theme
- if a browser UI style is missing/unselected, add or update `specs/pending-questions.md` with the style-selection question from `../../../docs/web-ui-style-guide.md` before implementing affected UI assets

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
- `akka-web-ui-lightweight-typescript`
- `akka-web-ui-api-client`
- `akka-web-ui-state-rendering`
- `akka-web-ui-forms-validation`
- `akka-web-ui-realtime`
- `akka-web-ui-accessibility-responsive`
- `akka-web-ui-testing`

Use this when the browser UI has real user journeys, multiple states, forms, navigation, or frontend application logic.

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

## TypeScript guidance

In this repository:

- TypeScript source lives under `src/main/web-ui/<example>/app.ts`
- served JavaScript lives under `src/main/resources/static-resources/<example>/app.js`
- build with `npm run build:web-ui`
- Akka implementation and tests remain Java-based

Tiny workflow note:
- after editing any `src/main/web-ui/...` file, run `npm run build:web-ui` before packaging or running endpoint integration tests so the served `app.js` stays in sync

Prefer TypeScript for interactive browser examples. Do not introduce a frontend framework for this skill family.

For non-trivial apps, use the modular TypeScript layout in `docs/web-ui-lightweight-typescript-architecture.md` instead of putting all browser behavior in one `app.ts`.

## Route guidance

Prefer clear route families:

- packaged UI shell and assets: `/ui...`
- JSON APIs: `/api/...`
- SSE streams: explicit stream prefix such as `/counter-stream/...`
- WebSocket routes: `/websockets/...`

Keep those route families separate so a future agent can infer intent from the path alone.

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
- introducing React, Angular, Vue, Vite, or bundler-specific assumptions in this repository wave
- hiding the TypeScript source path or the served JavaScript path
- relying on browser-only behavior without a route-level integration test
- treating a complete frontend app as only a static-content concern
- implementing only happy-path UI states

## Review checklist

Before finishing, verify:
- UI shell routes are under `/ui...`
- JSON API routes are under `/api/...`
- SSE and WebSocket routes remain explicit and separate
- TypeScript source and served JavaScript paths are easy to correlate
- integration tests fetch the packaged page and CSS/JS asset routes through `httpClient`
- non-trivial UI work has been reviewed against `docs/web-ui-quality-checklist.md` and the selected style guide
