---
name: akka-http-endpoints
description: Orchestrate Akka Java SDK HTTP endpoint work across request mapping, component calls, request context, browser UI hosting, streaming, and integration testing. Use when the task spans more than one HTTP endpoint concern.
---

# Akka HTTP Endpoints

Use this as the top-level skill for Akka Java SDK HTTP endpoint work when the endpoint is already selected as an exposure surface for one or more backend capabilities.

For broad product, PRD, feature, or UI/API requests, route through `capability-first-backend` and `akka-solution-decomposition` before implementing routes. Do not start from endpoint paths or CRUD screens when capability authority, scope, side effects, approval, and audit semantics are still unclear.

## Goal

Generate or review HTTP endpoint code that is:
- correct for Akka SDK 3.4+
- explicit about public API request and response shapes
- safe at the edge with clear validation and HTTP error mapping
- easy for AI agents to extend without reading unrelated files
- backed by integration tests that use `httpClient`

## AI-first substrate role

In AI-first SaaS implementations, use HTTP endpoints as browser-facing control and observation surfaces for goals, plans, approvals, exceptions, policy/governance changes, work traces, digests, and outcome dashboards.

Keep endpoints as edge adapters: validate request shape, map authentication/authorization context, translate to component commands or queries, and return HTTP-appropriate responses. Do not encode agent authority, policy decisions, or approval outcomes only in endpoint code; pass them to entities, workflows, agents, views, or timed actions that can preserve durable state and traces.

Security context is expected input for generated SaaS endpoints. Protected browser, service, gRPC, and MCP-style API surfaces must default to JWT/request-context extraction plus a backend authorization helper that verifies active account, membership, tenant/customer scope, roles/capabilities, and forbidden-access behavior. Only explicit public static asset routes may omit authentication; public read APIs must still be a deliberate product decision with audit/privacy review.

Pair AI-first HTTP endpoints with:
- `akka-web-ui-apps` and focused web UI skills for command center, decision-card, governance, digest, and audit surfaces
- `akka-http-endpoint-sse` or `akka-http-endpoint-websocket` for supervision streams and live work-trace updates
- `akka-http-endpoint-jwt` and `akka-http-endpoint-request-context` when identity, tenant, permission, or reviewer context affects behavior
- component-client endpoint skills for launching goals/plans, approving decisions, querying views, and invoking bounded agents


## Capability-first exposure rule

Treat every HTTP route as a selected browser/API exposure surface for a named governed-tool inside a backend capability, not as the capability itself. Before adding or changing a route, identify the capability id, governed-tool id, exposure label (browser-tool, internal API, service API, or MCP-facing adapter), allowed actors/callers, `AuthContext`, tenant/customer scope, input/output schema, side effects, idempotency, approval policy, audit/trace obligations, and tests.

For protected routes, preserve the capability contract at the edge: authenticate the caller, resolve or receive the selected tenant/customer context, authorize the required role/scope/capability, validate and redact HTTP payloads, map denials to explicit `401`/`403` behavior, and record required audit/work-trace events before calling components. Browser actions, API paths, hidden fields, and route names are not authorization controls.

When the same governed-tool/capability is also exposed through UI, agent-tools, workflows, gRPC, MCP, timers, or consumers, keep authority, validation, idempotency, approval, and audit semantics identical across surfaces. Consequential HTTP actions should call the workflow/entity/approval substrate that enforces policy instead of committing side effects only in endpoint code.

## Required reading before coding

Read these first if present:
- `akka-context/sdk/http-endpoints.html.md`
- `akka-context/sdk/access-control.html.md`
- `akka-context/sdk/component-and-service-calls.html.md`
- `akka-context/sdk/ai-coding-assistant-guidelines.html.md`
- existing project endpoints under `src/main/java/**/api/*Endpoint.java`
- matching endpoint tests under `src/test/java/**`

In this repository, prefer these examples:
- `../../examples/akka-components/src/main/java/com/example/api/GreetingEndpoint.java`
- `../../examples/akka-components/src/main/java/com/example/api/WebUiHomeEndpoint.java`
- `../../examples/akka-components/src/main/java/com/example/api/LowLevelHttpEndpoint.java`
- `../../examples/akka-components/src/main/java/com/example/api/ProxyGreetingEndpoint.java`
- `../../examples/akka-components/src/main/java/com/example/api/PingWebSocketEndpoint.java`
- `../../examples/akka-components/src/main/java/com/example/api/CounterStreamEndpoint.java`
- `../../examples/akka-components/src/main/java/com/example/api/DraftCartViewStreamEndpoint.java`
- `../../examples/akka-components/src/main/java/com/example/api/RequestHeadersEndpoint.java`
- `../../examples/akka-components/src/main/java/com/example/api/SecureGreetingEndpoint.java`
- `../../examples/akka-components/src/main/java/com/example/api/InternalStatusEndpoint.java`
- `../../examples/akka-components/src/main/java/com/example/api/ShoppingCartEndpoint.java`
- `../../examples/akka-components/src/main/java/com/example/api/DraftCartEndpoint.java`
- `../../examples/akka-components/src/main/java/com/example/api/OrderEndpoint.java`
- `../../examples/akka-components/src/main/java/com/example/api/PurchaseOrderEndpoint.java`
- `../../examples/akka-components/src/test/java/com/example/application/GreetingEndpointIntegrationTest.java`
- `../../examples/akka-components/src/test/java/com/example/application/WebUiHomeEndpointIntegrationTest.java`
- `../../examples/akka-components/src/test/java/com/example/application/LowLevelHttpEndpointIntegrationTest.java`
- `../../examples/akka-components/src/test/java/com/example/application/ProxyGreetingEndpointIntegrationTest.java`
- `../../examples/akka-components/src/test/java/com/example/application/PingWebSocketEndpointIntegrationTest.java`
- `../../examples/akka-components/src/test/java/com/example/application/CounterStreamEndpointIntegrationTest.java`
- `../../examples/akka-components/src/test/java/com/example/application/DraftCartViewStreamEndpointIntegrationTest.java`
- `../../examples/akka-components/src/test/java/com/example/application/RequestHeadersEndpointIntegrationTest.java`
- `../../examples/akka-components/src/test/java/com/example/application/SecureGreetingEndpointIntegrationTest.java`
- `../../examples/akka-components/src/test/java/com/example/application/InternalStatusEndpointIntegrationTest.java`
- `../../examples/akka-components/src/test/java/com/example/application/ShoppingCartIntegrationTest.java`
- `../../examples/akka-components/src/test/java/com/example/application/OrderEndpointIntegrationTest.java`
- `../../examples/akka-components/src/test/java/com/example/application/PurchaseOrderEndpointIntegrationTest.java`

## Companion skills

Load the companion skill that matches the current task:

- `akka-http-endpoint-component-client`
  - endpoints that call entities, views, agents, workflows, or other components through `ComponentClient`
- `akka-http-endpoint-request-context`
  - endpoints extending `AbstractHttpEndpoint` to read query params, headers, or other request metadata
- `akka-http-endpoint-low-level`
  - low-level responses, `HttpEntity.Strict`, and advanced request/response handling
- `akka-http-endpoint-http-client-provider`
  - endpoint-to-HTTP-service delegation through `HttpClientProvider`
- `akka-http-endpoint-sse`
  - server-sent events, reconnect support, view-backed SSE, and streaming endpoint tests
- `akka-http-endpoint-websocket`
  - WebSocket endpoint methods using `@WebSocket` and `Flow`
- `akka-http-endpoint-jwt`
  - bearer token validation and claim access through `requestContext().getJwtClaims()`
- `akka-http-endpoint-acl-internal`
  - internal-only service ACLs, method-level overrides, and principal inspection
- `akka-http-endpoint-testing`
  - `TestKitSupport` endpoint integration tests using `httpClient`

Config- or deployment-oriented HTTP endpoint topics are also in scope, but some are primarily routed through official docs rather than large local examples:
- TLS certificates for internet clients
- HTTP Basic authentication for invocation
- OpenAPI Maven plugin configuration
- fully streaming `HttpRequest` handlers using `Materializer`

Use this top-level skill plus the official docs when those are the main concern.

## Default package layout

Use:
- `com.<org>.<app>.domain`
- `com.<org>.<app>.application`
- `com.<org>.<app>.api`

Rules:
- HTTP endpoints belong in `api`
- API request and response records belong in the endpoint class or the `api` package
- domain records stay in `domain`
- component orchestration stays in `application`, not in the endpoint

## Core rules

1. An HTTP endpoint has `@HttpEndpoint(...)` and must not have `@Component`.
2. Add `@Acl(...)` explicitly. Without ACLs the endpoint is not accessible.
3. Prefer endpoint-specific API records over exposing internal domain state directly.
4. Use constructor injection for `ComponentClient` only when the endpoint actually calls other components.
5. Extend `AbstractHttpEndpoint` only when request context access is needed.
6. Request bodies may be endpoint-local records/classes, `String`, `List<T>`, `HttpEntity.Strict`, or in advanced cases `HttpRequest`.
7. Return values may be `String`, API records/classes, `void`/empty responses, `HttpResponse`, and in official Akka APIs also `CompletionStage<T>`.
8. Keep request validation explicit and return HTTP-oriented failures at the edge.
9. When returning `HttpResponse`, prefer `HttpResponses.ok(...)`, `created(...)`, `badRequest(...)`, or `notFound(...)` instead of throwing generic exceptions.
10. Keep production endpoint code synchronous with `.invoke()` unless a low-level streaming HTTP case truly requires otherwise.
11. Keep business rules in domain or components, not in the endpoint.
12. Test endpoints through `httpClient`, not `componentClient`.
13. For generated SaaS APIs, extract or receive `AuthContext` and authorize every protected route against tenant/customer scope and required capability before calling components.
14. Return explicit `401`/`403` behavior for missing authentication, disabled users, forbidden roles/scopes, and cross-tenant/customer attempts; include audit events for denials and consequential access when the foundation requires it.
15. Keep public static frontend assets separate from protected `/api/...` routes; do not let an app-shell route imply API authorization.

## Decision guide

Choose one of these modes before coding:

### 1. Pure edge mapping endpoint
Use when the endpoint mainly maps HTTP requests to API responses and does not need component calls.

Repository example:
- `GreetingEndpoint`

### 2. Component-calling endpoint
Use when the endpoint translates HTTP requests into entity or view calls and maps replies into API types.

Repository examples:
- `ShoppingCartEndpoint`
- `DraftCartEndpoint`
- `OrderEndpoint`
- `PurchaseOrderEndpoint`

### 3. Request-context endpoint
Use when endpoint logic depends on query parameters, request headers, JWT claims, or SSE reconnect metadata.

Repository examples:
- `GreetingEndpoint`
- `RequestHeadersEndpoint`

### 4. Low-level HTTP endpoint
Use when the endpoint needs `HttpResponse`, `HttpEntity.Strict`, or other lower-level HTTP model control.

Repository example:
- `LowLevelHttpEndpoint`

### 5. HTTP client provider endpoint
Use when the endpoint delegates to another HTTP service through `HttpClientProvider`.

Repository example:
- `ProxyGreetingEndpoint`

### 6. SSE endpoint
Use when the endpoint must stream a sequence of updates and optionally resume from the last seen event id.

Repository examples:
- `CounterStreamEndpoint`
- `DraftCartViewStreamEndpoint`
- `ShoppingCartEndpoint#notifications`
- `DraftCartEndpoint#notifications`

### 7. WebSocket endpoint
Use when the endpoint must support bidirectional streaming over a socket.

Repository example:
- `PingWebSocketEndpoint`

### 8. JWT-secured endpoint
Use when the endpoint requires bearer token validation and claim-aware behavior. This is the default for generated SaaS browser APIs under `/api/...`; pair JWT validation with local `/api/me`/membership authorization instead of treating token presence as permission.

Repository example:
- `SecureGreetingEndpoint`

### 9. Internal-only ACL endpoint
Use when the endpoint should be callable only by other services or needs class-level ACL defaults with method-level overrides.

Repository example:
- `InternalStatusEndpoint`

## Final review checklist

Before finishing, verify:
- `@HttpEndpoint` is present
- `@Acl` is present
- no `@Component` annotation is used on the endpoint
- API records are endpoint-facing, not leaked domain types
- `AbstractHttpEndpoint` is used only if request context is needed
- path parameters match method parameters in order
- request body parameter is last when combined with path parameters
- `HttpResponse` methods return explicit HTTP errors when relevant
- tests use `httpClient`
- protected routes have JWT/request-context handling and backend authorization checks
- tenant/customer ids are filtered or rejected server-side for every scoped command, query, stream, and component call
- forbidden, disabled-user, role/scope denial, cross-tenant/customer, and audit expectations are covered or explicitly delegated to foundation tests

## Response style

When answering coding tasks:
- name the endpoint path prefix explicitly
- call out whether the endpoint is pure edge logic or calls other components
- state how validation failures are mapped to HTTP behavior
- list the concrete example files used as references
