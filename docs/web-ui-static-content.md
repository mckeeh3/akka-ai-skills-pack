# Web UI/static content family plan

Purpose: historical plan for the original Akka-served web UI and static content coverage wave. For current full web app integration guidance, prefer `docs/web-ui-frontend-project-integration.md`, `docs/web-ui-frontend-decomposition.md`, and `skills/akka-web-ui-apps/SKILL.md`.

## Goal

Turn the current static content example into a small, coherent family of agent-oriented patterns for Akka services that:
- serve packaged web assets
- expose UI-facing HTTP APIs
- optionally stream live updates to the browser
- keep route structure, security, and tests easy for future agents to reuse

## Why this matters

Akka applications often need more than JSON endpoints. A common real-world shape is:
- a bundled admin or demo UI
- API endpoints in the same service
- optional SSE or WebSocket updates
- packaged docs or OpenAPI assets

Right now the repository has a useful starting point:
- `skills/akka-http-endpoint-static-content/SKILL.md`
- `src/main/java/com/example/api/StaticContentEndpoint.java`
- `src/test/java/com/example/application/StaticContentEndpointIntegrationTest.java`

The next step is to make this area first-class rather than leaving it as a single example.

## Desired outcome

After this work, a future coding agent should be able to answer all of these quickly:
- how do I serve packaged HTML/CSS/JS from an Akka HTTP endpoint?
- when is it worth authoring the browser-side code in TypeScript?
- how should I structure resource paths, TypeScript source paths, and endpoint routes?
- how do I combine a bundled UI with JSON endpoints in the same service?
- how do I expose live browser updates with SSE or WebSocket?
- when do I use public UI routes versus protected API routes?
- how do I test bundled UI assets and browser-facing behavior?

## TypeScript decision

TypeScript is possible here and is not inherently too complex for an Akka project, as long as it is kept strictly bounded to the browser-facing UI layer.

### Working recommendation

Use TypeScript for the interactive web UI examples in this family, but keep the Akka service implementation in Java.

That gives us the agent-development advantages of:
- explicit browser-side types
- clearer client-side contracts
- fewer JavaScript ambiguity errors
- better maintainability for SSE/WebSocket browser logic

without turning the repository into a frontend-framework project.

### Boundaries

- Akka components, endpoints, workflows, entities, and tests remain Java-based
- TypeScript is only for browser-side code that compiles to JavaScript assets served from Akka HTTP endpoints
- for deliberately small examples, keep the TypeScript toolchain minimal and obvious
- for full web apps, use the frontend project pattern instead of this historic lightweight wave guidance

### Minimal strategy

Prefer this model:
- HTML and CSS stay under `src/main/resources/static-resources/...`
- browser logic is authored in `src/main/web-ui/...` as `.ts`
- compiled JavaScript is served as `app.js` from `static-resources`
- route-level Akka examples stay small and readable

If build automation is added in this wave, keep it intentionally small:
- one `package.json`
- one TypeScript config file
- one or two npm scripts
- no framework runtime

## Scope

## In scope

1. Packaged static assets under `src/main/resources/static-resources`
2. Minimal TypeScript source for interactive browser code under a dedicated source tree such as `src/main/web-ui`
3. HTTP endpoints that serve:
   - individual files
   - static subtrees
   - directory indexes
   - packaged `openapi.yaml`
4. Web UI pages that call Akka-backed JSON endpoints
5. Web UI pages that consume SSE endpoints
6. Web UI pages that consume WebSocket endpoints
7. Route-shape guidance for co-hosted UI and API surfaces
8. Integration tests that demonstrate the intended usage patterns
9. Agent-oriented docs and skills that route future agents to the right example
10. A minimal documented path from TypeScript source to served JavaScript assets

## Out of scope for this wave

- advanced auth flows beyond the minimum needed to explain public/private route separation
- full browser-automation suites unless a lightweight example is clearly worth the added cost
- turning this repository into a general frontend build-system reference

## Design principles

- keep examples small and executable
- keep Akka implementation code in Java and browser-interaction code in TypeScript only where it adds real value
- prefer plain HTML/CSS plus framework-free TypeScript for small interactive examples, and the frontend project pattern for full apps
- keep one main idea per example
- make route shapes obvious from file names
- make TypeScript source paths and generated asset paths easy to correlate
- reuse existing endpoint examples when practical instead of duplicating behavior
- treat tests as reference material for future agents
- avoid hidden or complicated frontend build steps

## Proposed family structure

## 1. Broad entry skill

Create a higher-level skill for the whole area so future agents do not start from the narrow static-resource example alone.

### Proposed file

- `skills/akka-http-endpoint-web-ui/SKILL.md`

### Purpose

Route agents to the right pattern among:
- static content only
- UI + JSON API
- UI + SSE
- UI + WebSocket
- OpenAPI/static docs publication
- public vs internal UI/API route splits
- plain static assets vs TypeScript-authored interactive assets

## 2. Keep and refine the existing static-content skill

### Existing file

- `skills/akka-http-endpoint-static-content/SKILL.md`

### Updates

- reposition it as the narrow file-serving companion skill
- add links to the broader web-ui skill
- add explicit notes on when to move from “static resources only” to “UI + API” patterns
- clarify that pure static/file-serving examples do not need TypeScript

## 3. Add a compact routing/reference doc

### Proposed file

- `docs/web-ui-pattern-selection.md`

### Purpose

A small comparison doc answering:
- when to bundle a UI into the Akka service
- when to keep the service as API-only
- when plain JavaScript is enough versus when TypeScript is worth it
- when to use SSE vs WebSocket for browser updates
- when to publish `openapi.yaml` as a static asset
- how to avoid route collisions between UI paths and API paths

## 4. Add minimal TypeScript tooling guidance

### Proposed files

- `package.json`
- `tsconfig.web-ui.json`
- optional lightweight note in `docs/web-ui-pattern-selection.md` or a separate short doc if needed

### Purpose

Provide the smallest possible browser-code toolchain that still gives future agents typed UI logic.

## Example backlog

## Phase 0: establish the minimal TypeScript strategy

### Goal

Decide and document the smallest TypeScript setup that supports interactive UI examples without making the repository frontend-heavy.

### Deliverables

1. Choose the TypeScript source location
2. Choose the generated JavaScript location served by Akka
3. Add minimal tool files if needed:
   - `package.json`
   - `tsconfig.web-ui.json`
4. Document the source-to-served-asset mapping in the broad web-ui skill or pattern-selection doc

### Recommended path layout

- TypeScript source: `src/main/web-ui/<example>/app.ts`
- Served HTML/CSS/JS: `src/main/resources/static-resources/<example>/...`

### Acceptance criteria

- the TypeScript approach remains understandable from file names alone
- no large frontend framework or bundler is introduced
- future agents can find both the authored `.ts` file and the served `.js` file quickly

## Phase 1: strengthen the current static-content foundation

### Deliverables

1. Refine the existing static-content example to be the canonical file-serving reference
2. Ensure examples cover:
   - single file
   - CSS/JS asset
   - subtree serving
   - directory index
   - packaged `openapi.yaml`
3. Confirm tests remain tight and route-level

### Existing assets to keep using

- `src/main/java/com/example/api/StaticContentEndpoint.java`
- `src/test/java/com/example/application/StaticContentEndpointIntegrationTest.java`

### Acceptance criteria

- a future agent can learn basic `HttpResponses.staticResource(...)` usage from one skill and one endpoint example
- tests clearly show how to fetch assets using `httpClient`

## Phase 2: UI shell + JSON API in one service

### Goal

Show the minimal co-hosted UI pattern: one packaged UI page backed by normal Akka HTTP endpoints.

### Proposed files

- `src/main/java/com/example/api/WebUiHomeEndpoint.java`
- `src/main/java/com/example/api/WebUiDataEndpoint.java`
- `src/main/web-ui/web-ui/app.ts`
- `src/main/resources/static-resources/web-ui/index.html`
- `src/main/resources/static-resources/web-ui/app.css`
- `src/main/resources/static-resources/web-ui/app.js`
- `src/test/java/com/example/application/WebUiHomeEndpointIntegrationTest.java`
- `src/test/java/com/example/application/WebUiDataEndpointIntegrationTest.java`

### Pattern to demonstrate

- `GET /ui` serves the packaged page
- `GET /ui/app.css` and `GET /ui/app.js` serve packaged assets
- `GET /api/...` returns JSON for the page
- page markup references stable asset paths
- browser logic is authored in TypeScript and compiled to the served `app.js` asset

### Acceptance criteria

- repository contains a clear example of a bundled UI page consuming JSON from the same service
- the example shows where the TypeScript source lives and which JavaScript file is actually served
- UI and API routes are clearly separated and easy to copy

## Phase 3: UI + SSE pattern

### Goal

Show how a packaged UI consumes live updates from an Akka SSE endpoint.

### Proposed files

- `src/main/java/com/example/api/WebUiSsePageEndpoint.java`
- reuse or adapt an existing SSE endpoint where possible, or add a tiny focused one
- `src/main/web-ui/web-ui-sse/app.ts`
- `src/main/resources/static-resources/web-ui-sse/index.html`
- `src/main/resources/static-resources/web-ui-sse/app.js`
- `src/test/java/com/example/application/WebUiSsePageEndpointIntegrationTest.java`

### Pattern to demonstrate

- packaged page connects to `EventSource`
- page consumes updates from an SSE endpoint already backed by Akka state or notifications
- minimal client logic authored in TypeScript and served as compiled JavaScript

### Candidate reuse

Review whether one of these can be reused or wrapped instead of adding a new stream source:
- `CounterStreamEndpoint`
- `DraftCartViewStreamEndpoint`
- `SessionMemoryAlertStreamEndpoint`
- `SessionMemoryCompactionStreamEndpoint`

### Acceptance criteria

- future agents can see a concrete browser-facing SSE example, not just a raw endpoint example
- UI and stream endpoint routes are explicit and test-backed

## Phase 4: UI + WebSocket pattern

### Goal

Show the smallest useful packaged UI that talks to a WebSocket endpoint.

### Proposed files

- `src/main/java/com/example/api/WebUiWebSocketPageEndpoint.java`
- reuse `src/main/java/com/example/api/PingWebSocketEndpoint.java` if possible
- `src/main/web-ui/web-ui-websocket/app.ts`
- `src/main/resources/static-resources/web-ui-websocket/index.html`
- `src/main/resources/static-resources/web-ui-websocket/app.js`
- `src/test/java/com/example/application/WebUiWebSocketPageEndpointIntegrationTest.java`

### Pattern to demonstrate

- packaged page opens a WebSocket to an Akka-served endpoint
- simple send/receive flow
- browser socket logic is authored in TypeScript
- route naming makes the relationship obvious

### Acceptance criteria

- repo contains one canonical browser-facing WebSocket example with minimal complexity
- pattern choice between SSE and WebSocket becomes easier to route

## Phase 5: public UI + protected/internal API split

### Goal

Document and minimally demonstrate the boundary between a public UI shell and non-public service endpoints.

### Proposed files

- update or add a small doc: `docs/web-ui-pattern-selection.md`
- possibly add one focused endpoint example if existing ACL examples are not enough

### Pattern to demonstrate

- public static UI route
- protected API route or internal-only route
- explicit reminder that UI accessibility does not imply backend openness

### Candidate reuse

- `src/main/java/com/example/api/InternalStatusEndpoint.java`
- existing JWT and ACL skills under `skills/akka-http-endpoint-*`

### Acceptance criteria

- future agents can distinguish route exposure concerns for co-hosted UI/API services
- no need for a large new security example if existing files can be cross-linked effectively

## Documentation backlog

## Required docs/skill updates

1. Create `skills/akka-http-endpoint-web-ui/SKILL.md`
2. Update `skills/akka-http-endpoint-static-content/SKILL.md`
3. Add `docs/web-ui-pattern-selection.md`
4. Add minimal TypeScript tool files if this wave includes compiled interactive examples:
   - `package.json`
   - `tsconfig.web-ui.json`
5. Update any HTTP endpoint routing docs that should link to the new family
6. Update `CONTEXT-WARMUP.md` coverage text only if the new family materially changes the stated strong areas

## Suggested content for the new broad skill

The new skill should answer:
- when to use this skill
- which narrower skills to load next
- which example to read first for each pattern
- how to choose between:
  - packaged static docs
  - packaged interactive UI
  - UI + JSON API
  - UI + SSE
  - UI + WebSocket
- when plain JavaScript is sufficient versus when TypeScript is preferred

## Testing strategy

## Repository-level expectations

Use endpoint integration tests as the default verification layer.

### Test cases to cover

1. packaged HTML is served correctly
2. CSS/JS assets are served correctly
3. subtree and directory-index serving works
4. packaged `openapi.yaml` is served correctly
5. TypeScript-authored examples have an obvious source-to-served-asset mapping
6. UI page references the expected asset and API paths
7. JSON endpoint returns expected browser-consumable payloads
8. SSE UI page is wired to the expected stream path
9. WebSocket UI page is wired to the expected socket path
10. public/private path split is explicit where demonstrated

### Nice-to-have, if inexpensive

- content-type assertions where the test harness makes them easy
- a tiny browser-level smoke check only if it stays cheap and stable

## Implementation order

1. Establish the minimal TypeScript strategy and file layout
2. Harden the static-content example already in the repo
3. Add the broad `web-ui` routing skill
4. Add UI shell + JSON API example
5. Add UI + SSE example
6. Add UI + WebSocket example
7. Add the route/security comparison doc
8. Update cross-links and coverage references

## Success criteria for this family

This family is successful when:
- the repo has a clearly identifiable entry point for Akka-served web UI work
- static content is no longer represented by a single isolated example
- TypeScript is used only where it improves interactive browser examples and does not dominate the Akka-focused material
- there is at least one small, canonical example for each of these patterns:
  - static assets only
  - UI + JSON API
  - UI + SSE
  - UI + WebSocket
- tests show route-level usage patterns for all examples
- agents can choose a route shape and reference example with minimal ambiguity
- future agents can understand the TypeScript source path and the served JavaScript path without extra explanation

## Follow-on dependency

Once this family is complete, the next major priority should be:
- service setup, bootstrap, and configuration

That topic naturally complements bundled UI work because real web-facing services often need clearer examples for environment config, outbound clients, security boundaries, and deployment readiness.
