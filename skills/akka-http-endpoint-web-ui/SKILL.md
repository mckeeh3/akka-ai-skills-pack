---
name: akka-http-endpoint-web-ui
description: Route Akka Java SDK tasks involving Akka-hosted agent workstream frontend build output, co-hosted JSON APIs, SSE streams, and WebSocket routes. Use when the service should serve a React/Vite/TypeScript workstream web UI from HTTP endpoints.
---

# Akka HTTP Endpoint Web UI

Use this skill when an Akka service should host the production build output of a browser-facing frontend app.

For generated full-stack AI-first SaaS, the hosted app is an agent workstream shell by default: left-rail functional agents, main workstream panel, persistent bottom composer, context/authority indicators, and structured surfaces. For a complete user-facing frontend app, start with `akka-web-ui-apps` first, then return here for Akka HTTP hosting, route shape, packaged build assets, protected API/stream/socket separation, deep-link entries, and endpoint tests.


## Capability-first exposure rule

Treat every HTTP route as a selected exposure surface for a named backend capability, not as the capability itself. Before adding or changing a route, identify the capability id, allowed actors/callers, `AuthContext`, tenant/customer scope, input/output schema, side effects, idempotency, approval policy, audit/trace obligations, and tests.

For protected routes, preserve the capability contract at the edge: authenticate the caller, resolve or receive the selected tenant/customer context, authorize the required role/scope/capability, validate and redact HTTP payloads, map denials to explicit `401`/`403` behavior, and record required audit/work-trace events before calling components. Browser actions, API paths, hidden fields, and route names are not authorization controls.

When the same capability is also exposed through workstream surface actions, agent tools, workflows, gRPC, MCP, timers, or consumers, keep authority, validation, idempotency, approval, and audit semantics identical across surfaces. Consequential HTTP actions should call the workflow/entity/approval substrate that enforces policy instead of committing side effects only in endpoint code. Left-rail visibility, hidden controls, route names, and static frontend state are not authorization controls.

## Required reading

Read these first if present:
- `akka-context/sdk/http-endpoints.html.md`
- `../../../docs/workstream-ui-reference-architecture.md` for the canonical generated SaaS frontend reference and reusable `frontend/src/workstream/**` modules
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

The `WebUi*PageEndpoint` examples are endpoint delivery mechanics references. They do not replace the canonical generated SaaS app structure under `frontend/src/workstream/**` or the User Admin workstream vertical reference.

## Use this skill when

- the service should serve generated React/Vite/TypeScript browser UI assets through Akka HTTP endpoints
- the generated SaaS UI shell needs routes for left-rail functional agents, a main workstream, bottom composer, structured surfaces, and direct deep links
- `akka-web-ui-apps` has identified functional agents, structured surfaces, frontend state, and browser API contracts
- the UI calls JSON endpoints in the same service
- the UI should consume SSE updates
- the UI should consume a WebSocket endpoint
- you need route-shape guidance for `/ui`, `/api`, stream, socket, and workstream deep-link paths
- a standard frontend project should build production assets into `src/main/resources/static-resources/`
- generated CSS should apply the selected web UI style guide
- if a browser UI style is missing/unselected, add or update `specs/pending-questions.md` with the style-selection question from `../../../docs/web-ui-style-guide.md` before implementing affected UI assets
- public frontend asset routes are separated from JWT-protected `/api/...` routes; generated SaaS APIs use request-context and backend authorization helpers by default

## Pattern selection

### Complete agent workstream frontend app
Use first:
- `akka-web-ui-apps`

The default generated SaaS route plan serves a single authenticated shell with functional-agent rail, stream panel, composer, context indicators, and structured surfaces. Route/page navigation is an implementation detail for SPA state and refreshable deep links, not the primary UX architecture.

Then add focused frontend companions as needed:
- `akka-web-ui-frontend-project`
- `akka-web-ui-api-client`
- `akka-web-ui-state-rendering`
- `akka-web-ui-forms-validation`
- `akka-web-ui-realtime`
- `akka-web-ui-accessibility-responsive`
- `akka-web-ui-testing`

Use this when the browser UI has real user journeys, multiple states, forms, structured surfaces, functional-agent selection, stream state, navigation/deep links, or frontend application logic. Use `akka-web-ui-frontend-project` for full React/Vite or similar app projects.

### UI + JSON API
Read first:
- `../../../src/main/java/com/example/api/WebUiHomeEndpoint.java`
- `../../../src/main/java/com/example/api/WebUiDataEndpoint.java`

Use this when a browser surface should load JSON through `fetch`; for product UI work, keep the browser source in `frontend/src/**` and keep generated SaaS structure workstream-first.

### UI + SSE
Read first:
- `../../../src/main/java/com/example/api/WebUiSsePageEndpoint.java`
- `../../../src/main/java/com/example/api/CounterStreamEndpoint.java`

Use this when the browser needs server-to-client live updates; for product UI work, keep the browser source in `frontend/src/**` and model updates as workstream/surface events where generated SaaS semantics apply.

### UI + WebSocket
Read first:
- `../../../src/main/java/com/example/api/WebUiWebSocketPageEndpoint.java`
- `../../../src/main/java/com/example/api/PingWebSocketEndpoint.java`

Use this when the browser needs two-way communication; for product UI work, keep the browser source in `frontend/src/**` and avoid treating socket demo pages as generated SaaS UI structure.

### Public vs protected/internal routes
Read next as needed:
- `../../../docs/web-ui-pattern-selection.md`
- `../akka-http-endpoint-jwt/SKILL.md`
- `../akka-http-endpoint-request-context/SKILL.md`
- `../akka-http-endpoint-acl-internal/SKILL.md`
- `../../../src/main/java/com/example/api/InternalStatusEndpoint.java`

Default generated SaaS route boundary:
- public: static app shell/assets only, such as `/`, `/ui...`, `/assets/**`, and explicit deep-link entry routes that return the same static shell without embedding protected data
- protected: `/api/...`, SSE, and WebSocket routes that expose functional-agent availability, workstream data, surface payloads, tenant/customer data, decisions, traces, admin state, or consequential actions
- internal: service-only operational routes with explicit ACLs

Protected UI APIs must require JWT/request-context extraction, active local membership, tenant/customer filtering, capability checks, and forbidden-access behavior. Browser route guards, hidden buttons, left-rail filtering, and disabled controls are UX only.

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

- generated frontend app shell and assets: `/`, `/assets/**`, `/ui...`, or explicit app-specific/workstream entry routes
- workstream deep links: explicit entries for selected functional agent, stream item, or structured surface when refreshable URLs are required; all return the same static shell and load protected data through authorized APIs
- JSON APIs: `/api/...` with JWT/request-context and backend authorization for generated SaaS behavior
- SSE streams: explicit stream prefix such as `/counter-stream/...` or `/streams/...`; tenant/customer-scoped workstream/surface streams require authorization before subscription
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
- explicit workstream deep-link entries returning the static shell when configured
- explicit API, SSE, or WebSocket path references in the served app shell

## Anti-patterns

Avoid:
- treating browser UI work as ad hoc HTML/file-serving work
- treating the generated SaaS shell as a page-first CRUD site or chatbot-bolt-on instead of a workstream shell
- mixing UI routes and API routes under one ambiguous path family
- treating generated frontend build output as source code
- hiding the frontend source path or the served JavaScript/CSS path
- relying on browser-only behavior without a route-level integration test
- treating a complete frontend app as only an asset-hosting concern
- implementing only happy-path UI states

## Review checklist

Before finishing, verify:
- UI shell and workstream deep-link routes are explicit and do not conflict with asset wildcards
- static shell/deep-link routes do not embed protected tenant/customer data
- JSON API routes are under `/api/...`
- SSE and WebSocket routes remain explicit and separate
- frontend source paths and served JavaScript/CSS asset paths are easy to correlate
- integration tests fetch the generated app shell and CSS/JS asset routes through `httpClient`
- non-trivial UI work has been reviewed against `docs/web-ui-quality-checklist.md` and the selected style guide
- protected API, SSE, or WebSocket routes called by workstream surfaces, composer actions, or realtime subscriptions carry AuthContext, tenant/customer filtering, and forbidden-access tests
- public static asset exposure has been reviewed so it cannot leak backend secrets or protected data
