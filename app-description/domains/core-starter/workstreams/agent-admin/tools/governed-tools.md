# Tools: Agent Admin

## Uses

Global tool inventory: `../../../../../global/tools/foundation-governed-tools.md`.

## Workstream exposure

Agent Admin exposes governed tools for SaaS-admin-only agent document editing:

- `list-agent-doc-agents`: list/filter all generated agents by name, workstream/domain, placement, and scope provenance; returns agent name, short purpose, resolved profile scope, and last edit time.
- `read-agent-doc-agent`: read one generated agent's identity/provenance, purpose, placement, lifecycle status, steward, authority level, prompt link, assigned skills, allowed generated tools, reference links, profile history, and trace entry points.
- `inspect-agent-runtime-profile`: read safe `AgentDefinition`, `ModelConfigRef`, `AgentSkillManifest`, `AgentReferenceManifest`, allowed generated tool list, and `ToolPermissionBoundary` summaries without provider secrets or hidden policy internals.
- `update-agent-model-config-ref`: select an approved model config reference for an existing generated agent by creating a new behavior-profile version.
- `read-agent-prompt-doc`: read current or historical prompt doc version.
- `read-agent-skill-doc`: read current or historical skill doc version.
- `read-agent-skill-reference-doc`: read current or historical governed reference version associated with a skill/reference manifest.
- `draft-agent-doc-edit`: invoke the editing agent against the current active or draft version using free-form user instructions and relevant same-agent context; returns a structured behavior-change proposal, risk classification, authority-expansion flags, suggested tests/replay evidence, and proposed full content.
- `revise-agent-doc-edit`: send additional user instructions during the current editing session and return an updated proposal.
- `save-agent-doc-edit`: save the proposed prompt/skill/reference content as a non-active draft/proposal version.
- `submit-agent-doc-proposal-for-review`: route a proposal to review or decision-card workflow when risk/authority policy requires it.
- `approve-agent-doc-proposal`: record human review approval for a proposal when the caller has approval authority.
- `reject-agent-doc-proposal`: record rejection with rationale and leave active behavior unchanged.
- `activate-agent-doc-version`: activate an approved prompt/skill/reference version or low-risk reviewed draft through protected backend checks.
- `cancel-agent-doc-edit`: discard the current proposed edit and return to the current active version.
- `read-agent-doc-version-history`: list version numbers for a prompt, skill, or reference doc.
- `read-agent-doc-version-diff`: show version `N` diffed only against version `N-1`.
- `restore-agent-doc-version`: create a restore proposal copied from historical version `N` with edit request `Restored from version N`; activation is separate.
- `read-agent-behavior-profile-history`: list and inspect behavior-profile versions containing model config reference, prompt version, assigned skills, allowed generated tools, scope, and provenance.
- `restore-agent-behavior-profile-version`: create a restore proposal copied from a historical behavior-profile version; activation is separate.
- `assign-agent-skills`: assign/unassign independently managed skills for an existing generated agent by creating a new behavior-profile version.
- `assign-agent-generated-tools`: assign/unassign static generated tools for an existing generated agent by creating a new behavior-profile version; does not create, edit, or delete tool code.
- `list-agent-skill-library`: list/filter tenant-scoped skills independent of specific agents.
- `create-agent-skill`: create a tenant-scoped skill-library proposal using name, purpose/description, compact manifest hint, and editing-agent-drafted initial content.
- `delete-agent-skill`: deprecate by default or permanently delete a skill only when lifecycle policy permits, update manifest membership, and remove/reassign references after confirmation.
- `create-agent-skill-reference-doc`: create a governed reference proposal associated with a skill/reference manifest using title/name, short description/when-to-consult hint, and editing-agent-drafted initial content.
- `delete-agent-skill-reference-doc`: permanently delete or deprecate a reference according to lifecycle policy after confirmation.
- `read-agent-doc-runtime-traces`: read runtime `readSkill` / `readReferenceDoc` trace metadata.
- Runtime tools available to all agents: `readSkill` and `readReferenceDoc`.

## Tool boundaries

All Agent Admin browser and agent tools require SaaS Owner/Admin authorization. Browser controls are advisory; backend authorization, current-version consistency, proposal lifecycle, approval/activation rules, delete confirmation, version creation, and trace emission are authoritative. Model-facing editing-agent tools are available only when the active `AgentDefinition`, model policy, selected `AuthContext`, and `ToolPermissionBoundary` explicitly allow them.

There is no Agent Admin tool for provider-secret administration, generated tool code creation/edit/deletion, raw model settings mutation beyond selecting approved model config references, backend authorization implementation changes, authority expansion through text, activation without backend review checks, rollback without a restore proposal, or whole-agent creation/deletion. Per-agent generated tool assignment is allowed only as a versioned behavior-profile change and remains subject to backend authorization and tool-boundary enforcement.

## `human_chat_tool_plan` posture

The composer may route requests to available Agent Admin tools when the workstream agent can safely identify the target and action. Otherwise it should ask a clarifying question or return a system message. Consequential changes first create proposals/drafts and then execute only through protected backend review/activation tools such as Save Draft, Submit for Review, Approve, Activate, Restore Proposal, Change Model Config Reference, Assign Skills, Assign Generated Tools, Create Skill, Delete/Deprecate Skill, Create Reference, and Delete/Deprecate Reference.
