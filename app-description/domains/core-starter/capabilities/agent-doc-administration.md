# Capability: Agent doc administration

## Purpose

Allow SaaS Owner/Admin users to improve managed-agent behavior by editing versioned agent documents through an AI-assisted editing flow. Agent docs include each agent's prompt, its skills, and skill reference documents.

## Actors and scope

- SaaS Owner/Admin: may view all agents, view full agent docs, create/update/delete skills, create/update/delete skill reference docs, edit agent names and purposes, restore historical doc versions, and inspect Agent Admin audit/read traces.
- Tenant/organization/customer admins: not Agent Admin operators.
- Editing agent: interprets authorized SaaS admin edit requests, reads the relevant current doc and related context, drafts proposed Markdown-preserving changes, summarizes risks/warnings, asks clarifying questions when needed, and returns proposed content for human review.

Agent Admin applies to all agents in the app, including foundation agents and future business/domain-specific agents. It does not create or delete whole agents.

## Agent document model

- Every agent has exactly one prompt doc.
- Every agent may have zero or more skill docs.
- Every skill may have zero or more reference docs.
- Reference docs belong to a specific skill and are shown under that skill.
- Agent docs support Markdown, and the editing agent must preserve existing Markdown structure unless the user explicitly asks to reorganize it.
- Agent docs are initially created by the skills pack. SaaS admins may update prompt docs, create/update/delete skills, and create/update/delete skill reference docs.

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

1. User opens Agent Admin, optionally opens the dashboard, then shows the agent list.
2. User filters by agent name or workstream/domain and opens an agent.
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

Each time an agent handles a request, it retrieves the current prompt and appends the list of its skill names/descriptions to the prompt context. The model decides from those descriptions whether to call `readSkill`. Loaded skills include reference doc names/descriptions. All agents have `readSkill` and `readReferenceDoc` tools. Agents only know about skills listed for themselves; there is no discovery path for other agents' skills. Runtime skill/reference reads are audited/traced.

## Out of scope

- Tenant/organization/customer-scoped Agent Admin.
- Creating or deleting whole agents.
- Model settings administration.
- Tool permission administration.
- Lifecycle activation/publish workflows separate from Save.
- Prompt-risk approval gates as a required save blocker.
