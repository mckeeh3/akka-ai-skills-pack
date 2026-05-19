# Module 3 PRD: Agent Definition Foundation

## Status

Detailed PRD for the third MVP module in the progressive core AI-first SaaS seed app.

Read first:

- `00-document-development-process-context.md`
- `01-core-seed-progression-plan.md`
- `02-persistent-discussion-capture.md`
- `03-module-auth-app-access-prd.md`
- `04-module-user-admin-prd.md`


## Workstream architecture alignment

This module PRD is interpreted under `10-canonical-core-app-prd.md` and `../../agent-workstream-application-architecture.md`. Any legacy references to pages, screens, navigation, or route inventory mean structured workstream surfaces, surface actions, and route/deep-link implementation details inside the agent workstream shell. They must not be used to generate a page-first admin console or chatbot-bolt-on app.

## 1. Module purpose

Module 3 introduces the first durable AI-first administration surface: governed agent definitions.

The module lets authorized tenant admins define, inspect, enable/disable, and audit basic agent records before advanced runtime prompt editing, skill manifests, work traces, and evaluation loops are added in later modules.

An `AgentDefinition` is the app-owned description of an agent's purpose, tenant/customer scope, lifecycle status, model configuration placeholder, tool permission boundary, and governance metadata. It is not yet the full runtime prompt, skill catalog, or autonomous execution system.

## 2. User-visible outcome

At completion, an authorized admin can:

1. open an Agents area from the authenticated app shell;
2. view a tenant-scoped list of agent definitions;
3. create a basic agent definition with name, purpose, owner, scope, status, model placeholder, and tool permission boundary;
4. inspect agent details and governance metadata;
5. enable, disable, archive, or update a draft agent definition according to permissions;
6. see agent-definition changes in admin audit;
7. verify that non-admins and cross-tenant users cannot view or mutate agent definitions;
8. optionally run a safe placeholder/test interaction that proves an agent definition can be referenced without yet implementing prompt governance or skill loading.

## 3. MVP boundaries

### In scope

- Tenant-scoped Agent Admin functional-agent rail entry and structured surfaces.
- `AgentDefinition` durable object.
- Agent lifecycle basics: draft, active, disabled, archived.
- Agent purpose and responsibility statement.
- Agent owner/steward metadata.
- Tenant scope and optional customer-scope placeholder.
- Model/provider configuration placeholder without exposing secrets.
- Tool permission boundary placeholder: allowed tool categories and denied-by-default posture.
- Basic risk/authority classification: advisory, draft-only, approval-required, or disabled.
- Admin audit events for agent definition lifecycle and authorization denials.
- Views for agent list/detail by tenant/status.
- Backend authorization for all agent-definition routes and commands.
- Tests for tenant isolation, capability denial, disabled/archived behavior, audit, and frontend states.

### Out of scope for Module 3

- Runtime-managed prompt document editing and activation.
- Prompt assembly, prompt version history, and prompt diff/review UI.
- Governed skill documents, skill manifests, and `readSkill(skillId)` tool.
- Real agent execution against business data, unless limited to a deterministic placeholder/test console.
- Multi-agent orchestration, workflow execution, memory compaction, evaluator agents, and closed-loop improvement.
- Production tool integrations and secret management UI.
- Full policy governance or approval workflows beyond simple status/authority fields.
- Detailed work-trace timeline UI beyond admin audit events.

## 4. Actors

| Actor | Description | Module 3 expectations |
|---|---|---|
| Tenant Admin | Active tenant admin from Module 2. | Can view and manage agent definitions if granted agent admin capabilities. |
| Agent Owner / Steward | Business owner responsible for an agent's purpose and behavior. | Can be assigned as owner; may edit or review definitions if permitted. |
| Tenant Member | Active user without agent admin capabilities. | Cannot access Agent Admin functional-agent surfaces or APIs unless given read-only capability. |
| Auditor/Admin Reviewer | User with read-only audit or agent review capability. | Can inspect agent definitions and audit events without mutation if role exists. |
| Seed Operator | Initial admin used for demo/test. | Can create the first sample agent definition. |
| Future Agent Runtime | Later module/runtime that will consume active AgentDefinitions. | Not implemented here, but definition shape must be stable enough for later use. |

## 5. Authorization and capability model

Module 3 adds agent-definition capabilities to the Module 2 role model.

Required capabilities:

- `agents.read` — list and inspect agent definitions for selected tenant.
- `agents.create` — create draft agent definitions.
- `agents.update` — edit mutable metadata on draft or allowed active definitions.
- `agents.status.manage` — activate, disable, or archive agent definitions.
- `agents.permissions.manage` — edit tool permission boundaries or authority classification.
- `agents.audit.read` — inspect agent-related audit events; may map to `admin.audit.read`.

Recommended initial role mapping:

| Role | Module 3 capabilities |
|---|---|
| Tenant Admin | All Module 3 capabilities. |
| Agent Steward | `agents.read`, `agents.create`, `agents.update`; status/permission management optional. |
| Access Reviewer / Auditor | `agents.read`, `agents.audit.read`. |
| Member | No agent admin capabilities by default. |

Rules:

- Backend checks are authoritative for every endpoint and command.
- Agent definitions are tenant-scoped; cross-tenant ids must be rejected.
- A user cannot grant an agent tool boundary or authority level beyond what their role allows.
- Archived definitions are read-only except for explicit restore if supported.
- Disabled definitions cannot be used by later runtime modules.
- Secrets are never accepted in model or tool placeholder fields.

## 6. Durable objects and state ownership

### AgentDefinition

Represents an app-owned, tenant-scoped definition of an agent.

Required fields:

- `agentDefinitionId`
- `tenantId`
- optional `customerId` or scope placeholder
- display name
- short description
- purpose/responsibility statement
- status: `DRAFT`, `ACTIVE`, `DISABLED`, `ARCHIVED`
- owner/steward account id or team label
- authority level: `ADVISORY`, `DRAFT_ONLY`, `APPROVAL_REQUIRED`, `DISABLED`
- model configuration reference or placeholder, not raw provider secrets
- prompt document reference placeholder for Module 4
- skill manifest reference placeholder for Module 5
- allowed tool categories or tool permission boundary placeholder
- data access scope summary
- escalation/approval notes
- created by / updated by
- created/updated timestamps
- version/revision number

State owner expectation: Event Sourced Entity is preferred because agent definitions are behavior-shaping governance objects whose lifecycle and changes should be auditable. A Key Value Entity may be acceptable for current-state implementation only if audit events preserve lifecycle detail.

### AgentDefinitionVersion or Snapshot

Optional for Module 3, but the design should not block later version history.

Required if included:

- `agentDefinitionId`
- version number
- immutable snapshot of definition fields
- created by
- created timestamp
- change summary

State owner expectation: immutable Key Value Entity snapshot per version can align with the two-entity governed-document pattern used later for prompts and skills.

### ToolPermissionBoundary

Represents the declared boundary for what an agent may eventually do.

For Module 3 this may be embedded in AgentDefinition.

Required fields:

- allowed tool category ids or names;
- denied categories if explicit;
- data access scope summary;
- external side-effect flag;
- requires approval flag;
- human escalation notes.

### ModelConfigurationRef

Represents a safe reference to intended model/provider configuration.

Required fields:

- provider/model reference name;
- temperature/profile placeholder if safe;
- status or availability flag;
- no API keys, tokens, or secrets.

### AdminAuditEvent

Module 3 expands audit coverage with agent definition events.

Required event types:

- `AGENT_DEFINITION_CREATED`
- `AGENT_DEFINITION_UPDATED`
- `AGENT_DEFINITION_ACTIVATED`
- `AGENT_DEFINITION_DISABLED`
- `AGENT_DEFINITION_ARCHIVED`
- `AGENT_TOOL_BOUNDARY_CHANGED`
- `AGENT_AUTHORITY_CHANGED`
- `AGENT_ADMIN_AUTH_DENIED`
- `AGENT_DEFINITION_VIEWED` if view auditing is selected

State owner expectation: reuse Module 2 audit storage/query pattern and keep it compatible with Module 6 work trace expansion.

## 7. Capabilities

### 7.1 Agent navigation and access gate

The app shell must show an Agent Admin functional-agent rail entry to users with `agents.read` or higher. Direct URL access must still be checked by backend APIs and show a forbidden state when denied.

### 7.2 Agent definition list

Authorized users can view tenant-scoped agent definitions.

List columns:

- name;
- status;
- purpose summary;
- authority level;
- owner/steward;
- model config reference;
- tool boundary summary;
- updated timestamp;
- updated by.

Filters:

- status;
- owner;
- authority level;
- tool boundary/category;
- text search by name/purpose.

The list must never include agent definitions from other tenants.

### 7.3 Create agent definition

Authorized users can create a draft agent definition.

Required form fields:

- name;
- purpose/responsibility statement;
- owner/steward;
- authority level;
- model configuration reference/placeholder;
- tool permission boundary;
- data access scope summary;
- escalation/approval notes.

Validation:

- name is required and tenant-unique enough for admin clarity;
- purpose must be non-empty and specific;
- authority level cannot exceed creator permissions;
- tool boundary must be explicit, even if set to no tools;
- no secrets are accepted in any text or config field if detectable;
- tenant id comes from AuthContext, not trusted frontend input.

### 7.4 Inspect agent definition detail

Agent detail surface shows:

- all core definition fields;
- lifecycle status;
- owner/steward;
- authority and approval notes;
- model placeholder;
- prompt/skill reference placeholders;
- tool/data access boundary;
- recent audit events;
- available actions based on current user's capabilities and current status.

### 7.5 Update draft or mutable metadata

Authorized users can update draft definitions. Active definitions may allow limited metadata edits, or require disable/edit/reactivate depending on accepted safety rule.

Required update behavior:

- validate permissions and tenant scope;
- increment revision/version;
- audit before/after summary;
- update list/detail views;
- preserve status safety rules.

### 7.6 Activate, disable, and archive

Lifecycle actions:

- activate draft definition after required fields are complete;
- disable active definition to prevent future runtime use;
- archive disabled or draft definition to remove from normal active lists;
- optionally restore archived definition if accepted.

Activation readiness checks:

- purpose exists;
- owner/steward exists;
- authority level is explicit;
- tool boundary is explicit;
- model placeholder is selected or explicitly deferred;
- prompt/skill placeholders are either empty with later-module note or reference supported defaults.

### 7.7 Authority and tool boundary management

Admins can define the first safe boundary for agent authority.

Allowed authority levels:

- `ADVISORY` — may recommend or summarize only.
- `DRAFT_ONLY` — may draft artifacts but not commit changes.
- `APPROVAL_REQUIRED` — may prepare actions but requires human approval before side effects.
- `DISABLED` — cannot run.

Tool boundary examples:

- no tools;
- read-only tenant data;
- admin-audit read;
- draft artifact creation;
- external side-effect tools prohibited until later modules.

Module 3 does not need to execute tools, but it must make the intended boundary explicit and auditable.

### 7.8 Optional placeholder test console

If included, a minimal test console may let admins select an active AgentDefinition and run a deterministic placeholder response such as:

- show resolved definition metadata;
- validate that disabled/archived definitions cannot be selected;
- show what prompt/skill/runtime modules are still deferred.

This is not a production chat or real agent execution feature.

## 8. UI requirements

### 8.1 Agent Admin surfaces and route/deep-link inventory

Minimum routes:

- `/app/agents` agent definition list;
- `/app/agents/new` create form;
- `/app/agents/:agentDefinitionId` detail surface;
- `/app/agents/:agentDefinitionId/edit` edit form or modal;
- optional `/app/agents/:agentDefinitionId/test` placeholder test console;
- agent-related audit may link to `/app/admin/audit` from Module 2.

### 8.2 Agent list UI

Required states:

- loading;
- empty with create-agent call to action;
- populated list;
- filtered empty;
- forbidden;
- error.

Rows should visually distinguish draft, active, disabled, and archived definitions.

### 8.3 Create/edit form UI

Required form behavior:

- grouped sections: identity, purpose, authority, model, tool/data boundary, governance notes;
- inline validation;
- clear warning that secrets must not be pasted;
- capability-aware disabling/hiding of restricted authority/tool options;
- save draft action;
- activate action only when readiness checks pass.

### 8.4 Detail UI

Agent detail surface should include:

- summary header with name/status/authority;
- purpose and responsibility panel;
- owner/steward panel;
- model reference panel;
- prompt and skill placeholders with links disabled or marked "coming in later module";
- tool/data boundary panel;
- lifecycle action buttons;
- recent audit timeline or audit event list.

### 8.5 Accessibility and responsive behavior

- Forms must use accessible labels and error associations.
- Status badges must not rely on color alone.
- Destructive actions such as disable/archive require confirmation.
- Agent list must remain usable on tablet/narrow desktop widths.

## 9. API requirements

Exact endpoint names may be adjusted during implementation planning, but the module must cover these contracts.

### Agent definitions

- `GET /api/agents/definitions` — list definitions for selected tenant.
- `POST /api/agents/definitions` — create draft definition.
- `GET /api/agents/definitions/{agentDefinitionId}` — get detail scoped to selected tenant.
- `PUT /api/agents/definitions/{agentDefinitionId}` — update mutable fields.
- `POST /api/agents/definitions/{agentDefinitionId}/activate` — activate when readiness checks pass.
- `POST /api/agents/definitions/{agentDefinitionId}/disable` — disable active definition.
- `POST /api/agents/definitions/{agentDefinitionId}/archive` — archive definition.
- optional `POST /api/agents/definitions/{agentDefinitionId}/restore` — restore archived definition if included.
- optional `POST /api/agents/definitions/{agentDefinitionId}/test-placeholder` — deterministic placeholder/test response.

API rules:

- all endpoints require active account, selected tenant context, and relevant capability;
- target definitions must belong to selected tenant;
- command payloads must not be able to override tenant id;
- responses must not include provider/model secrets;
- errors must be typed for UI states but not leak other-tenant existence.

## 10. Authorization rules

Required backend authorization checks:

- resolve AuthContext from Module 1 for every request;
- require active membership in selected tenant;
- require relevant `agents.*` capability;
- verify target AgentDefinition belongs to selected tenant;
- validate authority/tool boundary changes against actor permissions;
- deny all mutations for archived definitions unless restore is explicitly supported;
- deny runtime/test selection of disabled or archived definitions;
- emit audit event for allowed mutations and denied attempts.

## 11. Governance and safety requirements

Module 3 creates behavior-shaping records, so governance fields are required even before real runtime execution.

Required safeguards:

- every agent has a named purpose and owner/steward;
- every agent has an explicit authority level;
- every agent has an explicit tool/data boundary, including "no tools";
- activation requires readiness checks;
- model config is a reference/placeholder only and cannot contain secrets;
- prompt and skill references are placeholders until Modules 4 and 5;
- high-authority levels can be blocked or require a stronger capability;
- all changes are auditable.

## 12. Audit and observability requirements

Required audit fields:

- audit event id;
- event type;
- timestamp;
- actor account id;
- selected tenant id;
- target `agentDefinitionId`;
- previous and new status/authority/tool boundary when applicable;
- change summary;
- authorization decision;
- denial reason if denied;
- request/correlation id;
- safe metadata only.

Observability requirements:

- structured logs for create/update/status changes and authorization denials;
- correlation id between API request, entity command, and audit event;
- metrics/counters for active/disabled definitions if metrics are in the seed stack.

## 13. Security and privacy requirements

- Agent definitions are tenant-scoped and must not leak across tenants.
- Secrets must never be stored in model placeholder, tool boundary, purpose, or notes fields.
- UI and API must clearly distinguish configuration references from secret values.
- Non-admins must not infer existence of other tenants' agent definitions through error differences.
- Disabled/archived definitions must be unavailable for any runtime/test path.
- Agent authority is metadata in Module 3; it must not grant backend permissions by itself.
- Future runtime modules must still enforce authorization mechanically; prompts or agent descriptions are not security boundaries.

## 14. Acceptance scenarios

### Scenario 1: Admin sees empty agent list

Given a tenant admin with `agents.read` signs in before any definitions exist, when they open Agents, then they see an empty state with create-agent action.

### Scenario 2: Non-agent admin is forbidden

Given a member lacks `agents.read`, when they open `/app/agents` or call the list API, then access is forbidden and an audit denial is emitted.

### Scenario 3: Admin creates draft agent definition

Given an admin has `agents.create`, when they submit a valid agent form, then a draft AgentDefinition is created for the selected tenant and an audit event is emitted.

### Scenario 4: Create rejects missing purpose or boundary

Given an admin submits an agent without a purpose or explicit tool boundary, when the command is processed, then validation fails and no definition is created.

### Scenario 5: Secret-like value is rejected or flagged

Given an admin pastes a provider API key into a model/tool field, when they submit the form, then the system rejects or blocks the value according to the accepted secret-boundary rule.

### Scenario 6: Agent detail is tenant-scoped

Given an agent definition exists in Tenant A, when a Tenant B admin requests it by id, then the backend denies or returns not found according to security policy and emits a denial audit event.

### Scenario 7: Admin activates ready draft

Given a draft definition has required fields, when an admin with `agents.status.manage` activates it, then status becomes active, revision updates, and audit captures the transition.

### Scenario 8: Activation readiness blocks incomplete draft

Given a draft lacks owner, purpose, authority, model placeholder, or tool boundary, when activation is requested, then the command is rejected with actionable validation errors.

### Scenario 9: Admin disables active definition

Given an active definition exists, when an admin disables it, then status becomes disabled, future placeholder/runtime selection is denied, and audit captures the transition.

### Scenario 10: Archived definition is read-only

Given a definition is archived, when an admin attempts normal update, then the update is rejected unless restore/edit flow is explicitly supported.

### Scenario 11: Restricted authority change is denied

Given an admin lacks `agents.permissions.manage`, when they attempt to raise an agent authority level or broaden tool boundary, then the command is forbidden and audited.

### Scenario 12: Agent audit is visible

Given create/update/activate events exist, when an authorized admin opens the agent detail or admin audit list, then recent agent audit events are visible and tenant-scoped.

### Scenario 13: Placeholder test refuses disabled definition

Given the optional test console is included and a definition is disabled, when an admin tries to run the placeholder test, then the action is denied with a clear disabled-state message.

## 15. Test requirements

Minimum test coverage:

- Agent list empty, populated, filtered, forbidden, and error UI states.
- Create draft success with audit emission.
- Create validation for required name, purpose, owner, authority, and tool boundary.
- Secret-boundary validation for model/tool fields.
- Tenant isolation for list/detail/update/status commands.
- `agents.read` denial for non-admin.
- `agents.create`, `agents.update`, `agents.status.manage`, and `agents.permissions.manage` capability denials.
- Activate success and readiness failure.
- Disable and archive lifecycle behavior.
- Archived definition mutation denial.
- Optional placeholder test allowed for active and denied for disabled/archived.
- Admin audit events emitted for lifecycle changes and denials.
- Frontend bundle/static asset test verifies no provider/model secrets are exposed.

## 16. Akka decomposition notes

This section is input for later `akka-prd-to-specs-backlog` and implementation planning. It is not the final design.

Likely Akka components:

- Event Sourced Entity for `AgentDefinition` lifecycle and revision history.
- Optional immutable Key Value Entity snapshots for `AgentDefinitionVersion` if version history is included now.
- Views for agent definitions by tenant/status/owner and agent definition detail.
- Consumer to project agent-definition lifecycle events into admin audit if audit is event-driven.
- HTTP endpoints for agent definition list/create/detail/update/status actions and optional placeholder test.
- React/Vite/TypeScript UI for agent list, create/edit form, detail, lifecycle actions, and audit links.

Implementation guidance:

- Reuse Module 1 AuthContext and Module 2 admin capability model.
- Treat AgentDefinition as governance state, not as runtime execution permission.
- Keep model configuration as a safe reference; do not add secret management here.
- Keep prompt and skill references as placeholders for Modules 4 and 5.
- Emit audit events from backend command paths.
- Keep tenant id from resolved AuthContext, not client-submitted form fields.

## 17. Demo flow

A successful Module 3 demo should run as follows:

1. Sign in as seed Tenant Admin.
2. Open Agents and see empty state.
3. Create a draft agent definition named, for example, "Admin Audit Summary Agent" with advisory authority and no external tools.
4. Open detail surface and inspect purpose, owner, model placeholder, prompt/skill placeholders, and tool boundary.
5. Activate the ready definition.
6. Attempt to broaden authority with a limited user and see forbidden state.
7. Disable the agent definition and confirm optional test/runtime selection is unavailable.
8. Open admin audit and inspect create/update/activate/disable events.
9. Run tests proving tenant isolation, capability denial, lifecycle validation, audit emission, and frontend secret boundary.

## 18. Explicit defers to later modules

Deferred to Module 4 Prompt Governance:

- runtime-managed prompt documents;
- prompt draft/review/approval/activation lifecycle;
- prompt history and diff UI;
- effective prompt assembly and prompt trace.

Deferred to Module 5 Skill Governance:

- governed skill documents;
- per-agent allowed skill manifest;
- `readSkill(skillId)` tool;
- skill version pinning and skill usage trace.

Deferred to Module 6 Audit and Work Trace:

- full work trace timeline;
- agent execution traces;
- model config, prompt, skill, tool invocation, and data access trace details;
- redaction policies for trace payloads.

Deferred to Module 7 Evaluation and Closed-Loop Improvement:

- evaluator agents;
- evaluation rubrics;
- improvement proposals;
- replay/simulation;
- activation/canary/rollback loops.

Deferred to later runtime modules:

- real agent execution against business workflows;
- external tool integrations;
- memory and session analytics;
- multi-agent orchestration.

## 19. Readiness checklist

Module 3 is ready for decomposition when the following are true:

- [ ] Module 1 AuthContext and Module 2 admin capability assumptions are accepted.
- [ ] AgentDefinition required fields are accepted.
- [ ] Agent lifecycle states and status transition rules are accepted.
- [ ] Authority levels and tool boundary placeholders are accepted.
- [ ] Model configuration reference/secret-boundary rule is accepted.
- [ ] Prompt and skill references are confirmed as placeholders for later modules.
- [ ] Agent Workstream surface, route/deep-link, form/detail states are accepted.
- [ ] Audit event coverage is accepted.
- [ ] Tenant isolation, capability denial, lifecycle validation, audit, and frontend secret-boundary tests are accepted.
- [ ] Deferred runtime execution, prompt governance, skill governance, work trace, and evaluation features are confirmed as not part of Module 3.
