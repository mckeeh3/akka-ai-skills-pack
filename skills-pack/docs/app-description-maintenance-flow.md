# App Description Maintenance Flow

## Purpose

This document defines the default harness operating flow for maintaining the internal app-description artifact system.

It complements `docs/internal-app-description-architecture.md` by defining **how** the layers should be updated over time.

Reference examples:
- canonical SaaS Foundation App description: the target project `app-description/README.md` plus `docs/core-ai-first-saas-foundation.md`
- SaaS Foundation App templates under `templates/ai-first-saas-core-app/app-description/**` for source-controlled cross-linking examples

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

1. bootstrap or repair the internal app-description tree if no usable root exists yet, creating the secure SaaS foundation capability, governed runtime agent foundation, AI-first operating model, behavior, tests, auth/security, observability, and mandatory UI surfaces; for starter/basic/chatbot-like generated SaaS requests, represent the built-in `SaaS Foundation App` domain (My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy) rather than a generic chatbot or separate baseline app
2. normalize the user input when it is broad, mixed, or ambiguous
3. intake and route the user input
4. identify impacted description layers by reconciling against the existing workstream graph rather than generating parallel structures
5. for generated full-stack SaaS user-facing changes, update or verify `12-workstreams/` ownership first: functional agents, role-specific dashboard surfaces, attention items, human surface graph nodes/edges, internal-agent support and internal workstream agent graph where relevant, durable workstreams, workstream icon descriptors, workstream expert bundles under `workstream-expertise/**`, structured surfaces, surface actions, and action-to-capability/governed-tool candidates
6. update `10-capabilities/` with governed capability contracts and governed-tool definitions before implementation choices: governed-tool ids, actors/callers, AuthContext/scope, schemas, side effects, idempotency, policy/approval, audit/trace, exposure surfaces, tests, and links back to source functional agents/surfaces/actions or internal-agent graph nodes for user-facing/internal exposure
7. update `12-workstreams/` again if capability modeling changes surface payloads, actions, authority, governed-tool mapping, or traceability; capability and surface modeling may iterate, but neither layer is complete without the other for user-facing actions
8. update `15-operating-model/` for generated AI-first SaaS operating semantics
9. update behavior-level meaning
10. update verification expectations
11. update auth/security, preserving explicit default-deny semantics for every route, tool, data access path, workflow action, view query, stream, consumer, timer, and generated UI action
12. update observability, preserving AdminAuditEvent and trace requirements for identity, Membership/role, support-access, billing-boundary, data-access, policy, approval, `PromptAssemblyTrace`, `SkillLoadTrace`, `AgentWorkTrace`, and consequential AI/tool activity
13. update mandatory UI descriptions in `55-ui/`, including agent catalog/detail, prompt governance, skill governance, skill manifest, tool permission, editing agent proposal, trace surfaces, workstream icon rendering/interactions, and `style-guide.md`, as browser realization of `12-workstreams/` and `10-capabilities/` for generated full-stack AI-first SaaS
14. update traceability and impact understanding
15. assess readiness
16. when realization planning artifacts already exist, reconcile affected specs/backlogs/pending tasks before more coding
17. either stop at description maintenance or proceed to generation
18. answer review questions through summaries

## Change-only flow

When the user is not explicitly asking for realization, use this flow:

### Step 0. Bootstrap if needed
Use `app-description-bootstrap` when no usable `app-description/` root exists yet. If the user asks for a starter, minimum app, basic app, or initial chatbot-like generated SaaS, bootstrap an authoritative tree for the SaaS Foundation App domain rather than requiring a separate generated baseline. The tree is usable when it captures the five foundation workstreams, selected AuthContext, backend capability boundaries for actions/tools, durable workstream logs, audit/work traces, denial behavior, and the extension seams for business-specific domains.

### Step 1. Normalize when needed
Use `app-description-input-normalization` when the input is broad, mixed, or ambiguous.

### Step 2. Route
Use `app-description-intake-router` to determine:
- description-only vs generation intent
- candidate affected layers
- whether clarification is needed

### Step 3. Update workstream and surface ownership for generated SaaS user-facing changes
Use `app-description-functional-agent-modeling` and `app-description-surface-modeling` when the request changes a dashboard, portal, work queue, admin console, command center, agent/chat area, browser action, approval, decision, audit timeline, workflow status, form, table, or other user-facing work area.

Capture or verify:
- source `12-workstreams/functional-agents.md` ownership, including workstream icon metadata (`WorkstreamIconDescriptor` semantics: stable icon id, visual hint derived from workstream responsibility, accent color token, tooltip, aria label, optional asset reference; rendered by the shell as approved SVG/icon-library artwork or semantic SVG fallback, not letters);
- role-specific dashboard surfaces for each affected actor/AuthContext and the attention items they must surface;
- the human surface graph: dashboard trunk, surface nodes, action edges, system-message surfaces, trace links, denial/recovery edges, and deep-link/open-workstream edges;
- the internal workstream agent graph when delegated/internal work is affected: virtual dashboard attention, worker nodes, governed-tools used, escalation rules, result/proposal return, and human attention items created;
- workstream expertise ownership under `12-workstreams/workstream-expertise/<functional-agent-id>.md` for each LLM-enabled functional agent, including prompt intent, governed skills, reference documents, compact expertise manifest, dashboard/surface graph summary, capability/governed-tool map, tool boundary, denials, user-help examples, traces, governance owner, default-content/upgrade policy, and tests;
- structured surface ids, payload/action candidates, states, traces, and tests;
- surface action-to-capability/governed-tool candidates, including governed surface-request actions such as `open_workstream` for buttons, links, icons, cards, rows, and status panels that open protected surfaces/workstreams;
- which changes are browser realization details for `55-ui/` rather than application meaning, including icon rendering while `12-workstreams/` owns icon semantics.

### Step 4. Update capabilities when scope changed
Use `app-description-capability-modeling` when the request changes business scope, actors, intended outcomes, protected operations, queries, or exposure surfaces.
For AI-first apps, connect capabilities to durable goals, delegated work, agent/team responsibilities, governance boundaries, decision surfaces, audit needs, and outcome loops.

Each affected capability should record enough backend contract detail to prevent generation-time invention:
- capability id/name and class;
- governed-tool ids/names and whether each governed-tool is exposed as a browser-tool, agent-tool, MCP-tool, workflow-tool, timer-tool, consumer-tool, or internal-tool;
- actors/callers and AuthContext with tenant/customer scope, role, permission, or named capability grants;
- typed inputs/outputs, validation, safe denial/error shape, redaction, idempotency, and correlation expectations;
- data access, side effects, policy/approval/escalation rules, and autonomy level;
- audit/work-trace obligations;
- selected exposure surfaces such as UI action, HTTP/gRPC API, agent-tool, MCP-tool, workflow step, view/query, timer-tool, consumer-tool, or internal-only method;
- linked success, validation, forbidden, tenant-isolation, idempotency, audit, approval, and surface-specific tests.

For user-facing generated SaaS capabilities, record the source functional agent, role-specific dashboard or surface/action, governed-tool mapping, and surface-to-capability map entry, or explicitly mark the capability internal-only. For internal delegated work, record the internal workstream agent graph node and service/AuthContext authority basis.

### Step 5. Update operating model when agentic semantics changed

Update `15-operating-model/` when the request changes:
- objectives, success criteria, constraints, or definitions of done
- delegated work versus retained human authority
- agent/team responsibilities, tools, data access, memory, thresholds, or escalation rules
- policies, clauses, guardrails, permissions, approval gates, or governed policy changes
- recommendations, decisions, exceptions, evidence, risk, confidence, impact, alternatives, or precedents
- work traces, decision traces, policy invocations, feedback, learning, replay/simulation, or outcome metrics

Route to focused AI-first companion skills only for the affected concerns; do not duplicate Akka implementation guidance in app-description artifacts.

### Step 6. Update behavior
Use `app-description-behavior-specification` to update the app's meaning.

Behavior follows the operating model and the secure SaaS foundation because:
- tests need something to verify
- security needs something to protect
- observability needs something meaningful to expose
- agentic work needs concrete transitions, pause/resume points, and exception semantics

### Step 7. Update tests
Use `app-description-test-specification` to make the behavior explicit and verifiable.

For generated SaaS apps, the test layer must retain baseline tenant isolation, forbidden access, disabled-user, role/scope denial, `/api/me`, audit, support-access, billing-boundary, idempotency, and frontend secret-boundary tests even when the current change is app-specific.

### Step 8. Analyze change impact
Use `app-description-change-impact` to determine:
- which additional layers must move
- which traceability artifacts must update
- whether readiness should be reassessed
- whether later regeneration can stay localized

### Step 9. Update production-readiness layers
Update as needed:
- `app-description-auth-security`
- `app-description-observability`

These are not optional polish layers.
They are part of the app definition. Missing secure SaaS foundation semantics must block readiness or generation rather than becoming assumptions.

### Step 10. Update mandatory UI

For generated full-stack AI-first SaaS, update `55-ui/` after `12-workstreams/` owns the relevant functional agents, workstream icon descriptors, surfaces, surface actions, and surface-to-capability traceability, and after capability contracts are clear enough to describe browser action authority. Preserve the My Account lower-left signed-in user tile launcher; do not duplicate My Account in the top rail.
Prefer goal-to-execution, command center, decision-card, governance/learning, digest, and audit/trace surfaces over record-management navigation. Conventional list/form routes can exist as implementation details, but structured workstream surfaces remain primary for generated AI-first SaaS.
Keep the style-selection rule below in force.

### Step 11. Update readiness
Use `app-description-readiness-assessment` to reflect the new current state.

### Step 12. Respond with review summaries when useful
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
- if `ready-with-assumptions`, proceed only for a named narrowed scope whose assumptions are explicit, accepted, non-runtime, and do not affect protected capabilities, API/UI action wiring, agent/provider calls, audit/work traces, tests, or local validation
- if `ready`, proceed

When the current scope is the SaaS Foundation App, generation or maintenance should update that built-in domain in place and preserve explicit follow-up work for any intentionally deferred foundation-domain behavior. Do not describe a separate baseline app or a parallel generated app unless the user explicitly asks for one.

### Step 4. Generate
Use `app-generate-app`.
Generation must remain subordinate to the current description.

### Step 5. Run and evaluate
For feature-bearing generated app work, run tests and the local Akka/API/UI/manual-smoke path needed to prove the named scope works. If validation cannot run, record it as incomplete and keep affected runtime features blocked or outside the completion claim. Manual evaluation artifacts supplement, not replace, real local runtime behavior.

### Step 6. Summarize results
Use generation and readiness summaries to explain what happened.

## Layer update order

When multiple layers are affected, prefer this order:

1. `12-workstreams/` first for generated full-stack SaaS user-facing changes: functional agents, workstream icon descriptors, structured surfaces, surface actions, and action-to-capability candidates
2. `10-capabilities/` for governed backend contracts, including source functional agent/surface/action links or explicit internal-only declarations
3. `12-workstreams/` again when capability contract details alter payloads, actions, authority, traces, or tests
4. `15-operating-model/` for generated AI-first SaaS apps
5. `20-behavior/`
6. `30-tests/`
7. `40-auth-security/`
8. `50-observability/`
9. `55-ui/` for generated full-stack AI-first SaaS browser realization, including `style-guide.md`
10. `70-traceability/`
11. `00-system/readiness-status.md`
12. `60-generation/` derived generation notes
13. `80-review/` optional summaries

This keeps workstream/surface ownership and governed capability contracts ahead of derived UI and implementation layers. Capability and surface modeling may iterate together, but `55-ui/` and generation are not ready until the `12-workstreams/`/`10-capabilities/` contract is coherent.

## Change-impact rule

Every description change should trigger a mental or explicit change-impact pass.

The harness should ask:
- which affected workstreams, role-specific dashboards, attention items, human surface graph nodes/edges, workstream icon descriptors, workstream expert bundles, internal agents, internal workstream agent graph nodes, structured surfaces, surface actions, and surface-to-capability maps changed?
- which capabilities changed?
- which governed-tools were added, changed, reused, deprecated, split, or remapped to browser-tool, agent-tool, internal-tool, workflow-tool, timer-tool, consumer-tool, or MCP-tool exposure?
- did any capability contract fields change: actors/callers, AuthContext/scope, schemas, side effects, idempotency, policy/approval, audit/trace, exposure surfaces, or tests?
- which behavior artifacts changed?
- which AI-first operating-model artifacts changed: goals, delegated work, retained authority, agents, governed runtime behavior artifacts, policies, approvals, decisions, exceptions, traces, learning, or outcomes?
- did the change affect a workstream expert bundle, reference documents, `AgentDefinition`, `PromptDocument`, `SkillDocument`, `AgentSkillManifest`, compact skill/reference manifest entries, `ToolPermissionBoundary`, behavior editing agent proposals, authorized `readSkill(skillId)`/reference loading, `PromptAssemblyTrace`, `SkillLoadTrace`, reference-load trace, or `AgentWorkTrace`?
- which tests now need updates?
- are there new or changed security implications, especially permission enforcement and authority boundaries?
- are there new or changed observability implications, especially work traces, decision traces, policy invocations, audit events, and outcome metrics?
- is a style guide selected and do supervision, decision, governance, digest, agent catalog/detail, prompt/skill governance, manifest/tool permissions, edit-agent proposal, or audit/trace surfaces need to change?
- does readiness status change?
- would generation scope be localized or broad?
- do any existing specs, backlogs, task briefs, or pending tasks need to be updated, blocked, deferred, or superseded?

## Readiness rule

Readiness is a maintained state, not a one-time milestone.

The harness should reassess readiness after material description changes, especially when they affect:
- capability contract completeness: actors/callers, AuthContext/scope, schemas, side effects, idempotency, approval, audit, exposure surfaces, and tests
- workstream expertise completeness: expert bundles, governed prompts/skills/references, compact manifests, authorized loaders, tool boundaries, trace obligations, UI/governance surfaces, default-content generation assets, and expertise tests
- state transitions
- failure semantics
- delegated authority, approvals, exceptions, or policy controls
- audit trace and outcome requirements
- auth/security
- observability
- operational tests

## UI style-selection rule

For generated full-stack AI-first SaaS, the app description or specs must contain a selected web UI style guide before web UI implementation or generation starts. If style is missing, add or update a `category: ui` entry in `specs/pending-questions.md` using `docs/web-ui-style-guide.md` rather than silently choosing visual styling. This blocks web UI work until answered; unrelated backend description or implementation work can continue.

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

## Broad vs localized realization rule

When implementation or generation is requested, the harness should decide between:
- localized extension/repair of the maintained runnable app
- broad regeneration or replacement of a named generated scope

Prefer localized extension/repair for existing repositories, especially this SaaS Foundation App repository and downstream forks. Use broad regeneration only when the user explicitly requests it, the scope is named, and the affected app/foundation files are safe to replace.

Localized realization must never preserve stale behavior that conflicts with the current description.

## Completion rule for a change request

A description change request is complete when:
- the relevant authoritative layers are updated
- `15-operating-model/` captures goals, delegated work, human authority, policies, decisions, traces, and outcomes sufficiently for the generated AI-first SaaS change
- linked verification expectations are updated, including mandatory secure foundation tests for generated SaaS apps
- linked security and observability expectations are updated when needed
- readiness has been reassessed or intentionally deferred
- the selected style guide is recorded or a pending style-selection question blocks UI realization
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
- treating auth/security or observability as later afterthoughts
- using generated code as the primary explanation of what changed
- leaving readiness stale after major description changes
- silently burying semantic uncertainty inside generation results
- converting delegated operational work into CRUD/page surfaces before modeling goals, authority, policies, decisions, traces, and outcomes
- treating a chatbot or prompt as a substitute for durable goals, plans, approvals, and auditability

## Recommended next-doc usage

Use this document together with:
- `docs/description-first-application-doctrine.md`
- `docs/internal-app-description-architecture.md`

The doctrine defines the why.
The architecture doc defines the artifact structure.
This document defines the maintenance flow.