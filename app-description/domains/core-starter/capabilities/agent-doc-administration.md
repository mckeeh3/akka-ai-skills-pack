# Capability: Agent doc administration

## Purpose

Allow SaaS Owner/Admin users to improve governed managed-agent behavior by editing versioned agent documents through an AI-assisted editing flow. Agent docs include each agent's `PromptDocument`, assigned `SkillDocument` records, skill reference documents, compact `AgentSkillManifest` entries, and related runtime loading traces.

## Actors and scope

- SaaS Owner/Admin: may view all agents through the agent catalog, view full agent docs, create/update/delete skills, create/update/delete skill reference docs, edit agent names and purposes, restore historical doc versions, and inspect Agent Admin audit/read traces.
- Tenant/organization/customer admins: not Agent Admin operators.
- `AgentBehaviorEditorAgent` (editing-agent): interprets authorized SaaS admin edit requests, reads the relevant current doc and related context, drafts proposed Markdown-preserving changes, summarizes risks/warnings, asks clarifying questions when needed, and returns proposed content for human review.

Agent Admin applies to all agents in the app, including foundation agents and future business/domain-specific agents. It does not create or delete whole agents.

## Agent document model

- Each governed managed agent is represented by an `AgentDefinition` plus one current `PromptDocument`/`PromptVersion`.
- Every agent may have zero or more `SkillDocument`/`SkillVersion` records assigned through its `AgentSkillManifest`.
- Every skill may have zero or more governed reference docs.
- Reference docs belong to a specific skill and are shown under that skill.
- Agent docs support Markdown, and the `AgentBehaviorEditorAgent` must preserve existing Markdown structure unless the user explicitly asks to reorganize it.
- Agent docs, manifests, and `ToolPermissionBoundary` records are initially created by governed setup from implementation defaults with provenance. SaaS admins may update prompt docs, create/update/delete skills, create/update/delete skill reference docs, and update manifest membership only through Agent Admin authorization checks.

## Versioning

- Each saved prompt, skill, or reference-doc edit immediately creates a new current immutable version.
- Version numbers are simple integers.
- All versions are retained except where a whole skill or reference doc is permanently deleted.
- Each version records version number, created time, actor, saved content, and the whole editing-session transcript/summary including all user instructions.
- Users can browse version history. A history row only needs to show the version number.
- Historical versions are read-only. Edit request input is enabled only on the current/latest version.
- A requested diff for version `N` compares only version `N` with version `N-1`.
- `Restore this version` immediately creates a new current version whose content is copied from the selected historical version and whose edit request is `Restored from version N`.

## Editing flow

1. User opens Agent Admin, optionally opens the dashboard, then shows the agent catalog/list.
2. User filters by agent name or workstream/domain and opens an agent detail.
3. User opens the prompt, a skill, or a skill reference doc.
4. On the current/latest version, user enters free-form instructions for improving behavior.
5. The editing agent may ask clarifying questions or propose a safer alternative for unsafe/out-of-scope requests.
6. The editing agent returns proposed full document content, a summary of changes, and advisory warnings/risks.
7. The user may continue providing input to refine the proposal.
8. The session ends with Save or Cancel.
9. Save creates the new current version and immediately updates runtime agent behavior; Cancel discards the proposal and shows the current version.

Diff viewing is on demand through a `Show diff` style toggle or action. Users may save without opening the diff. Warnings and risks are advisory only and do not block saving for authorized SaaS admins.

## Skill and reference-doc lifecycle

- Create skill inputs: skill name, editable purpose/description, and free-form content request handled by the editing agent.
- Delete skill is permanent and requires confirmation naming the skill, stating permanence, and listing/counting reference docs that will also be deleted.
- Deleted skills cannot be restored.
- Create skill reference doc inputs: reference doc name, short description used by the model to decide whether to read it, and free-form content request handled by the editing agent.
- Delete reference doc is permanent with a simple confirmation.
- Deleting a skill also deletes all reference docs under that skill.

## Runtime document loading

Each time an agent handles a request, it resolves the active `AgentDefinition`, current `PromptDocument`/`PromptVersion`, compact `AgentSkillManifest`, reference hints, model policy, selected `AuthContext`, and `ToolPermissionBoundary`. The model sees only compact assigned skill names/descriptions until it calls `readSkill(skillId)`. Loaded skills include reference doc names/descriptions so the model can decide whether to call `readReferenceDoc(referenceId)`. Agents only know about skills listed for themselves; there is no discovery path for other agents' skills. Runtime loading emits `PromptAssemblyTrace`, `SkillLoadTrace`, reference-load trace facts, and `AgentWorkTrace`, including safe denials for unauthorized `PromptDocument` access, unassigned skill denial, disabled-agent denial, inactive/deleted docs, and tool-boundary denial.

## Out of scope

- Tenant/organization/customer-scoped Agent Admin.
- Creating or deleting whole agents.
- Model settings administration.
- Tool permission administration beyond viewing/explaining `ToolPermissionBoundary` effects and preserving manifest assignments during skill edits.
- Lifecycle activation/publish workflows separate from Save.
- Prompt-risk approval gates as a required save blocker.
