---
name: app-description-readiness-assessment
description: Assess whether the current app description is sufficiently complete and unambiguous for reliable generation, testing, and manual evaluation, and recommend whether to continue description work or proceed to app realization.
---

# App Description Readiness Assessment

Use this skill when the harness needs to decide whether the current app description is complete enough to realize the app.

This skill does not primarily change the app description.
It evaluates the current description state and determines whether generation would be responsible, premature, or acceptable with assumptions.

## Lifecycle classification

- Phase: interview.
- Kind: readiness assessor.
- Family: app-description.
- Living-graph contract: readiness evaluates the app-description current-intent graph for semantic sufficiency across workers, execution harnesses, actor adapters, governed tools, capabilities, behavior, tests, security, observability, UI, and realization links.
- Build/compile handoff: only route to generation or implementation when the graph can safely proceed through `../docs/app-description-to-code-compile-contract.md`; otherwise return to focused description skills.

## Goal

Assess whether the current application description is sufficiently complete to support reliable:
- code generation
- test generation
- app execution
- manual testing and evaluation

The result should tell the harness and the user whether to:
- continue description work
- proceed with assumptions called out explicitly
- proceed to app generation now

## Required reading

Read these first if present:
- target project path: AGENTS.md
- `../README.md`
- `../docs/intent-compiler.md`
- `../docs/current-intent-model.md`
- `../docs/incremental-intent-processing.md`
- `../docs/intent-compiler-skill-contracts.md`
- `../docs/app-description-skill-output-contracts.md`
- `../docs/ai-first-saas-application-architecture.md`
- `../docs/requirements-to-workstream-development-process.md` for readiness gates across workstreams, attention, dashboards, surface actions, autonomous tasks, notifications/projections, and traces
- `../docs/capability-first-backend-architecture.md` for capability contract completeness criteria
- `../docs/agent-workstream-application-architecture.md` for functional-agent, workstream shell, and structured-surface readiness gates
- `../docs/workstream-expertise-model.md` for per-functional-agent workstream expert bundle readiness gates
- `../core-saas-foundation/SKILL.md` for mandatory secure SaaS foundation readiness criteria
- `../app-description-intake-router/SKILL.md`
- `../app-description-functional-agent-modeling/SKILL.md`
- `../app-description-surface-modeling/SKILL.md`
- `../app-description-behavior-specification/SKILL.md`
- `../app-description-test-specification/SKILL.md`
- `../app-description-auth-security/SKILL.md`
- `../app-description-observability/SKILL.md`
- `../app-description-ui/SKILL.md`
- `../ai-first-saas/SKILL.md` when delegated work, agents, decisions, governance, supervision, audit, or outcomes are in scope
- `../docs/web-ui-style-guide.md`

## Use this skill when

The input sounds like:
- "is the description ready?"
- "can we generate the app now?"
- "what is still missing before generation?"
- "assess whether this is sufficient for implementation"
- "ok, now generate the code and run the app"

Use it before generation when the harness must decide whether the current description is:
- incomplete
- sufficient with assumptions
- sufficiently complete

## Core operating rule

Readiness is about **semantic completeness**, not about whether code could be guessed.

A description is ready only when it is clear enough that generation is likely to preserve intended behavior without hiding major unresolved decisions.

This skill should resist premature generation when important agent workstream, role-specific dashboard attention, human surface graph, internal workstream agent graph, workstream expertise, bounded workstream tool catalog, governed-tool, actor-adapter mapping, structured surface action, confirmed human chat tool-plan confirmation, AI agent-tool boundary, autonomous task, notification/projection, capability contract, behavior, test, security, observability/trace-source, in-scope frontend/UI, mandatory secure SaaS foundation, or AI-first operating-model semantics are still undefined.

## Readiness dimensions

Use `../docs/app-description-skill-output-contracts.md` as the shared readiness contract. Assess semantic completeness for the declared scope across secure SaaS foundation, functional-agent workstreams, structured surfaces, capabilities/governed-tools, behavior, tests, auth/security, observability/traces, frontend/UI, realization maps, assumptions, and pending questions.

Apply these gates strictly:
- `SaaS Foundation App` readiness means the built-in foundation domain and its five workstreams are sufficiently described for the requested maintenance or extension scope.
- Business-domain extension readiness requires clear extension seams back to the SaaS Foundation App: AuthContext, roles/capabilities, tenant/customer scope, workstream placement, governed capabilities/tools, traces, UI surfaces, and tests.
- Narrower scopes must name omitted areas and cannot report those omitted areas ready.

## Allowed outcomes

Return exactly one of these states:

### `not-ready`
Use when important semantics are still too incomplete or ambiguous for responsible generation.

### `ready-with-assumptions`
Use only for a narrowed realization step when the remaining assumptions are non-runtime, explicitly listed, low-risk, and do not affect backend behavior, API contracts, auth/security, tenant isolation, agent/provider binding, governed-tools, audit/work traces, UI action wiring, tests, or local validation.

### `ready`
Use when the description is sufficiently complete for reliable generation, local runtime validation, and downstream manual evaluation.

## Standard readiness output shape

Use this concise report shape from `../docs/app-description-skill-output-contracts.md`:

1. overall state: `ready`, `ready-with-assumptions`, `not-ready`, or `blocked`;
2. declared scope label: SaaS Foundation App maintenance/extension, business-domain extension, app-specific feature, or another named scope;
3. blocking gaps by current-intent graph area;
4. acceptable assumptions;
5. unsafe assumptions/questions;
6. recommendation and next skill sequence.

Do not paste long graph inventories into the response unless the user asks for a detailed audit.

## Assessment rules

### 1. Prefer explicit gaps over silent optimism
If a critical area is underspecified, say so directly.
Do not mark the description ready just because generation is technically possible.

### 2. Weight missing production concerns appropriately
Missing secure SaaS foundation, agent workstream model, role-specific dashboard contracts, human surface graph nodes/edges, internal workstream agent graph delegation, governed-tool contracts, workstream tool catalogs, actor-adapter mappings, confirmed human chat plan/confirmation semantics, notification/projection semantics, autonomous task lifecycle semantics, capability contracts, auth/security, observability, operating-model, or AI-first UI details must block readiness when generation would otherwise invent Account/Profile/Settings, Tenant/Customer, Membership/Role/Permission, `/api/me`, backend authorization, audit, tenant isolation, functional agents, internal agents, structured surfaces, workstream shell behavior, actors/callers, AuthContext/scope, schemas, side effects, idempotency, exposure surfaces, browser-tool/human_chat_tool_plan/agent-tool/internal-tool mappings, authority, policies, approval gates, decision evidence, trace obligations, outcome metrics, or supervision surfaces.

### 3. Allow limited assumptions only when localized and non-runtime
`ready-with-assumptions` is valid only when the remaining assumptions are:
- few
- explicit
- low-risk
- unlikely to distort the app's core behavior
- unrelated to runtime completion of named features, protected capabilities, governed tool catalog membership, human chat confirmation, agent/provider calls, UI action wiring, audit/work traces, tests, or local validation

If an assumption would change whether a user-visible/API/workstream feature works through the real local Akka runtime path, the description is `not-ready` for that feature or must be labeled as a narrower scope with executable follow-up tasks.

For browser UI generation, a missing style guide is a blocking UI readiness gap unless the user explicitly defers it with an accepted default recorded in `specs/pending-questions.md` and the affected app-description/spec style-guide artifact.

For generated full-stack AI-first SaaS apps, missing workstream functional-agent, attention/dashboard, expertise, and structured-surface semantics under `domains/<domain>/workstreams/<workstream>/**` are blocking when generation would otherwise invent consequential work areas, expert skills/references/manifests/boundaries, left-rail authorization, workstream behavior, attention counts, surface payloads/actions/states, autonomous task progress/result surfaces, notifications, or User Admin / Agent Admin boundaries. Readiness is also blocked when the frontend plan uses legacy `frontend/src/screens/**`, page-first route tests, or static-resource mechanics as the generated SaaS UI model instead of the canonical `frontend/src/workstream/**` reference and User Admin vertical. User Admin dashboard/list/detail behavior is incomplete if it is fixture-only, API-only, or UI-only instead of fullstack through `user-admin-dashboard`, `user-admin-user-list`, and `user-admin-user-account` backed by scoped backend capabilities and tests.

For AI-first/delegated operations, missing global/domain/workstream operating-model semantics are blocking when generation would otherwise invent authority, policies, approval gates, decision evidence, trace obligations, outcome metrics, or supervision surfaces.

### 4. Treat manual evaluation as a runtime target, not a lower bar
If the user wants an early evaluation build, narrow the scope aggressively but still require the selected scope to run through real local Akka/API/UI paths with Akka component-backed normal runtime state and fail-closed provider/security handling. Do not use mock, fixture, deterministic, simulated, frontend-only, provider-bypass behavior, or missing internal Akka persistence as the normal runtime substitute for a named generated-app feature. Missing runtime behavior remains `not-ready`, blocked, or explicitly outside the narrowed scope.

### 5. Recommend generation proactively when justified
If the description is sufficiently mature, this skill may recommend moving on to generation even when the user has not yet explicitly asked.

## Handoff rules

Route onward as follows:
- if `not-ready`, route to the most relevant missing description skills:
  - `core-saas-foundation`, `app-description-bootstrap`, `app-description-auth-security`, and `app-description-test-specification` when Account/Profile/Settings, Tenant/Customer, Membership/Role/Permission, `/api/me`, backend authorization, audit, tenant isolation, disabled-user, forbidden-access, or foundation test semantics are missing
  - `app-description-functional-agent-modeling` when authenticated consequential work areas, role-authorized left-rail functional agents, attention/dashboard contracts, workstream expert bundles, User Admin, Agent Admin, internal-agent boundaries, traces, or tests are missing
  - `app-description-surface-modeling` when structured surfaces, attention/dashboard surfaces, autonomous task progress/result surfaces, surface schemas, allowed actions, notification/realtime behavior, states, rendering tests, or capability mappings are missing
  - `ai-first-saas` or focused AI-first companion skills when operating-model semantics are missing
  - `app-description-behavior-specification`
  - `app-description-test-specification`
  - `app-description-auth-security`
  - `app-description-observability`
  - `app-description-ui`
- if `ready-with-assumptions`, route to:
  - explicit assumption confirmation if needed
  - then `app-generate-app` if generation is requested or accepted
- if `ready`, route to:
  - `app-generate-app` when realization is requested or accepted

## Example readiness questions

Ask only when necessary:
- "Is repeated submission expected to succeed idempotently or fail?"
- "Are there caller roles or tenant boundaries not yet described?"
- "What operational evidence is required when this flow fails or times out?"
- "Are the current acceptance and regression cases enough to define expected behavior?"

## Anti-patterns

Avoid:
- treating vague behavior as ready because the model can improvise
- ignoring production concerns because they are not code yet
- hiding important assumptions inside a `ready` result
- blocking generation over trivial missing details that do not materially affect correctness
- recommending generation without listing the basis for readiness

## Final review checklist

Before finishing, verify:
- the result uses one of the three allowed states
- scope label was assessed before readiness state selection
- SaaS Foundation App maintenance/extension, when used, names affected foundation workstreams and records explicit follow-up work for any deferred behavior
- business-domain extension readiness preserves AuthContext, capabilities, tenant/customer scope, traces, workstream/surface placement, and tests
- core secure SaaS foundation completeness was assessed explicitly for generated SaaS apps
- missing foundation/security blocks generation or marks the description `not-ready`
- agent workstream application completeness was assessed explicitly for generated full-stack AI-first SaaS apps
- workstream attention, dashboard, notification/projection, and autonomous task surface semantics were assessed explicitly
- missing functional agents, attention/dashboard contracts, workstream expert bundles, bounded tool catalogs, adapter mappings, or structured surfaces for authenticated consequential work blocks generation or marks the description `not-ready`
- missing User Admin or Agent Admin blocks SaaS Foundation App generation/maintenance unless narrower scope is explicit
- AI-first operating-model completeness was assessed explicitly for generated AI-first SaaS
- behavior completeness was assessed explicitly
- test completeness was assessed explicitly
- auth/security completeness was assessed explicitly
- observability completeness was assessed explicitly
- frontend/UI completeness was assessed explicitly as mandatory generated SaaS scope
- remaining assumptions or gaps are listed clearly
- the recommendation is explicit
- the next skill or sequence is named clearly

## Response style

When answering:
- state the readiness result first
- be direct about blocking gaps
- distinguish critical gaps from acceptable assumptions
- keep the recommendation actionable
- if generation is appropriate, say so plainly
