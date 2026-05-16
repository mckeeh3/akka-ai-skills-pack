# AI-First UI Surfaces

## Purpose

This file defines the seed app's AI-first browser surfaces independently from route mechanics. These surfaces are the UI contract for delegated work, retained human authority, evidence-backed decisions, policy governance, audit traces, and outcome loops.

Design reference:

- `../../../../../docs/web-ui-style-guide.md` canonical `atlas-ops-supervisory-console` theme

## Cross-surface rules

- Consequential agent work must be visible as durable goals, plans, decisions, approvals, traces, policy proposals, or outcomes.
- The AI command strip may summarize, explain, or draft, but it does not replace durable state.
- Every recommendation that requires human authority exposes evidence, risk, confidence, impact, policy trigger, and allowed actions.
- Every high-impact action provides a trace path before or after execution.
- UI status must distinguish autonomous progress, waiting for review, blocked by policy, escalated, completed, failed, stale, and trace unavailable.
- Backend authorization remains authoritative even when the UI presents an action.

## Goal-to-Execution Workbench

- primary screen: Goal Workbench
- route family: `/ui/goals/new`, `/ui/goals/:goalId`
- users: supervisor, tenant admin where permitted
- human authority shown:
  - objective owner
  - editable success criteria and constraints
  - approval gates before launch
  - tool/data permission summary
- agent/system activity shown:
  - draft plan generation state
  - selected agent/team assignment
  - proposed steps and expected side effects
- evidence/risk/policy shown:
  - constraints that limit execution
  - policy gates triggered by the proposed plan
  - simulation or review warnings where available
- primary actions:
  - create draft goal
  - request draft plan
  - approve launch
  - cancel or revise plan
- trace/outcome links:
  - goal trace after creation
  - plan version history
  - outcome links after completion
- realtime/stale behavior:
  - plan generation progress
  - goal status updates
  - stale indicator when progress stream reconnects

## Mission Control / Briefing

- primary screen: Briefing / Mission Control
- route family: `/ui/briefing`
- users: supervisor, reviewer, tenant admin, auditor read-only where permitted
- human authority shown:
  - pending approvals and exceptions
  - policy/trust control summary
  - next actions requiring review
- agent/system activity shown:
  - agent execution timeline
  - active goals and plan progress
  - upcoming autonomous actions
  - agent/team autonomy and trust metrics
- evidence/risk/policy shown:
  - risk badges and exception counts
  - policy-triggered items in attention queue
  - outcome deltas and blocked work
- primary actions:
  - review highest-priority item
  - ask AI command strip for summary or risk explanation
  - open goal, decision, policy, or trace detail
- trace/outcome links:
  - activity row detail links
  - needs-attention card reasoning/evidence links
  - outcome summary links
- realtime/stale behavior:
  - live activity stream
  - live queue counts
  - reconnecting/stale banner when streams are interrupted

## Decision Card / Deviation Review

- primary screens: Decision Queue, Decision Card Detail
- route family: `/ui/decisions`, `/ui/decisions/:decisionId`
- users: reviewer, supervisor, policy owner for policy-change decisions
- human authority shown:
  - allowed actions based on user permission and policy gate
  - authority boundary that requires review
  - reviewer identity after action
- agent/system activity shown:
  - originating agent or workflow
  - recommendation generation timestamp
  - linked goal/plan/task
- evidence/risk/policy shown:
  - evidence items
  - confidence score
  - risk score
  - impact estimate
  - policy triggers and clauses
  - alternatives considered
- primary actions:
  - approve
  - reject
  - request changes
  - counterpropose
  - defer
  - escalate
  - convert to policy proposal where permitted
- trace/outcome links:
  - decision trace
  - related work trace
  - outcome feedback after completion
- realtime/stale behavior:
  - queue updates when cards are added, changed, resolved, or superseded
  - conflict handling if another reviewer already acted

## Policy / Governance / Learning Center

- primary screen: Governance Center / Policies
- route family: `/ui/governance/policies`
- users: policy owner, tenant admin, auditor read-only where permitted
- human authority shown:
  - policy owner approval requirement
  - policy proposal state
  - activation and rollback controls
- agent/system activity shown:
  - agent-drafted policy proposals
  - simulation/replay progress
  - learned example suggestions
- evidence/risk/policy shown:
  - policy version and clause ids
  - diff between active and proposed policy
  - simulation/replay results
  - affected approval gates and authority boundaries
- primary actions:
  - review proposal
  - run simulation/replay
  - approve policy commit
  - reject proposal
  - rollback to prior version where permitted
- trace/outcome links:
  - policy invocation history
  - proposal audit trail
  - outcome metrics affected by the policy
- realtime/stale behavior:
  - simulation completion updates
  - proposal status changes

## Async Digest / Executive Briefing

- primary screen: Briefing / Mission Control digest regions
- route family: `/ui/briefing`
- users: supervisor, tenant admin, outcome owner, auditor read-only where permitted
- human authority shown:
  - pending decisions
  - escalations
  - items awaiting owner input
- agent/system activity shown:
  - compressed routine activity
  - material events since last review
  - upcoming autonomous actions
- evidence/risk/policy shown:
  - only material risks, exceptions, and policy-bound items appear prominently
  - routine autonomous work remains inspectable but subordinate
- primary actions:
  - review material event
  - open decision queue
  - open trace
- trace/outcome links:
  - digest item trace links
  - outcome delta links
- realtime/stale behavior:
  - unread/material event count changes
  - digest generated timestamp

## Audit / Work Trace

- primary screen: Audit Trace Explorer
- route family: `/ui/audit/traces`
- users: auditor, tenant admin, supervisor for scoped traces
- human authority shown:
  - actor, approver, authorization basis, policy version, permission source
- agent/system activity shown:
  - chronological work trace
  - tool invocations
  - data access events
  - policy invocations
  - workflow steps
- evidence/risk/policy shown:
  - linked evidence items
  - decision and policy trace references
  - safe diagnostic context
- primary actions:
  - search traces
  - inspect trace detail
  - export permitted trace subset where allowed
- trace/outcome links:
  - every trace row links to related goal, decision, policy, tool, or outcome where available
- realtime/stale behavior:
  - trace search is request/response by default
  - live trace tail may be added later only if scoped and authorized
