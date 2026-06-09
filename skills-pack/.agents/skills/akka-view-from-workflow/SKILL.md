---
name: akka-view-from-workflow
description: Implement Akka Java SDK Views that consume Workflow state changes using TableUpdater.onUpdate(...). Use when indexing workflow state by status, owner, or other query fields.
---

# Akka View from Workflow

Use this skill when the source of the view is a `Workflow`.

Capability-first framing: use workflow-backed Views for supervised read/evidence capabilities over long-running work, approval waits, failures, compensation state, retry status, escalation queues, and human/agent handoff dashboards. The Workflow remains the execution authority; the View provides scoped evidence and queue access.

## Generated SaaS input contract

Use `../references/generated-saas-input-contract.md` as the shared gate. Do not implement generated SaaS runtime code until the required capability, AuthContext/scope, DTO, side-effect, trace, and test inputs are present or explicitly deferred; otherwise repair the brief or route back to `agent-workstream-apps` + `capability-first-backend`.

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

## Generated SaaS view contract

For generated SaaS read/evidence capabilities, require:
- tenant/customer scoped row keys and query filters aligned with the selected `AuthContext`;
- redacted DTO rows for the chosen UI/API/MCP/agent-tool consumers, not raw state dumps;
- stable surface payload or evidence-bundle mapping when used by structured surfaces;
- data-access audit/work-trace requirements at the query or endpoint boundary;
- tests for authorized query, forbidden/cross-tenant query, redaction, projection update/delete behavior, and surface/API/tool consumers where exposed.


## Review checklist

Before finishing, verify:
- the updater uses `@Consume.FromWorkflow(...)`
- `onUpdate(...)` maps workflow state to a query-focused row
- tests use `withWorkflowIncomingMessages(...)`
- query wrappers and aliases match exactly
- protected queue/evidence queries include scope filters, caller-safe fields, and tests for forbidden or cross-scope access through the selected exposure surface
- non-SSE `ORDER BY` columns also appear in the same query's `WHERE` conditions
- view queries exposed as SSE do not include `ORDER BY`
