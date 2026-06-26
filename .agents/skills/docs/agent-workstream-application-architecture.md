# Agent Workstream Application Architecture

## Status and scope

This is the canonical agent workstream application architecture doctrine for this skills pack.
It defines the mandatory default UI/application model for generated full-stack secure AI-first SaaS applications.

Use this doctrine below `./ai-first-saas-application-architecture.md` and above detailed app-description, web UI, agent, and Akka component guidance. For the compact schema-style workstream fields, type-vs-instance terminology, ownership/reuse rules, readiness levels, and id taxonomy, apply `./workstream-contract.md`. For the machine-readable app-description index, apply `./workstream-manifest-schema.md`. For one harness-sized implementation slice, apply `./minimum-implementable-workstream-slice.md`. For broad input, PRD, app-description, planning, backlog, and implementation-readiness work, apply `./requirements-to-workstream-development-process.md` before component selection. Use `./app-worker-tool-model.md` for the canonical separation of workers, harnesses, actor adapters, governed tools, capabilities, and Akka implementation. This document does not replace `./capability-first-backend-architecture.md`: workstream UI actions, agent tools, workflow steps, APIs, timers, consumers, and internal calls still map to governed backend capabilities before Akka implementation is selected.

For SaaS Foundation App description workstream and surface examples, use the source-controlled files under `templates/ai-first-saas-core-app/app-description/**`; do not depend on generated distribution output directories as template sources. For deterministic composer-to-surface routing and prefill-only behavior in new workstreams, apply `./workstream-surface-intent-routing.md`.

## Default generated-app architecture

Generated AI-first SaaS apps are **agent workstream applications by default**. The **workstream is the root application abstraction** for authenticated consequential work. First use: **functional/context-area agent** means a user-facing, role-authorized agent for one durable work area; this document shortens the term to **functional agent** after defining the alias. Use **workstream definition** for the design-time product vertical, **workstream instance** for the durable runtime timeline/log in a selected AuthContext scope, and **workstream view/session** for browser rendering.

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
→ per-workstream worker roster: human workers, functional-agent workers, internal/autonomous/evaluator agent workers, and system workers
→ worker responsibility, authority, supervision, and handoff map
→ per-workstream attention categories answering "what needs my attention?"
→ role-specific dashboard surfaces and WorkstreamAttentionSummary contracts
→ human surface graph: dashboard trunk, surface nodes, surface-action edges
→ internal workstream agent graph: virtual dashboard agent, worker agents, delegations, escalations
→ governed-tools inside capability files and surface/action maps
→ governed tools inside capability/API contracts
→ selected actor adapters and exposure channels
→ Akka substrate and participants
→ request-based workstream Agent turns and AutonomousAgent task candidates
→ events/notifications/projections
→ audit/work traces and tests
```

Default dashboard scoping is workstream-local and role-specific. A dashboard has two primary actionable jobs for the current authorized user in the selected `AuthContext`: first, and most prominently at the top of the dashboard, show what needs the current user's attention now; second, show what the current user can do next. The top attention region should answer what is blocked/overdue/risky/failed/paused, what decisions or approvals are pending, and which items need inspection, correction, retry, escalation, acknowledgement, dismissal, or learning. The next-action region should expose authorized actions, shortcuts, and safe starting points for the workstream. A dashboard may also show what is happening in that workstream, which users/agents/workflows/AutonomousAgent tasks are participating, recent changes, and status context, but those supporting visuals must not displace current-user attention. A dashboard is the trunk of the workstream's human surface graph, not a generic analytics page.

Dashboard objects are interactive work objects by default. Anything rendered as a thing that needs attention or a thing the user can do next—a card, row, counter, badge, chart segment, task/progress panel, icon, shortcut, or button—should be directly operable by click and keyboard activation. A common dashboard button is the entire modern rectangular tile/card/counter shape containing the thing's name and a prominent count; the whole shape is the button, not only a small nested action control. A zero count can still be actionable when it opens the empty queue, detail, explanation, setup, or history surface for that category. Activation must append a request-like workstream item and then append the target detail, decision, progress, evidence/trace, result, updated dashboard, or typed `system_message` surface. A non-actionable dashboard object is an explicit exception, visually distinct from actionable objects, and must have an `interaction: none` style reason such as read-only context, not-yet-available deferred capability, or static explanatory status. Ready dashboards should show what this actor can do; forbidden targets should normally be omitted rather than shown as disabled cards. Denial/system-message surfaces still apply for deep links, stale payloads, manual API calls, race conditions, or changed authorization.

The visual target is a modern, high-tech, AI-first command surface, not a legacy CRUD SaaS metrics grid. Dashboard controls should feel like powerful work buttons: tapping a visible work object should open governed context, evidence, decisions, approvals, progress, or actions where real consequential work can happen through capability-backed paths.

My Account is the main aggregate exception; its dashboard is the current user's cross-workstream attention inbox, with Profile/Settings shortcuts, personal queue items, and compact accessible-workstream status panels. Left rail attention indicators and My Account counts must come from governed backend attention projections, not frontend-only badge logic.

AutonomousAgent task progress/result surfaces are part of the workstream model when durable internal/background model-driven work exists. Task lifecycle events, notifications, snapshots, blocked states, rejected results, failures, and completion recommendations should update dashboards, attention items, traces, and governed surface actions; the task machinery never grants authority by itself.

## Workforce decomposition and workstream actors

Before selecting surfaces, capabilities, agent teams, or Akka components, identify the workstream workforce. A workforce roster names the human workers, functional-agent worker, internal specialist agent workers, durable autonomous/background agent workers, evaluator/reviewer agents, and deterministic system workers that perform or supervise the work.

Each worker should have an explicit behavior profile, reasoning/execution engine, responsibility, non-responsibilities, authority, AuthContext/scope, evidence needs, execution harnesses, actor adapters, governed tools, capabilities, surfaces used or produced, handoffs/escalations, audit/work traces, and failure/denial behavior. Use `./workforce-decomposition.md` and `./worker-artifact-contract.md` for the canonical contract. Agent workers are not generic AI helpers; they are bounded workers whose authority is narrower than, and separately declared from, the authority of the human workers they support.

A workstream definition is incomplete if it names a functional agent, internal agent, AutonomousAgent task, workflow, dashboard, or consequential surface action without identifying the responsible worker and actor adapter.

## Human-backed and AI-backed workstream actors

Use **workstream assistant** as the product/UX term for the user-facing helper inside a selected workstream. Use **functional agent** or **workstream agent** as the technical/governance term for the governed AI-backed worker, managed behavior profile, and Akka Agent runtime that backs that assistant. A workstream assistant helps the human worker understand the workstream, open or refresh surfaces, draft/recommend next steps, propose confirmed tool plans, invoke only explicitly exposed tools, explain denials/errors, and return structured result surfaces. It does not own the workstream, grant authority, or replace backend capability checks.

A workstream assistant/agent is the shared governed application harness for a durable area of work. It can host **human-backed actor** turns, **AI-backed actor** turns, system-worker results, or mixed human/AI collaboration in the same workstream instance. Externally, a signed-in app user is a human supervisor. Internally, a human-backed actor is the governed participant whose reasoning engine is that authenticated human rather than an AI model; its behavior profile still includes prompt/instructions, skills, tools, policies, evidence scope, and assistance mode. An AI-backed actor is the governed participant whose reasoning engine is a configured model invoked through the Akka Agent runtime path.

The harness shapes each actor through actor-specific adapters while preserving one authority model:

```text
Workstream assistant / functional-agent harness
├── human-backed actor adapter: surfaces, instructions, evidence, forms, actions, decisions
├── human chat tool-plan adapter: natural-language request, proposed plan, confirmation, execution results
├── AI-backed actor adapter: prompts, skills, references, tools, model responses
├── shared governed workstream tool catalog
├── shared capability authorization and policy gates
├── shared durable workstream log
└── shared audit/work traces
```

A governed workstream tool is a capability-backed operation, not inherently a UI action, chat command, or AI function tool. The workstream harness may expose the same governed tool through multiple channels: a browser surface action for a human-backed actor, a confirmed human chat tool plan, an agent-tool schema for an AI-backed actor, an internal/workflow/timer/consumer tool, an API, or an MCP tool. The exposure adapter changes presentation, input mediation, confirmation UX, and trace source; it does not redefine business authority, tenant scope, idempotency, approval policy, audit, or denial behavior.

The human worker and the workstream assistant are both constrained by the workstream's governed tool catalog, but through different harnesses. Human workers see tool-backed affordances as dashboards, forms, rows, buttons, confirmations, disabled/denied states, and result surfaces. The workstream assistant sees tool-backed affordances as model-facing schemas, prompt/skill/reference context, loader tools, structured-response contracts, and tool-boundary membership. The same governed tool may be shared by both harnesses only through declared actor adapters.

For AI-backed actors, prompts, skills, references, tool descriptions, and schemas instruct the model how to request tool use from the harness. For human-backed actors, the human-operating prompt, role skills, structured surfaces, and governed tool catalog shape the role: labels, fields, validation, evidence, confirmations, disabled/denied states, result surfaces, and trace links teach and constrain how the human supervisor can use the same workstream tools. A human chat request is a second human-backed adapter: the selected workstream assistant may interpret the request through the human worker's behavior profile, propose a sufficiently detailed tool plan, require explicit confirmation bound to that exact plan, and then execute only the selected workstream's governed tools through deterministic backend checks. If the plan changes materially, confirmation must be requested again before any consequential tool invocation.

Example: `useradmin.invitation.create` may be exposed as an **Invite user** surface action in the User Admin dashboard, as a confirmed `human_chat_tool_plan` in the selected User Admin workstream, and as an allowed agent tool for the User Admin Agent. A human can click the action and submit `jane.doe@gmail.com`; the same human can also ask the selected User Admin assistant, “create a user invite for jane.doe@gmail.com.” The chat path first returns a plan such as “create invitation for jane.doe@gmail.com in the selected organization with role X; send invitation email; record admin audit,” asks for confirmation, and only then invokes the same governed-tool sequence. In every path the backend invokes the same governed capability with selected `AuthContext`, tenant scope, role/capability checks, idempotency, policy gates, audit, traces, and safe denial behavior.

Human availability does not automatically grant AI availability. If a human-backed worker can perform a consequential action from a surface or confirmed chat plan, the AI-backed workstream agent may perform or propose that action only when the same governed tool is explicitly exposed to that agent, within its tool boundary and approval policy. Traces must distinguish direct human `surface_action` adapters, human-requested confirmed `human_chat_tool_plan` adapters, and AI-mediated `agent_tool_call` adapters while preserving their relationship, for example `actorType=human-backed`, `source=surface_action`; `actorType=human-backed`, `source=human_chat_tool_plan`, `confirmedBy=<human account/member>`; or `actorType=ai-backed`, `requestedBy=<human account/member>`, `source=agent_tool_call`. The AI model is never the security boundary; tool catalog membership, AuthContext, schemas, policy/approval, idempotency, and backend authorization decide whether an invocation proceeds.

## SaaS Foundation App workstream set

The repository ships the **SaaS Foundation App** out of the box. It is not a generic chatbot or throwaway starter; it is the built-in foundation domain that downstream users extend. Use this interpretation for prompts such as "starter app," "minimum AI-first app," "core app," "basic app," or "initial chatbot" unless the user explicitly asks for non-SaaS reference material.

The foundation shell contains these role-authorized functional agents:

1. **My Account Agent** — opened only from the signed-in user tile/email at the bottom of the left rail, not listed with the top workstream buttons.
2. **User Admin Agent**
3. **Agent Admin Agent**
4. **Audit/Trace Agent**
5. **Governance/Policy Agent**

Each foundation workstream uses the same shell model:

- **Left rail** — exposes role-authorized core functional agents for the bootstrap operator. User Admin, Agent Admin, Audit/Trace, and Governance/Policy appear in the top workstream rail when allowed. My Account is still one of the five core workstreams, but its only launcher is the signed-in user tile/email in the bottom rail user region. Unavailable richer SaaS Foundation App actions or surfaces are represented as explicit deferred/denied behavior, not hidden readiness claims.
- **Main workstream panel** — renders a durable request/response timeline for the selected core workstream with capability results, denials, trace references, and the first structured surface type: `markdown_response`.
- **Persistent composer** — accepts natural-language bootstrap requests for the selected core functional agent; it is an input channel, not an authorization boundary.
- **Context and authority indicators** — show selected AuthContext, bootstrap role/capability basis, available capabilities, denied/deferred actions, and trace links.

`markdown_response` is the lowest-ceremony structured surface available to these workstreams: model-authored markdown in a versioned surface payload, rendered as sanitized HTML with trace/correlation ids and explicit loading, success, error, forbidden, and empty states. It is a real structured surface contract, not an informal chat blob. The same workstream model also supports richer surfaces such as profile/settings cards, user tables, access-review queues, agent behavior diffs, audit timelines, policy/approval cards, forms, and other SaaS-style screens.

Audit/work trace recording is part of normal SaaS Foundation App behavior for identity basis, AuthContext selection, capability checks, prompt/tool use, denials, and rendered responses. When extending or modifying the foundation domain, preserve those traces instead of treating them as optional hardening.

A workstream is not production-ready just because fixture items render, a deterministic placeholder response exists, or a service directly calls a provider without the workstream Akka Agent. Named model-backed workstream behavior must pass through the real local Akka runtime path: selected AuthContext, backend authorization, durable workstream entries, governed prompt/runtime assembly, active managed configuration resolution, governed `readSkill`/`readReferenceDoc` loader tools, `ToolPermissionBoundary` enforcement, invocation of a concrete Akka `Agent` component for the functional agent with resolved tools registered through `effects().tools(runtimeTools)`, configured model/provider invocation from that Agent path, trace emission, API response, and frontend rendering. Workstream/foundation state claimed as implemented must be backed by Akka components in normal runtime. Provider/configuration failures should become safe system-message surfaces and traces. Mocks, fixtures, deterministic fakes, service-only provider bypasses, and test doubles are allowed only in tests or explicitly test-only adapters; they are not normal runtime substitutes.

## Core terms

| Term | Meaning |
|---|---|
| Agent workstream shell | Primary authenticated browser shell: role-authorized functional agents in the left rail, continuous workstream in the main panel, persistent composer at the bottom, and structured surfaces embedded in the stream. |
| Workstream assistant | Product/UX term for the user-facing helper inside a selected workstream. It helps the human worker understand work, open surfaces, draft/recommend next steps, propose confirmed plans, invoke only exposed tools, explain denials/errors, and return structured result surfaces. It is backed by a governed functional-agent runtime; it is not the root app abstraction and does not grant authority. |
| Functional/context-area agent | Technical/governance term for the user-facing, role-authorized AI-backed worker/runtime backing exactly one workstream and representing a functional area such as User Admin, Agent Admin, Procurement, Finance, Sales Pipeline, Support, Audit, Governance, or Outcome Metrics. Shortened to functional agent after first use. |
| Workstream agent | Technical shorthand for the functional agent in its role as the selected workstream user's assistant and shared governed harness for human-backed and AI-backed turns. Prefer `workstream assistant` in product-facing UX copy and `functional agent` / `workstream agent` in architecture, governance, and Akka runtime contexts. |
| Human-backed actor | Governed workstream participant whose reasoning engine is an authenticated human supervisor. The harness shapes this actor through surfaces, instructions, evidence, forms, action affordances, confirmations, result surfaces, and trace links. |
| AI-backed actor | Governed workstream participant whose reasoning engine is a configured model invoked through the Akka Agent runtime path. The harness shapes this actor through prompts, skills, references, tool schemas, tool boundaries, memory, guardrails, and traces. |
| `surface_action` actor adapter | The human-backed worker's structured surface adapter for a governed workstream tool, including labels, fields, validation, evidence, confirmation/approval UX, result/system-message surfaces, denial states, and trace links. |
| `human_chat_tool_plan` actor adapter | The human-backed worker's natural-language adapter for consequential governed-tool use. The selected workstream assistant / functional agent interprets the request, proposes a detailed plan, obtains explicit confirmation bound to that plan, then executes individually authorized and traced governed-tool invocations with result/partial-failure surfaces. |
| `agent_tool_call` actor adapter | The AI-backed worker's model-facing adapter for a governed workstream tool, exposed only when the tool boundary allows it and always enforced by backend authorization, policy, idempotency, and traces. |
| System/API/MCP actor adapters and exposure channels | Non-human and non-browser exposure of a governed workstream tool through `workflow_step`, `timer_invocation`, `consumer_reaction`, `internal_call`, `api_call`, or `mcp_tool_call`. These channels share the same capability contract and differ only by caller boundary, harness, and trace source. |
| Internal agent | Non-left-rail agent invoked by workflows, tools, consumers, timers, functional agents, or backend services for bounded work such as classification, summarization, evaluation, routing, replay, proposal drafting, or governance review. |
| Workstream definition | Design-time root app unit for authenticated consequential work, backed by exactly one functional agent, with role-specific dashboards, attention, surface graph, capability/governed-tool map, expertise, traces, and tests. |
| Workstream instance | Durable runtime conversational/operational timeline for one workstream definition in a selected organization/customer/AuthContext scope. It retains user requests, agent responses, tool/capability results, structured surfaces, system-message surfaces, decisions, workflow progress, traces, and follow-up actions. Retained records are not automatically separate browser surfaces; render one primary result surface per prompt/action unless a typed progress/detail surface is explicitly part of the contract. |
| Workstream view/session | Browser rendering of a selected workstream instance. It may be route/deep-link addressable but is not the durable source of truth. |
| Workstream icon | Universal shell metadata for a workstream launcher/status button: a compact icon chosen from the workstream name/domain, with stable id, accessible label, tooltip text, accent color, and optional glyph/vector asset. Icons keep rails and status panels compact while preserving full workstream names for hover, focus, and screen readers. |
| Surface | Typed renderable artifact in a workstream, such as a dashboard, form, data table, chart, decision card, diff review, audit timeline, entity detail, approval card, workflow status, exception card, system message, or outcome metric panel. |
| Surface graph | Human work tree for a workstream: the role-specific dashboard is the trunk, surface nodes are branches, and `surface_action` edges open surfaces, invoke browser adapters for governed tools, create system-message surfaces, update attention, start internal-agent work, open traces, or route approvals/decisions. |
| Internal workstream agent graph | Backend worker graph for a workstream: a virtual dashboard agent view determines agent attention, delegates bounded work to internal worker agents, collects results/proposals, updates attention/surfaces, and escalates to humans when required. |
| Capability | Product-level backend ability or grouping behind related governed tools. Capabilities define authority, scope, schemas, side effects, idempotency, policy/approval, audit, actor adapters/exposure channels, and tests. |
| Governed-tool / governed workstream tool | Executable semantic operation inside a capability boundary and surface/action map, with worker/caller rules, AuthContext, schemas, side effects, idempotency, policy/approval, audit/work trace, and implementation mapping. It is not inherently a UI action, chat command, or AI function tool; expose it only through qualified actor adapters/channels such as `surface_action`, confirmed `human_chat_tool_plan`, `agent_tool_call`, `internal_call`, `workflow_step`, `timer_invocation`, `consumer_reaction`, `api_call`, or `mcp_tool_call`. |
| Horizontal implementation | Akka entities, workflows, views, consumers, timed actions, agents, endpoints, web UI code, auth/security, audit, and tests that implement capabilities for vertical functional agents and surfaces. |

## Agent workstream shell

The default authenticated app shell uses a familiar AI chat layout, but the left rail is not a list of casual chat sessions. It is a role-authorized functional-area launcher.

Required shell regions:

1. **Left rail functional agents** — show only agents the selected `AuthContext` may use. Examples: User Admin, Agent Admin, Governance/Policy, Audit/Trace, Support Access, Billing, Procurement, Finance, Sales Pipeline, Approval Queue, Risk & Exceptions. The signed-in user tile/email at the bottom of the rail is the My Account launcher; do not also list My Account among the top rail workstream buttons, and do not replace it with a separate profile/settings menu. Each visible rail workstream has universal workstream icon metadata for compact launchers, tooltips, accessible labels, and cross-surface reuse.
2. **Main workstream panel** — shows a continuous vertical stream of user intent, primary result surfaces, structured surfaces, capability results, workflow status, decision cards, traces, and links. Do not duplicate the same turn as both a generic agent-response/activity card and a typed result surface such as `markdown_response`; activity and trace details should stay collapsed unless intentionally opened.
3. **Persistent composer** — accepts natural-language requests, commands, uploads where allowed, and contextual follow-ups for the selected functional agent / workstream assistant. It includes a standard **Show dashboard** button immediately to the right of **Send prompt**. The button uses a dashboard icon, is handled directly by the shell rather than prompting the assistant, and appends a `Show dashboard` request surface followed by the selected workstream's dashboard surface.
4. **Context and authority indicators** — show selected organization/customer context, active role/capability basis, pending approvals, trace links, and safe denial/recovery states.

Selecting a functional agent selects its workstream and should normally produce an initial dashboard, attention, or briefing surface for that work area. In product copy this selected helper may be called the workstream assistant. The selected workstream assistant may also request surfaces in response to natural language: “dashboard”, “show the access review queue”, “find Alex”, or “how do I add a new user?” should resolve to guidance, deterministic surface requests, prefilled surfaces, confirmed tool-plan proposals, or capability-backed actions within that workstream. Before falling back to model-backed chat, every new composer-enabled workstream should run a backend-owned surface intent router from `./workstream-surface-intent-routing.md`: high-confidence requests such as `create customer "Acme"` or `invite user jane@example.com` open the appropriate authorized surface with safe editable prefill and no mutation. That router rule is a safe default for surface routing, not a global prohibition on separately modeled confirmed human chat tool execution. A confirmed chat tool plan must still use the selected workstream's governed tool catalog, explicit plan confirmation, backend authorization, idempotent per-tool transaction boundaries, traces, and result/partial-failure surfaces. Dashboard visuals are actionable affordances by default when they represent attention, status needing follow-up, or available work: clicking buttons, links, cards, rows, icons, charts, badges, counters, task panels, and other controls should append a request/result surface pair, open an attention item/detail/decision/progress surface, or invoke a governed capability-backed surface action rather than forcing users through a page-first navigation hierarchy. Do not satisfy this requirement with buttons alone; the dashboard's visible work objects themselves should be operable when they represent attention or next work.

Universal shell navigation is still capability-aware. Controls that open a specific surface or another workstream are surface-request actions with typed result behavior, not ad hoc frontend-only jumps when protected data, selected context, or authorization is involved. Examples: Profile button opens the My Account Profile surface; Settings button opens the Settings surface; a User Admin dashboard button opens the User List surface; a My Account workstream status panel opens the target workstream dashboard. The backend or authorized bootstrap state remains authoritative for whether the target workstream/surface is visible and what denial/system-message surface appears when it is not.

## Shell request routing for surfaces and workstreams

The workstream shell must support a unified request pipeline for prompt-entered navigation, surface actions, My Account dashboard panels, rail selection, and deep links. The pipeline normalizes all of these inputs into a typed shell request, performs authorization and target resolution, appends a prompt-like request item in the target workstream, then renders the target surface/workstream or a typed `system_message` denial.

Canonical shell request types:

- `show_surface` — open or refresh a structured surface in the current workstream by default, for example `show surface users-list`.
- `open_workstream` — switch to another authorized functional-agent workstream, for example `show workstream user-admin`.
- `refresh_surface` — request a fresh payload for a visible surface.
- `open_attention_item` — open the workstream/surface associated with an attention item.

Prompt aliases such as `show users list`, `show user list`, and `open users` should normalize to canonical prompt feedback such as `show surface users-list` when the target can be resolved. This gives users precise feedback and trains the command vocabulary without requiring them to know internal ids first. Canonical commands should resolve deterministically where possible; ambiguous natural-language requests may be resolved by the selected workstream assistant / functional agent or a bounded router capability.

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
  visualHint: string;          // e.g. workstream event, invoice, chart, shield, user, wrench
  accentColorToken: string;    // shell theme token, not arbitrary user input
  tooltip: string;             // usually full workstream display name
  ariaLabel: string;           // accessible launcher/status label
  assetRef?: string;           // optional approved SVG/vector asset reference
};
```

When a new workstream is added, generate or select an icon from the workstream's actual domain name and responsibility. The shell must render the descriptor through an approved SVG/icon-library registry, not through text initials or arbitrary emoji. Keep icons semantically simple and consistent: Procurement may use a workstream event/purchase-order glyph, Inventory a package/warehouse glyph, Finance an invoice/currency glyph, Sales Pipeline a rising-chart glyph, Customer Success a health/heart glyph, Field Service a wrench/truck glyph, Governance a shield/checklist glyph. Unknown domain workstreams must still use the registry's semantic derivation/fallback SVG icon; letter-only fallback is not acceptable except as an explicitly failing development diagnostic. Do not encode authorization or sensitive state only through color; expose names and status through labels/tooltips and text.

## Workstreams and functional agents as verticals

A generated app grows by adding vertical workstreams. Each workstream is backed by exactly one functional agent. Each workstream/agent vertical should define:

- purpose and business responsibility;
- authorized roles/capabilities and tenant/customer scope;
- role-specific dashboard, attention, or briefing surfaces;
- durable workstream semantics and retention expectations;
- user intents the workstream assistant / functional agent must understand, including help/how-to prompts, shorthand surface requests, read requests, proposals, approvals, and allowed commands;
- prompt intent and a workstream expert bundle where LLM behavior is involved: governed prompt refs, skills, reference documents, compact expertise manifest, loader rules, tool boundaries, runtime tool bindings, and trace requirements;
- surfaces the workstream can render or reuse, arranged as a human surface graph;
- surface actions, including command actions and query/surface-request actions, modeled as graph edges;
- governed tools and capabilities it can call directly or through declared actor adapters such as `surface_action`, `human_chat_tool_plan`, `agent_tool_call`, `internal_call`, `workflow_step`, `timer_invocation`, `consumer_reaction`, `api_call`, or `mcp_tool_call`;
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

- `markdown_response` for sanitized model-authored markdown in the minimum SaaS Foundation App domain and other intentionally text-first responses;
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
- actor adapters and exposure channels: `surface_action`, confirmed `human_chat_tool_plan`, `api_call`, workstream-agent or internal-agent `agent_tool_call`, HTTP/gRPC/MCP endpoint or `mcp_tool_call`, `workflow_step`, `timer_invocation`, `consumer_reaction`, view/query, or `internal_call`;
- success, validation, forbidden, tenant-isolation, idempotency, approval, audit, confirmation, partial-failure, and rendering/tool/API tests.

`agent_tool_call` adapters are optional model-facing actor adapters for governed tools. Workstream-agent adapters are model-facing exposures of governed tools for the selected workstream; internal-agent adapters are backend AI-worker exposures. Human chat tool plans are human-backed, model-assisted plan/confirmation adapters, not autonomous agent authority. Side-effecting `agent_tool_call` adapters require explicit permission and should default to proposal or approval flows unless a bounded autonomous policy is accepted.

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
- optional user administration or optional agent administration when the SaaS Foundation App/domain scope requires them.

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
+ governed tools inside backend capabilities
+ Akka horizontals needed for those governed tools and capabilities
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
- [ ] User Admin and Agent Admin functional agents are present for SaaS Foundation App scope, or the narrower scope explicitly defers them.
- [ ] Internal agents are distinguished from functional agents and have governed behavior, tool boundaries, and traces.
- [ ] Surfaces are typed renderable artifacts with schemas, allowed actions, states, and rendering tests.
- [ ] System messages are modeled as typed surfaces, not ad hoc strings.
- [ ] `surface_action`, confirmed `human_chat_tool_plan`, `agent_tool_call`, `api_call`, `workflow_step`, `timer_invocation`, and `consumer_reaction` adapters map to backend capabilities and shared governed-tool ids where they perform the same operation.
- [ ] Prompt-entered surface/workstream requests, surface actions, My Account panels, rail selection, and deep links share one shell request pipeline with canonical prompt feedback, origin metadata, target-workstream-only request rendering, backend authorization, denial/system-message behavior, and tests.
- [ ] Confirmed human chat tool plans, when modeled, include detailed plan review, explicit confirmation binding, deterministic backend authorization, per-tool transaction/idempotency behavior, traces, and result/partial-failure surfaces.
- [ ] Capability-first backend design remains intact: auth, scope, validation, idempotency, side effects, approval, audit, actor adapters/exposure channels, and tests are defined before Akka component selection.
- [ ] Akka components are selected as horizontal implementation details from capability semantics.
- [ ] The UI shell includes left rail functional agents, main workstream, persistent composer, context/authority indicators, denial/recovery states, and trace links.
- [ ] Page-first, CRUD-first, and chatbot-bolt-on alternatives are not presented as equal generated-app defaults.

## App-description layer ownership

When this model is maintained in an app-description tree, keep ownership split by current-intent graph nodes:

- `domains/<domain>/workstreams/<workstream>/**` owns application meaning: functional agents, internal agents, durable workstreams, workstream expert bundles, surface contracts/bindings, reusable surface placement, action-to-capability mappings, trace semantics, readiness, and surface/action tests.
- `domains/<domain>/capabilities/**`, `domains/<domain>/data-state/**`, and `global/tools/**` own capability groupings, governed-tool contracts, durable state, and reusable operation semantics.
- Workstream `realization/frontend-routes.md`, `realization/api-contracts.md`, and related frontend realization notes own browser realization: shell rendering, functional-agent rail, workstream panel, persistent composer, structured-surface rendering, routes/deep links, forms/interactions, frontend API contracts, state/realtime, accessibility/responsive behavior, and style guide links.
- Realization nodes and generated frontend/backend source are downstream projections, not authoritative product meaning.

Legacy numbered folders such as `12-workstreams/`, `10-capabilities/`, `55-ui/`, and `60-generation/` may be maintained only as compatibility projections when they are clearly mapped to the current-intent graph above; do not present them as the canonical layout for new app-description work.

## Routing implications

For high-level product input, apply this sequence:

1. Preserve the mandatory secure AI-first SaaS foundation.
2. Apply `./requirements-to-workstream-development-process.md`: workstream inventory, attention categories, dashboard contracts, surfaces/actions, governed tools inside capability/API contracts, Akka substrate, agent/AutonomousAgent workers, events/notifications/projections, traces, and tests.
3. Interpret the product as an agent workstream application unless explicitly out of scope.
4. Identify functional agents, internal agents, initial workstreams, structured surfaces, and retained human authority.
5. Model governed backend capabilities for every surface action, tool, workflow step, API, timer, consumer, and internal operation.
6. Route to app-description maintenance, solution decomposition, PRD/spec/backlog planning, or focused implementation.
7. Use web UI and agent skills to implement the workstream shell and governed agents.
8. Use Akka component skills to implement the horizontal substrate from accepted capability contracts.

Use `./workstream-expertise-model.md` with this doctrine when a functional agent needs governed skills, reference documents, manifests, loader authorization, tool boundaries, traces, and tests that make it an expert in its workstream. Use `./agent-component-selection-guide.md` before choosing whether supporting agent work should be a request-based `Agent`, `AutonomousAgent`, `Workflow`, `Workflow + Agent`, or `Workflow + AutonomousAgent`.

When a target app-description already has or receives workstream surface contracts or legacy `12-workstreams/surface-contracts/**` compatibility files, run `tools/validate-surface-contracts.sh <app-description-dir>` as a lightweight structural check before treating the surface layer as ready for implementation.

Use `./current-intent-model.md` when capturing domain-level and workstream-level intent for the core SaaS app domain or app-specific domains. It defines the current app/domain/workstream graph, global definitions, workstream bindings, realization mappings, and tests.

This doctrine should be referenced by future app-description, web UI, agent, routing, and review tasks as the single generated-app UI/application architecture default.
