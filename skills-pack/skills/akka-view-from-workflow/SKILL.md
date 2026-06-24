---
name: akka-view-from-workflow
description: Implement Akka Java SDK Views that consume Workflow state changes using TableUpdater.onUpdate(...). Use when indexing workflow state by status, owner, or other query fields.
---

# Akka View from Workflow

Use this skill when the source of the view is a `Workflow`.

Capability-first framing: use workflow-backed Views for supervised read/evidence capabilities over long-running work, approval waits, failures, compensation state, retry status, escalation queues, and human/agent handoff dashboards. The Workflow remains the execution authority; the View provides scoped evidence and queue access.

## Compile contract gate

Use this skill only for a compile-ready slice under `../docs/app-description-to-code-compile-contract.md`, except for explicitly scoped doc/example maintenance. Before changing generated runtime code, confirm the accepted graph names the responsible worker/harness/actor adapter from `../docs/app-worker-tool-model.md`, the governed-tool and capability contract from `../docs/capability-first-backend-architecture.md`, and this Akka component's role as implementation evidence. If AuthContext, tenant/customer scope, validation, idempotency, denial, audit/trace, side-effect, exposure, or test obligations are missing, repair the brief or block instead of guessing.

## Required reading

Read these first if present:
- `akka-context/sdk/views.html.md`
- `akka-context/reference/views/concepts/table-updaters.html.md`
- `akka-context/sdk/workflows.html.md`
- `../docs/capability-first-backend-architecture.md`

## Source-specific rules

1. Subscribe with `@Consume.FromWorkflow(WorkflowClass.class)`.
2. Implement `onUpdate(State state)` in the `TableUpdater`.
3. Use `updateContext().eventSubject()` for the workflow id.
4. Treat the incoming value as the latest workflow state snapshot.
5. Add `@DeleteHandler` only when you need custom delete behavior.
6. Project capability fields needed for governance queues: tenant/customer scope, requester/assignee, status, risk, due time, approval state, correlation id, and redacted summary/evidence where relevant.

## Repository example

- a domain-specific workflow-status view
  - indexes workflow state by status
  - represents a queue/evidence read model rather than the workflow's authority to approve or execute
  - maps workflow id from `eventSubject()` into the view row
  - tested with mocked workflow incoming messages

## Generated SaaS compile review

For generated SaaS runtime work, apply the canonical compile contract, worker/tool model, and capability-first backend docs rather than duplicating shared validation, scope, idempotency, audit, and exposure rules here. In this component-specific review, verify the Akka mechanics above preserve the accepted governed-tool context, caller/scope fields, idempotent or no-op behavior, denial/retry semantics, and required tests/traces for the selected exposure path.

## Review checklist

Before finishing, verify:
- the updater uses `@Consume.FromWorkflow(...)`
- `onUpdate(...)` maps workflow state to a query-focused row
- tests use `withWorkflowIncomingMessages(...)`
- query wrappers and aliases match exactly
- protected queue/evidence queries include scope filters, caller-safe fields, and tests for forbidden or cross-scope access through the selected exposure surface
- non-SSE `ORDER BY` columns also appear in the same query's `WHERE` conditions
- view queries exposed as SSE do not include `ORDER BY`
