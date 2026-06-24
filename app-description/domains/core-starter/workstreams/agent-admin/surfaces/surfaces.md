# Surfaces: Agent Admin

## Workstream placement

Agent Admin is a SaaS-admin-only functional-agent workstream owned by `agent-admin-agent`. It is an AI-assisted agent-document editing workspace, not a broad governance console.

The workstream persists previous surfaces. There is no forced default surface. A first-time or cleared workstream may be blank with controls to Show dashboard, Show agents, and use the composer.

## Surface inventory

1. `surface-agent-admin-blank` — blank workstream state.
2. `surface-agent-admin-dashboard` — optional dashboard.
3. `surface-agent-admin-agent-list` — filterable agent list.
4. `surface-agent-admin-agent-detail` — one agent's docs and traces.
5. `surface-agent-admin-prompt-doc` — prompt doc view/edit.
6. `surface-agent-admin-skill-doc` — skill doc view/edit.
7. `surface-agent-admin-skill-reference-doc` — skill reference doc view/edit.
8. `surface-agent-admin-edit-session` — editing-agent proposal/refinement session.
9. `surface-agent-admin-version-history` — version list and historical version view.
10. `surface-agent-admin-version-diff` — selected version vs immediate predecessor.
11. `surface-agent-admin-create-skill` — create skill flow.
12. `surface-agent-admin-delete-skill-confirmation` — permanent skill delete confirmation.
13. `surface-agent-admin-create-reference-doc` — create skill reference doc flow.
14. `surface-agent-admin-delete-reference-doc-confirmation` — permanent reference doc delete confirmation.
15. `surface-agent-admin-runtime-traces` — runtime skill/reference read traces.
16. `surface-agent-admin-system-message` — denial, refusal, clarification, unavailable, stale, or recovery message.

Prompt, skill, and skill reference doc view/edit are separate surface contracts.

## Usability directive

Default surfaces must optimize for clear editing tasks. Use plain language such as Improve behavior, Edit prompt, Edit skill, Reference docs, Version history, Show diff, Save, Cancel, Restore this version, and Runtime reads. Do not lead with policy ids, proposal state machines, tool-boundary ids, model refs, seed imports, activation, or rollback.

Progressive disclosure order:

1. document or agent summary;
2. current content and primary action;
3. version/history/diff controls;
4. traces and audit metadata.

## `surface-agent-admin-blank`

Purpose: show an intentionally empty workstream when there is no persisted surface.

Default payload: simple empty copy, Show dashboard action, Show agents action, Clear workstream disabled/no-op state, composer available.

States: ready-empty, forbidden, failure.

## `surface-agent-admin-dashboard`

Purpose: optional dashboard for quick access, not a required entry surface.

Default payload:

- `thingsYouCanDo`: clickable total-agent count card opening `surface-agent-admin-agent-list`.
- `recentlyChangedAgents`: top five agents by last edit time; each row opens `surface-agent-admin-agent-detail`.

No `thingsNeedAttention` section is required until a concrete attention need is identified.

## `surface-agent-admin-agent-list`

Purpose: find an agent to improve.

Default payload:

- filters: agent name, workstream/domain;
- rows: agent name, short purpose, last edit time;
- row action: open agent detail.

States: loading, ready, empty-no-agents, empty-no-filter-matches, forbidden, validation-error, failure.

## `surface-agent-admin-agent-detail`

Purpose: inspect one existing agent and choose which doc to view/edit.

Default payload:

- editable agent name;
- editable agent purpose;
- link/action to prompt doc;
- clickable skill list with skill name and purpose/description;
- each skill may show reference docs as clickable nested rows;
- trace entry points for this agent.

Actions: save agent name/purpose, open prompt doc, open skill doc, create skill, delete skill, open reference doc, create reference doc under a skill, open runtime traces.

No whole-agent create/delete action appears here.

## `surface-agent-admin-prompt-doc`

Purpose: view and improve the selected agent's prompt.

Default payload: current prompt Markdown content, current version number, created/edited metadata, edit request/transcript summary that created the version, version history access, runtime trace access, free-form edit input enabled only for the current/latest version.

Edit action opens or appends to `surface-agent-admin-edit-session` for doc type `prompt`.

Historical view: read-only banner, content, metadata, edit request/transcript summary, optional diff action, restore action.

## `surface-agent-admin-skill-doc`

Purpose: view and improve one skill.

Default payload: skill name, editable purpose/description, current skill Markdown content, current version number, created/edited metadata, edit request/transcript summary, version history access, reference doc list, runtime trace access, free-form edit input enabled only for the current/latest version.

Edit action opens or appends to `surface-agent-admin-edit-session` for doc type `skill`.

Historical view: read-only banner, content, metadata, edit request/transcript summary, optional diff action, restore action.

## `surface-agent-admin-skill-reference-doc`

Purpose: view and improve one reference doc belonging to a skill.

Default payload: reference doc name, short description used by the model for read selection, current Markdown content, current version number, created/edited metadata, edit request/transcript summary, version history access, runtime trace access, free-form edit input enabled only for the current/latest version.

Edit action opens or appends to `surface-agent-admin-edit-session` for doc type `skill-reference`.

Historical view: read-only banner, content, metadata, edit request/transcript summary, optional diff action, restore action.

## `surface-agent-admin-edit-session`

Purpose: iterative AI-assisted editing session.

Default payload:

- target agent/doc identity;
- base current version number;
- user instruction transcript;
- clarifying question when needed;
- full proposed Markdown document;
- summary of changes;
- advisory warnings/risks;
- Show diff toggle/action comparing proposed content with the current base version;
- input for additional refinement instructions;
- Save and Cancel actions.

Save creates a new current version immediately. Cancel discards the proposed content and returns to the current doc. Users may Save without viewing the diff. Warnings/risks are advisory only.

States: drafting, clarification-needed, proposed, refining, saving, cancelled, saved, provider-unavailable, stale-current-version, forbidden, failure.

## `surface-agent-admin-version-history`

Purpose: browse immutable versions.

Default payload: version rows showing simple integer version numbers. Selecting a version shows content, created time, actor, edit request/transcript summary, read-only banner, Show diff action, and Restore this version action.

Restore immediately creates a new current version copied from the historical version and records `Restored from version N`.

## `surface-agent-admin-version-diff`

Purpose: show a selected version's changes.

Diff rule: selected version `N` is compared only with `N-1`. Example: version 7 diff is between version 7 and version 6. Version 1 has no predecessor and may show an empty/no-prior-version state.

## `surface-agent-admin-create-skill`

Purpose: create a new skill under an existing agent.

Inputs: skill name, purpose/description, free-form initial content request. The editing agent drafts the initial skill Markdown content. Save creates the skill and its first version. Cancel returns to agent detail.

## `surface-agent-admin-delete-skill-confirmation`

Purpose: confirm permanent skill deletion.

Default payload: skill name, permanent deletion warning, count/list of reference docs that will also be deleted. Confirm permanently deletes the skill and all reference docs. There is no restore.

## `surface-agent-admin-create-reference-doc`

Purpose: create a reference doc under a skill.

Inputs: reference doc name, short description used by the model to decide whether to read it, free-form initial content request. The editing agent drafts initial Markdown content. Save creates the reference doc and its first version. Cancel returns to skill doc.

## `surface-agent-admin-delete-reference-doc-confirmation`

Purpose: confirm permanent reference doc deletion.

Default payload: reference doc name and permanent deletion warning. Confirm permanently deletes the reference doc. There is no restore.

## `surface-agent-admin-runtime-traces`

Purpose: show runtime `readSkill` and `readReferenceDoc` trace metadata for all agents.

Filters: agent, skill/reference doc, time range.

Rows show: agent name, skill/reference doc read, timestamp, request/session id, user/customer context. Full skill/reference content read at runtime is not shown in trace rows.

Placement: accessible from agent detail, each doc page, and separate trace surface.

## `surface-agent-admin-system-message`

Purpose: show denial, clarification, unsupported request, provider unavailable, stale version, deleted doc, or safe alternative messages.

Unsafe or out-of-scope edit requests should explain the issue and propose a safer alternative. Missing SaaS admin authority denies access.

## Tests

Surface tests must cover blank entry, dashboard on demand, agent list filtering, agent detail navigation, separate prompt/skill/reference doc surfaces, edit input enabled only on current version, historical read-only views, version-to-previous diff semantics, iterative editing session, save/cancel, restore, skill/reference create/delete, SaaS-admin-only access, runtime traces, and Markdown preservation expectations.
