# Behavior: Agent Admin

## Current-state behavior

Agent Admin supports AI-assisted editing of versioned agent documents for all agents. Agent docs include each agent's required prompt, zero or more skills, and zero or more reference docs under each skill.

## Entry behavior

The workstream persists previous surfaces. There is no forced default surface. A first-time or cleared workstream may be blank. Users can use explicit controls such as Show dashboard, Show agents, and Clear workstream.

## Agent list and detail behavior

The agent list may be filtered by agent name and workstream/domain. Each row shows agent name, short purpose, and last edit time. Opening an agent shows agent name, editable purpose, a link to view/edit the prompt, and a clickable list of skills. Each skill shows its name/description and clickable reference docs.

Agent names and purposes are editable. Whole agents cannot be created or deleted in Agent Admin.

## Editing behavior

Editing is mediated by one editing agent with doc-type-specific skills for prompt editing, skill editing, and reference-doc editing. Users provide free-form instructions. The editing agent reads the current doc and relevant context from the same agent, preserves Markdown and existing structure unless asked to reorganize, and returns proposed full document content, a summary of changes, and advisory warnings/risks.

The editing agent may ask clarifying questions before proposing changes. If a request is unsafe or outside scope, it should explain the refusal and propose a safer alternative. Warnings/risks are advisory only and do not block Save for authorized SaaS admins.

The user may continue giving instructions to refine the proposed content. The editing session ends with Save or Cancel. Save creates a new current version immediately. Cancel discards the proposal and returns to the current version. Cancelled edit sessions are audited but are not retained in user-facing version history.

## Version behavior

Versions use simple integer numbers. Each saved version records created time, actor, content, and the whole editing-session transcript/summary including all user instructions. Version history rows show version number. Historical version views show content, metadata, edit request/transcript summary, optional diff to the immediate predecessor, and a read-only banner.

Edit input is enabled only on the current/latest version. Historical versions are read-only. Restore this version immediately creates a new current version copied from the historical content and records `Restored from version N` as the edit request. Restore-created versions appear in history.

## Skill/reference lifecycle

SaaS admins may create, update, and permanently delete skills. Create skill captures skill name, editable purpose/description, and a free-form request for initial content drafted by the editing agent. Delete skill requires confirmation naming the skill, stating deletion is permanent, and listing/counting reference docs that will also be deleted. Deleted skills cannot be restored.

SaaS admins may create, update, and permanently delete skill reference docs. Create reference doc captures name, short description used by models for read-selection, and free-form content request handled by the editing agent. Delete reference doc is permanent with a simple confirmation. Deleting a skill deletes all of its reference docs.

## Runtime behavior

Saving immediately updates the current doc used at runtime. Each agent request retrieves the current prompt and appends the list of skill names/descriptions. The model chooses whether to call `readSkill`. Loaded skills include reference doc names/descriptions. All agents have `readSkill` and `readReferenceDoc`; agents only know about skills listed for themselves. Runtime skill/reference reads are traced.
