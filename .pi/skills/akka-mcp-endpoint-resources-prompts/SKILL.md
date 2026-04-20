---
name: akka-mcp-endpoint-resources-prompts
description: Implement Akka Java SDK MCP resources and prompts using stable URIs, URI templates, packaged resource files, and agent-friendly prompt parameters. Use when MCP resources or prompts are the main concern.
---

# Akka MCP Resources and Prompts

Use this skill when the task is mainly about MCP resources or prompts.

## Required reading

Read these first if present:
- `akka-context/sdk/mcp-endpoints.html.md`
- `../../../src/main/java/com/example/api/ShoppingCartMcpEndpoint.java`
- `../../../src/main/resources/mcp/checkout-guidelines.md`
- `../../../src/test/java/com/example/application/ShoppingCartMcpEndpointTest.java`

## Resource patterns

### Static resource
Use when the resource has a fixed URI and no parameters.

Repository example:
- `ShoppingCartMcpEndpoint#checkoutGuidelines`

Rules:
- use `@McpResource(uri = ...)`
- use zero parameters
- prefer `String` for markdown or text
- prefer `byte[]` for binary assets
- package reusable content under `src/main/resources`

### Dynamic resource
Use when the URI contains placeholders.

Repository example:
- `ShoppingCartMcpEndpoint#cartSummaryResource`

Rules:
- use `@McpResource(uriTemplate = ...)`
- each placeholder must match a `String` parameter name
- return a JSON-serializable record when structured output is useful
- validate path-derived input before using it for file or component lookups

## Prompt patterns

Repository example:
- `ShoppingCartMcpEndpoint#respondToCartQuestion`

Rules:
- use `@McpPrompt(...)`
- return `String`
- prefer required `String` parameters in repository examples; verify optional prompt parameters against the exact SDK version before relying on them
- add `@Description` to every parameter
- include only the context the LLM actually needs
- keep prompts structured and easy to extend safely

## Review checklist

Before finishing, verify:
- resource URIs are stable and descriptive
- URI template placeholders match method parameter names exactly
- static resources are packaged with the service when appropriate
- prompt parameters are fully described
- prompt text is concise, structured, and task-specific
