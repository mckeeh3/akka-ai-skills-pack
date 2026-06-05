---
name: akka-http-endpoint-testing
description: Write Akka Java SDK HTTP endpoint integration tests using TestKitSupport and httpClient. Use for route-level validation of JSON APIs, HTTP error mapping, asset routes for generated frontend builds, and streaming endpoints.
---

# Akka HTTP Endpoint Testing

Use this skill for HTTP endpoint integration tests.


## Capability-first exposure rule

Treat every HTTP route as a selected exposure surface for a named backend capability, not as the capability itself. Before adding or changing a route, identify the capability id, allowed actors/callers, `AuthContext`, tenant/customer scope, input/output schema, side effects, idempotency, approval policy, audit/trace obligations, and tests.

For protected routes, preserve the capability contract at the edge: authenticate the caller, resolve or receive the selected tenant/customer context, authorize the required role/scope/capability, validate and redact HTTP payloads, map denials to explicit `401`/`403` behavior, and record required audit/work-trace events before calling components. Browser actions, API paths, hidden fields, and route names are not authorization controls.

When the same capability is also exposed through UI, agent tools, workflows, gRPC, MCP, timers, or consumers, keep authority, validation, idempotency, approval, and audit semantics identical across surfaces. Consequential HTTP actions should call the workflow/entity/approval substrate that enforces policy instead of committing side effects only in endpoint code.

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
- `akka-context/sdk/http-endpoints.html.md`

## Test harness rules

HTTP endpoint tests should:
- extend `TestKitSupport`
- use `httpClient` for route calls
- deserialize bodies as API request and response records or strings
- assert both success behavior and expected failure behavior

## Repository patterns

### Pure endpoint tests
- current endpoint integration tests for `MeEndpoint`, `WorkstreamEndpoint`, or `AdminEndpoint` when present in the target app
  - query parameter behavior
  - request body mapping
  - HTTP 400 mapping for invalid input
- domain-specific low-level endpoint integration tests
  - low-level response and `HttpEntity.Strict` handling
- domain-specific HTTP-client endpoint integration tests
  - endpoint-to-endpoint delegation through `HttpClientProvider`
- domain-specific request-context endpoint integration tests
  - request-header access through `requestContext()`
  - header validation mapped to HTTP 400
- JWT/protected-route endpoint integration tests for `MeEndpoint` or protected `/api/...` routes
  - bearer token injection through `Authorization` header
  - JWT claims available in the endpoint through `requestContext()`

### SSE endpoint tests
- domain-specific SSE endpoint integration tests
  - use `testKit.getSelfSseRouteTester()`
  - verify initial events, authorization, and resume-from-offset behavior
- domain-specific view/workstream stream endpoint integration tests
  - combine mocked source updates with `SseRouteTester`
  - verify view-backed or workstream-backed SSE emits initial rows and later updates

### WebSocket endpoint tests
- domain-specific WebSocket endpoint integration tests
  - use `testKit.getSelfWebSocketRouteTester()`
  - verify bidirectional text message exchange

### ACL-focused endpoint tests
- domain-specific internal-only endpoint integration tests
  - denied internet call for service-only route
  - allowed method-level ACL override
  - allowed impersonated service caller

### Component-calling endpoint tests
- `WorkstreamEndpointIntegrationTest`
- `AdminEndpointIntegrationTest`
- a domain-specific admin endpoint integration test

These cover route-to-component behavior and HTTP response mapping.

## What to cover

Prefer these categories:
1. successful route invocation
2. request body and path parameter mapping
3. query parameter or request-context behavior when relevant
4. HTTP validation failure behavior
5. generated frontend asset retrieval when the endpoint serves a built web app
6. low-level request or response handling when relevant
7. streaming route behavior for SSE or WebSockets when relevant

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
- using `componentClient` to test endpoint HTTP contracts
- testing only happy paths
- coupling endpoint tests to internal implementation details
- skipping route-level assertions for status and response body

## Review checklist

Before finishing, verify:
- test extends `TestKitSupport`
- route calls go through `httpClient`
- response bodies are deserialized as API-facing types
- failure behavior is asserted explicitly
- tests stay focused on the HTTP contract
