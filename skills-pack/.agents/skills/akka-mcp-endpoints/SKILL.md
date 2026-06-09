---
name: akka-mcp-endpoints
description: Orchestrate Akka Java SDK MCP endpoint work across tools, resources, prompts, request context, and testing. Use when the task spans more than one MCP endpoint concern.
---

# Akka MCP Endpoints

Use this as the top-level skill for Akka Java SDK MCP endpoint work when MCP is already selected as a remote LLM-facing exposure surface for one or more backend capabilities.

For broad product, PRD, feature, or agent-integration requests, route through `capability-first-backend` and `akka-solution-decomposition` before implementing tools, resources, or prompts. Do not start from an MCP tool list when capability authority, scope, side effects, approval, and audit semantics are still unclear.

## Goal

Generate or review MCP endpoint code that is:
- correct for Akka SDK 3.6.x
- explicit about tool, resource, and prompt contracts
- safe for LLM clients through strong descriptions and validated inputs
- easy for AI agents to extend without loading unrelated files
- backed by direct method tests and, when needed, raw MCP-over-HTTP tests

## AI-first substrate role

In AI-first SaaS implementations, use MCP endpoints to expose approved tools, resources, and prompts to LLM clients or external agent runtimes. Good MCP surfaces let agents inspect durable goals, plans, policies, decisions, traces, and outcome context or request bounded actions through component-backed tools.

Treat MCP as an authority-sensitive edge. Tool descriptions, resource contents, prompts, input schemas, JWT/request context, and ACLs must match the caller's allowed actions and data access. Do not expose broad mutation tools that bypass workflow approvals, policy gates, trace recording, or human supervision.

Pair AI-first MCP endpoints with:
- `akka-mcp-endpoint-component-client` for tools/resources backed by entities, views, workflows, or agents
- `akka-mcp-endpoint-request-context` when tool behavior depends on caller, tenant, or permission context
- `akka-mcp-endpoint-resources-prompts` for versioned guidance, policy snippets, and prompt resources
- `akka-agent-tools` or `akka-agent-mcp-tools` when Akka agents consume or expose MCP capabilities


## Capability-first exposure rule

Treat every MCP tool, resource, or prompt as a selected remote LLM-facing exposure surface for a named backend capability, not as the capability itself. Before adding or changing an MCP surface, identify the capability id, allowed callers, `AuthContext`, tenant/customer scope, input/output schema, data access, side effects, idempotency, approval policy, audit/trace obligations, and tests.

For protected MCP surfaces, preserve the capability contract at the edge: enforce ACL/JWT or service identity, resolve caller and tenant/customer context, authorize the required role/scope/capability, validate tool parameters, redact resource/tool output, filter allowed tools/resources per caller, map denials to safe errors, and record required audit/work-trace events. Tool descriptions, prompt text, resource URIs, and model instructions are not authorization controls.

Expose read-only scoped evidence capabilities more readily than side-effecting capabilities. Consequential MCP tools should default to proposal or approval-request capabilities unless an accepted policy grants bounded autonomous authority, and they must share the same authority, idempotency, approval, and audit semantics as any UI/API/workflow surface for the same capability.

## Required reading before coding

Read these first if present:
- `akka-context/sdk/mcp-endpoints.html.md`
- `akka-context/sdk/access-control.html.md`
- `akka-context/sdk/component-and-service-calls.html.md`
- `akka-context/sdk/ai-coding-assistant-guidelines.html.md`
- existing project MCP endpoints under `src/main/java/**/api/*McpEndpoint.java`
- matching MCP endpoint tests under `src/test/java/**`

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

Use the fixed Java base package `ai.first` for this SaaS Foundation App repository and downstream generated code. Keep package declarations, imports, tests, and source paths under `ai.first`; do not infer package names from examples.

Typical layer paths are:
- `<base>.domain`
- `<base>.application`
- `<base>.api`
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
5. Annotate every tool method with `@McpTool(...)`; set a clear `name`, `description`, and optional `inputSchema` when schema inference would be ambiguous.
6. Tool methods return `String`.
7. Use `@Description` on tool and prompt parameters so the calling LLM can understand the contract.
8. Prefer simple parameter lists for tools. If you need a richer input object, keep the fields simple and use `inputSchema` when automatic schema inference would be ambiguous.
9. If a manual `inputSchema` is used, it must exactly match the JSON shape Jackson will parse into the Java parameter.
10. Static resources use `uri`, zero parameters, and return `String`, `byte[]`, or JSON-serializable objects.
11. Dynamic resources use `uriTemplate`; each placeholder must match a `String` parameter name.
12. Prompt methods return `String`. Prefer required `String` parameters in repository examples; verify `Optional<String>` prompt parameters against the exact SDK version before relying on them.
13. Extend `AbstractMcpEndpoint` only when request context access is needed.
14. For generated SaaS MCP surfaces, resolve caller context and authorize every protected tool/resource/prompt against tenant/customer scope, allowed tool/resource grants, and required capability before calling components.
15. Side-effecting tools must preserve the named capability's idempotency, approval/policy, and audit/work-trace semantics; prefer proposal or approval-request tools when authority is not explicitly granted.
16. Default MCP testing is direct method invocation. Use raw MCP-over-HTTP payloads only when transport behavior itself is under test.

## Decision guide

Choose one of these modes before coding:

### 1. Component-calling MCP tools
Use when the MCP endpoint should expose current entity or view state as LLM-friendly JSON.

Repository example:
- a domain-specific MCP summary tool

### 2. Resource and prompt endpoint
Use when the main work is exposing static guidance, dynamic resources, or reusable prompt templates.

Pattern references:
- a packaged-resource guidance method such as `attentionGuidelines`
- a resource method that returns a domain-specific event or state summary
- a domain-specific MCP prompt/resource responder

### 3. Request-context or JWT-aware endpoint
Use when the endpoint behavior depends on headers, principals, or validated JWT claims.

Pattern to implement:
- a secure support/admin endpoint that validates bearer tokens, tenant headers, subject, issuer, and role through request context

### 4. MCP testing task
Use when you need to verify tool output, prompt construction, or context-aware behavior.

Pattern references:
- a component-backed endpoint test using `TestKitSupport` and direct endpoint instantiation
- a request-context endpoint test with a stubbed `McpRequestContext`

## Final review checklist

Before finishing, verify:
- `@McpEndpoint` is present
- `@Acl` is present
- no `@Component` annotation is used on the endpoint
- class-level `@JWT` is used when token validation is required
- every tool method has `@McpTool(...)`
- tool descriptions are specific enough for an LLM to choose the right tool
- tool inputs use `@Description` or an accurate manual schema
- `uri` and `uriTemplate` usage matches the method signature
- prompt parameters use the simplest shape that the current SDK/runtime version supports reliably
- request-context access uses `AbstractMcpEndpoint` only when needed
- protected tools/resources/prompts enforce backend authorization, tenant/customer scope, redaction, and allowed-surface filtering before returning data or invoking components
- side-effecting tools use explicit idempotency, approval/policy, and audit/work-trace behavior or are narrowed to proposal/approval-request semantics
- tests cover the tool/resource/prompt behavior directly, including forbidden/cross-scope behavior when protected

## Response style

When answering coding tasks:
- name the MCP server path explicitly
- state whether the endpoint exposes tools, resources, prompts, or a combination
- call out whether the endpoint is pure edge logic or calls other components
- list the concrete example files used as references
