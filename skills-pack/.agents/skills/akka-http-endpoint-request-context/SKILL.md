---
name: akka-http-endpoint-request-context
description: Implement Akka Java SDK HTTP endpoints that use AbstractHttpEndpoint and requestContext() for query params, headers, and other request metadata. Use when endpoint behavior depends on HTTP request context.
---

# Akka HTTP Endpoint Request Context

Use this skill when an endpoint needs request metadata.


## Capability-first exposure rule

Treat every HTTP route as an `api_call` or browser-facing actor adapter for a named governed tool inside a backend capability, not as the governed tool or capability itself. Before adding or changing a route, identify the responsible worker/harness, actor adapter, governed tool id, capability id, allowed actors/callers, `AuthContext`, tenant/customer scope, input/output schema, side effects, idempotency, approval policy, audit/trace obligations, selected Akka implementation path, and tests.

For protected routes, preserve the capability contract at the edge: authenticate the caller, resolve or receive the selected tenant/customer context, authorize the required role/scope/capability, validate and redact HTTP payloads, map denials to explicit `401`/`403` behavior, and record required audit/work-trace events before calling components. Browser actions, API paths, hidden fields, and route names are not authorization controls.

When the same capability is also exposed through UI, agent tools, workflows, gRPC, MCP, timers, or consumers, keep authority, validation, idempotency, approval, and audit semantics identical across surfaces. Consequential HTTP actions should call the workflow/entity/approval substrate that enforces policy instead of committing side effects only in endpoint code.

## Required reading

Read these first if present:
- `akka-context/sdk/http-endpoints.html.md`

## Use this pattern when

Generated SaaS APIs should treat request context as the source for authenticated caller metadata, correlation/audit metadata, and selected tenant/customer context before invoking protected application components.

- the endpoint behavior depends on query parameters
- the endpoint reads request headers, JWT claims, principals, or tracing metadata
- the endpoint should stay in the HTTP layer without unnecessary constructor injection
- the endpoint needs SSE reconnect metadata via request context

## Core pattern

1. Extend `akka.javasdk.http.AbstractHttpEndpoint`.
2. Access request metadata through `requestContext()`.
3. Keep request parsing small and explicit.
4. Return HTTP-oriented errors when required request context is missing or invalid.
5. Keep domain rules outside the endpoint.
6. For protected SaaS APIs, turn JWT/principal/header metadata into an `AuthContext` and pass only authorized actor/scope data to downstream components.
7. Reject missing, inconsistent, or forbidden tenant/customer context at the edge before running view queries, streams, workflow actions, agent tools, or component commands.
8. Preserve correlation ids, actor ids, tenant/customer ids, and denial reasons needed for AdminAuditEvent or work-trace creation.

## Repository examples

- current snapshot examples: `MeEndpoint`, `WorkstreamEndpoint`, or `AdminEndpoint` as applicable
  - reads query parameters through `requestContext().queryParams()`
  - combines path parameters and request body parameters
  - returns `HttpResponses.badRequest(...)` for invalid edge input
- a domain-specific request-context endpoint (or `MeEndpoint` for auth context)
  - reads required and optional headers through `requestContext().requestHeader(...)`
  - inspects principals through `requestContext().getPrincipals()`
- JWT-secured generated-SaaS API endpoints such as `MeEndpoint` and protected `/api/...` routes
  - reads JWT claims through `requestContext().getJwtClaims()` after `@JWT` validation

## Common request-context uses

- query parameters: filtering, localization, paging, formatting
- request headers: correlation ids, tenant routing, lightweight caller context
- SSE reconnect metadata: `lastSeenSseEventId()`
- JWT or principal metadata when access control is configured
- selected tenant/customer context and membership/capability hints for backend authorization
- correlation, request id, or audit trace metadata for protected operations

## Anti-patterns

Avoid:
- injecting request context through the constructor
- extending `AbstractHttpEndpoint` when the endpoint does not use request context
- hiding request parsing in unrelated helpers
- turning HTTP-specific context into domain dependencies

## Review checklist

Before finishing, verify:
- endpoint extends `AbstractHttpEndpoint`
- request metadata is read through `requestContext()`
- missing or invalid request metadata is handled explicitly
- path params, query params, and body mapping stay readable
- tests cover the request-context-dependent behavior
- protected endpoints fail closed when required AuthContext, tenant/customer scope, or active membership is absent
- forbidden/cross-tenant request-context combinations are covered in endpoint or foundation tests
