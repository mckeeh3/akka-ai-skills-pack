# Structured surface contracts

Use this document when defining or implementing typed surfaces in an agent workstream application. It turns the surface guidance from `agent-workstream-application-architecture.md` into an implementation contract for app descriptions, frontend code, HTTP APIs, realtime events, capability modeling, and tests.

A surface is a structured renderable artifact in a **functional/context-area agent** workstream; this document shortens the term to **functional agent** after first use. It is not a page, route, chat message, CRUD screen, endpoint, view, or Akka component. Routes, endpoints, views, tools, workflows, and frontend components realize or expose the surface after its contract is clear.

## Contract rule

Every surface contract must preserve this chain:

```text
functional/context-area agent workstream placement
→ surface type and payload schema
→ allowed actions and events
→ governed backend capabilities
→ backend authorization, audit, and trace
→ frontend rendering and tests
```

Frontend action visibility is advisory only. The linked backend capability remains authoritative for authentication, selected `AuthContext`, tenant/customer scope, membership status, role/capability checks, approval policy, idempotency, side effects, audit, and denial behavior.

In app-description trees, surface ownership belongs in `12-workstreams/`: surface index and contracts, reusable functional-agent placement, action-to-capability mappings, trace semantics, and surface/action tests. `55-ui/` owns browser realization of those contracts: shell placement, route/deep-link mappings, components, forms/interactions, frontend API contracts, state/realtime behavior, accessibility/responsive behavior, and style guide. Do not redefine surface purpose, authority, or capability semantics in `55-ui/`; link back to `12-workstreams/` and capability/security/test layers.

## Minimum contract fields

For each surface, define these fields before implementation:

| Field | Required content |
|---|---|
| Surface identity | Stable `surfaceId`, display name, canonical type, semantic version, owner, lifecycle status. |
| Placement | Owning functional agent, reusable functional agents, workstream entry point, embedded/drill-in/modal/side-panel/deep-link placement. |
| Purpose | User outcome, business context, and when the surface should appear or refresh. |
| Payload schema | Typed render payload, field formats, required/optional fields, nested records, pagination/sort/filter metadata, trace/correlation ids, version/stale markers. |
| Redaction | Role-dependent fields, PII/secret boundaries, support/auditor visibility, frontend-safe fields, agent-safe fields. |
| Data source | Read/evidence capabilities and view/query sources that produce the payload; no raw unscoped state dumps by default. |
| Actions | Allowed user/agent actions, labels, input payloads, confirmation/approval requirements, idempotency keys, result states, linked capability ids. |
| Events | Realtime/update events, event ids, ordering/dedupe rules, reconnect behavior, partial update semantics, stale markers. |
| Authority | AuthContext assumptions, tenant/customer scope, role/capability requirements, policy gates, denial shape, disabled-user behavior. |
| Audit/trace | Audit event types, work-trace fields, visible trace links, correlation ids, evidence references, retention/redaction expectations. |
| UI states | Loading, empty, ready, submitting, success, pending, approval-needed, error, forbidden, conflict, stale, reconnecting, partial-data, and no-op states. |
| Accessibility/responsive | Keyboard path, labels, focus behavior, status announcements, narrow-layout strategy, chart/table alternatives. |
| Tests | Rendering, payload parsing, action-to-capability invocation, auth/forbidden/tenant isolation, audit/trace, realtime/stale, accessibility and responsive checks. |

## Surface payload shape

Use an explicit envelope for browser-facing surface payloads. Keep it stable enough for frontend components and tests, while allowing type-specific `data` records.

```ts
type SurfaceEnvelope<TData, TAction extends SurfaceAction = SurfaceAction> = {
  surfaceId: string;
  surfaceType: string;
  surfaceVersion: string;
  title: string;
  ownerFunctionalAgentId: string;
  reusableByFunctionalAgentIds?: string[];
  authContext: {
    tenantId: string;
    customerId?: string;
    selectedContextId: string;
    visibleCapabilityIds: string[];
  };
  correlationId: string;
  traceIds: string[];
  generatedAt: string;
  stale?: {
    isStale: boolean;
    reason?: string;
    lastKnownEventId?: string;
  };
  redaction: {
    profile: "self" | "tenant-admin" | "support" | "auditor" | "agent";
    omittedFieldKeys?: string[];
  };
  data: TData;
  actions: TAction[];
  links?: SurfaceLink[];
};
```

Payload rules:

- Include `surfaceVersion` and type-specific schema version when payload semantics change.
- Include `correlationId` and trace ids for audit/debugging surfaces and consequential actions.
- Include stale/reconnect markers when rendered data may lag event streams or command results.
- Return frontend-safe, scoped, redacted fields only. Do not send hidden secrets or cross-tenant data and rely on the UI not to display them.
- Use role-specific action lists only as a UX hint; backend denial must still be correct if an action is submitted manually.

## Surface action shape

Each surface action is a UI exposure of a governed capability.

```ts
type SurfaceAction = {
  actionId: string;
  label: string;
  intent: "read" | "command" | "proposal" | "approval" | "workflow" | "governance" | "trace";
  capabilityId: string;
  inputSchemaRef?: string;
  requiresConfirmation?: boolean;
  requiresApproval?: boolean;
  disabled?: {
    reasonCode: string;
    message: string;
  };
  idempotency: {
    required: boolean;
    keySource?: "client-generated" | "surface-item" | "server-issued";
  };
  resultSurface?: {
    appendSurfaceType?: string;
    updateSurfaceId?: string;
    openPlacement?: "inline" | "modal" | "side-panel" | "deep-link";
  };
  audit: {
    eventType: string;
    traceRequired: boolean;
  };
};
```

Action rules:

- `capabilityId` is required for every consequential action.
- The capability definition owns input validation, authorization, idempotency, side effects, policy/approval, audit, and denial shape.
- Side-effecting actions should default to proposal or approval flows unless a bounded autonomous policy is explicitly accepted.
- Disabled/hidden actions must include a user-safe reason when visible; they must not be the only authorization control.
- Action results should append a new surface, update the current surface, or render workflow/progress state explicitly.

## Event shape

Use events when a surface is updated by SSE, WebSocket, workflow progress, consumer output, agent work, or timer-backed refresh.

```ts
type SurfaceEvent<TPatch = unknown> = {
  eventId: string;
  eventType:
    | "surface.created"
    | "surface.updated"
    | "surface.action.accepted"
    | "surface.action.denied"
    | "surface.workflow.progressed"
    | "surface.stale"
    | "surface.reconnected";
  surfaceId: string;
  surfaceType: string;
  surfaceVersion: string;
  tenantId: string;
  customerId?: string;
  correlationId: string;
  traceIds: string[];
  occurredAt: string;
  sequence?: number;
  patch?: TPatch;
};
```

Event rules:

- Scope every event by tenant/customer and selected context at the endpoint boundary.
- Include `eventId` and optional sequence for dedupe/replay-safe rendering.
- Treat malformed, duplicate, out-of-order, forbidden, or cross-context events as safe no-ops plus diagnostics, not frontend crashes.
- Reconnection must either replay from `lastEventId` or mark affected surfaces stale and require refresh.

## Capability mapping requirements

For every surface action and payload-producing query, the capability inventory must specify:

- capability id and class (`read/evidence`, `command`, `proposal`, `approval`, `workflow`, `governance`, `trace/audit`, `scheduled`, or `reactive`);
- actors/callers, including human roles, functional agents, internal agents, workflows, services, timers, consumers, or support roles;
- AuthContext, tenant/customer scope, permissions/capabilities, denial behavior, and disabled-user behavior;
- input/output schemas, redaction, validation, idempotency, and safe error shape;
- data access, side effects, policy/approval gates, audit/work-trace fields, and retention;
- selected capability exposure channels: structured surface action, browser API, agent tool, workflow step, timer, consumer, MCP, view, or internal method;
- success, validation, forbidden, tenant-isolation, idempotency, approval, audit, rendering, and realtime tests.

## Test contract

A structured surface is not implementable until tests are named for:

- payload rendering for ready, loading, empty, error, forbidden, conflict, stale/reconnecting, partial-data, and success states;
- action submission shape, idempotency key generation/preservation, and response/result surface behavior;
- backend capability authorization for authorized, missing role/scope, disabled account, wrong tenant/customer, and support/auditor boundary cases;
- audit/work-trace creation for payload access, action denial, approval, side effects, and agent/tool work;
- realtime event parsing, duplicate event no-op behavior, reconnect/stale behavior, and malformed event safety;
- accessibility and responsive behavior for the surface type, especially tables, forms, charts, decision cards, and timelines;
- frontend secret boundary: no sensitive data or provider secrets in static assets, payloads, browser state, logs, or test fixtures.

## Handoff checklist

Before moving from surface design to code generation, verify:

- [ ] Surface has stable identity, type, version, owner/reuse, placement, and purpose.
- [ ] Payload schema is typed, scoped, redacted, traceable, and frontend-safe.
- [ ] Every action maps to a governed backend capability.
- [ ] Backend authorization remains authoritative over UI visibility, prompt text, and tool descriptions.
- [ ] Events define ordering, dedupe, reconnect, stale, and cross-context handling.
- [ ] UI states and accessibility/responsive expectations are explicit.
- [ ] Rendering, capability/action, authorization, tenant-isolation, audit/trace, realtime, and frontend secret-boundary tests are defined.
