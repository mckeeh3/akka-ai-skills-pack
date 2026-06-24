---
name: akka-ese-domain-modeling
description: "Model Akka event sourced entity domain code: immutable state, commands, events, validators, command-to-event handlers, and pure event application. Use when creating or reviewing the domain package for an EventSourcedEntity."
---

# Akka ESE Domain Modeling

Use this skill for the `domain` package of an Akka Java SDK event sourced entity.

## Compile contract gate

Use this skill only for a compile-ready slice under `../docs/app-description-to-code-compile-contract.md`, except for explicitly scoped doc/example maintenance. Before changing generated runtime code, confirm the accepted graph names the responsible worker/harness/actor adapter from `../docs/app-worker-tool-model.md`, the governed-tool and capability contract from `../docs/capability-first-backend-architecture.md`, and this Akka component's role as implementation evidence. If AuthContext, tenant/customer scope, validation, idempotency, denial, audit/trace, side-effect, exposure, or test obligations are missing, repair the brief or block instead of guessing.

## Required reading

Read these first if present:
- `akka-context/sdk/event-sourced-entities.html.md`
- `akka-context/sdk/ai-coding-assistant-guidelines.html.md`

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
- `WorkstreamEvent.State`
- `Order.State`

## Command rules

Commands should:
- map to a named backend capability or an internal step of one
- use imperative names
- be records
- be nested under a descriptive type such as `Order.Command`
- represent caller intent, not persistence details
- carry scoped identifiers, idempotency/correlation fields, and safe defaults required by the capability contract

Examples:
- `AddItem`
- `Attention`
- `CreateOrder`
- `LineItemReadyToShip`

## Event rules

Events should:
- be records under one sealed interface
- represent facts that happened
- include `@TypeName` on every subtype
- be stable, private service data rather than public API types

Examples:
- `WorkstreamEvent.Event.ItemAdded`
- `Order.Event.OrderCreated`

## Validation pattern

Use a dedicated validator when the task benefits from explicit validation. Validate capability input shape and scoped ids in domain/application code; authorization and AuthContext checks may happen at the caller boundary, but prompt text, tool descriptions, and frontend state are never sufficient controls.

Preferred style:
- one overloaded `validate(...)` method per command type
- return `List<String>`
- keep messages explicit and deterministic
- do not throw for normal business validation

Examples:
- `WorkstreamEventValidator`
- `OrderValidator`

## Command-to-event pattern

Use a dedicated command handler helper for business decisions.

Rules:
- input is current state + valid command
- output is zero, one, or many domain events
- no Akka effects
- inspect current state first
- encode idempotent duplicate/stale behavior as no-op when the capability allows it
- no-op is represented by `Optional.empty()` or `List.of()`

Examples:
- `WorkstreamEventCommandHandler`
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
- `WorkstreamEventEventHandler`
- `OrderEventHandler`

## Multi-event decision pattern

When one command can change multiple facts:
- calculate the minimal event set
- keep this logic in a focused business-decision helper
- persist exactly the derived facts, nothing more

Repository example:
- domain-specific business-decision helper

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
- commands express capability intent clearly
- scoped identifiers and idempotency/correlation fields match the capability contract when required
- validator messages are deterministic
- command handler returns 0..N events only
- event handler is pure and replay-safe
- domain package has no Akka effect logic
