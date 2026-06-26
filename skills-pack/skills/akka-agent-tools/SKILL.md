---
name: akka-agent-tools
description: Implement Akka Java SDK agent function tools using @FunctionTool and external tool classes. Use companion skills for Akka component tools, remote MCP tool registration, or harness-like skill loading through tools.
---

# Akka Agent Tools

Use this skill when an agent must call local or external function tools as selected exposure surfaces for governed backend capabilities.

## Worker/tool/capability alignment

For generated AI-first SaaS app work, treat the agent runtime, autonomous task loop, or governed artifact in scope as a software-worker harness concern, not as the product operation or authorization boundary. Keep the chain explicit:

```text
software worker
→ Akka Agent/AutonomousAgent harness or focused governance artifact
→ actor adapter (`agent_tool_call`, `human_chat_tool_plan`, workflow/timer/consumer/API/MCP/internal adapter as applicable)
→ governed tool
→ backend capability
→ Akka/frontend implementation
```

Human surface availability, prompt/skill/reference text, model output, task instructions, and Akka tool registration do not grant tool authority. A model-facing tool, loader, or autonomous task action may be exposed only when the active workstream tool catalog, governed tool contract, backend `AuthContext`, and `ToolPermissionBoundary` explicitly allow that actor adapter; denials and approval-required paths must fail closed and be traced.


## Required reading

Read these first if present:
- `../docs/app-worker-tool-model.md`
- `../docs/app-description-to-code-compile-contract.md`
- `../docs/capability-first-backend-architecture.md`
- `akka-context/sdk/agents/extending.html.md`
- `akka-context/sdk/agents/failures.html.md`

If the main task is not local or external tool classes, load the focused companion skill instead:
- `akka-agent-tool-boundaries` for backend-enforced ToolPermissionBoundary grants, tool registry/catalog, denied-tool semantics, approval-required expansion, and tool invocation traces
- `akka-agent-component-tools`
- `akka-agent-mcp-tools`
- `akka-agent-harness-skills` for model-loadable internal guidance backed by packaged resources
- `akka-agent-skill-governance` for tenant-managed, versioned, audited `readSkill(skillId)` guidance tools
- `akka-agent-reference-governance` for tenant/customer-managed, versioned, audited `readReferenceDoc(referenceId)` reference/evidence tools
- `akka-resend-email-service` when a managed agent needs a governed Resend-backed email preview/send tool via `@FunctionTool`

## Use this pattern when

In generated SaaS apps, every tool that reads protected data or performs side effects must be compiled from the current app-description contract:

```text
software worker
→ agent runtime harness
→ agent_tool_call actor adapter
→ governed tool
→ capability
→ Akka implementation
```

The governed tool must receive or resolve AuthContext, enforce tenant/customer scope and permission/capability checks, apply policy/approval rules, and create required audit/work-trace records before returning data or committing actions.

Treat local/external Akka function tools as **AI-backed agent-tool adapters** for governed workstream tools. The governed tool id and capability contract own business authority; `@FunctionTool` naming, descriptions, prompt text, and loaded skills only help the model request the operation. If the same operation is also exposed to humans through a surface action or confirmed `human_chat_tool_plan`, reuse the same governed tool id and implement adapter-specific input mediation, confirmation/approval UX, trace source, and result/partial-failure reporting.

- a named governed tool/capability is intentionally exposed to the agent as a local or external function tool
- the model needs live or computed data to answer correctly
- the model should choose between multiple helper functions
- agent output depends on current date, lookup services, or component calls
- tool descriptions materially affect model behavior but do not enforce authorization
- an agent should approximate harness skill loading by exposing approved guidance blocks as tools
- a managed agent should load tenant-managed governed skills through `readSkill(skillId)` with per-agent manifest authorization; this is the required skill-loading tool pattern for generated managed agents
- a managed agent should load tenant/customer-governed workstream references through `readReferenceDoc(referenceId)` with per-agent reference manifest authorization, redaction checks, and ReferenceLoadTrace

## Core pattern

1. Start from the compile contract: responsible software worker, agent harness, `agent_tool_call` actor adapter, governed tool id, capability id, selected Akka implementation path, trace source, and required tests.
2. Start from the capability contract: id/name, purpose, actor/caller, AuthContext, schemas, data access, side effects, idempotency, policy/approval, audit/trace, and tests.
3. Expose only governed tools the agent is allowed to request; do not register helper methods or raw component methods merely because they exist.
4. Resolve the active workstream tool catalog and treat tool availability as the intersection of the caller's AuthContext, the selected workstream, the managed agent definition/profile, the `ToolPermissionBoundary`, and the tool's policy/approval state.
5. For managed agents or protected tools, load `akka-agent-tool-boundaries` and enforce the active `ToolPermissionBoundary` and tool registry/catalog before tool execution.
6. Annotate tools with `@FunctionTool`.
7. Add `@Description` to parameters when the model needs argument hints.
8. Register external tool classes with `.tools(instance)` or `.tools(Class)`.
9. Agent-local `@FunctionTool` methods are automatically available.
10. Keep tool behavior deterministic and fast.
11. Use a non-component tool facade when one governed tool should hide several component calls, combine component results, enforce capability policy/scope, redact data, or return a model-friendly computed result.
12. Handle tool failures with `.onFailure(...)` in the agent, returning safe denial/failure shapes.
13. Use `akka-agent-component-tools` for `.tools(ComponentClass.class)` only when a component method is intentionally exposed as the `agent_tool_call` adapter for a governed tool; the component method is still not the product contract.
14. Use `akka-agent-mcp-tools` for `.mcpTools(...)`.
15. Use `akka-agent-harness-skills` when tools return skill-like guidance from whitelisted `src/main/resources` content.
16. Use `akka-agent-skill-governance` when tools return tenant-managed SkillDocument/SkillVersion content through `readSkill(skillId)`; every generated-app managed Agent and AutonomousAgent must register this as a normal Akka `@FunctionTool` so Akka injects it with the rest of the allowed tool list where supported. Empty or unassigned skill manifests deny safely and trace.
17. Do not try to use one agent as a tool for another agent.
18. Tool descriptions must state side effects, required permissions, tenant/customer scope, policy/approval gates, and audit behavior when consequential.
19. Email-sending tools must route through `akka-resend-email-service`: Resend is the only supported production email service, local/dev/test uses captured outbox behavior, and sending external email is a side-effecting capability that requires `ToolPermissionBoundary`, idempotency, approval/autonomy policy, and traces.
20. Tools must fail closed for missing AuthContext, disabled users, forbidden scopes, or cross-tenant/customer access; do not rely on prompt instructions, hidden context, or tool descriptions as authorization.
21. Skill-loading tools must check the active agent-specific AgentSkillManifest and ToolPermissionBoundary and must not grant external tool/data permission by returning skill text.
22. Reference-loading tools must check the active AgentReferenceManifest, tenant/customer scope, redaction/access limits, token limits, and a separate ToolPermissionBoundary grant such as `read_reference`; a `read_skill` grant must not imply reference access.
23. For high-impact tool actions, return recommendations or approval requests unless the accepted policy grants autonomous authority.
24. Preserve the same capability semantics if the operation is also exposed through UI, confirmed human chat tool plans, HTTP/gRPC, MCP, workflow, timer, or consumer paths.

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
- keep the tool class a focused governed-tool adapter/facade, not a generic service locator;
- annotate only the public facade methods that are safe model-selectable operations for the declared `agent_tool_call` adapter;
- do not expose raw component ids, endpoint paths, tool names, URLs, or internal method choices as free-form model authority;
- prefer View calls for evidence, then entity/workflow calls for scoped current state or supervised actions;
- for side effects, derive idempotency keys/correlation ids and return `approval_required` unless policy grants bounded autonomy;
- register the facade instance with `.tools(instance)` when it needs request-scoped context or injected Akka services;
- register `.tools(Class)` only when dependencies can be supplied safely by the service `DependencyProvider` and no request-scoped context is required;
- component methods called internally through `ComponentClient` do not need `@FunctionTool`; annotate an underlying component method only when it is also intentionally exposed directly to the model as a component tool.

Canonical example:
- a domain-specific attention-advisor agent injects `ComponentClient` and registers a domain-specific tool facade instance.
- a domain-specific read-only attention-advice facade tool is a read-only `workstream event.attention-advice` facade that calls a domain-specific curated component read method and a domain-specific view query method, applies deterministic readiness logic, and returns a curated domain-specific advice result.

## Capability-first tool design

- Prefer read/evidence capability tools that return scoped, redacted, agent-safe data.
- Side-effecting tools must include explicit idempotency, audit, and approval/autonomy rules.
- Consequential actions should usually be proposal or approval-request tools, not commit-now tools.
- Tool input should include or derive correlation ids and idempotency keys when retries are possible; do not trust model-supplied tenant/customer ids or confirmation ids without backend lookup.
- Tool output should distinguish validation failure, forbidden access, approval required, policy denial, external failure, partial failure, and success.
- Tool tests should verify deterministic invocation, forbidden access, tenant/customer isolation, audit/trace creation, idempotency, and approval behavior where applicable, including AI-backed `agent_tool_call` traces and any human-requested `requestedBy` relationship.

## Repository examples

- `UserAdminAccessReviewAutonomousAgent`
  - local `@FunctionTool` for current date
  - external tool registration with `.tools(forecastTools)`
- `UserAdminEvidenceTools`
  - public external tool method with parameter descriptions
- a domain-specific approval agent / domain-specific proposal tools / a domain-specific approval capability test
  - consequential `refund.issue` capability exposed as a proposal-only tool; approval workflow or explicit bounded policy grant commits the side effect
- a domain-specific attention-advisor agent / domain-specific attention-advisor tools / a domain-specific attention-advisor agent test
  - non-component `workstream event.attention-advice` facade tool backed by `ComponentClient`, combining entity and view evidence plus deterministic processing into one model-facing tool

## Review checklist

Before finishing, verify:
- the compile chain is explicit: software worker, agent harness, `agent_tool_call` adapter, governed tool, capability, Akka implementation path, trace, and tests
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
