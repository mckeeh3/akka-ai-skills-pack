---
name: ai-first-saas-ui-surfaces
description: Design AI-first SaaS supervision, decision, governance, digest, audit, goal-to-execution, and collection-object progression UI surfaces, then route implementation to Akka web UI, HTTP endpoint, view, workflow, agent, trace, and outcome skills.
---

# AI-First SaaS UI Surfaces

Use this companion after `ai-first-saas` when a product or feature needs browser UI for delegation, supervision, decisions, governance, digests, audit, or outcome review.

This is a UI-surface and routing skill. It does not replace `akka-web-ui-*`, HTTP endpoint, View, Workflow, Agent, or security implementation skills.

## Required reading

Read first:
- `../docs/intent-compiler.md`
- `../docs/current-intent-model.md`
- `../docs/intent-to-realization-flow.md`
- `../docs/ai-first-saas-application-architecture.md`
- `../ai-first-saas/SKILL.md`

Use canonical doctrine plus this skill for surface selection. Also use `../docs/agent-workstream-application-architecture.md`, `../docs/structured-surface-contracts.md`, and `../docs/full-core-foundation-readiness.md` when deciding where a surface belongs in the agent workstream model. `../docs/structured-surface-contracts.md` is the canonical source for the mandatory collection-object surface progression. Use the runnable SaaS Foundation App repository root as the canonical runnable implementation reference and root `frontend/**` as the reusable frontend reference source. Pack examples are available under `.agents/skills/examples/**` after install.

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

For any durable collection of domain things such as users, customers, orders, policies, agents, invitations, or governed documents, start with the canonical collection-object surface progression from `../docs/structured-surface-contracts.md` unless the app-description explicitly records a safer override. Use domain-semantic names, not generic CRUD names. The default progression is: list/search for discovery and selection → lifecycle-aware show/inspection for one object → separate single-purpose create, edit, destructive lifecycle confirmation, or domain-specific task surfaces.

Before selecting any surface family, place it inside one or more functional/context-area agent workstreams:

- identify the owning functional agent responsible for the surface's user outcome;
- identify the human, agent, or system worker that needs, produces, or acts through the surface;
- identify reusable functional agents that may render or link the same surface without owning its semantics;
- record workstream placement: default briefing/dashboard, timeline item, attention queue, embedded card, modal, side panel, drill-in, or direct deep link;
- define payload/query source expectations: read/evidence capabilities, view/query sources, redaction, selected `AuthContext`, and user-visible versus drilldown/admin/support/auditor/internal metadata boundaries for trace/correlation fields;
- list capability-backed actions and denial/result surfaces; for collection-object surfaces, list row/card selection, create, edit, destructive lifecycle, and lifecycle-specific task edges as delegated surface requests or task surfaces; for dashboard/command-center/attention surfaces, list clickable and keyboard-operable work-object interactions for cards, rows, counters, badges, chart segments, task/progress panels, shortcuts, icons, and buttons; frontend controls are exposure details only;
- link audit/work traces for payload access, worker handoffs, agent work, decisions, approvals, denials, and side effects;
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
- capability-backed supervision actions, queue actions, drill-down reads, refresh/reconnect behavior, and denial/result surfaces; dashboard objects representing attention or next work open the detail/decision/progress/evidence/result surface directly and append request/result surfaces rather than remaining inert visuals
- compressed routine activity with drill-down to role-gated audit facts and work-trace links; the default command-center view must summarize what matters in business terms, not list raw internal agent/tool events

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
- payload/query source expectations for recommendation, evidence, confidence, risk, impact, alternatives, policy clause/version, affected goal/plan/entity, and trace/correlation ids, with default-visible evidence/risk summaries separated from privileged policy/audit diagnostics
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
- payload/query source expectations for chronological agent steps, tool calls, data access, policy invocations, approvals, actions, rollback, outcomes, audit events, authorization basis, redaction profile, and trace/correlation ids; split the UX into user-readable investigation summaries and role-gated raw audit/support detail
- capability-backed trace search, filter, export, evidence-open, and escalation controls with backend authorization, denial/result surfaces, and audit links
- stable links to goal, plan, decision, evidence, policy version, and audit events

Route to:
- trace modeling → `ai-first-saas-audit-trace`
- trace search/read models → `akka-views`
- trace APIs and streams → HTTP endpoint skills
- investigation UI → `akka-web-ui-apps`, `akka-web-ui-state-rendering`, `akka-web-ui-accessibility-responsive`

## UI design rules

- Select and describe surfaces through functional-agent workstream placement before frontend routes, pages, or components.
- Use the canonical collection-object progression for durable object collections: domain list/search surfaces always allow selecting a listed object; selected objects open lifecycle-appropriate show/inspection surfaces; show/inspection surfaces delegate consequential changes to separate edit, destructive lifecycle confirmation, and domain-specific single-action surfaces.
- Keep each surface single-purpose. Do not design one broad CRUD page that lists, shows, creates, edits, and deletes objects.
- Center structured surfaces on objectives, decisions, policies, traces, or outcomes; not only raw records.
- Design default surface views for the target SaaS user, not for the implementation team. Avoid exposing internal policy ids, capability ids, governed-tool ids, backend component names, provider/model details, prompt internals, raw event ids, and correlation/idempotency mechanics unless the selected surface is explicitly an admin/support/auditor/developer diagnostic view.
- Use progressive disclosure: ordinary users get concise business summaries and next actions; managers/admins get operational controls; auditors/compliance reviewers get scoped evidence and policy detail; support/developer views may expose diagnostic ids when authorized and visually subordinate.
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
- selected surfaces, owning functional agent, reusable functional agents, responsible human/agent/system workers, and primary human roles/temporal modes
- for each durable collection object in scope, the domain-semantic list/show/create/edit/destructive-lifecycle progression, including lifecycle-state-specific show/task routing
- workstream placement for each surface, plus route/deep-link behavior only as implementation detail
- payload/query source expectations, backing durable objects, read models, redaction, trace requirements, and visibility split for default user content, drilldowns, role-gated diagnostics, and internal-only metadata
- capability-backed actions per surface, including idempotency, approval/denial/result surfaces, and audit links
- API/realtime needs tied to the surface contract rather than page navigation
- frontend state, form, loading/error/empty, accessibility, responsive, visual hierarchy, and reduced-motion requirements
- downstream Akka and web UI skills to load next
- unresolved questions only where authority, evidence, realtime, access, or style semantics would otherwise be guessed

## Review checklist

Before implementation, verify:
- each selected surface has an owning functional agent, reusable placement if any, and explicit workstream placement
- every durable collection object uses the canonical progression or records an explicit justified override; list row/card selection opens a show/inspection surface; create, edit, and destructive lifecycle actions are separate surfaces
- routes/deep links are implementation details for selected functional agents, workstream items, or structured surfaces
- UI guidance routes to `akka-web-ui-*` and HTTP endpoint skills instead of replacing them
- approval/decision controls have evidence, policy, risk, impact, capability, and audit backing, while default copy translates internal backing into user-understandable language
- every agent activity or material event can link to a trace or source artifact
- routine summaries are auditable
- tests can cover loading, empty, error, success, action, authorization, audit/trace, and realtime update states where in scope
