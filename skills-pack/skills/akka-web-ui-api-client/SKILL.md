---
name: akka-web-ui-api-client
description: Design typed browser API clients for Akka-hosted web UIs, including fetch wrappers, DTOs, HTTP error mapping, and backend endpoint contract alignment.
---

# Akka Web UI API Client

Use this skill when a browser UI calls Akka HTTP JSON APIs.


## Generated SaaS input contract

Use `../references/generated-saas-input-contract.md`, `../docs/app-worker-tool-model.md`, and `../docs/app-description-to-code-compile-contract.md` as the shared gate. Do not implement generated SaaS runtime code until the responsible worker, execution harness, actor adapter, governed tool, capability, AuthContext/scope, DTO, side-effect/idempotency policy, trace/result surface, selected implementation path, and tests are present or explicitly deferred; otherwise repair the brief or route back to `agent-workstream-apps` + `capability-first-backend`.

## Required reading

- `../docs/web-ui-api-contract-patterns.md`
- `../docs/web-ui-frontend-decomposition.md`
- `../akka-http-endpoint-component-client/SKILL.md`
- `../akka-http-endpoint-request-context/SKILL.md`
- existing API endpoint classes and tests

## API contract rules

1. Define browser-facing DTOs intentionally; do not leak internal domain records by accident.
2. Keep endpoint routes stable and human-readable, but derive them from capability, governed-tool, browser-tool, and structured-surface graph contracts; do not use generic `/api/<resource>/...` routes as the primary generated-SaaS decomposition.
3. Represent validation errors with structured response bodies where useful.
4. Normalize browser errors into a small union such as `network`, `unauthorized`, `forbidden`, `notFound`, `validation`, and `server`.
5. Make loading and error states visible in UI state, not hidden in console logs.
6. Do not assume every response is JSON; handle empty responses explicitly.
7. Treat the browser client as transport and state mediation only: visible routes, action descriptors, hidden fields, cached capability ids, or frontend checks do not authorize protected work.
8. Keep secret boundaries intact: do not embed provider secrets, service credentials, raw tokens, tenant-internal authorization internals, or hidden workstream data in frontend code, static assets, logs, or browser-safe DTOs.
9. For web UI integration-only tasks, leave auth headers/session behavior to security guidance; only map unauthorized/forbidden responses if the backend contract already exposes them, and require protected calls to be rechecked by backend authorization, tenant/customer scope, redaction, and audit/work-trace behavior.

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
- owning workstream/surface graph edge, browser-tool name, governed-tool id, and capability id
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
