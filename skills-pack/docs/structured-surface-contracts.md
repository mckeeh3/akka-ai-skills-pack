# Structured surface contracts

Use this document when defining or implementing typed surfaces in an agent workstream application. It turns the surface guidance from `./agent-workstream-application-architecture.md` and the requirements-to-workstream process in `./requirements-to-workstream-development-process.md` into an implementation contract for app descriptions, frontend code, HTTP APIs, realtime events, capability modeling, and tests.

Source-controlled SaaS Foundation App assets live under `templates/ai-first-saas-core-app/app-description/**`. Use them as copy/adapt examples for the five-core workstream domain surface layer when a target project lacks an app-description surface baseline. Validate adapted target-project contracts with `tools/validate-surface-contracts.sh <app-description-dir>` when available.

A surface is a structured renderable artifact in a **functional/context-area agent** workstream; this document shortens the term to **functional agent** after first use. It is not a page, route, chat message, CRUD screen, endpoint, view, or Akka component. Routes, endpoints, views, tools, workflows, and frontend components realize or expose the surface after its contract is clear. All renderable system feedback in a workstream is also a surface, not an ad hoc UI string.

## Contract rule

Every surface contract must preserve this chain:

```text
functional/context-area agent workstream placement
→ attention/dashboard purpose where applicable
→ surface type and payload schema
→ allowed actions and events
→ governed backend capabilities
→ backend authorization, audit, and trace
→ frontend rendering and tests
```

For broad requirements, surfaces are discovered through the canonical process: workstream → attention categories → role-specific dashboard/`WorkstreamAttentionSummary` → human surface graph → structured surfaces/actions → governed backend capabilities and governed-tools → Akka substrate. Do not design surfaces as page-first or CRUD-first artifacts.

Frontend action visibility is advisory only. The linked backend capability and governed-tool contract remain authoritative for authentication, selected `AuthContext`, tenant/customer scope, membership status, role/capability checks, approval policy, idempotency, side effects, audit, and denial behavior.

## Surface graph contract

A workstream's surfaces form a **surface graph** rather than a flat page list. The role-specific dashboard is usually the graph trunk: it summarizes attention, exposes the next safe actions, and links to evidence, detail, decision, workflow, trace, and system-message nodes.

Define the graph explicitly enough that implementation can preserve navigation, authority, and traceability:

- **Nodes:** dashboard surfaces, attention-item surfaces, tables, forms, detail cards, decision/approval cards, workflow/task progress surfaces, audit/trace timelines, `markdown_response`, and `system_message` surfaces.
- **Edges:** surface actions, surface-request actions, deep links, prompt-entered requests, realtime refreshes, workflow/AutonomousAgent progress updates, and system-message results.
- **Edge contract:** every edge maps to a governed backend capability and, when executable, to a governed-tool exposure such as a browser-tool, agent-tool, workflow-tool, timer-tool, consumer-tool, MCP-tool, or internal-tool.
- **Result semantics:** an action should append a surface, update a surface, open another graph node, create a typed `system_message`, update dashboard attention, start/observe internal-agent work, or return a safe denial.
- **Role variants:** dashboard and graph edges may differ by role and selected `AuthContext`; the backend capability remains authoritative for allowed traversal and data access.
- **Tests:** graph tests must cover dashboard-to-node traversal, action result surfaces, denial/system-message surfaces, stale/reconnect behavior, audit/trace links, and tenant-isolated deep links.

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

## Attention, dashboard, and task progress surfaces

Dashboard and attention surfaces are first-class structured surfaces. A normal workstream dashboard is scoped primarily to its owning workstream and should expose what is happening, what needs the current user's attention, what is blocked/overdue/risky/failed/paused, who or what is participating, pending decisions/approvals, recent changes, and authorized next actions. My Account is the main aggregate exception: its dashboard may summarize attention across accessible workstreams and open target workstream dashboards or attention items through governed surface-request actions.

Use backend-produced attention projections for left rail badges, My Account aggregate panels, dashboard summary cards, digests, and briefing surfaces. The compact summary shape should align with `WorkstreamAttentionSummary` from `./requirements-to-workstream-development-process.md`; detailed attention items should link to their owning workstream, source event/message/trace, relevant capability, and any linked AutonomousAgent task. The SaaS Foundation App implements this as a shared attention backbone plus bounded producers/update refresh; generated apps must not treat frontend-only badge state, fixtures, or demo data as authoritative actionable attention.

AutonomousAgent task progress/result surfaces are required when durable internal/background model-driven work is user-visible or creates decisions, exceptions, approvals, failures, rejected results, blocked dependencies, or recommendations. Task progress notifications can update the surface, but task progress and attention state must derive from governed backend task/projection state. Actions such as `open_attention_item`, `retry_failed_action`, `request_approval`, `escalate`, `dismiss`, `start_investigation`, or `open_task_result` must map to governed capabilities.

## Base surface: `markdown_response`

Use `markdown_response` as the smallest valid structured surface for the SaaS Foundation App domain (My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy) and other low-ceremony explanatory replies. It is a structured surface, not a raw chat transcript or untyped assistant message.

Contract:

| Field | Requirement |
|---|---|
| Surface identity | `surfaceType: "markdown_response"`; stable `surfaceId`; semantic `surfaceVersion`; owner functional agent; workstream entry id. |
| Payload data | `markdown` string, optional `title`, optional `summary`, optional `sections[]` with stable anchors, optional `codeBlockLanguageHints[]`, and optional `sourceRefs[]` for cited capabilities, documents, or trace/evidence records. |
| Traceability | Include `correlationId`, `traceIds`, producing `agentId`, producing `workstreamEntryId`, selected `AuthContext`, and response generation timestamp. |
| Rendering | Render markdown only through an approved markdown parser and sanitizer pipeline; browser output is sanitized HTML, not raw model HTML. |
| Sanitization | Strip or neutralize raw HTML that can execute code, `<script>`, event-handler attributes, dangerous URL schemes such as `javascript:`, unsafe iframes/embeds, inline styles unless explicitly allow-listed, and untrusted target behavior. External links must be transformed according to the UI security policy, usually with safe `rel` attributes and visible destination affordance. |
| Redaction | Markdown content must already be scoped and redacted by the producing capability/agent; the renderer must not reveal hidden fields, secrets, prompt text, provider credentials, cross-tenant data, or support-only facts. |
| Actions | Prefer no consequential inline actions in SaaS Foundation App scope. Allowed actions, when present, use the normal `SurfaceAction` shape and link to governed capabilities such as `open_trace`, `copy_response`, `retry_request`, `request_clarification`, or `open_follow_up_task`; backend authorization remains authoritative. |
| UI states | Define loading/generating, ready, empty, error, forbidden, stale/reconnecting, and redacted states. Forbidden and redacted states must avoid leaking the original unsafe content. |
| Accessibility | Preserve semantic headings, lists, tables, code blocks, blockquotes, and links; provide keyboard navigation, visible focus, readable code-block wrapping/copy affordance where allowed, screen-reader-friendly status changes, and heading hierarchy that does not skip the surrounding shell structure. |
| Realtime | If streamed or updated incrementally, partial markdown must be rendered safely at every increment or shown as plain text until finalized; reconnect either resumes from a safe event id or marks the surface stale and requests refresh. |
| Tests | Cover markdown-to-sanitized-HTML rendering, blocked scripts/event handlers/unsafe links, code-block and table rendering, trace/correlation link presence, loading/empty/error/forbidden/redacted states, accessibility semantics, stale/reconnect behavior when used, frontend secret boundaries, and backend authorization for any action. |

Minimal type-specific data shape:

```ts
type MarkdownResponseData = {
  markdown: string;
  title?: string;
  summary?: string;
  workstreamEntryId: string;
  producingAgentId: string;
  sourceRefs?: Array<{
    refType: "capability" | "trace" | "document" | "evidence";
    refId: string;
    label: string;
  }>;
  sections?: Array<{
    anchor: string;
    title: string;
  }>;
};
```

`markdown_response` is acceptable for first-slice guidance, explanations, denials, and summaries. Do not use it to hide missing typed surfaces for decisions, approvals, forms, tables, settings, access reviews, audit timelines, or workflow status once those richer interactions are required.

## Base surface: `system_message`

Use `system_message` for typed system feedback in a workstream: success confirmations, denials, validation failures, warnings, approval-required notices, background-work-started notices, stale/reconnect notices, deferred-capability messages, no-op results, and safe recovery guidance. It is a structured surface, not a toast-only string or untracked frontend branch.

Contract:

| Field | Requirement |
|---|---|
| Surface identity | `surfaceType: "system_message"`; stable `surfaceId`; semantic `surfaceVersion`; owner functional agent; workstream entry id. |
| Payload data | Severity, message code, user-safe title/body, optional details, recovery actions, related surface/action ids, related capability id, correlation id, and trace ids. |
| Authority | Denial and forbidden messages must come from backend capability decisions where protected data or actions are involved; frontend-only hiding is not authorization. |
| Redaction | Do not leak secrets, missing privileged facts, prompt content, provider details, cross-tenant data, or hidden fields in error/recovery text. |
| Actions | Recovery, retry, open trace, request approval, request help, or navigate-to-surface actions use normal `SurfaceAction` with capability ids. |
| Tests | Cover success, warning, validation, forbidden, approval-required, deferred, stale/reconnect, no-op, redaction, trace-link, and action-to-capability behavior. |

Minimal type-specific data shape:

```ts
type SystemMessageData = {
  severity: "info" | "success" | "warning" | "error" | "forbidden";
  code: string;
  title: string;
  body: string;
  workstreamEntryId: string;
  relatedCapabilityId?: string;
  relatedSurfaceId?: string;
  relatedActionId?: string;
  recovery?: Array<{
    label: string;
    actionId?: string;
    description?: string;
  }>;
};
```

## Surface action shape

Each surface action is a browser-tool or surface-request exposure of a governed capability. Use `governed-tool` for the semantic executable contract and qualified exposure terms (`browser-tool`, `agent-tool`, `workflow-tool`, `timer-tool`, `consumer-tool`, `MCP-tool`, `internal-tool`) when describing where it is exposed.

```ts
type SurfaceAction = {
  actionId: string;
  label: string;
  intent: "read" | "surface-request" | "command" | "proposal" | "approval" | "workflow" | "governance" | "trace";
  browserToolId: string;
  governedToolId: string;
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
  shellRequest?: {
    requestType: "show_surface" | "open_workstream" | "refresh_surface" | "open_attention_item";
    targetFunctionalAgentId?: string;
    targetSurfaceId?: string;
    targetItemId?: string;
    displayText?: string;
  };
  audit: {
    eventType: string;
    traceRequired: boolean;
  };
};
```

Action rules:

- `browserToolId`, `governedToolId`, and `capabilityId` are required for every action, including read/query actions and surface-request actions such as show surface, show dashboard, open workstream, search, open detail, row-click-to-open-detail, refresh, open trace, and view audit timeline.
- `browserToolId` identifies the human-facing surface-action exposure, `governedToolId` identifies the executable semantic operation, and `capabilityId` identifies the product-level capability grouping that owns the governed-tool contract.
- The capability definition owns the governed-tool contract: input validation, authorization, idempotency, side effects, policy/approval, audit, and denial shape.
- In browser realization, a surface action usually invokes a backend API; the API returns an accepted/denied/error result plus a new surface, updated surface, workstream item, workflow/progress surface, or `system_message` surface.
- Side-effecting actions should default to proposal or approval flows unless a bounded autonomous policy is explicitly accepted.
- Disabled/hidden actions must include a user-safe reason when visible; they must not be the only authorization control.
- Action results should append a new surface, update the current surface, or render workflow/progress/system-message state explicitly.
- Surface-request actions should include `shellRequest` metadata where the action opens a specific surface, workstream, or attention item. The shell renders a prompt-like request item using the canonical prompt in the target workstream while preserving origin metadata for audit; deep links must use the same pipeline.

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
    | "surface.autonomous_task.progressed"
    | "surface.attention.changed"
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

- capability id and class (`read/evidence`, `command`, `proposal`, `approval`, `workflow`, `autonomous task`, `governance`, `trace/audit`, `scheduled`, or `reactive`);
- actors/callers, including human roles, functional agents, internal agents, workflows, services, timers, consumers, or support roles;
- AuthContext, tenant/customer scope, permissions/capabilities, denial behavior, and disabled-user behavior;
- input/output schemas, redaction, validation, idempotency, and safe error shape;
- data access, side effects, policy/approval gates, audit/work-trace fields, and retention;
- selected capability exposure channels: structured surface action, browser API, agent tool, workflow step, timer, consumer, MCP, view, or internal method;
- success, validation, forbidden, tenant-isolation, idempotency, approval, audit, rendering, and realtime tests.

## Test contract

A structured surface is not implementable until tests are named for:

- payload rendering for ready, loading, empty, error, forbidden, conflict, stale/reconnecting, partial-data, and success states;
- prompt-entered and action-entered shell request behavior for `show_surface`, `open_workstream`, deep links, My Account panels, canonical prompt feedback, origin metadata, and target-workstream-only request rendering;
- action submission shape, idempotency key generation/preservation, and response/result surface behavior;
- backend capability authorization for authorized, missing role/scope, disabled account, wrong tenant/customer, and support/auditor boundary cases;
- audit/work-trace creation for payload access, action denial, approval, side effects, and agent/tool work;
- realtime event parsing, duplicate event no-op behavior, reconnect/stale behavior, and malformed event safety;
- accessibility and responsive behavior for the surface type, especially tables, forms, charts, decision cards, and timelines;
- frontend secret boundary: no sensitive data or provider secrets in static assets, payloads, browser state, logs, or test fixtures.

## Source template and validation support

The source repository includes an app-description-only SaaS Foundation App template:

```text
templates/ai-first-saas-core-app/app-description/
  12-workstreams/surfaces-index.md
  12-workstreams/surface-contracts/*.md
  55-ui/structured-surface-rendering.md
  70-traceability/surface-to-capability-map.md
```

Use that template for SaaS Foundation App surface shape and field density; copy only the files relevant to the target project and then adapt ids, roles, capabilities, tests, and domain-specific surfaces. The template is not a generated runtime baseline. For a compact non-core domain example, see `./examples/domain-workstream-surface-contract-example.md`.

`tools/validate-surface-contracts.sh` performs lightweight structural checks for target app-description trees: index presence, referenced contract files, required contract fields, action/capability/governed-tool exposure details, auth/security, redaction, trace/correlation, tests, and traceability-map coverage. It is intentionally a guardrail, not a substitute for reviewing product semantics.

## Handoff checklist

Before moving from surface design to code generation, verify:

- [ ] Surface has stable identity, type, version, owner/reuse, placement, and purpose.
- [ ] Payload schema is typed, scoped, redacted, traceable, and frontend-safe.
- [ ] Every action, including surface-request/read actions, maps to a governed backend capability.
- [ ] Backend authorization remains authoritative over UI visibility, prompt text, and tool descriptions.
- [ ] Events define ordering, dedupe, reconnect, stale, and cross-context handling.
- [ ] UI states and accessibility/responsive expectations are explicit.
- [ ] Rendering, capability/action, authorization, tenant-isolation, audit/trace, realtime, and frontend secret-boundary tests are defined.
- [ ] SaaS Foundation App or repaired app-description surface layers pass `tools/validate-surface-contracts.sh <app-description-dir>` when the tool is available.
