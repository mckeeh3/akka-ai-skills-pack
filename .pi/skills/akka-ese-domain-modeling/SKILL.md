---
name: akka-ese-domain-modeling
description: Model Akka event sourced entity domain code: immutable state, commands, events, validators, command-to-event handlers, and pure event application. Use when creating or reviewing the domain package for an EventSourcedEntity.
---

# Akka ESE Domain Modeling

Use this skill for the `domain` package of an Akka Java SDK event sourced entity.

## Required reading

Read these first if present:
- `akka-context/sdk/event-sourced-entities.html.md`
- `akka-context/sdk/ai-coding-assistant-guidelines.html.md`
- `../../../src/main/java/com/example/domain/ShoppingCart.java`
- `../../../src/main/java/com/example/domain/ShoppingCartValidator.java`
- `../../../src/main/java/com/example/domain/ShoppingCartCommandHandler.java`
- `../../../src/main/java/com/example/domain/ShoppingCartEventHandler.java`
- `../../../src/main/java/com/example/domain/Order.java`
- `../../../src/main/java/com/example/domain/OrderValidator.java`
- `../../../src/main/java/com/example/domain/OrderCommandHandler.java`
- `../../../src/main/java/com/example/domain/OrderReadyToShipBusinessLogic.java`
- `../../../src/main/java/com/example/domain/OrderEventHandler.java`
- `../../../src/main/java/com/example/domain/ExpiringShoppingCart.java`

## Mission

Produce domain code that is:
- immutable
- replay-safe
- independent of Akka effects
- easy for application-layer entities to consume
- decomposed so AI agents can extend it safely

## Preferred file split

Prefer separate focused files:
- `<DomainName>.java`
  - state record
  - nested value records
  - command sealed interface or shared command records
  - event sealed interface
- `<DomainName>Validator.java`
- `<DomainName>CommandHandler.java`
- `<DomainName>EventHandler.java`
- optional focused business logic helper, for example `<DomainName>ReadyToShipBusinessLogic.java`

## State rules

State should:
- be a Java record
- be immutable
- expose pure transition methods
- expose query helpers like `findItem(...)`
- provide `empty(...)` factory when useful
- never know about Akka effects or entity APIs

Good examples:
- `ShoppingCart.State`
- `Order.State`

## Command rules

Commands should:
- use imperative names
- be records
- be nested under a descriptive type such as `Order.Command`
- represent caller intent, not persistence details

Examples:
- `AddItem`
- `Checkout`
- `CreateOrder`
- `LineItemReadyToShip`

## Event rules

Events should:
- be records under one sealed interface
- represent facts that happened
- include `@TypeName` on every subtype
- be stable, private service data rather than public API types

Examples:
- `ShoppingCart.Event.ItemAdded`
- `Order.Event.OrderCreated`

## Validation pattern

Use a dedicated validator when the task benefits from explicit validation.

Preferred style:
- one overloaded `validate(...)` method per command type
- return `List<String>`
- keep messages explicit and deterministic
- do not throw for normal business validation

Examples:
- `ShoppingCartValidator`
- `OrderValidator`

## Command-to-event pattern

Use a dedicated command handler helper for business decisions.

Rules:
- input is current state + valid command
- output is zero, one, or many domain events
- no Akka effects
- inspect current state first
- no-op is represented by `Optional.empty()` or `List.of()`

Examples:
- `ShoppingCartCommandHandler`
- `OrderCommandHandler`

## Event application pattern

Use a dedicated event handler helper when it clarifies replay logic.

Rules:
- `apply(state, event)` only
- pure function
- no validation
- no current time, external calls, or side effects
- always return the next state

Examples:
- `ShoppingCartEventHandler`
- `OrderEventHandler`

## Multi-event decision pattern

When one command can change multiple facts:
- calculate the minimal event set
- keep this logic in a focused business-decision helper
- persist exactly the derived facts, nothing more

Repository example:
- `OrderReadyToShipBusinessLogic`

## Hard rules

Never:
- return Akka effects from domain code
- mutate collections in place
- validate inside `applyEvent`
- use endpoint request/response types as domain events
- couple domain code to HTTP or gRPC

## Review checklist

Before finishing, verify:
- state is immutable
- events all have `@TypeName`
- commands express intent clearly
- validator messages are deterministic
- command handler returns 0..N events only
- event handler is pure and replay-safe
- domain package has no Akka effect logic
