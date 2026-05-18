# Agent runtime invocation pattern

## Purpose

Use this reference when implementing a managed runtime Akka agent whose behavior is selected from tenant-scoped governed records rather than only from static Java code.

This pattern is the implementation handoff between:
- secure SaaS `AuthContext` and capability authorization;
- active `AgentDefinition` lookup;
- governed prompt and skill assembly;
- `ToolPermissionBoundary` enforcement;
- Java SDK `Agent` invocation;
- `PromptAssemblyTrace`, `SkillLoadTrace`, and `AgentWorkTrace` emission.

## When to use

Use this pattern for agent invocations started by:
- protected HTTP/browser APIs;
- workflows or workflow steps;
- timed actions;
- consumers reacting to events;
- MCP or service boundaries;
- prompt/skill test consoles, replay, or evaluation runs.

Do not use this pattern for a one-off static example agent unless the product needs tenant-managed agent lifecycle, governed prompts/skills, manifests, tool boundaries, or execution traces.

## Runtime sequence

```text
request from HTTP/workflow/timer/consumer/test console
→ resolve caller and selected AuthContext
→ authorize the requested backend capability
→ create or propagate correlationId / workTraceId
→ AgentRuntimeResolver.resolve(agentDefinitionId, AuthContext, mode)
→ load active tenant-scoped AgentDefinition
→ reject disabled, archived, draft-in-runtime, cross-tenant, or unauthorized agents
→ resolve active PromptDocument/PromptVersion refs
→ resolve active AgentSkillManifest compact entries
→ resolve ToolPermissionBoundary and model config reference
→ assemble effective system prompt with compact manifest only
→ emit PromptAssemblyTrace for allowed or denied assembly
→ invoke Java Agent with assembled prompt/profile context
→ enforce ToolPermissionBoundary for every tool/data/readSkill request
→ authorize readSkill(skillId) against manifest and skill version
→ emit SkillLoadTrace for allowed and denied skill loads
→ emit AgentWorkTrace / AuditTraceEvent for invocation, tool/data access, denials, result summaries, approvals, and errors
→ return redacted response or safe denial to the caller
```

## `AgentRuntimeResolver` responsibility

Use an application service/helper such as `AgentRuntimeResolver` for orchestration that is not itself an Akka component command handler.

Recommended input:

```text
ResolveAgentRuntimeRequest
- tenantId
- selected customerId when applicable
- accountId or runtime actor id
- agentDefinitionId
- capabilityId
- mode: runtime | test | replay | evaluation
- correlationId / workTraceId
- requested prompt/skill overrides only when mode permits them
```

Recommended output:

```text
ResolvedAgentRuntime
- AuthContext
- AgentDefinition id/version/status/authority level
- PromptDocument/PromptVersion refs and assembled prompt text
- AgentSkillManifest id/version and compact manifest text
- ToolPermissionBoundary snapshot
- ModelConfigRef
- trace ids/checksums
- allowed mode and authorization basis summary
```

The resolver should fail closed with a safe denial result and trace emission when any prerequisite is invalid.

## What should be Akka components

Prefer Akka components for durable governed state and query models:

| Concern | Preferred carrier |
|---|---|
| `AgentDefinition` lifecycle, authority, prompt/skill/model/tool refs | Event Sourced Entity |
| `PromptDocument` lifecycle and edits | Event Sourced Entity |
| immutable `PromptVersion` snapshots | Key Value Entity or append-only projection |
| `SkillDocument` lifecycle and edits | Event Sourced Entity |
| immutable `SkillVersion` snapshots | Key Value Entity or append-only projection |
| `AgentSkillManifest` changes | Event Sourced Entity when consequential; Key Value Entity only for intentionally simple state plus audit |
| trace facts such as `PromptAssemblyTrace`, `SkillLoadTrace`, `AgentWorkTrace` | Event Sourced Entity, audit event entity, or normalized trace consumer pipeline |
| admin/runtime lookup lists | Views |
| long-running supervised agent execution | Workflow |
| deadline/replay/digest execution | Timed Action |
| event-reactive invocation or trace enrichment | Consumer |
| browser/service edge | HTTP/gRPC/MCP endpoint |

## What may be service/helper code

Use ordinary application helper classes for deterministic assembly and enforcement glue:

- `AgentRuntimeResolver` for profile lookup coordination and fail-closed validation;
- `PromptAssembler` for deterministic prompt layering and checksum calculation;
- `SkillManifestRenderer` for compact manifest text;
- `ToolBoundaryEnforcer` for tool id/category/scope/mode decisions;
- `SkillReadAuthorizer` for `readSkill(skillId)` checks;
- `TraceEmitter` or component clients that persist trace records;
- redaction and safe-summary helpers.

These helpers must not become hidden authorization stores. They should consume Akka-owned state, `AuthContext`, policies, and capability grants, then call components or trace sinks for durable effects.

## Checks before model invocation

Before invoking the Java `Agent`, verify:

1. Authenticated caller or trusted runtime actor is present.
2. Selected `AuthContext` has active tenant/customer membership where required.
3. Caller/runtime actor may invoke the capability and selected agent.
4. `AgentDefinition` belongs to the tenant and is active for runtime mode.
5. Draft or unapproved prompt/skill versions are used only in authorized test/replay/evaluation modes.
6. Active prompt version exists and passes checksum/secret-boundary checks.
7. Compact `AgentSkillManifest` is rendered; full skill text is not preloaded.
8. `ToolPermissionBoundary` is present and defaults to deny.
9. `ModelConfigRef` is allowed by tenant/agent/model policy and exposes no provider secrets.
10. Correlation/work trace ids are available.
11. `PromptAssemblyTrace` has been recorded or scheduled for durable recording.

If any check fails, deny before model invocation and emit an audit/work trace event.

## `readSkill(skillId)` runtime authorization

`readSkill(skillId)` is a governed capability, not a filesystem read and not an authority grant.

Required checks:
- tenant and `AuthContext` match the active invocation;
- active `AgentDefinition` matches the invocation;
- active `AgentSkillManifest` includes the requested skill id;
- `SkillDocument` belongs to the tenant;
- requested `SkillVersion` is approved/active, unless authorized test/replay mode permits another version;
- caller mode is allowed;
- returned content is under token/size limits and contains no secret-like data.

Allowed and denied reads must emit `SkillLoadTrace`. A denied read returns a safe denial message to the model and must not leak whether a cross-tenant skill exists.

## Tool boundary enforcement

Every registered local tool, component tool, MCP tool, data access helper, or guidance-loading tool must be checked against `ToolPermissionBoundary` before execution.

At minimum enforce:
- tool id and category allowlist;
- read-only vs side-effecting classification;
- tenant/customer scope;
- required capability/role;
- mode: runtime, test, replay, evaluation;
- approval requirement for consequential side effects;
- denial trace emission.

Tool descriptions and prompt text can explain boundaries to the model, but backend checks enforce them.

## Invocation surfaces

### HTTP/test console

Endpoint code should authenticate, resolve request context/JWT, select `AuthContext`, authorize the route capability, create a correlation id, then call `AgentRuntimeResolver`. The endpoint should not assemble prompts or bypass tool-boundary checks inline.

### Workflow

Workflow state should carry tenant/customer scope, capability id, agentDefinitionId, authorization basis, correlation/workTrace ids, approval state, and idempotency keys. Workflow steps call the resolver before agent invocation and reauthorize after pauses when needed.

### Timer or consumer

Timed actions and consumers must carry provenance and authority basis from the scheduled/event source. They must not invoke agents with platform-wide authority unless a product policy explicitly grants that authority and traces it.

### Replay/evaluation

Replay/evaluation mode may use pinned historical prompt/skill/model refs. It must be clearly mode-labeled, authorized, side-effect safe by default, and traceable.

## Tests to plan

Plan tests for:
- active profile resolution success;
- disabled/archived/draft agent denial in runtime mode;
- cross-tenant agent denial;
- missing permission/capability denial;
- active prompt assembly with compact `AgentSkillManifest` only;
- draft prompt allowed only in test/replay mode;
- `PromptAssemblyTrace` creation for allowed and denied assembly;
- `readSkill(skillId)` allowed for assigned active skill;
- unassigned, inactive, cross-tenant, oversized, or unauthorized `readSkill` denial with `SkillLoadTrace`;
- `ToolPermissionBoundary` allowed/denied tool invocation;
- side-effecting tool approval-required behavior;
- `AgentWorkTrace` correlation across prompt assembly, skill load, tool call, and result summary;
- provider secret and frontend secret-boundary checks for `ModelConfigRef`.

## Implementation routing

After using this pattern, load focused skills by the next implementation slice:
- `akka-agent-behavior-profiles` for `AgentDefinition` and runtime profile lookup;
- `akka-agent-prompt-governance` for `PromptDocument`, `PromptVersion`, and prompt assembly;
- `akka-agent-skill-governance` for `AgentSkillManifest`, `readSkill(skillId)`, and `SkillLoadTrace`;
- `akka-agent-work-trace` for trace records and timeline/search surfaces;
- `akka-agent-tools` and tool-boundary guidance for local/component/MCP tools;
- `akka-agent-component` only after the governed runtime profile contract is clear;
- `akka-workflows`, `akka-http-endpoints`, `akka-timed-actions`, or `akka-consumers` for the selected invocation surface.
