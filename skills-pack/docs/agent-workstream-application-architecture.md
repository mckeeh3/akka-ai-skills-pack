# Agent Workstream Application Architecture

## Status and scope

This is the canonical agent workstream application architecture doctrine for this skills pack.
It defines the mandatory default UI/application model for generated full-stack secure AI-first SaaS applications.

Use this doctrine below `ai-first-saas-application-architecture.md` and above detailed app-description, web UI, agent, and Akka component guidance. For broad input, PRD, app-description, planning, backlog, and implementation-readiness work, apply `requirements-to-workstream-development-process.md` before component selection. This document does not replace `capability-first-backend-architecture.md`: workstream UI actions, agent tools, workflow steps, APIs, timers, consumers, and internal calls still map to governed backend capabilities before Akka implementation is selected.

## Default generated-app architecture

Generated AI-first SaaS apps are **agent workstream applications by default**. The **workstream is the root application abstraction** for authenticated consequential work. First use: **functional/context-area agent** means a user-facing, role-authorized agent for one durable work area; this document shortens the term to **functional agent** after defining the alias.

```text
workstream
→ exactly one backing functional/context-area agent
→ continuous request/result flow
→ structured renderable surfaces, including system-message surfaces
→ AI-first managed agents with configuration-driven prompts, tools, and traces
→ governed backend capabilities
→ horizontal Akka implementation
```

The primary application model is not a conventional page tree, CRUD console, or traditional app with a chatbot attached. Authenticated consequential work areas should be modeled as workstreams, each backed by exactly one functional agent and implemented through structured surfaces. Traditional routes may still exist for implementation, deep linking, static/public pages, asset hosting, or direct surface URLs, but they are not the primary architecture.

## Requirements-to-workstream obligations

For generated SaaS requirements, preserve this vertical chain from the canonical requirements-to-workstream process:

```text
input / PRD / feature request / incremental change
→ affected workstream inventory
→ per-workstream attention categories answering "what needs my attention?"
→ role-specific dashboard surfaces and WorkstreamAttentionSummary contracts
→ human surface graph: dashboard trunk, surface nodes, surface-action edges
→ internal workstream agent graph: virtual dashboard agent, worker agents, delegations, escalations
→ governed-tools inside capability files and surface/action maps
→ governed capabilities/APIs and exposure channels
→ Akka substrate and participants
→ request-based workstream Agent turns and AutonomousAgent task candidates
→ events/notifications/projections
→ audit/work traces and tests
```

Default dashboard scoping is workstream-local and role-specific: a dashboard should answer what is happening in that workstream for this actor/AuthContext, what needs the current user's attention, what is blocked/overdue/risky/failed/paused, which users/agents/workflows/AutonomousAgent tasks are participating, what decisions or approvals are pending, and what actions are authorized next. A dashboard is the trunk of the workstream's human surface graph, not a generic analytics page. My Account is the main aggregate exception; its dashboard is the current user's cross-workstream attention inbox, with Profile/Settings shortcuts, personal queue items, and compact accessible-workstream status panels. Left rail attention indicators and My Account counts must come from governed backend attention projections, not frontend-only badge logic.

AutonomousAgent task progress/result surfaces are part of the workstream model when durable internal/background model-driven work exists. Task lifecycle events, notifications, snapshots, blocked states, rejected results, failures, and completion recommendations should update dashboards, attention items, traces, and governed surface actions; the task machinery never grants authority by itself.

## Minimum initial core workstream set

The smallest generated AI-first SaaS starter is a bootstrap-authorized **five core workstream v0 set**, not a generic chatbot. Use this interpretation for prompts such as "minimum AI-first app," "starter app," "basic app," or "initial chatbot" unless the user explicitly asks for non-SaaS reference material.

The minimum initial shell contains these role-authorized functional agents for the bootstrap operator:

1. **My Account Agent** — opened only from the signed-in user tile/email at the bottom of the left rail, not listed with the top workstream buttons.
2. **User Admin Agent**
3. **Agent Admin Agent**
4. **Audit/Trace Agent**
5. **Governance/Policy Agent**

Each core v0 workstream is intentionally narrow, but each must still use the same shell model:

- **Left rail** — exposes role-authorized core functional agents for the bootstrap operator. User Admin, Agent Admin, Audit/Trace, and Governance/Policy appear in the top workstream rail when allowed. My Account is still one of the five core workstreams, but its only launcher is the signed-in user tile/email in the bottom rail user region. Unavailable richer full-core actions or surfaces are represented as explicit deferred/denied behavior, not hidden readiness claims.
- **Main workstream panel** — renders a durable request/response timeline for the selected core workstream with capability results, denials, trace references, and the first structured surface type: `markdown_response`.
- **Persistent composer** — accepts natural-language bootstrap requests for the selected core functional agent; it is an input channel, not an authorization boundary.
- **Context and authority indicators** — show selected AuthContext, bootstrap role/capability basis, available capabilities, denied/deferred actions, and trace links.

The first renderable surface for each core workstream is `markdown_response`: model-authored markdown in a versioned surface payload, rendered as sanitized HTML with trace/correlation ids and explicit loading, success, error, forbidden, and empty states. It is a real structured surface contract, not an informal chat blob; richer surfaces such as profile/settings cards, user tables, access-review queues, agent behavior diffs, audit timelines, and policy/approval cards can be added after the v0 slice.

Audit/work trace recording starts in this first slice for identity basis, AuthContext selection, capability checks, prompt/tool use, denials, and rendered responses. The Audit/Trace workstream is present from the first runnable starter, but it may initially explain and link to the trace substrate through `markdown_response` before richer search and investigation surfaces are implemented.

Full-core generated SaaS readiness remains stricter than this minimum starter. The five core workstream v0 set is a valid first runnable slice only when follow-up work remains explicit for complete My Account, User Admin, Agent Admin, Audit/Trace, Governance/Policy, invitations/onboarding, governed runtime agent documents, tenant isolation, and full security coverage.

A workstream is not production-ready just because fixture items render, a deterministic placeholder response exists, or a service directly calls a provider without the workstream Akka Agent. Named model-backed workstream behavior must pass through the real local Akka runtime path: selected AuthContext, backend authorization, durable workstream entries, governed prompt/runtime assembly, active managed configuration resolution, governed `readSkill`/`readReferenceDoc` loader tools, `ToolPermissionBoundary` enforcement, invocation of a concrete Akka `Agent` component for the functional agent with resolved tools registered through `effects().tools(runtimeTools)`, configured model/provider invocation from that Agent path, trace emission, API response, and frontend rendering. Workstream/foundation state claimed as implemented must be backed by Akka components in normal runtime. Provider/configuration failures should become safe system-message surfaces and traces. Mocks, fixtures, deterministic fakes, service-only provider bypasses, and test doubles are allowed only in tests or explicitly test-only adapters; they are not normal runtime substitutes.

## Core terms

| Term | Meaning |
|---|---|
| Agent workstream shell | Primary authenticated browser shell: role-authorized functional agents in the left rail, continuous workstream in the main panel, persistent composer at the bottom, and structured surfaces embedded in the stream. |
| Functional/context-area agent | User-facing, role-authorized assistant backing exactly one workstream and representing a functional area such as User Admin, Agent Admin, Procurement, Finance, Sales Pipeline, Support, Audit, Governance, or Outcome Metrics. Shortened to functional agent after first use. |
| Workstream agent | The functional agent in its role as the selected workstream user's assistant. It can answer workstream-specific “how do I...” questions, interpret shorthand requests such as “dashboard” or “show users”, request or refresh surfaces, invoke allowed capability-backed actions, explain denials/errors, and guide users through tasks. It is not the root app abstraction and does not grant authority. |
| Internal agent | Non-left-rail agent invoked by workflows, tools, consumers, timers, functional agents, or backend services for bounded work such as classification, summarization, evaluation, routing, replay, proposal drafting, or governance review. |
| Workstream | Root app unit for authenticated consequential work: a durable conversational and operational timeline backed by exactly one functional agent. It contains user requests, agent responses, tool/capability results, structured surfaces, system-message surfaces, decisions, workflow progress, traces, and follow-up actions. |
| Workstream icon | Universal shell metadata for a workstream launcher/status button: a compact icon chosen from the workstream name/domain, with stable id, accessible label, tooltip text, accent color, and optional glyph/vector asset. Icons keep rails and status panels compact while preserving full workstream names for hover, focus, and screen readers. |
| Surface | Typed renderable artifact in a workstream, such as a dashboard, form, data table, chart, decision card, diff review, audit timeline, entity detail, approval card, workflow status, exception card, system message, or outcome metric panel. |
| Surface graph | Human work tree for a workstream: the role-specific dashboard is the trunk, surface nodes are branches, and surface actions are edges that open surfaces, invoke browser-tools, create system-message surfaces, update attention, start internal-agent work, open traces, or route approvals/decisions. |
| Internal workstream agent graph | Backend worker graph for a workstream: a virtual dashboard agent view determines agent attention, delegates bounded work to internal worker agents, collects results/proposals, updates attention/surfaces, and escalates to humans when required. |
| Capability | Product-level backend ability or grouping behind related operations, queries, workflows, timers, consumers, APIs, and internal component calls. Capabilities define authority, scope, schemas, side effects, idempotency, policy/approval, audit, exposure channels, and tests. |
| Governed-tool | Executable semantic operation inside a capability boundary and surface/action map, with actor/caller rules, AuthContext, schemas, side effects, idempotency, policy/approval, audit/work trace, and implementation mapping. Expose it only through qualified channels such as browser-tool, agent-tool, internal-tool, workflow/timer/consumer exposure, API, or MCP-tool. |
| Horizontal implementation | Akka entities, workflows, views, consumers, timed actions, agents, endpoints, web UI code, auth/security, audit, and tests that implement capabilities for vertical functional agents and surfaces. |

## Agent workstream shell

The default authenticated app shell uses a familiar AI chat layout, but the left rail is not a list of casual chat sessions. It is a role-authorized functional-area launcher.

Required shell regions:

1. **Left rail functional agents** — show only agents the selected `AuthContext` may use. Examples: User Admin, Agent Admin, Governance/Policy, Audit/Trace, Support Access, Billing, Procurement, Finance, Sales Pipeline, Approval Queue, Risk & Exceptions. The signed-in user tile/email at the bottom of the rail is the My Account launcher; do not also list My Account among the top rail workstream buttons, and do not replace it with a separate profile/settings menu. Each visible rail workstream has universal workstream icon metadata for compact launchers, tooltips, accessible labels, and cross-surface reuse.
2. **Main workstream panel** — shows a continuous vertical stream of user intent, agent responses, structured surfaces, capability results, workflow status, decision cards, traces, and links.
3. **Persistent composer** — accepts natural-language requests, commands, uploads where allowed, and contextual follow-ups for the selected functional agent. It includes a standard **Show dashboard** button immediately to the right of **Send prompt**. The button uses a dashboard icon, is handled directly by the shell rather than prompting the workstream agent, and appends a `Show dashboard` request surface followed by the selected workstream's dashboard surface.
4. **Context and authority indicators** — show selected tenant/customer context, active role/capability basis, pending approvals, trace links, and safe denial/recovery states.

Selecting a functional agent selects its workstream and should normally produce an initial dashboard, attention, or briefing surface for that work area. The selected workstream agent may also request surfaces in response to natural language: “dashboard”, “show the access review queue”, “find Alex”, or “how do I add a new user?” should resolve to guidance, surface requests, or capability-backed actions within that workstream. Clicking buttons, links, cards, rows, icons, and other controls should append or update workstream surfaces rather than forcing users through a page-first navigation hierarchy.

Universal shell navigation is still capability-aware. Controls that open a specific surface or another workstream are surface-request actions with typed result behavior, not ad hoc frontend-only jumps when protected data, selected context, or authorization is involved. Examples: Profile button opens the My Account Profile surface; Settings button opens the Settings surface; a User Admin dashboard button opens the User List surface; a My Account workstream status panel opens the target workstream dashboard. The backend or authorized bootstrap state remains authoritative for whether the target workstream/surface is visible and what denial/system-message surface appears when it is not.

## Shell request routing for surfaces and workstreams

The workstream shell must support a unified request pipeline for prompt-entered navigation, surface actions, My Account dashboard panels, rail selection, and deep links. The pipeline normalizes all of these inputs into a typed shell request, performs authorization and target resolution, appends a prompt-like request item in the target workstream, then renders the target surface/workstream or a typed `system_message` denial.

Canonical shell request types:

- `show_surface` — open or refresh a structured surface in the current workstream by default, for example `show surface users-list`.
- `open_workstream` — switch to another authorized functional-agent workstream, for example `show workstream user-admin`.
- `refresh_surface` — request a fresh payload for a visible surface.
- `open_attention_item` — open the workstream/surface associated with an attention item.

Prompt aliases such as `show users list`, `show user list`, and `open users` should normalize to canonical prompt feedback such as `show surface users-list` when the target can be resolved. This gives users precise feedback and trains the command vocabulary without requiring them to know internal ids first. Canonical commands should resolve deterministically where possible; ambiguous natural-language requests may be resolved by the selected workstream agent or a bounded router capability.

Default resolution scope is the current selected workstream. Cross-workstream surface requests are allowed for experienced users and deep links only after authorized target discovery across visible workstreams; unresolved or unauthorized targets must return a safe `system_message` surface rather than leaking hidden workstream or surface existence. Workstream switching requests render the prompt-like request item in the **new target workstream only**, not duplicated in the source workstream.

Every request item must preserve honest origin metadata even when it is visually rendered like a prompt:

```ts
type WorkstreamShellRequest = {
  requestType: "show_surface" | "open_workstream" | "refresh_surface" | "open_attention_item";
  origin: "user_prompt" | "surface_action" | "deep_link" | "my_account_panel" | "system_suggestion" | "shell_button";
  displayText: string;        // what the user typed or action label said
  canonicalPrompt: string;    // e.g. "show surface users-list"
  targetFunctionalAgentId?: string;
  targetSurfaceId?: string;
  targetItemId?: string;
  sourceFunctionalAgentId?: string;
  sourceSurfaceId?: string;
  sourceActionId?: string;
  scope: "current_workstream" | "authorized_cross_workstream";
  correlationId: string;
};
```

Deep links must enter through this same shell request pipeline. A route may preselect `agent`, `surfaceId`, or `itemId`, but the shell still appends the prompt-like request in the target workstream and relies on backend capability authorization for the protected payload.

Workstream icon guidance:

```ts
type WorkstreamIconDescriptor = {
  workstreamId: string;
  displayName: string;
  iconId: string;              // stable generated/selected icon id
  visualHint: string;          // e.g. cart, invoice, chart, shield, user, wrench
  accentColorToken: string;    // shell theme token, not arbitrary user input
  tooltip: string;             // usually full workstream display name
  ariaLabel: string;           // accessible launcher/status label
  assetRef?: string;           // optional approved SVG/vector asset reference
};
```

When a new workstream is added, generate or select an icon from the workstream's actual domain name and responsibility. The shell must render the descriptor through an approved SVG/icon-library registry, not through text initials or arbitrary emoji. Keep icons semantically simple and consistent: Procurement may use a cart/purchase-order glyph, Inventory a package/warehouse glyph, Finance an invoice/currency glyph, Sales Pipeline a rising-chart glyph, Customer Success a health/heart glyph, Field Service a wrench/truck glyph, Governance a shield/checklist glyph. Unknown domain workstreams must still use the registry's semantic derivation/fallback SVG icon; letter-only fallback is not acceptable except as an explicitly failing development diagnostic. Do not encode authorization or sensitive state only through color; expose names and status through labels/tooltips and text.

## Workstreams and functional agents as verticals

A generated app grows by adding vertical workstreams. Each workstream is backed by exactly one functional agent. Each workstream/agent vertical should define:

- purpose and business responsibility;
- authorized roles/capabilities and tenant/customer scope;
- role-specific dashboard, attention, or briefing surfaces;
- durable workstream semantics and retention expectations;
- user intents the workstream agent must understand, including help/how-to prompts, shorthand surface requests, read requests, proposals, approvals, and allowed commands;
- prompt intent and a workstream expert bundle where LLM behavior is involved: governed prompt refs, skills, reference documents, compact expertise manifest, loader rules, tool boundaries, runtime tool bindings, and trace requirements;
- surfaces the workstream can render or reuse, arranged as a human surface graph;
- surface actions, including command actions and query/surface-request actions, modeled as graph edges;
- capabilities and governed-tools it can call directly or through browser-tools, agent-tools, internal-tools, workflows, timers, consumers, APIs, or MCP exposures;
- escalation, approval, denial, and exception behavior;
- audit/work trace requirements;
- tests for authorization, surface rendering, capability invocation, tenant isolation, and audit.

Foundation generated SaaS apps must include user-facing functional agents for secure operation, especially:

- **My Account Agent** for current account, context selection, profile, settings, sign out, personal queue, cross-workstream attention counts, and safe self-service. Its default dashboard should answer “what do I need to do next?” with Profile and Settings shortcuts, a personal queue, and compact status panels for accessible workstreams. Selecting Profile or Settings appends the corresponding request and response surface in the My Account flow; selecting a queue item or workstream status panel opens the relevant target workstream/surface through a governed surface-request action.
- **User Admin Agent** for invitations, users, memberships, roles/capabilities, disabled access, access review, support access visibility, and admin audit.
- **Agent Admin Agent** for agent definitions, prompts, skills, manifests, tool boundaries, lifecycle, proposals, approvals, behavior tests, and traces.
- **Audit/Trace Agent** for security, authorization, data access, tool use, decision, workflow, and outcome investigation.
- **Governance/Policy Agent** where policies, prompts, skills, thresholds, approvals, simulations, or behavior changes are managed.

Other domains add their own workstreams backed by functional agents, such as Sales Pipeline, Account Management, Support, Marketing Campaign, Revenue Operations, Finance, Procurement, Inventory/Supply Chain, Order Management, HR/Workforce, Operations Control, Approval Queue, Executive Briefing, Risk & Exceptions, and Outcome Metrics.

Domain-specific intent should decompose into fully functional domain-specific workstreams before implementation. For each proposed domain workstream, capture: business purpose, backing functional agent, authorized roles/capabilities, default surface, required surfaces, user intents, surface actions, action-to-capability mappings, backend capability contracts, Akka component realization, audit/work traces, and tests. Domain workstreams are not special cases; they use the same workstream → functional agent → surfaces → capabilities → Akka substrate pattern as the core SaaS workstreams.

## Internal agents

Internal agents are horizontal or supporting workers, not primary application navigation units. Use them when responsibility boundaries justify bounded backend AI behavior that should not appear as a left-rail work area. For generated apps, durable task-oriented internal/background agents should default to Akka `AutonomousAgent` when model-driven iteration, typed task lifecycle, dependencies, snapshots, notifications, delegation, handoff, teams, moderation, or independent failure/cancellation semantics are needed. Keep request-based Akka `Agent` for one-shot internal helper calls and for user-facing workstream request/response turns.

Common internal agents:

- classifier or router agent;
- summarizer or briefing agent;
- evaluator / LLM-as-judge agent;
- policy or governance reviewer;
- proposal/diff drafting agent;
- replay/simulation analyst;
- extraction, enrichment, or normalization agent;
- escalation triage agent.

Each workstream should also model an internal workstream agent graph when delegated backend AI work is possible. The virtual dashboard agent view asks what requires agent attention, what can be safely delegated, what result/proposal surface should be produced, and what must be escalated to humans. Internal worker agents execute bounded tasks through governed-tools, then resolve the work, produce results/proposals, or create human attention items.

Internal agents still require governed managed-agent `AgentDefinition`, approved prompt/skill references, tool boundaries, model policy, AuthContext or service authority basis, trace emission, and deterministic tests where applicable. Akka autonomous `AgentDefinition` means the SDK definition returned by `AutonomousAgent.definition()` or supplied through `AgentSetup`; qualify that term whenever both meanings are in scope. Deterministic tests should use isolated test doubles; they do not replace the production-like local runtime path for model-backed or provider-backed behavior.

## Surfaces

Surfaces are structured renderable results, not just text. They are associated with workstreams and functional agents but may be reused across agents. **All renderable system messages are surfaces**: denials, warnings, success confirmations, validation failures, approval-required notices, background-task-started notices, stale/reconnect notices, deferred-capability notices, and safe recovery messages should use typed `system_message` surface contracts rather than ad hoc UI strings.

Canonical surface types:

- `markdown_response` for sanitized model-authored markdown in the minimum five core workstream v0 set and other intentionally text-first responses;
- `system_message` for typed system feedback, denial, warning, success, validation, approval-required, deferred, stale, background-work, and recovery messages;
- dashboard / attention surface;
- form or guided intake;
- data table / search results;
- chart / metric panel;
- entity detail card;
- decision, approval, exception, or deviation card;
- diff / proposed change review;
- audit or work-trace timeline;
- workflow status / progress card;
- evidence bundle;
- policy, prompt, or skill version card;
- outcome review panel.

Surfaces may be composed from well-defined reusable UI components such as text blocks, callouts, forms, field groups, tables, filters, pagination, charts, cards, timelines, action bars, confirmation prompts, trace links, status badges, and empty/error/forbidden states. Component reuse must not weaken the surface contract: the surface still owns payload schema, actions, authority assumptions, traceability, tests, and result behavior.

Every surface should have:

- stable surface type and version;
- typed payload schema and redaction rules;
- attention/dashboard semantics where the surface contributes to `WorkstreamAttentionSummary`, My Account aggregate panels, left rail counts, or task progress/result presentation;
- allowed actions mapped to backend capabilities;
- tenant/customer and AuthContext assumptions;
- loading, empty, error, forbidden, and stale/reconnect states where relevant;
- accessibility and responsive behavior expectations;
- rendering tests and capability/action tests.

A surface action is not authorization. The backend capability remains authoritative.

Surface actions include both state-changing actions and surface-request actions:

- **Read/query or surface-request actions** request another surface, another allowed workstream, or refresh/update the current surface, such as `show_surface`, `show_dashboard`, `open_profile`, `open_settings`, `open_workstream`, `search_users`, `view_user`, `open_audit_timeline`, or row-click-to-open-detail. These actions normalize into the shell request pipeline and render a prompt-like request item in the target workstream before the result surface.
- **Command actions** submit changes through classic forms or action controls, such as invite, revoke, disable, save, resend, or update settings.
- **Proposal/approval/workflow actions** draft changes, request approval, approve/reject, start long-running work, or show progress.
- **Governance/trace actions** open diffs, simulations, trace details, audit timelines, or behavior-change review surfaces.

Every surface action maps to a governed backend capability and usually to a named governed-tool within that capability. In browser realizations, the surface action is a browser-tool exposed through a backend API that enforces authorization and returns a result surface, updated surface, workstream item, or typed `system_message` surface. Frontend-only navigation between consequential work surfaces is not enough; even “show dashboard” and “view user details” are read/evidence capabilities when scoped protected data is involved.

## Capabilities remain the backend contract

Workstreams are the root application abstraction, but capabilities remain the backend contract. Workstreams do not make agents, tools, surfaces, UI controls, or routes the backend design root.

For each operation or query exposed in a workstream, define the capability and governed-tool first:

- capability id and product-level purpose;
- governed-tool id, purpose, class, and whether it composes other governed-tools;
- actors/callers, including functional agents, internal agents, humans, workflows, timers, consumers, services, or support roles;
- AuthContext, tenant/customer scope, role/capability requirements, and denial behavior;
- input/output schemas, validation, redaction, and idempotency;
- data access and side effects;
- policy, approval, escalation, and autonomy rules;
- audit/work-trace fields;
- exposure channels: surface/workstream action as browser-tool, browser API, workstream-agent agent-tool, internal-agent agent-tool, HTTP/gRPC/MCP endpoint or MCP-tool, workflow step, timer-tool, consumer-tool, view, or internal-tool;
- success, validation, forbidden, tenant-isolation, idempotency, approval, audit, and rendering/tool/API tests.

Agent-tools are optional capability exposure channels. Workstream-agent agent-tools are conversational exposures of governed-tools for the selected workstream; internal-agent agent-tools are backend AI-worker exposures. Side-effecting agent-tools require explicit permission and should default to proposal or approval flows unless a bounded autonomous policy is accepted.

## Horizontal Akka implementation

After vertical functional agents, surfaces, and capability contracts are clear, select Akka components horizontally:

| Need | Common Akka horizontal |
|---|---|
| Audit-grade decisions, policies, approvals, goals, traces, or lifecycle records | Event Sourced Entities |
| Current-state profiles, settings, or configuration | Key Value Entities |
| Deterministic long-running plans, approvals, retries, compensation, or product orchestration | Workflows |
| Durable model-driven internal/background tasks, handoffs, delegation, teams, moderation, task dependencies, snapshots, or notification-backed agent work | Autonomous Agents |
| Curated dashboard, table, search, evidence, or audit read models | Views |
| Bounded request/response planning, recommendation, summarization, classification, evaluation, explanation, or user-facing workstream turns | request-based Agents |
| Event reactions, trace enrichment, notification, publication, or integration side effects | Consumers |
| Expiry, reminders, digests, rechecks, replay, or retention | Timed Actions / timers |
| Browser/service APIs, streaming, and static/frontend hosting | HTTP/gRPC endpoints |
| Remote AI-client tools, resources, and prompts | MCP endpoints |
| Shell, workstream rendering, typed clients, state, forms, realtime, accessibility, tests | React/Vite/TypeScript web UI |

Do not choose components from a page-first or CRUD-first decomposition. Choose them from capability semantics.

## What this architecture is not

Do not present these as equivalent defaults for generated AI-first SaaS apps:

- a conventional page hierarchy with optional AI features;
- CRUD admin screens as the primary decomposition root;
- a dashboard with a chatbot panel attached;
- agent tools as the backend design root;
- ungoverned prompt-only automation;
- unaudited agents that can act without backend-enforced authority;
- optional user administration or optional agent administration for a full generated core SaaS app.

Allowed exceptions are narrow: public marketing/legal/static pages, direct deep links to surfaces, implementation routes, non-SaaS reference material, or repository-maintenance tasks explicitly outside generated-app architecture.

## Incremental delivery pattern

Build generated apps and process incremental changes in vertical slices:

```text
add or extend one affected workstream
+ exactly one backing functional agent
+ role-specific dashboard/attention surface
+ surface graph node/action-edge changes
+ internal workstream agent graph changes when delegated worker work exists
+ one or two useful user intents/actions
+ governed capabilities and governed-tools
+ Akka horizontals needed for those capabilities/governed-tools
+ workstream UI rendering
+ authorization, audit, tenant-isolation, governed-tool, and surface tests
```

Then repeat as the workstream gains more surfaces, skills, tools, workflows, internal-agent support, and outcome loops.

## Readiness checklist

Before treating a generated full-stack AI-first SaaS app as architecture-ready, verify:

- [ ] Authenticated consequential work areas are modeled as workstreams backed by exactly one functional agent, not primarily as pages.
- [ ] Left rail entries select role-authorized workstreams/functional agents from backend capabilities and selected AuthContext.
- [ ] Each workstream/functional agent has purpose, authority, supported user intents, surfaces, capability mappings, traces, and tests.
- [ ] Each LLM-backed functional agent is an AI-first managed agent with a workstream expert bundle or explicit deferral covering skills, reference documents, manifests, loader authorization, `ToolPermissionBoundary`, `effects().tools(runtimeTools)` registration, traces, and tests.
- [ ] User Admin and Agent Admin functional agents are present for full core SaaS scope, or the narrower scope explicitly defers them.
- [ ] Internal agents are distinguished from functional agents and have governed behavior, tool boundaries, and traces.
- [ ] Surfaces are typed renderable artifacts with schemas, allowed actions, states, and rendering tests.
- [ ] System messages are modeled as typed surfaces, not ad hoc strings.
- [ ] Surface actions, including surface-request actions, agent tools, APIs, workflows, timers, and consumers map to governed capabilities.
- [ ] Prompt-entered surface/workstream requests, surface actions, My Account panels, rail selection, and deep links share one shell request pipeline with canonical prompt feedback, origin metadata, target-workstream-only request rendering, backend authorization, denial/system-message behavior, and tests.
- [ ] Capability-first backend design remains intact: auth, scope, validation, idempotency, side effects, approval, audit, exposure channels, and tests are defined before Akka component selection.
- [ ] Akka components are selected as horizontal implementation details from capability semantics.
- [ ] The UI shell includes left rail functional agents, main workstream, persistent composer, context/authority indicators, denial/recovery states, and trace links.
- [ ] Page-first, CRUD-first, and chatbot-bolt-on alternatives are not presented as equal generated-app defaults.

## App-description layer ownership

When this model is maintained in an app-description tree, keep ownership split by layer:

- `12-workstreams/` owns application meaning: functional agents, internal agents, durable workstreams, workstream expert bundles under `workstream-expertise/**`, surface index and contracts, reusable surface placement, action-to-capability mappings, trace semantics, and surface/action tests.
- `55-ui/` owns browser realization: shell rendering, functional-agent rail, workstream panel, persistent composer, structured-surface rendering, routes/deep links, forms/interactions, frontend API contracts, state/realtime, accessibility/responsive behavior, and style guide.
- `55-ui/` must link back to `12-workstreams/`, capability, security, observability, and test layers instead of redefining functional agents, surface contracts, or capability semantics.
- `60-generation/` and generated frontend source are downstream projections, not authoritative product meaning.

## Routing implications

For high-level product input, apply this sequence:

1. Preserve the mandatory secure AI-first SaaS foundation.
2. Apply `requirements-to-workstream-development-process.md`: workstream inventory, attention categories, dashboard contracts, surfaces/actions, governed capabilities/APIs, Akka substrate, agent/AutonomousAgent workers, events/notifications/projections, traces, and tests.
3. Interpret the product as an agent workstream application unless explicitly out of scope.
4. Identify functional agents, internal agents, initial workstreams, structured surfaces, and retained human authority.
5. Model governed backend capabilities for every surface action, tool, workflow step, API, timer, consumer, and internal operation.
6. Route to app-description maintenance, solution decomposition, PRD/spec/backlog planning, or focused implementation.
7. Use web UI and agent skills to implement the workstream shell and governed agents.
8. Use Akka component skills to implement the horizontal substrate from accepted capability contracts.

Use `workstream-expertise-model.md` with this doctrine when a functional agent needs governed skills, reference documents, manifests, loader authorization, tool boundaries, traces, and tests that make it an expert in its workstream. Use `agent-component-selection-guide.md` before choosing whether supporting agent work should be a request-based `Agent`, `AutonomousAgent`, `Workflow`, `Workflow + Agent`, or `Workflow + AutonomousAgent`.

Use `domain-workstream-prd-structure.md` when capturing domain-level and workstream-level PRDs for the core SaaS app domain or app-specific domains. It defines the directory structure for domains, workstreams, workstream-agent prompts/skills, surfaces, capabilities, and tests.

This doctrine should be referenced by future app-description, web UI, agent, routing, and review tasks as the single generated-app UI/application architecture default.
