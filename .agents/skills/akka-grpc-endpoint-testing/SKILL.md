---
name: akka-grpc-endpoint-testing
description: Write Akka Java SDK gRPC endpoint integration tests using TestKitSupport and getGrpcEndpointClient(...). Use for unary behavior, status-code mapping, ACL tests, and streamed reply verification.
---

# Akka gRPC Endpoint Testing

Use this skill for gRPC endpoint integration tests.


## Capability-first exposure rule

Treat every gRPC method as a selected exposure surface for a named backend capability, not as the capability itself. Before adding or changing a method, identify the capability id, allowed actors/callers, `AuthContext`, tenant/customer scope, protobuf input/output schema, side effects, idempotency, approval policy, audit/trace obligations, and tests.

For protected services, preserve the capability contract at the edge: authenticate the caller or service identity, resolve or receive the selected tenant/customer context, authorize the required role/scope/capability, validate protobuf messages, redact replies, map denials to explicit gRPC statuses such as `UNAUTHENTICATED` or `PERMISSION_DENIED`, and record required audit/work-trace events before calling components. Metadata, service names, and method names are not authorization controls.

When the same capability is also exposed through UI, HTTP, agent tools, workflows, MCP, timers, or consumers, keep authority, validation, idempotency, approval, and audit semantics identical across surfaces. Consequential gRPC actions should call the workflow/entity/approval substrate that enforces policy instead of committing side effects only in endpoint code.

## Required reading

Read these first if present:
- `akka-context/sdk/grpc-endpoints.html.md`
- `../examples/akka-components/src/test/java/com/example/application/ShoppingCartGrpcEndpointIntegrationTest.java`
- `../examples/akka-components/src/test/java/com/example/application/InternalStatusGrpcEndpointIntegrationTest.java`
- `../examples/akka-components/src/main/java/com/example/api/ShoppingCartGrpcEndpointImpl.java`
- `../examples/akka-components/src/main/java/com/example/api/InternalStatusGrpcEndpointImpl.java`

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
