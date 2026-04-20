---
name: akka-http-endpoint-testing
description: Write Akka Java SDK HTTP endpoint integration tests using TestKitSupport and httpClient. Use for route-level validation of JSON APIs, HTTP error mapping, and static resource endpoints.
---

# Akka HTTP Endpoint Testing

Use this skill for HTTP endpoint integration tests.

## Required reading

Read these first if present:
- `akka-context/sdk/http-endpoints.html.md`
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

## Test harness rules

HTTP endpoint tests should:
- extend `TestKitSupport`
- use `httpClient` for route calls
- deserialize bodies as API request and response records or strings
- assert both success behavior and expected failure behavior

## Repository patterns

### Pure endpoint tests
- `GreetingEndpointIntegrationTest`
  - query parameter behavior
  - request body mapping
  - HTTP 400 mapping for invalid input
- `StaticContentEndpointIntegrationTest`
  - packaged HTML and CSS served through HTTP routes
  - static subtrees and `openapi.yaml` served as static resources
- `LowLevelHttpEndpointIntegrationTest`
  - low-level response and `HttpEntity.Strict` handling
- `ProxyGreetingEndpointIntegrationTest`
  - endpoint-to-endpoint delegation through `HttpClientProvider`
- `RequestHeadersEndpointIntegrationTest`
  - request-header access through `requestContext()`
  - header validation mapped to HTTP 400
- `SecureGreetingEndpointIntegrationTest`
  - bearer token injection through `Authorization` header
  - JWT claims available in the endpoint through `requestContext()`

### SSE endpoint tests
- `CounterStreamEndpointIntegrationTest`
  - uses `testKit.getSelfSseRouteTester()`
  - verifies initial events and resume-from-offset behavior
- `DraftCartViewStreamEndpointIntegrationTest`
  - combines mocked view source updates with `SseRouteTester`
  - verifies view-backed SSE emits initial rows and later updates

### WebSocket endpoint tests
- `PingWebSocketEndpointIntegrationTest`
  - uses `testKit.getSelfWebSocketRouteTester()`
  - verifies bidirectional text message exchange

### ACL-focused endpoint tests
- `InternalStatusEndpointIntegrationTest`
  - denied internet call for service-only route
  - allowed method-level ACL override
  - allowed impersonated service caller

### Component-calling endpoint tests
- `ShoppingCartIntegrationTest`
- `OrderEndpointIntegrationTest`
- `PurchaseOrderEndpointIntegrationTest`

These cover route-to-component behavior and HTTP response mapping.

## What to cover

Prefer these categories:
1. successful route invocation
2. request body and path parameter mapping
3. query parameter or request-context behavior when relevant
4. HTTP validation failure behavior
5. static content retrieval when the endpoint serves assets
6. low-level request or response handling when relevant
7. streaming route behavior for SSE or WebSockets when relevant

## Anti-patterns

Avoid:
- using `componentClient` to test endpoint HTTP contracts
- testing only happy paths
- coupling endpoint tests to internal implementation details
- skipping route-level assertions for status and response body

## Review checklist

Before finishing, verify:
- test extends `TestKitSupport`
- route calls go through `httpClient`
- response bodies are deserialized as API-facing types
- failure behavior is asserted explicitly
- tests stay focused on the HTTP contract
