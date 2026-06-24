---
name: capability-first-backend
description: Model backend behavior as governed tools inside capabilities before choosing Akka components, actor adapters, or exposure channels, then route to app-description, decomposition, PRD/backlog, or focused implementation skills.
---

# Capability-First Backend

Use this skill after secure AI-first SaaS interpretation and worker/workstream modeling, and before selecting entities, workflows, endpoints, `agent_tool_call` adapters, MCP adapters, timers, consumers, or UI `surface_action` adapters.

This is a Build/compile-preparation routing and framing skill. It does not replace `core-saas-foundation`, `ai-first-saas`, app-description/current-intent skills, `akka-solution-decomposition`, spec/backlog skills, or focused Stage 3 component skills. In the living app-description graph, capabilities and governed tools are first-class current-intent nodes that bind worker actions and actor adapters to realization paths.

## Lifecycle classification

- Phase role: Build/compile-preparation contract for governed operations and evidence reads after worker/workstream context exists.
- Graph layer: governed-tool and capability nodes, actor adapters, AuthContext, policy, side effects, traces, tests, and implementation mappings.
- Canonical chain: `worker → execution harness → actor adapter → governed tool → capability → Akka implementation`.

## Required reading

Read these first when using this skill:
- target project path: AGENTS.md
- `../README.md`
- `../docs/intent-compiler.md`
- `../docs/current-intent-model.md`
- `../docs/intent-to-realization-flow.md`
- `../docs/app-development-lifecycle.md`
- `../docs/app-worker-tool-model.md`
- `../docs/app-description-component-graph.md`
- `../docs/app-description-to-code-compile-contract.md`
- `../docs/ai-first-saas-application-architecture.md`
- `../docs/agent-workstream-application-architecture.md` for generated full-stack SaaS app modeling before backend capability design
- `../docs/workforce-decomposition.md` when workers, responsibilities, authority, supervision, or handoffs are in scope
- `../docs/structured-surface-contracts.md` when workstream surfaces, `surface_action` adapters, or browser UI actions are in scope
- `../docs/workstream-surface-intent-routing.md` when composer prompts open or prepopulate protected surfaces
- `../docs/capability-first-backend-architecture.md`
- `../agent-workstream-apps/SKILL.md` when generated SaaS intent has not already produced functional-agent, workstream, and structured-surface context

For generated-app durable AutonomousAgent worker tasks, also read `../docs/autonomous-agent-worker-runtime-pattern.md` before finalizing the capability contract.

Then load only the smallest downstream skill set needed for the selected path.

## Use when

Use this skill for broad product, feature, PRD, spec, backlog, app-description, or implementation-routing work where backend behavior must be shaped before coding.

Especially use it when the request involves:
- operations or queries that need explicit actors, authorization, tenant/customer scope, schemas, side effects, or audit;
- `agent_tool_call` or component-tool exposures;
- browser/API/MCP exposure choices;
- approval-gated, long-running, scheduled, or event-reactive work;
- shared semantics across `surface_action`, `agent_tool_call`, APIs, workflows, timers, consumers, and qualified governed-tool actor adapters/exposure channels.

## Do not use when

Do not use this skill as a substitute for:
- `core-saas-foundation` for mandatory SaaS identity, tenancy, authorization, `/api/me`, admin, invitation, audit, and security-test foundation;
- `ai-first-saas` for delegated work, goals/plans, agent teams, governance, decisions, supervision, traces, and outcomes;
- `app-descriptions` when the user is maintaining the authoritative application description;
- `akka-solution-decomposition` when a full Akka component plan must be produced;
- focused Stage 3 skills when the capability contract and component choice are already settled.

## Core rule

For generated full-stack SaaS apps, capabilities sit below the agent workstream application model and above Akka implementation:

```text
secure foundation → functional agents/workstreams/workforce → structured surfaces/actions → governed tools → capabilities → actor adapters/exposure channels → Akka substrates
```

Functional agents, workstreams, and structured surfaces define the user-facing application model. Capabilities group product-level backend abilities. Governed tools are the executable semantic operations or queries inside those capabilities and carry the shared authority/behavior contract behind every `surface_action`, confirmed `human_chat_tool_plan`, `agent_tool_call`, browser/API adapter, `workflow_step`, `timer_invocation`, `consumer_reaction`, or internal call. Deterministic surface intent routes that open or prepopulate protected surfaces must still resolve selected AuthContext and capability authorization in the backend and remain no-mutation by themselves. Do not use capability-first modeling to bypass functional-agent and surface modeling for generated SaaS apps.

For each governed tool, record actors/callers, AuthContext and scope, typed inputs/outputs, data access and redaction, side effects, idempotency, policy/approval rules, audit/trace obligations, selected actor adapters or exposure channels, and tests. `agent_tool_call` adapters, browser `surface_action` adapters, HTTP/gRPC/MCP endpoints, workflow steps, timer invocations, consumer reactions, view queries, and internal component methods are actor adapters, exposure channels, or realization choices for governed tools. They are not the backend design root.

## Interpretation workflow

### 1. Preserve the mandatory foundation

For generated SaaS apps, verify `core-saas-foundation` has been applied and keep its rules in force while modeling capabilities. The upstream handoff should already identify functional agents, workstreams, and structured surfaces; represent foundation work as protected capabilities behind those surfaces, not as unauthenticated object access. Every protected capability must enforce authenticated account, selected `AuthContext`, active membership, tenant/customer scope, role/permission/capability authorization, backend checks, audit, and tenant-isolation tests.

Prompt text, model-facing tool descriptions, frontend navigation, and hidden fields are never authorization controls.

### 2. Preserve workstream and surface context

For generated SaaS apps, first identify or load the upstream workstream model before inventing backend operations:
- role-authorized functional/context-area agents and their tenant/customer scope;
- durable workstreams and retained human authority for each functional agent;
- worker roster: human workers, functional-agent workers, internal/autonomous/evaluator agent workers, and system workers;
- worker responsibilities, authority levels, supervising humans, handoffs/escalations, failure behavior, and trace obligations;
- structured surfaces, payload-producing queries, allowed actions, events, and trace links;
- surface/action placement, reusable functional-agent placement, and denial/recovery states;
- candidate action-to-governed-tool/capability mappings from each `surface_action`, deterministic surface-intent route, confirmed `human_chat_tool_plan`, `agent_tool_call`, browser/API adapter, `workflow_step`, API call, `timer_invocation`, `consumer_reaction`, or `internal_call`.

If this context is missing for a generated full-stack SaaS request, route through `agent-workstream-apps` or record the gap before selecting capabilities or Akka components.

### 3. Inventory capabilities

For each workstream operation, structured `surface_action`, confirmed `human_chat_tool_plan` adapter, payload-producing query, governed tool, `agent_tool_call`, browser/API adapter, `workflow_step`, API, `timer_invocation`, `consumer_reaction`, or internal operation, define the stable capability/governed-tool ids, purpose, allowed actors/callers, AuthContext and scope, schemas, validation/redaction, idempotency/correlation, data access, side effects, policy/approval/escalation, audit/work-trace fields, selected actor adapters or explicit non-exposure, and required tests.

### 4. Classify capability shape

Use the shape to choose the Akka substrate later:
- read/evidence governed-tool in a capability → curated `View`, direct safe query, endpoint, browser `surface_action`, `agent_tool_call`, or resource exposure as needed;
- command governed-tool in a capability → entity/workflow command with validation, idempotency, auth, audit;
- proposal governed-tool in a capability → agent or human drafts change without committing side effects;
- approval governed-tool in a capability → human or policy-governed decision commits/rejects/delegates;
- AutonomousAgent worker governed-tool in a capability → durable task-oriented internal/background model-driven work; apply `../docs/autonomous-agent-worker-runtime-pattern.md` so start/read/cancel/accept/reject style capabilities preserve task contract, typed `worker.task.*` workstream events, attention, structured surfaces, provider fail-closed behavior, and no fake success;
- workflow governed-tool in a capability → long-running, retryable, approval-gated, or compensating process;
- policy/governance governed-tool in a capability → versioned policy, prompt, skill, threshold, simulation, activation, rollback;
- trace/audit governed-tool in a capability → record, search, explain, redact, or export history;
- scheduled governed-tool in a capability → timer-backed expiry, reminder, digest, replay, recheck, retention;
- reactive governed-tool in a capability → consumer-backed event reaction, enrichment, publication, or integration.

### 5. Select actor adapters and exposure channels after semantics

Choose only the actor adapters and exposure channels the governed tool needs. Use `structured surface` only for workstream renderable artifacts; use `actor adapter` for worker/harness entry paths and `exposure channel` for HTTP/gRPC/MCP, workflow, timer, consumer, view, and internal paths:
- `surface_action` as the structured human browser adapter;
- `human_chat_tool_plan` for natural-language requests that propose a detailed plan, require explicit confirmation, and execute governed tools only after backend checks;
- HTTP or gRPC API (`api_call`);
- `agent_tool_call` or component-tool exposure;
- MCP tool/resource/prompt (`mcp_tool_call`);
- workflow step (`workflow_step`);
- view/query;
- timer invocation (`timer_invocation`);
- consumer reaction (`consumer_reaction`);
- internal component method only (`internal_call`).

A governed tool may have multiple actor adapters or exposure channels, but all paths must preserve the same authority, validation, idempotency, audit, approval, and tenant/customer scope semantics. Human chat confirmation is separate from higher-level approval: confirmation authorizes execution of the proposed plan by that human actor, while policy approval may still be required before or during the governed-tool sequence.

Default stance: expose scoped read/evidence capabilities to agents more readily than side-effecting capabilities. Consequential side effects should default to proposal or approval-request capabilities unless accepted policy grants bounded autonomous authority.

## Routing

After capability semantics are clear, route to exactly one primary operating path:

- `app-descriptions` when maintaining or reviewing the app-description/current-intent graph source of truth. Preserve capability inventory under domain/workstream artifacts and link it to global definitions, behavior, auth/security, UI, observability, readiness, and tests.
- `akka-solution-decomposition` when deriving a direct Akka component plan. The decomposition must preserve the functional-agent/workstream/structured-surface inventory, map surface actions and payload-producing queries to governed tools and capabilities, then map capabilities to entities, workflows, views, agents, consumers, timers, endpoints, and web UI realization.
- `akka-prd-to-specs-backlog` or related planning skills when compiling accepted current intent into repo-ready specs, backlogs, and pending tasks. Generated tasks must preserve governed-tool ids, capability ids, auth/scope, schemas, side effects, idempotency, approval, audit, actor adapters/exposure channels, and tests.
- Focused Stage 3 skills only when the secure foundation, capability contract, actor adapter/exposure channel, and Akka component choice are already settled.

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
- upstream functional-agent/workstream/surface context, or an explicit non-SaaS/repository-maintenance-only statement;
- worker-to-capability context: worker, execution harness, actor adapter, governed tool, authority, handoff/escalation, and trace source;
- generated-SaaS surface/action-to-capability mapping and capability inventory with ids/classes;
- actors/callers, AuthContext, schemas, validation, side effects, idempotency, policy/approval, audit/trace obligations, and explicit non-exposures;
- selected actor adapters/exposure channels using qualified terms such as `surface_action`, `human_chat_tool_plan`, `agent_tool_call`, `internal_call`, `workflow_step`, `timer_invocation`, `consumer_reaction`, `api_call`, and `mcp_tool_call`;
- capability-to-governed-tool-to-Akka mapping, downstream skill routing, required tests, and blocking open questions.

## Anti-patterns

Avoid:
- jumping from product language directly to CRUD entities or endpoints;
- treating an agent tool, browser action, endpoint route, or component method as the backend design root;
- exposing all component methods as `agent_tool_call` adapters because Akka supports component-tool exposure;
- relying on prompt-only security, frontend-only filtering, or UI-hidden actions;
- returning raw state dumps when a scoped/redacted evidence capability is needed;
- letting one actor adapter or exposure channel drift from the governed tool's shared auth, idempotency, approval, or audit contract.
