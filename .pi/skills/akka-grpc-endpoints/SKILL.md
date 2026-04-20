---
name: akka-grpc-endpoints
description: Orchestrate Akka Java SDK gRPC endpoint work across protobuf contracts, unary methods, component calls, request context, streaming, and integration testing. Use when the task spans more than one gRPC endpoint concern.
---

# Akka gRPC Endpoints

Use this as the top-level skill for Akka Java SDK gRPC endpoint work.

## Goal

Generate or review gRPC endpoint code that is:
- correct for Akka SDK 3.4+
- explicit about protobuf contracts in `src/main/proto`
- safe at the edge with clear gRPC status mapping
- easy for AI agents to extend without reading unrelated files
- backed by integration tests that use `getGrpcEndpointClient(...)`

## Required reading before coding

Read these first if present:
- `akka-context/sdk/grpc-endpoints.html.md`
- `akka-context/sdk/access-control.html.md`
- `akka-context/sdk/ai-coding-assistant-guidelines.html.md`
- existing project protobuf files under `src/main/proto/**`
- existing project gRPC endpoints under `src/main/java/**/api/*GrpcEndpointImpl.java`
- matching gRPC endpoint tests under `src/test/java/**`

In this repository, prefer these examples:
- `../../../src/main/proto/com/example/api/grpc/shopping_cart_grpc_endpoint.proto`
- `../../../src/main/proto/com/example/api/grpc/internal_status_grpc_endpoint.proto`
- `../../../src/main/java/com/example/api/ShoppingCartGrpcEndpointImpl.java`
- `../../../src/main/java/com/example/api/InternalStatusGrpcEndpointImpl.java`
- `../../../src/main/java/com/example/api/SecureGreetingGrpcEndpointImpl.java`
- `../../../src/main/java/com/example/api/PatternSecureGreetingGrpcEndpointImpl.java`
- `../../../src/test/java/com/example/application/ShoppingCartGrpcEndpointIntegrationTest.java`
- `../../../src/test/java/com/example/application/InternalStatusGrpcEndpointIntegrationTest.java`
- `../../../src/test/java/com/example/application/SecureGreetingGrpcEndpointIntegrationTest.java`
- `../../../src/test/java/com/example/application/PatternSecureGreetingGrpcEndpointIntegrationTest.java`

## Companion skills

Load the companion skill that matches the current task:

- `akka-grpc-endpoint-component-client`
  - gRPC endpoints that call entities or views through `ComponentClient`
- `akka-grpc-endpoint-request-context`
  - endpoints extending `AbstractGrpcEndpoint` to read principals, metadata, JWT claims, or tracing
- `akka-grpc-endpoint-jwt`
  - bearer-token validation and claim-aware gRPC endpoint behavior
- `akka-grpc-endpoint-streaming`
  - server-streaming methods returning `Source<Reply, NotUsed>`
- `akka-grpc-endpoint-testing`
  - `TestKitSupport` integration tests using `getGrpcEndpointClient(...)`
- `akka-grpc-proto-design`
  - protocol design, schema evolution, common protobuf types, and external protobuf dependencies

Security and deployment topics are in scope, but some are primarily routed through official docs rather than large local examples:
- ACL design for public vs service-only endpoints
- JWT validation and claim-based behavior
- TLS certificates for production exposure

Use this top-level skill plus the official docs when those are the main concern.

## Default package layout

Use:
- `com.<org>.<app>.domain`
- `com.<org>.<app>.application`
- `com.<org>.<app>.api`
- `src/main/proto`

Rules:
- protobuf contracts live in `src/main/proto`
- gRPC endpoint implementation classes belong in `api`
- endpoint classes implement generated service interfaces
- endpoint class names end with `Impl`
- domain records stay in `domain`
- component orchestration stays in `application`, not in the endpoint

## Core rules

1. A gRPC endpoint has `@GrpcEndpoint` and must not have `@Component`.
2. Add `@Acl(...)` explicitly.
3. Define service and message contracts in `.proto` files under `src/main/proto`.
4. Implement the generated service interface in the endpoint class.
5. Extend `AbstractGrpcEndpoint` only when request context access is needed.
6. Return protobuf-generated message types from endpoint methods.
7. Convert internal domain or component types to protobuf with private `toApi(...)` helpers.
8. Signal expected client failures with `GrpcServiceException` or `StatusRuntimeException`.
9. Keep production endpoint code synchronous for unary calls.
10. For server streaming, return `Source<Reply, NotUsed>`.
11. Design streams so clients can resume explicitly if reconnects matter.
12. Test gRPC endpoints through `getGrpcEndpointClient(...)`, not `componentClient`.

## Decision guide

Choose one of these modes before coding:

### 1. Pure edge mapping endpoint
Use when the endpoint mainly maps protobuf requests to protobuf replies and does not need component calls.

Repository example:
- `InternalStatusGrpcEndpointImpl`

### 2. Component-calling unary endpoint
Use when the endpoint translates protobuf requests into entity or view calls and maps replies back into protobuf.

Repository example:
- `ShoppingCartGrpcEndpointImpl`

### 3. Request-context endpoint
Use when endpoint logic depends on gRPC metadata, principals, JWT claims, or tracing.

Repository example:
- `InternalStatusGrpcEndpointImpl`

### 4. Streaming endpoint
Use when a gRPC method should stream multiple replies.

Repository example:
- `ShoppingCartGrpcEndpointImpl#streamCheckedOutCarts`

### 5. Protocol-design task
Use when the main work is protobuf layout, compatibility, common message types, or external proto imports.

Repository examples:
- `shopping_cart_grpc_endpoint.proto`
- `internal_status_grpc_endpoint.proto`

## Final review checklist

Before finishing, verify:
- `@GrpcEndpoint` is present
- `@Acl` is present
- no `@Component` annotation is used on the endpoint
- endpoint class implements the generated interface
- endpoint class name ends with `Impl`
- protobuf contracts live in `src/main/proto`
- `AbstractGrpcEndpoint` is used only if request context is needed
- expected failures become explicit gRPC statuses
- streaming methods return `Source<Reply, NotUsed>`
- tests use `getGrpcEndpointClient(...)`

## Response style

When answering coding tasks:
- name the protobuf service explicitly
- call out whether the endpoint is pure edge logic or calls other components
- state how validation failures are mapped to gRPC status behavior
- list the concrete example files used as references
