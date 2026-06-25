---
name: akka-web-ui-realtime
description: Implement browser-side SSE and WebSocket behavior for Akka-hosted web UIs, including reconnect UX, stale state, and stream-to-state mapping.
---

# Akka Web UI Realtime

Use this skill when a browser UI consumes Akka SSE endpoints or WebSocket endpoints.

## AI-first realtime role

For AI-first SaaS surfaces, realtime behavior supports human supervision and timely intervention. Use SSE or WebSocket updates for active plan progress, agent/team activity, approval queues, exceptions, policy simulation status, work-trace updates, digest readiness, and outcome signals when stale data could mislead a supervisor or reviewer.

Realtime UX must make freshness visible. Show when the stream is connecting, live, reconnecting, stale, or disconnected; avoid presenting old recommendations, risk scores, policy status, or approval queues as current. Merge incoming trace/decision/work events idempotently so reconnects do not duplicate evidence or actions.


## Generated SaaS input contract

Use `../references/generated-saas-input-contract.md`, `../docs/app-worker-tool-model.md`, and `../docs/app-description-to-code-compile-contract.md` as the shared gate. Do not implement generated SaaS runtime code until the responsible worker, execution harness, actor adapter, governed tool, capability, AuthContext/scope, DTO, side-effect/idempotency policy, trace/result surface, selected implementation path, and tests are present or explicitly deferred; otherwise repair the brief or route back to `agent-workstream-apps` + `capability-first-backend`.

## Required reading

- `../akka-http-endpoint-sse/SKILL.md`
- `../akka-http-endpoint-websocket/SKILL.md`
- `../docs/web-ui-frontend-decomposition.md`
- `../docs/web-ui-quality-checklist.md`
- existing frontend realtime code under `frontend/src/**` if present

## Choose SSE vs WebSocket

Use SSE when:
- updates are server-to-browser only
- reconnect support and event ids are useful
- the browser should observe status/progress/read model updates

Use WebSocket when:
- the browser and server both send messages over the same live connection
- low-latency bidirectional interaction is central to the UI

## Browser realtime rules

1. Keep stream/socket lifecycle in a focused frontend module, hook, or service; do not put it directly in rendering/component output code.
2. Convert incoming messages into state transitions.
3. Show connection status: connecting, live, reconnecting, disconnected, stale.
4. Handle malformed messages without crashing the whole app.
5. For SSE, consider `Last-Event-ID`/resume behavior when the backend supports it.
6. For WebSocket, define client and server message types explicitly.
7. Clean up connections when navigating away from screens that no longer need them.
8. Avoid duplicating events after reconnect; define merge/idempotency behavior.
9. Treat stream/socket URLs and received event payloads as transport, not authorization; protected realtime endpoints must authorize before opening, filter by tenant/customer scope before emission, redact browser payloads, and expose stale/forbidden states safely.

## UI states to implement

- initial connecting
- live/connected
- reconnecting or stale
- disconnected with retry guidance
- stream error or unsupported browser behavior
- for AI-first surfaces when applicable: agent activity updated, approval queue changed, exception raised/resolved, policy simulation completed, trace event appended, digest ready, outcome signal updated

## Testing expectations

At minimum:
- endpoint integration tests validate stream/socket route availability
- page route tests assert the UI references the stream/socket path
- frontend checks validate message parsing and state mapping helpers
- AI-first realtime helpers, when present, handle duplicate/replayed trace or decision events without creating duplicate user-visible actions

Add browser-level smoke checks only when the project already has an appropriate browser test setup.
