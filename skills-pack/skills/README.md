# Akka Skill Routing Map

This directory contains AI-focused skills for creating and extending full-stack secure AI-first SaaS applications on Akka. It is an **internal routing layer for the harness**: users describe intent in natural language, and the harness loads the smallest relevant skill set.

In this repository, the skills serve the two-fold project purpose: maintain the secure AI-first SMB SaaS core app and help users of cloned/forked repos add business-specific domains, workstreams, surfaces, agents, Akka components, frontend extensions, app-description extensions, specs, docs, and tests.

The previous long-form routing matrix has been moved to maintainer-only pack history. Do not load historical routing by default; prefer this quick map plus the smallest focused skill that matches the current task.

## Install and path model

This file serves both:

- the **source repository**, where root `app-description/`, `specs/`, `frontend/`, `src/`, and `skills-pack/docs|examples|templates` are reference and runtime assets for the secure AI-first SMB SaaS core app;
- the **harness skills-library install**, where this routing map, `SKILL.md` files, and referenced pack docs/examples/templates/tools are copied under `.agents/skills` while the target project's maintained `app-description/`, `specs/`, source, and frontend stay in the project workspace.

When a skill lists required reads as target-project paths such as `AGENTS.md`, `specs/**`, `app-description/**`, `frontend/**`, or `src/**`, resolve them in the current target project workspace, not relative to a global `~/.agents/skills` install. Relative `../docs/**`, `../examples/**`, `../templates/**`, `../tools/**`, and `../references/**` paths are installed pack assets.

Java base package for generated code:

- always use the fixed base package `ai.first` for this core app baseline and downstream generated code;
- do not ask users to choose a Java package and do not create pending questions about the Java base package;
- record `ai.first` in app-description/spec/generation artifacts and apply it consistently.

Use `domain-specific` or the user's actual domain name for later product features. Never say `DCA-specific` unless DCA is explicitly the user's domain; do not use historical/example domain names as generic placeholders.

## Core-app-first routing

For new secure AI-first SaaS apps where the user wants an implementation baseline, prefer fork-and-extend from this runnable secure AI-first SMB SaaS core app repository root. Do not expect the skills install to contain or render a duplicate full-app baseline.

For this repository's runnable core app and downstream forks that keep its merge-friendly layout, preserve the standard Akka Java layers and use `foundation`, `coreapp`, and `business.<area>` partitions inside them:

- reusable SaaS/platform code under `<base>.api|application|domain.foundation.*`;
- built-in five-core-workstream code under `<base>.api|application|domain.coreapp.*`;
- user-owned domain-specific extensions under `<base>.api|application|domain.business.<area>.*`.

Do not place new product-specific Java code in legacy top-level `security`, `agentfoundation`, `admin`, or `workstream` packages when extending the core app. Users extend the root app workspace with business-specific domains, workstreams, surfaces, agents, Akka components, frontend extensions, app-description extensions, specs, docs, and tests. Do not regenerate a parallel fresh app, replace existing foundation files, or use `.agents/` resources as writable project source unless explicitly requested.

## AI-first SaaS entry routing

For high-level product input, treat the target as a secure AI-first SaaS **agent workstream application** unless the user explicitly asks for repository-maintenance-only or non-SaaS reference material.

Security is mandatory. Load `core-saas-foundation` early whenever generated-app foundation rules are in scope. The mandatory secure SaaS foundation includes WorkOS/AuthKit browser authentication, local Akka-owned authorization state, account/profile/settings/membership/role/capability state, `/api/me`, email-invite onboarding with Resend, backend authorization checks, tenant/customer scoping, support access, admin audit, audit/work traces, workstream UI surfaces, tenant-isolation tests, and frontend secret boundaries.

AI-first managed agents / governed runtime agent foundation is mandatory for model-backed workstream behavior. Use `../docs/governed-agent-substrate.md` for the shared record/runtime/trace model; focused agent-governance skills own only their specific slice.

Scope control: load only the smallest companion set needed for the current user request. Use the full foundation/admin path only when creating or changing secure SaaS foundation behavior, app-generation readiness, or model-backed workstream runtime. For narrow maintenance, documentation, isolated Akka component work, or domain-specific extension work, preserve the mandatory guardrails without expanding the task into unrelated foundation verticals.

Canonical doctrine:

- `../docs/generated-saas-canonical-doctrine.md`
- `../docs/ai-first-saas-application-architecture.md`
- `../docs/minimum-ai-first-saas-app.md`
- `../docs/requirements-to-workstream-development-process.md`
- `../docs/agent-workstream-application-architecture.md`
- `../docs/domain-workstream-prd-structure.md`
- `../docs/structured-surface-contracts.md`
- `../docs/capability-first-backend-architecture.md`
- `../docs/core-ai-first-saas-foundation.md`
- `../docs/core-saas-identity-tenancy-admin.md`
- `../docs/full-core-foundation-readiness.md`
- `../docs/core-saas-owner-tenant-billing.md`

Top-level entry skills:

- `ai-first-saas` — interpret product intent as a secure AI-first SaaS operating model and route to app-description, decomposition, planning, or implementation.
- `agent-workstream-apps` — interpret generated full-stack AI-first SaaS apps as role-authorized functional-agent workstream applications.
- `core-saas-foundation` — apply the non-optional secure SaaS baseline for every new project/app/PRD/spec/backlog unless the user explicitly asks for non-SaaS reference material.
- `capability-first-backend` — model backend behavior as governed capabilities before choosing Akka components or exposure surfaces.

Mandatory foundation companions:

- `akka-saas-invitation-onboarding` — Invitation entity/audit, InvitationWorkflow, Resend production email delivery, local/dev/test captured outbox, expiry/reminder timers, InvitationView, admin APIs/UI, and lifecycle tests.
- `akka-resend-email-service` — reusable Resend delivery/outbox for invitations, account emails, app emails, and governed agent email tools.
- `akka-basic-user-admin` — user directory/search, memberships, roles, support access, admin audit/search, access review, last-admin safeguards, and backend authorization checks.
- `akka-workos-user-auth` — WorkOS/AuthKit frontend auth, JWT bearer API calls, Akka `@JWT`, `/api/me`, and secret boundaries.

AI-first companion skills:

- `ai-first-saas-object-model`
- `ai-first-saas-agent-team-design`
- `ai-first-saas-admin-agents`
- `akka-agent-governed-documents`
- `ai-first-saas-policy-governance`
- `ai-first-saas-decision-cards`
- `ai-first-saas-audit-trace`
- `ai-first-saas-ui-surfaces`
- `ai-first-saas-outcomes-metrics`

Use companion skills only for concerns actually in scope, except foundation/admin companions that are mandatory when generating the secure core SaaS foundation.

## Canonical generated-app handoff order

Secure AI-first SaaS interpretation → agent workstream model → mandatory core SaaS foundation verticals → affected workstreams → role-specific dashboard attention model → human surface graph → internal workstream agent graph → governed-tools in capability files and surface/action maps → capability-first backend substrate → description/decomposition/planning path → focused implementation.

For minimum/core app/basic/chatbot-like generated SaaS prompts, the first runnable target is the five-core-workstream core app domain with `markdown_response` for My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy. It is not a generic public chatbot or page-first CRUD shell.

## Implementation completion standard

Generated Akka app features are complete only when they work at the stated scope through the intended local Akka/API/UI runtime path. For the canonical runtime-completion doctrine, including governed Akka `Agent` invocation, provider fail-closed behavior, and fixture/mock boundaries, read `references/generated-saas-runtime-completion.md`.

## Description-first path

Start here when the user is primarily describing or revising the app before realization:

- `app-descriptions` — orchestrate description-first work across intake, behavior, tests, security, observability, readiness, generation, and review.
- `app-description-bootstrap`
- `app-description-input-normalization`
- `app-description-intake-router`
- `app-description-functional-agent-modeling`
- `app-description-surface-modeling`
- `app-description-capability-modeling`
- `app-description-behavior-specification`
- `app-description-test-specification`
- `app-description-change-impact`
- `app-description-auth-security`
- `app-description-observability`
- `app-description-ui`
- `app-description-readiness-assessment`
- `app-generate-app`

## Planning and queue skills

- `akka-prd-to-specs-backlog` — PRD/high-level requirements to specs, backlog, execution order, and pending-task package.
- `akka-solution-decomposition` — decompose requirements into the minimal Akka Java SDK component set after capability contracts are clear.
- `akka-slice-spec-to-backlog`
- `akka-backlog-to-pending-tasks`
- `akka-backlog-item-to-task-brief`
- `akka-do-next-pending-task`
- `akka-pending-task-queue-maintenance`
- `akka-pending-question-generation`
- `akka-do-next-pending-question`
- `akka-pending-question-queue-maintenance`
- `akka-change-request-to-spec-update`
- `akka-revised-prd-reconciliation`
- `project-discussed-idea-to-pending-project`

Planning outputs must include AgentDefinition, PromptDocument, SkillDocument, AgentSkillManifest, readSkill, PromptAssemblyTrace, SkillLoadTrace, behavior editing / AgentBehaviorEditorAgent coverage, agent catalog, agent detail, invitation lifecycle, email delivery, UserDirectoryView, MembershipView, InvitationView, AdminAuditView, AccessReviewQueueView, AI admin/AdminRiskAgent/AccessReviewAgent, decision cards for risky admin, and vertical contract block/line details whenever the secure foundation is in scope.

## Focused implementation skill families

Load only the family/focused skill needed for the current implementation slice:

- Agents: `akka-agents`, `akka-agent-component`, `akka-agent-tools`, `akka-agent-component-tools`, `akka-agent-mcp-tools`, `akka-agent-memory`, `akka-agent-streaming`, `akka-agent-structured-responses`, `akka-agent-testing`, `akka-agent-guardrails`, `akka-agent-evaluation`, governed behavior/profile/prompt/skill/reference/model/tool-boundary/work-trace skills.
- Autonomous Agents: `akka-autonomous-agents`, `akka-autonomous-agent-tasks`, `akka-autonomous-agent-coordination`, `akka-autonomous-agent-governance`, `akka-autonomous-agent-testing`; read `../docs/autonomous-agent-worker-runtime-pattern.md` and `../docs/autonomous-agents-api-notes.md`.
- Entities: `akka-key-value-entities`, `akka-event-sourced-entities`, focused KVE/ESE domain, application, edge/flow, TTL, notification, replication, unit, and integration skills.
- Workflows: `akka-workflows`, `akka-workflow-component`, `akka-workflow-pausing`, `akka-workflow-compensation`, `akka-workflow-notifications`, `akka-workflow-testing`.
- Views: `akka-views`, source-specific view skills, query patterns, streaming, and testing.
- Consumers: `akka-consumers`, source-specific consumer skills, producing, and testing.
- Timers: `akka-timed-actions`, `akka-timers-scheduling`, `akka-timed-action-component`, `akka-timed-action-testing`.
- HTTP endpoints: `akka-http-endpoints`, component-client, JWT, request-context, ACL/internal, low-level, SSE, WebSocket, web UI, HTTP client provider, and testing.
- gRPC endpoints: `akka-grpc-endpoints`, proto design, component-client, JWT, request-context, streaming, and testing.
- MCP endpoints: `akka-mcp-endpoints`, component-client, request-context, resources/prompts, and testing.
- Web UI: `akka-web-ui-apps`, frontend project, API client, UX design, accessibility/responsive, state rendering, forms validation, realtime, testing.

When this quick map is too terse, inspect the relevant focused skill family directly instead of loading a broad historical routing catalog. For retired/static/legacy content boundaries, use `../docs/retired-content-boundaries.md` rather than repeating long warnings in normal skill text.
