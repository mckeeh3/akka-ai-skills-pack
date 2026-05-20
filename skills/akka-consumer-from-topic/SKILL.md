---
name: akka-consumer-from-topic
description: Implement Akka Java SDK Consumers that subscribe to broker topics, handle CloudEvent metadata, and forward messages into Akka components.
---

# Akka Consumer from Topic

Use this skill when a Consumer ingests messages from Kafka or Google Cloud Pub/Sub topics.

## Generated SaaS input contract

For generated full-stack AI-first SaaS work, implement only after the selected task, app-description, spec, or backlog supplies or explicitly defers:
- functional agent or explicit internal-only/foundation scope;
- workstream, structured surface id/type/version, and surface action or workstream event when user-facing;
- capability id/class, selected Akka substrate, and exposure surfaces;
- `AuthContext`, tenant/customer scope, roles/capabilities, and backend authorization boundary;
- input/output DTOs, redaction, side effects, idempotency, policy/approval/escalation, audit/work traces, and required tests.

If these are absent and the work is generated SaaS implementation, route back to `agent-workstream-apps` + `capability-first-backend` or block for task-brief repair instead of guessing.

## Required reading

Read these first if present:
- `akka-context/sdk/consuming-producing.html.md`
- `akka-context/sdk/component-and-service-calls.html.md`
- `../../../src/main/java/com/example/application/ShoppingCartCommandsTopicConsumer.java`
- `../../../src/test/java/com/example/application/ShoppingCartCommandsTopicConsumerIntegrationTest.java`

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

- `ShoppingCartCommandsTopicConsumer`
  - consumes typed command messages from `shopping-cart-commands`
  - uses `ce-subject` as the shopping-cart id
  - forwards commands to `ShoppingCartEntity`

## Testing rules

- configure `withTopicIncomingMessages("topic-name")`
- publish typed messages through `testKit.getMessageBuilder()` when metadata is needed
- assert the downstream state or outgoing produced messages

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
- `@Consume.FromTopic` uses the intended topic name
- handler parameter types match topic payload types
- `ce-subject` usage is explicit when routing depends on it
- raw `byte[]` is used only for binary payloads
- downstream calls are idempotent under redelivery
- protected messages carry or resolve tenant/customer scope, capability id, producer provenance, correlation id, and authorization/approval/audit references
- denial/no-op versus retry behavior is explicit for invalid, unauthorized, stale, duplicate, and transient-failure cases
