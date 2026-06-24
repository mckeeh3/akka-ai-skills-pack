# Realization: Frontend routes and surfaces for Agent Admin

Capability: `agent-doc-administration`.

## Frontend intent

Agent Admin frontend realization should prioritize AI-assisted document editing over governance-console workflows.

## Surface / route concerns

| Surface / route concern | Expected frontend realization |
|---|---|
| Blank workstream | Persisted workstream surface area can be blank; controls expose Show dashboard, Show agents, Clear workstream, and composer. |
| Optional dashboard | Shows clickable total-agent count and top five recently changed agents. No default needs-attention queue. |
| Agent list | Filter by agent name and workstream/domain; rows show name, short purpose, last edit time; row opens agent detail. |
| Agent detail | Editable name/purpose, prompt link, skill list, nested reference docs, create/delete skill/reference actions, runtime trace entry points. |
| Prompt doc editor | Current Markdown content, version metadata, version history, edit request input only on current version, historical read-only view, restore. |
| Skill doc editor | Skill name/purpose, Markdown content, reference docs, version/history/diff, current-only edit input, restore. |
| Skill reference doc editor | Name, short description, Markdown content, version/history/diff, current-only edit input, restore. |
| Editing session | Free-form instructions, editing-agent proposed full document, summary, advisory warnings/risks, Show diff toggle, refinement input, Save, Cancel. |
| Create/delete skill | Create uses name, purpose/description, editing-agent drafted content. Delete confirmation is permanent and lists reference docs affected. |
| Create/delete reference doc | Create uses name, short description, editing-agent drafted content. Delete confirmation is permanent. |
| Runtime traces | Trace metadata filterable by agent, doc, time range; visible from agent detail, doc pages, and separate trace surface. |

## Removed/de-emphasized frontend concerns

The Agent Admin default UX should not center model settings, tool permission editing, seed import, prompt-risk approval gates, behavior proposal queues, activation/deactivation/rollback confirmations, or tenant/org governance scopes. If old components remain, they are not authoritative for current Agent Admin intent unless reintroduced by a later accepted app-description change.

## Validation evidence to update

Existing frontend contract tests for Agent Admin should be reconciled to cover the new surface inventory, current-version-only edit input, simple integer version history, version-to-previous diff behavior, restore-created versions, editing-session Save/Cancel, skill/reference permanent deletion, SaaS-admin-only access, and runtime read trace metadata.
