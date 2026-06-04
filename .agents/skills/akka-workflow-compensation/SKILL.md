---
name: akka-workflow-compensation
description: Implement Akka Java SDK Workflow compensation patterns using explicit result types, compensating steps, and retry-safe downstream calls. Use when a workflow must undo prior work after a later step fails.
---

# Akka Workflow Compensation

Use this skill when the workflow must reverse earlier work if a later step cannot complete.

## Generated SaaS input contract

For generated full-stack AI-first SaaS work, implement only after the selected task, app-description, spec, or backlog supplies or explicitly defers:
- functional agent or explicit internal-only/foundation scope;
- workstream, structured surface id/type/version, and surface action or workstream event when user-facing;
- capability id/class, selected Akka substrate, and exposure surfaces;
- `AuthContext`, tenant/customer scope, roles/capabilities, and backend authorization boundary;
- input/output DTOs, redaction, side effects, idempotency, policy/approval/escalation, audit/work traces, and required tests.

If these are absent and the work is generated SaaS implementation, route back to `agent-workstream-apps` + `capability-first-backend` or block for task-brief repair instead of guessing.

## Required reading

Read these first if present:
- `../../docs/capability-first-backend-architecture.md`
- `akka-context/sdk/workflows.html.md`
- `../../examples/akka-components/src/main/java/com/example/application/TransferWorkflow.java`
- `../../examples/akka-components/src/main/java/com/example/application/WalletEntity.java`
- `../../examples/akka-components/src/main/java/com/example/domain/TransferState.java`
- `../../examples/akka-components/src/main/java/com/example/domain/Wallet.java`
- `../../examples/akka-components/src/test/java/com/example/application/TransferWorkflowIntegrationTest.java`
- `../../examples/akka-components/src/test/java/com/example/application/WalletEntityTest.java`

## Capability-first compensation role

Use compensation for governed capabilities whose side effects may need safe reversal or follow-up remediation. The capability contract should say which steps are consequential, which side effects can be compensated, which failures require human supervision, what audit/work-trace events are written, and which idempotency keys make retry and compensation safe.

## Core pattern

1. Have downstream components return explicit success or rejection results for known business outcomes.
2. Update workflow state with the known failure outcome instead of throwing for expected cases.
3. Transition to a compensating step when later work fails after earlier work succeeded.
4. Store durable command ids in workflow state so downstream retries are idempotent.
5. Reserve workflow recovery settings for unknown failures or timeouts.

## Repository example

- `TransferWorkflow`
  - withdraw succeeds first
  - deposit rejection updates workflow state to `DEPOSIT_REJECTED`
  - compensation deposits funds back to the source wallet
- `WalletEntity`
  - deduplicates commands by `commandId`
  - replies with `Wallet.Result.Success` or `Wallet.Result.Rejected`

## Design note

Do not use compensation as a substitute for normal business branching. If the failure is expected and modeled, branch explicitly with domain result types. Recovery and retries should handle the unknown cases that remain.

## Generated SaaS checks

For generated SaaS workflows, preserve the accepted capability contract:
- compensation, notification, approval, and escalation steps carry `AuthContext` or system-principal authority basis;
- downstream side effects are idempotent and retry safe;
- structured-surface/workstream events include surface id/version, event id, correlation id, trace ids, and stale/progress semantics;
- audit/work traces cover approval, denial, compensation, retry exhaustion, and side effects;
- tests cover authorized success, forbidden/cross-tenant, idempotency/no-op, surface/realtime updates, and audit/trace emission where exposed.


## Review checklist

Before finishing, verify:
- known failures are represented by result types, not exceptions
- compensation input data, authorization/approval basis, correlation id, and trace id are stored durably in workflow state
- compensating steps are themselves safe to retry
- downstream command ids are stable across retries and restarts
- audit/work-trace events distinguish original side effects, failures, compensation attempts, and unresolved supervision-needed states
- tests cover both the completed path and compensated path
