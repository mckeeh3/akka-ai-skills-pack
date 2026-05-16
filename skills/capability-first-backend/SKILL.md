---
name: capability-first-backend
description: Model backend behavior as governed capabilities before choosing Akka components or exposure surfaces, then route to app-description, decomposition, PRD/backlog, or focused implementation skills.
---

# Capability-First Backend

Use this skill after secure AI-first SaaS interpretation and before selecting entities, workflows, endpoints, agent tools, MCP tools, timers, consumers, or UI actions.

This is a routing and framing skill. It does not replace `core-saas-foundation`, `ai-first-saas`, app-description skills, `akka-solution-decomposition`, PRD/spec/backlog skills, or focused Stage 3 component skills.

## Required reading

Read these first when using this skill:
- `../../AGENTS.md`
- `../README.md`
- `../../docs/ai-first-saas-application-architecture.md`
- `../../docs/capability-first-backend-architecture.md`

Then load only the smallest downstream skill set needed for the selected path.

## Use when

Use this skill for broad product, feature, PRD, spec, backlog, app-description, or implementation-routing work where backend behavior must be shaped before coding.

Especially use it when the request involves:
- operations or queries that need explicit actors, authorization, tenant/customer scope, schemas, side effects, or audit;
- agent-accessible tools or component tools;
- browser/API/MCP exposure choices;
- approval-gated, long-running, scheduled, or event-reactive work;
- shared semantics across UI actions, APIs, workflows, timers, consumers, and tools.

## Do not use when

Do not use this skill as a substitute for:
- `core-saas-foundation` for mandatory SaaS identity, tenancy, authorization, `/api/me`, admin, invitation, audit, and security-test foundation;
- `ai-first-saas` for delegated work, goals/plans, agent teams, governance, decisions, supervision, traces, and outcomes;
- `app-descriptions` when the user is maintaining the authoritative application description;
- `akka-solution-decomposition` when a full Akka component plan must be produced;
- focused Stage 3 skills when the capability contract and component choice are already settled.

## Core rule

A backend capability is the root design object:

```text
capability = named operation or query
  + actors/callers
  + AuthContext and scope
  + typed inputs/outputs
  + data access and redaction
  + side effects
  + idempotency
  + policy/approval rules
  + audit/trace obligations
  + selected exposure surfaces
  + tests
```

Agent tools, HTTP/gRPC/MCP endpoints, browser actions, workflow steps, timer callbacks, consumer reactions, view queries, and internal component methods are exposure or realization choices for capabilities. They are not the root abstraction.

## Interpretation workflow

### 1. Preserve the mandatory foundation

For generated SaaS apps, load `core-saas-foundation` first. Every protected capability must enforce authenticated account, selected `AuthContext`, active membership, tenant/customer scope, role/permission/capability authorization, backend checks, audit, and tenant-isolation tests.

Prompt text, tool descriptions, frontend navigation, and hidden fields are never authorization controls.

### 2. Inventory capabilities

For each product operation or query, define:
- stable capability id/name in product language;
- purpose and business outcome;
- allowed actors/callers: humans, agents, workflows, services, timers, consumers, support roles;
- AuthContext, tenant/customer scope, roles, permissions, and named capability grants;
- input schema, validation, idempotency key, correlation id, and safe defaults;
- output schema, redaction, safe error and denial shapes;
- data reads/writes and tenant/customer filters;
- side effects: state changes, external calls, topics, timers, emails, notifications, workflow starts;
- policy, approval, escalation, risk/confidence/impact thresholds, and autonomy level;
- audit/work-trace fields and retention/redaction expectations;
- selected exposure surfaces or explicit non-exposure;
- success, validation, forbidden, tenant-isolation, idempotency, audit, approval, and surface-specific tests.

### 3. Classify capability shape

Use the shape to choose the Akka substrate later:
- read/evidence capability → curated `View`, direct safe query, endpoint/tool/resource exposure as needed;
- command capability → entity/workflow command with validation, idempotency, auth, audit;
- proposal capability → agent or human drafts change without committing side effects;
- approval capability → human or policy-governed decision commits/rejects/delegates;
- workflow capability → long-running, retryable, approval-gated, or compensating process;
- policy/governance capability → versioned policy, prompt, skill, threshold, simulation, activation, rollback;
- trace/audit capability → record, search, explain, redact, or export history;
- scheduled capability → timer-backed expiry, reminder, digest, replay, recheck, retention;
- reactive capability → consumer-backed event reaction, enrichment, publication, or integration.

### 4. Select exposure surfaces after semantics

Choose only the surfaces the capability needs:
- browser UI action;
- HTTP or gRPC API;
- agent tool or component tool;
- MCP tool/resource/prompt;
- workflow step;
- view/query;
- timer action;
- consumer reaction;
- internal component method only.

A capability may have multiple surfaces, but all surfaces must preserve the same authority, validation, idempotency, audit, approval, and tenant/customer scope semantics.

Default stance: expose scoped read/evidence capabilities to agents more readily than side-effecting capabilities. Consequential side effects should default to proposal or approval-request capabilities unless accepted policy grants bounded autonomous authority.

## Routing

After capability semantics are clear, route to exactly one primary operating path:

- `app-descriptions` when maintaining or reviewing the app-description source of truth. Preserve capability inventory in description layers alongside behavior, auth/security, UI, observability, readiness, and tests.
- `akka-solution-decomposition` when deriving a direct Akka component plan. The decomposition must map capabilities to entities, workflows, views, agents, consumers, timers, endpoints, and web UI surfaces.
- `akka-prd-to-specs-backlog` when creating repo-ready specs, backlogs, and pending tasks. Generated tasks must preserve capability ids, auth/scope, schemas, side effects, idempotency, approval, audit, exposure surfaces, and tests.
- Focused Stage 3 skills only when the secure foundation, capability contract, exposure surface, and Akka component choice are already settled.

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
- capability inventory with ids and classes;
- actors/callers and AuthContext rules;
- input/output schemas and validation notes;
- side effects, idempotency, policy/approval, audit/trace obligations;
- selected exposure surfaces and explicit non-exposures;
- Akka substrate/component mapping;
- downstream skill routing;
- tests required per capability and surface;
- open questions only where implementation would otherwise guess authority, risk, approval, audit, or scope.

## Anti-patterns

Avoid:
- jumping from product language directly to CRUD entities or endpoints;
- treating an agent tool as the backend design root;
- exposing all component methods as tools because Akka supports component tools;
- relying on prompt-only security, frontend-only filtering, or UI-hidden actions;
- returning raw state dumps when a scoped/redacted evidence capability is needed;
- letting one exposure surface drift from the capability's shared auth, idempotency, approval, or audit contract.
