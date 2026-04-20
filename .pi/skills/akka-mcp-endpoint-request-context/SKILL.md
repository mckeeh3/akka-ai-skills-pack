---
name: akka-mcp-endpoint-request-context
description: Implement Akka Java SDK MCP endpoints that use AbstractMcpEndpoint and requestContext() for headers, principals, JWT claims, and tracing. Use when MCP behavior depends on caller context.
---

# Akka MCP Endpoint Request Context

Use this skill when an MCP endpoint needs request metadata.

## Required reading

Read these first if present:
- `akka-context/sdk/mcp-endpoints.html.md`
- `akka-context/sdk/access-control.html.md`
- `../../../src/main/java/com/example/api/SecureSupportMcpEndpoint.java`
- `../../../src/test/java/com/example/application/SecureSupportMcpEndpointTest.java`

## Use this pattern when

- the endpoint must read request headers through `requestContext().requestHeader(...)`
- the endpoint must inspect principals through `requestContext().getPrincipals()`
- the endpoint must read validated JWT claims through `requestContext().getJwtClaims()`
- the endpoint should build tenant-aware prompts or caller-aware tool responses

## Core pattern

1. Extend `akka.javasdk.mcp.AbstractMcpEndpoint`.
2. Access request metadata through `requestContext()`.
3. Add class-level `@Acl(...)`.
4. Add class-level `@JWT(...)` when bearer-token validation is required.
5. Keep request-context logic in the MCP endpoint instead of pushing it into domain code.
6. Return compact summaries or prompts derived from validated caller context.

## Repository example

- `SecureSupportMcpEndpoint`
  - validates bearer tokens at class level
  - reads tenant header, subject, issuer, and role from request context
  - exposes `callerContext` as a tool
  - builds a tenant-aware `triagePrompt`

## Security notes

- MCP endpoint ACLs and JWT validation are class-level concerns.
- Do not assume method-level ACL or JWT overrides for MCP endpoints.
- Treat request headers as edge metadata and validate required ones explicitly when they affect behavior.

## Review checklist

Before finishing, verify:
- the endpoint extends `AbstractMcpEndpoint`
- request metadata is read through `requestContext()`
- `@Acl` and `@JWT` match the intended exposure level
- prompt and tool outputs include only the caller context that is actually needed
- tests cover the request-context-dependent behavior with a stubbed `McpRequestContext`
