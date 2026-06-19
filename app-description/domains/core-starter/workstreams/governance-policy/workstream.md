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
