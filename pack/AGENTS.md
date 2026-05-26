# AGENTS

Use this file as the **installed-pack guidance** for AI coding agents working with this Akka AI skills pack after it has been installed under `.agents/`.

This file is intended for pack users.
It is distinct from the repository-internal maintainer guidance used to develop the source repository.

## What this installed pack is for

This pack creates **full-stack secure AI-first SaaS on Akka**. Treat that as the generated-application target from the start of every project unless the user explicitly asks for repository-maintenance-only or non-SaaS reference material.

Security, the web UI, and AI-first managed agents are mandatory. Every generated SaaS application must begin with a secure foundation plus the agent workstream shell and configuration-driven managed-agent runtime: identity, local authorization, Tenant/Customer boundaries, memberships, roles/scopes, `/api/me`, email-invite onboarding through Resend (resend.com), backend authorization checks, audit events, tenant-isolation tests, security review, role-authorized functional agents, continuous workstreams, persistent composer, context/authority indicators, structured surfaces for sign-in, context selection, administration, supervision, decisions, governance, audit, and outcomes, active `AgentDefinition` resolution, governed prompts/skills/references, compact manifests, `ToolPermissionBoundary`, governed `readSkill`/`readReferenceDoc` loader tools, runtime tool registration via `effects().tools(runtimeTools)`, and durable prompt/skill/reference/work traces. Do not defer these to later hardening/polish or wait for the user to ask for them. Resend is the supported production email service for invitation/account emails and future app email features; local/dev/test uses captured outbox behavior; agent email sending must be exposed only as governed `@FunctionTool` capability surfaces.

AI-first SaaS means agents do bounded work while humans supervise, decide, approve, audit, teach, and own outcomes. First decide which durable goals/plans, bounded agents or agent teams, policy and approval controls, supervision surfaces, audit traces, and outcome loops the product needs; then model backend behavior as governed capabilities before applying only the justified Akka substrate components and exposure surfaces on top of the mandatory secure SaaS foundation.

This pack supports a secure AI-first interpretation layer plus two complementary ways of working in a real development project:

0. **secure AI-first SaaS interpretation**
   - inspect high-level product intent before decomposing into CRUD, page trees, or isolated components
   - model the mandatory secure SaaS foundation and agent workstream shell before app-specific features
   - model delegated work, retained human authority, policies, decisions, traces, supervision, and outcomes when applicable
   - route through `skills/ai-first-saas/SKILL.md` and focused AI-first companion skills only as needed
1. **capability-first backend modeling**
   - define named backend operations/queries with actors/AuthContext, tenant/customer scope, schemas, side effects, idempotency, audit/work traces, approval policy, selected exposure surfaces, and tests
   - treat agent tools, MCP tools/resources, APIs, browser actions, workflow steps, timers, consumers, views, and component methods as selected surfaces of governed capabilities, not authorization controls
   - route through `skills/capability-first-backend/SKILL.md` when backend capability contracts are not yet clear
2. **description-first application maintenance**
   - maintain an authoritative internal app-description as the source of truth
   - review readiness, change impact, security, observability, and test intent before realization
   - generate app outputs only when requested or accepted
3. **intent-driven Akka decomposition and implementation**
   - derive the Akka solution shape from high-level intent
   - load focused implementation skills
   - generate component code and corresponding tests

Users should be able to speak in natural language.
They do not need to know the pack's internal skill taxonomy.

Terminology guardrail:
- Use `domain-specific` or the user's actual domain name for app-specific follow-up work.
- Do not use `DCA`, `DCA-specific`, or other historical/example domain names as generic placeholders unless the user explicitly says the target product is that domain.

## Java base package intake

Before creating a new Java Akka project, scaffolding Java source files, or realizing an app description into Java code, ask the user for the application's Java base package unless it is already present in project configuration or the user has already provided it.

Use this initial question:

> What Java base package should I use for generated code? Press Enter to use `ai.first`.

Rules:
- default to `ai.first` only when the user accepts the default or defers the choice
- never use `com.example` as the generated application package unless the user explicitly requests it
- treat `com.example` paths in bundled examples as reference examples only, not as a generation default
- record the selected base package in planning/app-description generation artifacts and use it consistently for Maven/Gradle group id, Java package declarations, imports, tests, and generated source paths

## Core usage model

Treat the installed skills as an **internal routing layer for the harness**.

### AI-first interpretation layer

For broad product input, interpret the product as secure AI-first SaaS unless the request is explicitly outside generated SaaS application work. Use this layer when the input involves agents, delegated operational work, recommendations, approvals, exceptions, policy/permission controls, supervision, auditability, or accountable outcomes.

Start with `skills/ai-first-saas/SKILL.md`, model the mandatory secure SaaS foundation, then load only the focused companions that match the concern: object model, agent-team design, governance, decision cards, audit trace, UI surfaces, or outcomes/metrics. Before component implementation, use `skills/capability-first-backend/SKILL.md` to model governed backend capability contracts when operations, queries, authority, side effects, or exposure surfaces are not yet clear.

### Mode A: description-first application maintenance

Use this mode when the user is primarily describing or revising the app, asking what changed, asking whether it is ready, or asking to generate only after the description is sufficiently mature. For AI-first products, preserve the operating-model layer before generating outputs.

Recommended flow:
1. read the user's input completely
2. apply secure AI-first SaaS interpretation first, including mandatory identity, tenancy, authorization, audit, tenant-isolation, and web UI foundations
3. model governed backend capabilities before behavior, UI, security, observability, tests, or generation layers depend on them
4. if the user is working description-first, start with `skills/app-descriptions/SKILL.md`
5. bootstrap with `skills/app-description-bootstrap/SKILL.md` when no usable app-description tree exists yet
6. normalize broad or mixed input with `skills/app-description-input-normalization/SKILL.md`
7. route and maintain the smallest relevant app-description layer
8. use readiness and review-summary skills before or around generation as needed
9. realize outputs with `skills/app-generate-app/SKILL.md` only when generation is requested or accepted

Important installed-pack rule:
- the maintained app-description tree belongs to the **target project workspace**, not to `.agents/` itself, unless the user explicitly wants some other project-equivalent internal location
- the pack provides the skills and reference docs; it is not the application's source-of-truth storage location

### Mode B: intent-driven Akka decomposition and implementation

Use this mode when the user wants to derive the Akka solution shape and then implement components.

Recommended flow:
1. read the user's input completely
2. apply secure AI-first SaaS interpretation first, including mandatory identity, tenancy, authorization, audit, tenant-isolation, and web UI foundations
3. model governed backend capabilities with `skills/capability-first-backend/SKILL.md` when operation/query contracts are not clear
4. if the Akka solution shape is still unclear, start with `skills/akka-solution-decomposition/SKILL.md`
5. if one structural choice is still unresolved, use the focused decision skill such as `skills/akka-entity-type-selection/SKILL.md`
6. if unresolved decisions would make tasks speculative, create `specs/pending-questions.md` with `skills/akka-pending-question-generation/SKILL.md` and work through it one question at a time with `skills/akka-do-next-pending-question/SKILL.md`
7. once the architecture is clear, use `skills/README.md` to load only the smallest relevant implementation skill set
8. generate code and tests component by component

Do not jump straight into a component family when the broader architecture is still unclear.

## Installed pack layout

After installation, the main entry points are:
- `AGENTS.md` — this installed-pack guidance file
- `skills/README.md` — routing map across the installed skill library, including description-first and implementation paths
- `skills/<skill-name>/SKILL.md` — focused implementation or routing guidance
- `docs/` — selected pack-facing reference docs used by installed skills, including AI-first doctrine, `docs/minimum-ai-first-saas-app.md`, capability-first backend doctrine, core secure SaaS foundation docs, the AI-first SaaS seed app-description reference when packaged, and description-first mechanics examples
- `resources/examples/java/` — exported Akka Java SDK substrate examples and tests for concrete component implementation patterns; these examples are not product-architecture templates
- `resources/examples/frontend/` — exported React/Vite/TypeScript agent workstream UI reference, including `src/workstream/**` and contract tests for the generated SaaS workstream shell and User Admin vertical

Use the docs under `docs/` as routing/reference support.
Use the Java examples under `resources/examples/java/` as canonical local implementation references when they match the task.
Use the frontend workstream reference under `resources/examples/frontend/` when implementing generated SaaS browser UI; do not use static-resource output as the source structure.

## Official Akka docs

Official Akka SDK documentation is **not bundled** with the installed pack.

Some installed skills may refer to official Akka docs generically.
Use those references when you need:
- API confirmation
- edge-case semantics
- feature behavior not fully covered by the installed examples

## Implementation completion standard

A generated-app feature is complete only when the real local runtime path works at the stated scope. Treat Akka local execution as production-like validation: use it, plus endpoint/UI smoke checks where relevant, to prove that named behavior actually runs through the generated backend, authorization, durability, provider, trace, and frontend paths.

Do not mark user auth, sign-in, invitation onboarding, User Admin, Agent Admin, workstream agents, protected capabilities, provider-backed model calls, audit/traces, or app-specific workstreams done when the normal runtime path is deterministic/demo/mock/simulated/model-less. For model-backed workstream agents, normal message submission must invoke a concrete Akka `Agent` component through the configuration-driven governed runtime path: active `AgentDefinition` and prompt/manifest resolution, governed `readSkill`/`readReferenceDoc` tools, `ToolPermissionBoundary` enforcement, `.tools(runtimeTools)` registration on the Agent effects, provider invocation, and durable traces. A service-only provider call that bypasses the Agent component is an incomplete runtime even when it returns markdown. Missing provider/configuration must fail closed with an actionable error, not silently fall back to a canned response. Test fixtures, mocks, deterministic fakes, and test doubles are valid only as tests or explicitly named test adapters; they are not user-facing runtime substitutes and do not by themselves satisfy feature readiness.

## Working style

Prefer this pattern:
- decompose before coding when requirements are still high-level
- load only the smallest relevant skill set
- use examples and tests as implementation references
- state assumptions clearly when requirements are ambiguous
- ask clarifying questions when ambiguity materially affects architecture or component choice
- prefer `specs/pending-questions.md` over a large ad hoc question list when clarification will take multiple turns or sessions

## Pending question reminders

When the target project contains `specs/pending-questions.md`, treat it as the durable clarification queue.

At the end of planning responses, if actionable questions remain, include a short reminder unless the response is only a trivial clarification or the user explicitly asks not to receive reminders.

Reminder behavior:
1. Inspect `specs/pending-questions.md` when it is already in context or cheap to read.
2. Prefer `answered` questions that need reconciliation, then the first askable `blocking`, `important`, or `optional` question whose dependencies are resolved.
3. Do not ask multiple questions unless the user requests a batch.
4. Recommend `skills/akka-do-next-pending-question/SKILL.md` for the next question.

Use this reminder shape:

```md
Pending questions remain.

Next question:
- <Q-ID>: <title>

To continue, ask:
"Use akka-do-next-pending-question to ask the next pending question from specs/pending-questions.md."
```

If blocking questions remain, do not create or execute affected implementation tasks unless the question is explicitly deferred with an accepted default or limitation.

## Pending task reminders

When the user asks "what's next" after an app is running, recommend the next milestone from the target project's actual pending tasks, readiness gaps, or app description. For generated SaaS apps, prefer full core SaaS readiness before app/domain-specific features, but describe that follow-up as `domain-specific` or by the user's actual domain name, never as `DCA-specific` unless DCA is explicitly the user's domain.

When the target project contains `specs/pending-tasks.md`, treat it as the durable follow-on implementation queue.

At the end of each response, if pending runnable tasks remain, include a short reminder unless the response is only a trivial clarification or the user explicitly asks not to receive reminders.

Reminder behavior:
1. Inspect `specs/pending-tasks.md` when it is already in context or cheap to read.
2. Identify the first task with `status: pending` whose `depends on` entries are empty or all `done`; ignore `done`, `blocked`, `deferred`, and `superseded` tasks.
3. Do not automatically start the task unless the user asked to continue implementation.
4. Recommend a fresh context for the next task.
5. Keep the reminder short: name the next task and provide a copyable continuation prompt.

Use this reminder shape:

```md
Pending tasks remain.

Next runnable task:
- <TASK-ID>: <title>

To continue reliably, start a fresh context and ask:
"Use akka-do-next-pending-task to execute the next pending task from specs/pending-tasks.md."
```

If `specs/pending-tasks.md` exists but no pending task is runnable, mention the blocked/dependency state briefly instead of suggesting implementation.

When executing a pending task, use `skills/akka-do-next-pending-task/SKILL.md` and execute exactly one queue item.

For ongoing evolution after a queue exists:
- use `skills/akka-change-request-to-spec-update/SKILL.md` for bounded feature requests, bug reports, issues, and implementation discoveries
- use `skills/akka-revised-prd-reconciliation/SKILL.md` for revised or replacement PRDs
- use `skills/akka-pending-question-queue-maintenance/SKILL.md` for question queue audit, stale-question detection, duplicate cleanup, blocked-question review, unreconciled answers, and supersession
- use `skills/akka-pending-task-queue-maintenance/SKILL.md` for task queue audit, stale-task detection, duplicate cleanup, blocked-task review, and supersession

## Short routing rule

When unsure:
- start with `skills/ai-first-saas/SKILL.md` if the input involves delegated work, agents, decisions, governance, supervision, audit, or outcomes
- use `skills/capability-first-backend/SKILL.md` when backend operations/queries, authority, side effects, exposure surfaces, or tests are not yet modeled as capability contracts
- start with `skills/app-descriptions/SKILL.md` if the user is describing, revising, reviewing, or readiness-checking the app itself
- start with `skills/akka-solution-decomposition/SKILL.md` if the user wants direct Akka architecture derivation from high-level intent
- use `skills/README.md` to route to the smallest next skill set
- generate tests alongside component code, not afterward
