---
name: akka-grpc-endpoint-streaming
description: Implement Akka Java SDK gRPC endpoints that stream protobuf replies using Source<Reply, NotUsed>. Use when server streaming or stream-forwarding is the main concern.
---

# Akka gRPC Endpoint Streaming

Use this skill when the main problem is streaming gRPC replies.


## Capability-first exposure rule

Treat every gRPC method as a selected exposure surface for a named backend capability, not as the capability itself. Before adding or changing a method, identify the capability id, allowed actors/callers, `AuthContext`, tenant/customer scope, protobuf input/output schema, side effects, idempotency, approval policy, audit/trace obligations, and tests.

For protected services, preserve the capability contract at the edge: authenticate the caller or service identity, resolve or receive the selected tenant/customer context, authorize the required role/scope/capability, validate protobuf messages, redact replies, map denials to explicit gRPC statuses such as `UNAUTHENTICATED` or `PERMISSION_DENIED`, and record required audit/work-trace events before calling components. Metadata, service names, and method names are not authorization controls.

When the same capability is also exposed through UI, HTTP, agent tools, workflows, MCP, timers, or consumers, keep authority, validation, idempotency, approval, and audit semantics identical across surfaces. Consequential gRPC actions should call the workflow/entity/approval substrate that enforces policy instead of committing side effects only in endpoint code.

## Required reading

Read these first if present:
- `akka-context/sdk/grpc-endpoints.html.md`
- `akka-context/sdk/views.html.md`
- `../../src/main/proto/com/example/api/grpc/shopping_cart_grpc_endpoint.proto`
- `../../src/main/java/com/example/api/ShoppingCartGrpcEndpointImpl.java`
- `../../src/main/java/com/example/application/ShoppingCartsByCheckedOutView.java`
- `../../src/test/java/com/example/application/ShoppingCartGrpcEndpointIntegrationTest.java`

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
