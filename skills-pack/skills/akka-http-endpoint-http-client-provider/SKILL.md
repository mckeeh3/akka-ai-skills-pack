---
name: akka-http-endpoint-http-client-provider
description: Implement Akka Java SDK HTTP endpoints that call other HTTP services through HttpClientProvider. Use when endpoint-to-HTTP-service delegation or HTTP proxy patterns are the main concern.
---

# Akka HTTP Endpoint HttpClientProvider

Use this skill when an endpoint calls another HTTP service.

## Generated SaaS input contract

Use `../references/generated-saas-input-contract.md`, `../docs/app-worker-tool-model.md`, and `../docs/app-description-to-code-compile-contract.md` as the shared gate. Do not implement generated SaaS runtime code until the responsible worker, execution harness, actor adapter, governed tool, capability, AuthContext/scope, DTO, upstream service contract, side-effect/idempotency policy, trace/result surface, selected implementation path, and tests are present or explicitly deferred; otherwise repair the brief or route back to `agent-workstream-apps` + `capability-first-backend`.

## Capability-first exposure rule

Treat every HTTP route as an `api_call` or browser-facing actor adapter for a named governed tool inside a backend capability, not as the governed tool or capability itself. Before adding or changing a route, identify the responsible worker/harness, actor adapter, governed tool id, capability id, allowed actors/callers, `AuthContext`, tenant/customer scope, input/output schema, side effects, idempotency, approval policy, audit/trace obligations, selected Akka implementation path, and tests.

For protected routes, preserve the capability contract at the edge: authenticate the caller, resolve or receive the selected tenant/customer context, authorize the required role/scope/capability, validate and redact HTTP payloads, map denials to explicit `401`/`403` behavior, and record required audit/work-trace events before calling components. Browser actions, API paths, hidden fields, and route names are not authorization controls.

When the same capability is also exposed through UI, agent tools, workflows, gRPC, MCP, timers, or consumers, keep authority, validation, idempotency, approval, and audit semantics identical across surfaces. Consequential HTTP actions should call the workflow/entity/approval substrate that enforces policy instead of committing side effects only in endpoint code.

## Required reading

Read these first if present:
- `akka-context/sdk/http-endpoints.html.md`
- `akka-context/sdk/component-and-service-calls.html.md`

## Use this pattern when

- the endpoint delegates part of its work to another HTTP service
- the target is a configured Akka service name or allowlisted external base URL; caller-supplied arbitrary base URLs are test/example-only unless an explicit, secured proxy capability requires them
- the endpoint must translate upstream responses into its own API contract

## Core pattern

1. Inject `HttpClientProvider`.
2. Create a client with `httpClientFor(serviceNameOrBaseUrl)`.
3. Make an HTTP request with `GET`, `POST`, `PUT`, `PATCH`, or `DELETE`.
4. Propagate only the approved caller/service context, tenant/customer scope, correlation id, and idempotency key required by the upstream contract; do not forward bearer tokens, cookies, or internal headers blindly.
5. Deserialize upstream responses as endpoint-local API records.
6. Map upstream denials, validation failures, outages, and unsafe payloads to your own redacted API response or HTTP error, with audit/work-trace evidence where required.

## Target-project pattern

Use this pattern in the target project when endpoint-to-HTTP-service delegation is required; there is no current source snapshot example in this pack.

- a domain-specific HTTP-client endpoint
  - uses `HttpClientProvider`
  - reads a base URL from configuration or a secured/allowlisted capability contract
  - uses a request-header base URL only for a self-contained local example, never as the generated-app runtime default
  - delegates to another HTTP route and remaps the response

## Note on deployed services

In deployed Akka projects, prefer `httpClientFor("service-name")` when calling another Akka service in the same project.

Use absolute URLs only for configured/allowlisted external services or for specialized local examples. Generated app runtime must not accept arbitrary caller-supplied upstream URLs by default; missing upstream configuration should fail closed with an actionable error.

## Review checklist

Before finishing, verify:
- `HttpClientProvider` is injected where needed
- the target service name or base URL is explicit and configured/allowlisted; arbitrary caller-supplied upstream URLs are rejected outside explicit secured proxy capabilities
- approved caller/service identity, AuthContext, tenant/customer scope, correlation, and idempotency semantics are preserved without blindly forwarding sensitive headers
- upstream response parsing uses endpoint-local records
- upstream errors and denials are redacted, audited/traced where required, and mapped to the endpoint's own stable API contract
- missing upstream configuration fails closed with an actionable error
- tests cover configured success, forbidden or cross-tenant access, invalid upstream configuration, upstream error mapping, and redaction
