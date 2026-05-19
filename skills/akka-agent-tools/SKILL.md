---
name: akka-agent-tools
description: Implement Akka Java SDK agent function tools using @FunctionTool and external tool classes. Use companion skills for Akka component tools, remote MCP tool registration, or harness-like skill loading through tools.
---

# Akka Agent Tools

Use this skill when an agent must call local or external function tools as selected exposure surfaces for governed backend capabilities.

## Required reading

Read these first if present:
- `../../docs/capability-first-backend-architecture.md`
- `akka-context/sdk/agents/extending.html.md`
- `akka-context/sdk/agents/failures.html.md`
- `../../../src/main/java/com/example/application/WeatherAgent.java`
- `../../../src/main/java/com/example/application/WeatherForecastTools.java`

If the main task is not local or external tool classes, load the focused companion skill instead:
- `akka-agent-tool-boundaries` for backend-enforced ToolPermissionBoundary grants, tool registry/catalog, denied-tool semantics, approval-required expansion, and tool invocation traces
- `akka-agent-component-tools`
- `akka-agent-mcp-tools`
- `akka-agent-harness-skills` for model-loadable internal guidance backed by packaged resources
- `akka-agent-skill-governance` for tenant-managed, versioned, audited `readSkill(skillId)` guidance tools
- `akka-resend-email-service` when a managed agent needs a governed Resend-backed email preview/send tool via `@FunctionTool`

## Use this pattern when

In generated SaaS apps, every tool that reads protected data or performs side effects must expose a named capability contract. The tool must receive or resolve AuthContext, enforce tenant/customer scope and permission/capability checks, apply policy/approval rules, and create required audit/work-trace records before returning data or committing actions.

- a named capability is intentionally exposed to the agent as a local or external function tool
- the model needs live or computed data to answer correctly
- the model should choose between multiple helper functions
- agent output depends on current date, lookup services, or component calls
- tool descriptions materially affect model behavior but do not enforce authorization
- an agent should approximate harness skill loading by exposing approved guidance blocks as tools
- an agent should load tenant-managed governed skills through `readSkill(skillId)` with manifest authorization

## Core pattern

1. Start from the capability contract: id/name, purpose, actor/caller, AuthContext, schemas, data access, side effects, idempotency, policy/approval, audit/trace, and tests.
2. Expose only capabilities the agent is allowed to request; do not register helper methods merely because they exist.
3. For managed agents or protected tools, load `akka-agent-tool-boundaries` and enforce the active `ToolPermissionBoundary` and tool registry/catalog before tool execution.
4. Annotate tools with `@FunctionTool`.
5. Add `@Description` to parameters when the model needs argument hints.
6. Register external tool classes with `.tools(instance)` or `.tools(Class)`.
7. Agent-local `@FunctionTool` methods are automatically available.
8. Keep tool behavior deterministic and fast.
9. Handle tool failures with `.onFailure(...)` in the agent, returning safe denial/failure shapes.
10. Use `akka-agent-component-tools` for `.tools(ComponentClass.class)`.
11. Use `akka-agent-mcp-tools` for `.mcpTools(...)`.
12. Use `akka-agent-harness-skills` when tools return skill-like guidance from whitelisted `src/main/resources` content.
13. Use `akka-agent-skill-governance` when tools return tenant-managed SkillDocument/SkillVersion content through `readSkill(skillId)`.
14. Do not try to use one agent as a tool for another agent.
15. Tool descriptions must state side effects, required permissions, tenant/customer scope, policy/approval gates, and audit behavior when consequential.
16. Email-sending tools must route through `akka-resend-email-service`: Resend is the only supported production email service, local/dev/test uses captured outbox behavior, and sending external email is a side-effecting capability that requires `ToolPermissionBoundary`, idempotency, approval/autonomy policy, and traces.
17. Tools must fail closed for missing AuthContext, disabled users, forbidden scopes, or cross-tenant/customer access; do not rely on prompt instructions, hidden context, or tool descriptions as authorization.
18. Skill-loading tools must check AgentSkillManifest and ToolPermissionBoundary and must not grant external tool/data permission by returning skill text.
19. For high-impact tool actions, return recommendations or approval requests unless the accepted policy grants autonomous authority.
20. Preserve the same capability semantics if the operation is also exposed through UI, HTTP/gRPC, MCP, workflow, timer, or consumer paths.

## Capability-first tool design

- Prefer read/evidence capability tools that return scoped, redacted, agent-safe data.
- Side-effecting tools must include explicit idempotency, audit, and approval/autonomy rules.
- Consequential actions should usually be proposal or approval-request tools, not commit-now tools.
- Tool input should include or derive correlation ids and idempotency keys when retries are possible.
- Tool output should distinguish validation failure, forbidden access, approval required, policy denial, external failure, and success.
- Tool tests should verify deterministic invocation, forbidden access, tenant/customer isolation, audit/trace creation, idempotency, and approval behavior where applicable.

## Repository examples

- `WeatherAgent`
  - local `@FunctionTool` for current date
  - external tool registration with `.tools(forecastTools)`
- `WeatherForecastTools`
  - public external tool method with parameter descriptions
- `RefundApprovalAgent` / `RefundProposalTools` / `RefundApprovalCapabilityTest`
  - consequential `refund.issue` capability exposed as a proposal-only tool; approval workflow or explicit bounded policy grant commits the side effect

## Review checklist

Before finishing, verify:
- tool descriptions clearly say what the tool does, what side effects it has, and which permissions/scopes/policies are required
- parameters are documented when names alone are ambiguous
- tools are explicit in the effect chain
- protected tools are tied to named capability contracts before implementation
- protected tools accept or derive AuthContext and enforce tenant/customer filtering before data access
- prompt instructions, tool descriptions, and system messages are not treated as authorization controls
- tool denials, data access, approvals, and side effects are auditable when required by the secure foundation
- side-effecting tools preserve idempotency and approval/autonomy rules
- component, MCP, or harness-skill tool cases are routed to the focused companion skill when needed
- tools that return guidance do not expose arbitrary filesystem paths or unbounded resource content
- tenant-managed skill tools route through `akka-agent-skill-governance` and enforce manifest checks
- agent-to-agent chaining is replaced with workflow orchestration when coordination is needed
