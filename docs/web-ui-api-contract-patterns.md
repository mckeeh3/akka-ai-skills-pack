# Web UI API contract patterns

Use this doc when a browser UI calls Akka HTTP endpoints. For generated full-stack AI-first SaaS apps, browser APIs transport the agent workstream shell, structured surfaces, surface actions, system messages, realtime events, and capability results. They are not the source of product meaning.

Pair with:
- `docs/structured-surface-contracts.md`
- `docs/workstream-ui-reference-architecture.md`
- `docs/capability-first-backend-architecture.md`

## Core rule

Design browser APIs in this order:

```text
/api/me bootstrap
→ selected AuthContext
→ functional-agent rail and workstream shell
→ structured surface envelopes and system-message surfaces
→ surface/action requests
→ governed backend capabilities
→ Akka components and persistence behind those capabilities
```

Endpoints expose capabilities; they do not define authority. Every protected endpoint must enforce authentication, selected `AuthContext`, tenant/customer scope, role/capability checks, policy/approval gates, idempotency, audit/work traces, and frontend-safe redaction in the backend.

## Canonical source locations

Read or create browser API contracts in:
- Akka HTTP endpoint classes under `src/main/java/**/api/`
- frontend client modules under `frontend/src/**`, commonly `frontend/src/api/**`
- endpoint integration tests under `src/test/java/**`
- app-description/spec capability and surface contracts when present

## Route families

Prefer stable route families by purpose:

- UI shell/assets: `/ui...`
- session bootstrap: `/api/me`
- workstream shell and surface APIs: `/api/workstreams/...`
- surface action submission: `/api/surfaces/{surfaceId}/actions/{actionId}` or a similarly explicit action route
- capability-specific APIs when a surface action needs a named backend path: `/api/capabilities/{capabilityId}/...` or a domain-specific alias that still records the capability id
- SSE streams: `/api/workstream-streams/...`
- WebSockets only when bidirectional browser/server messaging is central: `/websockets/...`

Conventional object-shaped paths are acceptable as implementation aliases only after the workstream, surface, and capability contract exists. Do not start broad generated-SaaS planning from resource routes.

## Required API shapes

### `/api/me`

Return only browser-safe identity, selected context, memberships, visible capabilities, and functional-agent summaries. Do not expose provider secrets, raw tokens, backend-only permission internals, hidden workstreams, or cross-tenant data.

### Surface payload API

Use explicit surface envelopes with:
- stable `surfaceId`, `surfaceType`, and `surfaceVersion`
- owning functional agent and reusable functional-agent ids
- selected `AuthContext` summary and visible capability ids
- redacted, frontend-safe `data`
- allowed action descriptors with linked `capabilityId`
- `correlationId`, trace ids, stale/reconnect metadata, and links

### Action submission API

Action requests should carry:
- `actionId`, `capabilityId`, selected context, surface id, input, idempotency key, and correlation id

Action responses should return one of:
- accepted result surface
- updated surface
- `system_message` surface
- approval-required or workflow/progress surface
- validation, forbidden, conflict, no-op, or failure result with user-safe recovery text

### AutonomousAgent progress/result API

When durable internal/background agent work is visible to users, expose progress and results as governed surfaces/events, not as untracked log strings. Include task id, status, dependency/blocked state, human-needed state, result or rejection summary, trace ids, and authorized next actions.

## Error and system-message shapes

For protected or consequential actions, prefer a typed `system_message` surface or action result that includes:
- user-safe title/body
- severity and message code
- related surface/action/capability ids
- validation fields when relevant
- retry/recovery actions when allowed
- trace/correlation ids when visible

The frontend client should normalize:
- `400` validation or malformed input
- `401` unauthenticated
- `403` forbidden or disabled-user denial
- `404` unavailable surface/workstream/item
- `409` conflict/stale update
- `5xx` server failure
- network failure
- malformed response

## Backend endpoint checklist

For each browser API endpoint:
- use `@HttpEndpoint` and explicit `@Acl`
- extract JWT/request context and selected `AuthContext`
- enforce backend authorization, tenant/customer scoping, disabled-user handling, and support-access boundaries
- validate request bodies at the edge where HTTP-specific feedback is needed
- call Akka components through `ComponentClient` when business state is involved
- map component/domain failures to typed surface/action/system-message responses
- emit required audit/work-trace records for protected data access, denials, side effects, approvals, and consequential AI/tool work
- fail closed with actionable errors when provider/security configuration is missing
- test with `httpClient`

Only public static asset routes are outside authenticated API authorization.

## Frontend client checklist

For each API call:
- define request/response TypeScript types
- return an explicit `ApiResult<T>` or equivalent
- normalize non-2xx responses
- preserve idempotency keys for retries
- update UI states for loading, ready, empty, submitting, success, validation, unauthorized, forbidden, conflict, no-op, stale/reconnect, and server failure
- avoid raw `fetch` calls outside typed API modules

## Contract test expectations

Endpoint integration tests should verify:
- `/api/me` browser-safe context and denied/disabled states
- surface envelope response shape and redaction
- action accepted, validation, forbidden, conflict, no-op, approval-required, and failure shapes
- tenant/customer mismatch and role/scope denial
- audit/work-trace creation for data access, denials, side effects, and agent/tool work
- SSE/WebSocket event scoping, duplicate-safe behavior, stale/reconnect handling, and malformed-event safety when realtime is used
- route paths referenced by packaged HTML/JS

TypeScript checks should verify frontend DTO usage against local types. If generated contracts are introduced later, keep them explicit and traceable from endpoint DTO to frontend client type and surface contract.
