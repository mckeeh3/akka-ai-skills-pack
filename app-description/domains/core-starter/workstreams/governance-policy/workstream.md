# Workstream: Governance/Policy

## Purpose

Manage policy proposals, simulations, decisions, activation, rollback, approval gates, thresholds, behavior-change governance, and outcome notes.

## Functional agent

Owns `governance-policy-agent` as its exactly-one user-facing functional-agent binding. Runtime instances are selected-context workstream logs, not page sessions.

## Capability binding

Primary capability: `../../capabilities/governance-policy-lifecycle.md`.

## Attention model

Backend-owned attention includes stable categories `governance_policy.proposal.review_needed`, `governance_policy.simulation.ready`, `governance_policy.approval.required`, `governance_policy.threshold.high_risk`, `governance_policy.authority_change.high_risk`, `governance_policy.rollback.available`, `governance_policy.outcome.follow_up`, `governance_policy.impact_analysis.ready_for_review`, and `governance_policy.impact_analysis.blocked`. Producers are policy proposal lifecycle, simulation/impact-analysis tasks, approval-gate evaluation, activation/rollback flows, outcome-note follow-up scheduling, and provider/model/tool readiness checks. Each attention item has a backend idempotency key formed from selected scope, policy/proposal/task id, version, category, and lifecycle status; severity is backend-authored (`info`, `needs_review`, `approval_required`, `blocked`, `risk`) and terminal/resolved source states clear or downgrade the item. Counts feed the left rail and, where the signed-in human is a reviewer/approver/owner or assigned follow-up actor, My Account aggregation without exposing hidden policy clauses, raw model outputs, provider data, cross-scope facts, or hidden counts.

## Readiness posture

This node captures current intent only. Runtime readiness still requires local Akka/API/UI validation and model/provider fail-closed proof where applicable.


## Confirmed human chat tool-plan exposure

This workstream exposes a bounded `human_chat_tool_plan` adapter for execution-oriented chat prompts after deterministic no-mutation surface routing declines the prompt. The first-pass runtime path is implemented through backend-owned plan proposal, exact snapshot confirmation, catalog validation, dispatcher reauthorization, idempotency, and trace surfaces. It allows `governance-policy-agent` to propose a plan for the representative prompt **draft a policy proposal to require approval before redacted exports**, but it never permits prompt-only mutation, hidden target enumeration, or AI-autonomous authority.

Execution is allowed only when all of the following hold: the proposal was created with `noMutation=true`; the human explicitly confirms the exact plan snapshot; the backend reauthorizes the selected `AuthContext`, actor, capability, tool boundary, lifecycle state, approval policy, tenant/customer ownership, and idempotency on every step; and each step executes through its declared governed surface/action path as a separate transaction boundary.

Representative catalog binding: actions `action-governance-policy-draft-proposal`; governed tool ids `governance.policy.propose`; capabilities `governance.policy.propose`; input contract `schema.governance-policy.proposal.draft.v1` with title, rationale, browser-safe proposed change summary, affected capabilities, and idempotency key; expected result surfaces `surface-governance-policy-proposal`. The allowed effect is to create or return an inert draft policy proposal only; it cannot approve, activate, roll back, weaken security, expand authority, or count advisory analysis as approval.
