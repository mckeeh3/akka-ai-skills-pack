---
name: akka-agent-component-tools
description: Implement Akka Java SDK agents that use Views, EventSourcedEntities, KeyValueEntities, or Workflows as function tools via effects().tools(ComponentClass.class). Use when Akka components themselves should be exposed as tools.
---

# Akka Agent Component Tools

Use this skill when an agent should call selected Akka component command/query methods as tool exposure surfaces for governed backend capabilities.

## Required reading

Read these first if present:
- `../../docs/capability-first-backend-architecture.md`
- `akka-context/sdk/agents/extending.html.md`
- `akka-context/sdk/agents/failures.html.md`
- `../../../src/main/java/com/example/application/ShoppingCartEntity.java`
- `../../../src/main/java/com/example/application/CartInspectorAgent.java`
- `../../../src/test/java/com/example/application/CartInspectorAgentTest.java`

## Use this pattern when

- a named capability has been intentionally selected for agent tool exposure through an Akka component
- the model should query curated evidence or request bounded state changes through tool calls
- a View, entity, or workflow already exposes the exact capability operation the agent needs
- you want the model to choose when to fetch current application state

Do not use this pattern to expose arbitrary component internals. Component tools are one exposure surface for capabilities, not the backend design root.

## Core pattern

1. Start from the capability contract: id/name, actor/caller, AuthContext, input/output schemas, data access, side effects, idempotency, policy/approval, audit/trace, exposure surfaces, and tests.
2. Annotate only the selected component method with `@FunctionTool`.
3. Keep component tools public, focused, and aligned to one capability operation/query.
4. Register the component class with `.tools(ComponentClass.class)`.
5. For entities and workflows, remember the generated tool schema includes `uniqueId`; describe how it maps to the scoped aggregate/workflow id.
6. Prefer read-only evidence component tools unless state changes are truly required.
7. Describe side effects, required permissions, tenant/customer scope, approval gates, and audit behavior when a tool can mutate state.
8. Enforce authorization, tenant/customer scope, validation, idempotency, and denial behavior in backend code; tool descriptions and prompts only help the model choose the tool.
9. Record audit/work-trace events for protected data access, denials, approvals, and side effects as required by the capability contract.
10. Return scoped, redacted output; do not expose raw state dumps unless the capability explicitly allows that data shape.

## Capability-first component-tool guidance

- Views are usually the safest component-tool source for agent evidence because they can provide curated, scoped read models.
- Entity tools should expose product-level commands or safe read operations, not CRUD-shaped internals by default.
- Workflow tools should start or advance supervised, retryable, approval-gated, or long-running capabilities.
- Side-effecting component tools should default to proposal/approval flows unless accepted policy grants bounded autonomous authority.
- If the same capability is also exposed through UI, HTTP/gRPC, MCP, timer, or consumer paths, preserve the same authority, validation, idempotency, approval, audit, and tenant/customer scope semantics.

## Repository example

- `ShoppingCartEntity#inspectCartSummary`
  - read-only EventSourcedEntity capability tool for `cart.inspect-summary`
  - returns a curated `CartSummary` rather than raw entity state or event history
  - generated schema adds `uniqueId` for the cart id
- `CartInspectorAgent`
  - registers `ShoppingCartEntity.class` as a tool
  - instructs the model to use `ShoppingCartEntity_inspectCartSummary`

## Review checklist

Before finishing, verify:
- every component tool maps to a named capability and selected exposure surface
- the component method is public and returns the correct Akka effect type
- entity/workflow tool descriptions and system instructions explain the `uniqueId` input clearly without treating prompts as authorization
- AuthContext, tenant/customer scope, permission/capability checks, validation, denial shape, and redaction are enforced before protected reads or writes
- read-only, scoped evidence tools are preferred for lookup-style tasks
- state-changing tools define side effects, idempotency, approval/autonomy rules, and audit/work-trace records
- tests cover deterministic tool invocation plus forbidden, tenant-isolation, audit, idempotency, and approval behavior when applicable
- agents are not passed as tools
