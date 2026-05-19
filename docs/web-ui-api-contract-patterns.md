# Web UI API contract patterns

Use this doc when a browser UI calls Akka HTTP endpoints.

## Canonical source locations

Read or create the browser API contract in:
- Akka HTTP endpoint classes under `src/main/java/**/api/`
- frontend client modules under `frontend/src/**`, commonly `frontend/src/api/**`
- endpoint integration tests under `src/test/java/**`

## Principle

Browser APIs are edge contracts. Design them for UI needs rather than leaking internal domain/component types by default. For agent workstream structured surfaces, pair this document with `structured-surface-contracts.md`: browser APIs transport surface payloads/actions/events, while linked backend capabilities remain authoritative for authorization, idempotency, approval, side effects, audit, and denial behavior.

## Route conventions

- UI shell/assets: `/ui...`
- JSON APIs: `/api/<resource>...`
- commands/actions: clear verbs under resource paths when useful, e.g. `/api/requests/{id}/approve`
- SSE streams: explicit stream prefixes, e.g. `/request-streams/...`
- WebSockets: `/websockets/...`

## DTO conventions

For structured surface APIs, use explicit surface envelopes with stable surface type/version, scoped/redacted payload data, allowed action descriptors, trace/correlation fields, and stale/reconnect metadata as needed.

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
- apply the mandatory secure SaaS foundation: JWT/request-context extraction, selected AuthContext, backend authorization, tenant/customer scoping, and audit where protected data or actions are involved; only public static asset routes are outside authenticated API authorization
- record provider-specific placeholders when WorkOS/JWT setup details are unknown, without weakening local authorization or tenancy contracts
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
- success response shape, including structured surface payloads/actions/events when applicable
- validation failure response shape
- not-found behavior where relevant
- unauthorized/forbidden behavior for protected routes, including tenant/customer mismatch and role/scope denial
- route paths referenced by packaged HTML/JS

TypeScript checks should verify frontend DTO usage against local types. If generated contracts are introduced in a future wave, keep them explicit and easy for agents to trace from endpoint DTO to frontend client type.
