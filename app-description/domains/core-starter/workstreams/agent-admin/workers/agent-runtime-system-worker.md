# Worker: Agent Runtime System Worker

## Responsibility

Backend runtime resolver/loader worker that prepares managed-agent invocation state and enforces global/tenant-scoped profile, model, skill/reference, generated-tool assignment, and tool-boundary rules before model calls, loader-tool calls, or generated tool calls.

## Execution harness and adapters

- Internal runtime resolver.
- `internal_call` for active profile lookup, tenant-specific override resolution, and prompt assembly.
- Governed loader tools `readSkill(skillId)` and `readReferenceDoc(referenceId)` for model-requested content.
- Generated tool dispatch only when the resolved behavior profile allows the generated tool and backend authorization/tool-boundary checks pass.

## Required checks

Resolve the tenant-specific active behavior profile when present, otherwise the global active behavior profile, then resolve `AgentDefinition`, lifecycle status, authority level, current prompt version, compact `AgentSkillManifest`, compact `AgentReferenceManifest`, active `ModelConfigRef`/model policy, selected `AuthContext`, allowed generated tool list, and `ToolPermissionBoundary`. Deny disabled/archived agents, missing/inactive docs, unassigned skill/reference ids, unassigned generated tool ids, cross-scope access, missing provider/model config, and denied tool-boundary grants before model-visible content or tool execution.

## Traces

Emits profile-resolution trace facts, `PromptAssemblyTrace`, `SkillLoadTrace`, reference-load trace facts, generated-tool assignment decision trace facts, `AgentWorkTrace`, and denial categories without exposing provider secrets, generated tool internals, or unauthorized document bodies.
