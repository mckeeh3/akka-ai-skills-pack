# Web UI pattern selection

Use this doc when an Akka service needs packaged browser-facing assets, not only JSON APIs.

## Default conventions in this repository

### Source-to-served asset mapping

Use these paths:

- TypeScript source: `src/main/web-ui/<example>/app.ts`
- Served JavaScript asset: `src/main/resources/static-resources/<example>/app.js`
- Packaged HTML/CSS: `src/main/resources/static-resources/<example>/...`

Build the browser assets with:

- `npm run build:web-ui`

That keeps the authored browser code and the served Akka asset paths easy to correlate by file name alone.

## Pattern selection

| Need | Use | First example |
| --- | --- | --- |
| Packaged docs, HTML, CSS, OpenAPI, or simple file serving | Narrow static-content pattern | `src/main/java/com/example/api/StaticContentEndpoint.java` |
| Co-hosted browser page calling JSON endpoints | UI + JSON API pattern | `src/main/java/com/example/api/WebUiHomeEndpoint.java` and `src/main/java/com/example/api/WebUiDataEndpoint.java` |
| Browser page consuming live one-way updates | UI + SSE pattern | `src/main/java/com/example/api/WebUiSsePageEndpoint.java` plus `src/main/java/com/example/api/CounterStreamEndpoint.java` |
| Browser page needing two-way communication | UI + WebSocket pattern | `src/main/java/com/example/api/WebUiWebSocketPageEndpoint.java` plus `src/main/java/com/example/api/PingWebSocketEndpoint.java` |
| Route exposure and backend access boundaries | Public/protected/internal route split | `src/main/java/com/example/api/InternalStatusEndpoint.java` and `skills/akka-http-endpoint-jwt/SKILL.md` |

## When plain JavaScript is enough

Plain JavaScript is sufficient when:

- the page is static or nearly static
- the browser code is only a few lines
- there is no meaningful client-side state
- the example is teaching file serving rather than browser logic

Use the narrow static-content pattern for those cases.

## When TypeScript is preferred

Use TypeScript when the browser page has real interaction logic, especially when it:

- calls JSON APIs with typed payloads
- consumes SSE events and parses JSON data
- opens WebSocket connections and manages browser-side state
- updates multiple DOM elements based on structured responses
- is likely to be copied by future agents as a reusable starting point

In this repository, TypeScript is limited to browser-facing code only. Akka components, endpoints, and tests stay in Java.

## Why this repo avoids heavy frontend frameworks in this wave

This repository is Akka-first and agent-oriented.

Avoid React, Angular, Vue, Vite, Webpack, or similar tools here because they:

- add routing and build-system noise unrelated to Akka endpoint patterns
- increase the number of files an agent must read
- make small browser examples harder to reuse mechanically
- shift attention away from route shape, Akka APIs, and test patterns

The goal in this wave is:

- plain HTML
- plain CSS
- framework-free TypeScript
- explicit Akka endpoint routes
- integration tests that stay cheap and stable

## Recommended route shape

Use route families with clear separation:

- UI shell and packaged assets: `/ui...`
- JSON API routes: `/api/...`
- SSE streams: `/counter-stream/...`, `/view-streams/...`, or another explicit stream prefix
- WebSocket routes: `/websockets/...`
- internal-only routes: `/internal-...` or another clearly non-public prefix

This keeps route intent visible from URL shape alone.

## Public UI vs protected API vs internal-only routes

Do not assume a public UI implies a public backend.

### Public UI routes

Use public ACLs for packaged pages and assets only when the page should be broadly reachable.

Examples:

- `/ui`
- `/ui/app.js`
- `/ui/sse`
- `/ui/websocket`

### Protected API routes

Use JWT-secured routes when the browser or caller must present identity or claims.

Read:

- `skills/akka-http-endpoint-jwt/SKILL.md`

Typical shape:

- public page shell under `/ui...`
- protected JSON or action endpoints under `/api/...`

### Internal-only routes

Use class-level service ACLs for routes that are not meant for internet callers.

Read:

- `skills/akka-http-endpoint-acl-internal/SKILL.md`
- `src/main/java/com/example/api/InternalStatusEndpoint.java`

Typical examples:

- internal status endpoints
- service-to-service control routes
- operational routes not intended for browsers

## OpenAPI publication

Use the static-content pattern when the service should publish a packaged `openapi.yaml`.

That belongs with static assets, not with the interactive UI examples.

## What to read next

Start with:

- `skills/akka-http-endpoint-web-ui/SKILL.md`

Then load the focused companion skill you need:

- `skills/akka-http-endpoint-static-content/SKILL.md`
- `skills/akka-http-endpoint-sse/SKILL.md`
- `skills/akka-http-endpoint-websocket/SKILL.md`
- `skills/akka-http-endpoint-jwt/SKILL.md`
- `skills/akka-http-endpoint-acl-internal/SKILL.md`
