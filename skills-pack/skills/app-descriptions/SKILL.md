---
name: app-descriptions
description: Orchestrate description-first application work across intake, behavior, tests, auth/security, observability, readiness, generation, and review summaries. Use when the task spans more than one app-description concern.
---

# App Descriptions

Use this as the top-level skill for description-first application work.

This skill is for a harness operating model where the **application description is the semantic source of truth** and the maintained runnable implementation is kept consistent with it.

## Goal

Maintain or realize an application from its internal app-description system in a way that is:
- description-first
- harness-optimized
- explicit about behavior, tests, security, and observability
- safe about readiness before generation
- suitable for prompt/response review, diff review, and focused file maintenance

## Required reading

Read these first if present:
- target project path: AGENTS.md
- `../README.md`
- `../docs/description-first-application-doctrine.md`
- `../docs/ai-first-saas-application-architecture.md`
- `../docs/capability-first-backend-architecture.md` for governed capability inventory fields before Akka components or exposure surfaces
- `../core-saas-foundation/SKILL.md` for the mandatory secure SaaS foundation that every new app description, readiness review, and generation flow must preserve
- `../docs/internal-app-description-architecture.md`
- `../docs/app-description-maintenance-flow.md`
- `../docs/app-description-end-to-end-workflow-example.md` only as description-maintenance mechanics; do not treat its sample scenario as generated SaaS target architecture
- `../ai-first-saas/SKILL.md` when product intent involves delegated work, agents, decisions, governance, supervision, audit, or outcomes
- `../docs/web-ui-style-guide.md` for mandatory generated SaaS browser UI style selection

Prefer these local examples and references:
- the target project path: app-description/README.md plus `../docs/core-ai-first-saas-foundation.md` for secure AI-first SaaS foundation shape and current generated-app target architecture
- `../docs/minimum-ai-first-saas-app.md` for SaaS Foundation App, basic app, starter, or chatbot-like generated SaaS scope
- current target-project app-description files and SaaS Foundation App templates for description-layer mechanics; do not depend on removed historical domain examples

## Use this skill when

Use this top-level skill when the task spans more than one description concern, such as:
- a new app idea that needs description-first maintenance
- a broad product prompt that may need AI-first operating-model interpretation before normal description updates
- a feature request that affects behavior, tests, and security together
- a bug fix that requires behavior correction plus regression coverage
- a request to assess readiness and possibly generate the app
- a review question asking what changed or whether the description is ready

If the task is already clearly narrowed to one description concern, load the focused companion skill directly.

## Companion skills

Load the companion skill that matches the current task. Users do not need to name these skills; infer them from ordinary product language. For generated full-stack AI-first SaaS apps, requests for a dashboard, admin console, portal, workspace, workflow view, queue, form, table, approval surface, audit timeline, or agent/chat area should route first through functional-agent and structured-surface modeling, then capability modeling, before `55-ui/` browser realization or routes are discussed.

- `app-description-bootstrap`
  - create the initial internal app-description tree for a new app or sparse early app idea
- `app-description-input-normalization`
  - convert flexible user input into a structured app-description delta envelope before routing or maintenance
- `app-description-intake-router`
  - classify flexible input into description-change, generation, mixed, or review intent
- `app-description-functional-agent-modeling`
  - define or revise role-authorized user-facing functional/context-area agents, including prompt intent, skills, tools, surfaces, callable capabilities, authority, traces, and tests
- `app-description-surface-modeling`
  - define or revise structured workstream surfaces, typed payloads, reusable functional-agent placement, capability-backed actions, states, traces, and rendering tests
- `app-description-capability-modeling`
  - define or revise business capabilities, scope boundaries, actors, outcomes, and links to downstream layers
- `app-description-behavior-specification`
  - update behavior rules, transitions, invariants, forbidden behavior, and no-op semantics
- `app-description-test-specification`
  - update acceptance, regression, negative, idempotency, security, and operational verification expectations
- `app-description-change-impact`
  - determine which description layers, traceability artifacts, readiness state, implementation areas, and generated/derived outputs are affected by a change
- `app-description-auth-security`
  - update identity, authorization, trust-boundary, sensitive-data, and denial-behavior rules
- `app-description-observability`
  - update logs, metrics, traces, audit events, health signals, alerts, and diagnosability expectations
- `app-description-ui`
  - update functional-agent workstream UI, structured surfaces, surface actions, route/deep-link details, frontend API contracts, realtime states, accessibility, responsive behavior, and style guide; for AI-first apps, prioritize supervision, decision, governance, digest, and audit surfaces
- `app-description-readiness-assessment`
  - decide whether the current description is `not-ready`, `ready-with-assumptions`, or `ready`
- `app-generate-app`
  - realize the current app description as maintained runnable outputs
- `app-description-change-summary`
  - summarize what changed after a revision request
- `app-description-readiness-summary`
  - summarize why the description is or is not ready for generation

## Generated SaaS workstream ownership invariant

For generated full-stack SaaS, do not change `10-capabilities/`, `55-ui/`, readiness, generation scope, or implementation planning for a user-facing request without first checking whether `12-workstreams/functional-agents.md`, `12-workstreams/workstream-expertise/**`, `12-workstreams/surfaces-index.md`, and relevant `12-workstreams/surface-contracts/**` must change. `12-workstreams/` owns functional agent placement, workstream expert bundle contracts, surface meaning, surface actions, and surface-to-capability traceability. `10-capabilities/` owns governed backend contracts. `55-ui/` owns browser realization only.

## Companion boundary matrix

Use `../docs/app-description-skill-output-contracts.md` for the detailed output contract. Return only the actionable summary, affected artifacts/layers, required edits or queue changes, assumptions/questions, and next step. Preserve secure SaaS foundation, generated-SaaS runtime completion, tenant/customer scoping, backend authorization, governed agents/tools, traces, and tests when in scope.

## Decision guide

### 1. The user is starting a new app description
Start with:
- `ai-first-saas` first when the idea includes delegated work, agents, decisions, governance, supervision, audit, or outcomes
- `core-saas-foundation` for the mandatory foundation contract
- `app-description-bootstrap`
- then `app-description-intake-router`

### 2. The user is revising app meaning
Start with:
- `app-description-input-normalization` when the request is broad, mixed, or ambiguous
- `app-description-intake-router`
- then `app-description-functional-agent-modeling` when the request changes a user-facing work area, admin/operations portal, command center, work queue, or agent/chat area
- then `app-description-surface-modeling` when the request changes dashboards, forms, tables, charts, approvals, decisions, audit timelines, workflow status, or other renderable workstream artifacts
- then `app-description-capability-modeling` when capability scope, operations, queries, authority, side effects, or exposure surfaces are changing
- then `app-description-behavior-specification`
- then `app-description-test-specification`

### 3. The user is tightening production concerns
Start with:
- `app-description-input-normalization` when the request is broad, mixed, or ambiguous
- `app-description-intake-router`
- then `app-description-auth-security` and/or `app-description-observability`

### 4. The user asks whether the app is ready
Start with:
- `app-description-readiness-assessment`
- then `app-description-readiness-summary`

### 5. The user asks to generate the app
Start with:
- `app-description-readiness-assessment`
- then `app-generate-app` if appropriate

### 6. The user asks what changed
Start with:
- `app-description-change-summary`

## Final review checklist

Before finishing, verify:
- the current task is routed to the smallest suitable companion skill
- operating-model, behavior, tests, security, UI, and observability are kept separate when they change separately
- broad AI-first product input is interpreted before CRUD/component decomposition
- the secure SaaS foundation is present or the gap is treated as blocking readiness/generation
- readiness is not skipped before generation
- generation summaries are clearly distinguished from description changes
- review answers focus on app meaning and readiness rather than internal editing mechanics

## Response style

When answering:
- identify whether the task is description maintenance, readiness, generation, or review
- load only the smallest relevant companion skills
- keep the interaction natural and prompt/response oriented
- preserve description primacy throughout
