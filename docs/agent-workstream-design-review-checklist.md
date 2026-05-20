# Agent Workstream Design Review Checklist

## Purpose

Use this checklist when reviewing generated full-stack AI-first SaaS doctrine, skills, app descriptions, specs, examples, and generation guidance. It is a compact guardrail for keeping design content aligned with the canonical agent workstream model.

Canonical model:

```text
secure SaaS foundation
→ functional/context-area agents
→ durable workstreams
→ typed structured surfaces
→ governed backend capabilities
→ horizontal Akka implementation
```

## Review checks

### Functional/context-area agents

- [ ] Authenticated consequential work areas are modeled first as role-authorized functional/context-area agents, not as pages, screens, CRUD modules, dashboards, or chat sessions.
- [ ] First use defines the alias `functional/context-area agent`; later use may shorten to `functional agent`.
- [ ] Each functional agent has purpose, tenant/customer scope, allowed roles/capabilities, default briefing/dashboard/attention surface, callable capabilities, traces, and tests.
- [ ] Full core SaaS scope includes Access/Profile, User Admin, Agent Admin, Audit/Trace, and required governance/admin surfaces, or explicitly records a narrower deferred scope.
- [ ] Internal agents are separated from left-rail functional agents and have governed definitions, prompt/skill/tool boundaries, authority basis, traces, and tests where applicable.

### App-description layer ownership

- [ ] `12-workstreams/` owns the application model: functional agents, internal agents, durable workstreams, surface index, surface contracts, reusable surface placement, action-to-capability mappings, trace semantics, and surface/action tests.
- [ ] `55-ui/` owns browser realization: shell rendering, rail, panel/composer, surface rendering, routes/deep links, interactions/forms, frontend API contracts, state/realtime, accessibility/responsive behavior, and style guide.
- [ ] `55-ui/` does not redefine the application model that belongs in `12-workstreams/`; it links back to functional agents, surfaces, capabilities, security, observability, and tests.
- [ ] `60-generation/` and realized frontend files are downstream projections, not authoritative sources of product meaning.

### Structured surfaces

- [ ] Every important dashboard, form, table, chart, detail card, decision/approval/exception card, diff, audit timeline, workflow status card, evidence bundle, version card, or outcome panel is modeled as a structured surface.
- [ ] Each surface has stable id/type/version, owning functional agent, reusable functional-agent placement if any, typed payload schema, redaction rules, AuthContext/tenant/customer assumptions, trace/correlation fields, and rendering tests.
- [ ] Each surface records loading, empty, ready, submitting, success, pending, approval-needed, forbidden, validation-error, conflict, stale/reconnect, partial-data, and failure states as applicable.
- [ ] Surface actions are listed with labels, inputs, confirmation/approval needs, idempotency expectations, success/denial/error result surfaces, audit/trace requirements, and tests.

### Governed capabilities and auth

- [ ] Every surface action, workstream action, agent tool, workflow step, API, timer, consumer reaction, MCP tool/resource, or internal call maps to a governed backend capability.
- [ ] Capability contracts define actors/callers, AuthContext, tenant/customer scope, input/output schemas, validation, side effects, idempotency, policy/approval/escalation, audit/work trace, exposure surfaces, and tests.
- [ ] Backend authorization is authoritative for every protected command, query, stream, tool, workflow action, consumer side effect, and timer action.
- [ ] Frontend visibility, disabled buttons, prompts, and tool descriptions are UX hints only; they are never treated as authorization controls.
- [ ] Tests include authorization denial, tenant isolation, disabled-user/role/scope denial, idempotency/no-op behavior, audit/trace emission, and rendering/tool/API behavior where relevant.

### Routes, UI realization, and style guide

- [ ] Routes, pages, and deep links are implementation details for shell entry, selected functional agent, stream item, or direct surface access; they are not the primary decomposition.
- [ ] New generated SaaS frontend realization points to the workstream reference architecture under `frontend/src/workstream/**` and the User Admin vertical contract tests, not `frontend/src/screens/**`.
- [ ] `55-ui/routes-and-deep-links.md` maps each route/deep link to the selected functional agent, workstream item, or structured surface it opens.
- [ ] A `55-ui/style-guide.md` selection exists before web UI realization; if no style is selected, web UI implementation/generation is blocked by a pending style question rather than inventing visual styling.
- [ ] Frontend API contracts and realtime behavior preserve `/api/me`, selected AuthContext, capability ids, denial/error DTOs, idempotency/correlation ids, trace links, and stale/reconnect behavior.

### Legacy and mechanics quarantine

- [ ] Legacy page/screen/navigation examples are labeled as compatibility or mechanics references only and are not presented as canonical generated-SaaS architecture.
- [ ] Static asset hosting examples, endpoint wiring examples, and `frontend/src/screens/**` are quarantined as implementation mechanics or stale references unless explicitly migrated.
- [ ] Purchase-request or other conventional examples are linked only for description mechanics when generated AI-first SaaS target architecture is in scope.
- [ ] Consolidated historical files such as `55-ui/ui-surfaces.md` are either migrated into `12-workstreams/` + split `55-ui/` ownership or clearly labeled as reference-specific/non-canonical.

## Pass condition

A design artifact passes when it keeps application meaning in functional agents, workstreams, structured surfaces, governed capabilities, and mandatory security; treats browser UI and routes as realization details; and quarantines legacy page-first or static mechanics references.
