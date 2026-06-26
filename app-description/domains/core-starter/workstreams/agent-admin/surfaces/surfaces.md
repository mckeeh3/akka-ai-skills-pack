# Surfaces: Agent Admin

## Workstream placement

Agent Admin is a SaaS-admin-only functional-agent workstream owned by `agent-admin-agent`. It is an AI-assisted agent-document editing workspace, not a broad governance console.

The workstream persists previous surfaces. There is no forced default surface. A first-time or cleared workstream may be blank with controls to Show dashboard, Show agents, and use the composer.

## Surface inventory

1. `surface-agent-admin-blank` — blank workstream state.
2. `surface-agent-admin-dashboard` — optional dashboard.
3. `surface-agent-admin-agent-list` — filterable agent list.
4. `surface-agent-admin-agent-detail` — one agent's docs and traces.
5. `surface-agent-admin-prompt-doc` — prompt doc view/edit/proposal.
6. `surface-agent-admin-skill-doc` — skill doc view/edit/proposal.
7. `surface-agent-admin-skill-reference-doc` — governed reference doc view/edit/proposal.
8. `surface-agent-admin-edit-session` — editing-agent proposal/refinement session.
9. `surface-agent-admin-proposal-review` — behavior-change proposal review/activation/decision-card routing.
10. `surface-agent-admin-version-history` — version list and historical version view.
11. `surface-agent-admin-version-diff` — selected version vs immediate predecessor.
12. `surface-agent-admin-create-skill` — create skill proposal flow.
13. `surface-agent-admin-delete-skill-confirmation` — permanent/deprecation skill delete confirmation.
14. `surface-agent-admin-create-reference-doc` — create governed reference proposal flow.
15. `surface-agent-admin-delete-reference-doc-confirmation` — permanent/deprecation reference delete confirmation.
16. `surface-agent-admin-runtime-traces` — runtime prompt/skill/reference read traces.
17. `surface-agent-admin-system-message` — denial, refusal, clarification, unavailable, stale, or recovery message.

Prompt, skill, and governed reference doc view/edit are separate surface contracts.

## Usability directive

Default surfaces must optimize for clear editing and review tasks. Use plain language such as Improve behavior, Edit prompt, Edit skill, Reference docs, Version history, Show diff, Save draft, Review, Activate, Cancel, Restore this version, and Runtime reads. Do not lead with policy ids, proposal state machines, tool-boundary ids, model refs, seed imports, activation plumbing, or rollback internals.

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
- `proposalCounts`: clickable draft/in-review/approval-required counts when proposal lifecycle exists; each opens `surface-agent-admin-proposal-review` or a filtered list.
- `recentlyChangedAgents`: top five agents by last behavior change time; each row opens `surface-agent-admin-agent-detail`.

No operational `thingsNeedAttention` section is required until a concrete attention need is identified. Proposal counts are review routers and must not imply automatic activation.

## `surface-agent-admin-agent-list`

Purpose: find an agent to improve.

Default payload:

- filters: agent name, workstream/domain, placement, lifecycle status, steward, authority level;
- rows: agent name, short purpose, placement, lifecycle status, safe model alias summary, last behavior change time;
- row action: open agent detail.

States: loading, ready, empty-no-agents, empty-no-filter-matches, forbidden, validation-error, failure.

## `surface-agent-admin-agent-detail`

Purpose: inspect one existing agent and choose which doc to view/edit.

Default payload:

- editable agent name;
- editable agent purpose;
- placement (`functional_context_area` or `internal_worker`);
- lifecycle status, steward/owner, and authority level;
- safe `ModelConfigRef`/model policy alias summary, with no secrets;
- compact `AgentSkillManifest` and `AgentReferenceManifest` summaries;
- safe `ToolPermissionBoundary` summary showing categories/adapter classes, not hidden internals;
- link/action to prompt doc;
- clickable skill list with skill name and purpose/description;
- each skill may show governed references as clickable nested rows;
- trace entry points for this agent.

Actions: save agent name/purpose through protected proposal/update path, open prompt doc, open skill doc, create skill proposal, delete/deprecate skill, open reference doc, create reference doc under a skill/manifest, open runtime traces, open proposal review.

No whole-agent create/delete action appears here. Model settings, tool permission administration, lifecycle changes, and authority changes are inspection/explanation only in this workstream unless a future task adds protected governance flows.

## `surface-agent-admin-prompt-doc`

Purpose: view and improve the selected agent's prompt.

Default payload: current active prompt Markdown content, current version number/status, created/reviewed/activated metadata, edit request/transcript summary that created the version, risk/authority-expansion summary where present, version history access, runtime trace access, and free-form edit input enabled only for the current/latest editable draft or active document.

Edit action opens or appends to `surface-agent-admin-edit-session` for doc type `prompt`.

Historical view: read-only banner, content, metadata, edit request/transcript summary, optional diff action, restore action.

## `surface-agent-admin-skill-doc`

Purpose: view and improve one skill.

Default payload: skill name, editable purpose/description, compact manifest hint, current active skill Markdown content, current version number/status, created/reviewed/activated metadata, edit request/transcript summary, risk/authority-expansion summary where present, version history access, reference list, runtime trace access, and free-form edit input enabled only for the current/latest editable draft or active document.

Edit action opens or appends to `surface-agent-admin-edit-session` for doc type `skill`.

Historical view: read-only banner, content, metadata, edit request/transcript summary, optional diff action, restore action.

## `surface-agent-admin-skill-reference-doc`

Purpose: view and improve one governed reference associated with a skill/reference manifest.

Default payload: governed reference title/name, short description and when-to-consult hint used by the model for read selection, access/redaction summary when applicable, current active Markdown content, current version number/status, created/reviewed/activated metadata, edit request/transcript summary, risk/authority-expansion summary where present, version history access, runtime trace access, and free-form edit input enabled only for the current/latest editable draft or active document.

Edit action opens or appends to `surface-agent-admin-edit-session` for doc type `reference`.

Historical view: read-only banner, content, metadata, edit request/transcript summary, optional diff action, restore action.

## `surface-agent-admin-edit-session`

Purpose: iterative AI-assisted editing session.

Default payload:

- target agent/doc identity;
- base current version number;
- user instruction transcript;
- clarifying question when needed;
- full proposed Markdown document;
- structured behavior-change proposal id;
- summary/rationale;
- risk classification;
- authority-expansion flags;
- suggested tests/replay evidence;
- advisory warnings/risks;
- Show diff toggle/action comparing proposed content with the current base version;
- input for additional refinement instructions;
- Save draft/proposal and Cancel actions.

Save draft/proposal creates a non-active immutable draft/proposal. Cancel discards the proposed content and returns to the current active doc. Users may save a draft without viewing the diff. Activation happens only from the proposal review/activation surface after backend checks.

States: drafting, clarification-needed, proposed, refining, saving-draft, draft-saved, routed-to-review, cancelled, provider-unavailable, stale-current-version, forbidden, blocked-authority-expansion, failure.

## `surface-agent-admin-proposal-review`

Purpose: review behavior-change proposals before activation or routing to a decision card.

Default payload: proposal id, target agent/artifact, proposed diff, full proposed content, summary/rationale, risk classification, authority-expansion flags, suggested tests/replay evidence, reviewer status, trace links, and available actions.

Actions: approve, reject with rationale, activate low-risk reviewed draft when authorized, route medium/high-risk proposal to decision-card/review workflow, request changes, or cancel. Activation must re-check SaaS admin authority, artifact lifecycle, current-version consistency, model policy, tool-boundary/authority expansion flags, and provider/runtime availability where relevant. Rejection leaves active behavior unchanged.

States: draft, in-review, approved, rejected, activation-ready, activating, active, decision-card-required, blocked-authority-expansion, stale-current-version, forbidden, failure.

## `surface-agent-admin-version-history`

Purpose: browse immutable versions.

Default payload: version rows showing simple integer version numbers and lifecycle status. Selecting a version shows content, created/reviewed/activated time, actor/reviewer where allowed, edit request/transcript summary, risk/authority-expansion summary, read-only banner, Show diff action, and Restore this version action.

Restore creates a restore proposal copied from the historical version and records `Restored from version N`; activation of that proposal creates the new current active version.

## `surface-agent-admin-version-diff`

Purpose: show a selected version's changes.

Diff rule: selected version `N` is compared only with `N-1`. Example: version 7 diff is between version 7 and version 6. Version 1 has no predecessor and may show an empty/no-prior-version state.

## `surface-agent-admin-create-skill`

Purpose: create a new skill under an existing agent.

Inputs: skill name, purpose/description, compact manifest hint, and free-form initial content request. The editing agent drafts the initial skill Markdown content and proposal metadata. Save draft creates a non-active skill proposal; activation creates the skill and first active version. Cancel returns to agent detail.

## `surface-agent-admin-delete-skill-confirmation`

Purpose: confirm skill deprecation or permanent deletion according to lifecycle policy.

Default payload: skill name, deletion/deprecation warning, manifest membership effect, count/list of references that will also be deleted/deprecated or require reassignment, required typed confirmation, and trace impact. Confirm performs the configured lifecycle action after backend authorization and idempotency checks. Permanent deletion has no restore.

## `surface-agent-admin-create-reference-doc`

Purpose: create a governed reference associated with a skill/reference manifest.

Inputs: reference title/name, short description and when-to-consult hint used by the model to decide whether to read it, optional access/redaction classification, and free-form initial content request. The editing agent drafts initial Markdown content and proposal metadata. Save draft creates a non-active reference proposal; activation creates the reference and first active version. Cancel returns to skill doc.

## `surface-agent-admin-delete-reference-doc-confirmation`

Purpose: confirm reference deprecation or permanent deletion according to lifecycle policy.

Default payload: reference title/name, deletion/deprecation warning, manifest membership effect, required typed confirmation, and trace impact. Confirm performs the configured lifecycle action after backend authorization and idempotency checks. Permanent deletion has no restore.

## `surface-agent-admin-runtime-traces`

Purpose: show runtime prompt assembly, `readSkill`, and `readReferenceDoc` trace metadata for all agents.

Filters: agent, skill/reference doc, time range, allowed/denied decision, runtime mode.

Rows show: agent name, prompt/skill/reference doc read, manifest assignment status, version/checksum where allowed, safe model alias, tool-boundary decision category, timestamp, request/session id, and user/customer context. Full skill/reference content read at runtime is not shown in trace rows.

Placement: accessible from agent detail, each doc page, and separate trace surface.

## `surface-agent-admin-system-message`

Purpose: show denial, clarification, unsupported request, provider unavailable, stale version, deleted doc, or safe alternative messages.

Unsafe, authority-expanding, or out-of-scope edit requests should explain the issue and propose a safer alternative or review route. Missing SaaS admin authority denies access. Provider/model unavailable, inactive model config, missing tool-boundary grant, stale proposal, deleted artifact, and hidden target states must fail closed without fake success or hidden enumeration.

## Tests

Surface tests must cover blank entry, dashboard on demand, proposal-count routing, agent list filtering, agent detail profile summary, separate prompt/skill/reference doc surfaces, edit input enabled only on current/latest editable artifacts, historical read-only views, version-to-previous diff semantics, iterative editing session, save draft/cancel/proposal review/activation/rejection, restore proposal, skill/reference create/delete/deprecate, SaaS-admin-only access, runtime prompt/skill/reference traces, safe model/tool-boundary summaries, and Markdown preservation expectations.
