---
name: capability-first-backend
description: Model backend behavior as governed capabilities before choosing Akka components or exposure channels, then route to app-description, decomposition, PRD/backlog, or focused implementation skills.
---

# Capability-First Backend

Use this skill after secure AI-first SaaS interpretation and before selecting entities, workflows, endpoints, agent-tools, MCP-tools, timers, consumers, or UI actions.

This is a routing and framing skill. It does not replace `core-saas-foundation`, `ai-first-saas`, app-description skills, `akka-solution-decomposition`, PRD/spec/backlog skills, or focused Stage 3 component skills.

## Required reading

Read these first when using this skill:
- `../../../AGENTS.md`
- `../README.md`
- `../docs/ai-first-saas-application-architecture.md`
- `../docs/agent-workstream-application-architecture.md` for generated full-stack SaaS app modeling before backend capability design
- `../docs/structured-surface-contracts.md` when workstream surfaces, surface actions, or browser UI actions are in scope
- `../docs/capability-first-backend-architecture.md`
- `../agent-workstream-apps/SKILL.md` when generated SaaS intent has not already produced functional-agent, workstream, and structured-surface context

For generated-app durable AutonomousAgent worker tasks, also read `../docs/autonomous-agent-worker-runtime-pattern.md` before finalizing the capability contract.

Then load only the smallest downstream skill set needed for the selected path.

## Use when

Use this skill for broad product, feature, PRD, spec, backlog, app-description, or implementation-routing work where backend behavior must be shaped before coding.

Especially use it when the request involves:
- operations or queries that need explicit actors, authorization, tenant/customer scope, schemas, side effects, or audit;
- agent-tool or component-tool exposures;
- browser/API/MCP exposure choices;
- approval-gated, long-running, scheduled, or event-reactive work;
- shared semantics across UI actions, APIs, workflows, timers, consumers, and qualified governed-tool exposure channels.

## Do not use when

Do not use this skill as a substitute for:
- `core-saas-foundation` for mandatory SaaS identity, tenancy, authorization, `/api/me`, admin, invitation, audit, and security-test foundation;
- `ai-first-saas` for delegated work, goals/plans, agent teams, governance, decisions, supervision, traces, and outcomes;
- `app-descriptions` when the user is maintaining the authoritative application description;
- `akka-solution-decomposition` when a full Akka component plan must be produced;
- focused Stage 3 skills when the capability contract and component choice are already settled.

## Core rule

For generated full-stack SaaS apps, capabilities sit **below** the agent workstream application model and **above** Akka implementation:

```text
secure SaaS foundation
→ functional/context-area agents
→ durable workstreams
→ typed structured surfaces and actions
→ governed backend capabilities as product-level groupings
→ governed-tools inside those capabilities
→ selected qualified exposure channels
→ Akka components
```

Functional agents, workstreams, and structured surfaces define the user-facing application model. Capabilities define product-level backend abilities or groupings. Governed-tools are the executable semantic operations or queries inside those capabilities and carry the authority/behavior contract behind every surface action, agent-tool, browser-tool, workflow step, API, timer, consumer, or internal call. Do not use capability-first modeling to bypass functional-agent and surface modeling for generated SaaS apps.

A backend capability groups one or more governed-tools:

```text
capability = product ability or grouping
  → governed-tool = executable operation/query
    + actors/callers
    + AuthContext and scope
    + typed inputs/outputs
    + data access and redaction
    + side effects
    + idempotency
    + policy/approval rules
    + audit/trace obligations
    + selected qualified exposure channels
    + tests
```

Agent-tools, browser-tools, HTTP/gRPC/MCP endpoints, workflow-tools, timer-tools, consumer-tools, view queries, and internal component methods are exposure or realization choices for governed-tools. They are not the backend design root.

## Interpretation workflow

### 1. Preserve the mandatory foundation

For generated SaaS apps, verify `core-saas-foundation` has been applied and keep its rules in force while modeling capabilities. The upstream handoff should already identify functional agents, workstreams, and structured surfaces; represent foundation work as protected capabilities behind those surfaces, not as unauthenticated object access. Every protected capability must enforce authenticated account, selected `AuthContext`, active membership, tenant/customer scope, role/permission/capability authorization, backend checks, audit, and tenant-isolation tests.

Prompt text, agent-tool descriptions, frontend navigation, and hidden fields are never authorization controls.

### 2. Preserve workstream and surface context

For generated SaaS apps, first identify or load the upstream workstream model before inventing backend operations:
- role-authorized functional/context-area agents and their tenant/customer scope;
- durable workstreams and retained human authority for each functional agent;
- structured surfaces, payload-producing queries, allowed actions, events, and trace links;
- surface/action placement, reusable functional-agent placement, and denial/recovery states;
- candidate action-to-governed-tool/capability mappings from each surface action, agent-tool, browser-tool, workflow-tool, API call, timer-tool, consumer-tool, or internal-tool.

If this context is missing for a generated full-stack SaaS request, route through `agent-workstream-apps` or record the gap before selecting capabilities or Akka components.

### 3. Inventory capabilities

For each workstream operation, structured surface action, payload-producing query, governed-tool, agent-tool, browser-tool, workflow step, API, timer, consumer reaction, or internal operation, define:
- stable capability id/name in product language for the product ability or grouping;
- stable governed-tool id/name for each executable operation/query inside the capability;
- purpose and business outcome;
- allowed actors/callers: humans, agents, workflows, services, timers, consumers, support roles;
- AuthContext, tenant/customer scope, roles, permissions, and named capability grants;
- input schema, validation, idempotency key, correlation id, and safe defaults;
- output schema, redaction, safe error and denial shapes;
- data reads/writes and tenant/customer filters;
- side effects: state changes, external calls, topics, timers, emails, notifications, workflow starts;
- policy, approval, escalation, risk/confidence/impact thresholds, and autonomy level;
- audit/work-trace fields and retention/redaction expectations;
- selected qualified governed-tool exposure channels or explicit non-exposure;
- success, validation, forbidden, tenant-isolation, idempotency, audit, approval, and surface-specific tests.

### 4. Classify capability shape

Use the shape to choose the Akka substrate later:
- read/evidence governed-tool in a capability → curated `View`, direct safe query, endpoint/browser-tool/agent-tool/resource exposure as needed;
- command governed-tool in a capability → entity/workflow command with validation, idempotency, auth, audit;
- proposal governed-tool in a capability → agent or human drafts change without committing side effects;
- approval governed-tool in a capability → human or policy-governed decision commits/rejects/delegates;
- AutonomousAgent worker governed-tool in a capability → durable task-oriented internal/background model-driven work; apply `../docs/autonomous-agent-worker-runtime-pattern.md` so start/read/cancel/accept/reject style capabilities preserve task contract, v3 `worker.task.*` events, attention, structured surfaces, provider fail-closed behavior, and no fake success;
- workflow governed-tool in a capability → long-running, retryable, approval-gated, or compensating process;
- policy/governance governed-tool in a capability → versioned policy, prompt, skill, threshold, simulation, activation, rollback;
- trace/audit governed-tool in a capability → record, search, explain, redact, or export history;
- scheduled governed-tool in a capability → timer-backed expiry, reminder, digest, replay, recheck, retention;
- reactive governed-tool in a capability → consumer-backed event reaction, enrichment, publication, or integration.

### 5. Select capability exposure channels after semantics

Choose only the capability exposure channels the capability needs. Use `structured surface` only for workstream renderable artifacts; use `exposure channel` for HTTP/gRPC/MCP-tool/workflow-tool/timer-tool/consumer-tool/view/internal-tool paths:
- browser UI action;
- HTTP or gRPC API;
- agent-tool or component-tool;
- MCP-tool/resource/prompt;
- workflow step;
- view/query;
- timer action;
- consumer reaction;
- internal component method only.

A capability may have multiple exposure channels, but all channels must preserve the same authority, validation, idempotency, audit, approval, and tenant/customer scope semantics.

Default stance: expose scoped read/evidence capabilities to agents more readily than side-effecting capabilities. Consequential side effects should default to proposal or approval-request capabilities unless accepted policy grants bounded autonomous authority.

## Routing

After capability semantics are clear, route to exactly one primary operating path:

- `app-descriptions` when maintaining or reviewing the app-description source of truth. Preserve capability inventory in description layers alongside behavior, auth/security, UI, observability, readiness, and tests.
- `akka-solution-decomposition` when deriving a direct Akka component plan. The decomposition must preserve the functional-agent/workstream/structured-surface inventory, map surface actions and payload-producing queries to capabilities, then map capabilities to entities, workflows, views, agents, consumers, timers, endpoints, and web UI realization.
- `akka-prd-to-specs-backlog` when creating repo-ready specs, backlogs, and pending tasks. Generated tasks must preserve capability ids, auth/scope, schemas, side effects, idempotency, approval, audit, exposure channels, and tests.
- Focused Stage 3 skills only when the secure foundation, capability contract, exposure channel, and Akka component choice are already settled.

## Stage 3 mapping

Load focused component skills only after the capability contract says why the component exists:
- audit-grade facts, decisions, approvals, traces, policies, or history → `akka-event-sourced-entities`;
- current-state profiles, preferences, configuration, or cache-like state → `akka-key-value-entities`;
- long-running execution, retries, compensation, approval waits, or orchestration → `akka-workflows`;
- curated lists, search, reports, evidence, dashboards, or audit queries → `akka-views`;
- bounded LLM planning, classification, recommendation, explanation, evaluation → `akka-agents`;
- async reactions, enrichment, publication, or external side effects → `akka-consumers`;
- deadlines, reminders, expiry, periodic checks, replay, retention → `akka-timed-actions`;
- browser/service request boundaries → `akka-http-endpoints` or `akka-grpc-endpoints`;
- remote LLM-facing tool/resource/prompt boundary → `akka-mcp-endpoints`;
- browser supervision, decision, governance, audit, outcome, and admin surfaces → `akka-web-ui-apps`.

## Output expectations

When this skill is used directly, produce or hand off:
- upstream functional-agent/workstream/surface context, or an explicit statement that the task is non-SaaS/repository-maintenance-only;
- surface/action-to-capability mapping for generated SaaS apps;
- capability inventory with ids and classes;
- actors/callers and AuthContext rules;
- input/output schemas and validation notes;
- side effects, idempotency, policy/approval, audit/trace obligations;
- selected capability exposure channels and explicit non-exposures, using qualified terms such as browser-tool, agent-tool, internal-tool, workflow-tool, timer-tool, consumer-tool, and MCP-tool;
- capability-to-governed-tool-to-Akka substrate/component mapping;
- downstream skill routing;
- tests required per capability, structured surface, and exposure channel;
- open questions only where implementation would otherwise guess workstream ownership, authority, risk, approval, audit, or scope.

## Anti-patterns

Avoid:
- jumping from product language directly to CRUD entities or endpoints;
- treating an agent-tool, browser-tool, endpoint route, or component method as the backend design root;
- exposing all component methods as agent-tools because Akka supports component-tool exposure;
- relying on prompt-only security, frontend-only filtering, or UI-hidden actions;
- returning raw state dumps when a scoped/redacted evidence capability is needed;
- letting one capability exposure channel drift from the capability's shared auth, idempotency, approval, or audit contract.
