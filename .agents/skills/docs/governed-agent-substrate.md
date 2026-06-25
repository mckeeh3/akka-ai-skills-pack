# Governed agent substrate

Use this as the shared substrate reference for managed Akka workstream agents. Focused governed-agent skills should describe only their unique slice and link here for the common foundation.

## Core records

A managed model-backed workstream agent is not just a Java `Agent` class, and it is not itself the business operation it helps perform. It is the combination of a software worker, an Akka execution harness, actor-adapter exposure rules, tenant/customer-scoped durable records, and runtime assembly:

- `AgentDefinition` for identity, lifecycle, owner/steward, authority level, workstream placement, and model policy references;
- `PromptDocument` / `PromptVersion` for reviewed prompt text and activation state;
- `SkillDocument` / `SkillVersion` and `ReferenceDocument` / `ReferenceVersion` for governed behavior and knowledge artifacts;
- `AgentSkillManifest` and `AgentReferenceManifest` for compact approved expertise context and exact version bindings;
- `ToolPermissionBoundary` for allowed tools, scopes, side effects, approval requirements, and denied-load behavior;
- `ModelConfigRef` or equivalent model policy binding that keeps provider secrets outside browser and tenant-authored content;
- seed/default document provenance for first install and tenant bootstrap.

## Runtime assembly

Before invoking a model-backed functional agent, keep the worker/tool/capability chain explicit:

```text
software worker
→ Akka Agent/AutonomousAgent harness
→ actor adapter (`agent_tool_call`, confirmed `human_chat_tool_plan`, workflow/timer/consumer/API/MCP/internal adapter as applicable)
→ governed tool
→ backend capability
→ Akka/frontend implementation
```

Resolve the active `AgentDefinition`, model policy, prompt, manifests, tool boundary, AuthContext, and selected workstream/capability scope. Assemble compact prompt context and expose only authorized loader tools such as `readSkill(skillId)` and `readReferenceDoc(referenceId)`.

Normal model-backed workstream turns must invoke the concrete Akka `Agent`/`AutonomousAgent` runtime with registered governed tools and active provider configuration. Missing provider, model policy, security, loader-tool, or tool-boundary configuration fails closed with an actionable user-safe surface and trace; deterministic/model-less behavior is allowed only in explicit tests or fixture adapters, not as the normal user-facing runtime path.

Prompt/skill/reference content is behavior guidance only. It must not grant authority, expand data scope, enable tools, bypass approval, or change provider secret access. Tool and data authority comes from backend authorization plus `ToolPermissionBoundary` enforcement.

## Trace requirements

Every governed runtime path should create durable trace facts sufficient to explain:

- selected agent definition, prompt version, skill/reference versions, model config, and tool boundary;
- prompt assembly decisions and redactions;
- skill/reference load attempts, denials, and version ids;
- tool registrations, tool calls, policy/approval decisions, and data-access scope;
- provider invocation/failure, safe user-visible failure surface, and correlation ids.

Common trace names include `PromptAssemblyTrace`, `SkillLoadTrace`, `ReferenceLoadTrace`, and `AgentWorkTrace`. Use app-specific names only when they preserve these facts.

## Focused skill ownership

- Behavior profile skills own `AgentDefinition` lifecycle and authority shape.
- Prompt, skill, and reference governance skills own document/version review, activation, history, diff, loader contracts, and denials.
- Model governance owns model policy and provider secret boundaries.
- Tool-boundary skills own tool authorization, approval, data/scope enforcement, and side-effect denials.
- Seed-document skills own idempotent default content installation and upgrade provenance.
- Work-trace skills own investigation-grade trace timelines and UI/query surfaces.
- Behavior-editing and closed-loop improvement skills own proposal, review, simulation/evidence, activation, rollback, and audit flows.
