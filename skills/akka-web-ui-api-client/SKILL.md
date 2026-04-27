---
name: akka-web-ui-api-client
description: Design typed browser API clients for Akka-hosted web UIs, including fetch wrappers, DTOs, HTTP error mapping, and backend endpoint contract alignment.
---

# Akka Web UI API Client

Use this skill when a browser UI calls Akka HTTP JSON APIs.

## Required reading

- `../../../docs/web-ui-api-contract-patterns.md`
- `../../../docs/web-ui-frontend-decomposition.md`
- `../akka-http-endpoint-component-client/SKILL.md`
- `../akka-http-endpoint-request-context/SKILL.md`
- existing API endpoint classes and tests

## API contract rules

1. Define browser-facing DTOs intentionally; do not leak internal domain records by accident.
2. Keep endpoint routes stable and human-readable: `/api/<resource>/...`.
3. Represent validation errors with structured response bodies where useful.
4. Normalize browser errors into a small union such as `network`, `unauthorized`, `forbidden`, `notFound`, `validation`, and `server`.
5. Make loading and error states visible in UI state, not hidden in console logs.
6. Do not assume every response is JSON; handle empty responses explicitly.
7. Keep auth headers/session behavior explicit when protected endpoints are used.

## Browser client shape

Prefer:

```ts
export type ApiResult<T> =
  | { ok: true; value: T }
  | { ok: false; error: ApiError };
```

Use small functions:

```ts
export async function getSummary(): Promise<ApiResult<SummaryResponse>>;
export async function submitRequest(input: SubmitRequest): Promise<ApiResult<RequestDetails>>;
```

## Endpoint alignment checklist

For every browser API call, verify:
- HTTP method and route
- request DTO
- success response DTO
- validation error shape
- auth requirements
- idempotency/retry expectations
- matching endpoint integration tests

## Anti-patterns

Avoid:
- calling raw `fetch` from render functions
- ignoring non-2xx responses
- throwing unhandled exceptions for normal validation errors
- coupling UI directly to Akka component state classes
- letting frontend and backend DTO names drift without tests
