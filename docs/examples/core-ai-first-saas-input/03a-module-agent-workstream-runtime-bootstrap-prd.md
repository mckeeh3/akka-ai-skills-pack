# Module 2 PRD: Agent Workstream Runtime Bootstrap

## Status

Bridge PRD for the progressive core AI-first SaaS seed app. This module intentionally runs after minimal auth/app access and before full User Administration.

Read first:

- `00-document-development-process-context.md`
- `01-core-seed-progression-plan.md`
- `02-persistent-discussion-capture.md`
- `03-module-auth-app-access-prd.md`
- `10-canonical-core-app-prd.md`
- `../../agent-workstream-application-architecture.md`
- `../../agent-runtime-invocation-pattern.md`

## 1. Module purpose

Module 2 resolves the chicken-and-egg dependency between workstream-backed UI and later administration modules.

The full core app organizes consequential authenticated work through functional agents and continuous workstreams. User Admin itself must be reached through a User Admin functional agent, but a fully governed Agent Admin module is too large to require before any user administration exists. This module therefore creates the smallest safe, manually testable agent-workstream runtime substrate needed to back functional-agent UI behavior before full User Admin is implemented.

This module is not full Agent Admin, prompt governance, skill governance, audit trace, or closed-loop improvement. It is a bootstrap runtime and seed-behavior slice.

## 2. User-visible outcome

At completion, a seeded tenant admin can:

1. sign in through the Module 1 app shell;
2. see Access/Profile and seeded bootstrap functional agents in the left rail;
3. select a functional agent and see a continuous workstream backed by the governed Akka Agent runtime path;
4. submit a simple composer request such as `show my access context`, `show user admin status`, or `explain what user admin can do`;
5. receive a structured response and/or structured surface that is produced through the bootstrap agent runtime path, not hard-coded page navigation;
6. inspect basic prompt assembly, skill/manifest, tool-boundary, and work trace facts through developer/test-visible endpoints or diagnostic surfaces;
7. prove through tests that disabled agents, cross-tenant agent refs, unassigned skill reads, missing tool grants, and unauthorized runtime calls are denied before model invocation.

## 3. MVP boundaries

### In scope

- Seeded functional-agent catalog entries for Access/Profile and User Admin bootstrap placement.
- Minimal durable or seed-imported `AgentDefinition` records for bootstrap functional agents.
- Minimal seed prompt document/version records for governed runtime behavior.
- Minimal seed skill document/version records and compact `AgentSkillManifest` entries.
- Minimal `ToolPermissionBoundary` that defaults deny and allows only safe read/context tools plus `readSkill(skillId)` where assigned.
- `AgentRuntimeResolver`-style backend boundary that resolves AuthContext, active agent, active prompt, compact manifest, tool boundary, model provider ref, and traces.
- Concrete Akka `Agent` component invocation for normal workstream message submission; deterministic `TestModelProvider` behavior is allowed only in tests or explicitly named test adapters.
- Fail-closed provider configuration behavior: missing model-provider settings produce actionable blocked surfaces/traces instead of fallback markdown.
- Composer endpoint or workstream action endpoint that invokes the bootstrap runtime.
- Workstream UI integration for selected functional agent, composer request, response item, and structured surface output.
- Trace facts sufficient to prove prompt assembly, skill load allowed/denied, and agent work correlation.
- Tests for successful invocation and denial paths.

### Out of scope

- Admin-created agent definitions through UI.
- Full Agent Admin lifecycle management, diff/review, activation, archive, rollback, or behavior-edit proposals.
- Rich prompt editor, skill editor, manifest editor, tool-boundary editor, or model config UI.
- Production LLM/provider configuration or provider-secret management beyond safe placeholders.
- Side-effecting agent tools.
- Full User Admin functionality; this module only backs the User Admin functional-agent entry and introductory/status surfaces.
- Full Audit/Trace explorer; this module may expose diagnostics only as needed for tests/manual verification.
- Evaluation, rubrics, improvement proposals, and closed-loop behavior changes.

## 4. Actors

| Actor | Description | Module expectations |
|---|---|---|
| Seeded Tenant Admin | Active tenant admin from Module 1 seed/bootstrap data. | Can invoke bootstrap functional agents in selected tenant context. |
| Tenant Member | Active member without admin capabilities. | Can invoke Access/Profile; cannot invoke User Admin bootstrap agent if lacking capability. |
| Disabled account/member | Signed-in identity with disabled account or membership. | Runtime invocation is denied before agent resolution/model invocation. |
| Bootstrap runtime actor | Backend execution context that prepares governed requests and invokes the Akka Agent runtime. | May execute only within selected AuthContext and explicit tool boundary. |
| Future Agent Admin | Later module that replaces or extends bootstrap seed behavior with governed UI-managed records. | Consumes the same record shapes and runtime contract. |

## 5. Authorization and capability model

Required capabilities:

- `app.access` — may enter authenticated shell.
- `profile.read` — may invoke Access/Profile context behavior.
- `workstream.agent.invoke` — may invoke functional-agent runtime for allowed agents.
- `admin.users.read` or `admin.bootstrap.preview` — may see/invoke the User Admin bootstrap functional agent.
- `agents.runtime.test` — may run explicitly named test-mode invocations where exposed; it must not replace normal workstream runtime.

Rules:

- `/api/me` remains the source for browser-safe functional-agent rail visibility.
- Backend runtime authorization is authoritative; rail visibility is only a hint.
- A caller can invoke only agents assigned to the selected tenant/customer AuthContext and permitted by capability.
- Agent prompt/skill content cannot grant data access, tool access, role/capability access, tenant scope, or approval authority.
- All tool calls, including `readSkill(skillId)`, are denied unless both the active manifest and active tool boundary allow them.
- Runtime mode for this module is `test` or `bootstrap`; production side effects are not allowed.

## 6. Durable/seeded objects

### AgentDefinition

Minimum fields:

- `agentDefinitionId`
- `tenantId`
- optional `customerId`
- functional area id: `access-profile` or `user-admin-bootstrap`
- display name
- purpose
- status: `ACTIVE`, `DISABLED`, or `ARCHIVED`
- authority level: `READ_ONLY_CONTEXT` or `DRAFT_ONLY`
- prompt version ref
- skill manifest ref
- tool boundary ref
- model config/test provider ref
- seed provenance and checksum

State owner expectation: this may be seeded durable state using the same shape later managed by Agent Admin. Event Sourced Entity is preferred for full implementation, but this module may use a narrow seed loader plus durable current record if generation chooses an incremental path.

### PromptDocument / PromptVersion

Minimum seed prompt content:

- Access/Profile prompt: answer only questions about the caller's account, selected context, capabilities, and safe profile/settings surfaces.
- User Admin bootstrap prompt: explain available and forthcoming user-admin actions; route actual mutations to future User Admin capabilities; do not invent users or roles.

Prompt versions must have checksum, seed provenance, active status, and no provider secrets.

### SkillDocument / SkillVersion / AgentSkillManifest

Minimum bootstrap skills:

- `access-context-summary` — when to summarize account, selected context, memberships, and capabilities.
- `user-admin-orientation` — when to explain user admin concepts and available/future surfaces.

Prompt assembly includes compact manifest entries only. Full skill text is available only through authorized `readSkill(skillId)`.

### ToolPermissionBoundary

Minimum allowed tool classes:

- read current AuthContext summary;
- read browser-safe `/api/me`-equivalent profile/capability summary;
- `readSkill(skillId)` for assigned active skills.

Denied by default:

- user/membership mutation;
- invitation creation/resend/revoke/accept;
- role changes;
- support access;
- provider/model secret access;
- cross-tenant reads;
- external side effects.

### Runtime trace facts

Minimum trace facts:

- `PromptAssemblyTrace`
- `SkillLoadTrace`
- `AgentWorkTrace`

These may be stored in the audit/trace mechanism available after Module 1 or in a minimal trace sink that Module 6 later normalizes.

## 7. Capabilities

### 7.1 Functional agent catalog

The backend exposes a browser-safe catalog derived from `/api/me` and active bootstrap `AgentDefinition` records.

Required behavior:

- Access/Profile appears for active members with `profile.read`.
- User Admin bootstrap appears only for admin-capable users.
- Disabled/archived/cross-tenant agents are omitted from the rail and denied by backend if directly invoked.

### 7.2 Bootstrap runtime invocation

A protected endpoint or capability action accepts:

- selected functional agent id or agent definition id;
- user message/intent;
- selected AuthContext;
- idempotency/correlation id;
- mode: `bootstrap` or `test`.

Required behavior:

1. validate JWT/request context;
2. resolve selected AuthContext;
3. authorize `workstream.agent.invoke` plus any functional-agent-specific capability;
4. resolve active AgentDefinition;
5. assemble active prompt plus compact manifest;
6. emit PromptAssemblyTrace;
7. invoke the concrete Akka `Agent` component through the governed runtime path;
8. use the configured model/provider boundary from that Agent path, or fail closed with an actionable provider-configuration surface/trace;
9. authorize any `readSkill(skillId)` request;
10. emit SkillLoadTrace and AgentWorkTrace;
11. return a workstream item and optional structured surface payload.

### 7.3 Access/Profile bootstrap behavior

Supported intents:

- summarize my current context;
- show my capabilities;
- explain why a surface/action is unavailable;
- show profile/settings surface.

The agent must not expose secrets or unrelated tenant data.

### 7.4 User Admin bootstrap behavior

Supported intents:

- explain what User Admin will support;
- show current admin capability summary;
- show placeholder/status surface for users, invitations, roles/memberships, access review, and admin audit;
- deny attempts to mutate users until Module 3 User Administration implements real capabilities.

The bootstrap agent may prepare orientation text and structured status surfaces only.

## 8. Workstream UI requirements

Required surfaces:

- functional-agent rail using backend-derived catalog;
- workstream item for user composer request;
- workstream item for agent response;
- Access/Profile context summary surface;
- User Admin bootstrap/status surface;
- denied/unavailable action state for not-yet-implemented mutations;
- trace/correlation indicator for Akka Agent runtime calls and explicit test-adapter calls.

Routes may deep-link to selected functional agents or surfaces, but the workstream shell remains the primary UI model.

## 9. Manual test scenarios

At sprint completion, a developer running Akka locally can:

1. sign in or use local test auth as seeded tenant admin;
2. open the workstream shell;
3. select Access/Profile and ask `show my context`;
4. see a backend-runtime-backed response and context surface;
5. select User Admin and ask `what can I do here?`;
6. see a backend-runtime-backed orientation/status surface;
7. attempt an unavailable user mutation and see a safe denial/unavailable state;
8. inspect logs/test diagnostics showing prompt assembly, skill load, and work trace correlation.

## 10. Required tests

- successful Access/Profile bootstrap invocation;
- successful User Admin bootstrap invocation for admin-capable user;
- non-admin User Admin invocation denied;
- disabled account or membership denied before agent invocation;
- cross-tenant AgentDefinition denied;
- disabled/archived AgentDefinition denied;
- compact manifest appears in prompt assembly and full skill text does not;
- assigned active skill can be loaded through `readSkill(skillId)`;
- unassigned/inactive/cross-tenant skill load denied safely;
- missing `readSkill` tool grant denied;
- side-effecting user-admin tool request denied/unavailable;
- PromptAssemblyTrace, SkillLoadTrace, and AgentWorkTrace emitted;
- frontend workstream renders request, response, surface, denial, loading/error, and trace indicator states;
- frontend bundle contains no WorkOS, Resend, model-provider, raw invitation, or backend secrets.

## 11. Acceptance criteria

This module is complete when:

- the authenticated shell is workstream-backed by a real protected backend invocation path;
- Access/Profile and User Admin bootstrap functional agents are visible only when authorized;
- composer requests produce Akka Agent-backed workstream responses through the governed runtime path; deterministic model behavior is limited to tests or explicitly named test adapters;
- bootstrap prompts, skills, manifests, tool boundaries, and agent definitions are seeded or durable records compatible with later Agent Admin;
- denied runtime paths fail before model invocation and are traceable;
- no user-admin mutation is faked in the UI before the real User Admin module exists;
- all required tests pass.

## 12. Explicit handoff to later modules

The next User Administration module replaces User Admin bootstrap/status responses with real invitation, membership, role, access-review, and admin-audit capabilities while preserving the same functional-agent rail, composer, workstream item, surface action, AuthContext, and trace patterns.

The later Agent Admin modules expose CRUD/governance UI for the records seeded here. They must not replace the runtime contract; they govern it.
