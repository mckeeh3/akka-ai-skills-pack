---
name: akka-workflow-compensation
description: Implement Akka Java SDK Workflow compensation patterns using explicit result types, compensating steps, and retry-safe downstream calls. Use when a workflow must undo prior work after a later step fails.
---

# Akka Workflow Compensation

Use this skill when the workflow must reverse earlier work if a later step cannot complete.

## Required reading

Read these first if present:
- `akka-context/sdk/workflows.html.md`
- `../../../src/main/java/com/example/application/TransferWorkflow.java`
- `../../../src/main/java/com/example/application/WalletEntity.java`
- `../../../src/main/java/com/example/domain/TransferState.java`
- `../../../src/main/java/com/example/domain/Wallet.java`
- `../../../src/test/java/com/example/application/TransferWorkflowIntegrationTest.java`
- `../../../src/test/java/com/example/application/WalletEntityTest.java`

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

## Review checklist

Before finishing, verify:
- known failures are represented by result types, not exceptions
- compensation input data is stored durably in workflow state
- compensating steps are themselves safe to retry
- downstream command ids are stable across retries and restarts
- tests cover both the completed path and compensated path
