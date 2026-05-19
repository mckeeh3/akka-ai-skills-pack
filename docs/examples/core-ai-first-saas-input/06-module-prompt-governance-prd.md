# Module 5 PRD: Prompt Governance

## Status

Detailed PRD for the Prompt Governance module in the progressive core AI-first SaaS seed app.

Read first:

- `00-document-development-process-context.md`
- `01-core-seed-progression-plan.md`
- `02-persistent-discussion-capture.md`
- `03-module-auth-app-access-prd.md`
- `03a-module-agent-workstream-runtime-bootstrap-prd.md`
- `04-module-user-admin-prd.md`
- `05-module-agent-definition-prd.md`


## Workstream architecture alignment

This module PRD is interpreted under `10-canonical-core-app-prd.md` and `../../agent-workstream-application-architecture.md`. Any legacy references to pages, screens, navigation, or route inventory mean structured workstream surfaces, surface actions, and route/deep-link implementation details inside the agent workstream shell. They must not be used to generate a page-first admin console or chatbot-bolt-on app.

## 1. Module purpose

This module introduces governed, runtime-managed agent prompts.

The module lets authorized tenant admins and agent stewards create, edit, review, version, activate, and inspect system prompt documents associated with AgentDefinitions from Module 3. It establishes prompts as durable behavior-shaping artifacts rather than hard-coded strings hidden in application code.

This module does not introduce governed skills, full agent work traces, evaluator agents, or closed-loop self-improvement. It focuses on safe prompt lifecycle management and a minimal prompt assembly/test surface that later modules can extend.

## 2. User-visible outcome

At completion, an authorized admin or agent steward can:

1. open a Prompt Governance area from an agent definition;
2. create a prompt document for an agent;
3. edit a draft prompt;
4. review prompt differences between versions;
5. submit a prompt for review or approval;
6. approve and activate a prompt version according to permissions;
7. inspect active and historical prompt versions;
8. run a minimal agent test console using the active prompt version or a selected draft in a safe test mode;
9. see prompt lifecycle actions in admin audit;
10. verify that non-admins, cross-tenant users, and unauthorized agents cannot read or mutate prompt documents.

## 3. MVP boundaries

### In scope

- Tenant-scoped prompt governance UI integrated with Module 3 AgentDefinition detail surfaces.
- Governed prompt document model with draft/review/approved/active/deprecated lifecycle.
- Event-sourced current prompt document state.
- Immutable version snapshots for prompt history and diff UI.
- Prompt editor with validation, preview, diff, review, and activation actions.
- Active prompt reference on AgentDefinition or prompt binding.
- Minimal effective prompt assembly contract.
- Basic agent test console that uses selected prompt content with deterministic/test-safe behavior where possible.
- Prompt lifecycle audit events.
- Authorization for prompt read/edit/review/activate actions.
- Tests for versioning, activation, diff/history, authorization, tenant isolation, audit, and secret boundary.

### Out of scope for Module 4

- Governed skill catalog and per-agent skill manifests.
- `readSkill(skillId)` tool.
- Full runtime tool invocation and data-access tracing.
- Multi-agent orchestration and workflow execution.
- Evaluator agents, rubric management, replay/simulation, and closed-loop improvement proposals.
- Production-grade prompt experimentation, A/B testing, canary rollout, or automatic rollback.
- Full policy governance language and approval policies beyond prompt lifecycle permissions.
- Rich prompt-template variable marketplace or user-defined template engine.

## 4. Actors

| Actor | Description | Module 4 expectations |
|---|---|---|
| Tenant Admin | Active tenant admin with prompt governance capabilities. | Can manage prompt documents, approve/activate versions, and inspect audit. |
| Agent Steward | Owner/steward assigned to an AgentDefinition. | Can draft and possibly submit prompt changes for owned agents. |
| Prompt Reviewer / Approver | User with review/activation capability. | Can review diffs, approve, reject, and activate prompt versions. |
| Tenant Member | Active user without prompt capabilities. | Cannot access Prompt Governance surfaces or APIs. |
| Auditor/Admin Reviewer | User with read-only prompt/audit capability. | Can inspect prompt versions and audit without mutation. |
| Future Agent Runtime | Runtime path that resolves the active prompt version for an agent. | Consumes active version reference and prompt assembly metadata. |

## 5. Authorization and capability model

Module 4 adds prompt-governance capabilities to the existing role model.

Required capabilities:

- `prompts.read` — view prompt documents and versions for selected tenant.
- `prompts.create` — create a prompt document for an allowed AgentDefinition.
- `prompts.draft` — edit draft prompt content and metadata.
- `prompts.submit_review` — submit a draft for review.
- `prompts.review` — approve or reject submitted prompt versions.
- `prompts.activate` — activate an approved prompt version.
- `prompts.deprecate` — deprecate old versions or documents.
- `prompts.test` — run safe prompt test console.
- `prompts.audit.read` — inspect prompt-related audit events; may map to `admin.audit.read`.

Recommended initial role mapping:

| Role | Module 4 capabilities |
|---|---|
| Tenant Admin | All Module 4 capabilities. |
| Agent Steward | `prompts.read`, `prompts.create`, `prompts.draft`, `prompts.submit_review`, optional `prompts.test` for owned agents. |
| Prompt Reviewer | `prompts.read`, `prompts.review`, `prompts.activate`, `prompts.test`. |
| Auditor | `prompts.read`, `prompts.audit.read`. |
| Member | No prompt governance capabilities by default. |

Rules:

- Backend checks are authoritative.
- Prompt documents are tenant-scoped through their owning AgentDefinition.
- Users cannot manage prompts for agents outside their tenant.
- Prompt activation requires explicit capability and an approved version unless a simplified single-admin flow is accepted.
- Drafting prompt text does not grant runtime tool permissions.
- Prompt content is not a security boundary; backend permissions remain authoritative.

## 6. Durable objects and state ownership

### PromptDocument

Represents the canonical current state of a governed prompt attached to an AgentDefinition.

Required fields:

- `promptDocumentId`
- `tenantId`
- `agentDefinitionId`
- name/title
- purpose/description
- prompt type: system, role/persona, task instruction, output format, or other supported category
- lifecycle status: `DRAFT`, `IN_REVIEW`, `APPROVED`, `ACTIVE`, `DEPRECATED`, `ARCHIVED`
- current draft content if applicable
- active version number if activated
- latest version number
- owner/steward account id
- created by / updated by
- created/updated timestamps
- review metadata: submitted by, reviewed by, review notes, rejection reason
- activation metadata: activated by, activated at, replaced version

State owner expectation: Event Sourced Entity. Prompt documents are behavior-shaping governance objects where lifecycle history matters.

### PromptVersion

Immutable snapshot of prompt content and metadata at a version boundary.

Required fields:

- `promptDocumentId`
- `version`
- `tenantId`
- `agentDefinitionId`
- prompt content
- content checksum
- status at creation: draft snapshot, submitted, approved, active, deprecated
- change summary
- created by
- created timestamp
- review/approval metadata if applicable
- activation metadata if applicable

State owner expectation: immutable Key Value Entity or append-only version store populated from PromptDocument events. This follows the two-entity governed-document pattern preserved in the planning context.

### PromptAssemblyTrace

Represents the minimal trace metadata for prompt assembly in test or runtime paths.

For Module 4 this may be a lightweight audit-like record rather than full Module 6 work trace.

Required fields:

- assembly id or correlation id
- tenant id
- agentDefinitionId
- promptDocumentId and version
- model configuration reference
- timestamp
- checksum of assembled prompt
- caller account id
- test/runtime mode

State owner expectation: audit event or lightweight trace record. Full timeline UI is deferred to Module 6.

### AdminAuditEvent

Module 4 expands audit coverage with prompt lifecycle events.

Required event types:

- `PROMPT_DOCUMENT_CREATED`
- `PROMPT_DRAFT_UPDATED`
- `PROMPT_SUBMITTED_FOR_REVIEW`
- `PROMPT_REVIEW_APPROVED`
- `PROMPT_REVIEW_REJECTED`
- `PROMPT_VERSION_ACTIVATED`
- `PROMPT_VERSION_DEPRECATED`
- `PROMPT_DOCUMENT_ARCHIVED`
- `PROMPT_TEST_RUN_STARTED`
- `PROMPT_ASSEMBLED`
- `PROMPT_ADMIN_AUTH_DENIED`

## 7. Capabilities

### 7.1 Prompt navigation and access gate

Agent detail surfaces from Module 3 must show prompt governance entry points to users with `prompts.read`. Direct route/API access must still be backend-enforced.

### 7.2 Prompt document creation

Authorized users can create a prompt document for an AgentDefinition.

Required behavior:

- verify target agent belongs to selected tenant;
- verify user can manage prompts for the agent;
- create initial draft state;
- require purpose/description;
- optionally seed from a safe template;
- emit audit event.

### 7.3 Prompt editor

Authorized users can edit draft prompt content.

Required editor behavior:

- multi-line prompt editing;
- metadata editing: title, purpose, prompt type, change summary;
- validation for required fields;
- secret-like value warning/blocking;
- unsaved changes warning;
- save draft action;
- submit for review action;
- preview assembled prompt if enough data exists.

Prompt text should support deterministic variables only if they are explicitly defined, such as:

- `{{agent.name}}`
- `{{tenant.name}}`
- `{{user.displayName}}`
- `{{currentDate}}`

Unknown variables must be rejected or flagged.

### 7.4 Review and approval

Prompt reviewers can inspect submitted versions and approve or reject them.

Review surface must include:

- submitted prompt content;
- diff from active version or previous version;
- change summary;
- author/submission metadata;
- linked AgentDefinition purpose and authority;
- prompt validation warnings;
- approve/reject actions;
- required review note for rejection.

### 7.5 Activation

Authorized users can activate an approved prompt version.

Activation behavior:

- only approved versions can become active unless single-admin simplified flow is explicitly accepted;
- activation updates active version reference;
- previous active version becomes deprecated or replaced;
- AgentDefinition prompt placeholder/reference is updated if needed;
- audit event captures previous and new versions;
- runtime/test prompt resolution uses the new active version.

### 7.6 Version history and diff

Users with `prompts.read` can inspect prompt history.

Required behavior:

- list versions with status, author, timestamp, change summary, active marker;
- open immutable version detail;
- compare any version to active or previous version;
- show textual diff with additions/removals;
- never allow editing immutable version snapshots.

### 7.7 Effective prompt assembly

Module 4 must define a minimal prompt assembly contract.

Assembly layers for MVP:

1. platform non-negotiable safety prefix from code/config;
2. AgentDefinition purpose/authority/tool-boundary summary;
3. active PromptVersion content;
4. output-format hint if configured;
5. safe user/test input.

Assembly output must include:

- assembled prompt text for backend/test use;
- prompt version ids and checksums;
- model configuration reference;
- correlation id;
- no secrets.

### 7.8 Minimal prompt test console

Authorized users can run a safe test against the active prompt or selected draft.

Required behavior:

- select active prompt or draft version if permitted;
- enter test input;
- show assembled prompt metadata and model/reference used;
- use deterministic test model/provider in local/test when possible;
- show response or validation output;
- emit audit/assembly trace.

This console is for prompt validation, not general production chat.

## 8. UI requirements

### 8.1 Workstream surfaces and route/deep-link inventory

Minimum routes:

- `/app/agents/:agentDefinitionId/prompts` prompt document list/overview;
- `/app/agents/:agentDefinitionId/prompts/new` create prompt document;
- `/app/prompts/:promptDocumentId` prompt detail;
- `/app/prompts/:promptDocumentId/edit` draft editor;
- `/app/prompts/:promptDocumentId/review` review/approval surface;
- `/app/prompts/:promptDocumentId/versions` version history;
- `/app/prompts/:promptDocumentId/versions/:version` version detail;
- `/app/prompts/:promptDocumentId/diff` version diff view;
- `/app/prompts/:promptDocumentId/test` prompt test console.

### 8.2 Prompt overview UI

Prompt overview should show:

- linked AgentDefinition summary;
- active prompt version;
- draft/in-review status;
- last updated by/time;
- warnings if no active prompt exists;
- actions permitted by current user.

### 8.3 Editor UI

Editor should include:

- prompt metadata section;
- text editor area;
- variable reference/help panel;
- validation/warnings panel;
- save draft, submit for review, cancel actions;
- diff from active button when active version exists;
- secret-boundary warning.

### 8.4 Review/diff UI

Review and diff UI should include:

- side-by-side or unified diff;
- metadata changes;
- linked agent purpose/authority context;
- warnings for broadened behavior, unsafe language, unknown variables, or secret-like content;
- approve/reject controls with note capture.

### 8.5 Test console UI

Test console should include:

- prompt version selector constrained by permissions;
- test input field;
- run button;
- response/output panel;
- assembly metadata panel showing prompt version, checksum, model reference, and correlation id;
- warning that test output is not an approval by itself.

### 8.6 Accessibility and responsive behavior

- Editor and diff views must be keyboard usable.
- Form errors and warnings must be accessible.
- Diff colors must include text/icons and not rely on color alone.
- Long prompt text must be readable on narrower screens through wrapping or panels.

## 9. API requirements

Exact endpoint names may be adjusted during implementation planning, but the module must cover these contracts.

### Prompt documents

- `GET /api/agents/{agentDefinitionId}/prompts` — list prompt documents for an agent.
- `POST /api/agents/{agentDefinitionId}/prompts` — create prompt document.
- `GET /api/prompts/{promptDocumentId}` — prompt detail.
- `PUT /api/prompts/{promptDocumentId}/draft` — save draft content/metadata.
- `POST /api/prompts/{promptDocumentId}/submit-review` — submit draft for review.
- `POST /api/prompts/{promptDocumentId}/approve` — approve submitted version.
- `POST /api/prompts/{promptDocumentId}/reject` — reject submitted version.
- `POST /api/prompts/{promptDocumentId}/activate` — activate approved version.
- `POST /api/prompts/{promptDocumentId}/deprecate` — deprecate active/old version if supported.
- `POST /api/prompts/{promptDocumentId}/archive` — archive prompt document if supported.

### Versions and diff

- `GET /api/prompts/{promptDocumentId}/versions` — list versions.
- `GET /api/prompts/{promptDocumentId}/versions/{version}` — immutable version detail.
- `GET /api/prompts/{promptDocumentId}/diff?from=&to=` — diff two versions.

### Assembly and test

- `POST /api/prompts/{promptDocumentId}/assemble-preview` — assemble preview from active/draft version.
- `POST /api/prompts/{promptDocumentId}/test` — run safe prompt test console.

API rules:

- all prompt APIs require AuthContext and relevant prompt capability;
- prompt ids must be resolved to tenant through their AgentDefinition;
- payload tenant ids must not override resolved AuthContext;
- responses must not expose provider secrets, raw tokens, or backend-only policy details;
- errors must be typed for UI states without leaking other-tenant resources.

## 10. Authorization rules

Required backend authorization checks:

- resolve active account and selected tenant AuthContext;
- verify target AgentDefinition belongs to selected tenant;
- require capability for each prompt action;
- enforce owner/steward restrictions if configured;
- require review/activate separation if selected for MVP;
- deny mutation of immutable PromptVersion snapshots;
- deny activation of unapproved or invalid versions;
- deny test console use for disabled/archived AgentDefinitions;
- audit allowed consequential actions and denied attempts.

## 11. Prompt validation and safety requirements

Required validation:

- title and purpose are present;
- prompt content is non-empty for submit/approval;
- unknown variables are rejected or flagged;
- secret-like values are rejected or require explicit admin override if the policy allows;
- prompt content cannot claim authority beyond AgentDefinition authority without warning/blocking;
- prompt content cannot instruct bypassing backend authorization, policies, or audit;
- activation requires validation checks to pass.

Safety reminders:

- prompts guide model behavior but do not enforce security;
- backend authorization remains authoritative;
- tool permissions are governed separately and cannot be expanded by prompt text;
- prompt tests do not prove production safety without later evaluation/replay modules.

## 12. Audit and observability requirements

Required audit fields:

- audit event id;
- event type;
- timestamp;
- actor account id;
- selected tenant id;
- agentDefinitionId;
- promptDocumentId;
- version number when applicable;
- previous and new lifecycle status when applicable;
- content checksum, not full prompt text unless audit policy allows;
- change summary/review note;
- authorization decision;
- denial reason if denied;
- request/correlation id;
- safe metadata only.

Observability requirements:

- structured logs for prompt lifecycle transitions, validation failures, test runs, and authorization denials;
- correlation id between API request, PromptDocument event, PromptVersion creation, assembly trace, and audit event;
- metrics/counters for prompt documents by status if metrics are in the seed stack.

## 13. Security and privacy requirements

- Prompt documents are tenant-scoped through AgentDefinitions and must not leak across tenants.
- Prompt content may contain sensitive business instructions; read access must be permission-controlled.
- Prompt content and diffs must not expose to unauthorized members.
- Raw provider tokens, model API keys, WorkOS secrets, Resend email service secrets, and other credentials must never appear in prompt APIs or frontend bundles.
- Prompt assembly must not include backend-only secrets.
- Error messages must avoid leaking existence of cross-tenant prompt ids.
- Test console inputs/outputs must be treated as potentially sensitive and audited/safely logged.

## 14. Acceptance scenarios

### Scenario 1: Agent steward opens prompt overview

Given an AgentDefinition exists and the user has `prompts.read`, when they open the agent's Prompts tab, then they see prompt overview with no active prompt warning or active version details.

### Scenario 2: Non-prompt user is forbidden

Given a member lacks `prompts.read`, when they open a prompt route or call prompt APIs, then access is forbidden and an audit denial is emitted.

### Scenario 3: Create prompt document

Given a steward has `prompts.create`, when they create a prompt document for an agent in their tenant, then a PromptDocument is created in draft state and audit is emitted.

### Scenario 4: Cross-tenant prompt create is denied

Given a user in Tenant A attempts to create or read a prompt for a Tenant B AgentDefinition, when the request is processed, then it is denied or not found according to security policy and audited.

### Scenario 5: Draft prompt is saved

Given a draft prompt exists, when an authorized user edits content and saves, then the draft updates, revision changes, and a draft-updated audit event is emitted.

### Scenario 6: Unknown variable is blocked

Given prompt text contains an unsupported variable, when the user submits for review, then validation blocks submission or returns actionable warnings according to accepted rule.

### Scenario 7: Secret-like content is blocked

Given prompt text includes a value that looks like an API key or token, when the user saves or submits, then the system rejects or flags it according to the accepted secret-boundary rule.

### Scenario 8: Submit prompt for review

Given a valid draft exists, when the steward submits it for review, then an immutable PromptVersion is created or marked in review, status changes to in review, and audit is emitted.

### Scenario 9: Reviewer approves prompt

Given a prompt version is in review, when a reviewer approves it, then version status becomes approved and audit captures the reviewer and notes.

### Scenario 10: Reviewer rejects prompt

Given a prompt version is in review, when a reviewer rejects it with a reason, then status returns to draft/rejected state according to lifecycle rule and audit captures the rejection reason.

### Scenario 11: Activate approved version

Given an approved prompt version exists, when an authorized user activates it, then it becomes active, prior active version is deprecated/replaced, AgentDefinition prompt reference is updated if needed, and audit is emitted.

### Scenario 12: Activation rejects unapproved prompt

Given a draft or in-review version exists, when activation is requested without approval, then the command is rejected unless the accepted single-admin flow permits it.

### Scenario 13: Version history and diff work

Given multiple prompt versions exist, when the user opens history and compares versions, then immutable version details and textual diff are shown.

### Scenario 14: Test console assembles active prompt

Given an active prompt exists and the user has `prompts.test`, when they run a test input, then the backend assembles prompt layers, returns response/metadata, records prompt version/checksum, and emits audit/assembly trace.

### Scenario 15: Disabled agent cannot run prompt test

Given an AgentDefinition is disabled or archived, when a user attempts prompt test, then the action is denied with a clear disabled/archived message and audit is emitted.

## 15. Test requirements

Minimum test coverage:

- Prompt overview empty/no-active and active states.
- Create PromptDocument success and audit emission.
- Prompt create/read/update tenant isolation.
- Capability denials for read, create, draft, review, activate, and test.
- Draft save success and validation failure.
- Unknown variable rejection/flagging.
- Secret-like prompt content rejection/flagging.
- Submit-for-review creates immutable version/snapshot.
- Approve and reject lifecycle transitions.
- Activate approved version and deprecate/replace previous active version.
- Activation denial for draft/in-review/unapproved version.
- Version list/detail immutability.
- Diff API/UI behavior.
- Prompt assembly metadata includes version/checksum/model reference/correlation id and excludes secrets.
- Test console allowed for active agent and denied for disabled/archived agent.
- Audit events emitted for lifecycle actions and denials.
- Frontend states: loading, empty, validation error, forbidden, diff, review, activation success/failure.
- Frontend bundle/static asset test verifies no provider/model/backend secrets are exposed.

## 16. Akka decomposition notes

This section is input for later `akka-prd-to-specs-backlog` and implementation planning. It is not the final design.

Likely Akka components:

- Event Sourced Entity for `PromptDocument` lifecycle and current state.
- Immutable Key Value Entity for `PromptVersion` snapshots.
- Consumer from PromptDocument events to create/update PromptVersion snapshots and audit events if not emitted directly.
- Views for prompt documents by agent/tenant/status and versions by prompt document.
- Optional lightweight component or service for diff generation.
- Agent component or deterministic test adapter for minimal prompt test console.
- HTTP endpoints for prompt documents, versions, diff, assembly preview, and test console.
- React/Vite/TypeScript UI for prompt overview, editor, review, diff/history, and test console.

Implementation guidance:

- Reuse Module 1 AuthContext, Module 2 admin audit, and Module 3 AgentDefinition authorization patterns.
- Use the two-entity governed-document pattern: Event Sourced current document plus immutable version snapshots.
- Keep prompt assembly deterministic and traceable.
- Keep provider/model secrets outside prompt documents and frontend assets.
- Emit audit from backend command paths, not frontend-only actions.
- Keep skill manifests and `readSkill` explicitly deferred to Module 5.

## 17. Demo flow

A successful Module 4 demo should run as follows:

1. Sign in as Tenant Admin or Agent Steward.
2. Open an active AgentDefinition from Module 3.
3. Open Prompts and create a system prompt document.
4. Edit and save a draft prompt.
5. Submit the prompt for review.
6. Open review surface, inspect diff, and approve the version.
7. Activate the approved version.
8. Run the prompt test console and inspect assembly metadata.
9. Create a second draft, compare diff against active version, and reject or leave in draft.
10. Open admin audit and inspect prompt lifecycle events.
11. Run tests proving versioning, activation, tenant isolation, capability denial, audit, prompt assembly metadata, and frontend secret boundary.

## 18. Explicit defers to later modules

Deferred to Module 5 Skill Governance:

- governed skill documents;
- shared skill catalog;
- per-agent allowed skill manifest;
- compact skill manifest in prompt assembly;
- `readSkill(skillId)` tool;
- skill load audit traces.

Deferred to Module 6 Audit and Work Trace:

- full prompt assembly and agent execution timeline;
- model config, data access, tool invocation, and response trace details;
- advanced redaction and trace search UI.

Deferred to Module 7 Evaluation and Closed-Loop Improvement:

- evaluator agents;
- prompt quality rubrics;
- replay/simulation before activation;
- improvement proposals;
- canary activation and rollback automation.

Deferred to later runtime modules:

- production agent execution against business workflows;
- multi-agent orchestration;
- advanced memory/session analytics;
- prompt A/B testing and experimentation.

## 19. Readiness checklist

Module 4 is ready for decomposition when the following are true:

- [ ] Module 1 AuthContext, Module 2 admin audit, and Module 3 AgentDefinition assumptions are accepted.
- [ ] PromptDocument and PromptVersion fields are accepted.
- [ ] Prompt lifecycle states and transitions are accepted.
- [ ] Review/approval/activation separation rule is accepted, including any simplified single-admin exception.
- [ ] Prompt variable support and unknown-variable behavior are accepted.
- [ ] Secret-boundary rule for prompt text and model references is accepted.
- [ ] Minimal prompt assembly layers and trace metadata are accepted.
- [ ] Test console scope is accepted as safe validation, not production chat.
- [ ] Workstream surface, route/deep-link, editor/review/diff/test states are accepted.
- [ ] Audit event coverage is accepted.
- [ ] Tenant isolation, capability denial, versioning, activation, diff, audit, and frontend secret-boundary tests are accepted.
- [ ] Deferred skill governance, work trace, evaluation, and production runtime features are confirmed as not part of Module 4.
