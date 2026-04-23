---
name: akka-kve-domain-modeling
description: "Model Akka key value entity domain code: immutable state, commands, validators, command-to-state handlers, and pure business-decision helpers. Use when creating or reviewing the domain package for a KeyValueEntity."
---

# Akka KVE Domain Modeling

Use this skill for the `domain` package of an Akka Java SDK key value entity.

## Required reading

Read these first if present:
- `akka-context/sdk/key-value-entities.html.md`
- `akka-context/sdk/ai-coding-assistant-guidelines.html.md`
- `../../../src/main/java/com/example/domain/DraftCart.java`
- `../../../src/main/java/com/example/domain/DraftCartValidator.java`
- `../../../src/main/java/com/example/domain/DraftCartCommandHandler.java`
- `../../../src/main/java/com/example/domain/PurchaseOrder.java`
- `../../../src/main/java/com/example/domain/PurchaseOrderValidator.java`
- `../../../src/main/java/com/example/domain/PurchaseOrderCommandHandler.java`
- `../../../src/main/java/com/example/domain/PurchaseOrderReadyToShipBusinessLogic.java`
- `../../../src/main/java/com/example/domain/ExpiringDraftCartSession.java`

## Mission

Produce domain code that is:
- immutable
- independent of Akka effects
- easy for application-layer entities to consume
- decomposed so AI agents can extend it safely
- explicit about full-state replacement semantics

## Preferred file split

Prefer separate focused files:
- `<DomainName>.java`
  - state record
  - nested value records
  - command sealed interface or shared command records
  - optional notification sealed interface for notification payloads
- `<DomainName>Validator.java`
- `<DomainName>CommandHandler.java`
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
- `DraftCart.State`
- `PurchaseOrder.State`

## Command rules

Commands should:
- use imperative names
- be records
- be nested under a descriptive type such as `PurchaseOrder.Command`
- represent caller intent, not persistence details

Examples:
- `AddItem`
- `Checkout`
- `CreateOrder`
- `LineItemReadyToShip`

## Validation pattern

Use a dedicated validator when the task benefits from explicit validation.

Preferred style:
- one overloaded `validate(...)` method per command type
- return `List<String>`
- keep messages explicit and deterministic
- do not throw for normal business validation

Examples:
- `DraftCartValidator`
- `PurchaseOrderValidator`

## Command-to-state pattern

Use a dedicated command handler helper for business decisions.

Rules:
- input is current state + valid command
- output is updated state or no-op
- no Akka effects
- inspect current state first
- no-op is represented by `Optional.empty()` or a boolean decision

Examples:
- `DraftCartCommandHandler`
- `PurchaseOrderCommandHandler`

## Business-decision helper pattern

When one command may cause a more complex full-state transition:
- keep the logic in a focused helper
- return an updated state or empty
- calculate the minimal state change needed, then let the entity replace the full state once

Repository example:
- `PurchaseOrderReadyToShipBusinessLogic`

## Hard rules

Never:
- return Akka effects from domain code
- mutate collections in place
- couple domain code to HTTP or gRPC
- treat KVE code as if it were event sourced
- invent fake events unless the task explicitly needs notification payload types

## Review checklist

Before finishing, verify:
- state is immutable
- commands express intent clearly
- validator messages are deterministic
- command handler returns updated state or no-op only
- domain package has no Akka effect logic
- design reflects state replacement rather than event persistence
