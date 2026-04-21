---
name: akka-http-endpoint-jwt
description: Implement Akka Java SDK HTTP endpoints secured with JWT bearer token validation and requestContext().getJwtClaims(). Use when endpoint authentication or claim-based behavior is the main concern.
---

# Akka HTTP Endpoint JWT

Use this skill when an HTTP endpoint requires JWT authentication.

## Required reading

Read these first if present:
- `akka-context/sdk/http-endpoints.html.md`
- `akka-context/sdk/auth-with-jwts.html.md`
- `akka-context/reference/jwts.html.md`
- `../../../src/main/java/com/example/api/SecureGreetingEndpoint.java`
- `../../../src/test/java/com/example/application/SecureGreetingEndpointIntegrationTest.java`

## Use this pattern when

- the endpoint should require a bearer token
- issuer or claim validation is part of the contract
- endpoint behavior depends on JWT claims like `sub`, `iss`, or `role`
- you need an integration-test pattern for injecting bearer tokens locally

## Core pattern

1. Add `@Acl(...)` to the endpoint.
2. Add `@JWT(...)` at class or method level.
3. Extend `AbstractHttpEndpoint` when the handler must inspect claims.
4. Read claims through `requestContext().getJwtClaims()`.
5. Keep authentication and claim checks at the edge.
6. In integration tests, inject a bearer token through the `Authorization` header.

## Repository example

- `SecureGreetingEndpoint`
  - validates a bearer token issuer
  - requires a static claim
  - reads issuer, subject, and role from `requestContext().getJwtClaims()`

## Integration test rule

When testing locally or in integration tests:
- JWT signature is not validated by default
- claim presence and values are still enforced
- create a simple unsigned token and pass it in `Authorization: Bearer ...`

## Anti-patterns

Avoid:
- reading JWT claims without configuring `@JWT` when the endpoint depends on them
- scattering claim extraction logic across unrelated helpers
- leaking HTTP auth concerns into domain classes
- forgetting to test the route with an injected bearer token

## Review checklist

Before finishing, verify:
- `@JWT` is present where authentication is required
- `bearerTokenIssuers` matches the intended issuer contract
- required static claims are explicit when needed
- claim access uses `requestContext().getJwtClaims()`
- integration tests inject a bearer token header
