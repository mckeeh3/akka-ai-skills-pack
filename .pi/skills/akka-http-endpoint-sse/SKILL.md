---
name: akka-http-endpoint-sse
description: Implement Akka Java SDK HTTP endpoints that stream server-sent events, including resumable streams and component-backed notification streams. Use when SSE delivery and reconnect behavior are the main concerns.
---

# Akka HTTP Endpoint SSE

Use this skill when an HTTP endpoint returns server-sent events.

## Required reading

Read these first if present:
- `akka-context/sdk/http-endpoints.html.md`
- `../../../src/main/java/com/example/api/CounterStreamEndpoint.java`
- `../../../src/main/java/com/example/api/DraftCartViewStreamEndpoint.java`
- `../../../src/main/java/com/example/api/ShoppingCartEndpoint.java`
- `../../../src/main/java/com/example/api/DraftCartEndpoint.java`
- `../../../src/main/java/com/example/application/DraftCartsByCheckedOutView.java`
- `../../../src/test/java/com/example/application/CounterStreamEndpointIntegrationTest.java`
- `../../../src/test/java/com/example/application/DraftCartViewStreamEndpointIntegrationTest.java`

## Use this pattern when

- the endpoint must stream multiple events over one HTTP response
- browser or CLI clients should receive incremental updates
- reconnects should continue from the last seen event id
- entity notifications or view updates should be exposed as SSE

## Core pattern

1. Return `akka.http.javadsl.model.HttpResponse`.
2. Build an Akka `Source` of API-facing elements.
3. Wrap the source with `HttpResponses.serverSentEvents(...)`.
4. If clients may reconnect, extract `requestContext().lastSeenSseEventId()` and resume from there.
5. Prefer stable event ids and explicit event types when reconnect behavior matters.
6. Map internal domain or notification types to API-facing records before streaming.

## Repository examples

### Focused resumable SSE example
- `CounterStreamEndpoint`
  - finite deterministic stream
  - uses `requestContext().lastSeenSseEventId()`
  - sets SSE ids and event type explicitly
  - tested with `SseRouteTester`

### Component-backed SSE examples
- `ShoppingCartEndpoint#notifications`
  - maps event sourced entity notifications to API records
- `DraftCartEndpoint#notifications`
  - maps key value entity notifications to API records

### View-backed SSE example
- `DraftCartViewStreamEndpoint`
  - streams a view query with `streamUpdates = true`
  - forwards `lastSeenSseEventId()` to the view stream offset
  - returns `HttpResponses.serverSentEventsForView(...)`

## Testing rules

For SSE endpoint tests:
- extend `TestKitSupport`
- use `testKit.getSelfSseRouteTester()`
- assert both initial events and resume-from-offset behavior when relevant
- for view-backed SSE, account for eventual consistency before opening the stream
- prefer deterministic streams in focused SSE examples

## Anti-patterns

Avoid:
- exposing internal domain types directly in SSE payloads
- omitting event ids when reconnect support matters
- using local mutable JVM state as the only source of truth for long-lived streams
- assuming SSE connections are permanent

## Review checklist

Before finishing, verify:
- the endpoint returns `HttpResponse`
- the stream source is explicit and bounded or intentionally unbounded
- reconnect logic uses `lastSeenSseEventId()` when needed
- ids and event types are stable where relevant
- tests cover streaming behavior
