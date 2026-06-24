# Data State: Managed agent behavior state

## Responsibility

Platform-wide governed managed-agent records: `AgentDefinition` lifecycle/profile state, governed `PromptDocument`/`PromptVersion`, governed `SkillDocument`/`SkillVersion`, governed skill reference documents, compact per-agent `AgentSkillManifest` entries, `ToolPermissionBoundary` records, AI-assisted edit sessions, runtime document loading records, and runtime skill/reference read traces.

## Lifecycle and invariants

- `AgentDefinition`, `PromptDocument`, `SkillDocument`, reference document, manifest, and tool-boundary records are initially created by governed setup from implementation-developed defaults with tenant/scope provenance.
- Every enabled agent has exactly one active `PromptDocument` current `PromptVersion`.
- Every agent may have zero or more `SkillDocument` records assigned through its `AgentSkillManifest`.
- Every skill may have zero or more governed reference docs.
- Prompt, skill, and reference docs support Markdown.
- SaaS admins may edit agent names and purposes, update prompt docs, create/update/delete skills, create/update/delete skill reference docs, and adjust compact manifest entries only through Agent Admin capability checks.
- Whole agents are not created or deleted in Agent Admin.
- In this simplified SaaS-admin flow, each Save creates a new immutable current version that is immediately the active runtime version; there is no separate publish step.
- Historical versions are read-only; restore creates a new current version copied from the selected historical version.
- Version diffs compare selected version `N` only to `N-1`.
- Skill deletion is permanent and deletes all reference docs under the skill; deleted skills/reference docs cannot be restored.
- Prompt/skill/reference content is behavior guidance only and cannot grant backend authority, role permissions, tenant/customer scope, tool permissions, approval rights, or any authority expansion outside backend authorization and `ToolPermissionBoundary` enforcement.

## Runtime loading

Each agent request resolves the selected active `AgentDefinition`, current `PromptDocument`/`PromptVersion`, compact `AgentSkillManifest`, reference manifest entries, model policy, selected `AuthContext`, and `ToolPermissionBoundary`. Prompt assembly includes only the current prompt plus compact assigned skill names/descriptions and reference hints. Full `SkillDocument` or reference content is loaded only through authorized `readSkill(skillId)` or `readReferenceDoc(referenceId)` calls. Agents only know about skills listed for themselves; there is no discovery path for other agents' skills. Runtime loading must return safe denials for unauthorized `PromptDocument` access, unassigned skill denial, disabled-agent denial, cross-scope requests, inactive/deleted documents, and tool-boundary denial.

## Retention and traces

Versions retain created time, actor, content checksum/body reference, provenance, and the editing-session transcript/summary. Edit sessions are audited with user input, `AgentBehaviorEditorAgent` proposed output, Save/Cancel outcome, timestamps, actor, and saved content where applicable. Runtime prompt assembly emits `PromptAssemblyTrace`; `readSkill` emits `SkillLoadTrace`; reference loading emits reference-load trace facts; model/tool work emits `AgentWorkTrace`. Trace summaries include agent name, document read, version/checksum where allowed, timestamp, request/session id, tenant/customer/user context, authorization decision, and denial category without exposing provider secrets or unauthorized document bodies.
