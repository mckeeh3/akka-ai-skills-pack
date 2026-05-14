---
name: akka-agent-tools
description: Implement Akka Java SDK agent function tools using @FunctionTool and external tool classes. Use companion skills for Akka component tools, remote MCP tool registration, or harness-like skill loading through tools.
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
- `akka-agent-harness-skills` for model-loadable internal guidance backed by packaged resources

## Use this pattern when

In generated SaaS apps, every tool that reads protected data or performs side effects must receive or resolve AuthContext, enforce tenant/customer scope and permission/capability checks, and create required audit/work-trace records before returning data or committing actions.

- the model needs live or computed data to answer correctly
- the model should choose between multiple helper functions
- agent output depends on current date, lookup services, or component calls
- tool descriptions materially affect model behavior
- an agent should approximate harness skill loading by exposing approved guidance blocks as tools

## Core pattern

1. Annotate tools with `@FunctionTool`.
2. Add `@Description` to parameters when the model needs argument hints.
3. Register external tool classes with `.tools(instance)` or `.tools(Class)`.
4. Agent-local `@FunctionTool` methods are automatically available.
5. Keep tool behavior deterministic and fast.
6. Handle tool failures with `.onFailure(...)` in the agent.
7. Use `akka-agent-component-tools` for `.tools(ComponentClass.class)`.
8. Use `akka-agent-mcp-tools` for `.mcpTools(...)`.
9. Use `akka-agent-harness-skills` when tools return skill-like guidance from whitelisted `src/main/resources` content.
10. Do not try to use one agent as a tool for another agent.
11. Tool descriptions must state side effects, required permissions, tenant/customer scope, policy/approval gates, and audit behavior when consequential.
12. Tools must fail closed for missing AuthContext, disabled users, forbidden scopes, or cross-tenant/customer access; do not rely on prompt instructions as authorization.
13. For high-impact tool actions, return recommendations or approval requests unless the accepted policy grants autonomous authority.

## Repository examples

- `WeatherAgent`
  - local `@FunctionTool` for current date
  - external tool registration with `.tools(forecastTools)`
- `WeatherForecastTools`
  - public external tool method with parameter descriptions

## Review checklist

Before finishing, verify:
- tool descriptions clearly say what the tool does, what side effects it has, and which permissions/scopes/policies are required
- parameters are documented when names alone are ambiguous
- tools are explicit in the effect chain
- protected tools accept or derive AuthContext and enforce tenant/customer filtering before data access
- tool denials, data access, approvals, and side effects are auditable when required by the secure foundation
- component, MCP, or harness-skill tool cases are routed to the focused companion skill when needed
- tools that return guidance do not expose arbitrary filesystem paths or unbounded resource content
- agent-to-agent chaining is replaced with workflow orchestration when coordination is needed
