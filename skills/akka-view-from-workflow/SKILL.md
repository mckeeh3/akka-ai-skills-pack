---
name: akka-view-from-workflow
description: Implement Akka Java SDK Views that consume Workflow state changes using TableUpdater.onUpdate(...). Use when indexing workflow state by status, owner, or other query fields.
---

# Akka View from Workflow

Use this skill when the source of the view is a `Workflow`.

## Required reading

Read these first if present:
- `akka-context/sdk/views.html.md`
- `akka-context/reference/views/concepts/table-updaters.html.md`
- `akka-context/sdk/workflows.html.md`
- `../../../src/main/java/com/example/application/ReviewWorkflow.java`
- `../../../src/main/java/com/example/application/ReviewRequestsByStatusView.java`
- `../../../src/test/java/com/example/application/ReviewRequestsByStatusViewIntegrationTest.java`

## Source-specific rules

1. Subscribe with `@Consume.FromWorkflow(WorkflowClass.class)`.
2. Implement `onUpdate(State state)` in the `TableUpdater`.
3. Use `updateContext().eventSubject()` for the workflow id.
4. Treat the incoming value as the latest workflow state snapshot.
5. Add `@DeleteHandler` only when you need custom delete behavior.

## Repository example

- `ReviewRequestsByStatusView`
  - indexes workflow state by status
  - maps workflow id from `eventSubject()` into the view row
  - tested with mocked workflow incoming messages

## Review checklist

Before finishing, verify:
- the updater uses `@Consume.FromWorkflow(...)`
- `onUpdate(...)` maps workflow state to a query-focused row
- tests use `withWorkflowIncomingMessages(...)`
- query wrappers and aliases match exactly
- `ORDER BY` columns also appear in the same query's `WHERE` conditions
