---
name: akka-agent-mcp-tools
description: Implement Akka Java SDK agents that use remote MCP servers through effects().mcpTools(...) and RemoteMcpTools. Use when MCP-hosted tools are the main concern.
---

# Akka Agent MCP Tools

Use this skill when an agent should call remote MCP tools.

## Required reading

Read these first if present:
- `akka-context/sdk/agents/extending.html.md`
- `../../../src/main/java/com/example/api/ShoppingCartToolsMcpEndpoint.java`
- `../../../src/main/java/com/example/application/RemoteShoppingCartAgent.java`
- `../../../src/test/java/com/example/application/RemoteShoppingCartAgentTest.java`

## Use this pattern when

- tools are hosted by an MCP endpoint in another Akka service or an external server
- the agent should use only a filtered subset of remote tools
- the endpoint path is known but the tool implementation should stay outside the agent service

## Core pattern

1. Build remote tool configs with `RemoteMcpTools.fromService(...)` or `fromServer(...)`.
2. Use `.withAllowedToolNames(...)` or `.withToolNameFilter(...)` to narrow exposure.
3. Add headers or interceptors only when required.
4. Keep timeouts explicit for remote calls.
5. Treat MCP tools as remote dependencies and handle failures in the agent if graceful fallback is required.

## Repository example

- `ShoppingCartToolsMcpEndpoint`
  - default-path MCP server exposing `getCartSummary`
- `RemoteShoppingCartAgent`
  - connects to `/mcp`
  - allows only `getCartSummary`

## Review checklist

Before finishing, verify:
- the MCP server URI or service name is explicit
- only needed tool names are exposed
- timeouts are set deliberately
- remote tools are not confused with local `@FunctionTool` methods
