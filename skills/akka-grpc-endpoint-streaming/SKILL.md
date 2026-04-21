---
name: akka-grpc-endpoint-streaming
description: Implement Akka Java SDK gRPC endpoints that stream protobuf replies using Source<Reply, NotUsed>. Use when server streaming or stream-forwarding is the main concern.
---

# Akka gRPC Endpoint Streaming

Use this skill when the main problem is streaming gRPC replies.

## Required reading

Read these first if present:
- `akka-context/sdk/grpc-endpoints.html.md`
- `akka-context/sdk/views.html.md`
- `../../../src/main/proto/com/example/api/grpc/shopping_cart_grpc_endpoint.proto`
- `../../../src/main/java/com/example/api/ShoppingCartGrpcEndpointImpl.java`
- `../../../src/main/java/com/example/application/ShoppingCartsByCheckedOutView.java`
- `../../../src/test/java/com/example/application/ShoppingCartGrpcEndpointIntegrationTest.java`

## Core rules

1. Declare streaming in the `.proto` contract with `returns (stream Reply)`.
2. Return `Source<Reply, NotUsed>` from the generated Java method.
3. Map each upstream element to a protobuf reply with a small helper.
4. Prefer forwarding a component or view stream rather than building ad hoc in-memory state.
5. If reconnects matter, design the request so the client can resume from its own offset or cursor.
6. Do not rely on a single JVM instance staying alive for the lifetime of the gRPC connection.

## Repository example

- `ShoppingCartGrpcEndpointImpl#streamCheckedOutCarts`
  - forwards a streamed view query
  - maps each row to a protobuf `CartSummary`
- `ShoppingCartsByCheckedOutView#streamCarts`
  - query-stream method used by the endpoint

## Testing pattern

1. Populate the underlying entity or view state.
2. Wait for eventually consistent projections when a view backs the stream.
3. Collect the gRPC source in tests with `Sink.seq()` and `testKit.getMaterializer()`.

## Review checklist

Before finishing, verify:
- the `.proto` method uses `stream`
- the endpoint method returns `Source<Reply, NotUsed>`
- the upstream source is mapped to protobuf replies
- the stream contract documents how a client should resume if reconnects matter
- tests collect and assert stream elements explicitly
