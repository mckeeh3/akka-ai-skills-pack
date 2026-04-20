---
name: akka-ese-edge-and-flow-patterns
description: Apply the right EventSourcedEntity interaction pattern for Akka endpoints, consumers, and workflows. Use when deciding between edge-facing validation/replies and downstream internal idempotent no-op behavior.
---

# Akka ESE Edge and Flow Patterns

Use this skill when deciding how an event sourced entity participates in a larger flow.

## Required reading

Read these first if present:
- `akka-context/sdk/event-sourced-entities.html.md`
- `akka-context/sdk/ai-coding-assistant-guidelines.html.md`
- `../../../src/main/java/com/example/application/ShoppingCartEntity.java`
- `../../../src/main/java/com/example/application/OrderEntity.java`
- `../../../src/main/java/com/example/application/ShoppingCartCheckoutConsumer.java`
- `../../../src/main/java/com/example/api/ShoppingCartEndpoint.java`
- `../../../src/main/java/com/example/api/OrderEndpoint.java`

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
- `ShoppingCartEntity`
- `ShoppingCartEndpoint`

### Downstream/internal entity

Use when:
- a consumer, workflow, or timed action drives the entity
- commands may be duplicated or arrive after the state already advanced
- idempotent behavior is desirable

Typical traits:
- malformed input may still error
- duplicate or stale commands often become no-ops
- command handler returns `Done`
- command may emit zero, one, or many events

Repository example:
- `OrderEntity`
- `ShoppingCartCheckoutConsumer`

## Endpoint rules

For HTTP or gRPC endpoints that call entities:
- define API request/response records in the endpoint or api package
- do not expose domain state directly as the external API
- use constructor-injected `ComponentClient`
- prefer synchronous `.invoke()` in production code
- use `HttpResponses.created(...)` for create operations
- use `HttpResponses.badRequest(...)` for business validation failures when returning `HttpResponse`

Repository example:
- `ShoppingCartEndpoint`
- `OrderEndpoint`

## Consumer and workflow rules

When another component reacts to entity events:
- let the entity persist facts only
- let the consumer or workflow perform follow-up side effects
- use the consumed event subject to locate related entity ids when needed
- prefer idempotent downstream commands

Repository example:
- `ShoppingCartCheckoutConsumer`

## No-op guidance

A no-op is correct when a command is semantically safe to ignore, for example:
- item already removed
- entity already checked out
- order already created
- line item already marked ready

Represent no-op by:
- returning `Optional.empty()` or `List.of()` from domain decision logic
- replying successfully without persisting events at the entity layer

Do not use no-op when the caller clearly needs an explicit validation error.

## Review checklist

Before finishing, verify:
- the task uses the right interaction model
- edge-facing flows return useful errors to the caller
- downstream flows are idempotent where appropriate
- endpoints map domain types to API types
- side effects happen outside the entity
