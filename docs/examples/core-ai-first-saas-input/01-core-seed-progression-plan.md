# Core AI-First SaaS Seed Progression Plan

## Purpose

This document captures the recommended progressive module sequence for the core AI-first SaaS seed app. It is the implementation progression that future PRD/spec input documents should support.

The seed app is not implemented all at once. It is planned and delivered as a sequence of modules, each broken into sprints and harness-sized tasks. Each sprint should produce demonstrable behavior through UI and/or APIs, with tests.

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

## Module 1: Minimal Auth and App Access MVP

### Goal

Users can sign in, enter the app, retrieve their current identity/context, and use a functional authenticated browser shell.

### Required visible outcome

A user can authenticate, land in the app, see their profile/context, and encounter correct authorized/forbidden states.

### Core scope

- WorkOS/AuthKit or explicit authentication seam.
- Minimal `Account`.
- Minimal `UserProfile`.
- Minimal `Tenant`.
- Minimal `Membership`.
- Selected `AuthContext`.
- `/api/me` endpoint.
- Central backend authorization helper/pattern.
- Frontend authenticated app shell.
- Sign-in/sign-out UI.
- Authenticated landing page.
- Context display and minimal context selection if multiple memberships exist.
- Forbidden and unauthenticated UI states.
- Security tests for `/api/me`, forbidden access, tenant isolation basics, and frontend secret boundary.

### Explicit defers

- Full user administration.
- Invitation lifecycle beyond what is required for first access.
- Advanced roles/permissions UI.
- Agent definitions.
- Prompt/skill governance.
- Closed-loop improvement.

## Module 2: User Administration

### Goal

Authorized admins can manage people and access within tenant/customer boundaries.

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
- Admin UI: users, invitations, roles/memberships, access review, admin audit.
- Tests for role denial, disabled user denial, forbidden tenant access, invitation idempotency, and audit emission.

## Module 3: Agent Definition Foundation

### Goal

Admins can define, inspect, and govern basic agent records before advanced prompt/skill behavior is added.

### Required visible outcome

An admin can create or view agent definitions and see their purpose, status, assigned scope, model/config placeholder, and tool permission boundaries.

### Core scope

- `AgentDefinition`.
- Agent status and lifecycle basics.
- Agent purpose/description.
- Tenant/customer scope.
- Model configuration placeholder.
- Tool permission model placeholder.
- Agent list/detail UI.
- Audit for agent definition changes.
- Tests for authorization and audit.

## Module 4: Prompt Governance

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

## Module 5: Skill Governance

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

## Module 6: Audit and Work Trace

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

## Module 7: Evaluation and Closed-Loop Improvement

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
