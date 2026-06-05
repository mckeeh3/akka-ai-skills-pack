---
name: akka-consumer-from-workflow
description: Implement Akka Java SDK Consumers that subscribe to Workflow state changes and optional deletes, commonly for notifications or downstream integration.
---

# Akka Consumer from Workflow

Use this skill when a Consumer reacts to Workflow state changes.

## Generated SaaS input contract

For generated full-stack AI-first SaaS implementation work, apply `../references/generated-saas-input-contract.md` before coding. If the selected task lacks the required workstream/capability/AuthContext/surface/trace/test contract and does not explicitly defer it, route back to `agent-workstream-apps` + `capability-first-backend` or block for task-brief repair instead of guessing.

## Required reading

Read these first if present:
- `akka-context/sdk/consuming-producing.html.md`
- `akka-context/sdk/workflows.html.md`

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

## Pattern reference

- a domain-specific workflow topic consumer
  - consumes workflow state updates
  - ignores non-completed states
  - publishes a compact `ReviewCompleted` message to a topic with `ce-subject`

## Design note

If a consumer needs step origin, attempt count, or other execution details, encode the needed information into workflow state explicitly. Consumers receive state changes, not step callbacks.

## Generated SaaS consumer contract

For generated SaaS reactive capabilities, require:
- reactive capability id, event provenance, correlation id, and tenant/customer scope;
- system-principal or service-authority basis for downstream calls;
- idempotency key/dedupe strategy for at-least-once delivery;
- retry, poison, obsolete, forbidden, and no-op behavior;
- scoped/redacted publication when producing topics or service streams;
- audit/work-trace records for side effects, denials, retries, and emitted public events;
- tests for duplicate delivery, cross-tenant/forbidden input, idempotent downstream effects, audit/trace, and surface/realtime/API outcomes where exposed.


## Review checklist

Before finishing, verify:
- `@Consume.FromWorkflow` points at the right workflow
- the handler accepts the workflow state type
- status filtering is explicit
- `@DeleteHandler` is present only when delete behavior is needed
- produced messages include metadata when downstream ordering or routing depends on subject
