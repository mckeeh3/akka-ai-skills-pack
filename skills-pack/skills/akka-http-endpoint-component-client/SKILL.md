---
name: akka-http-endpoint-component-client
description: Implement Akka Java SDK HTTP endpoints that call entities, views, or other components through ComponentClient. Use when request/response mapping and HTTP error translation are the main concerns.
---

# Akka HTTP Endpoint ComponentClient Pattern

Use this skill when an HTTP endpoint calls Akka components.


## Capability-first exposure rule

Treat every HTTP route as an `api_call` actor adapter for a named governed tool and backend capability, not as the governed tool or capability itself. Before adding or changing a route, identify the responsible worker/harness, governed tool id, capability id, allowed actors/callers, `AuthContext`, tenant/customer scope, input/output schema, side effects, idempotency, approval policy, audit/trace obligations, selected Akka implementation path, and tests.

For protected routes, preserve the capability contract at the edge: authenticate the caller, resolve or receive the selected tenant/customer context, authorize the required role/scope/capability, validate and redact HTTP payloads, map denials to explicit `401`/`403` behavior, and record required audit/work-trace events before calling components. Browser actions, API paths, hidden fields, and route names are not authorization controls.

When the same capability is also exposed through UI, agent tools, workflows, gRPC, MCP, timers, or consumers, keep authority, validation, idempotency, approval, and audit semantics identical across surfaces. Consequential HTTP actions should call the workflow/entity/approval substrate that enforces policy instead of committing side effects only in endpoint code.

## Generated SaaS input contract

Use `../docs/app-worker-tool-model.md`, `../docs/app-description-to-code-compile-contract.md`, and `../references/generated-saas-input-contract.md` as the shared gate. Do not implement generated SaaS runtime code until the required worker, harness, actor adapter, governed tool, capability, AuthContext/scope, DTO, side-effect, trace, selected Akka path, and test inputs are present or explicitly deferred; otherwise repair the brief or route back to `agent-workstream-apps` + `capability-first-backend`.

## Required reading

Read these first if present:
- `../docs/app-worker-tool-model.md`
- `../docs/app-description-to-code-compile-contract.md`
- `akka-context/sdk/http-endpoints.html.md`
- `akka-context/sdk/component-and-service-calls.html.md`
- `../docs/workflow-endpoint-pattern.md`

## Use this pattern when

- the endpoint accepts HTTP JSON requests and calls an entity or view
- the endpoint must translate command failures into HTTP responses
- the external API shape should differ from the internal domain or state shape
- the endpoint needs `created`, `ok`, `badRequest`, or similar HTTP semantics

## Core pattern

1. Confirm the compile chain for the route: worker → harness → `api_call` adapter → governed tool → capability → Akka implementation.
2. Inject `ComponentClient` through the constructor.
3. Define request and response records in the endpoint or `api` package.
4. Map API request records to governed-tool/capability commands explicitly; do not expose raw component state as the public contract.
5. Call the component with synchronous `.invoke()`.
6. Map successful replies to API response records and browser-safe trace/status fields when required.
7. When returning `HttpResponse`, convert validation, authorization, or command failures into explicit HTTP responses.

## Repository examples

### Event sourced endpoint examples
- `WorkstreamEndpoint`
  - edge-facing entity endpoint
  - maps `CommandException` to `400 Bad Request`
  - exposes SSE notifications as API-specific records
- `AdminEndpoint`
  - validates requests before entity calls
  - returns `HttpResponses.created(...)` for create
  - exposes strongly consistent reads and replication filter commands

### Key value endpoint examples
- `MeEndpoint`
  - mirrors the event sourced endpoint shape for KVE comparison
- a domain-specific admin endpoint
  - mirrors the order endpoint shape for KVE comparison

### Workflow endpoint examples
- a domain-specific workflow endpoint
  - starts, reads, and streams workflow progress
- a domain-specific approval workflow endpoint
  - starts, resumes, reads, and streams paused workflow progress
- `../docs/workflow-endpoint-pattern.md`
  - tiny shared reference for workflow-specific route shapes and HTTP mappings

## Mapping rules

Prefer:
- endpoint request record -> domain command
- component reply -> endpoint response record

Avoid:
- returning domain records directly from the endpoint
- treating the endpoint path, HTTP method, or component method as the governed-tool contract
- embedding component-specific logic in the public API shape
- pushing HTTP concepts into the domain or entity code

## Error mapping rules

Use endpoint-level HTTP responses for edge failures.

Typical mapping:
- invalid request payload or failed validator -> `HttpResponses.badRequest(...)`
- successful create -> `HttpResponses.created(...)`
- successful update/read -> `HttpResponses.ok(...)` or direct typed return

If the endpoint returns `HttpResponse`, do not rely on uncaught generic exceptions for normal validation paths.

## Review checklist

Before finishing, verify:
- the route has an explicit compile chain: worker/harness, `api_call` adapter, governed tool, capability, Akka implementation path, trace, and tests
- `ComponentClient` is injected only where needed
- request/response records are endpoint-specific
- `.invoke()` is used in endpoint code
- command, authorization, or validation failures are translated to HTTP behavior
- reads and writes use the correct component client selector
- tests exercise allowed, forbidden/tenant-isolation, validation failure, trace/audit, and result mapping through `httpClient`
