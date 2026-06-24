# Tests: Agent Admin

## Acceptance

- Given a SaaS Owner/Admin, when they open Agent Admin with no persisted surface, then a blank workstream renders with Show dashboard, Show agents, and composer affordances.
- Given a SaaS Owner/Admin, when they open the dashboard, then it shows a clickable total-agent count and top five most recently changed agents, with no required needs-attention section.
- Given a SaaS Owner/Admin, when they filter the agent list by agent name or workstream/domain, then rows show agent name, short purpose, and last edit time and open agent detail.
- Given an agent detail, then it shows editable agent name/purpose, prompt link, skill list, nested reference docs, create/delete skill/reference actions, and trace entry points; it does not expose whole-agent create/delete.
- Given a prompt, skill, or reference doc current version, then edit input is enabled and accepts free-form instructions.
- Given a historical version, then content, metadata, edit request/transcript summary, optional diff, and restore action are visible, edit input is disabled, and a read-only banner is shown.
- Given version `N`, when Show diff is requested, then the diff compares only `N` to `N-1`; version 1 has no-prior-version behavior.
- Given a free-form edit request, when the editing agent proposes a change, then the surface shows full proposed Markdown content, summary, advisory warnings/risks, additional input, Save, Cancel, and Show diff.
- Given further user instructions, when the editing agent revises the proposal, then the transcript retains all user instructions.
- Given Save, then a new current immutable version is created immediately and runtime reads use it.
- Given Cancel, then no version is created and the current version is shown.
- Given Restore this version, then a new current version is immediately created with content copied from the historical version and edit request `Restored from version N`.
- Given create skill, then skill name, purpose/description, and editing-agent-drafted content create the skill and first version.
- Given delete skill, then confirmation names the skill, states deletion is permanent, lists/counts reference docs, and confirm permanently deletes skill plus reference docs with no restore.
- Given create reference doc, then name, short description, and editing-agent-drafted content create the reference doc and first version.
- Given delete reference doc, then confirmation permanently deletes it with no restore.

## Security and negative

- Non-SaaS-admin callers, tenant/org admins, customer admins, tenant employees, customer users, disabled users, inactive users, and unauthenticated callers are denied.
- Agent Admin does not create/delete whole agents, manage model settings, manage tool permissions, or require separate activation/publish flows.
- Unsafe or out-of-scope edit requests produce an explanation and safer alternative; warnings/risks are advisory and do not block Save for authorized SaaS admins.
- Historical versions cannot be directly edited.
- Stale current-version saves are rejected or recovered by backend consistency checks.

## Runtime and observability

- Each agent request loads the current prompt and skill names/descriptions.
- Agents can call `readSkill` and `readReferenceDoc`; runtime skill/reference reads are traced.
- Agent Admin trace surfaces show agent name, skill/reference doc read, timestamp, request/session id, and user/customer context, filterable by agent, doc, and time range.
- Trace rows do not show full skill/reference content.
- Every edit session audit includes user input, editing-agent proposed output, saved version content for Save, Save/Cancel outcome, timestamps, and actor.

## UX and formatting

- Prompt, skill, and reference doc content supports Markdown.
- The editing agent preserves existing Markdown and structure unless the user requests reorganization.
- Users may Save without opening the diff.
- No live-update behavior is required for concurrent edit sessions; backend consistency is authoritative.
