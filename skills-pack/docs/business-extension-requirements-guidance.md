# Business Extension Requirements Guidance

Use this document for Stage 2 business-extension transformation: when an agent is creating, normalizing, reviewing, or decomposing accepted business-focused input into current intent, goals, or extension plans for a product built on the SaaS Foundation App.

This document assumes Stage 1 business-intent capture has already produced accepted or draft app-description input under `docs/input/**`. For Stage 1 interviews with SMB owners or representatives, use `docs/business-intent-interview-process.md`, `business-intent-interview`, and `business-intent-to-app-input`.

This document describes how to use the Akka AI skills pack as a planning and intent substrate. It is not an implementation checklist for one Akka component. Implementation skills should be selected later, after the business extension is expressed as workstreams, governed capabilities, surfaces, agents, policies, traces, tests, and realization slices.

## Purpose of the Skills Pack

The skills pack guides harness agents that maintain and extend secure AI-first SaaS applications on Akka. Its primary target is the runnable SaaS Foundation App: an Akka Java SDK backend plus React/Vite/TypeScript frontend with secure SaaS foundation behavior and five built-in workstreams.

For business extensions, the pack's job is to help agents convert product intent into traceable application artifacts:

```text
business input
  -> normalized current intent
  -> domain and workstream model
  -> role-specific attention and surface graph
  -> governed capabilities and tools
  -> agent and policy boundaries
  -> Akka/frontend realization plan
  -> specs, pending questions, pending tasks, tests, and validation evidence
```

The installed `.agents/skills` directory is only harness guidance. The target application state stays in the project workspace:

```text
docs/input/          # human-authored PRDs, notes, issues, test findings
app-description/     # current-intent graph
specs/               # plans, backlogs, questions, task briefs, evidence
src/                 # Akka Java SDK backend source
frontend/            # React/Vite/TypeScript frontend source
docs/                # product and extension documentation
```

Do not write product requirements, app descriptions, specs, or generated app source under `.agents/skills` or `skills-pack/**` unless the task is explicitly skills-pack maintenance.

## Foundation App Assumptions

Business extensions are additive to the SaaS Foundation App. They should preserve the foundation rather than replace it.

The foundation includes:

- WorkOS/AuthKit browser authentication.
- Organization-facing account/workspace concepts backed by internal tenant isolation.
- Backend-owned authorization state, memberships, roles, permissions, and selected `AuthContext`.
- Email-invite onboarding and reusable Resend-backed email delivery boundaries.
- `/api/me` for browser-safe current user, membership, role, and context state.
- Backend authorization on every protected route, component command, view query, stream, workflow action, timer, consumer, endpoint, and agent tool.
- Durable audit/work traces for protected actions, policy decisions, agent work, tool use, and provider interaction.
- Governed runtime agent behavior: managed agent definitions, prompts, skills, references, manifests, tool permission boundaries, model configuration, and traces.
- React workstream UI surfaces with backend-backed attention, actions, denials, and traces.

Business requirements must not weaken these assumptions. If input appears to ask for self-registration, frontend-only authorization, direct provider calls from the browser, unscoped data access, untraced agent actions, or model-less runtime substitutes for model-backed behavior, record the conflict and route it to a pending question or security/intent repair.

## Required Business Extension Shape

For a business extension, start by identifying the business domain and its operational workstreams. Do not start with pages, database tables, endpoint lists, or Akka component classes.

A good extension requirement should answer:

- What business outcome is the extension responsible for?
- Which Organization, tenant, customer, user, or service actor owns or participates in the work?
- Which roles are authorized to view, decide, approve, correct, audit, or administer it?
- Which workstreams are needed, and who is the owner functional agent for each workstream?
- What needs a human's attention in each workstream?
- What surfaces, decisions, forms, queues, dashboards, traces, and result panels are required?
- What governed capabilities and executable governed-tools support those surfaces and agents?
- Which actions are human-backed browser-tools, AI-backed agent-tools, internal-tools, workflow steps, timers, consumers, or service APIs?
- Which state objects, policies, audit/work traces, metrics, tests, and validation paths prove the behavior?

Use the user's actual domain name in extension artifacts. If the product domain is not known yet, use `domain-specific`; do not reuse historical example names as placeholders.

## Current-Intent Graph Targets

The app-description current-intent graph is the canonical place for accepted product meaning. Business-extension agents should add or update only the affected nodes.

Typical extension paths:

```text
app-description/extensions/<domain>/
app-description/domains/<domain>/
specs/extensions/<domain>/
docs/extensions/<domain>/
frontend/src/extensions/<domain>/
src/main/java/ai/first/domain/business/<domain>/
src/main/java/ai/first/application/business/<domain>/
src/main/java/ai/first/api/business/<domain>/
src/test/java/ai/first/business/<domain>/
```

Use the target project's existing extension conventions when they differ, but keep the same ownership model:

- `domain.md` owns domain purpose, boundaries, non-goals, capabilities, and state responsibilities.
- `capabilities/*.md` own product-level authority contracts and governed-tool inventories.
- `data-state/*.md` own durable state, lifecycle, invariants, retention, and trace obligations.
- `workstreams/<workstream>/workstream.md` owns the workstream responsibility and readiness.
- `workstreams/<workstream>/access.md` owns roles, membership, authorization, context, denials, and visibility.
- `workstreams/<workstream>/behavior.md` owns the operational behavior, attention model, decisions, exceptions, and outcomes.
- `workstreams/<workstream>/surfaces/*.md` own dashboard, surface graph, forms, tables, cards, queues, timelines, and action edges.
- `workstreams/<workstream>/agents/*.md` own functional-agent and internal-agent responsibilities, tool boundaries, handoffs, stops, and escalations.
- `workstreams/<workstream>/tools/*.md` own workstream-specific governed-tool bindings and actor adapters.
- `workstreams/<workstream>/policies/*.md` own approval, threshold, denial, risk, and governance rules.
- `workstreams/<workstream>/traces/*.md` own audit/work trace semantics and evidence needs.
- `workstreams/<workstream>/tests/*.md` own acceptance, negative, security, runtime, and regression expectations.
- `workstreams/<workstream>/realization/*.md` owns Akka components, API contracts, frontend routes, and validation mapping.

Current intent should describe the intended current state. Do not preserve conversation chronology or obsolete alternatives unless writing a changelog or migration note.

## Workstream-Centered Planning Rule

For broad product input, follow this order:

```text
secure SaaS foundation assumptions
  -> affected domain and workstream inventory
  -> attention categories: what needs my attention?
  -> role-specific dashboard contracts
  -> human surface graph nodes and action edges
  -> internal workstream agent graph
  -> governed capabilities and governed-tools
  -> policy, approval, and authorization rules
  -> trace, audit, and outcome evidence
  -> Akka substrate and frontend realization mapping
  -> specs, questions, backlog, and task briefs
```

This order matters because workstreams express how humans and agents actually operate. A table, route, endpoint, queue, or Akka entity may be necessary, but it should be selected after the workstream, authority, and capability contracts are clear.

Planning output is implementation-ready only when each vertical slice can be traced as:

```text
workstream attention category
  -> role-specific dashboard state
  -> surface graph node/action edge
  -> governed capability and governed-tool
  -> exposure channel
  -> selected Akka substrate
  -> state, event, notification, or projection effect
  -> audit/work trace
  -> tests and local validation path
```

If a link is missing, create a bounded pending question or planning task instead of guessing.

## Goals and Requirements Quality Bar

Business goals should be durable enough to drive implementation. Prefer requirements that state the outcome, actor, authority, evidence, and validation path.

Weak requirement:

```text
Add supplier risk management.
```

Stronger requirement:

```text
Procurement Managers can supervise a Supplier Risk workstream for the selected Organization. The workstream highlights suppliers with blocked onboarding, expired certifications, unresolved policy exceptions, or high-risk changes. Authorized users can open supplier risk details, request remediation, approve or reject exceptions, and view the audit/work trace for each decision. The Supplier Risk Agent may summarize evidence and propose next actions, but it cannot approve exceptions unless its governed tool boundary and approval policy explicitly allow that action.
```

Before moving to backlog or code, a requirement should identify or explicitly defer:

- domain and workstream ownership;
- authorized roles and selected `AuthContext`;
- attention categories and dashboard behavior;
- surface graph nodes and actions;
- governed capability and governed-tool inventory;
- human-backed, AI-backed, and internal actor adapters;
- state, side effects, idempotency, events, notifications, and projections;
- policies, approvals, denials, redaction, retention, and trace obligations;
- agent behavior, tool boundaries, provider assumptions, and fail-closed paths;
- acceptance, negative, security, and runtime validation tests.

## Agent and AI Guidance

Treat agents as role-authorized participants inside workstreams, not as omnipotent assistants.

For every model-backed business behavior, record:

- the functional agent or internal worker responsibility;
- the managed agent definition and owner/steward;
- prompt, skill, reference, and workstream expertise requirements;
- allowed governed-tool ids and `ToolPermissionBoundary`;
- approval requirements and authority limits;
- data access and redaction rules;
- failure, denial, handoff, and escalation behavior;
- prompt assembly, skill/reference load, tool invocation, model invocation, and work trace expectations;
- tests proving allowed actions, denied actions, and missing-provider fail-closed behavior.

Prompt text, skill text, and examples cannot grant authority. Authority comes from backend capabilities, roles, policies, tool boundaries, approval gates, and tenant/customer scope.

Use request-based Akka `Agent` for immediate user-facing workstream turns. Use Akka `AutonomousAgent` only when durable internal/background model-driven work is justified by task lifecycle, dependencies, snapshots, notifications, delegation, handoff, cancellation, or independent failure semantics. Use deterministic workflows for ordered business processes, approval pauses, retries, compensation, and timeouts.

## Recommended Skill Routing

Start with the smallest relevant skill set. For business-focused requirements and intent, these are the usual entry points:

- `ai-first-saas` for high-level product intent and secure AI-first SaaS interpretation.
- `agent-workstream-apps` for workstream-centered application modeling.
- `core-saas-foundation` when foundation assumptions, identity, tenancy, authorization, onboarding, admin, audit, or managed-agent foundation are in scope.
- `app-descriptions` and its focused app-description skills when creating or updating current-intent graph nodes.
- `app-description-input-normalization` for messy product input, notes, or discussions.
- `app-description-intake-router` for deciding which current-intent nodes are affected.
- `app-description-functional-agent-modeling` for workstream agents and authority boundaries.
- `app-description-surface-modeling` for dashboards, surface graphs, decision cards, forms, queues, and action edges.
- `app-description-capability-modeling` for capabilities, governed-tools, schemas, authorization, idempotency, side effects, and traces.
- `app-description-auth-security` for roles, permissions, tenant/customer scope, denials, approval gates, and frontend secret boundaries.
- `app-description-behavior-specification` and `app-description-test-specification` for behavior and tests.
- `akka-prd-to-specs-backlog` for turning PRDs or broad requirements into specs, backlogs, pending questions, and pending tasks.
- `akka-change-request-to-spec-update` or `akka-revised-prd-reconciliation` for incremental changes to existing intent.
- `akka-solution-decomposition` only after workstream and capability contracts are clear enough to choose Akka substrates.

Implementation skills such as entity, workflow, view, consumer, timer, endpoint, agent, autonomous-agent, and web-ui skills should be selected only after the extension has a traceable vertical contract.

## Required Planning Artifacts

For a new or materially changed business extension, produce or update these artifacts as appropriate:

- normalized product input summary under `docs/input/` or a traceable source reference;
- app-description current-intent graph nodes for the affected domain, workstreams, capabilities, surfaces, agents, policies, traces, tests, and realization;
- specs that cite the current-intent nodes they realize;
- pending questions for unsafe ambiguities;
- pending tasks or task briefs that execute one bounded vertical slice at a time;
- validation expectations that distinguish `described`, `surface-ready`, `capability-ready`, `backend-ready`, `frontend-rendered`, `api-smoked`, `browser-smoked`, `manual-ready`, `runtime-ready`, and `production-ready`.

Do not claim a user-visible runtime feature is complete from description, generated code shape, fixture-only tests, deterministic demos, or frontend-only rendering. Runtime-ready means the intended local Akka/API/UI path works at the stated scope with authorization, state, side effects, traces, and denials.

## Common Pitfalls

Avoid these patterns when creating business requirements, intent, or goals:

- Decomposing broad input into CRUD resources before modeling workstreams and attention.
- Treating routes and pages as product architecture instead of realization details.
- Adding a chatbot panel instead of modeling functional agents, surfaces, capabilities, and traces.
- Letting AI prompts imply authority that backend tool boundaries do not grant.
- Omitting tenant/customer scope, selected `AuthContext`, or role-specific denials.
- Treating frontend gating as authorization.
- Creating untraced agent actions, internal jobs, provider calls, or policy decisions.
- Copying skills-pack examples into app source wholesale.
- Writing product intent into `.agents/skills` instead of the project app-description/specs.
- Expanding a business extension by editing core foundation behavior when an additive domain hook would preserve mergeability.

## Discovery Questions for Ambiguous Input

Ask or queue bounded questions when the answer changes security, data ownership, authority, or runtime shape. Useful prompts include:

- Which business domain owns this behavior?
- Which roles can see, decide, approve, administer, or audit it?
- Which Organization, tenant, customer, or data scope applies?
- What should appear in the workstream when nothing requires attention?
- Which conditions create attention items, and who receives them?
- What can the functional agent do directly, what can it only propose, and what must always require human approval?
- Which actions must be reversible, idempotent, or retained for audit?
- What data is sensitive, redacted, retained, exported, or tenant-isolated?
- Which external providers are required, and what is the fail-closed behavior when configuration is absent?
- What local runtime path proves the feature works?

Queue questions instead of guessing only when the ambiguity would make the compiled intent unsafe or materially wrong. Continue safe, unrelated normalization work when traceability remains clear.

## Primary References

Read these pack docs when deeper guidance is needed:

- `docs/skills-pack-user-guide.md`
- `docs/generated-saas-canonical-doctrine.md`
- `docs/ai-first-saas-application-architecture.md`
- `docs/current-intent-model.md`
- `docs/incremental-intent-processing.md`
- `docs/intent-to-realization-flow.md`
- `docs/requirements-to-workstream-development-process.md`
- `docs/agent-workstream-application-architecture.md`
- `docs/workstream-contract.md`
- `docs/workstream-attention-contracts.md`
- `docs/structured-surface-contracts.md`
- `docs/capability-first-backend-architecture.md`
- `docs/governed-agent-substrate.md`
- `docs/minimum-implementable-workstream-slice.md`

Use `skills/README.md` as the routing map for selecting focused skills after the intent shape is known.
