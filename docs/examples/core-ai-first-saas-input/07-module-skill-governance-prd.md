# Module 5 PRD: Skill Governance

## Status

Detailed PRD for the fifth MVP module in the progressive core AI-first SaaS seed app.

Read first:

- `00-document-development-process-context.md`
- `01-core-seed-progression-plan.md`
- `02-persistent-discussion-capture.md`
- `03-module-auth-app-access-prd.md`
- `04-module-user-admin-prd.md`
- `05-module-agent-definition-prd.md`
- `06-module-prompt-governance-prd.md`

## 1. Module purpose

Module 5 introduces governed skills for application-managed agents.

A skill is a durable, versioned guidance document that an agent may load during execution through an approved tool instead of receiving all guidance in its initial system prompt. This module creates the tenant-scoped skill catalog, skill versioning, per-agent skill manifests, and the `readSkill(skillId)` tool contract required for later governed agent execution.

This module establishes skill governance and a minimal demonstration path. It does not yet implement broad production orchestration, full work trace timelines, evaluator agents, or closed-loop improvement.

## 2. User-visible outcome

At completion, an authorized admin or agent steward can:

1. open a Skill Governance area;
2. create a governed skill document with name, purpose, body, tags, and intended use;
3. edit, review, approve, activate, deprecate, and inspect skill versions;
4. assign approved skill versions to an AgentDefinition through an allowed skill manifest;
5. inspect which skills an agent is allowed to load;
6. run a minimal agent test flow where the agent sees a compact skill manifest and loads an approved skill through `readSkill(skillId)`;
7. see skill lifecycle, manifest, and skill-load activity in audit/trace records;
8. verify that unauthorized users, cross-tenant agents, disabled skills, and unassigned skills are denied.

## 3. MVP boundaries

### In scope

- Tenant-scoped skill catalog.
- Governed skill document lifecycle and immutable skill version snapshots.
- Skill editor, review, activation, history, and diff UI.
- Per-agent allowed skill manifest.
- Compact skill manifest included in prompt/test context.
- `readSkill(skillId)` tool contract with tenant, agent, version, status, and manifest authorization checks.
- Skill usage audit/trace event for every skill load.
- Minimal test console proving approved skill loading and denial for unapproved/unassigned skills.
- Backend authorization for skill governance routes and tool calls.
- Tests for skill versioning, manifest enforcement, unauthorized skill denial, tenant isolation, audit/trace, and frontend secret boundary.

### Out of scope for Module 5

- Full production multi-agent orchestration.
- Skill marketplace, global public skill sharing, or cross-tenant skill publishing.
- Rich skill dependency resolution.
- Automatic skill recommendation or AI-authored skill changes without human review.
- Full work trace timeline UI beyond basic audit/trace evidence.
- Evaluator agents, replay/simulation, canary rollout, and closed-loop improvement.
- External MCP tool governance beyond documenting tool category boundaries.

## 4. Actors

| Actor | Description | Module 5 expectations |
|---|---|---|
| Tenant Admin | Admin with skill governance capabilities. | Can manage skills, versions, manifests, and audit. |
| Agent Steward | Owner/steward of an AgentDefinition. | Can propose/edit skills and assign allowed skills if permitted. |
| Skill Reviewer / Approver | User responsible for reviewing skill changes. | Can review diffs, approve/reject, and activate skill versions. |
| Tenant Member | User without skill governance capabilities. | Cannot read or mutate skill governance artifacts by default. |
| Auditor/Admin Reviewer | User with read-only governance/audit capability. | Can inspect skill versions, manifests, and skill-load events. |
| Agent Runtime | Agent execution context. | Can load only manifest-allowed active skill versions through `readSkill(skillId)`. |

## 5. Authorization and capability model

Required capabilities:

- `skills.read` — view skill documents and versions.
- `skills.create` — create skill documents.
- `skills.draft` — edit draft skill content.
- `skills.submit_review` — submit skill version for review.
- `skills.review` — approve or reject skill versions.
- `skills.activate` — activate approved skill versions.
- `skills.deprecate` — deprecate skill versions/documents.
- `skills.manifest.read` — view per-agent skill manifests.
- `skills.manifest.manage` — assign/remove approved skills for an AgentDefinition.
- `skills.test` — run skill-loading test console.
- `skills.audit.read` — inspect skill-related audit/trace events.

Recommended initial role mapping:

| Role | Module 5 capabilities |
|---|---|
| Tenant Admin | All Module 5 capabilities. |
| Agent Steward | `skills.read`, `skills.create`, `skills.draft`, `skills.submit_review`, `skills.manifest.read`, optional `skills.manifest.manage` for owned agents. |
| Skill Reviewer | `skills.read`, `skills.review`, `skills.activate`, `skills.test`. |
| Auditor | `skills.read`, `skills.manifest.read`, `skills.audit.read`. |
| Member | No skill governance capabilities by default. |

Rules:

- Backend checks are authoritative.
- Skill documents and manifests are tenant-scoped.
- Agents can load only active approved versions assigned in their manifest.
- A prompt cannot authorize loading a skill not present in the manifest.
- Skill text is trusted internal guidance for the current task but is lower authority than platform policy, backend authorization, and tenant policy.
- Skill loading does not grant tool permissions by itself.

## 6. Durable objects and state ownership

### SkillDocument

Represents the canonical current state of a governed skill.

Required fields:

- `skillDocumentId`
- `tenantId`
- name
- slug or stable skill key
- purpose/description
- tags/categories
- intended agent/use cases
- lifecycle status: `DRAFT`, `IN_REVIEW`, `APPROVED`, `ACTIVE`, `DEPRECATED`, `ARCHIVED`
- current draft content
- active version number if activated
- latest version number
- owner/steward account id
- created by / updated by
- created/updated timestamps
- review metadata
- activation metadata

State owner expectation: Event Sourced Entity because skills are behavior-shaping governed documents.

### SkillVersion

Immutable snapshot of skill content and metadata.

Required fields:

- `skillDocumentId`
- `version`
- `tenantId`
- name and slug at version time
- content body
- content checksum
- tags/categories
- status at creation/activation
- change summary
- created by
- created timestamp
- review/approval metadata
- activation metadata

State owner expectation: immutable Key Value Entity snapshot populated from SkillDocument events.

### AgentSkillManifest

Defines which skill versions an AgentDefinition may load.

Required fields:

- `manifestId`
- `tenantId`
- `agentDefinitionId`
- manifest status: `DRAFT`, `ACTIVE`, `DEPRECATED`
- allowed skill entries: skill id/slug, version pin or active-version policy, purpose note
- denied/removed skill entries if useful for audit
- created/updated by
- active version/revision
- timestamps

State owner expectation: Event Sourced Entity or Key Value Entity with strong audit. Event Sourced Entity is preferred if manifest changes are consequential governance changes.

### SkillLoadTrace

Records a runtime/test skill load through `readSkill(skillId)`.

Required fields:

- trace id
- tenant id
- agentDefinitionId
- skillDocumentId
- skill version
- manifest id/version
- caller/session/test context id
- requested skill id
- authorization decision
- denial reason if denied
- timestamp
- correlation id

State owner expectation: audit/trace event compatible with Module 6 work trace expansion.

## 7. Capabilities

### 7.1 Skill catalog

Authorized users can view tenant skills.

List columns:

- skill name;
- status;
- active version;
- tags;
- owner;
- last updated;
- assigned agent count.

Filters:

- status;
- tag/category;
- owner;
- text search;
- assigned/unassigned.

### 7.2 Skill creation and editing

Authorized users can create and edit draft skills.

Required fields:

- name;
- purpose;
- skill body;
- tags/categories;
- intended use;
- change summary for version submission.

Validation:

- name and purpose required;
- body required before review;
- slug stable and tenant-unique;
- unknown dangerous placeholders rejected or flagged;
- secret-like values rejected or blocked;
- content must not claim authority to bypass platform policy, backend authorization, or tool boundaries.

### 7.3 Review, activation, version history, and diff

Skill governance mirrors prompt governance:

- submit draft for review;
- create immutable SkillVersion snapshot;
- approve/reject with notes;
- activate approved version;
- deprecate old versions;
- inspect version history;
- compare versions with textual diff;
- prevent editing immutable snapshots.

### 7.4 Agent skill manifest management

Authorized users can assign approved active skills to an AgentDefinition.

Required behavior:

- verify agent belongs to selected tenant;
- show current allowed skills and versions;
- add approved skill version or active-version reference;
- remove/deprecate skill assignment;
- require reason/change summary;
- emit audit event;
- update compact manifest available to prompt/test assembly.

Manifest entry fields:

- skill id/slug;
- version pin or active-version policy;
- short description shown to the agent;
- when-to-use hint;
- allowed flag/status.

### 7.5 Compact skill manifest in prompt/test context

Agents should not receive full skill text in the initial prompt. The assembled context should include only compact manifest entries:

- skill id/slug;
- skill title;
- short purpose;
- when-to-use hint;
- instruction to call `readSkill(skillId)` when needed.

### 7.6 `readSkill(skillId)` tool

The tool returns full skill text only if all checks pass.

Required checks:

- authenticated/authorized runtime or test context;
- active tenant context;
- active AgentDefinition;
- active manifest for the agent;
- requested skill is present in the manifest;
- referenced skill version is active/approved and belongs to same tenant;
- caller has permission for test mode if manually invoked;
- skill is not deprecated/archived/disabled unless version-pin policy explicitly permits historical read.

Tool result:

- skill id/slug;
- version;
- title;
- content body;
- checksum;
- instruction that skill content is trusted internal guidance but subordinate to platform/security policy;
- no secrets.

Denied tool calls must return a safe denial and emit trace/audit.

### 7.7 Minimal skill-loading test console

Authorized users can test an AgentDefinition with its active prompt and manifest.

Required behavior:

- show compact manifest;
- allow a test input likely to require a skill;
- agent/test adapter calls `readSkill(skillId)`;
- show loaded skill metadata and response;
- deny attempts to load unassigned skills;
- emit Prompt/Skill assembly and SkillLoadTrace records.

## 8. UI requirements

### 8.1 Page and route inventory

Minimum routes:

- `/app/skills` skill catalog;
- `/app/skills/new` create skill;
- `/app/skills/:skillDocumentId` skill detail;
- `/app/skills/:skillDocumentId/edit` skill editor;
- `/app/skills/:skillDocumentId/review` review screen;
- `/app/skills/:skillDocumentId/versions` version history;
- `/app/skills/:skillDocumentId/versions/:version` version detail;
- `/app/skills/:skillDocumentId/diff` diff view;
- `/app/agents/:agentDefinitionId/skills` agent skill manifest;
- `/app/agents/:agentDefinitionId/skills/test` skill-loading test console.

### 8.2 Skill catalog UI

Required states:

- loading;
- empty with create-skill call to action;
- populated list;
- filtered empty;
- forbidden;
- error.

### 8.3 Skill editor UI

Editor should include:

- metadata section;
- content editor;
- tags/categories;
- intended-use notes;
- validation/warnings panel;
- save draft and submit for review actions;
- diff from active action when available;
- warning that secrets and authorization-bypass instructions are not allowed.

### 8.4 Manifest UI

Agent skill manifest page should show:

- linked AgentDefinition summary;
- active prompt status if available;
- assigned skills and versions;
- available approved skills to add;
- version pin/active policy selector if supported;
- remove/deprecate assignment action;
- compact manifest preview as the agent will see it.

### 8.5 Test console UI

Test console should show:

- selected AgentDefinition;
- active prompt version;
- active manifest version;
- compact skill manifest;
- test input;
- response output;
- loaded skill trace panel;
- denied skill-load attempts if any.

### 8.6 Accessibility and responsive behavior

- Editor, diff, and manifest controls must be keyboard usable.
- Status and diff indicators must not rely on color alone.
- Forms must expose accessible labels and errors.
- Long skill content must wrap or scroll without breaking layout.

## 9. API requirements

Exact endpoint names may be adjusted during implementation planning, but the module must cover these contracts.

### Skill documents

- `GET /api/skills` — list tenant skills.
- `POST /api/skills` — create skill document.
- `GET /api/skills/{skillDocumentId}` — skill detail.
- `PUT /api/skills/{skillDocumentId}/draft` — save draft.
- `POST /api/skills/{skillDocumentId}/submit-review` — submit for review.
- `POST /api/skills/{skillDocumentId}/approve` — approve version.
- `POST /api/skills/{skillDocumentId}/reject` — reject version.
- `POST /api/skills/{skillDocumentId}/activate` — activate approved version.
- `POST /api/skills/{skillDocumentId}/deprecate` — deprecate skill/version.
- `POST /api/skills/{skillDocumentId}/archive` — archive skill document.

### Skill versions and diff

- `GET /api/skills/{skillDocumentId}/versions` — list versions.
- `GET /api/skills/{skillDocumentId}/versions/{version}` — immutable version detail.
- `GET /api/skills/{skillDocumentId}/diff?from=&to=` — diff two versions.

### Agent skill manifest

- `GET /api/agents/{agentDefinitionId}/skill-manifest` — get manifest.
- `PUT /api/agents/{agentDefinitionId}/skill-manifest` — replace/update manifest draft.
- `POST /api/agents/{agentDefinitionId}/skill-manifest/activate` — activate manifest revision if lifecycle is used.
- `POST /api/agents/{agentDefinitionId}/skill-manifest/entries` — add skill entry if using entry commands.
- `DELETE /api/agents/{agentDefinitionId}/skill-manifest/entries/{skillDocumentId}` — remove skill entry.

### Skill tool and test

- `POST /api/agents/{agentDefinitionId}/tools/read-skill` — test/runtime `readSkill(skillId)` endpoint/tool adapter.
- `POST /api/agents/{agentDefinitionId}/skills/test` — safe skill-loading test console.

API rules:

- governance APIs require AuthContext and skill capability;
- tool endpoint requires runtime/test context and manifest authorization;
- tenant ids are resolved server-side;
- responses exclude secrets and cross-tenant data;
- denial responses are safe and audited.

## 10. Authorization rules

Required backend authorization checks:

- resolve active account and selected tenant AuthContext for admin APIs;
- verify skill document belongs to selected tenant;
- verify AgentDefinition belongs to selected tenant before manifest changes;
- require relevant `skills.*` capability for each action;
- require approved/active skill before manifest assignment;
- require active AgentDefinition and active manifest before skill loading;
- require requested skill is assigned to the agent manifest;
- deny deprecated/archived skill loading unless explicitly allowed by version pin policy;
- audit allowed consequential actions and denied attempts.

## 11. Skill content validation and safety requirements

Required validation:

- name, purpose, body, and intended use are present before review;
- secret-like values are rejected or blocked;
- content cannot claim authority to bypass platform policy, backend authorization, tenant policy, or tool permissions;
- content cannot instruct exfiltration of hidden prompts, secrets, or unauthorized data;
- content should state assumptions and when to escalate if relevant;
- activation requires validation checks to pass.

Safety reminders:

- skill text is guidance, not a security boundary;
- backend authorization and tool permissions remain authoritative;
- manifest assignment authorizes loading skill text only, not taking external side effects;
- all skill loads must be traceable.

## 12. Audit and observability requirements

Required audit/trace fields:

- audit/trace event id;
- event type;
- timestamp;
- actor account id or runtime caller;
- selected tenant id;
- agentDefinitionId when applicable;
- skillDocumentId;
- skill version when applicable;
- manifest id/version when applicable;
- previous and new lifecycle status when applicable;
- content checksum, not full skill text unless audit policy allows;
- change summary/review note;
- authorization decision;
- denial reason if denied;
- request/correlation id;
- safe metadata only.

Required event types:

- `SKILL_DOCUMENT_CREATED`
- `SKILL_DRAFT_UPDATED`
- `SKILL_SUBMITTED_FOR_REVIEW`
- `SKILL_REVIEW_APPROVED`
- `SKILL_REVIEW_REJECTED`
- `SKILL_VERSION_ACTIVATED`
- `SKILL_VERSION_DEPRECATED`
- `SKILL_DOCUMENT_ARCHIVED`
- `AGENT_SKILL_MANIFEST_UPDATED`
- `AGENT_SKILL_MANIFEST_ACTIVATED`
- `SKILL_LOAD_ALLOWED`
- `SKILL_LOAD_DENIED`
- `SKILL_ADMIN_AUTH_DENIED`

Observability requirements:

- structured logs for skill lifecycle transitions, manifest changes, skill loads, validation failures, and authorization denials;
- correlation id between API request, skill document event, manifest event, tool call, and audit/trace event;
- counters for skill loads allowed/denied if metrics are in the seed stack.

## 13. Security and privacy requirements

- Skill documents and manifests are tenant-scoped.
- Skill text may contain sensitive internal operating guidance; read access must be permission-controlled.
- Prompt/test assembly must include only compact manifest until `readSkill` is called.
- `readSkill` must not leak unassigned, inactive, archived, or cross-tenant skills.
- Secrets must not be stored in skill text, manifests, API responses, frontend bundles, or trace payloads.
- Denial errors must not reveal cross-tenant skill existence.
- Skill content must be treated as untrusted for authorization purposes; it cannot override platform policy.

## 14. Acceptance scenarios

### Scenario 1: Admin sees empty skill catalog

Given a tenant admin with `skills.read` signs in before skills exist, when they open Skills, then they see an empty state with create-skill action.

### Scenario 2: Non-skill user is forbidden

Given a member lacks `skills.read`, when they open Skills or call skill APIs, then access is forbidden and audited.

### Scenario 3: Create draft skill

Given a user has `skills.create`, when they submit valid skill metadata, then a SkillDocument is created in draft state and audit is emitted.

### Scenario 4: Skill validation blocks unsafe content

Given skill body contains secret-like values or authorization-bypass instructions, when submitted for review, then validation blocks or flags the content according to accepted policy.

### Scenario 5: Review and activate skill version

Given a valid draft skill exists, when it is submitted, approved, and activated, then an immutable SkillVersion exists, active version is updated, and audit events are emitted.

### Scenario 6: Version history and diff work

Given multiple skill versions exist, when an authorized user opens history and compares versions, then immutable version details and textual diff are shown.

### Scenario 7: Assign skill to agent manifest

Given an active AgentDefinition and active approved SkillVersion exist, when an authorized user adds the skill to the agent manifest, then the manifest updates and audit is emitted.

### Scenario 8: Manifest rejects inactive skill

Given a skill is draft, in review, deprecated, or archived, when a user tries to assign it to a manifest, then the command is rejected unless explicit version-pin policy allows it.

### Scenario 9: Agent loads assigned skill

Given an agent has an active manifest containing an approved skill, when the test flow calls `readSkill(skillId)`, then full skill content is returned with version/checksum and `SKILL_LOAD_ALLOWED` trace is emitted.

### Scenario 10: Agent cannot load unassigned skill

Given a skill exists but is not in the agent's manifest, when `readSkill(skillId)` is called, then the call is denied safely and `SKILL_LOAD_DENIED` is emitted.

### Scenario 11: Cross-tenant skill load is denied

Given an agent in Tenant A requests a skill from Tenant B, when the tool call is processed, then the call is denied without leaking Tenant B details and audited.

### Scenario 12: Disabled agent cannot load skills

Given an AgentDefinition is disabled or archived, when the test/runtime attempts `readSkill`, then the call is denied and audited.

## 15. Test requirements

Minimum test coverage:

- Skill catalog empty, populated, filtered, forbidden, and error UI states.
- Create SkillDocument success and audit emission.
- Skill draft validation for required fields and unsafe content.
- Submit/review/approve/reject/activate lifecycle transitions.
- Version list/detail immutability and diff behavior.
- Tenant isolation for skill read/update/version APIs.
- Capability denials for read, create, draft, review, activate, manifest manage, and test.
- Manifest add/remove/update success and audit emission.
- Manifest assignment denial for inactive/unapproved/cross-tenant skills.
- `readSkill` success for manifest-assigned active skill.
- `readSkill` denial for unassigned, inactive, archived, cross-tenant, and disabled-agent cases.
- Skill load trace/audit records include skill version, manifest version, agent id, tenant id, and correlation id.
- Test console shows compact manifest and loaded skill metadata.
- Frontend bundle/static asset test verifies no provider/model/backend secrets are exposed.

## 16. Akka decomposition notes

This section is input for later `akka-prd-to-specs-backlog` and implementation planning. It is not the final design.

Likely Akka components:

- Event Sourced Entity for `SkillDocument` lifecycle and current state.
- Immutable Key Value Entity for `SkillVersion` snapshots.
- Event Sourced Entity or Key Value Entity for `AgentSkillManifest`.
- Views for skills by tenant/status/tag, skill versions by document, manifests by agent, and assigned skills by tenant.
- Consumer from skill/manifest events to audit/trace records if not emitted directly.
- Agent tool adapter for `readSkill(skillId)`.
- Minimal Agent/test endpoint to demonstrate compact manifest plus skill loading.
- HTTP endpoints for skill documents, versions, diffs, manifests, `readSkill`, and test console.
- React/Vite/TypeScript UI for skill catalog, editor, review, diff/history, manifest, and test console.

Implementation guidance:

- Reuse Module 1 AuthContext, Module 2 admin audit, Module 3 AgentDefinition, and Module 4 prompt assembly patterns.
- Use the two-entity governed-document pattern: Event Sourced current document plus immutable version snapshots.
- Keep full skill content out of initial prompts; include compact manifest only.
- Enforce manifest authorization in backend tool code, not prompt instructions.
- Emit skill-load trace from the tool path.
- Keep production execution broadening deferred; the MVP test console proves the pattern safely.

## 17. Demo flow

A successful Module 5 demo should run as follows:

1. Sign in as Tenant Admin or Agent Steward.
2. Open Skills and create a draft skill such as "Summarize Admin Audit Events".
3. Submit, approve, and activate the skill version.
4. Open an active AgentDefinition from Module 3.
5. Open its Skill Manifest and assign the approved skill.
6. Open the test console and inspect the compact manifest.
7. Run a test that triggers `readSkill(skillId)` and shows loaded skill metadata.
8. Attempt to load an unassigned or cross-tenant skill and see safe denial.
9. Open admin audit/trace and inspect skill lifecycle, manifest update, and skill-load events.
10. Run tests proving versioning, manifest enforcement, unauthorized skill denial, tenant isolation, audit/trace, and frontend secret boundary.

## 18. Explicit defers to later modules

Deferred to Module 6 Audit and Work Trace:

- full skill-load and agent execution timeline;
- unified prompt/skill/model/tool/data-access traces;
- redaction policy UI;
- trace search and detail pages.

Deferred to Module 7 Evaluation and Closed-Loop Improvement:

- evaluator agents for skill quality;
- skill improvement proposals;
- replay/simulation before activation;
- canary activation and rollback;
- AI-assisted skill drafting with human-governed commits.

Deferred to later runtime modules:

- production multi-agent orchestration;
- external MCP tool permission governance;
- cross-tenant/global skill publishing;
- skill dependency management;
- advanced skill recommendation.

## 19. Readiness checklist

Module 5 is ready for decomposition when the following are true:

- [ ] Module 1 AuthContext, Module 2 admin audit, Module 3 AgentDefinition, and Module 4 prompt assembly assumptions are accepted.
- [ ] SkillDocument and SkillVersion fields are accepted.
- [ ] Skill lifecycle states and transitions are accepted.
- [ ] AgentSkillManifest fields and version pin/active policy are accepted.
- [ ] `readSkill(skillId)` authorization checks and tool result contract are accepted.
- [ ] Compact manifest format is accepted.
- [ ] Skill content validation and secret-boundary rules are accepted.
- [ ] Test console scope is accepted as safe demonstration, not production orchestration.
- [ ] UI route inventory and catalog/editor/review/diff/manifest/test states are accepted.
- [ ] Audit/trace event coverage is accepted.
- [ ] Tenant isolation, capability denial, versioning, manifest enforcement, skill-load denial, audit/trace, and frontend secret-boundary tests are accepted.
- [ ] Deferred work trace, evaluation, and production orchestration features are confirmed as not part of Module 5.
