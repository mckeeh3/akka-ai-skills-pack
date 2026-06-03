---
name: akka-agent-mcp-tools
description: Implement Akka Java SDK agents that use remote MCP servers through effects().mcpTools(...) and RemoteMcpTools. Use when MCP-hosted tools are the main concern.
---

# Akka Agent MCP Tools

Use this skill when an agent should call remote MCP tools.

## Capability-first boundary rule

Treat every remote MCP tool as a selected cross-service exposure surface for a named backend capability. Before registering a remote MCP server, identify the capability id, allowed caller service/agent, `AuthContext` and tenant/customer scope propagation, input/output schemas, side effects, idempotency, approval policy, audit/trace obligations, and expected denial behavior.

Register only the remote tools needed for the current agent task with `.withAllowedToolNames(...)` or `.withToolNameFilter(...)`. For managed agents or protected remote tools, also load `akka-agent-tool-boundaries` and enforce the active `ToolPermissionBoundary` against stable MCP tool ids/categories before remote invocation. Do not rely on MCP tool descriptions, prompt text, or model instructions for authorization. The remote MCP endpoint must still enforce ACL/JWT/service identity, validate scope, redact outputs, and audit access according to the capability contract.

Prefer read-only evidence capabilities for remote MCP tool use. Side-effecting remote MCP tools require an explicit capability contract and should usually be proposal or approval-request capabilities unless an accepted policy grants bounded autonomous authority.

## Required reading

Read these first if present:
- `akka-context/sdk/agents/extending.html.md`
- `../../examples/akka-components/src/main/java/com/example/api/ShoppingCartToolsMcpEndpoint.java`
- `../../examples/akka-components/src/main/java/com/example/application/RemoteShoppingCartAgent.java`
- `../../examples/akka-components/src/test/java/com/example/application/RemoteShoppingCartAgentTest.java`

## Use this pattern when

- tools are hosted by an MCP endpoint in another Akka service or an external server
- the agent should use only a filtered subset of remote tools
- the endpoint path is known but the tool implementation should stay outside the agent service

## Core pattern

1. Build remote tool configs with `RemoteMcpTools.fromService(...)` for another Akka service or `fromServer(...)` for an external HTTPS MCP server.
2. Use `.withAllowedToolNames(...)` or `.withToolNameFilter(...)` to narrow exposure to the selected capability surface.
3. Register each remote tool in the tool registry/catalog with stable tool id, MCP server/tool binding, capability id, read-only or side-effecting classification, scope rules, and trace requirements.
4. Check `ToolPermissionBoundary` before remote invocation; denied or approval-required MCP tools must not be called.
5. Add caller headers, bearer tokens, or interceptors only when the capability contract requires them; never use spoofable headers as the only authority source.
6. Keep timeouts explicit for remote calls.
7. Treat MCP tools as remote dependencies and handle failures in the agent if graceful fallback is required.
8. Keep tool results curated for the agent's purpose; do not ask a remote MCP tool for raw state when the capability requires redacted evidence.
9. Emit `ToolInvocation`/work trace records for allowed, denied, approval-required, and failed remote tool attempts.

## Repository example

- `ShoppingCartToolsMcpEndpoint`
  - default-path MCP server exposing the read-only `cart.summary.inspect` capability through `getCartSummary`
  - uses a service ACL to show the remote MCP boundary is selective rather than open by default
  - returns a compact cart summary, not raw entity state
- `RemoteShoppingCartAgent`
  - connects to an explicit remote MCP server URL
  - allows only `getCartSummary` for the `cart.summary.inspect` capability
- `GovernedRefundMcpEndpoint`
  - exposes side-effecting `refund.request_consequential` through `request-governed-refund`
  - preserves service ACL, stable MCP tool id, `ToolPermissionBoundary`, tenant/customer scope, idempotency, approval-required behavior, and trace emission
  - covered by `GovernedRefundToolBoundaryIntegrationTest` for ungranted MCP denial, approval-required result, duplicate idempotency behavior, and no direct side-effect execution

## Review checklist

Before finishing, verify:
- the MCP server URI or service name is explicit
- the registered tool names map to named capability ids
- only needed tool names are exposed
- stable MCP tool ids are checked against ToolPermissionBoundary before remote invocation
- caller/service identity, tenant/customer scope, and required headers/tokens are deliberate
- timeouts are set deliberately
- remote read outputs are curated/redacted and side-effecting tools preserve approval/idempotency/audit rules
- remote tools are not confused with local `@FunctionTool` methods
