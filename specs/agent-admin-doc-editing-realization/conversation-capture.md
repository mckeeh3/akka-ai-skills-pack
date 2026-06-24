# Conversation capture: Agent Admin doc-editing realization

## Source discussion

The Agent Admin workstream was revised from a broad managed-agent governance console into a SaaS-admin-only AI-assisted document editing workspace. The updated app-description is committed in `4f6ddfb4 Reframe Agent Admin as AI-assisted doc editing`.

## Confirmed decisions

- Agent Admin is for **SaaS Owner/Admin users only**.
- Tenant/organization/customer admins are not Agent Admin operators.
- Agent Admin applies to **all agents**, not only the foundation workstream agents.
- Users think in terms of **improving agent behavior**, not editing internal prompt/skill/reference machinery.
- The primary flow is: list/filter agents → open one agent → open prompt/skill/reference doc → describe desired behavior improvement → editing agent proposes content → user iterates → Save or Cancel.
- There is no forced default surface. The workstream persists previous surfaces. First open/cleared state may be blank with Show dashboard and Show agents controls.
- Optional dashboard has only `things you can do`: total number of agents as a clickable card and top five most recently changed agents. No `needs attention` queue is currently defined.
- Agent list rows show agent name, short purpose, and last edit time. Filters: agent name and workstream/domain.
- Agent detail shows editable agent name/purpose, prompt link, clickable skills, reference docs under skills, and runtime trace entry points.
- Whole agents cannot be created or deleted in Agent Admin.

## Agent document model

- Every agent has exactly one prompt doc.
- Every agent may have zero or more skills.
- Every skill may have zero or more reference docs.
- Reference docs belong to a specific skill and are shown with that skill.
- Agent docs are initially created by the skills pack.
- SaaS admins may create/update/delete skills.
- SaaS admins may create/update/delete skill reference docs.
- Deleting a skill permanently deletes its reference docs.
- Deleted skills/reference docs cannot be restored.
- Prompt, skill, and reference docs support Markdown.
- The editing agent preserves Markdown and existing structure unless the user asks to reorganize.

## AI-assisted editing flow

- Editing is not direct text editing.
- User provides free-form instructions only; no templates are required.
- One editing agent handles all doc editing, with doc-type-specific skills for prompt, skill, and reference-doc editing.
- The editing agent can read full prompt/skill/reference text for all agents under SaaS admin authority.
- The editing agent may use same-agent context, including prompt, skills, and relevant reference docs.
- The editing agent may ask clarifying questions.
- Unsafe or out-of-scope requests are refused with explanation and safer alternative.
- Proposed edit review shows full proposed document, summary of changes, advisory warnings/risks, and Show diff on demand.
- Users may keep giving more input to refine the proposal.
- Session ends with Save or Cancel.
- Save creates a new current version immediately used at runtime.
- Cancel discards the proposal and returns to the current version.
- Users may Save without viewing the diff.
- Warnings/risks are advisory only and do not block Save for authorized SaaS admins.

## Versioning and diffs

- Version numbers are simple integers.
- Every saved edit creates a new immutable current version.
- Each version records version number, created time, actor, saved content, and the whole editing-session transcript/summary including all user instructions.
- Version history rows need show only the version number.
- Historical version view shows historical content, metadata, edit request/transcript summary, optional diff, and read-only banner.
- Edit input is enabled only on current/latest version.
- Diffs are between selected version `N` and immediate predecessor `N-1`; for example, version 7 diff is between version 7 and version 6.
- Restore this version immediately creates a new current version copied from the historical content and records edit request `Restored from version N`.
- Restore-created versions appear in history.
- There is no undo beyond restore.

## Runtime loading and traces

- Every agent request retrieves the current prompt and appends skill names/descriptions to the prompt context.
- The model decides whether to call `readSkill` based on skill descriptions.
- Loaded skills include reference doc names/descriptions so the model can decide whether to call `readReferenceDoc`.
- All agents have `readSkill` and `readReferenceDoc` tools.
- Agents only know about their own listed skills; there is no discovery path for other agents' skills.
- Runtime `readSkill` / `readReferenceDoc` calls are audited/traced.
- Agent Admin shows read traces directly and may also link to Audit/Trace.
- Trace rows show agent name, skill/reference doc read, timestamp, request/session id, and user/customer context.
- Trace rows do not show the full skill/reference content that was read.
- Trace filters: agent, skill/reference doc, time range.

## Out of scope

- Tenant/organization/customer-scoped Agent Admin.
- Whole-agent creation/deletion.
- Model settings administration.
- Tool permission administration.
- Separate publish/activation/rollback lifecycle.
- Prompt-risk approval gates as save blockers.
- Seed import or customization-preservation workflows in Agent Admin.

## Current app-description authority

Primary files:

- `app-description/domains/core-starter/workstreams/agent-admin/workstream.md`
- `app-description/domains/core-starter/capabilities/agent-doc-administration.md`
- `app-description/domains/core-starter/workstreams/agent-admin/surfaces/surfaces.md`
- `app-description/domains/core-starter/workstreams/agent-admin/behavior.md`
- `app-description/domains/core-starter/workstreams/agent-admin/access.md`
- `app-description/domains/core-starter/workstreams/agent-admin/tools/governed-tools.md`
- `app-description/domains/core-starter/workstreams/agent-admin/traces/work-traces.md`
- `app-description/domains/core-starter/workstreams/agent-admin/tests/coverage.md`
- `app-description/domains/core-starter/workstreams/agent-admin/realization/*.md`
