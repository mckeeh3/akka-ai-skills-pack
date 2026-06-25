---
name: akka-http-endpoint-websocket
description: Implement Akka Java SDK HTTP endpoint WebSockets using @WebSocket and Flow, with route tests using WebSocketRouteTester. Use when bidirectional streaming or text/binary socket handling is the main concern.
---

# Akka HTTP Endpoint WebSockets

Use this skill when an HTTP endpoint exposes a WebSocket.

For generated full-stack AI-first SaaS, WebSockets are browser/API actor adapters for a named governed tool or live workstream interaction. Use them only after the responsible worker, execution harness, actor adapter, governed tool, capability, AuthContext/scope, DTO/message contract, idempotency/side-effect policy, trace/result surface, selected implementation path, and tests are known or explicitly deferred.

## Capability-first exposure rule

Treat every HTTP route as an `api_call` or browser-facing actor adapter for a named governed tool inside a backend capability, not as the governed tool or capability itself. Before adding or changing a route, identify the responsible worker/harness, actor adapter, governed tool id, capability id, allowed actors/callers, `AuthContext`, tenant/customer scope, input/output schema, side effects, idempotency, approval policy, audit/trace obligations, selected Akka implementation path, and tests.

For protected routes, preserve the capability contract at the edge: authenticate the caller, resolve or receive the selected tenant/customer context, authorize the required role/scope/capability, validate and redact HTTP payloads, map denials to explicit `401`/`403` behavior, and record required audit/work-trace events before calling components. Browser actions, API paths, hidden fields, and route names are not authorization controls.

When the same capability is also exposed through UI, agent tools, workflows, gRPC, MCP, timers, or consumers, keep authority, validation, idempotency, approval, and audit semantics identical across surfaces. Consequential socket messages should call the workflow/entity/approval substrate that enforces policy instead of committing side effects only in endpoint flow code.

For protected WebSockets, authenticate and authorize the caller before accepting or using the connection, bind the selected tenant/customer context to the connection or each scoped message, and reject cross-tenant/customer messages server-side. Browser route guards, the fact that a client can open a socket URL, frontend hidden state, and message names are not authorization controls. Re-check authorization per message when a socket multiplexes capabilities, tenants/customers, workstreams, or authority levels.

## Required reading

Read these first if present:
- `akka-context/sdk/http-endpoints.html.md`

## Use this pattern when

- the client and server need bidirectional streaming
- the endpoint should emit multiple responses over one socket
- browser or CLI clients consume WebSocket text or binary messages

## Core pattern

1. Add `@WebSocket("/path")` to an endpoint method.
2. Return a `Flow<In, Out, NotUsed>`.
3. Keep input and output types consistent and map wire messages to browser/API DTOs, not raw domain state.
4. Resolve authentication, request context, tenant/customer scope, and capability permission before emitting protected data or invoking components.
5. Prefer simple, restart-safe flows for focused examples; push durable work, approval, idempotency, and trace behavior into the selected Akka components/services.
6. Redact errors and outputs before sending them over the socket.
7. Test with `testKit.getSelfWebSocketRouteTester()`.

## Target-project pattern

Add a target-project WebSocket endpoint only when the named capability needs bidirectional streaming. A minimal teaching pattern can map `ping` to `pong`, but generated SaaS runtime sockets should use typed messages, backend authorization, tenant/customer filtering, trace/audit expectations, and route-level tests tied to the selected capability.

## Important note

WebSocket connections are instance-bound and reconnects may land on another service instance. Do not rely on local JVM state tied to one connection.

## Anti-patterns

Avoid:
- treating socket URL access, browser state, route guards, or message names as authorization
- keeping consequential state only in per-connection JVM memory
- exposing raw domain state or internal error details over the socket
- multiplexing unrelated capabilities on one socket without per-message authorization and trace semantics
- marking WebSocket-backed UI behavior runtime-ready without exercising the intended protected API/UI/socket path

## Review checklist

Before finishing, verify:
- the method is annotated with `@WebSocket`
- the return type is a `Flow`
- protected sockets resolve authentication/AuthContext and tenant/customer scope before protected data or effects
- per-message validation, authorization, redaction, idempotency, and trace/audit behavior matches the governed-tool contract
- the flow is safe for reconnects and instance restarts
- tests cover at least one round-trip message exchange
- protected generated-SaaS sockets include forbidden, cross-tenant/customer, malformed-message, reconnect/stale-state, and browser/API route evidence where applicable
