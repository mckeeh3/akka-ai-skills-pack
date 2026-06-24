---
name: akka-http-endpoint-http-client-provider
description: Implement Akka Java SDK HTTP endpoints that call other HTTP services through HttpClientProvider. Use when endpoint-to-HTTP-service delegation or HTTP proxy patterns are the main concern.
---

# Akka HTTP Endpoint HttpClientProvider

Use this skill when an endpoint calls another HTTP service.


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
4. Deserialize upstream responses as endpoint-local API records.
5. Return your own API response or HTTP error.

## Repository example

- a domain-specific HTTP-client endpoint (no current snapshot example)
  - uses `HttpClientProvider`
  - reads a base URL from a request header for a self-contained local example only
  - delegates to another HTTP route and remaps the response

## Note on deployed services

In deployed Akka projects, prefer `httpClientFor("service-name")` when calling another Akka service in the same project.

Use absolute URLs only for configured/allowlisted external services or for specialized local examples. Generated app runtime must not accept arbitrary caller-supplied upstream URLs by default; missing upstream configuration should fail closed with an actionable error.

## Review checklist

Before finishing, verify:
- `HttpClientProvider` is injected where needed
- the target service name or base URL is explicit
- upstream response parsing uses endpoint-local records
- the endpoint exposes its own stable API contract
