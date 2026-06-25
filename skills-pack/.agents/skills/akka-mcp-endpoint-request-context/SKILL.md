---
name: akka-mcp-endpoint-request-context
description: Implement Akka Java SDK MCP endpoints that use AbstractMcpEndpoint and requestContext() for headers, principals, JWT claims, and tracing. Use when MCP behavior depends on caller context.
---

# Akka MCP Endpoint Request Context

Use this skill when an MCP endpoint needs request metadata.


## Capability-first exposure rule

Treat every MCP tool, resource, or prompt as an `mcp_tool_call`/remote-LLM actor adapter for a named governed tool inside a backend capability, not as the governed tool or capability itself. Before adding or changing an MCP surface, identify the calling worker/client, MCP exposure adapter, governed tool id, capability id, allowed callers, `AuthContext`, tenant/customer scope, input/output schema, data access, side effects, idempotency, approval policy, audit/trace obligations, selected Akka implementation path, and tests.

For protected MCP surfaces, preserve the capability contract at the edge: enforce ACL/JWT or service identity, resolve caller and tenant/customer context, authorize the required role/scope/capability, validate tool parameters, redact resource/tool output, filter allowed tools/resources per caller, map denials to safe errors, and record required audit/work-trace events. Tool descriptions, prompt text, resource URIs, and model instructions are not authorization controls.

Expose read-only scoped evidence capabilities more readily than side-effecting capabilities. Consequential MCP tools should default to proposal or approval-request capabilities unless an accepted policy grants bounded autonomous authority, and they must share the same authority, idempotency, approval, and audit semantics as any UI/API/workflow surface for the same capability.

## Generated SaaS input contract

Use `../references/generated-saas-input-contract.md`, `../docs/app-worker-tool-model.md`, and `../docs/app-description-to-code-compile-contract.md` as the shared gate. Do not implement generated SaaS runtime MCP code until the responsible worker/client, MCP actor adapter, governed tool, capability, AuthContext/scope, DTO, side-effect/idempotency policy, trace/result surface, selected endpoint/component path, and tests are present or explicitly deferred; otherwise repair the brief or route back to `agent-workstream-apps` + `capability-first-backend`.

## Required reading

Read these first if present:
- `akka-context/sdk/mcp-endpoints.html.md`
- `akka-context/sdk/access-control.html.md`

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

## Pattern to implement

Create a secure support/admin MCP endpoint that demonstrates:
- validates bearer tokens at class level
- reads tenant header, subject, issuer, and role from request context
- exposes caller context as a tool
- builds a tenant-aware prompt

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
