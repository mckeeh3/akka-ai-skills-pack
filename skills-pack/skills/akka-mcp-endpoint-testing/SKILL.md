---
name: akka-mcp-endpoint-testing
description: Write Akka Java SDK MCP endpoint tests using direct method calls, TestKitSupport for component-backed logic, and stubbed McpRequestContext instances. Use when validating MCP tools, resources, prompts, or caller-context behavior.
---

# Akka MCP Endpoint Testing

Use this skill for MCP endpoint tests.


## Capability-first exposure rule

Treat every MCP tool, resource, or prompt as a selected remote LLM-facing exposure surface for a named backend capability, not as the capability itself. Before adding or changing an MCP surface, identify the capability id, allowed callers, `AuthContext`, tenant/customer scope, input/output schema, data access, side effects, idempotency, approval policy, audit/trace obligations, and tests.

For protected MCP surfaces, preserve the capability contract at the edge: enforce ACL/JWT or service identity, resolve caller and tenant/customer context, authorize the required role/scope/capability, validate tool parameters, redact resource/tool output, filter allowed tools/resources per caller, map denials to safe errors, and record required audit/work-trace events. Tool descriptions, prompt text, resource URIs, and model instructions are not authorization controls.

Expose read-only scoped evidence capabilities more readily than side-effecting capabilities. Consequential MCP tools should default to proposal or approval-request capabilities unless an accepted policy grants bounded autonomous authority, and they must share the same authority, idempotency, approval, and audit semantics as any UI/API/workflow surface for the same capability.

## Generated SaaS input contract

For generated full-stack AI-first SaaS implementation work, apply `../references/generated-saas-input-contract.md` before coding. If the selected task lacks the required workstream/capability/AuthContext/surface/trace/test contract and does not explicitly defer it, route back to `agent-workstream-apps` + `capability-first-backend` or block for task-brief repair instead of guessing.

## Required reading

Read these first if present:
- `akka-context/sdk/mcp-endpoints.html.md`

## Default test harness rules

Because there is no dedicated MCP test kit utility, prefer these test styles:
- direct method tests for tools, resources, and prompts
- `TestKitSupport` when the MCP endpoint needs `ComponentClient`
- stubbed `McpRequestContext` when the endpoint extends `AbstractMcpEndpoint`

## Repository patterns

### Component-backed MCP endpoint tests
- `CoreAppMcpEndpointTest`
  - extends `TestKitSupport`
  - creates entity state through `componentClient`
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

## Advanced transport and completion testing

Direct method and stubbed-context tests are unit-style coverage. For protected, side-effecting, or named generated-app MCP features, also exercise the local Akka endpoint transport with MCP-over-HTTP/JSON-RPC or an equivalent local smoke path before calling the feature complete. That smoke must cover caller identity/ACL or JWT handling, request serialization, allowed-tool/resource filtering where relevant, denial shape, and audit/work-trace effects.

Use handcrafted MCP JSON-RPC payloads when protocol-level behavior matters; for generated SaaS MCP surfaces, protected transport behavior normally matters.

## Generated SaaS test set

When an endpoint exposes a generated SaaS capability, include or delegate tests for:
- authorized success with selected `AuthContext` and tenant/customer scope;
- validation and safe denial/status DTOs;
- forbidden, disabled-user, missing role/scope, and cross-tenant access;
- idempotency keys, retry/no-op behavior, and duplicate event safety where applicable;
- audit/work-trace creation for data access, denial, approval, side effects, and tool/resource use;
- structured-surface action invocation, rendering payload contract, realtime reconnect/stale behavior, or agent/MCP tool parity when that endpoint is an exposure surface.


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
- protected or side-effecting generated-app MCP surfaces have local transport smoke/integration coverage or are reported blocked/not complete
- prompt tests assert the context that should appear in the generated prompt
