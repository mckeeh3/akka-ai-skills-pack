# Audit/Trace Summary AutonomousAgent

## Purpose

Create the next AutonomousAgent worker vertical using the extracted worker runtime pattern: **Audit/Trace scheduled audit summary**.

This worker summarizes recent security, authorization, capability, agent-work, provider-readiness, attention, and event traces for authorized audit reviewers. It should support a bounded manual or scheduled trigger, produce typed audit summary findings, emit v3 workstream events, create attention for review-ready/blocked/failed states, and render Audit/Trace summary result surfaces.

## Source context

Builds on:

- `docs/autonomous-agent-worker-runtime-pattern.md`
- `specs/autonomous-agent-worker-pattern-extraction/`
- `specs/autonomous-agent-runtime-integration/`
- `specs/agent-admin-prompt-risk-autonomous-agent/`
- `specs/workstream-event-backbone-v3/`
- starter Audit/Trace, attention, event backbone, and AutonomousAgent implementation files

## Scope

- Define Audit/Trace Summary AutonomousAgent task/result contract.
- Implement real Akka `AutonomousAgent` runtime path where feasible.
- Support bounded manual trigger first; scheduled trigger may be added if small and honest.
- Govern task start/read/cancel/acknowledge or accept/reject capabilities.
- Collect scoped/redacted trace evidence for the selected AuthContext.
- Emit `workflow.audit_trace.*` and `worker.task.*` v3 events.
- Derive attention for blocked, failed, completed-review-required/summary-ready, acknowledged states.
- Render Audit/Trace summary result/progress surfaces.
- Preserve provider/model fail-closed behavior and no model-less normal success.

## Non-goals

- Do not build a general digest platform.
- Do not implement enterprise SIEM/export integrations.
- Do not bypass trace redaction, tenant/customer scope, AuthContext, or audit permissions.
- Do not auto-mutate audit records or policies from summary output.
- Do not count deterministic/model-less summaries as normal runtime success.

## Execution model

Execute one task per fresh harness context. Each task must update `pending-tasks.md`, run checks or record blockers, and make one focused commit.

## Sprint sequence

1. Contract and trigger design.
2. Backend AutonomousAgent runtime and evidence collection.
3. Events, attention, and Audit/Trace surfaces.
4. Validation in rendered scaffold.
5. Docs/handoff.
6. Verification.

## Done state

Complete when the starter/reference assets have:

- documented Audit/Trace Summary AutonomousAgent contract;
- governed task lifecycle capabilities;
- trace evidence collection with scoped redaction;
- real Akka `AutonomousAgent` integration or explicit SDK/runtime blocker;
- provider/model fail-closed behavior;
- v3 events and attention mappings;
- Audit/Trace summary result/progress surfaces;
- tests for auth, tenant isolation/redaction, idempotency, event/attention linkage, provider fail-closed, and no fake/model-less normal success;
- docs identifying this as the scheduled/summary worker pattern candidate without overclaiming a general digest platform.
