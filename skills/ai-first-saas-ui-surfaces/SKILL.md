---
name: ai-first-saas-ui-surfaces
description: Design AI-first SaaS supervision, decision, governance, digest, audit, and goal-to-execution UI surfaces, then route implementation to Akka web UI, HTTP endpoint, view, workflow, agent, trace, and outcome skills.
---

# AI-First SaaS UI Surfaces

Use this companion after `ai-first-saas` when a product or feature needs browser UI for delegation, supervision, decisions, governance, digests, audit, or outcome review.

This is a UI-surface and routing skill. It does not replace `akka-web-ui-*`, HTTP endpoint, View, Workflow, Agent, or security implementation skills.

## Required reading

Read first:
- `../../../docs/ai-first-saas-application-architecture.md`
- `../ai-first-saas/SKILL.md`

Use canonical doctrine plus this skill for surface selection. Use `../../../docs/examples/agent-first-dca-app-description/` as the worked AI-first UI reference when an example is useful. Archived inbox UI notes are provenance only, not operative guidance.

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

### Goal-to-Execution Workbench

Use when humans convert intent into a durable goal, executable plan, agent assignments, tool/data permissions, and approval gates.

Must expose:
- goal/objective and success criteria
- plan phases/tasks and proposed agents
- required tools, data, permissions, risks, and approval gates
- simulate/edit/approve/cancel controls
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
- objective or operational scope header
- progress, outcome, risk, policy/autonomy status
- agent roster/activity, approval/exception queues, and trace links
- compressed routine activity with drill-down to audit facts

Route to:
- command-center read models → `akka-views`
- realtime streams → `akka-http-endpoint-sse` or `akka-http-endpoint-websocket` plus `akka-web-ui-realtime`
- activity/trace enrichment → `akka-consumers`
- frontend state/rendering → `akka-web-ui-state-rendering`

### Decision Card / Deviation Review

Use when a recommendation, exception, or policy deviation needs human judgment.

Must expose:
- recommendation, evidence, confidence, risk, impact, and alternatives
- policy clause/version that triggered review
- approve/reject/modify/defer/escalate/request-evidence controls
- learning options such as one-time exception, precedent, example, or policy proposal

Route to:
- decision modeling → `ai-first-saas-decision-cards`
- approval lifecycle → `akka-workflows` and `akka-workflow-pausing`
- decision queues → `akka-views`
- frontend actions/forms → `akka-web-ui-forms-validation`

### Policy / Governance / Learning Center

Use when users version, test, simulate, approve, or teach policy, prompts, thresholds, examples, or guardrails.

Must expose:
- versioned policy documents and stable clause IDs
- examples, ambiguity warnings, proposals, diffs, replay/simulation results
- commit/discard controls with human authorization for high-impact changes

Route to:
- policy governance → `ai-first-saas-policy-governance`
- versioned policy records → `akka-event-sourced-entities`
- simulation/replay orchestration → `akka-workflows` and `akka-timed-actions`
- governance views → `akka-views`

### Async Digest / Executive Briefing

Use when users return after time away and need compressed autonomous work status.

Must expose:
- time window and routine-activity compression count
- material events, pending decisions ranked by stakes, prior decision outcomes
- links to traces, decision cards, and affected goals

Route to:
- digest inputs and outcome views → `akka-views`
- scheduled digest generation → `akka-timed-actions`
- summary/evaluation agents → `akka-agents`
- notifications/publication → `akka-consumers`

### Audit / Work Trace

Use when users investigate who/what/when/why/how-authorized for work, decisions, tools, data access, or outcomes.

Must expose:
- chronological trace of agent steps, tool calls, data access, policy invocations, approvals, actions, rollback, and outcomes
- stable links to goal, plan, decision, evidence, policy version, and audit events

Route to:
- trace modeling → `ai-first-saas-audit-trace`
- trace search/read models → `akka-views`
- trace APIs and streams → HTTP endpoint skills
- investigation UI → `akka-web-ui-apps`, `akka-web-ui-state-rendering`, `akka-web-ui-accessibility-responsive`

## UI design rules

- Center screens on objectives, decisions, policies, traces, or outcomes; not only raw records.
- Make autonomy boundaries visible where actions occur.
- Separate automated work, human-needed work, exceptions, and FYI activity.
- Rank attention queues by stakes/risk/SLA, not only recency.
- Compress routine activity, but always preserve drill-down to audit facts.
- Do not make chat the primary control surface for consequential actions.
- Do not choose a visual theme implicitly; if a web UI is in scope and no style guide is selected, use the existing UI style-selection guidance before implementation.

## Output expectations

Produce a compact UI-surface plan with:
- selected surfaces and primary human roles/temporal modes
- backing durable objects, read models, and trace requirements
- API/realtime needs and required actions per surface
- frontend state, form, loading/error/empty, accessibility, and responsive requirements
- downstream Akka and web UI skills to load next
- unresolved questions only where authority, evidence, realtime, access, or style semantics would otherwise be guessed

## Review checklist

Before implementation, verify:
- UI guidance routes to `akka-web-ui-*` and HTTP endpoint skills instead of replacing them
- approval/decision controls have evidence, policy, risk, impact, and audit backing
- every agent activity or material event can link to a trace or source artifact
- routine summaries are auditable
- tests can cover loading, empty, error, success, action, authorization, and realtime update states where in scope
