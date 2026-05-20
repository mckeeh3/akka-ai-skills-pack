---
name: akka-mcp-endpoint-resources-prompts
description: Implement Akka Java SDK MCP resources and prompts using stable URIs, URI templates, packaged resource files, and agent-friendly prompt parameters. Use when MCP resources or prompts are the main concern.
---

# Akka MCP Resources and Prompts

Use this skill when the task is mainly about MCP resources or prompts.


## Capability-first exposure rule

Treat every MCP tool, resource, or prompt as a selected remote LLM-facing exposure surface for a named backend capability, not as the capability itself. Before adding or changing an MCP surface, identify the capability id, allowed callers, `AuthContext`, tenant/customer scope, input/output schema, data access, side effects, idempotency, approval policy, audit/trace obligations, and tests.

For protected MCP surfaces, preserve the capability contract at the edge: enforce ACL/JWT or service identity, resolve caller and tenant/customer context, authorize the required role/scope/capability, validate tool parameters, redact resource/tool output, filter allowed tools/resources per caller, map denials to safe errors, and record required audit/work-trace events. Tool descriptions, prompt text, resource URIs, and model instructions are not authorization controls.

Expose read-only scoped evidence capabilities more readily than side-effecting capabilities. Consequential MCP tools should default to proposal or approval-request capabilities unless an accepted policy grants bounded autonomous authority, and they must share the same authority, idempotency, approval, and audit semantics as any UI/API/workflow surface for the same capability.

## Generated SaaS input contract

For generated full-stack AI-first SaaS work, implement only after the selected task, app-description, spec, or backlog supplies or explicitly defers:
- functional agent or explicit internal-only/foundation scope;
- workstream, structured surface id/type/version, and surface action or workstream event when user-facing;
- capability id/class, selected Akka substrate, and exposure surfaces;
- `AuthContext`, tenant/customer scope, roles/capabilities, and backend authorization boundary;
- input/output DTOs, redaction, side effects, idempotency, policy/approval/escalation, audit/work traces, and required tests.

If these are absent and the work is generated SaaS implementation, route back to `agent-workstream-apps` + `capability-first-backend` or block for task-brief repair instead of guessing.

## Required reading

Read these first if present:
- `akka-context/sdk/mcp-endpoints.html.md`
- `../../src/main/java/com/example/api/ShoppingCartMcpEndpoint.java`
- `../../src/main/resources/mcp/checkout-guidelines.md`
- `../../src/test/java/com/example/application/ShoppingCartMcpEndpointTest.java`

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
