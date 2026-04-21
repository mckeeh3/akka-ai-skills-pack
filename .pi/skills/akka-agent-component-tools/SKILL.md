---
name: akka-agent-component-tools
description: Implement Akka Java SDK agents that use Views, EventSourcedEntities, KeyValueEntities, or Workflows as function tools via effects().tools(ComponentClass.class). Use when Akka components themselves should be exposed as tools.
---

# Akka Agent Component Tools

Use this skill when an agent should call Akka components as tools.

## Required reading

Read these first if present:
- `akka-context/sdk/agents/extending.html.md`
- `akka-context/sdk/agents/failures.html.md`
- `../../../src/main/java/com/example/application/ShoppingCartEntity.java`
- `../../../src/main/java/com/example/application/CartInspectorAgent.java`
- `../../../src/test/java/com/example/application/CartInspectorAgentTest.java`

## Use this pattern when

- the model should query or mutate Akka component state through tool calls
- a View, entity, or workflow already exposes the exact operation the agent needs
- you want the model to choose when to fetch current application state

## Core pattern

1. Annotate the component method with `@FunctionTool`.
2. Keep component tools public and focused.
3. Register the component class with `.tools(ComponentClass.class)`.
4. For entities and workflows, remember the generated tool schema includes `uniqueId`.
5. Prefer read-only component tools unless state changes are truly required.
6. Describe side effects clearly when a tool can mutate state.

## Repository example

- `ShoppingCartEntity#getCart`
  - read-only EventSourcedEntity tool
  - generated schema adds `uniqueId` for the cart id
- `CartInspectorAgent`
  - registers `ShoppingCartEntity.class` as a tool
  - instructs the model to use `ShoppingCartEntity_getCart`

## Review checklist

Before finishing, verify:
- the component method is public and returns the correct Akka effect type
- entity/workflow tool prompts explain the `uniqueId` input clearly
- read-only tools are preferred for lookup-style tasks
- agents are not passed as tools
