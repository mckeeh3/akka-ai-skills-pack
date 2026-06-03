---
name: akka-http-endpoint-jwt
description: Implement Akka Java SDK HTTP endpoints secured with JWT bearer token validation and requestContext().getJwtClaims(). Use when endpoint authentication or claim-based behavior is the main concern.
---

# Akka HTTP Endpoint JWT

Use this skill when an HTTP endpoint requires JWT authentication.

## Generated SaaS input contract

For generated full-stack AI-first SaaS JWT endpoint work, implement only after the task, app-description, spec, or backlog supplies or explicitly defers:
- source functional agent, structured surface action, browser/API caller, or explicit service/internal caller;
- governed capability id/class, route exposure, selected tenant/customer context, and matching backend substrate;
- JWT issuer/audience/claim contract, local `AuthContext`, roles/capabilities, disabled/forbidden behavior, and backend authorization boundary;
- DTOs, redaction, idempotency/correlation ids, policy/approval/escalation, audit/work trace events, denial/error shapes, and required tests.

If these are absent for generated SaaS implementation, route back to `agent-workstream-apps` + `capability-first-backend` or repair the task brief instead of treating JWT presence as sufficient authorization.

## Capability-first exposure rule

Treat every HTTP route as a selected exposure surface for a named backend capability, not as the capability itself. Before adding or changing a route, identify the capability id, allowed actors/callers, `AuthContext`, tenant/customer scope, input/output schema, side effects, idempotency, approval policy, audit/trace obligations, and tests.

For protected routes, preserve the capability contract at the edge: authenticate the caller, resolve or receive the selected tenant/customer context, authorize the required role/scope/capability, validate and redact HTTP payloads, map denials to explicit `401`/`403` behavior, and record required audit/work-trace events before calling components. Browser actions, API paths, hidden fields, and route names are not authorization controls.

When the same capability is also exposed through UI, agent tools, workflows, gRPC, MCP, timers, or consumers, keep authority, validation, idempotency, approval, and audit semantics identical across surfaces. Consequential HTTP actions should call the workflow/entity/approval substrate that enforces policy instead of committing side effects only in endpoint code.

## Required reading

Read these first if present:
- `akka-context/sdk/http-endpoints.html.md`
- `akka-context/sdk/auth-with-jwts.html.md`
- `akka-context/reference/jwts.html.md`
- `../../docs/security-pattern-selection.md`
- `../../docs/security-workos-auth-and-admin.md`
- `../../docs/security-review-checklist.md`
- `../../examples/akka-components/src/main/java/com/example/api/SecureGreetingEndpoint.java`
- `../../examples/akka-components/src/test/java/com/example/application/SecureGreetingEndpointIntegrationTest.java`

## Use this pattern when

For generated AI-first SaaS applications, use this pattern for protected browser APIs, service-facing HTTP APIs that accept bearer tokens, and any endpoint whose behavior depends on signed-in account, membership, tenant/customer scope, roles/capabilities, or audit context. Public static frontend asset routes are the normal exception.

- the endpoint should require a bearer token
- a browser frontend should call `/api/...` with `Authorization: Bearer <token>`
- WorkOS/AuthKit issues access tokens for frontend users
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
7. Keep application authorization in Akka-owned Account/Membership/Role/Permission state or explicit authorization services.
8. Build an `AuthContext` from validated claims plus local `/api/me`/membership data before protected component calls.
9. Enforce active status, tenant/customer scope, required capability, disabled-user rejection, and forbidden-access behavior after JWT authentication.
10. In integration tests, inject a bearer token through the `Authorization` header.

## Frontend-to-backend JWT pattern

For an Akka-hosted web app:
- frontend static assets may be public
- protected backend routes live under `/api/...`
- frontend obtains an access token from WorkOS/AuthKit
- frontend sends `Authorization: Bearer <token>`
- Akka validates the token with `@JWT`
- endpoint reads `sub`, `email`, `iss`, or other needed claims
- endpoint loads local Akka account, membership, selected AuthContext, and capability state
- endpoint enforces app authorization and records required audit events before consequential actions or sensitive data access

Do not trust hidden frontend navigation, JWT presence, email domain, or mutable JWT role claims as the sole authorization source.

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

For production browser APIs, configure trusted JWT keys/issuers according to the Akka JWT docs and the WorkOS token contract. Do not treat local unsigned-token behavior as production security.

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
- server-side authorization is enforced after authentication for every protected SaaS route, including tenant/customer filtering and disabled-user rejection
- `/api/me` or equivalent local account lookup supplies browser-safe roles/capabilities and selected context
- frontend/backend calls use the standard `Authorization: Bearer ...` header
- integration tests inject a bearer token header
