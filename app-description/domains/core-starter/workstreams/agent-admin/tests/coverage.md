# Tests: Agent Admin

## Acceptance

- Given a SaaS Owner/Admin, when they open Agent Admin with no persisted surface, then a blank workstream renders with Show dashboard, Show agents, and composer affordances.
- Given a SaaS Owner/Admin, when they open the dashboard, then it shows a clickable total-agent count and top five most recently changed agents, with no required needs-attention section.
- Given a SaaS Owner/Admin, when they filter the agent catalog by agent name or workstream/domain, then rows show agent name, short purpose, and last edit time and open agent detail.
- Given an agent detail, then it shows editable agent name/purpose, prompt link, skill list, nested reference docs, create/delete skill/reference actions, and trace entry points; it does not expose whole-agent create/delete.
- Given a `PromptDocument`, `SkillDocument`, or reference doc current version, then edit input is enabled and accepts free-form instructions.
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
- Unauthorized `PromptDocument` reads, unassigned skill denial, disabled-agent denial, cross-tenant/customer access, inactive/deleted document access, and `ToolPermissionBoundary` denial return safe system-message recovery without hidden document enumeration.
- Agent Admin does not create/delete whole agents, manage model settings, grant tool permissions, bypass backend authorization, or require separate activation/publish flows.
- Unsafe or out-of-scope edit requests, including authority expansion attempts through prompt/skill/reference text, produce an explanation and safer alternative; warnings/risks are advisory and do not block Save for authorized SaaS admins.
- Historical versions cannot be directly edited.
- Stale current-version saves are rejected or recovered by backend consistency checks.

## Runtime and observability

- Each agent request resolves the active `AgentDefinition`, current `PromptDocument`/`PromptVersion`, compact `AgentSkillManifest`, model policy, selected `AuthContext`, and `ToolPermissionBoundary`, then loads only the current prompt plus assigned skill names/descriptions.
- Agents can call `readSkill(skillId)` and `readReferenceDoc(referenceId)` only for authorized assigned documents; runtime prompt assembly emits `PromptAssemblyTrace`, skill loads emit `SkillLoadTrace`, reference loads emit reference-load trace facts, and model/tool work emits `AgentWorkTrace`.
- Agent Admin trace surfaces show agent name, skill/reference doc read, version/checksum where allowed, timestamp, request/session id, and tenant/customer/user context, filterable by agent, doc, and time range.
- Trace rows do not show full skill/reference content.
- Every edit session audit includes user input, editing-agent proposed output, saved version content for Save, Save/Cancel outcome, timestamps, and actor.

## UX and formatting

- Prompt, skill, and reference doc content supports Markdown.
- `AgentBehaviorEditorAgent` preserves existing Markdown and structure unless the user requests reorganization.
- Users may Save without opening the diff.
- No live-update behavior is required for concurrent edit sessions; backend consistency is authoritative.
