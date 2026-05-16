---
name: akka-mcp-endpoint-component-client
description: Implement Akka Java SDK MCP endpoints that call entities, views, or other components through ComponentClient. Use when MCP tools or resources need current Akka state.
---

# Akka MCP Endpoint ComponentClient Pattern

Use this skill when an MCP endpoint calls Akka components.


## Capability-first exposure rule

Treat every MCP tool, resource, or prompt as a selected remote LLM-facing exposure surface for a named backend capability, not as the capability itself. Before adding or changing an MCP surface, identify the capability id, allowed callers, `AuthContext`, tenant/customer scope, input/output schema, data access, side effects, idempotency, approval policy, audit/trace obligations, and tests.

For protected MCP surfaces, preserve the capability contract at the edge: enforce ACL/JWT or service identity, resolve caller and tenant/customer context, authorize the required role/scope/capability, validate tool parameters, redact resource/tool output, filter allowed tools/resources per caller, map denials to safe errors, and record required audit/work-trace events. Tool descriptions, prompt text, resource URIs, and model instructions are not authorization controls.

Expose read-only scoped evidence capabilities more readily than side-effecting capabilities. Consequential MCP tools should default to proposal or approval-request capabilities unless an accepted policy grants bounded autonomous authority, and they must share the same authority, idempotency, approval, and audit semantics as any UI/API/workflow surface for the same capability.

## Required reading

Read these first if present:
- `akka-context/sdk/mcp-endpoints.html.md`
- `akka-context/sdk/component-and-service-calls.html.md`
- `../../../src/main/java/com/example/api/ShoppingCartMcpEndpoint.java`
- `../../../src/test/java/com/example/application/ShoppingCartMcpEndpointTest.java`

## Use this pattern when

- an MCP tool should expose current entity or view state to an LLM
- an MCP resource should fetch dynamic content from Akka components
- the MCP output should be a compact JSON string rather than a raw domain object dump
- the endpoint should adapt internal state into an LLM-friendly summary

## Core pattern

1. Inject `ComponentClient` through the constructor.
2. Keep tool parameters small and explicit.
3. Call components synchronously with `.invoke()`.
4. Map internal component state to an MCP-facing summary record.
5. Serialize tool results with `JsonSupport.encodeToString(...)`.
6. Reuse the same summary mapper for matching dynamic resources when helpful.

## Repository example

- `ShoppingCartMcpEndpoint`
  - `getCartSummary` calls `ShoppingCartEntity::getCart`
  - adapts state to `CartSummary`
  - returns compact JSON for tool responses
  - reuses the same mapping for `cartSummaryResource`

## Mapping rules

Prefer:
- component state -> MCP summary record -> JSON string

Avoid:
- returning internal domain state directly without adaptation
- exposing component-specific validation or persistence details in the MCP contract
- overly large tool outputs when a compact summary is enough for the model

## Review checklist

Before finishing, verify:
- `ComponentClient` is injected only where needed
- tool output is compact and model-friendly
- `.invoke()` is used for normal MCP endpoint component calls
- component state is mapped to an explicit MCP-facing shape
- matching resources reuse the same transformation where practical
