# Web UI API contract patterns

Use this doc when a browser UI calls Akka HTTP endpoints.

## Canonical reference example

Read:
- `src/main/java/com/example/api/FrontendReferenceApiEndpoint.java`
- `src/main/web-ui/frontend-reference/api.ts`
- `src/test/java/com/example/application/FrontendReferenceWebUiIntegrationTest.java`

## Principle

Browser APIs are edge contracts. Design them for UI needs rather than leaking internal domain/component types by default.

## Route conventions

- UI shell/assets: `/ui...`
- JSON APIs: `/api/<resource>...`
- commands/actions: clear verbs under resource paths when useful, e.g. `/api/requests/{id}/approve`
- SSE streams: explicit stream prefixes, e.g. `/request-streams/...`
- WebSockets: `/websockets/...`

## DTO conventions

Use endpoint-facing records/classes for:
- list rows
- detail views
- form option lists
- command requests
- validation errors
- status summaries

Avoid returning rich internal state when the UI only needs a row summary.

## Error response shapes

Prefer structured validation errors for forms:

```json
{
  "message": "Fix the highlighted fields.",
  "fields": [
    { "field": "title", "message": "Title is required." }
  ]
}
```

The browser API client should normalize:
- `400` validation or malformed input
- `401` unauthorized
- `403` forbidden
- `404` not found
- `409` conflict/stale update
- `5xx` server failure
- network failure
- malformed response

## Backend endpoint checklist

For each browser API endpoint:
- use `@HttpEndpoint` and explicit `@Acl`
- record exposure assumptions or placeholders; choose JWT/internal security details only when security guidance is in scope
- validate request bodies at the edge where HTTP-specific feedback is needed
- call Akka components through `ComponentClient` when business state is involved
- map domain/component failures to HTTP-oriented responses
- test with `httpClient`

## Frontend client checklist

For each API call:
- define request and response TypeScript types
- normalize non-2xx responses
- return `ApiResult<T>` or an equivalent explicit result type
- update UI state for loading, success, validation, unauthorized, forbidden, conflict, and server failure
- avoid raw `fetch` calls outside `api.ts`

## Contract test expectations

Endpoint integration tests should verify:
- success response shape
- validation failure response shape
- not-found behavior where relevant
- unauthorized/forbidden behavior when security is in scope or already defined
- route paths referenced by packaged HTML/JS

TypeScript checks should verify frontend DTO usage against local types. If generated contracts are introduced in a future wave, keep them lightweight and explicit.
