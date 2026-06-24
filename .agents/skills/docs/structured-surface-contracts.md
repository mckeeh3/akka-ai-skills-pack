# Structured surface contracts

Use this document when defining or implementing typed surfaces in an agent workstream application. It turns the surface guidance from `./agent-workstream-application-architecture.md`, the compact workstream fields in `./workstream-contract.md`, the attention contracts in `./workstream-attention-contracts.md`, the worker/tool layer separation in `./app-worker-tool-model.md`, and the requirements-to-workstream process in `./requirements-to-workstream-development-process.md` into an implementation contract for app descriptions, frontend code, HTTP APIs, realtime events, capability modeling, and tests.

Source-controlled SaaS Foundation App assets live under `templates/ai-first-saas-core-app/app-description/**`. Use them as copy/adapt examples for the five-core workstream domain surface layer when a target project lacks an app-description surface baseline. Validate adapted target-project contracts with `tools/validate-surface-contracts.sh --mode template <app-description-dir>` for process/template baselines or `tools/validate-surface-contracts.sh --mode implementation <app-description-dir>` for app-level contracts. Implementation validation is readiness-aware; `capability-ready` and higher scopes must not rely on unresolved deferred result surfaces.

A surface is a structured renderable artifact in a **functional/context-area agent** workstream; this document shortens the term to **functional agent** after first use. It is not a page, route, chat message, CRUD screen, endpoint, view, or Akka component. Routes, endpoints, views, tools, workflows, and frontend components realize or expose the surface after its contract is clear. All renderable system feedback in a workstream is also a surface, not an ad hoc UI string. Internal activity records, tool-call details, trace bookkeeping, and response metadata are not separate user-visible surfaces unless the product contract deliberately exposes them as a typed audit/progress/detail surface. Even then, the contract must distinguish default user-readable summaries from privileged raw audit/support diagnostics.

If asked to summarize workstream surfaces, always include the collection-object progression: durable collections such as users, customers, orders, policies, agents, invitations, and governed documents use delegated, single-purpose surfaces by default. A domain list/search surface discovers and selects objects; every listed row/card opens a lifecycle-aware show/inspection surface; show/inspection surfaces delegate mutation to separate create, edit, destructive lifecycle confirmation, or domain-specific task surfaces. Do not describe generic combined CRUD pages as the default workstream surface model.

## Contract rule

Every surface contract must preserve this chain:

```text
functional/context-area agent workstream placement
→ attention/dashboard purpose where applicable
→ surface type and payload schema
→ composer surface-intent route and safe prefill behavior where applicable
→ allowed actions and events
→ governed backend capabilities
→ backend authorization, audit, and trace
→ frontend rendering and tests
```

For broad requirements, surfaces are discovered through the canonical process: workstream → attention categories → role-specific dashboard/`WorkstreamAttentionSummary` → delegated human surface graph → single-purpose structured surfaces/actions → governed backend capabilities and governed-tools → Akka substrate. Do not design surfaces as page-first or CRUD-first artifacts. CRUD-like collection objects are allowed only through the canonical collection-object surface progression below, not as generic catch-all CRUD screens.

Frontend action visibility is advisory only. The linked backend capability and governed-tool contract remain authoritative for authentication, selected `AuthContext`, tenant/customer scope, membership status, role/capability checks, approval policy, idempotency, side effects, audit, and denial behavior. Those internal ids and mechanics must not become normal SaaS user copy; surface contracts translate them into task-oriented language and expose raw identifiers only in role-gated diagnostics, audit, support, or developer surfaces.

Surfaces are the default human-backed actor's tool-use interface. A surface action should be understood as a human-facing adapter for a governed workstream tool: it supplies labels, fields, validation, evidence, confirmations, disabled/denied states, result surfaces, and trace links so the authenticated human supervisor can safely use the operation. The same governed tool may also be exposed as a confirmed human chat tool plan, AI agent tool, workflow/internal tool, API, or MCP tool, but those exposures must be declared separately in the capability/governed-tool map and tool boundary. Do not duplicate business semantics between the surface action, chat plan, and agent tool; all should point back to the same capability-backed operation.

For composer-enabled workstreams, model deterministic surface intent routes from `./workstream-surface-intent-routing.md` as surface graph edges. A route may open, refresh, or prepopulate a surface from a high-confidence natural-language request, but it must not submit side-effecting commands. Prefill is advisory visible form state; the user still reviews and submits through the normal backend-authorized surface action. This no-mutation router rule does not forbid a separately modeled `human_chat_tool_plan` adapter; that path must propose a detailed plan, bind explicit confirmation to the plan, execute governed-tools through backend authorization and idempotent transaction boundaries, and return result/partial-failure surfaces with traces.

## Surface graph contract

A workstream's surfaces form a **surface graph** rather than a flat page list. The role-specific dashboard is usually the graph trunk.

### Mandatory dashboard action-router rules

A workstream dashboard is not a report, passive status page, or decorative card grid. It is a router to governed work inside that workstream. Dashboard design and implementation must obey these rules:

1. **Only actionable dashboard content is visible.** Aside from section labels, control labels, and minimal explanatory microcopy, visible dashboard objects must be clickable or keyboard-operable indicators that route to something the current actor can inspect, decide, approve, assign, fix, retry, resume, configure, dismiss, learn from, or otherwise do.
2. **Top-to-bottom hierarchy is fixed.** Put **things that need my attention** first, then **things I can do**. Lower-priority context may only appear after those regions and should still be attached to an actionable object or drilldown.
3. **Every indicator answers “what can I do with this?”** Cards, rows, counters, chart segments, badges, icons, task/progress panels, shortcuts, and buttons must declare the surface/action they open or execute. Passive FYI metrics, inert charts, and unlabeled status tiles do not belong on a workstream dashboard; move them to a report/detail/analytics surface or make them an operable drilldown.
4. **Dense indicators are valid controls.** A rectangular metric/attention tile with a label and large count is itself the button. A zero count is still a useful actionable indicator when it opens an empty queue, explanation, setup, history, or validation surface; “0 things need your attention” is better than hiding the category or rendering no indicator.
5. **Context supports action; it does not replace action.** Brief reason text, freshness, evidence hints, and helper copy are allowed only as subordinate microcopy that clarifies why the action matters.

Activation should append a request-like workstream item and then append/open a structured surface, attention item, detail/decision/progress surface, trace/evidence node, governed capability-backed action result, updated dashboard, or typed `system_message`. Do not reduce dashboard interactivity to a separate row of buttons; the visible things that represent work should be operable.

Define the graph explicitly enough that implementation can preserve navigation, authority, and traceability:

- **Nodes:** dashboard surfaces, attention-item surfaces, domain list/search surfaces, show/inspection surfaces, create forms, edit forms, destructive lifecycle confirmation surfaces, decision/approval cards, workflow/task progress surfaces, audit/trace timelines, `markdown_response`, and `system_message` surfaces. Each node has exactly one owner functional agent; reusable surfaces declare explicit reusable-by functional agents/workstreams.
- **Edges:** surface actions, surface-request actions, deep links, prompt-entered requests, realtime refreshes, workflow/AutonomousAgent progress updates, and system-message results.
- **Dashboard object interactions:** every attention or next-action dashboard object declares an interaction such as `open_attention_item`, `show_surface`, `refresh_surface`, `open_trace`, a governed `surface_action` adapter, or `none` with a reason. Non-actionable objects are explicit exceptions and should not look like actionable work objects.
- **Edge contract:** every edge maps to a governed backend capability and, when executable, to a governed-tool exposure through a declared actor adapter such as `surface_action`, confirmed `human_chat_tool_plan`, `agent_tool_call`, `workflow_step`, `timer_invocation`, `consumer_reaction`, `mcp_tool_call`, `api_call`, or `internal_call`. If the same governed tool is exposed to surface, chat, and/or AI-backed actors, the mapping must preserve one operation id while declaring separate actor adapters, input mediation, confirmation/approval behavior, and trace source.
- **Result semantics:** a dashboard interaction or surface action should append one prompt-like request item and one primary browser-visible result surface, update an existing surface, open another graph node, create a typed `system_message`, update dashboard attention, or start/observe internal-agent work. A separate renderable request surface is not the default result for a submitted form or button click; use one only when the request itself needs user inspection, confirmation, approval, validation repair, or visible async progress. Successful edits commonly return the updated originating/show surface, while successful creates commonly return the created object's show/inspection surface or an updated collection/list surface. For example, a settings save can append `Save settings changes` and then return the updated settings surface; an invitation submit should append the invite request/action-feedback item and then return invitation detail, an updated invitations list, validation-error form state, approval-required decision surface, conflict/no-op surface, or denial `system_message`, not a redundant invite-request surface. Do not render a separate activity/detail surface for the same result unless the contract intentionally defines a visible progress/detail node. Forbidden targets should normally be omitted from ready dashboard payloads; denial `system_message` surfaces remain required for stale payloads, deep links, manual requests, races, or changed authorization.
- **Role variants:** dashboard and graph edges may differ by role and selected `AuthContext`; the backend capability remains authoritative for allowed traversal and data access.
- **Tests:** graph tests must cover dashboard-to-node traversal, action result surfaces, denial/system-message surfaces, stale/reconnect behavior, audit/trace links, and tenant-isolated deep links.

In app-description trees, surface ownership belongs in `12-workstreams/`: surface index and contracts, reusable functional-agent placement, action-to-capability mappings, trace semantics, and surface/action tests. `55-ui/` owns browser realization of those contracts: shell placement, route/deep-link mappings, components, forms/interactions, frontend API contracts, state/realtime behavior, accessibility/responsive behavior, selected style guide, named-theme contract, and component-catalog application. Do not redefine surface purpose, authority, or capability semantics in `55-ui/`; link back to `12-workstreams/` and capability/security/test layers.

A browser-rendered surface is not implementation-ready until its realization path names the selected app UI style artifact and the component-catalog anatomy it uses, or a blocking UI question records why that selection is missing. Intent-compiler planning must route such work through `app-description-ui` and `akka-web-ui-ux-design` before frontend implementation tasks.

Before moving a new or substantially changed dashboard, command center, queue, decision, audit, workflow/progress, form, table, detail, or other browser-rendered surface from description to implementation, ask these surface-description sufficiency review questions:

> Is this surface definition sufficiently unambiguous that a developer or generator can implement and review the surface without inventing payload fields, actions, states, auth/tenant behavior, trace links, tests, or visual/component semantics?
>
> Would the target SaaS user understand the default surface without knowing the app's architecture, policies, tool ids, trace model, provider/model setup, or backend component names?
>
> Does every default-visible field, badge, chart, id, trace/evidence item, and action help the user decide, act, recover, or understand a business outcome?

If any answer is no, make another pass on the surface description first. The developer review loop should inspect both artifacts: the generated surface in the running app and the current-intent surface description that drives code generation. Fixes to UI behavior should normally refine the description, then use that refined description to revise or repair surface-related code.

## Canonical collection-object surface progression

When a workstream exposes a durable collection of domain things such as users, customers, orders, policies, agents, invitations, or governed documents, use this pattern by default unless an app-description explicitly records why a different surface graph is safer. Name each surface domain-semantically, such as `customerDirectory`, `customerProfile`, `newCustomerIntake`, `customerEditForm`, or `customerArchiveConfirmation`; do not use generic `thing.list/show/create` names in app-facing contracts.

The progression is a delegated surface graph, not a page stack:

```text
owning functional-agent workstream
→ domain list/search surface
  → select one listed object
    → domain show/inspection surface, chosen by object lifecycle state when needed
      → domain-specific single-action surfaces such as edit, archive, revoke, approve, retry, or activate
→ domain create surface
  → created-object show/inspection surface
```

Mandatory surfaces for a collection object:

| Surface role | One job | Required graph behavior |
|---|---|---|
| List/search surface | Discover and select existing objects. | May search, filter, sort, and paginate. Every listed row/card is clickable and keyboard-operable, maps to a `show_surface`/read capability, and opens the appropriate show/inspection surface for that object's current lifecycle state. |
| Show/inspection surface | Inspect one object and choose the next allowed task. | Shows frontend-safe identity, status, summary, relationships, traces, and available task entry points. It does not directly perform consequential edits or deletion; it delegates to dedicated action surfaces. The selected show surface may vary by object lifecycle state, such as active, inactive, pending, failed, or archived. |
| Create surface | Create one new object. | Owns the creation form, validation, idempotency, submission, success/failure states, authorization, and audit. Success opens the created object's show/inspection surface. |
| Edit surface | Update mutable fields for one existing object. | Separate from show/inspection. Owns editable fields, validation, conflict/stale handling, idempotency, success/failure states, authorization, and audit. Success returns to or refreshes the object's show/inspection surface. |
| Destructive lifecycle confirmation surface | Perform one destructive or access-affecting lifecycle action. | Separate from show/inspection. Covers delete, archive, revoke, deactivate, cancel, or equivalent domain action with consequence copy, confirmation, authorization, idempotency, audit/work trace, and a typed result surface. Prefer domain lifecycle language over physical delete unless true deletion is required. |

Rules:

- Each surface does one thing well. Do not combine list, show, create, edit, and destructive lifecycle work into one broad surface.
- Show surfaces expose task entry points; dedicated task surfaces perform consequential mutation.
- Lifecycle state may determine the next show or task surface. For example, clicking an active customer may open an active-customer profile, while clicking an inactive customer may open a reactivation-focused inspection surface.
- Domain-specific task surfaces extend the pattern for non-CRUD lifecycles: approve, reject, suspend, resume, retry, invite, activate version, request evidence, merge, export, or similar single-purpose tasks.
- Bulk selection may appear on a list surface only as selection/discovery. Any bulk mutation must open a separate bulk-action surface contract; do not make bulk behavior implicit in the list.
- Every row/card selection, create/edit/delete submission, lifecycle transition, and result maps to a governed capability, `surface_action`/browser adapter id where applicable, governed-tool id, authorization decision, audit/work trace, and denial/result surface.

## Minimum contract fields

For each surface, define these fields before implementation:

| Field | Required content |
|---|---|
| Surface identity | Stable `surfaceId`, display name, canonical type, semantic version, exactly one owner functional agent, explicit reusable-by agents/workstreams, lifecycle status. |
| Placement | Owning functional agent, reusable functional agents, workstream entry point, embedded/drill-in/modal/side-panel/deep-link placement. |
| Purpose | User outcome, business context, and when the surface should appear or refresh. |
| Payload schema | Typed render payload, field formats, required/optional fields, nested records, pagination/sort/filter metadata, version/stale markers, and any trace/correlation ids required by authorized diagnostic or audit views. Split fields into default user-visible, user-requested drilldown, admin/support/auditor-only, and internal-only implementation metadata. |
| Redaction | Role-dependent fields, PII/secret boundaries, support/auditor visibility, frontend-safe fields, agent-safe fields, and explicit rules that prevent normal user views from showing raw policy ids, governed-tool ids, backend component names, provider/model details, prompt internals, raw event ids, or correlation/idempotency mechanics. |
| Data source | Read/evidence capabilities and view/query sources that produce the payload; no raw unscoped state dumps by default. |
| Actions | Allowed human-backed and AI-backed worker actions, labels, input payloads, confirmation/approval requirements, idempotency keys, result states, linked capability ids, governed-tool ids, actor adapters/exposure channels, and trace source such as `surface_action`, `human_chat_tool_plan`, or `agent_tool_call`. Dashboard cards/rows/counters/badges/charts/task panels/shortcuts that represent attention or next work must declare their interaction target, full-shape hit area, request/result append behavior, zero-count behavior where applicable, and `none` reason only when intentionally non-actionable. |
| Surface intent routes | High-confidence composer prompt aliases/patterns, target surface, prefill field mapping, ambiguity behavior, required capability, no-mutation guarantee, denial/system-message shape, and route-decision trace. |
| Events | Realtime/update events, event ids, ordering/dedupe rules, reconnect behavior, partial update semantics, stale markers. |
| Authority | AuthContext assumptions, tenant/customer scope, role/capability requirements, policy gates, denial shape, disabled-user behavior. |
| Audit/trace | Audit event types, work-trace fields, user-readable trace summaries, role-gated trace links, correlation ids/evidence references only when visible to the current actor, retention/redaction expectations, and raw-audit/support detail boundaries. |
| UI states | Loading, empty, ready, submitting, success, pending, approval-needed, error, forbidden, conflict, stale, reconnecting, partial-data, and no-op states. |
| Accessibility/responsive | Keyboard path, labels, focus behavior, status announcements, narrow-layout strategy, chart/table alternatives. |
| Style/catalog binding | Selected UI style guide artifact, named theme/default theme expectation, component-catalog anatomy used for the surface, and forbidden ad hoc/generic visual patterns. |
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
  correlationId?: string;
  traceIds?: string[];
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
  visibility?: {
    defaultVisibleFieldKeys?: string[];
    drilldownFieldKeys?: string[];
    adminSupportAuditorFieldKeys?: string[];
    internalOnlyFieldKeys?: string[];
  };
  data: TData;
  actions: TAction[];
  links?: SurfaceLink[];
};
```

Payload rules:

- Each user turn or surface action should produce at most one primary browser-visible result surface for the same content. The workstream should still append a prompt-like request/action-feedback item for conversational history, but should not also render a separate request surface unless the request itself requires inspection, confirmation, approval, validation repair, or async progress tracking. A progress surface followed by a final result is allowed only when durable/background work is intentionally visible.
- Activity-log rows, tool-call records, trace metadata, and response bookkeeping should be stored as workstream/audit history or collapsed details, not duplicated as sibling surfaces.
- Include `surfaceVersion` and type-specific schema version when payload semantics change.
- Include `correlationId` and trace ids for audit/debugging surfaces and consequential actions only when they are needed by the browser contract and visible to the current role. Do not make diagnostic ids prominent in ordinary user views.
- Include stale/reconnect markers when rendered data may lag event streams or command results.
- Return frontend-safe, scoped, redacted fields only. Do not send hidden secrets, cross-tenant data, privileged evidence, prompt internals, raw provider/model data, or internal-only metadata and rely on the UI not to display them.
- Use role-specific action lists only as a UX hint; backend denial must still be correct if an action is submitted manually.

## Attention, dashboard, and task progress surfaces

Dashboard and attention surfaces are first-class structured surfaces. A normal workstream dashboard is scoped primarily to its owning workstream and must apply the mandatory dashboard action-router rules above. Organize the ready state around two primary actionable goals for the current authorized user in the selected `AuthContext`: first, put what needs the current user's attention now at the top of the dashboard; second, show the authorized actions, shortcuts, and safe next steps the current user can take. The attention-first region should expose blocked/overdue/risky/failed/paused work, pending decisions/approvals, and items requiring inspection, correction, retry, escalation, acknowledgement, dismissal, or learning. Those exposed things must be directly operable through click and keyboard activation by default, leading to the surface where the actor can view details and take allowed action. Supporting context such as what is happening, who or what is participating, recent changes, and routine status must be subordinate to actionable indicators, not standalone dashboard content. If the user would reasonably ask “so what can I do with this information?”, the item belongs in a report/detail/analytics surface or must be converted into an actionable drilldown. My Account is the main aggregate exception: its dashboard may summarize attention across accessible workstreams and open target workstream dashboards or attention items through governed surface-request actions.

Use backend-produced attention projections for left rail badges, My Account aggregate panels, dashboard summary cards, digests, and briefing surfaces. The compact summary shape should align with `WorkstreamAttentionSummary` from `./requirements-to-workstream-development-process.md`; detailed attention items should link to their owning workstream, source event/message/trace, relevant capability, and any linked AutonomousAgent task. The SaaS Foundation App implements this as a shared attention backbone plus bounded producers/update refresh; generated apps must not treat frontend-only badge state, fixtures, or demo data as authoritative actionable attention.

AutonomousAgent task progress/result surfaces are required when durable internal/background model-driven work is user-visible or creates decisions, exceptions, approvals, failures, rejected results, blocked dependencies, or recommendations. Task progress notifications can update the surface, but task progress and attention state must derive from governed backend task/projection state. Actions such as `open_attention_item`, `retry_failed_action`, `request_approval`, `escalate`, `dismiss`, `start_investigation`, or `open_task_result` must map to governed capabilities.

## Base surface: `markdown_response`

Use `markdown_response` as the smallest valid structured surface for the SaaS Foundation App domain (My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy) and other low-ceremony explanatory replies. It is the actual markdown result surface for that turn, not a raw chat transcript, untyped assistant message, or companion to a separate "agent response detail" surface.

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

`markdown_response` is acceptable for first-slice guidance, explanations, denials, and summaries. Do not use it to hide missing typed surfaces for decisions, approvals, forms, tables, settings, access reviews, audit timelines, or workflow status once those richer interactions are required. Do not render both a generic agent-response/activity surface and a `markdown_response` for the same markdown content; the `markdown_response` envelope is the renderable result, while underlying activity/trace records stay collapsed or audit-only unless separately requested.

## Base surface: `system_message`

Use `system_message` for typed system feedback in a workstream: success confirmations, denials, validation failures, warnings, approval-required notices, background-work-started notices, stale/reconnect notices, deferred-capability messages, no-op results, and safe recovery guidance. It is a structured surface, not a toast-only string or untracked frontend branch.

Contract:

| Field | Requirement |
|---|---|
| Surface identity | `surfaceType: "system_message"`; stable `surfaceId`; semantic `surfaceVersion`; owner functional agent; workstream entry id. |
| Payload data | Severity, message code, user-safe title/body, optional details, recovery actions, and related surface/action/capability/correlation/trace ids only as role-appropriate diagnostic metadata rather than primary user copy. |
| Authority | Denial and forbidden messages must come from backend capability decisions where protected data or actions are involved; frontend-only hiding is not authorization. |
| Redaction | Do not leak secrets, missing privileged facts, prompt content, provider details, internal policy/capability ids, cross-tenant data, or hidden fields in error/recovery text. |
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

Each surface action is the `surface_action` actor adapter or surface-request adapter for a governed tool inside a capability. Use `governedToolId` for the semantic executable contract and qualified adapter terms (`surface_action`, `human_chat_tool_plan`, `agent_tool_call`, `workflow_step`, `timer_invocation`, `consumer_reaction`, `mcp_tool_call`, `api_call`, `internal_call`) when describing where it is exposed. A surface action and a confirmed chat plan may expose the same governed tool, but the surface contract remains responsible only for the browser/surface adapter; the chat-plan adapter belongs in the workstream tool map and expertise/runtime contract.

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

- `browserToolId`, `governedToolId`, and `capabilityId` are required implementation contract fields for every action, including read/query actions and surface-request actions such as show surface, show dashboard, open workstream, search, open detail, row-click-to-open-detail, refresh, open trace, and view audit timeline.
- `browserToolId` identifies the human-facing surface-action exposure, `governedToolId` identifies the executable semantic operation, and `capabilityId` identifies the product-level capability grouping that owns the governed-tool contract. These ids support implementation, tests, audit, and diagnostics; normal UI labels must use user-facing verbs and object names instead.
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

For every surface action and payload-producing query, the capability inventory must specify the details below. For workstreams at `capability-ready` or higher, also add the lightweight `surfaceActionMappings` entry in `12-workstreams/workstream-manifest.json` so validators can check the surface/action/capability/governed-tool link without duplicating this full contract.

- capability id and class (`read/evidence`, `command`, `proposal`, `approval`, `workflow`, `autonomous task`, `governance`, `trace/audit`, `scheduled`, or `reactive`);
- actors/callers, including human roles, functional agents, internal agents, workflows, services, timers, consumers, or support roles;
- AuthContext, tenant/customer scope, permissions/capabilities, denial behavior, and disabled-user behavior;
- input/output schemas, redaction, validation, idempotency, and safe error shape;
- data access, side effects, policy/approval gates, audit/work-trace fields, and retention;
- selected actor adapters/exposure channels: `surface_action`, confirmed `human_chat_tool_plan`, browser `api_call`, `agent_tool_call`, `workflow_step`, `timer_invocation`, `consumer_reaction`, `mcp_tool_call`, view/query, or `internal_call`;
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

`tools/validate-surface-contracts.sh` performs lightweight structural checks for target app-description trees. Use `--mode template` for process examples that may include compact mappings and deferred typed surfaces. Use `--mode implementation` for app-level contracts; it is readiness-aware and tightens checks for `capability-ready` and higher workstreams. Checks include index presence, referenced contract files, required contract fields, single-owner semantics, action/capability/governed-tool exposure details, auth/security, redaction, trace/correlation, tests, graph/map coverage, and deferred-surface handling. It is intentionally a guardrail, not a substitute for reviewing product semantics.

## Handoff checklist

Before moving from surface design to code generation, verify:

- [ ] Human surface-description sufficiency questions were asked and answered yes, or another description pass is queued/blocked before implementation.
- [ ] Surface has stable identity, type, version, exactly one owner functional agent, explicit reuse, placement, purpose, user goal, and primary user outcome.
- [ ] Payload schema is typed, scoped, redacted, traceable where appropriate, frontend-safe, and split into default user-visible, drilldown, role-gated diagnostic/audit, and internal-only metadata.
- [ ] Default-visible fields and copy use SaaS product language rather than internal component names, raw policy/tool/capability ids, prompt/provider details, or diagnostic ids.
- [ ] Every action, including surface-request/read actions, maps to a governed backend capability.
- [ ] Backend authorization remains authoritative over UI visibility, prompt text, model-generated plans, and tool descriptions.
- [ ] Any confirmed human chat tool-plan exposure reuses the same governed-tool id as the surface action, binds confirmation to the proposed plan, and records distinct trace source and result/partial-failure surfaces.
- [ ] Events define ordering, dedupe, reconnect, stale, and cross-context handling.
- [ ] UI states and accessibility/responsive expectations are explicit.
- [ ] Rendering, capability/action, authorization, tenant-isolation, audit/trace, realtime, and frontend secret-boundary tests are defined.
- [ ] SaaS Foundation App or repaired app-description surface layers pass `tools/validate-surface-contracts.sh <app-description-dir>` when the tool is available.
