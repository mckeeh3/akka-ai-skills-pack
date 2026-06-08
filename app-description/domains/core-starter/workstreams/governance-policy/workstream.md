# Workstream: Governance/Policy

## Purpose

Manage policy proposals, simulations, decisions, activation, rollback, approval gates, thresholds, behavior-change governance, and outcome notes.

## Functional agent

Owns `governance-policy-agent` as its exactly-one user-facing functional-agent binding. Runtime instances are selected-context workstream logs, not page sessions.

## Capability binding

Primary capability: `../../capabilities/governance-policy-lifecycle.md`.

## Attention model

Backend-owned attention includes pending policy proposals, simulation results, approval gates, high-risk threshold or authority changes, rollback candidates, and outcome follow-ups. Counts feed the left rail and, where personal, My Account aggregation.

## Readiness posture

This node captures current intent only. Runtime readiness still requires local Akka/API/UI validation and model/provider fail-closed proof where applicable.
