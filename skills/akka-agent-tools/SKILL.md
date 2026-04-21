---
name: akka-agent-tools
description: Implement Akka Java SDK agent function tools using @FunctionTool and external tool classes. Use companion skills for Akka component tools or remote MCP tool registration.
---

# Akka Agent Tools

Use this skill when an agent must call tools.

## Required reading

Read these first if present:
- `akka-context/sdk/agents/extending.html.md`
- `akka-context/sdk/agents/failures.html.md`
- `../../../src/main/java/com/example/application/WeatherAgent.java`
- `../../../src/main/java/com/example/application/WeatherForecastTools.java`

If the main task is not local or external tool classes, load the focused companion skill instead:
- `akka-agent-component-tools`
- `akka-agent-mcp-tools`

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
7. Use `akka-agent-component-tools` for `.tools(ComponentClass.class)`.
8. Use `akka-agent-mcp-tools` for `.mcpTools(...)`.
9. Do not try to use one agent as a tool for another agent.

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
- component or MCP tool cases are routed to the focused companion skill when needed
- agent-to-agent chaining is replaced with workflow orchestration when coordination is needed
