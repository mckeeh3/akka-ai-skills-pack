---
name: akka-workflow-testing
description: Write Akka Java SDK Workflow tests using TestKitSupport, componentClient.forWorkflow(...), supporting components, and Awaitility. Use for workflow happy paths, compensation, and pause/resume behavior.
---

# Akka Workflow Testing

Use this skill for executable workflow tests.

## Required reading

Read these first if present:
- `akka-context/sdk/workflows.html.md`
- `akka-context/sdk/agents/testing.html.md`
- `../../../src/test/java/com/example/application/TransferWorkflowIntegrationTest.java`
- `../../../src/test/java/com/example/application/ApprovalWorkflowIntegrationTest.java`
- `../../../src/test/java/com/example/application/WalletEntityTest.java`

## Test harness rules

Workflow tests should usually:
- extend `TestKitSupport`
- call workflow command handlers through `componentClient.forWorkflow(workflowId)`
- set up supporting entities or components through `componentClient`
- use `Awaitility` when waiting for later workflow steps to finish
- assert current workflow state through a read handler such as `get()`

## Repository patterns

### Straight-through and compensation flows
- `TransferWorkflowIntegrationTest`
  - creates supporting wallet entities first
  - starts the workflow through `componentClient.forWorkflow(...)`
  - waits for completion with `Awaitility`
  - asserts both successful completion and compensation behavior

### Pause/resume flows
- `ApprovalWorkflowIntegrationTest`
  - starts the workflow and verifies it is waiting
  - sends a later resume command
  - asserts the workflow ends in the approved state
  - covers invalid resume attempts

### Supporting idempotent components
- `WalletEntityTest`
  - verifies duplicate command ids become no-op replies
  - gives the workflow a retry-safe downstream target

## What to cover

Prefer these categories:
1. successful workflow completion
2. validation rejection in the start command
3. pause/resume or compensation path when present
4. downstream state after the workflow finishes
5. duplicate-start or invalid-state command rejection

## Anti-patterns

Avoid:
- assuming later steps complete before the start command returns
- testing workflow logic only through mocked incoming messages
- skipping setup of the downstream components the workflow actually calls
- relying on mutable test sleeps instead of `Awaitility`

## Review checklist

Before finishing, verify:
- tests extend `TestKitSupport`
- workflow calls go through `componentClient.forWorkflow(...)`
- `Awaitility` is used for asynchronous completion when needed
- assertions cover both workflow state and important downstream effects
