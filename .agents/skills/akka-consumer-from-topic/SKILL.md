---
name: akka-consumer-from-topic
description: Implement Akka Java SDK Consumers that subscribe to broker topics, handle CloudEvent metadata, and forward messages into Akka components.
---

# Akka Consumer from Topic

Use this skill when a Consumer ingests messages from Kafka or Google Cloud Pub/Sub topics.

## Compile contract gate

Use this skill only for a compile-ready slice under `../docs/app-description-to-code-compile-contract.md`, except for explicitly scoped doc/example maintenance. Before changing generated runtime code, confirm the accepted graph names the responsible worker/harness/actor adapter from `../docs/app-worker-tool-model.md`, the governed-tool and capability contract from `../docs/capability-first-backend-architecture.md`, and this Akka component's role as implementation evidence. If AuthContext, tenant/customer scope, validation, idempotency, denial, audit/trace, side-effect, exposure, or test obligations are missing, repair the brief or block instead of guessing.

## Required reading

Read these first if present:
- `akka-context/sdk/consuming-producing.html.md`
- `akka-context/sdk/component-and-service-calls.html.md`

## Use this pattern when

- external systems push commands or facts through a broker topic
- the consumer should route topic messages into entities or workflows
- CloudEvent subject metadata identifies the entity key
- raw bytes handling is needed for non-JSON messages

## Capability authority rules

Topic ingestion is a reactive capability boundary. Define the capability id, trusted producer/service identity, tenant/customer scope source, message provenance, required authorization or prior approval reference, idempotency key, and audit/trace fields before forwarding to components.

For protected or consequential commands, do not trust topic payloads as authorization by themselves. Verify service ACLs/signatures where available, reload or validate tenant/customer membership/policy from authoritative state, and call entity/workflow commands that enforce scope and idempotency. Invalid, unauthorized, stale, or cross-tenant messages should usually be audited and treated as terminal `done()`/`ignore()` outcomes; transient downstream failures may fail the handler for retry.

## Core pattern

1. Annotate the class with `@Consume.FromTopic("topic-name")`.
2. Add one handler per message type when consuming CloudEvents with typed payloads.
3. Use `messageContext().eventSubject()` when the `ce-subject` identifies the target entity.
4. Return `effects().done()` after a successful downstream call.
5. For raw binary input, accept `byte[]`.

## CloudEvent rules

For typed JSON messages, upstream publishers should provide:
- `Content-Type = application/json`
- `ce-specversion = 1.0`
- `ce-type = fully qualified class name`
- `ce-subject` when routing or per-entity ordering is needed

## Repository example

These are Akka substrate mechanics examples, not generated-product architecture templates.

- `WorkstreamEventAttentionConsumer`
  - consumes typed command messages from `workstream-event-commands`
  - uses `ce-subject` as the workstream-event id
  - forwards commands to `AgentDefinitionEntity`

## Testing rules

- configure `withTopicIncomingMessages("topic-name")`
- publish typed messages through `testKit.getMessageBuilder()` when metadata is needed
- assert the downstream state or outgoing produced messages

## Generated SaaS compile review

For generated SaaS runtime work, apply the canonical compile contract, worker/tool model, and capability-first backend docs rather than duplicating shared validation, scope, idempotency, audit, and exposure rules here. In this component-specific review, verify the Akka mechanics above preserve the accepted governed-tool context, caller/scope fields, idempotent or no-op behavior, denial/retry semantics, and required tests/traces for the selected exposure path.

## Review checklist

Before finishing, verify:
- `@Consume.FromTopic` uses the intended topic name
- handler parameter types match topic payload types
- `ce-subject` usage is explicit when routing depends on it
- raw `byte[]` is used only for binary payloads
- downstream calls are idempotent under redelivery
- protected messages carry or resolve tenant/customer scope, capability id, producer provenance, correlation id, and authorization/approval/audit references
- denial/no-op versus retry behavior is explicit for invalid, unauthorized, stale, duplicate, and transient-failure cases
