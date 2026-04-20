---
name: akka-mcp-endpoint-testing
description: Write Akka Java SDK MCP endpoint tests using direct method calls, TestKitSupport for component-backed logic, and stubbed McpRequestContext instances. Use when validating MCP tools, resources, prompts, or caller-context behavior.
---

# Akka MCP Endpoint Testing

Use this skill for MCP endpoint tests.

## Required reading

Read these first if present:
- `akka-context/sdk/mcp-endpoints.html.md`
- `../../../src/test/java/com/example/application/ShoppingCartMcpEndpointTest.java`
- `../../../src/test/java/com/example/application/SecureSupportMcpEndpointTest.java`
- `../../../src/main/java/com/example/api/ShoppingCartMcpEndpoint.java`
- `../../../src/main/java/com/example/api/SecureSupportMcpEndpoint.java`

## Default test harness rules

Because there is no dedicated MCP test kit utility, prefer these test styles:
- direct method tests for tools, resources, and prompts
- `TestKitSupport` when the MCP endpoint needs `ComponentClient`
- stubbed `McpRequestContext` when the endpoint extends `AbstractMcpEndpoint`

## Repository patterns

### Component-backed MCP endpoint tests
- `ShoppingCartMcpEndpointTest`
  - extends `TestKitSupport`
  - seeds entity state through `componentClient`
  - instantiates the MCP endpoint directly
  - parses returned JSON strings into endpoint-facing records
  - verifies prompt and packaged-resource output

### Request-context MCP endpoint tests
- `SecureSupportMcpEndpointTest`
  - creates a direct endpoint instance
  - injects a stubbed request context with `_internalSetRequestContext(...)`
  - verifies JWT claims and headers influence tool and prompt output

## What to cover

Prefer these categories:
1. tool output shape and key fields
2. resource URI behavior and returned content
3. prompt construction from inputs
4. component-backed state lookup when relevant
5. request-header or JWT-claim behavior when relevant

## Advanced transport testing

If the transport layer itself matters, use the Akka HTTP test client with handcrafted MCP JSON-RPC payloads.
Do this only when you need to verify protocol-level behavior rather than endpoint business logic.

## Anti-patterns

Avoid:
- skipping tests because the endpoint is not a regular HTTP or gRPC API
- testing only string containment when structured JSON can be parsed and asserted
- pushing all MCP logic into helpers and leaving the annotated methods untested

## Review checklist

Before finishing, verify:
- component-backed MCP tests use `TestKitSupport` only when needed
- structured tool output is parsed and asserted
- request-context behavior is covered with a stubbed `McpRequestContext`
- prompt tests assert the context that should appear in the generated prompt
