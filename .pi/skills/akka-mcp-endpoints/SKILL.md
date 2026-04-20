---
name: akka-mcp-endpoints
description: Orchestrate Akka Java SDK MCP endpoint work across tools, resources, prompts, request context, and testing. Use when the task spans more than one MCP endpoint concern.
---

# Akka MCP Endpoints

Use this as the top-level skill for Akka Java SDK MCP endpoint work.

## Goal

Generate or review MCP endpoint code that is:
- correct for Akka SDK 3.5+
- explicit about tool, resource, and prompt contracts
- safe for LLM clients through strong descriptions and validated inputs
- easy for AI agents to extend without loading unrelated files
- backed by direct method tests and, when needed, raw MCP-over-HTTP tests

## Required reading before coding

Read these first if present:
- `akka-context/sdk/mcp-endpoints.html.md`
- `akka-context/sdk/access-control.html.md`
- `akka-context/sdk/component-and-service-calls.html.md`
- `akka-context/sdk/ai-coding-assistant-guidelines.html.md`
- existing project MCP endpoints under `src/main/java/**/api/*McpEndpoint.java`
- matching MCP endpoint tests under `src/test/java/**`

In this repository, prefer these examples:
- `../../../src/main/java/com/example/api/ShoppingCartMcpEndpoint.java`
- `../../../src/main/java/com/example/api/SecureSupportMcpEndpoint.java`
- `../../../src/main/resources/mcp/checkout-guidelines.md`
- `../../../src/test/java/com/example/application/ShoppingCartMcpEndpointTest.java`
- `../../../src/test/java/com/example/application/SecureSupportMcpEndpointTest.java`

## Companion skills

Load the companion skill that matches the current task:

- `akka-mcp-endpoint-component-client`
  - MCP tools or resources that call entities, views, workflows, or other Akka components through `ComponentClient`
- `akka-mcp-endpoint-request-context`
  - endpoints extending `AbstractMcpEndpoint` to read request headers, principals, JWT claims, or tracing metadata
- `akka-mcp-endpoint-resources-prompts`
  - resource URIs, URI templates, packaged resource files, prompt parameters, and agent-facing descriptions
- `akka-mcp-endpoint-testing`
  - direct method tests, request-context stubs, and handcrafted MCP transport tests when protocol-level coverage matters

## Default package layout

Use:
- `com.<org>.<app>.domain`
- `com.<org>.<app>.application`
- `com.<org>.<app>.api`
- `src/main/resources`

Rules:
- MCP endpoints belong in `api`
- packaged MCP resource files belong in `src/main/resources`
- domain logic stays in `domain`
- component orchestration stays in `application`, not in the MCP endpoint

## Core rules

1. An MCP endpoint has `@McpEndpoint` and must not have `@Component`.
2. Add `@Acl(...)` explicitly at class level.
3. If bearer-token validation is required, add `@JWT(...)` at class level.
4. MCP endpoint methods are public and focused: tools, resources, or prompts.
5. Tool methods return `String`.
6. Use `@Description` on tool and prompt parameters so the calling LLM can understand the contract.
7. Prefer simple parameter lists for tools. If you need a richer input object, keep the fields simple and use `inputSchema` when automatic schema inference would be ambiguous.
8. If a manual `inputSchema` is used, it must exactly match the JSON shape Jackson will parse into the Java parameter.
9. Static resources use `uri`, zero parameters, and return `String`, `byte[]`, or JSON-serializable objects.
10. Dynamic resources use `uriTemplate`; each placeholder must match a `String` parameter name.
11. Prompt methods return `String`. Prefer required `String` parameters in repository examples; verify `Optional<String>` prompt parameters against the exact SDK version before relying on them.
12. Extend `AbstractMcpEndpoint` only when request context access is needed.
13. Default MCP testing is direct method invocation. Use raw MCP-over-HTTP payloads only when transport behavior itself is under test.

## Decision guide

Choose one of these modes before coding:

### 1. Component-calling MCP tools
Use when the MCP endpoint should expose current entity or view state as LLM-friendly JSON.

Repository example:
- `ShoppingCartMcpEndpoint#getCartSummary`

### 2. Resource and prompt endpoint
Use when the main work is exposing static guidance, dynamic resources, or reusable prompt templates.

Repository examples:
- `ShoppingCartMcpEndpoint#checkoutGuidelines`
- `ShoppingCartMcpEndpoint#cartSummaryResource`
- `ShoppingCartMcpEndpoint#respondToCartQuestion`

### 3. Request-context or JWT-aware endpoint
Use when the endpoint behavior depends on headers, principals, or validated JWT claims.

Repository example:
- `SecureSupportMcpEndpoint`

### 4. MCP testing task
Use when you need to verify tool output, prompt construction, or context-aware behavior.

Repository examples:
- `ShoppingCartMcpEndpointTest`
- `SecureSupportMcpEndpointTest`

## Final review checklist

Before finishing, verify:
- `@McpEndpoint` is present
- `@Acl` is present
- no `@Component` annotation is used on the endpoint
- class-level `@JWT` is used when token validation is required
- tool descriptions are specific enough for an LLM to choose the right tool
- tool inputs use `@Description` or an accurate manual schema
- `uri` and `uriTemplate` usage matches the method signature
- prompt parameters use the simplest shape that the current SDK/runtime version supports reliably
- request-context access uses `AbstractMcpEndpoint` only when needed
- tests cover the tool/resource/prompt behavior directly

## Response style

When answering coding tasks:
- name the MCP server path explicitly
- state whether the endpoint exposes tools, resources, prompts, or a combination
- call out whether the endpoint is pure edge logic or calls other components
- list the concrete example files used as references
