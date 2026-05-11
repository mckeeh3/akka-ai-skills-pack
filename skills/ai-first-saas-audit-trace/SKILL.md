---
name: ai-first-saas-audit-trace
description: Design AI-first SaaS audit and work traces for goals, plans, agents, policies, tools, data access, approvals, decisions, outcomes, and routing to Akka entities, consumers, views, endpoints, and UI skills.
---

# AI-First SaaS Audit Trace

Use this companion after `ai-first-saas` when delegated work, agent actions, decisions, approvals, policy use, tool calls, data access, or outcomes must be explainable and auditable.

This is a trace modeling and routing skill. It does not replace observability, logging, security, entity, consumer, view, or endpoint implementation guidance.

## Required reading

Read first:
- `../../../docs/ai-first-saas-application-architecture.md`
- `../ai-first-saas/SKILL.md`

Then load focused downstream skills only for selected trace producers, stores, projections, and surfaces.

## Use when

Use for tasks that mention or imply:
- audit trail, work trace, decision trace, provenance, explainability, or investigation
- tool invocation, data access, policy invocation, prompt/skill version, or agent activity history
- who/what/when/why/how-authorized questions
- approval, override, escalation, exception, rollback, or outcome linkage
- supervisor command centers, audit search, executive digests, or compliance review

## Trace event contract

For each consequential trace event, capture the fields needed to answer:

```text
Event id and time:
Tenant / actor / agent / component:
Goal / plan / task / workflow link:
Action or observation:
Authorization basis:
Policy or guardrail references:
Prompt / skill / model / agent version when relevant:
Tool or data resource used:
Input/output summary and redaction status:
Evidence and rationale links:
Decision / approval / exception link:
Outcome or rollback link:
Correlation / causation ids:
Retention and access classification:
```

## Trace design rules

- Create audit and trace records during execution; do not rely on reconstructing them from logs later.
- Capture enough context to explain authorization, evidence, policy use, and outcome without storing unnecessary sensitive payloads.
- Separate operational observability from business audit facts; both may be needed.
- Redact, classify, retain, and authorize trace access deliberately.
- Link traces across goal → plan → task → agent/tool/data/policy → decision/approval → action → outcome.
- Prefer append-only facts or event-sourced history for audit-grade traces.

## Akka substrate routing

- Audit-grade `WorkTrace`, `DecisionTrace`, `AuditEvent`, `ToolInvocation`, `DataAccessEvent`, and `PolicyInvocation` facts → `akka-event-sourced-entities` or append-only topic/consumer flows.
- Trace enrichment, notification, publication, and integration → `akka-consumers`.
- Trace search, command-center activity feeds, audit views, digest inputs, and outcome dashboards → `akka-views`.
- Trace-producing long-running plans and approval flows → `akka-workflows`.
- Agent prompt/version/tool/data traces → `akka-agents`, `akka-agent-tools`, `akka-agent-component-tools`, and `akka-agent-orchestration`.
- Periodic audit checks, digest generation, retention checks, or replay/simulation schedules → `akka-timed-actions`.
- Audit APIs, streaming activity feeds, and investigation tools → HTTP, SSE, gRPC, or MCP endpoint skills as appropriate.
- Trace and investigation UI → `akka-web-ui-apps` plus focused web UI skills.

## Output expectations

Produce a compact trace design with:
- trace event types and producer components
- required fields, redaction, retention, and access rules
- correlation model linking goals, plans, agents, tools, policies, decisions, and outcomes
- storage choice: event-sourced, topic/consumer append, derived view, or observability-only
- audit search, digest, and investigation surfaces
- tests needed for trace emission and authorization-sensitive access
- downstream Akka skills to load next
- unresolved questions only where retention, privacy, authority, or compliance semantics would otherwise be guessed

## Review checklist

Before implementation, verify:
- consequential actions emit durable trace facts
- tool, data, policy, approval, and decision links are represented
- sensitive content is redacted or access-controlled
- derived views do not become the only source of audit truth
- tests can verify trace emission for success, denial, approval, exception, and failure paths where in scope
