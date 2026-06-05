---
name: app-descriptions
description: Orchestrate description-first application work across intake, behavior, tests, auth/security, observability, readiness, generation, and review summaries. Use when the task spans more than one app-description concern.
---

# App Descriptions

Use this as the top-level skill for description-first application work.

This skill is for a harness operating model where the **application description is the source of truth** and generated code is a downstream projection.

## Goal

Maintain or realize an application from its internal app-description system in a way that is:
- description-first
- harness-optimized
- explicit about behavior, tests, security, and observability
- safe about readiness before generation
- suitable for prompt/response review instead of direct file editing

## Required reading

Read these first if present:
- `../../../AGENTS.md`
- `../README.md`
- `../docs/description-first-application-doctrine.md`
- `../docs/ai-first-saas-application-architecture.md`
- `../docs/capability-first-backend-architecture.md` for governed capability inventory fields before Akka components or exposure surfaces
- `../core-saas-foundation/SKILL.md` for the mandatory secure SaaS foundation that every new app description, readiness review, and generation flow must preserve
- `../docs/internal-app-description-architecture.md`
- `../docs/app-description-maintenance-flow.md`
- `../docs/app-description-end-to-end-workflow-example.md` only as description-maintenance mechanics; do not treat its purchase-request scenario as generated SaaS target architecture
- `../ai-first-saas/SKILL.md` when product intent involves delegated work, agents, decisions, governance, supervision, audit, or outcomes
- `../docs/web-ui-style-guide.md` for mandatory generated SaaS browser UI style selection

Prefer these local examples and references:
- the target project `app-description/README.md` plus `../docs/core-ai-first-saas-foundation.md` for secure AI-first SaaS foundation shape and current generated-app target architecture
- `../docs/minimum-ai-first-saas-app.md` for minimum/starter/basic/chatbot-like generated SaaS scope
- Purchase-request description examples only for description-layer mechanics, never target architecture doctrine:
  - `../docs/examples/purchase-request-app-description/README.md`
  - `../docs/examples/purchase-request-app-description/normalized-input-example.md`
  - `../docs/examples/purchase-request-app-description/app-description/00-system/app-manifest.md`
  - `../docs/examples/purchase-request-app-description/app-description/20-behavior/flows/01-submission-and-approval-flow.md`
  - `../docs/examples/purchase-request-app-description/app-description/30-tests/acceptance/01-purchase-request-acceptance.md`
  - `../docs/examples/purchase-request-app-description/app-description/40-auth-security/identity-and-authorization.md`
  - `../docs/examples/purchase-request-app-description/app-description/50-observability/logs-metrics-traces-and-alerts.md`
  - `../docs/examples/purchase-request-app-description/app-description/70-traceability/capability-to-behavior-map.md`

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
  - determine which description layers, traceability artifacts, readiness state, and generated outputs are affected by a change
- `app-description-auth-security`
  - update identity, authorization, trust-boundary, sensitive-data, and denial-behavior rules
- `app-description-observability`
  - update logs, metrics, traces, audit events, health signals, alerts, and diagnosability expectations
- `app-description-ui`
  - update functional-agent workstream UI, structured surfaces, surface actions, route/deep-link details, frontend API contracts, realtime states, accessibility, responsive behavior, and style guide; for AI-first apps, prioritize supervision, decision, governance, digest, and audit surfaces
- `app-description-readiness-assessment`
  - decide whether the current description is `not-ready`, `ready-with-assumptions`, or `ready`
- `app-generate-app`
  - realize the current app description as generated outputs
- `app-description-change-summary`
  - summarize what changed after a revision request
- `app-description-readiness-summary`
  - summarize why the description is or is not ready for generation

## Generated SaaS workstream ownership invariant

For generated full-stack SaaS, do not change `10-capabilities/`, `55-ui/`, readiness, generation scope, or implementation planning for a user-facing request without first checking whether `12-workstreams/functional-agents.md`, `12-workstreams/workstream-expertise/**`, `12-workstreams/surfaces-index.md`, and relevant `12-workstreams/surface-contracts/**` must change. `12-workstreams/` owns functional agent placement, workstream expert bundle contracts, surface meaning, surface actions, and surface-to-capability traceability. `10-capabilities/` owns governed backend contracts. `55-ui/` owns browser realization only.

## Companion boundary matrix

Use this ownership matrix to avoid loading every app-description skill for one change:

| Concern | Primary owner | Must link to |
|---|---|---|
| Initial tree, scope label, baseline readiness/generation policy | `app-description-bootstrap` | secure foundation, workstreams, capabilities, operating model, tests, security, observability, UI |
| Messy or mixed user input | `app-description-input-normalization` | downstream routing only; it is not authoritative app meaning |
| Intent classification and next-step choice | `app-description-intake-router` | smallest focused companion; no long-lived layer ownership |
| User-facing work areas, left-rail agents, and workstream expert bundles | `app-description-functional-agent-modeling` | surfaces, capabilities, governed prompts/skills/references, compact expertise manifests, tool boundaries/loaders, authority, traces, tests, UI |
| Typed renderable workstream artifacts and actions | `app-description-surface-modeling` | functional agents, capability ids, auth, observability, rendering tests, UI |
| Governed backend operations/queries | `app-description-capability-modeling` | source functional agent/surface/action or explicit `internal-only`, behavior, tests, security, observability |
| Rules, flows, state transitions, invariants | `app-description-behavior-specification` | existing or newly required capability contract, tests, security, observability |
| Acceptance, regression, negative, security, observability verification | `app-description-test-specification` | capability/behavior/security/UI/observability artifacts under test |
| Identity, authorization, tenant isolation, data protection, denial behavior | `app-description-auth-security` | protected capabilities, behavior, tests, audit/trace obligations |
| Logs, metrics, audit, traces, health, alerts, diagnosability | `app-description-observability` | capability evidence, behavior, tests, security, supervision/audit UI |
| Browser realization of workstream shell, surfaces, routes, APIs, states, style | `app-description-ui` | `12-workstreams/`, `10-capabilities/`, auth/security, tests, observability |
| Cross-layer impact and regeneration locality | `app-description-change-impact` | all changed authoritative layers, traceability, readiness, specs/backlogs when present |
| Generation readiness state | `app-description-readiness-assessment` | actual authoritative layers; it must not fill missing semantics |
| Realized code/tests/assets | `app-generate-app` | readiness result and current description; generation must not invent missing meaning |
| Human review summaries | `app-description-change-summary`, `app-description-readiness-summary` | authoritative layers or latest assessment; summaries are derived, not source of truth |

When a change crosses rows, update the primary owner first, then only the linked layers that the semantic change actually affects. Do not use readiness, summaries, UI routes, or generated output diffs to define application meaning that belongs in workstreams, capabilities, behavior, tests, security, or observability.

## Default flow

Prefer this sequence unless the task is already narrowly scoped:

1. apply `ai-first-saas` interpretation when broad input involves delegated work, agents, policy-bound decisions, approvals, supervision, audit, learning, or outcomes
2. apply `core-saas-foundation` for every new SaaS app description so Account/Profile/Settings, Tenant/Customer, Membership/Role/Permission, `/api/me`, backend authorization, audit, admin, and tenant isolation are seeded before app-specific features
3. bootstrap with `app-description-bootstrap` when no usable app-description tree exists yet, including `12-workstreams/`, `15-operating-model/`, and `55-ui/` for generated full-stack AI-first SaaS apps; for minimum/starter/basic/chatbot-like app requests, bootstrap the `minimum starter / not full core` five core workstream starter set from `../docs/minimum-ai-first-saas-app.md` — My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy — with `markdown_response`, durable workstream logs, trace substrate, bootstrap auth/security, governed managed-agent/runtime boundary, capability boundary, and explicit full-core follow-up gaps
4. normalize input with `app-description-input-normalization` when the request is broad, mixed, or ambiguous
5. route input with `app-description-intake-router`
6. model role-authorized user-facing functional agents with `app-description-functional-agent-modeling` when work areas, left-rail agents, prompt intent, skills, reference documents, expertise manifests, tool boundaries/loaders, surfaces, callable capabilities, authority, traces, or tests changed
7. model structured workstream surfaces with `app-description-surface-modeling` when dashboards, forms, tables, charts, decision cards, diffs, audit timelines, detail cards, approvals, workflow status, payloads, reusable placement, allowed actions, states, traces, or rendering tests changed
8. model governed capability contracts with `app-description-capability-modeling` when scope, business outcomes, protected operations, queries, actors, authority, side effects, approval, audit, or exposure surfaces changed
9. update `15-operating-model/` semantics when AI-first concerns changed; use focused AI-first companion skills for object model, agent teams, policy/governance, decision cards, audit traces, UI surfaces, and outcomes as needed
10. update behavior with `app-description-behavior-specification`
11. update tests with `app-description-test-specification`
12. run `app-description-change-impact` to identify cross-layer and realization implications
13. update security with `app-description-auth-security` when security semantics change; preserve the mandatory foundation in every app description
14. update observability with `app-description-observability` when observability semantics change; preserve audit requirements in every app description
15. update UI with `app-description-ui`; for generated full-stack AI-first SaaS apps the browser UI layer is mandatory, not optional polish
16. assess readiness with `app-description-readiness-assessment`
17. realize outputs with `app-generate-app` only when generation is requested or accepted
18. answer review questions with `app-description-change-summary` and `app-description-readiness-summary`


## Layer model

The default internal app-description structure is:
- `00-system/`
- `10-capabilities/`
- `12-workstreams/` for generated full-stack AI-first SaaS apps, including functional agents, internal agents, workstreams, workstream expert bundles, and structured surfaces
- `15-operating-model/` for generated AI-first SaaS apps
- `20-behavior/`
- `30-tests/`
- `40-auth-security/`
- `50-observability/`
- `55-ui/` for generated full-stack AI-first SaaS apps, including `style-guide.md`
- `60-generation/`
- `70-traceability/`
- `80-review/`

Use the architecture and maintenance-flow docs as the canonical reference for layer responsibilities and update order.

## Core rules

1. The app description is the source of truth.
2. Functional agents, workstream expert bundles, and structured surfaces in `12-workstreams/` are the primary generated-app verticals for authenticated consequential work; page/screen hierarchy is subordinate route/deep-link detail.
3. For user-facing generated SaaS changes, verify functional agent ownership, workstream expertise ownership, structured surface contracts, surface actions, and action-to-capability traceability before treating `10-capabilities/`, `55-ui/`, readiness, or generation scope as complete.
4. Capability inventory in `10-capabilities/` is the backend contract layer: record actors/callers, AuthContext/scope, schemas, side effects, idempotency, policy/approval, audit/trace, selected exposure surfaces, and tests before choosing Akka components or tools.
5. Generated code is a projection, not the definition of the app.
6. Humans do not directly edit generated code or internal app-description artifacts.
7. Tests are part of the app description, not only post-hoc verification.
8. AI-first operating-model semantics are first-class for generated SaaS apps; the foundation itself includes delegated admin agents, governance, decisions, traces, and outcomes.
9. The secure SaaS foundation is mandatory for generated apps: no route, agent tool, data access, workflow action, view query, stream, or generated UI is unauthenticated or unauthorized by default.
10. Auth/security and observability are first-class description concerns.
11. Browser UI style guides are first-class UI description concerns; do not invent them during generation.
12. Readiness must be assessed before generation.
13. Localized regeneration is an optimization, not a conceptual requirement.
14. Review should focus on semantic change, not only file churn.

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
