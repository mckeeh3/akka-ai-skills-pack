---
name: akka-agent-tools
description: Implement Akka Java SDK agent function tools using @FunctionTool and external tool classes. Use companion skills for Akka component tools, remote MCP tool registration, or harness-like skill loading through tools.
---

# Akka Agent Tools

Use this skill when an agent must call local or external function tools as selected exposure surfaces for governed backend capabilities.

## Required reading

Read these first if present:
- `../docs/capability-first-backend-architecture.md`
- `akka-context/sdk/agents/extending.html.md`
- `akka-context/sdk/agents/failures.html.md`
- `../examples/akka-components/src/main/java/ai/first/application/WeatherAgent.java`
- `../examples/akka-components/src/main/java/ai/first/application/WeatherForecastTools.java`
- `../examples/akka-components/src/main/java/ai/first/application/CartCheckoutAdvisorAgent.java`
- `../examples/akka-components/src/main/java/ai/first/application/CartCheckoutAdvisorTools.java`
- `../examples/akka-components/src/test/java/ai/first/application/CartCheckoutAdvisorAgentTest.java`

If the main task is not local or external tool classes, load the focused companion skill instead:
- `akka-agent-tool-boundaries` for backend-enforced ToolPermissionBoundary grants, tool registry/catalog, denied-tool semantics, approval-required expansion, and tool invocation traces
- `akka-agent-component-tools`
- `akka-agent-mcp-tools`
- `akka-agent-harness-skills` for model-loadable internal guidance backed by packaged resources
- `akka-agent-skill-governance` for tenant-managed, versioned, audited `readSkill(skillId)` guidance tools
- `akka-agent-reference-governance` for tenant/customer-managed, versioned, audited `readReferenceDoc(referenceId)` reference/evidence tools
- `akka-resend-email-service` when a managed agent needs a governed Resend-backed email preview/send tool via `@FunctionTool`

## Use this pattern when

In generated SaaS apps, every tool that reads protected data or performs side effects must expose a named capability contract. The tool must receive or resolve AuthContext, enforce tenant/customer scope and permission/capability checks, apply policy/approval rules, and create required audit/work-trace records before returning data or committing actions.

- a named capability is intentionally exposed to the agent as a local or external function tool
- the model needs live or computed data to answer correctly
- the model should choose between multiple helper functions
- agent output depends on current date, lookup services, or component calls
- tool descriptions materially affect model behavior but do not enforce authorization
- an agent should approximate harness skill loading by exposing approved guidance blocks as tools
- a managed agent should load tenant-managed governed skills through `readSkill(skillId)` with per-agent manifest authorization; this is the required skill-loading tool pattern for generated managed agents
- a managed agent should load tenant/customer-governed workstream references through `readReferenceDoc(referenceId)` with per-agent reference manifest authorization, redaction checks, and ReferenceLoadTrace

## Core pattern

1. Start from the capability contract: id/name, purpose, actor/caller, AuthContext, schemas, data access, side effects, idempotency, policy/approval, audit/trace, and tests.
2. Expose only capabilities the agent is allowed to request; do not register helper methods merely because they exist.
3. For managed agents or protected tools, load `akka-agent-tool-boundaries` and enforce the active `ToolPermissionBoundary` and tool registry/catalog before tool execution.
4. Annotate tools with `@FunctionTool`.
5. Add `@Description` to parameters when the model needs argument hints.
6. Register external tool classes with `.tools(instance)` or `.tools(Class)`.
7. Agent-local `@FunctionTool` methods are automatically available.
8. Keep tool behavior deterministic and fast.
9. Use a non-component tool facade when one tool should hide several component calls, combine component results, enforce capability policy/scope, redact data, or return a model-friendly computed result.
10. Handle tool failures with `.onFailure(...)` in the agent, returning safe denial/failure shapes.
11. Use `akka-agent-component-tools` for `.tools(ComponentClass.class)`.
12. Use `akka-agent-mcp-tools` for `.mcpTools(...)`.
13. Use `akka-agent-harness-skills` when tools return skill-like guidance from whitelisted `src/main/resources` content.
14. Use `akka-agent-skill-governance` when tools return tenant-managed SkillDocument/SkillVersion content through `readSkill(skillId)`; managed agents must register this as a normal Akka `@FunctionTool` so Akka injects it with the rest of the allowed tool list.
15. Do not try to use one agent as a tool for another agent.
16. Tool descriptions must state side effects, required permissions, tenant/customer scope, policy/approval gates, and audit behavior when consequential.
17. Email-sending tools must route through `akka-resend-email-service`: Resend is the only supported production email service, local/dev/test uses captured outbox behavior, and sending external email is a side-effecting capability that requires `ToolPermissionBoundary`, idempotency, approval/autonomy policy, and traces.
18. Tools must fail closed for missing AuthContext, disabled users, forbidden scopes, or cross-tenant/customer access; do not rely on prompt instructions, hidden context, or tool descriptions as authorization.
19. Skill-loading tools must check the active agent-specific AgentSkillManifest and ToolPermissionBoundary and must not grant external tool/data permission by returning skill text.
20. Reference-loading tools must check the active AgentReferenceManifest, tenant/customer scope, redaction/access limits, token limits, and a separate ToolPermissionBoundary grant such as `read_reference`; a `read_skill` grant must not imply reference access.
21. For high-impact tool actions, return recommendations or approval requests unless the accepted policy grants autonomous authority.
21. Preserve the same capability semantics if the operation is also exposed through UI, HTTP/gRPC, MCP, workflow, timer, or consumer paths.

## Tool pattern decision matrix

| Need | Prefer |
|---|---|
| Simple helper such as current date, formatting, or deterministic calculation | Agent-local `@FunctionTool` method |
| Reusable plain Java service/API wrapper with no Akka component orchestration | External tool class registered with `.tools(instance)` or `.tools(Class)` |
| One model-facing capability must call multiple Akka components, hide component layout, apply policy/redaction/scoring, or return a computed DTO | Non-component `ComponentClient`-backed tool facade |
| The agent should directly call one selected View/entity/workflow command or query already shaped as a capability surface | Component tool via `.tools(ComponentClass.class)` and `akka-agent-component-tools` |
| The tool is hosted by another service or third-party AI/tool boundary | Remote MCP tool via `.mcpTools(...)` and `akka-agent-mcp-tools` |
| The agent loads approved procedural guidance | Governed `readSkill(skillId)` or packaged guidance tools via the skill-governance/harness-skills companions |
| The agent loads approved workstream reference knowledge or evidence | Governed `readReferenceDoc(referenceId)` via `akka-agent-reference-governance` |

## Non-component tool facade pattern

Use this pattern when a single agent tool should represent a capability that is broader or safer than any one component method.

Prefer a non-component facade over a direct component tool when:
- the tool must call more than one Akka component through `ComponentClient`;
- the tool needs processing, ranking, summarization, scoring, policy checks, redaction, or DTO shaping before returning data to the model;
- the underlying component methods are internal, too low-level, or should not appear as model-selectable tools;
- the capability should keep a stable tool contract even if the component layout changes;
- authorization, tenant/customer scope, correlation ids, or idempotency keys should be resolved outside model-supplied arguments.

Implementation shape:

```text
Agent command handler
→ resolve request AuthContext/correlation/mode
→ construct or inject CapabilityTools(ComponentClient, AuthContext, policy services, trace sink)
→ effects().tools(capabilityTools)
→ model calls one stable @FunctionTool method
→ tool facade calls View/entity/workflow/component methods through ComponentClient
→ tool facade enforces scope/policy/idempotency/redaction and returns a curated result
```

Rules:
- keep the tool class a focused capability facade, not a generic service locator;
- annotate only the public facade methods that are safe model-selectable operations;
- do not expose raw component ids, tool names, URLs, or internal method choices as free-form model authority;
- prefer View calls for evidence, then entity/workflow calls for scoped current state or supervised actions;
- for side effects, derive idempotency keys/correlation ids and return `approval_required` unless policy grants bounded autonomy;
- register the facade instance with `.tools(instance)` when it needs request-scoped context or injected Akka services;
- register `.tools(Class)` only when dependencies can be supplied safely by the service `DependencyProvider` and no request-scoped context is required;
- component methods called internally through `ComponentClient` do not need `@FunctionTool`; annotate an underlying component method only when it is also intentionally exposed directly to the model as a component tool.

Canonical example:
- `CartCheckoutAdvisorAgent` injects `ComponentClient` and registers `new CartCheckoutAdvisorTools(componentClient)`.
- `CartCheckoutAdvisorTools#adviseCheckout` is a read-only `cart.checkout-advice` facade that calls `ShoppingCartEntity#inspectCartSummary` and `ShoppingCartsByCheckedOutView#getCarts`, applies deterministic readiness logic, and returns a curated `CartCheckoutAdvice` result.

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
- `CartCheckoutAdvisorAgent` / `CartCheckoutAdvisorTools` / `CartCheckoutAdvisorAgentTest`
  - non-component `cart.checkout-advice` facade tool backed by `ComponentClient`, combining entity and view evidence plus deterministic processing into one model-facing tool

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
- tools that return guidance or references do not expose arbitrary filesystem paths, URLs, classpath resource names, or unbounded resource content
- tenant-managed skill tools route through `akka-agent-skill-governance` and enforce manifest checks
- tenant/customer-managed reference tools route through `akka-agent-reference-governance`, enforce reference manifest/redaction/boundary checks, and emit ReferenceLoadTrace
- agent-to-agent chaining is replaced with workflow orchestration when coordination is needed
