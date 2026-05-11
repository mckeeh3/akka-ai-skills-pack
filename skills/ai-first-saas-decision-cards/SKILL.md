---
name: ai-first-saas-decision-cards
description: Design AI-first SaaS recommendation, approval, exception, and deviation review cards with evidence, risk, confidence, impact, alternatives, actions, and routing to Akka workflow, entity, view, agent, and UI skills.
---

# AI-First SaaS Decision Cards

Use this companion after `ai-first-saas` when humans or policies must review recommendations, approvals, exceptions, escalations, deviations, or high-impact agent actions.

This is a decision-surface and routing skill. It does not replace workflow, entity, agent, view, endpoint, or web UI implementation guidance.

## Required reading

Read first:
- `../../docs/ai-first-saas-application-architecture.md`
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
