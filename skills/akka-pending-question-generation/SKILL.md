---
name: akka-pending-question-generation
description: Create or update specs/pending-questions.md from a PRD, app-description, solution plan, specs, or backlog when unresolved decisions should be answered before safe task generation or implementation.
---

# Akka Pending Question Generation

Use this skill when planning has discovered uncertainty that should be captured as a durable, one-question-at-a-time clarification queue instead of a large ad hoc question list.

This is a planning and clarification skill. It does not implement application code. For generated secure AI-first SaaS, use questions to block only the specific missing link in the requirements-to-workstream chain: workstream, role-specific dashboard attention, human surface graph node/edge, structured surface action, governed-tool identity/exposure, capability/API, Akka substrate, internal workstream agent graph delegation/result handling, AutonomousAgent task lifecycle, notification/projection, audit/work trace, or local validation.

## Goal

Create or update:

```text
specs/pending-questions.md
```

The queue should capture only questions that affect downstream meaning, architecture, specs, backlog, task generation, tests, security, observability, UI/API contracts, or safe implementation.

The skill must:
- inspect the current PRD, app-description, solution plan, specs, backlogs, or existing question queue
- identify decisions the harness would otherwise have to guess
- order questions by dependency and impact
- mark blocking questions clearly
- preserve existing question IDs and statuses when updating a queue
- avoid asking a giant question list in chat
- report the next question to ask with `akka-do-next-pending-question`

## Use this skill when

Use this skill when the user says things like:
- "create pending-questions.md"
- "turn the open questions into a queue"
- "grill me, but one question at a time"
- "before pending-tasks, capture the unresolved decisions"
- "what decisions are blocking the plan?"
- "make a clarification backlog"

Also use it during PRD/spec planning when unresolved decisions would otherwise make `specs/pending-tasks.md` speculative.

Do **not** use this skill when:
- the next implementation step is already concrete and safe
- all uncertainties are minor optional refinements
- the user wants to execute implementation work; use `akka-do-next-pending-task`
- the user wants to answer an existing question; use `akka-do-next-pending-question`
- the user wants to audit an existing question queue; use `akka-pending-question-queue-maintenance`

## Required reading

Read these first if present:
- `../README.md`
- `../core-saas-foundation/SKILL.md` for the mandatory secure SaaS baseline and the provider-uncertainty rule
- `../../docs/ai-first-saas-application-architecture.md`
- `../../docs/requirements-to-workstream-development-process.md` when unresolved decisions affect workstreams, attention, dashboards, surface actions, capabilities, AutonomousAgent tasks, notifications/projections, or task materialization
- `../../docs/workstream-expertise-model.md` when unresolved decisions affect LLM-backed functional-agent expertise, model binding, skills, references, manifests, `readSkill`, `readReferenceDoc`, tool boundaries, traces, or expertise surfaces
- `../../docs/pending-question-queue.md`
- `../../docs/pending-task-queue.md`
- `../../docs/intent-driven-usage-flow.md`
- `../../docs/web-ui-style-guide.md`
- `../ai-first-saas/SKILL.md`
- `../../specs/README.md`
- `../../specs/pending-questions.md` if it already exists
- `../../specs/akka-solution-plan.md` if present
- relevant `../../specs/slices/*.md`
- relevant `../../specs/backlog/*-build-backlog.md`
- relevant app-description files if the project uses description-first artifacts
- the user-provided PRD or requirements file when it is the source of the plan

Do not read the entire codebase. This skill is about planning decisions, not implementation details.

## Question discovery rules

Do not ask security-provider selection questions in a way that blocks the core model itself. WorkOS/AuthKit is the supported browser authentication provider and Resend (resend.com) is the supported production email service. Unknown WorkOS setup values or missing Resend settings may block only provider-specific integration tasks; they must not block modeling Tenant/Customer boundaries, local Account/Membership/Role/Permission authorization, AuthContext, `/api/me`, AdminAuditEvent, tenant/customer-scoped commands and queries, or tenant-isolation tests.

For new Java generation/scaffolding, ensure the Java base package is resolved before implementation tasks write source files. If no package exists in project configuration or app-description/spec artifacts, create or ask a focused `category: generation` question: "What Java base package should I use for generated code? Press Enter to use `ai.first`." Default if deferred: `ai.first`. Mark it blocking only for Java source generation/scaffolding tasks. Never default to `com.example` unless explicitly selected; `com.example` examples are reference material only.

Create questions only when the answer can change one or more of:
- AI-first operating model: delegated work, retained human authority, supervision mode, or outcome loop
- requirements-to-workstream contract: workstream ownership, attention categories, role-specific dashboard summaries, left-rail/My Account attention behavior, human surface graph nodes/edges, structured surface actions, governed-tool ids and qualified exposure, capability mapping, internal workstream agent graph delegations/results, AutonomousAgent task lifecycle/results, notifications/projections, or audit/work trace linkage
- workstream expertise contract: model binding, prompt intent, skill/reference document ownership, compact manifests, `readSkill`/`readReferenceDoc` loader authorization, `ToolPermissionBoundary`, SkillLoadTrace/ReferenceLoadTrace/AgentWorkTrace, expertise surfaces, seed policy, or tests
- agent or agent-team authority: autonomous decisions, tool/data permissions, escalation rules, or memory/trace requirements
- governance model: policies, clauses, guardrails, prompts, thresholds, approval gates, simulations, or human-governed commits
- decision semantics: required evidence, risk/confidence/impact thresholds, alternatives, exception handling, or override behavior
- audit and outcome accountability: work traces, decision traces, policy invocations, data-access records, retention, metrics, or outcome links
- Akka component selection
- Event Sourced Entity vs Key Value Entity choice
- workflow shape, pause/resume, compensation, or deadline behavior
- view/query/reporting requirements
- consumer/integration behavior
- timer/reminder/expiry behavior
- HTTP/gRPC/MCP/API contract
- UI behavior, realtime requirements, dashboard behavior, attention item lifecycle, surface action states, notification/realtime requirements, or web UI style-guide selection
- auth, tenancy, audit, retention, or privacy model
- failure handling, retries, idempotency, or compensation
- acceptance, regression, evaluation, replay/simulation, or edge-case tests
- backlog slicing, dependencies, or task generation
- Java package namespace, group id, generated source paths, imports, and test package names

For generated full-stack AI-first SaaS, if no selected style exists in `app-description/55-ui/style-guide.md`, `specs/cross-cutting/*ui-style-guide*.md`, or an equivalent UI spec, append a `category: ui` question using the canonical AI-first style options from `../../docs/web-ui-style-guide.md`: `ai-first-workstream-enterprise` with the four initial named themes, or `custom` with a user-supplied style brief that preserves named-theme semantics. Mark it `priority: blocking` for web UI implementation/generation tasks; do not block unrelated backend work.

For AI-first SaaS inputs, prefer a small number of actionable blocker questions over broad product interviews. Queue a blocking question only when the harness cannot safely choose a default for a concrete implementation area. Good AI-first blocker patterns include:
- `category: behavior` — what work is delegated to agents versus retained by humans?
- `category: workstream-expertise` — which governed model binding, skills, references, manifests, `readSkill`/`readReferenceDoc` access, traces, and expertise surfaces make this functional agent ready for its workstream?
- `category: reference-governance` — which reference documents may the agent load, cite, or show as evidence, and what redaction/denial/ReferenceLoadTrace behavior is required?
- `category: authorization` — which actions, tools, data, or decisions may agents perform without approval?
- `category: workflow` — which approval, escalation, pause/resume, retry, or compensation gates are mandatory?
- `category: security` — which policy clauses, permissions, tenant boundaries, or redaction rules must be mechanically enforced beyond the mandatory core SaaS foundation?
- `category: security-provider` — which provider-specific issuer, audience, callback, claim mapping, or AuthKit details are needed for WorkOS/JWT integration tasks?
- `category: observability` — which work, decision, policy, tool, data-access, approval, and outcome traces are required?
- `category: testing` — which evaluations, replay/simulation checks, or threshold acceptance tests prove safe behavior?
- `category: ui` — which supervision, command-center, dashboard, attention inbox, decision-card, governance, digest, or audit surface is required first?
- `category: surface-graph` — which surface node, action edge, system-message surface, or dashboard transition should represent the work?
- `category: governed-tool` — which semantic operation is being added or changed, and is it a browser-tool, agent-tool, internal-tool, workflow/timer/consumer/MCP-tool, or multiple exposures?
- `category: internal-agent-graph` — which internal worker delegation, result, proposal, escalation, or human attention item should the workstream agent graph produce?
- `category: workflow` — which attention item lifecycle, notification/projection source, or AutonomousAgent task state should drive a dashboard or surface action?
- `category: generation` — what Java base package should generated code use, defaulting to `ai.first` if the user defers?

Avoid questions that are:
- cosmetic
- answerable from existing artifacts
- implementation trivia the harness can decide from conventions
- duplicates of existing unresolved questions
- broad multi-part questions that should be split

## Prioritization

Use `priority: blocking` only when task generation or implementation would require guessing. For AI-first work, this includes guessing about delegated authority, approval gates, risk/confidence thresholds, policy enforcement, audit obligations, or outcome metrics for consequential actions.

Use `priority: important` when the answer affects quality or later scope but does not block all safe progress.

Use `priority: optional` when the answer can safely wait.

If a question blocks only one slice or component family, say so in `blocks:`. Do not block unrelated work.

## Required output shape

Follow `../../docs/pending-question-queue.md` exactly.

Each question must include:
- stable question ID and short title
- status
- priority
- category
- dependencies
- blocked artifacts/decisions/work areas
- source/provenance
- one focused question
- why it matters
- options when useful
- default if deferred, or `none`
- answer/decision/decision impact placeholders
- reconciled-into list
- notes when useful

## Existing queue preservation

If `specs/pending-questions.md` already exists:
- preserve question IDs
- preserve `resolved`, `deferred`, and `superseded` history
- keep `answered` questions as `answered` until reconciliation is done
- update stale metadata when the same question remains valid
- mark obsolete unresolved questions `superseded` instead of deleting them
- append newly discovered questions using the next stable ID
- do not renumber questions for aesthetics

## Relationship to pending tasks

Before creating or updating `specs/pending-tasks.md`, check whether unresolved `blocking` questions affect planned tasks. For AI-first plans, also check whether tasks depend on unresolved workstream identity, attention/dashboard contracts, human surface graph nodes/edges, surface actions, governed-tool ids/exposure, capability ids, internal workstream agent graph delegation/result behavior, authority, approval, policy, evidence, risk, AutonomousAgent lifecycle, notification/projection, trace, UI-supervision, evaluation, or outcome-metric decisions. For LLM-backed functional-agent work, also check for unresolved workstream expert bundle, model-binding, skill/reference governance, `readReferenceDoc`, manifest assignment, loader authorization, tool-boundary, load-trace, expertise-surface, and seed/test decisions.

If blocking questions exist:
- create or update `specs/pending-questions.md`
- do not create implementation tasks for blocked work unless explicitly marked deferred with a safe default
- keep secure foundation modeling and local authorization-contract tasks unblocked when only provider-specific setup details are unknown
- create tasks only for unblocked work, or stop and ask the next question depending on the user's requested workflow

## Final review checklist

Before finishing, verify:
- `specs/pending-questions.md` exists or no durable questions were needed
- Java source generation has a selected base package or a pending/deferred base-package question with default `ai.first` that blocks only affected Java generation tasks
- browser UI work has a selected style guide or a pending/deferred style-selection question that blocks only affected UI tasks
- every question has a clear design impact
- AI-first questions are actionable blockers or meaningful quality decisions, not cosmetic prompts
- blocking questions name what they block
- question dependencies are valid
- existing statuses and IDs were preserved
- no application code was changed
- the next actionable question is identified

## Response style

When using this skill:
- summarize why a question queue was or was not needed
- list counts by status and priority
- name the next question to answer
- recommend continuing with `akka-do-next-pending-question`
- do not dump all question text in chat unless the user asks for the full queue
