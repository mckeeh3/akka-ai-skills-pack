---
name: akka-grpc-endpoint-testing
description: Write Akka Java SDK gRPC endpoint integration tests using TestKitSupport and getGrpcEndpointClient(...). Use for unary behavior, status-code mapping, ACL tests, and streamed reply verification.
---

# Akka gRPC Endpoint Testing

Use this skill for gRPC endpoint integration tests.

## Required reading

Read these first if present:
- `akka-context/sdk/grpc-endpoints.html.md`
- `../../../src/test/java/com/example/application/ShoppingCartGrpcEndpointIntegrationTest.java`
- `../../../src/test/java/com/example/application/InternalStatusGrpcEndpointIntegrationTest.java`
- `../../../src/main/java/com/example/api/ShoppingCartGrpcEndpointImpl.java`
- `../../../src/main/java/com/example/api/InternalStatusGrpcEndpointImpl.java`

## Test harness rules

gRPC endpoint tests should:
- extend `TestKitSupport`
- use `getGrpcEndpointClient(GeneratedClient.class)` for normal calls
- use `getGrpcEndpointClient(GeneratedClient.class, Principal)` when caller identity matters
- assert protobuf reply content directly
- assert failure behavior through `StatusRuntimeException`

## Repository patterns

### Unary endpoint tests
- `ShoppingCartGrpcEndpointIntegrationTest`
  - protobuf request-to-command mapping
  - `INVALID_ARGUMENT` assertion for rejected requests

### Streaming endpoint tests
- `ShoppingCartGrpcEndpointIntegrationTest`
  - collects stream replies with `Sink.seq()` and `testKit.getMaterializer()`
  - waits for view consistency before consuming the gRPC stream

### ACL/request-context tests
- `InternalStatusGrpcEndpointIntegrationTest`
  - allowed service principal via `Principal.localService(...)`
  - denied internet principal via `Principal.INTERNET`

## What to cover

Prefer these categories:
1. successful unary invocation
2. protobuf field mapping and response shape
3. explicit gRPC status behavior for expected failures
4. service-only vs public ACL behavior when relevant
5. streamed reply behavior when relevant
6. eventual-consistency waits when a view backs the gRPC stream

## Anti-patterns

Avoid:
- using `componentClient` to test the gRPC API contract
- testing only happy paths
- skipping status-code assertions on failures
- leaving stream tests uncollected or unasserted

## Review checklist

Before finishing, verify:
- the test extends `TestKitSupport`
- route calls go through a generated gRPC client
- caller-principal-sensitive tests use the `Principal` overload
- failure behavior is asserted with `StatusRuntimeException`
- stream tests use `Sink.seq()` with the test materializer
