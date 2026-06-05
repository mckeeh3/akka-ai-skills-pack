---
name: app-description-surface-modeling
description: Model structured workstream surfaces in app descriptions, including typed payloads, reusable functional-agent placement, capability-backed actions, rendering states, auth, traces, and tests.
---

# App Description Surface Modeling

Use this skill when maintaining `app-description/12-workstreams/surfaces-index.md`, `app-description/12-workstreams/surface-contracts/**`, or equivalent structured-surface artifacts for generated full-stack AI-first SaaS apps.

A surface is a typed renderable artifact in an agent workstream. It is not a page, route, CRUD screen, chat message, or backend component. Surfaces belong to or are reused by functional agents, render in durable workstreams, form a human surface graph rooted in role-specific dashboard surfaces, and expose only actions that map to governed backend capabilities and governed-tools.

## Required reading

Read these first if present:
- target project path: AGENTS.md
- `../README.md`
- `../docs/agent-workstream-application-architecture.md`
- `../docs/requirements-to-workstream-development-process.md` for attention/dashboard/surface-action/autonomous task notification semantics
- `../docs/structured-surface-contracts.md`
- `../docs/internal-app-description-architecture.md`
- `../docs/app-description-maintenance-flow.md`
- `../docs/capability-first-backend-architecture.md`
- `../app-descriptions/SKILL.md`
- `../agent-workstream-apps/SKILL.md`
- `../app-description-functional-agent-modeling/SKILL.md`
- `../app-description-capability-modeling/SKILL.md`
- `../app-description-ui/SKILL.md`
- existing `app-description/12-workstreams/**`
- existing `app-description/55-ui/**`
- existing `app-description/70-traceability/surface-to-capability-map.md`
- `../templates/ai-first-saas-core-app/app-description/README.md` and its `12-workstreams/**`, `55-ui/**`, and `70-traceability/**` files when bootstrapping or repairing core app surface contracts
- `../docs/examples/domain-workstream-surface-contract-example.md` when a domain-specific surface example is useful
- `../tools/validate-surface-contracts.sh` when validating a target app-description surface layer

## Use this skill when

The task asks to:
- add, remove, split, or revise a dashboard, briefing, form, table, chart, decision card, approval card, diff, audit timeline, detail card, workflow status, evidence bundle, version card, exception card, system-message surface, result surface, or outcome panel;
- define what a functional agent renders in its workstream;
- model surface graph nodes and edges from role-specific dashboard trunk to attention, evidence, decision, trace, progress, result, and denial surfaces;
- map surface actions to backend capabilities and governed-tools, including browser-tools and surface-request actions;
- specify reusable surfaces shared by multiple functional agents;
- capture surface payload schemas, redaction, loading/error/forbidden/stale states, accessibility, responsive behavior, realtime updates, or rendering tests;
- update app-description artifacts before workstream UI implementation.

Use `app-description-functional-agent-modeling` first when the change primarily adds or changes a left-rail functional agent or its workstream icon semantics. Use `app-description-ui` for shell layout, rail behavior, icon rendering/interaction, composer behavior, visual style, route/deep-link details, and frontend project handoff after the surface contract is clear.

## Artifact targets

Prefer these app-description artifacts:

```text
app-description/12-workstreams/
  surfaces-index.md
  surface-contracts/
    01-<surface-id>.md

app-description/55-ui/
  structured-surface-rendering.md
  states-and-realtime.md
  accessibility-and-responsive.md
  frontend-api-contracts.md

app-description/70-traceability/
  surface-to-capability-map.md
```

Create or update only the smallest files needed. Keep `12-workstreams/surface-contracts/**` authoritative for what a surface means; keep `12-workstreams/functional-agents.md` authoritative for workstream icon assignment and meaning; keep `55-ui/**` focused on rendering, interaction, route/deep-link, style, and frontend API realization details.

When the target app has no usable core app surface layer, copy and adapt the source-controlled core app files from `../templates/ai-first-saas-core-app/app-description/**` into the target project's `app-description/**`. Do not reference retired distribution output directories as template sources.

## Canonical surface types

Use these types unless the product intent requires a more specific typed surface:

- `markdown_response` for the minimum five-core-workstream core app domain (My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy) and other low-ceremony explanatory replies that still require a typed payload, sanitized rendering, trace/correlation ids, explicit states, accessibility, and rendering/security tests; do not treat it as an informal chat blob or as a substitute for richer typed surfaces when decisions, approvals, forms, tables, settings, audit timelines, or workflow status are required;
- `dashboard` / `attention-surface`;
- `autonomous-task-progress` / `autonomous-task-result` / `notification-summary` for durable internal/background work surfaced through governed task lifecycle capabilities;
- `form` / `guided-intake`;
- `table` / `search-results`;
- `chart` / `metric-panel`;
- `detail-card`;
- `decision-card`;
- `approval-card`;
- `exception-card` / `deviation-card`;
- `diff-review` / `proposed-change-review`;
- `audit-timeline` / `work-trace-timeline`;
- `workflow-status` / `progress-card`;
- `evidence-bundle`;
- `policy-version-card`, `prompt-version-card`, or `skill-version-card`;
- `outcome-review-panel`.

## Surface contract

For each surface, capture the fields below. Use `../docs/structured-surface-contracts.md` as the canonical implementation contract when the surface must specify payload envelopes, action envelopes, realtime events, auth, trace/audit fields, and rendering tests.

- stable surface id, display name, type, and version;
- purpose and user outcome;
- owning functional agent and reusable functional agents;
- workstream placement: default entry dashboard, attention queue, embedded response, drill-in, modal, side panel, system-message surface, result surface, or deep-linkable surface;
- surface graph role: dashboard trunk, node type, incoming edges, outgoing edges, prompt-entered shortcuts, deep links, result/system-message transitions, and cross-workstream surface-request constraints;
- attention contribution: category, severity, lifecycle state, target audience, My Account aggregation, left-rail count behavior, and when the surface opens/resolves/dismisses/escalates an attention item;
- payload schema: required fields, optional fields, lists, nested records, field formats, correlation ids, trace ids, pagination, sorting, autonomous task ids, notification ids, and realtime event ids;
- redaction and safe fields for user roles, support roles, auditors, functional agents, and internal agents;
- data sources and read/evidence capabilities behind the payload;
- allowed actions with labels, input payloads, confirmation requirements, idempotency keys, linked backend capability ids, and linked governed-tool ids/exposure names, including browser-tools and surface-request actions such as `show_surface`, `open_workstream`, `refresh_surface`, and `open_attention_item` for buttons, links, cards, rows, My Account panels, rail entries, deep links, or icons that open another protected surface/workstream;
- autonomous task bindings when applicable: start/query/cancel/result-read/external-complete/external-fail capabilities, task lifecycle states, snapshot/result payloads, progress notifications, dependencies, and escalation/attention rules;
- action authority: AuthContext, tenant/customer scope, role/capability requirements, approval/policy gates, and denial behavior;
- action side-effect visibility: success, pending, approval-needed, queued, workflow-started, no-op, and failed states;
- loading, empty, error, forbidden, stale, reconnecting, conflict, and partial-data states;
- notification/projection behavior: which domain events, AutonomousAgent task notifications, workflow events, consumer/timer results, or audit signals update the surface, dashboard, My Account, and left rail;
- realtime behavior: SSE/WebSocket/update polling, event ordering, stale markers, and recovery;
- accessibility and responsive expectations;
- audit/work-trace links visible to users, supervisors, admins, or auditors;
- rendering tests and capability/action tests.

## Standard output shape

Use the delta modeling contract in `../docs/app-description-skill-output-contracts.md`. For this surface skill, report the requested change, authoritative layer/file targets, in-scope and out-of-scope behavior, authority/scope, DTOs or payloads where relevant, side effects/idempotency/denials/traces/tests, linked layers, assumptions, and next handoff. Avoid repeating the full app-description layer model.

## Modeling rules

1. **Surfaces are structured artifacts.** Prefer typed payloads and explicit actions over free-text responses or page descriptions, and place each important surface in the workstream surface graph.
2. **Surfaces may be reused.** A surface can be owned by one functional agent and rendered by others when the same payload, action, auth, and trace contracts hold.
3. **Surface actions are not authorization.** Every action maps to a governed backend capability and executable governed-tool; backend authorization remains authoritative even when an action is hidden or disabled in the UI. Human-facing executable actions are `browser-tool` exposures. Controls that open protected surfaces or workstreams are governed surface-request actions, not frontend-only navigation. Surface-request actions use the shell request pipeline: canonical prompt feedback (`show surface <surface-id>` or `show workstream <workstream-id>`), honest origin metadata, target-workstream-only request rendering, and typed denial/system-message behavior.
4. **Payloads are safe by contract.** Record redaction, role-dependent fields, support/auditor visibility, secret boundaries, and tenant/customer scoping.
5. **Actions preserve capability semantics.** Carry AuthContext, validation, idempotency, side effects, policy/approval, audit, and denial semantics from the capability layer.
6. **Routes are subordinate.** Pages and deep links may address a surface, but they do not replace the functional-agent/workstream/surface model.
7. **States are part of the surface.** Loading, empty, error, forbidden, stale/reconnect, conflict, partial-data, autonomous task progress/result, and notification update behavior must be described before generation.
8. **Attention and dashboards are governed projections.** Role-specific dashboard badges, My Account counts, and left-rail indicators derive from backend state/projections linked to surface graph actions and capability/governed-tool results, not frontend-only notification math.
9. **Tests are mandatory.** A surface contract is incomplete without rendering, action, authorization, tenant-isolation, denial, audit/trace, and relevant realtime tests.

## Change handling

When a surface change adds or changes:

- owning or reusable functional agents, update `12-workstreams/functional-agents.md` via `app-description-functional-agent-modeling`;
- an action, query, command, approval, workflow launch, or side effect, update `10-capabilities/` via `app-description-capability-modeling`;
- shell rendering, layout, prompt-to-surface request routing, cross-workstream surface request behavior, route/deep-link, frontend API, state, realtime, accessibility, responsive behavior, icon rendering, tooltip/focus behavior, or style, update `55-ui/**` via `app-description-ui`;
- authorization, visibility, redaction, or denial behavior, update auth/security and `/api/me` capability exposure expectations;
- audit, trace links, correlation ids, or diagnostic evidence, update observability;
- acceptance, rendering, or security expectations, update tests and traceability maps.

## Handoff rules

Route onward as needed:

- to `app-description-functional-agent-modeling` for functional-agent ownership, reuse, rail visibility, workstream icon metadata, workstream entry behavior, prompt/tool boundary, and tests;
- to `app-description-capability-modeling` for any surface action or payload capability contract;
- to `app-description-ui` for rendering, layout, frontend API contracts, state/realtime, accessibility, responsive behavior, style, routes, and deep links;
- to `app-description-auth-security` for role visibility, action authority, redaction, forbidden states, and tenant/customer isolation;
- to `app-description-observability` for audit/work trace, correlation, evidence, and investigation links;
- to `app-description-test-specification` for rendering, authorization, capability invocation, realtime, denial, audit, and tenant-isolation tests;
- to `akka-web-ui-apps` only when realization of structured surfaces is requested.

## Anti-patterns

Avoid:
- treating a surface as a CRUD page or route-first screen;
- using chat text where a typed decision card, approval, table, detail card, diff, timeline, dashboard, or workflow status is required;
- flattening a workstream surface graph into an untyped page list;
- exposing UI actions without capability ids and governed-tool ids/exposure names;
- assuming hidden/disabled buttons enforce authorization;
- duplicating the same surface under multiple functional agents instead of modeling reuse;
- omitting redaction, tenant/customer scope, forbidden states, stale behavior, or tests;
- letting agent tool descriptions or prompt text define action authority.

## Final review checklist

Before finishing a surface update, verify:

- [ ] every surface has type, version, owner/reuse, payload schema, redaction, states, actions, traces, and tests;
- [ ] canonical surface types include dashboards, forms, tables, charts, decision cards, diffs, audit timelines, detail cards, approvals, and workflow status where relevant;
- [ ] each allowed action maps to a governed backend capability;
- [ ] backend authorization remains authoritative over UI visibility;
- [ ] surfaces can be reused across functional agents when appropriate;
- [ ] UI rendering details are linked without making page/screen hierarchy primary;
- [ ] surface-to-capability traceability is updated;
- [ ] `tools/validate-surface-contracts.sh <app-description-dir>` passes when the target project includes a surface-contract layer and the tool is available.
