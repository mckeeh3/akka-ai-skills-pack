---
name: akka-grpc-endpoint-jwt
description: Implement Akka Java SDK gRPC endpoints secured with JWT bearer token validation and requestContext().getJwtClaims(). Use when endpoint authentication or claim-based behavior is the main concern.
---

# Akka gRPC Endpoint JWT

Use this skill when a gRPC endpoint requires JWT authentication.

## Required reading

Read these first if present:
- `akka-context/sdk/grpc-endpoints.html.md`
- `akka-context/sdk/auth-with-jwts.html.md`
- `akka-context/reference/jwts.html.md`
- `../../../src/main/proto/com/example/api/grpc/secure_greeting_grpc_endpoint.proto`
- `../../../src/main/java/com/example/api/SecureGreetingGrpcEndpointImpl.java`
- `../../../src/test/java/com/example/application/SecureGreetingGrpcEndpointIntegrationTest.java`

## Use this pattern when

- the gRPC endpoint should require a bearer token
- issuer or claim validation is part of the contract
- endpoint behavior depends on JWT claims like `sub`, `iss`, `aud`, or `role`
- you need an integration-test pattern for injecting a bearer token into a generated gRPC client

## Core pattern

1. Add `@Acl(...)` to the gRPC endpoint.
2. Extend `AbstractGrpcEndpoint` when the handler must inspect claims.
3. Add `@JWT(...)` at class or method level.
4. Read claims through `requestContext().getJwtClaims()`.
5. Keep authentication and claim checks at the edge.
6. In integration tests, inject a bearer token through `addRequestHeader("Authorization", "Bearer ...")` on the generated gRPC client.

## Repository example

- `SecureGreetingGrpcEndpointImpl`
  - validates bearer token issuers
  - requires a static `role` claim
  - reads issuer, subject, role, and optional audience from `requestContext().getJwtClaims()`

## Integration test rule

When testing locally or in integration tests:
- JWT signature is not validated by default
- claim presence and values are still enforced
- create a simple unsigned token and pass it as an `Authorization` request header on the gRPC client
- prefer `Principal.INTERNET` when testing public JWT-protected gRPC endpoints

## Anti-patterns

Avoid:
- reading JWT claims without configuring `@JWT` when the endpoint depends on them
- using self or service principals for tests that are supposed to verify internet-facing JWT behavior
- scattering claim extraction logic across unrelated helpers
- leaking auth concerns into domain classes

## Review checklist

Before finishing, verify:
- `@JWT` is present where authentication is required
- `bearerTokenIssuers` matches the intended issuer contract
- required static claims are explicit when needed
- claim access uses `requestContext().getJwtClaims()`
- integration tests inject a bearer token header through the generated gRPC client
