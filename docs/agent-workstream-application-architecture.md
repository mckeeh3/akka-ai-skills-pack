# Agent Workstream Application Architecture

## Status and scope

This is the canonical agent workstream application architecture doctrine for this skills pack.
It defines the mandatory default UI/application model for generated full-stack secure AI-first SaaS applications.

Use this doctrine below `ai-first-saas-application-architecture.md` and above detailed app-description, web UI, agent, and Akka component guidance. It does not replace `capability-first-backend-architecture.md`: workstream UI actions, agent tools, workflow steps, APIs, timers, consumers, and internal calls still map to governed backend capabilities before Akka implementation is selected.

## Default generated-app architecture

Generated AI-first SaaS apps are **agent workstream applications by default**. The **workstream is the root application abstraction** for authenticated consequential work. First use: **functional/context-area agent** means a user-facing, role-authorized agent for one durable work area; this document shortens the term to **functional agent** after defining the alias.

```text
workstream
→ exactly one backing functional/context-area agent
→ continuous request/result flow
→ structured renderable surfaces, including system-message surfaces
→ governed backend capabilities
→ horizontal Akka implementation
```

The primary application model is not a conventional page tree, CRUD console, or traditional app with a chatbot attached. Authenticated consequential work areas should be modeled as workstreams, each backed by exactly one functional agent and implemented through structured surfaces. Traditional routes may still exist for implementation, deep linking, static/public pages, asset hosting, or direct surface URLs, but they are not the primary architecture.

## Minimum initial core workstream set

The smallest generated AI-first SaaS starter is a bootstrap-authorized **five core workstream v0 set**, not a generic chatbot. Use this interpretation for prompts such as "minimum AI-first app," "starter app," "basic app," or "initial chatbot" unless the user explicitly asks for non-SaaS reference material.

The minimum initial left rail contains these role-authorized functional agents for the bootstrap operator:

1. **My Account Agent**
2. **User Admin Agent**
3. **Agent Admin Agent**
4. **Audit/Trace Agent**
5. **Governance/Policy Agent**

Each core v0 workstream is intentionally narrow, but each must still use the same shell model:

- **Left rail** — exposes the five role-authorized core functional agents for the bootstrap operator; unavailable richer full-core actions or surfaces are represented as explicit deferred/denied behavior, not hidden readiness claims.
- **Main workstream panel** — renders a durable request/response timeline for the selected core workstream with capability results, denials, trace references, and the first structured surface type: `markdown_response`.
- **Persistent composer** — accepts natural-language bootstrap requests for the selected core functional agent; it is an input channel, not an authorization boundary.
- **Context and authority indicators** — show selected AuthContext, bootstrap role/capability basis, available capabilities, denied/deferred actions, and trace links.

The first renderable surface for each core workstream is `markdown_response`: model-authored markdown in a versioned surface payload, rendered as sanitized HTML with trace/correlation ids and explicit loading, success, error, forbidden, and empty states. It is a real structured surface contract, not an informal chat blob; richer surfaces such as profile/settings cards, user tables, access-review queues, agent behavior diffs, audit timelines, and policy/approval cards can be added after the v0 slice.

Audit/work trace recording starts in this first slice for identity basis, AuthContext selection, capability checks, prompt/tool use, denials, and rendered responses. The Audit/Trace workstream is present from the first runnable starter, but it may initially explain and link to the trace substrate through `markdown_response` before richer search and investigation surfaces are implemented.

Full-core generated SaaS readiness remains stricter than this minimum starter. The five core workstream v0 set is a valid first runnable slice only when follow-up work remains explicit for complete My Account, User Admin, Agent Admin, Audit/Trace, Governance/Policy, invitations/onboarding, governed runtime agent documents, tenant isolation, and full security coverage.

## Core terms

| Term | Meaning |
|---|---|
| Agent workstream shell | Primary authenticated browser shell: role-authorized functional agents in the left rail, continuous workstream in the main panel, persistent composer at the bottom, and structured surfaces embedded in the stream. |
| Functional/context-area agent | User-facing, role-authorized assistant backing exactly one workstream and representing a functional area such as User Admin, Agent Admin, Procurement, Finance, Sales Pipeline, Support, Audit, Governance, or Outcome Metrics. Shortened to functional agent after first use. |
| Workstream agent | The functional agent in its role as the selected workstream user's assistant. It can answer workstream-specific “how do I...” questions, interpret shorthand requests such as “dashboard” or “show users”, request or refresh surfaces, invoke allowed capability-backed actions, explain denials/errors, and guide users through tasks. It is not the root app abstraction and does not grant authority. |
| Internal agent | Non-left-rail agent invoked by workflows, tools, consumers, timers, functional agents, or backend services for bounded work such as classification, summarization, evaluation, routing, replay, proposal drafting, or governance review. |
| Workstream | Root app unit for authenticated consequential work: a durable conversational and operational timeline backed by exactly one functional agent. It contains user requests, agent responses, tool/capability results, structured surfaces, system-message surfaces, decisions, workflow progress, traces, and follow-up actions. |
| Surface | Typed renderable artifact in a workstream, such as a dashboard, form, data table, chart, decision card, diff review, audit timeline, entity detail, approval card, workflow status, exception card, system message, or outcome metric panel. |
| Capability | Governed backend contract behind actions, queries, tools, workflows, timers, consumers, APIs, and internal component calls. Capabilities define authority, scope, schemas, side effects, idempotency, policy/approval, audit, exposure channels, and tests. |
| Horizontal implementation | Akka entities, workflows, views, consumers, timed actions, agents, endpoints, web UI code, auth/security, audit, and tests that implement capabilities for vertical functional agents and surfaces. |

## Agent workstream shell

The default authenticated app shell uses a familiar AI chat layout, but the left rail is not a list of casual chat sessions. It is a role-authorized functional-area launcher.

Required shell regions:

1. **Left rail functional agents** — show only agents the selected `AuthContext` may use. Examples: My Account, User Admin, Agent Admin, Governance/Policy, Audit/Trace, Support Access, Billing, Procurement, Finance, Sales Pipeline, Approval Queue, Risk & Exceptions. The signed-in user tile at the bottom of the rail should open the My Account workstream rather than a separate profile/settings menu.
2. **Main workstream panel** — shows a continuous vertical stream of user intent, agent responses, structured surfaces, capability results, workflow status, decision cards, traces, and links.
3. **Persistent composer** — accepts natural-language requests, commands, uploads where allowed, and contextual follow-ups for the selected functional agent.
4. **Context and authority indicators** — show selected tenant/customer context, active role/capability basis, pending approvals, trace links, and safe denial/recovery states.

Selecting a functional agent selects its workstream and should normally produce an initial dashboard, attention, or briefing surface for that work area. The selected workstream agent may also request surfaces in response to natural language: “dashboard”, “show the access review queue”, “find Alex”, or “how do I add a new user?” should resolve to guidance, surface requests, or capability-backed actions within that workstream. Clicking actions should append or update workstream surfaces rather than forcing users through a page-first navigation hierarchy.

## Workstreams and functional agents as verticals

A generated app grows by adding vertical workstreams. Each workstream is backed by exactly one functional agent. Each workstream/agent vertical should define:

- purpose and business responsibility;
- authorized roles/capabilities and tenant/customer scope;
- default dashboard, attention, or briefing surface;
- durable workstream semantics and retention expectations;
- user intents the workstream agent must understand, including help/how-to prompts, shorthand surface requests, read requests, proposals, approvals, and allowed commands;
- prompt intent and a workstream expert bundle where LLM behavior is involved: governed prompt refs, skills, reference documents, compact expertise manifest, loader rules, and tool boundaries;
- surfaces the workstream can render or reuse;
- surface actions, including command actions and query/surface-request actions;
- capabilities it can call directly or through tools/workflows;
- escalation, approval, denial, and exception behavior;
- audit/work trace requirements;
- tests for authorization, surface rendering, capability invocation, tenant isolation, and audit.

Foundation generated SaaS apps must include user-facing functional agents for secure operation, especially:

- **My Account Agent** for current account, context selection, profile, settings, sign out, and safe self-service. Its default dashboard should be a simple workstream surface with Profile, Settings, and Sign out actions; selecting Profile or Settings appends the corresponding request and response surface in the My Account flow.
- **User Admin Agent** for invitations, users, memberships, roles/capabilities, disabled access, access review, support access visibility, and admin audit.
- **Agent Admin Agent** for agent definitions, prompts, skills, manifests, tool boundaries, lifecycle, proposals, approvals, behavior tests, and traces.
- **Audit/Trace Agent** for security, authorization, data access, tool use, decision, workflow, and outcome investigation.
- **Governance/Policy Agent** where policies, prompts, skills, thresholds, approvals, simulations, or behavior changes are managed.

Other domains add their own workstreams backed by functional agents, such as Sales Pipeline, Account Management, Support, Marketing Campaign, Revenue Operations, Finance, Procurement, Inventory/Supply Chain, Order Management, HR/Workforce, Operations Control, Approval Queue, Executive Briefing, Risk & Exceptions, and Outcome Metrics.

Domain-specific intent should decompose into fully functional domain-specific workstreams before implementation. For each proposed domain workstream, capture: business purpose, backing functional agent, authorized roles/capabilities, default surface, required surfaces, user intents, surface actions, action-to-capability mappings, backend capability contracts, Akka component realization, audit/work traces, and tests. Domain workstreams are not special cases; they use the same workstream → functional agent → surfaces → capabilities → Akka substrate pattern as the core SaaS workstreams.

## Internal agents

Internal agents are horizontal or supporting workers, not primary application navigation units. Use them when responsibility boundaries justify bounded backend AI behavior that should not appear as a left-rail work area.

Common internal agents:

- classifier or router agent;
- summarizer or briefing agent;
- evaluator / LLM-as-judge agent;
- policy or governance reviewer;
- proposal/diff drafting agent;
- replay/simulation analyst;
- extraction, enrichment, or normalization agent;
- escalation triage agent.

Internal agents still require governed `AgentDefinition`, approved prompt/skill references, tool boundaries, model policy, AuthContext or service authority basis, trace emission, and deterministic tests where applicable.

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
- allowed actions mapped to backend capabilities;
- tenant/customer and AuthContext assumptions;
- loading, empty, error, forbidden, and stale/reconnect states where relevant;
- accessibility and responsive behavior expectations;
- rendering tests and capability/action tests.

A surface action is not authorization. The backend capability remains authoritative.

Surface actions include both state-changing actions and surface-request actions:

- **Read/query or surface-request actions** request another surface or refresh/update the current surface, such as `show_dashboard`, `search_users`, `view_user`, `open_audit_timeline`, or row-click-to-open-detail.
- **Command actions** submit changes through classic forms or action controls, such as invite, revoke, disable, save, resend, or update settings.
- **Proposal/approval/workflow actions** draft changes, request approval, approve/reject, start long-running work, or show progress.
- **Governance/trace actions** open diffs, simulations, trace details, audit timelines, or behavior-change review surfaces.

Every surface action maps to a governed backend capability. In most browser realizations, that means the action invokes a backend API that enforces authorization and returns a result surface, updated surface, workstream item, or typed `system_message` surface. Frontend-only navigation between consequential work surfaces is not enough; even “show dashboard” and “view user details” are read/evidence capabilities when scoped protected data is involved.

## Capabilities remain the backend contract

Workstreams are the root application abstraction, but capabilities remain the backend contract. Workstreams do not make agents, tools, surfaces, UI controls, or routes the backend design root.

For each operation or query exposed in a workstream, define the capability first:

- capability id and purpose;
- actors/callers, including functional agents, internal agents, humans, workflows, timers, consumers, services, or support roles;
- AuthContext, tenant/customer scope, role/capability requirements, and denial behavior;
- input/output schemas, validation, redaction, and idempotency;
- data access and side effects;
- policy, approval, escalation, and autonomy rules;
- audit/work-trace fields;
- exposure channels: surface/workstream action, browser API, workstream-agent tool, internal-agent tool, HTTP/gRPC/MCP endpoint, workflow step, timer, consumer, view, or internal call;
- success, validation, forbidden, tenant-isolation, idempotency, approval, audit, and rendering/tool/API tests.

Agent tools are optional capability exposure channels. Workstream-agent tools are conversational exposures of capabilities for the selected workstream; internal-agent tools are backend AI-worker exposures. Side-effecting agent tools require explicit permission and should default to proposal or approval flows unless a bounded autonomous policy is accepted.

## Horizontal Akka implementation

After vertical functional agents, surfaces, and capability contracts are clear, select Akka components horizontally:

| Need | Common Akka horizontal |
|---|---|
| Audit-grade decisions, policies, approvals, goals, traces, or lifecycle records | Event Sourced Entities |
| Current-state profiles, settings, or configuration | Key Value Entities |
| Long-running plans, approvals, retries, compensation, handoffs, or agent orchestration | Workflows |
| Curated dashboard, table, search, evidence, or audit read models | Views |
| Bounded planning, recommendation, summarization, classification, evaluation, or explanation | Agents |
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

Build generated apps in vertical slices:

```text
add or extend one workstream
+ exactly one backing functional agent
+ default dashboard/attention surface
+ one or two useful user intents/actions
+ governed capabilities
+ Akka horizontals needed for those capabilities
+ workstream UI rendering
+ authorization, audit, tenant-isolation, and surface tests
```

Then repeat as the workstream gains more surfaces, skills, tools, workflows, internal-agent support, and outcome loops.

## Readiness checklist

Before treating a generated full-stack AI-first SaaS app as architecture-ready, verify:

- [ ] Authenticated consequential work areas are modeled as workstreams backed by exactly one functional agent, not primarily as pages.
- [ ] Left rail entries select role-authorized workstreams/functional agents from backend capabilities and selected AuthContext.
- [ ] Each workstream/functional agent has purpose, authority, supported user intents, surfaces, capability mappings, traces, and tests.
- [ ] Each LLM-backed functional agent has a workstream expert bundle or explicit deferral covering skills, reference documents, manifests, loader authorization, tool boundaries, traces, and tests.
- [ ] User Admin and Agent Admin functional agents are present for full core SaaS scope, or the narrower scope explicitly defers them.
- [ ] Internal agents are distinguished from functional agents and have governed behavior, tool boundaries, and traces.
- [ ] Surfaces are typed renderable artifacts with schemas, allowed actions, states, and rendering tests.
- [ ] System messages are modeled as typed surfaces, not ad hoc strings.
- [ ] Surface actions, including surface-request actions, agent tools, APIs, workflows, timers, and consumers map to governed capabilities.
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
2. Interpret the product as an agent workstream application unless explicitly out of scope.
3. Identify functional agents, internal agents, initial workstreams, structured surfaces, and retained human authority.
4. Model governed backend capabilities for every surface action, tool, workflow step, API, timer, consumer, and internal operation.
5. Route to app-description maintenance, solution decomposition, PRD/spec/backlog planning, or focused implementation.
6. Use web UI and agent skills to implement the workstream shell and governed agents.
7. Use Akka component skills to implement the horizontal substrate from accepted capability contracts.

Use `workstream-expertise-model.md` with this doctrine when a functional agent needs governed skills, reference documents, manifests, loader authorization, tool boundaries, traces, and tests that make it an expert in its workstream.

Use `domain-workstream-prd-structure.md` when capturing domain-level and workstream-level PRDs for the core SaaS app domain or app-specific domains. It defines the directory structure for domains, workstreams, workstream-agent prompts/skills, surfaces, capabilities, and tests.

This doctrine should be referenced by future app-description, web UI, agent, routing, and review tasks as the single generated-app UI/application architecture default.
