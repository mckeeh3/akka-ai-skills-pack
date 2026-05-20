# Akka Skill Routing Map

This directory contains AI-focused skills for creating full-stack secure AI-first SaaS applications on Akka: interpret high-level requirements through the mandatory SaaS security foundation and agent workstream application model, model governed backend capabilities, derive the right Akka Java SDK solution, then turn that solution plan into concrete backend, frontend, test, and delivery assets.

These skills are primarily an **internal routing layer for the harness**.
Users should be able to describe intent in natural language; the harness should infer the right path and load the smallest relevant skill set.

Java base package intake for generated code:
- before creating a new Java Akka project, scaffolding Java source files, or realizing an app description into Java code, ask: "What Java base package should I use for generated code? Press Enter to use `ai.first`."
- use `ai.first` as the default only when the user accepts or defers the choice
- do not use `com.example` as a generated application package unless explicitly requested; bundled `com.example` examples are reference material only
- record the selected package in app-description/spec/generation artifacts and apply it consistently to group id, packages, imports, tests, and source paths

This file serves both:
- the **source repository**, where app-description trees under `docs/examples/` are reference assets for the pack itself
- the **installed pack** in a real development project, where the project's maintained `app-description/` tree belongs in the project workspace rather than under `.agents/`, unless that project explicitly chooses another internal location

Starter scaffold routing for installed packs:
- skills-only install remains the default: `.agents/` is a guidance/resource library and application artifacts live in the target workspace
- for new secure AI-first SaaS apps where the user wants an implementation baseline, prefer explicit scaffold-then-extend: run `.agents/bin/scaffold-ai-first-saas-starter.sh` in an empty or bootstrap-only project, then extend the scaffolded `app-description/`, `specs/`, backend, and frontend
- the starter template is the canonical full-core generated-app implementation baseline; DCA/supplies, purchase-request, shopping-cart, and standalone static UI examples are domain/mechanics references only
- if `specs/scaffold-report.md` exists, treat the project as scaffolded from the starter; preserve the recorded Java base package, starter foundation, workstream UI, and queue history; update app-description/specs before adding implementation tasks
- do not regenerate a parallel fresh app, replace scaffolded foundation files, or use `.agents/resources/templates/ai-first-saas-starter/` as a writable project source unless the user explicitly asks for destructive reset or template maintenance

## AI-first SaaS entry routing

For high-level product input, treat the target as a secure AI-first SaaS **agent workstream application** unless the user explicitly asks for repository-maintenance-only or non-SaaS reference material. Interpret mandatory foundation security, role-authorized functional agents, continuous workstreams, structured surfaces, delegated operational work, autonomous or semi-autonomous decisions, agent or agent-team execution, policy/permission controls, human supervision, approval or exception handling, audit traces, and outcome accountability before modeling capabilities or decomposing into Akka components.

Mandatory secure SaaS, agent workstream, and web UI foundation before app-specific features:
- WorkOS/AuthKit browser user authentication (the supported user auth service)
- local Akka-owned authorization state: Account, UserProfile, UserSettings, Membership, Role, Permission/Capability, and selected AuthContext
- governed runtime agent foundation: AgentDefinition, PromptDocument/PromptVersion, SkillDocument/SkillVersion, AgentSkillManifest, ToolPermissionBoundary, PromptAssemblyTrace, SkillLoadTrace, AgentWorkTrace, deterministic prompt assembly, authorized readSkill(skillId), and first-install/tenant-bootstrap seeding of implementation-developed default prompt/skill/manifest/tool-boundary documents into governed storage
- SaaS Owner, Tenant, and Customer organization model with tenant/customer-scoped commands and queries
- `/api/me` for the signed-in account, memberships, selected context, profile, settings, and browser-safe capabilities
- complete email-invite onboarding with Resend (resend.com) as the supported production email service, explicit local/dev/test captured outbox behavior, and the same Resend email service reusable for other app email features and governed agent `@FunctionTool` email tools
- backend authorization checks for every protected route, component command, view query, stream, agent tool, workflow action, consumer side effect, and timer action
- AdminAuditEvent and audit/work traces for identity, authorization, policy, approval, data access, and consequential AI/tool activity
- mandatory agent workstream shell with role-authorized functional agents, continuous main workstream, persistent composer, context/authority indicators, and structured surfaces for sign-in, context selection, profile/settings, Users, Invitations, Roles/Memberships, Access Review, Support Access, Admin Audit, Tenant/Customer Settings, supervision, decisions, governance, audit/traces, and outcome review
- tenant-isolation, forbidden-access, disabled-user, role/scope denial, audit, frontend secret-boundary, UI, and security-review tests

Canonical doctrine:
- `../docs/ai-first-saas-application-architecture.md`
- `../docs/agent-workstream-application-architecture.md`
- `../docs/structured-surface-contracts.md`
- `../docs/capability-first-backend-architecture.md`
- `../docs/core-ai-first-saas-foundation.md`
- `../docs/core-saas-identity-tenancy-admin.md`
- `../docs/core-saas-owner-tenant-billing.md`

Top-level AI-first entry skill:
- `ai-first-saas` — interpret product intent as a secure AI-first SaaS operating model, identify mandatory foundation/security requirements, delegated work, and retained human authority, then route to app-description, decomposition, PRD planning, or focused implementation skills

Top-level agent workstream entry skill:
- `agent-workstream-apps` — interpret generated full-stack AI-first SaaS apps as role-authorized functional-agent workstream applications, identify functional agents, internal agents, workstreams, and structured surfaces, then route to app-description, capability-first backend, web UI, agent, decomposition, PRD/backlog, or focused implementation skills

Mandatory foundation skill:
- `core-saas-foundation` — apply the non-optional secure SaaS baseline for every new project/app/PRD/spec/backlog unless the user explicitly asks for non-SaaS reference material; define SaaS Owner, Tenant, Customer, Account, UserProfile, UserSettings, Membership, Role, Permission/Capability, Invitation, AuthContext, AdminAuditEvent, governed runtime agent foundation (`AgentDefinition`, `PromptDocument`/`PromptVersion`, `SkillDocument`/`SkillVersion`, `AgentSkillManifest`, `ToolPermissionBoundary`, `PromptAssemblyTrace`, `SkillLoadTrace`, `AgentWorkTrace`, authorized `readSkill(skillId)`), support-access, subscription/billing boundary, `/api/me`, backend authorization, tenant/customer-scoped commands and queries, and tenant-isolation tests before app-specific features

Mandatory foundation companion skill:
- `akka-saas-invitation-onboarding` — implement complete email-invite onboarding with Invitation entity/audit record, InvitationWorkflow, Resend (resend.com) production email delivery, local/dev/test captured outbox, email delivery/outbox Consumer, expiry/reminder TimedAction, InvitationView, admin endpoints/UI, resend, revoke, expiry, acceptance, delivery failure visibility, idempotency, and lifecycle tests
- `akka-resend-email-service` — implement the single supported production email service for generated apps: reusable Resend delivery/outbox for invitation/account emails and future app feature emails, local/dev/test captured outbox, delivery status/failure audit, and governed `@FunctionTool` exposure for agents

AI-first companion skills:
- `ai-first-saas-object-model` — select durable goals, plans, policies, decisions, traces, outcomes, and related substrate objects before choosing Akka components
- `ai-first-saas-agent-team-design` — design bounded coordinator/specialist/evaluator agent teams, authority limits, tools, escalation rules, traces, and workflow supervision
- `ai-first-saas-admin-agents` — make AI-assisted admin offload mandatory for generated SaaS foundations with bounded admin responsibilities backed by governed `AgentDefinition`, prompts, skills, manifests, tool boundaries, traces, decision cards, and approval rules
- `akka-agent-seed-documents` — seed implementation-developed default `AgentDefinition`, prompt, skill, manifest, and tool-boundary records into app-managed governed storage on first install or tenant bootstrap, with idempotency, provenance, upgrade behavior, audit, and tests
- `ai-first-saas-policy-governance` — model policies, clauses, permissions, thresholds, approval gates, proposals, simulations, and human-governed commits
- `ai-first-saas-decision-cards` — design recommendation, approval, exception, and deviation review surfaces with evidence, risk, confidence, impact, alternatives, and actions
- `ai-first-saas-audit-trace` — design durable work, decision, policy, tool, data-access, approval, and outcome traces
- `ai-first-saas-ui-surfaces` — select supervision, decision, governance, digest, goal-to-execution, and audit UI surfaces and route to web UI/API skills
- `ai-first-saas-outcomes-metrics` — define outcome loops, metrics, decision/outcome links, feedback, replay, and validation surfaces

After secure AI-first SaaS interpretation, route through `core-saas-foundation`, interpret the application as an agent workstream with functional agents, internal agents, workstreams, and structured surfaces, model the capability-first backend substrate, then choose the normal operating path:
1. use `app-descriptions` when the user is maintaining or reviewing the authoritative app description before realization; functional agents, surfaces, capability inventory, behavior, auth/security, UI, observability, readiness, and tests belong in the description layer
2. use `akka-solution-decomposition` when the user wants direct Akka solution shaping and the component set is not yet known; derive governed capabilities from workstream actions, surfaces, agents, workflows, APIs, timers, and consumers before selecting Akka components
3. use `akka-prd-to-specs-backlog` when the user wants repo-ready specs, backlog, and pending-task artifacts; preserve functional-agent/surface context plus capability ids, auth/scope, side effects, approval, audit, exposure surfaces, and tests in generated tasks
4. use focused Stage 3 component skills only after the secure foundation, agent workstream model, capability contracts, and solution shape are clear enough for implementation

Capability-first backend design is the substrate step below secure AI-first SaaS and the agent workstream application model, and above component implementation. A capability is a named operation or query with explicit actors/callers, AuthContext, input/output schemas, data access, side effects, idempotency, policy/approval rules, audit/trace requirements, selected exposure surfaces, and tests. Workstream actions, structured surface actions, browser actions, HTTP/gRPC/MCP endpoints, workflow steps, timers, consumers, internal component methods, and agent tools are exposure or realization choices for capabilities; they are not the root abstraction. Use the top-level `capability-first-backend` skill with `../docs/capability-first-backend-architecture.md` for capability modeling and routing.

Capability-first backend entry skill:
- `capability-first-backend` — model backend behavior as governed capabilities before choosing Akka components or exposure surfaces, then route to app-description, decomposition, PRD/backlog, or focused implementation skills

Use companion skills only for the AI-first concerns that are actually in scope, except `ai-first-saas-admin-agents` which is mandatory whenever generating the core SaaS user-administration foundation. Existing Stage 3 skills remain horizontal implementation substrate skills for the AI-first, agent-workstream, and capability-first architecture: agents, workflows, entities, views, consumers, timed actions, endpoints, and web UI delivery. Do not replace those skills with narrative guidance; route to them after functional agents, surfaces, goals, plans, authority, capabilities, supervision, traces, UI surfaces, and outcome loops are clear enough for the requested scope.

## Description-first intake skills

This repository now also includes an initial description-first skill path for maintaining **application descriptions as the source of truth** before app realization.

Start here when the user is primarily describing or revising the app rather than explicitly asking for code generation. For generated full-stack AI-first SaaS apps, the description-first path should capture the agent workstream model as the primary application structure: functional agents and reusable structured surfaces first, then governed capabilities and horizontal Akka implementation choices. Do not make a conventional page/screen tree the primary decomposition unless the task is explicitly non-SaaS, static/public, or repository-maintenance-only.

Top-level app-description entry skill:
- `app-descriptions` — orchestrate description-first work across intake, behavior, tests, security, observability, readiness, generation, and review

Initial app-description skills:
- `app-description-bootstrap` — create the initial internal app-description tree for a new app or sparse early app idea
- `app-description-input-normalization` — convert flexible user input into a structured app-description delta envelope
- `app-description-intake-router` — classify flexible user input and choose the next description-maintenance or realization path
- `app-description-functional-agent-modeling` — update the authoritative `12-workstreams/functional-agents.md` layer for role-authorized user-facing context-area agents, prompt intent, skills, tools, surfaces, capabilities, traces, and tests
- `app-description-surface-modeling` — update the authoritative `12-workstreams/surfaces-index.md` and `surface-contracts/**` layer for structured workstream surfaces, typed payloads, reusable functional-agent placement, capability-backed actions, states, traces, and tests
- `app-description-capability-modeling` — update the authoritative capability layer of the app description
- `app-description-behavior-specification` — update the authoritative behavior layer of the app description
- `app-description-test-specification` — update the authoritative test layer of the app description
- `app-description-change-impact` — determine which layers, maps, readiness state, and generated outputs are affected by a change
- `app-description-auth-security` — update the authoritative auth/security layer of the app description
- `app-description-observability` — update the authoritative observability layer of the app description
- `app-description-ui` — update the authoritative frontend/UI realization layer of the app description after functional-agent/surface/capability meaning is clear, including mandatory `55-ui/style-guide.md` for generated full-stack AI-first SaaS apps
- `app-description-readiness-assessment` — assess whether the current app description is sufficiently complete for generation
- `app-generate-app` — realize the current app description as generated outputs
- `app-description-change-summary` — summarize what changed after a revision request
- `app-description-readiness-summary` — summarize why the description is or is not ready for generation

Default description-first flow:
1. bootstrap with `app-description-bootstrap` when no usable app-description tree exists yet
2. normalize the input with `app-description-input-normalization` when the request is broad, mixed, or ambiguous
3. route the user input with `app-description-intake-router`
4. update functional agents with `app-description-functional-agent-modeling` when ordinary user language asks for a dashboard, admin console, portal, workspace, work queue, workflow view, command center, agent/chat area, user-facing work area, left-rail agent, prompt intent, skills, tools, surfaces, callable capabilities, authority, traces, or tests
5. update structured surfaces with `app-description-surface-modeling` when the request changes dashboards, forms, tables, charts, decision cards, diffs, audit timelines, detail cards, approvals, workflow status, surface payloads, reusable functional-agent placement, allowed actions, states, traces, or rendering tests
6. update capabilities with `app-description-capability-modeling` when the request changes scope, actors, business outcomes, backend operation/query contracts, protected operations/queries, action authority, side effects, approval, audit, or capability exposure surfaces
7. update behavior with `app-description-behavior-specification` when the request changes what the app should do
8. update tests with `app-description-test-specification` when the request needs acceptance, regression, edge-case, or verification definition
9. run `app-description-change-impact` to identify cross-layer and realization implications
10. update security with `app-description-auth-security` when the request changes identity, authorization, trust boundaries, frontend/backend JWT security, WorkOS authentication, basic administration, or data protection
11. update observability with `app-description-observability` when the request changes logs, metrics, traces, auditability, or diagnosability
12. update UI with `app-description-ui` when the request changes agent workstream shell regions, functional-agent rail behavior, workstream interactions, structured surfaces, deep-link routes, forms, frontend API contracts, realtime UI behavior, accessibility, responsive behavior, or web UI style guide
13. assess readiness with `app-description-readiness-assessment` before generation or when the user asks whether the description is ready
14. realize outputs with `app-generate-app` only when generation is requested or accepted
15. answer review questions with `app-description-change-summary` and `app-description-readiness-summary`

Reference docs:
- `../docs/examples/ai-first-saas-seed-app-description/README.md` — preferred secure AI-first SaaS seed app-description reference
- `../docs/description-first-application-doctrine.md`
- `../docs/app-description-skills-plan-backlog.md`
- `../docs/internal-app-description-architecture.md`
- `../docs/app-description-maintenance-flow.md`
- `../docs/app-description-end-to-end-workflow-example.md`
- `../docs/structured-surface-contracts.md`
- `../docs/examples/purchase-request-app-description/README.md` — description mechanics reference only; not target architecture doctrine
- `../docs/examples/purchase-request-app-description/normalized-input-example.md`

Important routing rule:
- use the description-first path to maintain or review the app's authoritative description
- use `app-generate-app` only when realization is actually requested or accepted
- use the 3-stage Akka path when the user wants direct Akka solution decomposition and implementation

## Intent-driven usage flow

Use the skills in this order:

1. read the requirements, PRD, spec, prompt, API sketch, UI brief, feature request, or change request
2. apply secure AI-first SaaS interpretation first, then interpret the generated app as an agent workstream application: role-authorized functional agents, internal agents, continuous workstreams, structured surfaces, retained human authority, supervision, decisions, governance, audit, and outcomes
3. model the capability-first backend substrate for every workstream action, surface action, tool, workflow step, API, timer, consumer, and internal operation: named operations/queries, actors, AuthContext, schemas, side effects, idempotency, policy/approval, audit/trace, exposure surfaces, and tests
4. use Stage 1 decomposition via `akka-solution-decomposition` when the solution shape is still unclear; Stage 1 should turn capabilities into an Akka component plan rather than skipping directly to entities, endpoints, pages, or agent tools
5. use Stage 2 only if one structural decision is still unresolved, such as `akka-entity-type-selection`
6. move to Stage 3 to load only the focused implementation skills for the chosen components and capability exposure surfaces
7. use the accepted solution plan as the implementation contract for the downstream coding phase
8. generate code and tests only after capability contracts, decomposition, and structural selection are done

Short reusable version:
- `../docs/intent-driven-usage-flow.md`
- `../docs/prd-to-akka-flow.md`
- `../docs/module-sprint-planning.md`

## Visible 3-stage skill model

Use the skill library as a visible 3-stage hierarchy:

### Stage 1: Intent and architecture
Start here when you have a PRD, requirements doc, user story, process description, API sketch, UI brief, or similar high-level input and still need to derive the Akka solution shape. For high-level product inputs, apply the AI-first SaaS interpretation rule above, interpret generated full-stack apps as agent workstream applications, then identify governed backend capabilities before mapping the work to Akka components.

Primary Stage 1 entry skills:
- `akka-solution-decomposition`
- `akka-prd-to-specs-backlog` — use when the user wants repo-ready planning artifacts under `specs/`, not just a solution plan

### Stage 2: Structural decisions
Use this stage when you already know part of the architecture, but still need to resolve a focused design choice before coding.
This is a narrower follow-on stage, not the default front door for broad requirements.

Primary Stage 2 skill currently available:
- `akka-entity-type-selection` — choose between Event Sourced Entity and Key Value Entity when you know you need state but not which state model

### Stage 3: Focused component implementation
Use this stage only after the capability contracts and solution shape are already clear enough to generate code.
This is the downstream implementation phase driven by the accepted capability-aware solution plan.

Stage 3 is the family of focused implementation skills for peer building blocks such as:
- workflows
- views
- consumers
- timed actions
- endpoints and web UI delivery
- agents
- entities

Entities are one Stage 3 family among several peers.
Not every task starts at Stage 3.
If all you have is a requirements artifact or other broad specification input, start at Stage 1.
Even if the problem sounds stateful, use Stage 1 first when the overall component set is still unknown.
Use Stage 2 only when the task is already narrowed to a stateful core and you still need to choose the entity style.
Move to Stage 3 when the architecture is settled enough to write code and tests.

Primary flow:
1. start from a PRD, requirements doc, user story, process description, API sketch, UI brief, or similar high-level input
2. identify functional agents, internal agents, initial workstreams, structured surfaces, and capability contracts before choosing implementation surfaces
3. use Stage 1 to decompose those capabilities into the right horizontal Akka component set
4. use Stage 2 when a focused architecture decision is still unresolved
5. use Stage 3 to load only the implementation skills needed for the chosen components and exposure surfaces

Current local Stage 3 suites:
- Agents
- Workflows
- Views
- Consumers
- Timed Actions
- HTTP Endpoints, Akka-hosted web UI apps, and web UI delivery patterns
- gRPC Endpoints
- MCP Endpoints
- Event Sourced Entities
- Key Value Entities

If you have high-level requirements, a prompt, or a specification file and do not yet know the Akka component set, first apply secure AI-first SaaS and agent workstream interpretation, then start with Stage 1:
- `akka-solution-decomposition`

If the task is already narrowed to a stateful component and you have not yet chosen the entity type, start with Stage 2:
- `akka-entity-type-selection`

You can also consult the comparison/reference files:
- `references/akka-entity-comparison.md`
- `references/akka-grpc-jwt-patterns.md`
- `../docs/agent-coverage-matrix.md`
- `../docs/agent-runtime-state-reference.md`
- `../docs/agent-runtime-invocation-pattern.md` — managed runtime invocation sequence from AuthContext through AgentDefinition, prompt assembly, compact AgentSkillManifest, ToolPermissionBoundary, Java Agent invocation, readSkill authorization, and PromptAssemblyTrace/SkillLoadTrace/AgentWorkTrace emission
- `../docs/workflow-endpoint-pattern.md`
- `../docs/timer-pattern-selection.md`

## PRD planning entry skills

### Solution decomposition skill

Start with:
- `akka-solution-decomposition`

Use when the goal is to derive the Akka component set and implementation handoff, but not necessarily to materialize a full repository planning package.

### PRD to specs/backlog skill

Start with:
- `akka-prd-to-specs-backlog`

Use when the task starts from a PRD or high-level requirements and the user wants the result written as a repository planning structure under `specs/`. Generated full-stack AI-first SaaS PRD planning should preserve the agent workstream application shape before capability and component planning: functional agents, internal agents, workstreams, structured surfaces, retained human authority, backend capabilities, exposure surfaces, and tests. Outputs include:
- `specs/akka-solution-plan.md`
- cross-cutting specs
- for large inputs, module specs under `specs/modules/` and vertical full-stack sprint specs under `specs/sprints/`
- for smaller inputs, numbered slice specs under `specs/slices/`
- numbered build backlogs aligned to sprints or slices
- `specs/pending-tasks.md` as the durable follow-on execution queue
- execution-order readmes
- optional leaf task briefs when one backlog item is still too large for a single focused harness run

This skill builds on `akka-solution-decomposition` and continues into harness-friendly file generation for downstream implementation. For larger PRDs, it should prefer module-oriented vertical sprints around functional agents and their surfaces so a team can finish, test, and review one workstream slice's backend and frontend stack before moving on.

### Slice spec to backlog skill

Start with:
- `akka-slice-spec-to-backlog`

Use when a `specs/slices/*.md` file already exists and the next task is to generate or refine only the matching `specs/backlog/*-build-backlog.md` file.

This is the narrow follow-on planning skill for turning one slice into an implementation-ready backlog without redoing the full PRD decomposition.
The backlog should expose bounded harness-sized task items and update `specs/pending-tasks.md`; if one task item is still too large, continue decomposition before coding.

### Backlog item to task brief skill

Start with:
- `akka-backlog-item-to-task-brief`

Use when a backlog file already exists and one specific item from its `Suggested harness task breakdown` still needs to be turned into a smaller physical task brief under `specs/tasks/` before coding.

This is the leaf-planning skill for converting one backlog item into a single focused implementation contract with exact reads, scope, non-goals, skills, outputs, tests, and done criteria, then updating the matching `specs/pending-tasks.md` entry.
See `skills/akka-backlog-item-to-task-brief/SKILL.md` for example invocation patterns.

### Pending question queue skills

Use these before pending-task materialization when unresolved product, architecture, security, testing, or delivery decisions would otherwise force the harness to guess.

Start with:
- `akka-pending-question-generation`

Use when a PRD, app-description, solution plan, slice spec, or backlog has open decisions that should be captured as `specs/pending-questions.md` and answered one at a time.

Follow-on skills:
- `akka-do-next-pending-question` — ask or reconcile exactly one actionable question from `specs/pending-questions.md`
- `akka-pending-question-queue-maintenance` — audit, repair, deduplicate, unblock, supersede, or reconcile a long-lived question queue

This queue is the clarification counterpart to `specs/pending-tasks.md`:
- `specs/pending-questions.md` records unresolved decisions and user answers
- `specs/pending-tasks.md` records implementation work

Do not create or execute implementation tasks for work blocked by unresolved `blocking` questions unless the question is explicitly deferred with an accepted default or limitation.

### Pending task queue materialization skill

Start with:
- `akka-backlog-to-pending-tasks`

Use when `specs/backlog/*.md` files already exist but `specs/pending-tasks.md` is missing, stale, incomplete, or needs to be synchronized with the current backlog.

Before materializing tasks, check `specs/pending-questions.md` when present. If unresolved `blocking` questions affect backlog work, block or defer only the affected tasks instead of guessing.

This is a repair/materialization skill. It creates queue entries from backlog `Suggested harness task breakdown` items without redoing the PRD decomposition and without implementing code.

### Iterative change request to spec update skill

Start with:
- `akka-change-request-to-spec-update`

Use when an existing app-description/spec/backlog/queue needs to absorb a bounded feature request, bug report, issue, or implementation discovery without redoing the full PRD decomposition. It updates authoritative meaning first, then affected specs/backlogs, then `specs/pending-tasks.md`.

### Revised PRD reconciliation skill

Start with:
- `akka-revised-prd-reconciliation`

Use when the user provides a revised or replacement PRD for a project that already has app-description/spec/backlog/queue artifacts. It compares the revised PRD with the maintained current state, preserves queue history, appends new work, blocks unresolved conflicts, and marks obsolete non-done tasks as `superseded`.

### Pending task queue maintenance skill

Start with:
- `akka-pending-task-queue-maintenance`

Use when a large or long-lived `specs/pending-tasks.md` needs audit, cleanup, stale-task detection, duplicate detection, blocked-task review, supersession, or next-runnable-task verification without implementing code.

### Pending task execution skill

Start with:
- `akka-do-next-pending-task`

Use when `specs/pending-tasks.md` exists and the user asks to continue, do the next pending task, execute a named task ID, or use `/do-next-pending-task`.

This is the manual queue-consumption skill for downstream implementation work. It selects one runnable task, prefers a fresh context session, loads only that task's required reads and skills, updates the task status, and reports the next runnable pending task.

References:
- `../docs/pending-task-queue.md`
- `../docs/examples/purchase-request-pending-tasks.md`

### Iterative planning quick routing

Use this routing for ongoing development after the first PRD planning run:
- Small feature request, bug report, issue, or implementation discovery: `akka-change-request-to-spec-update`
- Full revised/replacement PRD: `akka-revised-prd-reconciliation`
- Open design decisions need durable one-at-a-time clarification: `akka-pending-question-generation`
- Question queue exists and user wants the next question: `akka-do-next-pending-question`
- Question queue exists but may be stale/duplicated/blocked/unreconciled: `akka-pending-question-queue-maintenance`
- Task queue exists but may be stale/duplicated/blocked: `akka-pending-task-queue-maintenance`
- Task queue exists and user wants implementation: `akka-do-next-pending-task`
- Backlogs exist but task queue is missing/incomplete: `akka-backlog-to-pending-tasks`

### Solution decomposition details

Start with:
- `akka-solution-decomposition`

Use when the task begins from a product requirement, user story, process description, API sketch, UI brief, or a filename containing specifications and you need to decide the Akka component set before coding. For generated full-stack AI-first SaaS apps, identify the agent workstream verticals before decomposing into horizontal Akka components.

The output of this skill is not the final answer by itself.
It is the implementation contract for downstream work:
- it identifies the chosen components
- it defines implementation order
- it maps each component to the exact code-generation and test-generation skills to load next
- it preserves functional-agent and structured-surface context while decomposing backend behavior through governed capabilities
- it can also route endpoint generation, web UI generation, and documentation/snippet generation when those are part of the task

This skill routes to:
- `akka-workflows` for durable multi-step orchestration
- `akka-views` for list/search/reporting read models
- `akka-consumers` for async reactions, integrations, and republishing
- `akka-timed-actions` for deadlines, reminders, and expiry
- `akka-http-endpoints` for REST, SSE, WebSocket, and browser-hosted UI
- `akka-web-ui-apps` for fully capable frontend apps hosted by Akka HTTP endpoints, including standard frontend projects
- `akka-grpc-endpoints` for protobuf-first service APIs
- `akka-mcp-endpoints` for LLM-facing tools, resources, and prompts
- `akka-agents` when the solution genuinely needs LLM-driven behavior
- `akka-entity-type-selection` for EventSourcedEntity vs KeyValueEntity decisions when the plan includes a stateful core but the entity style is still undecided

## Planning-to-implementation handoff

Once a solution plan is accepted, treat it as the work queue for coding:
1. take the chosen components in implementation order
2. load only the Stage 3 skills named for the next component
3. generate that component's code and its corresponding tests
4. repeat for each remaining component
5. finish any downstream endpoint, web UI, or documentation/snippet work called out by the plan

For durable multi-session execution, materialize the work as `specs/pending-tasks.md` and use `akka-do-next-pending-task` to execute one task per fresh context.

Decomposition is complete only when it enables focused implementation work with low ambiguity.
Use `../docs/examples/ai-first-saas-seed-app-description/README.md` and `../docs/core-ai-first-saas-foundation.md` as the first references for generated SaaS foundation shape. Purchase-request examples are conventional planning/queue mechanics references only.
For a lightweight template, see `../docs/solution-plan-to-implementation-queue.md`.
For the durable queue contract, see `../docs/pending-task-queue.md`.

## Agent skills

Start with:
- `akka-agents`

Then load the focused skill that matches the current task:

### Behavior profiles
Use when agents are managed as tenant-scoped runtime actors with durable `AgentDefinition`, lifecycle, owner/steward, authority level, model references, tool permission boundaries, admin UI, or runtime profile lookup. For implementation handoff, pair this with `../docs/agent-runtime-invocation-pattern.md` to define the `AgentRuntimeResolver` sequence across AuthContext, active AgentDefinition, prompt assembly, compact AgentSkillManifest, ToolPermissionBoundary, Java Agent invocation, readSkill authorization, PromptAssemblyTrace, SkillLoadTrace, and AgentWorkTrace.
- `akka-agent-behavior-profiles`

### Governed documents
Use when prompts, skills, rubrics, policies, examples, or other behavior-shaping artifacts need tenant-scoped version history, immutable snapshots, review, approval, activation, deprecation, diff/history UI, or audit.
- `akka-agent-governed-documents`

### Seeded default agent documents
Use when implementation-developed default prompts, skills, manifests, tool boundaries, or AgentDefinitions must be loaded as the first governed document versions at app install, tenant bootstrap, or upgrade without overwriting tenant customizations.
- `akka-agent-seed-documents`

### Prompt governance
Use when agent system prompts need tenant-scoped review, approval, activation, version history, diff/history UI, effective prompt assembly, prompt assembly trace, or a safe prompt test console.
- `akka-agent-prompt-governance`

### Skill governance
Use when agents need tenant-scoped shared skills, skill versions, per-agent skill manifests, compact manifest prompt context, `readSkill(skillId)`, SkillLoadTrace, skill editor/review/diff UI, or a skill-loading test console.
- `akka-agent-skill-governance`

### Behavior editing
Use when an `AgentBehaviorEditorAgent` drafts prompt, skill, manifest, tool-boundary, policy, rubric, or example changes with structured proposed diffs, risk classification, draft versions, review/approval routing, decision cards, and denial of unauthorized authority expansion.
- `akka-agent-behavior-editing`

### Agent work trace
Use when agent activity needs audit/work trace events, prompt/skill/model/tool/data references, authorization basis, redaction, correlation ids, trace search, or investigation timelines.
- `akka-agent-work-trace`

### Closed-loop improvement
Use when evaluator output or trace analysis should produce EvaluationRuns, findings, improvement proposals, replay/simulation evidence, human approvals, activation, monitoring, or rollback.
- `akka-agent-closed-loop-improvement`

### Component structure
Use when writing the agent class itself.
- `akka-agent-component`

### Structured responses
Use when the agent should return typed JSON-mapped output.
- `akka-agent-structured-responses`

### Tools
Use when the agent should call local or external function tools.
- `akka-agent-tools`

### Tool permission boundaries
Use when managed agents need backend-enforced `ToolPermissionBoundary` grants, a tool registry/catalog, read-only vs side-effecting tool rules, component/MCP/readSkill tool authority, approval-required expansion, runtime denied-tool semantics, or tool invocation traces.
- `akka-agent-tool-boundaries`

### Model governance
Use when managed agents need governed `ModelConfigRef` records, model policy, tenant/agent/task model selection, fallback model policy, provider secret boundaries, model config audit/work traces, or tests for forbidden provider/secret exposure.
- `akka-agent-model-governance`

### Component tools
Use when an agent should call selected Akka View, entity, or workflow capability surfaces as tools after the capability contract permits model-invoked access.
- `akka-agent-component-tools`

### MCP tools
Use when the agent should call remote MCP-hosted tools.
- `akka-agent-mcp-tools`

### Harness-like skills through tools
Use when an Akka runtime agent should approximate harness skill loading by exposing small deploy-time packaged guidance blocks through `@FunctionTool` methods or MCP-backed resources. Use `akka-agent-skill-governance` for tenant-managed, versioned, audited runtime skills.
- `akka-agent-harness-skills`

### Multimodal
Use when the agent should send images or PDFs, or needs a custom content loader.
- `akka-agent-multimodal`

### Memory
Use when the main concern is session ids, bounded history, or filtered memory.
- `akka-agent-memory`

### Streaming
Use when the agent should stream tokens to HTTP or notifications.
- `akka-agent-streaming`

### Orchestration
Use when workflows or other components should call agents reliably.
- `akka-agent-orchestration`

### Guardrails
Use when runtime safety controls are the main concern.
- `akka-agent-guardrails`

### Evaluation
Use when LLM-as-judge or evaluator agents are the main concern. Use `akka-agent-closed-loop-improvement` when evaluator results become governed proposals, approvals, activations, monitoring, or rollback.
- `akka-agent-evaluation`

### Runtime state
Use when the task involves built-in PromptTemplate or SessionMemoryEntity state, including views, endpoints, analytics, or compaction flows.
- `akka-agent-runtime-state`

### Testing
Use:
- `akka-agent-testing` — deterministic `TestModelProvider` tests plus governed runtime checks for active profile resolution, disabled agent denial, draft prompt test/replay limits, unassigned skill denial, cross-tenant denial, `PromptAssemblyTrace`, `SkillLoadTrace`, `ToolPermissionBoundary` denial, `AgentBehaviorEditorAgent` proposal flow, and authority expansion approval/denial

## Event Sourced Entity skills

Start with:
- `akka-event-sourced-entities`

Then load the focused skill that matches the current task:

### Domain modeling
Use when working on state, events, commands, validators, command-to-event logic, or pure replay logic.
- `akka-ese-domain-modeling`

### Application entity core
Use when writing the `EventSourcedEntity` class itself.
- `akka-ese-application-entity`

### Application entity feature skills
Load these only when the task needs the feature:
- `akka-ese-ttl` — `expireAfter(...)` and automatic expiry
- `akka-ese-notifications` — `NotificationPublisher`, `NotificationStream`, SSE mapping
- `akka-ese-replication` — strong reads, replication filters, `@EnableReplicationFilter`

### Flow selection
Use when deciding how the entity participates in endpoint or internal flows.
- `akka-ese-edge-and-flow-patterns`

### Documentation snippets
Use when writing or replacing docs with focused ESE examples.
- `akka-ese-doc-snippets`

### Testing
Use:
- `akka-ese-unit-testing`
- `akka-ese-integration-testing`

## Key Value Entity skills

Start with:
- `akka-key-value-entities`

Then load the focused skill that matches the current task:

### Domain modeling
Use when working on state, commands, validators, command-to-state logic, or pure business-decision helpers.
- `akka-kve-domain-modeling`

### Application entity core
Use when writing the `KeyValueEntity` class itself.
- `akka-kve-application-entity`

### Application entity feature skills
Load these only when the task needs the feature:
- `akka-kve-ttl` — `expireAfter(...)` and automatic expiry
- `akka-kve-notifications` — `NotificationPublisher`, `NotificationStream`, SSE mapping
- `akka-kve-replication` — strong reads, replication filters, `@EnableReplicationFilter`

### Flow selection
Use when deciding how the entity participates in endpoint or internal flows.
- `akka-kve-edge-and-flow-patterns`

### Documentation snippets
Use when writing or replacing docs with focused KVE examples.
- `akka-kve-doc-snippets`

### Testing
Use:
- `akka-kve-unit-testing`
- `akka-kve-integration-testing`

## Workflow skills

Start with:
- `akka-workflows`

Then load the focused skill that matches the current task:

### Component structure
Use when writing the workflow class, state transitions, and `WorkflowSettings`.
- `akka-workflow-component`

### Compensation
Use when a later step must undo earlier work.
- `akka-workflow-compensation`

### Notifications
Use when clients should subscribe to workflow progress.
- `akka-workflow-notifications`

### Pause/resume
Use when the workflow must wait for an approval or later input.
- `akka-workflow-pausing`

### Testing
Use:
- `akka-workflow-testing`

## Timed action skills

Start with:
- `akka-timed-actions`

Then load the focused skill that matches the current task:

### Component structure
Use when writing the `TimedAction` class itself.
- `akka-timed-action-component`

### Timer scheduling
Use when the main work is `TimerScheduler.createSingleTimer(...)`, timer naming, replacement, or deletion.
- `akka-timers-scheduling`

### Testing
Use:
- `akka-timed-action-testing`

## Consumer skills

Start with:
- `akka-consumers`

Then load the focused skill that matches the current task:

### Source selection
Use the source-specific skill for the upstream you are consuming.
- `akka-consumer-from-event-sourced-entity`
- `akka-consumer-from-key-value-entity`
- `akka-consumer-from-workflow`
- `akka-consumer-from-topic`
- `akka-consumer-from-service-stream`

### Producing
Use when the consumer republishes or transforms messages into a topic or service stream.
- `akka-consumer-producing`

### Testing
Use when validating consumer flows with TestKit incoming or outgoing eventing hooks.
- `akka-consumer-testing`

## View skills

Start with:
- `akka-views`

Then load the focused skill that matches the current task:

### Source selection
Use the source-specific skill for the updater type you are implementing.
- `akka-view-from-event-sourced-entity`
- `akka-view-from-key-value-entity`
- `akka-view-from-workflow`
- `akka-view-from-topic`
- `akka-view-from-service-stream`

### Query design
Use when designing wrapper records, aliases, pagination, optional filters, or sorted queries.
- `akka-view-query-patterns`

Critical query constraints:
- every non-SSE `ORDER BY` column must also appear in the same query's `WHERE` conditions, otherwise Akka may reject the View query with `AK-00101`
- view queries exposed as SSE with `serverSentEventsForView(...)` must not include `ORDER BY`; SSE events are emitted in created/event order
- avoid optional-filter `OR` patterns; use separate query methods for separate access paths

### Streaming
Use when the view query should stream current rows or live updates.
- `akka-view-streaming`

### Testing
Use when validating projections with mocked incoming messages.
- `akka-view-testing`

## Akka-hosted web UI app skills

Start with:
- `akka-web-ui-apps`

Use when the browser UI is a real frontend application, not just asset-route wiring. For generated full-stack AI-first SaaS apps, the default UI architecture is the agent workstream shell: role-authorized functional-agent left rail, continuous main workstream, persistent composer, context/authority indicators, and structured surfaces. This family focuses on full web apps built as standard frontend projects such as React/Vite, while requiring excellent frontend behavior: surface intent, information hierarchy, UX copy, feedback/recovery states, deep-link navigation, selected style guide, state, forms, typed API clients, realtime behavior, accessibility, responsive layout, and tests.

Then load the focused skill that matches the current task:

- `akka-web-ui-ux-design` — workstream shell and structured surface intent, information hierarchy, UX copy, feedback/recovery, responsive behavior, and keyboard/focus path for non-trivial browser apps
- `akka-web-ui-frontend-project` — standard frontend project integration, build output, Akka static hosting, and SPA route shape
- `akka-web-ui-api-client` — typed fetch clients and API error mapping
- `akka-web-ui-state-rendering` — state model, rendering/component update boundaries, loading/empty/error/success states
- `akka-web-ui-forms-validation` — form parsing, validation, submit state, server error mapping
- `akka-web-ui-realtime` — browser SSE/WebSocket lifecycle and stale/reconnect behavior
- `akka-web-ui-accessibility-responsive` — semantic HTML, keyboard, focus, responsive layout
- `akka-web-ui-testing` — frontend checks/builds, route/asset/API tests, optional frontend smoke checks

Pair this family with `akka-http-endpoint-web-ui` for Akka hosting and with HTTP endpoint companion skills for JSON, SSE, or WebSocket routes. For generated SaaS apps, load JWT/request-context/internal ACL guidance as part of the mandatory security foundation; only public static asset routes are outside authenticated API authorization.

Reference docs:
- `../docs/web-ui-frontend-decomposition.md`
- `../docs/web-ui-frontend-project-integration.md`
- `../docs/web-ui-style-guide.md`
- `../docs/structured-surface-contracts.md`
- `../docs/web-ui-api-contract-patterns.md`
- `../docs/web-ui-quality-checklist.md`

## HTTP endpoint skills

Start with:
- `akka-http-endpoints`

Then load the focused skill that matches the current task:

### Component-calling endpoints
Use when the endpoint maps HTTP requests to Akka component calls.
- `akka-http-endpoint-component-client`

### Request-context endpoints
Use when the endpoint depends on query params, headers, principals, or other request metadata.
- `akka-http-endpoint-request-context`

### Web UI endpoints
Use when the service should host a packaged browser UI, especially with co-hosted JSON APIs, SSE pages, or WebSocket pages.
- `akka-http-endpoint-web-ui`

### Low-level HTTP endpoints
Use when the endpoint needs `HttpResponse`, `HttpEntity.Strict`, or other lower-level HTTP model APIs.
- `akka-http-endpoint-low-level`

### HTTP client provider endpoints
Use when the endpoint calls another HTTP service through `HttpClientProvider`.
- `akka-http-endpoint-http-client-provider`

### SSE endpoints
Use when the endpoint streams server-sent events or must support reconnects.
- `akka-http-endpoint-sse`

### WebSocket endpoints
Use when the endpoint needs bidirectional streaming over `@WebSocket`.
- `akka-http-endpoint-websocket`

### JWT-secured endpoints
Use when the endpoint validates bearer tokens and reads claims.
- `akka-http-endpoint-jwt`

Security references:
- `../docs/security-pattern-selection.md`
- `../docs/security-workos-auth-and-admin.md`
- `../docs/security-review-checklist.md`

### WorkOS user authentication
Use when an Akka-hosted browser app uses WorkOS/AuthKit for sign-in and calls `/api/...` with bearer JWTs.
- `akka-workos-user-auth`

### Basic user administration
Use when the app needs local user/account roles, `/api/me`, invites, startup admin bootstrap, role assignment, disable/activate, or tenant/customer admin scopes.
- `akka-basic-user-admin`

### SaaS invitation onboarding
Use when the app needs complete mandatory email-invite onboarding: InvitationWorkflow, invite email send/resend through Resend/outbox, revoke/cancel, expiry, acceptance, delivery status, delivery attempts, InvitationView, and admin invite UI/APIs. Add `akka-resend-email-service` whenever implementing the reusable Resend email service, other app email features, or agent email tools.
- `akka-saas-invitation-onboarding`

### Resend email service
Use when the app sends any email: invitation/account onboarding emails, reminders, decision notifications, digests, operational alerts, future app-specific feature emails, or agent-accessible email preview/send tools. Resend is the supported production email service; local/dev/test uses captured outbox behavior; agent email tools use governed `@FunctionTool` capability surfaces.
- `akka-resend-email-service`

### Internal-only ACL endpoints
Use when the endpoint should only be callable by services or needs method-level ACL overrides.
- `akka-http-endpoint-acl-internal`

### Testing
Use:
- `akka-http-endpoint-testing`

## gRPC endpoint skills

Start with:
- `akka-grpc-endpoints`

Then load the focused skill that matches the current task:

### Component-calling endpoints
Use when the endpoint maps protobuf requests to Akka component calls.
- `akka-grpc-endpoint-component-client`

### Request-context endpoints
Use when the endpoint depends on principals, gRPC metadata, JWT claims, or tracing.
- `akka-grpc-endpoint-request-context`

### Streaming endpoints
Use when the endpoint returns server-streamed protobuf replies.
- `akka-grpc-endpoint-streaming`

### JWT-secured endpoints
Use when the endpoint validates bearer tokens and reads claims.
- `akka-grpc-endpoint-jwt`

### Proto design
Use when the main task is `.proto` structure, schema evolution, or common/external protobuf types.
- `akka-grpc-proto-design`

### Testing
Use:
- `akka-grpc-endpoint-testing`

## MCP endpoint skills

Start with:
- `akka-mcp-endpoints`

Then load the focused skill that matches the current task:

### Component-calling MCP endpoints
Use when MCP tools or resources need current Akka component state.
- `akka-mcp-endpoint-component-client`

### Request-context MCP endpoints
Use when the MCP endpoint depends on headers, principals, JWT claims, or tracing.
- `akka-mcp-endpoint-request-context`

### MCP resources and prompts
Use when the task is mainly about resource URIs, URI templates, packaged resources, or prompt templates.
- `akka-mcp-endpoint-resources-prompts`

### Testing
Use:
- `akka-mcp-endpoint-testing`

## Practical combinations

### New managed runtime agent with durable behavior profile
Load:
- `akka-agents`
- `akka-agent-behavior-profiles`
- `core-saas-foundation`
- `ai-first-saas-audit-trace`
- `akka-event-sourced-entities`
- `akka-views`
- `akka-http-endpoints`
- `akka-web-ui-apps`

### New governed agent behavior documents
Load:
- `akka-agents`
- `akka-agent-governed-documents`
- `akka-agent-seed-documents` when default first versions are packaged with the app or tenant bootstrap
- `core-saas-foundation`
- `ai-first-saas-audit-trace`
- `akka-event-sourced-entities`
- `akka-key-value-entities`
- `akka-consumers`
- `akka-views`
- `akka-http-endpoints`
- `akka-web-ui-apps`

### New governed runtime prompts
Load:
- `akka-agents`
- `akka-agent-behavior-profiles`
- `akka-agent-governed-documents`
- `akka-agent-prompt-governance`
- `core-saas-foundation`
- `ai-first-saas-audit-trace`
- `akka-event-sourced-entities`
- `akka-key-value-entities`
- `akka-consumers`
- `akka-views`
- `akka-http-endpoints`
- `akka-web-ui-apps`

### New governed runtime skills
Load:
- `akka-agents`
- `akka-agent-behavior-profiles`
- `akka-agent-governed-documents`
- `akka-agent-skill-governance`
- `akka-agent-behavior-editing`
- `akka-agent-tools`
- `akka-agent-model-governance`
- `core-saas-foundation`
- `ai-first-saas-audit-trace`
- `akka-event-sourced-entities`
- `akka-key-value-entities`
- `akka-consumers`
- `akka-views`
- `akka-http-endpoints`
- `akka-web-ui-apps`

### Agent audit/work trace
Load:
- `akka-agents`
- `akka-agent-work-trace`
- `ai-first-saas-audit-trace`
- `core-saas-foundation`
- `akka-event-sourced-entities`
- `akka-key-value-entities`
- `akka-consumers`
- `akka-views`
- `akka-http-endpoints`
- `akka-web-ui-apps`

### Closed-loop agent improvement
Load:
- `akka-agents`
- `akka-agent-closed-loop-improvement`
- `akka-agent-evaluation`
- `akka-agent-work-trace`
- `akka-agent-governed-documents`
- `core-saas-foundation`
- `ai-first-saas-policy-governance`
- `ai-first-saas-audit-trace`
- `akka-workflows`
- `akka-workflow-pausing`
- `akka-event-sourced-entities`
- `akka-key-value-entities`
- `akka-views`
- `akka-http-endpoints`
- `akka-web-ui-apps`

### New single-purpose agent
Load:
- `akka-agents`
- `akka-agent-component`
- `akka-agent-structured-responses`
- `akka-agent-testing`

### New tool-using agent
Load:
- `akka-agents`
- `akka-agent-component`
- `akka-agent-tools`
- `akka-agent-testing`

### Agent with model-loadable internal guidance
Load:
- `akka-agents`
- `akka-agent-component`
- `akka-agent-tools`
- `akka-agent-harness-skills`
- `akka-agent-testing`

### New streaming agent exposed through HTTP
Load:
- `akka-agents`
- `akka-agent-streaming`
- `akka-http-endpoints`
- `akka-http-endpoint-component-client`
- `akka-agent-testing`
- `akka-http-endpoint-testing`

### New workflow-supervised agent flow
Load:
- `akka-agents`
- `akka-agent-orchestration`
- `akka-workflows`
- `akka-workflow-component`
- `akka-agent-testing`
- `akka-workflow-testing`

### Add guardrails to an agent
Load:
- `akka-agents`
- `akka-agent-guardrails`

### Add evaluator agents or LLM-as-judge checks
Load:
- `akka-agents`
- `akka-agent-evaluation`
- `akka-agent-testing`

### Work with prompt templates or session-memory runtime state
Load:
- `akka-agents`
- `akka-agent-runtime-state`

### Stateful core is already known; now decide between ESE and KVE
Load:
- `akka-entity-type-selection`

Use this only when the broader Akka component set is already clear enough that the remaining question is ESE vs KVE.
Then continue with either the ESE or KVE suite.

### New endpoint-facing event sourced entity
Load:
- `akka-event-sourced-entities`
- `akka-ese-domain-modeling`
- `akka-ese-application-entity`
- `akka-ese-edge-and-flow-patterns`
- `akka-ese-unit-testing`
- `akka-ese-integration-testing`

### New endpoint-facing key value entity
Load:
- `akka-key-value-entities`
- `akka-kve-domain-modeling`
- `akka-kve-application-entity`
- `akka-kve-edge-and-flow-patterns`
- `akka-kve-unit-testing`
- `akka-kve-integration-testing`

### Add TTL to an entity
Load either:
- `akka-ese-application-entity` + `akka-ese-ttl`
- `akka-kve-application-entity` + `akka-kve-ttl`

### Add live notifications
Load either:
- `akka-ese-application-entity` + `akka-ese-notifications`
- `akka-kve-application-entity` + `akka-kve-notifications`

### Add replication support
Load either:
- `akka-ese-application-entity` + `akka-ese-replication`
- `akka-kve-application-entity` + `akka-kve-replication`

### New workflow component
Load:
- `akka-workflows`
- `akka-workflow-component`
- `akka-workflow-testing`

### New workflow with compensation
Load:
- `akka-workflows`
- `akka-workflow-component`
- `akka-workflow-compensation`
- `akka-workflow-testing`

### New workflow with notifications
Load:
- `akka-workflows`
- `akka-workflow-component`
- `akka-workflow-notifications`
- `akka-workflow-testing`

### New workflow with pause/resume behavior
Load:
- `akka-workflows`
- `akka-workflow-component`
- `akka-workflow-pausing`
- `akka-workflow-testing`

### New timer-backed expiry or reminder flow
Load:
- `akka-timed-actions`
- `akka-timers-scheduling`
- `akka-timed-action-component`
- `akka-timed-action-testing`

Add one of these if the timer targets broader component work:
- `akka-http-endpoint-component-client`
- `akka-key-value-entities`
- `akka-event-sourced-entities`
- `akka-workflows`

### New workflow-triggered timer flow
Load:
- `akka-workflows`
- `akka-workflow-component`
- `akka-timed-actions`
- `akka-timers-scheduling`
- `akka-timed-action-testing`

### New consumer reacting to event sourced events
Load:
- `akka-consumers`
- `akka-consumer-from-event-sourced-entity`
- `akka-consumer-testing`

### New consumer reacting to key value updates
Load:
- `akka-consumers`
- `akka-consumer-from-key-value-entity`
- `akka-consumer-testing`

### New consumer reacting to workflow updates
Load:
- `akka-consumers`
- `akka-consumer-from-workflow`
- `akka-consumer-producing`
- `akka-consumer-testing`

### New topic-ingesting consumer
Load:
- `akka-consumers`
- `akka-consumer-from-topic`
- `akka-consumer-testing`

### New service-to-service subscriber consumer
Load:
- `akka-consumers`
- `akka-consumer-from-service-stream`
- `akka-consumer-producing`

### New topic or service-stream producer consumer
Load:
- `akka-consumers`
- `akka-consumer-producing`
- `akka-consumer-testing`

### New HTTP endpoint that calls components
Load:
- `akka-http-endpoints`
- `akka-http-endpoint-component-client`
- `akka-http-endpoint-testing`

### New HTTP endpoint using request context only
Load:
- `akka-http-endpoints`
- `akka-http-endpoint-request-context`
- `akka-http-endpoint-testing`

### New generated full-stack AI-first SaaS web UI app
Load:
- `akka-web-ui-apps`
- `akka-http-endpoints`
- `akka-http-endpoint-web-ui`
- `akka-web-ui-testing`
- `akka-http-endpoint-testing`

For generated AI-first SaaS, this UI work is mandatory and should implement the agent workstream shell by default, not a page-first or chatbot-bolt-on app. In this source repository, use `../docs/workstream-ui-reference-architecture.md`, reusable modules under `../frontend/src/workstream/**`, and the User Admin vertical test `../frontend/src/workstream-user-admin-vertical.contract.test.mjs` as the canonical frontend reference; in an installed pack, use the exported frontend reference under `../resources/examples/frontend/**`. If no style guide is selected in the app-description or specs, first add or answer the pending UI style-selection question from `../docs/web-ui-style-guide.md`; do not let web UI implementation choose implicitly.

Then add one or more focused frontend companions as needed:
- `akka-web-ui-frontend-project`
- `akka-web-ui-api-client`
- `akka-web-ui-state-rendering`
- `akka-web-ui-forms-validation`
- `akka-web-ui-realtime`
- `akka-web-ui-accessibility-responsive`

Then add one or more Akka HTTP companions as needed:
- `akka-http-endpoint-component-client`
- `akka-http-endpoint-sse`
- `akka-http-endpoint-websocket`
- `akka-http-endpoint-jwt` for generated SaaS API routes that require authenticated browser or service callers
- `akka-http-endpoint-acl-internal` for internal-only routes or method-level service ACLs

### New Akka-hosted frontend app shell routes
Load:
- `akka-http-endpoints`
- `akka-http-endpoint-web-ui`
- `akka-http-endpoint-testing`

Use this only for the Akka HTTP routes that serve generated frontend build output. For product UI source, load `akka-web-ui-apps` and `akka-web-ui-frontend-project`.

### New low-level HTTP endpoint
Load:
- `akka-http-endpoints`
- `akka-http-endpoint-low-level`
- `akka-http-endpoint-testing`

### New HTTP endpoint calling another HTTP service
Load:
- `akka-http-endpoints`
- `akka-http-endpoint-http-client-provider`
- `akka-http-endpoint-testing`

### New HTTP endpoint streaming SSE
Load:
- `akka-http-endpoints`
- `akka-http-endpoint-sse`
- `akka-http-endpoint-testing`

### New WebSocket endpoint
Load:
- `akka-http-endpoints`
- `akka-http-endpoint-websocket`
- `akka-http-endpoint-testing`

### New HTTP endpoint secured with JWTs
Load:
- `akka-http-endpoints`
- `akka-http-endpoint-jwt`
- `akka-http-endpoint-testing`

### New WorkOS-authenticated web app APIs
Load:
- `akka-workos-user-auth`
- `akka-http-endpoints`
- `akka-http-endpoint-jwt`
- `akka-http-endpoint-request-context`
- `akka-http-endpoint-testing`

Add `akka-web-ui-frontend-project` when implementing the frontend AuthKit shell. Add `akka-basic-user-admin` when `/api/me`, roles, invites, or admin APIs are in scope. Add `akka-saas-invitation-onboarding` when full invite lifecycle, Resend email delivery/outbox, resend, revoke, expiry, acceptance, and InvitationView work is in scope. Add `akka-resend-email-service` when implementing the shared Resend service, future feature emails, or agent `@FunctionTool` email surfaces.

### New basic user administration surface
Load:
- `akka-basic-user-admin`
- `akka-saas-invitation-onboarding`
- `akka-workos-user-auth`
- `akka-http-endpoints`
- `akka-http-endpoint-jwt`
- `akka-http-endpoint-component-client`
- `akka-http-endpoint-testing`

Add an entity skill (`akka-key-value-entities` or `akka-event-sourced-entities`) based on whether user/account state is current-state only or needs audit-grade event history. For invitation onboarding, also load `akka-resend-email-service`, `akka-workflows`, `akka-workflow-component`, `akka-consumers`, `akka-timed-actions`, `akka-timers-scheduling`, and `akka-views` as needed for InvitationWorkflow, Resend email delivery/outbox, expiry/reminders, and InvitationView.

### New internal-only HTTP endpoint
Load:
- `akka-http-endpoints`
- `akka-http-endpoint-acl-internal`
- `akka-http-endpoint-testing`

### New gRPC endpoint that calls components
Load:
- `akka-grpc-endpoints`
- `akka-grpc-endpoint-component-client`
- `akka-grpc-endpoint-testing`

### New gRPC endpoint using request context or ACLs
Load:
- `akka-grpc-endpoints`
- `akka-grpc-endpoint-request-context`
- `akka-grpc-endpoint-testing`

### New streaming gRPC endpoint
Load:
- `akka-grpc-endpoints`
- `akka-grpc-endpoint-streaming`
- `akka-grpc-endpoint-testing`

### New JWT-secured gRPC endpoint
Load:
- `akka-grpc-endpoints`
- `akka-grpc-endpoint-jwt`
- `akka-grpc-endpoint-testing`

### New gRPC protobuf contract
Load:
- `akka-grpc-endpoints`
- `akka-grpc-proto-design`

### New MCP endpoint that calls components
Load:
- `akka-mcp-endpoints`
- `akka-mcp-endpoint-component-client`
- `akka-mcp-endpoint-testing`

### New MCP endpoint using request context or JWTs
Load:
- `akka-mcp-endpoints`
- `akka-mcp-endpoint-request-context`
- `akka-mcp-endpoint-testing`

### New MCP resource or prompt endpoint
Load:
- `akka-mcp-endpoints`
- `akka-mcp-endpoint-resources-prompts`
- `akka-mcp-endpoint-testing`

### Create a view from an event sourced entity
Load:
- `akka-views`
- `akka-view-from-event-sourced-entity`
- `akka-view-query-patterns`
- `akka-view-testing`

### Create a view from a key value entity
Load:
- `akka-views`
- `akka-view-from-key-value-entity`
- `akka-view-query-patterns`
- `akka-view-testing`

### Create a view from a workflow
Load:
- `akka-views`
- `akka-view-from-workflow`
- `akka-view-query-patterns`
- `akka-view-testing`

### Create a view from a topic
Load:
- `akka-views`
- `akka-view-from-topic`
- `akka-view-query-patterns`
- `akka-view-testing`

### Create a view from another Akka service stream
Load:
- `akka-views`
- `akka-view-from-service-stream`
- `akka-view-query-patterns`

### Add view streaming
Load:
- `akka-views`
- `akka-view-streaming`
- `akka-view-testing`

## Repository reference examples

These Java examples are **Akka substrate references**, not generated-product architecture templates. Use them to copy component structure, APIs, tests, and edge patterns after secure AI-first SaaS foundation and operating-model decisions are already made. Do not use shopping-cart or other low-level examples to choose the product architecture.

### Event sourced entities
Core entities:
- `../src/main/java/com/example/application/ShoppingCartEntity.java`
- `../src/main/java/com/example/application/OrderEntity.java`
- `../src/main/java/com/example/application/ExpiringShoppingCartEntity.java`

Domain examples:
- `../src/main/java/com/example/domain/ShoppingCart.java`
- `../src/main/java/com/example/domain/Order.java`
- `../src/main/java/com/example/domain/ExpiringShoppingCart.java`

Testing examples:
- `../src/test/java/com/example/application/ShoppingCartEntityTest.java`
- `../src/test/java/com/example/application/OrderEntityTest.java`
- `../src/test/java/com/example/application/ExpiringShoppingCartEntityTest.java`

### Key value entities
Core entities:
- `../src/main/java/com/example/application/DraftCartEntity.java`
- `../src/main/java/com/example/application/PurchaseOrderEntity.java`
- `../src/main/java/com/example/application/ExpiringDraftCartSessionEntity.java`

Domain examples:
- `../src/main/java/com/example/domain/DraftCart.java`
- `../src/main/java/com/example/domain/PurchaseOrder.java`
- `../src/main/java/com/example/domain/ExpiringDraftCartSession.java`

Testing examples:
- `../src/test/java/com/example/application/DraftCartEntityTest.java`
- `../src/test/java/com/example/application/PurchaseOrderEntityTest.java`
- `../src/test/java/com/example/application/ExpiringDraftCartSessionEntityTest.java`

### Agents
Routing references:
- `akka-agent-behavior-profiles` for durable tenant-scoped AgentDefinition and runtime behavior profile design before implementation of managed agents
- `akka-agent-governed-documents` for tenant-scoped governed prompts, skills, rubrics, policies, and examples with immutable versions, review, activation, diff/history, and audit
- `akka-agent-seed-documents` for first-install/tenant-bootstrap loading of implementation-developed default AgentDefinition, PromptDocument, SkillDocument, AgentSkillManifest, and ToolPermissionBoundary records with idempotency, provenance, upgrade behavior, and audit
- `akka-agent-prompt-governance` for governed runtime-managed system prompts, PromptDocument/PromptVersion, effective prompt assembly, PromptAssemblyTrace, and prompt test consoles
- `akka-agent-skill-governance` for governed runtime skills, SkillDocument/SkillVersion, AgentSkillManifest, compact manifest prompt context, readSkill(skillId), and SkillLoadTrace
- `akka-agent-behavior-editing` for AgentBehaviorEditorAgent proposal flows, proposed diffs, draft versions, review/approval routing, decision cards, and authority-expansion denial
- `akka-agent-work-trace` for AgentWorkTrace and agent-specific prompt/skill/model/tool/data/policy usage traces, authorization basis, redaction, correlation, and timelines
- `akka-agent-closed-loop-improvement` for EvaluationRun/Finding, ImprovementProposal, replay/simulation, approval, activation, monitoring, and rollback loops

Core agent examples:
- `../src/main/java/com/example/application/ActivityAgent.java`
- `../src/main/java/com/example/application/TemplateBackedActivityAgent.java`
- `../src/main/java/com/example/application/WeatherAgent.java`
- `../src/main/java/com/example/application/WeatherForecastTools.java`
- `../src/main/java/com/example/application/StreamingActivityAgent.java`
- `../src/main/java/com/example/application/AgentTeamWorkflow.java`
- `../src/main/java/com/example/application/DynamicAgentTeamWorkflow.java`
- `../src/main/java/com/example/application/SelectorAgent.java`
- `../src/main/java/com/example/application/PlannerAgent.java`
- `../src/main/java/com/example/application/SummarizerAgent.java`
- `../src/main/java/com/example/application/SessionMemoryAlertsConsumer.java`
- `../src/main/java/com/example/application/SessionMemoryByComponentView.java`
- `../src/main/java/com/example/application/SessionMemoryAlertView.java`
- `../src/main/java/com/example/application/SessionMemoryCompactionAgent.java`
- `../src/main/java/com/example/application/SessionMemoryCompactionConsumer.java`
- `../src/main/java/com/example/application/SessionMemoryCompactionAuditConsumer.java`
- `../src/main/java/com/example/application/PromptTemplateHistoryView.java`
- `../src/main/java/com/example/application/ActivityAnswerEvaluatorAgent.java`
- `../src/main/java/com/example/application/CompetitorMentionGuard.java`
- `../src/main/java/com/example/api/ActivityAgentEndpoint.java`
- `../src/main/java/com/example/api/ActivityPromptEndpoint.java`
- `../src/main/java/com/example/api/PromptTemplateHistoryEndpoint.java`
- `../src/main/java/com/example/api/SessionMemoryViewEndpoint.java`
- `../src/main/java/com/example/api/SessionMemoryAlertStreamEndpoint.java`
- `../src/main/java/com/example/api/DynamicAgentTeamWorkflowEndpoint.java`
- `../src/main/resources/application.conf`

Testing examples:
- `../src/test/java/com/example/application/ActivityAgentTest.java`
- `../src/test/java/com/example/application/AgentTeamWorkflowIntegrationTest.java`
- `../src/test/java/com/example/application/DynamicAgentTeamWorkflowIntegrationTest.java`
- `../src/test/java/com/example/application/ActivityAgentEndpointIntegrationTest.java`
- `../src/test/java/com/example/application/ActivityPromptEndpointIntegrationTest.java`
- `../src/test/java/com/example/application/DynamicAgentTeamWorkflowEndpointIntegrationTest.java`
- `../src/test/java/com/example/application/PromptTemplateHistoryViewIntegrationTest.java`
- `../src/test/java/com/example/application/PromptTemplateHistoryEndpointIntegrationTest.java`
- `../src/test/java/com/example/application/SessionMemoryViewEndpointIntegrationTest.java`
- `../src/test/java/com/example/application/SessionMemoryAlertStreamEndpointIntegrationTest.java`
- `../src/test/java/com/example/application/SessionMemoryAlertsConsumerIntegrationTest.java`
- `../src/test/java/com/example/application/SessionMemoryByComponentViewIntegrationTest.java`
- `../src/test/java/com/example/application/SessionMemoryCompactionConsumerIntegrationTest.java`
- `../src/test/java/com/example/application/SessionMemoryCompactionAuditConsumerIntegrationTest.java`

### Workflows
Core workflow examples:
- `../src/main/java/com/example/application/TransferWorkflow.java`
- `../src/main/java/com/example/application/ApprovalWorkflow.java`
- `../src/main/java/com/example/application/ReviewWorkflow.java`
- `../src/main/java/com/example/application/WalletEntity.java`
- `../src/main/java/com/example/api/TransferWorkflowEndpoint.java`
- `../src/main/java/com/example/api/ApprovalWorkflowEndpoint.java`
- `../src/main/java/com/example/domain/TransferState.java`
- `../src/main/java/com/example/domain/ApprovalState.java`
- `../src/main/java/com/example/domain/Wallet.java`

Testing examples:
- `../src/test/java/com/example/application/TransferWorkflowIntegrationTest.java`
- `../src/test/java/com/example/application/TransferWorkflowEndpointIntegrationTest.java`
- `../src/test/java/com/example/application/ApprovalWorkflowIntegrationTest.java`
- `../src/test/java/com/example/application/ApprovalWorkflowEndpointIntegrationTest.java`
- `../src/test/java/com/example/application/WalletEntityTest.java`
- `../src/test/java/com/example/application/ReviewWorkflowTopicConsumerIntegrationTest.java`
- `../src/test/java/com/example/application/ReviewRequestsByStatusViewIntegrationTest.java`

### Timed actions
Core timer examples:
- `../src/main/java/com/example/domain/TicketReservation.java`
- `../src/main/java/com/example/application/TicketReservationEntity.java`
- `../src/main/java/com/example/application/TicketReservationTimedAction.java`
- `../src/main/java/com/example/api/TicketReservationEndpoint.java`
- `../src/main/java/com/example/domain/ReminderJob.java`
- `../src/main/java/com/example/application/ReminderJobEntity.java`
- `../src/main/java/com/example/application/ReminderJobTimedAction.java`
- `../src/main/java/com/example/api/ReminderJobEndpoint.java`
- `../src/main/java/com/example/domain/ApprovalDeadlineState.java`
- `../src/main/java/com/example/application/ApprovalDeadlineWorkflow.java`
- `../src/main/java/com/example/application/ApprovalDeadlineTimedAction.java`
- `../src/main/java/com/example/api/ApprovalDeadlineWorkflowEndpoint.java`

Testing examples:
- `../src/test/java/com/example/application/TicketReservationEntityTest.java`
- `../src/test/java/com/example/application/TicketReservationTimedActionTest.java`
- `../src/test/java/com/example/application/TicketReservationEndpointIntegrationTest.java`
- `../src/test/java/com/example/application/ReminderJobEntityTest.java`
- `../src/test/java/com/example/application/ReminderJobTimedActionTest.java`
- `../src/test/java/com/example/application/ReminderJobEndpointIntegrationTest.java`
- `../src/test/java/com/example/application/ApprovalDeadlineWorkflowIntegrationTest.java`
- `../src/test/java/com/example/application/ApprovalDeadlineTimedActionTest.java`
- `../src/test/java/com/example/application/ApprovalDeadlineWorkflowEndpointIntegrationTest.java`

### Consumers
Core consumer examples:
- `../src/main/java/com/example/application/ShoppingCartCheckoutConsumer.java`
- `../src/main/java/com/example/application/DraftCartCheckoutConsumer.java`
- `../src/main/java/com/example/application/ShoppingCartCommandsTopicConsumer.java`
- `../src/main/java/com/example/application/ShoppingCartEventsToTopicConsumer.java`
- `../src/main/java/com/example/application/ShoppingCartPublicEventsConsumer.java`
- `../src/main/java/com/example/application/ReviewWorkflowTopicConsumer.java`
- `../docs/consumer-reference.md`
- `../docs/service-to-service-consumers.md`

Testing examples:
- `../src/test/java/com/example/application/ShoppingCartCheckoutConsumerIntegrationTest.java`
- `../src/test/java/com/example/application/DraftCartCheckoutConsumerIntegrationTest.java`
- `../src/test/java/com/example/application/ShoppingCartCommandsTopicConsumerIntegrationTest.java`
- `../src/test/java/com/example/application/ReviewWorkflowTopicConsumerIntegrationTest.java`

### Views
Core view examples:
- `../src/main/java/com/example/application/ShoppingCartsByCheckedOutView.java`
- `../src/main/java/com/example/application/ShoppingCartAuditView.java`
- `../src/main/java/com/example/application/DraftCartsByCheckedOutView.java`
- `../src/main/java/com/example/application/DraftCartLifecycleView.java`
- `../src/main/java/com/example/application/ReviewRequestsByStatusView.java`
- `../src/main/java/com/example/application/ShoppingCartTopicView.java`
- `../docs/service-to-service-views.md`

Testing examples:
- `../src/test/java/com/example/application/ShoppingCartsByCheckedOutViewIntegrationTest.java`
- `../src/test/java/com/example/application/ShoppingCartAuditViewIntegrationTest.java`
- `../src/test/java/com/example/application/DraftCartsByCheckedOutViewIntegrationTest.java`
- `../src/test/java/com/example/application/DraftCartLifecycleViewIntegrationTest.java`
- `../src/test/java/com/example/application/ReviewRequestsByStatusViewIntegrationTest.java`
- `../src/test/java/com/example/application/ShoppingCartTopicViewIntegrationTest.java`

### HTTP endpoints
Core endpoint examples:

For generated SaaS browser UI, these endpoint examples are delivery mechanics only. Use `../docs/workstream-ui-reference-architecture.md` and the canonical frontend reference (`../frontend/src/workstream/**` in this source repository, or `../resources/examples/frontend/src/workstream/**` in an installed pack); do not treat `WebUi*PageEndpoint` examples as page-first UI architecture.

- `../src/main/java/com/example/api/GreetingEndpoint.java`
- `../src/main/java/com/example/api/WebUiHomeEndpoint.java`
- `../src/main/java/com/example/api/WebUiDataEndpoint.java`
- `../src/main/java/com/example/api/WebUiSsePageEndpoint.java`
- `../src/main/java/com/example/api/WebUiWebSocketPageEndpoint.java`
- `../src/main/java/com/example/api/LowLevelHttpEndpoint.java`
- `../src/main/java/com/example/api/ProxyGreetingEndpoint.java`
- `../src/main/java/com/example/api/PingWebSocketEndpoint.java`
- `../src/main/java/com/example/api/CounterStreamEndpoint.java`
- `../src/main/java/com/example/api/DraftCartViewStreamEndpoint.java`
- `../src/main/java/com/example/api/RequestHeadersEndpoint.java`
- `../src/main/java/com/example/api/SecureGreetingEndpoint.java`
- `../src/main/java/com/example/api/InternalStatusEndpoint.java`
- `../src/main/java/com/example/api/ShoppingCartEndpoint.java`
- `../src/main/java/com/example/api/DraftCartEndpoint.java`
- `../src/main/java/com/example/api/OrderEndpoint.java`
- `../src/main/java/com/example/api/PurchaseOrderEndpoint.java`
- `../src/main/java/com/example/api/TransferWorkflowEndpoint.java`
- `../src/main/java/com/example/api/ApprovalWorkflowEndpoint.java`

Testing examples:
- `../src/test/java/com/example/application/GreetingEndpointIntegrationTest.java`
- `../src/test/java/com/example/application/WebUiHomeEndpointIntegrationTest.java`
- `../src/test/java/com/example/application/WebUiDataEndpointIntegrationTest.java`
- `../src/test/java/com/example/application/WebUiSsePageEndpointIntegrationTest.java`
- `../src/test/java/com/example/application/WebUiWebSocketPageEndpointIntegrationTest.java`
- `../src/test/java/com/example/application/LowLevelHttpEndpointIntegrationTest.java`
- `../src/test/java/com/example/application/ProxyGreetingEndpointIntegrationTest.java`
- `../src/test/java/com/example/application/PingWebSocketEndpointIntegrationTest.java`
- `../src/test/java/com/example/application/CounterStreamEndpointIntegrationTest.java`
- `../src/test/java/com/example/application/DraftCartViewStreamEndpointIntegrationTest.java`
- `../src/test/java/com/example/application/RequestHeadersEndpointIntegrationTest.java`
- `../src/test/java/com/example/application/SecureGreetingEndpointIntegrationTest.java`
- `../src/test/java/com/example/application/InternalStatusEndpointIntegrationTest.java`
- `../src/test/java/com/example/application/ShoppingCartIntegrationTest.java`
- `../src/test/java/com/example/application/OrderEndpointIntegrationTest.java`
- `../src/test/java/com/example/application/PurchaseOrderEndpointIntegrationTest.java`
- `../src/test/java/com/example/application/TransferWorkflowEndpointIntegrationTest.java`
- `../src/test/java/com/example/application/ApprovalWorkflowEndpointIntegrationTest.java`

### gRPC endpoints
Core endpoint examples:
- `../src/main/proto/com/example/api/grpc/shopping_cart_grpc_endpoint.proto`
- `../src/main/proto/com/example/api/grpc/internal_status_grpc_endpoint.proto`
- `../src/main/proto/com/example/api/grpc/secure_greeting_grpc_endpoint.proto`
- `../src/main/proto/com/example/api/grpc/pattern_secure_greeting_grpc_endpoint.proto`
- `../src/main/java/com/example/api/ShoppingCartGrpcEndpointImpl.java`
- `../src/main/java/com/example/api/InternalStatusGrpcEndpointImpl.java`
- `../src/main/java/com/example/api/SecureGreetingGrpcEndpointImpl.java`
- `../src/main/java/com/example/api/PatternSecureGreetingGrpcEndpointImpl.java`

Testing examples:
- `../src/test/java/com/example/application/ShoppingCartGrpcEndpointIntegrationTest.java`
- `../src/test/java/com/example/application/InternalStatusGrpcEndpointIntegrationTest.java`
- `../src/test/java/com/example/application/SecureGreetingGrpcEndpointIntegrationTest.java`
- `../src/test/java/com/example/application/PatternSecureGreetingGrpcEndpointIntegrationTest.java`

### MCP endpoints
Core endpoint examples:
- `../src/main/java/com/example/api/ShoppingCartMcpEndpoint.java`
- `../src/main/java/com/example/api/SecureSupportMcpEndpoint.java`
- `../src/main/resources/mcp/checkout-guidelines.md`

Testing examples:
- `../src/test/java/com/example/application/ShoppingCartMcpEndpointTest.java`
- `../src/test/java/com/example/application/SecureSupportMcpEndpointTest.java`
