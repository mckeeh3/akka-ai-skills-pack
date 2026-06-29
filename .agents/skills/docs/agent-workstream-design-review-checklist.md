# Agent Workstream Design Review Checklist

## Purpose

Use this checklist when reviewing generated full-stack AI-first SaaS doctrine, skills, app descriptions, specs, examples, SaaS Foundation Apps, and generation guidance. It is a compact guardrail for keeping design content aligned with `./workstream-contract.md`, `./workstream-attention-contracts.md`, the canonical workstream graph, and governed-tool model.

Canonical model:

```text
secure SaaS foundation
→ affected workstream(s)
→ role-specific dashboard surfaces and attention items
→ human surface graph nodes/actions
→ internal workstream agent graph nodes/delegations/results
→ workstream expertise skills/references
→ governed-tools inside capability files and surface/action maps
→ qualified exposure channels: browser-tool, agent-tool, internal-tool, workflow-tool, timer-tool, consumer-tool, MCP-tool
→ horizontal Akka implementation and full runtime/API/UI validation
```

## Review checks

### Workstream decomposition and incremental inputs

- [ ] Workstream definition/type, runtime workstream instance/thread/log, and browser workstream view/session terminology are not conflated.
- [ ] Each workstream records the compact contract fields from `./workstream-contract.md`: id, responsibility, classification, exactly-one owning functional agent, required managed-agent definition id, icon metadata with tooltip, instance scope, AuthContext, default surface, attention, surface graph, capability/governed-tool map, expertise, internal agent graph, retention, traces, tests, and readiness level.
- [ ] Broad PRDs first decide whether the requested functionality belongs in one workstream, multiple workstreams, core workstreams, app-specific workstreams, or shared foundation concerns.
- [ ] Incremental feature requests, fixes, revised PRDs, runtime-validation findings, and support issues reconcile against existing workstream graphs instead of creating parallel duplicate workstreams, surfaces, or governed-tools.
- [ ] Authenticated consequential work areas are modeled first as role-authorized functional/context-area agents, not as pages, screens, CRUD modules, generic dashboards, or chat sessions.
- [ ] First use defines the alias `functional/context-area agent`; later use may shorten to `functional agent`.
- [ ] SaaS Foundation App scope includes My Account, User Admin, Agent Admin, Audit/Trace, Governance/Policy, and required governance/admin surfaces, or explicitly records a narrower deferred scope.

### Role-specific dashboard surfaces and attention

- [ ] Each workstream has role-specific dashboard surfaces whose primary purpose is to show what requires that actor's attention and what work can or should be done next.
- [ ] Dashboard behavior is role/AuthContext/tenant/customer scoped; different roles may see different attention items, allowed browser-tools, data visibility, and escalation obligations.
- [ ] Dashboard attention items identify source, freshness, severity/lifecycle, evidence basis, authority basis, trace/correlation ids, and whether the source is a projection/view, computed source, internal-agent result, policy/evidence result, external state, or mixed source.
- [ ] My Account and left-rail attention projections aggregate only authorized attention and do not replace per-workstream role-specific dashboards.
- [ ] Attention items follow `./workstream-attention-contracts.md`: backend producer id/version, deterministic idempotency, lifecycle, source/evidence refs, redaction, trace ids, stale/recompute behavior, and authorized actions.

### Human surface graph

- [ ] The dashboard is the trunk of the human work tree; surface graph nodes include list/search/filter, graph/trend, detail/admin/edit, decision, approval, trace, workflow/progress, result, and system-message surfaces as needed.
- [ ] Surface graph edges are explicit: button, link, row click, card click, prompt suggestion, deep link, refresh, or result transition.
- [ ] Every edge records source surface, target/result/system-message surface behavior, guarded state, role/AuthContext requirements, idempotency, audit/trace, denial behavior, and tests.
- [ ] Every important dashboard, form, table, chart, detail card, decision/approval/exception card, diff, audit timeline, workflow status card, evidence bundle, version card, or outcome panel is modeled as a structured surface.
- [ ] Each surface has stable id/type/version, owning functional agent, reusable functional-agent placement if any, typed payload schema, redaction rules, AuthContext/tenant/customer assumptions, trace/correlation fields, and rendering tests.
- [ ] Each surface records loading, empty, ready, submitting, success, pending, approval-needed, forbidden, validation-error, conflict, stale/reconnect, partial-data, and failure states as applicable.

### Internal workstream agent graph

- [ ] Internal agents are separated from left-rail functional agents and modeled as nodes in an internal workstream agent graph, not as hidden navigation workstreams.
- [ ] Each internal virtual dashboard agent identifies what requires agent attention, what can be delegated to worker internal agents, what can be resolved automatically, and what must be escalated to humans.
- [ ] Internal worker agents have governed definitions, prompt/skill/reference ownership, model policy, governed-tool exposure as agent-tools/internal-tools, tool boundaries, authority basis, trace obligations, and tests.
- [ ] Delegation/result edges record task lifecycle, progress/result surfaces, notifications/projections, failure/cancellation behavior, proposal/escalation outputs, and human attention creation or resolution.

### Workstream expertise

- [ ] Each LLM-backed workstream has governed workstream expertise skills/references that describe role-specific dashboard purpose, surface graph behavior, surface contracts, available governed-tools, denials, examples, and what users can do.
- [ ] Workstream expertise is represented through governed prompt/skill/reference documents, compact manifests, loader authority, `readSkill`/`readReferenceDoc`, `ToolPermissionBoundary`, prompt/skill/reference/load traces, and tests.
- [ ] Workstream expertise helps agents explain and operate the workstream but does not grant authorization or expand governed-tool authority.

### Governed capabilities, governed-tools, and auth

- [ ] Capabilities are product-level abilities or groupings. Executable operations/queries are modeled as governed-tools inside capability files and surface/action maps.
- [ ] Every surface action, workstream action, agent-tool, browser-tool, workflow-tool, timer-tool, consumer-tool, MCP-tool/resource, API, view/query, or internal-tool maps to a governed-tool id and a capability id.
- [ ] Governed-tool contracts define actors/callers, AuthContext, tenant/customer scope, input/output schemas, validation, side effects, idempotency, policy/approval/escalation, audit/work trace, exposure surfaces, and tests.
- [ ] Surface action descriptors and action requests carry `browserToolId`, `governedToolId`, `capabilityId`, source/target surface ids where relevant, idempotency, correlation, and audit/trace requirements.
- [ ] Backend authorization is authoritative for every protected command, query, stream, browser-tool, agent-tool, workflow action, consumer side effect, and timer action.
- [ ] Frontend visibility, disabled buttons, prompts, expertise text, and agent-tool descriptions are UX/model guidance only; they are never treated as authorization controls.
- [ ] Tests include authorization denial, tenant isolation, disabled-user/role/scope denial, idempotency/no-op behavior, audit/trace emission, rendering, browser-tool/API behavior, agent-tool behavior, and internal-agent delegation where relevant.

### App-description layer ownership

- [ ] `domains/<domain>/workstreams/<workstream>/**` owns the application model: functional agents, workstream definitions vs instance semantics, retention/redaction, attention categories, role-specific dashboards, internal agents, human surface graph, internal workstream agent graph, workstream expertise links, surface contracts/bindings, reusable surface placement, action-to-governed-tool/capability mappings, trace semantics, readiness labels, and surface/action tests.
- [ ] `domains/<domain>/capabilities/**`, `domains/<domain>/data-state/**`, and `global/tools/**` own capability groupings, governed-tool definitions, durable state, and reusable tool semantics; governed tools are not treated as frontend actions, raw routes, or prompt-only operations.
- [ ] Workstream realization files such as `realization/frontend-routes.md` and `realization/api-contracts.md`, plus target frontend source, own browser realization: shell rendering, rail, panel/composer, surface rendering, routes/deep links, interactions/forms, frontend API contracts, state/realtime, accessibility/responsive behavior, and style guide links.
- [ ] Realization files do not redefine the application model owned by workstreams, capabilities, data-state, policies, traces, and tests; they link back to functional agents, surfaces, governed-tools/capabilities, security, observability, and tests.
- [ ] Generated frontend/backend files are downstream projections, not authoritative sources of product meaning.

### Routes, UI realization, and style guide

- [ ] Routes, pages, and deep links are implementation details for shell entry, selected functional agent, stream item, or direct surface access; they are not the primary decomposition.
- [ ] New generated SaaS frontend realization points to the workstream reference architecture under `frontend/src/workstream/**` and vertical contract tests, not legacy `frontend/src/screens/**` page-first structure.
- [ ] `realization/frontend-routes.md` maps each route/deep link to the selected functional agent, workstream item, structured surface, or surface graph edge it opens; legacy `55-ui/routes-and-deep-links.md` may remain only as a compatibility projection.
- [ ] A style-guide selection exists in current-intent surface/frontend realization nodes before web UI realization; if no style is selected, web UI implementation/generation is blocked by a pending style question rather than inventing visual styling.
- [ ] Frontend API contracts and realtime behavior preserve `/api/me`, selected AuthContext, `browserToolId`, `governedToolId`, `capabilityId`, denial/error DTOs, idempotency/correlation ids, trace links, and stale/reconnect behavior.

### Legacy and mechanics cleanup

- [ ] Legacy page/screen/navigation examples are removed or explicitly migrated into current generated-SaaS architecture.
- [ ] Static asset hosting and endpoint wiring examples are retained only when they directly support focused skills-pack code examples.
- [ ] Historical domain-specific planning examples are not linked as generic description or queue mechanics.
- [ ] Consolidated historical files are either migrated into current-intent domain/workstream/capability/realization ownership or explicitly labeled legacy compatibility; numbered `12-workstreams/`, `10-capabilities/`, `55-ui/`, and `60-generation/` paths are not canonical for new work.

### Validation tooling

- [ ] `tools/validate-workstream-contracts.sh <app-description-dir>` passes for app-description trees that claim workstream-contract completeness.
- [ ] `tools/validate-surface-contracts.sh <app-description-dir>` passes for app-description trees that claim surface-contract completeness.

## Pass condition

A design artifact passes when it keeps application meaning in functional agents, workstream definitions/instances, role-specific dashboards, backend-owned attention, human surface graphs, internal workstream agent graphs, workstream expertise, governed-tools/capabilities, readiness labels, and mandatory security; treats browser UI and routes as realization details; and excludes removed page-first or standalone static UI references from generated-app guidance.
