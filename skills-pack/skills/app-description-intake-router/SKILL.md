---
name: app-description-intake-router
description: Classify flexible user input into description-maintenance or generation intent, extract candidate app-description deltas, and route to the smallest next app-description skill without forcing the user to know internal taxonomy.
---

# App Description Intake Router

Use this as the front door when the user provides natural-language input about an application and the harness must decide how to respond.

This skill exists for a **description-first operating model** where the application description is the semantic source of truth and the maintained runnable implementation is kept consistent with it.

## Goal

Interpret flexible user input and produce a routing decision that:
- defaults to maintaining the app description unless generation is explicitly requested
- detects when the user wants to change only the app description
- detects AI-first/delegated operating-model semantics before routing to capability, behavior, UI, or generation work
- detects when the user wants to generate the app or run it
- consumes a normalized input envelope when available
- extracts candidate workstream count/boundary, attention/role-specific dashboard, human surface graph, governed-tool/browser-tool/agent-tool/internal-tool, internal workstream agent graph, capability, autonomous task, event/notification/trace, behavior, test, security, UI, and observability deltas when normalization has not yet happened
- identifies the smallest next focused skill sequence to load without skipping workstream-attention-dashboard-surface-graph preprocessing
- asks only the minimum clarification needed to avoid an incorrect next step

## Required reading

Read these first if present:
- target project path: AGENTS.md
- `../README.md`
- `../docs/description-first-application-doctrine.md`
- `../docs/ai-first-saas-application-architecture.md`
- `../docs/minimum-ai-first-saas-app.md` for SaaS Foundation App, basic app, starter, or chatbot-like generated SaaS routing to the SaaS Foundation App domain
- `../docs/requirements-to-workstream-development-process.md` for the canonical input/PRD → workstreams → attention → dashboards → surfaces/actions → capabilities → Akka substrate process
- `../docs/agent-workstream-application-architecture.md` for generated full-stack SaaS workstream routing
- `../docs/structured-surface-contracts.md` for surface/action contract routing
- `../docs/capability-first-backend-architecture.md` for capability contract routing before behavior, surfaces, or components
- `../docs/internal-app-description-architecture.md`
- `../docs/app-description-maintenance-flow.md`
- `../app-description-input-normalization/SKILL.md`
- `../ai-first-saas/SKILL.md` when input involves delegated work, agents, decisions, governance, supervision, audit, or outcomes

## Default routing rule

Prefer to use `app-description-input-normalization` first when the input is broad, mixed, or ambiguous.
If normalization has not yet occurred, this skill may perform lightweight extraction itself.

This skill is routing-only. It may name candidate deltas and the next focused skill, but it must not treat its own routing notes as authoritative app-description content. Long-lived meaning belongs in the focused owner layer: `12-workstreams/`, `10-capabilities/`, `15-operating-model/`, `20-behavior/`, `30-tests/`, `40-auth-security/`, `50-observability/`, or `55-ui/`.

If the user does **not** explicitly ask to generate code, implement code, run the app, execute tests, or otherwise realize outputs, treat the input as:
- **change only the app description**

Before selecting a focused description skill for broad product input, check for AI-first signals: delegated operational work, agents, recommendations, policy-bound automation, approvals, exceptions, supervision, audit traces, learning, or outcome accountability. If present, route through AI-first interpretation and preserve `15-operating-model/` semantics instead of reducing the app to CRUD screens or a chatbot.

If the prompt asks for a “minimum app,” “SaaS Foundation App,” “basic app,” “smallest useful app,” “initial chatbot,” or other chatbot-like generated SaaS, route to `SaaS Foundation App`: the SaaS Foundation App domain (My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy) with `markdown_response`, bootstrap AuthContext, durable workstream logs, backend capability boundaries, and audit/work traces. Do not route those prompts to a standalone chatbot, page shell, or single-workstream slice.

For generated full-stack SaaS input, also run a workstream-attention-dashboard-surface-graph pre-check before capability or UI routing. If the user mentions dashboards, portals, work queues, admin consoles, command centers, agent/chat areas, browser actions, approvals, decisions, audit timelines, workflow status, forms, tables, blocked work, overdue work, failed actions, investigations, reviews, digests, notifications, or other work areas, identify candidate `12-workstreams/` functional agents, workstream boundary/count changes, role-specific attention categories, default dashboard summaries, human surface graph nodes/actions, system-message surfaces, surface-action capability candidates, and capability-contained governed-tool candidates first. Then route to capability, UI, behavior, security, observability, or tests as linked follow-up work.

Internal/background model-driven work must be evaluated for autonomous task semantics before component routing. If the input implies durable investigation, review, evaluation, summary, monitoring/remediation, specialist follow-up, coordination, handoff, team, moderation, dependency, cancellation, notification, snapshot/result, or long-running work, preserve an autonomous task candidate and route toward Akka `AutonomousAgent` after capability authority is defined. Request-based Akka `Agent` remains the default for immediate user-facing workstream turns.

Generation is opt-in unless the harness is only recommending it as a possible next step.

## Primary input modes

### 1. Change only the app description
Route here when the user is:
- adding or revising capabilities
- describing delegated work, agents, approvals, policies, exceptions, supervision, audit, learning, or outcomes
- changing rules or workflows
- clarifying behavior
- reporting a bug in expected behavior
- refining tests or edge cases
- tightening auth/security
- refining observability expectations
- asking conceptual questions about what the app should do

### 2. Realize or run the app
Route here only when the user explicitly asks to realize outputs, such as:
- generate or implement the code
- extend or repair the existing app
- run the app
- execute tests
- prepare the app for manual evaluation
- update affected outputs from the current description

Example:
- "ok, now generate the code and run the app"

## Intent signals

Classify the user's input using `../docs/app-description-skill-output-contracts.md`: description delta, generation/realization request, review/readiness request, planning/backlog request, implementation request, or pure question. Preserve confirmed facts separately from inferred candidate deltas.

## What this skill must extract

From the user input or normalized input envelope, identify candidate deltas in these categories:
- functional agents/workstreams: user-facing work areas, workstream count/boundary changes, role workspace, rail placement, prompt intent, authority, callable capabilities, tenant/customer scope, and core-foundation vs domain-specific classification
- attention/dashboard candidates: what needs my attention, target audience, severity/lifecycle/source/freshness, My Account and left-rail contribution, role-specific dashboard summaries, blocked/overdue/risky/failed/waiting states, participant visibility, and next authorized actions
- human surface graph/actions: dashboard trunk, surface nodes, forms, tables, decision cards, audit timelines, workflow status, reusable placement, payload/action candidates, system-message surfaces, surface-request actions, and action-to-capability/governed-tool candidates
- capability or scope, including actors/callers, AuthContext, schemas, side effects, idempotency, policy/approval, audit/trace, and exposure surfaces
- governed-tool candidates: semantic operation ids in capability files and surface/action maps plus qualified browser-tool, agent-tool, and internal-tool exposure candidates
- internal workstream agent graph candidates: virtual dashboard agent attention, worker delegations, escalation/result/proposal surfaces, expertise skill/reference updates, denial/help semantics, and human handoff points
- autonomous task candidates: durable internal/background model-driven investigations, reviews, evaluations, summaries, monitoring/remediation, specialist follow-up, dependencies, notifications, failure/cancellation, delegation, handoff, teams, or moderation that may route to Akka `AutonomousAgent`
- events/notifications/projections/traces: attention projections, workstream events/messages, task notifications, audit/work traces, and dashboard/My Account/left-rail read models
- AI-first operating-model concerns: goals, delegated work, retained human authority, agents, policies, approvals, decisions, exceptions, evidence, traces, learning, and outcomes
- behavior and invariants
- test and example expectations
- auth/security
- observability
- UI realization under `55-ui/`
- generation request
- review or explanation request

Do not require perfect certainty before routing.
Use provisional extraction plus targeted clarification when needed.

## Routing rules

Apply the concise rules in `../docs/app-description-skill-output-contracts.md` plus the focused skill's goal. Preserve mandatory secure SaaS foundation, generated-SaaS runtime completion, tenant/customer scoping, backend authorization, governed agent/tool boundaries, traces, and tests when those concerns are in scope. Ask only blocking questions; otherwise record assumptions and hand off to the next focused skill.

## Clarification policy

Ask questions only when a wrong routing choice would create the wrong internal description update or trigger premature generation.

Good clarification questions are:
- narrow
- binary or small-choice when possible
- about missing behavior, not implementation trivia
- just enough to select the next skill safely

Examples:
- "Do you want this request to change only the app description, or should I also generate the app afterward?"
- "Is this new rule part of normal behavior, or are you mainly specifying a test/regression case?"
- "Is this requirement about access control, or is it just a general business rule?"
- "Do you want me to update observability expectations, or only user-visible behavior?"

## Output contract

Produce a routing result with these sections:
1. Input summary
2. Primary intent
3. Candidate description deltas
4. Candidate functional agents
5. Candidate structured surfaces/actions
6. Surface-action capability candidates
7. Next skill or skill sequence
8. Clarifications needed, if any
9. Generation requested now: yes/no

## Standard output template

Return a concise routing envelope:

- input summary;
- primary intent and secondary intents;
- confirmed/candidate deltas;
- next skill or skill sequence;
- clarifications needed, if any.

Do not include full layer boilerplate; use `../docs/app-description-skill-output-contracts.md` for the detailed envelope.

## Anti-patterns

Avoid:
- jumping straight to code generation on a vague prompt
- routing SaaS Foundation App, basic app, starter, or chatbot-like generated SaaS prompts to a standalone chatbot, a generic page shell, or a single-workstream slice instead of the SaaS Foundation App domain
- converting agentic operational intent into CRUD screens before modeling workstreams, attention, dashboards, surfaces/actions, goals, authority, policies, decisions, traces, and outcomes
- routing dashboard, portal, work queue, approval, decision, notification, or action language directly to UI pages or endpoints before preserving surface-action capability context
- ignoring durable internal/background model-driven work that should be evaluated as an autonomous task / Akka `AutonomousAgent` candidate
- forcing the user to name a skill or internal artifact type
- treating all ambiguity as a reason to stop instead of routing provisionally
- confusing behavior rules with test-only examples when the user is clearly changing the app
- collapsing security or observability into generic behavior when they are explicitly called out
- asking broad discovery questions when a narrow route is already clear

## Final review checklist

Before finishing, verify:
- the primary intent is explicit
- generation is not assumed unless the user asked for it
- candidate workstream boundary/count, attention/role-specific dashboard, human surface graph/action, governed-tool exposure, internal workstream agent graph, capability, autonomous task, event/notification/projection/trace, operating-model, behavior, test, security, UI, and observability deltas are separated when present
- capability and governed-tool changes are not treated as isolated when they imply workstream, attention/dashboard, surface graph/action, internal workstream agent graph, autonomous task, event/notification/projection/trace, behavior, auth/security, tests, UI, observability, or readiness impacts
- the next skill is the smallest focused skill that matches the request
- clarification questions are minimal and justified

## Response style

When answering:
- keep the routing summary short
- state the inferred intent explicitly
- name the next skill or skill sequence clearly
- keep the user interaction natural rather than taxonomy-driven
