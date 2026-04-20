---
name: akka-workflow-notifications
description: Add live notification streams to Akka Java SDK Workflows using NotificationPublisher and NotificationStream, and expose them through SSE-friendly endpoint mappings. Use when clients must track workflow progress without polling.
---

# Akka Workflow Notifications

Use this skill when a workflow should push progress updates to subscribers.

## Required reading

Read these first if present:
- `akka-context/sdk/workflows.html.md`
- `../../../src/main/java/com/example/application/TransferWorkflow.java`
- `../../../src/main/java/com/example/application/ApprovalWorkflow.java`
- `../../../src/main/java/com/example/api/TransferWorkflowEndpoint.java`
- `../../../src/main/java/com/example/api/ApprovalWorkflowEndpoint.java`
- `../../../src/test/java/com/example/application/TransferWorkflowEndpointIntegrationTest.java`
- `../../../src/test/java/com/example/application/ApprovalWorkflowEndpointIntegrationTest.java`

## Core pattern

1. Inject `NotificationPublisher<Notification>` into the workflow.
2. Define a compact notification record or sealed interface for subscriber-facing progress updates.
3. Call `publish(...)` at meaningful milestones such as step completion, rejection, compensation, or unexpected failure.
4. Expose a public `NotificationStream<Notification>` method.
5. Map workflow notifications to API-facing SSE records in the endpoint.

## Repository example

- `TransferWorkflow`
  - publishes progress notifications for withdraw success, completion, rejection, compensation, and unexpected failure
  - exposes `updates()` for subscribers
- `ApprovalWorkflow`
  - publishes notifications when approval starts waiting and when approval is applied
  - exposes `updates()` for subscribers
- `TransferWorkflowEndpoint`
  - adapts workflow notifications into API records
  - returns them as SSE over HTTP
- `ApprovalWorkflowEndpoint`
  - adapts paused-workflow notifications into API records
  - returns them as SSE over HTTP

## Design note

Notifications are for user experience and observability, not business correctness. They do not replace the authoritative workflow state from `get()`.

## Review checklist

Before finishing, verify:
- the workflow injects `NotificationPublisher<...>`
- notifications are small and progress-oriented
- the workflow exposes a `NotificationStream<...>` method
- public APIs map internal notifications to API-facing records
- tests verify that subscribers receive workflow progress after the stream is opened
