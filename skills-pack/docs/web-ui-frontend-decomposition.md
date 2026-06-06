# Web UI frontend decomposition

Use this thin planning doc when requirements include a user-facing browser app hosted by Akka. For generated AI-first SaaS, decompose the UI as an authenticated agent workstream shell first, then add routes/deep links as implementation details.

Read first:

- `./web-ui-docs-index.md`
- `./workstream-ui-reference-architecture.md`
- `./structured-surface-contracts.md`
- `./web-ui-style-guide.md` when style is not already selected
- `./web-ui-quality-checklist.md` before completion

## Required frontend plan

Produce a compact plan with these sections before implementation.

### 1. Users, authority, and goals

- personas/actors and primary jobs
- selected tenant/customer/support-access context
- role/capability differences and hidden/denied workstreams
- success criteria for key journeys

### 2. Workstream shell

- functional-agent rail: visible agents, default selected agent, denied/hidden states, attention indicators
- main workstream panel: stream item types, grouping/history, progress/status, trace links, stale/reconnect behavior
- persistent composer: accepted requests, shortcuts/uploads if allowed, selected-agent context, disabled/forbidden states
- context/authority indicators: active account/scope, role/capability basis, pending approvals, recovery links

### 3. Structured surfaces and graph edges

For each surface:

- id, type, version, owning workstream/agent, purpose
- payload summary and frontend-safe fields
- states: loading, empty, ready, submitting, success, validation-error, forbidden, conflict, stale/reconnect, failure as applicable
- actions: browser action/tool id, governed backend capability/tool id, approval/idempotency/correlation behavior
- target/result/system-message surface
- audit/work-trace links and redaction rules
- accessibility and responsive behavior

### 4. APIs, realtime, and data dependencies

- `/api/me` bootstrap requirements
- query/action endpoints and DTO/error shapes
- authorization basis for every protected read/action/stream
- polling/SSE/WebSocket needs and stale/reconnect handling
- cache/freshness rules and duplicate/out-of-order event behavior

### 5. Frontend project and validation

- frontend source/build output locations
- route/deep-link families
- unit/component/contract tests
- endpoint/static asset tests
- user-flow or vertical contract tests for non-trivial product UI
- accessibility/responsive checks

## Route conventions

Prefer clear route families:

- UI/assets: `/`, `/assets/**`, `/ui...`, or explicit app/workstream entries
- protected APIs: `/api/...`
- stream APIs: `/streams/...`, `/view-streams/...`, or another explicit stream prefix
- WebSockets: `/websockets/...` only when bidirectional behavior is central

Routes, rail visibility, disabled controls, and prompt text are not authorization. Backend capabilities remain authoritative.
