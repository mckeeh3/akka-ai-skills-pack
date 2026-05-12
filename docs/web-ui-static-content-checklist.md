# Web UI/static content implementation checklist

Purpose: historical checklist for the original Akka-served web UI asset plan. Current browser UI work should use the React/Vite/TypeScript frontend project path.

Use this only as archived implementation context for the earlier web UI/asset-serving family.

## Execution order

## Phase 0 — establish the minimal TypeScript toolchain

Goal: add the smallest possible TypeScript setup for browser-side examples.

### Create first

- [ ] `package.json`
- [ ] `tsconfig.web-ui.json`
- [ ] `docs/web-ui-pattern-selection.md`

### Tasks

- [ ] Add `package.json` with only the minimum needed for browser TypeScript compilation
- [ ] Add `tsconfig.web-ui.json` scoped to the web UI examples
- [ ] In `docs/web-ui-pattern-selection.md`, document:
  - TypeScript source path convention
  - generated JavaScript asset path convention
  - when plain JavaScript is sufficient
  - when TypeScript is preferred
  - why lightweight examples avoid heavy frontend frameworks, and when full apps should use the frontend project pattern

### Expected result

- future agents can identify the TypeScript source tree immediately
- future agents can identify the served JavaScript asset immediately
- the repo has a stable convention before example files are added

---

## Phase 1 — strengthen the asset-serving foundation

Goal: keep the existing asset-serving example as the narrow canonical file-serving reference.

### Update first

- [ ] the removed dedicated asset-serving skill
- [ ] `removed asset-serving endpoint source`
- [ ] `removed asset-serving endpoint integration test`

### Add or confirm resource files

- [ ] `removed packaged HTML example`
- [ ] `removed packaged CSS example`
- [ ] `src/main/resources/static-resources/http-endpoint/help.txt`
- [ ] `src/main/resources/static-resources/http-endpoint/guide/index.html`
- [ ] `removed packaged OpenAPI example`

### Tasks

- [ ] Update the asset-serving skill to position it as the narrow file-serving skill
- [ ] Add a link from that skill to the broader web-ui skill that will be created later
- [ ] Confirm the endpoint demonstrates:
  - serving a single HTML file
  - serving a CSS asset
  - serving a subtree
  - serving a directory index
  - serving a packaged OpenAPI file
- [ ] Confirm the integration test demonstrates route-level access through `httpClient`

### Expected result

- there is one obvious example for `HttpResponses.staticResource(...)`
- pure static/file-serving guidance stays separate from interactive UI guidance

---

## Phase 2 — add the broad web-ui routing skill

Goal: provide one entry point for all Akka-served web UI patterns.

### Create first

- [ ] `skills/akka-http-endpoint-web-ui/SKILL.md`

### Update after creation

- [ ] the removed dedicated asset-serving skill
- [ ] `docs/web-ui-pattern-selection.md`

### Tasks

- [ ] Create the broad web-ui skill with routing guidance for:
  - asset delivery only
  - UI + JSON API
  - UI + SSE
  - UI + WebSocket
  - public vs internal route split
  - plain static assets vs TypeScript-authored interactive assets
- [ ] Cross-link the new broad skill to the narrow asset-serving skill
- [ ] Cross-link the pattern-selection doc to both skills

### Expected result

- future agents have one obvious starting point for web UI work
- the asset-serving skill remains focused instead of growing into a catch-all

---

## Phase 3 — UI shell + JSON API example

Goal: add the minimal co-hosted UI example with TypeScript-authored browser logic.

### Create first

- [ ] `src/main/java/com/example/api/WebUiHomeEndpoint.java`
- [ ] `src/main/java/com/example/api/WebUiDataEndpoint.java`
- [ ] `src/main/web-ui/web-ui/app.ts`
- [ ] `src/main/resources/static-resources/web-ui/index.html`
- [ ] `src/main/resources/static-resources/web-ui/app.css`
- [ ] `src/main/resources/static-resources/web-ui/app.js`
- [ ] `src/test/java/com/example/application/WebUiHomeEndpointIntegrationTest.java`
- [ ] `src/test/java/com/example/application/WebUiDataEndpointIntegrationTest.java`

### Tasks

- [ ] Create `WebUiHomeEndpoint.java` to serve the packaged UI page and assets
- [ ] Create `WebUiDataEndpoint.java` to return a small browser-consumable JSON payload
- [ ] Author browser logic in `src/main/web-ui/web-ui/app.ts`
- [ ] Compile or generate the served asset at `src/main/resources/static-resources/web-ui/app.js`
- [ ] For lightweight examples, keep `index.html` framework-free and explicit about API and asset paths
- [ ] Add integration tests for:
  - `GET /ui`
  - `GET /ui/app.css`
  - `GET /ui/app.js`
  - JSON API route(s)

### Expected result

- the repo shows a complete minimal UI + API pattern
- future agents can see both the TypeScript source and the served JavaScript asset path

---

## Phase 4 — UI + SSE example

Goal: show a packaged UI page consuming SSE from an Akka endpoint.

### Create first

- [ ] `src/main/java/com/example/api/WebUiSsePageEndpoint.java`
- [ ] `src/main/web-ui/web-ui-sse/app.ts`
- [ ] `src/main/resources/static-resources/web-ui-sse/index.html`
- [ ] `src/main/resources/static-resources/web-ui-sse/app.js`
- [ ] `src/test/java/com/example/application/WebUiSsePageEndpointIntegrationTest.java`

### Reuse or adapt one existing stream endpoint

- [ ] `src/main/java/com/example/api/CounterStreamEndpoint.java` or
- [ ] `src/main/java/com/example/api/DraftCartViewStreamEndpoint.java` or
- [ ] `src/main/java/com/example/api/SessionMemoryAlertStreamEndpoint.java` or
- [ ] `src/main/java/com/example/api/SessionMemoryCompactionStreamEndpoint.java`

### Tasks

- [ ] Choose the smallest existing SSE endpoint worth reusing
- [ ] Add `WebUiSsePageEndpoint.java` to serve the packaged page and asset(s)
- [ ] Author browser SSE logic in `src/main/web-ui/web-ui-sse/app.ts`
- [ ] Compile or generate the served asset at `src/main/resources/static-resources/web-ui-sse/app.js`
- [ ] Add an integration test proving the page route is wired to the intended SSE path

### Expected result

- future agents can connect a browser page to an Akka SSE endpoint without inference-heavy reading

---

## Phase 5 — UI + WebSocket example

Goal: show a packaged UI page consuming a WebSocket endpoint.

### Create first

- [ ] `src/main/java/com/example/api/WebUiWebSocketPageEndpoint.java`
- [ ] `src/main/web-ui/web-ui-websocket/app.ts`
- [ ] `src/main/resources/static-resources/web-ui-websocket/index.html`
- [ ] `src/main/resources/static-resources/web-ui-websocket/app.js`
- [ ] `src/test/java/com/example/application/WebUiWebSocketPageEndpointIntegrationTest.java`

### Reuse candidate

- [ ] `src/main/java/com/example/api/PingWebSocketEndpoint.java`

### Tasks

- [ ] Reuse or lightly adapt `PingWebSocketEndpoint.java` if that keeps the example smaller
- [ ] Add `WebUiWebSocketPageEndpoint.java` to serve the packaged page and asset(s)
- [ ] Author browser WebSocket logic in `src/main/web-ui/web-ui-websocket/app.ts`
- [ ] Compile or generate the served asset at `src/main/resources/static-resources/web-ui-websocket/app.js`
- [ ] Add an integration test proving the page route references the intended WebSocket path

### Expected result

- the repo has one clear browser-facing WebSocket example with minimal moving parts

---

## Phase 6 — public UI + protected/internal API split

Goal: document the route-exposure boundary for co-hosted UI and API services.

### Update first

- [ ] `docs/web-ui-pattern-selection.md`

### Candidate files to reference or reuse

- [ ] `src/main/java/com/example/api/InternalStatusEndpoint.java`
- [ ] `skills/akka-http-endpoint-acl-internal/SKILL.md`
- [ ] `skills/akka-http-endpoint-jwt/SKILL.md`

### Optional new file only if needed

- [ ] `src/main/java/com/example/api/WebUiSecurityExampleEndpoint.java`

### Tasks

- [ ] Add a route-split section to `docs/web-ui-pattern-selection.md`
- [ ] Document the difference between:
  - public UI shell routes
  - protected API routes
  - internal-only routes
- [ ] Only add a new endpoint example if existing ACL/JWT examples are not enough to make the pattern obvious

### Expected result

- future agents do not assume that a public UI implies a public backend

---

## Phase 7 — final cross-links and coverage updates

Goal: make the new family discoverable from the repo’s routing layer.

### Update first

- [ ] `CONTEXT-WARMUP.md`
- [ ] `skills/README.md`
- [ ] any affected HTTP endpoint skill docs

### Optional updates if they materially help routing

- [ ] `docs/agent-coverage-matrix.md`
- [ ] other HTTP endpoint reference docs in `docs/`

### Tasks

- [ ] Update `CONTEXT-WARMUP.md` only if the web-ui family materially changes the stated strong local coverage
- [ ] Update `skills/README.md` so future agents can find the new broad skill quickly
- [ ] Add links from related HTTP endpoint skills where useful

### Expected result

- the new web-ui asset-serving family is easy to discover from the repo’s normal routing paths

---

## First files to create in exact order

If starting immediately, create these first in this order:

1. `docs/web-ui-pattern-selection.md`
2. `package.json`
3. `tsconfig.web-ui.json`
4. `skills/akka-http-endpoint-web-ui/SKILL.md`
5. `src/main/java/com/example/api/WebUiHomeEndpoint.java`
6. `src/main/java/com/example/api/WebUiDataEndpoint.java`
7. `src/main/web-ui/web-ui/app.ts`
8. `src/main/resources/static-resources/web-ui/index.html`
9. `src/main/resources/static-resources/web-ui/app.css`
10. `src/main/resources/static-resources/web-ui/app.js`
11. `src/test/java/com/example/application/WebUiHomeEndpointIntegrationTest.java`
12. `src/test/java/com/example/application/WebUiDataEndpointIntegrationTest.java`

After those are in place, move to the SSE page example, then the WebSocket page example.

## Definition of done

The checklist is complete when:
- there is one broad web-ui skill and one narrow asset-delivery skill
- there is a documented minimal TypeScript strategy
- there is one canonical example each for:
  - asset delivery only
  - UI + JSON API
  - UI + SSE
  - UI + WebSocket
- tests exist for route-level usage of each example
- path conventions for `.ts` source and served `.js` assets are explicit and stable
