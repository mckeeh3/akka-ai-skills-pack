# Agent runtime invocation pattern

## Purpose

Use this reference when implementing a managed runtime Akka agent whose behavior is selected from tenant-scoped governed records rather than only from static Java code. For generated AI-first SaaS apps, this is **the required AI-first managed-agent invocation pattern**: do not invoke User Admin, Agent Admin, Governance, Audit/Trace, or app-specific managed agents through ad hoc static prompts or static tool registration that bypass `AgentDefinition`, compact expertise manifests, `readSkill(skillId)`, `readReferenceDoc(referenceId)`, tool boundaries, `effects().tools(runtimeTools)`, and traces.

This pattern is the implementation handoff between:
- secure SaaS `AuthContext` and capability authorization;
- active `AgentDefinition` lookup;
- governed prompt, skill, reference, and compact expertise manifest assembly;
- `ToolPermissionBoundary` enforcement for capability tools and loaders;
- Java SDK `Agent` invocation;
- `PromptAssemblyTrace`, `SkillLoadTrace`, `ReferenceLoadTrace`, and `AgentWorkTrace` emission.

## When to use

Use this pattern for agent invocations started by:
- protected HTTP/browser APIs;
- workflows or workflow steps;
- timed actions;
- consumers reacting to events;
- MCP or service boundaries;
- prompt/skill test consoles, replay, or evaluation runs.

Do not use this pattern for a one-off static example agent unless the product needs tenant-managed agent lifecycle, governed prompts/skills/references, expertise manifests, tool boundaries, or execution traces.

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
→ resolve active workstream expert bundle for the functional/internal agent
→ resolve active per-agent AgentSkillManifest compact entries with skill ids, names, descriptions, and when-to-use hints
→ resolve active per-agent AgentReferenceManifest compact entries with reference ids, summaries, when-to-consult hints, allowed use, and access notes
→ resolve ToolPermissionBoundary and model config reference
→ assemble effective system prompt with active prompt plus compact expertise manifest only; do not preload full skill/reference bodies
→ emit PromptAssemblyTrace for allowed or denied assembly, including manifest ids/checksums
→ resolve approved backend-owned Java tool bindings into `runtimeTools`
→ invoke Java Agent with assembled prompt/profile context and `effects().tools(runtimeTools)`, including `readSkill(skillId)` and `readReferenceDoc(referenceId)` when assigned
→ let Akka inject the registered tool list into the model context
→ enforce ToolPermissionBoundary for every tool/data/readSkill/readReferenceDoc request
→ authorize readSkill(skillId) against the skill manifest and skill version
→ authorize readReferenceDoc(referenceId) against the reference manifest, reference version, use mode, redaction/access rules, and customer scope
→ emit SkillLoadTrace and ReferenceLoadTrace for allowed and denied loads
→ emit AgentWorkTrace / AuditTraceEvent for invocation, tool/data/reference access, denials, result summaries, approvals, and errors
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
- workstreamExpertBundle id/version/status when the agent has workstream expertise
- AgentSkillManifest id/version and compact skill entries containing only assigned skill ids, names, descriptions, and when-to-use hints
- AgentReferenceManifest id/version and compact reference entries containing only assigned reference ids, summaries, when-to-consult hints, allowed use, and access notes
- compact expertise manifest text with separate skill and reference sections
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
| `AgentDefinition` lifecycle, authority, prompt/skill/reference/model/tool refs | Event Sourced Entity |
| `PromptDocument` lifecycle and edits | Event Sourced Entity |
| immutable `PromptVersion` snapshots | Key Value Entity or append-only projection |
| `SkillDocument` lifecycle and edits | Event Sourced Entity |
| immutable `SkillVersion` snapshots | Key Value Entity or append-only projection |
| `ReferenceDocument` lifecycle and edits | Event Sourced Entity |
| immutable `ReferenceVersion` snapshots | Key Value Entity or append-only projection |
| `AgentSkillManifest` changes | Event Sourced Entity when consequential; Key Value Entity only for intentionally simple state plus audit |
| `AgentReferenceManifest` changes | Event Sourced Entity when consequential; Key Value Entity only for intentionally simple state plus audit |
| trace facts such as `PromptAssemblyTrace`, `SkillLoadTrace`, `ReferenceLoadTrace`, `AgentWorkTrace` | Event Sourced Entity, audit event entity, or normalized trace consumer pipeline |
| admin/runtime lookup lists | Views |
| long-running supervised agent execution | Workflow |
| deadline/replay/digest execution | Timed Action |
| event-reactive invocation or trace enrichment | Consumer |
| browser/service edge | HTTP/gRPC/MCP endpoint |

## What may be service/helper code

Use ordinary application helper classes for deterministic assembly and enforcement glue:

- `AgentRuntimeResolver` for profile lookup coordination and fail-closed validation;
- `PromptAssembler` for deterministic prompt layering and checksum calculation;
- `ExpertiseManifestRenderer` for compact skill and reference manifest text;
- `ToolBoundaryEnforcer` for tool id/category/scope/mode decisions;
- `SkillReadAuthorizer` for `readSkill(skillId)` checks;
- `ReferenceReadAuthorizer` for `readReferenceDoc(referenceId)` checks;
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
7. Compact expertise manifest is rendered with only skills and references assigned to that agent; full skill/reference text is not preloaded.
8. `readSkill(skillId)` and, when references are assigned, `readReferenceDoc(referenceId)` are registered as normal Akka `@FunctionTool` loaders, and Akka injects them with the rest of the allowed tool list.
9. `ToolPermissionBoundary` is present, includes explicit `read_skill` and `read_reference` grants as applicable, and defaults to deny.
10. Runtime tool availability is resolved from active managed configuration to backend-owned Java bindings and passed to the Agent with `effects().tools(runtimeTools)`; tenant-managed records store stable tool ids/capability ids, not arbitrary Java class names.
11. `ModelConfigRef` is allowed by tenant/agent/model policy and exposes no provider secrets.
12. Correlation/work trace ids are available.
13. `PromptAssemblyTrace` has been recorded or scheduled for durable recording with expert-bundle and manifest references.

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

## `readReferenceDoc(referenceId)` runtime authorization

`readReferenceDoc(referenceId)` is a governed capability for loading factual/process workstream knowledge. It is not a filesystem read, URL fetch, arbitrary document search, or authority grant.

Required checks:
- tenant, selected customer where applicable, and `AuthContext` match the active invocation;
- active `AgentDefinition` and workstream expert bundle match the invocation;
- active `AgentReferenceManifest` includes the requested reference id;
- `ReferenceDocument` belongs to the tenant and selected customer scope when scoped;
- requested `ReferenceVersion` is approved/active, unless authorized test/replay/evaluation mode permits another version;
- caller mode and allowed use (`cite`, `consult`, `evidence`, `internal_context`) are permitted;
- `ToolPermissionBoundary` grants the `read_reference` loader separately from `read_skill`;
- returned content satisfies redaction/access rules, token/size limits, and secret-like content checks.

Allowed and denied reads must emit `ReferenceLoadTrace` or a generalized `DocumentLoadTrace(documentKind=reference)`. A denied read returns a safe non-enumerating denial message to the model and must not leak cross-tenant or wrong-customer reference existence.

## Tool boundary enforcement

Every registered local tool, component tool, MCP tool, data access helper, or guidance-loading tool must be checked against `ToolPermissionBoundary` before execution. For generated SaaS managed agents, tool registration is part of runtime resolution: construct request-scoped tool objects from backend-owned registry bindings and pass them to the Java Agent with `effects().tools(runtimeTools)`.

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
- active prompt assembly with compact expertise manifest entries only, separated into skill and reference sections;
- draft prompt/skill/reference allowed only in authorized test/replay/evaluation mode;
- `PromptAssemblyTrace` creation for allowed and denied assembly with manifest refs;
- `readSkill(skillId)` allowed for assigned active skill;
- unassigned, inactive, cross-tenant, oversized, or unauthorized `readSkill` denial with `SkillLoadTrace`;
- `readReferenceDoc(referenceId)` allowed for assigned active reference;
- unassigned, inactive, cross-tenant, wrong-customer, redaction-denied, oversized, or unauthorized `readReferenceDoc` denial with `ReferenceLoadTrace`;
- `ToolPermissionBoundary` allowed/denied tool invocation, including missing `read_skill` and `read_reference` grants;
- side-effecting tool approval-required behavior;
- skill/reference text cannot expand tools, roles, tenant/customer scope, approval authority, or backend capabilities;
- `AgentWorkTrace` correlation across prompt assembly, skill/reference load, tool call, and result summary;
- provider secret and frontend secret-boundary checks for `ModelConfigRef`.

## Implementation routing

After using this pattern, load focused skills by the next implementation slice:
- `akka-agent-behavior-profiles` for `AgentDefinition` and runtime profile lookup;
- `akka-agent-prompt-governance` for `PromptDocument`, `PromptVersion`, and prompt assembly;
- `akka-agent-skill-governance` for `AgentSkillManifest`, `readSkill(skillId)`, and `SkillLoadTrace`;
- `akka-agent-reference-governance` for `ReferenceDocument`, `AgentReferenceManifest`, `readReferenceDoc(referenceId)`, and `ReferenceLoadTrace`;
- `akka-agent-seed-documents` for seeded expert bundles, prompts, skills, references, manifests, and boundaries;
- `akka-agent-work-trace` for trace records and timeline/search surfaces;
- `akka-agent-tools` and tool-boundary guidance for local/component/MCP/readSkill/readReferenceDoc tools;
- `akka-agent-component` only after the governed runtime profile contract is clear;
- `akka-workflows`, `akka-http-endpoints`, `akka-timed-actions`, or `akka-consumers` for the selected invocation surface.
