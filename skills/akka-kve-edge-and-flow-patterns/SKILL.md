---
name: akka-kve-edge-and-flow-patterns
description: Apply the right KeyValueEntity interaction pattern for Akka endpoints, consumers, and workflows. Use when deciding between edge-facing validation/replies and downstream internal idempotent no-op behavior.
---

# Akka KVE Edge and Flow Patterns

Use this skill when deciding how a key value entity participates in a larger flow.

## Required reading

Read these first if present:
- `akka-context/sdk/key-value-entities.html.md`
- `akka-context/sdk/ai-coding-assistant-guidelines.html.md`
- `../../../src/main/java/com/example/application/DraftCartEntity.java`
- `../../../src/main/java/com/example/application/PurchaseOrderEntity.java`
- `../../../src/main/java/com/example/application/DraftCartCheckoutConsumer.java`
- `../../../src/main/java/com/example/api/DraftCartEndpoint.java`
- `../../../src/main/java/com/example/api/PurchaseOrderEndpoint.java`

## Choose one interaction model

### Edge-facing entity

Use when:
- an HTTP or gRPC endpoint calls the entity directly
- the caller needs immediate business feedback
- invalid user input should be rejected explicitly

Typical traits:
- entity validates commands
- invalid input returns `effects().error(...)`
- success often replies with current state
- endpoint catches `CommandException` and maps to HTTP response

Repository example:
- `DraftCartEntity`
- `DraftCartEndpoint`

### Downstream/internal entity

Use when:
- a consumer, workflow, or timed action drives the entity
- commands may be duplicated or arrive after the state already advanced
- idempotent behavior is desirable

Typical traits:
- malformed input may still error
- duplicate or stale commands often become no-ops
- command handler returns `Done`
- one command often results in one full-state replacement

Repository example:
- `PurchaseOrderEntity`
- `DraftCartCheckoutConsumer`

## Endpoint rules

For HTTP or gRPC endpoints that call entities:
- define API request/response records in the endpoint or api package
- do not expose domain state directly as the external API
- use constructor-injected `ComponentClient`
- prefer synchronous `.invoke()` in production code
- use `HttpResponses.created(...)` for create operations
- use `HttpResponses.badRequest(...)` for business validation failures when returning `HttpResponse`

Repository example:
- `DraftCartEndpoint`
- `PurchaseOrderEndpoint`

## Consumer and workflow rules

When another component reacts to key value entity state changes:
- let the entity update state only
- let the consumer or workflow perform follow-up side effects
- use the consumed state or delete handler to react appropriately
- prefer idempotent downstream commands

Repository example:
- `DraftCartCheckoutConsumer`

## No-op guidance

A no-op is correct when a command is semantically safe to ignore, for example:
- item already removed
- entity already checked out
- order already created
- line item already marked ready

Represent no-op by:
- returning `Optional.empty()` or a no-op decision from domain logic
- replying successfully without calling `updateState(...)` at the entity layer

Do not use no-op when the caller clearly needs an explicit validation error.

## Review checklist

Before finishing, verify:
- the task uses the right interaction model
- edge-facing flows return useful errors to the caller
- downstream flows are idempotent where appropriate
- endpoints map domain types to API types
- side effects happen outside the entity
