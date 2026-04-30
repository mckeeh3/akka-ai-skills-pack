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
- `../../../docs/security-pattern-selection.md`
- `../../../docs/security-workos-auth-and-admin.md`
- `../../../docs/security-review-checklist.md`
- `../../../src/main/java/com/example/api/SecureGreetingEndpoint.java`
- `../../../src/test/java/com/example/application/SecureGreetingEndpointIntegrationTest.java`

## Use this pattern when

- the endpoint should require a bearer token
- a browser frontend should call `/api/...` with `Authorization: Bearer <token>`
- WorkOS or another identity provider issues access tokens for frontend users
- issuer or claim validation is part of the contract
- endpoint behavior depends on JWT claims like `sub`, `iss`, `aud`, `email`, or `role`
- you need an integration-test pattern for injecting bearer tokens locally

## Core pattern

1. Add `@Acl(...)` to the endpoint. Browser-callable protected APIs commonly use `Acl.Principal.INTERNET` plus JWT validation.
2. Add `@JWT(...)` at class or method level.
3. Use `bearerTokenIssuers` and `staticClaims` when issuer/audience/claim contracts are known.
4. Extend `AbstractHttpEndpoint` when the handler must inspect claims.
5. Read claims through `requestContext().getJwtClaims()`.
6. Keep authentication and claim extraction at the edge.
7. Keep application authorization in Akka-owned user/account/role state or explicit authorization services.
8. In integration tests, inject a bearer token through the `Authorization` header.

## Frontend-to-backend JWT pattern

For an Akka-hosted web app:
- frontend static assets may be public
- protected backend routes live under `/api/...`
- frontend obtains an access token from WorkOS/AuthKit or another provider
- frontend sends `Authorization: Bearer <token>`
- Akka validates the token with `@JWT`
- endpoint reads `sub`, `email`, `iss`, or other needed claims
- endpoint loads local Akka user/account state and enforces app authorization

Do not trust hidden frontend navigation as authorization.

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

For production, configure trusted JWT keys/issuers according to the Akka JWT docs and the identity provider token contract. Do not treat local unsigned-token behavior as production security.

## Anti-patterns

Avoid:
- reading JWT claims without configuring `@JWT` when the endpoint depends on them
- scattering claim extraction logic across unrelated helpers
- leaking HTTP auth concerns into domain classes
- making frontend route guards the only authorization layer
- storing backend identity-provider secrets in frontend env files
- forgetting to test the route with an injected bearer token

## Review checklist

Before finishing, verify:
- `@JWT` is present where authentication is required
- `bearerTokenIssuers` matches the intended issuer contract when known
- required static claims, such as audience, are explicit when needed
- claim access uses `requestContext().getJwtClaims()`
- server-side authorization is enforced after authentication when roles/scopes matter
- frontend/backend calls use the standard `Authorization: Bearer ...` header
- integration tests inject a bearer token header
