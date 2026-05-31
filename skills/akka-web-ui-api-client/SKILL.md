---
name: akka-web-ui-api-client
description: Design typed browser API clients for Akka-hosted web UIs, including fetch wrappers, DTOs, HTTP error mapping, and backend endpoint contract alignment.
---

# Akka Web UI API Client

Use this skill when a browser UI calls Akka HTTP JSON APIs.


## Generated SaaS input contract

For generated full-stack AI-first SaaS UI work, implement only after the task, app-description, spec, or backlog supplies or explicitly defers:
- owning functional agent, workstream, structured surface id/type/version, and surface action or workstream event;
- governed capability id/class, selected Akka substrate, frontend/API/realtime exposure, and required tests;
- `AuthContext`, tenant/customer scope, roles/capabilities, disabled/forbidden behavior, and backend authorization boundary;
- input/output DTOs, redaction, side effects, idempotency, policy/approval/escalation, audit/work trace fields, correlation ids, and denial/error shapes.

If these are absent for generated SaaS implementation, route back to `agent-workstream-apps` + `capability-first-backend` or repair the task brief instead of guessing from UI mechanics.

## Required reading

- `../../docs/web-ui-api-contract-patterns.md`
- `../../docs/web-ui-frontend-decomposition.md`
- `../akka-http-endpoint-component-client/SKILL.md`
- `../akka-http-endpoint-request-context/SKILL.md`
- existing API endpoint classes and tests

## API contract rules

1. Define browser-facing DTOs intentionally; do not leak internal domain records by accident.
2. Keep endpoint routes stable and human-readable, but derive them from capability and structured-surface contracts; do not use generic `/api/<resource>/...` routes as the primary generated-SaaS decomposition.
3. Represent validation errors with structured response bodies where useful.
4. Normalize browser errors into a small union such as `network`, `unauthorized`, `forbidden`, `notFound`, `validation`, and `server`.
5. Make loading and error states visible in UI state, not hidden in console logs.
6. Do not assume every response is JSON; handle empty responses explicitly.
7. For web UI integration-only tasks, leave auth headers/session behavior to security guidance; only map unauthorized/forbidden responses if the backend contract already exposes them.

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
- security/auth placeholders when already defined elsewhere
- idempotency/retry expectations
- matching endpoint integration tests

## Anti-patterns

Avoid:
- calling raw `fetch` directly from rendering/component output code
- ignoring non-2xx responses
- throwing unhandled exceptions for normal validation errors
- coupling UI directly to Akka component state classes
- letting frontend and backend DTO names drift without tests
