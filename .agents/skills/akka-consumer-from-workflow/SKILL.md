---
name: akka-consumer-from-workflow
description: Implement Akka Java SDK Consumers that subscribe to Workflow state changes and optional deletes, commonly for notifications or downstream integration.
---

# Akka Consumer from Workflow

Use this skill when a Consumer reacts to Workflow state changes.

## Compile contract gate

Use this skill only for a compile-ready slice under `../docs/app-description-to-code-compile-contract.md`, except for explicitly scoped doc/example maintenance. Before changing generated runtime code, confirm the accepted graph names the responsible worker/harness/actor adapter from `../docs/app-worker-tool-model.md`, the governed-tool and capability contract from `../docs/capability-first-backend-architecture.md`, and this Akka component's role as implementation evidence. If AuthContext, tenant/customer scope, validation, idempotency, denial, audit/trace, side-effect, exposure, or test obligations are missing, repair the brief or block instead of guessing.

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

## Generated SaaS compile review

For generated SaaS runtime work, apply the canonical compile contract, worker/tool model, and capability-first backend docs rather than duplicating shared validation, scope, idempotency, audit, and exposure rules here. In this component-specific review, verify the Akka mechanics above preserve the accepted governed-tool context, caller/scope fields, idempotent or no-op behavior, denial/retry semantics, and required tests/traces for the selected exposure path.

## Review checklist

Before finishing, verify:
- `@Consume.FromWorkflow` points at the right workflow
- the handler accepts the workflow state type
- status filtering is explicit
- `@DeleteHandler` is present only when delete behavior is needed
- produced messages include metadata when downstream ordering or routing depends on subject
