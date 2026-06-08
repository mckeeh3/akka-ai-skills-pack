---
name: ai-first-saas-outcomes-metrics
description: Design AI-first SaaS outcome loops, metrics, decision/outcome links, feedback, replay, and validation surfaces, then route implementation to Akka entities, views, consumers, timers, agents, endpoints, and web UI skills.
---

# AI-First SaaS Outcomes and Metrics

Use this companion after `ai-first-saas` when delegated work, agent decisions, policies, approvals, or automation must be validated against business outcomes, safety, quality, timeliness, cost, or learning goals.

This is an outcome-loop and routing skill. It does not replace entity, workflow, view, consumer, timed action, agent, endpoint, or web UI implementation guidance.

## Required reading

Read first:
- `../docs/intent-compiler.md`
- `../docs/current-intent-model.md`
- `../docs/intent-to-realization-flow.md`
- `../docs/ai-first-saas-application-architecture.md`
- `../ai-first-saas/SKILL.md`

Then load focused downstream implementation skills only for the selected outcome records, projections, schedules, agents, APIs, and UI surfaces.

## Use when

Use for tasks that mention or imply:
- outcome metrics, success criteria, KPIs, quality, safety, value, ROI, SLA, or effectiveness
- connecting decisions, approvals, overrides, agent actions, or policy changes to later results
- learning loops, feedback, precedents, evaluations, replay, simulation, or policy improvement
- measuring automation usefulness, risk, correctness, timeliness, cost, or human workload
- outcome dashboards, executive briefings, post-decision reviews, or audit-ready impact reports

## Outcome model contract

Define outcomes as durable links between intent, execution, decisions, and later results:

```text
Goal / objective:
Success criteria:
Baseline or expected result:
Decision / action / policy version link:
Outcome metric name and type:
Measurement source:
Measurement window:
Observed value:
Confidence / data quality:
Owner and review cadence:
Trace / audit links:
Feedback or policy-learning implication:
```

## Metric categories

Choose only metrics that support product accountability.

### Business value metrics

Examples:
- revenue protected or created
- cases resolved, devices recovered, tickets closed, customer risk reduced
- cycle time, throughput, backlog reduction, service quality

Use when outcome owners need to know whether agent labor creates business value.

### Safety and risk metrics

Examples:
- policy violations, near misses, escalations, overrides
- high-risk autonomous actions, failed approvals, rollback rates
- data-access violations, unauthorized action attempts

Use when delegated work has material authority, privacy, compliance, or operational risk.

### Quality and correctness metrics

Examples:
- recommendation acceptance rate, human correction rate, false positive/negative rate
- evidence completeness, rationale quality, decision consistency
- evaluator scores and regression checks

Use when recommendations, classifications, summaries, or plans need validation.

### Timeliness and workload metrics

Examples:
- approval SLA, exception aging, time-to-resolution
- supervisor interruption rate, digest catch-up time, manual work avoided
- agent retry/failure rate and stuck-plan count

Use when the product promises operational leverage or faster resolution.

### Learning and governance metrics

Examples:
- feedback converted to examples, precedents, proposals, or policy commits
- replay/simulation impact, policy ambiguity warnings, evaluation drift
- policy version adoption and rollback outcomes

Use when human teaching should improve future behavior through governed change.

## Design rules

- Link outcomes to goals, plans, decisions, approvals, policy versions, agents, and traces where causally relevant.
- Avoid vanity dashboards that cannot drive supervision, learning, governance, or accountability.
- Record baseline, expected result, measurement window, and data source before claiming impact.
- Keep evaluator scores separate from real-world business outcomes, but allow both to inform learning.
- Track negative outcomes and near misses, not only successes.
- Make feedback actionable: one-time correction, precedent, reference example, policy proposal, threshold change, or regression test.
- Preserve tenant/privacy boundaries and redaction rules for outcome analytics.

## Akka substrate routing

- Durable `OutcomeMetric`, `OutcomeLink`, `DecisionOutcome`, feedback, precedent, and policy-impact facts → `akka-event-sourced-entities` when audit-grade history is needed.
- Current metric definitions, preferences, dashboard filters, or low-risk summary state → `akka-key-value-entities`.
- Long-running measurement, post-decision review, rollback follow-up, replay, or simulation flows → `akka-workflows`.
- Periodic measurement, digest generation, SLA checks, drift checks, and scheduled re-evaluation → `akka-timed-actions`.
- Outcome aggregation, dashboards, decision impact reports, and executive briefing inputs → `akka-views` and `akka-view-query-patterns`.
- Event-driven outcome enrichment from decisions, traces, integrations, and external results → `akka-consumers`.
- Quality evaluators, summary agents, anomaly explanation, and policy proposal drafting → `akka-agents`, `akka-agent-evaluation`, and `akka-agent-structured-responses`.
- Outcome APIs, exports, streams, and review actions → HTTP/gRPC endpoint skills as appropriate.
- Outcome dashboards, digest, policy impact, and post-decision review UI → `ai-first-saas-ui-surfaces` plus `akka-web-ui-apps` and focused web UI skills.

## Workstream handoff requirements

For generated full-stack SaaS work, every outcomes/metrics output must hand off an implementation-ready workstream contract before component selection:
- owning or reusable functional agent, such as Outcome Metrics, Governance/Policy, Audit/Trace, Executive Briefing, or a domain performance agent;
- structured surface id/type where user-facing, such as outcome dashboard, metric panel, post-decision review, policy-impact report, replay result, or executive digest;
- surface action list mapped to capability ids/classes, including review outcome, link decision, record feedback, request replay, create policy proposal, acknowledge negative outcome, or export report;
- `AuthContext`, tenant/customer scope, reviewer role/capability rules, attribution confidence, privacy/redaction, approval gates, audit/work-trace fields, and denial behavior;
- downstream Akka, frontend, scheduled/realtime, agent, and test skills needed for measurement, aggregation, review, feedback, and rendering.

## Output expectations

Produce a compact outcome-loop design with:
- selected success criteria and metric categories
- outcome records, metric definitions, measurement windows, and data sources
- causal links to goals, plans, decisions, approvals, policy versions, agents, traces, and business records
- feedback-to-learning paths and governance controls
- read models, schedules, integrations, and UI/API surfaces
- test strategy for metric calculation, linkage, privacy, missing data, and regression/evaluation paths
- downstream Akka skills to load next
- unresolved questions only where success criteria, attribution, measurement source, privacy, or review cadence would otherwise be guessed

## Review checklist

Before implementation, verify:
- metrics measure outcomes and safety, not only activity volume
- each consequential decision/action can be linked to later results when required
- measurement windows and sources are explicit
- human corrections can become governed learning artifacts
- outcome projections are derived from durable facts or authoritative integrations
- tests can cover success, negative outcome, missing data, delayed measurement, and privacy-sensitive reporting where in scope
