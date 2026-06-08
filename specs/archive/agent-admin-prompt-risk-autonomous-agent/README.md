# Agent Admin Prompt-Risk AutonomousAgent

## Purpose

Create the next AutonomousAgent vertical after the completed User Admin Access Review runtime integration.

This mini-project adds an **Agent Admin Prompt-Risk Review AutonomousAgent** for managed-agent behavior changes: prompt, skill manifest, reference manifest, model policy, and tool-boundary proposals. The worker should review proposed changes, produce structured risk findings, emit v3 workstream events, create attention where human review is needed, and render Agent Admin decision/review surfaces without ever committing behavior changes directly.

## Source context

Builds on:

- `specs/autonomous-agent-runtime-integration/`
- `specs/workstream-event-backbone-v3/`
- `specs/workstream-attention-backbone-v1/`
- `specs/workstream-attention-event-producers-v2/`
- starter Agent Admin/governance runtime files under `templates/ai-first-saas-starter/`

## Scope

- Define Agent Admin prompt-risk task/result contract.
- Implement real Akka `AutonomousAgent` lifecycle path where feasible.
- Govern task start/query/result review with Agent Admin capabilities.
- Emit `workflow.agent_admin.*` and `worker.task.*` v3 events.
- Derive attention for blocked, failed, completed-review-required, accepted, and rejected states.
- Render Agent Admin structured risk review/decision surfaces.
- Preserve provider/model fail-closed behavior and no model-less normal success.
- Add scaffolded backend/frontend validation.

## Non-goals

- Do not auto-activate prompts, skills, references, models, or tool boundaries.
- Do not implement every Agent Admin worker.
- Do not build multi-agent teams/delegation.
- Do not bypass managed-agent governance, approval, tool-boundary, AuthContext, audit, or v3 event semantics.

## Execution model

Execute one task per fresh harness context. Each task must update `pending-tasks.md`, run checks or record blockers, and make one focused commit.

## Sprint sequence

1. **Contract and SDK/pattern fit** — define prompt-risk task/result/capability/surface contract using the User Admin AutonomousAgent pattern.
2. **Runtime implementation** — implement Agent Admin prompt-risk AutonomousAgent lifecycle and fail-closed adapter.
3. **Events, attention, and surfaces** — wire v3 events, attention, Agent Admin review surfaces, and human accept/reject/request-changes actions.
4. **Validation** — run rendered scaffold backend/frontend checks and record evidence.
5. **Docs/handoff** — document the second AutonomousAgent vertical and reusable pattern.
6. **Verification** — confirm done state or append bounded follow-ups.

## Done state

Complete when the starter/reference assets have:

- documented Agent Admin Prompt-Risk AutonomousAgent contract;
- governed task lifecycle capabilities for start/query/result review;
- real Akka `AutonomousAgent` integration or explicit SDK/runtime blocker;
- provider/model fail-closed behavior;
- v3 events and attention mappings for task states;
- structured Agent Admin risk review surfaces with no direct activation;
- tests for auth, tenant isolation, idempotency, event/attention linkage, provider fail-closed, and no fake/model-less normal success;
- docs identifying this as the second reusable AutonomousAgent vertical pattern.
