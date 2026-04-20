---
name: akka-http-endpoint-acl-internal
description: Implement Akka Java SDK HTTP endpoints that are internal-only or selectively exposed through ACLs. Use when service-only access, method-level ACL overrides, or principal inspection are the main concerns.
---

# Akka HTTP Endpoint Internal ACLs

Use this skill when an HTTP endpoint should be callable only by other services, or when class-level internal ACLs need method-level overrides.

## Required reading

Read these first if present:
- `akka-context/sdk/access-control.html.md`
- `akka-context/sdk/http-endpoints.html.md`
- `../../../src/main/java/com/example/api/InternalStatusEndpoint.java`
- `../../../src/test/java/com/example/application/InternalStatusEndpointIntegrationTest.java`

## Use this pattern when

- the endpoint is for service-to-service use only
- internet clients must be denied by default
- one or more methods need a different ACL than the class default
- the endpoint needs to inspect principals through `requestContext().getPrincipals()`

## Core pattern

1. Add a class-level `@Acl(allow = @Acl.Matcher(service = "*"))` for internal-only endpoints.
2. Add method-level `@Acl(...)` only when a method must override the class default.
3. Extend `AbstractHttpEndpoint` when the endpoint needs to inspect principals.
4. Keep principal-aware behavior in the endpoint layer.
5. In tests, treat `httpClient` calls as internet calls unless explicitly impersonating a service.

## Repository example

- `InternalStatusEndpoint`
  - class-level service-only ACL
  - method-level override exposing one public route
  - principal inspection via `requestContext().getPrincipals()`
  - integration test pattern using `impersonate-service`

## Testing rules

Prefer these test cases:
1. internet call is denied
2. method-level override is allowed
3. impersonated service call is allowed and principal information is visible

## Anti-patterns

Avoid:
- using `principal = ALL` on endpoints that should be internal-only
- assuming class-level and method-level ACLs merge; method-level ACL overrides class-level ACL completely
- mixing ACL examples with unrelated business logic

## Review checklist

Before finishing, verify:
- class-level ACL matches the intended default access level
- method-level ACLs are present only for true overrides
- principal inspection uses `requestContext()`
- tests cover denied and allowed paths
