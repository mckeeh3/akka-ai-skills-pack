# Data State: Managed agent behavior state

## Responsibility

Platform-wide managed-agent records, versioned prompt documents, versioned skill documents, versioned skill reference documents, AI-assisted edit sessions, runtime document loading records, and runtime skill/reference read traces.

## Lifecycle and invariants

- Agent docs are initially created by the skills pack.
- Every agent has exactly one prompt doc.
- Every agent may have zero or more skills.
- Every skill may have zero or more reference docs.
- Prompt, skill, and reference docs support Markdown.
- SaaS admins may edit agent names and purposes, update prompt docs, create/update/delete skills, and create/update/delete skill reference docs.
- Whole agents are not created or deleted in Agent Admin.
- Each Save creates a new immutable current version immediately used at runtime.
- Historical versions are read-only; restore creates a new current version copied from the selected historical version.
- Version diffs compare selected version `N` only to `N-1`.
- Skill deletion is permanent and deletes all reference docs under the skill; deleted skills/reference docs cannot be restored.
- Prompt/skill/reference content cannot grant backend authority outside normal service authorization.

## Runtime loading

Each agent request loads the current prompt and appends the agent's skill names/descriptions. All agents have `readSkill` and `readReferenceDoc`. Loaded skills include reference doc names/descriptions so the model can decide which reference docs to read. Agents only know about skills listed for themselves.

## Retention and traces

Versions retain created time, actor, content, and the editing-session transcript/summary. Edit sessions are audited with user input, editing-agent proposed output, Save/Cancel outcome, timestamps, actor, and saved content where applicable. Runtime `readSkill` and `readReferenceDoc` calls are traced with agent name, document read, timestamp, request/session id, and user/customer context.
