---
name: akka-web-ui-realtime
description: Implement browser-side SSE and WebSocket behavior for Akka-hosted web UIs, including reconnect UX, stale state, and stream-to-state mapping.
---

# Akka Web UI Realtime

Use this skill when a browser UI consumes Akka SSE endpoints or WebSocket endpoints.

## Required reading

- `../akka-http-endpoint-sse/SKILL.md`
- `../akka-http-endpoint-websocket/SKILL.md`
- `../../../docs/web-ui-frontend-decomposition.md`
- `../../../docs/web-ui-quality-checklist.md`
- existing `src/main/web-ui/**/realtime.ts` if present

## Choose SSE vs WebSocket

Use SSE when:
- updates are server-to-browser only
- reconnect support and event ids are useful
- the browser should observe status/progress/read model updates

Use WebSocket when:
- the browser and server both send messages over the same live connection
- low-latency bidirectional interaction is central to the UI

## Browser realtime rules

1. Keep stream/socket lifecycle in `realtime.ts`, not in render functions.
2. Convert incoming messages into state transitions.
3. Show connection status: connecting, live, reconnecting, disconnected, stale.
4. Handle malformed messages without crashing the whole app.
5. For SSE, consider `Last-Event-ID`/resume behavior when the backend supports it.
6. For WebSocket, define client and server message types explicitly.
7. Clean up connections when navigating away from screens that no longer need them.
8. Avoid duplicating events after reconnect; define merge/idempotency behavior.

## UI states to implement

- initial connecting
- live/connected
- reconnecting or stale
- disconnected with retry guidance
- stream error or unsupported browser behavior

## Testing expectations

At minimum:
- endpoint integration tests validate stream/socket route availability
- page route tests assert the UI references the stream/socket path
- TypeScript checks validate message parsing and state mapping helpers

Add browser-level smoke checks only when the project already has a lightweight browser test setup.
