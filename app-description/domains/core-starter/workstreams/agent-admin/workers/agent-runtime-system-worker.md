# Worker: Agent Runtime System Worker

## Responsibility

Backend runtime resolver/loader worker that prepares managed-agent invocation and test-console state. It enforces global/tenant-scoped profile, prompt, skill/reference manifest, model-policy, provider/config, generated-tool assignment, and tool-boundary rules before model calls, loader-tool calls, generated tool calls, or test-console execution.

## Execution harness and adapters

- Internal runtime resolver.
- `internal_call` for active profile lookup, tenant-specific override resolution, prompt assembly, provider/model-policy checks, and trace emission.
- Governed loader tools `readSkill(skillId)` and `readReferenceDoc(referenceId)` for model-requested content.
- Generated tool dispatch only when the resolved behavior profile allows the generated tool and backend authorization/tool-boundary checks pass.
- Test-console mode only when explicitly authorized, provider/runtime configuration is available, and side effects are disabled or separately approved.

## Required checks

Resolve the tenant-specific active behavior profile when present, otherwise the global active behavior profile, then resolve `AgentDefinition`, lifecycle status, authority level, current prompt version, compact `AgentSkillManifest`, compact `AgentReferenceManifest`, active `ModelConfigRef`/model policy, provider availability, selected `AuthContext`, allowed generated tool list, and `ToolPermissionBoundary`. Deny disabled/archived agents, missing/inactive docs, unassigned skill/reference ids, unassigned generated tool ids, cross-scope access, missing provider/model config, unauthorized test mode, and denied tool-boundary grants before model-visible content or tool execution.

## Traces

Emits profile-resolution trace facts, `PromptAssemblyTrace`, `SkillLoadTrace`, `ReferenceLoadTrace`, generated-tool assignment decision trace facts, provider/model-policy fail-closed facts, test-console run facts, `AgentWorkTrace`, and denial categories without exposing provider secrets, generated tool internals, or unauthorized document bodies.
