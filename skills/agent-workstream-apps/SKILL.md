---
name: agent-workstream-apps
description: Interpret generated full-stack AI-first SaaS apps as role-authorized functional-agent workstream applications, then route to app-description, capability-first backend, web UI, agent, and Akka decomposition skills.
---

# Agent Workstream Apps

Use this routing skill after secure AI-first SaaS foundation framing and before app-description updates, UI design, agent design, capability modeling, PRD/backlog planning, or Akka implementation for generated full-stack SaaS apps. It is the default handoff from `ai-first-saas` for generated SaaS intent, so downstream work cannot skip from product goals directly to capabilities or Akka components.

This skill defines the application/UX shape. It does not replace `core-saas-foundation`, `capability-first-backend`, `app-descriptions`, `akka-solution-decomposition`, `akka-web-ui-apps`, `akka-agents`, or focused Akka component skills.

## Required reading

Read these first when using this skill:
- `../../AGENTS.md`
- `../README.md`
- `../../docs/agent-workstream-application-architecture.md`
- `../../docs/structured-surface-contracts.md`
- `../../docs/capability-first-backend-architecture.md`

For minimum, starter, basic, basic-chatbot, smallest-useful-app, or initial chatbot-like generated SaaS requests, also read `../../docs/minimum-ai-first-saas-app.md` before applying the minimum starter routing rule.

For high-level product input, also read `../../docs/ai-first-saas-application-architecture.md` and load `../ai-first-saas/SKILL.md` plus `../core-saas-foundation/SKILL.md`.

## Use when

Use this skill when the task involves a generated full-stack AI-first SaaS app and any of these are in scope:
- broad product, PRD, spec, backlog, or app-description planning;
- authenticated browser application shape;
- functional areas, work queues, command centers, admin consoles, supervision, decisions, governance, audit, or outcomes;
- user-facing agents, context-area agents, internal agents, agent tools, or work traces;
- web UI surfaces such as dashboards, forms, tables, charts, decision cards, diffs, approvals, workflow status, audit timelines, or outcome panels.

## Do not use when

Do not use this as the main skill for:
- repository-maintenance-only tasks unrelated to generated app architecture;
- narrow component implementation where the workstream model, capability contract, and component choice are already settled;
- public/static/non-SaaS reference material explicitly outside the secure AI-first SaaS default.

## Core rule

Generated AI-first SaaS apps are agent workstream applications by default:

```text
secure SaaS foundation
→ role-authorized functional agents
→ continuous workstreams
→ structured renderable surfaces
→ governed backend capabilities
→ horizontal Akka implementation
```

Functional agents and their surfaces are the vertical application slices. Akka entities, workflows, views, consumers, timed actions, agents, endpoints, frontend code, auth, audit, and tests are horizontal implementation substrates selected after capability semantics are clear.

Do not make a conventional page tree, CRUD console, or chatbot-bolt-on design the primary architecture for authenticated consequential work. Routes and pages may exist for implementation, deep links, public/static content, and direct surface URLs.

## Minimum starter routing

For natural-language requests such as “minimum AI-first app,” “starter app,” “basic app,” “basic chatbot,” “smallest useful app,” or an initial chatbot-like generated SaaS, apply `../../docs/minimum-ai-first-saas-app.md`.

The correct first runnable target is the **five core workstream v0 set**:
- bootstrap-authorized user and selected AuthContext;
- role-authorized functional agents for My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy with bounded bootstrap authority;
- durable request/response timeline and audit/work trace substrate for each visible core workstream;
- first structured surface type `markdown_response`, rendered as sanitized HTML;
- no public self-registration, autonomous privilege expansion, generic unauthenticated chatbot, or page-first CRUD admin app.

Treat this as a narrower starter readiness state, not full-core SaaS readiness. Record follow-up work for richer My Account, User Admin, Agent Admin, Audit/Trace, Governance/Policy, invitations/onboarding, governed behavior documents, and security completeness. Even in the minimum slice, route every backend action, surface action, browser API, and agent tool through capability-first modeling before selecting Akka components.

## Interpretation workflow

### 1. Preserve mandatory foundation

For generated SaaS apps, keep `core-saas-foundation` mandatory: identity, AuthContext, tenant/customer scope, memberships, roles/capabilities, `/api/me`, invitations, admin audit, backend authorization, tenant isolation, and security tests.

Full core app scope also requires complete My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy functional agents unless the user explicitly chooses a narrower deferred scope. The minimum starter is the accepted narrower scope: the five core v0 workstream set first, each using `markdown_response`, with full-core foundation items kept as explicit follow-up/readiness gates.

### 2. Identify vertical functional agents

For each user-facing work area, define:
- purpose and business responsibility;
- authorized roles/capabilities and tenant/customer scope;
- default dashboard, attention, or briefing surface;
- workstream semantics and retention expectations;
- prompt intent, governed documents, skills, reference documents, skill/reference manifests, tools, and tool boundaries when LLM behavior is involved;
- surfaces it can render or reuse;
- capabilities it can call directly or through tools/workflows;
- escalation, approval, denial, exception, audit, trace, and test needs.

Common foundation functional agents include My Account, User Admin, Agent Admin, Audit/Trace, Governance/Policy, Support Access, and Billing.

### 3. Distinguish internal agents

Internal agents are not left-rail navigation units. Use them for bounded backend AI work such as classification, summarization, evaluation, routing, proposal drafting, governance review, extraction, replay, or escalation triage. They still need governed `AgentDefinition`, prompts/skills/references, skill/reference manifests when expertise is loaded, tool boundaries, model policy, AuthContext or service authority basis, traces, and tests.

### 4. Define structured surfaces

Model surfaces as typed renderable artifacts, not text-only messages. Use `../../docs/structured-surface-contracts.md` when a surface needs implementation-ready payload, action, event, auth, trace, or rendering-test detail. Each surface should define:
- stable type and version;
- payload schema and redaction rules;
- allowed actions mapped to backend capabilities;
- tenant/customer and AuthContext assumptions;
- loading, empty, error, forbidden, stale/reconnect, accessibility, and responsive states;
- rendering tests and capability/action tests.

Canonical surface types include dashboards, forms, tables, charts, detail cards, decision/approval/exception cards, diffs, audit timelines, workflow status cards, evidence bundles, version cards, and outcome panels.

### 5. Route through capabilities before components

For every workstream action, surface action, agent tool, workflow step, timer, consumer reaction, API, MCP tool/resource, or internal call, load `capability-first-backend` and define actors, AuthContext, inputs/outputs, data access, side effects, idempotency, policy/approval, audit/trace, exposure surfaces, and tests.

Frontend gating, prompt text, and tool descriptions are never authorization controls. Backend capabilities remain authoritative.

## Downstream routing

Before choosing a downstream path, produce the handoff contract in this order:

```text
functional agents
→ internal agents where applicable
→ initial workstreams
→ structured surfaces
→ surface action-to-capability mappings
→ candidate horizontal Akka components and skills
```

Choose the smallest next path:

- `app-descriptions` when maintaining or reviewing the authoritative app description. Capture functional agents, internal agents, workstreams, surfaces, capabilities, auth/security, observability, UI, tests, readiness, and horizontal implementation notes.
- `capability-first-backend` when backend operations/queries are not yet explicit enough for decomposition or implementation.
- `akka-solution-decomposition` when the user wants a direct Akka component plan from the accepted workstream/capability model.
- `akka-prd-to-specs-backlog` when creating repo-ready specs, backlogs, sprints, slices, or pending tasks.
- `akka-web-ui-apps` plus focused web UI skills when implementing the shell, functional-agent rail, workstream panel, persistent composer, structured surfaces, typed clients, state, realtime behavior, accessibility, and tests.
- `akka-agents` plus focused agent skills when implementing functional agents, internal agents, governed behavior profiles/documents, tool boundaries, work traces, model governance, orchestration, evaluation, or tests.
- Focused Akka component skills only after capability contracts select the horizontal substrate: entities, workflows, views, consumers, timed actions, HTTP/gRPC/MCP endpoints, and tests.

## Cleanup warnings

For design-content review, use `../../docs/agent-workstream-design-review-checklist.md` as the compact pass/fail checklist.

When revising existing docs, specs, examples, or skills:
- replace page-first, CRUD-first, dashboard-with-chat, or chatbot-bolt-on defaults with the workstream model;
- keep conventional routes/pages as implementation/deep-link details, not the primary decomposition;
- keep agent tools as capability exposure surfaces, not backend design roots;
- do not make user administration, agent administration, audit, or security optional for full generated core SaaS scope;
- do not rewrite unrelated focused component guidance unless the task explicitly calls for that scope.

## Minimal output

When this skill is used for planning, hand downstream work a concise model containing:
- selected functional agents and their roles/capabilities;
- internal agents when applicable, clearly separated from left-rail functional agents;
- initial workstreams and default surfaces;
- structured surface contracts or required follow-up to create them;
- surface action-to-capability mappings;
- retained human authority, approval, escalation, and denial behavior;
- required audit/work traces;
- horizontal Akka component candidates and exact skills to load next;
- tests for authorization, tenant isolation, surface rendering, capability invocation, audit, and agent/tool boundaries.
