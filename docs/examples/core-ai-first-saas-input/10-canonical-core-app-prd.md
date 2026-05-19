# Canonical Core App PRD: Agent Workstream Foundation

## Status

Canonical full-core PRD target for the secure AI-first SaaS seed app.

Use this document as the hard PRD input when generating the full core app. The module PRDs in this directory remain detailed progressive implementation inputs; this PRD defines the full target scope and the allowed narrower first-slice scope.

Read first:

- `00-document-development-process-context.md`
- `01-core-seed-progression-plan.md`
- `03-module-auth-app-access-prd.md`
- `04-module-user-admin-prd.md`
- `05-module-agent-definition-prd.md`
- `06-module-prompt-governance-prd.md`
- `07-module-skill-governance-prd.md`
- `08-module-audit-work-trace-prd.md`
- `09-module-evaluation-closed-loop-improvement-prd.md`
- `../../agent-workstream-application-architecture.md`

## 1. Product purpose

Build a generated full-stack secure AI-first SaaS core app that proves the skills pack's default application architecture:

```text
secure SaaS foundation
→ role-authorized functional agents
→ continuous workstreams
→ structured renderable surfaces
→ governed backend capabilities
→ horizontal Akka implementation
```

The app is not a page-first admin console and not a conventional app with a chatbot bolted on. Authenticated consequential work is organized through functional agents in an agent workstream shell. Routes and pages may exist only as implementation, deep-link, public/auth-transition, or direct surface details.

## 2. Scope choices

### Full core scope

Full core scope is the canonical generation target. It requires all foundation functional agents listed in this PRD, including **User Admin** and **Agent Admin**.

A full core app may not omit user administration or agent administration. If either is deferred, the selected scope is not full core.

### Module 1-only scope

A narrower Module 1-only scope is allowed only when explicitly recorded before generation. It includes minimal authentication, `/api/me`, selected AuthContext, profile/context display, an authenticated shell, and protected sample access.

Module 1-only scope must explicitly defer:

- User Admin functional agent;
- Agent Admin functional agent;
- invitation lifecycle and email outbox;
- governed prompts, skills, manifests, and tool boundaries;
- unified audit/work trace UI;
- governance/policy/evaluation loops.

The generated output and planning artifacts must label this as `Module 1-only / not full core`.

## 3. User-visible outcome

At full core completion, an authorized tenant operator can:

1. sign in through WorkOS/AuthKit;
2. enter an authenticated agent workstream shell;
3. see only left-rail functional agents authorized for the selected tenant/customer AuthContext;
4. use Access/Profile to inspect identity, selected context, profile, settings, and safe self-service;
5. use User Admin to invite users, manage memberships/roles, disable access, run access review, and inspect admin audit;
6. use Agent Admin to manage agent definitions, prompts, skills, manifests, tool boundaries, lifecycle state, and safe behavior tests;
7. use Audit/Trace to investigate security, admin, prompt, skill, agent-test, tool-load, decision, and work-trace activity;
8. use Governance/Policy to review behavior-change proposals, approvals, evaluation findings, and policy/rubric thresholds where the closed-loop module is included;
9. receive clear unauthenticated, no-access, disabled, forbidden, validation, approval-required, delivery-failed, stale/reconnect, and redacted states;
10. prove through tests that backend authorization, tenant isolation, audit/trace, tool-boundary denial, governed document versioning, frontend secret boundaries, and workstream surface rendering are enforced.

## 4. Functional agents

### 4.1 Access/Profile Agent

Purpose: let the signed-in user understand and manage their own safe app context.

Required surfaces:

- current identity and selected context card;
- profile/settings form;
- membership/context selector;
- capability summary card;
- no-access, disabled, forbidden, and context-invalid recovery surfaces.

Required capabilities:

- `me.read` or `profile.read`;
- `profile.update` where settings/profile editing is included;
- `tenant.context.select`;
- `app.access`.

Required tests:

- `/api/me` active, unauthenticated, no-access, disabled, and forbidden behavior;
- selected-context tenant isolation;
- browser-safe redaction;
- frontend secret-boundary checks.

### 4.2 User Admin Agent

Purpose: manage tenant users, invitations, memberships, roles/capabilities, disabled access, access review, and admin audit.

Required surfaces:

- user/member directory table;
- invitation list and invitation form;
- member detail card;
- role/capability assignment form;
- disabled/revoked access status cards;
- access review table;
- admin audit timeline/detail surfaces.

Required capabilities:

- `admin.users.read`;
- `admin.invitations.manage`;
- `admin.memberships.manage`;
- `admin.roles.manage`;
- `admin.access_review.read`;
- `admin.access_review.commit`;
- `admin.audit.read`.

Required tests:

- tenant isolation for user, invitation, membership, role, and audit reads;
- role denial and disabled-user denial;
- invitation create/resend/revoke/expire/accept idempotency;
- last-admin and self-demotion safety;
- Resend production email boundary and local/dev/test captured outbox;
- audit events for allowed and denied admin actions.

### 4.3 Agent Admin Agent

Purpose: govern runtime agent behavior as tenant-owned records and documents before agents act on production data.

Required surfaces:

- agent definition list/detail/create/edit surfaces;
- lifecycle status cards for draft, active, disabled, and archived agents;
- prompt document editor, review, diff, version history, activation, and test surfaces;
- skill document catalog/editor, manifest assignment, diff/version, and skill-loading test surfaces;
- tool permission boundary editor/review surface;
- model configuration reference surface that never accepts or displays secrets;
- agent behavior test console with deterministic/local-safe test behavior.

Required capabilities:

- `agents.read`, `agents.create`, `agents.update`, `agents.status.manage`, `agents.permissions.manage`, `agents.audit.read`;
- `prompts.read`, `prompts.create`, `prompts.draft`, `prompts.submit_review`, `prompts.review`, `prompts.activate`, `prompts.test`;
- `skills.read`, `skills.create`, `skills.draft`, `skills.submit_review`, `skills.review`, `skills.activate`, `skills.assign_manifest`, `skills.test`;
- `tool_boundaries.read`, `tool_boundaries.manage`;
- `models.read` for safe model references.

Required tests:

- capability denial for every agent/prompt/skill/tool-boundary mutation;
- cross-tenant denial for definitions, prompts, skills, versions, manifests, and test runs;
- activation readiness and disabled/archived runtime denial;
- immutable version snapshots and diff/history behavior;
- `readSkill(skillId)` allowed and denied paths based on manifest and tenant scope;
- PromptAssemblyTrace, SkillLoadTrace, AgentWorkTrace, and admin audit emission;
- frontend bundle secret-boundary checks for provider/model secrets.

### 4.4 Audit/Trace Agent

Purpose: make consequential identity, admin, governance, and agent activity inspectable.

Required surfaces:

- audit/trace landing dashboard;
- event search/list table;
- work trace list;
- correlated timeline detail;
- redacted event detail;
- optional realtime trace stream for test/demo runs;
- redacted export/copy surface if export is included.

Required capabilities:

- `audit.read`;
- `trace.read`;
- `trace.sensitive.read` when sensitive fields may be revealed;
- `trace.export` if export/copy is included;
- `trace.stream` if realtime stream is included.

Required tests:

- trace search is selected-tenant scoped;
- trace access denial is audited;
- correlation links API request, entity/workflow command, prompt assembly, skill load, tool invocation, and trace view where applicable;
- sensitive fields are redacted unless explicitly authorized;
- secret fields are never stored or displayed.

### 4.5 Governance/Policy Agent

Purpose: govern behavior-changing decisions, review evidence, and close the loop from evaluation to approved improvement.

Required surfaces:

- policy/rubric overview cards;
- evaluation run and finding tables;
- improvement proposal decision cards;
- prompt/skill/tool-boundary proposed diff reviews;
- approval/rejection workflow status cards;
- activation, canary/shadow, rollback, and outcome review surfaces where included.

Required capabilities:

- `governance.read`;
- `governance.proposals.create`;
- `governance.proposals.review`;
- `governance.approvals.commit`;
- `evaluations.read`;
- `evaluations.run` where evaluator runs are included;
- `policies.read` and `policies.manage` where policy editing is included.

Required tests:

- proposal creation from evaluator findings;
- approval denial and approval success;
- activation and rollback path;
- evidence, trace, and artifact references are preserved;
- unauthorized authority expansion is blocked and audited.

## 5. Internal agents

Full core scope may include internal agents only as backend workers, not left-rail navigation units.

Required internal-agent candidates:

- Admin briefing/summarizer for User Admin and Audit/Trace surfaces;
- behavior proposal drafting agent for Agent Admin and Governance/Policy;
- evaluator agent for closed-loop improvement;
- trace summarizer for investigation timelines;
- policy/rubric reviewer for behavior-change approval support.

Each internal agent requires an `AgentDefinition`, governed prompt/skill references when available, explicit tool boundary, model reference, AuthContext or service authority basis, trace emission, and tests.

## 6. Structured surfaces and workstream rules

Every functional agent must open with a default dashboard, attention, or briefing surface. User requests, agent responses, capability results, decisions, approvals, errors, and traces appear in the continuous main workstream.

Every surface must define:

- stable type and version;
- typed payload schema and redaction rules;
- allowed actions mapped to backend capabilities;
- tenant/customer and AuthContext assumptions;
- loading, empty, error, forbidden, stale/reconnect, and success states;
- accessible and responsive rendering behavior;
- rendering tests and action/capability tests.

Surface actions are never authorization. Backend capabilities remain authoritative.

## 7. Core durable objects

Full core scope includes at least these durable object families:

- identity and tenancy: `Account`, `UserProfile`, `UserSettings`, `Tenant`, optional `Customer`, `Membership`, `Role`, `Capability`, `AuthContext`;
- onboarding/admin: `Invitation`, `InvitationWorkflow`, `EmailOutboxMessage`, `AccessReviewItem`, `AdminAuditEvent`;
- agent governance: `AgentDefinition`, `ToolPermissionBoundary`, `ModelConfigurationRef`;
- governed documents: `PromptDocument`, `PromptVersion`, `SkillDocument`, `SkillVersion`, `AgentSkillManifest`;
- trace: `PromptAssemblyTrace`, `SkillLoadTrace`, `AgentWorkTrace`, `AuditTraceEvent`, `WorkTrace`, `TraceRedactionPolicy`;
- governance/outcomes: `EvaluationRun`, `EvaluationFinding`, `ImprovementProposal`, `ApprovalDecision`, optional `PolicyDocument`/`RubricDocument`, rollback/canary state where included.

## 8. Capability-first backend requirements

For every workstream action, surface action, agent tool, API, workflow step, timer, consumer, stream, and internal operation, define a governed capability before choosing Akka components.

Each capability contract must include:

- capability id and purpose;
- human/agent/workflow/timer/consumer/service callers;
- selected AuthContext, tenant/customer scope, role/capability requirements, and denial behavior;
- input/output schemas and redaction rules;
- validation, idempotency, side effects, and data access;
- policy, approval, escalation, and autonomy rules;
- audit/work-trace fields;
- exposure surfaces;
- success, validation, forbidden, tenant-isolation, idempotency, approval, audit/trace, tool-boundary, and rendering tests.

## 9. Akka implementation expectations

Likely horizontal Akka substrates:

- Key Value Entities for current Account, Tenant, Membership, UserProfile/UserSettings, Role/Capability, safe model references, and selected configuration;
- Event Sourced Entities for Invitation, AgentDefinition, PromptDocument, SkillDocument, AdminAuditEvent/AuditTraceEvent, governance proposals, approvals, and other audit-grade lifecycle records;
- immutable Key Value snapshots for PromptVersion and SkillVersion;
- Workflows for invitation lifecycle, approval/review flows, proposal activation, evaluator-to-improvement loops, and any long-running behavior-change orchestration;
- Consumers for outbox/email delivery, audit/trace normalization, version snapshot creation, work-trace summary maintenance, and notification/projection side effects;
- Timed Actions for invitation expiry/reminders, access review nudges, trace retention placeholders, evaluation schedules, and reminder/digest flows;
- Views for `/api/me` lookup support, user/invitation/member lists, agent/prompt/skill catalogs, manifests, audit/trace search, timelines, evaluation findings, and proposals;
- Agents for bounded internal summarization, proposal drafting, evaluation, policy review, and safe test-console behavior;
- HTTP endpoints for authenticated browser APIs, invitation acceptance, SSE streams where included, and static React/Vite frontend hosting;
- React/Vite/TypeScript frontend for the agent workstream shell, functional-agent left rail, main stream, persistent composer, typed structured surfaces, state, forms, realtime behavior, accessibility, and tests.

## 10. Security and trust boundaries

- WorkOS/AuthKit is the supported production browser authentication service; no alternate user auth service is currently supported by this skills pack.
- Resend (resend.com) is the supported production email service for invitation/account emails and future app email features.
- Local/dev/test uses explicit fakes and captured outbox behavior; automated tests must not send real email.
- Provider secrets, model API keys, Resend email service keys, raw tokens, invitation secret tokens, and backend-only policy details must never reach frontend bundles or browser-safe APIs.
- Backend authorization is required for every protected endpoint, component command, view query, stream, workflow action, consumer side effect, timer, agent tool, and internal-agent operation.
- Functional-agent rail visibility is derived from backend-resolved browser-safe capabilities and selected AuthContext.
- Tenant/customer ids supplied by clients are never trusted over the resolved AuthContext.
- Prompt text, skill content, tool descriptions, and UI affordances are not security boundaries.

## 11. Acceptance scenarios

1. **Authenticated shell** — a seeded tenant admin signs in, `/api/me` returns browser-safe selected context, and the app opens the agent workstream shell.
2. **Role-authorized functional rail** — the left rail shows Access/Profile, User Admin, Agent Admin, Audit/Trace, and Governance/Policy only when the selected AuthContext has matching capabilities.
3. **Module 1-only scope is labeled** — if generation defers User Admin or Agent Admin, artifacts state `Module 1-only / not full core` and list deferred agents.
4. **User Admin invite flow** — an admin creates, resends, revokes, expires, and accepts invitations with captured outbox in tests and audit events throughout.
5. **Disabled access denial** — disabled account or membership cannot access protected app surfaces or APIs and denial appears in audit/trace.
6. **Agent definition governance** — an admin creates, activates, disables, and archives AgentDefinitions with tenant isolation, readiness checks, and audit.
7. **Prompt governance** — prompt drafts, review, diff, activation, assembly metadata, and prompt test console preserve versions/checksums and tenant-scoped authorization.
8. **Skill governance** — skills are versioned, assigned through manifests, and loaded only through authorized `readSkill(skillId)` with SkillLoadTrace.
9. **Audit/Trace investigation** — an auditor opens a correlated timeline showing invitation, prompt, skill, and agent-test activity with redaction.
10. **Governance proposal** — an evaluator finding produces an improvement proposal that a human approves or rejects before activation.
11. **Cross-tenant denial** — attempts to access users, invitations, agents, prompts, skills, traces, or proposals from another tenant are denied without data leakage.
12. **Frontend secret boundary** — production-like frontend build contains no WorkOS, Resend, model-provider, backend, token, or invitation secrets.

## 12. Minimum test set

Full core generation must include tests for:

- `/api/me` states and selected-context behavior;
- tenant isolation across all core resource families;
- disabled-user and role/capability denial;
- invitation lifecycle, idempotency, expiry, outbox, and audit;
- User Admin surface rendering and form states;
- AgentDefinition lifecycle and capability denials;
- PromptDocument/PromptVersion lifecycle, diff/history, activation, assembly trace, and secret-boundary validation;
- SkillDocument/SkillVersion/AgentSkillManifest lifecycle, `readSkill` allowed/denied behavior, and SkillLoadTrace;
- tool permission boundary denial for unassigned or side-effecting tools;
- AuditTraceEvent and WorkTrace correlation, redaction, export/stream if included, and access denial;
- Governance/Policy proposal, approval, activation, rollback, and unauthorized authority expansion denial;
- workstream shell rendering: functional rail, main stream, persistent composer, structured surfaces, loading/empty/error/forbidden/stale states;
- frontend bundle/static asset secret-boundary checks.

## 13. Readiness checklist

The full core app is ready for decomposition/generation only when:

- [ ] scope is recorded as full core, or explicitly as Module 1-only / not full core;
- [ ] full core includes Access/Profile, User Admin, Agent Admin, Audit/Trace, and Governance/Policy functional agents;
- [ ] User Admin and Agent Admin are not omitted from full core scope;
- [ ] functional agents have role/capability authorization, default surfaces, capability mappings, traces, and tests;
- [ ] internal agents are separated from functional agents and have governed definitions and traces;
- [ ] structured surfaces have typed payloads, allowed actions, states, redaction, and rendering/action tests;
- [ ] backend capabilities are specified before Akka component selection;
- [ ] AuthContext, tenant/customer scope, invitation onboarding, `/api/me`, backend authorization, audit/trace, and tenant-isolation tests are present;
- [ ] prompt/skill/tool/model governance records are managed as tenant-scoped durable state, not only hard-coded runtime assets;
- [ ] workstream shell UI and browser APIs are mandatory generated outputs;
- [ ] all defers for narrower scope are explicit and visible in generated planning artifacts.
