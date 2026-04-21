---
name: akka-grpc-endpoint-request-context
description: Implement Akka Java SDK gRPC endpoints that use AbstractGrpcEndpoint and requestContext() for principals, metadata, JWT claims, and tracing. Use when endpoint behavior depends on gRPC request context.
---

# Akka gRPC Endpoint Request Context

Use this skill when a gRPC endpoint needs request metadata.

## Required reading

Read these first if present:
- `akka-context/sdk/grpc-endpoints.html.md`
- `akka-context/sdk/access-control.html.md`
- `akka-context/sdk/ai-coding-assistant-guidelines.html.md`
- `../../../src/main/proto/com/example/api/grpc/internal_status_grpc_endpoint.proto`
- `../../../src/main/java/com/example/api/InternalStatusGrpcEndpointImpl.java`
- `../../../src/test/java/com/example/application/InternalStatusGrpcEndpointIntegrationTest.java`

## Use this pattern when

- the endpoint needs principals through `requestContext().getPrincipals()`
- the endpoint needs gRPC metadata through `requestContext().metadata()`
- the endpoint needs JWT claims through `requestContext().getJwtClaims()`
- the endpoint should be internal-only with `@Acl(allow = @Acl.Matcher(service = "*"))`
- the endpoint needs tracing through `requestContext().tracing()`

## Core pattern

1. Extend `AbstractGrpcEndpoint`.
2. Access request metadata through `requestContext()`.
3. Add explicit ACLs.
4. Keep request-context logic in the endpoint rather than pushing it into components.
5. Return protobuf replies that summarize the request-context-derived behavior.

## Repository example

- `InternalStatusGrpcEndpointImpl`
  - service-only ACL
  - principal inspection via `requestContext().getPrincipals()`
  - metadata access via `requestContext().metadata()`
  - optional JWT subject mapping via `requestContext().getJwtClaims()`

## Security notes

### ACLs
- public gRPC endpoints: `@Acl(allow = @Acl.Matcher(principal = Acl.Principal.ALL))`
- service-only gRPC endpoints: `@Acl(allow = @Acl.Matcher(service = "*"))`

### JWTs
When bearer token validation is required, add `@JWT(...)` on the gRPC method and read validated claims through `requestContext().getJwtClaims()`.

### TLS
TLS is configured operationally. Use the official Akka docs for certificate setup rather than inventing endpoint-local workarounds.

## Review checklist

Before finishing, verify:
- the endpoint extends `AbstractGrpcEndpoint`
- principals are read through `requestContext().getPrincipals()`
- metadata is read through `requestContext().metadata()`
- JWT claim access uses `requestContext().getJwtClaims()` when relevant
- ACLs match the intended exposure level
- tests cover both allowed and denied caller principals when security is relevant
