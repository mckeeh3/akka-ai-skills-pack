# Surfaces: Agent Admin

## Workstream placement

Agent Admin is a SaaS-admin-only functional-agent workstream owned by `agent-admin-agent`. It is an AI-assisted generated-agent behavior workspace for all agents, not a whole-agent or tool-code creation console.

The workstream persists previous surfaces. There is no forced default surface. A first-time or cleared workstream may be blank with controls to Show dashboard, Show agents, and use the composer.

## Surface inventory

1. `surface-agent-admin-blank` — blank workstream state.
2. `surface-agent-admin-dashboard` — optional dashboard.
3. `surface-agent-admin-agent-list` — filterable agent list.
4. `surface-agent-admin-agent-detail` — one generated agent's behavior profile, docs, assignments, and traces.
5. `surface-agent-admin-agent-profile-history` — versioned agent behavior profile history including model config, prompt, skill assignments, and allowed generated tools.
6. `surface-agent-admin-prompt-doc` — prompt doc view/edit/proposal.
7. `surface-agent-admin-skill-library` — independently managed tenant-scoped skill catalog.
8. `surface-agent-admin-skill-doc` — skill doc view/edit/proposal.
9. `surface-agent-admin-skill-assignment` — assign/unassign skills for one generated agent through a behavior-profile version.
10. `surface-agent-admin-tool-assignment` — assign/unassign static generated tools for one generated agent through a behavior-profile version.
11. `surface-agent-admin-skill-reference-doc` — governed reference doc view/edit/proposal.
12. `surface-agent-admin-edit-session` — editing-agent proposal/refinement session.
13. `surface-agent-admin-proposal-review` — behavior-change proposal review/activation/decision-card routing.
14. `surface-agent-admin-version-history` — version list and historical version view.
15. `surface-agent-admin-version-diff` — selected version vs immediate predecessor.
16. `surface-agent-admin-create-skill` — skill-library create proposal flow.
17. `surface-agent-admin-delete-skill-confirmation` — deprecation-by-default skill removal confirmation.
18. `surface-agent-admin-create-reference-doc` — create governed reference proposal flow.
19. `surface-agent-admin-delete-reference-doc-confirmation` — permanent/deprecation reference delete confirmation.
20. `surface-agent-admin-runtime-traces` — runtime profile/prompt/skill/reference/tool read traces.
21. `surface-agent-admin-system-message` — denial, refusal, clarification, unavailable, stale, or recovery message.

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

- filters: agent name, workstream/domain, placement, lifecycle status, steward, authority level, scope provenance;
- rows: agent name, short purpose, placement, lifecycle status, safe model alias summary, scope provenance (`global` or tenant-specific), last behavior change time;
- row action: open agent detail.

States: loading, ready, empty-no-agents, empty-no-filter-matches, forbidden, validation-error, failure.

## `surface-agent-admin-agent-detail`

Purpose: inspect one existing agent and choose which doc to view/edit.

Default payload:

- generated agent id/name and static app-description provenance;
- agent purpose;
- placement (`functional_context_area`, `internal_worker`, evaluator, autonomous/background, system/foundation, or future generated placement);
- lifecycle status, steward/owner, and authority level;
- scope provenance showing whether the resolved behavior profile is global or tenant-specific;
- safe `ModelConfigRef`/model policy alias summary, with no secrets;
- active prompt link;
- compact `AgentSkillManifest` and `AgentReferenceManifest` summaries;
- allowed generated tool list and safe `ToolPermissionBoundary` summary showing categories/adapter classes, not hidden internals;
- clickable assigned skill list with skill name and purpose/description;
- each skill may show governed references as clickable nested rows;
- behavior-profile version/history entry point;
- trace entry points for this agent.

Actions: update model config reference, open prompt doc, open skill library, open skill assignment, open generated tool assignment, open skill doc, create skill proposal, deprecate skill, open reference doc, create reference doc under a skill/manifest, open runtime traces, open proposal review.

No whole-agent create/delete action appears here. Generated agent identity, generated tool implementations, and static app-description placement are read-only. Model config reference, prompt, skill assignment, and generated tool assignment changes create a new tenant-scoped behavior profile version for the selected tenant; they do not alter global generated defaults unless the selected scope is the reserved `saas-app-owner`/global maintenance scope explicitly used for app-owner governance.

## `surface-agent-admin-agent-profile-history`

Purpose: browse immutable versions of a generated agent's behavior profile.

Default payload: profile version rows showing version number, scope (`global`, reserved `saas-app-owner`, or tenant id), active prompt version, model config reference, assigned skill ids/version refs, allowed generated tool ids, created/reviewed/activated metadata, provenance, and trace links. Historical profile versions are read-only. Restore creates a profile restore proposal that, when activated, creates a new active behavior-profile version.

Scope rule: generated agents initially use the global profile from app-description defaults. The first tenant-specific change clones the global profile into the tenant scope and activates the changed tenant profile. SaaS app owners operate through the reserved `saas-app-owner` tenant scope.

## `surface-agent-admin-prompt-doc`

Purpose: view and improve the selected agent's prompt.

Default payload: current active prompt Markdown content, current version number/status, created/reviewed/activated metadata, edit request/transcript summary that created the version, risk/authority-expansion summary where present, version history access, runtime trace access, and free-form edit input enabled only for the current/latest editable draft or active document.

Edit action opens or appends to `surface-agent-admin-edit-session` for doc type `prompt`.

Historical view: read-only banner, content, metadata, edit request/transcript summary, optional diff action, restore action.

## `surface-agent-admin-skill-library`

Purpose: browse and manage the tenant-scoped governed skill library independent of any single agent.

Default payload: filters by skill name, purpose/description, lifecycle status, assigned agent count, last changed time, and scope; rows show skill id/name, purpose/description, compact manifest hint, lifecycle status, assigned agent count, last active version, and trace/history entry points. Actions include open skill doc, create skill, deprecate/remove skill, and view assigned agents.

Skills may be assigned to many agents. Creating or editing a skill changes the skill document/version. Assigning or unassigning a skill changes the target agent's behavior-profile version.

## `surface-agent-admin-skill-doc`

Purpose: view and improve one skill.

Default payload: skill name, editable purpose/description, compact manifest hint, current active skill Markdown content, current version number/status, created/reviewed/activated metadata, edit request/transcript summary, risk/authority-expansion summary where present, version history access, reference list, runtime trace access, and free-form edit input enabled only for the current/latest editable draft or active document.

Edit action opens or appends to `surface-agent-admin-edit-session` for doc type `skill`.

Historical view: read-only banner, content, metadata, edit request/transcript summary, optional diff action, restore action.

## `surface-agent-admin-skill-assignment`

Purpose: modify the selected generated agent's allowed skill list.

Default payload: selected agent, resolved scope, currently assigned skills, available active tenant-scoped skills, deprecated/unavailable skill warnings, proposed added/removed skills, profile version impact, trace impact, and confirmation action. Activating an assignment change creates a new versioned agent behavior profile for the selected tenant scope. Skill assignment changes are part of the agent behavior profile version history, not the skill document version history.

## `surface-agent-admin-tool-assignment`

Purpose: modify the selected generated agent's allowed static generated tool list.

Default payload: selected agent, resolved scope, current allowed generated tools, available generated tool catalog entries from app-description/code generation, safe tool purpose/category summaries, proposed added/removed tools, generated tool implementation provenance, profile version impact, trace impact, and confirmation action. Activating a tool assignment change creates a new versioned agent behavior profile for the selected tenant scope. Tool assignment changes are protected and audited but are not automatically authority-expanding solely because a tool is added; runtime backend authorization and tool-boundary enforcement remain authoritative.

No action on this surface creates, edits, or deletes tool code.

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

Purpose: create a new tenant-scoped library skill independent of any single agent.

Inputs: skill name, purpose/description, compact manifest hint, and free-form initial content request. The editing agent drafts the initial skill Markdown content and proposal metadata. Save draft creates a non-active skill proposal; activation creates the skill and first active version. Optional follow-up assignment to one or more agents creates separate agent behavior-profile versions. Cancel returns to the skill library or agent detail entry point.

## `surface-agent-admin-delete-skill-confirmation`

Purpose: confirm skill deprecation or permanent deletion according to lifecycle policy.

Default payload: skill name, deprecation-by-default warning, assigned-agent effect, manifest membership effect, count/list of references that will also be deprecated/deleted or require reassignment, required typed confirmation, and trace impact. Confirm performs the configured lifecycle action after backend authorization and idempotency checks. Hard deletion is allowed only when lifecycle policy explicitly permits it; permanent deletion has no restore.

## `surface-agent-admin-create-reference-doc`

Purpose: create a governed reference associated with a skill/reference manifest.

Inputs: reference title/name, short description and when-to-consult hint used by the model to decide whether to read it, optional access/redaction classification, and free-form initial content request. The editing agent drafts initial Markdown content and proposal metadata. Save draft creates a non-active reference proposal; activation creates the reference and first active version. Cancel returns to skill doc.

## `surface-agent-admin-delete-reference-doc-confirmation`

Purpose: confirm reference deprecation or permanent deletion according to lifecycle policy.

Default payload: reference title/name, deletion/deprecation warning, manifest membership effect, required typed confirmation, and trace impact. Confirm performs the configured lifecycle action after backend authorization and idempotency checks. Permanent deletion has no restore.

## `surface-agent-admin-runtime-traces`

Purpose: show runtime profile resolution, prompt assembly, `readSkill`, `readReferenceDoc`, and generated-tool assignment trace metadata for all agents.

Filters: agent, skill/reference doc, time range, allowed/denied decision, runtime mode.

Rows show: agent name, resolved profile scope/version, prompt/skill/reference doc read, manifest assignment status, allowed generated tool decision where applicable, version/checksum where allowed, safe model alias, tool-boundary decision category, timestamp, request/session id, and user/customer context. Full skill/reference content read at runtime is not shown in trace rows.

Placement: accessible from agent detail, each doc page, and separate trace surface.

## `surface-agent-admin-system-message`

Purpose: show denial, clarification, unsupported request, provider unavailable, stale version, deleted doc, or safe alternative messages.

Unsafe, authority-expanding, or out-of-scope edit requests should explain the issue and propose a safer alternative or review route. Missing SaaS admin authority denies access. Provider/model unavailable, inactive model config, missing tool-boundary grant, stale proposal, deleted artifact, and hidden target states must fail closed without fake success or hidden enumeration.

## Tests

Surface tests must cover blank entry, dashboard on demand, proposal-count routing, all-agent list filtering, agent detail profile summary, generated/global-to-tenant scope provenance, separate prompt/skill-library/skill-assignment/tool-assignment/reference doc surfaces, edit input enabled only on current/latest editable artifacts, historical read-only views, behavior-profile versioning for model config/prompt/skill/tool assignment changes, version-to-previous diff semantics, iterative editing session, save draft/cancel/proposal review/activation/rejection, restore proposal, skill/reference create/delete/deprecate, SaaS-admin-only access, runtime profile/prompt/skill/reference/tool traces, safe model/tool-boundary summaries, and Markdown preservation expectations.
