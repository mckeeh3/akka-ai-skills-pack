# My Account Personal Attention Digest AutonomousAgent

## Purpose

Create the next AutonomousAgent worker vertical after integrated regression readiness for four workers: **My Account Personal Attention Digest**.

This worker summarizes the current user's authorized cross-workstream attention into a personal digest. It should read only backend-authorized attention summaries/items, preserve tenant/customer/workstream visibility, emit v3 events, create digest-ready attention where appropriate, and render My Account digest surfaces without becoming a general notification platform.

## Source context

Builds on:

- `specs/autonomous-agent-fullstack-regression-readiness/`
- `docs/autonomous-agent-worker-runtime-pattern.md`
- `specs/autonomous-agent-worker-pattern-extraction/`
- `specs/workstream-attention-backbone-v1/`
- `specs/workstream-attention-event-producers-v2/`
- `specs/workstream-event-backbone-v3/`
- starter My Account, attention, event backbone, and AutonomousAgent worker files

## Scope

- Define My Account personal attention digest task/result contract.
- Implement real Akka `AutonomousAgent` runtime path where feasible.
- Govern task start/read/cancel/acknowledge capabilities.
- Read authorized cross-workstream attention only; hidden workstreams/items must not leak.
- Emit `workflow.my_account.*` and `worker.task.*` v3 events.
- Derive digest-ready/blocked/failed/acknowledged attention states.
- Render My Account digest summary/progress surfaces.
- Preserve provider/model fail-closed behavior and no model-less normal success.

## Non-goals

- Do not build a general notification center or enterprise digest platform.
- Do not send emails/push notifications in this first slice.
- Do not expose hidden workstream names, counts, traces, or inferred existence.
- Do not mutate source attention items from digest output except through separate governed capabilities.
- Do not count deterministic/model-less summaries as normal runtime success.

## Execution model

Execute one task per fresh harness context. Each task must update `pending-tasks.md`, run checks or record blockers, and make one focused commit.

## Sprint sequence

1. Contract and visibility/redaction design.
2. Backend AutonomousAgent runtime and authorized attention evidence collection.
3. Events, attention, and My Account digest surfaces.
4. Validation in rendered scaffold.
5. Docs/handoff.
6. Verification.

## Handoff

Current docs/handoff artifact: `my-account-personal-attention-digest-handoff.md`.

## Done state

Complete when the starter/reference assets have:

- documented My Account Personal Attention Digest AutonomousAgent contract;
- governed task lifecycle capabilities;
- authorized attention evidence collection with strict redaction;
- real Akka `AutonomousAgent` integration or explicit SDK/runtime blocker;
- provider/model fail-closed behavior;
- v3 events and attention mappings;
- My Account digest result/progress surfaces;
- tests for auth, tenant isolation/redaction, idempotency, event/attention linkage, provider fail-closed, and no fake/model-less normal success;
- docs identifying this as a personal attention digest worker without overclaiming a notification platform.
