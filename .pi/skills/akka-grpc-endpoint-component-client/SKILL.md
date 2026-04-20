---
name: akka-grpc-endpoint-component-client
description: Implement Akka Java SDK gRPC endpoints that call entities or views through ComponentClient. Use when protobuf mapping, component invocation, and gRPC error translation are the main concerns.
---

# Akka gRPC Endpoint ComponentClient Pattern

Use this skill when a gRPC endpoint calls Akka components.

## Required reading

Read these first if present:
- `akka-context/sdk/grpc-endpoints.html.md`
- `akka-context/sdk/component-and-service-calls.html.md`
- `../../../src/main/proto/com/example/api/grpc/shopping_cart_grpc_endpoint.proto`
- `../../../src/main/java/com/example/api/ShoppingCartGrpcEndpointImpl.java`
- `../../../src/main/java/com/example/application/ShoppingCartEntity.java`
- `../../../src/main/java/com/example/application/ShoppingCartsByCheckedOutView.java`
- `../../../src/test/java/com/example/application/ShoppingCartGrpcEndpointIntegrationTest.java`

## Use this pattern when

- the endpoint accepts protobuf requests and calls an entity or view
- the external protobuf shape should differ from the internal domain or state shape
- entity or view errors should become explicit gRPC statuses
- the endpoint should expose both unary and streamed methods over the same service contract

## Core pattern

1. Inject `ComponentClient` through the constructor.
2. Validate protobuf fields explicitly before component calls.
3. Map protobuf requests to domain commands explicitly.
4. Call the component with synchronous `.invoke()` for unary methods.
5. Map successful replies to protobuf responses with private `toApi(...)` helpers.
6. Convert normal validation failures into `GrpcServiceException(Status.INVALID_ARGUMENT...)`.
7. For streamed replies, delegate to a streamed component source and map each element to protobuf.

## Repository example

- `ShoppingCartGrpcEndpointImpl`
  - unary protobuf-to-entity mapping
  - `CommandException` translated into `INVALID_ARGUMENT`
  - server-streaming reply backed by a view stream
  - `google.protobuf.Timestamp` and `StringValue` used in API messages

## Mapping rules

Prefer:
- protobuf request -> domain command
- component reply -> protobuf response

Avoid:
- returning domain records directly from the endpoint
- embedding component-specific logic in the protobuf contract
- leaking internal validation exceptions as generic `INTERNAL` failures

## Error mapping rules

Use endpoint-level gRPC status translation for edge failures.

Typical mapping:
- malformed or incomplete protobuf request -> `INVALID_ARGUMENT`
- missing business data when that is part of the contract -> `NOT_FOUND`
- unexpected infrastructure failure -> allow Akka to surface `INTERNAL`

For normal validation paths, do not rely on generic runtime exceptions.

## Review checklist

Before finishing, verify:
- `ComponentClient` is injected only where needed
- protobuf request and reply messages are API-facing, not leaked domain types
- `.invoke()` is used for unary endpoint code
- component rejections become explicit gRPC status errors
- streamed methods map source rows to protobuf replies
- tests exercise the endpoint through a generated gRPC client
