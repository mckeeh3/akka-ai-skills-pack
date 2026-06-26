# Worker: Agent Runtime System Worker

## Responsibility

Backend runtime resolver/loader worker that prepares managed-agent invocation state and enforces profile, model, skill/reference, and tool-boundary rules before model calls or loader-tool calls.

## Execution harness and adapters

- Internal runtime resolver.
- `internal_call` for active profile lookup and prompt assembly.
- Governed loader tools `readSkill(skillId)` and `readReferenceDoc(referenceId)` for model-requested content.

## Required checks

Resolve active `AgentDefinition`, lifecycle status, authority level, current prompt version, compact `AgentSkillManifest`, compact `AgentReferenceManifest`, active `ModelConfigRef`/model policy, selected `AuthContext`, and `ToolPermissionBoundary`. Deny disabled/archived agents, missing/inactive docs, unassigned skill/reference ids, cross-scope access, missing provider/model config, and denied tool-boundary grants before model-visible content or tool execution.

## Traces

Emits `PromptAssemblyTrace`, `SkillLoadTrace`, reference-load trace facts, `AgentWorkTrace`, and denial categories without exposing provider secrets or unauthorized document bodies.
