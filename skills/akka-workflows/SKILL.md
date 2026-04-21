---
name: akka-workflows
description: Orchestrate Akka Java SDK Workflow work across state modeling, step design, notifications, pause/resume flows, compensation, and integration testing. Use when the task spans more than one workflow concern.
---

# Akka Workflows

Use this as the top-level skill for Akka Java SDK workflow work.

## Goal

Generate or review workflow code that is:
- correct for Akka SDK 3.4+
- explicit about command handlers vs step handlers
- durable across retries and restarts
- safe to compensate or pause when business flow requires it
- easy for AI agents to extend with focused tests

## Required reading before coding

Read these first if present:
- `akka-context/sdk/workflows.html.md`
- `akka-context/sdk/ai-coding-assistant-guidelines.html.md`
- existing project workflow examples under `src/main/java/**/application/*Workflow.java`
- matching workflow tests under `src/test/java/**`

In this repository, prefer these examples:
- `../../../src/main/java/com/example/application/TransferWorkflow.java`
- `../../../src/main/java/com/example/application/ApprovalWorkflow.java`
- `../../../src/main/java/com/example/application/ReviewWorkflow.java`
- `../../../src/main/java/com/example/application/WalletEntity.java`
- `../../../src/main/java/com/example/domain/TransferState.java`
- `../../../src/main/java/com/example/domain/ApprovalState.java`
- `../../../src/main/java/com/example/api/TransferWorkflowEndpoint.java`
- `../../../src/main/java/com/example/api/ApprovalWorkflowEndpoint.java`
- `../../../docs/workflow-endpoint-pattern.md`
- `../../../src/test/java/com/example/application/TransferWorkflowIntegrationTest.java`
- `../../../src/test/java/com/example/application/TransferWorkflowEndpointIntegrationTest.java`
- `../../../src/test/java/com/example/application/ApprovalWorkflowIntegrationTest.java`
- `../../../src/test/java/com/example/application/ApprovalWorkflowEndpointIntegrationTest.java`
- `../../../src/test/java/com/example/application/ReviewWorkflowTopicConsumerIntegrationTest.java`
- `../../../src/test/java/com/example/application/ReviewRequestsByStatusViewIntegrationTest.java`

## Companion skills

Load the companion skill that matches the current task:

- `akka-workflow-component`
  - core workflow class structure, state updates, commands, steps, and settings
- `akka-workflow-compensation`
  - explicit failure handling, compensating steps, and idempotent downstream calls
- `akka-workflow-notifications`
  - `NotificationPublisher`, `NotificationStream`, and SSE-friendly progress updates
- `akka-workflow-pausing`
  - pause/resume flows that wait for external input before continuing
- `akka-workflow-testing`
  - `TestKitSupport`, `componentClient.forWorkflow(...)`, and `Awaitility`

If the workflow drives or is consumed by other components, also load:
- `akka-consumer-from-workflow`
- `akka-view-from-workflow`
- `akka-http-endpoint-component-client` when exposing workflow commands through HTTP
- `../../../docs/workflow-endpoint-pattern.md` for the shared HTTP workflow endpoint shape

## Default package layout

Use:
- `com.<org>.<app>.domain`
- `com.<org>.<app>.application`
- `com.<org>.<app>.api`

Rules:
- workflow state records belong in `domain`
- workflow classes belong in `application`
- endpoints that start or query workflows belong in `api`
- consumers and views reacting to workflow state belong in `application`

## Core rules

1. A workflow extends `Workflow<State>` and has `@Component(id = "...")`.
2. Public command handlers return `Effect<T>` or `ReadOnlyEffect<T>`.
3. Step methods return `StepEffect` and should usually be private.
4. Use `effects()` in command handlers and `stepEffects()` in step handlers.
5. Start the workflow by updating state and transitioning to the first step.
6. Keep workflow state explicit about progress, decisions, and data needed by later steps.
7. Make downstream calls replay-safe or idempotent because steps may retry.
8. Use `settings()` for timeouts and recovery, not `definition()`.
9. Use method references for transitions, not string step names.
10. Test both happy-path completion and rejection or pause/compensation paths.

## Decision guide

### 1. Straight-through orchestration workflow
Use when the workflow coordinates a short series of durable steps.

Repository example:
- `TransferWorkflow`

### 2. Workflow with live notifications
Use when clients should subscribe to workflow progress instead of polling.

Repository examples:
- `TransferWorkflow`
- `TransferWorkflowEndpoint`
- `ApprovalWorkflow`
- `ApprovalWorkflowEndpoint`

### 3. Pause-and-resume workflow
Use when a human or external signal must unblock the next step.

Repository examples:
- `ApprovalWorkflow`
- `ApprovalWorkflowEndpoint`

### 4. Workflow as upstream source for views or consumers
Use when other components react to workflow state snapshots.

Repository examples:
- `ReviewWorkflow`
- `ReviewWorkflowTopicConsumer`
- `ReviewRequestsByStatusView`

## Final review checklist

Before finishing, verify:
- `settings()` is used instead of `definition()`
- command handlers accept 0 or 1 parameter and return `Effect` or `ReadOnlyEffect`
- step methods accept 0 or 1 parameter and return `StepEffect`
- workflow state captures all data needed for later steps and compensation
- transitions use method references
- pause, compensation, or notification logic is explicit when needed
- downstream component calls are idempotent under retries
- tests use `componentClient.forWorkflow(...)`

## Response style

When answering coding tasks:
- name the workflow state type and workflow class explicitly
- call out whether the workflow is straight-through, compensating, or paused
- mention the idempotency strategy for downstream calls
- list the concrete example files used as references
