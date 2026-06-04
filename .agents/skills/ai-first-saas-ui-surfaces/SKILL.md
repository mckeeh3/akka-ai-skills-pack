---
name: ai-first-saas-ui-surfaces
description: Design AI-first SaaS supervision, decision, governance, digest, audit, and goal-to-execution UI surfaces, then route implementation to Akka web UI, HTTP endpoint, view, workflow, agent, trace, and outcome skills.
---

# AI-First SaaS UI Surfaces

Use this companion after `ai-first-saas` when a product or feature needs browser UI for delegation, supervision, decisions, governance, digests, audit, or outcome review.

This is a UI-surface and routing skill. It does not replace `akka-web-ui-*`, HTTP endpoint, View, Workflow, Agent, or security implementation skills.

## Required reading

Read first:
- `../../docs/ai-first-saas-application-architecture.md`
- `../ai-first-saas/SKILL.md`

Use canonical doctrine plus this skill for surface selection. Also use `../../docs/agent-workstream-application-architecture.md` and `../../docs/structured-surface-contracts.md` when deciding where a surface belongs in the agent workstream model. Use the upstream runnable core app repository root as the canonical full-core implementation baseline; in an installed pack, use `../../resources/examples/frontend/**` only as reusable frontend reference source for the target project. Archived inbox UI notes are provenance only, not operative guidance.

Then load focused downstream implementation skills only for the selected UI, API, realtime, and backing component scope.

## Use when

Use for tasks that mention or imply:
- command center, mission control, operations dashboard, or supervisor console
- goal launch, plan review, agent assignment, approval gates, or delegation workbench
- approval queue, exception queue, decision card, deviation review, or reviewer actions
- policy editor, governance center, simulation, replay, threshold tuning, or learning from feedback
- async digest, executive briefing, catch-up summary, or material-events feed
- audit trace, work trace, investigation timeline, evidence/provenance view, or outcome dashboard

## Surface selection

Choose only the surfaces justified by the requested workflow.

Before selecting any surface family, place it inside one or more functional/context-area agent workstreams:

- identify the owning functional agent responsible for the surface's user outcome;
- identify reusable functional agents that may render or link the same surface without owning its semantics;
- record workstream placement: default briefing/dashboard, timeline item, attention queue, embedded card, modal, side panel, drill-in, or direct deep link;
- define payload/query source expectations: read/evidence capabilities, view/query sources, redaction, selected `AuthContext`, and trace/correlation fields;
- list capability-backed actions and denial/result surfaces; frontend controls are exposure details only;
- link audit/work traces for payload access, agent work, decisions, approvals, denials, and side effects;
- treat routes and deep links only as implementation details that reopen a selected functional agent, workstream item, or structured surface.

### Goal-to-Execution Workbench

Use when humans convert intent into a durable goal, executable plan, agent assignments, tool/data permissions, and approval gates.

Must expose:
- owning functional agent such as Goal Planning, Operations, or a domain-specific context-area agent; reusable placements such as Governance/Policy, Audit/Trace, and Outcome Metrics where justified
- workstream placement as a goal-launch or plan-review surface in the owning agent's continuous workstream; routes/deep links only reopen that surface
- payload/query source expectations for objective, success criteria, plan phases, proposed agents, tool/data permission needs, risk/evidence, approval gates, policy versions, and trace/correlation ids
- capability-backed simulate/edit/approve/cancel controls with idempotency, approval/denial result surfaces, and audit/work-trace links
- policy version and activation audit event

Route to:
- plan lifecycle → `akka-workflows`
- planning/recommendation agent → `akka-agents`
- durable goals/plans/policies → entity skills
- launch/review APIs → `akka-http-endpoints` + `akka-http-endpoint-component-client`
- frontend implementation → `akka-web-ui-apps`, `akka-web-ui-forms-validation`, `akka-web-ui-state-rendering`, `akka-web-ui-accessibility-responsive`

### Command Center / Mission Control

Use when supervisors monitor active objectives, agent work, risks, exceptions, and material activity.

Must expose:
- owning functional agent such as Operations, Supervisor, or a domain-specific command context; reusable placements such as Audit/Trace, Governance/Policy, and Outcome Metrics where justified
- workstream placement as the default dashboard/attention surface for the owning agent; routes/deep links, drill-ins, and direct links are implementation details
- payload/query source expectations for objective or operational scope, progress, outcome, risk, policy/autonomy status, agent roster/activity, approval/exception queues, material events, and trace/correlation ids
- capability-backed supervision actions, queue actions, drill-down reads, refresh/reconnect behavior, and denial/result surfaces
- compressed routine activity with drill-down to audit facts and work-trace links

Route to:
- command-center read models → `akka-views`
- realtime streams → `akka-http-endpoint-sse` or `akka-http-endpoint-websocket` plus `akka-web-ui-realtime`
- activity/trace enrichment → `akka-consumers`
- frontend state/rendering → `akka-web-ui-state-rendering`

### Decision Card / Deviation Review

Use when a recommendation, exception, or policy deviation needs human judgment.

Must expose:
- owning functional agent responsible for the decision outcome, such as Approval Queue, Risk & Exceptions, Governance/Policy, or a domain-specific reviewer agent; reusable placements such as Audit/Trace and Outcome Metrics where justified
- workstream placement as an attention item, approval-needed card, exception card, or side-panel drill-in; routes/deep links only reopen that workstream item or surface
- payload/query source expectations for recommendation, evidence, confidence, risk, impact, alternatives, policy clause/version, affected goal/plan/entity, and trace/correlation ids
- capability-backed approve/reject/modify/defer/escalate/request-evidence controls with confirmation/approval, idempotency, denial/result surfaces, and audit/work-trace links
- learning options such as one-time exception, precedent, example, or policy proposal

Route to:
- decision modeling → `ai-first-saas-decision-cards`
- approval lifecycle → `akka-workflows` and `akka-workflow-pausing`
- decision queues → `akka-views`
- frontend actions/forms → `akka-web-ui-forms-validation`

### Policy / Governance / Learning Center

Use when users version, test, simulate, approve, or teach policy, prompts, thresholds, examples, or guardrails.

Must expose:
- owning functional agent such as Governance/Policy or Agent Admin; reusable placements such as Audit/Trace, User Admin, and affected domain agents where justified
- workstream placement as governance dashboard, version card, diff/review item, simulation result, or proposal surface; routes/deep links only reopen the selected policy/prompt/skill surface
- payload/query source expectations for versioned policy/prompt/skill documents, stable clause IDs, examples, ambiguity warnings, proposals, diffs, replay/simulation results, authorization basis, and trace/correlation ids
- capability-backed propose/test/simulate/approve/commit/discard controls with human authorization for high-impact changes, denial/result surfaces, and audit/work-trace links

Route to:
- policy governance → `ai-first-saas-policy-governance`
- versioned policy records → `akka-event-sourced-entities`
- simulation/replay orchestration → `akka-workflows` and `akka-timed-actions`
- governance views → `akka-views`

### Async Digest / Executive Briefing

Use when users return after time away and need compressed autonomous work status.

Must expose:
- owning functional agent such as Executive Briefing, Operations, or a domain-specific supervisor context; reusable placements such as Outcome Metrics, Audit/Trace, and relevant domain agents where justified
- workstream placement as a return-after-absence briefing, scheduled digest item, or pinned summary in the owning agent's workstream; routes/deep links only reopen digest sections or linked surfaces
- payload/query source expectations for time window, routine-activity compression count, material events, pending decisions ranked by stakes, prior decision outcomes, affected goals, and trace/correlation ids
- capability-backed acknowledge, open decision, request detail, subscribe/unsubscribe, or schedule controls with denial/result surfaces and audit/work-trace links
- links to traces, decision cards, and affected goals

Route to:
- digest inputs and outcome views → `akka-views`
- scheduled digest generation → `akka-timed-actions`
- summary/evaluation agents → `akka-agents`
- notifications/publication → `akka-consumers`

### Audit / Work Trace

Use when users investigate who/what/when/why/how-authorized for work, decisions, tools, data access, or outcomes.

Must expose:
- owning functional agent such as Audit/Trace, with reusable placements from every functional agent that needs investigation drill-down
- workstream placement as an investigation timeline, trace drill-in, decision evidence drawer, or audit detail surface; routes/deep links only reopen scoped trace surfaces
- payload/query source expectations for chronological agent steps, tool calls, data access, policy invocations, approvals, actions, rollback, outcomes, audit events, authorization basis, redaction profile, and trace/correlation ids
- capability-backed trace search, filter, export, evidence-open, and escalation controls with backend authorization, denial/result surfaces, and audit links
- stable links to goal, plan, decision, evidence, policy version, and audit events

Route to:
- trace modeling → `ai-first-saas-audit-trace`
- trace search/read models → `akka-views`
- trace APIs and streams → HTTP endpoint skills
- investigation UI → `akka-web-ui-apps`, `akka-web-ui-state-rendering`, `akka-web-ui-accessibility-responsive`

## UI design rules

- Select and describe surfaces through functional-agent workstream placement before frontend routes, pages, or components.
- Center structured surfaces on objectives, decisions, policies, traces, or outcomes; not only raw records.
- Make autonomy boundaries visible where actions occur.
- Separate automated work, human-needed work, exceptions, and FYI activity.
- Rank attention queues by stakes/risk/SLA, not only recency.
- Compress routine activity, but always preserve drill-down to audit facts.
- Do not make chat the primary control surface for consequential actions.
- Do not choose visual styling implicitly; for generated full-stack AI-first SaaS, the web UI is mandatory, so if no style guide is selected, use the existing UI style-selection guidance before implementation.
- Use visual craft only as a cosmetic layer that clarifies existing surfaces, states, authority, and attention hierarchy; it must not change functional agents, surface contracts, capability mappings, authorization, APIs, tests, or readiness semantics.
- Keep routes, pages, and deep links as browser realization details for reopening a functional agent, workstream item, or structured surface; never use them as the primary decomposition.

## Output expectations

Produce a compact UI-surface plan with:
- selected surfaces, owning functional agent, reusable functional agents, and primary human roles/temporal modes
- workstream placement for each surface, plus route/deep-link behavior only as implementation detail
- payload/query source expectations, backing durable objects, read models, redaction, and trace requirements
- capability-backed actions per surface, including idempotency, approval/denial/result surfaces, and audit links
- API/realtime needs tied to the surface contract rather than page navigation
- frontend state, form, loading/error/empty, accessibility, responsive, visual hierarchy, and reduced-motion requirements
- downstream Akka and web UI skills to load next
- unresolved questions only where authority, evidence, realtime, access, or style semantics would otherwise be guessed

## Review checklist

Before implementation, verify:
- each selected surface has an owning functional agent, reusable placement if any, and explicit workstream placement
- routes/deep links are implementation details for selected functional agents, workstream items, or structured surfaces
- UI guidance routes to `akka-web-ui-*` and HTTP endpoint skills instead of replacing them
- approval/decision controls have evidence, policy, risk, impact, capability, and audit backing
- every agent activity or material event can link to a trace or source artifact
- routine summaries are auditable
- tests can cover loading, empty, error, success, action, authorization, audit/trace, and realtime update states where in scope
