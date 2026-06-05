---
name: akka-http-endpoint-sse
description: Implement Akka Java SDK HTTP endpoints that stream server-sent events, including resumable streams and component-backed notification streams. Use when SSE delivery and reconnect behavior are the main concerns.
---

# Akka HTTP Endpoint SSE

Use this skill when an HTTP endpoint returns server-sent events.

## Generated SaaS input contract

Use `../references/generated-saas-input-contract.md` as the shared gate. For this skill, require the task/app-description/spec/backlog to name or explicitly defer the relevant functional agent/internal trigger, capability, AuthContext/scope, DTOs, side effects, audit/work traces, and tests before implementing generated SaaS runtime code. If those inputs are absent, route back to `agent-workstream-apps` + `capability-first-backend` or repair the task brief instead of guessing.

## Capability-first exposure rule

Treat every HTTP route as a selected exposure surface for a named backend capability, not as the capability itself. Before adding or changing a route, identify the capability id, allowed actors/callers, `AuthContext`, tenant/customer scope, input/output schema, side effects, idempotency, approval policy, audit/trace obligations, and tests.

For protected routes, preserve the capability contract at the edge: authenticate the caller, resolve or receive the selected tenant/customer context, authorize the required role/scope/capability, validate and redact HTTP payloads, map denials to explicit `401`/`403` behavior, and record required audit/work-trace events before calling components. Browser actions, API paths, hidden fields, and route names are not authorization controls.

When the same capability is also exposed through UI, agent tools, workflows, gRPC, MCP, timers, or consumers, keep authority, validation, idempotency, approval, and audit semantics identical across surfaces. Consequential HTTP actions should call the workflow/entity/approval substrate that enforces policy instead of committing side effects only in endpoint code.

## Required reading

Read these first if present:
- `akka-context/sdk/http-endpoints.html.md`

## Use this pattern when

- the endpoint must stream multiple events over one HTTP response
- browser or CLI clients should receive incremental updates
- reconnects should continue from the last seen event id
- entity notifications or view updates should be exposed as SSE

## Core pattern

1. Return `akka.http.javadsl.model.HttpResponse`.
2. Build an Akka `Source` of API-facing elements.
3. Wrap the source with `HttpResponses.serverSentEvents(...)`.
4. If clients may reconnect, extract `requestContext().lastSeenSseEventId()` and resume from there.
5. Prefer stable event ids and explicit event types when reconnect behavior matters.
6. Map internal domain or notification types to API-facing records before streaming.
7. For view-backed SSE via `serverSentEventsForView(...)`, use a dedicated view stream query without `ORDER BY`; view SSE events are emitted in created/event order.

## Repository examples

### Focused resumable SSE example
- a domain-specific SSE endpoint
  - finite deterministic stream
  - uses `requestContext().lastSeenSseEventId()`
  - sets SSE ids and event type explicitly
  - tested with `SseRouteTester`

### Component-backed SSE examples
- `WorkstreamEndpoint#events`
  - maps event sourced entity notifications to API records
- a domain-specific authenticated notification stream
  - maps key value entity notifications to API records

### View-backed SSE example
- a domain-specific workstream log stream endpoint
  - streams a view query with `streamUpdates = true`
  - uses an SSE-specific view query with no `ORDER BY`
  - forwards `lastSeenSseEventId()` to the view stream offset
  - returns `HttpResponses.serverSentEventsForView(...)`

## Testing rules

For SSE endpoint tests:
- extend `TestKitSupport`
- use `testKit.getSelfSseRouteTester()`
- assert both initial events and resume-from-offset behavior when relevant
- for view-backed SSE, account for eventual consistency before opening the stream
- prefer deterministic streams in focused SSE examples

## Anti-patterns

Avoid:
- exposing internal domain types directly in SSE payloads
- omitting event ids when reconnect support matters
- using local mutable JVM state as the only source of truth for long-lived streams
- assuming SSE connections are permanent
- adding `ORDER BY` to view stream queries that are exposed as SSE

## Review checklist

Before finishing, verify:
- the endpoint returns `HttpResponse`
- the stream source is explicit and bounded or intentionally unbounded
- reconnect logic uses `lastSeenSseEventId()` when needed
- ids and event types are stable where relevant
- tests cover streaming behavior
