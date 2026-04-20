---
name: akka-http-endpoints
description: Orchestrate Akka Java SDK HTTP endpoint work across request mapping, component calls, request context, static content, and integration testing. Use when the task spans more than one HTTP endpoint concern.
---

# Akka HTTP Endpoints

Use this as the top-level skill for Akka Java SDK HTTP endpoint work.

## Goal

Generate or review HTTP endpoint code that is:
- correct for Akka SDK 3.4+
- explicit about public API request and response shapes
- safe at the edge with clear validation and HTTP error mapping
- easy for AI agents to extend without reading unrelated files
- backed by integration tests that use `httpClient`

## Required reading before coding

Read these first if present:
- `akka-context/sdk/http-endpoints.html.md`
- `akka-context/sdk/access-control.html.md`
- `akka-context/sdk/component-and-service-calls.html.md`
- `akka-context/sdk/ai-coding-assistant-guidelines.html.md`
- existing project endpoints under `src/main/java/**/api/*Endpoint.java`
- matching endpoint tests under `src/test/java/**`

In this repository, prefer these examples:
- `../../../src/main/java/com/example/api/GreetingEndpoint.java`
- `../../../src/main/java/com/example/api/StaticContentEndpoint.java`
- `../../../src/main/java/com/example/api/LowLevelHttpEndpoint.java`
- `../../../src/main/java/com/example/api/ProxyGreetingEndpoint.java`
- `../../../src/main/java/com/example/api/PingWebSocketEndpoint.java`
- `../../../src/main/java/com/example/api/CounterStreamEndpoint.java`
- `../../../src/main/java/com/example/api/DraftCartViewStreamEndpoint.java`
- `../../../src/main/java/com/example/api/RequestHeadersEndpoint.java`
- `../../../src/main/java/com/example/api/SecureGreetingEndpoint.java`
- `../../../src/main/java/com/example/api/InternalStatusEndpoint.java`
- `../../../src/main/java/com/example/api/ShoppingCartEndpoint.java`
- `../../../src/main/java/com/example/api/DraftCartEndpoint.java`
- `../../../src/main/java/com/example/api/OrderEndpoint.java`
- `../../../src/main/java/com/example/api/PurchaseOrderEndpoint.java`
- `../../../src/test/java/com/example/application/GreetingEndpointIntegrationTest.java`
- `../../../src/test/java/com/example/application/StaticContentEndpointIntegrationTest.java`
- `../../../src/test/java/com/example/application/LowLevelHttpEndpointIntegrationTest.java`
- `../../../src/test/java/com/example/application/ProxyGreetingEndpointIntegrationTest.java`
- `../../../src/test/java/com/example/application/PingWebSocketEndpointIntegrationTest.java`
- `../../../src/test/java/com/example/application/CounterStreamEndpointIntegrationTest.java`
- `../../../src/test/java/com/example/application/DraftCartViewStreamEndpointIntegrationTest.java`
- `../../../src/test/java/com/example/application/RequestHeadersEndpointIntegrationTest.java`
- `../../../src/test/java/com/example/application/SecureGreetingEndpointIntegrationTest.java`
- `../../../src/test/java/com/example/application/InternalStatusEndpointIntegrationTest.java`
- `../../../src/test/java/com/example/application/ShoppingCartIntegrationTest.java`
- `../../../src/test/java/com/example/application/OrderEndpointIntegrationTest.java`
- `../../../src/test/java/com/example/application/PurchaseOrderEndpointIntegrationTest.java`

## Companion skills

Load the companion skill that matches the current task:

- `akka-http-endpoint-component-client`
  - endpoints that call entities, views, agents, workflows, or other components through `ComponentClient`
- `akka-http-endpoint-request-context`
  - endpoints extending `AbstractHttpEndpoint` to read query params, headers, or other request metadata
- `akka-http-endpoint-static-content`
  - endpoints serving packaged HTML, CSS, JavaScript, OpenAPI files, or other static resources
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

## Decision guide

Choose one of these modes before coding:

### 1. Pure edge mapping endpoint
Use when the endpoint mainly maps HTTP requests to API responses and does not need component calls.

Repository examples:
- `GreetingEndpoint`
- `StaticContentEndpoint`

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

### 4. Static content endpoint
Use when the endpoint serves packaged HTML, CSS, static subtrees, OpenAPI files, or small self-contained UI assets.

Repository example:
- `StaticContentEndpoint`

### 5. Low-level HTTP endpoint
Use when the endpoint needs `HttpResponse`, `HttpEntity.Strict`, or other lower-level HTTP model control.

Repository example:
- `LowLevelHttpEndpoint`

### 6. HTTP client provider endpoint
Use when the endpoint delegates to another HTTP service through `HttpClientProvider`.

Repository example:
- `ProxyGreetingEndpoint`

### 7. SSE endpoint
Use when the endpoint must stream a sequence of updates and optionally resume from the last seen event id.

Repository examples:
- `CounterStreamEndpoint`
- `DraftCartViewStreamEndpoint`
- `ShoppingCartEndpoint#notifications`
- `DraftCartEndpoint#notifications`

### 8. WebSocket endpoint
Use when the endpoint must support bidirectional streaming over a socket.

Repository example:
- `PingWebSocketEndpoint`

### 9. JWT-secured endpoint
Use when the endpoint requires bearer token validation and claim-aware behavior.

Repository example:
- `SecureGreetingEndpoint`

### 10. Internal-only ACL endpoint
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

## Response style

When answering coding tasks:
- name the endpoint path prefix explicitly
- call out whether the endpoint is pure edge logic or calls other components
- state how validation failures are mapped to HTTP behavior
- list the concrete example files used as references
