---
name: akka-kve-application-entity
description: Implement core Akka Java SDK KeyValueEntity classes in the application package, including command handlers, read handlers, updateState, and delete behavior. Use companion skills for TTL, notifications, and replication.
---

# Akka KVE Application Entity

Use this skill for the `application` package entity class itself.

Use it after the entity's named capability contract is known: command/read purpose, AuthContext and tenant/customer scope, idempotency, audit/trace requirements, approval rules, and selected exposure surfaces.

## Compile contract gate

Use this skill only for a compile-ready slice under `../docs/app-description-to-code-compile-contract.md`, except for explicitly scoped doc/example maintenance. Before changing generated runtime code, confirm the accepted graph names the responsible worker/harness/actor adapter from `../docs/app-worker-tool-model.md`, the governed-tool and capability contract from `../docs/capability-first-backend-architecture.md`, and this Akka component's role as implementation evidence. If AuthContext, tenant/customer scope, validation, idempotency, denial, audit/trace, side-effect, exposure, or test obligations are missing, repair the brief or block instead of guessing.

## Required reading

Read these first if present:
- `akka-context/sdk/key-value-entities.html.md`
- `akka-context/sdk/ai-coding-assistant-guidelines.html.md`

Load companion skills when needed:
- `akka-kve-ttl`
- `akka-kve-notifications`
- `akka-kve-replication`

## Entity skeleton

The entity should:
- extend `KeyValueEntity<State>`
- have `@Component(id = "...")`
- override `emptyState()`
- implement command handlers that return `Effect<T>` or `ReadOnlyEffect<T>`

## Constructor rules

Use constructor injection for supported dependencies, for example:
- `KeyValueEntityContext`
- `NotificationPublisher<Notification>`

If `emptyState()` needs the entity id, store `context.entityId()` in a field.

## Command handler algorithm

For each command capability:
1. name the capability this handler implements or supports
2. inspect `currentState()`
3. validate input, including tenant/customer scoped ids carried by the command
4. enforce or assume a documented caller-boundary AuthContext/scope check; do not rely on UI or prompt-only authorization
5. return `effects().error(...)` on invalid input, forbidden scope, or business rejection when appropriate
6. apply idempotency rules before replacing state; duplicate or stale downstream commands should usually no-op
7. delegate business decision logic to domain helpers
8. if no state change is needed, reply without calling `updateState`
9. if state should change, compute the new full state
10. call `effects().updateState(newState)`
11. ensure caller-side audit records, notifications, or separate audit-grade components satisfy the capability audit/trace contract when consequential
12. reply in `thenReply(...)`

## Read handler rules

Entity reads are read/evidence capability surfaces. Return only scoped, redacted response shapes that are safe for the selected caller; do not expose raw internal state to endpoints or tools by default.

Use:
- `ReadOnlyEffect<T>` for ordinary reads
- `Effect<T>` for strongly consistent reads in replicated deployments

Repository example:
- a domain-specific strongly consistent read method

## Delete pattern

When deleting an entity:
- call `.deleteEntity()`
- then reply
- do not call `updateState(...)` afterward
- if the task only needs a reset, consider updating to empty state instead of deleting

Repository example:
- `DurableIdentityRepositoryEntity.delete(...)`

## Agent tool exposure

Expose KVE command/read handlers as agent component tools only when the capability contract explicitly selects that surface. Read-only current-state evidence handlers are safer defaults. Side-effecting updates require backend AuthContext/scope checks, idempotency, audit/trace, and approval policy before tool exposure.

## Feature-specific companion skills

For focused guidance, load:
- `akka-kve-ttl`
- `akka-kve-notifications`
- `akka-kve-replication`

## Anti-patterns

Never:
- mutate state directly in command handlers
- call external services from the entity
- rely on prompt text, tool descriptions, or frontend state for authorization
- expose side-effecting handlers as tools without capability permission, idempotency, audit, and approval rules
- skip `emptyState()` when a sensible default exists
- model KVE writes as persisted events

## Review checklist

Before finishing, verify:
- entity extends `KeyValueEntity<State>`
- `@Component(id = ...)` exists
- `emptyState()` is sensible
- validation and capability scope checks happen before `updateState` at the right boundary
- no-op/idempotent commands do not update state
- audit/trace requirements are satisfied by caller-side audit records, notifications, or separate audit-grade components when needed
- selected endpoint/tool/workflow exposure is documented and not broader than the capability contract
- delete behavior is explicit when needed
- TTL, notifications, and replication use the companion skills when included
