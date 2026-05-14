# App Description Maintenance Flow

## Purpose

This document defines the default harness operating flow for maintaining the internal app-description artifact system.

It complements `docs/internal-app-description-architecture.md` by defining **how** the layers should be updated over time.

Reference example:
- `docs/examples/purchase-request-app-description/app-description/`

## Core interaction model

The user interacts through flexible prompt/response.
The harness interprets intent and updates internal description artifacts.
For high-level product input, the harness first checks whether the product should be treated as AI-first SaaS and applies the mandatory secure SaaS foundation: Account/Profile/Settings, Tenant/Customer, Membership/Role/Permission, `/api/me`, backend authorization, audit, tenant isolation, durable goals, delegated work, bounded agents, policy/permission controls, supervision, approvals/exceptions, traces, and outcome accountability.
The user does not edit:
- generated code
- generated tests
- internal app-description artifacts

All app evolution enters as user input to the harness.

## Primary user intents

The harness should initially support two primary intents.

### 1. Change only the app description
This is the default.

Use when the user is:
- adding or changing capabilities
- revising workflows or rules
- clarifying tests
- tightening auth/security
- refining observability
- reporting bugs in intended behavior

### 2. Generate the app
Use only when the user explicitly asks to realize outputs or accepts a harness recommendation to do so.

## Default harness flow

Prefer this default sequence:

1. bootstrap the internal app-description tree if no usable root exists yet, seeding the secure SaaS foundation capability, behavior, tests, auth/security, observability, and UI surfaces when in scope
2. normalize the user input when it is broad, mixed, or ambiguous
3. intake and route the user input
4. identify impacted description layers
5. update `15-operating-model/` when AI-first/delegated operations are in scope
6. update behavior-level meaning
7. update verification expectations
8. update auth/security, preserving explicit default-deny semantics for every route, tool, data access path, workflow action, view query, stream, consumer, timer, and generated UI action
9. update observability, preserving AdminAuditEvent and trace requirements for identity, Membership/role, support-access, billing-boundary, data-access, policy, approval, and consequential AI/tool activity
10. update UI descriptions, including `55-ui/style-guide.md`, when a browser frontend is in scope
11. update traceability and impact understanding
12. assess readiness
13. when realization planning artifacts already exist, reconcile affected specs/backlogs/pending tasks before more coding
14. either stop at description maintenance or proceed to generation
15. answer review questions through summaries

## Change-only flow

When the user is not explicitly asking for realization, use this flow:

### Step 0. Bootstrap if needed
Use `app-description-bootstrap` when no usable `app-description/` root exists yet.

### Step 1. Normalize when needed
Use `app-description-input-normalization` when the input is broad, mixed, or ambiguous.

### Step 2. Route
Use `app-description-intake-router` to determine:
- description-only vs generation intent
- candidate affected layers
- whether clarification is needed

### Step 3. Update capabilities when scope changed
Use `app-description-capability-modeling` when the request changes business scope, actors, or intended outcomes.
For AI-first apps, connect capabilities to durable goals, delegated work, agent/team responsibilities, governance boundaries, decision surfaces, audit needs, and outcome loops.

### Step 4. Update operating model when agentic semantics changed

Update `15-operating-model/` when the request changes:
- objectives, success criteria, constraints, or definitions of done
- delegated work versus retained human authority
- agent/team responsibilities, tools, data access, memory, thresholds, or escalation rules
- policies, clauses, guardrails, permissions, approval gates, or governed policy changes
- recommendations, decisions, exceptions, evidence, risk, confidence, impact, alternatives, or precedents
- work traces, decision traces, policy invocations, feedback, learning, replay/simulation, or outcome metrics

Route to focused AI-first companion skills only for the affected concerns; do not duplicate Akka implementation guidance in app-description artifacts.

### Step 5. Update behavior
Use `app-description-behavior-specification` to update the app's meaning.

Behavior follows the operating model and the secure SaaS foundation because:
- tests need something to verify
- security needs something to protect
- observability needs something meaningful to expose
- agentic work needs concrete transitions, pause/resume points, and exception semantics

### Step 6. Update tests
Use `app-description-test-specification` to make the behavior explicit and verifiable.

For generated SaaS apps, the test layer must retain baseline tenant isolation, forbidden access, disabled-user, role/scope denial, `/api/me`, audit, support-access, billing-boundary, idempotency, and frontend secret-boundary tests even when the current change is app-specific.

### Step 7. Analyze change impact
Use `app-description-change-impact` to determine:
- which additional layers must move
- which traceability artifacts must update
- whether readiness should be reassessed
- whether later regeneration can stay localized

### Step 8. Update production-readiness layers
Update as needed:
- `app-description-auth-security`
- `app-description-observability`

These are not optional polish layers.
They are part of the app definition. Missing secure SaaS foundation semantics must block readiness or generation rather than becoming assumptions.

### Step 9. Update UI when browser supervision is in scope

When a browser frontend is in scope, update `55-ui/` after the operating model and behavior are clear enough to describe the user's work surfaces.
For AI-first apps, prefer goal-to-execution, command center, decision-card, governance/learning, digest, and audit/trace surfaces over CRUD navigation unless the product intent is explicitly CRUD-centric.
Keep the style-selection rule below in force.

### Step 10. Update readiness
Use `app-description-readiness-assessment` to reflect the new current state.

### Step 11. Respond with review summaries when useful
Use:
- `app-description-change-summary`
- `app-description-readiness-summary`

## Generate-app flow

When the user explicitly asks to realize the app, use this flow:

### Step 1. Confirm current description state
Read the current authoritative layers.

### Step 2. Assess readiness
Use `app-description-readiness-assessment`.
Do not skip this step just because generation was requested.

### Step 3. Decide outcome
- if `not-ready`, explain blockers and continue description work; missing Account/Profile/Settings, Tenant/Customer, Membership/Role/Permission, `/api/me`, backend authorization, audit, tenant isolation, disabled-user, forbidden-access, role/scope-denial, or foundation test semantics are blocking gaps for generated SaaS apps
- if `ready-with-assumptions`, surface assumptions explicitly and proceed only if acceptable
- if `ready`, proceed

### Step 4. Generate
Use `app-generate-app`.
Generation must remain subordinate to the current description.

### Step 5. Run and evaluate if requested
Run tests, run the app, or prepare manual evaluation artifacts as requested and supported.

### Step 6. Summarize results
Use generation and readiness summaries to explain what happened.

## Layer update order

When multiple layers are affected, prefer this order:

1. `10-capabilities/`
2. `15-operating-model/` when AI-first/delegated operations are in scope
3. `20-behavior/`
4. `30-tests/`
5. `40-auth-security/`
6. `50-observability/`
7. `55-ui/` when a browser frontend is in scope, including `style-guide.md`
8. `70-traceability/`
9. `00-system/readiness-status.md`
10. `60-generation/` derived generation notes
11. `80-review/` optional summaries

This keeps the semantic source layers ahead of the derived layers.

## Change-impact rule

Every description change should trigger a mental or explicit change-impact pass.

The harness should ask:
- which capabilities changed?
- which behavior artifacts changed?
- which AI-first operating-model artifacts changed: goals, delegated work, retained authority, agents, policies, approvals, decisions, exceptions, traces, learning, or outcomes?
- which tests now need updates?
- are there new or changed security implications, especially permission enforcement and authority boundaries?
- are there new or changed observability implications, especially work traces, decision traces, policy invocations, audit events, and outcome metrics?
- if a browser UI is in scope, is a style guide selected and do supervision, decision, governance, digest, or audit surfaces need to change?
- does readiness status change?
- would generation scope be localized or broad?
- do any existing specs, backlogs, task briefs, or pending tasks need to be updated, blocked, deferred, or superseded?

## Readiness rule

Readiness is a maintained state, not a one-time milestone.

The harness should reassess readiness after material description changes, especially when they affect:
- state transitions
- failure semantics
- delegated authority, approvals, exceptions, or policy controls
- audit trace and outcome requirements
- auth/security
- observability
- operational tests

## UI style-selection rule

When a browser UI is in scope, the app description or specs must contain a selected web UI style guide before web UI implementation or generation starts. If style is missing, add or update a `category: ui` entry in `specs/pending-questions.md` using `docs/web-ui-style-guide.md` rather than silently choosing a theme. This blocks only affected web UI work; unrelated backend description or implementation work can continue.

## Regeneration rule

Generation is allowed only as a realization step.

The harness must not let generation become the place where missing semantics are silently invented.
Secure SaaS foundation gaps are especially blocking: generation must not invent which routes, agent tools, data access paths, workflow actions, view queries, streams, consumers, timers, or generated UI actions are public or authorized by default.
If generation exposes missing semantics, the harness should:
1. identify the semantic gap
2. update the description or ask for clarification
3. regenerate as needed

## Review rule

Human review is prompt/response.

The harness should prefer answering questions such as:
- what changed?
- what behavior changed?
- what is still missing?
- is the description ready?
- what was generated?

The review answer should be based on authoritative description layers, not on generated code diffs alone.

## Summary artifact rule

Review summaries are useful, but they are not the source of truth.

If stored at all, they should be treated as derived artifacts under `80-review/`.
The real app definition remains in the earlier authoritative layers.

## Minimal clarification rule

The harness should ask only the smallest clarifying question needed to avoid one of these mistakes:
- changing the wrong semantic layer
- inventing a security model implicitly
- inventing secure SaaS foundation behavior, tests, or UI surfaces implicitly
- inventing observability requirements implicitly
- generating too early
- treating a user review question as a generation request

## Full vs localized regeneration rule

When generation is requested, the harness should decide between:
- full regeneration
- localized regeneration

Use localized regeneration only when the changed description area and affected outputs are sufficiently clear.
Otherwise prefer full regeneration.

Localized regeneration is an optimization.
It must never preserve stale behavior that conflicts with the current description.

## Completion rule for a change request

A description change request is complete when:
- the relevant authoritative layers are updated
- when AI-first/delegated operations are in scope, `15-operating-model/` captures goals, delegated work, human authority, policies, decisions, traces, and outcomes sufficiently for the change
- linked verification expectations are updated, including mandatory secure foundation tests for generated SaaS apps
- linked security and observability expectations are updated when needed
- readiness has been reassessed or intentionally deferred
- when a browser UI is in scope, the selected style guide is recorded or a pending style-selection question blocks only UI realization
- affected realization specs/backlogs/task briefs/pending tasks are reconciled when they already exist
- the user can receive a coherent summary of what changed

A generation request is complete when:
- readiness was assessed
- generation scope was chosen deliberately
- outputs were generated
- execution steps and results were summarized clearly
- any remaining semantic gaps were surfaced back to the description layer

## Anti-patterns

Avoid:
- defaulting to code generation for every prompt
- updating tests without first clarifying behavior
- treating auth/security or observability as optional afterthoughts
- using generated code as the primary explanation of what changed
- leaving readiness stale after major description changes
- silently burying semantic uncertainty inside generation results
- converting delegated operational work into CRUD screens before modeling goals, authority, policies, decisions, traces, and outcomes
- treating a chatbot or prompt as a substitute for durable goals, plans, approvals, and auditability

## Recommended next-doc usage

Use this document together with:
- `docs/description-first-application-doctrine.md`
- `docs/internal-app-description-architecture.md`
- `docs/app-description-skills-plan-backlog.md`

The doctrine defines the why.
The architecture doc defines the artifact structure.
This document defines the maintenance flow.