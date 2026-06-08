# Workstream: Agent Admin

## Purpose

Govern managed-agent definitions, prompts, skills, references, manifests, tool boundaries, model refs, seed imports, behavior proposals, activation, rollback, and runtime traces.

## Functional agent

Owns `agent-admin-agent` as its exactly-one user-facing functional-agent binding. Runtime instances are selected-context workstream logs, not page sessions.

## Capability binding

Primary capability: `../../capabilities/managed-agent-governance.md`.

## Attention model

Backend-owned attention includes draft behavior proposals, approval-required activations, failed seed/provider/model/tool-boundary checks, manifest drift, loader denials, and risky authority-expansion attempts. Counts feed the left rail and, where personal, My Account aggregation.

## Readiness posture

This node captures current intent only. Runtime readiness still requires local Akka/API/UI validation and model/provider fail-closed proof where applicable.
