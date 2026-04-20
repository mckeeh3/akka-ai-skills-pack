---
name: akka-consumer-from-workflow
description: Implement Akka Java SDK Consumers that subscribe to Workflow state changes and optional deletes, commonly for notifications or downstream integration.
---

# Akka Consumer from Workflow

Use this skill when a Consumer reacts to Workflow state changes.

## Required reading

Read these first if present:
- `akka-context/sdk/consuming-producing.html.md`
- `akka-context/sdk/workflows.html.md`
- `../../../src/main/java/com/example/application/ReviewWorkflow.java`
- `../../../src/main/java/com/example/application/ReviewWorkflowTopicConsumer.java`
- `../../../src/test/java/com/example/application/ReviewWorkflowTopicConsumerIntegrationTest.java`

## Use this pattern when

- downstream notifications depend on workflow status
- another system should hear about workflow completion or failure
- a view is not enough and an external side effect or produced message is needed

## Core pattern

1. Annotate the class with `@Consume.FromWorkflow(MyWorkflow.class)`.
2. Accept the workflow state type in `onUpdate(...)`.
3. Filter on stable workflow-state fields such as status.
4. Use `messageContext().eventSubject()` for the workflow id.
5. Add `@DeleteHandler` if workflow deletion matters.
6. Produce or trigger side effects only when the status you care about is reached.

## Repository example

- `ReviewWorkflowTopicConsumer`
  - consumes workflow state updates
  - ignores non-completed states
  - publishes a compact `ReviewCompleted` message to a topic with `ce-subject`

## Design note

If a consumer needs step origin, attempt count, or other execution details, encode the needed information into workflow state explicitly. Consumers receive state changes, not step callbacks.

## Review checklist

Before finishing, verify:
- `@Consume.FromWorkflow` points at the right workflow
- the handler accepts the workflow state type
- status filtering is explicit
- `@DeleteHandler` is present only when delete behavior is needed
- produced messages include metadata when downstream ordering or routing depends on subject
