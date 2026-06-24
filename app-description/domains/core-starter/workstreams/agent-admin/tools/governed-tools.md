# Tools: Agent Admin

## Uses

Global tool inventory: `../../../../../global/tools/foundation-governed-tools.md`.

## Workstream exposure

Agent Admin exposes governed tools for SaaS-admin-only agent document editing:

- `list-agent-doc-agents`: list/filter all agents by name and workstream/domain; returns agent name, short purpose, and last edit time.
- `read-agent-doc-agent`: read one agent's name, purpose, prompt link, skills, reference doc links, and trace entry points.
- `update-agent-name-purpose`: update agent name and purpose for an existing agent.
- `read-agent-prompt-doc`: read current or historical prompt doc version.
- `read-agent-skill-doc`: read current or historical skill doc version.
- `read-agent-skill-reference-doc`: read current or historical skill reference doc version.
- `draft-agent-doc-edit`: invoke the editing agent against the current version using free-form user instructions and relevant same-agent context.
- `revise-agent-doc-edit`: send additional user instructions during the current editing session and return an updated proposed document.
- `save-agent-doc-edit`: save the proposed prompt/skill/reference-doc content as the new current version.
- `cancel-agent-doc-edit`: discard the current proposed edit and return to the current version.
- `read-agent-doc-version-history`: list version numbers for a prompt, skill, or reference doc.
- `read-agent-doc-version-diff`: show version `N` diffed only against version `N-1`.
- `restore-agent-doc-version`: immediately create a new current version copied from historical version `N` with edit request `Restored from version N`.
- `create-agent-skill`: create a skill for an existing agent using name, purpose/description, and editing-agent-drafted initial content.
- `delete-agent-skill`: permanently delete a skill and all of its reference docs after confirmation.
- `create-agent-skill-reference-doc`: create a reference doc under a skill using name, short description, and editing-agent-drafted initial content.
- `delete-agent-skill-reference-doc`: permanently delete a reference doc after confirmation.
- `read-agent-doc-runtime-traces`: read runtime `readSkill` / `readReferenceDoc` trace metadata.
- Runtime tools available to all agents: `readSkill` and `readReferenceDoc`.

## Tool boundaries

All Agent Admin browser and agent tools require SaaS Owner/Admin authorization. Browser controls are advisory; backend authorization, current-version consistency, delete confirmation, version creation, and trace emission are authoritative.

There is no Agent Admin tool for tenant/org scoped governance, model settings, tool permission administration, prompt-risk approval gates, activation, rollback as a separate lifecycle, or whole-agent creation/deletion.

## `human_chat_tool_plan` posture

The composer may route requests to available Agent Admin tools when the workstream agent can safely identify the target and action. Otherwise it should ask a clarifying question or return a system message. Consequential mutations still execute through protected backend tools such as Save, Restore, Create Skill, Delete Skill, Create Reference Doc, and Delete Reference Doc.
