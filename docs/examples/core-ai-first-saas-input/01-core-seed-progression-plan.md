# Core AI-First SaaS Seed Progression Plan

## Purpose

This document captures the recommended progressive module sequence for the core AI-first SaaS seed app. It is the implementation progression that future PRD/spec input documents should support.

Use `10-canonical-core-app-prd.md` as the full-core PRD target. Full core scope requires the agent workstream shell plus Access/Profile, User Admin, Agent Admin, Audit/Trace, and Governance/Policy functional agents. If User Admin or Agent Admin are deferred, the selected scope is explicitly a narrower non-full-core scope rather than full core.

The seed app is not implemented all at once. It is planned and delivered as a sequence of modules, each broken into sprints and harness-sized tasks. Each sprint should produce demonstrable behavior through the live app UI and/or APIs, with tests.

For minimum/starter/basic/chatbot-like generated SaaS requests, the first implementation slice is not the full-core module sequence below. It is the `docs/minimum-ai-first-saas-app.md` minimum starter: **User Admin workstream v0** with bootstrap authorization, selected `AuthContext`, bounded `UserAdminAgent`, durable request/response timeline, `markdown_response`, backend capability boundary, and audit/work trace substrate. Full-core expansion remains explicit and non-optional for full readiness.

Because the canonical UI is workstream-agent-backed, the minimum starter begins with a narrow User Admin workstream runtime rather than a generic chatbot, page-first CRUD console, or hard-coded shell state. Full User Admin structured surfaces, Agent Admin, governed prompt/skill documents, Audit/Trace UI, invitations/onboarding, support access, and security completeness arrive later as follow-up modules/gates.

## Delivery model

```text
module PRD
→ module spec
→ one or more sprint specs
→ build backlog
→ focused implementation tasks
→ demonstrable full-stack increment
```

The first sprint of each module should create user-visible value. Backend components, endpoints, frontend UI, tests, and audit/security behavior should be planned together.

## Minimum starter prelude: User Admin workstream v0

### Goal

A bootstrap-authorized user can enter the app, operate in a selected `AuthContext`, and use the User Admin functional agent through a durable workstream timeline whose first structured surface is `markdown_response`.

### Scope label

The prelude by itself is `minimum starter ready / not full core`. It explicitly defers full User Admin structured capabilities, Agent Admin, Audit/Trace UI, invitations/onboarding, support access, full governed behavior document administration, and app-specific workstreams.

### Required visible outcome

A bootstrap user can ask the User Admin agent what access exists, what capabilities are available, what full-core gaps remain, and why unsupported actions are denied or deferred. The response is rendered through sanitized `markdown_response`, linked to workstream log entries and trace facts.

### Core scope

- Bootstrap-authorized human user only; no public self-registration.
- Selected local `AuthContext` with account/user identity, scope, roles/capabilities, and actor metadata.
- Role-authorized User Admin functional-agent rail entry and persistent composer.
- Bounded `UserAdminAgent` for orientation, access explanation, safe next-step guidance, and denials/deferments.
- Durable workstream log for request, response, capability/tool result, denial, correlation id, and trace references.
- `markdown_response` payload and sanitized rendering contract.
- Backend capability boundary for every protected workstream, API, component, and tool action.
- Audit/work trace substrate for identity, authorization, prompt/skill/tool use, capability checks, data access, denials, and response generation.
- Tests for allowed bootstrap access, forbidden access, trace creation, markdown sanitization, frontend secret boundary, and unsupported action denial.

### Explicit defers

- Complete invitation lifecycle and Resend/outbox production readiness.
- Full user directory, membership, role, access-review, support-access, and admin audit management.
- Agent Admin surfaces and full prompt/skill/manifest/tool-boundary governance.
- Audit/Trace search/detail UI.
- Governance/Policy proposal and outcome loops.
- App-specific domain functional agents and capabilities.

## Module 1: Minimal Auth and App Access MVP

### Goal

Users can sign in, enter the app, retrieve their current identity/context, and use a functional authenticated agent workstream shell with an Access/Profile functional agent.

### Scope label

Module 1 by itself is a narrower non-full-core scope. It must not be used to override the minimum starter rule for starter/basic/chatbot-like requests, and it explicitly defers User Admin, Agent Admin, Audit/Trace, and Governance/Policy functional agents.

### Required visible outcome

A user can authenticate, land in the app, see their profile/context, and encounter correct authorized/forbidden states.

### Core scope

- WorkOS/AuthKit as the supported browser authentication service.
- Minimal `Account`.
- Minimal `UserProfile`.
- Minimal `Tenant`.
- Minimal `Membership`.
- Selected `AuthContext`.
- `/api/me` endpoint.
- Central backend authorization helper/pattern.
- Frontend authenticated agent workstream shell.
- Access/Profile functional agent in the role-authorized left rail.
- Sign-in/sign-out UI.
- Authenticated landing surface in the main workstream.
- Context display and minimal context selection if multiple memberships exist.
- Forbidden and unauthenticated UI states.
- Security tests for `/api/me`, forbidden access, tenant isolation basics, and frontend secret boundary.

### Explicit defers

- Full user administration.
- Invitation lifecycle beyond what is required for first access.
- Advanced roles/permissions UI.
- Agent workstream runtime bootstrap beyond minimal/static shell behavior.
- Agent definitions.
- Prompt/skill governance.
- Closed-loop improvement.

## Module 2: Agent Workstream Runtime Bootstrap

### Goal

The authenticated shell becomes backed by a real protected agent-runtime path before full User Admin is implemented.

### Required visible outcome

A seeded tenant admin can select Access/Profile or User Admin in the functional-agent rail, submit a composer request, and receive deterministic backend-agent-runtime-backed workstream responses and structured surfaces. Runtime invocation resolves AuthContext, active seeded AgentDefinition, active prompt, compact AgentSkillManifest, ToolPermissionBoundary, deterministic model/test provider, and trace facts before returning a response.

### Core scope

- Seeded `AgentDefinition` records for Access/Profile and User Admin bootstrap functional agents.
- Seeded prompt versions, skill versions, compact manifests, and deny-by-default tool boundaries.
- `AgentRuntimeResolver`-style backend boundary.
- Authorized `readSkill(skillId)` for assigned active skills.
- Deterministic local/test invocation path; no production model provider required.
- Workstream composer endpoint/action that returns agent responses and structured surfaces.
- PromptAssemblyTrace, SkillLoadTrace, and AgentWorkTrace facts sufficient for diagnostics and later Audit/Trace normalization.
- Tests for successful invocation, disabled/cross-tenant agent denial, non-admin User Admin denial, unassigned skill denial, missing tool grant denial, side-effect denial, and frontend rendering.

### Explicit defers

- User/membership/invitation mutations.
- Full Agent Admin CRUD/governance UI.
- Prompt/skill editors and approval workflows.
- Production LLM provider configuration.
- Full Audit/Trace explorer.

Detailed PRD: `03a-module-agent-workstream-runtime-bootstrap-prd.md`.

## Module 3: User Administration

### Goal

Authorized admins can manage people and access within tenant/customer boundaries through the User Admin functional agent. User Admin must reuse the Module 2 workstream runtime, composer, structured surface, AuthContext, and trace patterns instead of becoming a page-first admin console.

### Required visible outcome

An admin can view users, invite users, manage memberships/roles, disable access, and inspect admin audit events.

### Core scope

- User directory.
- Invitation lifecycle: create, resend, revoke/cancel, expire, accept.
- Membership lifecycle.
- Role/capability assignment.
- Disabled/suspended user handling.
- Admin audit events.
- Access review basics.
- Support access boundary if included in the seed scope.
- User Admin functional agent surfaces: users, invitations, roles/memberships, access review, and admin audit.
- Tests for role denial, disabled user denial, forbidden tenant access, invitation idempotency, and audit emission.

## Module 4: Agent Definition Foundation

### Goal

Admins can define, inspect, and govern basic agent records through the Agent Admin functional agent before advanced prompt/skill behavior is added.

### Required visible outcome

An admin can create or view agent definitions and see their purpose, status, assigned scope, model/config placeholder, and tool permission boundaries.

### Core scope

- `AgentDefinition`.
- Agent status and lifecycle basics.
- Agent purpose/description.
- Tenant/customer scope.
- Model configuration placeholder.
- Tool permission model placeholder.
- Agent Admin functional agent list/detail surfaces.
- Audit for agent definition changes.
- Tests for authorization and audit.

## Module 5: Prompt Governance

### Goal

Admins can edit, version, review, activate, and inspect runtime-managed agent system prompts.

### Required visible outcome

An admin can draft a prompt change, review differences, activate a version, and run or inspect an agent using the active prompt version.

### Core scope

- Governed prompt document model.
- Event-sourced current prompt document.
- Immutable version snapshots.
- Draft/review/approved/active/deprecated lifecycle.
- Prompt history and diff UI.
- Effective prompt assembly contract.
- Prompt assembly trace.
- Basic agent test console.
- Tests for versioning, activation, diff/history, authorization, and trace capture.

## Module 6: Skill Governance

### Goal

Admins can define shared skills, version them, and assign an approved skill manifest to each agent.

### Required visible outcome

An admin can create or inspect a skill, assign it to an agent, and observe an agent loading an approved skill through a skill read tool.

### Core scope

- Governed skill document model.
- Skill catalog.
- Skill version snapshots.
- Per-agent allowed skill manifest.
- `readSkill(skillId)` tool with authorization and version checks.
- Skill usage trace.
- Skill editor and diff UI.
- Agent test flow demonstrating skill loading.
- Tests for unauthorized skill denial, version pinning, audit trace, and prompt-injection-sensitive skill content handling.

## Module 7: Audit and Work Trace

### Goal

Users with appropriate permissions can inspect what agents and admins did, why, and under which authorized prompt/skill/policy/tool context.

### Required visible outcome

A reviewer can search and inspect trace timelines for admin actions, agent executions, prompt/skill usage, tool calls, approvals, and outcomes.

### Core scope

- Durable trace event model.
- Prompt version, skill version, model config, tool permission, data access, policy, and authorization capture.
- Work trace and decision trace views.
- Audit search/list/detail UI.
- Timeline UI.
- Correlation ids across user/session/agent/task/workflow.
- Redaction/access-control rules for trace details.
- Tests for trace emission and trace access authorization.

## Module 8: Evaluation and Closed-Loop Improvement

### Goal

Evaluator agents and review workflows can identify improvement opportunities and create governed behavior-change proposals.

### Required visible outcome

A production agent response can be evaluated, an improvement proposal can be created, reviewed with evidence, approved or rejected, and activated or rolled back according to policy.

### Core scope

- Evaluator agent definitions.
- Evaluation runs and rubrics.
- Failure/issue classification.
- Behavior improvement proposals.
- Replay/simulation evidence.
- Human approval workflow or explicitly bounded auto-approval policy.
- Canary/shadow activation support where appropriate.
- Rollback path.
- Improvement review UI.
- Evaluation result UI.
- Tests for proposal creation, approval denial, activation, rollback, and audit trace.

## Progression after the seed

After these core modules, skills pack users continue the same process for their own app-specific modules:

```text
new product module input
→ PRD/spec update
→ sprint/backlog/task generation
→ full-stack demonstrable implementation
→ audit/governance integration
```

The seed app establishes the secure, governed, AI-first substrate that later modules reuse.

## Cross-cutting requirements across all modules

Every module should preserve:

- tenant/customer scoping;
- backend authorization checks;
- frontend forbidden/empty/loading/error states;
- audit or trace emission for consequential actions;
- role/capability-aware UI;
- tests for allowed and denied behavior;
- explicit defers for capabilities not in the current module;
- visible UI/API behavior at sprint completion.
