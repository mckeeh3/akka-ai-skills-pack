# Data State: Managed agent behavior state

## Responsibility

Platform-wide governed managed-agent records: `AgentDefinition` lifecycle/profile state, governed `PromptDocument`/`PromptVersion`, governed `SkillDocument`/`SkillVersion`, governed `ReferenceDocument`/`ReferenceVersion`, compact per-agent `AgentSkillManifest` and `AgentReferenceManifest` entries, `ModelConfigRef` references, `ToolPermissionBoundary` records, AI-assisted behavior-change proposals/edit sessions, runtime document loading records, and runtime prompt/skill/reference read traces.

## Lifecycle and invariants

- `AgentDefinition`, `PromptDocument`, `SkillDocument`, `ReferenceDocument`, manifest, model-config reference, and tool-boundary records are initially created by governed setup from implementation-developed defaults with tenant/scope provenance.
- Every enabled agent has exactly one active `PromptDocument` current `PromptVersion`, an active behavior profile, an active/approved model-config reference, and an active `ToolPermissionBoundary`.
- Every agent may have zero or more `SkillDocument` records assigned through its `AgentSkillManifest`.
- Every agent may have zero or more governed references assigned through its `AgentReferenceManifest`; the UI may group references under skills for discoverability.
- Prompt, skill, and reference docs support Markdown.
- SaaS admins may edit agent names and purposes, update prompt docs, create/update/deprecate/delete skills, create/update/deprecate/delete governed references, and adjust compact manifest entries only through Agent Admin capability, proposal, review, activation, and trace checks.
- Whole agents are not created or deleted in Agent Admin.
- Each Save creates an immutable non-active draft/proposal version. Activation is a separate protected command; low-risk copy/clarity drafts may be reviewed and activated immediately by the same authorized SaaS admin as the documented foundation simplification. Medium/high-risk or authority-expanding proposals require review/decision-card routing or denial.
- Historical versions are read-only; restore creates a restore proposal copied from the selected historical version.
- Version diffs compare selected version `N` only to `N-1`.
- Skill/reference deletion is permanent only when lifecycle policy chooses hard deletion; otherwise deprecation is preferred. Deleting a skill must remove, deprecate, or reassign associated references and manifest entries without leaving hidden loader access.
- Prompt/skill/reference content is behavior guidance only and cannot grant backend authority, role permissions, tenant/customer scope, model-provider access, tool permissions, approval rights, or any authority expansion outside backend authorization, model policy, and `ToolPermissionBoundary` enforcement.

## Runtime loading

Each agent request resolves the selected active `AgentDefinition`, lifecycle status, authority level, current active `PromptDocument`/`PromptVersion`, compact `AgentSkillManifest`, compact `AgentReferenceManifest`, active `ModelConfigRef`/model policy, selected `AuthContext`, and `ToolPermissionBoundary`. Prompt assembly includes only the current prompt plus compact assigned skill/reference names/descriptions and hints. Full `SkillDocument` or reference content is loaded only through authorized `readSkill(skillId)` or `readReferenceDoc(referenceId)` calls. Agents only know about skills/references listed for themselves; there is no discovery path for other agents' skills or references. Runtime loading must return safe denials for unauthorized `PromptDocument` access, unassigned skill/reference denial, disabled/archived-agent denial, cross-scope requests, inactive/deleted documents, inactive/denied model config, and tool-boundary denial.

## Retention and traces

Versions retain created time, proposed/reviewed/activated actor data, lifecycle status, content checksum/body reference, provenance, risk classification, authority-expansion flags, and the editing-session transcript/summary. Edit sessions and behavior-change proposals are audited with user input, `AgentBehaviorEditorAgent` structured proposal output, Save Draft/Review/Approve/Reject/Activate/Cancel outcome, timestamps, actors, decision-card links when applicable, and saved content where allowed. Runtime prompt assembly emits `PromptAssemblyTrace`; `readSkill` emits `SkillLoadTrace`; reference loading emits reference-load trace facts; model/tool work emits `AgentWorkTrace`. Trace summaries include agent name, document read, version/checksum where allowed, safe model alias, tool-boundary decision category, timestamp, request/session id, tenant/customer/user context, authorization decision, and denial category without exposing provider secrets or unauthorized document bodies.
