# Akka gRPC JWT Patterns for AI Coding Agents

Use this reference when implementing or reviewing Akka Java SDK gRPC endpoints secured with JWT bearer tokens.

## Decision summary

| If the requirement is... | Prefer |
|---|---|
| fixed allowed issuer plus fixed claim values | `@JWT(..., bearerTokenIssuers = ..., staticClaims = @JWT.StaticClaim(values = ...))` |
| fixed issuer plus claim values that must match a regex | `@JWT(..., staticClaims = @JWT.StaticClaim(pattern = ...))` |
| endpoint must read JWT claims | extend `AbstractGrpcEndpoint` and use `requestContext().getJwtClaims()` |
| integration test of public JWT-protected gRPC route | `getGrpcEndpointClient(..., Principal.INTERNET)` + `.addRequestHeader("Authorization", "Bearer ...")` |

## Core rules

1. Add `@GrpcEndpoint` and `@Acl(...)`.
2. Use `@JWT(...)` on the class or method that requires authentication.
3. Extend `AbstractGrpcEndpoint` if the implementation reads claims.
4. Read claims only through `requestContext().getJwtClaims()`.
5. Use `bearerTokenIssuers` for `iss`, not a static claim.
6. Use `values = ...` when allowed values are fixed.
7. Use `pattern = ...` when allowed values are not fully known in advance.
8. In tests, use an unsigned token with `alg=none`; claims are still enforced locally and in integration tests.

## Two repository patterns

### 1. Fixed-value JWT validation
Reference files:
- `src/main/proto/com/example/api/grpc/secure_greeting_grpc_endpoint.proto`
- `src/main/java/com/example/api/SecureGreetingGrpcEndpointImpl.java`
- `src/test/java/com/example/application/SecureGreetingGrpcEndpointIntegrationTest.java`

Use when:
- issuer list is known
- one or more claims must equal exact values
- endpoint behavior depends on claims like `sub`, `iss`, `role`, `aud`

Pattern shape:
```java
@JWT(
    validate = JWT.JwtMethodMode.BEARER_TOKEN,
    bearerTokenIssuers = {"test-issuer", "backup-issuer"},
    staticClaims = @JWT.StaticClaim(claim = "role", values = "reader"))
```

### 2. Regex-based JWT validation
Reference files:
- `src/main/proto/com/example/api/grpc/pattern_secure_greeting_grpc_endpoint.proto`
- `src/main/java/com/example/api/PatternSecureGreetingGrpcEndpointImpl.java`
- `src/test/java/com/example/application/PatternSecureGreetingGrpcEndpointIntegrationTest.java`

Use when:
- claim value is not fully known in advance
- claim still must match a format or allowed family of values
- examples include role whitelist, UUID subject, and non-blank name

Pattern shape:
```java
@JWT(
    validate = JWT.JwtMethodMode.BEARER_TOKEN,
    bearerTokenIssuers = "pattern-issuer",
    staticClaims = {
      @JWT.StaticClaim(claim = "role", pattern = "^(admin|editor)$"),
      @JWT.StaticClaim(claim = "sub", pattern = "^[0-9a-fA-F-]{36}$"),
      @JWT.StaticClaim(claim = "name", pattern = "^\\S+$")
    })
```

## Minimal endpoint algorithm

1. let Akka validate the bearer token through `@JWT`
2. read validated claims from `requestContext().getJwtClaims()`
3. map claims into a protobuf response
4. do not re-implement token parsing or validation in endpoint code

## Minimal test algorithm

1. create client with `getGrpcEndpointClient(GeneratedClient.class, Principal.INTERNET)`
2. create unsigned token from header + payload
3. attach header with `.addRequestHeader("Authorization", "Bearer " + token)`
4. call endpoint
5. assert success or `StatusRuntimeException`

Token helper shape:
```java
private String bearerTokenWith(Map<String, String> claims) throws Exception {
  var header = Base64.getEncoder().encodeToString("{\"alg\":\"none\"}".getBytes());
  var payload = Base64.getEncoder().encodeToString(JsonSupport.getObjectMapper().writeValueAsBytes(claims));
  return header + "." + payload;
}
```

## Claim choice guide

| Claim need | Best tool |
|---|---|
| exact issuer match | `bearerTokenIssuers` |
| exact role or audience | `@JWT.StaticClaim(values = ...)` |
| role whitelist pattern | `@JWT.StaticClaim(pattern = ...)` |
| UUID subject format | `@JWT.StaticClaim(pattern = ...)` |
| optional business use after validation | `requestContext().getJwtClaims()` |

## Anti-patterns

Avoid:
- reading JWT claims without `@JWT`
- testing a public JWT route with a service principal instead of `Principal.INTERNET`
- validating `iss` with a static claim instead of `bearerTokenIssuers`
- putting auth logic into domain or entity code
- hand-parsing the bearer token inside the endpoint

## Quick copy targets

- fixed-value JWT example: `SecureGreetingGrpcEndpointImpl`
- regex-based JWT example: `PatternSecureGreetingGrpcEndpointImpl`
- test helper and header injection: `SecureGreetingGrpcEndpointIntegrationTest`, `PatternSecureGreetingGrpcEndpointIntegrationTest`
