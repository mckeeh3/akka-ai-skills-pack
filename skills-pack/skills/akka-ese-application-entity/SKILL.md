---
name: akka-ese-application-entity
description: Implement core Akka Java SDK EventSourcedEntity classes in the application package, including command handlers, read handlers, persist/persistAll, and delete behavior. Use companion skills for TTL, notifications, and replication.
---

# Akka ESE Application Entity

Use this skill for the `application` package entity class itself.

Use it after the entity's named capability contract is known: command/read purpose, AuthContext and tenant/customer scope, idempotency, audit/trace requirements, approval rules, and selected exposure surfaces.

## Compile contract gate

Use this skill only for a compile-ready slice under `../docs/app-description-to-code-compile-contract.md`, except for explicitly scoped doc/example maintenance. Before changing generated runtime code, confirm the accepted graph names the responsible worker/harness/actor adapter from `../docs/app-worker-tool-model.md`, the governed-tool and capability contract from `../docs/capability-first-backend-architecture.md`, and this Akka component's role as implementation evidence. If AuthContext, tenant/customer scope, validation, idempotency, denial, audit/trace, side-effect, exposure, or test obligations are missing, repair the brief or block instead of guessing.

## Required reading

Read these first if present:
- `akka-context/sdk/event-sourced-entities.html.md`
- `akka-context/sdk/ai-coding-assistant-guidelines.html.md`

Load companion skills when needed:
- `akka-ese-ttl`
- `akka-ese-notifications`
- `akka-ese-replication`

## Entity skeleton

The entity should:
- extend `EventSourcedEntity<State, Event>`
- have `@Component(id = "...")`
- override `emptyState()`
- implement command handlers that return `Effect<T>` or `ReadOnlyEffect<T>`
- implement a pure `applyEvent(Event event)`

## Constructor rules

Use constructor injection for supported dependencies, for example:
- `EventSourcedEntityContext`
- `NotificationPublisher<Event>`

If `emptyState()` needs the entity id, store `context.entityId()` in a field.

## Command handler algorithm

For each command capability:
1. name the capability this handler implements or supports
2. inspect `currentState()`
3. validate input, including tenant/customer scoped ids carried by the command
4. enforce or assume a documented caller-boundary AuthContext/scope check; do not rely on UI or prompt-only authorization
5. return `effects().error(...)` on invalid input, forbidden scope, or business rejection when appropriate
6. apply idempotency rules before emitting new facts; duplicate or stale downstream commands should usually no-op
7. delegate business decision logic to domain helpers
8. if no events are needed, reply without persisting
9. if one event is needed, use `persist(event)`
10. if many events are needed, use `persistAll(events)`
11. ensure persisted events or caller-side audit records satisfy the capability audit/trace contract
12. reply in `thenReply(...)`

## Read handler rules

Entity reads are read/evidence capability surfaces. Return only scoped, redacted response shapes that are safe for the selected caller; do not expose raw internal state to endpoints or tools by default.

Use:
- `ReadOnlyEffect<T>` for ordinary reads
- `Effect<T>` for strongly consistent reads in replicated deployments

Repository examples:
- `PromptDocumentEntity.detail(...)`
- `PromptDocumentEntity.activeRuntimeLookup(...)`

For strongly consistent reads in replicated deployments, use the Akka SDK docs and add a target-project example; the current SaaS Foundation App example snapshot does not include a replicated read-consistency handler.

## Delete pattern

When deleting an entity:
- persist a final domain event first
- call `.deleteEntity()`
- then reply
- do not persist more events afterward

Repository example:
- `AgentDefinitionEntity.delete(...)`

## Agent tool exposure

Expose ESE command/read handlers as agent component tools only when the capability contract explicitly selects that surface. Read-only evidence handlers are safer defaults. Side-effecting commands require backend AuthContext/scope checks, idempotency, audit/trace, and approval policy before tool exposure.

## Feature-specific companion skills

For focused guidance, load:
- `akka-ese-ttl`
- `akka-ese-notifications`
- `akka-ese-replication`

## Anti-patterns

Never:
- put business logic directly in `applyEvent`
- mutate state directly in command handlers
- call external services from the entity
- rely on prompt text, tool descriptions, or frontend state for authorization
- expose side-effecting handlers as tools without capability permission, idempotency, audit, and approval rules
- publish notifications before persist succeeds
- skip `emptyState()` when a sensible default exists

## Review checklist

Before finishing, verify:
- entity extends `EventSourcedEntity<State, Event>`
- `@Component(id = ...)` exists
- `emptyState()` is sensible
- `applyEvent` is pure
- validation and capability scope checks happen before persist at the right boundary
- no-op/idempotent commands do not persist events
- audit/trace requirements are satisfied by events or caller-side audit records
- selected endpoint/tool/workflow exposure is documented and not broader than the capability contract
- delete behavior is explicit when needed
- TTL, notifications, and replication use the companion skills when included
