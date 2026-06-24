---
name: ai-first-saas-decision-cards
description: Design AI-first SaaS recommendation, approval, exception, and deviation review cards with evidence, risk, confidence, impact, alternatives, actions, and routing to Akka workflow, entity, view, agent, and UI skills.
---

# AI-First SaaS Decision Cards

Use this companion after `ai-first-saas` when humans or policies must review recommendations, approvals, exceptions, escalations, deviations, or high-impact agent actions.

This is a decision-surface and routing skill. It does not replace workflow, entity, agent, view, endpoint, or web UI implementation guidance.

## Lifecycle classification

- Phase role: Interview-phase decision-surface modeling with Build/compile handoff constraints for approval workflows, governed tools, surfaces, traces, and tests.
- Graph layer: recommendation/decision workers, execution harnesses, actor adapters, governed tools, capabilities, decision cards, approval state, policy/evidence links, and traces.
- Canonical chain: `worker → execution harness → actor adapter → governed tool → capability → Akka implementation`.

## Required reading

Read first:
- `../docs/intent-compiler.md`
- `../docs/current-intent-model.md`
- `../docs/intent-to-realization-flow.md`
- `../docs/app-development-lifecycle.md`
- `../docs/app-worker-tool-model.md`
- `../docs/app-description-component-graph.md`
- `../docs/app-description-to-code-compile-contract.md`
- `../docs/workforce-decomposition.md`
- `../docs/agent-workstream-application-architecture.md`
- `../docs/structured-surface-contracts.md`
- `../docs/capability-first-backend-architecture.md`
- `../docs/ai-first-saas-application-architecture.md`
- `../ai-first-saas/SKILL.md`

Then load focused downstream implementation skills only for the selected components.

## Use when

Use for tasks that mention or imply:
- approval requests, exception handling, escalations, or deviation review
- recommendations that require human or policy-authorized decisions
- evidence, provenance, confidence, risk, impact, alternatives, or rationale
- human override, counterproposal, rejection, deferral, or request-for-more-evidence
- separating agent recommendation from final decision authority

## Decision card contract

A useful decision card should expose:

```text
Decision subject:
Goal / plan / task link:
Recommended action:
Decision authority: human role | policy rule | bounded automation
Evidence considered:
Policy clauses / guardrails triggered:
Confidence:
Risk and impact:
Alternatives considered:
Known gaps / uncertainty:
Available actions: approve | reject | modify | defer | escalate | request evidence
Worker / execution harness / actor adapter for each action:
Governed tool id and capability id for each action:
Decision deadline or SLA:
Trace links:
Outcome follow-up:
```

## Modeling rules

- Keep `Recommendation` distinct from `Decision`.
- Store approval, rejection, override, escalation, and exception outcomes as durable facts when consequential.
- Capture the evidence and policy basis available at decision time.
- Include alternatives and uncertainty when the decision involves risk, ambiguity, or impact.
- Human corrections should be eligible to become feedback, precedent, policy proposal, or training/evaluation examples through governed flow.

## Akka substrate routing

- Consequential recommendation, decision, approval, override, and precedent history → `akka-event-sourced-entities`.
- Approval, exception, escalation, pause/resume, and deadline lifecycles → `akka-workflows`, `akka-workflow-pausing`, and `akka-workflow-testing`.
- Recommendation, explanation, risk summary, or alternative generation → `akka-agents` and `akka-agent-structured-responses`.
- Decision queues, reviewer worklists, risk-ranked cards, and status views → `akka-views` and `akka-view-query-patterns`.
- Browser decision-card APIs and actions → `akka-http-endpoints` and `akka-http-endpoint-component-client`.
- Interactive card UI, feedback states, realtime queue updates, accessibility, and responsive behavior → `akka-web-ui-apps` plus focused web UI skills.
- Decision deadlines, reminders, stale-card escalation, and digest inclusion → `akka-timed-actions`.
- Notifications and trace enrichment after decisions → `akka-consumers`.

## Workstream handoff requirements

For generated full-stack SaaS work, every decision-card output must hand off an implementation-ready workstream contract before component selection:
- owning or reusable functional-agent workstream, such as Approval Queue, Governance/Policy, User Admin, Audit/Trace, or a domain supervisor agent;
- responsible human reviewer/approver worker, recommending agent/system worker, and any workflow system worker;
- structured surface id/type for each recommendation, approval, exception, or deviation card;
- actor-adapter list mapped to governed-tool ids and capability ids/classes, including approve, reject, modify, defer, escalate, request evidence, and acknowledge outcome;
- `AuthContext`, tenant/customer scope, reviewer role/capability rules, approval authority, audit/work-trace fields, trace links, and denial behavior;
- downstream Akka, frontend, realtime, and test skills needed for workflow, view, endpoint, agent, and surface rendering implementation.

## Output expectations

Produce a compact decision-card design with:
- decision types and trigger conditions
- required evidence, policy citations, risk, confidence, impact, and alternatives
- allowed reviewer actions and their state effects
- approval/exception workflow states and deadlines
- durable decision, audit, feedback, and outcome records
- views and UI surfaces needed for reviewers and supervisors
- downstream Akka skills to load next
- unresolved questions only where authority, risk, evidence, or SLA semantics would otherwise be guessed

## Review checklist

Before implementation, verify:
- recommendations are not silently treated as final decisions
- every available action maps to durable state or workflow transition
- approval authority and escalation rules are explicit
- decision cards can explain why the recommendation was made
- tests can cover approve, reject, modify, escalate, timeout, and missing-evidence paths where in scope
