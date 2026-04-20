---
name: akka-http-endpoint-websocket
description: Implement Akka Java SDK HTTP endpoint WebSockets using @WebSocket and Flow, with route tests using WebSocketRouteTester. Use when bidirectional streaming or text/binary socket handling is the main concern.
---

# Akka HTTP Endpoint WebSockets

Use this skill when an HTTP endpoint exposes a WebSocket.

## Required reading

Read these first if present:
- `akka-context/sdk/http-endpoints.html.md`
- `../../../src/main/java/com/example/api/PingWebSocketEndpoint.java`
- `../../../src/test/java/com/example/application/PingWebSocketEndpointIntegrationTest.java`

## Use this pattern when

- the client and server need bidirectional streaming
- the endpoint should emit multiple responses over one socket
- browser or CLI clients consume WebSocket text or binary messages

## Core pattern

1. Add `@WebSocket("/path")` to an endpoint method.
2. Return a `Flow<In, Out, NotUsed>`.
3. Keep input and output types consistent.
4. Prefer simple, stateless flows for focused examples.
5. Test with `testKit.getSelfWebSocketRouteTester()`.

## Repository example

- `PingWebSocketEndpoint`
  - text WebSocket example
  - maps `ping` to `pong`
  - echoes any other text with a stable prefix

## Important note

WebSocket connections are instance-bound and reconnects may land on another service instance. Do not rely on local JVM state tied to one connection.

## Review checklist

Before finishing, verify:
- the method is annotated with `@WebSocket`
- the return type is a `Flow`
- the flow is safe for reconnects and instance restarts
- tests cover at least one round-trip message exchange
