---
name: akka-http-endpoint-component-client
description: Implement Akka Java SDK HTTP endpoints that call entities, views, or other components through ComponentClient. Use when request/response mapping and HTTP error translation are the main concerns.
---

# Akka HTTP Endpoint ComponentClient Pattern

Use this skill when an HTTP endpoint calls Akka components.

## Required reading

Read these first if present:
- `akka-context/sdk/http-endpoints.html.md`
- `akka-context/sdk/component-and-service-calls.html.md`
- `../../../src/main/java/com/example/api/ShoppingCartEndpoint.java`
- `../../../src/main/java/com/example/api/DraftCartEndpoint.java`
- `../../../src/main/java/com/example/api/OrderEndpoint.java`
- `../../../src/main/java/com/example/api/PurchaseOrderEndpoint.java`
- `../../../src/test/java/com/example/application/ShoppingCartIntegrationTest.java`
- `../../../src/test/java/com/example/application/OrderEndpointIntegrationTest.java`
- `../../../src/test/java/com/example/application/PurchaseOrderEndpointIntegrationTest.java`

## Use this pattern when

- the endpoint accepts HTTP JSON requests and calls an entity or view
- the endpoint must translate command failures into HTTP responses
- the external API shape should differ from the internal domain or state shape
- the endpoint needs `created`, `ok`, `badRequest`, or similar HTTP semantics

## Core pattern

1. Inject `ComponentClient` through the constructor.
2. Define request and response records in the endpoint or `api` package.
3. Map API request records to domain commands explicitly.
4. Call the component with synchronous `.invoke()`.
5. Map successful replies to API response records.
6. When returning `HttpResponse`, convert validation or command failures into explicit HTTP responses.

## Repository examples

### Event sourced endpoint examples
- `ShoppingCartEndpoint`
  - edge-facing entity endpoint
  - maps `CommandException` to `400 Bad Request`
  - exposes SSE notifications as API-specific records
- `OrderEndpoint`
  - validates requests before entity calls
  - returns `HttpResponses.created(...)` for create
  - exposes strongly consistent reads and replication filter commands

### Key value endpoint examples
- `DraftCartEndpoint`
  - mirrors the event sourced shopping cart endpoint shape for KVE comparison
- `PurchaseOrderEndpoint`
  - mirrors the order endpoint shape for KVE comparison

## Mapping rules

Prefer:
- endpoint request record -> domain command
- component reply -> endpoint response record

Avoid:
- returning domain records directly from the endpoint
- embedding component-specific logic in the public API shape
- pushing HTTP concepts into the domain or entity code

## Error mapping rules

Use endpoint-level HTTP responses for edge failures.

Typical mapping:
- invalid request payload or failed validator -> `HttpResponses.badRequest(...)`
- successful create -> `HttpResponses.created(...)`
- successful update/read -> `HttpResponses.ok(...)` or direct typed return

If the endpoint returns `HttpResponse`, do not rely on uncaught generic exceptions for normal validation paths.

## Review checklist

Before finishing, verify:
- `ComponentClient` is injected only where needed
- request/response records are endpoint-specific
- `.invoke()` is used in endpoint code
- command or validation failures are translated to HTTP behavior
- reads and writes use the correct component client selector
- tests exercise the route through `httpClient`
