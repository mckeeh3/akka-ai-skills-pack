---
name: akka-prd-to-specs-backlog
description: Turn a PRD or other high-level requirements artifact into a repo-ready planning package; master Akka solution plan, cross-cutting specs, module/sprint specs for large inputs or slice specs for smaller inputs, numbered build backlogs, and execution-order docs under specs/.
---

# Akka PRD to Specs Backlog

Context-budget rule: load the canonical full-core inventory from `../docs/full-core-foundation-readiness.md` by reference, then read only the sections below that match the requested planning shape. Do not paste the full foundation inventory into every generated spec, backlog, or queue item; summarize selected obligations and link the canonical checklist.

Use this skill when the user does not just want an Akka component plan, but wants the plan materialized into the repository as a harness-friendly `specs/` tree.

This is a **project-specific planning skill** that builds on the ideas in `akka-solution-decomposition` and continues all the way to implementation-ready planning artifacts.

## Goal

Generate a consistent planning package from a PRD, requirements document, or high-level feature set that:
- interprets product intent through the full-stack secure AI-first SaaS operating model before record-management or component decomposition
- derives governed backend capabilities before Akka component or capability exposure-channel selection
- processes broad input through the requirements-to-workstream chain: one-workstream vs multi-workstream decision → workstreams → attention breakdowns → role-specific dashboards → human surface graph nodes/actions → internal workstream agent graph → governed-tools inside capability files/surface maps → capabilities/APIs → Akka substrate → request-based workstream Agents and AutonomousAgent task candidates → events/notifications → attention projections → audit/work traces
- produces a master Akka solution plan with explicit Java base package, operating-model, workstream decomposition decision, role-specific dashboard model, human surface graph, internal workstream agent graph, workstream expertise plan, governed-tool inventory, capability inventory, governance, mandatory UI-surface, autonomous task, notification/projection, outcome, and substrate mapping sections for generated SaaS apps
- for large inputs, splits the plan into workstream-oriented or cross-workstream-foundation vertical sprint specs
- for smaller inputs, splits the plan into bounded vertical slice specs
- turns each sprint or slice into a build backlog suitable for one or more independent harness operations
- creates or updates `specs/pending-questions.md` when unresolved decisions should be answered before safe task generation, including AI-first authority, policy, evidence, risk, approval, trace, UI mode, and outcome blockers
- creates or updates `specs/pending-tasks.md` as the durable execution queue when follow-on implementation work is sufficiently unblocked, preserving capability ids, authority, schemas, side effects, audit, approval, exposure decisions, and workstream expertise requirements
- creates explicit tasks for every new or materially changed functional-agent workstream expert bundle: prompt intent, approved model binding (`ModelConfigRef`/`ModelPolicy` or explicit inherited governed default), skill documents, reference documents, compact manifests, tool boundaries, authorized loaders, default-content governance, UI/governance surfaces, traces, and tests
- optionally materializes leaf task briefs when a backlog item is still too large for a single focused harness run
- writes index files that make execution order and dependencies explicit
- keeps files small enough to support focused downstream coding sessions

## Use this skill when

The task sounds like one of these:
- "I have a PRD. How do I build this in Akka?"
- "Turn this requirements doc into an implementation plan"
- "Break this PRD into harness-friendly tasks"
- "Create specs and backlogs from this product doc"
- "Write a master plan plus module/sprint specs and backlog files"
- "Write a master plan plus slice specs and backlog files"

Do **not** use this skill when the user already has a settled slice/backlog and wants code directly. In that case, use the focused Stage 3 implementation skills.

## Existing-app planning

If the target project already contains implementation artifacts or a legacy `specs/scaffold-report.md`, use this skill to extend the existing baseline, not to plan a separate fresh application:

- read the existing app-description/specs and legacy report when present before creating or rewriting planning artifacts
- preserve the fixed Java base package `ai.first`, Maven group id, foundation scope, workstream UI baseline, and existing app-description/spec structure
- reconcile new PRD/domain input into the existing `app-description/` and `specs/` before creating tasks
- create vertical capability backlogs and `specs/pending-tasks.md` entries that build on the existing foundation
- queue questions for conflicts with core app foundation semantics instead of overwriting them silently

Only plan a full replacement of core app files when the user explicitly asks for a destructive reset or fresh regeneration.

## Relationship to other skills

This skill sits above normal decomposition.

Use it as:
1. high-level repo planning entry point
2. repository materialization step
3. handoff generator for downstream coding work

It should reuse the reasoning shape of:
- `../akka-solution-decomposition/SKILL.md`

Then continue into repo file generation.

Lifecycle ownership:
- `akka-prd-to-specs-backlog` owns the initial PRD/requirements → solution plan → cross-cutting specs → module/sprint or slice specs → backlog → pending-question/pending-task package.
- `akka-revised-prd-reconciliation` owns replacement or substantially revised PRDs after planning artifacts already exist; it produces a controlled delta instead of regenerating the queue from scratch.
- `akka-change-request-to-spec-update` owns local feature/bug/discovery deltas against existing app-description/spec/backlog/queue artifacts.
- `akka-slice-spec-to-backlog` owns one existing slice or sprint → one matching build backlog plus queue entries.
- `akka-backlog-to-pending-tasks` owns queue materialization or repair from existing backlogs; it does not redo PRD decomposition.
- `akka-backlog-item-to-task-brief` owns one oversized backlog item → one focused task brief and matching queue entry.
- `akka-do-next-pending-question` resolves one queued planning question; `akka-do-next-pending-task` executes one already-runnable queue task.

Do not skip lifecycle stages by turning a PRD directly into code, turning a local change into a full replan, or regenerating pending tasks without preserving existing IDs, statuses, source capability ids, AuthContext/scope, approval, audit/trace, tests, and existing-app/base-package decisions.

## Required reading

Read these first if present:
- `../README.md`
- `../core-saas-foundation/SKILL.md` for the mandatory secure SaaS baseline every new app PRD/spec/backlog must include
- `../akka-saas-invitation-onboarding/SKILL.md` when planning complete email-invite onboarding tasks
- `../ai-first-saas-admin-agents/SKILL.md` when planning the mandatory AI-assisted admin offload foundation: AccessReviewAgent, AdminRiskAgent, InvitationDraftAgent, RoleRecommendationAgent, SupportAccessReviewAgent, AdminAuditSummaryAgent, admin decision cards, and approval boundaries
- `../ai-first-saas/SKILL.md` for high-level product, PRD, feature, governance, agentic, decision, supervision, audit, or outcome inputs
- `../agent-workstream-apps/SKILL.md` for generated full-stack SaaS workstream interpretation before module, page, or component decomposition
- `../capability-first-backend/SKILL.md` for capability inventory, authority, schemas, side effects, audit, approval, exposure channels, and tests before component planning
- `../akka-solution-decomposition/SKILL.md`
- `../docs/ai-first-saas-application-architecture.md` for the canonical AI-first doctrine
- `../docs/agent-workstream-application-architecture.md` for functional agents, workstreams, structured surfaces, and vertical delivery
- `../docs/workstream-expertise-model.md` when planning any functional agent with LLM behavior or material expertise changes
- `../docs/structured-surface-contracts.md` for surface payload/action contracts
- `../docs/agent-workstream-design-review-checklist.md` when reviewing generated planning artifacts for page-first or component-first drift
- `../../../specs/README.md`
- `../../../specs/backlog/README.md`
- `../../../specs/tasks/README.md`
- target project implementation artifacts or legacy `../../../specs/scaffold-report.md` if present, to detect existing-app extension mode and preserve fixed package/path decisions
- `../../../specs/pending-questions.md` if it already exists
- `../../../specs/pending-tasks.md` if it already exists
- `../docs/pending-question-queue.md`
- `../docs/pending-task-queue.md`
- `../docs/module-sprint-planning.md` when the input is large, multi-module, or includes backend plus frontend delivery
- `../docs/web-ui-style-guide.md` for mandatory generated SaaS browser UI style selection
- `../../../specs/akka-solution-plan.md` if it already exists
- `../references/akka-entity-comparison.md`

If the user provided a path to a PRD or requirements file:
1. read that file completely
2. first extract AI-first operating-model signals: delegated work, retained human authority, goals/plans, agents, policies, decisions, approvals, exceptions, evidence, risk, traces, outcome loops, and supervision/governance UI needs
3. then decide whether the input is one workstream, multiple workstreams, or an incremental change to existing workstream graph nodes/edges/governed-tools; record split/merge rationale and shared foundation/cross-workstream concerns
4. then extract the agent workstream model for generated full-stack SaaS: functional agents, internal agents, durable workstreams, structured surfaces, surface actions or workstream events, and candidate action-to-capability links
5. then extract per-workstream attention breakdowns and role-specific dashboard contracts: `what needs my attention?`, target audiences, severity/lifecycle, summary cards, detail surfaces, My Account aggregate implications, left rail count behavior, and authoritative projection/query needs
6. then extract the human surface graph: dashboard trunk, surface nodes, system-message nodes, surface-request/action edges, edge effects, result surfaces, trace links, realtime/refresh behavior, and rendering states
7. for each functional agent with LLM behavior, extract or plan its workstream expert bundle: prompt intent, approved model binding (`ModelConfigRef`/`ModelPolicy` or explicit inherited governed default), allowed modes, fallback/no-fallback policy, provider secret boundary, model-use trace facts, `SkillDocument` entries, `ReferenceDocument` entries, compact `AgentSkillManifest`/`AgentReferenceManifest`, `ToolPermissionBoundary`, authorized `readSkill`/`readReferenceDoc` loaders, traces, governance owner, default-content governance expectations, UI surfaces, user-help examples, denials, and tests
8. then extract governed backend capabilities and governed-tools before component choices: capability ids/classes, governed-tool ids/classes, actors/callers, AuthContext and tenant/customer scope, input/output schemas, data access, side effects, idempotency, policy/approval rules, audit/trace needs, qualified exposure channels (`browser-tool`, `agent-tool`, `internal-tool`, workflow/timer/consumer/MCP exposure), and tests
9. then identify the internal workstream agent graph and internal/background model-driven worker candidates; map durable work to Akka `AutonomousAgent` tasks when lifecycle, snapshots/results, notifications, dependencies, failure/cancellation, delegation, handoff, teams, or moderation fit; keep request-based Akka `Agent` as the default for immediate user-facing workstream turns
10. then map events, notifications, projections, and work traces that connect capabilities, governed-tools, autonomous tasks, dashboards, My Account, and left rail attention indicators
11. then generate the file set

If `specs/` already exists:
- preserve numbering consistency where possible
- update indexes rather than duplicating them
- keep names aligned with the existing module/sprint/backlog or slice/backlog naming pattern

## Full-core planning gate

Before creating specs, backlogs, or pending tasks, classify the requested readiness level using `../docs/full-core-foundation-readiness.md`:
- `core app baseline`: plan the five-core-workstream shell from `../docs/minimum-ai-first-saas-app.md`, state that it is not full-core ready, and queue explicit full-core follow-up gates.
- `full core`: include the canonical full-core foundation scope and tests from `../docs/full-core-foundation-readiness.md` before app-specific slices.
- `Module 1-only / not full core` or any narrower scope: name the scope, list deferred full-core areas, and do not describe the result as full-core-ready.

Do not create a full-core `specs/pending-tasks.md` queue that collapses or omits core workstreams, Invitation onboarding, governed runtime agents, workstream UI, or required tests. If the PRD asks for full core but these areas cannot be planned yet, create/update `specs/pending-questions.md` or block the affected tasks instead of silently narrowing scope. If the plan claims app-specific readiness, it must first include full-core readiness plus product/domain functional agents, capabilities, structured surfaces, tests, and operational reviews.

## What this skill must produce

At minimum, create or update these files under `specs/`:

### Top-level
- `specs/akka-solution-plan.md`
- `specs/README.md`
- `specs/pending-questions.md` when blocking or important unresolved decisions exist
- `specs/pending-tasks.md` when follow-on implementation work is sufficiently unblocked

### Cross-cutting specs
For every SaaS app PRD, create the secure foundation spec first:
- `specs/cross-cutting/01-auth-tenancy-audit.md` — required for Account/Profile/Settings, Tenant/Customer, Membership/Role/Permission, WorkOS/JWT seam, `/api/me`, backend authorization, protected capability authorization, audit, support-access, billing boundary, and tenant-isolation tests unless the task is explicitly non-SaaS reference material.

Create additional cross-cutting specs as justified by the PRD:
- `specs/cross-cutting/00-common-domain-and-conventions.md` — record the fixed Java base package `ai.first`
- `specs/cross-cutting/02-ui-style-guide.md` for generated full-stack AI-first SaaS when style is selected
- `specs/cross-cutting/03-<integration-or-platform-concern>.md`

### Module and sprint specs for larger PRDs
When the input is large, multi-module, or includes meaningful backend plus frontend delivery, prefer:
- `specs/modules/01-<module-name>.md`
- `specs/modules/02-<module-name>.md`
- `specs/sprints/01-<sprint-name>-sprint.md`
- `specs/sprints/02-<sprint-name>-sprint.md`

Module specs define durable boundaries. Sprint specs define ordered vertical full-stack delivery increments. See `../docs/module-sprint-planning.md`.

### Vertical slice specs for smaller plans
For smaller plans, or when preserving an existing project shape, create numbered files such as:
- `specs/slices/01-<slice-name>.md`
- `specs/slices/02-<slice-name>.md`
- `specs/slices/03-<slice-name>.md`

For SaaS app PRDs, the first slice must be a full-stack secure foundation slice unless the task is explicitly non-SaaS reference material. Its scope comes from `../docs/full-core-foundation-readiness.md`; summarize the selected foundation workstream, authorization, onboarding/email, governed-agent, UI, audit, and test obligations instead of pasting the entire inventory into each slice.

Do not create both `specs/slices/` and `specs/sprints/` for new planning output unless project history requires it.

### Build backlogs
Create matching numbered files such as:
- `specs/backlog/README.md`
- `specs/backlog/01-<sprint-or-slice-name>-build-backlog.md`
- `specs/backlog/02-<sprint-or-slice-name>-build-backlog.md`
- `specs/backlog/03-<sprint-or-slice-name>-build-backlog.md`

### Optional leaf task briefs
Create these only when a backlog item would still be too large or too ambiguous for one focused harness run:
- `specs/tasks/README.md`
- `specs/tasks/01-<sprint-or-slice-name>/01-<task-name>.md`
- `specs/tasks/01-<sprint-or-slice-name>/02-<task-name>.md`

## Output contract

This skill is complete only when a future harness run can:
- read `specs/pending-tasks.md`
- select the next runnable task
- read the relevant module and sprint spec, or the relevant slice spec for smaller plans
- read the matching backlog
- implement a bounded piece of work without rereading the entire PRD

If the backlog is still too broad for that, either:
- tighten the backlog's harness task breakdown, or
- create leaf task briefs under `specs/tasks/`

If the output is still too broad for that, the skill has not decomposed far enough.

## Standard repository shape

Prefer this structure, choosing either `modules/` + `sprints/` for large plans or `slices/` for smaller/existing slice-based plans:

```text
specs/
  README.md
  akka-solution-plan.md
  pending-questions.md  # when unresolved decisions should be queued
  pending-tasks.md      # when implementation work is sufficiently unblocked
  cross-cutting/
    00-common-domain-and-conventions.md
    01-auth-tenancy-audit.md
    ...
  modules/             # use with sprints for large multi-module PRDs
    01-....md
    02-....md
  sprints/             # use with modules for vertical full-stack module sprints
    01-....-sprint.md
    02-....-sprint.md
  slices/              # alternative to sprints for smaller plans or existing slice-based projects
    01-....md
    02-....md
    ...
  backlog/
    README.md
    01-....-build-backlog.md
    02-....-build-backlog.md
    ...
  tasks/               # optional leaf layer for extra-large sprints or slices
    README.md
    01-<sprint-or-slice-name>/
      01-<task-name>.md
      02-<task-name>.md
```

## Decomposition workflow

### 1. Produce the master solution plan

Start with the same architecture reasoning as `akka-solution-decomposition` and `core-saas-foundation`.

The master plan must include:
1. Inputs
2. Scope label: `core app baseline`, `full core`, `Module 1-only / not full core`, or another explicit narrower scope, with deferred full-core areas listed when not full core
3. AI-first interpretation: objective, delegated work, retained human authority, durable substrate objects, governance/approval needs, supervision UI, audit/trace needs, and outcome loop when applicable
4. Core secure SaaS foundation: SaaS Owner, Tenant, Customer, Account, UserProfile, UserSettings, Membership, Role, Permission/Capability, Invitation, complete email-invite onboarding, AuthContext, AdminAuditEvent, support-access, subscription/billing boundary, `/api/me`, backend authorization, tenant/customer-scoped commands and queries, governed runtime agent foundation (`AgentDefinition`, `PromptDocument`/`PromptVersion`, `SkillDocument`/`SkillVersion`, `ReferenceDocument`/`ReferenceVersion`, `AgentSkillManifest`, `AgentReferenceManifest`, `ToolPermissionBoundary`, deterministic prompt assembly, authorized `readSkill(skillId)`, authorized `readReferenceDoc(referenceId)`, `PromptAssemblyTrace`, `SkillLoadTrace`, `ReferenceLoadTrace`, `AgentWorkTrace`, behavior editing agent proposals), and tenant-isolation/agent-governance tests
5. Workstream decomposition decision: one-workstream vs multi-workstream, affected workstreams, split/merge rationale, and shared foundation/cross-workstream concerns
6. Agent workstream model: functional agents, internal agents, durable workstreams, retained authority, default/attention surfaces, workstream expert bundle ids, and which work area owns each user-facing increment
7. Workstream attention and role-specific dashboard model: attention categories, target audiences, severity/lifecycle, dashboard summary/detail surfaces, My Account aggregate behavior, left rail counts/highest severity, authoritative projections/queries, and tests
8. Human surface graph and actions: dashboard trunk, surface type/version, typed payload, allowed edge actions, surface requests, workstream events, AuthContext assumptions, trace links, frontend/API/realtime needs, rendering states, and result-surface behavior
9. Surface action-to-capability and governed-tool map: every user-facing action, browser-tool, agent-tool, workflow event, timer, consumer, API, autonomous task lifecycle action, or internal call maps to a stable capability id/class plus governed-tool id/class, or is explicitly non-actionable/read-only
10. Internal workstream agent graph: virtual dashboard agent, worker agents, delegation edges, allowed governed-tools/tool boundaries, stop/escalation/handoff rules, result/proposal surfaces, and trace obligations
11. Autonomous task candidates and notification/projection map: candidate id, owning workstream, task start/result/read/notification capabilities, chosen `AutonomousAgent` vs request-based `Agent` rationale, result DTO/surface, notification-to-attention rules, and trace requirements
12. Workstream expertise plan: prompt intent, workstream skill/reference families, compact manifests, tool boundaries, authorized loaders, denied-load behavior, default-content governance expectations, UI/governance surfaces, user-help examples, and tests
13. Capability and governed-tool inventory: stable capability/governed-tool ids/names, classes, actors/callers, AuthContext and scope, input/output schemas, data access, side effects, idempotency, policy/approval rules, audit/trace obligations, selected qualified exposure channels or explicit non-exposure, and required tests
14. Capability-to-component mapping
15. Chosen components
16. Why each component exists, including how Akka components implement AI-first substrate objects when applicable
17. Skill routing
18. Open questions and assumptions
19. Recommended implementation order by workstream/attention/dashboard/surface/governed-tool/capability increments
20. Required tests, including capability success, governed-tool success/denial, validation, forbidden, tenant-isolation, idempotency, audit, approval, surface rendering/action, notification/projection, autonomous-task lifecycle when applicable, API/realtime, and exposure-specific tests

Write that to:
- `specs/akka-solution-plan.md`

### 2. Identify cross-cutting concerns

Separate concerns that should not be duplicated across modules, sprints, or slices, such as:
- ID and domain conventions
- AI-first operating-model vocabulary: goals, plans, tasks, agent/team definitions, policy clauses, decisions, approvals, exceptions, traces, and outcomes for generated SaaS apps
- tenancy, auth, permission, capability-grant, and authority-boundary rules
- policy, guardrail, approval-gate, and governance-versioning rules
- audit, work-trace, decision-trace, tool-invocation, data-access, retention, and redaction rules
- evaluation, replay, simulation, feedback, and outcome-metric conventions when agentic behavior or policy evolution is in scope
- external system integration model
- notification delivery model
- mandatory web UI style guide, named-theme contract, design tokens, My Account preference behavior when in scope, and brand adaptation for generated full-stack AI-first SaaS
- mandatory supervision, decision-card, governance-center, digest, audit, and outcome UI-surface conventions for generated AI-first SaaS apps
- export/reporting conventions

Create one file per cross-cutting concern when it affects multiple modules, sprints, or slices.

### 3. Split into module-oriented sprints or harness-friendly slices

For large PRDs, prefer workstream-oriented vertical sprint planning:
1. first decide whether the PRD is a single workstream, multiple workstreams, or a shared foundation/cross-workstream increment; record split/merge rationale rather than assuming module names from nouns or pages
2. identify cross-workstream foundation concerns and durable workstream/module boundaries, then write `specs/modules/NN-<module-or-workstream>.md` files
3. define ordered vertical delivery sprints around one workstream's attention/dashboard/surface-graph/governed-tool/capability increments, or an explicit cross-workstream foundation increment, and write `specs/sprints/NN-<sprint>-sprint.md` files
4. make each sprint testable through its backend and frontend workstream/dashboard/surface graph behavior for generated full-stack AI-first SaaS
5. keep cross-cutting foundation work explicit rather than duplicating it in every module

A good module spec contains boundaries, owned workstreams, attention categories/dashboard obligations, owned capabilities, actors/callers, AuthContext and scope rules, human operating roles, delegated work and retained authority when applicable, autonomous task ownership when needed, state ownership, agent/team ownership if any, policy/audit/outcome ownership, UI area, integrations, and related sprints.

A good sprint spec contains:
- one workstream, cross-workstream foundation, or tightly related module increment
- vertical workstream scope: functional agent(s), durable workstream item or event, attention category, dashboard summary/detail behavior, structured surface(s), surface action(s), and mapped capability id(s)/class(es)
- AI-first scope when applicable: goals/plans, agents, policies, decisions, approvals/exceptions, traces, governance surfaces, and outcome loop delivered by the sprint
- autonomous task scope when applicable: task definition/result DTO, start/read/result/notification capabilities, selected `AutonomousAgent` or request-based `Agent` rationale, and notification-to-attention behavior
- backend scope: capability contracts first, then entities, workflows, views, consumers, timers, endpoints, agents, AutonomousAgents, and selected capability exposure channels
- frontend/API/realtime scope: functional-agent workstream shell changes, dashboard cards, structured surfaces, forms/surface actions, route/deep-link details, API client calls, realtime/notification behavior, My Account/left rail attention effects, and supervision/decision/governance/audit/outcome surfaces when applicable
- AuthContext, tenant/customer scope, role/capability rules, approval/audit/trace obligations, and required tests for every user-facing action
- acceptance behavior and module-level tests
- pending questions and explicit defer list
- done criteria that define the working local app state for the sprint and include full-stack smoke/integration validation when applicable
- local app-run or manual test checklist expectations for the visible/API/workstream behavior named by the sprint; if local execution is not applicable, state why

For smaller plans, create vertical slices that are:
- independently meaningful to the business
- anchored to functional agent(s), attention category, dashboard/surface state, workstream events or surface actions, capability id(s)/class(es), AuthContext/rules, selected Akka substrate, autonomous task candidates when applicable, frontend/API/realtime work, notifications/projections, and tests
- explicit about AI-first operating-model scope when delegated work, decisions, governance, audit, or outcomes are in scope
- small enough for focused implementation
- ordered by dependency
- clear about what they intentionally exclude

Avoid increments that are either:
- too broad: "build the whole platform"
- too tiny: "add one enum"
- layer-only for large PRDs: "all entities" or "all UI"

### 4. Turn each sprint or slice into a build backlog

For each sprint or slice, create a matching backlog file that includes:
- purpose
- delivery goal
- vertical workstream contract for every implementation task: functional agent, attention category, dashboard card/detail surface, structured surface and action or workstream event, capability id/class, AuthContext and role/capability rules, selected Akka substrate, frontend/API/realtime work, and required tests
- AI-first operating-model scope when applicable: delegated work, retained authority, durable objects, agent/team responsibilities, policy/approval/exception rules, evidence/risk/confidence/impact requirements, audit traces, UI surfaces, and outcomes
- autonomous task scope when applicable: task definition/result DTO, task lifecycle capabilities, `AutonomousAgent` vs request-based `Agent` rationale, dependencies, notification stream, failure/cancellation behavior, result surface, and attention/projection effects
- capability contracts delivered or revised by the backlog: ids, actors/callers, AuthContext and scope, schemas, side effects, idempotency, approval, audit/trace, selected capability exposure channels, and tests
- notification/projection contracts: events/messages emitted or consumed, dashboard projections, My Account aggregate behavior, left rail summaries, realtime refresh, and audit/work trace linkage
- package layout additions if needed
- class-by-class file list
- endpoint list
- write-model decisions
- workflow/view/consumer/timer/agent design notes as relevant
- test plan by class/family, including guardrail/evaluation/governance/audit/outcome tests when applicable
- implementation order
- suggested harness task breakdown
- done criteria, including what must work through the locally running app or API/UI surface
- explicit defer list, including whether each deferral narrows the delivery goal or blocks calling the feature implemented

The suggested harness task breakdown is the default leaf layer.
Each task item should be phrased as a bounded independent vertical implementation prompt that carries the workstream/structured-surface/capability contract, not as a vague module/page/component slice. For module sprints, include a final module-level full-stack smoke/integration task when backend plus frontend or multiple backend exposure channels must work together. If the sprint goal names a user-visible feature, at least one backlog or queue item must prove that feature through the locally running Akka app, an endpoint smoke path, a browser/workstream smoke path, or an explicit manual-test checklist. Do not count deferred work as complete when that deferral prevents the named feature from functioning.

### 5. Materialize optional leaf task briefs when needed

Create physical task files under `specs/tasks/` only when at least one backlog task item is still too large, too ambiguous, or too cross-cutting for a single focused harness run.

A good task brief should contain:
- purpose
- required reads
- exact vertical scope: functional agent, attention category, dashboard card/detail surface, surface/action or workstream event, capability id/class, AuthContext and role/capability rules, selected Akka substrate, autonomous task candidate when applicable, notification/projection effects, frontend/API/realtime work, and required tests
- explicit non-goals
- Akka components involved
- exact skills to load
- expected outputs
- required tests
- done criteria

### 6. Create the pending question queue when needed

Create or update:
- `specs/pending-questions.md`

Use `../docs/pending-question-queue.md` as the queue contract.

Create this queue when unresolved decisions would otherwise make the solution plan, module specs, sprint specs, slice specs, backlogs, or task queue speculative. Questions should be one-at-a-time, dependency-aware, and tied to concrete design impact.

For AI-first inputs, create blocking or scoped questions when the plan would otherwise guess consequential semantics such as delegated authority, autonomous action limits, approval gates, exception ownership, policy/permission boundaries, required evidence, risk/confidence/impact thresholds, audit retention/redaction, supervision UI mode, governed policy changes, evaluation/replay needs, or outcome metrics.

Unknown security-provider details may block only provider-specific integration tasks, such as WorkOS issuer/audience/callback configuration. They must not block modeling the mandatory local authorization contracts, Tenant/Customer boundaries, AuthContext, role/capability checks, `/api/me`, audit events, or tenant-isolation tests.

The queue must:
- use stable question IDs such as `Q-001`, `Q-002`, `Q-003`
- preserve existing question IDs and statuses when updating an existing queue
- distinguish `answered` from `resolved`
- mark questions `blocking` only for the work they actually block
- include why each question matters and what artifacts or decisions it affects
- avoid dumping a large interrogation list into the chat response

Use the fixed Java base package `ai.first` for Java source generation/scaffolding. Do not ask a base-package pending question and package selection is out of scope.

If no selected style guide or named-theme contract exists in `specs/cross-cutting/*ui-style-guide*.md`, `app-description/55-ui/style-guide.md`, or equivalent UI spec for a generated AI-first SaaS app, create a `category: ui` style-selection question with the canonical AI-first style options from `../docs/web-ui-style-guide.md`: `ai-first-workstream-enterprise` with four initial named themes, or `custom` with a user-supplied style brief that preserves named-theme semantics. This blocks web UI implementation/generation tasks until style and named themes are selected.

If unresolved `blocking` questions affect planned implementation work, either:
- stop before creating blocked implementation tasks and recommend `akka-do-next-pending-question`, or
- create tasks only for unblocked work and document blocked areas.

### 7. Create the pending task queue

Create or update:
- `specs/pending-tasks.md`

Use `../docs/pending-task-queue.md` as the queue contract.

The queue must:
- start with runnable full-stack secure foundation tasks from `../docs/full-core-foundation-readiness.md` before app-specific domain-specific tasks; split onboarding/email, admin search, governed-agent records/loaders/traces, AI admin offload, UI surfaces, audit, and security/frontend/agent-governance tests into bounded tasks
- contain one task for each bounded, unblocked item in each backlog's `Suggested harness task breakdown`
- block or omit work still gated by unresolved `blocking` questions in `specs/pending-questions.md`
- use stable task IDs such as `TASK-001`, `TASK-002`, `TASK-003`
- preserve existing task IDs and statuses when updating an existing queue
- mark obsolete non-done tasks as `superseded` instead of deleting them when requirements have replaced them
- set new tasks to `status: pending` unless a blocking question requires `status: blocked`
- represent dependencies with `depends on: [...]`
- include the smallest `required reads` needed for the task; include the AI-first doctrine and focused AI-first companion skills only when the task implements or verifies goals/plans, agents, policies, decisions, approvals, traces, UI surfaces, governance, or outcomes
- include the exact implementation `skills` to load, pairing AI-first companion skills with the concrete Akka substrate skills rather than replacing them
- include expected outputs, required checks, local/runtime validation when applicable, and done criteria
- require Akka component-backed normal runtime state for claimed workstream/foundation features; fail-closed behavior may cover missing external provider/security configuration or unbound pre-runtime setup, but must not replace internal Akka persistence
- preserve vertical workstream context in each implementation task: functional agent(s), attention category, dashboard card/detail surface, structured surface/action or workstream event, capability id(s)/class(es), AuthContext, role/capability rules, selected Akka substrate, autonomous task candidate when applicable, notification/projection effects, frontend/API/realtime work, and required tests
- preserve capability context in each implementation task: capability id(s), authority/scope, schemas, side effects, idempotency, approval rules, audit/trace obligations, and exposure channels affected
- mark or keep tasks `blocked` when they name only a component, page, module, or generic UI feature without the workstream/surface/capability contract needed for implementation
- point to a task brief when one exists, or use `task brief: none`

The queue is the durable follow-on execution index. A user should be able to start a fresh harness session and ask to run `akka-do-next-pending-task` without rereading the whole PRD.

### 8. Create execution-order docs

Update or create:
- `specs/README.md`
- `specs/backlog/README.md`

These must explain:
- read order
- sprint/backlog or slice/backlog numbering alignment
- dependencies between modules, sprints, or slices
- recommended harness execution style
- how to resolve design blockers with `specs/pending-questions.md` and `akka-do-next-pending-question`
- how to continue implementation with `specs/pending-tasks.md` and `akka-do-next-pending-task`

## Sizing rules

### Module and sprint spec sizing
A module spec should usually be stable and concise: module boundary, ownership, actors, state, UI area, integrations, and related sprints.

A sprint spec should usually be:
- 500 to 1500 words
- one bounded full-stack delivery increment
- understandable without the full PRD open beside it
- testable at module level before moving to the next sprint

### Slice spec sizing
For smaller plans, a slice spec should usually be:
- 500 to 1500 words
- one bounded capability area
- understandable without the full PRD open beside it

### Backlog sizing
A backlog file should be detailed enough to support several small harness runs, but not so large that it becomes a second PRD.

### Harness-operation sizing
Within a backlog, prefer work items like:
- one vertical functional-agent surface/action plus the Akka substrate needed to serve it
- one governed capability and its endpoint/tool/workflow exposure plus direct tests
- one read/evidence surface backed by its view/query/API/client/rendering tests
- one workflow step or timed/consumer reaction tied to a named workstream event and trace obligation
- one security/admin/agent-governance regression pack for a named capability or surface family

Use component family names to describe implementation files, not to define the task boundary. If a work item still spans multiple unrelated capabilities or too many files, split it again into a task brief rather than handing it directly to code generation.

## Naming rules

Keep numbering aligned:
- `sprints/01-domain-specific-sprint.md` ↔ `backlog/01-domain-specific-build-backlog.md`
- `sprints/02-domain-specific-process-sprint.md` ↔ `backlog/02-domain-specific-process-build-backlog.md`
- or, for smaller slice-based plans, `slices/01-domain-specific.md` ↔ `backlog/01-domain-specific-build-backlog.md`

Use stable names:
- module names should describe durable app areas
- sprint or slice names should describe business delivery capability
- backlog names should match sprint or slice names exactly plus `-build-backlog`, dropping the `-sprint` suffix when present
- endpoint names should be feature-family oriented
- entity/workflow/view names should be explicit about their Akka role

## Recommended module sprint pattern

Prefer vertical module sprint order like this when the PRD supports it:
1. foundation: common domain plus the full core secure SaaS foundation from `core-saas-foundation` before app-specific domain-specific features
2. first business module core: write model, endpoints, minimal UI, full-stack smoke test
3. next module increment: workflow/read side/UI flow, full-stack smoke test
4. operational reactions: consumers, timers, notifications, realtime UI if needed
5. reporting/admin/export modules

Adjust when domain dependencies clearly suggest another order. Avoid layer-only sprints for large PRDs.

## Recommended slice pattern

For smaller slice-based plans, prefer an order like this when the PRD supports it:
1. foundational current-state visibility or core write model
2. operational reactions and notifications
3. orchestration-heavy business flow
4. service/human-ops flow
5. reporting/contracts/export layer

Adjust only if the domain clearly suggests another order.

## Required content for each module spec

Each `specs/modules/*.md` file should contain:
- Module boundary and purpose
- Owned capabilities, including ids, classes, actors/callers, AuthContext/scope, schemas, side effects, idempotency, policy/approval, audit/trace, exposure channels, and tests
- Actors, human operating roles, and authorization/authority boundary
- Delegated work, retained human authority, and outcome responsibility when AI-first concerns exist
- Domain objects, AI-first substrate objects, and state ownership
- Backend components likely owned by the module
- Functional-agent workstream surfaces, surface actions, and route/deep-link details owned by the module, including supervision/decision/governance/audit surfaces when applicable
- Integrations and events in/out
- Cross-cutting specs referenced
- Out of scope
- Related sprints/backlogs

## Required content for each sprint spec

Each `specs/sprints/*.md` file should contain:
- Sprint goal
- Parent module or modules
- Dependencies and prerequisite questions
- Vertical workstream increment: functional agents, attention categories, dashboard summary/detail behavior, structured surfaces, surface actions or workstream events, capability ids/classes, AuthContext/rules, selected Akka substrate, autonomous task candidates when applicable, notification/projection effects, frontend/API/realtime work, and tests
- AI-first operating-model increment when applicable: goal/plan, agent/team, policy, decision, approval/exception, trace, UI-surface, and outcome scope
- Backend scope
- Mandatory frontend scope for generated full-stack AI-first SaaS
- Acceptance behavior
- Pending questions affecting the sprint
- Implementation task groups
- Module-level full-stack test plan, including evaluation, policy, trace, and outcome validation when applicable
- Done criteria
- Explicit defer list

## Required content for each slice spec

Each `specs/slices/*.md` file should contain:
- Scope
- Business goal
- AI-first interpretation when applicable: delegated work, retained authority, durable substrate objects, policy/approval/exception rules, trace needs, UI surfaces, and outcome loop
- Attention/dashboard and structured-surface contract: attention categories, dashboard cards/detail surfaces, My Account/left rail effects, payload queries, surface actions, states, and tests
- Capability contracts involved before Akka components: ids, actors/callers, AuthContext/scope, schemas, side effects, idempotency, policy/approval, audit/trace, exposure channels, and tests
- Autonomous task and notification/projection contract when applicable: task definition/result DTO, lifecycle capabilities, notifications, attention projection effects, and tests
- Akka components involved
- Domain shape or business objects
- Commands and write operations
- Views and queries
- Endpoint/API scope
- Invariants
- Integrations
- Required tests
- Out of scope
- Handoff

## Required content for each backlog file

Each `specs/backlog/*.md` file should contain:
- Purpose
- Delivery goal
- Vertical workstream contract per task: functional agent, attention category, dashboard card/detail surface, surface/action or workstream event, capability id/class, AuthContext and role/capability rules, selected Akka substrate, autonomous task candidate when applicable, notification/projection effects, frontend/API/realtime work, and required tests
- AI-first scope when applicable, including delegated work, retained authority, durable objects, agent/team boundaries, policies, approvals/exceptions, evidence/risk/confidence/impact, traces, UI surfaces, and outcomes
- Capability contracts: ids, actors/callers, AuthContext/scope, input/output schemas, data access, side effects, idempotency, policy/approval rules, audit/trace obligations, selected capability exposure channels, and required tests
- Recommended package layout additions
- Class-by-class file list
- Concrete endpoint list
- Write-model design decisions
- Agent/AutonomousAgent/workflow/view/consumer/timer design as relevant, including why durable internal/background model-driven work does or does not use Akka `AutonomousAgent`
- Workstream expertise plan for each new or materially changed functional agent: expert bundle id, approved model binding (`ModelConfigRef`/`ModelPolicy` or explicit inherited governed default), allowed modes, fallback/no-fallback policy, provider secret boundary, model-use trace facts, prompt/skill/reference document families, compact manifests, tool-boundary grants, loaders, traces, default-content governance path, UI/governance surfaces, and tests
- Test plan by file/class, including guardrail/evaluation/policy/audit/outcome checks when applicable
- Implementation order
- Suggested harness task breakdown
- Done criteria
- Explicit defer list

For the first SaaS foundation backlog, the `Suggested harness task breakdown` must split user-admin and governed runtime agent foundation work into concrete tasks instead of one vague `auth/admin` or `agent governance` item. Include bounded tasks for invitation lifecycle, email delivery/outbox, UserDirectoryView, MembershipView, InvitationView, AdminAuditView, AccessReviewQueueView, membership/role management, admin audit/search, `AgentDefinition` lifecycle/profile and agent catalog/detail, `PromptDocument`/`PromptVersion` governance and prompt assembly/`PromptAssemblyTrace`, `SkillDocument`/`SkillVersion` governance, `ReferenceDocument`/`ReferenceVersion` governance, `AgentSkillManifest`, `AgentReferenceManifest`, compact expertise manifest assembly, authorized `readSkill(skillId)`/`SkillLoadTrace`, authorized `readReferenceDoc(referenceId)`/`ReferenceLoadTrace`, `ToolPermissionBoundary`, `AgentWorkTrace` search/detail, behavior editing agent proposal/review flow, AI admin responsibilities such as AdminRiskAgent and AccessReviewAgent or a skilled `UserAdminAgent`, decision cards for risky admin actions, admin/agent-governance UI surfaces, and security/admin/agent-governance tests before app-specific domain features.

For every new or materially changed domain-specific functional agent, the `Suggested harness task breakdown` must create self-contained fresh-session workstream expertise tasks rather than a single `make the agent expert` item. Split as needed into bounded tasks for app-description expert-bundle definition, governed `ModelConfigRef`/`ModelPolicy` or inherited default binding, fallback/no-fallback policy, provider secret boundary, model-use traces, default prompt content, procedural `SkillDocument` content, factual/process `ReferenceDocument` content, compact skill/reference manifests, `ToolPermissionBoundary` and loader grants, runtime `readSkill`/`readReferenceDoc` authorization and denied-load behavior, `PromptAssemblyTrace`/`SkillLoadTrace`/`ReferenceLoadTrace`/`AgentWorkTrace`, expertise manifest and governance UI surfaces, and tests for model binding success/denial/fallback and no provider-secret leakage, assigned loads, unassigned denials, boundary denials, no authority expansion from text, tenant isolation, audit/trace visibility, and surface rendering.

## Required content for the pending question queue

`specs/pending-questions.md` must follow `../docs/pending-question-queue.md` and include, for each question:
- question ID and title
- status
- priority
- category
- dependencies
- blocked decisions/artifacts/work areas
- source/provenance
- focused question text
- why it matters
- options when useful
- default if deferred
- answer, decision, decision impact, reconciled-into, and notes fields

Create this queue only when open decisions are meaningful enough to affect planning or implementation. Do not use it as a cosmetic preference checklist.

## Required content for the pending task queue

`specs/pending-tasks.md` must follow `../docs/pending-task-queue.md` and include, for each task:
- task ID and title
- status
- source backlog path
- functional agent(s) or explicit internal-only/foundation scope
- attention category and dashboard card/detail surface, or explicit non-attention/internal-only reason
- surface/action or workstream event, or explicit non-UI/internal trigger
- capability id(s)/class(es) or explicit foundation/cross-cutting scope, when applicable
- workstream expert bundle scope when the task touches a functional agent's model binding, prompt, skills, references, manifests, loaders, boundaries, traces, default content, governance UI, or expertise tests; LLM-backed agent tasks must name the approved `ModelConfigRef`/`ModelPolicy` or explicit inherited governed default, fallback policy, and model-use trace expectation
- AuthContext and role/capability rules
- selected Akka substrate plus frontend/API/realtime work when applicable
- autonomous task definition/result/notification scope when the task starts, reads, renders, tests, or exposes durable internal/background model-driven work
- notification/projection/attention effects, including My Account and left rail behavior when applicable
- task brief path or `none`
- dependencies
- required reads
- skills
- expected outputs
- required checks
- done criteria that say what observable behavior proves the task is complete
- local-run/manual-smoke validation when the task implements runtime app behavior, or an explicit non-runtime/internal-only reason
- notes when useful

Create queue tasks from backlog `Suggested harness task breakdown` items, not from every class name. A queue task should be one focused harness implementation run. For SaaS foundation work, never collapse invitation lifecycle, email delivery, user directory/search, membership/role management, admin audit/search, access review queues, `AgentDefinition`, `PromptDocument`, `SkillDocument`, `ReferenceDocument`, `AgentSkillManifest`, `AgentReferenceManifest`, `readSkill`, `readReferenceDoc`, `PromptAssemblyTrace`, `SkillLoadTrace`, `ReferenceLoadTrace`, behavior editing agents, AI admin, decision cards for risky admin recommendations, admin/agent-governance UI, and security/admin/agent-governance tests into a single broad `auth/admin` or `agent governance` queue item. For domain-specific functional agents, never collapse expert bundle definition, prompts, skills, references, manifests, tool boundaries, authorized loaders, governance UI, and tests into one broad `agent expertise` task unless the backlog proves that single task is still a bounded fresh-session edit.

## Anti-patterns

Avoid:
- stopping at a single master plan when the user asked for implementation-ready planning artifacts
- writing giant module, sprint, or slice files that still require the whole PRD for context
- generating backlog files that are just restatements of the sprint or slice title
- mixing unrelated capabilities into one sprint/slice only because they are both "backend"
- creating layer-only large-PRD sprints such as "all entities" or "all frontend" instead of vertical module increments
- skipping tests in planning docs
- numbering sprints/slices and backlogs inconsistently
- omitting `specs/pending-questions.md` when unresolved blocking decisions would otherwise force guesses
- creating `specs/pending-questions.md` as a tedious cosmetic questionnaire instead of a design-impact queue
- omitting `specs/pending-tasks.md` when follow-on implementation work exists and is sufficiently unblocked
- generating queue tasks that are too broad for one fresh harness context
- generating tasks that mention only component names while dropping capability authority, schemas, side effects, approval, audit, or capability exposure-channel decisions
- generating tasks that name only a module, page, dashboard, CRUD screen, or generic UI feature without functional-agent ownership, surface/action contract, capability id/class, AuthContext/rules, Akka substrate, frontend/API/realtime work, and tests
- generating a vague `make the agent expert`, `agent expertise`, or `agent governance` task without separate model binding, prompt, skill, reference, manifest, boundary, loader, UI/governance, trace, and test obligations or an explicit reason one bounded task is sufficient
- inventing new directory structures when `specs/` already has an established pattern

## Final review checklist

Before finishing, verify:
- the PRD has been fully read
- the solution plan exists
- every solution plan, sprint/slice, backlog, and pending queue has an explicit scope label when full core could be confused with core app baseline, Module 1-only, or another narrower scope
- core-baseline plans include the five-core-workstream core app domain with `markdown_response` surfaces for My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy, and queue follow-up work for full User Admin, Agent Admin, Audit/Trace UI/search, invitations/onboarding, governed agent documents, and full security coverage
- full-core plans do not omit or collapse My Account, User Admin, Agent Admin, Audit/Trace, Governance/Policy, Invitation onboarding, governed runtime agents, workstream UI, or required tests; narrower plans are explicitly labeled `core app baseline`, `Module 1-only / not full core`, or otherwise named
- `core-saas-foundation` was applied and `specs/cross-cutting/01-auth-tenancy-audit.md` plus a first foundation sprint/slice were created for SaaS apps
- first runnable pending tasks implement the full-stack secure foundation before domain-specific features: Account/Profile/Settings, Tenant/Customer, Membership/Role/Permission, WorkOS/JWT seam, `/api/me`, central authorization, complete email-invite onboarding with InvitationWorkflow, email delivery/outbox, expiry/reminder timers, InvitationView, admin invite UI/APIs, managed-agent foundation (`AgentDefinition`, `PromptDocument`/`PromptVersion`, `SkillDocument`/`SkillVersion`, `ReferenceDocument`/`ReferenceVersion`, `AgentSkillManifest`, `AgentReferenceManifest`, `ToolPermissionBoundary`, prompt assembly, authorized `readSkill(skillId)`, authorized `readReferenceDoc(referenceId)`, `PromptAssemblyTrace`, `SkillLoadTrace`, `ReferenceLoadTrace`, `AgentWorkTrace`, behavior editing agent flow), AI admin responsibilities, decision cards for risky admin actions, audit, frontend shell/context selection/admin/agent-governance surfaces, and tenant-isolation/frontend/agent-governance tests
- high-level product inputs were checked for AI-first SaaS concerns before CRUD/module decomposition
- AI-first planning sections exist wherever delegated work, agentic decisions, governance, supervision, audit, or outcomes are applicable
- module/sprint specs exist for large PRDs, or slice specs exist for smaller plans, and they are dependency-ordered around workstreams, attention/dashboard increments, surfaces/actions, and capabilities rather than CRUD/page/component layers
- backlog files exist and align by number with sprint or slice specs
- backlogs preserve vertical workstream contracts and AI-first operating-model, attention/dashboard, capability authority/schemas/side effects/approval/audit/exposure-channel decisions, autonomous task candidates, notification/projection effects, governance, UI-surface, and outcome implications before Akka task breakdown
- every generated implementation task carries functional agent, attention category, dashboard card/detail surface, surface/action or workstream event, capability id/class, AuthContext/rules, selected Akka substrate, autonomous task candidate when applicable, notification/projection effects, frontend/API/realtime work, and tests, or is explicitly internal-only/foundation/cross-cutting
- every new or materially changed functional agent with LLM behavior has planned workstream expertise tasks for the expert bundle, approved model binding (`ModelConfigRef`/`ModelPolicy` or explicit inherited governed default), fallback/no-fallback behavior, provider secret boundary, prompts, `SkillDocument`/`ReferenceDocument` records, `AgentSkillManifest`/`AgentReferenceManifest`, `ToolPermissionBoundary`, authorized loaders, default-content governance, UI/governance surfaces, traces, and model success/denial/fallback, no-secret-leakage, assigned/denied/boundary/tenant-isolation tests
- `specs/pending-questions.md` exists when unresolved decisions block safe task generation
- unresolved `blocking` questions do not silently become implementation assumptions
- `specs/pending-tasks.md` exists when follow-on implementation work remains and is sufficiently unblocked
- pending tasks map to backlog task-breakdown items
- pending tasks preserve capability ids, authority/scope, schemas, side effects, idempotency, audit, approval, and exposure decisions when implementing capability behavior
- pending tasks include required reads, skills, expected outputs, checks, local/runtime validation where applicable, and done criteria
- no sprint, backlog, or pending task describes a named feature such as user auth, sign-in, onboarding, User Admin, Agent Admin, or an app-specific workflow as implemented unless the required backend, API, UI/workstream surface, authorization, audit/trace, and tests for the stated scope are included or the scope is explicitly narrowed
- full-core User Admin planning includes `user-admin-dashboard`, `user-admin-user-list`, and `user-admin-user-account` fullstack acceptance gates; fixture-only, API-only, or UI-only coverage remains a blocking gap until dashboard load, list search/filter, detail open, safe mutation or decision-card action, audit/trace output, and required negative cases are planned
- AI-first pending tasks include relevant AI-first reads/skills plus concrete Akka substrate skills
- cross-cutting concerns are not duplicated excessively across modules, sprints, or slices
- browser UI work, including agent catalog/detail, prompt governance, skill governance, manifest management, tool-boundary, behavior editing proposal, and trace surfaces, has a selected style-guide spec or a pending/deferred style-selection question before UI tasks are created
- each backlog supports bounded implementation work
- optional task briefs exist when backlog items are still too broad
- execution-order docs point to the correct files
- naming is consistent across `specs/`, `modules/`, `sprints/` or `slices/`, `backlog/`, optional `tasks/`, and `pending-tasks.md`

## Response style

When using this skill:
- briefly summarize the proposed module/sprint structure for large PRDs, or slice structure for smaller plans, first
- then create or update the files
- clearly list which files were added or changed
- include `specs/pending-questions.md` in the changed-file summary when unresolved decisions were queued
- include `specs/pending-tasks.md` in the changed-file summary when follow-on implementation tasks exist
- name the next pending question when blocking decisions remain
- name the first runnable pending task and recommend a fresh context with `akka-do-next-pending-task` when tasks are ready
- keep planning explicit and repo-oriented
- do not jump straight into application code
