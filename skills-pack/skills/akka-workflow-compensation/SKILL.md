---
name: akka-workflow-compensation
description: Implement Akka Java SDK Workflow compensation patterns using explicit result types, compensating steps, and retry-safe downstream calls. Use when a workflow must undo prior work after a later step fails.
---

# Akka Workflow Compensation

Use this skill when the workflow must reverse earlier work if a later step cannot complete.

## Compile contract gate

Use this skill only for a compile-ready slice under `../docs/app-description-to-code-compile-contract.md`, except for explicitly scoped doc/example maintenance. Before changing generated runtime code, confirm the accepted graph names the responsible worker/harness/actor adapter from `../docs/app-worker-tool-model.md`, the governed-tool and capability contract from `../docs/capability-first-backend-architecture.md`, and this Akka component's role as implementation evidence. If AuthContext, tenant/customer scope, validation, idempotency, denial, audit/trace, side-effect, exposure, or test obligations are missing, repair the brief or block instead of guessing.

## Required reading

Read these first if present:
- `../docs/capability-first-backend-architecture.md`
- `akka-context/sdk/workflows.html.md`

## Capability-first compensation role

Use compensation for governed capabilities whose side effects may need safe reversal or follow-up remediation. The capability contract should say which steps are consequential, which side effects can be compensated, which failures require human supervision, what audit/work-trace events are written, and which idempotency keys make retry and compensation safe.

## Core pattern

1. Have downstream components return explicit success or rejection results for known business outcomes.
2. Update workflow state with the known failure outcome instead of throwing for expected cases.
3. Transition to a compensating step when later work fails after earlier work succeeded.
4. Store durable command ids in workflow state so downstream retries are idempotent.
5. Reserve workflow recovery settings for unknown failures or timeouts.

## Pattern reference

- a domain-specific workflow
  - withdraw succeeds first
  - deposit rejection updates workflow state to `DEPOSIT_REJECTED`
  - compensation deposits funds back to the source wallet
- `WalletEntity`
  - deduplicates commands by `commandId`
  - replies with `Wallet.Result.Success` or `Wallet.Result.Rejected`

## Design note

Do not use compensation as a substitute for normal business branching. If the failure is expected and modeled, branch explicitly with domain result types. Recovery and retries should handle the unknown cases that remain.

## Generated SaaS compile review

For generated SaaS runtime work, apply the canonical compile contract, worker/tool model, and capability-first backend docs rather than duplicating shared validation, scope, idempotency, audit, and exposure rules here. In this component-specific review, verify the Akka mechanics above preserve the accepted governed-tool context, caller/scope fields, idempotent or no-op behavior, denial/retry semantics, and required tests/traces for the selected exposure path.

## Review checklist

Before finishing, verify:
- known failures are represented by result types, not exceptions
- compensation input data, authorization/approval basis, correlation id, and trace id are stored durably in workflow state
- compensating steps are themselves safe to retry
- downstream command ids are stable across retries and restarts
- audit/work-trace events distinguish original side effects, failures, compensation attempts, and unresolved supervision-needed states
- tests cover both the completed path and compensated path
