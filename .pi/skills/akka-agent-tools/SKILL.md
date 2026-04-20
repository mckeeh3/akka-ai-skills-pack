---
name: akka-agent-tools
description: Implement Akka Java SDK agent function tools using @FunctionTool, external tool classes, component tools, and MCP tool registration. Use when tool calling is the main concern.
---

# Akka Agent Tools

Use this skill when an agent must call tools.

## Required reading

Read these first if present:
- `akka-context/sdk/agents/extending.html.md`
- `akka-context/sdk/agents/failures.html.md`
- `../../../src/main/java/com/example/application/WeatherAgent.java`
- `../../../src/main/java/com/example/application/WeatherForecastTools.java`

Also use the official docs for:
- Akka component tools through `effects().tools(ComponentClass.class)`
- remote MCP tools through `effects().mcpTools(...)`

## Use this pattern when

- the model needs live or computed data to answer correctly
- the model should choose between multiple helper functions
- agent output depends on current date, lookup services, or component calls
- tool descriptions materially affect model behavior

## Core pattern

1. Annotate tools with `@FunctionTool`.
2. Add `@Description` to parameters when the model needs argument hints.
3. Register external tool classes with `.tools(instance)` or `.tools(Class)`.
4. Agent-local `@FunctionTool` methods are automatically available.
5. Keep tool behavior deterministic and fast.
6. Handle tool failures with `.onFailure(...)` in the agent.
7. Do not try to use one agent as a tool for another agent.

## Repository examples

- `WeatherAgent`
  - local `@FunctionTool` for current date
  - external tool registration with `.tools(forecastTools)`
- `WeatherForecastTools`
  - public external tool method with parameter descriptions

## Review checklist

Before finishing, verify:
- tool descriptions clearly say what the tool does and what side effects it has
- parameters are documented when names alone are ambiguous
- tools are explicit in the effect chain
- agent-to-agent chaining is replaced with workflow orchestration when coordination is needed
