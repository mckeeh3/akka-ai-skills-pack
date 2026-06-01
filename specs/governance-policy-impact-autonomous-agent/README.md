# Governance/Policy Impact AutonomousAgent

## Purpose

Create the next AutonomousAgent worker vertical using the extracted worker runtime pattern: **Governance/Policy Policy-Change Impact Analysis**.

This worker reviews proposed policy, threshold, approval-rule, permission/capability-boundary, or governance-setting changes before human activation. It should collect scoped evidence, produce typed impact/risk findings, emit v3 workstream events, create attention for review-ready/blocked/failed states, and render Governance/Policy impact decision surfaces without committing policy changes directly.

## Source context

Builds on:

- `docs/autonomous-agent-worker-runtime-pattern.md`
- `specs/autonomous-agent-worker-pattern-extraction/`
- `specs/autonomous-agent-runtime-integration/`
- `specs/agent-admin-prompt-risk-autonomous-agent/`
- `specs/audit-trace-summary-autonomous-agent/`
- `specs/workstream-event-backbone-v3/`
- starter Governance/Policy, attention, event backbone, and AutonomousAgent implementation files

## Scope

- Define Governance/Policy impact task/result contract.
- Implement real Akka `AutonomousAgent` runtime path where feasible.
- Govern task start/read/cancel/accept/reject or request-changes capabilities.
- Collect scoped evidence for proposed policy changes without leaking tenant/customer data.
- Emit `workflow.governance_policy.*` and `worker.task.*` v3 events.
- Derive attention for blocked, failed, completed-review-required/impact-ready, accepted, rejected, and request-changes states.
- Render Governance/Policy impact result/decision surfaces.
- Preserve provider/model fail-closed behavior and no model-less normal success.

## Non-goals

- Do not auto-activate policy changes from worker output.
- Do not build a full policy simulation platform in this first slice.
- Do not bypass governance approval, AuthContext, capability checks, audit, or tool boundaries.
- Do not count deterministic/model-less impact summaries as normal runtime success.

## Execution model

Execute one task per fresh harness context. Each task must update `pending-tasks.md`, run checks or record blockers, and make one focused commit.

## Sprint sequence

1. Contract and evidence design.
2. Backend AutonomousAgent runtime.
3. Events, attention, and Governance/Policy surfaces.
4. Validation in rendered scaffold.
5. Docs/handoff.
6. Verification.

## Done state

Complete when the starter/reference assets have:

- documented Governance/Policy Impact AutonomousAgent contract;
- governed task lifecycle capabilities;
- scoped evidence collection for proposed governance/policy changes;
- real Akka `AutonomousAgent` integration or explicit SDK/runtime blocker;
- provider/model fail-closed behavior;
- v3 events and attention mappings;
- Governance/Policy impact result/decision surfaces;
- tests for auth, tenant isolation/redaction, idempotency, event/attention linkage, provider fail-closed, and no fake/model-less normal success;
- docs identifying this as a policy-impact worker pattern without overclaiming full policy simulation.
