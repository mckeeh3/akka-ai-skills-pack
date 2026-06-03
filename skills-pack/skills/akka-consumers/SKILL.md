---
name: akka-consumers
description: Orchestrate Akka Java SDK Consumer work across entity/workflow/topic sources, producing patterns, and integration testing. Use when the task spans more than one consumer concern.
---

# Akka Consumers

Use this as the top-level skill for Akka Java SDK Consumer work when the consumer is already selected as a reactive execution surface for one or more backend capabilities.

For broad product, PRD, feature, or integration requests, route through `capability-first-backend` and `akka-solution-decomposition` before implementing consumers. Do not start from event glue when capability authority, provenance, tenant/customer scope, side effects, idempotency, and audit semantics are still unclear.

## Goal

Generate or review Consumer code that is:
- correct for Akka SDK 3.4+
- explicit about the source being consumed
- safe for at-least-once delivery and duplicate redelivery
- clear about whether the consumer ignores, completes, or produces
- backed by executable examples and tests

## Capability-first AI-first substrate role

In AI-first SaaS implementations, use consumers for reactive capabilities: asynchronous trace fanout, enrichment, notifications, downstream publication, integration with external signals, and curation of material events for supervision, audit, digest, or outcome loops.

Treat each consumer reaction as a selected consumer-tool/internal-tool execution surface for a named governed-tool inside a backend capability, not as anonymous event glue. Before coding, identify the capability id, governed-tool id, event provenance, allowed caller/system principal, tenant/customer scope, required prior authorization or approval decision, side effects, idempotency key, retry behavior, denial shape, and audit/work-trace obligations.

Keep consumers idempotent and side-effect boundaries explicit. Preserve actor, tenant/customer, policy, approval, correlation, and trace context from upstream messages when side effects or downstream publication are scoped. If the upstream event does not carry enough authority context, reload it from the authoritative entity/workflow or reject/ignore safely with an audit record. Do not hide authority transitions, approval decisions, or policy commits inside consumers; route consequential state changes to entities or workflows, then use consumers to react, enrich, publish, update role-specific dashboard attention, or project follow-on work for the human surface graph and internal workstream agent graph.

Pair AI-first consumers with:
- `akka-views` for command centers, decision queues, digests, and audit/outcome reporting
- `akka-workflows` for durable follow-up, exception handling, retries, or compensations
- `akka-timed-actions` for reminders, delayed rechecks, periodic digests, or replay/simulation schedules
- endpoint skills when consumed events must surface to browser, service, or MCP clients

## Required reading before coding

Read these first if present:
- `akka-context/sdk/consuming-producing.html.md`
- `akka-context/sdk/component-and-service-calls.html.md`
- `akka-context/sdk/dev-best-practices.html.md`
- `akka-context/sdk/access-control.html.md`
- `akka-context/sdk/running-locally.html.md`
- existing project consumers under `src/main/java/**/application/*Consumer.java`
- matching tests under `src/test/java/**`

In this repository, prefer these examples:
- `../../examples/akka-components/src/main/java/com/example/application/ShoppingCartCheckoutConsumer.java`
- `../../examples/akka-components/src/main/java/com/example/application/DraftCartCheckoutConsumer.java`
- `../../examples/akka-components/src/main/java/com/example/application/ShoppingCartCommandsTopicConsumer.java`
- `../../examples/akka-components/src/main/java/com/example/application/ShoppingCartEventsToTopicConsumer.java`
- `../../examples/akka-components/src/main/java/com/example/application/ShoppingCartPublicEventsConsumer.java`
- `../../examples/akka-components/src/main/java/com/example/application/ReviewWorkflowTopicConsumer.java`
- `../../docs/consumer-reference.md`
- `../../examples/akka-components/src/test/java/com/example/application/ShoppingCartCheckoutConsumerIntegrationTest.java`
- `../../examples/akka-components/src/test/java/com/example/application/DraftCartCheckoutConsumerIntegrationTest.java`
- `../../examples/akka-components/src/test/java/com/example/application/ShoppingCartCommandsTopicConsumerIntegrationTest.java`
- `../../examples/akka-components/src/test/java/com/example/application/ReviewWorkflowTopicConsumerIntegrationTest.java`

## Companion skills

Load the companion skill that matches the current task:

- `akka-consumer-from-event-sourced-entity`
  - consuming journal events from an event sourced entity
  - optional `@SnapshotHandler`
- `akka-consumer-from-key-value-entity`
  - consuming current-state updates and optional deletes from a key value entity
- `akka-consumer-from-workflow`
  - consuming workflow state transitions and optional deletes
- `akka-consumer-from-topic`
  - consuming CloudEvents or raw `byte[]` messages from a broker topic
- `akka-consumer-from-service-stream`
  - consuming public streams from another Akka service
- `akka-consumer-producing`
  - producing to a topic or service stream, including metadata and ACLs
- `akka-consumer-testing`
  - TestKit topic/workflow/entity incoming messages and outgoing topic assertions

## Core rules

1. A consumer extends `akka.javasdk.consumer.Consumer` and has `@Component(id = "...")`.
2. Choose exactly one source annotation that matches the upstream:
   - `@Consume.FromEventSourcedEntity`
   - `@Consume.FromKeyValueEntity`
   - `@Consume.FromWorkflow`
   - `@Consume.FromTopic`
   - `@Consume.FromServiceStream` when subscribing to another service
3. Handlers return one of:
   - `effects().done()`
   - `effects().ignore()`
   - `effects().produce(...)`
4. Consumers must tolerate duplicate delivery. Make downstream calls idempotent.
5. Use `messageContext().eventSubject()` when the entity or message subject is needed.
6. Prefer explicit filtering with `ignore()` over hidden conditionals.
7. Put business state changes in entities or workflows, not in mutable consumer fields.
8. For per-entity broker ordering, include `ce-subject` metadata when producing.
9. For service streams, add `@Acl` so other Akka services can subscribe.
10. Test topic flows with `TestKitSupport` and mocked incoming/outgoing messages; when the upstream component exists in the same generated service and the named feature depends on the reaction, also exercise the real upstream-to-consumer path before marking the feature complete.
11. For generated SaaS flows, include tenant/customer ids and actor/audit metadata in consumed messages or load them from the authoritative source before side effects.
12. Recheck authorization, use a system/service principal with an explicit capability grant, or consume an explicit prior authorization/approval decision before consequential side effects.
13. Prevent cross-tenant/customer fanout by preserving `ce-subject`, tenant/customer metadata, and scoped downstream command payloads.
14. Emit or propagate AdminAuditEvent/work-trace records for data access, denials, publications, retries, and consequential side effects.
15. Treat duplicate delivery and handler retry as normal: downstream commands need idempotency keys based on event id/source/subject or the consumer must detect duplicates before side effects.
16. Define denial/retry semantics explicitly: invalid or unauthorized messages should usually become audited terminal `done()`/`ignore()` outcomes, while transient dependency failures may fail the handler to trigger retry.

## Decision guide

### 1. Downstream reaction to event sourced facts
Use `akka-consumer-from-event-sourced-entity`.

Repository examples:
- `ShoppingCartCheckoutConsumer`
- `ShoppingCartEventsToTopicConsumer`
- `ShoppingCartPublicEventsConsumer`

### 2. Downstream reaction to key value state snapshots
Use `akka-consumer-from-key-value-entity`.

Repository example:
- `DraftCartCheckoutConsumer`

### 3. Downstream reaction to workflow progress
Use `akka-consumer-from-workflow`.

Repository example:
- `ReviewWorkflowTopicConsumer`

### 4. Command ingestion from a broker topic
Use `akka-consumer-from-topic`.

Repository example:
- `ShoppingCartCommandsTopicConsumer`

### 5. Service-to-service subscription from another Akka service
Use `akka-consumer-from-service-stream`.

Repository references:
- `ShoppingCartPublicEventsConsumer` for the producer side
- `docs/consumer-reference.md` for the subscriber-side snippet

### 6. Event publication to topics or service streams
Use `akka-consumer-producing`.

Repository examples:
- `ShoppingCartEventsToTopicConsumer`
- `ShoppingCartPublicEventsConsumer`
- `ReviewWorkflowTopicConsumer`

### 7. Integration and eventing tests
Use `akka-consumer-testing`.

Repository examples:
- `ShoppingCartCommandsTopicConsumerIntegrationTest`
- `ReviewWorkflowTopicConsumerIntegrationTest`
- `ShoppingCartCheckoutConsumerIntegrationTest`
- `DraftCartCheckoutConsumerIntegrationTest`

## Final review checklist

Before finishing, verify:
- the correct `@Consume` source annotation is used
- the handler input type matches the source semantics
- `effects().ignore()` is explicit for unhandled events or states
- downstream side effects are idempotent under redelivery and use a stable dedupe/idempotency key
- tenant/customer scope, actor/system principal, authorization/approval, and trace metadata are preserved or intentionally reloaded before side effects
- unauthorized, stale, invalid, or cross-tenant/customer messages have explicit audited denial/no-op behavior instead of accidental retries
- transient failures that should retry are not converted to terminal success
- cross-tenant/customer side effects are rejected or impossible by payload design
- `ce-subject` metadata is added when producing ordered per-entity topic messages
- `@Acl` is present for `@Produce.ServiceStream`
- tests use the right TestKit incoming/outgoing message hooks and cover duplicate/retry and denial semantics

## Response style

When answering coding tasks:
- name the exact consumer source
- state whether the consumer is pure side-effecting or a producer
- call out the idempotency strategy
- list the concrete example files used as references
